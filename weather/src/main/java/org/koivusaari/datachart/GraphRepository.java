package org.koivusaari.datachart;

import org.koivusaari.weather.pojo.Graph;
import org.springframework.data.repository.CrudRepository;

public interface GraphRepository extends CrudRepository<Graph, Long>  {

}
