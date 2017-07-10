package org.koivusaari.weather;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

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
import org.koivusaari.weather.repositories.GraphDataSeriesRepository;
import org.koivusaari.weather.repositories.GraphRepository;
import org.koivusaari.weather.repositories.SensorRepository;
import org.koivusaari.weather.scale.AbstractGraphParameters;
import org.koivusaari.weather.scale.OutsideTempGraphParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChartDataController {

	private static final String PARAM_TO = "to";

	private static final String PARAM_FROM = "from";

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
	OutsideTempGraphParameters outsideTempGraphParameters;
	@Autowired
	Map<String,AbstractGraphParameters> graphParameters;
	
	public ChartDataController(final NamedParameterJdbcTemplate jdbcTemplate
			                  ,final SensorRepository sensorRepository
			                  ,final GraphRepository graphRepository
			                  ,final GraphDataSeriesRepository graphDataSeriesRepository
			                  ,final OutsideTempGraphParameters outsideTempGraphParameters) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.sensorRepository = sensorRepository;
		this.graphRepository = graphRepository;
		this.graphDataSeriesRepository = graphDataSeriesRepository;
		this.outsideTempGraphParameters = outsideTempGraphParameters;
	}

	@CrossOrigin
	@RequestMapping("/chartdata")
    public Object index(@RequestParam(value="id") final String graphId,
    		            final HttpServletResponse response) {
    	        
		if (log.isDebugEnabled()){
			log.debug("Params:");
			log.debug("graphId: "+graphId);
			log.debug("Response: "+response);
		}
		
		final Graph graph=graphRepository.findOne(Long.parseLong(graphId));

		if (log.isDebugEnabled()){
			log.debug("Querying data for today:");
		}
		final HashMap<String,Object> params=new HashMap<String,Object>();
		final String sql=createSelect(graph,params);
		if (log.isDebugEnabled()){
			log.debug("select:\n"+sql);
		}
		if (graph.getFrom()!=null || graph.getTo()!=null){
			if (graph.getDynamic()==null||graph.getDynamic()==Graph.STATIC_TIME) {
				params.put(PARAM_FROM, Date.from(graph.getFrom().atZone(ZoneId.systemDefault()).toInstant()));
				params.put(PARAM_TO, Date.from(graph.getTo().atZone(ZoneId.systemDefault()).toInstant()));
			} else {
				Duration p=Duration.between(graph.getFrom(), graph.getTo());
				LocalDateTime to=LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
				LocalDateTime from=to.minus(p);
				params.put(PARAM_FROM, Date.from(from.atZone(ZoneId.systemDefault()).toInstant()));
				params.put(PARAM_TO, Date.from(to.atZone(ZoneId.systemDefault()).toInstant()));
				if (log.isDebugEnabled()){
					log.debug("Dynamic true.");
					log.debug("period: "+p);
					log.debug("fromDate: "+params.get(PARAM_FROM));
					log.debug("toDate: "+params.get(PARAM_TO));
				}
			}
		}

		if (log.isDebugEnabled()){
			log.debug("Params: "+params);
		}
		final List<ChartRow> data=jdbcTemplate.query(sql,params,new DataMapper());
		response.setHeader("Cache-Control","max-age="+getMaxAge(graph, data));
    	ChartData chartData = dataToChart(data,graph);
    	chartData=metadata(data, graph, chartData);
        return chartData;
    }

	@CrossOrigin
	@RequestMapping("/chartdataupdate")
    public Object chartDataUpdate(final @RequestParam(value="id") String graphId
    		                     ,final @RequestParam(value=PARAM_FROM) String updateFrom
    		                     ,final HttpServletResponse response) {
    	        
		if (log.isDebugEnabled()){
			log.debug("Params:");
			log.debug("graphId: "+graphId);
			log.debug("from: "+updateFrom);
			log.debug("Response: "+response);
		}
		
		final Graph graph=graphRepository.findOne(Long.parseLong(graphId));

		final HashMap<String,Object> params=new HashMap<String,Object>();
		final String sql=createSelect(graph,params);
		if (log.isDebugEnabled()){
			log.debug("select:\n"+sql);
		}

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		final LocalDateTime from = LocalDateTime.parse(updateFrom, formatter);
		final LocalDateTime to=LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

		params.put(PARAM_FROM, Date.from(from.atZone(ZoneId.systemDefault()).toInstant()));
		params.put(PARAM_TO, Date.from(to.atZone(ZoneId.systemDefault()).toInstant()));

		if (log.isDebugEnabled()){
			log.debug("Params: "+params);
		}
		final List<ChartRow> data=jdbcTemplate.query(sql,params,new DataMapper());
		response.setHeader("Cache-Control","max-age="+getMaxAge(graph, data));
//    	ChartData chartData = dataToChart(data,graph);
//		protected ChartData dataToChart(List<ChartRow> data, Graph graph) {
//			ChartData chartData=new ChartData();
//	        ArrayList<ChartCol> chartCols = createCols(data, graph);
//	        chartData.setCols(chartCols);
	        
	        final ArrayList<ArrayList<ChartV>> chartrows=new ArrayList<ArrayList<ChartV>>();
	        for (ChartRow row:data){
	        	ArrayList<ChartV> list=new ArrayList<ChartV>();
	        	list.add(new ChartV(row.getTime()));
	        	for (Float value:row.getData()){
	        		list.add(new ChartV(value));
	        	}
//	        	ChartC c=new ChartC();
//	        	c.setC(list);
//	        	chartrows.add(c);
	        	chartrows.add(list);
	        }
	        return chartrows;
//	        chartData.setRows(chartrows);

//	        return chartData;
//		}

//		return chartData;
    }

	protected ChartData metadata(final List<ChartRow> data, final Graph graph, final ChartData chart){
		ArrayList<Float> maxValues=new ArrayList<Float>();
		ArrayList<Float> minValues=new ArrayList<Float>();
		
		for (Float value:data.get(0).getData()){
			maxValues.add(value);
			minValues.add(value);
		}
		
		for (ChartRow row:data){
			for (int i=0;i<row.getData().size();i++){
				Float val=row.getData().get(i);
				if (maxValues.get(i)==null){
					maxValues.set(i, val);
				} else if (val!=null && maxValues.get(i).compareTo(val)>0){
					maxValues.set(i, val);
				}
				if (minValues.get(i)==null){
					minValues.set(i, val);
				} else if (val!=null && minValues.get(i).compareTo(val)<0){
					minValues.set(i, val);
				}
			}
		}
		
		if (log.isDebugEnabled()){
			log.debug("Set scales");
		}
		for (int i=0;i<maxValues.size();i++){
			Series series = graph.getGraphSeries().get(i).getSeries();
			if (log.isDebugEnabled()){
				log.debug("Series "+series.getName()+" "+series.getScaleClass());
			}
			if (series.getScaleClass()!=null){
				AbstractGraphParameters graphParam=graphParameters.get(series.getScaleClass());
				//Time column is the first one, skip it.
				ChartCol col=chart.getCols().get(i+1);
				AbstractGraphParameters.GraphScale scale= graphParam.getScale(minValues.get(i).floatValue(), maxValues.get(i).floatValue());
				col.setScaleMax(scale.getTo());
				col.setScaleMin(scale.getFrom());
				col.setTicks(scale.getTicks());
				col.setGridLinesCount(scale.getGridLinesCount());
				col.setMinorGridLinesCount(scale.getMinorGridLinesCount());
			}
		}

		return chart;
	}
	
	protected long getMaxAge(Graph graph, List<ChartRow> data){
		final String trunc=getSmallestTrunc(graph);
		if (log.isDebugEnabled()){
			log.debug("trunc="+trunc);
		}
		
		final ChartRow row=data.get(data.size()-1);
		final LocalDateTime lastData=LocalDateTime.ofInstant(row.getTime().toInstant(), ZoneId.systemDefault());
		if (log.isDebugEnabled()){
			log.debug("lastData="+lastData);
		}
		
		final Pattern groupByPattern = Pattern.compile("(\\d*) ?(\\D+)"); //numbers as groups  
		final Matcher m=groupByPattern.matcher(trunc);
		if (log.isDebugEnabled()){
			log.debug("match found="+m.matches());
		}
		String amount=m.group(1);
		if ("".equals(amount)){
		  	amount="1";
		}
		final String unit=m.group(2);
		final Duration duration=Duration.of(Long.parseLong(amount), ChronoUnit.valueOf(unit));
		
		final LocalDateTime cacheEnd=lastData.plus(duration);
		if (log.isDebugEnabled()){
			log.debug("cacheEnd="+cacheEnd);
		}
		final Duration maxAge=Duration.between(LocalDateTime.now(), cacheEnd);
		if (log.isDebugEnabled()){
			log.debug("duration="+maxAge+" maxAge="+maxAge.getSeconds());
		}
		return maxAge.getSeconds();
	}
	
	protected ChartData dataToChart(final List<ChartRow> data, final Graph graph) {
		final ChartData chartData=new ChartData();
        final ArrayList<ChartCol> chartCols = createCols(data, graph);
        chartData.setCols(chartCols);
        
        final ArrayList<ChartC> chartrows=new ArrayList<ChartC>();
        for (final ChartRow row:data){
        	final ArrayList<ChartV> list=new ArrayList<ChartV>();
        	list.add(new ChartV(row.getTime()));
        	for (final Float value:row.getData()){
        		list.add(new ChartV(value));
        	}
        	final ChartC c=new ChartC();
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
		String select;
		String from;
//		String where="";
//		String groupBy="";

		//TODO Currently there is trunking of data with long timespans
//		String defaultTrunc=getGroupBy(graph.getFrom(), graph.getTo());
		String defaultTrunc=getSmallestTrunc(graph);
		
		select="SELECT time_series";
		from  ="  FROM generate_series("+getTruncFunction(defaultTrunc, ":from::timestamp")+", "+getTruncFunction(defaultTrunc, ":to::timestamp")+" - INTERVAL '1 minute', '"+(defaultTrunc==null?"1 minute":defaultTrunc)+"') time_series\n";
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
				if ("1 MINUTES".equals(trunc)){
				    from  =from  +"       LEFT JOIN (select time as time, value as value from data where sensorid=:"+i+" and time between :from::timestamp and :to::timestamp) d"+i+" ON time_series = d"+i+".time\n";
				} else {
				    from  =from  +"       LEFT JOIN (select "+getTruncFunction(trunc)+" as time, "+(series.getGroupby()==null?"value":series.getGroupby())+" as value from data where sensorid=:"+i+" and time between "+getTruncFunction(defaultTrunc, ":from::timestamp")+" and "+getTruncFunction(defaultTrunc, ":to::timestamp")+" group by "+getTruncFunction(trunc)+") d"+i+" ON time_series = d"+i+".time\n";
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
					log.debug("GraphSeries("+seriesid+")="+graphSeriesMap.get(Long.valueOf(seriesid)));
					String value=processValueFunction("combineddata"+j+".value",graphSeriesMap.get(Long.valueOf(seriesid)).getValuefunction());
					value="("+value+")";
					valueFunction=valueFunction.replaceAll(m.group(),value );
					if (trunc==null){
					    from  =from  +"       LEFT JOIN (select time, value from data where sensorid=:compound"+j+") combineddata"+j+" ON time_series = combineddata"+j+".time\n";
					} else {
					    from  =from  +"       LEFT JOIN (select date_trunc('"+trunc+"',time) as time, "+graphSeriesMap.get(Long.valueOf(seriesid)).getGroupby()+"(value) as value from data where sensorid=:compound"+j+" group by date_trunc('"+trunc+"',time)) combineddata"+j+" ON time_series = combineddata"+j+".time\n";
					}
					params.put("compound"+j, graphSeriesMap.get(Long.valueOf(seriesid)).getSensorid());
					j++;
				}
				select=select+", "+valueFunction;

			}
		}
		select = select+"\n";
		
//		return select+from+where+groupBy+" ORDER BY 1";
		return select+from+" ORDER BY 1";
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
			    if ("".equals(amount)){
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
			if ("MINUTES".equals(unit)){
				if ("1".equals(amount)){
					function=field;
				} else {
					function="round_minutes("+field+","+amount+")";
				}
			} else if ("HOURS".equals(unit)){
				if ("1".equals(amount)){
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
		String groupBy;
	    long hours=ChronoUnit.HOURS.between(from,to);
	    if (hours>24*30) { //month
	    	groupBy="day";
	    } else if (hours>24*7){ //week
	    	groupBy="hour";
	    } else if (hours>24){ //day
	    	groupBy="hour";
	    } else {
	    	groupBy=null;
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
