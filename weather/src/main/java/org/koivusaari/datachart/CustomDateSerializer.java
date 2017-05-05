package org.koivusaari.datachart;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomDateSerializer extends JsonSerializer<Object> {
	
	  @SuppressWarnings("deprecation")
	  @Override
	  public void serialize(Object value, JsonGenerator gen, SerializerProvider arg2) throws 
	      IOException, JsonProcessingException {      

		  String formatted;
		  if (value instanceof Date){
			  Date d=(Date)value;
			  formatted = String.format("Date(%d, %d ,%d ,%d ,%d ,00)",d.getYear()+1900
					                                                  ,d.getMonth()
					                                                  ,d.getDate()
					                                                  ,d.getHours()
					                                                  ,d.getMinutes());
		  } else {
			  formatted=value.toString();
		  }

	      gen.writeString(formatted);

	  }
}
