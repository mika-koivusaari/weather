package org.koivusaari.weather.pojo;

public class AggregateFunction {
	private String function;
	private Long aggregateId;
	private String name;
	private String description;

	public AggregateFunction(Long aggregateId, String function, String name, String description) {
		super();
		this.aggregateId = aggregateId;
		this.function = function;
		this.name = name;
		this.description = description;
	}

	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public Long getAggregateId() {
		return aggregateId;
	}
	public void setAggregateId(Long aggregateId) {
		this.aggregateId = aggregateId;
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

	@Override
	public String toString() {
		return String.format("AggregateFunction [aggregateId=%s, function=%s, name=%s]", aggregateId, function, name);
	}
	
	
}
