package GBEB
{
	import flare.vis.data.EdgeSprite;
	
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.utils.Timer;
	
	import org.osmf.events.TimeEvent;



	//Helper class to Data
	public class DataDisplay extends Sprite
	{
		private var _bounds:Rectangle;
		private var _visBoundary:Sprite = new Sprite();
		private var _textFieldMouseTracker:TextField = new TextField();
		private var _mesh:Mesh; 
		private var _currentShape:Shape;
		private var _timer:Timer = new Timer(1000);
		private var _counter:int = 0;
		
		private var _displayContainer:Sprite = new Sprite();
		private var _meshContainer:Sprite = new Sprite();
		private var _highlightShapeContainer:Sprite = new Sprite();
		
		public function DataDisplay()
		{
			addChild(_visBoundary);
			
			
			_textFieldMouseTracker.selectable = false;
			addChild(_textFieldMouseTracker);
			//addChild(_textFieldGridTracker);
			addEventListener(MouseEvent.MOUSE_MOVE, mouseTracker,true);
			
			/*_timer.start();
			_timer.addEventListener(TimerEvent.TIMER, function tick(e:TimerEvent):void 
			{
				trace("DataDisplay: Timer: " + _counter++);
				
			});*/
			//add timer (=
				
			addChild(_displayContainer);
			trace("DataDisplay added");
		} 
		
		private function redraw():void
		{
			
			
			
			
		}
		
		public function updateBounds(rec:Rectangle = null):void
		{
			_bounds = rec;
			_visBoundary.graphics.clear();	
			_visBoundary.graphics.beginFill(0x000000, 0);
			_visBoundary.graphics.lineStyle(3,0xFF0000);
			_visBoundary.graphics.drawRect(_bounds.x, _bounds.y, _bounds.width, _bounds.height);
			_visBoundary.graphics.endFill();
	
		}
		
		private function mouseTracker(e:MouseEvent):void
		{
			_textFieldMouseTracker.width = 120; 
			_textFieldMouseTracker.border = true; _textFieldMouseTracker.background = true;
			_textFieldMouseTracker.x = e.stageX + 10 ;   _textFieldMouseTracker.y = e.stageY + 10;
			_textFieldMouseTracker.text = "   " + e.stageX + " , "+ e.stageY;
			
			_textFieldMouseTracker.text += displayMeshData(e.stageX, e.stageY);
			
			
		}

			////////////////////////////////////////////////////
			//function for Displaying information of the Mesh
			////////////////////////////////////////////////////
			public function updateMesh(mesh:Mesh):void
			{
				_mesh = mesh;
			}
			
			private function displayMeshData(mouseX:int, mouseY:int):String
			{
				if(_mesh == null) return "No mesh";
				
				//variables used to adjust amount of info in grid display
				var highlightShape:Boolean = true;
				var displayDirection:Boolean = true;
				var displayEdgeSourceTarget:Boolean = true;
				
				var shapeInfo:String;	
				
				_currentShape = _mesh.returnShape(mouseX, mouseY);
				
				if(_currentShape != null)
				{	
					
					shapeInfo = "\n" + _currentShape.gridIndex[0].toString();
					
					if(highlightShape)
					{
						highlightShapeFxn(_currentShape);
					}
					
					if(displayDirection)
					{
						shapeInfo += "\nShape Direction = " + _currentShape.direction;
					}
					
				
					if(displayEdgeSourceTarget)
					{
						for each (var edge:EdgeSprite in _currentShape.storedDataEdges)
						{
							shapeInfo += "\n" + edge.source.data["name"] + " to " + edge.target.data["name"];
							
						}
					}
		
				}
				
				return shapeInfo;
				
			}
			public function displayMesh():void
			{
				if(_mesh == null) return;
				
				var grid:Array = _mesh._grid;
				
				for each (var shape:Shape in grid)
				{
				
					for each ( var g:Rectangle in shape.storedGrids)
					{
						var visGrid:Sprite = new Sprite();
						visGrid.graphics.beginFill(0x000000, 0);
						visGrid.graphics.lineStyle(0.2,0xFF0000,0.5);
						visGrid.graphics.drawRect(g.x, g.y, g.width, g.height);
						visGrid.graphics.endFill();
					}
					_meshContainer.addChild(visGrid);
					
					/*trace("Mesh: Display Shape: Drawing..." + g.x);
					addEventListener(MouseEvent.MOUSE_MOVE, function mouseoverGrid(e:MouseEvent):void{
					
					//_displayContainer.removeChildAt(_displayContainer.numChildren - 1);
					
					var _textFieldGridTracker:TextField = new TextField();
					_textFieldGridTracker.x = e.stageX; _textFieldGridTracker.y = e.stageY + 20;
					_textFieldGridTracker.text = shape.gridIndex + " has " + shape.storedDataEdges.length + " edges";
					_displayContainer.addChild(_textFieldGridTracker);
					trace("Hi");
					}); */				
				}
				_displayContainer.addChild(_meshContainer);
			}
			
			private function highlightShapeFxn(shape:Shape):void
			{
			
				while(_highlightShapeContainer.numChildren != 0) 	
				{	
					_highlightShapeContainer.removeChildAt(0);
				}
					
				for each ( var p:Point in shape.gridIndex)
				{
					var Coor:Point = _mesh.returnXYFromIndex(p);
					
					
					var visGrid:Sprite = new Sprite();
					visGrid.graphics.beginFill(0x000000, 0);
					visGrid.graphics.lineStyle(0.4, 0x00FF00, 1);
					visGrid.graphics.drawRect(Coor.x, Coor.y, _mesh.gridSize, _mesh.gridSize);
					visGrid.graphics.endFill();
					
					//The lines that define the grids may not be asthetically pleasing but it will have to do for now. (=
					_highlightShapeContainer.addChild(visGrid);
				}
				
				_displayContainer.addChild(_highlightShapeContainer);
			}
	
	}// end of class
}