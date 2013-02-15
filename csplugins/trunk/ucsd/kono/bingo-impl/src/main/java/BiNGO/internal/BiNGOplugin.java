package BiNGO.internal;

/**
 * * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: BiNGO is a Cytoscape plugin for the functional annotation of gene clusters.          
 **/


import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CyAction;
import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;


import java.util.Properties;



/**
 * *****************************************************************
 * BiNGOplugin.java Steven Maere & Karel Heymans (c) March 2005 ----------------
 * <p/>
 * Main class of the BiNGO plugin ; extends the CytoscapePlugin class from
 * Cytoscape.
 * <p/>
 * 
 * Updated by Keiichiro Ono for Cytoscape 3
 * 
 * ******************************************************************
 */

public class BiNGOplugin extends AbstractCyActivator {

	private static final long serialVersionUID = 4190390703299860130L;


	public BiNGOplugin() {
		super();
	}


	public void start(BundleContext bc) {

		CySwingApplication cytoscapeDesktopService = getService(bc,CySwingApplication.class);

		BiNGOAction binGOAction = new BiNGOAction(cytoscapeDesktopService);
		registerService(bc,binGOAction,CyAction.class, new Properties());
	}
}
