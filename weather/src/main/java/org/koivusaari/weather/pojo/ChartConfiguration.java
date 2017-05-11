package org.koivusaari.weather.pojo;

import java.util.Arrays;
import java.util.Date;

public class ChartConfiguration {
	
	Long chartid;
	GraphDataSeries[] seriesa[];
	GraphDataSeries[] seriesb[];
	String name;
	Date from;
	Date to;
	Boolean dynamicDate;
	
	public Long getChartid() {
		return chartid;
	}
	public void setChartid(Long chartid) {
		this.chartid = chartid;
	}
	public GraphDataSeries[][] getSeriesA() {
		return seriesa;
	}
	public void setSeriesA(GraphDataSeries[][] seriesa) {
		this.seriesa = seriesa;
	}
	public GraphDataSeries[][] getSeriesB() {
		return seriesb;
	}
	public void setSeriesB(GraphDataSeries[][] seriesb) {
		this.seriesb = seriesb;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	
	
	public Boolean getDynamicDate() {
		return dynamicDate;
	}
	public void setDynamicDate(Boolean dynamicDate) {
		this.dynamicDate = dynamicDate;
	}
	
	@Override
	public String toString() {
		return String.format("ChartConfiguration [chartid=%s, seriesA=%s, seriesB=%s, name=%s, from=%s, to=%s]", chartid,
				Arrays.toString(seriesa), Arrays.toString(seriesb), name, from, to);
	}
	
	
}
