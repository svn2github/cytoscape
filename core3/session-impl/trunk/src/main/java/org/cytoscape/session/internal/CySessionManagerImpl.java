/*
  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.session.internal;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.session.Cysession;
import org.cytoscape.property.session.NetworkFrame;
import org.cytoscape.session.CySession;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.session.events.SessionAboutToBeSavedEvent;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link org.cytoscape.session.CySessionManager}.
 * 
 * @author Christian Lopes
 */
public class CySessionManagerImpl implements CySessionManager {

    private String currentFileName;
    private CySession currentSession;

    private final CyEventHelper cyEventHelper;
    private final CyNetworkManager netMgr;
    private final CyTableManager tblMgr;
    private final VisualMappingManager vmMgr;
    private final VisualStyleSerializer vsSer;
    private final CyNetworkViewManager nvMgr;

    private final CyProperty<Properties> properties;
    private final CyProperty<Bookmarks> bookmarks;

    private static final Logger logger = LoggerFactory.getLogger(CySessionManagerImpl.class);

    public CySessionManagerImpl(CyEventHelper cyEventHelper,
                                CyNetworkManager netMgr,
                                CyTableManager tblMgr,
                                VisualMappingManager vmMgr,
                                VisualStyleSerializer vsSer,
                                CyNetworkViewManager nvMgr,
                                CyProperty<Properties> props,
                                CyProperty<Bookmarks> bkmarks) {
        this.cyEventHelper = cyEventHelper;
        this.netMgr = netMgr;
        this.tblMgr = tblMgr;
        this.vmMgr = vmMgr;
        this.vsSer = vsSer;
        this.nvMgr = nvMgr;
        this.properties = props;
        this.bookmarks = bkmarks;
    }

    public CySession getCurrentSession() {
        // Plugins who want to save anything to a session will have to listen
        // for this event
        // and will then be responsible for adding files through
        // SessionAboutToBeSavedEvent.addPluginFiles(..)
        SessionAboutToBeSavedEvent savingEvent = new SessionAboutToBeSavedEvent(this);
        cyEventHelper.fireSynchronousEvent(savingEvent);

        CysessionFactory cysessFactory = new CysessionFactory();
        Cysession cysess = cysessFactory.createCysession(savingEvent.getDesktop(), savingEvent.getCytopanels(), null);

        Map<String, List<File>> pluginMap = savingEvent.getPluginFileListMap();

        Set<CyTable> tables = tblMgr.getAllTables(true);
        Set<CyNetworkView> netViews = nvMgr.getNetworkViewSet();

        Set<VisualStyle> allStyles = vmMgr.getAllVisualStyles();
        Properties vmProps = vsSer.createProperties(allStyles);

        Map<CyNetworkView, String> stylesMap = new HashMap<CyNetworkView, String>();

        if (netViews != null) {
            for (CyNetworkView nv : netViews) {
                VisualStyle style = vmMgr.getVisualStyle(nv);

                if (style != null) {
                    logger.debug("    NetView=" + nv + " :: Style=" + style.getTitle());
                    stylesMap.put(nv, style.getTitle());
                }
            }
        }

        Properties props = properties != null ? properties.getProperties() : null;
        Bookmarks bkmarks = bookmarks != null ? bookmarks.getProperties() : null;

        CySession sess = new CySession.Builder().cytoscapeProperties(props).bookmarks(bkmarks).cysession(cysess)
                .pluginFileListMap(pluginMap).tables(tables).networkViews(netViews).vizmapProperties(vmProps)
                .viewVisualStyleMap(stylesMap).build();

        return sess;
    }

