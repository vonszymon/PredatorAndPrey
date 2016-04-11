package predatorPrey;

import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Leopard {
	
	private static int REPRODUCE_RADIUS = 5;
	
	private static long LEOPARD_COUNT = 0;
	
	private static int MAX_HEALTH = 30;
	
	private static int MAX_STAMINA = 10;
	
	private static int REPRODUCE_ENERGY = MAX_HEALTH / 2;
	
	private static int SPEED = 2;
	
	private static int REPRODUCE_INTERVAL = 40;
	
	private static int MAX_CHILDREN = 3;

	private ContinuousSpace<Object> space;
	
	private Grid <Object> grid;
	
	private int health;
	
	private int stamina;
	
	private long ID;
	
	private long lastReproduceTime = 0;
	
	public Leopard(ContinuousSpace<Object> space, Grid<Object> grid){
		this.space = space;
		this.grid = grid;
		this.health = MAX_HEALTH;
		this.stamina = MAX_STAMINA;
		this.ID = LEOPARD_COUNT++;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void act() {
		health -= 1;
		if(health <= 0){
			Context <Object> context = ContextUtils.getContext(this);
			context.remove(this);
			System.out.println(this+" died from starvation");
		} else {
			if(stamina - SPEED <= 0){
				stamina = MAX_STAMINA;
			} else {
				List<GridCell<Antelope>> gridCells = getNeighborsPrey(SPEED);
				boolean eaten = searchForFood(gridCells);
				if(!eaten){
					NdPoint position = space.getLocation(this);
					double deltaX = RandomHelper.nextDoubleFromTo(-SPEED, SPEED);
					double deltaY = RandomHelper.nextDoubleFromTo(-SPEED, SPEED);
					moveTowards(new NdPoint(position.getX()+deltaX, position.getY()+deltaY));
				}
				reproduce();
			}
		}
	}
	
	public boolean searchForFood(List<GridCell<Antelope>> gridCells){
		for(GridCell<Antelope> cell : gridCells) {
			GridPoint point = cell.getPoint();
			for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
				if(obj instanceof Antelope){
					moveTowards(space.getLocation(obj));
					eat(obj);
					System.out.println(this+" eaten "+obj);
					return true;
				}
			}
		}
		return false;
	}
	
	public void reproduce(){
		long actualStep = (long) RepastEssentials.GetTickCount();
		if((actualStep - lastReproduceTime >= REPRODUCE_INTERVAL) && (health >= REPRODUCE_ENERGY)){
			List<GridCell<Leopard>> gridCells = getNeighbors(REPRODUCE_RADIUS);
			if(gridCells.size() > 0){
				int children = RandomHelper.nextIntFromTo(1, MAX_CHILDREN);
				for(int i = 0; i < children; i++){
					double deltaX = RandomHelper.nextDoubleFromTo(-2.0, 2.0);
					double deltaY = RandomHelper.nextDoubleFromTo(-2.0, 2.0);
					NdPoint location = space.getLocation(this);
					createChild(location.getX() + deltaX, location.getY() + deltaY);
				}
				lastReproduceTime = actualStep;
				for(GridCell<Leopard> cell : gridCells) {
					GridPoint point = cell.getPoint();
					for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
						if(obj instanceof Leopard){
							Leopard partner = (Leopard) obj;
							partner.lastReproduceTime = actualStep;
							System.out.println(this+" and "+partner+" produced "+children+" children");
							return;
						}
					}
				}
			}
				
		}
	}
	
	public void moveTowards(NdPoint pt) {
		space.moveTo(this, pt.getX(), pt.getY());
		grid.moveTo(this,(int)pt.getX(),(int)pt.getY());
		stamina -= SPEED;
	}
	
	public void eat(Object obj){
		Context <Object> context = ContextUtils.getContext(obj);
		context.remove(obj);
		stamina = MAX_STAMINA;
		health = MAX_HEALTH;
	}
	
	public void createChild(double posX, double posY){
		Context <Object> context = ContextUtils.getContext(this);
		Leopard leopard = new Leopard(space, grid);
		leopard.lastReproduceTime = (long) RepastEssentials.GetTickCount();
		context.add(leopard);
		space.moveTo(leopard, posX, posY);
		grid.moveTo(leopard, (int)posX, (int)posY);
	}
	
	public List<GridCell<Leopard>> getNeighbors(int radius){
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Leopard> nghCreator = new GridCellNgh<Leopard>(grid, pt, Leopard.class, radius, radius);
		List<GridCell<Leopard>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		return gridCells;
	}
	
	public List<GridCell<Antelope>> getNeighborsPrey(int radius){
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Antelope> nghCreator = new GridCellNgh<Antelope>(grid, pt, Antelope.class, radius, radius);
		List<GridCell<Antelope>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		return gridCells;
	}
	
	@Override
	public String toString(){
		return "Leopard: "+ID;
	}
}
