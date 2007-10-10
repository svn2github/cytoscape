/*
 File: OSGIPlugin.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.plugin;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import cytoscape.CyMain;
import cytoscape.Cytoscape;
import cytoscape.ServiceHandler;

public class HostActivator implements BundleActivator
{
    private BundleContext m_context = null;
    
    public void start(BundleContext context)
    {
		System.out.println("host activator start");
        m_context = context;
       
        new Thread() {
        	public void run() {
        		try {
        			String params = m_context.getProperty("cytoscape.old.params");

        			CyMain.main(params.split(" "));
					ServiceHandler sh = new ServiceHandler(m_context);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
        }.start();
    }

    public void stop(BundleContext context)
    {
		System.out.println("host activator stop");
        m_context = null;
        Cytoscape.exit(0);
    }

    public Bundle[] getBundles()
    {
        if (m_context != null)
        {
            return m_context.getBundles();
        }
        return null;
    }
}
