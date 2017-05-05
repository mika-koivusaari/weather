package org.koivusaari.datachart.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Sensors {

	@Id
	@Column(name="SENSORID")
	private Long sensorId;
	private String name;
	private String Description;
	@Column(name="UNITID")
	private Long unitId;
	
	public Long getSensorId() {
		return sensorId;
	}
	public void setSensorId(Long sensorId) {
		this.sensorId = sensorId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public Long getUnitId() {
		return unitId;
	}
	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}
	@Override
	public String toString() {
		return String.format("Sensors [sensorId=%s, name=%s, Description=%s, unitId=%s]", sensorId, name, Description,
				unitId);
	}
	
	
}
