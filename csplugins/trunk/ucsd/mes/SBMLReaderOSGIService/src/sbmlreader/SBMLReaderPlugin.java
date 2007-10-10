
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

package sbmlreader;

import cytoscape.util.CyFileFilter;
import cytoscape.util.GraphFileFilter;


import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import java.util.Hashtable; 

/**
 This plugin will allow the reading of an SBML level 2 file.
 *
 *W.P.A. Ligtenberg, Eindhoven University of Technology
 */
public class SBMLReaderPlugin implements BundleActivator {
	/**
	 * This constructor creates an action and adds it to the Plugins menu.
	 */
	 ServiceRegistration filterReg;
	 //ServiceRegistration visualStyleReg;
	public void start(BundleContext bc) {
		//                  just an interface, implementation of interface, metadata
		filterReg = bc.registerService(GraphFileFilter.class.getName(), new SBMLFilter(), new Hashtable());
	//	visualStyleReg = bc.registerService(VisualStyle.class.getName(), new SBMLVisualStyle(), new Hashtable());
	}

	public void stop(BundleContext bc) {
	 	filterReg.unregister();	
	//	visualStyleReg.unregister();
	}

}
