package org.koivusaari.weather;

import org.koivusaari.weather.pojo.StationData;

public interface WeatherRepository {

	StationData findLastData();
}
