package cytoscape.unitTests;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Runs all Cytoscape Unit Tests.
 *
 * @author Ethan Cerami
 */
public class AllTests extends TestCase {

    /**
     * The suite method kicks off all of the tests.
     *
     * @return junit.framework.Test
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(CoreSuite.suite());
        //  All other suites will go here ...
        return suite;
    }

    /**
     * Run the all tests method.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0] != null && args[0].equals("-ui")) {
            String newargs[] = {"cytoscape.unitTests.AllTests",
                                "-noloading"};
            junit.swingui.TestRunner.main(newargs);
        } else {
            junit.textui.TestRunner.run(suite());
        }
    }
}