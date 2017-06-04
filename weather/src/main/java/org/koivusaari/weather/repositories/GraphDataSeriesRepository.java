package org.koivusaari.weather.repositories;

import org.koivusaari.weather.pojo.GraphDataSeries;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface GraphDataSeriesRepository extends CrudRepository<GraphDataSeries, Long> {

}
