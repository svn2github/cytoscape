package src;

import giny.model.Node;
import giny.view.NodeView;
import giny.model.Edge;
import giny.view.EdgeView;

import java.awt.Color;
import java.awt.Paint;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.*;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;


public class Interpolator {

	

	
	public Interpolator(){
	
		//generateInterpolatedFrames(one, two, 10);
	}

	public CyFrame[] makeFrames(List<CyFrame> frameList){
		if(frameList.size() == 0){ return null; }
		int framecount = frameList.size();
		for(int i=0; i<frameList.size()-1; i++){ framecount = framecount + frameList.get(i).intercount; }
		CyFrame[] cyFrameArray = new CyFrame[framecount+1]; //(frameList.size()-1)*framecount + 1];
	   
		for(int i=0; i<cyFrameArray.length; i++){
   			cyFrameArray[i] = new CyFrame(frameList.get(0).currentNetwork);
   		}
	   	
		List<FrameInterpolator> nodeInterpolators = new ArrayList<FrameInterpolator>();
		List<FrameInterpolator> edgeInterpolators = new ArrayList<FrameInterpolator>();
		List<FrameInterpolator> networkInterpolators = new ArrayList<FrameInterpolator>();
		
		nodeInterpolators.add(new interpolateNodePosition());
		nodeInterpolators.add(new interpolateNodeColor());
		nodeInterpolators.add(new interpolateNodeOpacity());
		edgeInterpolators.add(new interpolateEdgeColor());
		networkInterpolators.add(new interpolateNetworkZoom());
		networkInterpolators.add(new interpolateNetworkColor());
		//networkInterpolators.add(new interpolateNetworkPosition());
		
		int start = 0;
		int end = 0;
	    //for(int i=0; i < frameList.size()-1; i++){
	   		
	   	    //framecount = frameList.get(i).intercount;
	   		//end = start + framecount;
	   		//cyFrameArray[start] = frameList.get(i);

   		for(int i=0; i < frameList.size()-1; i++){
   		
   			framecount = frameList.get(i).intercount;
	   	    end = start + framecount;
	   	    cyFrameArray[start] = frameList.get(i);
	        List<Node> nodeList = nodeUnionize(frameList.get(i), frameList.get(i+1));
	 	    List<Edge> edgeList = edgeUnionize(frameList.get(i), frameList.get(i+1));
	 	    
	 	    
	   	      for(FrameInterpolator interp: nodeInterpolators){
	   	    	  cyFrameArray = interp.interpolate(nodeList, frameList.get(i), frameList.get(i+1), start, end, cyFrameArray);
	   	      }

	   	      for(FrameInterpolator interp: edgeInterpolators){
	   	    	  cyFrameArray = interp.interpolate(edgeList, frameList.get(i), frameList.get(i+1), start, end, cyFrameArray);
	   	      }

	   	      for(FrameInterpolator interp: networkInterpolators){
	   	    	  cyFrameArray = interp.interpolate(nodeList, frameList.get(i), frameList.get(i+1), start, end, cyFrameArray);
	   	      }
	   	      
	   	    
	   	      start = end;
	     }
	   		
	   		//cyFrameArray = interpolateNodes(frameList.get(i), frameList.get(i+1), start, end, cyFrameArray);
	   		//cyFrameArray = interpolateNodeColor(frameList.get(i), frameList.get(i+1), start, end, cyFrameArray);
	   		//cyFrameArray = interpolateNetwork(frameList.get(i), frameList.get(i+1), start, end, cyFrameArray);
	   		//cyFrameArray = interpolateEdges(frameList.get(i), frameList.get(i+1), start, end, cyFrameArray);
	   		
	   		//start = end;
	   	//}
	   	cyFrameArray[end] = frameList.get(frameList.size()-1);
	   	
		return cyFrameArray;
	}
	
	
	public CyFrame[] makeFrames(CyFrame frameOne, CyFrame frameTwo, int framenum){
		if(!frameOne.currentNetwork.getIdentifier().equals(frameTwo.currentNetwork.getIdentifier())){
			//throw new Exception("Frames cannot be interpolated across different networks.");
			System.out.println("Frames cannot be interpolated across different networks.");
		}
		
		
		CyNetwork currentNetwork = frameOne.currentNetwork;
		
		
		CyFrame[] cyFrameArray = new CyFrame[framenum+2];
		cyFrameArray[0] = frameOne;
		cyFrameArray[framenum+1] = frameTwo;
		for(int i=1; i<framenum+1; i++){
			cyFrameArray[i] = new CyFrame(currentNetwork);
		}
		
		//cyFrameArray = interpolatePosition(frameOne, frameTwo, 0, framenum+2, cyFrameArray);
		//cyFrameArray = interpolateNodeColor(frameOne, frameTwo, 0, framenum+2, cyFrameArray);
		
	
		return cyFrameArray;
		
	}
	
	
	
	
	public List<Node> nodeUnionize(CyFrame frameOne, CyFrame frameTwo){
		
		List<Node> list1 = frameOne.nodeList;
		List<Node> list2 = frameTwo.nodeList;
		List<Node> bigList = new ArrayList<Node>();	
		
	    Iterator<Node> list1iter = list1.iterator();
	    Iterator<Node> list2iter = list2.iterator();
	    while(list1iter.hasNext() && list2iter.hasNext()){
	    	Node node1 = list1iter.next();
	    	Node node2 = list2iter.next();
	 
	    	/*
	    	if(node1 == null && node2 != null){ 
	    		bigList.add(node2); 
	    		continue;
	    	}
	    	if(node1 != null && node2 == null){ 
	    		bigList.add(node1); 
	    		continue;
	    	}
			*/

	    	if(node1 == node2){ 
	    		bigList.add(node1); }
	    	else{
	    		bigList.add(node1);
	    		bigList.add(node2);
	    	}
	    }
	    while(list1iter.hasNext()){ bigList.add(list1iter.next()); }
	    while(list2iter.hasNext()){ bigList.add(list2iter.next()); }
	    
	    return bigList;
	}
	
