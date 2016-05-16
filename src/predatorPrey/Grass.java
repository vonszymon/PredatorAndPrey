package predatorPrey;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Grass extends Plant { 
	
	private static int MAX_GRASS = 4000;
	
	public static long GRASS_COUNT = 0;
	
	private int reproduceInterval;
	
	public Grass(ContinuousSpace<Object> space, Grid<Object> grid) {
		super(space, grid, Grass.class, GRASS_COUNT);
		this.reproduceInterval = RandomHelper.nextIntFromTo(10,15);
		GRASS_COUNT++;
	}
	
	@Override
	public String toString(){
		return "Grass: "+ID;
	}

	@Override
	public int getReproduceInterval() {
		return reproduceInterval;
	}

	@Override
	public long getPlantsCount() {
		return GRASS_COUNT;
	}

	@Override
	public int getMaxPlants() {
		return MAX_GRASS;
	}

	@Override
	public void decPlantsCount() {
		GRASS_COUNT--;
	}
}
