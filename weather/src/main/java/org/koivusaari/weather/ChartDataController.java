package org.koivusaari.weather;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.koivusaari.weather.DataMapper;
import org.koivusaari.weather.SensorRepository;
import org.koivusaari.weather.pojo.ChartRow;
import org.koivusaari.weather.pojo.Graph;
import org.koivusaari.weather.pojo.GraphDataSeries;
import org.koivusaari.weather.pojo.GraphSeries;
import org.koivusaari.weather.pojo.Sensors;
import org.koivusaari.weather.pojo.Series;
import org.koivusaari.weather.pojo.googlecharts.ChartC;
import org.koivusaari.weather.pojo.googlecharts.ChartCol;
import org.koivusaari.weather.pojo.googlecharts.ChartData;
import org.koivusaari.weather.pojo.googlecharts.ChartV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChartDataController {

	public enum GroupBy {
		MONTH("month"),
		WEEK("week"),
		DAY("day"),
		HOUR("hour");
		
		private String groupBy;
		private GroupBy(String groupBy) {
			this.groupBy = groupBy;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(ChartDataController.class);

	NamedParameterJdbcTemplate jdbcTemplate;
	SensorRepository sensorRepository;
	GraphDataSeriesRepository graphDataSeriesRepository;
	GraphRepository graphRepository;
	HashMap<Long,GraphDataSeries> dataSeriesMap=new HashMap<Long,GraphDataSeries>();
	HashMap<Long,GraphDataSeries> graphSeriesMap=new HashMap<Long,GraphDataSeries>();
	
	public ChartDataController(NamedParameterJdbcTemplate jdbcTemplate
			                  ,SensorRepository sensorRepository
			                  ,GraphRepository graphRepository
			                  ,GraphDataSeriesRepository graphDataSeriesRepository) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.sensorRepository = sensorRepository;
		this.graphRepository = graphRepository;
		this.graphDataSeriesRepository = graphDataSeriesRepository;
	}

	@CrossOrigin
	@RequestMapping("/chartdata")
    public Object index(@RequestParam(value="id") String graphId, HttpServletResponse response) {
    	        
		log.info("Params:");
		log.info("graphId: "+graphId);
		log.info("Response: "+response);
		
		Graph graph=graphRepository.findOne(Long.parseLong(graphId));

		log.info("Querying data for today:");
		HashMap<String,Object> params=new HashMap<String,Object>();
		String sql=createSelect(graph,params);
		log.info("select:\n"+sql);
		if (graph.getFrom()!=null || graph.getTo()!=null){
			if (graph.getDynamic()==null||graph.getDynamic()==Graph.STATIC_TIME) {
				params.put("from", Date.from(graph.getFrom().atZone(ZoneId.systemDefault()).toInstant()));
				params.put("to", Date.from(graph.getTo().atZone(ZoneId.systemDefault()).toInstant()));
			} else {
				Duration p=Duration.between(graph.getFrom(), graph.getTo());
				LocalDateTime to=LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
				LocalDateTime from=to.minus(p);
				params.put("from", Date.from(from.atZone(ZoneId.systemDefault()).toInstant()));
				params.put("to", Date.from(to.atZone(ZoneId.systemDefault()).toInstant()));
				log.info("Dynamic true.");
				log.info("period: "+p);
				log.info("fromDate: "+params.get("from"));
				log.info("toDate: "+params.get("to"));
			}
		}

		log.debug("Params: "+params);
		List<ChartRow> data=jdbcTemplate.query(sql,params,new DataMapper());
		response.setIntHeader("max-age", (int)getMaxAge(graph, data));
    	ChartData chartData = dataToChart(data,graph);
        return chartData;
    }

	protected long getMaxAge(Graph graph, List<ChartRow> data){
		String trunc=getSmallestTrunc(graph);
		log.debug("trunc="+trunc);
		
		ChartRow row=data.get(data.size()-1);
		LocalDateTime lastData=LocalDateTime.ofInstant(row.getTime().toInstant(), ZoneId.systemDefault());
		log.debug("lastData="+lastData);
		
		Pattern groupByPattern = Pattern.compile("(\\d*) ?(\\D+)"); //numbers as groups  
		Matcher m=groupByPattern.matcher(trunc);
		log.debug("match found="+m.matches());
		String amount=m.group(1);
		if (amount.equals("")){
		  	amount="1";
		}
		String unit=m.group(2);
		Duration duration=Duration.of(Long.parseLong(amount), ChronoUnit.valueOf(unit));
		
		LocalDateTime cacheEnd=lastData.plus(duration);
		log.debug("cacheEnd="+cacheEnd);
		Duration maxAge=Duration.between(LocalDateTime.now(), cacheEnd);
		log.debug("duration="+maxAge+" maxAge="+maxAge.getSeconds());
		return maxAge.getSeconds();
	}
	
	protected ChartData dataToChart(List<ChartRow> data, Graph graph) {
		ChartData chartData=new ChartData();
        ArrayList<ChartCol> chartCols = createCols(data, graph);
        chartData.setCols(chartCols);
        
        ArrayList<ChartC> chartrows=new ArrayList<ChartC>();
        for (ChartRow row:data){
        	ArrayList<ChartV> list=new ArrayList<ChartV>();
        	list.add(new ChartV(row.getTime()));
        	for (Float value:row.getData()){
        		list.add(new ChartV(value));
        	}
        	ChartC c=new ChartC();
        	c.setC(list);
        	chartrows.add(c);
        }
        chartData.setRows(chartrows);
		return chartData;
	}

	/**
	 * Create column headers for google charts
	 * 
	 * @param data
	 * @param idList List of sensor id's to include
	 * @return
	 */
	protected ArrayList<ChartCol> createCols(List<ChartRow> data, Graph graph) {
		ArrayList<ChartCol> chartCols=new ArrayList<ChartCol>();
        chartCols.add(new ChartCol("time","datetime"));
        int i=0;
		for (GraphSeries gs:graph.getGraphSeries()){
			Series series=gs.getSeries();
        	i++;
        	if (series.getSensorid()!=null) {
            	Sensors sensors=sensorRepository.findOne(series.getSensorid());
	            chartCols.add(new ChartCol("c"+Integer.toString(i),"number",sensors.getName()));
	            chartCols.get(i).setUnit(sensors.getUnitId().toString());
        	} else{
	            chartCols.add(new ChartCol("c"+Integer.toString(i),"number",series.getName()));
	            chartCols.get(i).setUnit("-1"); //TODO get some unit for compound series
        	}
        		
        }
		return chartCols;
	}
	
	protected String createSelect(Graph graph,HashMap<String,Object> params){
		Pattern seriesIdPattern = Pattern.compile(":(\\d+)"); //numbers as groups  
		String select="";
		String from="";
		String where="";
		String groupBy="";

		//TODO Currently there is trunking of data with long timespans
//		String defaultTrunc=getGroupBy(graph.getFrom(), graph.getTo());
		String defaultTrunc=getSmallestTrunc(graph);
		
		select="SELECT time_series";
		from  ="  FROM generate_series("+getTruncFunction(defaultTrunc, ":from::timestamp")+", :to::timestamp, '"+(defaultTrunc==null?"1 minute":defaultTrunc)+"') time_series\n";
		int i=0;
		for (GraphSeries gs:graph.getGraphSeries()){
			Series series=gs.getSeries();
    		i++;
			if (series.getSensorid()!=null){
				String trunc=series.getMinGroupByTime()==null?defaultTrunc:series.getMinGroupByTime();
				String valueFunction=series.getValuefunction();
	            String value="d"+i+".value";
	            log.debug("trunc="+trunc+" valueFunction="+valueFunction+" value="+value);
				select=select+", "+processValueFunction(value, valueFunction);
				if (trunc=="1 MINUTES"){
				    from  =from  +"       LEFT JOIN (select time as time, value as value from data where sensorid=:"+i+" and time between :from::timestamp and :to::timestamp) d"+i+" ON time_series = d"+i+".time\n";
				} else {
				    from  =from  +"       LEFT JOIN (select "+getTruncFunction(trunc)+" as time, "+(series.getGroupby()==null?"value":series.getGroupby())+" as value from data where sensorid=:"+i+" and time between "+getTruncFunction(defaultTrunc, ":from::timestamp")+" and :to::timestamp group by "+getTruncFunction(trunc)+") d"+i+" ON time_series = d"+i+".time\n";
				}
				params.put(Integer.toString(i), series.getSensorid());
			} else {
				String valueFunction=series.getValuefunction();
				log.debug("Combined valuefunction: "+valueFunction);
				Matcher m = seriesIdPattern.matcher(valueFunction);
				int j=1;
				while (m.find()){
					//TODO fix compound functions probably won't work
					String trunc=series.getMinGroupByTime()==null?defaultTrunc:series.getMinGroupByTime();
					log.debug("Value "+m.group(1));
					int seriesid=Integer.parseInt(m.group(1));
					log.debug("GraphSeries("+seriesid+")="+graphSeriesMap.get(new Long(seriesid)));
					String value=processValueFunction("combineddata"+j+".value",graphSeriesMap.get(new Long(seriesid)).getValuefunction());
					value="("+value+")";
					valueFunction=valueFunction.replaceAll(m.group(),value );
					if (trunc==null){
					    from  =from  +"       LEFT JOIN (select time, value from data where sensorid=:compound"+j+") combineddata"+j+" ON time_series = combineddata"+j+".time\n";
					} else {
					    from  =from  +"       LEFT JOIN (select date_trunc('"+trunc+"',time) as time, "+graphSeriesMap.get(new Long(seriesid)).getGroupby()+"(value) as value from data where sensorid=:compound"+j+" group by date_trunc('"+trunc+"',time)) combineddata"+j+" ON time_series = combineddata"+j+".time\n";
					}
					params.put("compound"+j, graphSeriesMap.get(new Long(seriesid)).getSensorid());
					j++;
				}
				select=select+", "+valueFunction;

			}
		}
		select = select+"\n";
		
		return select+from+where+groupBy+" ORDER BY 1";
	}

	protected String getSmallestTrunc(Graph graph){
		ArrayList<Trunc> truncs = new ArrayList<Trunc>(); 
		for (GraphSeries gs:graph.getGraphSeries()){
			Series series=gs.getSeries();
			truncs.add(new Trunc(series.getMinGroupByTime()));
		}
		
		Collections.sort(truncs);
		return truncs.get(0).getTrunc();
		
	}
	private class Trunc implements Comparable<Trunc>{
		
		private String trunc;
		private Duration duration;
		
		public Trunc(String trunc){
			if (trunc==null){
				duration=Duration.of(1, ChronoUnit.MINUTES);
				this.trunc="1 MINUTES";
				return;
			}
			this.trunc=trunc;
			Pattern groupByPattern = Pattern.compile("(\\d*) ?(\\D+)"); //numbers as groups  
			Matcher m=groupByPattern.matcher(trunc);
			if (m.matches()){
			    String amount=m.group(1);
			    if (amount.equals("")){
			    	amount="1";
			    }
			    String unit=m.group(2);
			    duration=Duration.of(Long.parseLong(amount), ChronoUnit.valueOf(unit));
			} else {
				duration=Duration.of(1, ChronoUnit.MINUTES);
				this.trunc="1 MINUTES";
			}
		}

		
		public Duration getDuration() {
			return duration;
		}
		public String getTrunc() {
			return trunc;
		}
		public void setTrunc(String trunc) {
			this.trunc = trunc;
		}
		public void setDuration(Duration duration) {
			this.duration = duration;
		}


		@Override
		public int compareTo(Trunc o) {
			return duration.compareTo(o.getDuration());
		}
	}
	
	protected String getTruncFunction(String trunc){
		return getTruncFunction(trunc, "time");
	}
	protected String getTruncFunction(String trunc,String field){
		Pattern groupByPattern = Pattern.compile("(\\d+) (\\D+)"); //numbers as groups  
		String function = null;

		if (trunc==null) {
			return field;
		}
		
		Matcher m=groupByPattern.matcher(trunc);
		if (m.matches()){
			String amount=m.group(1);
			String unit=m.group(2);
			if (unit.equals("MINUTES")){
				if (amount.equals("1")){
					function=field;
				} else {
					function="round_minutes("+field+","+amount+")";
				}
			} else if (unit.equals("HOURS")){
				if (amount.equals("1")){
					function="date_trunc('HOUR',"+field+")";
				} else {
					function="round_hours("+field+","+amount+")";
				}
			} else {
//				throw new Exception("Unknown temporal unit: "+unit);
				log.error("Unknown temporal unit: "+unit);
			}
		} else {
			function="date_trunc('"+trunc+"',"+field+")";
		}
		return function;
	}
	
	protected String processValueFunction(String value, String valueFunction){
	    if (valueFunction==null) {
	    	return value;
	    } else {
	    	return valueFunction.replaceAll(":value", value);
	    }
	}
	
	protected String getGroupBy(LocalDateTime from, LocalDateTime to){
		log.info("From: "+from+" to:"+to);
		if (from==null || to==null){
			return null;
		}
		String groupBy=null;
	    long hours=ChronoUnit.HOURS.between(from,to);
	    if (hours>24*30) { //month
	    	groupBy="day";
	    } else if (hours>24*7){ //week
	    	groupBy="hour";
	    } else if (hours>24){ //day
	    	groupBy="hour";
	    }
	    log.info("hours: "+hours+" group by: "+groupBy);
	    return groupBy;
	}
	
	protected void fillDataSeriesMap() {
		Iterable<GraphDataSeries> series=graphDataSeriesRepository.findAll();
		for (GraphDataSeries s:series){
			dataSeriesMap.put(s.getSeriesid(), s);
			graphSeriesMap.put(s.getSeriesid(), s);
		}
	}

}
