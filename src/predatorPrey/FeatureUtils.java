package predatorPrey;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureUtils {
	
	public static PrintWriter LOGGER;
	
	public static final List<String> COMBAT_FEATURE_NAMES;
	
	public static final List<String> ATTRIBUTE_FEATURE_NAMES;
	
    public static final Map<String, List<String>> FEATURE_RELATIONS;
    
    public static final List<Class<? extends Object>> AGENT_TYPES;
    
    static
    {	
    	try {
			LOGGER = new PrintWriter("log/agent_data.log");
			LOGGER.println(String.format("%-10s %-10s %-30s %-70s %-70s", "Step", "Species", "Prey", "AttributeFeatures", "CombatFeatures"));
			LOGGER.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    	AGENT_TYPES = Arrays.asList(Antelope.class, Caribou.class, Carrot.class, Fox.class, Grass.class, Leopard.class, Lichen.class, Rabbit.class, Wolf.class);
    	
    	COMBAT_FEATURE_NAMES = Arrays.asList("Poison", "LongClaws", "Stench", "Web", "Paralysis", "SharpTooth", "Armor", "Horns", "Camouflage", "ScaryBody");
    	
    	ATTRIBUTE_FEATURE_NAMES = Arrays.asList("SpeedUp", "MaxEnergyUp", "MaxStaminaUp", "ReproduceEnergyDown", "ReproduceIntervalDown", "MaxChildrenUp");
    	
    	FEATURE_RELATIONS = new HashMap<String, List<String>>();
    	
    	FEATURE_RELATIONS.put("Poison", Arrays.asList("Camouflage", "ScaryBody"));
    	FEATURE_RELATIONS.put("LongClaws", Arrays.asList("Armor", "SharpTooth"));
    	FEATURE_RELATIONS.put("Stench", Arrays.asList("Horns", "SharpTooth"));
    	FEATURE_RELATIONS.put("Web", Arrays.asList("Paralysis", "Stench"));
    	FEATURE_RELATIONS.put("Paralysis", Arrays.asList("Poison", "Camouflage"));
    	FEATURE_RELATIONS.put("SharpTooth", Arrays.asList("Armor", "ScaryBody"));
    	FEATURE_RELATIONS.put("Armor", Arrays.asList("Poison", "Web"));
    	FEATURE_RELATIONS.put("Horns", Arrays.asList("Stench", "Web"));
    	FEATURE_RELATIONS.put("Camouflage", Arrays.asList("Poison", "LongClaws"));
    	FEATURE_RELATIONS.put("ScaryBody", Arrays.asList("LongClaws", "Horns"));
    }
    

}
