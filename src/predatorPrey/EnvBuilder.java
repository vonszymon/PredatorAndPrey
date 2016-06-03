package predatorPrey;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class EnvBuilder implements ContextBuilder<Object>{

	@Override
	public Context build(Context<Object> context) {
		context.setId("PredatorPrey");
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 100, 100);
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(new WrapAroundBorders(), 
				new SimpleGridAdder<Object>(), true, 100, 100));
		int leopardCount = 75;
		Leopard.lastEvolutionAnimalCount = leopardCount;
		List<Feature> combatFeatures = Arrays.asList(new Feature("Poison", 0.5f), new Feature("LongClaws", 0.5f));
		List<Feature> attributeFeatures = Arrays.asList(new Feature("SpeedUp", 0.5f), new Feature("MaxEnergyUp", 0.5f));
		for (int i = 0; i < leopardCount ; i++) {
			context.add(new Leopard(space, grid, new LinkedList<>(combatFeatures), new LinkedList<>(attributeFeatures)));
		}
		int antelopeCount = 500;
		Antelope.lastEvolutionAnimalCount = antelopeCount;
		combatFeatures = Arrays.asList(new Feature("Stench", 0.5f), new Feature("Armor", 0.5f));
		attributeFeatures = Arrays.asList(new Feature("ReproduceEnergyDown", 0.5f), new Feature("MaxEnergyUp", 0.5f));
		for(int i = 0; i < antelopeCount; i++) {
			context.add(new Antelope(space, grid, new LinkedList<>(combatFeatures), new LinkedList<>(attributeFeatures)));
		}
		int grassCount = 1000;
		for(int i = 0; i < grassCount; i++) {
			context.add(new Grass(space, grid));
		}
		int wolfCount = 75;
		Wolf.lastEvolutionAnimalCount = wolfCount;
		combatFeatures = Arrays.asList(new Feature("Web", 0.5f), new Feature("Paralysis", 0.5f));
		attributeFeatures = Arrays.asList(new Feature("MaxStaminaUp", 0.5f), new Feature("MaxChildrenUp", 0.5f));
		for (int i = 0; i < wolfCount ; i++) {
			context.add(new Wolf(space, grid, new LinkedList<>(combatFeatures), new LinkedList<>(attributeFeatures)));
		}
		int caribouCount = 500;
		Caribou.lastEvolutionAnimalCount = caribouCount;
		combatFeatures = Arrays.asList(new Feature("Horns", 0.5f), new Feature("Camouflage", 0.5f));
		attributeFeatures = Arrays.asList(new Feature("SpeedUp", 0.5f), new Feature("MaxChildrenUp", 0.5f));
		for(int i = 0; i < caribouCount; i++) {
			context.add(new Caribou(space, grid, new LinkedList<>(combatFeatures), new LinkedList<>(attributeFeatures)));
		}
		int lichenCount = 1000;
		for(int i = 0; i < lichenCount; i++) {
			context.add(new Lichen(space, grid));
		}
		int foxCount = 75;
		Fox.lastEvolutionAnimalCount = foxCount;
		combatFeatures = Arrays.asList(new Feature("SharpTooth", 0.5f), new Feature("LongClaws", 0.5f));
		attributeFeatures = Arrays.asList(new Feature("MaxEnergyUp", 0.5f), new Feature("ReproduceIntervalDown", 0.5f));
		for (int i = 0; i < foxCount ; i++) {
			context.add(new Fox(space, grid, new LinkedList<>(combatFeatures), new LinkedList<>(attributeFeatures)));
		}
		int rabbitCount = 500;
		Rabbit.lastEvolutionAnimalCount = rabbitCount;
		combatFeatures = Arrays.asList(new Feature("ScaryBody", 0.5f), new Feature("Poison", 0.5f));
		attributeFeatures = Arrays.asList(new Feature("ReproduceIntervalDown", 0.5f), new Feature("ReproduceEnergyDown", 0.5f));
		for(int i = 0; i < rabbitCount; i++) {
			context.add(new Rabbit(space, grid, new LinkedList<>(combatFeatures), new LinkedList<>(attributeFeatures)));
		}
		int carrotCount = 1000;
		for(int i = 0; i < carrotCount; i++) {
			context.add(new Carrot(space, grid));
		}
		for ( Object obj : context ) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int) pt.getX(), (int) pt.getY());
		}
		return context;
	}
}
