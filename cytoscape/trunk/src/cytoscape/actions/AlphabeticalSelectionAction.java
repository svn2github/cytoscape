//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cytoscape.view.NetworkView;
import cytoscape.data.CyNetworkUtilities;
//-------------------------------------------------------------------------
public class AlphabeticalSelectionAction extends AbstractAction {
    NetworkView networkView;
    
    public AlphabeticalSelectionAction(NetworkView networkView) {
        super("By Name...");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        String answer = 
            (String) JOptionPane.showInputDialog(networkView.getMainFrame(), 
            "Select nodes whose name (or synonym) starts with");
        if (answer != null && answer.length() > 0) {
           if ( CyNetworkUtilities.selectNodesStartingWith(networkView.getNetwork(),
                                                       answer.trim(),
                                                       networkView.getCytoscapeObj(), networkView ) ){
						       
		networkView.redrawGraph(false, true);
		}
						       
        
		else
		{
		StringBuffer sb = new StringBuffer();
                sb.append("Node by name: ");
		sb.append(answer);
		sb.append(" or starting with: ");
		sb.append(answer);
		sb.append(" not found.");
		JOptionPane.showMessageDialog(networkView.getMainFrame(),
                                              sb.toString(),
                                              "Node by name not found",
                                              JOptionPane.INFORMATION_MESSAGE);
	}
	}
    }
}

