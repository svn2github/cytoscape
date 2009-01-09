//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.Date;


import cytoscape.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.ExpressionData;
import cytoscape.data.Semantics;
import cytoscape.process.ui.ProgressUI;
import cytoscape.process.ui.ProgressUIControl;
import cytoscape.view.NetworkView;
import cytoscape.view.CyMenus;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.actions.CheckBoxFileChooser;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;

//-------------------------------------------------------------------------
public class LoadGraphFileAction extends CytoscapeAction {


  protected CyMenus windowMenu;
  /* windowMenu remembered so that when new CyNetwork is created, the menu
     can be added as a listener to its graphView - menu choices
     (e.g. save) need to be disabled whenever the network is changed so as
     to become empty or non empty.
  */

//   public LoadGraphFileAction ( String text ) {
//     super(text);
//     setPreferredMenu( "File.Load" );
//     setAcceleratorCombo( java.awt.event.KeyEvent.VK_G, ActionEvent.CTRL_MASK );
//   }

  public LoadGraphFileAction ( CyMenus windowMenu ) {
    super("Graph...");
    setPreferredMenu( "File.Load" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_L, ActionEvent.CTRL_MASK );
    this.windowMenu = windowMenu;
  }

  public LoadGraphFileAction ( CyMenus windowMenu, boolean label ) {
    super();
    this.windowMenu = windowMenu;
  }


  public void actionPerformed(ActionEvent e)  {
   
    boolean appendFlag = false;
    
    // create FileFilters
    CyFileFilter intFilter   = new CyFileFilter();
    CyFileFilter gmlFilter   = new CyFileFilter();
    CyFileFilter graphFilter = new CyFileFilter();
    
    // Add accepted File Extensions
    gmlFilter.addExtension("gml");
    gmlFilter.setDescription("GML files");
    intFilter.addExtension("sif");
    intFilter.setDescription("Interaction files");
    graphFilter.addExtension("sif");
    graphFilter.addExtension("gml");
    graphFilter.setDescription("All graph files");
   
    // get the file name
    final String name;
    try {
      name = FileUtil.getFile( "Load Graph File",
                               FileUtil.LOAD,
                               new CyFileFilter[] { graphFilter, intFilter, gmlFilter } ).toString();
    } catch ( Exception exp ) {
      // this is because the selection was canceled
      return;
    }
    
    // if the name is not null, then load
    if ( name != null ) {
      int fileType = Cytoscape.FILE_SIF;
      if (name.length() > 4) {//long enough to have a "gml" extension
        String extension = name.substring( name.length()-3 );
        if (extension.equalsIgnoreCase("gml"))
          fileType = Cytoscape.FILE_GML;
      }

      final boolean canonicalize = !CytoscapeInit.noCanonicalization();
        
      //TODO: get species info on a per network basis
           
      int root_nodes = Cytoscape.getRootGraph().getNodeCount();
      int root_edges = Cytoscape.getRootGraph().getEdgeCount();

      final int fileTypeF = fileType;
      final ProgressUIControl contrl =
        ProgressUI.startProgress(Cytoscape.getDesktop(),
                                 "Loading...",
                                 "Loading graph; please wait...",
                                 null);
      final CyNetwork[] newNetwork = new CyNetwork[1];
      Runnable loadGraph = new Runnable() {
        public void run()
          {
            newNetwork[0] = Cytoscape.createNetwork( name,
                                                     fileTypeF,
                                                     canonicalize,
                                                     Cytoscape.getBioDataServer(),
                                                     CytoscapeInit.getDefaultSpeciesName() );
            contrl.dispose();
          } // run()
        }; // new Runnable()
      (new Thread(loadGraph)).start();
      contrl.show(); // This blocks until busyDialog.dispose() is called, see JDK API spec.
      if ( newNetwork[0] != null ) {//valid read
     
        int nn = Cytoscape.getRootGraph().getNodeCount()- root_nodes;
        int ne = Cytoscape.getRootGraph().getEdgeCount()- root_edges;
        
        StringBuffer sb = new StringBuffer();
        String lineSep = System.getProperty("line.separator");
        //give the user some confirmation
        sb.append("Succesfully loaded graph from " + name + lineSep);
        sb.append("Graph contains " + newNetwork[0].getNodeCount());
        sb.append(" nodes and " + newNetwork[0].getEdgeCount());
        sb.append(" edges."+lineSep);
        sb.append("There were "+nn+" unique nodes, and "+ne+" unique edges."+lineSep+lineSep);
        
        if ( newNetwork[0].getNodeCount() < CytoscapeInit.getViewThreshold() ) {
          sb.append( "Your Network is Under "+CytoscapeInit.getViewThreshold() +" nodes, a View  will be automatically created." );
        } else { 
          sb.append( "Your Network is Over nodes "+CytoscapeInit.getViewThreshold() +", a View  will be not be created."+lineSep+"If you wish to view this Network use \"Create View\" from the \"Edit\" menu." );
        }
        JOptionPane.showMessageDialog( Cytoscape.getDesktop(),
                                      sb.toString(),
                                      "Load graph successful",
                                      JOptionPane.INFORMATION_MESSAGE);
        windowMenu.setNodesRequiredItemsEnabled();
      } else {//give the user an error dialog
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("Could not read graph from file " + name + lineSep);
        sb.append("This file may not be a valid GML or SIF file." + lineSep);
        JOptionPane.showMessageDialog( Cytoscape.getDesktop(),
                                      sb.toString(),
                                      "Error loading graph",
                                      JOptionPane.ERROR_MESSAGE);
      }

    } // if name != null
    //networkView.getView().addGraphViewChangeListener(windowMenu);
  } // actionPerformed
}

