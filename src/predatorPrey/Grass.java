package predatorPrey;

import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Grass { 
	
	private static int MAX_GRASS = 5000;
	
	private static long GRASS_COUNT = 0;
	
	private ContinuousSpace<Object> space;
	
	private Grid<Object> grid;
	
	private final int reproduceInterval;
	
	private long ID;
	
	public static int grassCount = 0;
	
	public Grass(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
		this.reproduceInterval = RandomHelper.nextIntFromTo(7,10);
		this.ID = GRASS_COUNT++;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void act() {
		if((((long) RepastEssentials.GetTickCount()) % reproduceInterval == 0) && (grassCount < MAX_GRASS)){
			Context <Object> context = ContextUtils.getContext(this);
			Grass grass = new Grass(space, grid);
			context.add(grass);
			double posX = RandomHelper.nextDoubleFromTo(0.0, space.getDimensions().getWidth());
			double posY = RandomHelper.nextDoubleFromTo(0.0, space.getDimensions().getHeight());
			space.moveTo(grass, posX, posY);
			grid.moveTo(grass, (int)posX, (int)posY);
			grassCount++;
		}
	}
	
	@Override
	public String toString(){
		return "Grass: "+ID;
	}
}
