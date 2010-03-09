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


import cytoscape.data.eqn_attribs.AttribFunction;
import cytoscape.data.eqn_attribs.parse_tree.*;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;


public class Interpreter {
	private final Instruction[] opCodes;
	private final Stack<Object> argumentStack;
	private final Map<String, IdentDescriptor> nameToDescriptorMap;

		public Interpreter(final Instruction[] opCodes, final Stack<Object> argumentStack,
		                   final Map<String, IdentDescriptor> nameToDescriptorMap)
		throws IllegalStateException
	{
		if (opCodes == null || opCodes.length == 0)
			throw new IllegalStateException("null or empty opcodes!");
		if (argumentStack == null || argumentStack.empty())
			throw new IllegalStateException("argument stack must not be null nor empty!");

		this.opCodes = opCodes;
		this.argumentStack = argumentStack;
		this.nameToDescriptorMap = nameToDescriptorMap;
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
			for (final Instruction opCode : opCodes) {
				switch (opCode) {
				case FADD:
					fadd();
					break;
				case FSUB:
					fsub();
					break;
				case FMUL:
					fmul();
					break;
				case FDIV:
					fdiv();
					break;
				case FPOW:
					fpow();
					break;
				case SCONCAT:
					sconcat();
					break;
				case SCONV:
					sconv();
					break;
				case BEQLF:
					beqlf();
					break;
				case BNEQLF:
					bneqlf();
					break;
				case BGTF:
					bgtf();
					break;
				case BLTF:
					bltf();
					break;
				case BGTEF:
					bgtef();
					break;
				case BLTEF:
					bltef();
					break;
				case BEQLS:
					beqls();
					break;
				case BNEQLS:
					bneqls();
					break;
				case BGTS:
					bgts();
					break;
				case BLTS:
					blts();
					break;
				case BGTES:
					bgtes();
					break;
				case BLTES:
					bltes();
					break;
				case BEQLB:
					beqlb();
					break;
				case BNEQLB:
					bneqlb();
					break;
				case CALL:
					call();
					break;
				case FUMINUS:
					fuminus();
					break;
				case FUPLUS:
					fuplus();
					break;
				case AREF:
					aref();
					break;
				case AREF2:
					aref2();
					break;
				default:
					throw new IllegalStateException("unknown opcode: " + opCode + "!");
				}
			}
		} catch (final EmptyStackException e) {
			throw new IllegalStateException("inconistent number of stack entries detected!");
		}

		final Object retval = argumentStack.pop();
		if (retval instanceof Double)
			return retval;
		if (retval instanceof String)
			return retval;
		if (retval instanceof Boolean)
			return retval;

