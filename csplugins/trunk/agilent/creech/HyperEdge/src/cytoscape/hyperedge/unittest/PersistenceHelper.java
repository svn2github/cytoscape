/* -*-Java-*-
********************************************************************************
*
* File:         PersistenceHelper.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/unittest/PersistenceHelper.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri Aug 18 09:15:19 2006
* Modified:     Mon Jul 30 14:48:40 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
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
    protected String load_save_loc; // location to load and save hyperedges

    public PersistenceHelper() {
        // setup location and ensure all directories exist where tests will be
        // placed:
        // MLC 07/30/07 BEGIN:
        HyperEdgeManager heMan = HyperEdgeFactory.INSTANCE.getHyperEdgeManager();
        load_save_loc = PluginManager.getPluginManager()
                                     .getPluginManageDirectory() +
                        File.separator + HyperEdgePlugin.MY_NAME + '-' +
                        heMan.getHyperEdgeVersionNumber() + File.separator +
                        "test-results" + File.separatorChar;

        //        load_save_loc = System.getProperty("user.home") + File.separatorChar +
        //            ".hyperedge" + File.separatorChar + "test-results" +
        //            File.separatorChar;
        // MLC 07/30/07 END.
        File load_save_loc_file = new File(load_save_loc);

        if (!load_save_loc_file.exists()) {
            if (!load_save_loc_file.mkdirs()) {
                HEUtils.log("TestBase(): couldn't make directories for '" +
                            load_save_loc + "'");
            }
        }
    }

    // Tests are saved under user's home directory in '.hyperedge/test-results/'.
    protected void saveTestHelper(String file_name, CyNetwork net) {
        String full_loc = load_save_loc + file_name;

        // File full_loc_as_file = new File(full_loc);
        // CytoscapeSessionWriter sw = new CytoscapeSessionWriter(full_loc);
        try {
            // sw.writeSessionToDisk();
            FileWriter  fileWriter = new FileWriter(full_loc);
            XGMMLWriter writer = new XGMMLWriter(net,
                                                 Cytoscape.getNetworkView(net.getIdentifier()));
            writer.write(fileWriter);
            fileWriter.close();

            // SIMULATE what Cytoscape does--implementation dependent:
            Object[] ret_val = new Object[3];
            ret_val[0] = net;
            ret_val[1] = new File(full_loc).toURI();
            ret_val[2] = new Integer(Cytoscape.FILE_XGMML);
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);
        } catch (Exception e) {
            // Assert.fail("Could not write session to the file: " + full_loc +
            Assert.fail("Could not write xgmml to the file: " + full_loc + " " +
                        e.getMessage());
        }

        //	return manager.save(full_loc_as_file.toURI().toString(), gp,
        //			    HyperEdgeManager.Format.XML);
    }

    // Tests are loaded under user's home directory in '.hyperedge/test-results/'.
    protected void restoreTestHelper(String file_name) {
        String full_loc = load_save_loc + file_name;

        try {
            CyNetwork net     = Cytoscape.createNetworkFromFile(full_loc);
            Object[]  ret_val = new Object[2];
            // SIMULATE what Cytoscape does--implementation dependent:
            ret_val[0] = net;
            ret_val[1] = new File(full_loc).toURI();
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, ret_val);

            // CytoscapeSessionReader sr = new CytoscapeSessionReader(full_loc);
            // sr.read();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not open the XGMML file:" + full_loc + " " +
                        e.getMessage());
        }

        //	catch (IOException e) {
        //            e.printStackTrace();
        //            Assert.fail("Cannot open the session file:" + full_loc + " " +
        //                e.getMessage());
        //        } catch (JAXBException e) {
        //            e.printStackTrace();
        //            Assert.fail("Cannot unmarshall document." + full_loc + " " +
        //                e.getMessage());
        //        } catch (XGMMLException e) {
        //            e.printStackTrace();
        //            Assert.fail(e.getMessage());
        //        }

        // return manager.load(full_loc_as_file.toURI().toString(), gp,
        //		    HyperEdgeManager.Format.XML);
    }
}
