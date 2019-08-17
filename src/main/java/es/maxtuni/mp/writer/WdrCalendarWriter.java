package es.maxtuni.mp.writer;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import es.maxtuni.mp.Calendar;
import es.maxtuni.mp.Calendar.Match;

public class WdrCalendarWriter implements Consumer<Calendar> {

	@Override
	public void accept(Calendar calendar) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		for(Match match : calendar.getMatches()) {
			if(match.hasTeam("Racing") && calendar.getSchedules().containsKey(match)) {
				String jornada = match.getRound().split(" ")[1];
				String time = formatter.format(calendar.getSchedules().get(match));
				System.out.println(String.format("%s|%s|%s|%s", time, jornada, match.getHome(), match.getAway()));	
			}
		}
	}

}
