package predatorPrey;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Carrot extends Plant { 
	
	private static int MAX_CARROT = 1200;
	
	public static long CARROT_COUNT = 0;
	
	private int reproduceInterval;
	
	public Carrot(ContinuousSpace<Object> space, Grid<Object> grid) {
		super(space, grid, Carrot.class, CARROT_COUNT);
		this.reproduceInterval = RandomHelper.nextIntFromTo(8,13);
		CARROT_COUNT++;
	}
	
	@Override
	public String toString(){
		return "Carrot: "+ID;
	}

	@Override
	public int getReproduceInterval() {
		return reproduceInterval;
	}

	@Override
	public long getPlantsCount() {
		return CARROT_COUNT;
	}

	@Override
	public int getMaxPlants() {
		return MAX_CARROT;
	}

	@Override
	public void decPlantsCount() {
		CARROT_COUNT--;
	}
}
