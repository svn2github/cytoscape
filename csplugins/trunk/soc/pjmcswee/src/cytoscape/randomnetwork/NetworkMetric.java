/* File: NetworkMetric.java
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

package cytoscape.randomnetwork;


/**
 *  Interface for network metrics.  Metrics compute some attribute or property of networks.
 *  New metrics can and should be added by implementing this interface.  Currenlty only 
 *  metrics which return a double are allowed for statistical processing.
 *
 *
 * @author Patrick J. McSweeney
 * @version 1.0
 */
public interface  NetworkMetric {
	
	 
	
	/**
	 *  This function actually calculates the metric.  Currently it is necessary to again specify 
	 *  whether or not pNet is directed.
	 *
	 * @param pNet The network to analyze.
	 * @param pDirected Specifices to treat pNet as directed (true) or undirected (false).
	 * @return The result of this metric on pNet.
	 */
	public abstract double analyze(RandomNetwork pNetwork, boolean pDirected);
	
	
	/**
	 * Returns the name of this metric, used for display purposes.
	 *
	 * @return The string conical name of this metric. 
	 */
	public abstract String getDisplayName();
	
	
	public abstract NetworkMetric copy();
	
}





