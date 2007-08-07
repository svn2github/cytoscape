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
import cytoscape.actions.CheckBoxFileChooser;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.InteractionsReader;

//-------------------------------------------------------------------------
public class LoadGraphFileAction extends AbstractAction {
  protected NetworkView networkView;
  protected CyMenus windowMenu;
  /* windowMenu remembered so that when new CyNetwork is created, the menu
     can be added as a listener to its graphView - menu choices
     (e.g. save) need to be disabled whenever the network is changed so as
     to become empty or non empty.
  */

  public LoadGraphFileAction(NetworkView networkView, CyMenus windowMenu, String text ) {
    super(text);
    if (networkView == null || windowMenu == null) {
      throw new IllegalArgumentException("Bad arguments to LoadGraphFileAction constructor");
    }
    this.networkView = networkView;
    this.windowMenu = windowMenu;
  }

  public LoadGraphFileAction(NetworkView networkView, CyMenus windowMenu ) {
    super("Graph...");
    if (networkView == null || windowMenu == null) {
      throw new IllegalArgumentException("Bad arguments to LoadGraphFileAction constructor");
    }
    this.networkView = networkView;
    this.windowMenu = windowMenu;
  }


  public void actionPerformed(ActionEvent e)  {
    CytoscapeObj cytoscapeObj = networkView.getCytoscapeObj();
    File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
    JFileChooser chooser = new JFileChooser(currentDirectory);
    //JFileChooser chooser = new CheckBoxFileChooser(currentDirectory, "append graph? (not implemented)");
    boolean appendFlag = false;
    //chooser.setApproveButtonText("TEST TEXT");
    CyFileFilter intFilter   = new CyFileFilter();
    CyFileFilter gmlFilter   = new CyFileFilter();
    CyFileFilter graphFilter = new CyFileFilter();
    CyNetwork newNetwork;

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
    if (chooser.showOpenDialog(networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
      currentDirectory = chooser.getCurrentDirectory();
      networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
      //appendFlag = chooser.getCheckBoxState();
      //if(appendFlag) System.out.println("appending graph");

      String  name = chooser.getSelectedFile().toString();
      int fileType = Cytoscape.FILE_SIF;
      if (name.length() > 4) {//long enough to have a "gml" extension
	String extension = name.substring( name.length()-3 );
	if (extension.equalsIgnoreCase("gml"))
	    fileType = Cytoscape.FILE_GML;
      }

      boolean canonicalize = Semantics.getCanonicalize(cytoscapeObj);
      String species = Semantics.getDefaultSpecies(networkView.getNetwork(), cytoscapeObj);
      //GraphReader reader = null;

      newNetwork = Cytoscape.createNetwork( name,
					    fileType,
					    canonicalize,
					    cytoscapeObj.getBioDataServer(),
					    species );
        //reader = new GMLReader(name);
        //newNetwork = CyNetworkFactory.createNetworkFromGMLFile( name );
        //reader = new InteractionsReader(cytoscapeObj.getBioDataServer(),species,name);

      // newNetwork = CyNetworkFactory.createNetworkFromGraphReader(reader,canonicalize);
      //else {
      //    boolean canonicalize = Semantics.getCanonicalize(cytoscapeObj);
      //    String  species = Semantics.getDefaultSpecies( networkView.getNetwork(), cytoscapeObj );
      //newNetwork =
      //    CyNetworkFactory.createNetworkFromInteractionsFile( name,
      //							canonicalize,
      //							cytoscapeObj.getBioDataServer(),
      //							species );
      //}
      if (newNetwork != null) {//valid read
	//apply the semantics we usually expect
	Semantics.applyNamingServices(newNetwork, networkView.getCytoscapeObj());
  networkView.getGraphViewController().stopListening();
	networkView.getNetwork().setNewGraphFrom(newNetwork, false);
  networkView.getGraphViewController().resumeListening();
	//all the following is for future consideration
	//we want to preserve the old attributes or expression data. Expression
	//data is read-only, so we can just reference the same object, but we
	//need to make copies of the attributes objects so future changes only
	//affect the copies. So, we
	//1. make a copy of the old attributes objects
	//2. wipe the old object-to-name mappings
	//3. import the new attributes into the copy of the old, which overwrites
	//   any duplicated attributes
	//4. put this merged attributes object into the new network
	/*
	  GraphObjAttributes newNodeAttributes = new GraphObjAttributes();
	  newNodeAttributes.inputAll( networkView.getNetwork().getNodeAttributes() );
	  newNodeAttributes.clearNameMap();
	  newNodeAttributes.clearObjectMap();
	  newNodeAttributes.inputAll( newNetwork.getNodeAttributes() );
	  newNetwork.setNodeAttributes(newNodeAttributes);

	  GraphObjAttributes newEdgeAttributes = new GraphObjAttributes();
	  newEdgeAttributes.inputAll( networkView.getNetwork().getEdgeAttributes() );
	  newEdgeAttributes.clearNameMap();
	  newEdgeAttributes.clearObjectMap();
	  newEdgeAttributes.inputAll( newNetwork.getEdgeAttributes() );
	  newNetwork.setEdgeAttributes(newEdgeAttributes);

	  newNetwork.setExpressionData( networkView.getNetwork().getExpressionData() );
	  //now we switch the window to the new network
	  networkView.setNewNetwork(newNetwork);
	*/
	networkView.setWindowTitle(name);//and set a new title

	//hack to apply layout information from a GML file
	if( fileType == Cytoscape.FILE_GML ) {
    //GMLReader reader = new GMLReader(name);
	    Cytoscape.getLastGraphReaderForDoingLayout().layout(networkView.getView());
	}

	//give the user some confirmation
	String lineSep = System.getProperty("line.separator");
	StringBuffer sb = new StringBuffer();
	sb.append("Succesfully loaded graph from " + name + lineSep);
	sb.append("Graph contains " + newNetwork.getGraphPerspective().getNodeCount());
	sb.append(" nodes and " + newNetwork.getGraphPerspective().getEdgeCount());
	sb.append(" edges.");
	JOptionPane.showMessageDialog(networkView.getMainFrame(),
				      sb.toString(),
				      "Load graph successful",
				      JOptionPane.INFORMATION_MESSAGE);
      } else {//give the user an error dialog
	String lineSep = System.getProperty("line.separator");
	StringBuffer sb = new StringBuffer();
	sb.append("Could not read graph from file " + name + lineSep);
	sb.append("This file may not be a valid GML or SIF file." + lineSep);
	JOptionPane.showMessageDialog(networkView.getMainFrame(),
				      sb.toString(),
				      "Error loading graph",
				      JOptionPane.ERROR_MESSAGE);
      }
    } // if
    networkView.getView().addGraphViewChangeListener(windowMenu);
    windowMenu.setNodesRequiredItemsEnabled();
  } // actionPerformed
}

