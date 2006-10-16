package org.mskcc.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.mskcc.test.biopax.TestBioPaxEntityParser;
import org.mskcc.test.biopax.TestBioPaxUtil;
import org.mskcc.test.biopax.TestBioPaxFileChecker;
import org.mskcc.test.mapper.TestBioPaxToCytoscapeMapper;
import org.mskcc.test.plugin.TestBioPaxImportWrapper;
import org.mskcc.test.rdf.TestRdfQuery;
import org.mskcc.test.util.TestExternalLinkUtil;
import org.mskcc.test.util.TestWebFileConnect;

/**
 * Runs all Unit Tests for the BioPAX Plugin.
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
        suite.addTestSuite(TestBioPaxEntityParser.class);
        suite.addTestSuite(TestBioPaxFileChecker.class);
        suite.addTestSuite(TestBioPaxUtil.class);
        suite.addTestSuite(TestBioPaxToCytoscapeMapper.class);
        suite.addTestSuite(TestBioPaxImportWrapper.class);
        suite.addTestSuite(TestRdfQuery.class);
        suite.addTestSuite(TestExternalLinkUtil.class);
        suite.addTestSuite(TestWebFileConnect.class);
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