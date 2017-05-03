package org.koivusaari.weather;

import org.koivusaari.weather.pojo.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.templateresolver.TemplateResolver;

@Controller
public class WeatherController {

	@Autowired
	private TemplateResolver templateResolver;
	@Value("${google.analytics.id:#{null}}")
	private String analId;
	
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
        model.addAttribute("analId", analId);

        templateResolver.initialize();
        log.debug(templateResolver.toString());
        try {
            log.debug("resolver prefix: "+templateResolver.getPrefix());
          log.debug("resolver suffix: "+templateResolver.getSuffix());
        } catch (org.thymeleaf.exceptions.NotInitializedException e){
          log.debug("resolvel not init");
        }
        return "weather";
    }

}
