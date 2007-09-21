package pinnaclez;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import oiler.Graph;
import oiler.util.IntIterator;
import modlab.Filter;

/**
 * P-value cutoff filter where the p-value is the
 * empirical p-value of the score of the augmented
 * vector in a null distribution of randomized classes.
 * An augmented vector is all the activity vectors
 * in the module combined together.
 *
 * <p>This filter does not expect any order
 * of nodes in the module.</p>
 *
 * <p>Because this filter augments all the vectors
 * in a module together, it needs to access the
 * activity vectors of a module. Thus, the node-object
 * parameter must be Activity, but the edge-object can be
 * arbitrary.</p>
 */
public class ST3Filter implements Filter<Activity,String>
{
	final Graph<Activity,String> network;
	final ExpressionMatrix matrix;
	final AbstractActivityScore score;
	final int numOfRandomTrials;
	final double pvalCutoff;
	final Map<Graph<Activity,String>,Double> pvals = new HashMap<Graph<Activity,String>,Double>();
	final Random random = new Random();

	public ST3Filter(final Graph<Activity,String> network, final ExpressionMatrix matrix, final AbstractActivityScore score)
	{
		this(network, matrix, score, 20000, 0.00005);
	}
	
	public ST3Filter(final Graph<Activity,String> network, final ExpressionMatrix matrix, final AbstractActivityScore score, final int numOfRandomTrials, final double pvalCutoff)
	{
		this.network = network;
		this.matrix = matrix;
		this.score = score;
		this.numOfRandomTrials = numOfRandomTrials;
		this.pvalCutoff = pvalCutoff;
	}

	public List<Graph<Activity,String>> filter(List<Graph<Activity,String>> modules)
	{
		List<Graph<Activity,String>> result = new ArrayList<Graph<Activity,String>>();
		for (Graph<Activity,String> module : modules)
		{
			final double[] activity = new double[matrix.experiments.length];
			for (int i = 0; i < activity.length; i++)
			{
				final IntIterator nodes = module.nodes();
				double sum = 0.0;
				int count = 0;
				while (nodes.hasNext())
				{
					final int node = nodes.next();
					final int matrixIndex = network.nodeObject(node).matrixIndex;
					if (matrixIndex < 0)
						continue;
					sum += matrix.matrix[matrixIndex][i];
					count++;
				}
				activity[i] = sum / Math.sqrt(count);
			}

			final int[] randomClasses = new int[matrix.classes.length];
			System.arraycopy(matrix.classes, 0, randomClasses, 0, matrix.classes.length);

			final double[] randomTrialScores = new double[numOfRandomTrials];
			for (int i = 0; i < numOfRandomTrials; i++)
			{
				randomizeArray(randomClasses);
				randomTrialScores[i] = score.scoreVector(activity, randomClasses);
			}
			Arrays.sort(randomTrialScores);

			final double originalScore = score.scoreVector(activity, matrix.classes);
			final double pval = Statistics.calculateEmpiricalPValue(originalScore, randomTrialScores);
			if (pval < pvalCutoff)
			{
				result.add(module);
				pvals.put(module, pval);
			}
		}

		return result;
	}

	private void randomizeArray(int[] array)
	{
		for (int i = 0; i < array.length; i++)
		{
			final int other = random.nextInt(array.length);
			final int t = array[other];
			array[other] = array[i];
			array[i] = t;
		}
	}

	public double pValue(Graph<Activity,String> module)
	{
		return pvals.get(module);
	}
}
