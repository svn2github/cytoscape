package gbeb.view.operator.router {
	import flash.geom.Point;

    public class MeshEdge {
        public var x1:Number;
        public var y1:Number;
        public var x2:Number;
        public var y2:Number;
        public var source:MeshNode;
        public var target:MeshNode;
        public var name:String;
				public var sourceOriPos:Point;
				public var targetOriPos:Point;
				public var controlPoints:Array = [];
				public var dataEdgeIntersectionPairs:Array = []; //stores the dataEdge and intersection point pair, which is used in calculating the eventual 
																							// intersection of this edge with all the data curves that passes it
        
        
        public function MeshEdge():void {
                
        }
    }
}