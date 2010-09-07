package gbeb.view.operator.router
{
	import flash.geom.Point;

	public class pathMap
	{
		private var _map:Array = new Array();
		private var _mapIndex:Array = new Array();
		
		public function pathMap(nodes:Array):void
		{	
			for each(var p1:Point in nodes)
			{
				var array:Array = new Array();		
				for each (var p2:Point in nodes)
				{
					var count:int = 0;
					array.push(count);			
				}
				_map.push(array);
				_mapIndex.push(p1);
			}
			//trace("PathMap: Array created. Length: " + _map.length + " | mapIndex.length: " + _mapIndex.length);
		}
		
		public function getPathCount(source:Point, target:Point):int
		{
			//trace("pathMap: (before) getPathCount: map.indexOf(source): " + _mapIndex.indexOf(source));
			var array:Array = _map[_mapIndex.indexOf(source)];
			
			trace("pathMap: (after) getPathCount: " + array );
			return array[array.indexOf(target)];
		}
		
		public function insertPath(source:Point, target:Point):void
		{
			if(source == target) return;
			if(_mapIndex.indexOf(source) == -1 || _mapIndex.indexOf(target) == -1) return;
			
			var array:Array = _map[_mapIndex.indexOf(source)];
			array[_mapIndex.indexOf(target)] += 1;
			
			trace("Pathmap: source, target: " + source, target);
			
			trace("pathMap: " + array);
			
			array = _map[_mapIndex.indexOf(target)];
			array[_mapIndex.indexOf(source)] += 1;
		}
	} //end of Class
}