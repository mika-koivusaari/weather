package org.koivusaari.weather.scale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.koivusaari.weather.ChartDataController;
import org.koivusaari.weather.DataMapper;
import org.koivusaari.weather.pojo.ChartRow;
import org.koivusaari.weather.pojo.ScaleMinMax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class OutsideTempGraphParameters extends AbstractGraphParameters {

	private static final Logger log = LoggerFactory.getLogger(OutsideTempGraphParameters.class);

	private LinkedHashMap<String,OutsideTempGraphParameters.GraphScale> scales=new LinkedHashMap<String,OutsideTempGraphParameters.GraphScale>();
	private GraphScale previous;
	NamedParameterJdbcTemplate jdbcTemplate;
	
	private static final Long SCALE_ID=Long.valueOf(117);
	private static final Long TEMP_ID=Long.valueOf(111);

	public OutsideTempGraphParameters(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate=jdbcTemplate;

		scales.put("mid",new GraphScale("mid", -15, 15,new float[] {-10,0,10}));
		scales.put("winter",new GraphScale("winter", -30, 5,new float[]{-30,-20,-10,0}));
		scales.put("summer",new GraphScale("summer", -5, 35,new float[]{0,10,20,30}));
		
		generateScales();
		if (previous==null){
			previous=getLastScale();
		}
	}
	
	public void generateScales() {
		log.debug("generateScales");
		String selectLast="select max(time) from datatext where sensorid=:scaleSensorId";
		Map<String,Long> selectLastParams=new HashMap<String,Long>();
		selectLastParams.put("scaleSensorId", SCALE_ID);
		Date lastScale=jdbcTemplate.queryForObject(selectLast,selectLastParams,Date.class);
		log.debug("Last generated daily scale "+lastScale);
		
		String selectMinMaxValues="select date_trunc('day',time) as time, min(value) as min, max(value) as max "+
                                  "from   data "+
                                  "where  sensorid=:tempSensorId "+
                                  "and    (date_trunc('day',time)>:lastDate or :lastDate::timestamp is null) "+
                                  "and    time<current_date "+
                                  "group by date_trunc('day',time) "+
                                  "order by 1";
		Map<String,Object> selectMinMaxValuesParams=new HashMap<String,Object>();
		selectMinMaxValuesParams.put("tempSensorId", TEMP_ID);
	    selectMinMaxValuesParams.put("lastDate", lastScale);
		
		BeanPropertyRowMapper<ScaleMinMax> scaleRowMapper=BeanPropertyRowMapper.newInstance(ScaleMinMax.class);
		//		BeanPropertyRowMapper<ScaleMinMax> scaleRowMapper=new BeanPropertyRowMapper<ScaleMinMax>();
		List<ScaleMinMax> values=jdbcTemplate.query(selectMinMaxValues, selectMinMaxValuesParams,scaleRowMapper);
		log.info("Generating "+values.size()+" scales.");
		String insertScales="INSERT INTO datatext (sensorid,time,text) VALUES (:sensorId,:time,:text)";
		Map<String,Object> insertParams=new HashMap<String,Object>();
		int numberOfInserts=0;
		for (ScaleMinMax scale:values){
			insertParams.clear();
			if (previous!=null && previous.isBetween(scale.getMin(), scale.getMax())){
				insertParams.put("sensorId",SCALE_ID);
				insertParams.put("time",Date.from(scale.getTime().atZone(ZoneId.systemDefault()).toInstant()));
				insertParams.put("text",previous.getName());
			} else {
				for (Map.Entry<String, GraphScale> entry:scales.entrySet()){
				    String key = entry.getKey();
				    GraphScale s = entry.getValue();
					if (s.isBetween(scale.getMin(), scale.getMax())){
						previous=s;
						insertParams.put("sensorId",SCALE_ID);
						insertParams.put("time",Date.from(scale.getTime().atZone(ZoneId.systemDefault()).toInstant()));
						insertParams.put("text",previous.getName());
					}
				}
			}
			if (!insertParams.isEmpty()){
				jdbcTemplate.update(insertScales, insertParams);
				numberOfInserts++;
			}
		}
		log.info("Inserted "+numberOfInserts+" scales.");
	}
	
	protected GraphScale getLastScale() {
		String sql="select text from datatext where sensorid=:sensorId and time=(select max(time) from datatext where sensorid=:sensorId)";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("sensorId",SCALE_ID);
		String lastScale=jdbcTemplate.queryForObject(sql,params,String.class);
		
		return scales.get(lastScale);
	}
	
	public GraphScale getScale(float min, float max){
		GraphScale scale=null;
		if (previous.isBetween(min, max)){
			scale=previous;
		} else {
			for (Map.Entry<String, GraphScale> entry:scales.entrySet()){
			    String key = entry.getKey();
			    GraphScale s = entry.getValue();
				if (s.isBetween(min, max)){
					scale=s;
				}
			}
		}
		if (scale==null){
			scale=new GraphScale("Generated", Math.round(min-2), Math.round(max+2),null);
		}
		return scale;
	}
}
