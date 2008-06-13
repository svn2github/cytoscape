package SawdPinnacleZ;

import pinnaclez.*;
import java.util.*;
import oiler.util.*;

public class Filter
{
	public static void filterST1(SawdClient client, Settings settings)
	{
		// collect all the scores
		System.err.print("Collecting scores..."); System.err.flush();
		IntIntHashMap startNodes = Iteration.getStartNodes(client);
		double[] scores = new double[settings.numOfTrials * startNodes.size()];
		int scoresIndex = 0;
		IntIterator iterator = startNodes.valuesIterator();
		while (iterator.hasNext())
		{
			int index = iterator.next();
			for (int i = 0; i < settings.numOfTrials; i++)
			{
				String scoreString = client.get_graph_attribute(index, "pinnaclez.score.random." + i);
				scores[scoresIndex++] = Double.parseDouble(scoreString);
			}
		}
		Arrays.sort(scores);
		System.err.println(" done.");

		System.err.println("Filtering modules with statistical test 1...");
		ST1Filter filter = new ST1Filter(scores, settings.ST1PValCutoff);
		iterator = startNodes.valuesIterator();
		ProgressBar progressBar = new ProgressBar(iterator.numRemaining());
		if (settings.verbose)
			progressBar.start();
		while (iterator.hasNext())
		{
			int index = iterator.next();
			String scoreString = client.get_graph_attribute(index, "pinnaclez.score.real");
			double score = Double.parseDouble(scoreString);
			double pval = filter.calculatePValue(score);
			if (pval < settings.ST1PValCutoff)
				client.set_graph_attribute(index, "pinnaclez.pvalue.st1", Double.toString(pval));
			else
				client.delete_graph(index);
			if (settings.verbose)
				progressBar.increment();
		}
	}

	public static void filterST2(SawdClient client, Settings settings)
	{
		ST2Filter filter = null;
		if (settings.scoreModel == Settings.ScoreModel.MI)
			filter = new ST2Filter(null, ST2Filter.Distribution.GAMMA, settings.ST2PValCutoff);
		else
			filter = new ST2Filter(null, ST2Filter.Distribution.NORMAL, settings.ST2PValCutoff);

		System.err.println("Filtering modules with statistical test 2...");
		double[] scores = new double[settings.numOfTrials];
		IntIntHashMap startNodes = Iteration.getStartNodes(client);
		IntIterator iterator = startNodes.valuesIterator();
		ProgressBar progressBar = new ProgressBar(iterator.numRemaining());
		if (settings.verbose)
			progressBar.start();
		while (iterator.hasNext())
		{
			// collect the scores of the trial
			int index = iterator.next();
			int scoresIndex = 0;
			for (int i = 0; i < settings.numOfTrials; i++)
			{
				String scoreString = client.get_graph_attribute(index, "pinnaclez.score.random." + i);
				scores[scoresIndex++] = Double.parseDouble(scoreString);
			}

			String scoreString = client.get_graph_attribute(index, "pinnaclez.score.real");
			double score = Double.parseDouble(scoreString);
			double pval = filter.calculatePValue(score, scores);
			if (pval < settings.ST2PValCutoff)
				client.set_graph_attribute(index, "pinnaclez.pvalue.st2", Double.toString(pval));
			else
				client.delete_graph(index);
			if (settings.verbose)
				progressBar.increment();
		}
	}
}
