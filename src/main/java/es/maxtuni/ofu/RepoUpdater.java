package es.maxtuni.ofu;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import es.maxtuni.ofu.model.Calendar;
import es.maxtuni.ofu.reader.MarcaReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("!test")
public class RepoUpdater implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		RepoFolder repoFolder = repoConfig.getRepoFolder();
        repoFolder.init();
        OFFolder ofFolder = new OFFolder(repoConfig.getFolder());
		for(CalendarDetails cal : config.getCalendars()) {
			log.debug("Reading calendar from {}", cal.getUrl());
			try(InputStream is = cal.getUrl().openStream()) {
		        Calendar calendar = new MarcaReader(cal.getName()).read(is);
		        log.debug("Parsed {} matches, with {} schedules and {} results", calendar.getMatches().size(), calendar.getSchedules().size(), calendar.getResults().size());
		        ofFolder.updateCalendar(calendar, cal.getDest(), Locale.forLanguageTag(cal.getLocale()));
		    }	
		}
        repoFolder.publishChanges(Pattern.compile(".+\\.txt"), repoConfig.getEmail());
	}
	
	@Autowired
	private CalendarsConfig config;

	@Autowired
	private OFRepoConfig repoConfig;

	@Configuration
	@ConfigurationProperties(prefix = "")
	@Data
	public static class CalendarsConfig {
		private List<CalendarDetails> calendars;
	}
	
	@Data
	public static class CalendarDetails {
		private String name, dest, locale;
		private URL url;
	}
	
	@Configuration
	@ConfigurationProperties(prefix = "openfootball-repo")
	@Data
	static class OFRepoConfig {
		
		private String url, user, pw, email;
		private File folder;
		
		private RepoFolder getRepoFolder() {
			return new RepoFolder(folder, url, user, pw);
		}
		
	}

}
