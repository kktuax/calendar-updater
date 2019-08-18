package es.maxtuni.mp.model;

import lombok.Data;

@Data
public class Match {
	
	private final String round, home, away;
	
	public boolean hasTeam(String name) {
		return name.equals(getHome()) || name.equals(getAway());
	}
	
}