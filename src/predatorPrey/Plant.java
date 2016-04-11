package predatorPrey;

import java.lang.reflect.Constructor;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public abstract class Plant { 
	
	private ContinuousSpace<Object> space;
	
	private Grid<Object> grid;
	
	protected long ID;
	
	public static int grassCount = 0;
	
	private Class<? extends Plant> self;
	
	public abstract int getReproduceInterval();
	
	public abstract long getPlantsCount();
	
	public abstract int getMaxPlants();
	
	public abstract void decPlantsCount();
	
	public Plant(ContinuousSpace<Object> space, Grid<Object> grid, Class<? extends Plant> self, long ID) {
		this.space = space;
		this.grid = grid;
		this.self = self;
		this.ID = ID;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void act() {
		if((((long) RepastEssentials.GetTickCount()) % getReproduceInterval() == 0) && (getPlantsCount() < getMaxPlants())){
			Context <Object> context = ContextUtils.getContext(this);
			Constructor<?> ctor;
			Plant plant = null; 
			try {
				ctor = self.getConstructor(ContinuousSpace.class, Grid.class);
				plant = (Plant) ctor.newInstance(new Object[] { space, grid });
			} catch (Exception e) {
				e.printStackTrace();
			}
			double posX = RandomHelper.nextDoubleFromTo(0.0, space.getDimensions().getWidth());
			double posY = RandomHelper.nextDoubleFromTo(0.0, space.getDimensions().getHeight());
			context.add(plant);
			space.moveTo(plant, posX, posY);
			grid.moveTo(plant, (int)posX, (int)posY);
		}
	}
	
	@Override
	public String toString(){
		return "Plant: "+ID;
	}
}
