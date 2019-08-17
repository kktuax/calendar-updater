package es.maxtuni.mp;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import es.maxtuni.mp.CalendarParser.Match;

public class WdrCalendarConsumer implements Consumer<List<Match>> {

	@Override
	public void accept(List<Match> matches) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		for(Match match : matches) {
			if(match.hasTeam("Racing")) {
				String jornada = match.getRound().split(" ")[1];
				String time = formatter.format(match.getTime());
				System.out.println(String.format("%s|%s|%s|%s", time, jornada, match.getHome(), match.getAway()));	
			}
		}
	}

}
