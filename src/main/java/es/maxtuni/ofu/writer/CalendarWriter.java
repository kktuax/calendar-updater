package es.maxtuni.ofu.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Locale;

import es.maxtuni.ofu.model.Calendar;

@FunctionalInterface
public interface CalendarWriter {

	public void write(Calendar calendar, BufferedWriter writer, Locale locale) throws IOException;
	
}
