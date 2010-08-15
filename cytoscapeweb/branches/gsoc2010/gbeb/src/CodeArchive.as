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
