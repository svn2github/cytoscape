//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.layout.circular.CircularLayouter;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class CircularLayoutAction extends AbstractAction   {
    NetworkView networkView;
    
    public CircularLayoutAction (NetworkView networkView) {
        super("Circular");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        networkView.setLayouter( new CircularLayouter() );
    }
}

