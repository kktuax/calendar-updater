package es.maxtuni.mp.reader;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import es.maxtuni.mp.model.Calendar;
import es.maxtuni.mp.model.Season;

public class OFReaderTest {

	@Test
	public void testParse() throws Exception {
		try(InputStream is = getClass().getResourceAsStream("/1-liga.txt")){
			Calendar calendar = new OFReader().read("Primera División", is);
			Assert.assertEquals(new Season(2019, 2020), calendar.getSeason());
		}
	}
	
}
