package cytoscape.unitTests;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import cytoscape.visual.unitTests.VisualSuite;
import cytoscape.util.unitTests.UtilSuite;
import cytoscape.data.unitTests.DataSuite;
import cytoscape.data.readers.unitTests.DataReaderSuite;
import cytoscape.data.annotation.unitTests.AnnotationSuite;
import cytoscape.data.synonyms.unitTests.SynonymSuite;
import cytoscape.undo.unitTests.UndoSuite;

/**
 * Runs all Cytoscape Unit Tests.
 * In order to run all Cytoscape Unit Tests from one central location,
 * unit tests are organized into Test Suites.  In general, each package
 * gets it own test suite.  For example, all the VizMapper Unit tests are
 * organized into the VizMapperSuite.  This class then organizes all the
 * package suites into one master test suite, and tests them all at once.
 * This makes it much easier to do regression testing with all existing
 * unit tests.
 * <P>
 * The AllTest program can be run under two modes:
 * 1.  Text (no command line arguments):  This is the default mode
 * for JUnit.  Each passing test is represented with a single dot (.).
 * 2.  GUI (set -ui as a command line argument).  This runs the graphical
 * JUnit interface.
 * <P>
 * Some developers like running unit tests from a package directory,
 * rather than from this central class.  To accommodate both approaches,
 * we use a System property named "JUNIT_TEST_ALL".  If this property is
 * set, Unit tests should do two things:
 *
 * 1.  Hide all calls to System.out.  Otherwise, we will get so many output
 * messages, that they will no longer be meaningful.  To conditionally output
 * messages to System.out, use the AllTest.standardOut() method.
 *
 * 2.  Set the correct location of files.  If a Unit test loads data from a
 * file, files are usually located relative to the package directory.  However,
 * the AllTest class will always run from the root cytoscape directory.  Unit
 * tests therefore need to check the System property, and specify file
 * locations accordingly.  To test if the "JUNIT_TEST_ALL" property is set,
 * use the runAllTests() method query.
 *
 * @author Ethan Cerami
 */
public class AllTests extends TestCase {
    public static final String TEST_ALL = "JUNIT_TEST_ALL";

    /**
     * The suite method kicks off all of the tests.
     *
     * @return junit.framework.Test
     */
    public static Test suite() {
        //  Set the JUNIT_TEST_ALL Property to TRUE.
        System.setProperty(TEST_ALL, "TRUE");

        //  Organize all suites into one master suite.
        TestSuite suite = new TestSuite();
        suite.addTest(CoreSuite.suite());
        suite.addTest(DataSuite.suite());
        suite.addTest(DataReaderSuite.suite());
        suite.addTest(AnnotationSuite.suite());
        suite.addTest(SynonymSuite.suite());
        suite.addTest(UndoSuite.suite());
        suite.addTest(UtilSuite.suite());
        suite.addTest(VisualSuite.suite());

        suite.setName("Cytoscape JUnit Tests");
        return suite;
    }

    /**
     * Runs all Cytoscape Unit Tests.
     *
     * @param args Command Line Arguments. use -ui to run the JUnit Graphical
     * interface.
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

    /**
     * Conditionally output a message to System.out.
     * If we are running All Tests, messages will not be shown.
     * Otherwise, messages will be shown.
     * @param msg Message to output.
     */
    public static void standardOut (String msg) {
        String runAllTests = System.getProperty(AllTests.TEST_ALL);
        if (runAllTests == null) {
            System.out.println(msg);
        }
    }

    /**
     * Is the JUNIT_TEST_ALL Property Set?
     * @return true or false.
     */
    public static boolean runAllTests () {
        String runAllTestProperty = System.getProperty(AllTests.TEST_ALL);
        if (runAllTestProperty == null) {
            return false;
        } else {
            return true;
        }
    }
}