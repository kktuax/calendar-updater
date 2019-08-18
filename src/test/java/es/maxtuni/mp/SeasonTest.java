package es.maxtuni.mp;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

import es.maxtuni.mp.Calendar.Match;

public class SeasonTest {

	@Test
	public void testSeasonStr() throws Exception {
		LocalDateTime time1 = LocalDateTime.of(2019, 1, 1, 0, 0);
		LocalDateTime time2 = LocalDateTime.of(2020, 1, 1, 0, 0);
		Calendar calendar = Calendar.builder()
			.schedule(new Match("1", "home", "away"), time1)
			.schedule(new Match("2", "away", "home"), time2)
			.build();
		Assert.assertEquals("2019/20", Season.from(calendar).toString());
	}
	
}
