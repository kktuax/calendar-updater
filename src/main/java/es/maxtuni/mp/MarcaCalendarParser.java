package es.maxtuni.mp;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.maxtuni.mp.Calendar.Match;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarcaCalendarParser {

	public static Calendar parse(String name, InputStream calendarIs) throws IOException {
		Calendar.CalendarBuilder builder = Calendar.builder()
			.name(name);
		Document doc = Jsoup.parse(calendarIs, "ISO-8859-1", "https://www.marca.com/");
		Optional<Season> season = Season.fromTitle(doc);
		Elements tables = doc.select("table.jor");
		log.debug("Found {} tables", tables.size());
		for (Element table : tables) {
			String round = table.select("caption").text();
			log.debug("Parsing table for round: {}", round);	
			for (Element matchTr : table.select("tr")) {
				Optional<Element> homeSpan = Optional.ofNullable(matchTr.selectFirst("td.local span"));
				Optional<Element> awaySpan = Optional.ofNullable(matchTr.selectFirst("td.visitante span"));
				if(homeSpan.isPresent() && awaySpan.isPresent()) {
					Match match = new Match(round, homeSpan.get().text(), awaySpan.get().text());
					builder = builder.match(match);
					log.debug("Found match: {}", match);
					Optional<LocalDateTime> time = time(matchTr, season);
					if(time.isPresent()) {
						builder = builder.schedule(match, time.get());
					}
				}
			}
		}
		return builder.build();
	}
	
	static Optional<LocalDateTime> time(Element matchTr, Optional<Season> season){
		Optional<Integer> dayOfMonth = Optional
			.ofNullable(matchTr.selectFirst("td.resultado span.fecha"))
			.map(e -> e.text())
			.filter(str -> str.contains("/"))
			.map(str -> str.split("/")[0])
			.map(Integer::valueOf);
		Optional<Integer> month = Optional
			.ofNullable(matchTr.selectFirst("td.resultado span.fecha"))
			.map(e -> e.text())
			.filter(str -> str.contains("/"))
			.map(str -> str.split("/")[1])
			.map(Integer::valueOf);
		if(season.isPresent() && dayOfMonth.isPresent() && month.isPresent()) {
			Integer hour = Optional
				.ofNullable(matchTr.selectFirst("td.resultado span.hora"))
				.map(e -> e.text())
				.filter(str -> str.contains(":"))
				.map(str -> str.split(":")[0])
				.map(Integer::valueOf)
				.orElse(18);
			Integer minute = Optional
				.ofNullable(matchTr.selectFirst("td.resultado span.hora"))
				.map(e -> e.text())
				.filter(str -> str.contains(":"))
				.map(str -> str.split(":")[1])
				.map(Integer::valueOf)
				.orElse(0);
			return Optional.of(season.get().getTime(dayOfMonth.get(), month.get(), hour, minute));
		}
		return Optional.empty();
	}
	
	@Data
	private static class Season {
		
		final int start, end;
		
		public static Optional<Season> fromTitle(Document doc) {
			Pattern yearsPattern = Pattern.compile(".*(\\d{4}) - (\\d{4}).*");
			String title = doc.getElementsByTag("title").text();
			Matcher matcher = yearsPattern.matcher(title);
			if(matcher.matches()) {
				Integer start = Integer.valueOf(matcher.group(1));
				Integer end = Integer.valueOf(matcher.group(2));
				Season season = new Season(start, end);
				log.debug("Found season {}", season);
				return Optional.of(season);
			}
			return Optional.empty();
		}
		
		public LocalDateTime getTime(int dayOfMonth, int month, int hour, int minute) {
			int year = month <=7 ? end : start;
			return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
		}
		
	}
	
}