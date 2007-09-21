package pinnaclez;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import modlab.Filter;
import oiler.Graph;

/**
 * P-Value cutoff filter, where the p-value for a module
 * is its empirical p-value in a null distribution of
 * randomized trials.
 *
 * <p>This filter handles any node or edge type for the
 * module.</p>
 *
 * <p>This filter does not expect any order of the nodes
 * in a module.</p>
 */
public class ST1Filter<N,E> implements Filter<N,E>
{
	final double pvalCutoff;
	final double[] sortedRandomTrialScores;
	final Map<Graph<N,E>,Double> pvals;

	public ST1Filter(final Trials trials)
	{
		this(trials, 0.05);
	}

	public ST1Filter(final Trials trials, final double pvalCutoff)
	{
		this.sortedRandomTrialScores = trials.getAllTrialScores();
		this.pvalCutoff = pvalCutoff;
		pvals = new HashMap<Graph<N,E>,Double>();
	}

	public List<Graph<N,E>> filter(List<Graph<N,E>> modules)
	{
		List<Graph<N,E>> result = new ArrayList<Graph<N,E>>();
		for (Graph<N,E> module : modules)
		{
			final double pval = Statistics.calculateEmpiricalPValue(module.score(), sortedRandomTrialScores);
			if (pval < pvalCutoff)
			{
				result.add(module);
				pvals.put(module, pval);
			}
		}
		return result;
	}

	/**
	 * Return the p-value calculated for a module that
	 * has been filtered.
	 */
	public double pValue(Graph<N,E> module)
	{
		return pvals.get(module);
	}
}
