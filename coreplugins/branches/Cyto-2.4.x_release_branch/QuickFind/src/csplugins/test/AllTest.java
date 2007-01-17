package csplugins.test;

import csplugins.test.widgets.test.unitTests.text.TestTextIndex;
import csplugins.test.widgets.test.unitTests.text.TestNumberIndex;
import csplugins.test.widgets.test.unitTests.view.TestTextIndexComboBox;
import csplugins.test.quickfind.test.TestCyAttributesUtil;
import csplugins.test.quickfind.test.TestQuickFind;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Runs all Unit Tests for the Quick Find Project.
 *
 * @author Ethan Cerami.
 */
public class AllTest extends TestCase {

    /**
     * Master Test Suite.
     *
     * @return Test Suite to run.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestTextIndex.class);
        suite.addTestSuite(TestNumberIndex.class);
        suite.addTestSuite(TestTextIndexComboBox.class);
        suite.addTestSuite(TestQuickFind.class);
        suite.addTestSuite(TestCyAttributesUtil.class);
        suite.setName("Quick Find Tests");
        return suite;
    }

    /**
     * Run the all tests method.
     *
     * @param args java.lang.String[]
     * @throws Exception All Errors.
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0] != null && args[0].equals("-ui")) {
            String newargs[] = {"csplugins.test.AllTest", "-noloading"};
            junit.swingui.TestRunner.main(newargs);
        } else {
            junit.textui.TestRunner.run(suite());
        }
    }
}