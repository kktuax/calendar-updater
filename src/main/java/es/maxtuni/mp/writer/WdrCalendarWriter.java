package es.maxtuni.mp.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import es.maxtuni.mp.Calendar;
import es.maxtuni.mp.Calendar.Match;

public class WdrCalendarWriter implements CalendarWriter {

	@Override
	public void write(Calendar calendar, BufferedWriter writer) throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		for(Match match : calendar.getMatches()) {
			if(match.hasTeam("Racing") && calendar.getSchedules().containsKey(match)) {
				String jornada = match.getRound().split(" ")[1];
				String time = formatter.format(calendar.getSchedules().get(match));
				writer.write(String.format("%s|%s|%s|%s", time, jornada, match.getHome(), match.getAway()));	
				writer.newLine();
				writer.flush();
			}
		}
	}
	
}
