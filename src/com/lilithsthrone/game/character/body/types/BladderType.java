package com.lilithsthrone.game.character.body.types;

import com.lilithsthrone.game.character.body.abstractTypes.AbstractBladderType;
import com.lilithsthrone.game.character.race.AbstractRace;
import com.lilithsthrone.game.character.race.Race;
import com.lilithsthrone.utils.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

/**
 * Bladder type and hardcoded human bladder.
 * @since 0.4.9_alfador0.1.0
 * @version 0.4.9_alfador0.1.0
 * @author Alfador
 */
public class BladderType {
	public static final int DEFAULT_BLADDER_CAPACITY = 500; // 500mL is a reasonable bladder capacity for an adult human
	public static final int DEFAULT_BLADDER_CONTINENCE = 100; // being fully potty trained is reasonable for an adult human

	public static AbstractBladderType HUMAN = new AbstractBladderType(
			Race.HUMAN,
			FluidType.URINE_HUMAN){
	};

	public static AbstractBladderType ANGEL = new AbstractBladderType(
			Race.ANGEL,
			FluidType.URINE_ANGEL){
	};

	public static AbstractBladderType DEMON = new AbstractBladderType(
			Race.DEMON,
			FluidType.URINE_DEMON){
	};

	public static AbstractBladderType COW_MORPH = new AbstractBladderType(
			Race.COW_MORPH,
			FluidType.URINE_COW_MORPH){
	};

	public static AbstractBladderType DOG_MORPH = new AbstractBladderType(
			Race.DOG_MORPH,
			FluidType.URINE_DOG_MORPH){
	};

	public static AbstractBladderType FOX_MORPH = new AbstractBladderType(
			Race.FOX_MORPH,
			FluidType.URINE_FOX_MORPH){
	};

	public static AbstractBladderType WOLF_MORPH = new AbstractBladderType(
			Race.WOLF_MORPH,
			FluidType.URINE_WOLF_MORPH){
	};

	public static AbstractBladderType CAT_MORPH = new AbstractBladderType(
			Race.CAT_MORPH,
			FluidType.URINE_CAT_MORPH){
	};

	public static AbstractBladderType HORSE_MORPH = new AbstractBladderType(
			Race.HORSE_MORPH,
			FluidType.URINE_HORSE_MORPH){
	};

	public static AbstractBladderType REINDEER_MORPH = new AbstractBladderType(
			Race.REINDEER_MORPH,
			FluidType.URINE_REINDEER_MORPH){
	};

	public static AbstractBladderType ALLIGATOR_MORPH = new AbstractBladderType(
			Race.ALLIGATOR_MORPH,
			FluidType.URINE_ALLIGATOR_MORPH){
	};

	public static AbstractBladderType SQUIRREL_MORPH = new AbstractBladderType(
			Race.SQUIRREL_MORPH,
			FluidType.URINE_SQUIRREL_MORPH){
	};

	public static AbstractBladderType RAT_MORPH = new AbstractBladderType(
			Race.RAT_MORPH,
			FluidType.URINE_RAT_MORPH){
	};

	public static AbstractBladderType RABBIT_MORPH = new AbstractBladderType(
			Race.RABBIT_MORPH,
			FluidType.URINE_RABBIT_MORPH){
	};

	public static AbstractBladderType BAT_MORPH = new AbstractBladderType(
			Race.BAT_MORPH,
			FluidType.URINE_BAT_MORPH){
	};

	public static AbstractBladderType HARPY = new AbstractBladderType(
			Race.HARPY,
			FluidType.URINE_HARPY){
	};
    
    private static List<AbstractBladderType> allBladderTypes;
	private static Map<AbstractBladderType, String> bladderToIdMap = new HashMap<>();
	private static Map<String, AbstractBladderType> idToBladderMap = new HashMap<>();
	
	static {
		allBladderTypes = new ArrayList<>();

		// Modded types:
		
		Map<String, Map<String, File>> moddedFilesMap = Util.getExternalModFilesById("/race", "bodyParts", null);
		for(Entry<String, Map<String, File>> entry : moddedFilesMap.entrySet()) {
			for(Entry<String, File> innerEntry : entry.getValue().entrySet()) {
				if(Util.getXmlRootElementName(innerEntry.getValue()).equals("bladder")) {
					try {
						AbstractBladderType type = new AbstractBladderType(innerEntry.getValue()) {};
						String id = innerEntry.getKey().replaceAll("bodyParts_", "");
						allBladderTypes.add(type);
						bladderToIdMap.put(type, id);
						idToBladderMap.put(id, type);
					} catch(Exception ex) {
						ex.printStackTrace(System.err);
					}
				}
			}
		}
		
		// External res types:
		
		Map<String, Map<String, File>> filesMap = Util.getExternalFilesById("res/race", "bodyParts", null);
		for(Entry<String, Map<String, File>> entry : filesMap.entrySet()) {
			for(Entry<String, File> innerEntry : entry.getValue().entrySet()) {
				if(Util.getXmlRootElementName(innerEntry.getValue()).equals("bladder")) {
					try {
						AbstractBladderType type = new AbstractBladderType(innerEntry.getValue()) {};
						String id = innerEntry.getKey().replaceAll("bodyParts_", "");
						allBladderTypes.add(type);
						bladderToIdMap.put(type, id);
						idToBladderMap.put(id, type);
					} catch(Exception ex) {
						ex.printStackTrace(System.err);
					}
				}
			}
		}
		
		// Add in hard-coded bladder types:
		
		Field[] fields = BladderType.class.getFields();
		
		for(Field f : fields){
			if (AbstractBladderType.class.isAssignableFrom(f.getType())) {
				
				AbstractBladderType ct;
				try {
					ct = ((AbstractBladderType) f.get(null));

					bladderToIdMap.put(ct, f.getName());
					idToBladderMap.put(f.getName(), ct);
					
					allBladderTypes.add(ct);
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		Collections.sort(allBladderTypes, (t1, t2)->
			t1.getRace()==Race.NONE
				?-1
				:(t2.getRace()==Race.NONE
					?1
					:t1.getRace().getName(false).compareTo(t2.getRace().getName(false))));
	}
	
	public static AbstractBladderType getBladderTypeFromId(String id) {
		id = Util.getClosestStringMatch(id, idToBladderMap.keySet());
		return idToBladderMap.get(id);
	}
	
	public static String getIdFromBladderType(AbstractBladderType bladderType) {
		return bladderToIdMap.get(bladderType);
	}
	
	public static List<AbstractBladderType> getAllBladderTypes() {
		return allBladderTypes;
	}
	
	private static Map<AbstractRace, List<AbstractBladderType>> typesMap = new HashMap<>();
	public static List<AbstractBladderType> getBladderTypes(AbstractRace r) {
		if(typesMap.containsKey(r)) {
			return typesMap.get(r);
		}
		
		List<AbstractBladderType> types = new ArrayList<>();
		for(AbstractBladderType type : BladderType.getAllBladderTypes()) {
			if(type.getRace()==r) {
				types.add(type);
			}
		}
		typesMap.put(r, types);
		return types;
	}
}
