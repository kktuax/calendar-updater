package es.maxtuni.mp.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.maxtuni.mp.model.Calendar;
import es.maxtuni.mp.model.Match;
import es.maxtuni.mp.model.Result;
import es.maxtuni.mp.model.Season;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OFReader implements CalendarReader {

	private final String name;
	
	@Override
	public Calendar read(InputStream calendarIs) throws IOException {
		Calendar.CalendarBuilder builder = Calendar.builder()
			.name(name);
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(calendarIs, "UTF-8"))){
			String line = null, round = null;
			Integer dayOfMonth = null, month = null;
			Season season = null;
			while((line = reader.readLine()) != null) {
				if(line.trim().isEmpty()) {
					round = null;
				}else if(season == null && line.contains(name)) {
					season = Season.from(line.substring(line.indexOf(name) + name.length()).trim());
					builder = builder.season(season);
				}else if(round == null) {
					round = line;
				}else{
					Matcher dateLineMatcher = DATE_LINE_PATTERN.matcher(line), 
						resultLineMatcher = RESULT_LINE_PATTERN.matcher(line);
					if(dateLineMatcher.matches()) {
						dayOfMonth = Integer.valueOf(dateLineMatcher.group(1));
						month = Integer.valueOf(dateLineMatcher.group(2));
					}else if(resultLineMatcher.matches()) {
						Match match = new Match(round, resultLineMatcher.group(2).trim(), resultLineMatcher.group(5).trim());
						builder = builder.match(match);
						String resultHome = resultLineMatcher.group(3), resultAway = resultLineMatcher.group(4);
						if(resultHome != null && resultAway != null) {
							Result result = new Result(Integer.valueOf(resultHome), Integer.valueOf(resultAway));
							log.debug("Saving result {} for match: {}", result, match);
							builder = builder.result(match, result);
						}
						LocalDateTime time;
						String timeStr = resultLineMatcher.group(1);
						if(timeStr != null) {
							String[] parts = timeStr.split(":");
							time = season.getTime(dayOfMonth, month, Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
						}else {
							time = season.getTime(dayOfMonth, month);
						}
						builder = builder.schedule(match, time);
						log.debug("Saving match {} at time: {}", match, time);
					}
				}
			}
		}
		return builder.build();
	}
	
	static final Pattern RESULT_LINE_PATTERN = Pattern.compile("\\s*(\\d\\d:\\d\\d)?\\s*(.+)\\s+(\\d+)?-(\\d+)?\\s+(.+)\\s*"),
		DATE_LINE_PATTERN = Pattern.compile("\\s*\\[.{3}\\.\\s(\\d+)\\.(\\d+)\\.\\]\\s*");
	
}
