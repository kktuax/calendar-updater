package es.maxtuni.ofu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import es.maxtuni.ofu.model.Calendar;
import es.maxtuni.ofu.reader.OFReader;
import es.maxtuni.ofu.writer.OFWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
class OFFolder {

	private final File folder;
	
	/** updates calendar file, or creates if if folder/season/dest is not existing
	 * @param calendar
	 * @param dest name of calendar file in folder e.g. 1-liga.txt
	 * @throws IOException
	 */
	public void updateCalendar(Calendar calendar, String dest, Locale locale) throws IOException {
		File seasonFolder = new File(folder, calendar.getSeason().toString().replaceAll("/", "-"));
        File localCalendarFile = new File(seasonFolder, dest);
        if(localCalendarFile.exists()) {
        	try(InputStream lis = new FileInputStream(localCalendarFile)) {
        		Calendar existingCalendar = new OFReader(calendar.getName()).read(lis);
        		calendar = existingCalendar.update(calendar);
        	}
        }else {
        	if(localCalendarFile.getParentFile().isDirectory()) {
        		if(!localCalendarFile.getParentFile().mkdirs()) {
        			throw new FileNotFoundException(localCalendarFile.getParentFile().getAbsolutePath());
        		}
        	}
        }
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localCalendarFile), "UTF-8"))){
        	log.info("Writing result calendar to: {}", localCalendarFile);
    		new OFWriter().write(calendar, writer, locale);
    	}
	}
	
}
