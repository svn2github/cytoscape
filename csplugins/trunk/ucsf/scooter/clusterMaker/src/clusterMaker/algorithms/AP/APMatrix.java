package clusterMaker.algorithms.AP;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public abstract class APMatrix {
	protected double lambda; /*lambda value from 0 to 1 dampens messages passed to avoid numberical oscillation*/
	protected DoubleMatrix2D matrix;
	protected DoubleMatrix2D s_matrix;

	public APMatrix (DoubleMatrix2D s_matrix, double lambda) {
		this.matrix = DoubleFactory2D.sparse.make(s_matrix.rows(), s_matrix.columns());
		this.s_matrix = s_matrix;
		this.lambda = lambda;
	}

	public abstract double getEvidence (int row);

	public double get(int row, int column) { return matrix.get(row, column); }

	public void setDamped(int row, int column, double value) {
		double previousValue = matrix.get(row, column);
		matrix.set(row, column, previousValue*lambda+value*(1-lambda));
	}

}
