package org.koivusaari.weather;


import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class NewDateSerializer extends JsonSerializer<Object> {
	
	  @Override
	  public void serialize(Object value, JsonGenerator gen, SerializerProvider arg2) throws 
	      IOException, JsonProcessingException {      

		  String formatted;
		  if (value instanceof LocalDateTime){
			  LocalDateTime d=(LocalDateTime)value;
			  formatted = String.format("new Date(%d, %d ,%d ,%d ,%d ,00)",d.getYear()
					                                                  ,d.getMonth().getValue()-1
					                                                  ,d.getDayOfMonth()
					                                                  ,d.getHour()
					                                                  ,d.getMinute());
		  } else {
			  formatted=value.toString();
		  }

	      gen.writeString(formatted);

	  }
}
