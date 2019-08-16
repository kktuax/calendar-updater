package es.maxtuni.mp;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import es.maxtuni.mp.CalendarParser.Match;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CalendarParserRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		try(InputStream is = calendarUrl.openStream()) {
	        List<Match> matches = CalendarParser.parse(is);
	        log.debug("Parsed {} matches", matches.size());
	    }
	}
	
	@Value("${calendar.url}")
	private URL calendarUrl;

}
