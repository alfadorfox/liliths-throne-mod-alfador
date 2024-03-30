package com.lilithsthrone.game.character.body;

import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.body.abstractTypes.AbstractBladderType;
import com.lilithsthrone.game.character.body.valueEnums.FluidRegeneration;
import com.lilithsthrone.game.dialogue.utils.UtilText;

/**
 * Bladder body part to hold urine. Because we can't just use the Testicle class. It's not stored there.
 * @since 0.4.9_alfador0.1.0
 * @version 0.4.9_alfador0.1.0
 * @author Alfador
 */
public class Bladder implements BodyPartInterface {
    public static final int MAXIMUM_BLADDER_CAPACITY = 100_000; // this is a frankly absurd amount of bladder capacity, but it matches the max breast milk storage.
    // And I do plan for "hyper diapers" you could fill with an absolutely preposterous amount of pee.

    protected AbstractBladderType type;
    // TODO: Capacity! Incontinence! Urine regeneration!

    protected int urineStorage;
	protected float urineStored;
	/** Measured in mL/day */
	protected int urineRegeneration;
    protected int continencePercent; // 0-100 continence, NYI but 100 means total control and 0 means zero control

	protected FluidUrine urine;

    public Bladder(AbstractBladderType type, int urineStorage, int continencePercent) {
        this.type = type;
        this.urineStorage = urineStorage;
        this.urineStored = urineStorage;

        // a bit fast for bladder filling but if this is enabled at all that's almost certainly what you want.
        this.urineRegeneration = FluidRegeneration.TWO_FAST.getMedianRegenerationValuePerDay();
        this.continencePercent = continencePercent;
        urine = new FluidUrine(type.getFluidType());
    }

    public FluidUrine getUrine() {
        return urine;
    }

    @Override
    public AbstractBladderType getType() {
        return type;
    }

    public String setType(GameCharacter owner, AbstractBladderType type) {
        this.type = type;
        urine.setType(type.getFluidType());
        return "";
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
        return type.getDescriptor(gc);
    }

    public int getUrineStorage() {
        return urineStorage;
    }

    public float getUrineStored() {
        return urineStored;
    }

    public FluidRegeneration getUrineRegeneration() {
        return FluidRegeneration.getFluidRegenerationFromInt(urineRegeneration);
    }

    public int getRawUrineRegenerationValue() {
        return urineRegeneration;
    }

    public int getContinencePercent() {
        return continencePercent;
    }

    /**
     * Sets the urineStorage. Value is bound to >=0 && <=MAXIMUM_BLADDER_CAPACITY
     * @param urineStorage
     */
    public String setUrineStorage(GameCharacter owner, int urineStorage) {
        int oldPeeTank = this.urineStorage;
        this.urineStorage = Math.max(0, Math.min(urineStorage, MAXIMUM_BLADDER_CAPACITY));
        int peeCapacityChange = this.urineStorage - oldPeeTank;
        if(owner==null) {
            return "";
        }

        if (peeCapacityChange == 0) {
            return UtilText.parse(owner, "<p style='text-align:center;'>[style.colourDisabled(The amount of [npc.urine] that [npc.nameIsFull] able to store doesn't change...)]</p>");
        }
        if (peeCapacityChange > 0) {
            return UtilText.parse(owner,
                    "<p>"
                            + "[npc.Name] [npc.verb(feel)] a strange pressure in [npc.her] abdomen, as of something growing bigger, combined with a sense of relieved pressure within."
                            + " [npc.She] realizes [npc.her] enlarged [npc.bladder] can now hold more urine before needing to release!"
                            + "</p>");
        }
        return UtilText.parse(owner,
                "<p>"
                        + "[npc.Name] [npc.verb(feel)] a strange tightening in [npc.her] abdomen, as of something shrinking, combined with a sense of increased pressure within."
                        + " [npc.She] realizes [npc.her] smaller [npc.bladder] now holds less urine before needing to release!"
                        + "</p>");
    }

    // TODO: messages for changes in bladder stats, handling for involuntary peeing due to over capacity (maybe here? Maybe elsewhere.)
    public void setUrineStored(float urineStored) {
        this.urineStored = urineStored;
    }

    public void setUrineRegeneration(int urineRegeneration) {
        this.urineRegeneration = urineRegeneration;
    }

    public void setContinencePercent(int continencePercent) {
        this.continencePercent = continencePercent;
    }

    @Override
    public boolean isFeral(GameCharacter owner) {
        if(owner==null) {
            return false;
        }
        return owner.isFeral();
    }
}
