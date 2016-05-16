package predatorPrey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureUtils {
	
	public static final List<String> COMBAT_FEATURE_NAMES;
	
	public static final List<String> ATTRIBUTE_FEATURE_NAMES;
	
    public static final Map<String, List<String>> FEATURE_RELATIONS;
    
    static
    {	
    	COMBAT_FEATURE_NAMES = Arrays.asList("Poison", "LongClaws", "Stench", "Web", "Paralysis", "SharpTooth", "Armor", "Horns", "Camouflage", "ScaryBody");
    	
    	ATTRIBUTE_FEATURE_NAMES = Arrays.asList("SpeedUp", "MaxEnergyUp", "MaxStaminaUp", "ReproduceEnergyDown", "StaminaLossDown", "ReproduceIntervalDown", "MaxChildrenUp");
    	
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
