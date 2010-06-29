package GBEB
{
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.Rectangle;

	public class Adjustment extends Sprite
	{	
		private var _tool:Sprite;
		private var _bounds:Rectangle;
		private var _adjust:Boolean = false;
		
		public function Adjustment(bounds:Rectangle) 
		{
			_bounds = bounds;
		
				
			init();
			_tool.addEventListener(MouseEvent.CLICK, toggleAdjust);
			
		}
		
		private function init():void
		{			
			_tool = new Sprite();
			_tool.graphics.beginFill(0x0000FF);
			_tool.graphics.drawCircle(_bounds.right - 30, _bounds.bottom -30, 30);
			_tool.graphics.endFill();
		
			
			_tool.mouseEnabled = true;
			_tool.buttonMode = true;
			
			
			addChild(_tool);
			trace("Toolllll");
		}
		
		private function toggleAdjust(e:Event):void
		{
			_adjust = true;
			trace("Hello!!" + _adjust);
		}
		
	} // end of class
}