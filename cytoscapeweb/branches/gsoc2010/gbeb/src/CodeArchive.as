//Code Archive





//this method changes the set of control points stored in edge.prop.$controlPointsArray 
public static function changeToDerivedPoints(e:EdgeSprite):void
{
	//trace("Geometry Util: Targeting Edge: " + e.toString());
	var controlPoints:Array = e.props.$controlPointsArray;
	
	var tempPoint:Point;
	var newCPArray:Array = []; //to store derived CP Array
	
	if(controlPoints == null ) return;
	if(controlPoints.length <= 0 ) return;
	
	if(e.target == null || e.source == null) return;
	
	controlPoints.unshift(new Point(e.source.x, e.source.y));
	controlPoints.push(new Point(e.target.x, e.target.y));
	
	for(var i:int = 1; i < controlPoints.length - 1; i++)
	{
		var p:Point = controlPoints[i] as Point;
		
		if(p == null) controlPoints.splice(i, 1);
	}
	
	//trace("Geometry Util: CP.length: " + (controlPoints.length - 2).toString());
	
	for(var i:int = 1; i < controlPoints.length - 1; i++)
	{
		var p0:Point = controlPoints[i-1] as Point;
		var p1:Point = controlPoints[i] as Point;
		var p2:Point = controlPoints[i+1] as Point;
		
		var derivedPt:Point = derivePoint(p0, p1, p2);
		newCPArray.push(derivedPt);				
		//trace("Geometry Util:" , p0, p1, p2);
		
	}
	
	e.props.$controlPointsArray = newCPArray;
	
}


/****
 * From Pathfinding
 * 
 * 		
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
		 */



/*
* Draw a quadratic Bezier curve through a set of control points anchorPoints[], with the first point being the start and last points 
* being static anchor points of the curve. 
* 
* This method extends from the solution given at: http://stackoverflow.com/questions/2075544/how-can-i-modify-my-code-to-line-through-the-bezier-control-points,
* with question+code by WillyCornbread and solution+code given by Sly_cardinal
*/
		public static function drawCPDirectedCurves(g:Graphics, anchorPoints:Array):void
		{
			// clear old line and draw new / begin fill
			g.clear();
			g.lineStyle(0.5, 0, 1);
			g.beginFill(0x0099FF,.1);
			
			//move to starting anchor point
			var startX:Number = anchorPoints[0].x;
			var startY:Number = anchorPoints[0].y;
			g.moveTo(startX, startY);
			
			// Connect the dots
			var p0:Point = new Point(startX, startY);
			var p2:Point;
			
			var numAnchors:Number = anchorPoints.length;
			for (var i:Number=1; i<numAnchors; i++) {
				
				p2 = new Point(anchorPoints[i].x, anchorPoints[i].y);
				
				// curve to next anchor through control
				//var b1:Point = new Point(controlPoints[i].x,controlPoints[i].y);
				var b1:Point = new Point(anchorPoints[i].x,anchorPoints[i].y);
				var p1:Point = derivePoint(p0, b1, p2);
				
				g.curveTo(p1.x, p1.y, p2.x, p2.y);
				
				p0 = p2;
				
			}
			// Close the loop - not necessarys
			//g.curveTo(controlPoints[0].x,controlPoints[0].y,startX,startY);
}
