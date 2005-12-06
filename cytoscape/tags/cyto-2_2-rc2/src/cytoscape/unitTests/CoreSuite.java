package cytoscape.unitTests;

import junit.framework.TestCase;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all Core JUnit Tests.
 *
 * @author Ethan Cerami
 */
public class CoreSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        //suite.addTestSuite(CytoscapeConfigTest.class);
        //suite.addTestSuite(PluginLoaderTest.class);
        suite.setName("Core Cytoscape Tests");
        return suite;
    }
}
