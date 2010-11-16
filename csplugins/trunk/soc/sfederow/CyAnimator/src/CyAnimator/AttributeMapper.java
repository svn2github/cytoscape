package CyAnimator;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNode;
import cytoscape.visual.LineStyle;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.*;


public class AttributeMapper {

	private List<Node> nodeList = null;
	private CyAttributes cyNodeAttrs = null;
	private CyNetwork currentNetwork = null;
	private CyNetworkView networkView = null;
	private FrameManager frameManager = null;
	

	
	public AttributeMapper(){

		frameManager = new FrameManager();
		networkView = Cytoscape.getCurrentNetworkView();
		
		currentNetwork = Cytoscape.getCurrentNetwork();
		
		// Get our initial nodeList
		nodeList = currentNetwork.nodesList();

		
		cyNodeAttrs = Cytoscape.getNodeAttributes();
		String[] attNames = cyNodeAttrs.getAttributeNames();
		
		ArrayList<String> numNames = new ArrayList<String>();
		
		for(int i=0; i<attNames.length; i++){
			if(cyNodeAttrs.getType(attNames[i]) == 2){
				numNames.add(attNames[i]);
			}
			//System.out.println(attNames[i]+"\t"+cyNodeAttrs.getType(attNames[i]));
			
		}
		
		for(String name: numNames){
			//System.out.println(name);
		
		
			for(Node node: nodeList){
				NodeView nodeView = networkView.getNodeView(node);
				System.out.println(cyNodeAttrs.getAttribute(node.getIdentifier(), name));
				double nodeval = 1;
				try{
					nodeval = cyNodeAttrs.getDoubleAttribute(node.getIdentifier(), name);
				}catch(Exception excp) {
	    			continue; //System.out.println("hey"+excp.getMessage()); 
	    			
				}
		
					

				
				
				if(nodeval < 0){ nodeView.setUnselectedPaint(Color.RED); }
				else{ nodeView.setUnselectedPaint(Color.GREEN);}//origColor); }
				
				if(nodeval == 0 || nodeval < .01){ nodeval = 1; }
				nodeView.setHeight(Math.abs(nodeval)*100);
				nodeView.setWidth(Math.abs(nodeval)*100);
				
			}	
			
			frameManager.addKeyFrame();
		}
		
		ArrayList<CyFrame> adjFrames = new ArrayList<CyFrame>();
		ArrayList<CyFrame> frameList = frameManager.getKeyFrameList();
		for(CyFrame frame: frameList){
			frame.setInterCount(10);
			adjFrames.add(frame);
		}
		frameManager.setKeyFrameList(adjFrames);
		//frameManager.play();
		
	}

	
	public FrameManager getFrameManager(){
		return frameManager;
	}
}
