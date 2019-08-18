package es.maxtuni.mp.writer;

import java.io.BufferedWriter;
import java.io.IOException;

import es.maxtuni.mp.model.Calendar;

@FunctionalInterface
public interface CalendarWriter {

	public void write(Calendar calendar, BufferedWriter writer) throws IOException;
	
}
