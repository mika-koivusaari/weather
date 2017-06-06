package org.koivusaari.weather;

import junit.framework.TestCase;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ChartDataControllerTest extends TestCase {

	@Mock
	private NamedParameterJdbcTemplate jdbcTemplate;
	@Mock
	private SensorRepository sensorRepository;
	@Mock
	private GraphDataSeriesRepository graphDataSeriesRepository;
	@Mock
	GraphRepository graphRepository;
	@Mock
	Graph graph;
	private ChartDataController cdController;
	
	private static final Logger log = LoggerFactory.getLogger(ChartDataControllerTest.class);

	public void setUp(){
	    MockitoAnnotations.initMocks(this);
	    fillGraphDataSeriesRepositoryMock(5,2);
		cdController=new ChartDataController(jdbcTemplate
				                            ,sensorRepository
				                            ,graphRepository
				                            ,graphDataSeriesRepository);
	}

//	public void testIndex() {
//		fail("Not yet implemented");
//	}
//
	public void testDataToChart() {
		ArrayList<Long> ids=new ArrayList<Long>();
		ids.add(new Long(1));
		ids.add(new Long(2));
		ids.add(new Long(3));

		List<ChartRow> data=new ArrayList<ChartRow>();
		GregorianCalendar cal = new GregorianCalendar();
		//row 1
		ChartRow row=new ChartRow();
		cal.set(2017, 0, 1, 12, 30);
		row.setTime(cal.getTime());
		ArrayList<Float> rowData=new ArrayList<Float>();
		rowData.add(new Float(10.1));
		rowData.add(new Float(20.1));
		rowData.add(new Float(30.1));
		row.setData(rowData);
		data.add(row);
		//row 2
		row=new ChartRow();
		cal.set(2017, 0, 1, 13, 30);
		row.setTime(cal.getTime());
		rowData=new ArrayList<Float>();
		rowData.add(new Float(100.1));
		rowData.add(new Float(200.1));
		rowData.add(new Float(300.1));
		row.setData(rowData);
		data.add(row);
		
        for (int i=1;i<=data.get(0).getData().size();i++){
        	Sensors sensor=new Sensors();
        	sensor.setUnitId(new Long(i));
		    when(sensorRepository.findOne(ids.get(i-1))).thenReturn(sensor);
        }

		ArrayList<GraphSeries> gsList=new ArrayList<GraphSeries>();
        for (int i=1;i<=data.get(0).getData().size();i++){
			GraphSeries gs=new GraphSeries();
			Series s=mock(Series.class);
			when(s.getSensorid()).thenReturn(new Long(i));
			gs.setSeries(s);
			gsList.add(gs);

        	Sensors sensor=new Sensors();
        	sensor.setUnitId(new Long(i));
        	sensor.setName("gen name "+i);
		    when(sensorRepository.findOne(ids.get(i-1))).thenReturn(sensor);
        }
		when(graph.getGraphSeries()).thenReturn(gsList);

		ChartData chartData=cdController.dataToChart(data, graph);

        ArrayList<ChartC> chartrows=chartData.getRows();
        assertEquals(2, chartrows.size()); //expect 2 rows
		for (ChartC c:chartrows){
        	ArrayList<ChartV> vlist=c.getC();
        	assertEquals(4, vlist.size()); //expect 3+1 colums, time + 3 data
//TODO add data checks
//        	for (ChartV v:vlist){
//        		System.out.print(v.getV().toString()+", ");
//        	}
//        	System.out.println("");
		}
	}
	
	public void testCreateCols(){
		ArrayList<Long> ids=new ArrayList<Long>();
		ids.add(new Long(1));
		ids.add(new Long(2));
		ids.add(new Long(3));

		List<ChartRow> data=new ArrayList<ChartRow>();
		GregorianCalendar cal = new GregorianCalendar();
		//row 1
		ChartRow row=new ChartRow();
		cal.set(2017, 0, 1, 12, 30);
		row.setTime(cal.getTime());
		ArrayList<Float> rowData=new ArrayList<Float>();
		rowData.add(new Float(10.1));
		rowData.add(new Float(20.1));
		rowData.add(new Float(30.1));
		row.setData(rowData);
		data.add(row);
		//row 2
		row=new ChartRow();
		cal.set(2017, 0, 1, 13, 30);
		row.setTime(cal.getTime());
		rowData=new ArrayList<Float>();
		rowData.add(new Float(100.1));
		rowData.add(new Float(200.1));
		rowData.add(new Float(300.1));
		row.setData(rowData);
		data.add(row);

		ArrayList<GraphSeries> gsList=new ArrayList<GraphSeries>();
        for (int i=1;i<=data.get(0).getData().size();i++){
			GraphSeries gs=new GraphSeries();
			Series s=mock(Series.class);
			when(s.getSensorid()).thenReturn(new Long(i));
			gs.setSeries(s);
			gsList.add(gs);

        	Sensors sensor=new Sensors();
        	sensor.setUnitId(new Long(i));
        	sensor.setName("gen name "+i);
		    when(sensorRepository.findOne(ids.get(i-1))).thenReturn(sensor);
        }
		when(graph.getGraphSeries()).thenReturn(gsList);
		List<ChartCol> cols=cdController.createCols(data, graph);
		assertEquals(4, cols.size());
	}

	public void testGetSmallestTruncValues(){
		ArrayList<GraphSeries> gsList=new ArrayList<GraphSeries>();

		GraphSeries gs=new GraphSeries();
		Series s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn("1 HOURS");
		gs.setSeries(s);
		gsList.add(gs);

		gs=new GraphSeries();
		s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn("1 MINUTES");
		gs.setSeries(s);
		gsList.add(gs);

		gs=new GraphSeries();
		s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn("10 MINUTES");
		gs.setSeries(s);
		gsList.add(gs);

		when(graph.getGraphSeries()).thenReturn(gsList);

		assertEquals(null, "1 MINUTES", cdController.getSmallestTrunc(graph));
	}

	public void testGetSmallestTruncNull(){
		ArrayList<GraphSeries> gsList=new ArrayList<GraphSeries>();

		GraphSeries gs=new GraphSeries();
		Series s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn("1 HOURS");
		gs.setSeries(s);
		gsList.add(gs);

		gs=new GraphSeries();
		s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn(null);
		gs.setSeries(s);
		gsList.add(gs);

		gs=new GraphSeries();
		s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn("10 MINUTES");
		gs.setSeries(s);
		gsList.add(gs);

		when(graph.getGraphSeries()).thenReturn(gsList);

		assertEquals(null, "1 MINUTES", cdController.getSmallestTrunc(graph));
	}

	public void testGetTruncFunctionNone(){
		String function=cdController.getTruncFunction(null,"time");
		assertEquals("time", function);
	}

	public void testGetTruncFunction1Hours(){
		String function=cdController.getTruncFunction("1 HOURS","time");
		assertEquals("date_trunc('HOUR',time)", function);
	}

	public void testGetTruncFunction3Hours(){
		String function=cdController.getTruncFunction("3 HOURS","time");
		assertEquals("round_hours(time,3)", function);
	}

	public void testGetTruncFunction1Minutes(){
		String function=cdController.getTruncFunction("1 MINUTES","time");
		assertEquals("time", function);
	}

	public void testGetTruncFunction10Minutes(){
		String function=cdController.getTruncFunction("10 MINUTES","time");
		assertEquals("round_minutes(time,10)", function);
	}
	
	public void testCreateSelectDay() {
		ArrayList<GraphSeries> gsList=new ArrayList<GraphSeries>();

		GraphSeries gs=new GraphSeries();
		Series s=mock(Series.class);
//		when(s.getMinGroupByTime()).thenReturn("1 HOURS");
		when(s.getSensorid()).thenReturn(new Long(1));
		gs.setSeries(s);
		gsList.add(gs);

		gs=new GraphSeries();
		s=mock(Series.class);
//		when(s.getMinGroupByTime()).thenReturn("1 MINUTES");
		when(s.getSensorid()).thenReturn(new Long(2));
		gs.setSeries(s);
		gsList.add(gs);

		gs=new GraphSeries();
		s=mock(Series.class);
//		when(s.getMinGroupByTime()).thenReturn("10 MINUTES");
		when(s.getSensorid()).thenReturn(new Long(3));
		gs.setSeries(s);
		gsList.add(gs);

		when(graph.getGraphSeries()).thenReturn(gsList);
		when(graph.getFrom()).thenReturn(LocalDateTime.of(2017, 1, 1, 0, 0));
		when(graph.getTo()).thenReturn(LocalDateTime.of(2017, 1, 2, 0, 0));
		
		HashMap<String,Object> params=new HashMap<String,Object>();

		String select=cdController.createSelect(graph,params);

		log.debug(select);
		log.debug(params.toString());
		
		String selectExpected="SELECT time_series, d1.value, d2.value, d3.value\n"+
                              "  FROM generate_series(:from::timestamp, :to::timestamp, '1 MINUTES') time_series\n"+
                              "       LEFT JOIN (select time as time, value as value from data where sensorid=:1 and time between :from::timestamp and :to::timestamp) d1 ON time_series = d1.time\n"+
                              "       LEFT JOIN (select time as time, value as value from data where sensorid=:2 and time between :from::timestamp and :to::timestamp) d2 ON time_series = d2.time\n"+
                              "       LEFT JOIN (select time as time, value as value from data where sensorid=:3 and time between :from::timestamp and :to::timestamp) d3 ON time_series = d3.time\n"+
                              " ORDER BY 1";
		assertEquals(selectExpected, select);
	}

	public void testCreateSelectMinGroupBy() {
		ArrayList<GraphSeries> gsList=new ArrayList<GraphSeries>();

		GraphSeries gs=new GraphSeries();
		Series s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn("1 HOURS");
		when(s.getGroupby()).thenReturn("AVG(value)");
		when(s.getSensorid()).thenReturn(new Long(1));
		gs.setSeries(s);
		gsList.add(gs);

		gs=new GraphSeries();
		s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn("1 MINUTES");
		when(s.getSensorid()).thenReturn(new Long(2));
		gs.setSeries(s);
		gsList.add(gs);

		gs=new GraphSeries();
		s=mock(Series.class);
		when(s.getMinGroupByTime()).thenReturn("10 MINUTES");
		when(s.getGroupby()).thenReturn("SUM(value)");
		when(s.getSensorid()).thenReturn(new Long(3));
		gs.setSeries(s);
		gsList.add(gs);

		when(graph.getGraphSeries()).thenReturn(gsList);
		when(graph.getFrom()).thenReturn(LocalDateTime.of(2017, 1, 1, 0, 0));
		when(graph.getTo()).thenReturn(LocalDateTime.of(2017, 1, 2, 0, 0));
		
		HashMap<String,Object> params=new HashMap<String,Object>();

		String select=cdController.createSelect(graph,params);

		log.debug(select);
		log.debug(params.toString());
		
		String selectExpected="SELECT time_series, d1.value, d2.value, d3.value\n"+
                              "  FROM generate_series(:from::timestamp, :to::timestamp, '1 MINUTES') time_series\n"+
                              "       LEFT JOIN (select date_trunc('HOUR',time) as time, AVG(value) as value from data where sensorid=:1 and time between :from::timestamp and :to::timestamp group by date_trunc('HOUR',time)) d1 ON time_series = d1.time\n"+
                              "       LEFT JOIN (select time as time, value as value from data where sensorid=:2 and time between :from::timestamp and :to::timestamp) d2 ON time_series = d2.time\n"+
                              "       LEFT JOIN (select round_minutes(time,10) as time, SUM(value) as value from data where sensorid=:3 and time between :from::timestamp and :to::timestamp group by round_minutes(time,10)) d3 ON time_series = d3.time\n"+
                              " ORDER BY 1";
		assertEquals(selectExpected, select);
	}

//	public void testCreateSelectWeek() {
//		ArrayList<GraphSeries> gsList=new ArrayList<GraphSeries>();
//
//		GraphSeries gs=new GraphSeries();
//		Series s=mock(Series.class);
////		when(s.getMinGroupByTime()).thenReturn("1 HOURS");
//		when(s.getSensorid()).thenReturn(new Long(1));
//		gs.setSeries(s);
//		gsList.add(gs);
//
//		gs=new GraphSeries();
//		s=mock(Series.class);
////		when(s.getMinGroupByTime()).thenReturn("1 MINUTES");
//		when(s.getSensorid()).thenReturn(new Long(2));
//		gs.setSeries(s);
//		gsList.add(gs);
//
//		gs=new GraphSeries();
//		s=mock(Series.class);
////		when(s.getMinGroupByTime()).thenReturn("10 MINUTES");
//		when(s.getSensorid()).thenReturn(new Long(3));
//		gs.setSeries(s);
//		gsList.add(gs);
//
//		when(graph.getGraphSeries()).thenReturn(gsList);
//		when(graph.getFrom()).thenReturn(LocalDateTime.of(2017, 1, 1, 0, 0));
//		when(graph.getTo()).thenReturn(LocalDateTime.of(2017, 1, 30, 0, 0));
//		
//		HashMap<String,Object> params=new HashMap<String,Object>();
//
//		String select=cdController.createSelect(graph,params);
//
//		log.debug(select);
//		log.debug(params.toString());
//		
//		String selectExpected="SELECT time_series, d1.value, d2.value, d3.value\n"+
//                              "  FROM generate_series(:from::timestamp, :to::timestamp, '1 MINUTES') time_series\n"+
//                              "       LEFT JOIN (select time as time, value as value from data where sensorid=:1 and time between :from::timestamp and :to::timestamp) d1 ON time_series = d1.time\n"+
//                              "       LEFT JOIN (select time as time, value as value from data where sensorid=:2 and time between :from::timestamp and :to::timestamp) d2 ON time_series = d2.time\n"+
//                              "       LEFT JOIN (select time as time, value as value from data where sensorid=:3 and time between :from::timestamp and :to::timestamp) d3 ON time_series = d3.time\n"+
//                              " ORDER BY 1";
//		assertEquals(selectExpected, select);
//	}

//	public void testCreateSelectWeek() {
//		ArrayList<Long> ids=new ArrayList<Long>();
//		ids.add(new Long(1));
//		ids.add(new Long(2));
//		ids.add(new Long(3));
//
////		GregorianCalendar cal = new GregorianCalendar();
////		cal.set(2017, 0, 1, 0, 0);
////		Date from=cal.getTime();
////		cal.set(2017, 0, 8, 23, 0);
////		Date to=cal.getTime();
//		LocalDate from=LocalDate.of(2017, 1, 1);
//		LocalDate to=LocalDate.of(2017, 1, 9);
//		HashMap<String,Object> params=new HashMap<String,Object>();
//
//		String select=cdController.createSelect(ids,params,from,to);
//
//		String selectExpected="SELECT time_series, d1.value, d2.value, d3.value\n"+
//                              "  FROM generate_series(:from::timestamp, :to::timestamp, '1 hour') time_series\n"+
//                              "       LEFT JOIN (select date_trunc('hour',time) as time ,avg(value) as value from data where sensorid=:1 group by date_trunc('hour',time)) d1 ON time_series = d1.time\n"+
//                              "       LEFT JOIN (select date_trunc('hour',time) as time ,avg(value) as value from data where sensorid=:2 group by date_trunc('hour',time)) d2 ON time_series = d2.time\n"+
//                              "       LEFT JOIN (select date_trunc('hour',time) as time ,avg(value) as value from data where sensorid=:3 group by date_trunc('hour',time)) d3 ON time_series = d3.time\n"+
//                              " ORDER BY 1";
//
//		assertEquals(selectExpected, select);
//	}

//	public void testCreateSelectCompound() {
//		ArrayList<Long> ids=new ArrayList<Long>();
//		ids.add(new Long(1));
//		ids.add(new Long(2));
//		ids.add(new Long(6));
//
////		GregorianCalendar cal = new GregorianCalendar();
////		cal.set(2017, 0, 1, 0, 0);
////		Date from=cal.getTime();
////		cal.set(2017, 0, 1, 23, 59);
////		Date to=cal.getTime();
//		LocalDate from=LocalDate.of(2017, 1, 1);
//		LocalDate to=LocalDate.of(2017, 1, 2);
//		HashMap<String,Object> params=new HashMap<String,Object>();
//
//		String select=cdController.createSelect(ids,params,from,to);
//
//		String selectExpected="SELECT time_series, d1.value, d2.value, (combineddata1.value)-(combineddata2.value)\n"+
//                              "  FROM generate_series(:from::timestamp, :to::timestamp, '1 minute') time_series\n"+
//                              "       LEFT JOIN (select sensorid,time,value from data where sensorid=:1) d1 ON time_series = d1.time\n"+
//                              "       LEFT JOIN (select sensorid,time,value from data where sensorid=:2) d2 ON time_series = d2.time\n"+
//                              "       LEFT JOIN (select sensorid,time,value from data where sensorid=:compound1) combineddata1 ON time_series = combineddata1.time\n"+
//                              "       LEFT JOIN (select sensorid,time,value from data where sensorid=:compound2) combineddata2 ON time_series = combineddata2.time\n"+
//                              " ORDER BY 1";
//
//		assertEquals(selectExpected, select);
//	}

	protected void fillGraphDataSeriesRepositoryMock(int series,int compoundSeries){
		ArrayList<GraphDataSeries> dataSeriesList=new ArrayList<GraphDataSeries>(); 
        for (int i=1;i<=series;i++){
        	GraphDataSeries s=new GraphDataSeries();
        	s.setSeriesid(new Long(i));
        	s.setSensorid(new Long(i));
        	s.setDescription("Mocked series "+i);
        	s.setGroupby("avg");
        	dataSeriesList.add(s);
        }
        for (int i=series+1;i<=(series+compoundSeries);i++){
        	GraphDataSeries s=new GraphDataSeries();
        	s.setSeriesid(new Long(i));
        	s.setSensorid(null);
        	s.setValuefunction(":1-:2");
        	s.setDescription("Mocked series "+i);
        	s.setGroupby("avg");
        	dataSeriesList.add(s);
        }
	    when(graphDataSeriesRepository.findAll()).thenReturn(dataSeriesList);
	}
}
