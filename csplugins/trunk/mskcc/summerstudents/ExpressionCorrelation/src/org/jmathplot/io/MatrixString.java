package org.jmathplot.io;

import java.text.*;
import java.util.*;

public class MatrixString {

	private static int decimalSize = 10;
	private static String defaultWordDelimiter = " ";
	private static String defaultSentenceDelimiter = "\n";

	public static String printMatrix(double[][] m) {
		return printMatrix(m, defaultWordDelimiter, defaultSentenceDelimiter);
	}

	public static String printMatrix(double[][] m, String wordDelimiter, String sentenceDelimiter) {

		StringBuffer str = new StringBuffer(25 * m.length * m[0].length);

		DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(decimalSize);
		format.setMinimumFractionDigits(decimalSize);
		format.setGroupingUsed(false);

		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				String s = format.format(m[i][j]); // format the number
				str = str.append(wordDelimiter);
				str = str.append(s);
			}
			str = str.append(sentenceDelimiter);
		}
		return str.toString();
	}

	public static double[][] readString(String s) {
		return readString(s, defaultWordDelimiter, defaultSentenceDelimiter);
	}

	public static double[][] readString(String s, String wordDelimiter, String sentenceDelimiter) {

		double[][] array;

		String delimiterString = wordDelimiter;
		String newlineString = sentenceDelimiter;

		StringTokenizer linesTokenizer = new StringTokenizer(s, newlineString);
		StringTokenizer wordsTokenizer;

		Vector lines = new Vector();

		do {
			Vector words = new Vector();
			wordsTokenizer = new StringTokenizer(linesTokenizer.nextToken(),
					 delimiterString);

			do {
				words.addElement(Double.valueOf(wordsTokenizer.nextToken()));
			} while (wordsTokenizer.hasMoreElements());

			double[] line_i = new double[words.size()];

			//words.copyInto(line_i);
			for (int i = 0; i < line_i.length; i++) {
				line_i[i] = ( (Double) words.get(i)).doubleValue();
			}
			lines.add(line_i);

		} while (linesTokenizer.hasMoreElements());

		array = new double[lines.size()][];
		lines.copyInto(array);

		return array;
	}

}