package es.maxtuni.mp;

import java.io.InputStream;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import es.maxtuni.mp.Calendar.Match;
import es.maxtuni.mp.reader.MarcaCalendarReader;

public class CalendarParserTest {

	@Test
	public void testParse() throws Exception {
		try(InputStream is = getClass().getResourceAsStream("/calendar-2394092156733122534.html")){
			Calendar calendar = MarcaCalendarReader.parse("", is);
			Assert.assertFalse(calendar.getMatches().isEmpty());
			Assert.assertEquals(42, calendar.getMatches().stream()
				.collect(Collectors.groupingBy(Match::getRound))
				.keySet()
				.size()
			);
			Assert.assertEquals(42, calendar.getMatches().stream()
				.filter(m -> m.hasTeam("Racing"))
				.count()
			);
		}
	}
	
}
