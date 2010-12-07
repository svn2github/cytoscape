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

import java.util.List;

import org.cytoscape.property.session.Cysession;
import org.cytoscape.property.session.Cytopanel;
import org.cytoscape.property.session.Cytopanels;
import org.cytoscape.property.session.Desktop;
import org.cytoscape.property.session.Network;
import org.cytoscape.property.session.NetworkTree;
import org.cytoscape.property.session.ObjectFactory;
import org.cytoscape.property.session.OntologyServer;
import org.cytoscape.property.session.Server;
import org.cytoscape.property.session.SessionState;


public class CysessionFactory {
	
	private static final String DEFAULT_SESSION_NOTE = "You can add note for this session here.";
	
	private final ObjectFactory factory;
	
	public CysessionFactory() {
		this.factory = new ObjectFactory();
	}
	
	public Cysession createDefaultCysession() {
		return createCysession(null, null, null);
	}

	/**
	 * Create a Cysession object from the current Cytoscape session attributes.
	 */
	public Cysession createCysession(Desktop desktop, List<Cytopanel> cytopanels, String note) {
		Cysession session = null;
		
		// Initialize objects for the marshaller:
		session = factory.createCysession();
		
		if (note == null) note = DEFAULT_SESSION_NOTE;
		session.setSessionNote(note);

		NetworkTree tree = factory.createNetworkTree();
		
		SessionState sState = factory.createSessionState();
		
		if (desktop == null) desktop = factory.createDesktop();
		sState.setDesktop(desktop);
		
		session.setSessionState(sState);
		Cytopanels cps = getCytoPanelStates(cytopanels);
		
		// TODO?
		List<Network> netList = tree.getNetwork();
		
		sState.setCytopanels(cps);
		sState.setServer(getServerState());
		
		return session;
	}
	
	/**
	 * Check loaded ontologies and save those states in cysession.xml.
	 * @return Server object
	 */
	private Server getServerState() {
		Server server = factory.createServer();
		OntologyServer os = factory.createOntologyServer();

		// We've omitted the concept of Ontology from the core api in 3.0,
		// so that code will exist in the future as a plugin (although with a public API)
//		Set<String> ontoNames = Cytoscape.getOntologyServer().getOntologyNames();
//		Map<String, URL> sources = Cytoscape.getOntologyServer().getOntologySources();
//
//		for (String name : ontoNames) {
//			Ontology onto = factory.createOntology();
//			onto.setName(name);
//			onto.setHref(sources.get(name).toString());
//			os.getOntology().add(onto);
//		}

		server.setOntologyServer(os);

		return server;
	}
	
	/**
	 * Extract states of the 3 Cytopanels.
	 *
	 * @return
	 * Note: We will store the states of plugins near future. The location of
	 * those states will be stored here.
	 */
	private Cytopanels getCytoPanelStates(List<Cytopanel> cytopanels) {
		Cytopanels cps = factory.createCytopanels();
		List<Cytopanel> cytoPanelList = cps.getCytopanel();
		
		if (cytopanels != null) {
			for (Cytopanel panel : cytopanels) cytoPanelList.add(panel);
		}
// TODO: cleanup. This should be done by the swing related plugin
//		String[] cytopanelStates = new String[CYTOPANEL_COUNT + 1];
//		int[] selectedPanels = new int[CYTOPANEL_COUNT + 1];
//
//		// Extract states of 3 panels.
//		cytopanelStates[1] = application.getCytoPanel(CytoPanelName.WEST).getState().toString();
//		selectedPanels[1] = application.getCytoPanel(CytoPanelName.WEST).getSelectedIndex();
//		cytopanelStates[2] = application.getCytoPanel(CytoPanelName.SOUTH).getState().toString();
//		selectedPanels[2] = application.getCytoPanel(CytoPanelName.SOUTH).getSelectedIndex();
//		cytopanelStates[3] = application.getCytoPanel(CytoPanelName.EAST).getState().toString();
//		selectedPanels[3] = application.getCytoPanel(CytoPanelName.EAST).getSelectedIndex();
//
//		// Number of Cytopanels. Currently, we have 3 panels.
//		final int CYTOPANEL_COUNT = 3;
		
//		for (int i = 1; i < (CYTOPANEL_COUNT + 1); i++) {
//			Panels internalPanels = factory.createPanels();
//			List<Panel> iPanelList = internalPanels.getPanel();
//			Panel iPanel = factory.createPanel();
//			iPanel.setId("test");
//
//			iPanelList.add(iPanel);
//
//			Cytopanel curCp = factory.createCytopanel();
//			curCp.setId("CytoPanel" + i);
//			curCp.setPanelState(cytopanelStates[i]);
//			curCp.setSelectedPanel(Integer.toString(selectedPanels[i]));
//			curCp.setPanels(internalPanels);
//			cytoPanelList.add(curCp);
//		}

		return cps;
	}

// TODO: cleanup. This should be done by the swing related plugin
//	private void setDesktopStates(SessionState sState) {
//		DesktopSize dSize = factory.createDesktopSize();
//		NetworkFrames frames = factory.createNetworkFrames();
//
//		// TODO
//		Component[] networkFrames = Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane().getComponents();
//
//		for (int i = 0; i < networkFrames.length; i++) {
//			if(networkFrames[i] instanceof JInternalFrame) {
//				JInternalFrame networkFrame = (JInternalFrame) networkFrames[i];
//				NetworkFrame frame = factory.createNetworkFrame();
//				frame.setFrameID(networkFrame.getTitle());
//				frame.setWidth(BigInteger.valueOf(networkFrame.getWidth()));
//				frame.setHeight(BigInteger.valueOf(networkFrame.getHeight()));
//				frame.setX(BigInteger.valueOf(networkFrame.getX()));
//				frame.setY(BigInteger.valueOf(networkFrame.getY()));
//				frames.getNetworkFrame().add(frame);
//			}
//		}
//
//		dSize.setHeight(BigInteger.valueOf(application.getJFrame().getSize().height));
//		dSize.setWidth(BigInteger.valueOf(application.getJFrame().getSize().width));
//
//		Desktop desktop = factory.createDesktop();
//		desktop.setDesktopSize(dSize);
//		desktop.setNetworkFrames(frames);
//		sState.setDesktop(desktop);
//	}
}
