package
{
	import com.degrafa.geometry.splines.BezierSpline;
	
	import flash.display.DisplayObjectContainer;
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import gbeb.util.GeometryUtil;
	import gbeb.util.delaunay.Delaunay;
	import gbeb.util.delaunay.ITriangle;
	import gbeb.util.delaunay.XYZ;

	[SWF(width="800",height="600", backgroundColor="#FFFFFF", frameRate="30")]
	public class Test extends Sprite
	{
		
		public function Test()
		{
			trace("Test: Running");
			
			var visContainer:Sprite = new Sprite();
		
			var colour:* = new Object();
			colour = {
				0: 0x000000,
				1: 0xFF00FF,
				2: 0x6633FF,
				3: 0x00CCFF
			};
			
			for(var i:int = 1; i < 15; i++)
			{
				drawKmeansResult(visContainer, colour, i, 30 * i);
			}
	
		}
		
		public function drawKmeansResult(s:Sprite, colour:*, numPoints:int, height:int):void
		{		
			var points:Array = [];
			var clusters:Array;
			var originalPoints:Sprite = new Sprite();
			var clusteredPointsContainter:Sprite = new Sprite();
			var lineContainer:Sprite = new Sprite();
			var numGeneratedPoints:int = numPoints;
			var alpha:Number = 1 / (numPoints + 5);
			
			s.addChild(lineContainer);
			s.addChild(clusteredPointsContainter);
			s.addChild(originalPoints);
			
			
			for(var i:int = 0; i < numPoints; i++)
			{
				points.push(new Point(Math.random()*600, height));
				//trace("Points: " + points[i]);
			}
			
			drawPoints(originalPoints, points, 3, 0x000000);
			
			clusters = GeometryUtil.kmeans(points);
			
			for ( var i:int = 0; i < Math.ceil( Math.pow( (points.length / 2), 0.5 )); i++)
			{
				drawCentroid(lineContainer, clusters[i], colour[i]);
				var clusteredPoints:Sprite = new Sprite();
				clusteredPointsContainter.addChild(clusteredPoints);
				drawPoints(clusteredPoints, clusters[i], 10, colour[i], alpha);
				
			}
		}
		
		public function drawPoints(s:Sprite, points:Array, radius:Number, colour:uint, alpha:Number = 1):void
		{
			for each(var p:Point in points)
			{
				var s:Sprite = new Sprite();
				var g:Graphics = s.graphics;
				g.beginFill(colour, alpha)
				g.drawCircle(p.x, p.y, radius);
				g.endFill();
				addChild(s);
			}	
		}
		
		public function drawCentroid(s:Sprite, cluster:Array, colour:uint):void //? why is this function not working
		{
			var centroid:Point = GeometryUtil.findCentroidFromPoints(cluster);
			var line:Sprite = new Sprite();
			var g:Graphics = line.graphics;
			
			g.beginFill(colour);
			g.drawRect(centroid.x, centroid.y - 15, 4, 30);
			g.endFill();
			
			s.addChild(line);
			trace("Drawing lines", centroid.toString(), colour);
		}

	}
}