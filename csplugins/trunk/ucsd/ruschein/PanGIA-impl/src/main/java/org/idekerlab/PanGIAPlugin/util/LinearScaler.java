/*
  File: LinearScaler.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.idekerlab.PanGIAPlugin.util;


/**
 *  Used to scale a list of values to [a,b]
 */
class LinearScaler extends AbstractScaler {
	public double[] scale(final double values[], final double a, final double b) throws IllegalArgumentException
	{
		if (values.length < 2)
			throw new IllegalArgumentException("need at least 2 values for scaling!");
		if (a >= b)
			throw new IllegalArgumentException("bad bounds!");

		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (final double d : values) {
			if (d < min)
				min = d;
			if (d > max)
				max = d;
		}

		if (min == max)
			throw new IllegalArgumentException("input values are all identical!");

		final double c = (a - b) / (min - max);
		final double d = a - c * min;

		final double[] scaledValues = new double[values.length];
		for (int i = 0; i < values.length; ++i)
			scaledValues[i] = c * values[i] + d;

		return scaledValues;
	}

	public float[] scale(final float values[], final float a, final float b) throws IllegalArgumentException
	{
		if (values.length < 2)
			throw new IllegalArgumentException("need at least 2 values for scaling!");
		if (a >= b)
			throw new IllegalArgumentException("bad bounds!");

		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		for (final float d : values) {
			if (d < min)
				min = d;
			if (d > max)
				max = d;
		}

		if (min == max)
			throw new IllegalArgumentException("input values are all identical!");

		final float c = (a - b) / (min - max);
		final float d = a - c * min;

		final float[] scaledValues = new float[values.length];
		for (int i = 0; i < values.length; ++i)
			scaledValues[i] = c * values[i] + d;

		return scaledValues;
	}
}
