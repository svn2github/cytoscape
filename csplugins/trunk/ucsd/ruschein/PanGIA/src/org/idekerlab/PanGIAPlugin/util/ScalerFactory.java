/*
  File: ScalerFactory.java

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


import java.util.Map;
import java.util.TreeMap;


public class ScalerFactory {
	private static Map<String, Scaler> typeToScalerMap = null;

	/**
	 *  @return one of the registered Scaler types.  Preregistered are "linear" and "rank".
	 */
	public static synchronized Scaler getScaler(final String type) throws IllegalArgumentException {
		if (typeToScalerMap == null)
			init();

		final Scaler scaler = typeToScalerMap.get(type);
		if (scaler == null)
			throw new IllegalArgumentException("unknown type \"" + type + "\"!");

		return scaler;
	}

	public static synchronized void registerScaler(final String type, final Scaler newScaler) {
		if (typeToScalerMap == null)
			init();

		if (typeToScalerMap.containsKey(type))
			throw new IllegalArgumentException("trying to register a duplicate type \"" + type + "\"!");

		typeToScalerMap.put(type, newScaler);
	}

	private static void init() {
		if (typeToScalerMap != null)
			throw new IllegalStateException("already initialised!");
		typeToScalerMap = new TreeMap<String, Scaler>();

		typeToScalerMap.put("linear", new LinearScaler());
		typeToScalerMap.put("rank", new RankScaler());
	}
}
