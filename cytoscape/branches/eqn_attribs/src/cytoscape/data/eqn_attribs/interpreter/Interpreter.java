/*
  File: Interpreter.java

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
package cytoscape.data.eqn_attribs.interpreter;


import cytoscape.data.eqn_attribs.parse_tree.*;
import java.util.EmptyStackException;
import java.util.Stack;


public class Interpreter {
	private final Stack<Object> runtimeStack;

	public Interpreter(final Stack<Object> runtimeStack) throws IllegalStateException {
		if (runtimeStack == null || runtimeStack.empty())
			throw new IllegalStateException("runtime stack must not be null nor empty!");

		this.runtimeStack = runtimeStack;
	}

	/**
	 *  Excutes the code that was passed into the constructor.
	 *  @returns a Double, Boolean or String object that is the result of a successful execution.
	 *  @throws ArithmeticException thrown if an arithmetic error was detected like a division by zero etc.
	 *  @throws IllegalArgumentException thrown if a function invocation resulted in a function detecting an invalid argument
	 *  @throws IllegalStateException thrown if an invalid interpreter internal state was reached
	 */
	public Object run() throws ArithmeticException, IllegalArgumentException, IllegalStateException {
		try {
			while (runtimeStack.size() > 1) {
				final int opCode = (Integer)runtimeStack.pop();
				switch (opCode) {
				case Instructions.FADD:
					fadd();
					break;
				case Instructions.FSUB:
					fsub();
					break;
				case Instructions.FMUL:
					fmul();
					break;
				case Instructions.FDIV:
					fdiv();
					break;
				case Instructions.FPOW:
					fpow();
					break;
				case Instructions.SCONCAT:
					unimplemented("SCONCAT");
					break;
				case Instructions.FFROMI:
					unimplemented("FFROMI");
					break;
				case Instructions.BEQLF:
					unimplemented("BEQLF");
					break;
				case Instructions.BNEQLF:
					unimplemented("BNEQLF");
					break;
				case Instructions.BGTF:
					unimplemented("BGTF");
					break;
				case Instructions.BLTF:
					unimplemented("BLTF");
					break;
				case Instructions.BGTEF:
					unimplemented("BGTEF");
					break;
				case Instructions.BLTEF:
					unimplemented("BLTEF");
					break;
				case Instructions.BEQLS:
					unimplemented("BEQLS");
					break;
				case Instructions.BNEQLS:
					unimplemented("BNEQLS");
					break;
				case Instructions.BGTS:
					unimplemented("BGTS");
					break;
				case Instructions.BLTS:
					unimplemented("BLTS");
					break;
				case Instructions.BGTES:
					unimplemented("BGTES");
					break;
				case Instructions.BLTES:
					unimplemented("BLTES");
					break;
				case Instructions.CALL:
					unimplemented("CALL");
					break;
				case Instructions.FUMINUS:
					fuminus();
					break;
				case Instructions.FUPLUS:
					fuplus();
					break;
				default:
					throw new IllegalStateException("unknown opcode: " + opCode + "!");
				}
			}
		} catch (final EmptyStackException e) {
			throw new IllegalStateException("inconistent number of stack entries detected!");
		}

		final Object retval = runtimeStack.pop();
		if (retval instanceof Double)
			return retval;
		if (retval instanceof String)
			return retval;
		if (retval instanceof Boolean)
			return retval;

		throw new IllegalStateException("illegal result type at end of interpretation: " + retval.getClass() + "!");
	}

	private void fadd() throws EmptyStackException {
		final double float1 = getFloat(runtimeStack.pop());
		final double float2 = getFloat(runtimeStack.pop());
		runtimeStack.push(new FloatConstantNode(float1 + float2));
	}

	private void fsub() throws EmptyStackException {
		final double float1 = getFloat(runtimeStack.pop());
		final double float2 = getFloat(runtimeStack.pop());
		runtimeStack.push(new FloatConstantNode(float1 - float2));
	}

	private void fmul() throws EmptyStackException {
		final double float1 = getFloat(runtimeStack.pop());
		final double float2 = getFloat(runtimeStack.pop());
		runtimeStack.push(new FloatConstantNode(float1 * float2));
	}

	private void fdiv() throws EmptyStackException {
		final double float1 = getFloat(runtimeStack.pop());
		final double float2 = getFloat(runtimeStack.pop());
		if (float2 == 0.0)
			throw new ArithmeticException("illegal division by zero!");
		runtimeStack.push(new FloatConstantNode(float1 + float2));
	}

	private void fpow() throws EmptyStackException {
		final double float1 = getFloat(runtimeStack.pop());
		final double float2 = getFloat(runtimeStack.pop());
		runtimeStack.push(new FloatConstantNode(Math.pow(float1, float2)));
	}

	private void fuminus() throws EmptyStackException {
		final double float1 = getFloat(runtimeStack.pop());
		runtimeStack.push(new FloatConstantNode(-float1));
	}

	private void fuplus() throws EmptyStackException {
		final double float1 = getFloat(runtimeStack.pop());
		runtimeStack.push(new FloatConstantNode(+float1));
	}

	private double getFloat(final Object o) {
		if (o instanceof FloatConstantNode)
			return ((FloatConstantNode)o).getValue();

		if (o instanceof IdentNode) {
			final IdentNode identNode = (IdentNode)o;
			final Object nodeValue = identNode.getValue();
			if (nodeValue instanceof Double)
				return (Double)nodeValue;
			throw new IllegalStateException("can't convert the value of {" + identNode.getAttribName() + "} to a floating point number!");
		}

		throw new IllegalStateException("can't convert a " + o.getClass() + " to a floating point number!");
	}

	/**
	 *  Only used during debugging.
	 */
	private void unimplemented(final String opCode) {
		throw new IllegalStateException("encountered an unimplemented opcode: " + opCode + "!");
	}
}
