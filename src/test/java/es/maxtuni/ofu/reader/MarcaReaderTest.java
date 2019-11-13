package es.maxtuni.ofu.reader;

import java.io.InputStream;
import java.net.URL;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import es.maxtuni.ofu.model.Calendar;
import es.maxtuni.ofu.model.Match;
import es.maxtuni.ofu.reader.MarcaReader;

public class MarcaReaderTest {

	@Ignore("external resource")
	@Test
	public void testParseUrl() throws Exception {
		try(InputStream is = new URL("https://www.marca.com/futbol/segunda-division/calendario.html").openStream()){
			Calendar calendar = new MarcaReader("Test").read(is);
			Assert.assertFalse(calendar.getMatches().isEmpty());
		}
	}
	
	@Test
	public void testParse() throws Exception {
		try(InputStream is = getClass().getResourceAsStream("/calendar-2394092156733122534.html")){
			Calendar calendar = new MarcaReader("Test").read(is);
			Assert.assertFalse(calendar.getMatches().isEmpty());
			Assert.assertEquals(42, calendar.getMatches().stream()
				.collect(Collectors.groupingBy(Match::getRound))
				.keySet()
				.size()
			);
			Assert.assertEquals(42, calendar.getMatches().stream()
				.filter(m -> m.hasTeam("Racing Santander"))
				.count()
			);
			Assert.assertEquals(42, calendar.getMatches().stream()
				.filter(m -> m.hasTeam("UD Las Palmas"))
				.count()
			);
		}
	}
	
}
