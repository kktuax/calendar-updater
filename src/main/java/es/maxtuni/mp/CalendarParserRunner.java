package es.maxtuni.mp;

import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CalendarParserRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		try(InputStream is = calendarUrl.openStream()) {
	        Calendar calendar = MarcaCalendarParser.parse(is);
	        log.debug("Parsed {} matches", calendar.getMatches().size());
	    }
	}
	
	@Value("${calendar.url}")
	private URL calendarUrl;

}