	public List<Edge> edgeUnionize(CyFrame frameOne, CyFrame frameTwo){
		
		List<Edge> list1 = frameOne.edgeList;
		List<Edge> list2 = frameTwo.edgeList;
		List<Edge> bigList = new ArrayList<Edge>();	
		
	    Iterator<Edge> list1iter = list1.iterator();
	    Iterator<Edge> list2iter = list2.iterator();
	    while(list1iter.hasNext() && list2iter.hasNext()){
	    	Edge edge1 = list1iter.next();
	    	Edge edge2 = list2iter.next();
	 
	    	/*
	    	if(edge1 == null && edge2 != null){ 
	    		bigList.add(edge2); 
	    		continue;
	    	}
	    	if(edge1 != null && edge2 == null){ 
	    		bigList.add(edge1); 
	    		continue;
	    	}
			*/

	    	if(edge1 == edge2){ 
	    		bigList.add(edge1); }
	    	else{
	    		bigList.add(edge1);
	    		bigList.add(edge2);
	    	}
	    }
	    while(list1iter.hasNext()){ bigList.add(list1iter.next()); }
	    while(list2iter.hasNext()){ bigList.add(list2iter.next()); }
	    
	    return bigList;
		
	}
	
	
	
	
	
	
	public CyFrame[] interpolateNodes(CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){ // throws Exception {
		
		if(!frameOne.currentNetwork.getIdentifier().equals(frameTwo.currentNetwork.getIdentifier())){
			//throw new Exception("Frames cannot be interpolated across different networks.");
			System.out.println("Frames cannot be interpolated across different networks.");
		}
		
		int framenum = (stop-start) - 1;
		
		CyNetwork currentNetwork = frameOne.currentNetwork;
		
		/*
		CyFrame[] cyFrameArray = new CyFrame[framenum+2];
		cyFrameArray[0] = frameOne;
		cyFrameArray[framenum+1] = frameTwo;
		for(int i=1; i<framenum+1; i++){
			cyFrameArray[i] = new CyFrame(currentNetwork);
		}
		*/
		
		
		List<Node> nodeList = currentNetwork.nodesList();
		
		
		 
			
		for(Node node: nodeList){
			
			
			
			String nodeid = node.getIdentifier();
			
			//Get the node colors and do the color interpolation
			Paint colorOne = frameOne.nodeColMap.get(nodeid);
		    Paint colorTwo = frameTwo.nodeColMap.get(nodeid);
		    Paint[] paints = interpolateColor(colorOne, colorTwo, framenum);
		    
		    
		    //Get the node transparencies and set up the transparency interpolation
			float[] transOne = frameOne.nodeOpacityMap.get(nodeid);
			float[] transTwo = frameTwo.nodeOpacityMap.get(nodeid);
			float transIncLength = Math.abs(transTwo[0] - transOne[0])/framenum;
			float[] transArray = new float[framenum+2];
			transArray[1] = transOne[0] + transIncLength;
			
			
			//Get the node positions and set up the position interpolation
			double[] xyOne = frameOne.nodePosMap.get(nodeid);
		    double[] xyTwo = frameTwo.nodePosMap.get(nodeid);				
			double incrementLength = (xyTwo[0] - xyOne[0])/framenum;
			double[] xArray = new double[framenum+2];
			xArray[1] = xyOne[0] + incrementLength;
			
			
			
			for(int k=1; k<framenum+1; k++){
				
				
				double[] xy = new double[2];
				xy[0] = 0;
				xy[1] = 0;
				
				xArray[k+1] = xArray[k] + incrementLength;
				xy[0] = xArray[k];
				
				//Do the position interpolation
				if((xyTwo[0] - xyOne[0]) == 0){
					xy[1] = xyOne[1]; 
				}else{
					
					xy[1] = xyOne[1] + ((xArray[k] - xyOne[0])*((xyTwo[1]-xyOne[1])/(xyTwo[0] - xyOne[0])));
					
				}
				
				//Do the transparency interpolation
				float[] tran = new float[1];
				if(transOne[0] < transTwo[0]){
					transArray[k+1] = transArray[k] + transIncLength;
				}else{ 
					if(transOne[0] > transTwo[0]){
						transArray[k+1] = transArray[k] - transIncLength;
					}
				}
				tran[0] = transArray[k];
				
			
				//System.out.println(k+"  "+xy[0]+"  "+xy[1]);
				cyFrameArray[start+k].nodePosMap.put(node.getIdentifier(), xy);
				cyFrameArray[start+k].nodeColMap.put(node.getIdentifier(), paints[k]);
				cyFrameArray[start+k].nodeOpacityMap.put(node.getIdentifier(), tran);
				//Paint oldpaint = frameOne.nodeColMap.get(node.getIdentifier());
				//cyFrameArray[k].nodeColMap.put(node.getIdentifier(), oldpaint);
			
			}	
			
		}	
		
		return cyFrameArray;
	}
	/*
	public CyFrame[] interpolateNodeColor(CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){
		
		if(!frameOne.currentNetwork.getIdentifier().equals(frameTwo.currentNetwork.getIdentifier())){
			System.out.println("Frames Cannot be interpolated across different networks.");
		}
		
		CyNetwork currentNetwork = frameOne.currentNetwork;
		
		int framenum = (stop-start) - 1;
		
		List<Node> nodeList = currentNetwork.nodesList();
	
		for(Node node: nodeList){
			
			String nodeid = node.getIdentifier();
			
			Paint colorOne = frameOne.nodeColMap.get(nodeid);
		    Paint colorTwo = frameTwo.nodeColMap.get(nodeid);
			float[] transOne = frameOne.nodeTransMap.get(nodeid);
			float[] transTwo = frameTwo.nodeTransMap.get(nodeid);
			
		    /*
		     * ##Ask scooter: would the missing node in the 
		     
		    
		   
			
		    Paint[] paints = interpolateColor(colorOne, colorTwo, framenum);
		    
			for(int k=1; k<framenum+1; k++){
				
		        //Paint col = new Color(rArray[k+1], gArray[k+1], bArray[k+1]);		
				Paint col = paints[k];
				//cyFrameArray[k].nodePosMap.put(node.getIdentifier(), xy);
				//Paint oldpaint = frameOne.nodeColMap.get(node.getIdentifier());
				cyFrameArray[start+k].nodeColMap.put(node.getIdentifier(), col);
			
			}	
			
		}	
		
		return cyFrameArray;
	}
	*/
	
