
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

/*
*
* Revisions:
*
* Mon Jul 30 14:46:55 2007 (Michael L. Creech) creech@w235krbza760
*  Changed constructor to set load and save location to use the Cytoscape
*  PluginManager directory structure for placing files.
********************************************************************************
*/
package cytoscape.hyperedge.unittest;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.writers.XGMMLWriter;

import cytoscape.hyperedge.HyperEdgeFactory;
import cytoscape.hyperedge.HyperEdgeManager;
import cytoscape.hyperedge.HyperEdgePlugin;

import cytoscape.hyperedge.impl.utils.HEUtils;

import cytoscape.plugin.PluginManager;

import junit.framework.Assert;

import java.io.File;
import java.io.FileWriter;


/**
 * Help with saving and restoring HyperEdges for testing.
 */
public class PersistenceHelper {
    private String loadSaveLoc; // location to load and save hyperedges

    /**
     * setup location and ensure all directories exist where tests will be
     * placed.
     */
    public PersistenceHelper() {
        final HyperEdgeManager heMan = HyperEdgeFactory.INSTANCE.getHyperEdgeManager();
        loadSaveLoc = PluginManager.getPluginManager()
                                     .getPluginManageDirectory() +
                        File.separator + HyperEdgePlugin.MY_NAME + '-' +
                        heMan.getHyperEdgeVersionNumber() + File.separator +
                        "test-results" + File.separatorChar;
        final File loadSaveLocFile = new File(loadSaveLoc);

        if (!loadSaveLocFile.exists()) {
            if (!loadSaveLocFile.mkdirs()) {
                HEUtils.log("TestBase(): couldn't make directories for '" +
                            loadSaveLoc + "'");
            }
        }
    }

    /**
     * Simulate what Cytoscape does in saving HyperEdge information.
     * Tests are saved under user's home directory in '.hyperedge/test-results/'.
     * @param fileName the file to save HyperEdge info to.
     * @param net the CyNetwork we are saving.
     * NOTE: Cytoscape implementation dependent!
     */
    void saveTestHelper(final String fileName, final CyNetwork net) {
        final String fullLoc = loadSaveLoc + fileName;

        // File full_loc_as_file = new File(full_loc);
        // CytoscapeSessionWriter sw = new CytoscapeSessionWriter(full_loc);
        try {
            // sw.writeSessionToDisk();
            final FileWriter  fileWriter = new FileWriter(fullLoc);
            final XGMMLWriter writer = new XGMMLWriter(net,
                                                 Cytoscape.getNetworkView(net.getIdentifier()));
            writer.write(fileWriter);
            fileWriter.close();

            // SIMULATE what Cytoscape does--implementation dependent:
            final Object[] retVal = new Object[3];
            retVal[0] = net;
            retVal[1] = new File(fullLoc).toURI();
            retVal[2] = Cytoscape.FILE_XGMML;
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, retVal);
        } catch (Exception e) {
            // Assert.fail("Could not write session to the file: " + full_loc +
            Assert.fail("Could not write xgmml to the file: " + fullLoc + ' ' +
                        e.getMessage());
        }

        //	return manager.save(full_loc_as_file.toURI().toString(), gp,
        //			    HyperEdgeManager.Format.XML);
    }

    /**
     * Simulate what Cytoscape does in restoring HyperEdge information.
     * Tests are loaded under user's home directory in '.hyperedge/test-results/'.
     * NOTE: Cytoscape implementation dependent!
     * @param fileName the file to load HyperEdge info from.
     */
    void restoreTestHelper(final String fileName) {
        final String fullLoc = loadSaveLoc + fileName;

        try {
            final CyNetwork net     = Cytoscape.createNetworkFromFile(fullLoc);
            final Object[]  retVal = new Object[2];
            // SIMULATE what Cytoscape does--implementation dependent:
            retVal[0] = net;
            retVal[1] = new File(fullLoc).toURI();
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, retVal);

            // CytoscapeSessionReader sr = new CytoscapeSessionReader(full_loc);
            // sr.read();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not open the XGMML file:" + fullLoc + " " +
                        e.getMessage());
        }
    }
}
