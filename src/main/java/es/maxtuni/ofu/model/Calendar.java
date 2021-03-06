package es.maxtuni.ofu.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Calendar {

	private final String name;
	
	private final Season season;
	
	@Singular
	private final List<Match> matches;
	
	@Singular
	private final Map<Match, LocalDateTime> schedules;
	
	@Singular
	private final Map<Match, Result> results;
	
	public Calendar update(Calendar newData) {
		CalendarBuilder b = Calendar.builder()
			.name(newData.name);
		for(Match m : getMatches()) {
			b = b.match(m);
		}
		for(Match m : newData.getMatches()) {
			if(!getMatches().contains(m)) {
				b = b.match(m);
			}
		}
		for(Entry<Match, Result> m : getResults().entrySet()) {
			b = b.result(m.getKey(), m.getValue());
		}
		for(Entry<Match, Result> m : newData.getResults().entrySet()) {
			b = b.result(m.getKey(), m.getValue());
		}
		for(Entry<Match, LocalDateTime> m : getSchedules().entrySet()) {
			b = b.schedule(m.getKey(), m.getValue());
		}
		for(Entry<Match, LocalDateTime> m : newData.getSchedules().entrySet()) {
			b = b.schedule(m.getKey(), m.getValue());
		}
		return b.build();
	}
	
}
