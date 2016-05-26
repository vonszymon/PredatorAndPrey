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
import repast.simphony.util.collections.IndexedIterable;

public abstract class Animal {

	private ContinuousSpace<Object> space;
	
	private Grid <Object> grid;
	
	private int energy;
	
	private int stamina;
	
	protected long ID;
	
	private long lastReproduceTime = 0;
	
	private long lastEatTimeTick = 0;
	
	private long eatInterval = 5;
	
	private List<Class<? extends Object>> prey;
	
	private Class<? extends Animal> self;
	
	private List<Feature> combatFeatures;
	
	private List<Feature> attributeFeatures;
	
	public abstract int getReproduceRadius();
	
	public abstract int getDefaultMaxEnergy();
	
	public abstract int getDefaultMaxStamina();
	
	public abstract int getDefaultReproduceEnergy();
	
	public abstract int getDefaultSpeed();
	
	public abstract int getDefaultStaminaLoss();
	
	public abstract int getDefaultReproduceInterval();
	
	public abstract int getDefaultMaxChildren();
	
	public abstract boolean getIsToEvolve();
	
	public abstract void setIsToEvolve(boolean value);
	
	public abstract int getEvolveTimeOffset();
	
	public abstract int getLastEvolutionAnimalCount();
	
	public abstract void setLastEvolutionAnimalCount(int value);
	
	public int getMaxEnergy(){
		for(Feature feature : attributeFeatures){
			if(feature.getName().equals("MaxEnergyUp")){
				return (int) (getDefaultMaxEnergy() * (1.0 + feature.getEffectiveness()));
			}
		}
		return getDefaultMaxEnergy();
	}
	
	public int getMaxStamina(){
		for(Feature feature : attributeFeatures){
			if(feature.getName().equals("MaxStaminaUp")){
				return (int) (getDefaultMaxStamina() * (1.0 + feature.getEffectiveness()));
			}
		}
		return getDefaultMaxStamina();
	}
	
	public int getReproduceEnergy(){
		for(Feature feature : attributeFeatures){
			if(feature.getName().equals("ReproduceEnergyDown")){
				return (int) (getDefaultReproduceEnergy() * (1.0 - feature.getEffectiveness()));
			}
		}
		return getDefaultReproduceEnergy();
	}
	
	public int getSpeed(){
		for(Feature feature : attributeFeatures){
			if(feature.getName().equals("SpeedUp")){
				return (int) (getDefaultSpeed() * (1.0 + feature.getEffectiveness()) + 0.5);
			}
		}
		return getDefaultSpeed();
	}
	
	public int getStaminaLoss(){
		return getDefaultStaminaLoss();
	}
	
	public int getReproduceInterval(){
		for(Feature feature : attributeFeatures){
			if(feature.getName().equals("ReproduceIntervalDown")){
				return (int) (getDefaultReproduceInterval() * (1.0 - (feature.getEffectiveness() / 2.0)));
			}
		}
		return getDefaultReproduceInterval();
	}
	
	public int getMaxChildren(){
		for(Feature feature : attributeFeatures){
			if(feature.getName().equals("MaxChildrenUp")){
				return (int) (getDefaultMaxChildren() * (1.0 + feature.getEffectiveness()) + 0.5);
			}
		}
		return getDefaultMaxChildren();
	}
	
	public Animal(ContinuousSpace<Object> space, Grid<Object> grid, Class<? extends Animal> self, Class<? extends Object> prey, long ID, List<Feature> attributeFeatures){
		this.space = space;
		this.grid = grid;
		this.attributeFeatures = attributeFeatures;
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
			int staminaAfter = stamina - getStaminaLoss();
			if(staminaAfter <= 0){
				stamina = staminaAfter;
				if(stamina <= -2){
					stamina = getMaxStamina();
				}
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
		isToEvolve();
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
		stamina -= getStaminaLoss();
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
			Animal partner = getReproducePartner(gridCells, actualStep);
			if(partner != null){
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
				this.lastReproduceTime = actualStep;
				partner.lastReproduceTime = actualStep;
				System.out.println(this+" and "+partner+" produced "+children+" children");
			}
		}	
	}
	
	private Animal getReproducePartner(List<GridCell<Object>> gridCells, long actualStep){
		for(GridCell<Object> cell : gridCells) {
			GridPoint point = cell.getPoint();
			for(Object obj : grid.getObjectsAt(point.getX(), point.getY())) {
				if(obj.getClass() == self){
					Animal partner =  (Animal) obj;
					if(actualStep - partner.lastReproduceTime >= getReproduceInterval()){
						return partner;
					}
				}
			}
		}
		return null;
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
		Constructor<?> ctor = self.getConstructor(ContinuousSpace.class, Grid.class, List.class, List.class);
		Animal animal = (Animal) ctor.newInstance(new Object[] { space, grid, new LinkedList<>(combatFeatures), new LinkedList<>(attributeFeatures) });
		animal.lastReproduceTime = (long) RepastEssentials.GetTickCount();
		context.add(animal);
		space.moveTo(animal, posX, posY);
		grid.moveTo(animal, (int)posX, (int)posY);
	}
	
