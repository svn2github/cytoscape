/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.dataviewer.task;

import csplugins.dataviewer.mapper.MapInteractionsToGraph;
import csplugins.dataviewer.mapper.MapPsiInteractionsToGraph;
import csplugins.dataviewer.ui.ErrorDisplay;
import csplugins.task.BaseTask;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.view.CyNetworkView;
import org.mskcc.dataservices.live.interaction.ReadPsiFromFileOrWeb;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Task to Import PSI-MI Data.
 *
 * @author Ethan Cerami
 */
public class LoadPsiTask extends BaseTask {
    private URL url;
    private File file;
    private String networkTitle;
    private Component parentFrame;
    private static final String TASK_TITLE = "Retrieving PSI-MI File";

    /**
     * Constructor.
     *
     * @param url URL Location of PSI-MI File.
     */
    public LoadPsiTask(URL url) {
        super(TASK_TITLE);
        this.url = url;
    }

    /**
     * Constructor.
     *
     * @param file File Location of PSI-MI File.
     */
    public LoadPsiTask(File file) {
        super(TASK_TITLE);
        this.file = file;
    }

    /**
     * Sets Parent Frame.
     *
     * @param parentFrame Parent Frame.
     */
    public void setParentFrame(Component parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Import PSI Data.
     *
     * @throws Exception All Exceptions.
     */
    public void executeTask() throws Exception {
        try {
            ReadPsiFromFileOrWeb reader = new ReadPsiFromFileOrWeb();
            ArrayList interactions = null;

            //  Set Title, Status and Indeterminate Flag
            setIndeterminate(true);
            setProgressMessage("Retrieving PSI-MI File...");

            if (file != null) {
                interactions = reader.getInteractionsFromUrl(file.toString());
                networkTitle = file.getName();
            } else {
                interactions = reader.getInteractionsFromUrl(url.toString());
                networkTitle = "PSI-MI From Web";
            }

            setProgressMessage("Mapping to Cytoscape Network.  Please wait..");

            CyNetwork cyNetwork = Cytoscape.createNetwork(networkTitle);
            cyNetwork.setTitle(networkTitle);

            //  The two lines below are a hack, and require some explanation.
            //  When you create an empty CyNetwork object via:
            //  Cytoscape.createNetwork (String title) method, a CyNetworkView
            //  is automatically created.  That's because the code conditionally
            //  creates a network based on the number of nodes in the network.
            //  But, since this is an empty network with 0 nodes, a view is
            //  always created.  The trick to preventing a network view
            //  is to programmatically create a view directly, and then 
            //  destroy it.
            CyNetworkView networkView = Cytoscape.createNetworkView(cyNetwork);
            Cytoscape.destroyNetworkView(networkView);

            MapPsiInteractionsToGraph mapper =
                    new MapPsiInteractionsToGraph(interactions, cyNetwork,
                            MapInteractionsToGraph.MATRIX_VIEW);
            mapper.setBaseTask((BaseTask) this);
            mapper.doMapping();

            StringBuffer msg = new StringBuffer("Loaded PSI-MI File with "
                    + "a total of: " + interactions.size()
                    + " interactions.\n");
            int threshold = Integer.parseInt(CytoscapeInit.getProperties().getProperty
                    ("viewThreshold", "5000"));
            if (cyNetwork.getNodeCount() < threshold) {
                msg.append("\nYour Network is Under " + threshold
                        + " nodes --> a Cytoscape View  will be "
                        + "automatically created.");
                setProgressMessage("Creating Network View.  Please wait.");
                CyNetworkView view = Cytoscape.createNetworkView(cyNetwork);
                setProgressMessage("Applying Visual Styles.");
                Cytoscape.getVisualMappingManager().applyAppearances();
            } else {
                msg.append("\nYour Network is Over "
                        + threshold + " nodes --> a Cytoscape View  will not be "
                        + "automatically created.");
            }
            updateUser(msg.toString());
        } catch (Error e) {
            showError(e);
        } catch (Exception e) {
            showError(e);
            throw e;
        }
    }

    /**
     * Update User about what just happend.
     *
     * @param msg Message.
     */
    private void updateUser(final String msg) {
        Runnable runnable = new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parentFrame, msg);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Show Error Message.
     *
     * @param exception Exception.
     */
    private void showError(final Throwable exception) {
        Runnable runnable = new Runnable() {
            public void run() {
                ErrorDisplay errorDisplay =
                        new ErrorDisplay((JFrame) parentFrame);
                errorDisplay.displayError(exception);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }
}
