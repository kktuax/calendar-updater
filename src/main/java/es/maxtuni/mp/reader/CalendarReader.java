package es.maxtuni.mp.reader;

import java.io.IOException;
import java.io.InputStream;

import es.maxtuni.mp.model.Calendar;

public interface CalendarReader {

	public Calendar read(InputStream calendarIs) throws IOException;
	
}
