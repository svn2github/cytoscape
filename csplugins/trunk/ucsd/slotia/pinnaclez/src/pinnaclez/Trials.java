package pinnaclez;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import oiler.Graph;

/**
 * Stores all random trials together.
 */
public class Trials
{
	final Map<Integer,double[]> randomScores;

	public<N,E> Trials(final List<List<Graph<N,E>>> trials)
	{
		final int numOfGenes = trials.get(0).size();
		final int numOfTrials = trials.size();
		randomScores = new HashMap<Integer,double[]>(numOfGenes);

		int trialCount = 0;
		for (final List<Graph<N,E>> trial : trials)
		{
			for (final Graph<N,E> module : trial)
			{
				final int startNode = module.nodes().next();
				double[] scores = randomScores.get(startNode);
				if (scores == null)
				{
					scores = new double[numOfTrials];
					randomScores.put(startNode, scores);
				}
				scores[trialCount] = module.score();
			}
			trialCount++;
		}

		for (final double[] scores : randomScores.values())
			Arrays.sort(scores);
	}	

	/**
	 * Return a specific module's random scores.
	 */
	public double[] getTrialScores(final int startNode)
	{
		return randomScores.get(startNode);
	}

	/**
	 * Collect every random trial's module scores and sort them.
	 */
	public double[] getAllTrialScores()
	{
		final int numOfTrials = randomScores.size();
		final int numOfGenes = randomScores.values().iterator().next().length;
		final double[] array = new double[numOfGenes * numOfTrials];
		int i = 0;
		for (final double[] scores : randomScores.values())
		{
			System.arraycopy(scores, 0, array, i, scores.length);
			i += scores.length;
		}
		Arrays.sort(array);
		return array;
	}
}
