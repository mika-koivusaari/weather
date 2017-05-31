package org.koivusaari.weather.pojo.googlecharts;

import org.koivusaari.weather.CustomDateSerializer;
import org.koivusaari.weather.NewDateSerializer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ChartV {
    private Object v;

    @JsonSerialize(using = CustomDateSerializer.class)
//    @JsonFormat(pattern="'new Date'(yyyy, MM ,dd ,HH ,mm ,00)")
    @JsonRawValue
//    @JsonSerialize(using = NewDateSerializer.class)
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
