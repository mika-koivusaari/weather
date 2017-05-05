package org.koivusaari.datachart.pojo.googlecharts;

public class ChartCol {
	private String id;
	private String type;
	private String label=null;
	private String pattern=null;
	private String p=null;
	private String unit;
	
	
	public ChartCol(String id, String type) {
		super();
		this.id = id;
		this.type = type;
	}
	
	public ChartCol(String id, String type, String label) {
		super();
		this.id = id;
		this.type = type;
		this.label = label;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
}
