
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

package org.cytoscape.cmdline.launcher.internal;

import java.util.Hashtable;

import org.cytoscape.cmdline.launcher.CommandLineProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


/**
 * Manager that provides the commandline arguments to other <i>OSGi bundles</i> for parsing and execution of <code>TaskFactories</code>
 * 
 * @author pasteur
 *
 */
public class CommandLineProviderImpl implements CommandLineProvider, BundleActivator {

	/**
	 * commandline arguments
	 */
	private String[] args;
	
	/**
	 * OSGi ServiceRegistry to register <code>CommandLineProviderImpl</code> as a service
	 */
	private ServiceRegistration reg;
	
	/**
	 * specific commandline arguments for each detected <code>TaskFactory</code>
	 */
	private String[] specificArgs;
	
	/**
	 * The commandline provider
	 * 
	 * @param args commandline arguments
	 */
	public CommandLineProviderImpl(String[] args) {
		if ( args == null )
			this.args = new String[0];
		else
			this.args = args;
	}

	
	/**
	 * Method executed when the bundle is started : it registers <code>CommandLineProvider</code> as a service
	 */
	public void start(BundleContext bc) {
		reg = bc.registerService(CommandLineProvider.class.getName(),this,new Hashtable());
	}

	/**
	 * Method executed when the bundle is stopped : it deletes <code>CommandLineProvider</code> from the ServiceRegistry
	 */
	public void stop(BundleContext bc) {
		if ( reg != null )
			reg.unregister();
	}
	
	
	/**
	 * To get the commandline arguments
	 * 
	 * @return the commandline arguments
	 */
	public String[] getCommandLineCompleteArgs() {
		String[] ret = new String[args.length];
		System.arraycopy(args,0,ret,0,args.length);
		return ret;
	}
		
	/**
	 * To set the specific arguments of each <code>TaskFactory</code>
	 * 
	 * @param the specific arguments
	 */
	public void setSpecificArgs(String[] arg){
		this.specificArgs = arg;
	}
	
	/**
	 * To get the specific arguments of a choosen <code>TaskFactory</code>
	 * 
	 * @return the specific arguments of a <code>TaskFactory</code>
	 */
	public String[] getSpecificArgs(){
		String[] ret = new String[specificArgs.length];
		System.arraycopy(specificArgs,0,ret,0,specificArgs.length);
		return ret;
	}
	
	
	
}


