package es.maxtuni.ofu.writer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.maxtuni.ofu.model.Calendar;
import es.maxtuni.ofu.model.Match;
import es.maxtuni.ofu.model.Result;
import lombok.Builder;
import lombok.Data;

@Data
@Builder 
class MatchInfo {
	
	private final Match match;
	
	@Builder.Default
	private final Optional<LocalDateTime> time = Optional.empty();
	
	@Builder.Default
	private final Optional<Result> result = Optional.empty();
	
	public static List<MatchInfo> fromCalendar(Calendar calendar){
		List<MatchInfo> matches = new ArrayList<>();
		for(Match m : calendar.getMatches()) {
			MatchInfoBuilder b = MatchInfo.builder().match(m);
			if(calendar.getSchedules().containsKey(m)) {
				b = b.time(Optional.of(calendar.getSchedules().get(m)));
			}
			if(calendar.getResults().containsKey(m)) {
				b = b.result(Optional.of(calendar.getResults().get(m)));
			}
			matches.add(b.build());
		}
		return matches;
	}
	
}