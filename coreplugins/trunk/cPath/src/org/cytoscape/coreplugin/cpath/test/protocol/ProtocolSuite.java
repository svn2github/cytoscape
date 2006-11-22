package org.cytoscape.coreplugin.cpath.test.protocol;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.cytoscape.coreplugin.cpath.test.task.TestCPathTimeEstimator;

/**
 * Suite of Mapper JUnit Tests.
 *
 * @author Ethan Cerami.
 */
public class ProtocolSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     *
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestCPathProtocol.class);
        suite.setName("Test cPath Protocol");
        return suite;
    }
}
