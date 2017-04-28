package org.koivusaari.weather.pojo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author mik
 * 
 * All datafields of one station in one point in time.
 *
 */
public class StationData {
	
	private LocalDateTime time;
	private Float temperature;
	private Float airPressure;
	
	private Float rain; //Last minute
	private Float rain10; //Last 10 minutes
	private Float rain60; //Last 60 minutes
	private Float windSpeed; //Last 10 min average
	private Float windDirection; //Last 10 min average
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public void setTime(Date time){
		Instant instant = Instant.ofEpochMilli(time.getTime());
		this.time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
	public Float getTemperature() {
		return temperature;
	}
	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}
	public Float getTemperatureFeelsLike() {
		//formula from https://fi.wikipedia.org/wiki/Pakkasen_purevuus
		if (temperature!=null && windSpeed!=null){
			if (temperature<10){
			    return new Float(13.12+0.6215*temperature-13.956*Math.pow(windSpeed,0.16)+0.4867*temperature*Math.pow(windSpeed, 0.16));
			}
		}
		return temperature;
	}
	public Float getRain() {
		return rain;
	}
	public void setRain(Float rain) {
		this.rain = rain;
	}
	public Float getRain10() {
		return rain10;
	}
	public void setRain10(Float rain10) {
		this.rain10 = rain10;
	}
	public Float getRain60() {
		return rain60;
	}
	public void setRain60(Float rain60) {
		this.rain60 = rain60;
	}
	public Float getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(Float windSpeed) {
		this.windSpeed = windSpeed;
	}
	public Float getWindDirection() {
		return windDirection;
	}
	public void setWindDirection(Float windDirection) {
		this.windDirection = windDirection;
	}
	public Float getAirPressure() {
		return airPressure;
	}
	public void setAirPressure(Float airPressure) {
		this.airPressure = airPressure;
	}
	@Override
	public String toString() {
		return String.format(
				"StationData [time=%s, temperature=%s, temperatureFeelsLike=%s, airPressure=%s, rain=%s, rain10=%s, rain60=%s, windSpeed=%s, windDirection=%s]",
				time, temperature, getTemperatureFeelsLike(), airPressure, rain, rain10, rain60, windSpeed, windDirection);
	}

	
}
