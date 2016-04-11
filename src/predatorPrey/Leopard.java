package predatorPrey;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Leopard extends Animal {
	
	private static int REPRODUCE_RADIUS = 5;
	
	public static long LEOPARD_COUNT = 0;
	
	private static int MAX_HEALTH = 30;
	
	private static int MAX_STAMINA = 10;
	
	private static int REPRODUCE_ENERGY = MAX_HEALTH / 2;
	
	private static int SPEED = 2;
	
	private static int REPRODUCE_INTERVAL = 40;
	
	private static int MAX_CHILDREN = 3;
	
	private static Class<? extends Object> initialPrey = Antelope.class;
	
	public Leopard(ContinuousSpace<Object> space, Grid<Object> grid){
		super(space, grid, Leopard.class, initialPrey, LEOPARD_COUNT);
		LEOPARD_COUNT++;
	}

	@Override
	public String toString(){
		return "Leopard: "+ID;
	}

	@Override
	public int getReproduceRadius() {
		return REPRODUCE_RADIUS;
	}

	@Override
	public int getMaxHealth() {
		return MAX_HEALTH;
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
}
