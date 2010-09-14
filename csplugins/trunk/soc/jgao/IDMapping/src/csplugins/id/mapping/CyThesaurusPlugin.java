/* 
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package csplugins.id.mapping;

import csplugins.id.mapping.service.CyThesaurusNamespace;
import csplugins.id.mapping.service.IDMappingServiceSuppport;
import csplugins.id.mapping.ui.CyThesaurusDialog;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;

import org.bridgedb.bio.BioDataSource;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.Map;

/**
 * Plugin for attribute-based ID mapping
 * 
 * 
 */
public final class CyThesaurusPlugin extends CytoscapePlugin {
    public static Map mapSrcAttrIDTypes = null;
    public static double VERSION = 1.20;

    public CyThesaurusPlugin() {
        try {
            BioDataSource.init();
            addListeners();

            IDMapperClientManager.cache();

            IDMappingServiceSuppport.addService();
            CyThesaurusNamespace.register(CyThesaurusNamespace.NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Cytoscape.getDesktop().getCyMenus().getOperationsMenu()
                        .add(new IDMappingAction());

//        Cytoscape.getDesktop().getCyMenus().getOperationsMenu()
//                        .add(new CyThesurrusServiceAttributeBasedIDMappingAction());
    }

    private void addListeners() {
        PropertyChangeSupport pcs = Cytoscape.getPropertyChangeSupport();

        pcs.addPropertyChangeListener(Cytoscape.CYTOSCAPE_INITIALIZED,
                new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
//                IDMapperClientManager.reloadFromCytoscapeGlobalProperties();
                registerDefaultClients();
                IDMapperClientManager.reCache();
                mapSrcAttrIDTypes = null;
            }
        });

        pcs.addPropertyChangeListener(Cytoscape.SESSION_SAVED,
                new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                // remove the old client but not the session properties
                IDMapperClientManager.removeAllClients(false);

                // reload the clients for this session (change the prop prefix)
                IDMapperClientManager.reloadFromCytoscapeSessionProperties();
                IDMapperClientManager.reCache();
            }
        });

        pcs.addPropertyChangeListener(Cytoscape.SESSION_LOADED,
                new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                // remove the old client include session properties
                IDMapperClientManager.removeAllClients(true);

                // reload the clients for this session
                IDMapperClientManager.reloadFromCytoscapeSessionProperties();

                if (IDMapperClientManager.countClients()==0) {
                    // load the default clients if no client
//                    IDMapperClientManager.reloadFromCytoscapeGlobalProperties();
                    registerDefaultClients();
                }

                IDMapperClientManager.reCache();

                mapSrcAttrIDTypes = null;
            }
        });

        pcs.addPropertyChangeListener(Cytoscape.PREFERENCE_MODIFIED,
                new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ((CytoscapeInit.getProperties().getProperty("defaultSpeciesName") == evt.getOldValue())
                    || (CytoscapeInit.getProperties().getProperty("defaultSpeciesName") == evt.getNewValue())) {
                    IDMapperClientManager.registerDefaultClient((String)evt.getNewValue(), (String)evt.getOldValue());
                    IDMapperClientManager.reCache();
                }
            }
        });
    }

    private void registerDefaultClients() {
        IDMapperClientManager.reloadFromCytoscapeGlobalProperties();
        if (IDMapperClientManager.countClients()==0) {
            IDMapperClientManager.registerDefaultClient();
        }
    }

    class IDMappingAction extends CytoscapeAction {

        public IDMappingAction() {
            super(FinalStaticValues.PLUGIN_NAME);
        }

        /**
         * This method is called when the user selects the menu item.
         */
        @Override
        public void actionPerformed(final ActionEvent ae) {
            NewDialogTask task = new NewDialogTask();

            final JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(Cytoscape.getDesktop());
            jTaskConfig.displayCloseButton(false);
            jTaskConfig.displayCancelButton(false);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(true);
            jTaskConfig.setMillisToPopup(100); // always pop the task

            // Execute Task in New Thread; pop open JTask Dialog Box.
            TaskManager.executeTask(task, jTaskConfig);

            final CyThesaurusDialog dialog = task.dialog();
            dialog.setVisible(true);
            //if (!dialog.isCancelled()) {
                mapSrcAttrIDTypes = dialog.getMapSrcAttrIDTypes();
            //}
        }
    }

    private class NewDialogTask implements Task {
        private TaskMonitor taskMonitor;
        private CyThesaurusDialog dialog;

        public NewDialogTask() {
        }

        /**
         * Executes Task.
         */
        //@Override
        public void run() {
                try {
                        taskMonitor.setStatus("Initializing...");
                        dialog = new CyThesaurusDialog(Cytoscape.getDesktop(), true);
                        dialog.setLocationRelativeTo(Cytoscape.getDesktop());
                        dialog.setMapSrcAttrIDTypes(mapSrcAttrIDTypes);
                        taskMonitor.setPercentCompleted(100);
                } catch (Exception e) {
                        taskMonitor.setPercentCompleted(100);
                        taskMonitor.setStatus("Failed.\n");
                        e.printStackTrace();
                }
        }

        public CyThesaurusDialog dialog() {
            return dialog;
        }


        /**
         * Halts the Task: Not Currently Implemented.
         */
        //@Override
        public void halt() {
            
        }

        /**
         * Sets the Task Monitor.
         *
         * @param taskMonitor
         *            TaskMonitor Object.
         */
        //@Override
        public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
                this.taskMonitor = taskMonitor;
        }

        /**
         * Gets the Task Title.
         *
         * @return Task Title.
         */
        //@Override
        public String getTitle() {
                return "Initializing...";
        }
    }

    private class CyThesurrusServiceAttributeBasedIDMappingAction extends CytoscapeAction {

        public static final String actionName = "Attribute ID mapping service example (See Tutorial)";
        public CyThesurrusServiceAttributeBasedIDMappingAction() {
            super(actionName); //TODO rename
        }

        /**
         * This method is called when the user selects the menu item.
         */
        @Override
        public void actionPerformed(final ActionEvent ae) {

            java.util.Set<String> srcAttrNames = new java.util.HashSet<String>();
            srcAttrNames.add("ID");
            srcAttrNames.add("EntrezGene ID");
            String tgtIDType = "uniprot_swissprot_accession"; //biomart
//            String tgtIDType = "RefSeq"; //bridgedb
            Map<String,Object> args = new java.util.HashMap<String,Object>(5);
            args.put("sourceattr", srcAttrNames);
            args.put("targettype", tgtIDType);

            cytoscape.command.CyCommandResult result = null;
            try {
                result = cytoscape.command.CyCommandManager.execute("idmapping", "attribute based mapping", args);
            } catch (cytoscape.command.CyCommandException e) {
                e.printStackTrace();
            }

            if (result.successful()) {
                System.out.println(result.getMessages());
            }

        }
    }
}