package es.maxtuni.mp;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import es.maxtuni.mp.MarcaCalendarsConfig.MarcaCalendar;
import es.maxtuni.mp.reader.MarcaCalendarReader;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CalendarParserRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		for(MarcaCalendar cal : config.getMarcaCalendars()) {
			try(InputStream is = cal.getUrl().openStream()) {
		        Calendar calendar = new MarcaCalendarReader().read(cal.getName(), is);
		        log.debug("Parsed {} matches", calendar.getMatches().size());
		    }	
		}
	}
	
	@Autowired
	private MarcaCalendarsConfig  config;

}
