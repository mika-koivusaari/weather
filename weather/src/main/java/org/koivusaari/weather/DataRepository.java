package org.koivusaari.weather;

import org.koivusaari.weather.pojo.Data;
import org.koivusaari.weather.pojo.DataPK;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface DataRepository extends CrudRepository<Data, DataPK>{

}
