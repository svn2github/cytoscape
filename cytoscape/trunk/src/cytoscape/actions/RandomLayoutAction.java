//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.layout.random.RandomLayouter;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class RandomLayoutAction extends AbstractAction {
    NetworkView networkView;
    
    public RandomLayoutAction(NetworkView networkView) {
        super("Random");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        networkView.setLayouter( new RandomLayouter() );
    }
}

