//

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
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
 
// UndoableGraphHider.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import java.util.*;

import y.base.*;

import cytoscape.undo.*;

public class UndoableGraphHider {
    
    Graph graph;
    UndoManager undoManager;
    Set hiddenNodes = new HashSet();
    Set hiddenEdges = new HashSet();
    
    public UndoableGraphHider(Graph graph, UndoManager undoManager) {
        this.graph = graph;
        this.undoManager = undoManager;
    }
    
    public Graph getGraph() {return graph;}
    
    public Set hide(Node n) {
        Set returnSet = new HashSet();
        if (!graph.contains(n)) {return returnSet;}
        hiddenNodes.add(n);
        Set edgeSet = new HashSet();
        for (EdgeCursor ec = n.edges() ; ec.ok(); ec.next()) {
            Edge e = ec.edge();
            hiddenEdges.add(e);
            edgeSet.add(e);
            returnSet.add(e);
        }
        
        undoManager.saveState( new NodeHiddenUndoItem(this, n, edgeSet) );
        undoManager.pause();
        graph.hide(n); //also removes all adjacent edges
        undoManager.resume();
        
        return returnSet; //return a copy of the hidden edges container
    }
    
    protected void undoHide(Node n) {
        if (!hiddenNodes.contains(n)) {return;}
        //undo object is responsible for unhiding the edges
        graph.unhide(n);
        hiddenNodes.remove(n);
    }
    protected void redoHide(Node n) {
        if (!graph.contains(n)) {return;}
        //save neighboring edges as hidden
        for (EdgeCursor ec = n.edges(); ec.ok(); ec.next()) {
            Edge e = ec.edge();
            hiddenEdges.add(e);
        }
        //now hide the node, which automatically hides all adjacent edges
        graph.hide(n);
        hiddenNodes.add(n);
    }
    
    public void unhide(Node n) {
        if (!hiddenNodes.contains(n)) {return;}
        undoManager.saveState( new NodeShownUndoItem(this, n) );
        undoManager.pause();
        graph.unhide(n);
        hiddenNodes.remove(n);
        undoManager.resume();
    }
    
    protected void undoUnhide(Node n) {
        if (!graph.contains(n)) {return;}
        //save neighboring edges as hidden
        for (EdgeCursor ec = n.edges(); ec.ok(); ec.next()) {
            Edge e = ec.edge();
            hiddenEdges.add(e);
        }
        //now hide the node, which automatically hides all adjacent edges
        graph.hide(n);
        hiddenNodes.add(n);
    }
    protected void redoUnhide(Node n) {
        if (!hiddenNodes.contains(n)) {return;}
        graph.unhide(n);
        hiddenNodes.remove(n);
    }
    
    public void hide(Edge e) {
        if (!graph.contains(e)) {return;}
        undoManager.saveState( new EdgeHiddenUndoItem(this, e) );
        undoManager.pause();
        graph.hide(e);
	hiddenEdges.add(e);
	undoManager.resume();
    }
    
    protected void undoHide(Edge e) {
        if (!hiddenEdges.contains(e)) {return;}
        graph.unhide(e);
        hiddenEdges.remove(e);
    }
    protected void redoHide(Edge e) {
        if (!graph.contains(e)) {return;}
        graph.hide(e);
        hiddenEdges.add(e);
    }
    
    public void unhide(Edge e) {
        if (!hiddenEdges.contains(e)) {return;}
        undoManager.saveState( new EdgeShownUndoItem(this, e) );
	undoManager.pause();
	graph.unhide(e);
	hiddenEdges.remove(e);
	undoManager.resume();
    }
    
    protected void undoUnhide(Edge e) {
        if (!graph.contains(e)) {return;}
        graph.hide(e);
        hiddenEdges.add(e);
    }
    protected void redoUnhide(Edge e) {
        if (!hiddenEdges.contains(e)) {return;}
        graph.unhide(e);
        hiddenEdges.remove(e);
    }
    
    
    public void hideSelfLoops() {
        for (EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    Edge e = ec.edge();
	    if ( e.source().equals(e.target()) ) this.hide(e);
	}
    }
    
    public Set hide(NodeCursor nc) {
        graph.firePreEvent();
        Set returnSet = new HashSet();
        for (nc.toFirst(); nc.ok(); nc.next()) {
            returnSet.addAll( this.hide(nc.node()) );
        }
        graph.firePostEvent();
        return returnSet;
    }
    
    public Set hide(NodeList nl) {
        return hide( nl.nodes() );
    }
    
    public Set hideNodes() {return this.hide( graph.nodes() );}
    
    public void unhideNodes() {
        graph.firePreEvent();
        //need a copy of the set to prevent concurrent modification exceptions
        Set unhideSet = new HashSet(hiddenNodes);
        for (Iterator si = unhideSet.iterator(); si.hasNext(); ) {
            this.unhide( (Node)si.next() );
        }
        graph.firePostEvent();
    }
    
    public void hide(EdgeCursor ec) {
        graph.firePreEvent();
        for (ec.toFirst(); ec.ok(); ec.next()) {
            this.hide(ec.edge());
        }
        graph.firePostEvent();
    }
    
    public void hide(EdgeList el) {
        hide( el.edges() );
    }
    
    public void hideEdges() {this.hide( graph.edges() );}
    
    public void unhideEdges() {
        graph.firePreEvent();
        //need a copy of the set to prevent concurrent modification exceptions
        Set unhideSet = new HashSet(hiddenEdges);
        for (Iterator si = unhideSet.iterator(); si.hasNext(); ) {
            this.unhide( (Edge)si.next() );
        }
        graph.firePostEvent();
    }
    
    public void unhideAll() {
        graph.firePreEvent();
        //must copy the sets to avoid concurrent modification exceptions
        Set theNodes = new HashSet(hiddenNodes);
        for (Iterator si = theNodes.iterator(); si.hasNext(); ) {
            this.unhide( (Node)si.next() );
        }
        Set theEdges = new HashSet(hiddenEdges);
        for (Iterator si = theEdges.iterator(); si.hasNext(); ) {
            this.unhide( (Edge)si.next() );
        }
        hiddenNodes.clear();
        hiddenEdges.clear();
        graph.firePostEvent();
    }
}

