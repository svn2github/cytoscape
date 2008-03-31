package de.layclust.taskmanaging;

import java.util.concurrent.Semaphore;

import de.layclust.layout.ILayoutInitialiser;
import de.layclust.layout.ILayouter;
import de.layclust.layout.LayoutFactory;
import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.geometric_clustering.GeometricClusteringFactory;
import de.layclust.layout.geometric_clustering.IGeometricClusterer;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.layout.postprocessing.IPostProcessing;
import de.layclust.layout.postprocessing.PostProcessingFactory;

public class ClusterPostProcessingTask implements Runnable{

	private ConnectedComponent cc = null;
	private Semaphore semaphore = null;
	private IParameters[] allparameters = null;
	private int[] layouterIntTypes = null;


	public ClusterPostProcessingTask (ConnectedComponent cc, 
			IParameters[] allparameters, int[] layouterIntTypes) {

		this.cc = cc;
		this.allparameters = allparameters;
		this.layouterIntTypes = layouterIntTypes;
	}

	public void run() {
		
		try {
			
			/* ==== LAYOUTING PHASE ==== */
			
			/* iterate over layouters */
			ILayouter previousLayouter = null;
			for (int i = 0; i < this.layouterIntTypes.length; i++) {
				IParameters param = this.allparameters[i];

				/* create correct layouter */
				ILayouter layouter = LayoutFactory
						.getLayouterByType(this.layouterIntTypes[i]);

				if (previousLayouter == null) {
					/* initialise cc positions */
					ILayoutInitialiser li = LayoutFactory
							.getLayouterInitialiserByType(this.layouterIntTypes[i]);
					li.initLayoutInitialiser(this.cc);

					/* initialise and run layouter */
					layouter.initLayouter(this.cc, li, param);
					layouter.run();
					previousLayouter = layouter;
				} else {
					/*
					 * initialise and run layouter with previous calculated
					 * positions
					 */
					layouter.initLayouter(this.cc, previousLayouter, param);
					layouter.run();
				}
			}


			/* ==== GEOMETRIC CLUSTERING PHASE ==== */
			IGeometricClusterer geoCluster = GeometricClusteringFactory.getGeometricClusterByType(
					TaskConfig.geometricClusteringType, this.cc);
			geoCluster.run();
			
			
			/* ==== POST-PROCESSING - MERGE AND REARRANGE ==== */ //TODO fixed type!! 
			IPostProcessing pp = PostProcessingFactory.getPostProcessorByType(PostProcessingFactory.PP_REARRANGE_AND_MERGE_BEST);
			pp.initPostProcessing(this.cc);
			pp.run();
			
	
			/* release this thread from semaphore to signal finished status */
			if(semaphore != null){
				semaphore.release();
			}
			
		} catch (InvalidTypeException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * If the use of Semaphores is wanted, then one can be set. Otherwise it is
	 * null;
	 * 
	 * @param semaphore
	 *            The semaphore to set.
	 */
	public void setSemaphore(Semaphore semaphore) {
		this.semaphore = semaphore;

		if (semaphore != null) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				// Thread interrupted, semaphore can't aquire
				e.printStackTrace();
			}
		}
	}
}