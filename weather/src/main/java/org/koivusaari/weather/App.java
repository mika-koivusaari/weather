package org.koivusaari.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
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

}
