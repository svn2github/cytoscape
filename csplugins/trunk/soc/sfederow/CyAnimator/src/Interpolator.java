package CyAnimator;

import giny.model.Node;
import giny.view.NodeView;
import giny.model.Edge;
import giny.view.EdgeView;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.*;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;


public class Interpolator {
	List<FrameInterpolator> nodeInterpolators = new ArrayList<FrameInterpolator>();
	List<FrameInterpolator> edgeInterpolators = new ArrayList<FrameInterpolator>();
	List<FrameInterpolator> networkInterpolators = new ArrayList<FrameInterpolator>();
	
	public Interpolator(){
		
		nodeInterpolators.add(new interpolateNodePosition());
		nodeInterpolators.add(new interpolateNodeColor());
		nodeInterpolators.add(new interpolateNodeOpacity());

		edgeInterpolators.add(new interpolateEdgeColor());
		edgeInterpolators.add(new interpolateEdgeOpacity());

		networkInterpolators.add(new interpolateNetworkZoom());
		networkInterpolators.add(new interpolateNetworkColor());
		networkInterpolators.add(new interpolateNetworkCenter());
	
		//generateInterpolatedFrames(one, two, 10);
	}

	public CyFrame[] makeFrames(List<CyFrame> frameList) {

		if(frameList.size() == 0){ return null; }
		int framecount = frameList.size();
		for(int i=0; i<frameList.size()-1; i++){ framecount = framecount + frameList.get(i).getInterCount() - 1; }
		CyFrame[] cyFrameArray = new CyFrame[framecount]; //(frameList.size()-1)*framecount + 1];

		for(int i=0; i<cyFrameArray.length; i++){
			cyFrameArray[i] = new CyFrame(frameList.get(0).getCurrentNetwork());
		}

		int start = 0;
		int end = 0;

		for(int i=0; i < frameList.size()-1; i++) {
			framecount = frameList.get(i).getInterCount();
			end = start + framecount;
			cyFrameArray[start] = frameList.get(i);
			List<NodeView> nodeList = nodeViewUnionize(frameList.get(i), frameList.get(i+1));
			List<EdgeView> edgeList = edgeViewUnionize(frameList.get(i), frameList.get(i+1));

			for (int k = start+1; k < end; k++) {
				cyFrameArray[k].setNodeViewList(nodeList);
				cyFrameArray[k].setEdgeViewList(edgeList);
			}


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
	   		
		cyFrameArray[end] = frameList.get(frameList.size()-1);
	   	
		return cyFrameArray;
	}
	
	
	public CyFrame[] makeFrames(CyFrame frameOne, CyFrame frameTwo, int framenum) {
		// if(!frameOne.getCurrentNetwork().getIdentifier().equals(frameTwo.getCurrentNetwork().getIdentifier())){
			//throw new Exception("Frames cannot be interpolated across different networks.");
		// 	System.out.println("Frames cannot be interpolated across different networks.");
		// }
		
		
		CyNetwork currentNetwork = frameOne.getCurrentNetwork();
		
		
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
	
	
	public List<NodeView> nodeViewUnionize(CyFrame frameOne, CyFrame frameTwo){
		
		List<NodeView> list1 = frameOne.getNodeViewList();
		List<NodeView> list2 = frameTwo.getNodeViewList();
		Map<NodeView,NodeView> bigList = new HashMap<NodeView,NodeView>();	
		
		for (NodeView node: list1) {
			bigList.put(node, node);
		}

		for (NodeView node: list2) {
			bigList.put(node, node);
		}

		return new ArrayList<NodeView>(bigList.keySet());
	}
	
	public List<EdgeView> edgeViewUnionize(CyFrame frameOne, CyFrame frameTwo){
		
		List<EdgeView> list1 = frameOne.getEdgeViewList();
		List<EdgeView> list2 = frameTwo.getEdgeViewList();
		Map<EdgeView,EdgeView> bigList = new HashMap<EdgeView,EdgeView>();	

		for (EdgeView edge: list1) {
			bigList.put(edge, edge);
		}

		for (EdgeView edge: list2) {
			bigList.put(edge, edge);
		}
		
		return new ArrayList<EdgeView>(bigList.keySet());
		
	}

	public Color[] interpolateColor(Color colorOne, Color colorTwo, int framenum){
		Color[] paints = new Color[framenum+1];
		
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
		
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){

			List<Node> nodeList = valueList; //new ArrayList<FrameInterpolator>();

			int framenum = (stop-start) - 1;

			List<NodeView> nodeViewList = valueList;
		
			for(NodeView nv: nodeViewList){
				
				Node node = nv.getNode();

				String nodeid = node.getIdentifier();
				//Get the node positions and set up the position interpolation
				double[] xyOne = frameOne.getNodePosition(nodeid);
				double[] xyTwo = frameTwo.getNodePosition(nodeid);		
				if(xyOne == null && xyTwo == null){ continue; }

				// Handle missing (or appearing) nodes
				if (xyOne == null || xyTwo == null) {
					double[] xy = new double[2];
					if (xyOne == null)
						xy = xyTwo;
					else
						xy = xyOne;

					for(int k=1; k<framenum+1; k++) {
						cyFrameArray[start+k].setNodePosition(node.getIdentifier(), xy);
					}
					continue;
				}
				
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

					cyFrameArray[start+k].setNodePosition(node.getIdentifier(), xy);
				}

			}
			return cyFrameArray;
		}
	}
	
