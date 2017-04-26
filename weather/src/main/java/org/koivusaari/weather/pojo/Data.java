package org.koivusaari.weather.pojo;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(value=DataPK.class)
public class Data {

	@Id
	private LocalDateTime time;
	@Id
	private long sensorId;
	private float value;

	
}
