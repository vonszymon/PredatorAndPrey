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
	
	private int energy;
	
	private int stamina;
	
	protected long ID;
	
	private long lastReproduceTime = 0;
	
	private long lastEatTimeTick = 0;
	
	private long eatInterval = 3;
	
	private List<Class<? extends Object>> prey;
	
	private Class<? extends Animal> self;
	
	private List<Feature> combatFeatures;
	
	private List<Feature> attributeFeatures;
	
	public abstract int getReproduceRadius();
	
	public abstract int getMaxEnergy();
	
	public abstract int getMaxStamina();
	
	public abstract int getReproduceEnergy();
	
	public abstract int getSpeed();
	
	public abstract int getStaminaLoss();
	
	public abstract int getReproduceInterval();
	
	public abstract int getMaxChildren();
	
	public Animal(ContinuousSpace<Object> space, Grid<Object> grid, Class<? extends Animal> self, Class<? extends Object> prey, long ID){
		this.space = space;
		this.grid = grid;
		this.energy = getMaxEnergy();
		this.stamina = getMaxStamina();
		this.prey = new ArrayList<Class<? extends Object>>();
		this.self = self;
		this.prey.add(prey);
		this.ID = ID;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void act() {
		energy -= 1;
		if(energy <= 0){
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			System.out.println(this+" died from starvation");
		} else {
			int speed = getSpeed();
			if(stamina - getStaminaLoss() <= 0){
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
	
	private boolean searchForFood(List<GridCell<Object>> gridCells){
		if((long)RepastEssentials.GetTickCount() - lastEatTimeTick > eatInterval){
			for(GridCell<Object> cell : gridCells) {
				GridPoint point = cell.getPoint();
				for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
					for(Class<? extends Object> preyCandidate : prey){
						if(obj.getClass() == preyCandidate){
							if(isAttackSuccesfull(obj)){
								moveTowards(space.getLocation(obj));
								eat(obj);
								System.out.println(this+" eaten "+obj);
								return true;
							}
							return false;
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean isAttackSuccesfull(Object obj){
		if(obj instanceof Plant) return true;
		Animal prey = (Animal) obj;
		float predatorScore = 0.0f;
		float preyScore = 0.0f;
		for(Feature predatorFeature : combatFeatures){
			predatorScore += predatorFeature.getEffectiveness();
			List<String> effectiveAgainst = FeatureUtils.FEATURE_RELATIONS.get(predatorFeature.getName());
			if(effectiveAgainst != null){
				for(Feature preyFeature : prey.combatFeatures){
					if(effectiveAgainst.contains(preyFeature.getName())){
						predatorScore += predatorFeature.getEffectiveness();
						break;
					}
				}
			}
		}
		for(Feature preyFeature : prey.combatFeatures){
			preyScore += preyFeature.getEffectiveness();
			List<String> effectiveAgainst = FeatureUtils.FEATURE_RELATIONS.get(preyFeature.getName());
			if(effectiveAgainst != null){
				for(Feature predatorFeature : combatFeatures){
					if(effectiveAgainst.contains(predatorFeature.getName())){
						preyScore += preyFeature.getEffectiveness();
						break;
					}
				}
			}
		}
		predatorScore += predatorScore * Math.random();
		preyScore += preyScore * Math.random();
		if(predatorScore > preyScore) return true;
		return false;
	}
	
	private List<GridCell<Object>> getNeighborsPrey(int radius){
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Object> nghCreator = new GridCellNgh<Object>(grid, pt, Object.class, radius, radius);
		List<GridCell<Object>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		return gridCells;
	}
	
	private void moveTowards(NdPoint pt) {
		space.moveTo(this, pt.getX(), pt.getY());
		grid.moveTo(this,(int)pt.getX(),(int)pt.getY());
		stamina -= getSpeed();
	}
	
	private void eat(Object obj){
		if(obj instanceof Plant){
			Plant plant = (Plant) obj;
			plant.decPlantsCount();
		}
		Context <Object> context = ContextUtils.getContext(obj);
		context.remove(obj);
		stamina = getMaxStamina();
		energy = getMaxEnergy();
		lastEatTimeTick = (long) RepastEssentials.GetTickCount();
	}
	
	private void reproduce(){
		long actualStep = (long) RepastEssentials.GetTickCount();
		if((actualStep - lastReproduceTime >= getReproduceInterval()) && (energy >= getReproduceEnergy())){
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
	
	private List<GridCell<Object>> getNeighbors(int radius){
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
	
	private void createChild(double posX, double posY) throws Exception {
		Context <Object> context = ContextUtils.getContext(this);
		Constructor<?> ctor = self.getConstructor(ContinuousSpace.class, Grid.class, List.class);
		Animal animal = (Animal) ctor.newInstance(new Object[] { space, grid, combatFeatures });
		animal.lastReproduceTime = (long) RepastEssentials.GetTickCount();
		context.add(animal);
		space.moveTo(animal, posX, posY);
		grid.moveTo(animal, (int)posX, (int)posY);
	}
	
	@Override
	public String toString(){
		return "Animal: "+ID;
	}
	
	public void setCombatFeatures(List<Feature> combatFeatures){
		this.combatFeatures = combatFeatures;
	}

}
