package filter.cytoscape.network;

import java.awt.event.*;
import javax.swing.*;

import filter.*;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;


public class FilterDataView extends CytoscapeAction {
  
  protected FilterBrowserSelection fbs;

  public FilterDataView ( ImageIcon icon ) {
    super( "Attributes Browser if passed Filter", icon );
    setPreferredMenu( "Data" );
  }

  public void actionPerformed ( ActionEvent e ) {

    getFilterBrowserSelection().setVisible( true );

  }

  public FilterBrowserSelection getFilterBrowserSelection () {
    if ( fbs == null ) {
      fbs = new FilterBrowserSelection();
    }
    return fbs;
  }
  


}
