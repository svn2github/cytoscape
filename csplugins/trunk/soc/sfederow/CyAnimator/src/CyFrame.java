package src;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.Paint;
import java.util.HashMap;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;

public class CyFrame {
	
	HashMap<String, double[]> nodePosMap;
	HashMap<String, Paint> nodeColMap;
	CyNetworkView networkView = null;
	CyNetwork currentNetwork = null;
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
		
		
		for(int i=0;i<nodeList.size();i++)
		{
		   
		   NodeView nodeView = networkView.getNodeView(nodeList.get(i));
		   
		   double[] xy = new double[2];
		   xy[0] = nodeView.getXPosition();
		   xy[1] = nodeView.getYPosition();
		   
		   nodePosMap.put(nodeList.get(i).getIdentifier(), xy);
		   nodeColMap.put(nodeList.get(i).getIdentifier(), nodeView.getUnselectedPaint());
		   System.out.println(nodeView.getUnselectedPaint()+"    X: "+nodeView.getXPosition()+"    Y: "+nodeView.getYPosition());
		   
		}
	}
	
	 
	public void display(){
		System.out.println("WOOOO");
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		List<Node> nodeList = currentNetwork.nodesList();
		for(int i=0;i<nodeList.size();i++)
        {
			
			NodeView nodeView = networkView.getNodeView(nodeList.get(i));
			double[] xy = nodePosMap.get(nodeList.get(i).getIdentifier());
			Paint p = nodeColMap.get(nodeList.get(i).getIdentifier());
			
			System.out.println("DISPLAY: "+xy[0]+"  "+xy[1]);
			
			nodeView.setXPosition(xy[0]);
			nodeView.setYPosition(xy[1]);
				
			nodeView.setUnselectedPaint(p);
			//nodeView.setXPosition(currentFrame[i].getXPosition());
			//nodeView.setYPosition(currentFrame[i].getYPosition());
				
				
			//nodeView.setXPosition(cframe.get(nodeList.get(i).getIdentifier()));
			//nodeView.setYPosition(cframe.get(nodeList.get(i).getIdentifier()));
	    	   
        }
		networkView.updateView();
	}
	
	
}