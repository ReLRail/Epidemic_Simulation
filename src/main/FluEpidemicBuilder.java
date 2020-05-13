package fluEpidemic;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class FluEpidemicBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		context.setId("fluEpidemic");
		
		// for getting the parameters from the model
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		int dimension = 225;
		
		// build the infection network
		NetworkBuilder<Object> netBuilder =
				new NetworkBuilder<Object>("infection network", context, true);
		netBuilder.buildNetwork();
		
		// factories
		ContinuousSpaceFactory spaceFactory = 
				ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space =
				spaceFactory.createContinuousSpace("space", context, 
						new RandomCartesianAdder<Object>(), 
						new repast.simphony.space.continuous.WrapAroundBorders(), 
						dimension, dimension);
		
		GridFactory gridFactory =
				GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid =
				gridFactory.createGrid("grid", context, 
						new GridBuilderParameters<Object>(new WrapAroundBorders(), 
								new SimpleGridAdder<Object>(), 
								true, 
								dimension, dimension));
		
		////////////////////////////////////
		
		// adding the agents to the model
		int infectedCount = 2;//(Integer)params.getValue("infected_count");
		for(int i=0; i < infectedCount; i++) {
			context.add(new Infected(space, grid, 0));
		}
		
		int susceptibleCount = 2048;//(Integer)params.getValue("susceptible_count");
		for(int i=0; i < susceptibleCount; i++) {
			context.add(new Susceptible(space, grid));
		}
		
		////////////////////////////////////
		
		// moving the agents to the Grid location that
		// corresponds to their ContinuousSpace location
		for(Object obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)pt.getX(), (int)pt.getY());
		}
		
		return context;
	}
}
