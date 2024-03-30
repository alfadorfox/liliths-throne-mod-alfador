package com.lilithsthrone.game.character.body.valueEnums;

import java.util.List;

import com.lilithsthrone.game.character.body.coverings.AbstractBodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.colours.Colour;
import com.lilithsthrone.utils.colours.PresetColour;

/**
 * @since 0.2.0
 * @version 0.4.9_alfador0.1.0
 * @author Innoxia
 */
public enum FluidTypeBase {
	
	CUM(Util.newArrayListOfValues("cum", "cream", "jism", "jizz", "seed", "spooge"),
			BodyCoveringType.CUM,
			PresetColour.CUM),
	
	GIRLCUM(Util.newArrayListOfValues("vaginal fluid", "discharge", "cunny juice"), // Alfador has changed this as always calling it "girlcum" isn't fair to characters who aren't girls but have a vagina.
			BodyCoveringType.GIRL_CUM, // ...not going to attempt to refactor the enums, heck no
			PresetColour.GIRLCUM),
	
	MILK(Util.newArrayListOfValues("milk"),
			BodyCoveringType.MILK,
			PresetColour.MILK),

	/**
	 * Alfador inserted code here to support fluid urine.
	 */
	URINE(Util.newArrayListOfValues("urine", "pee", "piss"),
			BodyCoveringType.URINE,
			PresetColour.URINE);
	/**
	 * End of Alfador-inserted code.
	 */
	
	private List<String> names;
	private AbstractBodyCoveringType coveringType;
	private Colour colour;

	private FluidTypeBase(List<String> names, AbstractBodyCoveringType coveringType, Colour colour) {
		this.names = names;
		this.coveringType = coveringType;
		this.colour = colour;
	}

	public List<String> getNames() {
		return names;
	}
	
	public AbstractBodyCoveringType getCoveringType() {
		return coveringType;
	}

	public Colour getColour() {
		return colour;
	}
}
