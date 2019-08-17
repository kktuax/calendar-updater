package es.maxtuni.mp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import es.maxtuni.mp.CalendarParser.Match;

public class OpenFootballCalendarConsumer implements Consumer<List<Match>> {
	
	@Override
	public void accept(List<Match> matches) {
		Map<Integer, List<Match>> rounds = matches.stream()
			.collect(Collectors.groupingBy(
				m -> Integer.valueOf(m.getRoundNumber().orElse(0)),
				TreeMap::new, Collectors.toList()
			));
		for(Map.Entry<Integer, List<Match>> round : rounds.entrySet()) {
			System.out.println("");
			System.out.println("");
			System.out.println(round.getValue().iterator().next().getRound());
			for(Map.Entry<LocalDateTime, List<Match>> day : round.getValue().stream()
				.collect(Collectors.groupingBy(
					m -> m.getTime().truncatedTo(ChronoUnit.DAYS),
					TreeMap::new, Collectors.toList()
				))
				.entrySet()) {
				System.out.println(String.format("[%s]", DATE_FMT.format(day.getKey())));
				for(Match match : day.getValue().stream()
					.sorted(Comparator.comparing(Match::getTime))
					.collect(Collectors.toList())
				) {
					StringBuilder sb = new StringBuilder();
					sb.append(String.format("  %s  ", TIME_FMT.format(match.getTime())));
					sb.append(String.format("%-23s", match.getHome()));
					sb.append(String.format(" - %s", match.getAway()));
					System.out.println(sb.toString());
				}
			}
		}
	}

	static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm"),
		DATE_FMT = DateTimeFormatter.ofPattern("EEE'.' dd'.'M'.'");
	
}
