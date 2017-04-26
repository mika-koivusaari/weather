package org.koivusaari.weather;

import java.util.Date;

import org.koivusaari.weather.pojo.StationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
		StationData stationData=weatherRepository.findLastData();
		log.debug("StationData: "+stationData);
        model.addAttribute("stationdata", stationData);
        return "weather";
    }

}