	class interpolateNodeColor implements FrameInterpolator {

		public interpolateNodeColor(){
			
		}
		
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){

			// if(!frameOne.getCurrentNetwork().getIdentifier().equals(frameTwo.getCurrentNetwork().getIdentifier())){
			// 	System.out.println("Frames Cannot be interpolated across different networks.");
			// }
			
			CyNetwork currentNetwork = frameOne.getCurrentNetwork();
			
			int framenum = (stop-start) - 1;
			
			List<NodeView> nodeViewList = valueList;
		
			for(NodeView nv: nodeViewList){
				
				Node node = nv.getNode();
				String nodeid = node.getIdentifier();
				
				Color colorOne = frameOne.getNodeColor(nodeid);
				Color colorTwo = frameTwo.getNodeColor(nodeid);
				if(colorOne == null && colorTwo == null){ continue; }

				// Handle missing (or appearing) nodes
				if (colorOne == null) 
					colorOne = colorTwo;
				else if (colorTwo == null)
					colorTwo = colorOne;
			
				if (colorOne == colorTwo) {
					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setNodeColor(node.getIdentifier(), colorOne);
					}	
				} else {
					Color[] paints = interpolateColor(colorOne, colorTwo, framenum);

					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setNodeColor(node.getIdentifier(), paints[k]);
					}	
				}
				
			}	
			return cyFrameArray;
		}
		
	}
	
	
	
	class interpolateNodeOpacity implements FrameInterpolator {
		
		public interpolateNodeOpacity(){
			
		}
		
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;	
			CyNetwork currentNetwork = frameOne.getCurrentNetwork();
			List<NodeView> nodeViewList = valueList;
		
			for(NodeView nv: nodeViewList){
				
				Node node = nv.getNode();
				String nodeid = node.getIdentifier();
				
				//Get the node transparencies and set up the transparency interpolation
				Integer transOne = frameOne.getNodeOpacity(nodeid);
				Integer transTwo = frameTwo.getNodeOpacity(nodeid);
				
				if (transOne == null) transOne = new Integer(0);
				if (transTwo == null) transTwo = new Integer(0);

				if (transOne.intValue() == transTwo.intValue()) {
					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setNodeOpacity(node.getIdentifier(), transOne);
					}
					continue;
				}
				
				int transIncLength = (transTwo - transOne)/framenum;
				int[] transArray = new int[framenum+2];
				transArray[1] = transOne + transIncLength;
				
				for(int k=1; k<framenum+1; k++){
					transArray[k+1] = transArray[k] + transIncLength;
					cyFrameArray[start+k].setNodeOpacity(node.getIdentifier(), transArray[k]);
				}	
				
			}
			return cyFrameArray;
		}
	}
	
	
	
	class interpolateEdgeColor implements FrameInterpolator {
		
		public interpolateEdgeColor(){
			
		}
	
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;	
			CyNetwork currentNetwork = frameOne.getCurrentNetwork();

			List<EdgeView> edgeViewList = valueList;
		
			for(EdgeView ev: edgeViewList){
				
				Edge edge = ev.getEdge();
			  String edgeid = edge.getIdentifier();
				
				Color colorOne = frameOne.getEdgeColor(edgeid);
			  Color colorTwo = frameTwo.getEdgeColor(edgeid);
				if(colorOne == null && colorTwo == null){ continue; }

				// Handle missing (or appearing) nodes
				if (colorOne == null) 
					colorOne = colorTwo;
				else if (colorTwo == null)
					colorTwo = colorOne;
			
				if (colorOne == colorTwo) {
					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setEdgeColor(edgeid, colorOne);
					}	
				} else {
					Color[] paints = interpolateColor(colorOne, colorTwo, framenum);

					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setEdgeColor(edgeid, paints[k]);
					}
				}
			}
			return cyFrameArray;
		}
	}
		
	class interpolateEdgeOpacity implements FrameInterpolator {
		public interpolateEdgeOpacity(){
			
		}
		
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;	
			CyNetwork currentNetwork = frameOne.getCurrentNetwork();
			List<EdgeView> edgeViewList = valueList;
		
			for(EdgeView ev: edgeViewList){
				
				Edge edge = ev.getEdge();
			  String edgeid = edge.getIdentifier();
				
				//Get the node transparencies and set up the transparency interpolation
				Integer transOne = frameOne.getEdgeOpacity(edgeid);
				Integer transTwo = frameTwo.getEdgeOpacity(edgeid);
				
				if (transOne == null) transOne = new Integer(0);
				if (transTwo == null) transTwo = new Integer(0);

				if (transOne.intValue() == transTwo.intValue()) {
					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setEdgeOpacity(edge.getIdentifier(), transOne);
					}
					continue;
				}
				
				int transIncLength = (transTwo - transOne)/framenum;
				int[] transArray = new int[framenum+2];
				transArray[1] = transOne + transIncLength;
				
				for(int k=1; k<framenum+1; k++){
					transArray[k+1] = transArray[k] + transIncLength;
					cyFrameArray[start+k].setEdgeOpacity(edge.getIdentifier(), transArray[k]);
				}	
				
			}
			return cyFrameArray;
		}
	}
	
	
	class interpolateNetworkZoom implements FrameInterpolator {
		
		public interpolateNetworkZoom(){
			
		}
	
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;
			
			double[] zoomValues = new double[framenum+2];
			zoomValues[0] = 0;
			zoomValues[1] = frameOne.getZoom();
			zoomValues[framenum+1] = frameTwo.getZoom();
			double zoomInc = Math.abs(frameOne.getZoom() - frameTwo.getZoom())/framenum;
			
			for(int k=1; k<framenum+1; k++){
				
				
				if(frameOne.getZoom() < frameTwo.getZoom()){
					zoomValues[k+1] = zoomValues[k] + zoomInc;
				}else{
					zoomValues[k+1] = zoomValues[k] - zoomInc;
				}
				
				cyFrameArray[start+k].setZoom(zoomValues[k+1]);
			}
			return cyFrameArray;
		}
	}
	
	
	
	class interpolateNetworkColor implements FrameInterpolator {
		
		public interpolateNetworkColor(){
			
		}
	
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){
	
			int framenum = (stop-start) - 1;
			
			Color colorOne = (Color)frameOne.getBackgroundPaint();
			Color colorTwo = (Color)frameTwo.getBackgroundPaint();
			Color[] paints = interpolateColor(colorOne, colorTwo, framenum);
			
			for(int k=1; k<framenum+1; k++){
				cyFrameArray[start+k].setBackgroundPaint(paints[k]);
			}
			return cyFrameArray;
		}
	}
	
	class interpolateNetworkCenter implements FrameInterpolator {
	
		public interpolateNetworkCenter(){}
		
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
                int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;
			
			double xone = frameOne.getCenterPoint().getX();
			double yone = frameOne.getCenterPoint().getY();
			
			double xtwo = frameTwo.getCenterPoint().getX();
			double ytwo = frameTwo.getCenterPoint().getY();
			
			double incrementLength = (xtwo - xone)/framenum;
			double[] xArray = new double[framenum+2];
			xArray[1] = xone;

			for(int k=1; k<framenum+1; k++){

				Point2D xy = new Point2D.Double(0, 0);
				
				xArray[k+1] = xArray[k] + incrementLength;
				//xy.setLocation(xArray[k], arg1)[0] = xArray[k];

				//Do the position interpolation
				if((xtwo - xone) == 0){
					xy.setLocation(xArray[k], yone);
				}else{

					double y = yone + ((xArray[k] - xone)*((ytwo-yone)/(xtwo -xone)));
					xy.setLocation(xArray[k], y);
				}

				cyFrameArray[start+k].setCenterPoint(xy);
			}
			
			return cyFrameArray;
		}
		
	}
}	
