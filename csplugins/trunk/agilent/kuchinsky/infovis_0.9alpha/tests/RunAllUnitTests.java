/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
import junit.framework.Test;
import junit.framework.TestSuite;

public class RunAllUnitTests {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestSuite(IdManagerTest.class));
        suite.addTest(new TestSuite(ColumnsTest.class));
        suite.addTest(new TestSuite(TableTest.class));
        suite.addTest(new TestSuite(TreeTest.class));
        suite.addTest(new TestSuite(GraphTest.class));
        suite.addTest(new TestSuite(EqualizedOrderedColorTest.class));
        suite.addTest(new TestSuite(DijkstraTest.class));
        suite.addTest(new TestSuite(PermutationTest.class));
        suite.addTest(new TestSuite(TestRBTree.class));

        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }
}
