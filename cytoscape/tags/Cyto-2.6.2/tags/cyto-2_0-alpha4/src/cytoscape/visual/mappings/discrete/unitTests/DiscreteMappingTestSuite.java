//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete.unitTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DiscreteMappingTestSuite extends TestCase {

    /**
     * The suite method runs all the tests.
     * @return Suite of JUnit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestDiscreteMappingReader.class);
        suite.addTestSuite(TestDiscreteMappingWriter.class);
//        suite.addTestSuite(TestContinuousColorRangeCalculator.class);
        suite.setName("VizMapper::Discrete Mapper Tests");
        return suite;
    }
}
