package de.layclust.layout.acc;

import java.util.Iterator;
import java.util.Vector;

public class GroupOfAnts implements ISAnt {
	private Vector ants;

	private int [] position;
	private int stepsize = 1;
	private StackPlayground playground;
	private double m;
	private double n;
	private double lnN;
	
	public GroupOfAnts(Vector ants, int[] position, StackPlayground playground) {
		this.ants = ants;
		this.position = position;
		this.playground = playground;		
		this.m = playground.getM();
		this.n = playground.getN();
		this.lnN = Math.log(n);
	}
	
	
	/**
	 * Computes the (summed up) similarity of the ants of this group to another ant.
	 * @return
	 */
	public float computeSimilarity(SleepingAnt secondAnt) {
		float simsum = 0;
		for(int i = 0; i < ants.size(); i++) {
			simsum += this.playground.getSimilarity((ISAnt)(ants.get(i)), secondAnt);
		}
		return (float) ((1.0 / ants.size()) *  simsum);
	}
	
	public float computeSimilarity(GroupOfAnts secondAnt) {
		float simsum = 0;
		for(int i = 0; i < ants.size(); i++) {
			simsum += this.playground.getSimilarity((ISAnt)(ants.get(i)), secondAnt);
		}
		return (float) ((1.0 / ants.size()) *  simsum);
	}


	public void makeStep() {
		//System.out.println("M:" +m); 
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
	
	
	
	private boolean wakeUp(){
		//TODO wakeupmodifier!
		Vector items = playground.getPosition(position);
		//System.out.println(items.size());
		float sum = 0;
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			ISAnt ant = (ISAnt) iter.next();
				if(this.equals(ant)) continue;
				sum += playground.getSimilarity(this, ant);
			
		}
		//double wakeUpProb = playground.getIterationquotient() * wakeupmodifier * 1.0/2.0;
		//double wakeUpProb = 1.0;
		if(items.size() > 1) {
			sum = sum / (items.size()-1);
		}
		double wakeUpProb = (1-playground.getIterationquotient()) * Math.exp(-m * sum + lnN);
		//System.out.println("Sum:"+sum+" / WakeUpProb: "+wakeUpProb);
		if(Math.random() < wakeUpProb) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean restHere(){
		//TODO wakeupmodifier!
		Vector items = playground.getPosition(position);
		//System.out.println(items.size());
		float sum = 0;
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			ISAnt ant = (ISAnt) iter.next();
			if (this.equals(ant)) continue;
			sum += playground.getSimilarity(this, ant);
		}
		//double wakeUpProb = playground.getIterationquotient() * wakeupmodifier * 1.0/2.0;
		//double wakeUpProb = 1.0;
		if(items.size() > 1) {
			sum = sum / (items.size()-1);
		}
		if(items.size() == 0) {
			sum = (float) 0.35;
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
	}
	
	
	
	public void setSpreadMode(boolean spreadMode) {
	}

	public void drop() {
	}

	public boolean isSpreadMode() {
		return false;
	}
	
	
	public int[] getPosition() {
		return position;
	}


	public void setPosition(int[] position) {
		this.position = position;
	}


	public Vector getAnts() {
		return ants;
	}


}
