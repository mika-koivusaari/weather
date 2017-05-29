package org.koivusaari.weather;

import java.util.List;

import org.koivusaari.weather.pojo.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface MessageRepository extends CrudRepository<Message,Long>{

	@Query("SELECT m FROM Message m WHERE m.to IS NULL OR localtimestamp BETWEEN m.from AND m.to ORDER BY m.from ASC")
	public List<Message> findCurrentMessages();
	
	public List<Message> findAllByOrderByFromAsc();
}
