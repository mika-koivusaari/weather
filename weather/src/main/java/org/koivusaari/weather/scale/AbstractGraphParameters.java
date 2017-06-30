package org.koivusaari.weather.scale;

import org.koivusaari.weather.scale.AbstractGraphParameters.GraphScale;

public abstract class AbstractGraphParameters {

	abstract GraphScale getScale(float min, float max);

	public class GraphScale{
	
		private String name;
		private float from;
		private float to;
		private float[] ticks;
	
		public GraphScale(String name, float from, float to,float[] ticks) {
			super();
			this.name = name;
			this.from = from;
			this.to = to;
			this.ticks = ticks;
		}
	
		public boolean isBetween(float min, float max){
			return min>=from && min<=to &&
				   max>=from && max<=to;
		}
		
		public String getName() {
			return name;
		}
	
		public void setName(String name) {
			this.name = name;
		}
	
		public float getFrom() {
			return from;
		}
	
		public void setFrom(float from) {
			this.from = from;
		}
	
		public float getTo() {
			return to;
		}
	
		public void setTo(int to) {
			this.to = to;
		}
	
		public float[] getTicks() {
			return ticks;
		}
	
	}
}
