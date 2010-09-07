package gbeb.util
{
	import flare.vis.data.Data;
	import flare.vis.data.EdgeSprite;
	
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import gbeb.view.operator.router.pathMap;
	
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
		
		// Stores the list routes for reach index (index represents number of CP)
		// Since there non-repeateable routes for index = 0 or 1, 2 empty objects are added as place holders.
		private var _routes:Array = new Array({},{}); 
		
		//for Kmeans alt
		private var _CPClusters:Array;
		private var _centroidArray:Array = [];
		
		//for Astar pathfind
		private var _map:pathMap;
		
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
		
		function A_starPathfinding(edge:EdgeSprite):void
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

		private function findCost(currPoint:Point, nextPoint:Point, edgeLength:Number, currAngle:Number):Number
		{
			var cost:Number = 0;
			//var distanceTraveledScore:Number = Point.distance(currPoint, nextPoint) / edgeLength ;
			//var angleDeviationScore:Number = calAngleDeviationScore(currPoint, nextPoint, currAngle);
			var prevPathScore:Number = Math.pow( 0.5, _map.getPathCount(currPoint, nextPoint));

			cost = -1 * prevPathScore;
			
			return cost;
		}
		
		private function calAngleDeviationScore(currPoint:Point, nextPoint:Point, currAngle:Number):Number
		{
			
			var changeInAngle = Math.abs( GBEBInterfaceUtil.getPolarCoor360(currPoint, nextPoint) - currAngle);
			var changeInAngle = (changeInAngle > 180 ? (360 - changeInAngle) : changeInAngle );
			//trace("Pathfinder: A*: calAngleDeviation: Done!");
			
			return changeInAngle / 180;
		}

		//This function takes in an EdgeSprite and finds the appropriate path for the edge by first setting up 
		//a search space to find all the neighbouring nodes that it can pass through and use a heuristic pathfinding algorithm
		//to generate a path that fits the quality citeria for the curve.
		private function _pathfind(edge:EdgeSprite):void
		{
			//var searchSpace:Array = generateSearchSpace(_mesh.CP, edge);
			var searchSpaceTrace:String = ""; //debug
			
			//kmeans_Pathfinding(edge, searchSpace);
			
			Kmeans_Pathfinding2(edge);
			//removeSharpEdges(edge);
			
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