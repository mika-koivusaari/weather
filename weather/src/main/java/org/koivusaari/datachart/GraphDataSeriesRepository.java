package org.koivusaari.datachart;

import org.koivusaari.datachart.pojo.GraphDataSeries;
import org.springframework.data.repository.CrudRepository;

public interface GraphDataSeriesRepository extends CrudRepository<GraphDataSeries, Long> {

}
