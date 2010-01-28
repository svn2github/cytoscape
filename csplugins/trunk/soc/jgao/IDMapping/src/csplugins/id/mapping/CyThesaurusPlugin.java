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

import csplugins.id.mapping.ui.CyThesaurusDialog;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
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
    static Map mapSrcAttrIDTypes = null;

    public CyThesaurusPlugin() {
        BioDataSource.init();
        IDMapperClientManager.reloadFromCytoscapeGlobalProperties();
        listenToSessionEvent();

        IDMappingServiceSuppport.addService();
        
//        try {
//            csplugins.id.mapping.command.CyThesaurusNamespace.register("idmapping");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Cytoscape.getDesktop().getCyMenus().getOperationsMenu()
                        .add(new IDMappingAction());
    }

    private void listenToSessionEvent() {
        PropertyChangeSupport pcs = Cytoscape.getPropertyChangeSupport();

        pcs.addPropertyChangeListener(Cytoscape.CYTOSCAPE_INITIALIZED,
                new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                IDMapperClientManager.reloadFromCytoscapeGlobalProperties();

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
                    IDMapperClientManager.reloadFromCytoscapeGlobalProperties();
                }

                mapSrcAttrIDTypes = null;
            }
        });
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
//            prepare(); //TODO: remove in Cytoscape3
            final CyThesaurusDialog dialog = new CyThesaurusDialog(Cytoscape.getDesktop(), true);
            dialog.setLocationRelativeTo(Cytoscape.getDesktop());
            dialog.setMapSrcAttrIDTypes(mapSrcAttrIDTypes);
            dialog.setVisible(true);
            //if (!dialog.isCancelled()) {
                mapSrcAttrIDTypes = dialog.getMapSrcAttrIDTypes();
            //}
        }
    }
}