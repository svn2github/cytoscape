package gbeb.util.delaunay
{
	public class ITriangle {
		
		public var p1, p2, p3; //the 3 points that make up the triangle (references key positions in points array)
		public var eArray:Array; //this will store the edge IDs of all three triangle edges
		
		public function ITriangle() {
		
		}
	}
}