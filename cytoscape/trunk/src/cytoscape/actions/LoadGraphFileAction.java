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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.io.File;
import java.util.Date;


import cytoscape.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.ExpressionData;
import cytoscape.data.Semantics;
import cytoscape.process.ui.ProgressUI;
import cytoscape.view.NetworkView;
import cytoscape.view.CyMenus;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
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
   
    File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    JFileChooser chooser = new JFileChooser(currentDirectory);
    
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
    chooser.addChoosableFileFilter(graphFilter);
    chooser.addChoosableFileFilter(intFilter);
    chooser.addChoosableFileFilter(gmlFilter);
    chooser.setFileFilter(graphFilter);
    if (chooser.showOpenDialog( Cytoscape.getDesktop() ) == chooser.APPROVE_OPTION) {
      currentDirectory = chooser.getCurrentDirectory();
      Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
      //appendFlag = chooser.getCheckBoxState();
      //if(appendFlag) System.out.println("appending graph");

      final String  name = chooser.getSelectedFile().toString();
      int fileType = Cytoscape.FILE_SIF;
      if (name.length() > 4) {//long enough to have a "gml" extension
        String extension = name.substring( name.length()-3 );
        if (extension.equalsIgnoreCase("gml"))
          fileType = Cytoscape.FILE_GML;
      }

      final boolean canonicalize = Semantics.getCanonicalize(Cytoscape.getCytoscapeObj());

      //TODO: get species info on a per network basis
      final String species = Semantics.getDefaultSpecies( Cytoscape.getCurrentNetwork(),Cytoscape.getCytoscapeObj() );
      Cytoscape.setSpecies();
      //GraphReader reader = null;

      final int root_nodes = Cytoscape.getRootGraph().getNodeCount();
      final int root_edges = Cytoscape.getRootGraph().getEdgeCount();

      final int fileTypeF = fileType;
      Frame f = Cytoscape.getDesktop();
      final JDialog busyDialog = new JDialog(f, "Loading...", true);
      busyDialog.setResizable(false);
      busyDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      JPanel panel = new JPanel(new BorderLayout());
      panel.setBorder(new EmptyBorder(20, 20, 20, 20));
      panel.add(new JLabel("Loading graph; please wait..."),
                BorderLayout.CENTER);
      JProgressBar progress = new JProgressBar();
      progress.setIndeterminate(true);
      panel.add(progress, BorderLayout.SOUTH);
      busyDialog.getContentPane().add(panel);
      busyDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      busyDialog.pack();
      busyDialog.move((f.size().width - busyDialog.size().width) / 2 + f.location().x,
                      (f.size().height - busyDialog.size().height) / 2 + f.location().y);
      final CyNetwork[] newNetwork = new CyNetwork[1];
      Runnable loadGraph = new Runnable() {
        public void run()
    {
      newNetwork[0] = Cytoscape.createNetwork( name,
                                            fileTypeF,
                                            canonicalize,
                                            Cytoscape.getCytoscapeObj().getBioDataServer(),
                                            species );
      busyDialog.dispose();
    } // run()
    }; // new Runnable()
      (new Thread(loadGraph)).start();
      busyDialog.show(); // This blocks until busyDialog.dispose() is called, see JDK API spec.
      if ( newNetwork[0] != null ) {//valid read
        //apply the semantics we usually expect
        //Semantics.applyNamingServices( newNetwork, Cytoscape.getCytoscapeObj() );
        //  networkView.getGraphViewController().stopListening();
        //         networkView.getNetwork().setNewGraphFrom(newNetwork, false);
        //         networkView.getGraphViewController().resumeListening();
        //         networkView.setWindowTitle(name);//and set a new title

        //hack to apply layout information from a GML file
	//   if( fileTypeF == Cytoscape.FILE_GML ) {
	//           //GMLReader reader = new GMLReader(name);
	//           //Cytoscape.getLastGraphReaderForDoingLayout().layout( Cytoscape.getCurrentNetworkView() );
	//           newNetwork.putClientData( "GML", Cytoscape.getLastGraphReaderForDoingLayout() );
	//         }

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
        
        if ( newNetwork[0].getNodeCount() < Cytoscape.getCytoscapeObj().getViewThreshold() ) {
          sb.append( "Your Network is Under "+Cytoscape.getCytoscapeObj().getViewThreshold() +" nodes, a View  will be automatically created." );
        } else { 
          sb.append( "Your Network is Over nodes "+Cytoscape.getCytoscapeObj().getViewThreshold() +", a View  will be not be created."+lineSep+"If you wish to view this Network use \"Create View\" from the \"Edit\" menu." );
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

    } // if
    //networkView.getView().addGraphViewChangeListener(windowMenu);
  } // actionPerformed
}

