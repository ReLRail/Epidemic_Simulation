package fluEpidemic;

import java.util.List;
import java.util.Random;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

public class Recovered extends Human {

	public Recovered(ContinuousSpace<Object> space, Grid<Object> grid) {
		super(space, grid);
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		Grid<Object> grid = getGrid();
		// get the grid location
		GridPoint pt = grid.getLocation(this);
		
		// create a neighborhood for the Recovered
		GridCellNgh<Object> nghCreator = 
				new GridCellNgh<Object>(grid, pt, Object.class, 1, 1);
		List<GridCell<Object>> gridCells = nghCreator.getNeighborhood(true);
		// shuffle the list of ngh cells so the movement will be random
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		// move randomly
		//move(gridCells.get(new Random().nextInt(gridCells.size())).getPoint());
		move(gridCells.get(0).getPoint());
	}
	
	
	public void move(GridPoint pt) {
		Grid<Object> grid = getGrid();
		ContinuousSpace<Object> space = getSpace();
		
		// move if not already on the same grid location
		if(!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			// calculate the angle along which the Susceptible should move
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
			
			// move 1 point in the direction of the angle
			space.moveByVector(this, 1, angle, 0);
			
			// update the grid coordinates according to the space coordinates
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
		}
	}

}
