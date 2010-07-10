package GBEB
{
	import flare.util.Shapes;
	import flare.vis.data.DataSprite;
	import flare.vis.data.EdgeSprite;
	import flare.vis.data.NodeSprite;
	import flare.vis.data.render.EdgeRenderer;
	
	import flash.geom.Point;

	public class Bundler extends EdgeRenderer
	{
		public function Bundler()
		{
		}
		
		public override function render(d:DataSprite):void
		{
			if (!(d is EdgeSprite)) return;
			
			var e:EdgeSprite = d as EdgeSprite;
			
			e.lineColor = 0xFFFFC0CB;
			e.shape = Shapes.BEZIER;
			
			addControlPoints(e);
			
			//strightLineSegRenderer(e);
			
			super.render(e);
			
			//trace("Bundler: " + d.name + " has been rendered! " + e.shape );
			
			return;
		}
		
		private function addControlPoints(e:EdgeSprite):void
		{	
			var prop:GBEBProperty = e.props.GBEBProperty;
			
			if(prop == null) return;
			
			if(e.points == null) e.points = new Array();
			
			//trace("Bundler: ", e, e.points, prop);
			
			for each (var p:Point in prop.controlPointsArray)
			{
				if(p == null) continue;
				
				e.points.push(p.x);
				e.points.push(p.y);
			}
			
			//trace("Bundler: addingControlPoints!");
			
			return;
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