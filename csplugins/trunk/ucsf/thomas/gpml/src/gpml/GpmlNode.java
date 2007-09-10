package gpml;

import giny.view.GraphView;
import giny.view.NodeView;

import java.util.HashMap;

import org.pathvisio.model.ObjectType;
import org.pathvisio.model.PathwayElement;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import ding.view.DGraphView;
import ding.view.DingCanvas;

/**
 * Class that holds a Cytoscape edge that has a GPML representation, which is stored
 * as edge attributes
 * @author thomas
 *
 */
public class GpmlNode {
	CyAttributes attributes = Cytoscape.getNodeAttributes();
	
	CyNode parent;
	PathwayElement pwElm;
	HashMap<GraphView, Annotation> annotations = new HashMap<GraphView, Annotation>();
	
	public GpmlNode(CyNode parent, PathwayElement pwElm) {
		this.parent = parent;
		this.pwElm = pwElm;
		GpmlAttributeHandler.transferAttributes(parent.getIdentifier(), pwElm, attributes);
	}
	
	public CyNode getParent() {
		return parent;
	}

	public void addAnnotation(GraphView view) {
		if(annotations.containsKey(view)) return; //Annotation already added
		
		NodeView nv = view.getNodeView(parent);

		DGraphView dview = (DGraphView) view;
		DingCanvas aLayer = dview.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
		
		Annotation a = null;
		
		switch(pwElm.getObjectType()) {
		case ObjectType.SHAPE:
			a = new Shape(pwElm, dview);
			break;
		case ObjectType.LABEL:
			a = new Label(pwElm, dview);
			break;
		case ObjectType.LINE:
			a = new Line(pwElm, dview);
			break;
		case ObjectType.LEGEND:
		case ObjectType.MAPPINFO:
		case ObjectType.INFOBOX:
			//Only hide the node
			view.hideGraphObject(nv);
			break;
		}
		if(a != null) {
			aLayer.add(a);
			view.hideGraphObject(nv);
			annotations.put(view, a);
		}
	}
	
	public void resetPosition(GraphView view) {
		NodeView nv = view.getNodeView(parent);
		nv.setXPosition(GpmlImporter.mToV(pwElm.getMCenterX()), false);
		nv.setYPosition(GpmlImporter.mToV(pwElm.getMCenterY()), false);
	}

	public PathwayElement getPathwayElement() {
		return pwElm;
	}
}
