package com.lilithsthrone.game.inventory.item;

import com.lilithsthrone.controller.xmlParsing.XMLUtil;
import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.body.*;
import com.lilithsthrone.game.inventory.ItemTag;
import com.lilithsthrone.game.inventory.clothing.AbstractClothing;
import com.lilithsthrone.game.inventory.clothing.AbstractClothingType;
import com.lilithsthrone.game.inventory.enchanting.ItemEffect;
import com.lilithsthrone.game.inventory.enchanting.TFModifier;
import com.lilithsthrone.utils.XMLSaving;
import com.lilithsthrone.utils.colours.Colour;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diaper items can absorb multiple fluid types and should handle displaying what they've soaked up.
 * @since 0.4.9_alfador0.1.0
 * @version 0.4.9_alfador0.1.0
 * @author Alfador
 */
public class AbstractDiaper extends AbstractClothing implements XMLSaving {
    private static final int DEFAULT_DIAPER_CAPACITY = 5000;
    private static final String XML_MAIN_TAG = "diaper";
    private static final String XML_CAPACITY_ATTR = "capacityMl";
    private static final String XML_IGNORE_CAPACITY_ATTR = "ignoreCapacity";
    private static final String XML_WASHABLE_ATTR = "washable";
    private static final String XML_FLUIDS_TAG = "fluidsAbsorbed";
    private static final String XML_FLUID_ENTRY_TAG = "fluid";
    private static final String XML_FLUID_AMOUNT_ATTR = "amt";
    private static final String XML_FLUID_URINE_TAG = "urine";
    private static final String XML_FLUID_MILK_TAG = "milk";
    private static final String XML_FLUID_CROTCHMILK_TAG = "milkCrotch";
    private static final String XML_FLUID_CUM_TAG = "cum";
    private static final String XML_FLUID_VAGCUM_TAG = "girlcum";

    private Map<FluidInterface, Integer> fluidsAbsorbed;
    private int capacityMl;
    private boolean ignoreCapacity; // for hyper diapers which never stop absorbing! They still get dirty at 50% normal max capacity, though.
    private boolean washable; // if true, a normal clothing wash effect will drain all fluids. If false, it can't be cleaned past 50% max capacity!

    // Hack to work around the fact that a diaper that's not *externally* dirty will be "washed" as part of loading a save file.
    private boolean isLoadingFromXml = false;

    /**
     * TODO:
     * figure out how to have separate images for folded clean, worn clean, worn dirty(could just alter color?), and rolled dirty
     * change value based on fluids stored--valuable fluids could be magically extracted and gives you a sell incentive for your dirty disposables
     */

    public AbstractDiaper(AbstractClothingType clothingType, List<Colour> colours, boolean allowRandomEnchantment) {
        super(clothingType, colours, allowRandomEnchantment);
        this.initDiaperAttributes(clothingType);
    }

    public AbstractDiaper(AbstractClothingType clothingType, List<Colour> colours, List<ItemEffect> effects) {
        super(clothingType, colours, effects);
        this.initDiaperAttributes(clothingType);
    }

    public AbstractDiaper(AbstractDiaper diaper) {
        super(diaper);
        this.fluidsAbsorbed = diaper.fluidsAbsorbed;
        this.capacityMl = diaper.capacityMl;
        this.ignoreCapacity = diaper.ignoreCapacity;
        this.washable = diaper.washable;
    }

