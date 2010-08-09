package gbeb.util
{
	import flare.vis.data.Data;
	import flare.vis.data.EdgeSprite;
	
	import flash.display.Graphics;
	import flash.geom.Point;
	
	import gbeb.view.operator.router.MeshNode;

	public class GeometryUtil
	{
		public function GeometryUtil()
		{
		}
		
		//Intersection of 2 lines Source: http://keith-hair.net/blog/2008/08/04/find-intersection-point-of-two-lines-in-as3
		//---------------------------------------------------------------
		//Checks for intersection of Segment if as_seg is true.
		//Checks for intersection of Line if as_seg is false.
		//Return intersection of Segment AB and Segment EF as a Point
		//Return null if there is no intersection
		//---------------------------------------------------------------
		public static function lineIntersectLine(A:Point,B:Point,E:Point,F:Point,as_seg:Boolean=true):Point {
			var ip:Point;
			var a1:Number;
			var a2:Number;
			var b1:Number;
			var b2:Number;
			var c1:Number;
			var c2:Number;
			
			a1= B.y-A.y;
			b1= A.x-B.x;
			c1= B.x*A.y - A.x*B.y;
			a2= F.y-E.y;
			b2= E.x-F.x;
			c2= F.x*E.y - E.x*F.y;
			
			var denom:Number=a1*b2 - a2*b1;
			if (denom == 0) {
				return null;
			}
			ip=new Point();
			ip.x=(b1*c2 - b2*c1)/denom;
			ip.y=(a2*c1 - a1*c2)/denom;
			
			//---------------------------------------------------
			//Do checks to see if intersection to endpoints
			//distance is longer than actual Segments.
			//Return null if it is with any.
			//---------------------------------------------------
			if(as_seg){
				if(Math.pow(ip.x - B.x, 2) + Math.pow(ip.y - B.y, 2) > Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2))
				{
					return null;
				}
				if(Math.pow(ip.x - A.x, 2) + Math.pow(ip.y - A.y, 2) > Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2))
				{
					return null;
				}
				
				if(Math.pow(ip.x - F.x, 2) + Math.pow(ip.y - F.y, 2) > Math.pow(E.x - F.x, 2) + Math.pow(E.y - F.y, 2))
				{
					return null;
				}
				if(Math.pow(ip.x - E.x, 2) + Math.pow(ip.y - E.y, 2) > Math.pow(E.x - F.x, 2) + Math.pow(E.y - F.y, 2))
				{
					return null;
				}
			}
			return ip;
		}
		
		/**
		 * This function uses Pythagoras' Theorem to calculate the distance between node1 and node2.
		 */
		//Step 4b.1 This function checks if 2 nodeSprites are too close together. The minimum distance is
		//defined by the const _meshNodesMinDistance
		public static function calculateDistanceBetweenNodes(node1:MeshNode, node2:MeshNode):Number
		{
			var distance:Number = 0;
			
			//this is Pythogoras' Theorem
			distance = Math.sqrt( Math.pow((node1.x - node2.x), 2) + Math.pow((node1.y - node2.y), 2));
			//if (distance < _meshNodesMinDistance) trace("Mesh: CalculateDistanceBetweenNodes: " + distance,  "|", 
			//    node1.x, node1.y, "||", node2.x, node2.y);
			
			return distance;
		}
		
		public static function calculateDistanceBetweenPoints(p1:Point, p2:Point):Number
		{
			var distance:Number = 0;
			
			//this is Pythogoras' Theorem
			distance = Math.sqrt( Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2));
			//if (distance < _meshNodesMinDistance) trace("Mesh: CalculateDistanceBetweenNodes: " + distance,  "|", 
			//    node1.x, node1.y, "||", node2.x, node2.y);
			
			return distance;
		}
		
		
		/**
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
		
		/**
		 * This two function below derives the position of the anchor point based of the location of the control point.
		 * 
		 * Credit belongs to WillyCornbread and solution+code given by Sly_cardinal.
		 */	
		public static function derivePoint(p0:Point, b1:Point, p2:Point, t:Number = 0.5):Point
		{
			var p:Point = new Point(deriveTerm(p0.x, b1.x, p2.x, t), deriveTerm(p0.y, b1.y, p2.y, t));
			return p;
		}

		private static function deriveTerm(p0:Number, bt:Number, p2:Number, t:Number):Number
		{
			var negT:Number = 1 - t;
			
			var a0:Number = Math.pow(negT, 2) * p0;
			var a1:Number = 2 * negT * t;
			var a2:Number = Math.pow(t, 2) * p2;
			
			var p1:Number = (bt - a0 - a2) / a1;
			return p1;
		}
		
		
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
		
		public static function findCentroidFromPoints(points:Array):Point
		{
			var avgX:Number = 0;
			var avgY:Number = 0;
			var numPoints:int = points.length;
			
			for each (var p:Point in points)
			{
				avgX += p.x;
				avgY += p.y;
			}
			
			return new Point( (avgX / numPoints), (avgY / numPoints));
		}		

		// 2 dimensional k- means clutering algorithm
		// This is a heuristic algorithm which takes in a array of 2D points [p1..pn], and creates k (k = Ceil(n/2) ^ 0.5),
		// clusters and return the array of cluters of points
		// This work is done with reference to: 
		// http://people.revoledu.com/kardi/tutorial/kMean/NumericalExample.htm
		// http://en.wikipedia.org/wiki/Determining_the_number_of_clusters_in_a_data_set
		// http://en.wikipedia.org/wiki/K-means_clustering
		public static function kmeans(points:Array):Array
		{ //check 1
			const bundlingDist:int = 50;
			
			if(points == null) throw new Error("Kmeans: The points array cannot be null")
			if(points.length == 0) throw new Error("Kmeans: There are 0 points in the array");
			for each(var p:* in points)  //necessary?
			{
				if(! (p is Point)) throw new Error("Kmeans: " + p + " is not a point");
			}
			
			var clusters:Array = [];
			var prevCentroids:Array = [];
			var centroids:Array = []; //stores the centroid of each cluster
			var numClusters:int = Math.ceil( Math.pow( (points.length / 2), 0.5 ));
			var iterations:int = 0; //debug
			
			if(points.length == 1) //?clustering does not perform well with points <= 2
			{
				clusters.push(points);
				return clusters;
			} else if (points.length == 2)
			{
				if ( calculateDistanceBetweenPoints(points[0], points[1]) < bundlingDist)
				{
					clusters.push(points);
					return clusters;
				} else {
					clusters.push(new Array(points[0]));
					clusters.push(new Array(points[1]));
					return clusters;
				}
			}
			
			//trace("GeoUtil: Kmeans: There will be " + numClusters + " clusters created");
			
			for(var i:int = 0; i < numClusters; i++) //clusters are indexed at 0
			{
				clusters.push(new Array());
				centroids.push(new Point( points[i].x, points[i].y));
				//prevCentroids.push(new Point(-1, -1)); //not necessary, since copyCentroids is called before clustering check
			}
				
			do {	
				//trace("GeoUtil: kmeans: Iterations: " + ++iterations);
				prevCentroids = copyCentroids(centroids);
				assignPointsToClusters(points, centroids, clusters);
				recalcuateCentroids(centroids, clusters);		
				
			} while(!clusteringComplete(centroids, prevCentroids))
		
			return clusters;
		}
					
					//this function use a weaker condition, if Centroids.before = Centroid.after to check for clustering status. It is originally recommended to use
					// check if Clusters.before and the Clusters.after after each iteration of the while loop is the same.
					// This method saves more computational time and space, while it works very reliable for reasonably ( > 20 points) large set of data
					private static function clusteringComplete(centroids:Array, prevCentroids:Array):Boolean
					{
						for(var i:int = 0; i < centroids.length; i++)
						{
							if(Math.abs(centroids[i].x - prevCentroids[i].x) > 1) return false;
							if(Math.abs(centroids[i].y - prevCentroids[i].y) > 1) return false;
						}
						return true;
					}
					
					//
					private static function assignPointsToClusters(points:Array, centroids:Array, clusters:Array):void
					{
						var distanceToCentroid:Array = []; //stores the distance between each point to the centroid at each round of iteration
						for each( var p:Point in points)
						{
							distanceToCentroid = [];
							for each( var c:Point in centroids)
							{
								distanceToCentroid.push(calculateDistanceBetweenPoints(c, p));
							}
							clusters[getMinDisIndex(distanceToCentroid)].push(p);
						}
						
						for each (var set:Array in clusters) //debug
						{
							var traceString:String = "";
							for each (var p:Point in set)
							{
								traceString += p.toString() + " , ";
							}
							//trace("GeoUtil: kmeans: Points in this clusters: " + traceString);
						}
					}
		
					//this function recalcuates the (x,y) value based on the new clusters
					private static function recalcuateCentroids(centroids:Array, clusters:Array):void
					{
						for(var i:int = 0; i < clusters.length; i++)
						{
							centroids[i] = findCentroidFromPoints(clusters[i]);
							//trace("GeoUtil: Kmeans: Centroid" + i + " is placed at " + centroids[i].toString());
						}
					}
					
					//
					private static function copyCentroids(centroids:Array):Array
					{
						var prevCentroids:Array = new Array();
						
						for(var i:int = 0; i < centroids.length; i++)
						{
							prevCentroids.push(new Point(centroids[i].x, centroids[i].y));
						}
						
						return prevCentroids;
					}

		
					private static function getMinDisIndex(distances:Array):int
					{
						//if(distances.length <= 0) return -1;
						var minDis:Number = Number.MAX_VALUE;
						var minDisIndex = -1;
						
						for(var i:int=0; i < distances.length;i++)
						{
							if(distances[i] < minDis)
							{
								minDis = distances[i];
								minDisIndex = i;
							}
						}
						
						return minDisIndex;
					}
		

	} //end of class
}