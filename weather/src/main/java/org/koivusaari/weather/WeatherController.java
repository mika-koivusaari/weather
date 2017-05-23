package org.koivusaari.weather;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletResponse;

import org.koivusaari.weather.pojo.Message;
import org.koivusaari.weather.pojo.WeatherData;
import org.koivusaari.weather.pojo.WpPost;
import org.koivusaari.weather.pojo.wp.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


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
		
		ArrayList<WpPost> postList = getLastPosts();

        model.addAttribute("posts", postList);

        model.addAttribute("weatherdata", weatherData);
        model.addAttribute("analId", analId);
        model.addAttribute("site", site);
        model.addAttribute("messages",messages);

		response.setHeader("Cache-Control","max-age=60");

        return "weather";
    }

	protected ArrayList<WpPost> getLastPosts() {
		ArrayList<WpPost> postList=new ArrayList<WpPost>(); 
		try {
			String url="https://weather.khyron.dy.fi/wp/wp-json/wp/v2/posts?context=view&per_page=3";
			URLConnection connection = new URL(url).openConnection();
			InputStream resp = connection.getInputStream();
	
			String responseBody;
			try (Scanner scanner = new Scanner(resp)) {
			    responseBody = scanner.useDelimiter("\\A").next();
			    log.debug(responseBody);
			}

//			Gson g = new Gson();
			Gson g = new GsonBuilder()
			           .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
			           .create();
			Type collectionType = new TypeToken<Collection<Post>>(){}.getType();
			Collection<Post> posts = g.fromJson(responseBody, collectionType);
			
//			WpPost post = g.fromJson(responseBody, WpPost.class);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			for (Post post:posts){
			    log.debug(post.getTitle().getRendered());
			    log.debug(post.getDate());
			    log.debug(post.getLink());
				LocalDateTime modified = LocalDateTime.parse(post.getDate(), formatter);
			    postList.add(new WpPost(modified,post.getLink(),post.getTitle().getRendered()));
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return postList;
	}

}
