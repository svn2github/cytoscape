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
public class BuildTest extends TestCase {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Test suite() {
		return new TestSuite(BuildTest.class);
	}

	/**
	 * Creates a new BuildTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public BuildTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void tearDown() {
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testSimple() {
		Options opts = new Options();

		opts.addOption("a", false, "toggle -a");

		opts.addOption("b", true, "toggle -b");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testDuplicateSimple() {
		Options opts = new Options();
		opts.addOption("a", false, "toggle -a");

		opts.addOption("a", true, "toggle -a*");

		assertEquals("last one in wins", "toggle -a*", opts.getOption("a").getDescription());
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testLong() {
		Options opts = new Options();

		opts.addOption("a", "--a", false, "toggle -a");

		opts.addOption("b", "--b", true, "set -b");
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testDuplicateLong() {
		Options opts = new Options();
		opts.addOption("a", "--a", false, "toggle -a");

		opts.addOption("a", "--a", false, "toggle -a*");
		assertEquals("last one in wins", "toggle -a*", opts.getOption("a").getDescription());
	}
}
