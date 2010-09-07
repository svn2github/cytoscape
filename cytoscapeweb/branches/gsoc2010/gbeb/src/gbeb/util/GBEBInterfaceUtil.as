package gbeb.util
{
	import flare.vis.data.EdgeSprite;
	
	import flash.display.DisplayObjectContainer;
	import flash.display.Shape;
	import flash.display.Sprite;
	import flash.geom.Point;
	import flash.text.TextField;
	
	import gbeb.util.delaunay.ITriangle;
	import gbeb.util.delaunay.XYZ;
	import gbeb.view.operator.router.MeshEdge;
	import gbeb.view.operator.router.MeshNode;

	/**
	 * This class is created as a static class to interface between GBEBRouter and the generic geometrical methods, which
	 * allows the GBEBRouter code to be kept cleaner and generic methods available for re-using.
	 */
	
	public class GBEBInterfaceUtil
	{
		// ========[ CONSTRUCTOR ]==================================================================
		
		/**
		 * This constructor will throw an error, as this is an abstract class. 
		 */
		
		public function GBEBInterfaceUtil()
		{
			throw new Error("This is an abstract class.");
		}
		
		// ========[ PUBLIC METHODS ]===============================================================
		
		/**
		 * This function uses Pythagoras' Theorem to calculate the distance between meshNode1 and meshNode2.
		 */
		
		public static function calculateDistanceBetweenNodes(node1:MeshNode, node2:MeshNode):Number
		{
			var distance:Number = 0;
			distance = Math.sqrt( Math.pow((node1.x - node2.x), 2) + Math.pow((node1.y - node2.y), 2));
			return distance;
		}
		
		/** DrawDelaunay takes standard output of Delaunay as input (arrays of points and triangles)
		* draws the triangulation on the passed-in display object container
		*/
		
		public static function drawDelaunay(tris:Array, points:Array, clip:DisplayObjectContainer, toLabel:Boolean = false) {
			var p:Sprite = new Sprite(); // for the points
			var t:Sprite = new Sprite(); // for the triangles
			var TriCounter:int = 0;
			
			clip.addChild(t);
			clip.addChild(p);
			if (toLabel) {
				var l:Sprite = new Sprite(); // for the labels
				clip.addChild(l);
			}
			
			for each(var point:XYZ in points) {
				if (point==null) continue;
				var circ:Shape = new Shape();
				circ.graphics.beginFill(0x42C0FB);
				circ.graphics.drawCircle(0,0,4);
				circ.graphics.endFill();
				circ.x = point.x;
				circ.y = point.y;
				p.addChild(circ);
				
				if (toLabel) {
					var tf:TextField = new TextField();
					tf.text = point.z;
					tf.x = point.x + 1;
					tf.y = -(point.y + 13);
					l.addChild(tf);
				}
			}
			for each(var tri in tris) {
				with (t.graphics) {
					lineStyle(1.5, 0x42C0FB);
					moveTo(points[tri.p1].x, points[tri.p1].y);
					lineTo(points[tri.p2].x, points[tri.p2].y);
					lineTo(points[tri.p3].x, points[tri.p3].y);
					lineTo(points[tri.p1].x, points[tri.p1].y);
					TriCounter++;
				}
			}
			
			var testGraphics:Sprite = new Sprite();
			testGraphics.graphics.beginFill(0x000000);
			testGraphics.graphics.drawCircle(100, 100, 50);
			testGraphics.graphics.endFill();
			
			trace("Delaunay: Finished Triangulating. There are", TriCounter, "triangles");
		}
		
		/** ConvertToMeshEdges retrieves the result of the Delaunay triangulations and converts them to an
		 *  array of meshEdges
		 */
		
		public static function convertToMeshEdges(triangles:Array, points:Array, meshNodes:Array):Array
		{
			var meshEdgeArray:Array = [];
			var meshEdge:MeshEdge;
			
			var mapArray:Array = []; //variable length array to store the index mapping of meshEdges to points array
			for(var i:int = 0; i < points.length; i++)
			{
				mapArray.push(new Array());
			}
			
			trace("Delaunay: pointArray.length: " + points.length);
			
			for each (var tri:ITriangle in triangles)
			{
				meshEdge = generateEdge(tri.p1, tri.p2, points, meshNodes, mapArray, meshEdgeArray);
				if( meshEdge != null) meshEdgeArray.push(meshEdge);
				
				meshEdge = generateEdge(tri.p2, tri.p3, points, meshNodes, mapArray, meshEdgeArray);
				if( meshEdge != null) meshEdgeArray.push(meshEdge);
				
				meshEdge = generateEdge(tri.p3, tri.p1, points, meshNodes, mapArray, meshEdgeArray);
				if( meshEdge != null) meshEdgeArray.push(meshEdge);
			}
			
			trace("Delaunay: Finished Conversion: convertToMeshEdges: " + meshEdgeArray.length + " edges have been created!");
			return meshEdgeArray;
		}
						
		//return the polarCoor of an Edge Sprite with a max diff of 180 degrees
		public static function getPolarCoor180(e:EdgeSprite):Number
		{ 
			var angle:Number = Math.atan2 ((e.y2 - e.y1), (e.x2 - e.x1) );
			angle = Math.round(angle / Math.PI * 180); //working in degrees	
			angle = (angle < 0 ? 0 - angle : 180 - angle);
			//trace("Shape: getPolar: " + e.source.data["name"] + " to " + e.target.data["name"] + " | angle = " + angle);
			return angle; 
		}		
		
		//return the polarCoor of an Edge Sprite
		public static function getPolarCoor360(p1:Point, p2:Point):Number
		{ 
			var angle:Number = Math.atan2 ((p2.y - p1.y), (p2.x - p1.x) );
			angle = Math.round(angle / Math.PI * 180) + 180; //working in degrees	
			return angle; 
		}	
		
		// ========[ PRIVATE METHODS ]==============================================================
		
		// Create an Edge given the points. This function prevents duplicate edge creation.
		private static function generateEdge(sourceIdx:int, targetIdx:int, points:Array, meshNodes:Array, mapArray:Array, meshEdgeArray:Array):MeshEdge
		{
			var meshEdge:MeshEdge;
			var sourceAssigned:Boolean = false;
			var targetAssigned:Boolean = false;
			
			//checking mapArray to see if the Edge has already been created
			//reads: if edge, as defined by the the start and ends points exists, return
			if( (mapArray[sourceIdx] as Array).indexOf(targetIdx) != -1) return null; 
			if( (mapArray[targetIdx] as Array).indexOf(sourceIdx) != -1) return null;
			//trace("Delanunay catching errantEdges at ( " + sourceIdx + "," + targetIdx + " )");
			
			(mapArray[sourceIdx] as Array).push(targetIdx);
			meshEdge = new MeshEdge();	
			
			meshEdge.x1 = points[sourceIdx].x; meshEdge.y1 = points[sourceIdx].y;
			meshEdge.x2 = points[targetIdx].x; meshEdge.y2 = points[targetIdx].y;
			
			//trace("Delaunay: " + meshEdge.x1, meshEdge.y1 + " | " + meshEdge.x2, meshEdge.y2);
			if(Math.abs(meshEdge.x1 - meshEdge.x2) < 0.01 && Math.abs(meshEdge.y1 - meshEdge.y2) < 0.01) return null;
			
			for each(var node:MeshNode in meshNodes)
			{
				if( Math.abs(node.x - meshEdge.x1) < 0.1 && Math.abs(node.y - meshEdge.y1) < 0.1)
				{
					meshEdge.source = node; 
					sourceAssigned = true;
				} else if (Math.abs(node.x - meshEdge.x2) < 0.1 && Math.abs(node.y - meshEdge.y2) < 0.1)
				{
					meshEdge.target = node; 
					targetAssigned = true;
				}
				if(sourceAssigned && targetAssigned) break;
			}
			
			if(!(sourceAssigned && targetAssigned)) trace("Delaunay: generateEdge: This edge either does not have a target or a source or both!");
			
			meshEdge.name = "mE" + meshEdgeArray.length; //debug: meshEdgeArray is passed in strictly for debug purposes. Can be removed. 
			
			//trace("Delaunay: generateEdge: An Edge spanning " + sourceIdx + ", " + targetIdx + " has been created");
			return meshEdge;
		}
		
	}//end of class
}