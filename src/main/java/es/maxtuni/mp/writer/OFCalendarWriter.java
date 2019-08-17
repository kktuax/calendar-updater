package es.maxtuni.mp.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import es.maxtuni.mp.Calendar;

/**
 * Writes a calendar in <a href="https://github.com/openfootball/">Open Football</a> format
 *
 */
public class OFCalendarWriter implements CalendarWriter {
	
	@Override
	public void write(Calendar calendar, BufferedWriter writer) throws IOException {
		List<MatchInfo> matches = MatchInfo.fromCalendar(calendar);
		Map<Integer, List<MatchInfo>> rounds = matches.stream()
			.collect(Collectors.groupingBy(
				m -> Integer.valueOf(m.getMatch().getRoundNumber().orElse(0)),
				TreeMap::new, Collectors.toList()
			));
		for(Map.Entry<Integer, List<MatchInfo>> round : rounds.entrySet()) {
			writer.newLine();
			writer.newLine();
			writer.write(round.getValue().get(0).getMatch().getRound());
			writer.newLine();
			for(Map.Entry<LocalDateTime, List<MatchInfo>> day : round.getValue().stream()
				.filter(m -> m.getTime().isPresent())
				.collect(Collectors.groupingBy(
					m -> m.getTime().get().truncatedTo(ChronoUnit.DAYS),
					TreeMap::new, Collectors.toList()
				))
				.entrySet()) {
				writer.write(String.format("[%s]", DATE_FMT.format(day.getKey())));
				writer.newLine();
				for(MatchInfo mi : day.getValue().stream()
					.sorted(Comparator.comparing(m -> m.getTime().get()))
					.collect(Collectors.toList())
				) {
					StringBuilder sb = new StringBuilder();
					sb.append(String.format("  %s  ", TIME_FMT.format(mi.getTime().get())));
					sb.append(String.format("%-23s", mi.getMatch().getHome()));
					sb.append(String.format(" - %s", mi.getMatch().getAway()));
					writer.write(sb.toString());
					writer.newLine();
					writer.flush();
				}
			}
		}
	}

	static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm"),
		DATE_FMT = DateTimeFormatter.ofPattern("EEE'.' dd'.'M'.'");
	
}
