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
package clusterMaker.algorithms.attributeClusterers;

import java.lang.Math;

//FIXME EUCLIDEAN is actually EUCLIDEAN_SQUARED!!!

public enum DistanceMetric {
	VALUE_IS_CORRELATION("None -- attributes are correlations"),
	UNCENTERED_CORRELATION("Uncentered correlation"),
	CORRELATION("Pearson correlation"),
	ABS_UNCENTERED_CORRELATION("Uncentered correlation, absolute value"),
	ABS_CORRELATION("Pearson correlation, absolute value"),
	SPEARMANS_RANK("Spearman's rank correlation"),
	KENDALLS_TAU("Kendall's tau"),
	EUCLIDEAN("Euclidean distance"),
	CITYBLOCK("City-block distance");

	private String name;

	DistanceMetric(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	public double getMetric(BaseMatrix data1, BaseMatrix data2, double[] weights,
	                        int index1, int index2) {
		switch (this) {
			case EUCLIDEAN:
				return euclidMetric(data1, data2, weights, index1, index2);
			case CITYBLOCK:
				return cityblockMetric(data1, data2, weights, index1, index2);
			case CORRELATION:
				return correlationMetric(data1, data2, weights, index1, index2);
			case ABS_CORRELATION:
				return acorrelationMetric(data1, data2, weights, index1, index2);
			case UNCENTERED_CORRELATION:
				return ucorrelationMetric(data1, data2, weights, index1, index2);
			case ABS_UNCENTERED_CORRELATION:
				return uacorrelationMetric(data1, data2, weights, index1, index2);
			case SPEARMANS_RANK:
				return spearmanMetric(data1, data2, weights, index1, index2);
			case KENDALLS_TAU:
				return kendallMetric(data1, data2, weights, index1, index2);
			case VALUE_IS_CORRELATION:
				return (1-data1.doubleValue(index1, index2));
		}
		return euclidMetric(data1, data2, weights, index1, index2);
	}

	// Distance metric calculations
	private static double euclidMetric(BaseMatrix data1, BaseMatrix data2, double[] weights, 
	                            int index1, int index2) {
		double result = 0.0;
		double tweight = 0.0;
		for (int i = 0; i < data1.nColumns(); i++) {
			if (data1.hasValue(index1, i) && data2.hasValue(index2, i)) {
				double term = data1.doubleValue(index1, i) - data2.doubleValue(index2, i);
				result += weights[i]*term*term;
				tweight += weights[i];
			}
		}
		if (tweight == 0.0) return 0;
		return (result/tweight);
	}

	private static double cityblockMetric(BaseMatrix data1, BaseMatrix data2, double[] weights, 
	                                      int index1, int index2) {
		double result = 0.0;
		double tweight = 0.0;
		for (int i = 0; i < data1.nColumns(); i++) {
			if (data1.hasValue(index1, i) && data2.hasValue(index2, i)) {
				double term = data1.doubleValue(index1, i) - data2.doubleValue(index2, i);
				result = result + weights[i]*Math.abs(term);
				tweight += weights[i];
			}
		}
		if (tweight == 0.0) return 0;
		return (result/tweight);
	}

	private static double correlationMetric(BaseMatrix data1, BaseMatrix data2, double[] weights, 
	                                        int index1, int index2) {
		double result = 0.0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double denom1 = 0.0;
		double denom2 = 0.0;
		double tweight = 0.0;
		for (int i = 0; i < data1.nColumns(); i++) {
			if (data1.hasValue(index1, i) && data2.hasValue(index2, i)) {
				double term1 = data1.doubleValue(index1, i);
				double term2 = data2.doubleValue(index2, i);
				double w = weights[i];
				sum1 += w*term1;
				sum2 += w*term2;
				result += w*term1*term2;
				denom1 += w*term1*term1;
				denom2 += w*term2*term2;
				tweight += w;
			}
		}
		if (tweight == 0.0) return 0;
		result -= sum1 * sum2 / tweight;
		denom1 -= sum1 * sum1 / tweight;
		denom2 -= sum2 * sum2 / tweight;
		if (denom1 <= 0) return 1;
		if (denom2 <= 0) return 1;
		result = result / Math.sqrt(denom1*denom2);
		return (1.0 - result);
	}

	private static double acorrelationMetric(BaseMatrix data1, BaseMatrix data2, double[] weights, 
	                                         int index1, int index2) {
		double result = 0.0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		double denom1 = 0.0;
		double denom2 = 0.0;
		double tweight = 0.0;
		for (int i = 0; i < data1.nColumns(); i++) {
			if (data1.hasValue(index1, i) && data2.hasValue(index2, i)) {
				double term1 = data1.doubleValue(index1, i);
				double term2 = data2.doubleValue(index2, i);
				double w = weights[i];
				sum1 += w*term1;
				sum2 += w*term2;
				result += w*term1*term2;
				denom1 += w*term1*term1;
				denom2 += w*term2*term2;
				tweight += w;
			}
		}
		if (tweight == 0.0) return 0;
		result -= sum1 * sum2 / tweight;
		denom1 -= sum1 * sum1 / tweight;
		denom2 -= sum2 * sum2 / tweight;
		if (denom1 <= 0) return 1;
		if (denom2 <= 0) return 1;
		result = Math.abs(result) / Math.sqrt(denom1*denom2);
		return (1.0 - result);
	}

	private static double ucorrelationMetric(BaseMatrix data1, BaseMatrix data2, double[] weights, 
	                                         int index1, int index2) {
		double result = 0.0;
		double denom1 = 0.0;
		double denom2 = 0.0;
		boolean flag = false;

		for (int i = 0; i < data1.nColumns(); i++) {
			if (data1.hasValue(index1,i) && data2.hasValue(index2,i)) {
				double term1 = data1.doubleValue(index1,i);
				double term2 = data2.doubleValue(index2,i);
				double w = weights[i];
				result += w*term1*term2;
				denom1 += w*term1*term1;
				denom2 += w*term2*term2;
				flag = true;
			}
		}
		if (!flag) return 0.0;
		if (denom1 == 0) return 1;
		if (denom2 == 0) return 1;
		result = result / Math.sqrt(denom1*denom2);
		return (1.0 - result);
	}

	private static double uacorrelationMetric(BaseMatrix data1, BaseMatrix data2, double[] weights, 
	                                          int index1, int index2) {
		double result = 0.0;
		double denom1 = 0.0;
		double denom2 = 0.0;
		boolean flag = false;

		for (int i = 0; i < data1.nColumns(); i++) {
			if (data1.hasValue(index1,i) && data2.hasValue(index2,i)) {
				double term1 = data1.doubleValue(index1,i);
				double term2 = data2.doubleValue(index2,i);
				double w = weights[i];
				result += w*term1*term2;
				denom1 += w*term1*term1;
				denom2 += w*term2*term2;
				flag = true;
			}
		}
		if (!flag) return 0.0;
		if (denom1 == 0) return 1;
		if (denom2 == 0) return 1;
		result = Math.abs(result) / Math.sqrt(denom1*denom2);
		return (1.0 - result);
	}

	private static double spearmanMetric(BaseMatrix data1, BaseMatrix data2, double[] weights, 
	                                     int index1, int index2) {
		double result = 0.0;
		double denom1 = 0.0;
		double denom2 = 0.0;
		double[] rank1 = data1.getRank(index1);
		double[] rank2 = data2.getRank(index2);

		if (rank1 == null || rank2 == null)
			return 0.0;

		double avgrank = 0.5*(rank1.length-1);

		for (int i = 0; i < rank1.length; i++) {
			double value1 = rank1[i];
			double value2 = rank2[i];
			result += value1 * value2;
			denom1 += value1 * value1;
			denom2 += value2 * value2;
		}
		result /= rank1.length;
		denom1 /= rank1.length;
		denom2 /= rank1.length;
		result -= avgrank * avgrank;
		denom1 -= avgrank * avgrank;
		denom2 -= avgrank * avgrank;
		if (denom1 <= 0) return 1;
		if (denom2 <= 0) return 1;
		result = result / Math.sqrt(denom1*denom2);
		return (1.0 - result);
	}

	private static double kendallMetric(BaseMatrix data1, BaseMatrix data2, double[] weights, 
	                                    int index1, int index2) {
		int con = 0;
		int dis = 0;
		int exx = 0;
		int exy = 0;
		boolean flag = false;
		double denomx;
		double denomy;
		double tau;
		for (int i = 0; i < data1.nColumns(); i++) {
			for (int j = 0; j < i; j++) {
				if (data1.hasValue(index1, j) && data2.hasValue(index2, j)) {
					double x1 = data1.doubleValue(index1, i);
					double x2 = data1.doubleValue(index1, j);
					double y1 = data2.doubleValue(index2, i);
					double y2 = data2.doubleValue(index2, j);
					if (x1 < x2 && y1 < y2) con++;
					if (x1 > x2 && y1 > y2) con++;
					if (x1 < x2 && y1 > y2) dis++;
					if (x1 > x2 && y1 < y2) dis++;
					if (x1 == x2 && y1 != y2) exx++;
					if (x1 != x2 && y1 == y2) exy++;
					flag = true;
				}
			}
		}
		if (!flag) return 0.0;
		denomx = con + dis + exx;
		denomy = con + dis + exy;
		if (denomx == 0) return 1;
		if (denomy == 0) return 1;
		tau = (con-dis)/Math.sqrt(denomx*denomy);
		return 1.-tau;
	}
}
