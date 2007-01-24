package filter.cytoscape;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
public class FilterMenuItem extends CytoscapeAction {

 
  protected JFrame frame;
  protected CsFilter csfilter;

  public FilterMenuItem ( ImageIcon icon, CsFilter csfilter ) {
    super( "Use Filters", icon );
    this.csfilter = csfilter;
    setPreferredMenu( "Select" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_F7, 0 );
  }

  public FilterUsePanel getFilterUsePanel () {
    return csfilter.getFilterUsePanel();
  }
                                            

  public void actionPerformed (ActionEvent e) {
    csfilter.show();
  }

  public boolean isInToolBar () {
    return false;
  }

  public boolean isInMenuBar () {
    return true;
  }


}
