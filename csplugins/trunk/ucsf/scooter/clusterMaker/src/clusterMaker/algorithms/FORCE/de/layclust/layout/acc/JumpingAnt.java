package de.layclust.layout.acc;

import java.lang.reflect.Array;
import java.util.Arrays;

public class JumpingAnt extends SimpleAnt {

	protected int max_stepsize = 20;
	
	
	public JumpingAnt(Playground playground) {
		super(playground);
		System.out.println(kp);
		this.playground = playground;
		position = new int[playground.getDimension()];
		System.out.println();
		this.item = 0;
		this.alpha = 1.00;
		this.numberOfFailedDropOperationsToIncreaseAlpha = 199;
		this.numberActiveMoves = 200;
		this.numberOfMoves = 0;
		this.failedDropOperations = 0;
		this.max_stepsize = playground.getMaxStepSizeForJumpingAnts();
		performPickUpJump();
	}
	
	protected void performMovement(int stepsize) {
		//compute random movement:
		int[] mov = new int[playground.getDimension()];
		for (int i = 0; i < mov.length; i++) {
			//move -max_stepsize-max_stepsize in one dimension
			mov[i] = ((int)Math.round(Math.random() * 2 * stepsize) -stepsize) ;
		}
		//perform movement:
		for (int i = 0; i < mov.length; i++) {
			position[i] = (position[i] + mov[i]);
			if(position[i] < 0) {
				position[i] = playground.getSize() + position[i];
			} else {
				position[i] = position[i] % (playground.getSize());
			}
		}
	}
	
	protected void performMovement() {
		//compute random movement:
		int[] mov = new int[playground.getDimension()];
		for (int i = 0; i < mov.length; i++) {
			//move -max_stepsize-max_stepsize in one dimension
			mov[i] = ((int)Math.round(Math.random() * 2 * max_stepsize) -max_stepsize) ;
		}
		//perform movement:
		for (int i = 0; i < mov.length; i++) {
			position[i] = (position[i] + mov[i]);
			if(position[i] < 0) {
				position[i] = playground.getSize() + position[i];
			} else {
				position[i] = position[i] % (playground.getSize());
			}
		}
	}
	
	public void makeStep(boolean last) {
		//when ant carries no item, check for new item to pick up:
		if(item == 0) {
			performPickUpJump();
		//otherwise compute probability to unload the item
		} else {
			//System.out.print("Ant carries item "+item+" and moves from "+Arrays.toString(position));
			performMovement();
			//System.out.println(" to "+Arrays.toString(position));
			if(playground.getLocation(position) == 0) {
				double prob = computeDropProbability();
				if(Math.random() < prob) {
					//System.out.println("Dropped "+item);
					playground.setLocation(item, position);
					playground.setItemPosition(item, position);
					playground.addUncarriedItem(item);
					item = 0;
					updateAlpha(true);
					if(!last) {
						performPickUpJump();
					}
				} else {
					updateAlpha(false);
				}
			} else {
				//System.out.println("Place already used.");
			}
		}
	}
	
	public void drop() {
		if (item == 0) return;
		System.out.println("Last Item: "+item);
		while(item != 0) {
			makeStep(true);
		}
	}
	
	
	public void makeStep(){
		makeStep(false);
	}
	
	protected void performPickUpJump() {
		//while (this.item == 0) {
			int it = playground.getUncarriedItem();
			this.position = playground.getItemLocation(it);
			double prob = computePickUpProbability();
			item = playground.getLocation(position);
			if(Math.random() < prob) {
				//System.out.println("Picked up "+playground.getLocation(position));
				playground.setLocation(0, position);
			} else {
				playground.addUncarriedItem(item);
				item = 0;
			}
		//}
	}
	
	
}
