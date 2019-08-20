package es.maxtuni.mp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import es.maxtuni.mp.model.Calendar;
import es.maxtuni.mp.reader.MarcaReader;
import es.maxtuni.mp.reader.OFReader;
import es.maxtuni.mp.writer.OFWriter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("!test")
public class CalendarUpdater implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		for(CalendarDetails cal : config.getCalendars()) {
			log.debug("Reading calendar from {}", cal.getUrl());
			try(InputStream is = cal.getUrl().openStream()) {
		        Calendar calendar = new MarcaReader(cal.getName()).read(is);
		        log.debug("Parsed {} matches, with {} schedules and {} results", calendar.getMatches().size(), calendar.getSchedules().size(), calendar.getResults().size());
		        File seasonFolder = new File(openfootballFolder, calendar.getSeason().toString().replaceAll("/", "-"));
		        File localCalendarFile = new File(seasonFolder, cal.getDest());
		        if(localCalendarFile.exists()) {
		        	try(InputStream lis = new FileInputStream(localCalendarFile)) {
		        		Calendar existingCalendar = new OFReader(cal.getName()).read(lis);
		        		calendar = existingCalendar.update(calendar);
		        	}
		        }else {
		        	if(localCalendarFile.getParentFile().isDirectory()) {
		        		if(!localCalendarFile.getParentFile().mkdirs()) {
		        			throw new FileNotFoundException(localCalendarFile.getParentFile().getAbsolutePath());
		        		}
		        	}
		        }
		        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localCalendarFile), "UTF-8"))){
		        	log.info("Writing result calendar to: {}", localCalendarFile);
	        		new OFWriter().write(calendar, writer);
	        	}
		    }	
		}
	}
	
	@Value("${openfootball-folder}")
	private File openfootballFolder;
	
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
