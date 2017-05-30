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
import java.util.Scanner;

import org.koivusaari.weather.pojo.WpPost;
import org.koivusaari.weather.pojo.wp.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Component
public class WPIntegration {
	@Value("${wp.url:#{null}}")
	private String wpUrl;
	@Value("${wp.posts:#{3}}")
	private int wpPosts;

	private static final Logger log = LoggerFactory.getLogger(WPIntegration.class);

	private ArrayList<WpPost> lastPosts;
	
	
	public ArrayList<WpPost> getLastPosts() {
		return lastPosts;
	}


	public void setLastPosts(ArrayList<WpPost> lastPosts) {
		this.lastPosts = lastPosts;
	}

	
	@Scheduled(fixedDelay=600000) //update posts every 10 minutes
	protected void updateLastPosts() {
		log.debug("Get last WordPress posts");
		ArrayList<WpPost> postList=new ArrayList<WpPost>(); 
		try {
			String url=wpUrl+"wp-json/wp/v2/posts?context=view&per_page="+Integer.toString(wpPosts);
			URLConnection connection = new URL(url).openConnection();
			InputStream resp = connection.getInputStream();
	
			String responseBody;
			try (Scanner scanner = new Scanner(resp)) {
			    responseBody = scanner.useDelimiter("\\A").next();
			    log.debug(responseBody);
			}

			Gson g = new GsonBuilder()
			           .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
			           .create();
			Type collectionType = new TypeToken<Collection<Post>>(){}.getType();
			Collection<Post> posts = g.fromJson(responseBody, collectionType);
			
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
		setLastPosts(postList);
	}

}
