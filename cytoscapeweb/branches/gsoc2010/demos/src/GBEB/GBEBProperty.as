package GBEB
{
	import flash.geom.Point;

	public class GBEBProperty
	{
		public var controlPointsArray:Array = new Array();
		
		public function GBEBProperty():void
		{
		}
		
		public function addControlPoint(controlPoint:Point):void
		{
			controlPointsArray.push(controlPoint);
			
			return;
		}
	}//end of class
}