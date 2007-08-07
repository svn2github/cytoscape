package cytoscape.visual.mappings.continuous.unitTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ContinuousMappingTestSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestContinuousMappingReader.class);
        suite.addTestSuite(TestContinuousMappingWriter.class);
        suite.addTestSuite(TestContinuousColorRangeCalculator.class);
        suite.setName("VizMapper::Continuous Mapper Tests");
        return suite;
    }
}
