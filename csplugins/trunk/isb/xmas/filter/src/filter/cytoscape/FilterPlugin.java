package filter.cytoscape;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.CyNetwork;
public class FilterPlugin extends CytoscapeAction {

  protected JFrame frame;
  protected CyNetwork network;
  protected CyWindow window;
 protected CsFilter csfilter;

  public FilterPlugin ( CyNetwork network, CyWindow window, ImageIcon icon, CsFilter csfilter  ) {
    super( "", icon );
    this.network = network;
    this.window = window;
    this.csfilter = csfilter;
    setPreferredMenu( "Filters" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK );
  }

  public FilterUsePanel getFilterUsePanel () {
    return csfilter.getFilterUsePanel();
  }
                                            

  public void actionPerformed (ActionEvent e) {
     csfilter.show();
  
    
  }

  public boolean isInToolBar () {
    return true;
  }

  public boolean isInMenuBar () {
    return false;
  }


}
