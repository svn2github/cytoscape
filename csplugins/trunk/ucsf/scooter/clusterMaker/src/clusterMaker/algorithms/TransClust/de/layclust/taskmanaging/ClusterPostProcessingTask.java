package de.layclust.taskmanaging;

import java.util.concurrent.Semaphore;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.fixedparameterclustering.FixedParameterClusterer;
import de.layclust.geometric_clustering.IGeometricClusterer;
import de.layclust.greedy.GreedyClusterer;
import de.layclust.layout.ILayoutInitialiser;
import de.layclust.layout.ILayouter;
import de.layclust.layout.IParameters;
import de.layclust.layout.LayoutFactory;
import de.layclust.postprocessing.IPostProcessing;
import de.layclust.postprocessing.PP_RearrangeAndMergeBest;
import de.layclust.postprocessing.PostProcessingFactory;
import de.layclust.taskmanaging.TaskConfig;
/**
 * This class carries out the clustering procedure for post-processing. This means no output is made
 * and the post-processing used here is just {@link PP_RearrangeAndMergeBest}.
 * This is fixed here.
 * 
 * @author Sita Lange
 *
 */
public class ClusterPostProcessingTask implements Runnable {

	private ConnectedComponent cc = null;

	private Semaphore semaphore = null;

	private IParameters[] allparameters = null;

	private LayoutFactory.EnumLayouterClass[] layouterEnumTypes = null;

	public ClusterPostProcessingTask(ConnectedComponent cc,
			IParameters[] allparameters,
			LayoutFactory.EnumLayouterClass[] layouterEnumTypes) {

		this.cc = cc;
		this.allparameters = allparameters;
		this.layouterEnumTypes = layouterEnumTypes;
	}

	public void run() {
	
	//		if(false){
		if(!TaskConfig.fixedParameter||this.cc.getNodeNumber()>=TaskConfig.fixedParameterMax) TaskConfig.fpStopped = true;
		
		if(TaskConfig.fixedParameter && this.cc.getNodeNumber()<TaskConfig.fixedParameterMax){
			new GreedyClusterer(this.cc);
			new FixedParameterClusterer(this.cc,this.cc.getClusteringScore());
		}		
		if(TaskConfig.greedy&&TaskConfig.fpStopped){
			GreedyClusterer gc = new GreedyClusterer(this.cc);
			
			
		}else if(TaskConfig.fpStopped){
			/* ==== LAYOUTING PHASE ==== */

			/* iterate over layouters */
			ILayouter previousLayouter = null;
			for (int i = 0; i < this.layouterEnumTypes.length; i++) {
				IParameters param = this.allparameters[i];

				/* create correct layouter */
				ILayouter layouter = this.layouterEnumTypes[i].createLayouter();

				if (previousLayouter == null) {
					/* initialise cc positions */
					ILayoutInitialiser li = this.layouterEnumTypes[i]
							.createLayoutInitialiser();
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
			IGeometricClusterer geoCluster = TaskConfig.geometricClusteringEnum
					.createGeometricClusterer();
			geoCluster.initGeometricClusterer(this.cc);
			geoCluster.run();
		}
		
		

		/* ==== POST-PROCESSING - MERGE AND REARRANGE ==== */// TODO
		// fixed type!!
		IPostProcessing pp = PostProcessingFactory.EnumPostProcessingClass.
			PP_REARRANGE_AND_MERGE_BEST.createPostProcessor();
		pp.initPostProcessing(this.cc);
		pp.run();

		/* release this thread from semaphore to signal finished status */
		if (semaphore != null) {
			semaphore.release();
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