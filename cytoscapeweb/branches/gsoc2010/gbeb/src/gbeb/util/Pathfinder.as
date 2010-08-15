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
				_data = data;
				_mesh = mesh;
				_maxDis = GeometryUtil.calculateDistanceBetweenPoints(bounds.topLeft, bounds.bottomRight);
				
				Kmeans_Pathfinding2_Preprocessing(_mesh.CP);
				_data.edges.visit(_pathfind);
				
				
		}
		
		//This function takes in an EdgeSprite and finds the appropriate path for the edge by first setting up 
		//a search space to find all the neighbouring nodes that it can pass through and use a heuristic pathfinding algorithm
		//to generate a path that fits the quality citeria for the curve.
		private function _pathfind(edge:EdgeSprite):void
		{
			var searchSpace:Array = generateSearchSpace(_mesh.CP, edge);
			var searchSpaceTrace:String = ""; //debug
			
			
			edge.lineWidth *= 0.5; edge.lineAlpha = 0.5;
			
			//kmeans_Pathfinding(edge, searchSpace);
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
		
		//Get the set of nodes in which the edge can transverse across, WayPoints.
		//				WayPoints = {nodes that are control points of the edge} + {nodes that are within the 'search zone'}
		private function generateSearchSpace(CPset:Array, edge:EdgeSprite):Array
		{
			var searchSpace:Array = [];
			var CPArrayFromEdge:Array = edge.props.$controlPointsArray;
			var s:Point = new Point(edge.source.x, edge.source.y);
			var t:Point = new Point(edge.target.x, edge.target.y);
			var angle:Number = Math.PI / 6;
			var maxSearchDist:int = getMaxSearchDist(s, t, angle); //stores the maximum search distance for nodes that are not already on the edgeSprite
	
			//should i return here if there are no contol points?
			
			//adds CP by inclusive addition
			for each (var p:Point in CPset)
			{
					if(checkSearchSpace(p, t, s, maxSearchDist)) searchSpace.push(p);	
					//trace("Pathfinder: Hi " + CPset.length, maxSearchDist);			
			}
	
			trace("Pathfinder: GSP: searchSpace.length: " + searchSpace.length, "maxSearchDist: " + maxSearchDist, edge.name);
			if(CPArrayFromEdge == null) return searchSpace;

			
			for each(var p:Point in CPArrayFromEdge)
			{
				if(searchSpace.indexOf(p) == -1) searchSpace.push(p);
			}
			
			trace("Pathfinder: GSP (After): searchSpace.length: " + searchSpace.length, edge.name);
			
			return searchSpace;
		} 
		
		// Checks if a particular CP from the CPset should be included in the search space base on its distance to the 
		// source and target node of the EdgeSprite: The search space is a semi-circle in shape.
		private function checkSearchSpace(currNode:Point, s:Point, t:Point, maxDis:int):Boolean
		{
			var c:Point = new Point(currNode.x, currNode.y); //current point
			var dis:int = Math.floor(GeometryUtil.calculateDistanceBetweenPoints(c, s) + Math.floor(GeometryUtil.calculateDistanceBetweenPoints(c, t)));
			//trace("Pathfinder: dis: " + dis + " | Max Dis: " + maxDis);
			return ( dis < maxDis ? true : false);
		}		
		
		// Returns the boundary distance of the search space. The further the source and target nodes are apart, 
		// the larger the boundary.
		private function getMaxSearchDist(t:Point, s:Point, angle:Number):int
		{
			return Math.floor(GeometryUtil.calculateDistanceBetweenPoints(s, t) * Math.tan(angle) * 2 );
		}
		
		// Astar is a pathfinding algorithm...
		private function AStar_pathfindingAlgrithm(edge:EdgeSprite, searchSpace:Array):void
		{
			var debugString:String = ""; //debug
			var ctrl:Array = edge.props.$controlPointsArray;
			var curr:Point = new Point(edge.source.x, edge.source.y); 
			var end:Point = new Point(edge.target.x, edge.target.y); 
			var selectedPoint:int = -1;
			var wayPoints:Array = []; //stores in order the final path of the edgeSprite

					
			if(ctrl == null) return;	
			edge.props.$controlPointsArray = searchSpace;
			sortCPByDistance(edge);
			
			trace("Pathfinding: Test: " + (searchSpace.length == edge.props.$controlPointsArray.length) + " | ctrl.length: " + ctrl.length );
			trace(edge.props.$controlPointsArray.length);
			
			while(Math.abs(curr.x - end.x) > 0.1 || Math.abs(curr.y - end.y) > 0.1) //Just in case there is some floating points mismatch
			{
				for(var i:int = 0; i < searchSpace.length; i++)
				{
					
				}
				
			}
			
		}
		
		
		//private test2
		//should i include start and end in kmeans?
		private function kmeans_Pathfinding(edge:EdgeSprite, searchSpace:Array):void
		{
			var ctrl:Array = edge.props.$controlPointsArray;
			var curr:Point = new Point(edge.source.x, edge.source.y); 
			var end:Point = new Point(edge.target.x, edge.target.y);
			var wayPointsSet:Array; // Stores the sets of way point path after kmeans. 
			var wayPoints:Array = []; //stores in order the final path of the edgeSprite
			
			if(searchSpace.length == 0) return;	
			
			wayPointsSet = GeometryUtil.kmeans(searchSpace);
			
			for each (var cluster:Array in wayPointsSet)
			{
				if(cluster[0] != null) wayPoints.push(cluster[0]);
				//trace("Pathfinder: tracing clusters: " + cluster[0]);
			}
			
			trace("Pathfinder: KmeansPF: WayPoints.length: " + wayPoints.length);
			
			edge.props.$controlPointsArray = wayPoints;
			sortCPByDistance(edge);
			
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
					distToCentroid = GeometryUtil.calculateDistanceBetweenPoints(p, centroid);
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
		}
		
		
		public function sortCPByDistance(e:EdgeSprite):void
		{
			var ctrl:Array = e.props.$controlPointsArray;
			if(ctrl == null) return;	
			
			var sourceNode:Point = new Point(e.source.x, e.source.y); //casting source node as mesh modes
			var targetNode:Point = new Point(e.target.x, e.target.y);
			var swapArray:Array = [];
			var disSourceTarget:Number = GeometryUtil.calculateDistanceBetweenPoints(sourceNode, targetNode);
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
				
				var disTargetP:Number = GeometryUtil.calculateDistanceBetweenPoints(targetNode, ctrl[i]);
				if(disTargetP > disSourceTarget)
				{
					swapArray.push(ctrl[i]);
					ctrl.splice(ctrl.indexOf(i), 1);
				}
			}
			
			swapArray = bubbleSortPointsArray(swapArray, targetNode, false);
			
			for each (var p:Point in swapArray) 
			{
				ctrl.unshift(swapArray.shift()); 
				distance += " " + GeometryUtil.calculateDistanceBetweenPoints(sourceNode, p); //debug
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
					currDist = GeometryUtil.calculateDistanceBetweenPoints(targetPoint,a[j]);
					nextDist = GeometryUtil.calculateDistanceBetweenPoints(targetPoint,a[j + 1]);
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