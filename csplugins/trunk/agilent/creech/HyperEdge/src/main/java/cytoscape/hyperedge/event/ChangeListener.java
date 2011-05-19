
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

package cytoscape.hyperedge.event;

import java.util.EventListener;


/**
 * Interface for determining how a HyperEdge object has changed.
 *
 * @author Michael L. Creech
 * @version 1.0
 */
public interface ChangeListener extends EventListener {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Called when an HyperEdge is modified (changed).
     * Changes include most types of modifications, such as changes to
     * the name, type, attributes, or objects associated with this
     * object (e.g., adding Edges to a HyperEdge).  Change events are
     * only triggered when an HyperEdge is in a NORMAL state,
     * not when it is being created or deleted.
     *
     * <P>Since this callback may be invoked while the system is in the
     * middle of making major object changes, care must be taken in
     * examining and modifying arbitrary HyperEdges while in this
     * callback. You should always check the state of any objects (using
     * isState()) you wish to examine or modify before actually
     * examining or modifying them.
     *
     * @param notification an object containing specific
     * information on the type of change that occurred. This object will
     * include the HyperEdge that was changed.
     *   
     * <P><STRONG>CAUTION: Be careful when modifying the HyperEdge
     *        passed within notification!  Most of the implementations
     *        that fire the events that call this method do
     *        <EM>not</EM> copy this notification, or HyperEdge
     *        passed within it, for each separate listener. Therefore,
     *        you must be very careful when modifying this
     *        object. Since the order of listener invocation is
     *        arbitrary, if you modify this HyperEdge in one
     *        listener, another listener may see this object in its
     *        unmodified or modified form. Also, care must be taken to
     *        avoid changes to the object that might fire unexpected
     *        and undesired change events. </STRONG>
     *
     * @see EventNote
     */
    void objectChanged(EventNote notification);
  }
