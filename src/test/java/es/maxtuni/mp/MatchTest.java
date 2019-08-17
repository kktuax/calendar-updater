package es.maxtuni.mp;

import org.junit.Assert;
import org.junit.Test;

import es.maxtuni.mp.Calendar.Match;

public class MatchTest {

	@Test
	public void testRoundNumber() throws Exception {
		Match match = new Match("Jornada 10", "home", "away");
		Assert.assertEquals(Integer.valueOf(10), match.getRoundNumber().get());
	}
	
}
