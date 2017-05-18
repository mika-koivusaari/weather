package org.koivusaari.weather.pojo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

@Entity
public class GraphSeries implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1181453571868737416L;

	@ManyToOne
	@JoinColumn(name="graph_id")
	private Graph graph;
	@ManyToOne
	@JoinColumn(name="series_id")
	private Series series;
	@Id
	private Long orderBy;

	public Graph getGraph() {
		return graph;
	}
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	public Series getSeries() {
		return series;
	}
	public void setSeries(Series series) {
		this.series = series;
	}
	public Long getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}
	

	
	
}
