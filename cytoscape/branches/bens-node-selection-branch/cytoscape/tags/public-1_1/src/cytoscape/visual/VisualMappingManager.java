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
 * CytoscapeWindow, a CalculatorCatalog, and a VisualStyle.
 *
 * Note that a null VisualStyle is not allowed; this class always provides
 * at least a default object.
 */
public class VisualMappingManager {

    Network network;
    CytoscapeWindow cytoscapeWindow;
    CalculatorCatalog catalog;
    VisualStyle visualStyle;

    
    public VisualMappingManager(CytoscapeWindow cytoscapeWindow,
                                CalculatorCatalog catalog,
                                VisualStyle style) {
        this.cytoscapeWindow = cytoscapeWindow;
        this.network = new Network(cytoscapeWindow);
        this.catalog = catalog;
        setVisualStyle(style);
    }


    public Network getNetwork() {
	return network;
    }

    public CytoscapeWindow getCytoscapeWindow() {
	return cytoscapeWindow;
    }
    
    public CalculatorCatalog getCalculatorCatalog() {return catalog;}
    
    public VisualStyle getVisualStyle() {return visualStyle;}
    public VisualStyle setVisualStyle(VisualStyle vs) {
        if (vs != null) {
            VisualStyle tmp = visualStyle;
            visualStyle = vs;
            return tmp;
        } else {
            String s = "VisualMappingManager: Attempt to set null VisualStyle";
            cytoscapeWindow.getLogger().severe(s);
            return null;
        }
    }
    public VisualStyle setVisualStyle(String name) {
        VisualStyle vs = catalog.getVisualStyle(name);
        if (vs != null) {
            VisualStyle tmp = visualStyle;
            visualStyle = vs;
            return tmp;
        } else {
            String s = "VisualMappingManager: unknown VisualStyle: " + name;
            cytoscapeWindow.getLogger().severe(s);
            return null;
        }
    }

    public void applyAppearances() {
	Graph2DView graphView = cytoscapeWindow.getGraphView();

	/** first apply the node appearance to all nodes */
        NodeAppearanceCalculator nodeAppearanceCalculator =
                visualStyle.getNodeAppearanceCalculator();
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
        EdgeAppearanceCalculator edgeAppearanceCalculator =
                visualStyle.getEdgeAppearanceCalculator();
        Edge[] edges = graphView.getGraph2D().getEdgeArray();
	for (int i=0; i < edges.length; i++) {
	    Edge edge = edges[i];
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
	}
        
        /** now apply global appearances */
        GlobalAppearanceCalculator globalAppearanceCalculator =
        visualStyle.getGlobalAppearanceCalculator();
        GlobalAppearance ga = globalAppearanceCalculator.calculateGlobalAppearance(network);
        DefaultBackgroundRenderer bgRender =
                (DefaultBackgroundRenderer)graphView.getBackgroundRenderer();
        bgRender.setColor( ga.getBackgroundColor() );
        NodeRealizer.setSloppySelectionColor( ga.getSloppySelectionColor() );

        //don't repaint here; instead, rely on caller to call redrawGraph()
        //in CytoscapeWindow, which will call this method
	/** finally, have CytoscapeWindow update. */
	//graphView.updateView(); // forces the view to update its contents
	// paintImmediately() needed because sometimes updates can be buffered
	//graphView.paintImmediately(0,0,graphView.getWidth(),
	//			   graphView.getHeight());
	//cytoscapeWindow.updateStatusText();
    }
}