		throw new IllegalStateException("illegal result type at end of interpretation: " + retval.getClass() + "!");
	}

	private void sconv() throws EmptyStackException {
		argumentStack.push(argumentStack.pop().toString());
	}

	private void fadd() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 + float2);
	}

	private void fsub() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 - float2);
	}

	private void fmul() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 * float2);
	}

	private void fdiv() throws EmptyStackException, ArithmeticException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		if (float2 == 0.0)
			throw new ArithmeticException("illegal division by zero!");
		argumentStack.push(float1 / float2);
	}

	private void fpow() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(Math.pow(float1, float2));
	}

	private void sconcat() throws EmptyStackException {
		final String string1 = getString(argumentStack.pop());
		final String string2 = getString(argumentStack.pop());
		argumentStack.push(string1 + string2);
	}

	private void beqlf() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 == float2);
	}

	private void bneqlf() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 != float2);
	}

	private void bltf() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 < float2);
	}

	private void bgtf() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 > float2);
	}

	private void bltef() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 <= float2);
	}

	private void bgtef() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		final double float2 = getFloat(argumentStack.pop());
		argumentStack.push(float1 >= float2);
	}

	private void beqls() throws EmptyStackException {
		final String string1 = getString(argumentStack.pop());
		final String string2 = getString(argumentStack.pop());
		argumentStack.push(string1.equals(string2));
	}

	private void bneqls() throws EmptyStackException {
		final String string1 = getString(argumentStack.pop());
		final String string2 = getString(argumentStack.pop());
		argumentStack.push(!string1.equals(string2));
	}

	private void blts() throws EmptyStackException {
		final String string1 = getString(argumentStack.pop());
		final String string2 = getString(argumentStack.pop());
		argumentStack.push(string1.compareTo(string2) < 0);
	}

	private void bgts() throws EmptyStackException {
		final String string1 = getString(argumentStack.pop());
		final String string2 = getString(argumentStack.pop());
		argumentStack.push(string1.compareTo(string2) > 0);
	}

	private void bltes() throws EmptyStackException {
		final String string1 = getString(argumentStack.pop());
		final String string2 = getString(argumentStack.pop());
		argumentStack.push(string1.compareTo(string2) <= 0);
	}

	private void bgtes() throws EmptyStackException {
		final String string1 = getString(argumentStack.pop());
		final String string2 = getString(argumentStack.pop());
		argumentStack.push(string1.compareTo(string2) >= 0);
	}

	private void beqlb() throws EmptyStackException {
		final boolean bool1 = getBoolean(argumentStack.pop());
		final boolean bool2 = getBoolean(argumentStack.pop());
		argumentStack.push(bool1 == bool2);
	}

	private void bneqlb() throws EmptyStackException {
		final boolean bool1 = getBoolean(argumentStack.pop());
		final boolean bool2 = getBoolean(argumentStack.pop());
		argumentStack.push(bool1 != bool2);
	}

	private void call() throws EmptyStackException, IllegalStateException {
		// 1. get the function
		final Object o = argumentStack.pop();
		if (!(o instanceof AttribFunction))
			throw new IllegalStateException("expected an attribute function after the CALL opcode but found \"" + o.getClass() + "\" instead!");
		final AttribFunction func = (AttribFunction)o;

		// 2. get and validate the argument count
		final int argCount;
		try {
			argCount = (Integer)argumentStack.pop();
		} catch (final Exception e) {
			throw new IllegalStateException("invalid argument count type following a CALL opcode!");
		}
		final Class[] argTypes = func.getParameterTypes();
		final boolean varargs = func.getMinNumberOfArgsForVariableArity() != -1;
		if (varargs && (argCount < func.getMinNumberOfArgsForVariableArity() || argCount > func.getMaxNumberOfArgsForVariableArity()))
			throw new IllegalStateException("invalid number of arguments in call to " + func.getName() + "() (1)!");
		else if (argCount != argTypes.length)
			throw new IllegalStateException("invalid number of arguments in call to " + func.getName() + "() (2)!");

		final int MIN_ARG_COUNT = 0;
		final int MAX_ARG_COUNT = 50;
		if (argCount < MIN_ARG_COUNT || argCount > MAX_ARG_COUNT)
			throw new IllegalStateException("invalid argument count type following a CALL opcode (range must be in [" + MIN_ARG_COUNT + ", " + MAX_ARG_COUNT + "])!");

		// 3. collect and validate the actual arguments
		final Object args[] = new Object[argCount];
		for (int argNo = 0; argNo < argCount; ++argNo) {
			final Class expectedType = argTypes[varargs ? 0 : argNo];
			if (expectedType == Double.class)
				args[argNo] = new Double(getFloat(argumentStack.pop()));
			else if (expectedType == String.class)
				args[argNo] = getString(argumentStack.pop());
			else if (expectedType == Boolean.class)
				args[argNo] = new Boolean(getBoolean(argumentStack.pop()));
			else
				throw new IllegalStateException("unknown function argument type: " + expectedType.getClass() + "!");
		}

		// 4. now actually call the function
		argumentStack.push(func.evaluateFunction(args));
	}

	private void fuminus() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		argumentStack.push(-float1);
	}

	private void fuplus() throws EmptyStackException {
		final double float1 = getFloat(argumentStack.pop());
		argumentStack.push(+float1);
	}

	private void aref() throws EmptyStackException {
		final String attribName = (String)argumentStack.pop();
		final IdentDescriptor identDescriptor = nameToDescriptorMap.get(attribName);
		if (identDescriptor == null)
			throw new IllegalStateException("unknown attribute reference: \"" + attribName + "\" (2)!");
		final Object value = identDescriptor.getValue();
		if (value == null)
			throw new IllegalStateException("undefined attribute reference: \"" + attribName + "\" (2)!");
	}

	private void aref2() throws EmptyStackException {
System.err.println("*** Entering AREF2");
		final String attribName = (String)argumentStack.pop();
		final Object defaultValue = (String)argumentStack.pop();
		final IdentDescriptor identDescriptor = nameToDescriptorMap.get(attribName);
		if (identDescriptor == null)
			throw new IllegalStateException("unknown attribute reference: \"" + attribName + "\" (2)!");
		final Object value = identDescriptor.getValue();
System.err.println("*** About to push: " + (value != null ? value : defaultValue));
		argumentStack.push(value != null ? value : defaultValue);
	}

	private double getFloat(final Object o) throws IllegalStateException {
		if (o instanceof Double)
			return (Double)o;

		throw new IllegalStateException("can't convert a " + o.getClass() + " (" + o + ") to a floating point number!");
	}

	private String getString(final Object o) throws IllegalStateException {
		if (o instanceof String)
			return (String)o;

		throw new IllegalStateException("can't convert a " + o.getClass() + " to a string!");
	}

	private boolean getBoolean(final Object o) throws IllegalStateException {
		if (o instanceof Boolean)
			return (Boolean)o;

		throw new IllegalStateException("can't convert a " + o.getClass() + " to a boolean!");
	}
}
