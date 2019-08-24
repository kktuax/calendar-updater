package es.maxtuni.ofu.reader;

import java.io.IOException;
import java.io.InputStream;

import es.maxtuni.ofu.model.Calendar;

public interface CalendarReader {

	public Calendar read(InputStream calendarIs) throws IOException;
	
}
