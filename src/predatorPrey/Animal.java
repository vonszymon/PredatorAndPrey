package predatorPrey;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
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

public abstract class Animal {

	private ContinuousSpace<Object> space;
	
	private Grid <Object> grid;
	
	private int health;
	
	private int stamina;
	
	protected long ID;
	
	private long lastReproduceTime = 0;
	
	private List<Class<? extends Object>> prey;
	
	private Class<? extends Animal> self;
	
	public abstract int getReproduceRadius();
	
	public abstract int getMaxHealth();
	
	public abstract int getMaxStamina();
	
	public abstract int getReproduceEnergy();
	
	public abstract int getSpeed();
	
	public abstract int getReproduceInterval();
	
	public abstract int getMaxChildren();
	
	public Animal(ContinuousSpace<Object> space, Grid<Object> grid, Class<? extends Animal> self, Class<? extends Object> prey, long ID){
		this.space = space;
		this.grid = grid;
		this.health = getMaxHealth();
		this.stamina = getMaxStamina();
		this.prey = new ArrayList<Class<? extends Object>>();
		this.self = self;
		this.prey.add(prey);
		this.ID = ID;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void act() {
		health -= 1;
		if(health <= 0){
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			System.out.println(this+" died from starvation");
		} else {
			int speed = getSpeed();
			if(stamina - speed <= 0){
				stamina = getMaxStamina();
			} else {
				List<GridCell<Object>> gridCells = getNeighborsPrey(speed);
				boolean eaten = searchForFood(gridCells);
				if(!eaten){
					NdPoint position = space.getLocation(this);
					double deltaX = RandomHelper.nextDoubleFromTo(-speed, speed);
					double deltaY = RandomHelper.nextDoubleFromTo(-speed, speed);
					moveTowards(new NdPoint(position.getX()+deltaX, position.getY()+deltaY));
				}
				reproduce();
			}
		}
	}
	
	public boolean searchForFood(List<GridCell<Object>> gridCells){
		for(GridCell<Object> cell : gridCells) {
			GridPoint point = cell.getPoint();
			for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
				for(Class<? extends Object> preyCandidate : prey){
					if(obj.getClass() == preyCandidate){
						moveTowards(space.getLocation(obj));
						eat(obj);
						//Grass.grassCount--;
						System.out.println(this+" eaten "+obj);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public List<GridCell<Object>> getNeighborsPrey(int radius){
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Object> nghCreator = new GridCellNgh<Object>(grid, pt, Object.class, radius, radius);
		List<GridCell<Object>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		return gridCells;
	}
	
	public void moveTowards(NdPoint pt) {
		space.moveTo(this, pt.getX(), pt.getY());
		grid.moveTo(this,(int)pt.getX(),(int)pt.getY());
		stamina -= getSpeed();
	}
	
	public void eat(Object obj){
		if(obj instanceof Plant){
			Plant plant = (Plant) obj;
			plant.decPlantsCount();
		}
		Context <Object> context = ContextUtils.getContext(obj);
		context.remove(obj);
		stamina = getMaxStamina();
		health = getMaxHealth();
	}
	
	public void reproduce(){
		long actualStep = (long) RepastEssentials.GetTickCount();
		if((actualStep - lastReproduceTime >= getReproduceInterval()) && (health >= getReproduceEnergy())){
			List<GridCell<Object>> gridCells = getNeighbors(getReproduceRadius());
			if(gridCells.size() > 0){
				int children = RandomHelper.nextIntFromTo(1, getMaxChildren());
				for(int i = 0; i < children; i++){
					double deltaX = RandomHelper.nextDoubleFromTo(-2.0, 2.0);
					double deltaY = RandomHelper.nextDoubleFromTo(-2.0, 2.0);
					NdPoint location = space.getLocation(this);
					try {
						createChild(location.getX() + deltaX, location.getY() + deltaY);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				lastReproduceTime = actualStep;
				for(GridCell<Object> cell : gridCells) {
					GridPoint point = cell.getPoint();
					for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
						if(obj.getClass() == self){
							Animal partner =  (Animal) obj;
							partner.lastReproduceTime = actualStep;
							System.out.println(this+" and "+partner+" produced "+children+" children");
							return;
						}
					}
				}
			}	
		}
	}
	
	public List<GridCell<Object>> getNeighbors(int radius){
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Object> nghCreator = new GridCellNgh<Object>(grid, pt, Object.class, radius, radius);
		List<GridCell<Object>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		List<GridCell<Object>> gridCellsFiltered = new LinkedList<>();
		for(GridCell<Object> cell : gridCells){
			GridPoint point = cell.getPoint();
			for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
				if(obj.getClass() == self){
					gridCellsFiltered.add(cell);
					break;
				}
			}
		}
		return gridCellsFiltered;
	}
	
	public void createChild(double posX, double posY) throws Exception {
		Context <Object> context = ContextUtils.getContext(this);
		Constructor<?> ctor = self.getConstructor(ContinuousSpace.class, Grid.class);
		Animal animal = (Animal) ctor.newInstance(new Object[] { space, grid });
		animal.lastReproduceTime = (long) RepastEssentials.GetTickCount();
		context.add(animal);
		space.moveTo(animal, posX, posY);
		grid.moveTo(animal, (int)posX, (int)posY);
	}
	
	@Override
	public String toString(){
		return "Animal: "+ID;
	}

}
