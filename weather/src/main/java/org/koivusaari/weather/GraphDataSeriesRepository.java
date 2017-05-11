package org.koivusaari.weather;

import org.koivusaari.weather.pojo.GraphDataSeries;
import org.springframework.data.repository.CrudRepository;

public interface GraphDataSeriesRepository extends CrudRepository<GraphDataSeries, Long> {

}