	public CyFrame[] interpolateEdges(CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){

		CyNetwork currentNetwork = frameOne.currentNetwork;
		
		List<Edge> edgeList = currentNetwork.edgesList();
		
		for(Edge edge: edgeList){
			
			Paint colorOne = frameOne.edgeColMap.get(edge.getIdentifier());
		    Paint colorTwo = frameTwo.edgeColMap.get(edge.getIdentifier());
			
		    /*
		     * ##Ask scooter: would the missing node in the 
		     */
		    int framenum = (stop-start) - 1;
		   
			
		    Paint[] paints = interpolateColor(colorOne, colorTwo, framenum);
		    
			for(int k=1; k<framenum+1; k++){
				
		        //Paint col = new Color(rArray[k+1], gArray[k+1], bArray[k+1]);		
				Paint col = paints[k];
				//cyFrameArray[k].nodePosMap.put(node.getIdentifier(), xy);
				//Paint oldpaint = frameOne.nodeColMap.get(node.getIdentifier());
				cyFrameArray[start+k].edgeColMap.put(edge.getIdentifier(), col);
			
			}	
			
		}
		return cyFrameArray;
	}	
	
	
	public CyFrame[] interpolateNetwork(CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){
		
		Paint colorOne = frameOne.backgroundPaint;
		Paint colorTwo = frameTwo.backgroundPaint;
		
		int framenum = (stop-start) - 1;
		
		Paint[] paints = interpolateColor(colorOne, colorTwo, framenum);
		double[] zoomValues = new double[framenum+2];
		zoomValues[0] = 0;
		zoomValues[1] = frameOne.zoom;
		zoomValues[framenum+1] = frameTwo.zoom;
		double zoomInc = Math.abs(frameOne.zoom - frameTwo.zoom)/framenum;
		
		for(int k=1; k<framenum+1; k++){
			
			
			if(frameOne.zoom < frameTwo.zoom){
				zoomValues[k+1] = zoomValues[k] + zoomInc;
			}else{
				zoomValues[k+1] = zoomValues[k] - zoomInc;
			}
			
			cyFrameArray[start+k].zoom = zoomValues[k+1];
			cyFrameArray[start+k].backgroundPaint = paints[k];
		}
		
		return cyFrameArray;
	}

	
	
