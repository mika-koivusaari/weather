package org.koivusaari.weather.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="MESSAGES")
public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8438607185000699024L;
	@Id
	private Long messageId;
	private String author;
    @JsonFormat(pattern="dd.MM.yyyy HH:mm")
	private LocalDateTime from;
    @JsonFormat(pattern="dd.MM.yyyy HH:mm")
	private LocalDateTime to;
	private String message;
	public Long getMessageId() {
		return messageId;
	}
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public LocalDateTime getFrom() {
		return from;
	}
	public void setFrom(LocalDateTime from) {
		this.from = from;
	}
	public LocalDateTime getTo() {
		return to;
	}
	public void setTo(LocalDateTime to) {
		this.to = to;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return String.format("Message [messageId=%s, author=%s, from=%s, to=%s, message=%s]", messageId, author, from,
				to, message);
	}
	
	
}
