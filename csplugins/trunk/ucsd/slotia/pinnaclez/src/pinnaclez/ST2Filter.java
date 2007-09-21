package pinnaclez;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import oiler.Graph;
import modlab.Filter;

/**
 * P-value cutoff filter, where the p-value for
 * a module is 1 - the cdf of the distribution
 * of random trials with the same starting node
 * as the module.
 *
 * <p>This filter handles any node or edge type for the
 * module.</p>
 *
 * <p>This filter does not expect any order
 * of the nodes in a module.</p>
 */
public class ST2Filter<N,E> implements Filter<N,E>
{
	public enum Distribution
	{
		GAMMA, NORMAL
	}

	final Trials trials;
	final Distribution distribution;
	final double pvalCutoff;
	final Map<Graph<N,E>,Double> pvals;

	public ST2Filter(final Trials trials, final Distribution distribution)
	{
		this(trials, distribution, 0.05);
	}

	public ST2Filter(final Trials trials, final Distribution distribution, final double pvalCutoff)
	{
		this.trials = trials;
		this.distribution = distribution;
		this.pvalCutoff = pvalCutoff;
		pvals = new HashMap<Graph<N,E>,Double>();
	}

	public List<Graph<N,E>> filter(List<Graph<N,E>> modules)
	{
		List<Graph<N,E>> result = new ArrayList<Graph<N,E>>();
		for (Graph<N,E> module : modules)
		{
			final int startNode = module.nodes().next();
			final double[] trialScores = trials.getTrialScores(startNode);
			
			double pval = 0.0;
			switch(distribution)
			{
				case GAMMA:
					pval = 1.0 - Statistics.gammaCDF(module.score(), trialScores);
					break;
				case NORMAL:
					pval = 1.0 - Statistics.normalCDF(module.score(), trialScores);
					break;
			}

			if (pval < pvalCutoff)
			{
				result.add(module);
				pvals.put(module, pval);
			}
		}
		return result;
	}

	public double pValue(Graph<N,E> module)
	{
		return pvals.get(module);
	}
}
