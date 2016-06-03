package predatorPrey;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Lichen extends Plant { 
	
	private static int MAX_LICHEN = 1200;
	
	public static long LICHEN_COUNT = 0;
	
	private int reproduceInterval;
	
	public Lichen(ContinuousSpace<Object> space, Grid<Object> grid) {
		super(space, grid, Lichen.class, LICHEN_COUNT);
		this.reproduceInterval = RandomHelper.nextIntFromTo(8,13);
		LICHEN_COUNT++;
	}
	
	@Override
	public String toString(){
		return "Lichen: "+ID;
	}

	@Override
	public int getReproduceInterval() {
		return reproduceInterval;
	}

	@Override
	public long getPlantsCount() {
		return LICHEN_COUNT;
	}

	@Override
	public int getMaxPlants() {
		return MAX_LICHEN;
	}

	@Override
	public void decPlantsCount() {
		LICHEN_COUNT--;
	}
}
