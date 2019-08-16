package es.maxtuni.mp;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import es.maxtuni.mp.CalendarParser;
import es.maxtuni.mp.CalendarParser.Match;

public class CalendarParserTest {

	@Test
	public void testParse() throws Exception {
		try(InputStream is = getClass().getResourceAsStream("/calendar-2394092156733122534.html")){
			List<Match> matches = CalendarParser.parse(is);
			Assert.assertFalse(matches.isEmpty());
			Assert.assertEquals(42, matches.stream()
				.collect(Collectors.groupingBy(Match::getRound))
				.keySet()
				.size()
			);
			Assert.assertEquals(42, matches.stream()
				.filter(m -> m.hasTeam("Racing"))
				.count()
			);
		}
	}
	
}
