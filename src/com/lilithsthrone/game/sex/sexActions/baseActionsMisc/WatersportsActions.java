package com.lilithsthrone.game.sex.sexActions.baseActionsMisc;

import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.body.CoverableArea;
import com.lilithsthrone.game.inventory.ItemTag;
import com.lilithsthrone.game.inventory.clothing.AbstractClothing;
import com.lilithsthrone.game.inventory.item.AbstractDiaper;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * Actions for sex that involve pee. Peeing yourself should go in the SelfNoPen class!
 * TODO: Add all actions to be in here to the sexActions field in SexActionPresets (near where SadisticActions and LovingActions get added)
 * TODO: disable the actions when watersports is turned off!
 * TODO: More actions! (finish the ones in SelfNoPen too)
 * @since 0.4.9_alfador0.1.0
 * @version 0.4.9_alfador0.1.0
 * @author Alfador
 */
public class WatersportsActions {
    // TODO: light sprinkling of pee {all targets except self}
    // TODO: small spray of pee {all targets except self}
    // TODO: empty bladder {all targets except self}
    // TODO: pee self (wets diaper! drenches pants!) {all amounts} [in SelfNoPen]
    // TODO: during blowjob, pee in partner's mouth {all amounts}
    // TODO: during eating out, pee in partner's mouth {all amounts}
    // TODO: pee on partner exterior {all amounts}
    // TODO: contingent on partner wearing diaper, pee in that diaper

    /**
     * Method to use to pee your pants. Also works if you're not wearing pants.
     * @param peeingCharacter The character from whom the urine originates.
     * @param otherCharacter Another character who might be getting peed on. Null if the floor is the final destination of leaks.
     * @param peeOrigin The area of the body the pee comes from--currently should only ever be penis or vagina, but this method
     *                  might be generalized to work for other fluids coming out of other orifices. Milk, cum, vaginal secretions,
     *                  even maybe saliva!
     * @param urineMl The amount of urine (or other fluid) exiting the body.
     * @return a parser-formatted string to be displayed.
     */
    public static String wetThroughClothing(GameCharacter peeingCharacter, GameCharacter otherCharacter, CoverableArea peeOrigin, int urineMl) {
        if (peeingCharacter == null) {
            return "";
        }
        StringBuilder actionString = new StringBuilder();
        int remainingFlow = urineMl;
        List<AbstractClothing> clothStack = peeingCharacter.getInventory().getBlockingCoverableAreaClothingList(peeingCharacter, peeOrigin, false);
        clothStack.sort(Comparator.comparingInt(o -> o.getSlotEquippedTo().getZLayer()));
        ListIterator<AbstractClothing> clothingIterator = clothStack.listIterator();
        AbstractClothing lastPeedIn = null;
        // TODO: Different dialogue words for different amount categories of remainingFlow, eg. "small stain" or "drip on floor" for small amounts vs. "drenches" "puddles" for large amounts
        // - Good idea to have a urine equivalent to the Lactation and CumProduction enums to standardize comparisons
        // TODO: This could also later be standardized to get called for cum, vagCum, and milk soakings.
        while (remainingFlow > 0 && clothingIterator.hasNext()) {
            AbstractClothing toPeeIn = clothingIterator.next();
            while(toPeeIn.equals(lastPeedIn) && clothingIterator.hasNext()) {
                toPeeIn = clothingIterator.next();
            }
            if (!clothingIterator.hasNext() && toPeeIn.equals(lastPeedIn)) {
                break;
            }
            lastPeedIn = toPeeIn;
            if (toPeeIn.getItemTags(toPeeIn.getSlotEquippedTo()).contains(ItemTag.DIAPER)) {
                int previousFlow = remainingFlow;
                remainingFlow = ((AbstractDiaper)toPeeIn).absorb(peeingCharacter.getBladder().getUrine(), remainingFlow);
                if (previousFlow == remainingFlow) {
                    actionString.append("<p style='text-align:center;'>");
                    actionString.append("[style.italicsUrine(");
                    actionString.append("[npc.NamePos] utterly saturated "+toPeeIn.getName()+" cannot hold any more of [npc.her] [npc.urine], and ");
                    actionString.append("everything leaks out!");
                    actionString.append(")]");
                    actionString.append("</p>");
                } else if (remainingFlow > 0) {
                    actionString.append("<p style='text-align:center;'>");
                    actionString.append("[style.italicsUrine(");
                    actionString.append("[npc.NamePos] "+toPeeIn.getName()+" "+(toPeeIn.getClothingType().isPlural()?"absorb":"absorbs")+" some of [npc.her] [npc.urine], but ");
                    actionString.append("some leaks out!");
                    actionString.append(")]");
                    actionString.append("</p>");
                } else {
                    actionString.append("<p style='text-align:center;'>");
                    actionString.append("[style.italicsUrine(");
                    actionString.append("[npc.NamePos] "+toPeeIn.getName()+" "+(toPeeIn.getClothingType().isPlural()?"absorb":"absorbs")+" all of [npc.her] [npc.urine]!");
                    actionString.append(")]");
                    actionString.append("</p>");
                }
            } else {
                toPeeIn.setDirty(peeingCharacter, true);
                actionString.append("<p style='text-align:center;'>");
                actionString.append("[style.italicsUrine(");
                actionString.append("[npc.NamePos] "+toPeeIn.getName()+" "+(toPeeIn.getClothingType().isPlural()?"are":"is")+" stained with [npc.her] [npc.urine]!");
                actionString.append(")]");
                actionString.append("</p>");
            }
        }
        if (remainingFlow > 0) {
            if (otherCharacter != null) {
                // TODO: mark other characters clothes splashed on as dirty; not needed at this point since no non-self pee targets are yet implemented
                actionString.append("<p style='text-align:center;'>");
                actionString.append("[style.italicsUrine(");
                actionString.append("[npc.NamePos] [npc.urine] trickles onto [npc2.name]!");
                actionString.append(")]");
                actionString.append("</p>");
            } else {
                actionString.append("<p style='text-align:center;'>");
                actionString.append("[style.italicsUrine(");
                actionString.append("[npc.NamePos] [npc.urine] splashes and puddles on the floor!");
                actionString.append(")]");
                actionString.append("</p>");
            }
        }
        return actionString.toString();
    }
}
