package es.maxtuni.ofu.model;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.util.comparator.Comparators;

import lombok.Data;

@Data 
public class Season {
	
	final int start, end;

	public LocalDateTime getTime(int dayOfMonth, int month) {
		return getTime(dayOfMonth, month, 0, 0);
	}
	
	public LocalDateTime getTime(int dayOfMonth, int month, int hour, int minute) {
		int year = finalYearOfSeason(month) ? end : start;
		return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
	}

	static boolean finalYearOfSeason(int month){
		return month <= 7;
	}
	
	/**
	 * @param seasonStr e.g.: 2019/20
	 * @return
	 */
	public static Season from(String seasonStr) {
		String[] parts = seasonStr.split("/");
		String startStr = parts[0];
		String endStr = parts[1];
		if(endStr.length() < startStr.length()) {
			endStr = startStr.substring(0, startStr.length() - endStr.length()) + endStr;
		}
		return new Season(Integer.valueOf(startStr), Integer.valueOf(endStr));
	}
		
	public static Season from(Calendar calendar) {
		Optional<Integer> startYear = calendar.getSchedules().values().stream()
			.min(Comparators.comparable())
			.map(d -> d.getYear());
		Optional<Integer> endYear = calendar.getSchedules().values().stream()
			.max(Comparators.comparable())
			.map(d -> d.getYear());
		if(startYear.isPresent() && endYear.isPresent()) {
			return new Season(startYear.get(), endYear.get());	
		}else {
			return currentSeason();
		}
	}
	
	public static Season currentSeason() {
		LocalDateTime now = LocalDateTime.now();
		int startYear = finalYearOfSeason(now.getMonthValue()) ? now.getYear() -1 : now.getYear();
		int endYear = finalYearOfSeason(now.getMonthValue()) ? now.getYear() : now.getYear() + 1;
		return new Season(startYear, endYear);
	}


	/**
	 * Creates a string in the form of startYear/endYearWithoutPrefix, e.g.: 2019/20
	 */
	@Override
	public String toString() {
		String startYearStr = start + "";
		String endYearStr = end + "";
		String prefix = "";
		int minLength = Math.min(startYearStr.length(), endYearStr.length());
		for (int i = 0; i < minLength; i++) {
			if (startYearStr.charAt(i) != endYearStr.charAt(i)) {
				prefix = startYearStr.substring(0, i);
				break;
			}
		}		
		return String.format("%s/%s", startYearStr, endYearStr.substring(prefix.length()));
	}

}