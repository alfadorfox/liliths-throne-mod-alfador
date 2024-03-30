package com.lilithsthrone.game.character.body;

import com.lilithsthrone.controller.xmlParsing.XMLUtil;
import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.body.abstractTypes.AbstractFluidType;
import com.lilithsthrone.game.character.body.types.FluidType;
import com.lilithsthrone.game.character.body.valueEnums.FluidFlavour;
import com.lilithsthrone.game.character.body.valueEnums.FluidModifier;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.game.inventory.enchanting.ItemEffect;
import com.lilithsthrone.utils.Util;
import com.lilithsthrone.utils.XMLSaving;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Urine fluid to separate it out from existing bodily fluids.
 * @since 0.4.9_alfador0.1.0
 * @version 0.4.9_alfador0.1.0
 * @author Alfador
 */
public class FluidUrine implements FluidInterface, XMLSaving {

    protected AbstractFluidType type;
    protected FluidFlavour flavour;
    protected List<FluidModifier> fluidModifiers;
    protected List<ItemEffect> transformativeEffects;

    /**
     * TODO:
     * setFlavour
     * hasFluidModifier
     * addFluidModifier (default should include MUSKY if musk content enabled)
     * removeFluidModifier
     * getTransformativeEffects
     * addTransformativeEffect
     */
	public FluidUrine(AbstractFluidType type) {
		this.type = type;
		this.flavour = type.getFlavour();
		transformativeEffects = new ArrayList<>();

		fluidModifiers = new ArrayList<>();
		fluidModifiers.addAll(type.getDefaultFluidModifiers());
	}

	public Element saveAsXML(Element parentElement, Document doc) {
		Element element = doc.createElement("urine");
		parentElement.appendChild(element);

		XMLUtil.addAttribute(doc, element, "type", FluidType.getIdFromFluidType(this.type));
		XMLUtil.addAttribute(doc, element, "flavour", this.flavour.toString());

		Element urineModifiers = doc.createElement("urineModifiers");
		element.appendChild(urineModifiers);
		for(FluidModifier fm : this.getFluidModifiers()) {
			XMLUtil.addAttribute(doc, urineModifiers, fm.toString(), "true");
		}

		return element;
	}

	public static FluidUrine loadFromXML(Element parentElement, Document doc) {
		return loadFromXML(parentElement, doc, null);
	}

