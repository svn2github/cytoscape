/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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
package nodeCharts.view;

// System imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Cytoscape imports
import cytoscape.command.CyCommandException;

public class ValueUtils {

	public static List<Double> convertInputToDouble(Object input) throws CyCommandException {
		if (input == null)
			throw new CyCommandException("no input data?");

		List<Double>values = null;
		if (input instanceof String) {
			values = ValueUtils.parseStringList((String)input);
		} else if (input instanceof List) {
			Object o = ((List)input).get(0);
			if (o instanceof Double) {
				values = (List<Double>)input;
			} else if (o instanceof String) {
				values = ValueUtils.convertStringList((List<String>)input);
			} else if (o instanceof Integer) {
				values = ValueUtils.convertIntegerList((List<Integer>)input);
			}
		}
		return values;
	}

	public static List<Double> convertStringList(List<String> input) throws CyCommandException {
		List<Double> values = new ArrayList<Double>(input.size());
		for (String s: input) {
			try {
				Double d = Double.valueOf(s);
				values.add(d);
			} catch (NumberFormatException e) {
				throw new CyCommandException("Non-numeric value: '"+s+"' in values array");
			}
		}
		return values;
	}

	public static List<Double> convertIntegerList(List<Integer> input) {
		List<Double> values = new ArrayList<Double>(input.size());
		for (Integer s: input) {
			double d = s.doubleValue();
			values.add(d);
		}
		return values;
	}

	public static List<Double> parseStringList(String input) throws CyCommandException {
		if (input == null)
			throw new CyCommandException("no input data?");

		String[] inputArray = input.split(",");
		return convertStringList(Arrays.asList(inputArray));
	}
}
