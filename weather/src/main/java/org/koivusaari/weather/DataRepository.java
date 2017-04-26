package org.koivusaari.weather;

import org.koivusaari.weather.pojo.Data;
import org.koivusaari.weather.pojo.DataPK;
import org.springframework.data.repository.CrudRepository;

public interface DataRepository extends CrudRepository<Data, DataPK>{

}
