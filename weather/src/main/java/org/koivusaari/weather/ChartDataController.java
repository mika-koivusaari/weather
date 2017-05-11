package org.koivusaari.weather;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.koivusaari.weather.DataMapper;
import org.koivusaari.weather.SensorRepository;
import org.koivusaari.weather.pojo.ChartRow;
import org.koivusaari.weather.pojo.Graph;
import org.koivusaari.weather.pojo.GraphDataSeries;
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

	private static final Logger log = LoggerFactory.getLogger(ChartDataController.class);

	NamedParameterJdbcTemplate jdbcTemplate;
	SensorRepository sensorRepository;
	GraphDataSeriesRepository graphDataSeriesRepository;
	GraphRepository graphRepository;
	HashMap<Long,GraphDataSeries> dataSeriesMap=new HashMap<Long,GraphDataSeries>();
	HashMap<Long,GraphDataSeries> graphSeriesMap=new HashMap<Long,GraphDataSeries>();
	
	public ChartDataController(NamedParameterJdbcTemplate jdbcTemplate, SensorRepository sensorRepository,GraphRepository graphRepository, GraphDataSeriesRepository graphDataSeriesRepository) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.sensorRepository = sensorRepository;
		this.graphRepository = graphRepository;
		this.graphDataSeriesRepository = graphDataSeriesRepository;
//		fillDataSeriesMap();
	}

	@CrossOrigin
	@RequestMapping("/chartdata")
    public Object index(@RequestParam(value="id") String graphId) {
    	        
		log.info("Params:");
		log.info("graphId: "+graphId);

		Graph graph=graphRepository.findOne(Long.parseLong(graphId));

//		ArrayList<Long> idaList=new ArrayList<Long>();
//		for (int i=0;i<ida.length;i++){
//			idaList.add(new Long(ida[i]));
//		}
//		ArrayList<Long> idbList=new ArrayList<Long>();
//		if (idb!=null){
//			for (int i=0;i<idb.length;i++){
//				idbList.add(new Long(idb[i]));
//			}
//		}
//		log.debug("debug");
		log.info("Querying data for today:");
		HashMap<String,Object> params=new HashMap<String,Object>();
		String sql=createSelect(graph,params);
		log.info("select:\n"+sql);
//		HashMap<String,Object> params=new HashMap<String,Object>();
//		for (int i=0;i<idList.size();i++){
//			if (dataSeriesMap.get(idList.get(i)).getSensorid()!=null){
//				params.put(Integer.toString(i+1), dataSeriesMap.get(idList.get(i)).getSensorid());
//			}
//		}
		if (graph.getFrom()!=null || graph.getTo()!=null){
			if (graph.getDynamic()==null||graph.getDynamic()==Graph.STATIC_TIME) {
				params.put("from", Date.from(graph.getFrom().atZone(ZoneId.systemDefault()).toInstant()));
				params.put("to", Date.from(graph.getTo().atZone(ZoneId.systemDefault()).toInstant()));
			} else {
				Duration p=Duration.between(graph.getFrom(), graph.getTo());
//				LocalDate to=LocalDate.now().plusDays(1);
				LocalDateTime to=LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
				LocalDateTime from=to.minus(p);
				params.put("from", Date.from(from.atZone(ZoneId.systemDefault()).toInstant()));
				params.put("to", Date.from(to.atZone(ZoneId.systemDefault()).toInstant()));
//				params.put("from", from);
//				params.put("to", to);
				log.info("Dynamic true.");
				log.info("period: "+p);
				log.info("fromDate: "+params.get("from"));
				log.info("toDate: "+params.get("to"));
			}
		}

		log.debug("Params: "+params);
		List<ChartRow> data=jdbcTemplate.query(sql,params,new DataMapper());
    	ChartData chartData = dataToChart(data,graph);
        return chartData;
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
        for (Series series:graph.getSeries()){
        	i++;
//        for (int i=1;i<=data.get(0).getData().size();i++){
//        	if ( dataSeriesMap.get(idList.get(i-1)).getSensorid()!=null) {
        	if (series.getSensorid()!=null) {
//            	Sensors sensors=sensorRepository.findOne(dataSeriesMap.get(idList.get(i-1)).getSensorid());
            	Sensors sensors=sensorRepository.findOne(series.getSensorid());
	            chartCols.add(new ChartCol("c"+Integer.toString(i),"number",sensors.getName()));
	            chartCols.get(i).setUnit(sensors.getUnitId().toString());
        	} else{
//	            chartCols.add(new ChartCol("c"+Integer.toString(i),"number",dataSeriesMap.get(idList.get(i-1)).getName()));
	            chartCols.add(new ChartCol("c"+Integer.toString(i),"number",series.getName()));
	            chartCols.get(i).setUnit("-1"); //TODO
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

		String trunc=getGroupBy(graph.getFrom(), graph.getTo());
		
		select="SELECT time_series";
		from  ="  FROM generate_series(:from::timestamp, :to::timestamp, '1 "+(trunc==null?"minute":trunc)+"') time_series\n";
//		for (int i=1;i<=graph.getSeries().size();i++){
		int i=0;
    	for (Series series:graph.getSeries()){
    		i++;
			if (series.getSensorid()!=null){
				String valueFunction=series.getValuefunction();
	            String value="d"+i+".value";
				select=select+", "+processValueFunction(value, valueFunction);
				if (trunc==null){
				    from  =from  +"       LEFT JOIN (select sensorid,time,value from data where sensorid=:"+i+") d"+i+" ON time_series = d"+i+".time\n";
				} else {
				    from  =from  +"       LEFT JOIN (select date_trunc('"+trunc+"',time) as time ,"+series.getGroupby()+"(value) as value from data where sensorid=:"+i+" group by date_trunc('"+trunc+"',time)) d"+i+" ON time_series = d"+i+".time\n";
				}
				params.put(Integer.toString(i), series.getSensorid());
			} else {
				String valueFunction=series.getValuefunction();
				log.debug("Combined valuefunction: "+valueFunction);
				Matcher m = seriesIdPattern.matcher(valueFunction);
				int j=1;
				while (m.find()){
					log.debug("Value "+m.group(1));
					int seriesid=Integer.parseInt(m.group(1));
					log.debug("GraphSeries("+seriesid+")="+graphSeriesMap.get(new Long(seriesid)));
					String value=processValueFunction("combineddata"+j+".value",graphSeriesMap.get(new Long(seriesid)).getValuefunction());
					value="("+value+")";
					valueFunction=valueFunction.replaceAll(m.group(),value );
					if (trunc==null){
					    from  =from  +"       LEFT JOIN (select sensorid,time,value from data where sensorid=:compound"+j+") combineddata"+j+" ON time_series = combineddata"+j+".time\n";
					} else {
					    from  =from  +"       LEFT JOIN (select date_trunc('"+trunc+"',time) as time ,"+graphSeriesMap.get(new Long(seriesid)).getGroupby()+"(value) as value from data where sensorid=:compound"+j+" group by date_trunc('"+trunc+"',time)) combineddata"+j+" ON time_series = combineddata"+j+".time\n";
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