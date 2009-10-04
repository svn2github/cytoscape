package de.layclust.layout.acc;

import java.util.Iterator;
import java.util.Vector;

/**
 * This class implements a SleepingAnt. SleepingAnts walk over the grid, 
 * searching for a suitable neighbourhood which is one with similar ants.
 * Before performing a movement the ant decides whether to wake up. 
 * The probability to wake up is based on the similarity to the ants on the
 * same place of the grid. Low similarity leeds to higher, high similarity to
 * lower probability.
 * 
 * @author Nils Kleinbölting
 *
 */
public class SleepingAnt implements ISAnt {

	private int[] position;
	private int id;
	private int stepsize = 1;
	private StackPlayground playground;
	private double m;
	private double n;
	private double lnN;
	
	/**
	 * Initialises the SleepingAnt. 
	 * 
	 * @param position the initial position on the grid.
	 * @param id id of the ant.
	 * @param playground the StackPlayground instance.
	 */
	public SleepingAnt(int [] position, int id, StackPlayground playground) {
		this.position = position;
		this.id = id;
		this.playground = playground;
		this.m = playground.getM();
		this.n = playground.getN();
		this.lnN = Math.log(n);
	}
	
	/**
	 * Returns the current position of this ant.
	 * @return current position
	 */
	public int[] getPosition() {
		return position;
	}

	/**
	 * Sets the current position of this ant.
	 * 
	 * @param position new position
	 */
	public void setPosition(int[] position) {
		this.position = position;
	}

	public void drop() {		
	}

	/**
	 * Performs a step. A step includes, the decision about waking up or not, 
	 * and movement (if ant wakes up) until a suitable place to rest is found.
	 */
	public void makeStep() {
		if(wakeUp()) {
			boolean suc = playground.removeAnt(this, position);
			if(!suc) {
				//System.out.println("!!!!");
				boolean suc2 = playground.findAndRemoveAnt(this);
				//if(suc2) System.out.println(":))))))");
			}
			performMovement();
			while (!restHere()) {
				performMovement();
			}
			playground.setAntToPosition(this, position);
		}
	}


	/**
	 * Decides if the ant wakes up or not. The decision is based on the similarity 
	 * of ants at the same grid cell.
	 * 
	 * @return true if ant should wake up, false otherwise
	 */
	private boolean wakeUp(){
		Vector items = playground.getPosition(position);
		//System.out.println(items.size());
		float sum = 0;
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			ISAnt ant = (ISAnt) iter.next();
			if (ant.equals(this)) continue;
			sum += playground.getSimilarity(this, ant);
		}
		//double wakeUpProb = playground.getIterationquotient() * wakeupmodifier * 1.0/2.0;
		//double wakeUpProb = 1.0;
		if(items.size() > 1) {
			sum = sum / (items.size()-1);
		}
		double wakeUpProb = (1-playground.getIterationquotient()) * Math.exp(-m * sum + lnN);
		//System.out.println("Sum:"+sum+" / WakeUpProb: "+wakeUpProb);
		if(items.size() == 0) {
			wakeUpProb = 1;
		}
		if(Math.random() < wakeUpProb) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Decides if the current grid cell is a good place to rest, meaning one
	 * with similar ants in the neighbourhood.
	 * 
	 * @return true if ant should rest here, false otherwise
	 */
	private boolean restHere(){
		Vector items = playground.getPosition(position);
		//System.out.println(items.size());
		float sum = 0;
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			ISAnt ant = (ISAnt)iter.next();
			if (ant.equals(this)) continue;
			sum += playground.getSimilarity(this, ant);
		}
		//double wakeUpProb = playground.getIterationquotient() * wakeupmodifier * 1.0/2.0;
		//double wakeUpProb = 1.0;
		if(items.size() > 1) {
			sum = sum / (items.size()-1);
		}
		if(items.size() == 0) {
			sum = (float) 0.1;
		}
		double restProb = sum; 
		//System.out.println("Sum:"+sum+" / WakeUpProb: "+wakeUpProb);
		if(Math.random() < restProb) {
			return true;
		} else {
			return false;
		}
	}

	protected void performMovement() {
		//compute random movement:
//		boolean davor = playground.checkConsis();
		int[] mov = new int[playground.getDimension()];
		for (int i = 0; i < mov.length; i++) {
			//move -max_stepsize-max_stepsize in one dimension
			mov[i] = ((int)Math.round(Math.random() * 2 * stepsize) -stepsize) ;
		}
		//perform movement:
		for (int i = 0; i < mov.length; i++) {
			position[i] = (position[i] + mov[i]);
			while (position[i] < 0) {
				position[i] = playground.getSize() + position[i];
			} 
			while (position[i] >= playground.getSize()) {
				position[i] = position[i] - playground.getSize();
			}
		}
//		boolean danach = playground.checkConsis();
//		if(davor != danach) {
//			System.out.println("DA!!!!!");
//			System.out.println(mov[0]+"//"+mov[1]);
//			System.out.println("playgroundsize: "+playground.getSize()+"moved to: "+position[0]+"/"+position[1]);
//		}
	}

	public int getId() {
		return id;
	}
}
