package es.maxtuni.mp;

import java.net.URL;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "")
@Data
public class MarcaCalendarsConfig {

	private List<MarcaCalendar> marcaCalendars;
	
	@Data
	public static class MarcaCalendar {
		private String name;
		private URL url;
	}
	
}
