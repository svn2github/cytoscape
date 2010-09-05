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

Additional Remarks: 
Intersection of 2 lines is taken from Source: http://keith-hair.net/blog/2008/08/04/find-intersection-point-of-two-lines-in-as3

*/

package gbeb.util
{
	import flare.vis.data.Data;
	import flare.vis.data.EdgeSprite;
	
	import flash.display.Graphics;
	import flash.geom.Point;
	
	import gbeb.view.operator.router.MeshNode;

	public class GeometryUtil
	{
		
		// ========[ CONSTRUCTOR ]==================================================================
		
		/**
		 * This constructor will throw an error, as this is an abstract class. */
		
		public function GeometryUtil()
		{
			throw new Error("This is an abstract class.");
		}
		
		
		// ========[ PUBLIC METHODS ]===============================================================
		
		/**
		 * This function takes in 4 points, AB of line 1 and EF of line 2 and check if the lines intersect. It returns the intersection
		 * point if the lines intersect and null otherwise. Checks for intersection of Segment if as_seg is true.
		 * 
		 * Source: http://keith-hair.net/blog/2008/08/04/find-intersection-point-of-two-lines-in-as3*/

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
			
			// Do checks to see if intersection to endpoints distance is longer than actual Segments. 
			// Return null if it is with any.
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
		 * This function derives the position of the anchor point based of the location of the control point
		 * for a normal qradratic Bezier curve. 
		 *  
		 * The solution is represented in StackOverflow
		 * Credit belongs to WillyCornbread and solution+code given by Sly_cardinal.
		 */	
		
		public static function derivePoint(p0:Point, b1:Point, p2:Point, t:Number = 0.5):Point
		{
			var p:Point = new Point(deriveTerm(p0.x, b1.x, p2.x, t), deriveTerm(p0.y, b1.y, p2.y, t));
			return p;
		}

	
		/**
		 * This function uses accepts and array of 2D points and return the geometric center of all the points*/

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

		/**
		 * This function takes in 2 lines segment as 4 points, Line1 - AB | Line 2 ~ EF and returns the angle between them.
		 * Uses the idea of a dot product of vectors. Note the lines are directed, meaning it starts from A and end at B
		 * 
		 * Coded with reference to http://www.kynd.info/library/mathandphysics/dotProduct_01/*/
		
		public static function getAnglesFromLines(A:Point, B:Point, E:Point, F:Point):Number
		{
			var dotProduct:Number = (B.x - A.x) * (F.x - E.x) + (B.y - A.y) * (F.y - E.y); 
			var demoninator:Number = Point.distance(A, B) * Point.distance(E, F); //This is a multiplication of magnitude
			var angle:Number = Math.acos(dotProduct /demoninator);
			
			return angle;
		}
		
		/**
		 * This function is a 2 dimensional k- means clutering algorithm. It is a heuristic algorithm which 
		 * takes in a array of 2D points [p1..pn], and creates k (k = Ceil(n/2) ^ 0.5) number of clusters. It return 
		 * the array of cluters of points. The clusters are also arrays. 
		 * 
		 * This work is done with reference to:
		 * http://people.revoledu.com/kardi/tutorial/kMean/NumericalExample.htm 
		 * http://en.wikipedia.org/wiki/Determining_the_number_of_clusters_in_a_data_set
		 * http://en.wikipedia.org/wiki/K-means_clustering */
		
		public static function kmeans(points:Array):Array
		{ 
					
			if(points == null) throw new Error("Kmeans: The points array cannot be null")
			if(points.length == 0) throw new Error("Kmeans: There are 0 points in the array");
			for each(var p:* in points)  
			{
				if(! (p is Point)) throw new Error("Kmeans: " + p + " is not a point");
			}
			
			var clusters:Array = [];
			var centroids:Array = []; // Stores the centroid of each cluster
			var prevCentroids:Array = []; // Stores the array of previous Centroids for comparison
			var numClusters:int = Math.ceil( Math.pow( (points.length / 2), 0.5 ));
			const bundlingDist:int = 50; 
			
			// Clustering does not perform well with points <= 2, so a mannual fix is necessary
			if(points.length == 1) 
			{
				clusters.push(points);
				return clusters;
			} else if (points.length == 2)
			{
				if ( Point.distance(points[0], points[1]) < bundlingDist)
				{
					clusters.push(points);
					return clusters;
				} else {
					clusters.push(new Array(points[0]));
					clusters.push(new Array(points[1]));
					return clusters;
				}
			}
			
			for(var i:int = 0; i < numClusters; i++) 
			{
				clusters.push(new Array());
				centroids.push(new Point( points[i].x, points[i].y));
			}
			// The algorithm will loop between assigning the points to the clusters that the points are nearest to 
			// and recalculating the centroids for each clusters after all the points are assigned until the centroids 
			// does not shift any more. 
			do {	
				prevCentroids = copyCentroids(centroids); 
				assignPointsToClusters(points, centroids, clusters); 
				recalcuateCentroids(centroids, clusters);			
			} while(!clusteringComplete(centroids, prevCentroids))
			return clusters;
		}
		
		// ========[ PRIVATE METHODS ]==============================================================
					
		//This function uses a weaker condition, if Centroids.before = Centroid.after to check for clustering status. It is 
		//originally recommended to use check if Clusters.before and the Clusters.after after each iteration of the while 
		//loop is the same.
		//This method saves more computational time and space, while being reasonable reliable.
		private static function clusteringComplete(centroids:Array, prevCentroids:Array):Boolean
		{
			for(var i:int = 0; i < centroids.length; i++)
			{
				if(Math.abs(centroids[i].x - prevCentroids[i].x) > 1) return false;
				if(Math.abs(centroids[i].y - prevCentroids[i].y) > 1) return false;
			}
			return true;
		}
		
		//This function assign the points to the cluster which has the closest centroid to the point. 
		private static function assignPointsToClusters(points:Array, centroids:Array, clusters:Array):void
		{ 
			var distanceToCentroid:Array = []; //stores the distance between each point to the centroid at each round of iteration
			for each( var p:Point in points)
			{
				distanceToCentroid = []; 
				if(isNaN(p.x) || isNaN(p.y)) 
				{
					points.splice(p,1); 
					continue;
				}
				for each( var c:Point in centroids)
				{ 
					distanceToCentroid.push(Point.distance(c, p)); 
				} 
				clusters[getMinDisIndex(distanceToCentroid)].push(p); 
			} 
		}

		//This function recalcuates the position of the centroid of each clusters based on the clustering in each iteration
		private static function recalcuateCentroids(centroids:Array, clusters:Array):void
		{
			for(var i:int = 0; i < clusters.length; i++)
			{
				centroids[i] = findCentroidFromPoints(clusters[i]);
			}
		}
		
		// This function creates a copy of the centroids for evaluating if there are any more changes to the 
		// positions of centroids
		private static function copyCentroids(centroids:Array):Array
		{
			var prevCentroids:Array = new Array();		
			for(var i:int = 0; i < centroids.length; i++)
			{
				prevCentroids.push(new Point(centroids[i].x, centroids[i].y));
			}
			return prevCentroids;
		}

		// This fucntion returns the index of the cluster that the point is closest to.
		private static function getMinDisIndex(distances:Array):int
		{
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
		
		// This function calculates the x and y pos of each anchor point of quadratic bezier curve in order for the curve
		// to pass through the input (x,y)
		private static function deriveTerm(p0:Number, bt:Number, p2:Number, t:Number):Number
		{
			var negT:Number = 1 - t;
			var a0:Number = Math.pow(negT, 2) * p0;
			var a1:Number = 2 * negT * t;
			var a2:Number = Math.pow(t, 2) * p2;
			var p1:Number = (bt - a0 - a2) / a1;
			return p1;
		}
		
		
		
		
		/**
		 * Source: http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
		*/
		public static function segmentDistToPoint(segA:Point, segB:Point, p:Point):Number
		{
			var p2:Point = new Point(segB.x - segA.x, segB.y - segA.y);
			var something:Number = p2.x*p2.x + p2.y*p2.y;
			var u:Number = ((p.x - segA.x) * p2.x + (p.y - segA.y) * p2.y) / something;
			
			if (u > 1)
				u = 1;
			else if (u < 0)
				u = 0;
			
			var x:Number = segA.x + u * p2.x;
			var y:Number = segA.y + u * p2.y;
			
			var dx:Number = x - p.x;
			var dy:Number = y - p.y;
			
			var dist:Number = Math.sqrt(dx*dx + dy*dy);
			
			return dist;
		}
		

	} //end of class
}