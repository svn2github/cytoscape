//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.Properties;

import y.base.*;
import y.view.*;

import cytoscape.CytoscapeWindow;
//----------------------------------------------------------------------------
/**
 * Top-level class for controlling the visual appearance of nodes and edges
 * according to data attributes. This class holds a reference to a
 * CytoscapeWindow, a CalculatorCatalog object and the current calculators
 * for nodes and edges.
 *
 * Note that null objects for the node and edge calculators are not allowed;
 * this class always provides at least a default object.
 */
public class VisualMappingManager {

    Network network;
    CytoscapeWindow cytoscapeWindow;
    CalculatorCatalog catalog;
    NodeAppearanceCalculator nodeAppearanceCalculator;
    EdgeAppearanceCalculator edgeAppearanceCalculator;

    public Network getNetwork() {
	return network;
    }

    public CytoscapeWindow getCytoscapeWindow() {
	return cytoscapeWindow;
    }
    
    public VisualMappingManager(CytoscapeWindow cytoscapeWindow,
                                NodeAppearanceCalculator nodeCalc,
				EdgeAppearanceCalculator edgeCalc,
                                CalculatorCatalog catalog) {
        this.cytoscapeWindow = cytoscapeWindow;
        this.network = new Network(cytoscapeWindow);
        this.catalog = catalog;
        setNodeAppearanceCalculator(nodeCalc);
        setEdgeAppearanceCalculator(edgeCalc);
    }

    public VisualMappingManager(CytoscapeWindow cytoscapeWindow,
				NodeAppearanceCalculator nodeCalc,
				EdgeAppearanceCalculator edgeCalc) {
	this.cytoscapeWindow = cytoscapeWindow;
	this.network = new Network(cytoscapeWindow);
	catalog = new CalculatorCatalog();
	catalog.addNodeAppearanceCalculator("defaultNAC", nodeCalc);
	catalog.addEdgeAppearanceCalculator("defaultEAC", edgeCalc);
	setNodeAppearanceCalculator("defaultNAC");
	setEdgeAppearanceCalculator("defaultEAC");
    }

    public VisualMappingManager(CytoscapeWindow cytoscapeWindow, Properties props) {
        this.cytoscapeWindow = cytoscapeWindow;
        //catalog = cytoscapeWindow.getCalculatorCatalog();
        catalog = new CalculatorCatalog(props);
        String nodeCName = props.getProperty("nodeAppearanceCalculator");
        if (nodeCName == null) {
            setNodeAppearanceCalculator(new NodeAppearanceCalculator());
        } else {
            setNodeAppearanceCalculator(nodeCName);
            if (nodeAppearanceCalculator == null) {//invalid name
                nodeAppearanceCalculator = new NodeAppearanceCalculator();
            }
        }
        String edgeCName = props.getProperty("edgeAppearanceCalculator");
        if (edgeCName == null) {
            setEdgeAppearanceCalculator(new EdgeAppearanceCalculator());
        } else {
            setEdgeAppearanceCalculator(edgeCName);
            if (edgeAppearanceCalculator == null) {//invalid name
                edgeAppearanceCalculator = new EdgeAppearanceCalculator();
            }
        }
    }
    
    public CalculatorCatalog getCalculatorCatalog() {return catalog;}
    
    public NodeAppearanceCalculator getNodeAppearanceCalculator() {
        return nodeAppearanceCalculator;
    }
    public void setNodeAppearanceCalculator(NodeAppearanceCalculator c) {
        if (c != null) {
            nodeAppearanceCalculator = c;
        } else {
            String s = "VisualMappingManager: Attempt to set null NodeAppearanceCalculator ignored";
            cytoscapeWindow.getLogger().severe(s);
        }
    }
    public void setNodeAppearanceCalculator(String name) {
        NodeAppearanceCalculator c = catalog.getNodeAppearanceCalculator(name);
        if (c != null) {
            nodeAppearanceCalculator = c;
        } else {
            String s = "VisualMappingManager: unknown NodeAppearanceCalculator: " + name;
            cytoscapeWindow.getLogger().severe(s);
        }
    }
    
    public EdgeAppearanceCalculator getEdgeAppearanceCalculator() {
        return edgeAppearanceCalculator;
    }
    public void setEdgeAppearanceCalculator(EdgeAppearanceCalculator c) {
        if (c != null) {
            edgeAppearanceCalculator = c;
        } else {
            String s = "VisualMappingManager: Attempt to set null EdgeAppearanceCalculator ignored";
            cytoscapeWindow.getLogger().severe(s);
        }
    }
    public void setEdgeAppearanceCalculator(String name) {
        EdgeAppearanceCalculator c = catalog.getEdgeAppearanceCalculator(name);
        if (c != null) {
            edgeAppearanceCalculator = c;
        } else {
            String s = "VisualMappingManager: unknown EdgeAppearanceCalculator: " + name;
            cytoscapeWindow.getLogger().severe(s);
        }
    }
    public void applyAppearances() {
	Graph2DView graphView = cytoscapeWindow.getGraphView();

	/** first apply the node appearance to all nodes */
	Node [] nodes = graphView.getGraph2D().getNodeArray();
	for (int i=0; i < nodes.length; i++) {
	    Node node = nodes [i];
	    NodeAppearance na = new NodeAppearance();
	    nodeAppearanceCalculator.calculateNodeAppearance(na,node,network);
	    NodeRealizer nr = graphView.getGraph2D().getRealizer(node);
	    nr.setFillColor(na.getFillColor());
	    nr.setLineColor(na.getBorderColor());
	    nr.setLineType(na.getBorderLineType());
	    nr.setHeight(na.getHeight());
	    nr.setWidth(na.getWidth());
	    if (nr instanceof ShapeNodeRealizer) {
		ShapeNodeRealizer snr = (ShapeNodeRealizer)nr;
		snr.setShapeType(na.getShape());
	    }
	    NodeLabel nl = nr.getLabel();
	    nl.setText(na.getLabel());
	    nl.setFont(na.getFont());
	    //nr.setToolTip(na.getToolTip()); // how do you do this?
	}

	/** then apply the edge appearance to all edges */
	EdgeCursor cursor = graphView.getGraph2D().edges();
	cursor.toFirst ();
	for (int i=0; i < cursor.size (); i++) {
	    Edge edge = cursor.edge ();
	    EdgeAppearance ea = new EdgeAppearance();
	    edgeAppearanceCalculator.calculateEdgeAppearance(ea,edge,network);
	    EdgeRealizer er = graphView.getGraph2D().getRealizer(edge);
	    er.setLineColor(ea.getColor());
	    er.setLineType(ea.getLineType());
	    er.setSourceArrow(ea.getSourceArrow());
	    er.setTargetArrow(ea.getTargetArrow());
	    EdgeLabel el = er.getLabel();
	    // this is really dumb, but EdgeRealizer doesn't support setLabel()
	    er.removeLabel(el);
	    el.setText(ea.getLabel());
	    el.setFont(ea.getFont());
	    er.addLabel(el);
	    //er.setToolTip(ea.getToolTip()); // how do you do this?
	    cursor.cyclicNext();
	}

	/** finally, have cytoscapeWindow update. */
	graphView.updateView(); //forces the view to update it's contents
	// paintImmediately() needed because sometimes updates can be buffered
	graphView.paintImmediately(0,0,graphView.getWidth(),
				   graphView.getHeight());
	cytoscapeWindow.updateStatusText();
    }
}

