package org.koivusaari.weather;

import org.koivusaari.weather.pojo.WeatherData;
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
    private final String selectRain10 = "SELECT (MAX(value)-MIN(value))*0.254 rain10\n"+
    							         "FROM   DATA\n"+
                                         "WHERE  sensorid=105\n"+
                                         "AND    time>(current_timestamp-interval '10 minute')";	
    private final String selectRain60 = "SELECT (MAX(value)-MIN(value))*0.254 rain60\n"+
	         "FROM   DATA\n"+
           "WHERE  sensorid=105\n"+
           "AND    time>(current_timestamp-interval '10 minute')";	
    private final String selectRainToday = "SELECT (MAX(value)-MIN(value))*0.254 rain_today\n"+
	         "FROM   DATA\n"+
           "WHERE  sensorid=105\n"+
           "AND    time>current_date";	
    private final String selectWindSpeed = "SELECT 0.44704*(1.25*(MAX(value)-MIN(value))/600) wind_speed\n"+
                         	         "FROM   DATA\n"+
                                     "WHERE  sensorid=106\n"+
                                     "AND    time>(current_timestamp-interval '10 minute')";	
    private final String selectWindDir = "SELECT round(avg(hb_ads_wind_vane_voltage_to_dir(value))) wind_direction\n"+
                                         "FROM   DATA\n"+
                                         "WHERE  sensorid=107\n"+
                                         "AND    time>(current_timestamp-interval '10 minute')";	
	NamedParameterJdbcTemplate jdbcTemplate;

	public WeatherRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

	public WeatherData findLastData() {
		log.debug("getLastData");
		WeatherDataExtractor rse=new WeatherDataExtractor();
		log.debug("basic");
		WeatherData sd=jdbcTemplate.query(selectBasic, rse);
		log.debug("rain10");
		sd=jdbcTemplate.query(selectRain10, rse);
		log.debug("rain60");
		sd=jdbcTemplate.query(selectRain60, rse);
		log.debug("rainToday");
		sd=jdbcTemplate.query(selectRainToday, rse);
		log.debug("wind speed");
		sd=jdbcTemplate.query(selectWindSpeed, rse);
		log.debug("wind direction");
		sd=jdbcTemplate.query(selectWindDir, rse);
		return sd;
	}

}
