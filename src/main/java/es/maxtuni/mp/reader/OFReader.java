package es.maxtuni.mp.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import es.maxtuni.mp.model.Calendar;
import es.maxtuni.mp.model.Season;

public class OFReader implements CalendarReader {

	@Override
	public Calendar read(String name, InputStream calendarIs) throws IOException {
		Calendar.CalendarBuilder builder = Calendar.builder()
			.name(name);
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(calendarIs, "UTF-8"))){
			String line;
			Season season = null;
			while((line = reader.readLine()) != null) {
				if(line.trim().isEmpty()) {
					continue;
				}else if(season == null && line.contains(name)) {
					season = Season.from(line.substring(line.indexOf(name) + name.length()).trim());
					builder = builder.season(season);
				}
			}
		}
		return builder.build();
	}

}
