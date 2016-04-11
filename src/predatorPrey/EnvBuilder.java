package predatorPrey;

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
		Grass.GRASS_COUNT = 0;
		Leopard.LEOPARD_COUNT = 0;
		Antelope.ANTELOPE_COUNT = 0;
		Wolf.WOLF_COUNT = 0;
		Caribou.CARIBOU_COUNT = 0;
		Lichen.LICHEN_COUNT = 0;
		Fox.FOX_COUNT = 0;
		Rabbit.RABBIT_COUNT = 0;
		Carrot.CARROT_COUNT = 0;
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), 100, 100);
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(new WrapAroundBorders(), 
				new SimpleGridAdder<Object>(), true, 100, 100));
		int leopardCount = 75;
		for (int i = 0; i < leopardCount ; i++) {
			context.add(new Leopard(space, grid));
		}
		int antelopeCount = 300;
		for(int i = 0; i < antelopeCount; i++) {
			context.add(new Antelope(space, grid));
		}
		int grassCount = 900;
		for(int i = 0; i < grassCount; i++) {
			context.add(new Grass(space, grid));
		}
		int wolfCount = 75;
		for (int i = 0; i < wolfCount ; i++) {
			context.add(new Wolf(space, grid));
		}
		int caribouCount = 300;
		for(int i = 0; i < caribouCount; i++) {
			context.add(new Caribou(space, grid));
		}
		int lichenCount = 900;
		for(int i = 0; i < lichenCount; i++) {
			context.add(new Lichen(space, grid));
		}
		int foxCount = 75;
		for (int i = 0; i < foxCount ; i++) {
			context.add(new Fox(space, grid));
		}
		int rabbitCount = 300;
		for(int i = 0; i < rabbitCount; i++) {
			context.add(new Rabbit(space, grid));
		}
		int carrotCount = 900;
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
