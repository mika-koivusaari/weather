package org.koivusaari.weather.repositories;

import org.koivusaari.weather.pojo.WeatherData;

public interface WeatherRepository {

	WeatherData findLastData();
}
