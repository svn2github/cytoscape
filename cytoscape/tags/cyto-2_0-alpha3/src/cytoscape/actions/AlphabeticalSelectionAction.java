//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;

import cytoscape.view.CyNetworkView;
import cytoscape.data.CyNetworkUtilities;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

import ViolinStrings.Strings;


public class AlphabeticalSelectionAction extends CytoscapeAction  {
   
  public AlphabeticalSelectionAction () {
    super("By Name...");
    setPreferredMenu( "Select.Nodes" );
  }


  public void actionPerformed (ActionEvent e) {

    String answer = 
      (String) JOptionPane.showInputDialog( Cytoscape.getDesktop(), 
                                            "<HTML>Select nodes whose <B>name or synonym</B> is like <small>(use \"*\" and \"?\" for wildcards)</small></HTML>");
    if (answer != null && answer.length() > 0) {
      if ( CyNetworkUtilities.selectNodesStartingWith( Cytoscape.getCurrentNetwork(),
                                                       answer.trim(),
                                                       Cytoscape.getCytoscapeObj(), Cytoscape.getCurrentNetworkView() ) ){
						       
        
      }
						       
        
      else
        {
          StringBuffer sb = new StringBuffer();
          sb.append("Node by name: ");
          sb.append(answer);
          sb.append(" or starting with: ");
          sb.append(answer);
          sb.append(" not found.");
          JOptionPane.showMessageDialog( Cytoscape.getDesktop(),
                                         sb.toString(),
                                         "Node by name not found",
                                         JOptionPane.INFORMATION_MESSAGE);
        }
    }
  }
}

