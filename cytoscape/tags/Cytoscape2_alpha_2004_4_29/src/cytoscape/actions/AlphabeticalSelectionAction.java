//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;

import cytoscape.view.NetworkView;
import cytoscape.data.CyNetworkUtilities;

import ViolinStrings.Strings;

//-------------------------------------------------------------------------
public class AlphabeticalSelectionAction extends AbstractAction  {
  
  NetworkView networkView;
 
  public AlphabeticalSelectionAction( NetworkView networkView ) {
    super("By Name...");
    this.networkView = networkView;
  }


  public void actionPerformed (ActionEvent e) {

    String answer = 
      (String) JOptionPane.showInputDialog(networkView.getMainFrame(), 
                                           "<HTML>Select nodes whose <B>name or synonym</B> is like <small>(use \"*\" and \"?\" for wildcards)</small></HTML>");
    if (answer != null && answer.length() > 0) {
      if ( CyNetworkUtilities.selectNodesStartingWith(networkView.getNetwork(),
                                                      answer.trim(),
                                                      networkView.getCytoscapeObj(), networkView ) ){
						       
        networkView.redrawGraph(false, false);
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

