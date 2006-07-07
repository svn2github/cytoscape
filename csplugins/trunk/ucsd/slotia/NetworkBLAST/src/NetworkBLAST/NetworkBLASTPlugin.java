package NetworkBLAST;

import java.util.Map;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * NetworkBLAST plugin provides NCT functionality to Cytoscape.
 */

import nct.graph.Graph;
import nct.graph.Edge;
import nct.graph.basic.BasicGraph;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
import nct.visualization.cytoscape.CytoscapeConverter;
import java.util.Iterator;

public class NetworkBLASTPlugin extends CytoscapePlugin
{
  private NetworkBLASTDialog dialog = null;
  
  public NetworkBLASTPlugin()
  {
    this.dialog = new NetworkBLASTDialog(Cytoscape.getDesktop());
    
    JMenu nbMenu = new JMenu("NetworkBLAST");
    
    JMenuItem aboutMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "About", 0, this.dialog));
					  
    JMenuItem comptMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Generate Compatiblity Graph",
					  1, this.dialog));
					  
    JMenuItem pathMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Path Search",
					  2, this.dialog));
					  
    JMenuItem compMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Complex Search",
					  3, this.dialog));
					  
    JMenuItem scoreMenuItem = new JMenuItem(new NetworkBLASTAction(
                                          "Score Model Settings",
					  4, this.dialog));

					  
    nbMenu.add(aboutMenuItem);
    nbMenu.addSeparator();
    nbMenu.add(comptMenuItem);
    nbMenu.add(pathMenuItem);
    nbMenu.add(compMenuItem);
    nbMenu.addSeparator();
    nbMenu.add(scoreMenuItem);

    nbMenu.add(new JMenuItem(new javax.swing.AbstractAction("yadda")
    {
      public void actionPerformed(java.awt.event.ActionEvent _e)
      {
        Graph<String,Double> nctGraph = new BasicGraph<String,Double>("testgraph");
	nctGraph.addNode("node1");
	nctGraph.addNode("node2");
	nctGraph.addNode("node3");
	nctGraph.addNode("node4");
	nctGraph.addEdge("node1", "node2", 0.1, "edge1");
	nctGraph.addEdge("node2", "node3", 0.2, "edge2");
	nctGraph.addEdge("node3", "node4", 0.3, "edge3");
	nctGraph.addEdge("node4", "node1", 0.4, "edge4");
	
	System.out.println("nctGraph edge dump:");
	for (Edge<String,Double> edge : nctGraph.getEdges())
	{
	  System.out.println(edge.getSourceNode() + "\t--(" + edge.getWeight() + "/" + edge.getDescription() + ")-->\t" + edge.getTargetNode());
	}
	System.out.println("\n------------------");
	
	CyNetwork cyGraph = CytoscapeConverter.convert(nctGraph);
	System.out.println("cyGraph edge dump:");
	
	Iterator edgesIter = cyGraph.edgesIterator();
	CyAttributes cyAttrs = Cytoscape.getEdgeAttributes();
	while (edgesIter.hasNext())
	{
	  CyEdge e = (CyEdge) edgesIter.next();
	  System.out.println(e.getSource().getIdentifier() + " --> " + e.getTarget().getIdentifier());
	  System.out.println(cyAttrs.getStringAttribute(e.getIdentifier(), "interaction") + ", " + e.getIdentifier());
	}

	
      }
    }));
    
    Cytoscape.getDesktop().getCyMenus().getMenuBar().
    		getMenu("Plugins").add(nbMenu);
  }
}
