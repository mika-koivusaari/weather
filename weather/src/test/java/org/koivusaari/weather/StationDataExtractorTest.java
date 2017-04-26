package org.koivusaari.weather;

import junit.framework.TestCase;

public class StationDataExtractorTest extends TestCase {

	private StationDataExtractor ste;
	
	public StationDataExtractorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		ste=new StationDataExtractor();
		super.setUp();
	}

	public void testGenerateMethodNameBasic(){
		assertEquals("setTemperature", ste.generateMethodName("TEMPERATURE"));
	}
	public void testGenerateMethodNameUnderscore(){
		assertEquals("setAirPressure", ste.generateMethodName("AIR_PRESSURE"));
	}
}
