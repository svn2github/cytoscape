// not sure this is the package this class should be in

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

package cytoscape.data.readers;

import y.base.*;
import y.view.*;
import y.io.YGFIOHandler;
import y.io.GMLIOHandler;

import cytoscape.GraphObjAttributes;
import cytoscape.data.Interaction;
import cytoscape.data.GraphProps;
/**
 * GMLWriter should be called when a GML file is saved.
 *
 * @author namin@mit.edu
 */
public class GMLWriter {
    GraphProps props;
    
    public GMLWriter(GraphProps props) {
	this.props = props;
    }

    public void write(String filename) {
	GMLIOHandler ioh = new GMLIOHandler ();
	Graph2D graph = props.getGraph();
	// Node labels should record the orf/id name.
	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    String name = props.getName(node);
	    graph.setLabelText(node, name);
	}
	// Edge labels should record the interaction type.
	for(EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    Edge edge = ec.edge();
	    String type = props.getType(edge);
	    graph.setLabelText(edge, type);
	}

        try {
          ioh.write (graph,filename);
          }
        catch (java.io.IOException e) {
          System.err.println ("error reading '" + filename + "' -- " + e.getMessage ());
          e.printStackTrace ();
          return;
          }


	// Erase the edge labels.
	for(EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    Edge edge = ec.edge();
	    graph.setLabelText(edge, null);
	}
    }
}


