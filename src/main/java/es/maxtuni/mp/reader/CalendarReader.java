package es.maxtuni.mp.reader;

import java.io.IOException;
import java.io.InputStream;

import es.maxtuni.mp.Calendar;

public interface CalendarReader {

	public Calendar read(String name, InputStream calendarIs) throws IOException;
	
}