package org.mskcc.test.plugin;

import junit.framework.TestCase;
import org.mskcc.biopax_plugin.plugin.BioPaxFilter;

import java.io.File;

/**
 * Tests the BioPax Import Wrapper, which integrates BioPax with the Cytoscape ImportHandler.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxImportWrapper extends TestCase {

    /**
     * Tests the BioPaxFilter class.
     */
    public void testBioPaxFilter() {
        BioPaxFilter filter = new BioPaxFilter();

        //  This should be accepted
        boolean acceptFlag = filter.accept(new File("testData/biopax_sample1.owl"));
        assertEquals (true, acceptFlag);

        //  This should be rejected
        acceptFlag = filter.accept(new File("testData/psi_sample1.xml"));
        assertEquals (false, acceptFlag);
    }
}
