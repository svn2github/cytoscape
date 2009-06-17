package src;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.Paint;
import java.util.*;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;


public class LinearInterpolator {

	
	
	
	public LinearInterpolator(){
	
		
		//generateInterpolatedFrames(one, two, 10);
		
		
	}
	
	
	public CyFrame[] generateInterpolatedFrames(CyFrame frameOne, CyFrame frameTwo, int framenum){
		
		if(!frameOne.currentNetwork.getIdentifier().equals(frameTwo.currentNetwork.getIdentifier())){
			System.out.println("Frames Cannot be interpolated across different networks.");
		}
		
		
		CyNetwork currentNetwork = frameOne.currentNetwork;
		
		
		CyFrame[] cyFrameArray = new CyFrame[framenum+2];
		cyFrameArray[0] = frameOne;
		cyFrameArray[framenum+1] = frameTwo;
		for(int i=1; i<framenum+1; i++){
			cyFrameArray[i] = new CyFrame(currentNetwork);
		}
		
		
		
		List<Node> nodeList = currentNetwork.nodesList();
		
		
		
		 
			
		for(int i=0; i<nodeList.size(); i++){
			
			
			double[] xyOne = frameOne.nodePosMap.get(nodeList.get(i).getIdentifier());
		    double[] xyTwo = frameTwo.nodePosMap.get(nodeList.get(i).getIdentifier());
					
			double incrementLength = (xyTwo[0] - xyOne[0])/framenum;
				
			double[] xArray = new double[framenum+1];
			
			xArray[0] = xyOne[0] + incrementLength;
			System.out.println("Node ID: "+nodeList.get(i).getIdentifier());
			
			for(int k=1; k<framenum+1; k++){
				
				double[] xy = new double[2];
				xy[0] = 0;
				xy[1] = 0;
				
				xArray[k] = xArray[k-1] + incrementLength;
				
				xy[0] = xArray[k];
				
				
				
				if((xyTwo[0] - xyOne[0]) == 0){
					xy[1] = xyOne[1]; 
				}else{
					
					xy[1] = xyOne[1] + ((xArray[k] - xyOne[0])*((xyTwo[1]-xyOne[1])/(xyTwo[0] - xyOne[0])));
					
				}
				//System.out.println(xy[0]+"  "+xy[1]);
				
				
				
			
				
				System.out.println(k+"  "+xy[0]+"  "+xy[1]);
				cyFrameArray[k].nodePosMap.put(nodeList.get(i).getIdentifier(), xy);
				Paint oldpaint = frameOne.nodeColMap.get(nodeList.get(i).getIdentifier());
				cyFrameArray[k].nodeColMap.put(nodeList.get(i).getIdentifier(), oldpaint);
			
			}	
			
		}	
		
		return cyFrameArray;
	}
	
	
}
