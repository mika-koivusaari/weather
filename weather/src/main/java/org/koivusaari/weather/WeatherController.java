package org.koivusaari.weather;

import org.koivusaari.weather.pojo.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WeatherController {

	private WeatherRepository weatherRepository;
	private static final Logger log = LoggerFactory.getLogger(WeatherController.class);
	
	public WeatherController(WeatherRepository weatherRepository) {
		super();
		this.weatherRepository = weatherRepository;
	}

	@RequestMapping("/weather")
    public String createGraph(Model model) {
		WeatherData weatherData=weatherRepository.findLastData();
		log.debug("WeatherData: "+weatherData);
        model.addAttribute("weatherdata", weatherData);
        return "weather";
    }

}
