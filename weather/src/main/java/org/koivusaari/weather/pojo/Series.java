package org.koivusaari.weather.pojo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="GRAPHDATASERIES")
public class Series implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8708281078817679895L;
	@Id
    private Long seriesid;
    private Long sensorid;
    private String groupby;
    private String valuefunction;
	private String name;
    private String description;
    private String minGroupByTime;
    @OneToMany(mappedBy="series")
    private List<GraphSeries> graphSeries;
    
	public Long getSeriesid() {
		return seriesid;
	}
	public void setSeriesid(Long seriesid) {
		this.seriesid = seriesid;
	}
	public Long getSensorid() {
		return sensorid;
	}
	public void setSensorid(Long sensorid) {
		this.sensorid = sensorid;
	}
	public String getGroupby() {
		return groupby;
	}
	public void setGroupby(String groupby) {
		this.groupby = groupby;
	}
	public String getValuefunction() {
		return valuefunction;
	}
	public void setValuefunction(String valuefunction) {
		this.valuefunction = valuefunction;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMinGroupByTime() {
		return minGroupByTime;
	}
	public void setMinGroupByTime(String minGroupByTime) {
		this.minGroupByTime = minGroupByTime;
	}
	
	
	public List<GraphSeries> getGraphSeries() {
		return graphSeries;
	}
	public void setGraphSeries(List<GraphSeries> graphSeries) {
		this.graphSeries = graphSeries;
	}
	@Override
	public String toString() {
		return String.format("Series [seriesid=%s, sensorid=%s, groupby=%s, valuefunction=%s, name=%s, description=%s]",
				seriesid, sensorid, groupby, valuefunction, name, description);
	}

    
}
