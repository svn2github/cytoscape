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
import cytoscape.view.NetworkView;
import cytoscape.view.CyMenus;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.actions.CheckBoxFileChooser;
import cytoscape.data.readers.GMLReader;
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
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_G, ActionEvent.CTRL_MASK );
    this.windowMenu = windowMenu;
  }

  public LoadGraphFileAction ( CyMenus windowMenu, boolean label ) {
    super();
    this.windowMenu = windowMenu;
  }


  public void actionPerformed(ActionEvent e)  {
   
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    JFileChooser chooser = new JFileChooser(currentDirectory);
    
    boolean appendFlag = false;
    
    // create FileFilters
    CyFileFilter intFilter   = new CyFileFilter();
    CyFileFilter gmlFilter   = new CyFileFilter();
    CyFileFilter graphFilter = new CyFileFilter();
    
    CyNetwork newNetwork;

    // Add accepted File Extensions
    gmlFilter.addExtension("gml");
    gmlFilter.setDescription("GML files");
    intFilter.addExtension("sif");
    intFilter.setDescription("Interaction files");
    graphFilter.addExtension("sif");
    graphFilter.addExtension("gml");
    graphFilter.setDescription("All graph files");
    chooser.addChoosableFileFilter(graphFilter);
    chooser.addChoosableFileFilter(intFilter);
    chooser.addChoosableFileFilter(gmlFilter);
    chooser.setFileFilter(graphFilter);
    if (chooser.showOpenDialog( Cytoscape.getDesktop() ) == chooser.APPROVE_OPTION) {
      currentDirectory = chooser.getCurrentDirectory();
      Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
      //appendFlag = chooser.getCheckBoxState();
      //if(appendFlag) System.out.println("appending graph");

      String  name = chooser.getSelectedFile().toString();
      int fileType = Cytoscape.FILE_SIF;
      if (name.length() > 4) {//long enough to have a "gml" extension
        String extension = name.substring( name.length()-3 );
        if (extension.equalsIgnoreCase("gml"))
          fileType = Cytoscape.FILE_GML;
      }

      boolean canonicalize = Semantics.getCanonicalize(Cytoscape.getCytoscapeObj());

      //TODO: get species info on a per network basis
      String species = Semantics.getDefaultSpecies( Cytoscape.getCurrentNetwork(),Cytoscape.getCytoscapeObj() );
      Cytoscape.setSpecies();
      //GraphReader reader = null;

      int root_nodes = Cytoscape.getRootGraph().getNodeCount();
      int root_edges = Cytoscape.getRootGraph().getEdgeCount();

      newNetwork = Cytoscape.createNetwork( name,
                                            fileType,
                                            canonicalize,
                                            Cytoscape.getCytoscapeObj().getBioDataServer(),
                                            species );
     
      if ( newNetwork != null ) {//valid read
        //apply the semantics we usually expect
        //Semantics.applyNamingServices( newNetwork, Cytoscape.getCytoscapeObj() );
        //  networkView.getGraphViewController().stopListening();
        //         networkView.getNetwork().setNewGraphFrom(newNetwork, false);
        //         networkView.getGraphViewController().resumeListening();
        //         networkView.setWindowTitle(name);//and set a new title

        //hack to apply layout information from a GML file
        if( fileType == Cytoscape.FILE_GML ) {
          //GMLReader reader = new GMLReader(name);
          //Cytoscape.getLastGraphReaderForDoingLayout().layout( Cytoscape.getCurrentNetworkView() );
          newNetwork.putClientData( "GML", Cytoscape.getLastGraphReaderForDoingLayout() );
        }

        System.out.println( "New Nodes: "+ (Cytoscape.getRootGraph().getNodeCount()- root_nodes ) );
        System.out.println( "New Edges: "+ (Cytoscape.getRootGraph().getEdgeCount()- root_edges ) );
        

        //give the user some confirmation
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("Succesfully loaded graph from " + name + lineSep);
        sb.append("Graph contains " + newNetwork.getNodeCount());
        sb.append(" nodes and " + newNetwork.getEdgeCount());
        sb.append(" edges.");
        JOptionPane.showMessageDialog( Cytoscape.getDesktop(),
                                      sb.toString(),
                                      "Load graph successful",
                                      JOptionPane.INFORMATION_MESSAGE);
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
    } // if
    //networkView.getView().addGraphViewChangeListener(windowMenu);
    windowMenu.setNodesRequiredItemsEnabled();
  } // actionPerformed
}

