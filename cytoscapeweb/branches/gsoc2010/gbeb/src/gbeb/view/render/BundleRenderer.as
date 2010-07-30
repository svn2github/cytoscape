package gbeb.view.render
{
	import flare.vis.data.DataSprite;
	import flare.vis.data.EdgeSprite;
	import flare.vis.data.NodeSprite;
	
	import flash.display.Graphics;
	import flash.geom.Point;
	
	import gbeb.util.GeometryUtil;

	public class BundleRenderer extends EdgeRenderer
	{
	    // singleton
		private static var _instance:BundleRenderer = new BundleRenderer();
        public static function get instance():BundleRenderer { return _instance; }
		
		public override function render(d:DataSprite):void
		{
			var e:EdgeSprite = d as EdgeSprite;
			var ctrl:Array = e.props.$controlPointsArray;
			//var eCopy:EdgeSprite = cloneEdgeSprite(e);
			
			if (e == null) return;
			//curveSegRenderer(e);
			
			//strightLineSegRenderer(e);
			//trace("BundleRenderer: LineColour: " + e.lineColor);
			
			if (ctrl != null && ctrl.length > 0)
			     addControlPoints(e);
			     
			super.render(e);
			
			
			/*if(ctrl != null && ctrl.length > 3) //debug
			{
				super.render(e);
			} */
			
			//addControlPoints(eCopy, true);
			//super.render(eCopy); //debug
			//trace("Bundler: " + d.name + " has been rendered! " + e.shape );
		}
		
		private function addControlPoints(e:EdgeSprite, isClone:Boolean = false):void
		{	
			var props:Object = e.props;
			if (props == null) return;

            var points:Array = e.points;

            if (points == null) { 
                points = [];
            } else if (points.length > 0) {
            	// Remove previous points, or it will keep adding the same ones again,
            	// every time the edge is rendered/updated
            	var length:int = points.length;
            	for (var i:uint=0; i < length; i++) {
            	   points.pop();
            	}
            }
			
			for each (var p:Point in props.$controlPointsArray) {
				if (p == null) continue;
				points.push(p.x);
				points.push(p.y);
			}
			
			if (e.points == null) e.points = points;
			
			if (isClone) trace("BundleRouter: Adding CP to clone!");
		}
		
		private function strightLineSegRenderer(e:EdgeSprite):void
		{			
			var g:Graphics = e.graphics;
			var s:Point = new Point(e.source.x, e.source.y); 
			var t:Point = new Point(e.target.x, e.target.y);
			var tempPoint:Point;
			var dPoint:Point; //derived point
			var stDist:Number = GeometryUtil.calculateDistanceBetweenPoints(s,t); //Distance between the source and target nodes
			
			var ctrl:Array = e.props.$controlPointsArray;
			var ctrlgradient:Array = e.props.$CPGradientArray; //used to store the gradient of each control point
			
			if(ctrl == null || ctrlgradient == null) return;
			//if(ctrl.length != ctrlgradient.length) trace("BundleRenderer: Warning! Length of ctrl and ctrlGradient is not the same!");
			g.clear();
			//g.beginFill(e.lineColor, e.lineAlpha);
			g.lineStyle(e.lineWidth, e.lineColor, e.lineAlpha);
			g.moveTo(s.x, s.y)
			
			//testing special case
			if (ctrl.length > 0 )
			{				
				ctrl.push(t);
				t = ctrl[1]; 
				//g.curveTo(ctrl[0].x, ctrl[0].y, e.target.x, e.target.y);
				for(var i:int = 0; i < ctrl.length - 1; i++)
				{
					tempPoint = new Point(ctrl[i].x, ctrl[i].y);
					//if CP is too close to the source or target nodes)
					if( GeometryUtil.calculateDistanceBetweenPoints(tempPoint , s) < 3
					|| GeometryUtil.calculateDistanceBetweenPoints(tempPoint, t) < 3 )
					{
						g.lineTo(tempPoint.x, tempPoint.y);
						g.lineTo(t.x, t.y);
						trace("BundleRouter: Distance Between CP and Nodes is too short!");
						if(Math.abs(t.x - e.target.x) < 0.5 && Math.abs(t.y - e.target.y) < 0.5)
						{
							
							break;
						} else {
							s = ctrl[i+1]; t = ctrl[i+2];
							continue;
						}
					} 
					
					var ratio:Number = GeometryUtil.calculateDistanceBetweenPoints(s, ctrl[i]) / GeometryUtil.calculateDistanceBetweenPoints(s, ctrl[i+1]);
					ratio = ( ratio < 0.3 ? 0.3 : ratio); ratio = ( ratio > 0.7 ? 0.7 : ratio);
					
					dPoint = GeometryUtil.derivePoint(s, ctrl[i], t, ratio);
					g.curveTo(dPoint.x, dPoint.y, t.x, t.y);
					s = ctrl[i]; t = ctrl[i+1];
				}
		
			} else {
				//g.lineTo(e.target.x, e.target.y);
			}
			/*
			for(var i:int = 0; i < e.points.length; i + 2)
			{
				e.graphics.lineTo(e.points[i], e.points[i+1]);
			} */
			
			//g.endFill();
			
			showDebugCP(e);

		}
		
		private function showDebugCP(e:EdgeSprite):void
		{
			var g:Graphics = e.graphics;
			
			for each (var p:Point in e.props.$controlPointsArray) { //debug
				if(p != null)
				{
					g.beginFill(0x993232);
					g.drawCircle(p.x, p.y, 5);
					g.endFill();
				}
			} 
		}
				//For debug
				private function cloneEdgeSprite(e:EdgeSprite):EdgeSprite
				{
					var eCopy:EdgeSprite = new EdgeSprite();
					eCopy.source = cloneNodeSprite(e.source);
					eCopy.target = cloneNodeSprite(e.target);
					eCopy.x1 = eCopy.source.x; eCopy.y1 = eCopy.source.y;
					eCopy.x2 = eCopy.target.x; eCopy.y2 = eCopy.target.y;
					eCopy.points = e.points;
					eCopy.shape = e.shape;
					
					return eCopy;
				}
				
				//For debug
				private function cloneNodeSprite(n:NodeSprite):NodeSprite
				{
					var nCopy: NodeSprite = new NodeSprite();
					nCopy.data = n.data;
					nCopy.x = n.x;
					nCopy.y = n.y;
					
					return nCopy;
				}
		
	}
}