	public Paint[] interpolateColor(Paint colorOne, Paint colorTwo, int framenum){
		Paint[] paints = new Color[framenum+1];
		
		 Pattern p = Pattern.compile("java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
			Matcher cOne = p.matcher(colorOne.toString());
		    Matcher cTwo = p.matcher(colorTwo.toString());
			
		    int red1 = 0; 
		    int green1 = 0; 
		    int blue1 = 0;
		    
		    int red2 = 0;
		    int green2 = 0;
		    int blue2 = 0;
		    
		    if(cOne.matches()){
				red1 = Integer.parseInt(cOne.group(1));
				green1 = Integer.parseInt(cOne.group(2));
				blue1 = Integer.parseInt(cOne.group(3));
			}
			if(cTwo.matches()){
				red2 = Integer.parseInt(cTwo.group(1));
				green2 = Integer.parseInt(cTwo.group(2));
				blue2 = Integer.parseInt(cTwo.group(3));
			}
		    
			
		    int rIncLen = Math.round((Math.abs(red1 - red2))/framenum);
			int gIncLen = Math.round((Math.abs(green1 - green2))/framenum);
			int bIncLen = Math.round((Math.abs(blue1 - blue2))/framenum);
		   
			
			int[] rArray = new int[framenum+2];
			int[] gArray = new int[framenum+2];
			int[] bArray = new int[framenum+2];
		    
			rArray[0] = 0;
			gArray[0] = 0;
			bArray[0] = 0;
			
			rArray[1] = red1;// + rIncLen;
			rArray[framenum+1] = red2;
			gArray[1] = green1;// + gIncLen;
			gArray[framenum+1] = green2;
			bArray[1] = blue1 ;//+ bIncLen;
			bArray[framenum+1] = blue2;
			
			for(int k=1; k<framenum+1; k++){
				
				if(red1 < red2){	
					rArray[k+1] = rArray[k] + rIncLen;
				}else{
					if((rArray[k] - rIncLen) > 0){
						rArray[k+1] = rArray[k] - rIncLen;
					}
				}
				if(green1 < green2){	
					gArray[k+1] = gArray[k] + gIncLen;
				}else{
					if((gArray[k] - gIncLen) > 0){
						gArray[k+1] = gArray[k] - gIncLen;
					}	
				}
				if(blue1 < blue2){	
					bArray[k+1] = bArray[k] + bIncLen;
				}else{
					if((bArray[k] - bIncLen) > 0){
						bArray[k+1] = bArray[k] - bIncLen;
					}	
				}
				
		        paints[k] = new Color(rArray[k+1], gArray[k+1], bArray[k+1]);	
		        
			}
			
				
		
		return paints;
	}

	
	class interpolateNodePosition implements FrameInterpolator {
		
		public interpolateNodePosition(){
			
		}
		
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){

			List<Node> nodeList = valueList; //new ArrayList<FrameInterpolator>();

			int framenum = (stop-start) - 1;

			for(Node node: nodeList){

				String nodeid = node.getIdentifier();
				//Get the node positions and set up the position interpolation
				double[] xyOne = frameOne.nodePosMap.get(nodeid);
				double[] xyTwo = frameTwo.nodePosMap.get(nodeid);		
				if(xyOne == null || xyTwo == null){ continue; }
				
				double incrementLength = (xyTwo[0] - xyOne[0])/framenum;
				double[] xArray = new double[framenum+2];
				xArray[1] = xyOne[0] + incrementLength;


				for(int k=1; k<framenum+1; k++){


					double[] xy = new double[2];
					xy[0] = 0;
					xy[1] = 0;

					xArray[k+1] = xArray[k] + incrementLength;
					xy[0] = xArray[k];

					//Do the position interpolation
					if((xyTwo[0] - xyOne[0]) == 0){
						xy[1] = xyOne[1]; 
					}else{

						xy[1] = xyOne[1] + ((xArray[k] - xyOne[0])*((xyTwo[1]-xyOne[1])/(xyTwo[0] - xyOne[0])));
					}

					cyFrameArray[start+k].nodePosMap.put(node.getIdentifier(), xy);
				}
				
			
			}
			return cyFrameArray;
		}
	}
	
