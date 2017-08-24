package org.koivusaari.weather.scale;

import junit.framework.TestCase;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.koivusaari.weather.pojo.ScaleMinMax;
import org.koivusaari.weather.scale.AbstractGraphParameters.GraphScale;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class OutsideTempGraphParametersTest extends TestCase {

	private OutsideTempGraphParameters graphParam;
	@Mock
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	protected void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    graphParam=createGraphParam();
		super.setUp();
	}
	
	protected OutsideTempGraphParameters createGraphParam(){
		//get last generated
		String selectLast="select max(time) from datatext where sensorid=:scaleSensorId";
		Map<String,Long> selectLastParams=new HashMap<String,Long>();
		selectLastParams.put("scaleSensorId", OutsideTempGraphParameters.SCALE_ID);
		Date lastScale=new Date();
	    when(jdbcTemplate.queryForObject(selectLast,selectLastParams,Date.class)).thenReturn(lastScale);

	    //generate ungenerated 
		String selectMinMaxValues="select date_trunc('day',time) as time, min(value) as min, max(value) as max "+
                "from   data "+
                "where  sensorid=:tempSensorId "+
                "and    (date_trunc('day',time)>:lastDate or :lastDate::timestamp is null) "+
                "and    time<current_date "+
                "group by date_trunc('day',time) "+
                "order by 1";
        Map<String,Object> selectMinMaxValuesParams=new HashMap<String,Object>();
        selectMinMaxValuesParams.put("tempSensorId", OutsideTempGraphParameters.TEMP_ID);
        selectMinMaxValuesParams.put("lastDate", lastScale);

        BeanPropertyRowMapper<ScaleMinMax> scaleRowMapper=BeanPropertyRowMapper.newInstance(ScaleMinMax.class);
        List<ScaleMinMax> values=new ArrayList<ScaleMinMax>(); 
	    when(jdbcTemplate.query(selectMinMaxValues, selectMinMaxValuesParams,scaleRowMapper)).thenReturn(values);

	    //GetLastScale
		String sql="select text from datatext where sensorid=:sensorId and time=(select max(time) from datatext where sensorid=:sensorId)";
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("sensorId",OutsideTempGraphParameters.SCALE_ID);
		when(jdbcTemplate.queryForObject(sql,params,String.class)).thenReturn("mid");

	    graphParam=new OutsideTempGraphParameters(jdbcTemplate);

	    return graphParam;
	}
	
	public void testGetScaleMid(){
		GraphScale scale=graphParam.getScale(-15, 0);
		assertEquals("mid", scale.getName());
	}

	public void testGetScaleMidLastMid(){
		GraphScale scale=graphParam.getScale(-15, 0);
		assertEquals("mid", scale.getName());
		scale=graphParam.getScale(0, 15);
		assertEquals("mid", scale.getName());
	}

	public void testGetScaleMidLastWinter(){
		GraphScale scale=graphParam.getScale(-20, 0);
		assertEquals("winter", scale.getName());
		scale=graphParam.getScale(0, 15);
		assertEquals("mid", scale.getName());
	}

	public void testGetScaleWinter(){
		GraphScale scale=graphParam.getScale(-20, 0);
		assertEquals("winter", scale.getName());
	}

	public void testGetScaleWinterlastWinter(){
		GraphScale scale=graphParam.getScale(-20, 0);
		assertEquals("winter", scale.getName());
		scale=graphParam.getScale(-5, 0);
		assertEquals("winter", scale.getName());
	}

	public void testGetScaleWinterlastMid(){
		GraphScale scale=graphParam.getScale(-2, 0);
		assertEquals("mid", scale.getName());
		scale=graphParam.getScale(-20, 0);
		assertEquals("winter", scale.getName());
	}

	public void testGetScaleSummer(){
		GraphScale scale=graphParam.getScale(0, 20);
		assertEquals("summer", scale.getName());
	}
	
	public void testGetScaleSummerLastSummer(){
		GraphScale scale=graphParam.getScale(0, 20);
		assertEquals("summer", scale.getName());
		scale=graphParam.getScale(0, 2);
		assertEquals("summer", scale.getName());
	}

	public void testGetScaleSummerLastMid(){
		GraphScale scale=graphParam.getScale(0, 2);
		assertEquals("mid", scale.getName());
		scale=graphParam.getScale(0, 20);
		assertEquals("summer", scale.getName());
	}

	public void testGetScaleGeneratedCold(){
		GraphScale scale=graphParam.getScale(-35, -20);
		assertEquals("Generated", scale.getName());
	}

	public void testGetScaleGeneratedHot(){
		GraphScale scale=graphParam.getScale(20, 40);
		assertEquals("Generated", scale.getName());
	}

}
