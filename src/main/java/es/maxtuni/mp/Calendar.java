package es.maxtuni.mp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Singular;

@Data
@Builder
public class Calendar {

	@Singular
	private final List<Match> matches;
	
	@Singular
	private final Map<Match, LocalDateTime> schedules;
	
	@Singular
	private final Map<Match, Result> results;
	
	@Data
	public static class Result {
		private final int home, away;
	}
	
	@Data
	public static class Match {
		
		private final String round, home, away;
		
		@Getter(lazy = true)
		private final Optional<Integer> roundNumber = parseRoundNumber();
		
		Optional<Integer> parseRoundNumber() {
			try {
				return Optional.of(Integer.valueOf(round.replaceAll("[^\\d.]", "")));	
			}catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		
		public boolean hasTeam(String name) {
			return name.equals(getHome()) || name.equals(getAway());
		}
		
	}
	
}
