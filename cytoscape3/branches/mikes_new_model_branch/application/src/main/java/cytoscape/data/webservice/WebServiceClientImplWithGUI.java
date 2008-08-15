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
package cytoscape.data.webservice;

import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.data.webservice.ui.WebServiceClientGUI;
import org.cytoscape.tunable.ModuleProperties;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.NodeView;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Client with GUI.
 *
 * @param <S>  Client stub type.
 * @param <U>  User interface type.
 */
public abstract class WebServiceClientImplWithGUI<S, U extends Container>
    extends WebServiceClientImpl<S> implements WebServiceClientGUI<U> {
	// GUI for this client.  This is optional and default is null.
	protected U gui = null;

	/**
	 * Creates a new WebServiceClientImpl object.
	 *
	 * @param serviceName  DOCUMENT ME!
	 * @param displayName  DOCUMENT ME!
	 * @param types  DOCUMENT ME!
	 * @param props  DOCUMENT ME!
	 */
	public WebServiceClientImplWithGUI(final String serviceName, final String displayName,
	                                   final ClientType[] types, final ModuleProperties props,
	                                   final S clientStub, final U gui) {
		super(serviceName, displayName, types, props, clientStub);
		this.gui = gui;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public U getGUI() {
		return gui;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param gui DOCUMENT ME!
	 */
	public void setGUI(U gui) {
		this.gui = gui;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param i DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon(WebServiceClientGUI.IconSize i) {
		return null;
	}

	/**
	 *  Returns client dependent context menu for nodes
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<JMenuItem> getNodeContextMenuItems(NodeView nv) {
		return null;
	}
	
	/**
	 *  Returns client dependent context menu for edges
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<JMenuItem> getEdgeContextMenuItems(EdgeView ev) {
		return null;
	}
}
