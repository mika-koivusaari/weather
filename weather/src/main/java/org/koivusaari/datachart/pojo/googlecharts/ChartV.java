package org.koivusaari.datachart.pojo.googlecharts;

import org.koivusaari.datachart.CustomDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ChartV {
    private Object v;

    @JsonSerialize(using = CustomDateSerializer.class)
    public Object getV() {
		return v;
	}

	public void setV(Object v) {
		this.v = v;
	}

	public ChartV(Object v) {
		super();
		this.v = v;
	}
    
    
}
