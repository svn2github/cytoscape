package clusterMaker.algorithms.AP;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class AvailabilityMatrix extends APMatrix {
	private DoubleMatrix1D evidenceVector = null;

	public AvailabilityMatrix (DoubleMatrix2D s_matrix, double lambda) {
		super(s_matrix, lambda);
	}

	public double getEvidence (int row) {
		if (evidenceVector == null) {
			updateEvidence();
		}
		return evidenceVector.get(row);
	}

	public void updateEvidence () { 
		evidenceVector = DoubleFactory1D.dense.make(s_matrix.rows());
		s_matrix.forEachNonZero(new CalculateEvidence(evidenceVector));
	}

	public void update(ResponsibilityMatrix r_matrix) {
		s_matrix.forEachNonZero(new UpdateAvailability(r_matrix));
	}

	//
	// These inner classes are used for the "forEachNonZero" calls...
	//
	class CalculateEvidence implements IntIntDoubleFunction {
		DoubleMatrix1D maxVector;

		public CalculateEvidence(DoubleMatrix1D maxMat) {
			this.maxVector = maxMat;
		}

		public double apply(int row, int col, double value) {
			if (row != col)
				maxVector.set(row, maxVector.get(row)+get(row,col)+value);

			return value;
		}
	}

	class UpdateAvailability implements IntIntDoubleFunction {
		ResponsibilityMatrix rMatrix;

		public UpdateAvailability(ResponsibilityMatrix responsibilityMatrix) {
			rMatrix = responsibilityMatrix;
		}

		public double apply(int row, int col, double value) {
			double newValue;
			if (row != col)
				newValue = Math.min(0.0, rMatrix.get(row, col) + rMatrix.getEvidence(col));
			else
				newValue = rMatrix.getEvidence(col);

			setDamped(row, col, newValue);

			return value;
		}
	}
}
