package org.koivusaari.weather;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.koivusaari.weather.pojo.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class WeatherDataExtractor implements ResultSetExtractor<WeatherData> {

	private static final Logger log = LoggerFactory.getLogger(WeatherDataExtractor.class);
	private WeatherData weatherData;
	
	public WeatherData extractData(ResultSet rs) throws SQLException, DataAccessException {
		if (weatherData==null) {
			weatherData=new WeatherData();
		}
		ResultSetMetaData rsmt=rs.getMetaData();
		boolean next=rs.next();
		log.debug("Next call: "+next);
		if (!next) {
			return null;
		}
		for (int i=1;i<=rsmt.getColumnCount();i++){
			Object value=rs.getObject(i);
			if (value!=null){
				if (value instanceof Date) {
					Instant instant = Instant.ofEpochMilli(((Date)value).getTime());
	    			value = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
				} else if (value instanceof Number){
					value=new Float(((Number)value).floatValue());
				}
				try {
					Method m=weatherData.getClass().getMethod(generateMethodName(rsmt.getColumnLabel(i)), new Class[]{value.getClass()});
					m.invoke(weatherData, value);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return weatherData;
	}
	
	protected String generateMethodName(String columname){
		StringBuffer sb=new StringBuffer();
		sb.append("set");
		boolean upper=true;
		for (int i=0;i<columname.length();i++){
			if (columname.substring(i, i+1).compareTo("_")==0){
				upper=true;
			} else{
				if (upper){
					sb.append(columname.substring(i, i+1).toUpperCase());
					upper=false;
				} else{
					sb.append(columname.substring(i, i+1).toLowerCase());
				}
			}
		}
		
		return sb.toString();
	}
	

}
