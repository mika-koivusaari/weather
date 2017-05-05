package org.koivusaari.weather.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Graph {

	@Id
	private Long graphId;
    private String name;
    private String role;
    private LocalDateTime from;
    private LocalDateTime to;
    private Long dynamic;
    static final public long STATIC_TIME=0;
    static final public long DYNAMIC_TIME=1;
    @ManyToMany
    @JoinTable(name="graph_series",
    joinColumns=@JoinColumn(name="graph_id"),
    inverseJoinColumns=@JoinColumn(name="series_id"))
    private List<Series> series;
    
	public Long getGraphId() {
		return graphId;
	}
	public void setGraphId(Long graphId) {
		this.graphId = graphId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public LocalDateTime getFrom() {
		return from;
	}
	public void setFrom(LocalDateTime from) {
		this.from = from;
	}
	public LocalDateTime getTo() {
		return to;
	}
	public void setTo(LocalDateTime to) {
		this.to = to;
	}
	public Long getDynamic() {
		return dynamic;
	}
	public void setDynamic(Long dynamic) {
		this.dynamic = dynamic;
	}
	public List<Series> getSeries() {
		return series;
	}
	public void setSeries(List<Series> series) {
		this.series = series;
	}
	@Override
	public String toString() {
		return String.format("Graph [graphId=%s, name=%s, role=%s, from=%s, to=%s, dynamic=%s, series=%s]", graphId,
				name, role, from, to, dynamic, series);
	}

    
}
