// ShrinkExpandGraphUI 
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.dialogs;
//--------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;

import y.base.*;
import y.view.*;
import y.algo.*;

import cytoscape.data.*;
import cytoscape.dialogs.ShrinkExpandGraph;

//--------------------------------------------------------------------------
public class ShrinkExpandGraphUI {
    protected CytoscapeWindow cytoscapeWindow;
    protected Graph2D graph;
    public ShrinkExpandGraphUI (CytoscapeWindow cytoscapeWindow)
    {
	this.cytoscapeWindow = cytoscapeWindow;
 	cytoscapeWindow.getLayoutMenu().add
	    (new ShrinkExpandGraph (cytoscapeWindow,"Shrink Graph", 0.8));
 	cytoscapeWindow.getLayoutMenu().add
	    (new ShrinkExpandGraph (cytoscapeWindow,"Expand Graph", 1.25));
    }


    
} // class


