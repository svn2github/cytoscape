/* 
* Created on 18. November 2007
 * 
 */
package de.layclust.layout.parameter_training;

import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.Semaphore;

// import org.apache.log4j.Logger;

import de.layclust.layout.ILayoutInitialiser;
import de.layclust.layout.LayoutFactory;
import de.layclust.layout.data.ConnectedComponent;
import de.layclust.taskmanaging.ClusterTrainingTask;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;

/**
 * @author sita
 * 
 */
public class ParameterTraining_SE implements IParameterTraining {
	
	// private static org.apache.log4j.Logger log = Logger.getLogger(ParameterTraining_SE.class);

	/* type of layouter that is being used */
	private int layouterType = -1;
	/* size of each generationg */
	private int generationSize = -1;
	/* number of generations to carry out */
	private int noOfGenerations = -1;
	
	private IParametersComparator paramComparator= null;

	/* previous best parameter configuration
	 * this means only one instance should be created for the 
	 * parameter training of one dataset.
	 */
	private IParameters bestPreviousIParameters = null;
	
	private Vector<IParameters> bestConfigs = null;


//	private IParameters[] generationParameterSet = null;

	public void initialise(int layouterType, int generationSize, int noOfGenerations) {
		this.layouterType = layouterType;
		this.generationSize = generationSize;
		this.noOfGenerations = noOfGenerations;
		this.paramComparator = new IParametersComparator();
		this.bestConfigs = new Vector<IParameters>();
	}

	public IParameters run(ConnectedComponent cc) {
		try {

			boolean terminateTraining = false;
			
			/* initialise positions of the cc - the same
			 * initial positions are used for all training
			 * rounds
			 */
			ILayoutInitialiser li = LayoutFactory.
				getLayouterInitialiserByType(this.layouterType);
			li.initLayoutInitialiser(cc);
			li.run();

			/* run initial generation */
			IParameters[] initialGeneration = createInitialParameterGeneration();
			runOneGeneration(initialGeneration, cc, 0);
			terminateTraining = terminateTraining(initialGeneration);
			/* add the best 10 random configs to the bestConfigs collection Vector */
			for (int i = 0; i < 10; i++) {
				this.bestConfigs.add(initialGeneration[i]);
			}
//			System.out.println("terminate training? "+terminateTraining);
			
			/* run all following generations */
			IParameters[] generation;
			for(int i=1;i<=this.noOfGenerations;i++){
				if(terminateTraining){
					break;
				}
				
				generation = createParameterGeneration();
				runOneGeneration(generation, cc, i);
				terminateTraining = terminateTraining(generation);
				for (int j = 0; j < this.generationSize/2; j++) {
					this.bestConfigs.add(initialGeneration[j]);
				}
//				System.out.println("terminate training? "+terminateTraining);
			}
			
			/* convert best configurations vector to array */
			IParameters[] bestConfigsArray = new IParameters[bestConfigs.size()];
			for (int i = 0; i < bestConfigs.size(); i++) {
				bestConfigsArray[i] = bestConfigs.get(i);
			}
			/* sort the IParameters array according to their score and return the
			 * best parameter set.
			 */
			Arrays.sort(bestConfigsArray, this.paramComparator);
			this.bestPreviousIParameters = bestConfigsArray[0];

			return bestConfigsArray[0];

		} catch (InvalidTypeException ex) {
			// log.fatal(ex.getMessage());
			System.err.println(ex.getMessage());
			ex.printStackTrace();	
			System.exit(-1);
		}
		return null;

	}
	
