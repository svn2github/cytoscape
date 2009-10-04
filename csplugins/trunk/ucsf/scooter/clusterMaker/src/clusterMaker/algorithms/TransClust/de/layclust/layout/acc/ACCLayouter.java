package de.layclust.layout.acc;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.layout.ILayoutInitialiser;
import de.layclust.layout.ILayouter;
import de.layclust.layout.IParameters;

/**
 * A Layouter that uses ant-colony-clustering for layouting.
 * @author Nils Kleinbölting
 *
 */
public class ACCLayouter implements ILayouter {

	private ConnectedComponent cc;
	private IPlayground playground;
	private ILayoutInitialiser initialiser;
	private ACCParameters parameters;
	private ILayouter layouter;
	
	/**
	 * Constructor for initialising ACCLayouter with an initial layouter
	 * (first layouter). The layout initaliser is then run in the constructor.
	 * @param cc The connected Component.
	 * @param li The layout initialiser.
	 * @param parameters The parameters for ACC.
	 */
	public void initLayouter(ConnectedComponent cc, ILayoutInitialiser li, 
			IParameters parameters) {
		this.cc = cc;
		li.run();
		if(((ACCParameters)parameters).getAntType().equals("SleepingAnt")) {
			this.playground = new StackPlayground(cc,parameters);
		} else {
			this.playground = new Playground(cc, parameters);
		}
		this.parameters = (ACCParameters) parameters;
		this.initialiser = li;
	}
	
	/**
	 * The constructor for initalising ACCLayouter if a previous {@link ILayouter}
	 * has already been run. Just in case a different {@link ConnectedComponent}
	 * is used here than for the previous layouter, the positions of the previous
	 * layouter are set to this cc.
	 * @param cc The connected Component.
	 * @param layouter The previously run layouter.
	 * @param parameters The parameters for ACC.
	 */
	public void initLayouter(ConnectedComponent cc, ILayouter layouter, 
			IParameters parameters) {
		this.cc = cc;
		if(((ACCParameters)parameters).getAntType().equals("SleepingAnt")) {
			this.playground = new StackPlayground(cc,parameters);
		} else {
			this.playground = new Playground(cc, parameters);
		}
		this.layouter = layouter;
		this.parameters = (ACCParameters)parameters;
		
	}
	
	/**
	 * This Constructor is for the parameter training. Here the positions
	 * should have already been initialised. If this is not so, then
	 * the positions are initialised with the correct {@link ILayoutInitialiser}
	 * implementation.
	 * @param cc The connected Component.
	 * @param parameters The parameters for ACC.
	 */
	public void initLayouter(ConnectedComponent cc, IParameters parameters) {
		this.cc = cc;
		if(((ACCParameters)parameters).getAntType().equals("SleepingAnt")) {
			this.playground = new StackPlayground(cc,parameters);
		} else {
			this.playground = new Playground(cc, parameters);
		}

		this.parameters = (ACCParameters) parameters;
	}

	/**
	 * Runs the ACC algorithm to layout the objects for one {@link ConnectedComponent}.
	 */
	public void run() {
		if(initialiser != null ) {
			initialiser.run();
		}
		if(layouter != null) {
			layouter.run();
		}
		playground.run();
		this.cc = playground.getCC();
		
	}




	/**
	 * This method is needed for passing on the positions of a previous ILayouter to
	 * the next one.
	 * @return The node positions of the object's ConnectedComponent instance.
	 */
	public double[][] getNodePositions() {
		return playground.getCC().getCCPositions();
	}




}
