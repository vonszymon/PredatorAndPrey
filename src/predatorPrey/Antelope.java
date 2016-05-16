package predatorPrey;

import java.util.List;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Antelope extends Animal {
	
	private static int REPRODUCE_RADIUS = 5;
	
	public static long ANTELOPE_COUNT = 0;
	
	private static int MAX_ENERGY = 30;
	
	private static int MAX_STAMINA = 10;
	
	private static int REPRODUCE_ENERGY = MAX_ENERGY / 2;
	
	private static int SPEED = 2;
	
	private static int STAMINA_LOSS = SPEED;
	
	private static int REPRODUCE_INTERVAL = 15;
	
	private static int MAX_CHILDREN = 1;
	
	private static Class<? extends Object> initialPrey = Grass.class;
	
	public Antelope(ContinuousSpace<Object>space, Grid<Object> grid, List<Feature> combatFeatures) {
		super(space, grid, Antelope.class, initialPrey, ANTELOPE_COUNT);
		setCombatFeatures(combatFeatures);
		ANTELOPE_COUNT++;
	}
	
	@Override
	public String toString(){
		return "Antelope: "+ID;
	}

	@Override
	public int getReproduceRadius() {
		return REPRODUCE_RADIUS;
	}

	@Override
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}

	@Override
	public int getMaxStamina() {
		return MAX_STAMINA;
	}

	@Override
	public int getReproduceEnergy() {
		return REPRODUCE_ENERGY;
	}

	@Override
	public int getSpeed() {
		return SPEED;
	}

	@Override
	public int getReproduceInterval() {
		return REPRODUCE_INTERVAL;
	}

	@Override
	public int getMaxChildren() {
		return MAX_CHILDREN;
	}

	@Override
	public int getStaminaLoss() {
		return STAMINA_LOSS;
	}
}
