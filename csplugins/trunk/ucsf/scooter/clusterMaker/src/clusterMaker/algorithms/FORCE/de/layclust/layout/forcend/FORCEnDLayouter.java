package de.layclust.layout.forcend;

// import org.apache.log4j.Logger;

import de.layclust.layout.ILayoutInitialiser;
import de.layclust.layout.ILayouter;
import de.layclust.layout.LayoutFactory;
import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;

public class FORCEnDLayouter implements ILayouter {
	
	// private static org.apache.log4j.Logger log = Logger.getLogger(FORCEnDLayouter.class);
	
	private ConnectedComponent cc = null;
	private int dim = -1;
	private FORCEnDParameters parameters = null;
	
	public FORCEnDLayouter(){}

	/**
	 * Constructor for initialising FORCEnDLayouter with an initial layouter
	 * (first layouter). The layout initaliser is then run in the constructor.
	 * @param cc The connected Component.
	 * @param li The layout initialiser.
	 * @param parameters The parameters for FORCEnD.
	 */
	public void initLayouter(ConnectedComponent cc, ILayoutInitialiser li, 
			IParameters parameters) {
		this.dim = TaskConfig.dimension;
		this.cc = cc;
		this.parameters = (FORCEnDParameters) parameters;
		li.run();
	}

	/**
	 * The constructor for initalising FORCEnDLayouter if a previous {@link ILayouter}
	 * has already been run. Just in case a different {@link ConnectedComponent}
	 * is used here than for the previous layouter, the positions of the previous
	 * layouter are set to this cc.
	 * @param cc The connected Component.
	 * @param layouter The previously run layouter.
	 * @param parameters The parameters for FORCEnD.
	 */
	public void initLayouter(ConnectedComponent cc, ILayouter layouter,
			IParameters parameters) {
		this.dim = TaskConfig.dimension;
		this.cc = cc;
		this.parameters = (FORCEnDParameters) parameters;
		
		/* sets node positions to those of the previous layouter */
		this.cc.setCCPositions(layouter.getNodePositions());
	}
	
	/**
	 * This Constructor is for the parameter training. Here the positions
	 * should have already been initialised. If this is not so, then
	 * the positions are initialised with the correct {@link ILayoutInitialiser}
	 * implementation.
	 * @param cc The connected Component.
	 * @param parameters The parameters for FORCEnD.
	 */
	public void initLayouter(ConnectedComponent cc, IParameters parameters){
		this.dim = TaskConfig.dimension;
		this.cc = cc;
		this.parameters = (FORCEnDParameters) parameters;
		
		if(cc.getCCPositions()==null){
			System.out.println("Positions have not been initialised, perhaps" +
			 		"wrong use of this constructor!");
			// log.warn("Positions have not been initialised, perhaps" +
			// 		"wrong use of this constructor!");
			ILayoutInitialiser li;
			try {
				li = LayoutFactory.getLayouterInitialiserByType(LayoutFactory.FORCEND);
				li.initLayoutInitialiser(cc);
				li.run();
			} catch (InvalidTypeException e) {
				// log.error(e.getMessage());
				System.err.println(e.getMessage());
				// log.warn("Using LayoutInitHSphere as default layout initialiser. If this is" +
				// 		"unwanted, then the previous error need to be taken care of.");
				System.out.println("Using LayoutInitHSphere as default layout initialiser. If this is" +
						"unwanted, then the previous error need to be taken care of.");
				
				li = new LayoutInitHSphere(cc);
				li.run();
			}
		}
	}

	/**
	 * Runs the FORCEnD algorithm to layout the objects for one {@link ConnectedComponent}.
	 */
	public void run() {

		int node_no = this.cc.getNodeNumber();
		double[][] node_pos = this.cc.getCCPositions();

		double[][] allDisplacements = new double[node_no][this.dim];

		/* for each iteration calculate the displacement vectors 
		 * and move all nodes by this after calculation in one go */
		for(int it = 0; it<this.parameters.getIterations();it++){
			
			/* the cooling temperature factor for this iteration */
			double temperature = FORCEnDLayoutUtility.calculateTemperature(it, 
					node_no, this.parameters);
			
			FORCEnDLayoutUtility.calculateDisplacementVectors(allDisplacements, this.cc, 
					this.dim, this.parameters);
			
			FORCEnDLayoutUtility.moveAllNodesByDisplacement(allDisplacements, 
					node_pos, node_no, this.dim, temperature);			
	
		}
	}
	



	/**
	 * This method is needed for passing on the positions of a previous ILayouter to
	 * the next one.
	 * @return The node positions of the object's ConnectedComponent instance.
	 */
	public double[][] getNodePositions() {
		return this.cc.getCCPositions();
	}

}
