package src;   

import giny.model.Node;
import giny.view.NodeView;
import giny.model.Edge;
import giny.view.EdgeView;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.*;
import java.awt.Paint;
import java.util.HashMap;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;

public class CyFrame {
	
	private String frameid = "";
	HashMap<String, double[]> nodePosMap;
	HashMap<String, Paint> nodeColMap;
	HashMap<String, float[]> nodeOpacityMap;
	HashMap<String, float[]> nodeBorderMap;
	
	HashMap<String, Paint> edgeColMap;
	
	Paint backgroundPaint = null;
	double zoom = 0;
	
	double xalign;
	double yalign;
	
	CyNetworkView networkView = null;
	CyNetwork currentNetwork = null;
	Image networkImage = null;
	List<Node> nodeList = null;
	List<Edge> edgeList = null;
	int intercount = 0;
	
	public CyFrame(CyNetwork currentNetwork){
		nodePosMap = new HashMap<String, double[]>();
		nodeColMap = new HashMap<String, Paint>();
		nodeOpacityMap = new HashMap<String, float[]>();
		edgeColMap = new HashMap<String, Paint>();
		this.currentNetwork = currentNetwork;
		nodeList = currentNetwork.nodesList();
		edgeList = currentNetwork.edgesList();
		networkView = Cytoscape.getCurrentNetworkView();
		populate(currentNetwork, networkView);
		
	}
	
	//public CyFrame(CyNetwork currentNetwork, List<HashMap<String, double[]>> valuesList){
		
 	//}
	
	
	public void populate(CyNetwork network, CyNetworkView networkView){
		
		nodeList = network.nodesList();
		this.currentNetwork = network;
		backgroundPaint = networkView.getBackgroundPaint();
		zoom = networkView.getZoom();
		xalign = networkView.getComponent().getAlignmentX();
		yalign = networkView.getComponent().getAlignmentY();
		
		for(Node node: nodeList)
        {
		   
		   NodeView nodeView = networkView.getNodeView(node);
		   if(nodeView == null){ continue; }
		   
		   double[] xy = new double[2];
		   xy[0] = nodeView.getXPosition();
		   xy[1] = nodeView.getYPosition();
		   
		   nodePosMap.put(node.getIdentifier(), xy);
		   nodeColMap.put(node.getIdentifier(), nodeView.getUnselectedPaint());
		   float[] trans = new float[1];
		   trans[0] = nodeView.getTransparency();
		  
		   nodeOpacityMap.put(node.getIdentifier(), trans);
		   
		   //System.out.println(nodeView.getUnselectedPaint()+"    X: "+nodeView.getXPosition()+"    Y: "+nodeView.getYPosition());
		   
		}
		for(Edge edge: edgeList)
		{
			EdgeView edgeView = networkView.getEdgeView(edge);
			if(edgeView == null){  continue; }
			Paint p = edgeView.getUnselectedPaint();
			
			//if(edge == null || p == null){ return; }
			edgeColMap.put(edge.getIdentifier(), p);
		
		}
	}
	
	public void captureImage(){
		
		double scale = .35;
		double wscale = .25;

		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		
		
		
		InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		int width  = (int) (ifc.getWidth() * wscale);
		int height = (int) (ifc.getHeight() * scale);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = (Graphics2D) image.getGraphics();
		g.scale(scale, scale);
		ifc.print(g);
		g.dispose();

		networkImage = image;
	
	}
	
	public void display(){
		
		
		List<Node> nodeList = currentNetwork.nodesList();
		
		
		
		for(Node node: nodeList)
        {
			
			NodeView nodeView = networkView.getNodeView(node);
			double[] xy = nodePosMap.get(node.getIdentifier());
			Paint p = nodeColMap.get(node.getIdentifier());
			float[] trans = nodeOpacityMap.get(node.getIdentifier());
			//System.out.println("DISPLAY: "+xy[0]+"  "+xy[1]);
			
			nodeView.setXPosition(xy[0]);
			nodeView.setYPosition(xy[1]);
			nodeView.setTransparency(trans[0]);	
			nodeView.setUnselectedPaint(p);
			
        }
		for(Edge edge: edgeList)
		{
			EdgeView edgeView = networkView.getEdgeView(edge);
			Paint p = edgeColMap.get(edge.getIdentifier());
			edgeView.setUnselectedPaint(p);
		}
		networkView.setBackgroundPaint(backgroundPaint);
		networkView.setZoom(zoom);
		//networkView.getComponent().
		
		networkView.updateView();
	}
	
	public String getID(){
		return frameid;
	}
	public void setID(String ID){
		frameid = ID;
	}
}