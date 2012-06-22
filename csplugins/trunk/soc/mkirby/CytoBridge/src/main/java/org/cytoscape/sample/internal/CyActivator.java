package org.cytoscape.sample.internal;

/*
 Copyright (c) 2010 Delft University of Technology (www.tudelft.nl)

 This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; either version 3.0 of the License, or
any later version.

 This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
documentation provided hereunder is on an "as is" basis, and the  Delft
University of Technology has no obligations to provide  maintenance,
support, updates, enhancements or modifications.
 In no event shall the Delft University of Technology  be liable to any
party for direct, indirect, special,  incidental or consequential
damages, including lost profits, arising  out of the use of this
software and its documentation, even if the  Delft University of
Technology have been advised of the possibility  of such damage. See the
GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
License  along with this library; if not, write to the Free Software
Foundation,  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
USA.
*/

import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;


import java.util.Properties;

/**
 * CytoscapeRPC plugin.
 * A Cytoscape plugin which allows users to manipulate and query networks
 * through an XML-RPC interface.
 * @author Jan Bot
 * Delft Bioinformatics Lab
 * Delft University of Technology
 */
public class CyActivator extends AbstractCyActivator{

    public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {
		CySwingApplication cytoscapeDesktopService = getService(bc,CySwingApplication.class);
		CytoBridgeAction cytoBridgeAction = new CytoBridgeAction(cytoscapeDesktopService);
		registerService(bc,cytoBridgeAction,CyAction.class, new Properties());
	}
}
