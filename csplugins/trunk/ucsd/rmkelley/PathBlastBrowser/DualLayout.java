package csplugins.ucsd.rmkelley.PathBlastBrowser.Layout;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.geom.Point2D;
import java.io.*;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.CyNetwork;
import cytoscape.util.GinyFactory;
import cytoscape.data.Semantics;
import cytoscape.view.CyWindow;
import cytoscape.view.GraphViewController;
import cytoscape.data.readers.GMLTree;
/**
 * This is a plugin to separate a compatability graph into two
 * separate graphs, one for each species. It tries to lay the graphs
 * out such that homologous nodes are in a similar position in each graph.
 * In order to achieve this, it uses a force-directed layout, where the relevant
 * forces are repulsion between nodes, attraction between nodes connected by edge
 * and psuedo-attraction between homologous nodes (node will actuall be attracted to
 * that is "offset" away from the real node.
 */
public class DualLayout extends CytoscapePlugin{

  DualLayoutCommandLineParser parser;
  public static String NEW_TITLE = "Split Graph";
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public DualLayout() {
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new DualLayoutAction() );
    parser = new DualLayoutCommandLineParser(Cytoscape.getCytoscapeObj().getConfiguration().getArgs());
    if(parser.run()){
      Thread t = new DualLayoutTask(Cytoscape.getCurrentNetwork(),parser,Cytoscape.getCurrentNetwork().getTitle()+" - Split Graph"); 
      t.start();
    }
  }

  /**
   * This class gets attached to the menu item.
   */
  public class DualLayoutAction extends AbstractAction {

    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public DualLayoutAction() {super("Dual Layout");}

    /**
     * Gives a description of this plugin.
     */
    public String describe() {
      StringBuffer sb = new StringBuffer();
      sb.append("Split a compatability graph and try to lay it out");
      return sb.toString();
    }

    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {

      //inform listeners that we're doing an operation on the network
      Thread t = new DualLayoutTask(Cytoscape.getCurrentNetwork(),parser,Cytoscape.getCurrentNetwork().getTitle()+" - Split"); 
      t.start();
    }
  }
}

