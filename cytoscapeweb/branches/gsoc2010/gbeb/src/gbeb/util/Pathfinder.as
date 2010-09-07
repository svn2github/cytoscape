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

package gbeb.util
{
	import flare.vis.data.Data;
	import flare.vis.data.EdgeSprite;
	
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import gbeb.view.operator.router.pathMap;
	
	/**
	 * Pathfinder uses Kmeans clustering instead of the suggested strategy by weiwei to find bundling points. 
	 * 
	 * It also uses A* pathfinding to tidy up the bundling, by force more edges through common paths.
	 *  A* can be further extended to include considerations of curvature and displacement. */
	
	public class Pathfinder
	{
		private var _data:Data;
		private var _mesh:Object;
		private var _maxDis:Number;
		
		//for Kmeans alt
		private var _CPClusters:Array;
		private var _centroidArray:Array = [];
		
		//for Astar pathfind
		private var _map:pathMap;
		
		// ========[ CONSTRUCTOR ]==================================================================
		
		/**
		 * Does nothing. Data has to be loaded with the pathfind command. */
		public function Pathfinder()
		{
		}
		
		// ========[ PUBLIC METHODS ]===============================================================
		
		/**
		 * This function is exposed to allow the main GBEB script to call it once all the required data
		 * processing steps are done. */
		public function pathfind(data:Data, mesh:*, bounds:Rectangle)
		{
				trace("Pathfinder: Starting...");
				_data = data;
				_mesh = mesh;
				_maxDis = Point.distance(bounds.topLeft, bounds.bottomRight);
				
				Kmeans_Pathfinding2_Preprocessing(_mesh.CP);

				_data.edges.visit(Kmeans_Pathfinding);

				//For Astar pathfind
				_data.edges.sortBy("props.$controlPointsArray.length");
		
				/* _data.edges.visit(function traceCPLength(edge:EdgeSprite):void{
					var CPArray:Array = edge.props.$controlPointsArray;
					if(CPArray == null) return;
					trace("Pathfinder: " + edge + " | $CPArraylength: " + CPArray.length);
				}); //debug */
				
				_map = new pathMap(_centroidArray);
				_data.edges.visit(A_starPathfinding);
				
		}
		
		// ========[ PRIVATE METHODS ]==============================================================
		
		// Uses a scoring function to find the optimal path for an edge. The optimal path has the lowest scores
		private function A_starPathfinding(edge:EdgeSprite):void
		{
			var searchSpace:Array = edge.props.$controlPointsArray;
			if(searchSpace == null) return;
			if(searchSpace.length < 2) return;
			
			var currPoint:Point = searchSpace[0];
			var startPoint:Point = new Point(edge.source.x, edge.source.y);
			var endPoint:Point = new Point(edge.target.x, edge.target.y);
			var edgeLength:Number = Point.distance(currPoint, endPoint);
			var prevAngle:Number = GBEBInterfaceUtil.getPolarCoor360(currPoint, endPoint); //stores the angle of the path direction thus far
			var path:Array = [];
			
			while(searchSpace.length != 0){
			
				var minCost:Number = Number.MAX_VALUE;
				var costForCurrPath:Number = -1;
				var nextPoint:Point = null;
				var disNextPointToEnd:Number = 0;
			
				for( var i:int = 0; i < searchSpace.length; i++)
				{
					costForCurrPath = findCost(currPoint, searchSpace[i], edgeLength, prevAngle);
					//trace("Pathfinder: A*: EDGE cost: " + edge.source.data["name"], edge.target.data["name"] + " | cost: ");
					//trace(costForCurrPath);
					if(costForCurrPath < minCost) {
						minCost = costForCurrPath;
						nextPoint = searchSpace[i];
						searchSpace.splice(i,1); 
					}
				}
	
				//after next Point is chosen
				prevAngle = GBEBInterfaceUtil.getPolarCoor360(currPoint, nextPoint);
				_map.insertPath(currPoint, nextPoint);
				currPoint = nextPoint;
				path.push(nextPoint);
				disNextPointToEnd = Point.distance(nextPoint, endPoint);
			
				//clean up: remove points that are further away to edge.target as compared to the next point, as well as current point
				for( var i:int = 0; i < searchSpace.length; i++) //Does it throw infinite loop error as searchSpace decreases?
				{
					if (Point.distance(searchSpace[i], endPoint) >= disNextPointToEnd)
					{
						searchSpace.splice(i, 1);						
					}
				}	
			}
			edge.props.$controlPointsArray = path;
		}

		// Cost calculation function of A* pathfinding. Extend with more variables here. 
		private function findCost(currPoint:Point, nextPoint:Point, edgeLength:Number, currAngle:Number):Number
		{
			var cost:Number = 0;
			//var distanceTraveledScore:Number = Point.distance(currPoint, nextPoint) / edgeLength ;
			//var angleDeviationScore:Number = calAngleDeviationScore(currPoint, nextPoint, currAngle);
			var prevPathScore:Number = Math.pow( 0.5, _map.getPathCount(currPoint, nextPoint));

			cost = -1 * prevPathScore;
			
			return cost;
		}
		
		// Not used. Function attempts to score the points via the angle difference between consequtive paths in order to
		// the less bendy path. 
		private function calAngleDeviationScore(currPoint:Point, nextPoint:Point, currAngle:Number):Number
		{
			
			var changeInAngle:Number = Math.abs( GBEBInterfaceUtil.getPolarCoor360(currPoint, nextPoint) - currAngle);
			var changeInAngle:Number = (changeInAngle > 180 ? (360 - changeInAngle) : changeInAngle );
			//trace("Pathfinder: A*: calAngleDeviation: Done!");
			
			return changeInAngle / 180;
		}

		//some pre-processing such that the CP forms clusters
		private function Kmeans_Pathfinding2_Preprocessing(CPSet:Array):void
		{
			
			_CPClusters = GeometryUtil.kmeans(CPSet);		
			for (var i:int = 0; i < _CPClusters.length; i++)
			{
				_centroidArray.push(GeometryUtil.findCentroidFromPoints(_CPClusters[i]));
				
			}		
			trace("Pathfinding: _centroidArray.length: " + _centroidArray.length);
		}
		
		private function Kmeans_Pathfinding(edge:EdgeSprite):void
		{
			var ctrl:Array = edge.props.$controlPointsArray;
			var newCtrl:Array = [];
			if(ctrl == null) return;
			
			for each(var p:Point in ctrl)
			{
				var closetDist:Number = Number.MAX_VALUE;
				var distToCentroid:Number = -1;
				var tempCentroid:Point = null;
				
				//check which cluster is it nearest to
				for each(var centroid:Point in _centroidArray)
				{
					distToCentroid = Point.distance(p, centroid);
					if(distToCentroid == closetDist)
					{
						//search the array and find out which cluster does the point belong to
					}
					if(distToCentroid < closetDist)
					{
						closetDist = distToCentroid;
						tempCentroid = centroid;
					}
				}
				
				if(tempCentroid != null && newCtrl.indexOf(tempCentroid) == -1) newCtrl.push(tempCentroid);
			}
			
			edge.props.$controlPointsArray = newCtrl;
			sortCPByDistance(edge);
			//trace("Pathfinder: " + edge.source.data["name"], edge.target.data["name"], edge.props.$controlPointsArray);
		}
		
		
		private function sortCPByDistance(edge:EdgeSprite):void
		{
			var ctrl:Array = edge.props.$controlPointsArray;
			if(ctrl == null) return;	
			
			var sourceNode:Point = new Point(edge.source.x, edge.source.y); //casting source node as mesh modes
			var targetNode:Point = new Point(edge.target.x, edge.target.y);
			var swapArray:Array = [];
			var disSourceTarget:Number = Point.distance(sourceNode, targetNode);
			var distance:String = ""; //debug

			//trace("GBEBRouter: Bubble sorting CP by Distance...", e.name);
			for each (var p:Point in ctrl)
			{
				if( p == null){
					ctrl.splice(ctrl.indexOf(p), 1); //trace("A null node has been spliced");
				} 
			}
			ctrl = bubbleSortPointsArray(ctrl, sourceNode);
			
			for(var i:int = 0; i < ctrl.length; i++)
			{
				
				var disTargetP:Number = Point.distance(targetNode, ctrl[i]);
				if(disTargetP > disSourceTarget)
				{
					swapArray.push(ctrl[i]); 
					ctrl.splice(ctrl.indexOf(ctrl[i]), 1);
				}
			}
			
			swapArray = bubbleSortPointsArray(swapArray, targetNode, false);
			
			for each (var p:Point in swapArray) 
			{
				ctrl.unshift(swapArray.shift()); 
				distance += " " + Point.distance(sourceNode, p); //debug
			}
			//trace("GBEBRouter: BubbleSort - Swap Array trace: " + distance, e.source.data["name"], e.target.data["name"]);	
		}
		
		// takes in an array and result a sorted arraying in increasing distance away from target point.
		private function bubbleSortPointsArray(a:Array, targetPoint:Point, increasing:Boolean = true):Array
		{
			var currDist:Number; var nextDist:Number; var temp:*;
			for (var i:int = 0; i < a.length; i++)
			{
				for (var j:int = 0; j < a.length - i - 1; j++)
				{
					currDist = Point.distance(targetPoint,a[j]);
					nextDist = Point.distance(targetPoint,a[j + 1]);
					if(increasing)
					{
						if(currDist > nextDist)
						{
							temp = a[j+1]
							a[j+1] = a[j];
							a[j] = temp;
						}
					} else {
						if(currDist < nextDist)
						{
							temp = a[j+1]
							a[j+1] = a[j];
							a[j] = temp;
						}
					}
				}
			}
			return a;
		}

	} //end of class
}