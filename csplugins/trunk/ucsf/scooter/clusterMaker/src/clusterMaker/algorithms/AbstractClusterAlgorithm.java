/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package clusterMaker.algorithms;

import java.util.Arrays;
import java.lang.Math;
import javax.swing.JPanel;

// clusterMaker imports

public abstract class AbstractClusterAlgorithm implements ClusterAlgorithm {

	/************************************************************************
 	 * Abstract inteface -- override these methods!                         *
	 ***********************************************************************/

	public abstract String getShortName();
	public abstract String getName();
	public abstract JPanel getSettingsPanel();
	public abstract void revertSettings();
	public abstract void updateSettings();
	public abstract ClusterProperties getSettings();
	public abstract String cluster();

	public String toString() { return getName(); }

	
	public static double[][] distanceMatrix(Matrix matrix, DistanceMetric metric) {
		double[][] result = new double[matrix.nRows()][matrix.nColumns()];
		for (int row = 1; row < matrix.nRows(); row++) {
			for (int column = 0; column < row; column++) {
				result[row][column] = metric.getMetric(matrix, matrix, matrix.getWeights(), row, column);
			}
		}
		return result;
	}

	public static double mean(Double[] vector) {
	double result = 0.0;
		for (int i = 0; i < vector.length; i++) {
			result += vector[i].doubleValue();
		}
		return (result/(double)vector.length);
	}

	// Inefficient, but simple approach to finding the median
	public static double median(Double[] vector) {
		// Clone the input vector
		Double[] vectorCopy = new Double[vector.length];
		for (int i = 0; i < vector.length; i++) {
			vectorCopy[i] = new Double(vector[i].doubleValue());
		}
	
		// sort it
		Arrays.sort(vectorCopy);
	
		// Get the median
		int mid = vector.length/2;
		if (vector.length%2 == 1) {
			return (vectorCopy[mid].doubleValue());
		}
		return ((vectorCopy[mid-1].doubleValue()+vectorCopy[mid].doubleValue()) / 2);
	}
}
