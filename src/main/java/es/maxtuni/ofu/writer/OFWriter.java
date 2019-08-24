package es.maxtuni.ofu.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import es.maxtuni.ofu.model.Calendar;
import es.maxtuni.ofu.model.Season;

/**
 * Writes a calendar in <a href="https://github.com/openfootball/">Open Football</a> format
 *
 */
public class OFWriter implements CalendarWriter {
	
	@Override
	public void write(Calendar calendar, BufferedWriter writer) throws IOException {
		writer.write("###################################");
		writer.newLine();
		writer.write(String.format("# %s %s", calendar.getName(), Season.from(calendar)));
		writer.newLine();
		List<MatchInfo> matches = MatchInfo.fromCalendar(calendar);
		Map<String, List<MatchInfo>> rounds = matches.stream()
			.sorted(Comparator.comparing(mi -> Integer.valueOf(mi.getMatch().getRound().replaceAll("[^\\d.]", ""))))
			.collect(Collectors.groupingBy(
				m -> m.getMatch().getRound(),
				LinkedHashMap::new, Collectors.toList()
			));
		for(Map.Entry<String, List<MatchInfo>> round : rounds.entrySet()) {
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
				writer.write(String.format("[%s]", capitalize(DATE_FMT.format(day.getKey()))));
				writer.newLine();
				for(MatchInfo mi : day.getValue().stream()
					.sorted(Comparator.comparing(m -> m.getTime().get()))
					.collect(Collectors.toList())
				) {
					StringBuilder sb = new StringBuilder();
					sb.append(String.format(" %s ", TIME_FMT.format(mi.getTime().get())));
					sb.append(String.format("%-23s", mi.getMatch().getHome()));
					if(mi.getResult().isPresent()) {
						sb.append(String.format(" %s-%s ", mi.getResult().get().getHome(), mi.getResult().get().getAway()));
					}else {
						sb.append(" - ");
					}
					sb.append(mi.getMatch().getAway());
					writer.write(sb.toString());
					writer.newLine();
					writer.flush();
				}
			}
		}
	}
	
	static String capitalize(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
	
	static final DateTimeFormatter TIME_FMT = DateTimeFormatter
		.ofPattern("HH:mm")
		.withLocale(Locale.forLanguageTag("es-ES"));
	
	static final DateTimeFormatter DATE_FMT = DateTimeFormatter
		.ofPattern("EEE'.' d'.'M'.'")
		.withLocale(Locale.forLanguageTag("es-ES"));
	
}