    private void initDiaperAttributes(AbstractClothingType clothingType) {
        this.fluidsAbsorbed = new HashMap<>();
        this.capacityMl = DEFAULT_DIAPER_CAPACITY;
        if(clothingType.getDefaultItemTags().contains(ItemTag.WASHABLE_DIAPER)) {
            capacityMl /= 2;
        }
        if(clothingType.getDefaultItemTags().contains(ItemTag.PULLUP)) {
            capacityMl /= 5;
        }
        this.ignoreCapacity = clothingType.getDefaultItemTags().contains(ItemTag.HYPER_DIAPER);
        this.washable = clothingType.getDefaultItemTags().contains(ItemTag.WASHABLE_DIAPER);

        for(ItemEffect ie : this.effects) {
            if(ie.getPrimaryModifier() == TFModifier.CLOTHING_DIAPER) {
                switch (ie.getPotency()) {
                    case MAJOR_DRAIN:
                        capacityMl /= 4;
                        break;
                    case DRAIN:
                        capacityMl /= 2;
                        break;
                    case MINOR_DRAIN:
                        capacityMl = capacityMl * 3 / 4;
                        break;
                    case MINOR_BOOST:
                        capacityMl = capacityMl * 5 / 4;
                        break;
                    case BOOST:
                        capacityMl *= 2;
                        break;
                    case MAJOR_BOOST:
                        capacityMl *= 3;
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public Element saveAsXML(Element parentElement, Document doc) {
        Element clothingElement = super.saveAsXML(parentElement, doc);
        Element diaperElement = doc.createElement(XML_MAIN_TAG);
        clothingElement.appendChild(diaperElement);

        XMLUtil.addAttribute(doc, diaperElement, XML_CAPACITY_ATTR, String.valueOf(this.capacityMl));
        XMLUtil.addAttribute(doc, diaperElement, XML_IGNORE_CAPACITY_ATTR, String.valueOf(this.ignoreCapacity));
        XMLUtil.addAttribute(doc, diaperElement, XML_WASHABLE_ATTR, String.valueOf(this.washable));

        if (!this.fluidsAbsorbed.isEmpty()) {
            Element fluidsElement = doc.createElement(XML_FLUIDS_TAG);
            diaperElement.appendChild(fluidsElement);
            for (FluidInterface eachFluid : fluidsAbsorbed.keySet()) {
                Element fElement = doc.createElement(XML_FLUID_ENTRY_TAG);
                fluidsElement.appendChild(fElement);
                XMLUtil.addAttribute(doc, fElement, XML_FLUID_AMOUNT_ATTR, String.valueOf(fluidsAbsorbed.get(eachFluid)));
                if (eachFluid instanceof FluidUrine) {
                    ((FluidUrine)eachFluid).saveAsXML(fElement, doc);
                } else if (eachFluid instanceof FluidCum) {
                    ((FluidCum) eachFluid).saveAsXML(fElement, doc);
                } else if (eachFluid instanceof FluidGirlCum) {
                    ((FluidGirlCum) eachFluid).saveAsXML(fElement, doc);
                } else if (eachFluid instanceof FluidMilk) {
                    if (((FluidMilk) eachFluid).isCrotchMilk()) {
                        ((FluidMilk) eachFluid).saveAsXML(XML_FLUID_CROTCHMILK_TAG, fElement, doc);
                    } else {
                        ((FluidMilk) eachFluid).saveAsXML(XML_FLUID_MILK_TAG, fElement, doc);
                    }
                }
            }
        }
        return clothingElement;
    }

    public void loadDiaperValuesFromXML(Element parentElement, Document doc) {
        isLoadingFromXml = true;
        try {
            Element diaperElement = (Element) parentElement.getElementsByTagName(XML_MAIN_TAG).item(0);
            if(!diaperElement.getAttribute(XML_CAPACITY_ATTR).isEmpty()) {
                this.capacityMl = Integer.parseInt(diaperElement.getAttribute(XML_CAPACITY_ATTR));
            }

            if(!diaperElement.getAttribute(XML_IGNORE_CAPACITY_ATTR).isEmpty()) {
                this.ignoreCapacity = Boolean.parseBoolean(diaperElement.getAttribute(XML_IGNORE_CAPACITY_ATTR));
            }

            if(!diaperElement.getAttribute(XML_WASHABLE_ATTR).isEmpty()) {
                this.washable = Boolean.parseBoolean(diaperElement.getAttribute(XML_WASHABLE_ATTR));
            }

            Element fluidsElement = (Element) diaperElement.getElementsByTagName(XML_FLUIDS_TAG).item(0);
            if(fluidsElement!=null) {
                NodeList nodes = fluidsElement.getElementsByTagName(XML_FLUID_ENTRY_TAG);
				for(int i=0; i<nodes.getLength(); i++) {
                    Element fElement = (Element) nodes.item(i);
                    String amtString = fElement.getAttribute(XML_FLUID_AMOUNT_ATTR);
                    if(!amtString.isEmpty()) {
                        int amount = Integer.parseInt(amtString);
                        Element urine = (Element)fElement.getElementsByTagName(XML_FLUID_URINE_TAG).item(0);
                        Element milk = (Element)fElement.getElementsByTagName(XML_FLUID_MILK_TAG).item(0);
                        Element crotchMilk = (Element)fElement.getElementsByTagName(XML_FLUID_CROTCHMILK_TAG).item(0);
                        Element cum = (Element)fElement.getElementsByTagName(XML_FLUID_CUM_TAG).item(0);
                        Element vagCum = (Element)fElement.getElementsByTagName(XML_FLUID_VAGCUM_TAG).item(0);
                        if (urine != null) {
                            FluidUrine absorbedUrine = FluidUrine.loadFromXML(fElement, doc);
                            this.absorbExactly(absorbedUrine, amount);
                        }
                        if (milk != null) {
                            FluidMilk absorbedMilk = FluidMilk.loadFromXML(XML_FLUID_MILK_TAG, fElement, doc);
                            this.absorbExactly(absorbedMilk, amount);
                        }
                        if (crotchMilk != null) {
                            FluidMilk absorbedCrotchMilk = FluidMilk.loadFromXML(XML_FLUID_CROTCHMILK_TAG, fElement, doc);
                            this.absorbExactly(absorbedCrotchMilk, amount);
                        }
                        if (cum != null) {
                            FluidCum absorbedCum = FluidCum.loadFromXML(fElement, doc);
                            this.absorbExactly(absorbedCum, amount);
                        }
                        if (vagCum != null) {
                            FluidGirlCum absorbedVagCum = FluidGirlCum.loadFromXML(fElement, doc);
                            this.absorbExactly(absorbedVagCum, amount);
                        }
					}
				}
            }
        } catch (Exception ex) { // TODO: some actual log when this fails to load. Swallowing it is just not right even if it's what AbstractClothing does.
            System.out.println("DEBUG: Failed to load diaper! Exception: " + ex.toString());
            ex.printStackTrace(System.out);
        }
    }


    public Map<FluidInterface, Integer> getFluidsAbsorbed() {
        return fluidsAbsorbed;
    }

    public boolean isWashable() {
        return this.washable;
    }

    public boolean isIgnoreCapacity() {
        return this.ignoreCapacity;
    }

    public int getFullCapacity() {
        return this.capacityMl;
    }

    // TODO: maybe some diapers could stay undirty longer, or some like training pants get visibly dirty with any wetness?
    public int getCapacityTillDirty() {
        return this.capacityMl / 2;
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || (2 * this.getTotalAbsorbedAmount()) > capacityMl;
    }

    /**
     * If this diaper isn't washable, refuse to clean it--otherwise, empty out the fluids!
     */
    @Override
    public void setDirty(GameCharacter owner, boolean dirty) {
        if(!dirty && !this.isWashable()) {
            return;
        }
        if(owner!=null) {
            if(owner.getClothingCurrentlyEquipped().contains(this)) {
                AbstractDiaper c = new AbstractDiaper(this) {};
                owner.forceUnequipClothingIntoVoid(owner, this);
                c.forceSetDirty(dirty);
                if (!dirty && !isLoadingFromXml) {
                    c.clearStoredFluids();
                }
                owner.equipClothingOverride(c, c.getSlotEquippedTo(), false, false);

            } else if(owner.removeClothing(this)) {
                AbstractDiaper c = new AbstractDiaper(this) {};
                c.forceSetDirty(dirty);
                if (!dirty && !isLoadingFromXml) {
                    c.clearStoredFluids();
                }
                owner.addClothing(c, false);

            } else {
                this.forceSetDirty(dirty);
                if (!dirty && !isLoadingFromXml) {
                    this.clearStoredFluids();
                }
            }
        } else {
            this.forceSetDirty(dirty);
            if (!dirty && !isLoadingFromXml) { // technically this should be the only place we need to check isLoadingFromXml but I put it before every other clearStoredFluids just in case I missed something.
                this.clearStoredFluids();
            }
        }
        isLoadingFromXml = false;
    }

    /**
     * This equality check excludes equip slot checks, as it is intended to be used for if an unequipped item of clothing is to be compared to an equipped item of clothing.
     */
    public boolean equalsWithoutEquippedSlot(Object o) {
        return super.equalsWithoutEquippedSlot(o) && this.equalsByFields(o);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && this.equalsByFields(o);
    }

    private boolean equalsByFields(Object o) {
        return o instanceof AbstractDiaper
            && ((AbstractDiaper)o).isWashable() == this.isWashable()
            && ((AbstractDiaper)o).isIgnoreCapacity() == this.isIgnoreCapacity()
            && ((AbstractDiaper)o).getFullCapacity() == this.getFullCapacity();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.isWashable() ? 1 : 0);
        result = 31 * result + (this.isIgnoreCapacity() ? 1 : 0);
        result = 31 * result + this.getFullCapacity();
        return result;
    }

    /**
     * Adds wetness indicator to the diaper by listing the fluids currently absorbed into it.
     * TODO: color each fluid's text according to what it is!
     * TODO: support ounces vs mL
     * @return full description
     */
    @Override
    public String getDescription() {
        StringBuilder descriptionBuilder = new StringBuilder(super.getDescription());
        descriptionBuilder.append("<p>" + (getClothingType().isPlural() ? "They can" : "It can") + " hold a total of "
                + this.capacityMl + " mL of fluid and will absorb " + this.getCapacityTillDirty() + " mL before being visibly soiled.</p>");
        if (getTotalAbsorbedAmount() <= 0) {
            descriptionBuilder.append("<p>" + (getClothingType().isPlural() ? "They are" : "It is") + " completely dry!</p>");
        } else {
            for (FluidInterface wetness : fluidsAbsorbed.keySet()) {
                int amount = fluidsAbsorbed.get(wetness);
                if(amount > 0) {
                    descriptionBuilder.append("<p>" + (getClothingType().isPlural() ? "They have" : "It has") + " absorbed "
                            + amount + "mL of " + wetness.getFlavour().getRandomFlavourDescriptor() + " " + wetness.getType().getRace().getName(false) + " " + wetness.getNameSingular(null) + ".</p>");
                }
            }
        }
        return descriptionBuilder.toString();
    }

    /**
     * Attempts to absorb a fluid into this diaper. If a diaper is over 50% full of fluid, it always counts as dirty and
     *  will leak some of the fluid absorbed. When it's at capacity, it will leak all of the additional fluid. If the
     *  diaper is a hyper diaper (ignoreCapacity is set to true) then it does not leak; it will absorb over its capacity.
     *  It still counts as dirty when over 50% of its base capacity.
     * @param fluidToAbsorb The fluid for the diaper to soak up.
     * @param amountToAbsorb The amount of the fluid to try to soak up.
     * @return The amount of the fluid that leaked. This should spread into the environment.
     */
    public Integer absorb(FluidInterface fluidToAbsorb, Integer amountToAbsorb) {
        if (this.ignoreCapacity) {
            this.absorbExactly(fluidToAbsorb, amountToAbsorb);
            return 0; // Hyper diapers, guaranteed not to leak!
        } else {
            int currentContents = this.getTotalAbsorbedAmount();
            int capacityAvailableToHalf = Math.max(0, capacityMl / 2 - currentContents);
            if (amountToAbsorb <= capacityAvailableToHalf) {
                this.absorbExactly(fluidToAbsorb, amountToAbsorb);
                return 0; // If it's less than half full it'll soak up all of it
            } else {
                int capacityAvailableOverHalf = Math.max(0, capacityMl - Math.max(currentContents, capacityMl / 2));
                if ((amountToAbsorb - capacityAvailableToHalf) / 2 <= capacityAvailableOverHalf) {
                    this.absorbExactly(fluidToAbsorb, (amountToAbsorb + capacityAvailableToHalf) / 2);
                    return (amountToAbsorb - capacityAvailableToHalf) / 2; // Half the volume over 50% gets leaked.
                } else {
                    this.absorbExactly(fluidToAbsorb, capacityAvailableToHalf + capacityAvailableOverHalf);
                    return amountToAbsorb - capacityAvailableOverHalf - capacityAvailableToHalf; // If half the volume over 50% is enough to fill the diaper, the remainder leaks.
                }
            }
        }
    }

    private void absorbExactly(FluidInterface fluidtoAbsorb, Integer amountToAbsorb) {
        if (amountToAbsorb > 0) {
            if (fluidsAbsorbed.containsKey(fluidtoAbsorb)) {
                fluidsAbsorbed.put(fluidtoAbsorb, amountToAbsorb + fluidsAbsorbed.get(fluidtoAbsorb));
            } else {
                fluidsAbsorbed.put(fluidtoAbsorb, amountToAbsorb);
            }
        }
    }

    public int getTotalAbsorbedAmount() {
        return fluidsAbsorbed.values().stream().reduce(0, Integer::sum);
    }

    /**
     * Washes the diaper by clearing out fluidsAbsorbed.
     * If you are an exhibitionist who's into watersports, you can have a public void. ;3
     * TODO: check for washable status; though maybe this goes in the logic around calling this method.
     */
    public void clearStoredFluids() {
        fluidsAbsorbed.clear();
    }
}
