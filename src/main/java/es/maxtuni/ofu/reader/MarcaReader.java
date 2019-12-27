package es.maxtuni.ofu.reader;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.maxtuni.ofu.model.Calendar;
import es.maxtuni.ofu.model.Match;
import es.maxtuni.ofu.model.Result;
import es.maxtuni.ofu.model.Season;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MarcaReader implements CalendarReader {

	private final String name;
	
	private final Map<String, String> teamNamesMapping;

	public MarcaReader(String name) throws IOException {
		this(name, defaultMappings());
	}
	
	private static Map<String, String> defaultMappings() throws IOException{
		Properties p = new Properties();
		try(InputStream is = MarcaReader.class.getResourceAsStream("/marca-mappings.properties")) {
			p.load(is);
		}
		Map<String, String> res = new HashMap<>();
		for(String name: p.stringPropertyNames())
			res.put(name, p.getProperty(name));
		return res;		
	}
	
	@Override
	public Calendar read(InputStream calendarIs, String cs) throws IOException {
		Document doc = Jsoup.parse(calendarIs, cs, "https://www.marca.com/");
		Optional<Season> season = season(doc);
		Calendar.CalendarBuilder builder = Calendar.builder()
			.name(name)
			.season(season.orElse(Season.currentSeason()));
		Elements tables = doc.select("table.jor");
		log.debug("Found {} tables", tables.size());
		for (Element table : tables) {
			String round = table.select("caption").text();
			log.debug("Parsing table for round: {}", round);	
			for (Element matchTr : table.select("tr")) {
				Optional<Element> homeSpan = Optional.ofNullable(matchTr.select("td.local span").first());
				Optional<Element> awaySpan = Optional.ofNullable(matchTr.select("td.visitante span").first());
				if(homeSpan.isPresent() && awaySpan.isPresent()) {
					Match match = new Match(round, teamName(homeSpan.get().text()), teamName(awaySpan.get().text()));
					builder = builder.match(match);
					log.debug("Found match: {}", match);
					Optional<LocalDateTime> time = time(matchTr, season);
					if(time.isPresent()) {
						builder = builder.schedule(match, time.get());
					}
					Optional<Result> result = result(matchTr);
					if(result.isPresent()) {
						builder = builder.result(match, result.get());
					}
				}
			}
		}
		return builder.build();
	}

	private String teamName(String webPageName) {
		if(teamNamesMapping != null && teamNamesMapping.containsKey(webPageName)) {
			return Optional.ofNullable(teamNamesMapping.get(webPageName)).orElse(webPageName);
		}
		return webPageName;
	}
	
	static Optional<Result> result(Element matchTr){
		Optional<Integer> home = Optional
			.ofNullable(matchTr.select("td.resultado span.resultado-partido").first())
			.map(e -> e.text())
			.filter(str -> str.contains("-") && str.length() > 1)
			.map(str -> str.split("-")[0].trim())
			.map(Integer::valueOf);
		Optional<Integer> away = Optional
			.ofNullable(matchTr.select("td.resultado span.resultado-partido").first())
			.map(e -> e.text())
			.filter(str -> str.contains("-") && str.length() > 1)
			.map(str -> str.split("-")[1].trim())
			.map(Integer::valueOf);
		if(home.isPresent() && away.isPresent()) {
			return Optional.of(new Result(home.get(), away.get()));
		}else {
			return Optional.empty();
		}
	}
	
	static Optional<LocalDateTime> time(Element matchTr, Optional<Season> season){
		Optional<Integer> dayOfMonth = Optional
			.ofNullable(matchTr.select("td.resultado span.fecha").first())
			.map(e -> e.text())
			.filter(str -> str.contains("/"))
			.map(str -> str.split("/")[0])
			.map(Integer::valueOf);
		Optional<Integer> month = Optional
			.ofNullable(matchTr.select("td.resultado span.fecha").first())
			.map(e -> e.text())
			.filter(str -> str.contains("/"))
			.map(str -> str.split("/")[1])
			.map(Integer::valueOf);
		if(season.isPresent() && dayOfMonth.isPresent() && month.isPresent()) {
			Integer hour = Optional
				.ofNullable(matchTr.select("td.resultado span.hora").first())
				.map(e -> e.text())
				.filter(str -> str.contains(":"))
				.map(str -> str.split(":")[0])
				.map(Integer::valueOf)
				.orElse(18);
			Integer minute = Optional
				.ofNullable(matchTr.select("td.resultado span.hora").first())
				.map(e -> e.text())
				.filter(str -> str.contains(":"))
				.map(str -> str.split(":")[1])
				.map(Integer::valueOf)
				.orElse(0);
			return Optional.of(season.get().getTime(dayOfMonth.get(), month.get(), hour, minute));
		}
		return Optional.empty();
	}
	
	static Optional<Season> season(Document doc) {
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
	
}