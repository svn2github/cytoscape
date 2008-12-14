/**
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 *
 */
public class GnuParseTest extends TestCase {
	private Options _options = null;
	private CommandLineParser _parser = null;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		return new TestSuite(GnuParseTest.class);
	}

	/**
	 * Creates a new GnuParseTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public GnuParseTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
		_options = new Options().addOption("a", "enable-a", false, "turn [a] on or off")
		                        .addOption("b", "bfile", true, "set the value of [b]")
		                        .addOption("c", "copt", false, "turn [c] on or off");

		_parser = new GnuParser();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void tearDown() {
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testSimpleShort() {
		String[] args = new String[] { "-a", "-b", "toast", "foo", "bar" };

		try {
			CommandLine cl = _parser.parse(_options, args);

			assertTrue("Confirm -a is set", cl.hasOption("a"));
			assertTrue("Confirm -b is set", cl.hasOption("b"));
			assertTrue("Confirm arg of -b", cl.getOptionValue("b").equals("toast"));
			assertTrue("Confirm size of extra args", cl.getArgList().size() == 2);
		} catch (ParseException e) {
			fail(e.toString());
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testSimpleLong() {
		String[] args = new String[] { "--enable-a", "--bfile", "toast", "foo", "bar" };

		try {
			CommandLine cl = _parser.parse(_options, args);

			assertTrue("Confirm -a is set", cl.hasOption("a"));
			assertTrue("Confirm -b is set", cl.hasOption("b"));
			assertTrue("Confirm arg of -b", cl.getOptionValue("b").equals("toast"));
			assertTrue("Confirm size of extra args", cl.getArgList().size() == 2);
		} catch (ParseException e) {
			fail(e.toString());
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testExtraOption() {
		String[] args = new String[] { "-a", "-d", "-b", "toast", "foo", "bar" };

		boolean caught = false;

		try {
			CommandLine cl = _parser.parse(_options, args);

			assertTrue("Confirm -a is set", cl.hasOption("a"));
			assertTrue("Confirm -b is set", cl.hasOption("b"));
			assertTrue("confirm arg of -b", cl.getOptionValue("b").equals("toast"));
			assertTrue("Confirm size of extra args", cl.getArgList().size() == 3);
		} catch (UnrecognizedOptionException e) {
			caught = true;
		} catch (ParseException e) {
			fail(e.toString());
		}

		assertTrue("Confirm UnrecognizedOptionException caught", caught);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testMissingArg() {
		String[] args = new String[] { "-b" };

		boolean caught = false;

		try {
			CommandLine cl = _parser.parse(_options, args);
		} catch (MissingArgumentException e) {
			caught = true;
		} catch (ParseException e) {
			fail(e.toString());
		}

		assertTrue("Confirm MissingArgumentException caught", caught);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testStop() {
		String[] args = new String[] { "-c", "foober", "-b", "toast" };

		try {
			CommandLine cl = _parser.parse(_options, args, true);
			assertTrue("Confirm -c is set", cl.hasOption("c"));
			assertTrue("Confirm  3 extra args: " + cl.getArgList().size(),
			           cl.getArgList().size() == 3);
		} catch (ParseException e) {
			fail(e.toString());
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testMultiple() {
		String[] args = new String[] { "-c", "foobar", "-b", "toast" };

		try {
			CommandLine cl = _parser.parse(_options, args, true);
			assertTrue("Confirm -c is set", cl.hasOption("c"));
			assertTrue("Confirm  3 extra args: " + cl.getArgList().size(),
			           cl.getArgList().size() == 3);

			cl = _parser.parse(_options, cl.getArgs());

			assertTrue("Confirm -c is not set", !cl.hasOption("c"));
			assertTrue("Confirm -b is set", cl.hasOption("b"));
			assertTrue("Confirm arg of -b", cl.getOptionValue("b").equals("toast"));
			assertTrue("Confirm  1 extra arg: " + cl.getArgList().size(),
			           cl.getArgList().size() == 1);
			assertTrue("Confirm  value of extra arg: " + cl.getArgList().get(0),
			           cl.getArgList().get(0).equals("foobar"));
		} catch (ParseException e) {
			fail(e.toString());
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testMultipleWithLong() {
		String[] args = new String[] { "--copt", "foobar", "--bfile", "toast" };

		try {
			CommandLine cl = _parser.parse(_options, args, true);
			assertTrue("Confirm -c is set", cl.hasOption("c"));
			assertTrue("Confirm  3 extra args: " + cl.getArgList().size(),
			           cl.getArgList().size() == 3);

			cl = _parser.parse(_options, cl.getArgs());

			assertTrue("Confirm -c is not set", !cl.hasOption("c"));
			assertTrue("Confirm -b is set", cl.hasOption("b"));
			assertTrue("Confirm arg of -b", cl.getOptionValue("b").equals("toast"));
			assertTrue("Confirm  1 extra arg: " + cl.getArgList().size(),
			           cl.getArgList().size() == 1);
			assertTrue("Confirm  value of extra arg: " + cl.getArgList().get(0),
			           cl.getArgList().get(0).equals("foobar"));
		} catch (ParseException e) {
			fail(e.toString());
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testDoubleDash() {
		String[] args = new String[] { "--copt", "--", "-b", "toast" };

		try {
			CommandLine cl = _parser.parse(_options, args);

			assertTrue("Confirm -c is set", cl.hasOption("c"));
			assertTrue("Confirm -b is not set", !cl.hasOption("b"));
			assertTrue("Confirm 2 extra args: " + cl.getArgList().size(),
			           cl.getArgList().size() == 2);
		} catch (ParseException e) {
			fail(e.toString());
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testSingleDash() {
		String[] args = new String[] { "--copt", "-b", "-", "-a", "-" };

		try {
			CommandLine cl = _parser.parse(_options, args);

			assertTrue("Confirm -a is set", cl.hasOption("a"));
			assertTrue("Confirm -b is set", cl.hasOption("b"));
			assertTrue("Confirm arg of -b", cl.getOptionValue("b").equals("-"));
			assertTrue("Confirm 1 extra arg: " + cl.getArgList().size(), cl.getArgList().size() == 1);
			assertTrue("Confirm value of extra arg: " + cl.getArgList().get(0),
			           cl.getArgList().get(0).equals("-"));
		} catch (ParseException e) {
			fail(e.toString());
		}
	}
}
