package predatorPrey;

import java.util.List;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Caribou extends Animal {
	
	private static int REPRODUCE_RADIUS = 5;
	
	public static long CARIBOU_COUNT = 0;
	
	private static int MAX_ENERGY = 20;
	
	private static int MAX_STAMINA = 10;
	
	private static int REPRODUCE_ENERGY = MAX_ENERGY / 2;
	
	private static int SPEED = 2;
	
	private static int STAMINA_LOSS = SPEED;
	
	private static int REPRODUCE_INTERVAL = 15;
	
	private static int MAX_CHILDREN = 1;
	
	private static Class<? extends Object> initialPrey = Lichen.class;
	
	private static boolean isToEvolve = true;
	
	private static int evolveTimeOffset = 10;
	
	public static int lastEvolutionAnimalCount; 
	
	public Caribou(ContinuousSpace<Object>space, Grid<Object> grid, List<Feature> combatFeatures, List<Feature> attributeFeatures) {
		super(space, grid, Caribou.class, initialPrey, CARIBOU_COUNT, attributeFeatures);
		setCombatFeatures(combatFeatures);
		if(this.ID == 0) logDataToFile();
		CARIBOU_COUNT++;
	}
	
	@Override
	public String toString(){
		return "Caribou: "+ID;
	}

	@Override
	public int getReproduceRadius() {
		return REPRODUCE_RADIUS;
	}

	@Override
	public int getDefaultMaxEnergy() {
		return MAX_ENERGY;
	}

	@Override
	public int getDefaultMaxStamina() {
		return MAX_STAMINA;
	}

	@Override
	public int getDefaultReproduceEnergy() {
		return REPRODUCE_ENERGY;
	}

	@Override
	public int getDefaultSpeed() {
		return SPEED;
	}

	@Override
	public int getDefaultReproduceInterval() {
		return REPRODUCE_INTERVAL;
	}

	@Override
	public int getDefaultMaxChildren() {
		return MAX_CHILDREN;
	}

	@Override
	public int getDefaultStaminaLoss() {
		return STAMINA_LOSS;
	}
	

	@Override
	public boolean getIsToEvolve() {
		return isToEvolve;
	}

	@Override
	public void setIsToEvolve(boolean value) {
		isToEvolve = value;		
	}
	
	@Override
	public int getEvolveTimeOffset() {
		return evolveTimeOffset;
	}
	
	@Override
	public int getLastEvolutionAnimalCount() {
		return lastEvolutionAnimalCount;
	}

	@Override
	public void setLastEvolutionAnimalCount(int value) {
		lastEvolutionAnimalCount = value;
	}
}
