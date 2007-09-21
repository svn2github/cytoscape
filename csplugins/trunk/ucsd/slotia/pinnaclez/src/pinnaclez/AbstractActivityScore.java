package pinnaclez;

import oiler.Graph;
import oiler.util.IntIterator;
import modlab.AbstractScore;
import modlab.Score;

/**
 * A base class for scoring algorithms that score
 * an activity vector.
 *
 * <p>Some scoring algorithms score a vector
 * (in other words, an array of doubles) instead of a
 * module. This is the case for MIScore and TScore.
 * This class scores a module by</p>
 *
 * <p>Because this class augments all the vectors
 * in a module together, it needs to access the
 * activity vectors of a module. Thus, the node-object
 * parameter must be Activity, but the edge-object can be
 * arbitrary.</p>
 *
 * <p><ol>
 * <li>constructing an augmented activity vector by combining
 * the vectors associated with every node in the module,</li>
 * <li>and returning the result of scoreVector() of the
 * augmented vector.</li>
 * </ol></p>
 *
 * <p>This class expects an order in the nodes of
 * a module. If the last node in the module does not
 * have an activity vector, it returns -1.0.</p>
 */
public abstract class AbstractActivityScore extends AbstractScore<Activity,String> implements Score<Activity,String>
{
	protected final ExpressionMatrix matrix;

	public AbstractActivityScore(ExpressionMatrix matrix)
	{
		this.matrix = matrix;
	}

	public abstract double scoreVector(double[] vector, int[] classes);

	public double scoreModule(final Graph<Activity,String> module)
	{
		final double vector[] = new double[matrix.experiments.length];
		final IntIterator nodes = module.nodes();

		boolean lastEmpty = true;
		int count = 0;
		while (nodes.hasNext())
		{
			final int node = nodes.next();
			final int matrixIndex = module.nodeObject(node).matrixIndex;
			count++;

			if (matrixIndex < 0)
			{
				lastEmpty = true;
				continue;
			}
			
			final double[] geneActivity = matrix.matrix[matrixIndex];
			if (lastEmpty)
			{
				System.arraycopy(geneActivity, 0, vector, 0, vector.length);
				lastEmpty = false;
				continue;
			}

			final double sqrtCount   = Math.sqrt(count - 1);
			final double sqrtCountP1 = Math.sqrt(count);

			for (int i = 0; i < vector.length; i++)
				vector[i] = (geneActivity[i] + vector[i] * sqrtCount) / sqrtCountP1;
		}
		if (lastEmpty)
			return -1.0;
		return scoreVector(vector, matrix.classes);
	}
}
