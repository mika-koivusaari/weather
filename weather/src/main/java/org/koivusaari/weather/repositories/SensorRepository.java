package org.koivusaari.weather.repositories;

import org.koivusaari.weather.pojo.Sensors;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface SensorRepository extends CrudRepository<Sensors, Long> {

}
