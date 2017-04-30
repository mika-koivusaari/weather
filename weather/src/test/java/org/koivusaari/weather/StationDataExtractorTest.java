package org.koivusaari.weather;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.koivusaari.weather.pojo.WeatherData;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import junit.framework.TestCase;

public class StationDataExtractorTest extends TestCase {

	private WeatherDataExtractor ste;
	@Mock
	private ResultSet rs;
	@Mock
	private ResultSetMetaData rsmt;
	
	public StationDataExtractorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
		ste=new WeatherDataExtractor();
		super.setUp();
	}

	public void testGenerateMethodNameBasic(){
		assertEquals("setTemperature", ste.generateMethodName("TEMPERATURE"));
	}
	public void testGenerateMethodNameUnderscore(){
		assertEquals("setAirPressure", ste.generateMethodName("AIR_PRESSURE"));
	}
	
	@SuppressWarnings("deprecation")
	public void testExtractDataDate() throws SQLException{
	    when(rs.getMetaData()).thenReturn(rsmt);
	    when(rs.next()).thenReturn(true);
	    when(rsmt.getColumnCount()).thenReturn(1);
	    when(rs.getObject(1)).thenReturn(new Date(117, 0, 1, 12, 10));
	    when(rsmt.getColumnLabel(1)).thenReturn("TIME");
	    
	    WeatherData st=ste.extractData(rs);
	    assertEquals(LocalDateTime.of(2017, 1, 1, 12, 10), st.getTime());
	}
	public void testExtractDataNumber() throws SQLException{
	    when(rs.getMetaData()).thenReturn(rsmt);
	    when(rs.next()).thenReturn(true);
	    when(rsmt.getColumnCount()).thenReturn(1);
	    when(rs.getObject(1)).thenReturn(new BigDecimal(1.1));
	    when(rsmt.getColumnLabel(1)).thenReturn("TEMPERATURE");
	    
	    WeatherData st=ste.extractData(rs);
	    assertEquals(new Float(1.1), st.getTemperature());
	}
}
