//
// CytoscapeUndoManager.java
//
// $Revision$
// $Date$
// $Author$
//


package cytoscape.undo;

import cytoscape.*;
import java.util.*;
import y.base.*;
import y.view.*;

/**
 * This class provides Cytoscape specific extensions to UndoManager.
 * These include utility functions for backing up realizers and acting
 * as a GraphListener to interpret graph events.
 */
public class CytoscapeUndoManager
    extends UndoManager implements GraphListener {

    CytoscapeWindow window;
    Graph2D graph;

    /**
     * Holds the number of POST_EVENTs that must fire before
     * onGraphEvent() will begin to listen to graph events again.
     */
    int ignoreEventDepth;

    /**
     * Holds the number of nested PRE_EVENTS when ignoreEventDepth is
     * not taken to account.
     */
    int preEventDepth;


    /**
     * Registers this object as a GraphListener to the given graph.
     */
    public CytoscapeUndoManager (CytoscapeWindow window, Graph2D graph) {
	this.window = window;
	this.graph = graph;

	ignoreEventDepth = 0;
	preEventDepth = 0;
    }

    /**
     * The state of the graph has changed.  If ignoreEventDepth is
     * non-zero, then this event will be ignored.  Otherwise, a new
     * UndoItem will be created, whose type depends on the type of the
     * given event.
     */
    public void onGraphEvent (GraphEvent e) {
	// ignore all events if we are told to ignore them
	if (ignoreEventDepth > 0) {
	    if (e.getType() == GraphEvent.PRE_EVENT)
		ignoreEventDepth++;
	    else if (e.getType() == GraphEvent.POST_EVENT)
		ignoreEventDepth--;
	}

	// otherwise process them
	else {
	    UndoItem ui = null;
	    
	    switch (e.getType()) {

	    case GraphEvent.PRE_EVENT:
		// as-of-yet unidentified pre-event signal.

		preEventDepth++;

		beginUndoItemList();
		saveState(new RealizerUndoItem(graph));

		/*
		if (itemList == null)
		    itemList = new UndoItemList();

		// temporarily save realizers because ... it might be
		// a gui selection movement event!  hooray!
		itemList.add(new RealizerUndoItem(graph));
		*/

		break;
    

	    case GraphEvent.POST_EVENT:
		// if we still want to keep the realizers

		preEventDepth--;

		endUndoItemList();

		/*
		if (itemList != null) {
		    saveState(itemList);

		    if (preEventDepth == 0)
			itemList = null;
		}
		*/

		break;


	    case GraphEvent.EDGE_CREATION:
	    case GraphEvent.EDGE_REINSERTION:
		saveState(new EdgeCreationUndoItem(graph, (Edge)e.getData()));
		/*
		ui = new EdgeCreationUndoItem(graph, (Edge)e.getData());

		if (itemList != null)
		    itemList.add(ui);
		else
		    saveState(ui);
		*/
		
		break;
		
	    case GraphEvent.EDGE_REVERSAL:
		saveState(new EdgeReversalUndoItem(graph, (Edge)e.getData()));
		/*
		ui = new EdgeReversalUndoItem(graph, (Edge)e.getData());

		if (itemList != null)
		    itemList.add(ui);
		else
		    saveState(ui);
		*/
		
		break;
		
	    case GraphEvent.NODE_CREATION:
	    case GraphEvent.NODE_REINSERTION:
		saveState(new NodeCreationUndoItem(graph, (Node)e.getData()));
		/*
		ui = new NodeCreationUndoItem(graph, (Node)e.getData());

		if (itemList != null)
		    itemList.add(ui);
		else
		    saveState(ui);
		*/
		
		break;
		
	    case GraphEvent.PRE_EDGE_REMOVAL:
		saveState(new EdgeRemovalUndoItem(graph, (Edge)e.getData()));
		/*
		ui = new EdgeRemovalUndoItem(graph, (Edge)e.getData());

		if (itemList != null)
		    itemList.add(ui);
		else
		    saveState(ui);
		*/
		
		break;
		
	    case GraphEvent.PRE_NODE_REMOVAL:
		saveState(new NodeRemovalUndoItem(graph, (Node)e.getData()));
		/*
		ui = new NodeRemovalUndoItem(graph, (Node)e.getData());

		if (itemList != null)
		    itemList.add(ui);
		else
		    saveState(ui);
		*/
		
		break;

	    default:
		// otherwise, it is unsupported, so forget the history
		// clearHistory();
		break;
	    }

	    window.updateUndoRedoMenuItemStatus();
	}
    }

    /**
     * Saves the current realizers using a RealizerUndoItem
     */
    public void saveRealizerState() {
	saveState(new RealizerUndoItem(graph));
	window.updateUndoRedoMenuItemStatus();
    }

    /**
     * Informs the undo manager to ignore graph events until told to
     * resume listening.
     */
    public void pause() {
	ignoreEventDepth++;
	graph.firePreEvent();
	super.pause();
    }

    /**
     * Informs the manager to resume listening to graph events.
     */
    public void resume() {
	graph.firePostEvent();
	ignoreEventDepth--;
	super.resume();
    }
}
