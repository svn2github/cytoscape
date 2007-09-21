package pinnaclez;

import java.util.HashMap;
import java.util.Map;
import oiler.Graph;
import oiler.LinkedListGraph;
import oiler.TypeConverter;

/**
 * A matrix representing gene expression data and
 * related information.
 *
 * <p>Each column in the matrix is
 * called an <i>experiment</i>. The
 * X-axis of the matrix represents
 * the experiments.</p>
 *
 * <p>Each row in the matrix is
 * called the <i>activity vector</i>
 * of a gene. The Y-axis of the
 * matrix represents the gene
 * activities.</p>
 *
 * <p>There are two parallel arrays for
 * the X-axis of the matrix:
 * experiments[] and classes[].
 * experiments[] represents the name of
 * the experiments, and classes[] represents
 * the associated class-labels.</p>
 *
 * <p>There is one parallel array for the
 * Y-axis of the matrix: genes[].
 * genes[] represents the name of the gene
 * associated with the activity vector.</p>
 */
public class ExpressionMatrix
{
	/**
	 * matrix[][] can be accessed as such:
	 * given an experiment index called x
	 * and an activity vector index called y,
	 * the value is obtained by matrix[y][x].
	 */
	public double[][] matrix;
	public String[] genes;
	public String[] experiments;
	public int[] classes;

	/**
	 * Allocates space for the arrays; does not fill anything in.
	 */
	public ExpressionMatrix(int numOfGenes, int numOfExperiments)
	{
		if (numOfGenes <= 0)
			throw new IllegalArgumentException("numOfGenes <= 0");
		if (numOfExperiments <= 0)
			throw new IllegalArgumentException("numOfExperiments <= 0");

		matrix		= new double[numOfGenes][numOfExperiments];
		genes		= new String[numOfGenes];
		experiments	= new String[numOfExperiments];
		classes		= new int[numOfExperiments];
	}

	/**
	 * Copies <code>original</code> matrix, genes, experiments, and classes.
	 */
	public ExpressionMatrix(ExpressionMatrix original)
	{
		this(original.genes.length, original.experiments.length);
		System.arraycopy(original.genes, 0, genes, 0, genes.length);
		System.arraycopy(original.experiments, 0, experiments, 0, experiments.length);
		System.arraycopy(original.classes, 0, classes, 0, classes.length);
		for (int i = 0; i < matrix.length; i++)
			System.arraycopy(original.matrix[i], 0, matrix[i], 0, matrix[i].length);
	}
}
