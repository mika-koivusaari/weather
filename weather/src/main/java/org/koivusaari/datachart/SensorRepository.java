package org.koivusaari.datachart;

import org.koivusaari.datachart.pojo.Sensors;
import org.springframework.data.repository.CrudRepository;

public interface SensorRepository extends CrudRepository<Sensors, Long> {

}
