package org.jmathplot.util;

/**
 * <p>Copyright : BSD License</p>
 * @author Yann RICHET
 */

public class DoubleArray {

	public static void checkColumnDimension(double[][] M, int n) {
		for (int i = 0; i < M.length; i++) {
			if (M[i].length != n) {
				throw new IllegalArgumentException("row " + i + " has " + M[i].length +
					" columns instead of " + n + " columns expected.");
			}
		}
	}

	public static double[][] random(int m, int n) {
		double[][] array = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				array[i][j] = Math.random();
			}
		}
		return array;
	}

	public static double[] min(double[][] M) {
		double[] min = new double[M[0].length];
		for (int j = 0; j < min.length; j++) {
			min[j] = M[0][j];
			for (int i = 1; i < M.length; i++) {
				min[j] = Math.min(min[j], M[i][j]);
			}
		}
		return min;
	}

	public static double[] max(double[][] M) {
		double[] max = new double[M[0].length];
		for (int j = 0; j < max.length; j++) {
			max[j] = M[0][j];
			for (int i = 1; i < M.length; i++) {
				max[j] = Math.max(max[j], M[i][j]);
			}
		}
		return max;
	}

	public static double[][] getColumns(double[][] M, int j1, int j2) {
		double[][] array = new double[M.length][j2 - j1 + 1];
		for (int i = 0; i < M.length; i++) {
			for (int j = j1; j <= j2; j++) {
				array[i][j - j1] = M[i][j];
			}
		}
		return array;
	}

	public static double[] getColumns(double[] M, int j1, int j2) {
		double[] array = new double[j2 - j1 + 1];
			for (int j = j1; j <= j2; j++) {
				array[j - j1] = M[j];
			}
		return array;
	}

}