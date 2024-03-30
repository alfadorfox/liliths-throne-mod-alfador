package com.lilithsthrone.game.character.gender;

/**
 * @since 0.1.86
 * @version 0.4.9_alfador0.1.0
 * @author Innoxia
 */
public enum GenderNames {
	// Alfador has altered the default entries here to avoid the more egregious slurs. Yes there's still some porn-derived terms in here; it's a porn game.
	//  The options do still exist in-game to change them to what you think is appropriate. eg. "busty-boy" wasn't even all that offensive, but it seemed
	//  weird that you'd do that but not call N_Y_N_F a "flat-girl" or something.
	Y_PENIS_Y_VAGINA_Y_BREASTS(true, true, true, "hermaphrodite", "hermaphrodite", "hermaphrodite"),
	Y_PENIS_Y_VAGINA_N_BREASTS(true, true, false, "hermaphrodite", "hermaphrodite", "hermaphrodite"),
	Y_PENIS_N_VAGINA_Y_BREASTS(true, false, true, "dickgirl", "enby", "male"),
	Y_PENIS_N_VAGINA_N_BREASTS(true, false, false, "dickgirl", "enby", "male"),
	N_PENIS_Y_VAGINA_Y_BREASTS(false, true, true, "female", "enby",  "cuntboy"),
	N_PENIS_Y_VAGINA_N_BREASTS(false, true, false, "female", "enby", "cuntboy"),
	N_PENIS_N_VAGINA_Y_BREASTS(false, false, true, "neuter", "neuter", "neuter"),
	N_PENIS_N_VAGINA_N_BREASTS(false, false, false, "neuter", "neuter", "neuter");
	
	
	private boolean hasPenis, hasVagina, hasBreasts;
	private String feminine, masculine, neutral;
	
	private GenderNames(boolean hasPenis, boolean hasVagina, boolean hasBreasts, String feminine, String neutral, String masculine){
		this.hasPenis = hasPenis;
		this.hasVagina = hasVagina;
		this.hasBreasts = hasBreasts;
		this.feminine = feminine;
		this.neutral = neutral;
		this.masculine = masculine;
	}

	public boolean isHasPenis() {
		return hasPenis;
	}

	public boolean isHasVagina() {
		return hasVagina;
	}

	public boolean isHasBreasts() {
		return hasBreasts;
	}
	
	public String getFeminine() {
		return feminine;
	}

	public String getMasculine() {
		return masculine;
	}

	public String getNeutral() {
		return neutral;
	}
}