	/**
	 *
	 * @param parentElement
	 * @param doc
	 * @param baseType If you pass in a baseType, this method will ignore the saved type in parentElement.
	 */
	public static FluidUrine loadFromXML(Element parentElement, Document doc, AbstractFluidType baseType) {
		Element urine = (Element)parentElement.getElementsByTagName("urine").item(0);
		AbstractFluidType fluidType = FluidType.URINE_HUMAN;

		if(baseType!=null) {
			fluidType = baseType;

		} else {
			try {
				fluidType = FluidType.getFluidTypeFromId(urine.getAttribute("type"));
			} catch(Exception ex) {
			}
		}

		FluidUrine fluidUrine = new FluidUrine(fluidType);

		String flavourId = urine.getAttribute("flavour");
		if(flavourId.equalsIgnoreCase("SLIME")) {
			fluidUrine.flavour = FluidFlavour.BUBBLEGUM;
		} else {
			fluidUrine.flavour = FluidFlavour.valueOf(flavourId);
		}


		Element urineModifiersElement = (Element)urine.getElementsByTagName("urineModifiers").item(0);
		fluidUrine.fluidModifiers.clear();
		if(urineModifiersElement!=null) {
			Collection<FluidModifier> urineFluidModifiers = fluidUrine.fluidModifiers;
			Body.handleLoadingOfModifiers(FluidModifier.values(), null, urineModifiersElement, urineFluidModifiers);
		}

		return fluidUrine;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof FluidUrine){
			if(((FluidUrine)o).getType().equals(this.getType())
					&& ((FluidUrine)o).getFlavour() == this.getFlavour()
					&& ((FluidUrine)o).getFluidModifiers().equals(this.getFluidModifiers())
					&& ((FluidUrine)o).getTransformativeEffects().equals(this.getTransformativeEffects())){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + this.getType().hashCode();
		result = 31 * result + this.getFlavour().hashCode();
		result = 31 * result + this.getFluidModifiers().hashCode();
		result = 31 * result + this.getTransformativeEffects().hashCode();
		return result;
	}

	@Override
	public String getDeterminer(GameCharacter gc) {
		return type.getDeterminer(gc);
	}

	@Override
	public String getName(GameCharacter gc) {
		return type.getName(gc);
	}

	@Override
	public String getNameSingular(GameCharacter gc) {
		return type.getNameSingular(gc);
	}

	@Override
	public String getNamePlural(GameCharacter gc) {
		return type.getNamePlural(gc);
	}

	@Override
	public String getDescriptor(GameCharacter gc) {
		String modifierDescriptor = "";
		if(!fluidModifiers.isEmpty()) {
			modifierDescriptor = fluidModifiers.get(Util.random.nextInt(fluidModifiers.size())).getName();
		}

		return UtilText.returnStringAtRandom(
				modifierDescriptor,
				flavour.getRandomFlavourDescriptor(),
				type.getDescriptor(gc));
	}

	@Override
	public AbstractFluidType getType() {
		return type;
	}

	public void setType(AbstractFluidType type) {
		this.type = type;
	}

	public FluidFlavour getFlavour() {
		return flavour;
	}

	public String setFlavour(GameCharacter owner, FluidFlavour flavour) {
		if (owner == null) {
			this.flavour = flavour;
			return "";
		}
		if(this.flavour == flavour) {
			return "<p style='text-align:center;'>[style.colourDisabled(Nothing happens...)]</p>";
		}

		this.flavour = flavour;

		return UtilText.parse(owner,
				"<p>"
						+ "A soothing warmth spreads down into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out an involuntary [npc.moan].<br/>"
						+ "[npc.NamePos] [npc.urine] "
						+ (flavour==FluidFlavour.FLAVOURLESS
						?"is now <b style='color:"+flavour.getColour().toWebHexString()+";'>"+flavour.getName()+"</b>"
						:"now tastes of <b style='color:"+flavour.getColour().toWebHexString()+";'>"+flavour.getName()+"</b>.")
						+ "</p>");
	}

	public boolean hasFluidModifier(FluidModifier fluidModifier) {
		return fluidModifiers.contains(fluidModifier);
	}

	public String addFluidModifier(GameCharacter owner, FluidModifier fluidModifier) {
		if(owner==null && !fluidModifiers.contains(fluidModifier)) {
			fluidModifiers.add(fluidModifier);
			return "";
		}
		if(fluidModifiers.contains(fluidModifier)) {
			return "<p style='text-align:center;'>[style.colourDisabled(Nothing happens...)]</p>";
		}

		fluidModifiers.add(fluidModifier);

		switch(fluidModifier) {
			case ADDICTIVE:
				return UtilText.parse(owner,
						"<p>"
								+ "A strange, pulsating heat takes root deep within [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(addictive)]!"
								+ "</p>");
			case ALCOHOLIC:
				fluidModifiers.remove(FluidModifier.ALCOHOLIC_WEAK);
				return UtilText.parse(owner,
						"<p>"
								+ "A strange, soothing warmth takes root deep within [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(strongly alcoholic)]!"
								+ "</p>");
			case ALCOHOLIC_WEAK:
				fluidModifiers.remove(FluidModifier.ALCOHOLIC);
				return UtilText.parse(owner,
						"<p>"
								+ "A strange, soothing warmth takes root deep within [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(alcoholic)]!"
								+ "</p>");
			case BUBBLING:
				return UtilText.parse(owner,
						"<p>"
								+ "A light, bubbly feeling rises up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(bubbly)]!"
								+ "</p>");
			case HALLUCINOGENIC:
				return UtilText.parse(owner,
						"<p>"
								+ "A series of strange pulses shoot down into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(psychoactive)]!"
								+ "</p>");
			case MINERAL_OIL:
				return UtilText.parse(owner,
						"<p>"
								+ "A soothing warmth flows into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now imbued with [style.boldGrow(mineral oil)], and can melt condoms!"
								+ "</p>");
			case MUSKY:
				return UtilText.parse(owner,
						"<p>"
								+ "A slow, creeping warmth rises up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(musky)]!"
								+ "</p>");
			case SLIMY:
				return UtilText.parse(owner,
						"<p>"
								+ "A strange, soothing warmth flows up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(slimy)]!"
								+ "</p>");
			case STICKY:
				return UtilText.parse(owner,
						"<p>"
								+ "A thick, sickly warmth flows up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(sticky)]!"
								+ "</p>");
			case VISCOUS:
				return UtilText.parse(owner,
						"<p>"
								+ "A heavy heat slowly rises up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out [npc.a_moan+].<br/>"
								+ "[npc.NamePos] [npc.urine] is now [style.boldGrow(viscous)]!"
								+ "</p>");
		}

		return "<p style='text-align:center;'>[style.colourDisabled(Nothing happens...)]</p>";
	}

	public String removeFluidModifier(GameCharacter owner, FluidModifier fluidModifier) {
		if(!fluidModifiers.contains(fluidModifier) || !owner.hasVagina()) {
			return "<p style='text-align:center;'>[style.colourDisabled(Nothing happens...)]</p>";
		}

		fluidModifiers.remove(fluidModifier);

		switch(fluidModifier) {
			case ADDICTIVE:
				return UtilText.parse(owner,
						"<p>"
								+ "A soft coolness spreads up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a gentle sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer addictive)]!"
								+ "</p>");
			case ALCOHOLIC:
			case ALCOHOLIC_WEAK:
				return UtilText.parse(owner,
						"<p>"
								+ "A soft coolness spreads up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a gentle sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer alcoholic)]!"
								+ "</p>");
			case BUBBLING:
				return UtilText.parse(owner,
						"<p>"
								+ "A calm, settling feeling spreads up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a gentle sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer bubbly)]!"
								+ "</p>");
			case HALLUCINOGENIC:
				return UtilText.parse(owner,
						"<p>"
								+ "A series of soothing waves wash up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a gentle sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer psychoactive)]!"
								+ "</p>");
			case MUSKY:
				return UtilText.parse(owner,
						"<p>"
								+ "A gentle coolness rises up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a soft sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer musky)]!"
								+ "</p>");
			case SLIMY:
				return UtilText.parse(owner,
						"<p>"
								+ "A calming coolness flows up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a gentle sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer slimy)]!"
								+ "</p>");
			case STICKY:
				return UtilText.parse(owner,
						"<p>"
								+ "A soft warmth flows up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a gentle sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer sticky)]!"
								+ "</p>");
			case VISCOUS:
				return UtilText.parse(owner,
						"<p>"
								+ "A soft coolness rises up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a gentle sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer viscous)]!"
								+ "</p>");
			case MINERAL_OIL:
				return UtilText.parse(owner,
						"<p>"
								+ "A short relief flows up into [npc.namePos] [npc.bladder], causing [npc.herHim] to let out a gentle sigh.<br/>"
								+ "[npc.NamePos] [npc.urine] is [style.boldShrink(no longer mineral oil)]!"
								+ "</p>");
		}

		return "<p style='text-align:center;'>[style.colourDisabled(Nothing happens...)]</p>";
	}

	public List<ItemEffect> getTransformativeEffects() {
		return transformativeEffects;
	}

	public void addTransformativeEffect(ItemEffect ie) {
		transformativeEffects.add(ie);
	}

	/**
	 * DO NOT MODIFY! (this notice copied from its appearance in FluidCum, FluidGirlCum, and FluidMilk classes.
	 */
	public List<FluidModifier> getFluidModifiers() {
		return fluidModifiers;
	}

	public void clearFluidModifiers() {
		fluidModifiers.clear();
	}

	public float getValuePerMl() {
		return 0.01f; // value copied from milk, on the assumption that similar volumes of output availability exist
	}

	@Override
	public boolean isFeral(GameCharacter owner) {
		if(owner==null) {
			return false;
		}
		return owner.isFeral() || (owner.getLegConfiguration().getFeralParts().contains(FluidUrine.class) && getType().getRace().isFeralPartsAvailable());
	}
}
