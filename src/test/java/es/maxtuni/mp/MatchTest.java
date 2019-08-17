package es.maxtuni.mp;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

import es.maxtuni.mp.CalendarParser.Match;


public class MatchTest {

	@Test
	public void testRoundNumber() throws Exception {
		Match match = new Match("Jornada 10", "home", "away", LocalDateTime.now());
		Assert.assertEquals(Integer.valueOf(10), match.getRoundNumber().get());
	}
	
}
