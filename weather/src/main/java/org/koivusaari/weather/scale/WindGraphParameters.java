package org.koivusaari.weather.scale;

import java.util.LinkedHashMap;
import java.util.Map;

import org.koivusaari.weather.scale.AbstractGraphParameters.GraphScale;

public class WindGraphParameters extends AbstractGraphParameters {

	private LinkedHashMap<String,AbstractGraphParameters.GraphScale> scales=new LinkedHashMap<String,OutsideTempGraphParameters.GraphScale>();

	public WindGraphParameters(){
		scales.put("kohtalainen",new GraphScale("kohtalainen", 0, 8,new float[] {0,2,4,6,8},8,0));
		scales.put("kova",new GraphScale("kova", 0, 16,new float[] {0,4,8,12,16},8,0));
		scales.put("myrsky",new GraphScale("myrsky", 0, 32,new float[] {0,8,16,24,32},8,0));
	}
	
	@Override
	public GraphScale getScale(float min, float max) {
		GraphScale scale=null;
		for (Map.Entry<String, GraphScale> entry:scales.entrySet()){
		    String key = entry.getKey();
		    GraphScale s = entry.getValue();
			if (s.isBetween(min, max)){
				scale=s;
				break;
			}
		}
		if (scale==null){
			scale=new GraphScale("Generated", 0, Math.round(max+2),null);
		}
		return scale;
	}

}