    public void setCurrentSession(CySession sess, String fileName) {
        boolean emptySession = sess == null;

        // Always remove the current session first
        disposeCurrentSession(!emptySession);

        if (emptySession) {
            logger.debug("Creating empty session...");
            Set<VisualStyle> allStyles = vmMgr.getAllVisualStyles();
            Properties vmProps = vsSer.createProperties(allStyles);
            Cysession cysess = new CysessionFactory().createDefaultCysession();

            // TODO: set default properties again
            Properties props = properties != null ? properties.getProperties() : new Properties();
            // TODO: set default bookmarks again
            Bookmarks bkmarks = bookmarks != null ? bookmarks.getProperties() : new Bookmarks();

            sess = new CySession.Builder().cytoscapeProperties(props).bookmarks(bkmarks).cysession(cysess)
                    .vizmapProperties(vmProps).build();
        } else {
            logger.debug("Restoring the session...");

            // Restore tables
            // ------------------------------------------------------------------------------
            // TODO: add tables that are not associated with networks
            // logger.debug("Restoring unattached tables...");
            // Set<CyTable> tables = sess.getTables();
            //			
            // for (CyTable tbl : tables) {
            // CyTableFactory.createTable();
            // }

            // Restore networks
            logger.debug("Restoring networks...");
            Set<CyNetworkView> netViews = sess.getNetworkViews();

            for (CyNetworkView nv : netViews) {
                netMgr.addNetwork(nv.getModel());
                nvMgr.addNetworkView(nv);
            }

            // Restore visual styles
            logger.debug("Restoring visual styles...");
            Properties stylesProps = sess.getVizmapProperties();
            Collection<VisualStyle> allStyles = vsSer.createVisualStyles(stylesProps);
            Map<String, VisualStyle> stylesMap = new HashMap<String, VisualStyle>();

            if (allStyles != null) {
                for (VisualStyle vs : allStyles) {
                    vmMgr.addVisualStyle(vs);
                    stylesMap.put(vs.getTitle(), vs);
                    // TODO: what if a style with the same name already exits?
                }
            }

            // Get network frames info
            Cysession cysess = sess.getCysession();

            if (cysess.getSessionState().getDesktop().getNetworkFrames() != null) {
                List<NetworkFrame> frames = cysess.getSessionState().getDesktop().getNetworkFrames().getNetworkFrame();
                Map<String, NetworkFrame> framesLookup = new Hashtable<String, NetworkFrame>();

                for (NetworkFrame nf : frames) {
                    framesLookup.put(nf.getFrameID(), nf);
                }

                // Set visual styles to network views
                Map<CyNetworkView, String> netStyleMap = sess.getViewVisualStyleMap();

                for (Entry<CyNetworkView, String> entry : netStyleMap.entrySet()) {
                    CyNetworkView netView = entry.getKey();
                    String stName = entry.getValue();
                    VisualStyle vs = stylesMap.get(stName);

                    if (vs != null) {
                        vmMgr.setVisualStyle(vs, netView);
                        vs.apply(netView);
                    }

                    // Set network width/height
                    String name = netView.getModel().getCyRow().get(CyNetwork.NAME, String.class);

                    if (name != null && name.length() > 0) {
                        NetworkFrame nf = framesLookup.get(name);

                        if (nf != null) {
                            BigInteger w = nf.getWidth();
                            BigInteger h = nf.getHeight();

                            if (w != null)
                                netView.setVisualProperty(MinimalVisualLexicon.NETWORK_WIDTH, w.doubleValue());
                            if (h != null)
                                netView.setVisualProperty(MinimalVisualLexicon.NETWORK_HEIGHT, h.doubleValue());
                        }
                    }

                    netView.updateView();
                }
            }
        }

        currentSession = sess;
        currentFileName = fileName;

        cyEventHelper.fireSynchronousEvent(new SessionLoadedEvent(this, currentSession, getCurrentSessionFileName()));
    }

    public String getCurrentSessionFileName() {
        return currentFileName;
    }

    private void disposeCurrentSession(boolean removeVisualStyles) {
        logger.debug("Disposing current session...");

        // Destroy network views and models
        Set<CyNetworkView> netViews = nvMgr.getNetworkViewSet();

        for (CyNetworkView nv : netViews) {
            nvMgr.destroyNetworkView(nv);
            netMgr.destroyNetwork(nv.getModel());
        }

        // Destroy styles
        if (removeVisualStyles) {
            logger.debug("Removing current visual styles...");
            VisualStyle defaultStyle = vmMgr.getDefaultVisualStyle();
            List<VisualStyle> allStyles = new ArrayList<VisualStyle>(vmMgr.getAllVisualStyles());

            for (int i = 0; i < allStyles.size(); i++) {
                VisualStyle vs = allStyles.get(i);

                if (!vs.equals(defaultStyle)) {
                    vmMgr.removeVisualStyle(vs);
                }
            }
        }

        // TODO: destroy unattached tables--how?
    }
}
