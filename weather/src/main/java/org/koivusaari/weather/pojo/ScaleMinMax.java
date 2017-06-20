package org.koivusaari.weather.pojo;

import java.time.LocalDateTime;

import javax.persistence.Convert;
import javax.persistence.Entity;

import org.koivusaari.weather.LocalDateTimeConverter;

public class ScaleMinMax {

    @Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime time;
	private Float min;
	private Float max;
	
	public ScaleMinMax() {
		super();
	}

	public ScaleMinMax(LocalDateTime time, Float min, Float max) {
		super();
		this.time = time;
		this.min = min;
		this.max = max;
	}
	
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public Float getMin() {
		return min;
	}
	public void setMin(Float min) {
		this.min = min;
	}
	public Float getMax() {
		return max;
	}
	public void setMax(Float max) {
		this.max = max;
	}
	
}