	private IParameters[] createInitialParameterGeneration() throws InvalidTypeException{
		// create enough random configurations to start with
		int initialSize = this.generationSize+10;
		IParameters[] paramsGen = new IParameters[initialSize];
		IParameters param;
		/* get parameters from config */
		param = LayoutFactory.getParametersByType(this.layouterType);
		param.readParametersFromConfig();
		paramsGen[0] = param;
		/* get the best parameter configuration for the previous training
		 * round if it exists. */
		if(this.bestPreviousIParameters!=null){
			paramsGen[1] = bestPreviousIParameters;
		} else {
			param = LayoutFactory.getParametersByType(this.layouterType);
			param.createRandomConfiguration();
			paramsGen[1] = param;
		}
		for(int i=2;i<initialSize;i++){
			param = LayoutFactory.getParametersByType(this.
					layouterType);
			param.createRandomConfiguration();
			paramsGen[i] = param;
		}				
		return paramsGen;
	}
	
	
	private IParameters[] createParameterGeneration() throws InvalidTypeException{
		IParameters[] paramsGen = new IParameters[this.generationSize];
		IParameters param;

		/* convert best configurations vector to array */
		IParameters[] bestConfigsArray = new IParameters[bestConfigs.size()];
		for (int i = 0; i < bestConfigs.size(); i++) {
			bestConfigsArray[i] = bestConfigs.get(i);
		}

		/* add mean of best configs */
		param = LayoutFactory.getParametersByType(this.layouterType);
		param.combineConfigurationsRandomly(bestConfigsArray);
		paramsGen[0] = param;
		
		int currentPosition = 1;
		int third = (this.generationSize-1)/3;
		
		/* add combinations of the best configurations for first third*/
		for(int i=currentPosition;i<currentPosition+third;i++){
			param = LayoutFactory.getParametersByType(this.layouterType);
			param.combineConfigurationsRandomly(bestConfigsArray);
			paramsGen[i] = param;
		}
		currentPosition += third;
		
		/* add combinations of best half plus new random parameters */
		for(int i=currentPosition;i<currentPosition+third;i++){
			param = LayoutFactory.getParametersByType(this.layouterType);
			param.combineParametersRandomlyAndGetNewRandom(bestConfigsArray);
			paramsGen[i] = param;
		}
		currentPosition += third;
		
		/* fill the rest with random configuratons */
		for(int i=currentPosition;i<this.generationSize;i++){
			param = LayoutFactory.getParametersByType(this.layouterType);
			param.createRandomConfiguration();
			paramsGen[i] = param;
		}
		return paramsGen;
	}
	
	
	/**
	 * Sorts the parameters according to their score and determines if the training 
	 * should be terminated. If almost all parameter configurations have the same (best) score, 
	 * except for 2, then the parameter training can be terminated.
	 * This means the best possible configuration has been found, or that
	 * it doesn't matter much which parameter values are used, the connected
	 * component is always clustered correctly (min score).
	 * Important is that this method is called after the training has been
	 * carried out for this generation - otherwise all scores are 0.
	 * @param params All parameter configurations of one generation.
	 * @return True if the training should be terminated, otherwise false to carry on.
	 */
	private boolean terminateTraining(IParameters[] params){
		Arrays.sort(params, this.paramComparator);
		double bestScore = params[0].getScore();
		if(bestScore==0){
			return false;
		}
		for(int i=1;i<params.length-2;i++){
			if(params[i].getScore() > bestScore){
				return false;
			}
		}
		return true;
	}

	private void runOneGeneration(IParameters[] generationParameterSet,
			ConnectedComponent cc, int gen) {
		int size = generationParameterSet.length;
		
		try {
			/* collect all semaphores in an array */
			Semaphore[] allSemaphore = new Semaphore[size];
			/* one semaphore per thread to track running status */
			Semaphore semaphore;
			ClusterTrainingTask clusteringTask;

			for (int i = 0; i < size; i++) {

				/* copy the cc (ConnectedComponent) - shares same resources! */
				ConnectedComponent newCC = cc.copy();
								
				/* sets a new positions array for the copy */
				newCC.setCCPositions(cc.copyCCPositions());
				
				/* start clustering */				
				clusteringTask = new ClusterTrainingTask(newCC, 
						generationParameterSet[i], layouterType);
				/* use threads */
				if(TaskConfig.useThreadsForParameterTraining){					
					// log.info("Using threads for layout parameter training");
					System.out.println("Using threads for layout parameter training");
					/* create a semaphore for tracking the thread */
					semaphore = new Semaphore(1, true);
					allSemaphore[i] = semaphore;
					clusteringTask.setSemaphore(semaphore);
					(new Thread(clusteringTask)).start();
				/* don't use threads */
				} else {
					clusteringTask.run();
				}
			}
			
			if(TaskConfig.useThreadsForParameterTraining){
				
				/*
				 * For each semaphore it is tested if a permit can be aquired. This
				 * forces the main program thread to wait until all threads are finished.
				 */
				for (Semaphore sem : allSemaphore) {
					sem.acquire();
				}
			}

		} catch (InterruptedException e) {
			/* Exception created by Semaphore.aquire() - if the thread is 
			 * interrupted */
			// log.fatal(e.getMessage());
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
