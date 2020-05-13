package main;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public abstract class Human {
	
	// variables that hold the location of the healthy person
		private ContinuousSpace<Object> space;
		private Grid<Object> grid;
		
		public Human(ContinuousSpace<Object> space, Grid<Object> grid) {
			this.space = space;
			this.grid = grid;
		}

		public ContinuousSpace<Object> getSpace() {
			return space;
		}

		public void setSpace(ContinuousSpace<Object> space) {
			this.space = space;
		}

		public Grid<Object> getGrid() {
			return grid;
		}

		public void setGrid(Grid<Object> grid) {
			this.grid = grid;
		}

		abstract void step();
}
