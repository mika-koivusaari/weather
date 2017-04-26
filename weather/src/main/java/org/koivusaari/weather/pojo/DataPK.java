package org.koivusaari.weather.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DataPK implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6320047343794411898L;
	private LocalDateTime time;
	private long sensorId;
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public long getSensorId() {
		return sensorId;
	}
	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	
}
