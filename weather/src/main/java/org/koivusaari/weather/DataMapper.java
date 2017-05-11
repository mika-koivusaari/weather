package org.koivusaari.weather;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.koivusaari.weather.pojo.ChartRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

public class DataMapper implements RowMapper<ChartRow> {

	private static final Logger log = LoggerFactory.getLogger(DataMapper.class);

//	@Override
	public ChartRow mapRow(ResultSet rs, int rowNum) throws SQLException {
		float value;
		
		log.debug(String.format("mapping row %s", rowNum));
		ChartRow row=new ChartRow();
		row.setTime(rs.getTimestamp(1));
		log.debug(String.format("time %s", row.getTime()));
		int count=rs.getMetaData().getColumnCount();
		log.debug(String.format("count %s", count));
		ArrayList<Float> data=new ArrayList<Float>();
		for (int i=2;i<=count;i++){
			value=rs.getFloat(i);
			data.add(rs.wasNull()?null:new Float(value));
		}
		row.setData(data);
		log.debug(String.format("data %s", row.getData()));
		return row;
	}

}
