package cytoscape.undo.unitTests;

import junit.framework.TestCase;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Suite of all Undo JUnit Tests.
 *
 * @author Ethan Cerami
 */
public class UndoSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(UndoableGraphHiderTest.class);
        suite.setName("Undo Tests");
        return suite;
    }
}