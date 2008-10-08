
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

import cytoscape.hyperedge.HyperEdge;

import java.util.EventListener;


/**
 * Interface for retrieving information on the creation of a new HyperEdge
 * object.
 *
 * <P><STRONG>CAUTION: Be careful when modifying the Object passed to
 *        the method defined in this interface!  Most of the
 *        implementations that fire the events that call this method
 *        do <EM>not</EM> copy the Object passed to this method for
 *        each separate listener. Therefore, you must be very careful
 *        when modifying this object. Since the order of listener
 *        invocation is arbitrary, if you modify the object
 *        passed as a parameter in one listener, another listener may
 *        see this object in its unmodified or modified form. Also,
 *        care must be taken to avoid changes to the object that might
 *        fire unexpected and undesired change events. </STRONG>
 *
 * @author Michael L. Creech
 * @version 1.05 */
public interface NewObjectListener extends EventListener {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Called when an new HyperEdge has just been created.
     * @param hedge the newly created HyperEdge.
     * hedge should be in a well-defined state with all required
     * fields and attributes set.
     *
     * <P>Note that since this callback may be invoked while in the
     * middle of making major object changes, care must be taken in
     * examining and modifying arbitrary HyperEdges while in this
     * callback. You should always check the state any objects (using
     * isState()) you wish to examine or modify before actually
     * examining or modifying them.
     */
    void objectCreated (HyperEdge hedge);
}
