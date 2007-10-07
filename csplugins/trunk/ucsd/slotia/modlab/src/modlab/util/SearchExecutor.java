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

	/**
	 * An interface for classes that duplicate
	 * graphs for each thread in SearchExecutor.
	 */
	public interface Duplicate<N,E>
	{
		public Graph<N,E> duplicate(Graph<N,E> g);
	}

	/**
	 * A class that monitors
	 * the progress of a search and for
	 * prematurely halting a search.
	 */
	public static abstract class ProgressMonitor
	{
		boolean needsToHalt = false;

		/**
		 * When using SearchExecutor, one typically writes
		 * an anonymous class that extends ProgressMonitor
		 * and implements the setPercentCompleted() method.
		 * This method will be called by SearchExecutor whenever
		 * the search has progressed.
		 */
		public void setPercentCompleted(double percent)
		{
		}

		/**
		 * Call this method if the search needs to be prematurely halted.
		 * search() will return null if the search was halted.
		 */
		public final void halt()
		{
			needsToHalt = true;
		}

		/**
		 * Returns true if the search will halt.
		 */
		public final boolean needsToHalt()
		{
			return needsToHalt;
		}
	}
	
	private enum Trial { REAL, RANDOM }

	/**
	 * Executes a search in multiple threads.
	 *
	 * <p>Real and random trials are executed concurrently
	 * for better performance.</p>
	 *
	 * @param network The network to search.
	 * @param search The searching algorithm to use.
	 *  If <code>duplicate</code> is not null, SearchExecutor
	 *  will create a unique network instance, and it will
	 *  pass the unique instance to <code>search</code>.
	 *  <i>WARNING!</i> If the network passed in to
	 *  <code>search</code> is retained, it will most likely
	 *  be modified by a randomization later on.
	 *  Thus, the network instance will become undefined
	 *  in the future. If <code>search</code> retains
	 *  the network passed in, <b>MAKE SURE</b>
	 *  retained instances of the network are handled correctly!
	 * @param score The scoring algorithm to use.
	 * @param randomize The randomizing algorithm to use
	 *  before starting the search algorithm for a random trial.
	 *  The network passed in may be the original network
	 *  or it may have been previously randomized.
	 *  If <code>duplicate</code> is not null, SearchExecutor
	 *  will create a unique network instance, and it will
	 *  pass the unique instance to <code>randomize</code>.
	 * @param duplicate A class that can duplicate
	 *  a network. SearchExecutor can create a unique
	 *  instance of the network for each thread.
	 *  If there are unique instances of the network for
	 *  each thread, a thread can randomize the network
	 *  and search the network without affecting
	 *  randomizations and searches of other threads.
	 *  If this functionality is not needed, pass in null.
	 * @param monitor Monitors the progress
	 *  of a search. Pass in null if this functionality
	 *  is not needed.
	 * @param numOfRandomTrials The number of random
	 *  trials to execute.
	 * @param numOfThreads The number of threads
	 *  to execute the search concurrently.
	 * @return The trials produced by the search
	 *  algorithm, or null if the search
	 *  was prematurely terminated. The first trial (at index 0)
	 *  in the list is always the real trial.
	 * @throws IllegalArgumentException if <code>search</code>
	 *  or <code>randomize</code> is null, or if
	 *  <code>numOfThreads</code> is less than 1.
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
		if (search == null)
			throw new IllegalArgumentException("search == null");
		if (randomize == null)
			throw new IllegalArgumentException("randomize == null");
		if (numOfThreads < 1)
			throw new IllegalArgumentException("numOfThreads < 1");

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
			Graph<N,E> newNetwork = network;
			if (duplicate != null)
				newNetwork = duplicate.duplicate(network);
			threads[i] = new SearchThread<N,E>(newNetwork, search, score, randomize, trials, workQueue, monitor);
			threads[i].start();
		}

		if (monitor != null) monitor.setPercentCompleted(0.0);

		// Wait for the threads to finish
		for (int i = 0; i < numOfThreads; i++)
			try { threads[i].join(); } catch (InterruptedException e) {}

		// Return null if the search was prematurely halted
		if (monitor != null && monitor.needsToHalt()) return null;

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
				// Make sure we aren't told to halt
				if (monitor != null)
					if (monitor.needsToHalt())
						break;

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
