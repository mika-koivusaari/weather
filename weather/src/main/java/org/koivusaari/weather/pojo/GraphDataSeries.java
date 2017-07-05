package org.koivusaari.weather.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="GRAPHDATASERIES")
public class GraphDataSeries {
	
	@Id
    private Long seriesid;
    private Long sensorid;
    private String groupby;
    private String valuefunction;
	private String name;
    private String description;
    private String minGroupByTime;
    private String scaleClass;

    public String getMinGroupByTime() {
		return minGroupByTime;
	}
	public void setMinGroupByTime(String minGroupByTime) {
		this.minGroupByTime = minGroupByTime;
	}
	public String getScaleClass() {
		return scaleClass;
	}
	public void setScaleClass(String scaleClass) {
		this.scaleClass = scaleClass;
	}
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

}
