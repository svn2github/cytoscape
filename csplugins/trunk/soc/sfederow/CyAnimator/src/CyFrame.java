package src;   

import giny.model.Node;
import giny.view.NodeView;

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
	
	HashMap<String, double[]> nodePosMap;
	HashMap<String, Paint> nodeColMap;
	CyNetworkView networkView = null;
	CyNetwork currentNetwork = null;
	Image networkImage = null;
	List<Node> nodeList = null;
	
	
	public CyFrame(CyNetwork currentNetwork){
		nodePosMap = new HashMap<String, double[]>();
		nodeColMap = new HashMap<String, Paint>();
		
		this.currentNetwork = currentNetwork;
		nodeList = currentNetwork.nodesList();
		networkView = Cytoscape.getCurrentNetworkView();
		
	}
	
	//public CyFrame(CyNetwork currentNetwork, List<HashMap<String, double[]>> valuesList){
		
 	//}
	
	
	public void populate(CyNetworkView networkView){
		
		

		for(Node node: nodeList)
        {
		   
		   NodeView nodeView = networkView.getNodeView(node);
		   
		   double[] xy = new double[2];
		   xy[0] = nodeView.getXPosition();
		   xy[1] = nodeView.getYPosition();
		   
		   nodePosMap.put(node.getIdentifier(), xy);
		   nodeColMap.put(node.getIdentifier(), nodeView.getUnselectedPaint());
		   //System.out.println(nodeView.getUnselectedPaint()+"    X: "+nodeView.getXPosition()+"    Y: "+nodeView.getYPosition());
		   
		}
	}
	
	public void captureImage(){
		
		double scale = .3;
		double wscale = .2;

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
		
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		List<Node> nodeList = currentNetwork.nodesList();
		for(Node node: nodeList)
        {
			
			NodeView nodeView = networkView.getNodeView(node);
			double[] xy = nodePosMap.get(node.getIdentifier());
			Paint p = nodeColMap.get(node.getIdentifier());
			
			//System.out.println("DISPLAY: "+xy[0]+"  "+xy[1]);
			
			nodeView.setXPosition(xy[0]);
			nodeView.setYPosition(xy[1]);
				
			nodeView.setUnselectedPaint(p);
			
        }
		networkView.updateView();
	}
	
	
}