	class interpolateNodeColor implements FrameInterpolator {
		
		
		public interpolateNodeColor(){
			
		}
		
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){

			if(!frameOne.currentNetwork.getIdentifier().equals(frameTwo.currentNetwork.getIdentifier())){
				System.out.println("Frames Cannot be interpolated across different networks.");
			}
			
			CyNetwork currentNetwork = frameOne.currentNetwork;
			
			int framenum = (stop-start) - 1;
			
			List<Node> nodeList = valueList;
		
			for(Node node: nodeList){
				
				String nodeid = node.getIdentifier();
				
				Paint colorOne = frameOne.nodeColMap.get(nodeid);
			    Paint colorTwo = frameTwo.nodeColMap.get(nodeid);
				if(colorOne == null || colorTwo == null){ continue; }
			    
			    Paint[] paints = interpolateColor(colorOne, colorTwo, framenum);
			    
				for(int k=1; k<framenum+1; k++){
					cyFrameArray[start+k].nodeColMap.put(node.getIdentifier(), paints[k]);
				}	
				
			}	
			return cyFrameArray;
		}
		
	}
	
	
	
	class interpolateNodeOpacity implements FrameInterpolator {
		
		public interpolateNodeOpacity(){
			
		}
		
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;	
			CyNetwork currentNetwork = frameOne.currentNetwork;
			List<Node> nodeList = valueList;
			
			for(Node node: nodeList){
				
			    String nodeid = node.getIdentifier();
				
				//Get the node transparencies and set up the transparency interpolation
				float[] transOne = frameOne.nodeOpacityMap.get(nodeid);
				float[] transTwo = frameTwo.nodeOpacityMap.get(nodeid);
				
				
				if(transOne == null){ transOne = new float[1]; }
				if(transTwo == null){ transTwo = new float[1]; }
				
				float transIncLength = Math.abs(transTwo[0] - transOne[0])/framenum;
				float[] transArray = new float[framenum+2];
				transArray[1] = transOne[0] + transIncLength;
				
				for(int k=1; k<framenum+1; k++){
					
					float[] opacity = new float[1];
					if(transOne[0] < transTwo[0]){
						transArray[k+1] = transArray[k] + transIncLength;
					}else{ 
						if(transOne[0] > transTwo[0]){
							transArray[k+1] = transArray[k] - transIncLength;
						}
					}
					opacity[0] = transArray[k];
			    	
					cyFrameArray[start+k].nodeOpacityMap.put(node.getIdentifier(), opacity);
				}	
				
			}
			return cyFrameArray;
		}
	}
	
	
	
	class interpolateEdgeColor implements FrameInterpolator {
		
		public interpolateEdgeColor(){
			
		}
	
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;	
			CyNetwork currentNetwork = frameOne.currentNetwork;
			List<Edge> edgeList = valueList;
			
			for(Edge edge: edgeList){
				
				Paint colorOne = frameOne.edgeColMap.get(edge.getIdentifier());
			    Paint colorTwo = frameTwo.edgeColMap.get(edge.getIdentifier());
				if(colorOne == null || colorTwo == null){ continue; }
				
			    Paint[] paints = interpolateColor(colorOne, colorTwo, framenum);
			    
				for(int k=1; k<framenum+1; k++){
					cyFrameArray[start+k].edgeColMap.put(edge.getIdentifier(), paints[k]);
				
				}		
			}
			return cyFrameArray;
		}
	}

	
	
	class interpolateNetworkZoom implements FrameInterpolator {
		
		public interpolateNetworkZoom(){
			
		}
	
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;
			
			double[] zoomValues = new double[framenum+2];
			zoomValues[0] = 0;
			zoomValues[1] = frameOne.zoom;
			zoomValues[framenum+1] = frameTwo.zoom;
			double zoomInc = Math.abs(frameOne.zoom - frameTwo.zoom)/framenum;
			
			for(int k=1; k<framenum+1; k++){
				
				
				if(frameOne.zoom < frameTwo.zoom){
					zoomValues[k+1] = zoomValues[k] + zoomInc;
				}else{
					zoomValues[k+1] = zoomValues[k] - zoomInc;
				}
				
				cyFrameArray[start+k].zoom = zoomValues[k+1];
			}
			return cyFrameArray;
		}
	}
	
	
	
	class interpolateNetworkColor implements FrameInterpolator {
		
		public interpolateNetworkColor(){
			
		}
	
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, int start, int stop, CyFrame[] cyFrameArray){
	
			int framenum = (stop-start) - 1;
			
			
			Paint colorOne = frameOne.backgroundPaint;
			Paint colorTwo = frameTwo.backgroundPaint;
			Paint[] paints = interpolateColor(colorOne, colorTwo, framenum);
			
			for(int k=1; k<framenum+1; k++){
				cyFrameArray[start+k].backgroundPaint = paints[k];
			}
			return cyFrameArray;
		}
	}	
}	