package clusterMaker.algorithms.AP;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class ResponsibilityMatrix extends APMatrix {
	private DoubleMatrix1D evidenceVector = null;
	private DoubleMatrix1D prefVector;

	public ResponsibilityMatrix (DoubleMatrix2D s_matrix, DoubleMatrix1D prefVector, double lambda) {
		super(s_matrix, lambda);
		this.prefVector = prefVector;
	}

	public double getEvidence (int col) {
		if (evidenceVector == null) {
			updateEvidence();
		}
		return evidenceVector.get(col);
	}

	public void updateEvidence () { 
		evidenceVector = DoubleFactory1D.dense.make(s_matrix.columns());
		s_matrix.forEachNonZero(new CalculateEvidence(evidenceVector));
	}

	public void update(AvailabilityMatrix a_matrix) {
		s_matrix.forEachNonZero(new UpdateResponsibility(a_matrix, prefVector));
	}
	
	class UpdateResponsibility implements IntIntDoubleFunction {
		AvailabilityMatrix a_matrix;
		DoubleMatrix1D prefVector;

		public UpdateResponsibility(AvailabilityMatrix a_matrix, DoubleMatrix1D pref) {
			this.a_matrix = a_matrix;
			this.prefVector = pref;
		}

		public double apply(int row, int col, double value) {
			double newValue;
			if (row != col)
				newValue = value - a_matrix.getEvidence(row);
			else
				newValue = prefVector.get(row) - a_matrix.getEvidence(row);

			// Damp
			setDamped(row, col, newValue);

			return value;
		}
	}

	class CalculateEvidence implements IntIntDoubleFunction {
		DoubleMatrix1D maxVector;

		public CalculateEvidence(DoubleMatrix1D maxMat) {
			maxVector = maxMat;
		}

		public double apply(int row, int col, double value) {
			if (row != col) {
				maxVector.set(col, maxVector.get(col) + Math.max(0.0, get(row, col)));
			}
			return value;
		}
	}

}
