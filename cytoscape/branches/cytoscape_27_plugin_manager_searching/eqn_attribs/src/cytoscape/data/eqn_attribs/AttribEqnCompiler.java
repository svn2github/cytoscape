/*
  File: AttribEqnCompiler.java

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


import java.util.Map;
import java.util.Stack;
import cytoscape.data.eqn_attribs.AttribParser;
import cytoscape.data.eqn_attribs.interpreter.Instruction;
import cytoscape.data.eqn_attribs.parse_tree.Node;


public class AttribEqnCompiler {
	private AttribParser parser;
	private Object[] code;
	private String errorMsg;

	public AttribEqnCompiler() {
		this.parser = new AttribParser();
		this.code = null;
		this.errorMsg = null;
	}

	public void registerFunction(final AttribFunction func) throws IllegalArgumentException {
		parser.registerFunction(func);
	}

	public boolean compile(final String equation, final Map<String, Class> attribNameToTypeMap) {
		if (!parser.parse(equation, attribNameToTypeMap)) {
			errorMsg = parser.getErrorMsg();
			return false;
		}

		final Node parseTree = parser.getParseTree();

		final Stack<Object> codeStack = new Stack<Object>();
		try {
			parseTree.genCode(codeStack);
		} catch (final IllegalStateException e) {
			errorMsg = e.getCause().toString();
			return false;
		}

		code = new Object[codeStack.size()];
		for (int i = code.length - 1; i >= 0; --i)
			code[i] = codeStack.pop();

		errorMsg = null;
		return true;
	}

	public String getLastErrorMsg() { return errorMsg; }

	public Object[] getCode() { return code; }
}
