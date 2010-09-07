/*
This file is part of Cytoscape Web.
Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

The Cytoscape Consortium is:
- Agilent Technologies
- Institut Pasteur
- Institute for Systems Biology
- Memorial Sloan-Kettering Cancer Center
- National Center for Integrative Biomedical Informatics
- Unilever
- University of California San Diego
- University of California San Francisco
- University of Toronto

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA

*/

package gbeb.view.operator.router
{
	import flare.util.Shapes;
	import flare.vis.data.EdgeSprite;
	
	import flash.geom.Point;
	
	import gbeb.util.GBEBInterfaceUtil;
	import gbeb.util.GeometryUtil;

	public class Shape
	{
		public var gridSize:int;
		public var storedGrids:Array; //stores Rec objects
		public var gridIndex:Array; // stores Point objects
		public var storedDataNodes:Array; // NodeSprite objects
		public var storedDataEdges:Array; // EdgeSprite objects
		public var direction:Number = -1; //stores the main direction of edges that are inside the Shape. -1 if there is no main distance
		public var stronglyClustered:Boolean; //flag to indicate if the edges are strongly clustered within this Shape
		public var centroid:Point; //Stores the "centre of area (mass) for a 2d shape
		public var meshEdge:MeshEdge;
		
		private const bandwidth:int = 3; //bandwidth of KDE
		private const majorityPercentage:Number = 0.5; // refers to the total weight percentage a particular group has to have before it can be considered as the main direction
		
		private var sum:Number = 0; 
		//private var numEdges:int = storedDataEdges.length;
		private var angleArray:Array = new Array(180/bandwidth + 1); //Each array block has a bandwidth of 3 degrees
		private var angleGroupWeightArray:Array = new Array(180/bandwidth + 1);
		private var majorityWeight:Number;
		private var angleArrayDirectionIdx:int = -1;
		
		// ========[ CONSTRUCTOR ]==================================================================
		/**
		 * This shape class is created to act as storage and abstraction for the GBEB router. It should never be 
		 * implemented for other purposes.
		 * 
		 * Shape keeps a list of the data edges that passes through it.
		 * Shapes can be combined with other Shapes to give a new Shape.
		 * Shape calculate the majority angle using Kernel Density Estimator  */
		
		public function Shape():void
		{
			gridIndex = new Array();
			storedDataEdges = new Array();
			storedDataNodes = new Array();
			storedGrids = new Array();		
		}
		
		// ========[ PUBLIC METHODS ]===============================================================
		
		/** Returns the general direction of the edges in this particular shape by calculation using Kernel Density Estimator 
		 * of bandwidth of 3 degrees. This function ought to be called whenever Shapes are merged. 
		 * Positve x-axis = 0 degrees. Angle increases Counterclockwise.*/
		
		public function computeDirection():Number
		{	//initialising all the angles in the array to zero. 
			var i:int = 0, j:int = 0;
			
			if(storedDataEdges.length < 2) return -1;
			
			for (i = 0; i < angleArray.length ; i++)
			{
				j = 0; //setting type for array. 
				angleArray[i] = j;
				angleGroupWeightArray[i] = j;
			}

			for each(var edge:EdgeSprite in storedDataEdges)
			{
				//getting the gradient of the line and expressing as a fraction of Pi. 
				var angle:int = GBEBInterfaceUtil.getPolarCoor180(edge);
				
				//trace("Shape: CalculateDirection: " + edge.source.data["name"], edge.target.data["name"] + " has an angle of " + angle);
				
				var boxNo:int = Math.floor(angle / bandwidth);
				
				// The if statements are used to handle special cases where angle is close to zero or PI
				// The distributed weight to each box is determined by me using a approximately normal distribution of probability density. 
				// additional notes: Guassian Distribution can be used to generalise. 
				angleArray[((boxNo - 2 < 0) ? angleArray.length - 2 : boxNo - 2)] += 15;
				angleArray[((boxNo - 1 < 0) ? angleArray.length - 1 : boxNo - 1)] += 20;
				angleArray[boxNo] += 30;
				angleArray[((boxNo + 1 > angleArray.length - 1) ? 0 : boxNo + 1)] += 20;
				angleArray[((boxNo + 2 > angleArray.length - 1) ? 1 : boxNo +2)] += 15;	
			}
			
			/*for (i = 0; i < angleArray.length; i++)
			{
				trace("Shape " + gridIndex[0] + " : angleArray: " + i, angleArray[i]);
			} */ //debug
			
			//for a directon to be the majority direction. It's group culmulative must be larger than this. 
			 majorityWeight = storedDataEdges.length * 100 * majorityPercentage;  
			 
			//adds up the density for each angle, across a range of 15 degrees.  
			for (i = 2; i < angleGroupWeightArray.length + 2; i++)
			{
				for(j = -2; j <= 2; j++)
				{	
					angleGroupWeightArray[i % angleGroupWeightArray.length] += angleArray[(i+j) % angleArray.length ];
					
					if(angleGroupWeightArray[i % angleGroupWeightArray.length] > majorityWeight)
					{
						majorityWeight = angleGroupWeightArray[i % angleGroupWeightArray.length];
						angleArrayDirectionIdx = i % angleGroupWeightArray.length;
						stronglyClustered = true;
					}
				}		
			}
			
			if(stronglyClustered)
			{
				direction = angleArrayDirectionIdx * bandwidth; 
				//trace("Shape " + gridIndex[0] + " :Direction " + direction); // should shift by 1.5 to get middle of bandwidth, but I dont think this level of accuracy is necessary
				return direction; 
			} 		
			return -1;
		}
		
		/**adds controlPoint to meshEdge of a shape. 
		 * This function is called x times if the dataEdge cuts across x number of shapes. */
		
		public function addControlPoint(angleResolution:int = 15):void {
			var intersectionPointsArray:Array = new Array();
			var intersectionPoint:Point;
			var cp:Point; 
			var dataEdgeDirection:int;
			var gradient:Number = -1 / Math.tan((this.direction / 180) * Math.PI);
			var a:Point, b:Point; //a,b stores the end points of the meshEdge of each shape
			var e:Point, f:Point; //e,f stores the end points of the each dataEdge		
			var edge:EdgeSprite;
			
			a = new Point(meshEdge.x1, meshEdge.y1);
			b = new Point(meshEdge.x2, meshEdge.y2);	
			
			for each (edge in storedDataEdges) {
				
				e = new Point(edge.source.x, edge.source.y);
				f = new Point(edge.target.x, edge.target.y);
				
				intersectionPoint = GeometryUtil.lineIntersectLine(a, b, e, f);
				
				if (intersectionPoint != null) {
					intersectionPointsArray.push(intersectionPoint);
				}
			}
			
			if(intersectionPointsArray.length != 0) {
				cp = GeometryUtil.findCentroidFromPoints(intersectionPointsArray);
			} else {
				trace("Shape: addControlPoints: " + (gridIndex[0] as Point).toString() + " has no controlPoint");
			}
			
			for each (edge in storedDataEdges) {
				//				edge.lineWidth = edge.lineWidth /2 ; //lower width gives better visual quality
				//		    edge.shape = Shapes.BSPLINE; //Here to change curve type
				//				edge.lineAlpha = 0.5;
				var ctrl:Array = edge.props.$controlPointsArray;
				var ctrlgradient:Array = edge.props.$CPGradientArray; //used to store the gradient of each control point
				if (ctrl == null) edge.props.$controlPointsArray = ctrl = [];
				if (ctrlgradient == null) edge.props.$CPGradientArray = ctrlgradient = [];
				
				ctrl.push(cp);
				ctrlgradient.push(gradient);  //trace(edge.source.data["name"], gradient);
			}
		}				
		
	}//end of class
}