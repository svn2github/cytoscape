/*
  File: Parser.java

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
package org.cytoscape.equations;


import org.cytoscape.equations.builtins.*;


/**
 *  A singleton EqnParser
 */
public class Parser {
	static private EqnParser eqnParser;

	synchronized static public EqnParser getParser() {
		if (eqnParser == null) {
			eqnParser = new EqnParserImpl();

			eqnParser.registerFunction(new Abs());
			eqnParser.registerFunction(new And());
			eqnParser.registerFunction(new Average());
			eqnParser.registerFunction(new Combin());
			eqnParser.registerFunction(new Count());
			eqnParser.registerFunction(new Exp());
			eqnParser.registerFunction(new First());
			eqnParser.registerFunction(new GeoMean());
			eqnParser.registerFunction(new HarMean());
			eqnParser.registerFunction(new If());
			eqnParser.registerFunction(new Largest());
			eqnParser.registerFunction(new LCase());
			eqnParser.registerFunction(new Last());
			eqnParser.registerFunction(new Left());
			eqnParser.registerFunction(new Len());
			eqnParser.registerFunction(new Ln());
			eqnParser.registerFunction(new Log());
			eqnParser.registerFunction(new Max());
			eqnParser.registerFunction(new Median());
			eqnParser.registerFunction(new Mid());
			eqnParser.registerFunction(new Min());
			eqnParser.registerFunction(new Mod());
			eqnParser.registerFunction(new Mode());
			eqnParser.registerFunction(new Not());
			eqnParser.registerFunction(new Nth());
			eqnParser.registerFunction(new Or());
			eqnParser.registerFunction(new Permut());
			eqnParser.registerFunction(new Pi());
			eqnParser.registerFunction(new Right());
			eqnParser.registerFunction(new Round());
			eqnParser.registerFunction(new Sign());
			eqnParser.registerFunction(new StDev());
			eqnParser.registerFunction(new Sqrt());
			eqnParser.registerFunction(new Substitute());
			eqnParser.registerFunction(new Trunc());
			eqnParser.registerFunction(new UCase());
			eqnParser.registerFunction(new Value());
			eqnParser.registerFunction(new Var());
		}

		return eqnParser;
	}
}
