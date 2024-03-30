package com.lilithsthrone.game.character.body.abstractTypes;

import com.lilithsthrone.controller.xmlParsing.Element;
import com.lilithsthrone.game.character.GameCharacter;
import com.lilithsthrone.game.character.body.Body;
import com.lilithsthrone.game.character.body.coverings.AbstractBodyCoveringType;
import com.lilithsthrone.game.character.body.coverings.BodyCoveringType;
import com.lilithsthrone.game.character.body.types.BodyPartTypeInterface;
import com.lilithsthrone.game.character.body.types.FluidType;
import com.lilithsthrone.game.character.race.AbstractRace;
import com.lilithsthrone.game.character.race.Race;
import com.lilithsthrone.game.dialogue.utils.UtilText;
import com.lilithsthrone.main.Main;
import com.lilithsthrone.utils.Util;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract type for the bladder.
 * @since 0.4.9_alfador0.1.0
 * @version 0.4.9_alfador0.1.0
 * @author Alfador
 */
public class AbstractBladderType implements BodyPartTypeInterface {
    private AbstractRace race;
    private AbstractFluidType fluidType;

    private List<String> names;
    private List<String> namesPlural;
    private List<String> descriptors;

    public AbstractBladderType(AbstractRace race, AbstractFluidType fluidType, List<String> names, List<String> namesPlural, List<String> descriptors) {
        this.race = race;
        this.fluidType = fluidType;
        this.names = names;
        this.namesPlural = namesPlural;
        this.descriptors = descriptors;
    }

    public AbstractBladderType(AbstractRace race, AbstractFluidType fluidType) {
        this(race, fluidType, null, null, null);
    }

    public AbstractBladderType(File XMLFile) {
        if (XMLFile.exists()) {
            try {
                Document doc = Main.getDocBuilder().parse(XMLFile);

                // Cast magic:
                doc.getDocumentElement().normalize();

                Element coreElement = Element.getDocumentRootElement(XMLFile);

                this.race = Race.getRaceFromId(coreElement.getMandatoryFirstOf("race").getTextContent());
                this.fluidType = FluidType.getFluidTypeFromId(coreElement.getMandatoryFirstOf("fluidType").getTextContent());

                this.names = new ArrayList<>();
                for (Element e : coreElement.getMandatoryFirstOf("names").getAllOf("name")) {
                    names.add(e.getTextContent());
                }

                this.namesPlural = new ArrayList<>();
                for (Element e : coreElement.getMandatoryFirstOf("namesPlural").getAllOf("name")) {
                    namesPlural.add(e.getTextContent());
                }

                this.descriptors = new ArrayList<>();
                if (coreElement.getOptionalFirstOf("descriptors").isPresent()) {
                    for (Element e : coreElement.getMandatoryFirstOf("descriptors").getAllOf("descriptor")) {
                        descriptors.add(e.getTextContent());
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("AbstractBladderType was unable to be loaded from file! (" + XMLFile.getName() + ")\n" + ex);
            }
        }
    }

    public AbstractFluidType getFluidType() {
        return fluidType;
    }

    @Override
    public AbstractRace getRace() {
        return race;
    }

    @Override
    public boolean isDefaultPlural(GameCharacter gc) {
        return false;
    }

    @Override
    public String getNameSingular(GameCharacter gc) {
        if(names==null || names.isEmpty()) {
            return UtilText.returnStringAtRandom("bladder");
        }
        return Util.randomItemFrom(names);
    }

    @Override
    public String getNamePlural(GameCharacter gc) {
        if(namesPlural==null || namesPlural.isEmpty()) {
            return UtilText.returnStringAtRandom("bladders");
        }
        return Util.randomItemFrom(namesPlural);
    }

    @Override
    public String getDescriptor(GameCharacter gc) {
        if(descriptors==null || descriptors.isEmpty()) {
            return "";
        }
        return Util.randomItemFrom(descriptors);
    }

    @Override
    public String getDeterminer(GameCharacter gc) {
        return "";
    }

    /**
     * Required to be implemented but as this is an internal body part, its body covering should not ever be required.
     * @param body The body that this covering type is a part of.
     * @return
     */
    @Override
    public AbstractBodyCoveringType getBodyCoveringType(Body body) {
        return BodyCoveringType.HUMAN;
    }
}
