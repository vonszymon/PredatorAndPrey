package predatorPrey;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Antelope {
	
	private static int REPRODUCE_RADIUS = 5;
	
	private static long ANTELOPE_COUNT = 0;
	
	private static int MAX_HEALTH = 30;
	
	private static int MAX_STAMINA = 10;
	
	private static int REPRODUCE_ENERGY = MAX_HEALTH / 2;
	
	private static int SPEED = 2;
	
	private static int REPRODUCE_INTERVAL = 15;
	
	private static int MAX_CHILDREN = 1;
	
	private ContinuousSpace<Object> space;
	
	private Grid<Object> grid;

	private int health;
	
	private int stamina;
	
	private long ID;
	
	private long lastReproduceTime = 0;
	
	public Antelope(ContinuousSpace<Object>space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
		this.health = MAX_HEALTH;
		this.stamina = MAX_STAMINA;
		this.ID = ANTELOPE_COUNT++;
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
				List<GridCell<Grass>> gridCells = getNeighborsPrey(SPEED);
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
	
	public boolean searchForFood(List<GridCell<Grass>> gridCells){
		for(GridCell<Grass> cell : gridCells) {
			GridPoint point = cell.getPoint();
			for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
				if(obj instanceof Grass){
					moveTowards(space.getLocation(obj));
					eat(obj);
					Grass.grassCount--;
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
			List<GridCell<Antelope>> gridCells = getNeighbors(REPRODUCE_RADIUS);
			if(gridCells.size() > 0){
				int children = RandomHelper.nextIntFromTo(1, MAX_CHILDREN);
				for(int i = 0; i < children; i++){
					double deltaX = RandomHelper.nextDoubleFromTo(-2.0, 2.0);
					double deltaY = RandomHelper.nextDoubleFromTo(-2.0, 2.0);
					NdPoint location = space.getLocation(this);
					createChild(location.getX() + deltaX, location.getY() + deltaY);
				}
				lastReproduceTime = actualStep;
				for(GridCell<Antelope> cell : gridCells) {
					GridPoint point = cell.getPoint();
					for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
						if(obj instanceof Antelope){
							Antelope partner = (Antelope) obj;
							partner.lastReproduceTime = actualStep;
							System.out.println(this+" and "+partner+" produced "+children+" children");
							return;
						}
					}
				}
			}
				
		}
	}
	
	
	public List<GridCell<Grass>> getNeighborsPrey(int radius){
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Grass> nghCreator = new GridCellNgh<Grass>(grid, pt, Grass.class, radius, radius);
		List<GridCell<Grass>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		return gridCells;
	}
	
	public List<GridCell<Antelope>> getNeighbors(int radius){
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Antelope> nghCreator = new GridCellNgh<Antelope>(grid, pt, Antelope.class, radius, radius);
		List<GridCell<Antelope>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		return gridCells;
	}
	
	public void moveTowards(NdPoint pt) {
		space.moveTo(this, pt.getX(), pt.getY());
		grid.moveTo(this,(int)pt.getX(),(int)pt.getY());
		stamina -= SPEED;
	}
	
	public void createChild(double posX, double posY){
		Context <Object> context = ContextUtils.getContext(this);
		Antelope antelope = new Antelope(space, grid);
		antelope.lastReproduceTime = (long) RepastEssentials.GetTickCount();
		context.add(antelope);
		space.moveTo(antelope, posX, posY);
		grid.moveTo(antelope, (int)posX, (int)posY);
	}
	
	public void eat(Object obj){
		Context <Object> context = ContextUtils.getContext(obj);
		context.remove(obj);
		stamina = MAX_STAMINA;
		health = MAX_HEALTH;
	}
	
	@Override
	public String toString(){
		return "Antelope: "+ID;
	}
}
