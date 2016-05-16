package predatorPrey;

public class Feature {
	
	private String name;
	
	private Float effectiveness;
	
	public Feature(String name, Float effectiveness){
		this.name = name;
		this.effectiveness = effectiveness;
	}
	
	public String getName(){
		return name;
	}
	
	public Float getEffectiveness(){
		return effectiveness;
	}

}
