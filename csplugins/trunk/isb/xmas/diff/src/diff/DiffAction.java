package diff;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;

public class DiffAction extends CytoscapeAction {

  protected DiffView diffView;

  public DiffAction () {
    super( "Diff" );
    setPreferredMenu( "Data" );
  }

  public void actionPerformed ( ActionEvent e ) {
    getDiffView().setVisible( true );    
  }

  protected DiffView getDiffView () {
    if ( diffView == null )
      diffView = new DiffView();
    return diffView;
  }
                                    
}
