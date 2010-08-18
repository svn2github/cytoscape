package gbeb.util
{
	import flare.vis.data.Data;
	import flare.vis.data.EdgeSprite;
	
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	/**Pseudo Code for pathtraversal, inspired by A* algorithm. This algorithm has:
	 * - modified cost calculation function that takes into accountthe angles of the edges
	 * 	 as well as the perpendicular distance of the nodes(vertices) to the original edge
	 * - to transversed complete graphs generated from the set by all the participating nodes
	 *   hence has to use a heuristic function (and maybe dynamic programing) to improve run time
	 * 
	 * Idea:
	 * 		1) Get the set of nodes in which the edge can transverse across, WayPoints.
	 * 				WayPoints = {nodes that are control points of the edge} + {nodes that are within the 'search zone'}
	 * 		2) Although conceptually the idea is to create complete graphs, there is no need to 
	 * 		  	in exhaustively search through all possibilities.
	 * 					- Initialize = Define start node as the closest and have the same direction as the original edge
	 * 						Define end node similarly.
	 * 					a) During stepping (moving through the graph), we would just step through the top 10
	 * 					or less nodes in terms of search quality.
	 * 					The search quality equation is given by:  Q = DistanceCost - Q(angle) - Q (distance)  
	 * 					b) We would eliminate nodes that are further than 125% of the current node to reduce the search space
	 * 					c) Repeat a and b until there is a path from start node to end node. 
	 * 					d) Draw the Path
	 * 					e) ?Smooth
	 * 		Repeat for all dataEdges. 
	 * 
	 * */
	
	public class Pathfinder
	{
		private var _data:Data;
		private var _mesh:Object;
		private var _maxDis:Number;
		
		//for Kmeans alt
		private var _CPClusters:Array;
		private var _centroidArray:Array = [];
		
		public function Pathfinder()
		{
		}
		
		//This public function is exposed to allow call only after all the required steps are done. 
		public function pathfind(data:Data, mesh:*, bounds:Rectangle)
		{
				trace("Pathfinder: Starting...");
				_data = data;
				_mesh = mesh;
				_maxDis = Point.distance(bounds.topLeft, bounds.bottomRight);
				
				Kmeans_Pathfinding2_Preprocessing(_mesh.CP);
				_data.edges.visit(_pathfind);
				
				
		}
		
		//This function takes in an EdgeSprite and finds the appropriate path for the edge by first setting up 
		//a search space to find all the neighbouring nodes that it can pass through and use a heuristic pathfinding algorithm
		//to generate a path that fits the quality citeria for the curve.
		private function _pathfind(edge:EdgeSprite):void
		{
			//var searchSpace:Array = generateSearchSpace(_mesh.CP, edge);
			var searchSpaceTrace:String = ""; //debug
			
			//kmeans_Pathfinding(edge, searchSpace);
			removeSharpEdges2(edge);
			Kmeans_Pathfinding2(edge);
			
			//AStar_pathfindingAlgrithm(edge, searchSpace);
			//trace("Pathfinder: " + e.source.data["name"] + " | " + e.target.data["name"]);
			//trace("Search Space: " + searchSpace.length);
			/*for each (var p:Point in searchSpace) //debug loop
			{
				searchSpaceTrace += p.toString() + " | ";
			}
			trace(searchSpaceTrace); */
		}

		
		//some pre-processing such that the CP forms clusters
		private function Kmeans_Pathfinding2_Preprocessing(CPSet:Array):void
		{
			
			_CPClusters = GeometryUtil.kmeans(CPSet);
			trace("Pathfinder: KmeansPF: Pre starting");
			
			for (var i:int = 0; i < _CPClusters.length; i++)
			{
				_centroidArray.push(GeometryUtil.findCentroidFromPoints(_CPClusters[i]));
				
			}		
			trace("Pathfinding: _centroidArray.length: " + _centroidArray.length);
		}
		
		//alternative kmeans_pathfinding
		private function Kmeans_Pathfinding2(edge:EdgeSprite):void
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
			
			
			//trace("Pathfinder: " + edge.source.data["name"], edge.target.data["name"], newCtrl);
			edge.props.$controlPointsArray = newCtrl;
			sortCPByDistance(edge);
			//removeSharpEdges(edge);
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
			
			/*for each (var p:Point in ctrl) //debug
			{
			distance += " " + GeometryUtil.calculateDistanceBetweenPoints(sourceNode, p);
			} */
			
			//trace("GBEBRouter: BubbleSort - Array trace: " + distance );//+ distance, e.source.data["name"], e.target.data["name"]);
			
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
		
		private function removeSharpEdges(edge:EdgeSprite):void
		{	
			var ctrl:Array = edge.props.$controlPointsArray;
			var source:Point = new Point(edge.source.x, edge.source.y);
			var target:Point = new Point(edge.target.x, edge.target.y);		 
			const minAngle:Number = Math.PI / 2;
			
			if(ctrl.length < 1 ) return;
				
			do
			{
				var angles:Array = []; //stores the angle between 2 lines.
				var prev:Point = source, next:Point = ( ctrl.length == 1 ? target : ctrl[1]);
				var p:Point;
				var minAngleInArray:Number = Number.MAX_VALUE; var minAngleIndex:int = -1;
				
				for (var i:int = 0; i < ctrl.length; i++)
				{
					p = ctrl[i];
					angles[i] = GeometryUtil.getAnglesFromLines(prev, p, p, next);
					prev = p; 
					if( i == ctrl.length - 1)
					{
						next = target;
					} else {
						next = ctrl[i+1];
					}				
				}
				
				for (var i:int = 0; i < angles.length; i++)
				{
					if(Math.abs(angles[i]) < minAngleInArray)
					{
						minAngleInArray = angles[i];
						minAngleIndex = i;
					}
				}
				
				if( minAngleIndex != -1 && Math.abs(angles[minAngleIndex]) < minAngle){
					trace("Pathfinder: removeSharpEdges: " + angles[minAngleIndex] + " spliced");
					ctrl.splice(minAngleIndex, 1); 
				} 
			} while(checkAngles(angles, minAngle))
		}
		
		private function checkAngles(angles:Array, minAngle:Number):Boolean
		{		
			//if(angles.length == 0) return false;
			for each (var angle:Number in angles) 
			{
				if(angle < minAngle) return true;
			}
			
			return false;
		}
		
		private function removeSharpEdges2(edge:EdgeSprite):void
		{
			var ctrl:Array = edge.props.$controlPointsArray;
			var source:Point = new Point(edge.source.x, edge.source.y);
			var target:Point = new Point(edge.target.x, edge.target.y);
			
			var turnsArray:Array = []; //stores in consecutive lists the number of points per turn.
			
			for each (var p:Point in ctrl)
			{
				trace("Pathfinder: removeSharpEdges2: " + edge, ctrl.length);
			}
			
			
		}

	} //end of class
}