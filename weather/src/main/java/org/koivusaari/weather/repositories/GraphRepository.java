package org.koivusaari.weather.repositories;

import org.koivusaari.weather.pojo.Graph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface GraphRepository extends CrudRepository<Graph, Long>  {

}
