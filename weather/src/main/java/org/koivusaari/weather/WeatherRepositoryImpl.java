package org.koivusaari.weather;

import org.koivusaari.weather.pojo.StationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherRepositoryImpl implements WeatherRepository {

	private static final Logger log = LoggerFactory.getLogger(WeatherRepositoryImpl.class);

	private final String selectBasic = "SELECT temperature.time, temperature.value temperature,air_pressure.value air_pressure\n"+
                           "FROM   data temperature\n"+
                           "LEFT JOIN data as air_pressure ON temperature.time=air_pressure.time and air_pressure.sensorid=104\n"+
                           "WHERE  temperature.sensorid=111\n"+
                           "AND    temperature.time>(current_timestamp-interval '2 minute')\n"+
                           "ORDER BY 1 DESC";
	
	NamedParameterJdbcTemplate jdbcTemplate;

	public WeatherRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

	public StationData findLastData() {
		StationDataExtractor rse=new StationDataExtractor();
		StationData sd=jdbcTemplate.query(selectBasic, rse);
		return sd;
	}

}
