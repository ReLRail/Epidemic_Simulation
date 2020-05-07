package fluEpidemic;

import java.util.ArrayList;
import java.util.List;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
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

public class Infected extends Human {
	
	// parameter for setting how many steps are in one day
	private static final int STEPS_DAY = 12;
	
	// for overseeing the duration of the infection, which on average
	// lasts for 7 days
	private int stepsInfected;
	
	public Infected(ContinuousSpace<Object> space, Grid<Object> grid, int stepsInfected) {
		super(space, grid);
		this.stepsInfected = stepsInfected;
	}

	// method that gets executed every step in the simulation
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		Grid<Object> grid = getGrid();
		// get the grid location
		GridPoint pt = grid.getLocation(this);
		
		// create a neighborhood for the Infected
		GridCellNgh<Object> nghCreator = 
				new GridCellNgh<Object>(grid, pt, Object.class, 1, 1);
		List<GridCell<Object>> gridCells = nghCreator.getNeighborhood(true);
		// shuffle the list of ngh cells so if there are same cells the 
		// movement will be random
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		// move randomly
		move(gridCells.get(0).getPoint());
		//move(gridCells.get(new Random().nextInt(gridCells.size())).getPoint());
		
		// infect if there are any Healthy population in the ngh
		// the person is infectious to another from 1 day after catching the
		// flu up to 7 days
		if(stepsInfected > STEPS_DAY) {
			infect(gridCells);
		}
		
		// if the Infected is alive after 7 days, it becomes
		// Recovered and now he is immune to the influenza
		// in the current flu season
		if(stepsInfected >= 7*STEPS_DAY) {
			recover();
		}
		
		stepsInfected++;
	}

	public void move(GridPoint pt) {
		Grid<Object> grid = getGrid();
		ContinuousSpace<Object> space = getSpace();
		
		// move if not already on the same grid location
		if(!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			// calculate the angle along which the Infected should move
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			
			// move 1 point in the direction of the angle
			space.moveByVector(this, 1, angle, 0);
			
			// update the grid coordinates according to the space coordinates
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		}
	}
	
	
	public void infect(List<GridCell<Object>> gridCells) {
		Grid<Object> grid = getGrid();
		ContinuousSpace<Object> space = getSpace();
		
		GridPoint pt = grid.getLocation(this);
		// list for the Susceptible population near the Infected
		List<Object> healthy = new ArrayList<Object>();
		
		// get all the Susceptible in the neighborhood and on the same tile
		for(GridCell<Object> nghCell : gridCells) {
			for(Object obj : nghCell.items()) {
				if(obj instanceof Susceptible) {
					healthy.add(obj);
				}
			}
		}
		
		if(healthy.size() > 0) {
			
			for(Object obj : healthy) {
				NdPoint spacePt = space.getLocation(obj);
				
				double rand = Math.random();
				// the probability of getting infected is 19.3%
				if(0.193 >= rand) {
				
					Context<Object> context = ContextUtils.getContext(obj);
					// remove the Susceptible person
					context.remove(obj);
					
					// create the new Infected
					Infected infected = new Infected(space, grid, 0);
					context.add(infected);
					
					// place the Infected on the place of the Susceptible that has been infected
					space.moveTo(infected, spacePt.getX(), spacePt.getY());
					grid.moveTo(infected, pt.getX(), pt.getY());
					
					// create a network to know who infected who
					Network<Object> net = 
							(Network<Object>)context.getProjection("infection network");
					net.addEdge(this, infected);
				}
			}
		}
	}
	
	
	// recover from the flu
	public void recover() {
		Grid<Object> grid = getGrid();
		ContinuousSpace<Object> space = getSpace();
		
		// store the grid and space location of the current instance (this)
		GridPoint pt = grid.getLocation(this);
		NdPoint spacePt = space.getLocation(this);
		
		Object obj = (Object)this;
		
		Context<Object> context = ContextUtils.getContext(obj);
		// remove the Infected person
		context.remove(obj);
		
		// create the new Recovered on the place of the Infected
		Recovered recovered = new Recovered(space, grid);
		context.add(recovered);
		
		// place the Recovered on the place of the Infected that has recovered
		space.moveTo(recovered, spacePt.getX(), spacePt.getY());
		grid.moveTo(recovered, pt.getX(), pt.getY());
	}
}
