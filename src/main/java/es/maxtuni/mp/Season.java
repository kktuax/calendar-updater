package es.maxtuni.mp;

import java.time.LocalDateTime;

import org.springframework.util.comparator.Comparators;

import lombok.Data;

@Data 
public class Season {
	
	final int start, end;
	
	public LocalDateTime getTime(int dayOfMonth, int month, int hour, int minute) {
		int year = finalYearOfSeason(month) ? end : start;
		return LocalDateTime.of(year, month, dayOfMonth, hour, minute);
	}

	static boolean finalYearOfSeason(int month){
		return month <= 7;
	}
	
	public static Season from(Calendar calendar) {
		LocalDateTime now = LocalDateTime.now();
		int startYear = calendar.getSchedules().values().stream()
			.min(Comparators.comparable())
			.map(d -> d.getYear())
			.orElse(finalYearOfSeason(now.getMonthValue()) ? now.getYear() -1 : now.getYear());
		int endYear = calendar.getSchedules().values().stream()
			.max(Comparators.comparable())
			.map(d -> d.getYear())
			.orElse(finalYearOfSeason(now.getMonthValue()) ? now.getYear() : now.getYear() + 1);
		return new Season(startYear, endYear);
	}

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