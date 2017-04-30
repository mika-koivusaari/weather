package org.koivusaari.weather;

import org.koivusaari.weather.pojo.WeatherData;

public interface WeatherRepository {

	WeatherData findLastData();
}
