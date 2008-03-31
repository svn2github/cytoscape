package de.layclust.layout.acc;

public class JumpingAntWithIncreasingViewField extends JumpingAnt {

	protected int maxSteps;
	protected int steps;
	protected int maxViewSize; 
	
	
	
	public JumpingAntWithIncreasingViewField(Playground playground) {
		super(playground);
		this.maxSteps = playground.getNoOfIterations();
		this.steps = 0;
		this.maxViewSize = 3;
	}
	
	public void makeStep(){
		super.makeStep(false);
		steps++;
		//increase view-size linear:
		if(steps % 10000 == 0) {
			//System.out.println("Steps: "+steps);
			//System.out.println(kp);
		}
		if(view != maxViewSize && steps > view * maxSteps / maxViewSize) {
			view++;
			//System.out.println("View: "+ view);
		}
		//decreasing pick-up-probability:
		kp = basic_kp * (double) ( maxSteps-steps) / (maxSteps); 
	}

	public int getMaxSteps() {
		return maxSteps;
	}

	public void setMaxSteps(int maxSteps) {
		this.maxSteps = maxSteps;
	}
}
