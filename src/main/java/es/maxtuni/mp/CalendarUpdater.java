package es.maxtuni.mp;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import es.maxtuni.mp.reader.MarcaCalendarReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CalendarUpdater implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		MarcaCalendarReader reader = new MarcaCalendarReader();
		for(CalendarDetails cal : config.getCalendars()) {
			log.debug("Reading calendar from {}", cal.getUrl());
			try(InputStream is = cal.getUrl().openStream()) {
		        Calendar calendar = reader.read(cal.getName(), is);
		        log.debug("Parsed {} matches, with {} schedules and {} results", calendar.getMatches().size(), calendar.getSchedules().size(), calendar.getResults().size());
		    }	
		}
	}
	
	@Autowired
	private CalendarsConfig config;

	@Component
	@ConfigurationProperties(prefix = "")
	@Data
	public static class CalendarsConfig {
		private List<CalendarDetails> calendars;
	}
	
	@Data
	public static class CalendarDetails {
		private String name, dest;
		private URL url;
	}
	
}
