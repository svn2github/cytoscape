// UndoableGraphHiderTest.java

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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.undo.unitTests;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import y.base.*;
import y.view.Graph2D;

import cytoscape.undo.*;
//----------------------------------------------------------------------------
public class UndoableGraphHiderTest extends TestCase {

//----------------------------------------------------------------------------
    public UndoableGraphHiderTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testFunction () throws Exception {
        Graph2D graph = new Graph2D();
        Node n0 = graph.createNode();
        Node n1 = graph.createNode();
        Node n2 = graph.createNode();
        Edge e1 = graph.createEdge(n0, n1);
        Edge e2 = graph.createEdge(n1, n2);
        
        UndoManager undoManager = new EmptyUndoManager(null, graph);
        UndoableGraphHider hider = new UndoableGraphHider(graph, undoManager);
        
        Set edgeSet = hider.hide(n0);
        assert( edgeSet.size() == 1 );
        assert( ((Edge)edgeSet.iterator().next()) == e1 );
        assert( graph.nodeCount() == 2 );
        assert( graph.edgeCount() == 1 );
        hider.unhide(n0);
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 1 ); //hidden edge is not automatically unhid
        hider.unhide(e1);
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        
        hider.hide(e2);
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 1 );
        hider.unhide(e2);
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        
        Set allEdges = hider.hideNodes();
        assert( allEdges.size() == 2 );
        assert( graph.isEmpty() );
        hider.unhideAll();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        hider.hideEdges();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 0 );
        hider.unhideEdges();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        
        NodeList nl = new NodeList();
        nl.add(n0);
        nl.add(n1);
        edgeSet = hider.hide(nl);
        assert( edgeSet.size() == 2 );
        assert( graph.nodeCount() == 1 );
        assert( graph.edgeCount() == 0 );
        hider.unhideAll();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        edgeSet = hider.hide( nl.nodes() );
        assert( edgeSet.size() == 2 );
        assert( graph.nodeCount() == 1 );
        assert( graph.edgeCount() == 0 );
        hider.unhideAll();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        EdgeList el = new EdgeList();
        el.add(e2);
        hider.hide(el);
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 1 );
        hider.unhideAll();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        hider.hide( el.edges() );
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 1 );
        hider.unhideAll();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        
        Edge es = graph.createEdge(n2, n2);
        assert( graph.edgeCount() == 3 );
        hider.hideSelfLoops();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 2 );
        hider.unhideAll();
        assert( graph.nodeCount() == 3 );
        assert( graph.edgeCount() == 3 );
    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (UndoableGraphHiderTest.class));
    }
//----------------------------------------------------------------------------
}


