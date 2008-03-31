package de.layclust.taskmanaging;

import java.util.concurrent.Semaphore;

import de.layclust.layout.ILayouter;
import de.layclust.layout.LayoutFactory;
import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.geometric_clustering.GeometricClusteringFactory;
import de.layclust.layout.geometric_clustering.IGeometricClusterer;
import de.layclust.layout.geometric_clustering.SingleLinkageClusterer;
import de.layclust.layout.parameter_training.IParameters;
import de.layclust.layout.postprocessing.IPostProcessing;
import de.layclust.layout.postprocessing.PP_DivideAndReclusterRecursively;
import de.layclust.layout.postprocessing.PP_DivideIntoSubgraphsUsingClusterResults;
import de.layclust.layout.postprocessing.PostProcessingFactory;


public class ClusterTrainingTask implements Runnable {

	private ConnectedComponent cc = null;
	private Semaphore semaphore = null;
	private IParameters parameters = null;
	private int layoutType = -1;


	public ClusterTrainingTask(ConnectedComponent cc, IParameters parameters, 
			int layoutType) {

		this.cc = cc;
		this.parameters = parameters;
		this.layoutType = layoutType;
	}

	public void run() {
		
		try {
			
			/* ====LAYOUTING PHASE ==== */
			ILayouter layouter = LayoutFactory
					.getLayouterByType(this.layoutType);
			layouter.initLayouter(this.cc, this.parameters);
//			layouter.initLayouter(this.cc, layouter, parameters);
			layouter.run();

			
			/* ==== CLUSTERING PHASE ==== */
			IGeometricClusterer slcluster = GeometricClusteringFactory.
			getGeometricClusterByType(TaskConfig.geometricClusteringType, 
					this.cc);
			slcluster.run();
			
			
			/* ====POST-PROCESSING PHASE ==== */
			if(TaskConfig.doLayoutParameterTraining){
				int ppType = TaskConfig.postProcessingType;
				IPostProcessing pp = PostProcessingFactory.getPostProcessorByType(
						ppType);
				
	//			IPostProcessing pp = PostProcessingFactory.getPostProcessorByType(PostProcessingFactory.PP_REARRANGE_AND_MERGE_BEST);
				pp.initPostProcessing(this.cc);
				/* training can only be done for one layouter at a time! */
				int[] layouterTypes = {this.layoutType};
				IParameters[] params = {this.parameters};
				if(ppType == PostProcessingFactory.PP_DIVIDE_INTO_SUBGRAPHS_USING_RESULTS){
					((PP_DivideIntoSubgraphsUsingClusterResults) pp).setLayoutingInfo(params, layouterTypes);
				} else if(ppType == PostProcessingFactory.PP_DIVIDE_AND_RECLUSTER_RECURSIVELY){
					((PP_DivideAndReclusterRecursively) pp).setLayoutingInfo(params, layouterTypes);
				}
				pp.run();
			}
			
			/* ==== STUFF AT END ==== */
			/* set the score for the parameters object for the training */
			parameters.setScore(this.cc.getClusteringScore());
			
			/* release this thread from semaphore to signal finished status */
			if(this.semaphore != null){
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