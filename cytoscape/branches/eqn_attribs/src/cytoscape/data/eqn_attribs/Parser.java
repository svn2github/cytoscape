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
package cytoscape.data.eqn_attribs;


import cytoscape.data.eqn_attribs.builtins.*;


/**
 *  A singleton AttribParser
 */
public class Parser {
	static private AttribParser attribParser;

	synchronized static public AttribParser getParser() {
		if (attribParser == null) {
			attribParser = new AttribParserImpl();

			attribParser.registerFunction(new And());
			attribParser.registerFunction(new Or());
			attribParser.registerFunction(new Log());
			attribParser.registerFunction(new Abs());
			attribParser.registerFunction(new Not());
			attribParser.registerFunction(new LCase());
			attribParser.registerFunction(new UCase());
			attribParser.registerFunction(new Substitute());
			attribParser.registerFunction(new If());
			attribParser.registerFunction(new Ln());
			attribParser.registerFunction(new Exp());
			attribParser.registerFunction(new Left());
			attribParser.registerFunction(new Right());
			attribParser.registerFunction(new Mid());
			attribParser.registerFunction(new Len());
			attribParser.registerFunction(new Round());
			attribParser.registerFunction(new Trunc());
			attribParser.registerFunction(new Pi());
			attribParser.registerFunction(new Value());
			attribParser.registerFunction(new Average());
			attribParser.registerFunction(new Min());
			attribParser.registerFunction(new Max());
			attribParser.registerFunction(new Count());
			attribParser.registerFunction(new Median());
			attribParser.registerFunction(new Nth());
			attribParser.registerFunction(new First());
			attribParser.registerFunction(new Last());
		}

		return attribParser;
	}
}
