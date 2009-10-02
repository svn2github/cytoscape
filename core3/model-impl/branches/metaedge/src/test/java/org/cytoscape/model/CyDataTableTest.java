/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.model;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.internal.CyEventHelperImpl;
import org.cytoscape.model.internal.CyDataTableImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.mock.MockBundleContext;
import org.springframework.osgi.mock.MockServiceReference;

/**
 * DOCUMENT ME!
 */
public class CyDataTableTest extends AbstractCyDataTableTest {

	private ServiceReference reference;
	private BundleContext bundleContext;
	private Object service;
	
	 
	/**
	 * DOCUMENT ME!
	 */
	public void setUp() {
		
		reference = new MockServiceReference();
		
		bundleContext = new MockBundleContext() {

			public ServiceReference getServiceReference(String clazz) {
				return reference;
			}

			public ServiceReference[] getServiceReferences(String clazz, String filter) 
					throws InvalidSyntaxException {
				return new ServiceReference[] { reference };
			}
			
			public Object getService(ServiceReference ref) {
			    if (reference == ref)
			       return service;
			    
			    return null;
			}
		};
		CyEventHelper helper = new CyEventHelperImpl(bundleContext);
		mgr = new CyDataTableImpl(null, "homer", true, helper);
		attrs = mgr.getRow(1);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void tearDown() {
		mgr = null;
		attrs = null;
	}
}
