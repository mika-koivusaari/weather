package org.koivusaari.weather;

import java.util.Map;

import org.koivusaari.weather.scale.AbstractGraphParameters;
import org.koivusaari.weather.scale.OutsideTempGraphParameters;
import org.koivusaari.weather.scale.WindGraphParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@Configuration
@EnableScheduling
public class App 
{

	private static final Logger log = LoggerFactory.getLogger(App.class);

	public static void main( String[] args )
    {
		SpringApplication.run(App.class, args);
    }

	@Bean
	public OutsideTempGraphParameters outsideTempGraphParameters(NamedParameterJdbcTemplate jdbcTemplate) {
		return new OutsideTempGraphParameters(jdbcTemplate);
	}
	@Bean
	public WindGraphParameters windGraphParameters() {
		return new WindGraphParameters();
	}
	
	@Bean
	public Map<String,AbstractGraphParameters> graphParameters(ApplicationContext appContext){
		Map<String,AbstractGraphParameters> graphParameters=appContext.getBeansOfType(AbstractGraphParameters.class);
		if (log.isDebugEnabled()){
			for (String key:graphParameters.keySet()){
				log.debug(key+" "+graphParameters.get(key).toString());
			}
		}
		return graphParameters;
	}

}
