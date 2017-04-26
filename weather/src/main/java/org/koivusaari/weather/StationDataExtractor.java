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

import org.koivusaari.weather.pojo.StationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class StationDataExtractor implements ResultSetExtractor<StationData> {

	private static final Logger log = LoggerFactory.getLogger(StationDataExtractor.class);
	private StationData stationData;
	
	public StationData extractData(ResultSet rs) throws SQLException, DataAccessException {
		if (stationData==null) {
			stationData=new StationData();
		}
		ResultSetMetaData rsmt=rs.getMetaData();
		log.debug("Next call: "+rs.next());
		for (int i=1;i<=rsmt.getColumnCount();i++){
			Object value=rs.getObject(i);
			if (value instanceof Date) {
				Instant instant = Instant.ofEpochMilli(((Date)value).getTime());
    			value = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			} else if (value instanceof Number){
				value=new Float(((Number)value).floatValue());
			}
			try {
				Method m=stationData.getClass().getMethod(generateMethodName(rsmt.getColumnLabel(i)), new Class[]{value.getClass()});
				m.invoke(stationData, value);
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
		
		return stationData;
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
