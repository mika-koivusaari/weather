package org.koivusaari.weather;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.koivusaari.weather.pojo.Message;
import org.koivusaari.weather.pojo.WeatherData;
import org.koivusaari.weather.pojo.WpPost;
import org.koivusaari.weather.repositories.MessageRepository;
import org.koivusaari.weather.repositories.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class WeatherController {

	@Value("${google.analytics.id:#{null}}")
	private String analId;
	@Value("${weather.site:#{null}}")
	private String site;
	
	private String version;

	private WPIntegration wpIntegration;
	private WeatherRepository weatherRepository;
	private MessageRepository messageRepository;
	private static final Logger log = LoggerFactory.getLogger(WeatherController.class);
	
	public WeatherController(WeatherRepository weatherRepository
			                ,MessageRepository messageRepository
			                ,WPIntegration wpIntegration) {
		super();
		this.weatherRepository = weatherRepository;
		this.messageRepository = messageRepository;
		this.wpIntegration = wpIntegration;
	}

	@RequestMapping("/weather")
    public String createGraph(Model model, HttpServletResponse response) {
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
		
        model.addAttribute("posts", wpIntegration.getLastPosts());

        model.addAttribute("weatherdata", weatherData);
        model.addAttribute("analId", analId);
        model.addAttribute("site", site);
        model.addAttribute("messages",messages);

        setVersion(model);
        

        response.setHeader("Cache-Control","max-age=60");

        return "weather";
    }

	@RequestMapping("/weatherdata")
    public @ResponseBody WeatherData getCurretnData(HttpServletResponse response) {
		WeatherData weatherData=weatherRepository.findLastData();
		log.debug("WeatherData: "+weatherData);

        response.setHeader("Cache-Control","max-age=60");

        return weatherData;
    }

	@RequestMapping("/lastnews")
    public @ResponseBody ArrayList<WpPost> getLastNews(HttpServletResponse response) {
		ArrayList<WpPost> posts=wpIntegration.getLastPosts();
		log.debug("Last news: "+posts);

//        response.setHeader("Cache-Control","max-age=60");

        return posts;
    }

	protected void setVersion(Model model) {
		version=getClass().getPackage().getImplementationVersion();
        if (version==null) {
        	version="***";
        }
        model.addAttribute("version", version);
	}

	@RequestMapping("/currentmessages")
    public @ResponseBody List<Message> currentMessages(Model model, HttpServletResponse response) {

		List<Message> messages=messageRepository.findCurrentMessages();

        response.setHeader("Cache-Control","max-age=60");

        return messages;
	}

	@RequestMapping("/messages")
    public String messages(Model model, HttpServletResponse response) {

		Iterable<Message> messages=messageRepository.findAllByOrderByFromDesc();

        model.addAttribute("analId", analId);
        model.addAttribute("site", site);
        model.addAttribute("messages",messages);

        model.addAttribute("posts", wpIntegration.getLastPosts());

        setVersion(model);

        response.setHeader("Cache-Control","max-age=60");

        return "messages";
	}
}
