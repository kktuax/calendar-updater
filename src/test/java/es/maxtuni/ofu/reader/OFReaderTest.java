package es.maxtuni.ofu.reader;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import es.maxtuni.ofu.model.Calendar;
import es.maxtuni.ofu.model.Season;
import es.maxtuni.ofu.reader.OFReader;

public class OFReaderTest {

	@Test
	public void testParse() throws Exception {
		try(InputStream is = getClass().getResourceAsStream("/1-liga.txt")){
			Calendar calendar = new OFReader("Primera División").read(is);
			Assert.assertEquals(new Season(2019, 2020), calendar.getSeason());
			Assert.assertFalse(calendar.getMatches().isEmpty());
			Assert.assertFalse(calendar.getSchedules().isEmpty());
			Assert.assertFalse(calendar.getResults().isEmpty());
		}
	}
	
}
