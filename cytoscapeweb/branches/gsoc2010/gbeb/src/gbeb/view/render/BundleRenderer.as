package gbeb.view.render
{
	import flare.vis.data.DataSprite;
	import flare.vis.data.EdgeSprite;
	
	import flash.display.Sprite;
	import flash.geom.Point;

	public class BundleRenderer extends EdgeRenderer
	{
	    // singleton
		private static var _instance:BundleRenderer = new BundleRenderer();
        public static function get instance():BundleRenderer { return _instance; }
		
		public override function render(d:DataSprite):void
		{
			var e:EdgeSprite = d as EdgeSprite;
			if (e == null) return;
			
			addControlPoints(e);
			
			//strightLineSegRenderer(e);
			
			super.render(e);
			//trace("Bundler: " + d.name + " has been rendered! " + e.shape );
		}
		
		private function addControlPoints(e:EdgeSprite):void
		{	
			var props:Object = e.props;
			if (props == null) return;

            if (e.points == null) e.points = [];
			
			for each (var p:Point in props.$controlPointsArray)
			{
				if (p == null) continue;
				e.points.push(p.x);
				e.points.push(p.y);
			}
		}
		
		private function strightLineSegRenderer(e:EdgeSprite):void
		{
			//if(e.points.length % 2 != 0) return;
			
			e.graphics.beginFill(e.lineColor, e.lineAlpha);
			e.graphics.lineStyle(e.lineWidth, e.lineColor, e.lineAlpha);
			e.graphics.moveTo(e.source.x, e.source.y);
					
			for(var i:int = 0; i < e.points.length; i + 2)
			{
				e.graphics.lineTo(e.points[i], e.points[i+1]);
			}
			
			e.graphics.moveTo(e.target.x, e.target.y);
			e.graphics.endFill();
		}
		
	}
}