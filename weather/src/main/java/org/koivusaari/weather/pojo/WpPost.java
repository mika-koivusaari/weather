package org.koivusaari.weather.pojo;

import java.time.LocalDateTime;

public class WpPost {

	private LocalDateTime modified;
	private String link;
	private String title;
	
	
	public WpPost(LocalDateTime modified, String link, String title) {
		super();
		this.modified = modified;
		this.link = link;
		this.title = title;
	}
	
	public LocalDateTime getModified() {
		return modified;
	}
	public void setModified(LocalDateTime modified) {
		this.modified = modified;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
}
