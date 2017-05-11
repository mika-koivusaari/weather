package org.koivusaari.weather;

import org.koivusaari.weather.pojo.Sensors;
import org.springframework.data.repository.CrudRepository;

public interface SensorRepository extends CrudRepository<Sensors, Long> {

}
