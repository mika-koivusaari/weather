package org.koivusaari.weather.pojo;

import org.koivusaari.weather.WeatherDataExtractor;
import org.mockito.MockitoAnnotations;

import junit.framework.TestCase;

public class WeatherDataTest extends TestCase {

	private WeatherData wd;
	
	protected void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
		wd=new WeatherData();
		super.setUp();
	}
	
	public void testGetTemperatureFeelsLikeCold(){
		wd.setTemperature(new Float(0));
		wd.setWindSpeed(new Float(0));
		assertEquals(0, wd.getTemperatureFeelsLike(), 0.01);

		wd.setTemperature(new Float(-10));
		wd.setWindSpeed(new Float(0));
		assertEquals(-10, wd.getTemperatureFeelsLike(), 0.01);

		wd.setTemperature(new Float(-10));
		wd.setWindSpeed(new Float(5));
		assertEquals(-17.446, wd.getTemperatureFeelsLike(), 0.01);

		wd.setTemperature(new Float(-10));
		wd.setWindSpeed(new Float(10));
		assertEquals(-20.30, wd.getTemperatureFeelsLike(), 0.01);
}

}
