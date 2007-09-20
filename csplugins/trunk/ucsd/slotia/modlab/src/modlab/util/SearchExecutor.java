package modlab.util;

import oiler.Graph;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import modlab.Randomize;
import modlab.Score;
import modlab.Search;

/**
 * Executes a search in multiple threads so that
 * real and random trials are executed concurrently.
 */
public class SearchExecutor
{
	protected SearchExecutor() {}

	public interface Duplicate<N,E>
	{
		public Graph<N,E> duplicate(Graph<N,E> g);
	}

	public interface ProgressMonitor
	{
		public void setPercentCompleted(double percent);
	}

	protected enum Trial { REAL, RANDOM }

	/**
	 * Executes a search in multiple threads.
	 *
	 * <p>Real and random trials are executed concurrently
	 * for better performance. The number of threads
	 * SearchExecutor will use is the number of CPUs
	 * available to Java.</p>
	 *
	 * <p>The network passed in MUST
	 * implement the <code>clone()</code> method!</p>
	 *
	 * @param network The network to search. The network
	 *        passed in MUST implement the
	 *        <code>clone()</code> method!
	 * @param search The searching algorithm to use.
	 *        SearchExecutor will ensure the
	 *        network passed to the search
	 *        algorithm will be unique to the
	 *        thread calling the search algorithm.
	 * @param score The scoring algorithm to use.
	 * @param randomize The randomizing algorithm to use
	 *        before starting the search algorithm for
	 *        a random trial.
	 *        SearchExecutor will ensure the
	 *        network passed to the randomizing
	 *        algorithm will be unique to the
	 *        thread calling the randomizing algorithm.
	 * @param numOfRandomTrials The number of random
	 *        trials to execute.
	 * @return The trials produced by the search
	 *         algorithm. The first trial (at index 0)
	 *         in the list is always the real trial.
	 */
	public static <N,E> List<List<Graph<N,E>>> execute(
							final Graph<N,E> network,
							final Search<N,E> search,
							final Score<N,E> score,
							final Randomize<N,E> randomize,
							final Duplicate<N,E> duplicate,
							final int numOfRandomTrials)
	{
		return execute(network, search, score, randomize, duplicate, null, numOfRandomTrials);
	}

	public static <N,E> List<List<Graph<N,E>>> execute(
							final Graph<N,E> network,
							final Search<N,E> search,
							final Score<N,E> score,
							final Randomize<N,E> randomize,
							final Duplicate<N,E> duplicate,
							final ProgressMonitor monitor,
							final int numOfRandomTrials)
	{
		final int numOfThreads = Runtime.getRuntime().availableProcessors();
		return execute(network, search, score, randomize, duplicate, monitor, numOfRandomTrials, numOfThreads);
	}
	
	/**
	 * Executes a search in multiple threads.
	 *
	 * <p>Real and random trials are executed concurrently
	 * for better performance.</p>
	 *
	 * <p>The network passed in MUST
	 * implement the <code>clone()</code> method!</p>
	 *
	 * @param network The network to search. The network
	 *        passed in MUST implement the
	 *        <code>clone()</code> method!
	 * @param search The searching algorithm to use.
	 *        SearchExecutor will ensure the
	 *        network passed to the search
	 *        algorithm will be unique to the
	 *        thread calling the search algorithm.
	 * @param score The scoring algorithm to use.
	 * @param randomize The randomizing algorithm to use
	 *        before starting the search algorithm for
	 *        a random trial.
	 *        SearchExecutor will ensure the
	 *        network passed to the randomizing
	 *        algorithm will be unique to the
	 *        thread calling the randomizing algorithm.
	 * @param numOfRandomTrials The number of random
	 *        trials to execute.
	 * @param numOfThreads The number of threads
	 *        to execute the search concurrently.
	 * @return The trials produced by the search
	 *         algorithm. The first trial (at index 0)
	 *         in the list is always the real trial.
	 */
	public static <N,E> List<List<Graph<N,E>>> execute(
							final Graph<N,E> network,
							final Search<N,E> search,
							final Score<N,E> score,
							final Randomize<N,E> randomize,
							final Duplicate<N,E> duplicate,
							final ProgressMonitor monitor,
							final int numOfRandomTrials,
							final int numOfThreads)
	{
		// Setup trials
		final List<List<Graph<N,E>>> trials = new ArrayList<List<Graph<N,E>>>(numOfRandomTrials + 1);

		// Setup the work queue
		final BlockingQueue<Trial> workQueue = new ArrayBlockingQueue<Trial>(numOfRandomTrials + 1);
		workQueue.offer(Trial.REAL);
		for (int i = 0; i < numOfRandomTrials; i++)
			workQueue.offer(Trial.RANDOM);

		// Create and start the threads
		Thread[] threads = new Thread[numOfThreads];
		for (int i = 0; i < numOfThreads; i++)
		{
			threads[i] = new SearchThread<N,E>(duplicate.duplicate(network), search, score, randomize, trials, workQueue, monitor);
			threads[i].start();
		}

		if (monitor != null) monitor.setPercentCompleted(0.0);

		// Wait for the threads to finish
		for (int i = 0; i < numOfThreads; i++)
			try { threads[i].join(); } catch (InterruptedException e) {}
		
		if (monitor != null) monitor.setPercentCompleted(1.0);

		return trials;
	}

	private static class SearchThread<N,E> extends Thread
	{
		Graph<N,E> network;
		Search<N,E> search;
		Score<N,E> score;
		Randomize<N,E> randomize;
		BlockingQueue<Trial> workQueue;
		List<List<Graph<N,E>>> trials;
		ProgressMonitor monitor;
		
		public SearchThread(	final Graph<N,E> network,
					final Search<N,E> search,
					final Score<N,E> score,
					final Randomize<N,E> randomize,
					final List<List<Graph<N,E>>> trials,
					final BlockingQueue<Trial> workQueue,
					final ProgressMonitor monitor)
		{
			this.network = network;
			this.search = search;
			this.score = score;
			this.randomize = randomize;
			this.trials = trials;
			this.workQueue = workQueue;
			this.monitor = monitor;
		}
		
		public void run()
		{
			while (true)
			{
				// Get some work
				Trial work = workQueue.poll();
				if (work == null)
					break;
				
				// Determine if we need to randomize the network
				if (work == Trial.RANDOM)
					randomize.randomize(network);

				// Perform the search
				List<Graph<N,E>> trial = search.search(network, score);

				// Add the trial. If it is a real trial, add it
				// to the beginning of the list; if it is a
				// random trial, add it to the end of the list.
				// This ensures the real trial is always
				// the first element of the list.
				if (work == Trial.RANDOM)
					synchronized(trials) { trials.add(trial); }
				else if (work == Trial.REAL)
					synchronized(trials) { trials.add(0, trial); }
				
				if (monitor != null)
				{
					synchronized(monitor)
					{
						monitor.setPercentCompleted((double) workQueue.remainingCapacity() / (workQueue.size() + workQueue.remainingCapacity()));
					}
				}
			}
		}
	}
}
