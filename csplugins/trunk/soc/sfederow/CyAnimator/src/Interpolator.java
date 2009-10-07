/*
 * File: AttributeSaverDialog.java
 * Google Summer of Code
 * Written by Steve Federowicz with help from Scooter Morris
 * 
 * The Interpolator is what make the animations go smoothly. It works by taking a list of the key frames for the animation from which it
 * determines how many frames will be in the final animation after interpolation. It then creates an array of CyFrames which gets "filled"
 * with all of the interpolation data as it is generated. This works by creating lists of FrameInterpolators which is a generic interface
 * (FrameInterpolator.java) that has only one method, interpolate(). There are then many inner classes in Interpolator.java which implement
 * FrameInterpolator and do the interpolation of a single visual property. For example there is currently interpolateNodeColor, interpolateNodeOpacity
 * interpolateNodePosition, interpolateEdgeColor, interpolateNetworkColor etc... Thus the design is such that many interpolators can ultimately
 * be made and swapped in or out at will. After the set of interpolators are decided, they are iterated through and all of the NodeView, EdgeView,
 * and NetworkView data is interpolated appropriately for each frame in the frame array. 
 * 
 * 
 */


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
	
	/*
	 * These lists of FrameInterpolators define the set of visual properties
	 * which will be interpolated for and updated in the CyNetworkView during
	 * the animation.
	 */
	List<FrameInterpolator> nodeInterpolators = new ArrayList<FrameInterpolator>();
	List<FrameInterpolator> edgeInterpolators = new ArrayList<FrameInterpolator>();
	List<FrameInterpolator> networkInterpolators = new ArrayList<FrameInterpolator>();
	
	public Interpolator(){
		
		//add any desired interpolators to their respective interpolator lists
		nodeInterpolators.add(new interpolateNodePosition());
		nodeInterpolators.add(new interpolateNodeColor());
		nodeInterpolators.add(new interpolateNodeOpacity());
		nodeInterpolators.add(new interpolateNodeSize());
		nodeInterpolators.add(new interpolateNodeBorderWidth());

		edgeInterpolators.add(new interpolateEdgeColor());
		edgeInterpolators.add(new interpolateEdgeOpacity());
		edgeInterpolators.add(new interpolateEdgeWidth());

		networkInterpolators.add(new interpolateNetworkZoom());
		networkInterpolators.add(new interpolateNetworkColor());
		networkInterpolators.add(new interpolateNetworkCenter());
	
		
	}

	/**
	 * This is the driver method which takes a list of key frames and runs the frames
	 * in sets of two through the interpolators to generate the intermediate frames.
	 * 
	 * @param frameList is a list of CyFrames which are key frames in the animation
	 * @return an array of CyFrames which contains each of the key frames with the interpolated frames appropriately interspersed
	 */
	public CyFrame[] makeFrames(List<CyFrame> frameList) {

		if(frameList.size() == 0){ return null; }
		
		//initialize the framecount to the number of key frames
		int framecount = frameList.size();
		
		//add on the number of frames to be interpolated
		for(int i=0; i<frameList.size()-1; i++){ 
			
			//each frame contains the number of frames which will be interpolated after it which is the interCount
			framecount = framecount + frameList.get(i).getInterCount() - 1; 
		}
		
		//create the main CyFrame array which will then be run through all of the interpolators
		CyFrame[] cyFrameArray = new CyFrame[framecount]; //(frameList.size()-1)*framecount + 1];

		//initialize the CyFrame array
		for(int i=0; i<cyFrameArray.length; i++){
			cyFrameArray[i] = new CyFrame(frameList.get(0).getCurrentNetwork());
		}

		int start = 0;
		int end = 0;

		/*
		 * Runs through the key frame list and adds to the CyFrame array by interpolating between 
		 * two frames at a time in succession. For example it might take key frame one and key frame 
		 * two and then interpolate the node visual properties from frame one to frame two, then the
		 * edge visual properties from frame one to two etc.. It then does the same thing for frame
		 * two and frame three, then frame three and frame four, etc... until the key frames are fully
		 * interpolated.
		 */
		for(int i=0; i < frameList.size()-1; i++) {
			
			//set framecount for this round of interpolation
			framecount = frameList.get(i).getInterCount();
			
			//set ending point for frames to be made
			end = start + framecount;
			
			//set the first frame to the the first key frame
			cyFrameArray[start] = frameList.get(i);
			List<NodeView> nodeList = nodeViewUnionize(frameList.get(i), frameList.get(i+1));
			List<EdgeView> edgeList = edgeViewUnionize(frameList.get(i), frameList.get(i+1));

			//reset the nodeLists once the unionizer has updated them
			for (int k = start+1; k < end; k++) {
				cyFrameArray[k].setNodeViewList(nodeList);
				cyFrameArray[k].setEdgeViewList(edgeList);
			}

			/*
			 * Interpolates all of the node, edge, and network visual properties, this happens by 
			 * iterating through the respective lists of FrameInterpolators which are classes that
			 * implement FrameInterpolator.  This allows for modularization of the interpolation as
			 * you can easily change which FrameInterpolators are in the node, edge, and network 
			 * interpolation lists.
			 */
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
	
	
	/**
	 * Takes two CyFrames and returns a list of NodeViews which is the union of the list of 
	 * NodeViews that are in each of the two frames.  This is done to accomodate the adding/deleting
	 * of nodes between frames in animation as the union provides a complete set of nodes when
	 * moving across frames.
	 * 
	 * @param frameOne is the first of two frames to be unionized
	 * @param frameTwo is the second of two frames to be unionized
	 * @return the unionized list of NodeViews
	 */
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
	
	
	/**
	 * Takes two CyFrames and returns the union of the EdgeView lists that are contained
	 * within each frame.  This is to ensure that when edges are added/deleted they will
	 * be able to be interpolated from one frame to the next instead of just instantly
	 * disappearing.
	 * 
	 * @param frameOne is the first frame whose edge list will be unionized
	 * @param frameTwo is the second frame whose edge list will be unionized
	 * @return the unionized list of EdgeViews
	 * 
	 */
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

	
	/**
	 * This method performs a generic color interpolation and is used by many of the interpolators
	 * to do their color interpolations.  It simply takes the absolute difference between the R, G, and B
	 * values from the two colors, divides the difference by the number of intervals which will
	 * be interpolated, and then eithers adds or subtracts the appropriate amount for each R, G, and B
	 * value and creates a new color which is placed in the color array.  The color array thus has colorOne
	 * as its first value and colorTwo as its last value with the interpolated colors filling the middle of
	 * the array.
	 * 
	 * @param colorOne is the color to be interpolated from
	 * @param colorTwo is the color to be interpolated to
	 * @param framenum is the number or frames which need to be interpolated for and thus the length of the color array
	 * @return the array of interpolated colors 
	 * 
	 */
	public Color[] interpolateColor(Color colorOne, Color colorTwo, int framenum){
		Color[] paints = new Color[framenum+1];

		//set up regex for RGB values
		Pattern p = Pattern.compile("java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
		Matcher cOne = p.matcher(colorOne.toString());
		Matcher cTwo = p.matcher(colorTwo.toString());

		int red1 = 0; 
		int green1 = 0; 
		int blue1 = 0;

		int red2 = 0;
		int green2 = 0;
		int blue2 = 0;

		//match RBG values for colorOne
		if(cOne.matches()){
			red1 = Integer.parseInt(cOne.group(1));
			green1 = Integer.parseInt(cOne.group(2));
			blue1 = Integer.parseInt(cOne.group(3));
		}
		
		//match RGB values for colorTwo
		if(cTwo.matches()){
			red2 = Integer.parseInt(cTwo.group(1));
			green2 = Integer.parseInt(cTwo.group(2));
			blue2 = Integer.parseInt(cTwo.group(3));
		}

		//Set up the increment lengths for each RGB values
		int rIncLen = Math.round((Math.abs(red1 - red2))/framenum);
		int gIncLen = Math.round((Math.abs(green1 - green2))/framenum);
		int bIncLen = Math.round((Math.abs(blue1 - blue2))/framenum);

		//arrays which will hold the RGB values at each increment, these arrays are parallel to the Color[]
		int[] rArray = new int[framenum+2];
		int[] gArray = new int[framenum+2];
		int[] bArray = new int[framenum+2];

		rArray[0] = 0;
		gArray[0] = 0;
		bArray[0] = 0;

		/*
		 * Initialize the RGB arrays, start of the array contains the value from colorOne, 
		 * end of the arrays contain the value from colorTwo.
		 */
		rArray[1] = red1;// + rIncLen;
		rArray[framenum+1] = red2;
		gArray[1] = green1;// + gIncLen;
		gArray[framenum+1] = green2;
		bArray[1] = blue1 ;//+ bIncLen;
		bArray[framenum+1] = blue2;

		
		//fill the middle of the RGB arrays
		for(int k=1; k<framenum+1; k++){

			//general strategy is if red1 is less than red2 increment, else decrement
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
			
			//create the new color and put it in the Color[]
			paints[k] = new Color(rArray[k+1], gArray[k+1], bArray[k+1]);	

		}

		return paints;
	}

	
	/**
	 * Interpolates the node position, using the standard linear interpolation formula described
	 * at http://en.wikipedia.org/wiki/Linear_interpolation. It essentially just finds the absolute
	 * difference between the position of a node in frame one, and in frame two.  It then divides
	 * this distance by the number of frames which will be interpolated and increments or decrements
	 * from the node position in the first frame to the node position in the second.  The incrementing
	 * is done on the x values, which are then plugged into the interpolation formula to generate a y-value.
	 * 
	 */
	
	class interpolateNodePosition implements FrameInterpolator {
		
		public interpolateNodePosition(){
			
		}
		
		/**
		 * Performs the interpolation.
		 *  
		 * @param valueList is in this case a list of NodeViews
		 * @param frameOne is the frame to be interpolated from
		 * @param frameTwo is the frame to be interpolated to
		 * @param start is the starting position of the frame in the CyFrame array
		 * @param end is the ending positiong of the interpolation in the CyFrame array
		 * @param cyFrameArray is the array of CyFrames which gets populated with the interpolated data
		 * @return the array of CyFrames filled with interpolated node position data
		 */
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){

			List<Node> nodeList = valueList; 

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
	
	/**
	 * Fills in the interpolated color values for NodeViews.  Works by using the inner
	 * interpolateColor() method.
	 * 
	 */
	class interpolateNodeColor implements FrameInterpolator {

		public interpolateNodeColor(){
			
		}
		
		
		/**
		 * Performs the interpolation.
		 *  
		 * @param valueList is in this case a list of NodeViews
		 * @param frameOne is the frame to be interpolated from
		 * @param frameTwo is the frame to be interpolated to
		 * @param start is the starting position of the frame in the CyFrame array
		 * @param end is the ending positiong of the interpolation in the CyFrame array
		 * @param cyFrameArray is the array of CyFrames which gets populated with the interpolated data
		 * @return the array of CyFrames filled with interpolated node position data
		 */
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
		                             int start, int stop, CyFrame[] cyFrameArray){

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
	
	
	/**
	 * Interpolates node opacity by linearly incrementing or decrementing the opacity value. 
	 * 
	 */
	class interpolateNodeOpacity implements FrameInterpolator {
		
		public interpolateNodeOpacity(){
			
		}
		
		
		/**
		 * Performs the interpolation.
		 *  
		 * @param valueList is in this case a list of NodeViews
		 * @param frameOne is the frame to be interpolated from
		 * @param frameTwo is the frame to be interpolated to
		 * @param start is the starting position of the frame in the CyFrame array
		 * @param end is the ending positiong of the interpolation in the CyFrame array
		 * @param cyFrameArray is the array of CyFrames which gets populated with the interpolated data
		 * @return the array of CyFrames filled with interpolated node position data
		 */
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
	
	/**
	 * 
	 * Linearly interpolates both the height and width of a node simultaneously 
	 * to achieve the affect of interpolating the size.
	 *
	 */
	class interpolateNodeSize implements FrameInterpolator {

		public interpolateNodeSize(){

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
				double[] sizeOne = frameOne.getNodeSize(nodeid);
				double[] sizeTwo = frameTwo.getNodeSize(nodeid);

				if (sizeOne == null) sizeOne = new double[2];
				if (sizeTwo == null) sizeTwo = new double[2];
				
				
				if (sizeOne[0] == sizeTwo[0] && sizeOne[1] == sizeTwo[1]) {
					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setNodeSize(node.getIdentifier(), sizeOne);
					}
					continue;
				}

				double sizeIncXlength = (sizeTwo[0] - sizeOne[0])/framenum;
				double sizeIncYlength = (sizeTwo[1] - sizeOne[1])/framenum;
				double[] sizeXArray = new double[framenum+2];
				double[] sizeYArray = new double[framenum+2];
				sizeXArray[1] = sizeOne[0] + sizeIncXlength;
				sizeYArray[1] = sizeOne[1] + sizeIncYlength;
					
				for(int k=1; k<framenum+1; k++){
					sizeXArray[k+1] = sizeXArray[k] + sizeIncXlength;
					sizeYArray[k+1] = sizeYArray[k] + sizeIncYlength;
					double[] temp = {sizeXArray[k], sizeYArray[k]};
					cyFrameArray[start+k].setNodeSize(node.getIdentifier(), temp);
				}	

			}
			return cyFrameArray;
		}
	}
	
	/**
	 * 
	 * Linearly interpolates the node border width.
	 *
	 */
	class interpolateNodeBorderWidth implements FrameInterpolator {

		public interpolateNodeBorderWidth(){

		}

		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
				int start, int stop, CyFrame[] cyFrameArray){

			int framenum = (stop-start) - 1;	
			CyNetwork currentNetwork = frameOne.getCurrentNetwork();
			List<NodeView> nodeViewList = valueList;

			for(NodeView nv: nodeViewList){

				Node node = nv.getNode();
				String nodeID = node.getIdentifier();
				
				
				//get the border widths of the node from each of the two frames
				float widthOne = frameOne.getNodeBorderWidth(nodeID);
				float widthTwo = frameTwo.getNodeBorderWidth(nodeID);
				
				
				//if (widthOne == null) sizeOne = new Integer(1);
				//if (widthTwo == null) sizeTwo = new Integer(1);
				
				
				if (widthOne == widthTwo) {
					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setNodeBorderWidth(node.getIdentifier(), widthOne);
					}
					continue;
				}

				float widthInclength = (widthTwo - widthOne)/framenum;
				float[] widthArray = new float[framenum+2];
				widthArray[1] = widthOne + widthInclength;
					
				for(int k=1; k<framenum+1; k++){
					widthArray[k+1] = widthArray[k] + widthInclength;
					cyFrameArray[start+k].setNodeBorderWidth(node.getIdentifier(), widthArray[k]);
				}	

			}
			return cyFrameArray;
		}
	}
	/**
	 * Interpolates edgeColor using the interpolateColor() method.
	 */
	class interpolateEdgeColor implements FrameInterpolator {
		
		public interpolateEdgeColor(){
			
		}
	
		/**
		 * Performs the interpolation.
		 *  
		 * @param valueList is in this case a list of EdgeViews
		 * @param frameOne is the frame to be interpolated from
		 * @param frameTwo is the frame to be interpolated to
		 * @param start is the starting position of the frame in the CyFrame array
		 * @param end is the ending positiong of the interpolation in the CyFrame array
		 * @param cyFrameArray is the array of CyFrames which gets populated with the interpolated data
		 * @return the array of CyFrames filled with interpolated node position data
		 */
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
		
	
	/**
	 * Linearly interpolates the edge opacity.
	 */
	class interpolateEdgeOpacity implements FrameInterpolator {
		public interpolateEdgeOpacity(){
			
		}
		
		/**
		 * Performs the interpolation.
		 *  
		 * @param valueList is in this case a list of EdgeViews
		 * @param frameOne is the frame to be interpolated from
		 * @param frameTwo is the frame to be interpolated to
		 * @param start is the starting position of the frame in the CyFrame array
		 * @param end is the ending positiong of the interpolation in the CyFrame array
		 * @param cyFrameArray is the array of CyFrames which gets populated with the interpolated data
		 * @return the array of CyFrames filled with interpolated node position data
		 */
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
	
	/**
	 * 
	 * Linearly interpolates the edge line width.
	 *
	 */
	class interpolateEdgeWidth implements FrameInterpolator {

		public interpolateEdgeWidth(){

		}
		/**
		 * 
		 * 
		 */
		public CyFrame[] interpolate(List valueList, CyFrame frameOne, CyFrame frameTwo, 
				int start, int stop, CyFrame[] cyFrameArray){
			
			int framenum = (stop-start) - 1;	
			CyNetwork currentNetwork = frameOne.getCurrentNetwork();
			List<EdgeView> edgeViewList = valueList;
		
			for(EdgeView ev: edgeViewList){
				
				Edge edge = ev.getEdge();
			  String edgeID = edge.getIdentifier();
				
				
				//get the edge widths of the edge from each of the two frames
				float widthOne = frameOne.getEdgeWidth(edgeID);
				float widthTwo = frameTwo.getEdgeWidth(edgeID);
				
				
				//if (widthOne == null) sizeOne = new Integer(1);
				//if (widthTwo == null) sizeTwo = new Integer(1);
				
				
				if (widthOne == widthTwo) {
					for(int k=1; k<framenum+1; k++){
						cyFrameArray[start+k].setEdgeWidth(edgeID, widthOne);
					}
					continue;
				}

				float widthInclength = (widthTwo - widthOne)/framenum;
				float[] widthArray = new float[framenum+2];
				widthArray[1] = widthOne + widthInclength;
					
				for(int k=1; k<framenum+1; k++){
					widthArray[k+1] = widthArray[k] + widthInclength;
					cyFrameArray[start+k].setEdgeWidth(edgeID, widthArray[k]);
				}	

			}
			return cyFrameArray;
		}
	}
	
	/**
	 * Linearly interpolates the network zoom.
	 * 
	 */
	class interpolateNetworkZoom implements FrameInterpolator {
		
		public interpolateNetworkZoom(){
			
		}
	
		/**
		 * Performs the interpolation.
		 *  
		 * @param valueList is not used in this case 
		 * @param frameOne is the frame to be interpolated from
		 * @param frameTwo is the frame to be interpolated to
		 * @param start is the starting position of the frame in the CyFrame array
		 * @param end is the ending positiong of the interpolation in the CyFrame array
		 * @param cyFrameArray is the array of CyFrames which gets populated with the interpolated data
		 * @return the array of CyFrames filled with interpolated node position data
		 */
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
	
		/**
		 * Performs the interpolation.
		 *  
		 * @param valueList is not used in this case
		 * @param frameOne is the frame to be interpolated from
		 * @param frameTwo is the frame to be interpolated to
		 * @param start is the starting position of the frame in the CyFrame array
		 * @param end is the ending positiong of the interpolation in the CyFrame array
		 * @param cyFrameArray is the array of CyFrames which gets populated with the interpolated data
		 * @return the array of CyFrames filled with interpolated node position data
		 */
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
		
		/**
		 * Performs the interpolation.
		 *  
		 * @param valueList is not used in this case
		 * @param frameOne is the frame to be interpolated from
		 * @param frameTwo is the frame to be interpolated to
		 * @param start is the starting position of the frame in the CyFrame array
		 * @param end is the ending positiong of the interpolation in the CyFrame array
		 * @param cyFrameArray is the array of CyFrames which gets populated with the interpolated data
		 * @return the array of CyFrames filled with interpolated node position data
		 */
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