	private void isToEvolve(){
		if(((long)RepastEssentials.GetTickCount() + getEvolveTimeOffset() + 2) % 40 == 0){
			if(getIsToEvolve()){
				Context<Object> context = ContextUtils.getContext(this);
				IndexedIterable<Object> objects = null;
				if(context != null && self != null){
					objects = context.getObjects(self);
					if(getLastEvolutionAnimalCount() > objects.size()){
						int preyCount = 0;
						for(Class<? extends Object> type: prey){
							preyCount += context.getObjects(type).size();
						}
						if(preyCount * 2 < objects.size()){
							Class<? extends Object> candidate = null;
							int candidateCount = 0;
							for(Class<? extends Object> type: FeatureUtils.AGENT_TYPES){
								if(prey.contains(type) || self.equals(type)) continue;
								int count = context.getObjects(type).size();
								if(count > candidateCount){
									candidateCount = count;
									candidate = type;
								}
							}
							if(candidate != null){
								if(RandomHelper.nextIntFromTo(0, 1) == 0){
									int removePreyIndex = RandomHelper.nextIntFromTo(0, prey.size() - 1);
									Class<? extends Object> removed = null;
									for(Object object : objects){
										Animal animal = (Animal) object;
										removed = animal.prey.remove(removePreyIndex);
										animal.prey.add(candidate);
									}
									System.out.println(this+" removed possible prey: "+removed.getSimpleName());
									System.out.println(this+" acquired possible prey: "+candidate.getSimpleName());
								} else {
									if(removeFeature(objects, false)){
										for(Object object : objects){
											Animal animal = (Animal) object;
											animal.prey.add(candidate);
										}
										System.out.println(this+" acquired possible prey: "+candidate.getSimpleName());
									}
								}
							}
						} else {
							if(removeFeature(objects, true)){
								addFeature(objects);
							}
						}
					}
				}
				setIsToEvolve(false);
				if(objects != null) setLastEvolutionAnimalCount(objects.size());
			}
		} else {
			setIsToEvolve(true);
		}
	}
	
	private boolean removeFeature(IndexedIterable<Object> objects, boolean removablePrey){
		if(removablePrey){
			if(RandomHelper.nextIntFromTo(0, 100) > 90){
				if(prey.size() > 1){
					int preyIndex = RandomHelper.nextIntFromTo(0, prey.size() - 1);
					Class<? extends Object> removed = null;
					for(Object object : objects){
						Animal animal = (Animal) object;
						removed = animal.prey.remove(preyIndex);
					}
					System.out.println(this+" removed possible prey: "+removed.getSimpleName());
					return true;
				}
			}
		}
		if(RandomHelper.nextIntFromTo(0, 1) == 0){
			if(attributeFeatures.size() > 1){
				int attributeFeaturesIndex = RandomHelper.nextIntFromTo(0, attributeFeatures.size() - 1);
				Feature feature = null;
				for(Object object : objects){
					Animal animal = (Animal) object;
					feature = animal.attributeFeatures.remove(attributeFeaturesIndex);
				}
				System.out.println(this+" removed attribute feature: "+feature.getName());
				return true;
			}
		}
		if(combatFeatures.size() > 1){
			int combatFeaturesIndex = RandomHelper.nextIntFromTo(0, combatFeatures.size() - 1);
			Feature feature = null;
			for(Object object : objects){
				Animal animal = (Animal) object;
				feature = animal.combatFeatures.remove(combatFeaturesIndex);
			}
			System.out.println(this+" removed combat feature: "+feature.getName());
			return true;
		} else {
			if(attributeFeatures.size() > 1){
				int attributeFeaturesIndex = RandomHelper.nextIntFromTo(0, attributeFeatures.size() - 1);
				Feature feature = null;
				for(Object object : objects){
					Animal animal = (Animal) object;
					feature = animal.attributeFeatures.remove(attributeFeaturesIndex);
				}
				System.out.println(this+" removed attribute feature: "+feature.getName());
				return true;
			}
		}
		return false;
	}
	
	private void addFeature(IndexedIterable<Object> objects){
		String featureName = null;
		boolean exists = false;
		if(RandomHelper.nextIntFromTo(0, 1) == 0){
			while(!exists){
				int index = RandomHelper.nextIntFromTo(0, FeatureUtils.ATTRIBUTE_FEATURE_NAMES.size() - 1);
				for(Feature feature : attributeFeatures){
					if(feature.getName().equals(FeatureUtils.ATTRIBUTE_FEATURE_NAMES.get(index))){
						exists = true;
						break;
					}
				}
				if(exists == false){
					featureName = FeatureUtils.ATTRIBUTE_FEATURE_NAMES.get(index);
					exists = true;
				} else {
					exists = false;
				}
			}
			for(Object object : objects){
				Animal animal = (Animal) object;
				animal.attributeFeatures.add(new Feature(featureName, (float) RandomHelper.nextDoubleFromTo(0.0, 1.0) ));
			}
			System.out.println(this+" added attribute feature: "+featureName);
		} else {
			while(!exists){
				int index = RandomHelper.nextIntFromTo(0, FeatureUtils.COMBAT_FEATURE_NAMES.size() - 1);
				for(Feature feature : attributeFeatures){
					if(feature.getName().equals(FeatureUtils.COMBAT_FEATURE_NAMES.get(index))){
						exists = true;
						break;
					}
				}
				if(exists == false){
					featureName = FeatureUtils.COMBAT_FEATURE_NAMES.get(index);
					exists = true;
				} else {
					exists = false;
				}
			}
			for(Object object : objects){
				Animal animal = (Animal) object;
				animal.combatFeatures.add(new Feature(featureName, (float) RandomHelper.nextDoubleFromTo(0.0, 1.0) ));
			}
			System.out.println(this+" added combat feature: "+featureName);
		}
	}
	
	@Override
	public String toString(){
		return "Animal: "+ID;
	}
	
	public void setCombatFeatures(List<Feature> combatFeatures){
		this.combatFeatures = combatFeatures;
	}

}
