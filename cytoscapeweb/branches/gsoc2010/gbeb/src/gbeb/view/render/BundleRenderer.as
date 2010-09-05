package gbeb.view.render
{
	import flare.util.Shapes;
	import flare.vis.data.DataSprite;
	import flare.vis.data.EdgeSprite;
	import flare.vis.data.NodeSprite;
	
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.geom.Point;
	
	import gbeb.util.GeometryUtil;
	import gbeb.view.render.EdgeRenderer;

	public class BundleRenderer extends EdgeRenderer
	{
	  // singleton
		private static var _instance:BundleRenderer = new BundleRenderer();
    public static function get instance():BundleRenderer { return _instance; }
		
		// ========[ CONSTRUCTOR ]==================================================================
		
		public override function render(d:DataSprite):void
		{
			var e:EdgeSprite = d as EdgeSprite;
			var ctrl:Array = e.props.$controlPointsArray;	
			if (e == null) return;
			
			if(ctrl != null && ctrl.length > 0) //debug
			{
				super.render(e);
			} 
		}
	}
}