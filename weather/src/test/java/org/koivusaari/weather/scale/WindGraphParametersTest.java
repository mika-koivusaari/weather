package org.koivusaari.weather.scale;

import static org.mockito.Mockito.*;

import org.koivusaari.weather.scale.AbstractGraphParameters.GraphScale;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import junit.framework.TestCase;

public class WindGraphParametersTest extends TestCase {

	private WindGraphParameters graphParam;
	
	protected void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    graphParam=new WindGraphParameters();
		super.setUp();
	}
	
	public void testGetScaleKohtalainen(){
		GraphScale scale=graphParam.getScale(0, 8);
		assertEquals("kohtalainen", scale.getName());
	}
	
	public void testGetScaleKova(){
		GraphScale scale=graphParam.getScale(0, 16);
		assertEquals("kova", scale.getName());
	}

	public void testGetScaleMyrsky(){
		GraphScale scale=graphParam.getScale(0, 32);
		assertEquals("myrsky", scale.getName());
	}
}
