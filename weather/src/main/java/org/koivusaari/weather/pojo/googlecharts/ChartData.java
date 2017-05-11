package org.koivusaari.weather.pojo.googlecharts;

import java.util.ArrayList;

public class ChartData {
	private ArrayList<ChartCol> cols;
	private ArrayList<ChartC> rows;
	public ArrayList<ChartCol> getCols() {
		return cols;
	}
	public void setCols(ArrayList<ChartCol> cols) {
		this.cols = cols;
	}
	public ArrayList<ChartC> getRows() {
		return rows;
	}
	public void setRows(ArrayList<ChartC> rows) {
		this.rows = rows;
	}
	
	@Override
	public String toString() {
		return String.format("ChartData [cols=%s, rows=%s]", cols, rows);
	}
}
