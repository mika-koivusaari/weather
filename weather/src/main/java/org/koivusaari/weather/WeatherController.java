package org.koivusaari.weather;

import java.util.List;

import org.koivusaari.weather.pojo.Message;
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

	@Value("${google.analytics.id:#{null}}")
	private String analId;
	@Value("${weather.site:#{null}}")
	private String site;
	
	private WeatherRepository weatherRepository;
	private MessageRepository messageRepository;
	private static final Logger log = LoggerFactory.getLogger(WeatherController.class);
	
	public WeatherController(WeatherRepository weatherRepository
			                ,MessageRepository messageRepository) {
		super();
		this.weatherRepository = weatherRepository;
		this.messageRepository = messageRepository;
	}

	@RequestMapping("/weather")
    public String createGraph(Model model) {
		WeatherData weatherData=weatherRepository.findLastData();
		log.debug("WeatherData: "+weatherData);
		List<Message> messages=messageRepository.findCurrentMessages();
		if (weatherData==null){
			Message m=new Message();
			m.setAuthor("WeatherController");
			m.setMessage("Dataa ei löydy");
			messages.add(m);
		} else if (weatherData.getTemperature()==null||
				   weatherData.getAirPressure()==null||
				   weatherData.getRain10()==null||
				   weatherData.getWindSpeed()==null){
			Message m=new Message();
			m.setAuthor("WeatherController");
			m.setMessage("Data puutteellista");
			messages.add(m);
		}
		
        model.addAttribute("weatherdata", weatherData);
        model.addAttribute("analId", analId);
        model.addAttribute("site", site);
        model.addAttribute("messages",messages);

        return "weather";
    }

}
