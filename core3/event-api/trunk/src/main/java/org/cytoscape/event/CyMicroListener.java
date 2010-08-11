
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

package org.cytoscape.event;

/**
 * The basic interface that any class interested in <b> high
 * frequency events </b> should implement - in general you 
 * should <b>NOT</b> implement this interface unless you are
 * very confident in your usage. If you have any doubt, you
 * should use the normal CyEvent/CyListener combination. At the very
 * least you should try that approach first.
 * <p> 
 * <b> Any class implementing this inteface must implement the
 * method:
 * <p> <code>public void handleMicroEvent(XYZ xyz, ...);</code>
 * <p>where XYZ can be any type of data that needs to be
 * passed from the event source to the listener.</b> 
 * <p>
 * There is no associated CyMicroEvent for CyMicroListener, instead
 * the intent is for the handleMicroEvent method to pass as arguments
 * all data that would have otherwised been passed in an event. 
 * <p>
 * The benefit of this design is that it is fast because we avoid the
 * overhead of creating an event object.  This is useful for high
 * frequency events like adding nodes to a network at load time.
 * <p>
 * However, there are two significant disadvantages to this approach
 * which mean that you should <b>only use this approach if absolutely
 * necessary!!!</b>  The first problem is that this approach is inflexible,
 * since every class that implements the CyMicroListener interface would
 * need to change, something very time consuming for implementers.  The
 * second problem is that CyMicroListeners are tightly coupled with the
 * event souce object.  
 * <p>
 * The CyEvent/CyListener white-board approach avoids both of these 
 * at the cost of being slightly more resource intensive.  Prefer the
 * CyEvent/CyListener approach for its flexibility, but use the
 * CyMicroListener when speed and efficiency are key concerns.
 */
public interface CyMicroListener {
	/**
	 * The specific object that will call the "event" in question,
	 * which is to say, the object that you are listening to.
	 * This should only be used to register/unregister CyMicroListener 
	 * services.  
	 */
	Object getEventSource();

	// implement 
	// public void handleEvent(CyEvent e);
}
