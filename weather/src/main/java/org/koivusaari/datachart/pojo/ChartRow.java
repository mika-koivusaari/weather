package org.koivusaari.datachart.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartRow {
	private Date time=null;
	private List<Float> data=new ArrayList<Float>();

	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public List<Float> getData() {
		return data;
	}
	public void setData(List<Float> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return String.format("ChartRow [time=%s, data=%s]", time, data);
	}
	
	
	
}
