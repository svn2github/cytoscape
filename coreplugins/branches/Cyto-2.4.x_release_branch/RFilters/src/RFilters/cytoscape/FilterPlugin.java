package filter.cytoscape;

import java.awt.event.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

public class FilterPlugin extends CytoscapeAction {

  protected JFrame frame;
  protected CsFilter csfilter;

  public FilterPlugin ( ImageIcon icon, CsFilter csfilter  ) {
    super( "", icon );
    //  Set SHORT_DESCRIPTION;  used to create tool-tip
    this.putValue(Action.SHORT_DESCRIPTION, "Use Filters");
    this.csfilter = csfilter;
    setPreferredMenu( "Select" );
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
