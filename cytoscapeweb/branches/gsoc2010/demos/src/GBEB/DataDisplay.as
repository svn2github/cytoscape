package GBEB
{
	import flare.vis.data.EdgeSprite;
	import flare.vis.operator.encoder.PropertyEncoder;
	import flare.vis.operator.label.Labeler;
	
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.utils.Timer;
	


	//Helper class to Data
	public class DataDisplay extends Sprite
	{
		private var _bounds:Rectangle;
		private var _data:Data;
		private var _visBoundary:Sprite = new Sprite();
		private var _textFieldMouseTracker:TextField = new TextField();
		private var _mesh:Mesh; 
		private var _currentShape:Shape;
		//private var _timer:Timer = new Timer(1000);
		private var _counter:int = 0;
		
		private var _displayContainer:Sprite = new Sprite();
		private var _meshContainer:Sprite = new Sprite();
		private var _highlightShapeContainer:Sprite = new Sprite();
		private var _visCentroid:Sprite = new Sprite();
		private var _visEdgesContainer:Sprite = new Sprite();
		
		private var user:Object = {
			nodes: {shape: Shapes.WEDGE, lineColor: 0xffffffff},
			edges: {lineWidth:2}
		}
		
		public function DataDisplay(d:Data):void
		{
			_data = d;
			addChild(_visBoundary);
			
			
			_textFieldMouseTracker.selectable = false;
			addChild(_textFieldMouseTracker);
			//addChild(_textFieldGridTracker);
			addEventListener(MouseEvent.MOUSE_MOVE, mouseTracker, false, 0, true);
			
			/*_timer.start();
			_timer.addEventListener(TimerEvent.TIMER, function tick(e:TimerEvent):void 
			{
				trace("DataDisplay: Timer: " + _counter++);
				
			});*/
			//add timer (=
				
			addChild(_displayContainer);
			trace("DataDisplay added");
		} 
		
		public function cleanup():void
		{
			removeChild(_textFieldMouseTracker);
			removeChild(_displayContainer);
			removeEventListener(MouseEvent.MOUSE_MOVE, mouseTracker);
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
				for each (var p:Point in _mesh.nonRedundantShapeIndexArray)
				{
					var s:Shape = _mesh.returnShapeFromIndex(p);
					//trace("DataDisplay: Tracing Edge ", s.meshEdge.name + ": " + s.meshEdge.source.x, s.meshEdge.source.y, s.meshEdge.target.x, s.meshEdge.target.y);
				} 
				
				//displayGrids();
				//redrawMesh();
				
				trace("DataDisplay: Mesh is updated!");
			}
			
			private function displayMeshData(mouseX:int, mouseY:int):String
			{
				if(_mesh == null) return "No mesh";
				
				//variables used to adjust amount of info in grid display
				var highlightShape:Boolean = true;
				var displayCentroid:Boolean = true;
				var displayDirection:Boolean = true;
				var displayMeshEdges:Boolean = true;
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
					
					if(displayCentroid)
					{
						displayCentroidFxn(_currentShape);
					}
					
					if(displayDirection)
					{
						shapeInfo += "\nShape Direction = " + _currentShape.direction;
					}
					
					if(displayMeshEdges)
					{
						shapeInfo += drawMeshEdges(_currentShape);
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
			
			public function displayGrids():void
			{
				if(_mesh == null) return;
				
				var grid:Array = _mesh.grid;
				
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
			
				//trace("DD :XX: "+ _highlightShapeContainer.numChildren);
				
				while(_highlightShapeContainer.numChildren > 0) 	
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
			
			private function displayCentroidFxn(s:Shape):void
			{	
				if(s.centroid == null) return;

				if(_displayContainer.contains(_visCentroid)) _displayContainer.removeChild(_visCentroid);
			
				_visCentroid = new Sprite();
			
				_visCentroid.graphics.beginFill( 0x121212);
				_visCentroid.graphics.drawCircle(s.centroid.x, s.centroid.y, 2);
				_visCentroid.graphics.endFill();
				
				_displayContainer.addChild(_visCentroid);
			}
			
			private function drawMeshEdges(s:Shape):String
			{
				var e:EdgeSprite = s.meshEdge;
								
				if( e == null) return "";
				
				while(_visEdgesContainer.numChildren > 0)
				{
					_visEdgesContainer.removeChildAt(0);
				} 				
				//trace("DataDisplay: " + e.name + " :: " + s.meshEdge.x1, s.meshEdge.x2, s.meshEdge.y1); //  .x1, e.y1, " | ", e.x2, e.y2, e.target.x);

				var visEdge:Sprite = new Sprite();
				visEdge.graphics.beginFill(0x42C0FB, 0);
				visEdge.graphics.lineStyle(3, 0xFF6347);
				visEdge.graphics.moveTo(e.source.x, e.source.y);
				visEdge.graphics.lineTo(e.target.x, e.target.y);
				visEdge.graphics.endFill();
				
				_visEdgesContainer.addChild(visEdge);
				
				_displayContainer.addChild(_visEdgesContainer);		
				//trace("DataDisplay: _displayContainer.numChild: " + _displayContainer.numChildren);
				
				return "\nMeshEdge no: " + e.name;
			}
			
			private function redrawMesh():void
			{
				var visEdgeContainer:Sprite = new Sprite();
				var visEdge:Sprite = new Sprite();
				for each (var edge:EdgeSprite in _mesh._mesh.edges)
				{
					
					visEdge.graphics.lineStyle(2, 0x42C0FB);
					visEdge.graphics.moveTo(edge.source.x, edge.source.y);
					visEdge.graphics.lineTo(edge.target.x, edge.target.y);
					
					visEdge.graphics.beginFill(0x42426F,0);
					visEdge.graphics.lineStyle(2, 0x42C0FB);
					visEdge.graphics.drawCircle(edge.source.x, edge.source.y, 2);
					visEdge.graphics.drawCircle(edge.target.x, edge.target.y, 2);
					visEdge.graphics.endFill();
					
					visEdgeContainer.addChild(visEdge);
				}
			
				_displayContainer.addChild(visEdgeContainer);
			
				displayControlPoints();
			}
			
			private function displayControlPoints():void
			{
				var visControlPoints:Sprite = new Sprite();
				var visCP:Sprite = new Sprite();
				var cpArray:Array;

				
				
				for each (var dataEdge:EdgeSprite in _data.edges)
				{
					cpArray = dataEdge.props.GBEBProperty.controlPointsArray;
					
					if(cpArray == null || cpArray.length <= 0) continue;
					
					for each ( var cp:Point in cpArray)
					{
						if (cp == null) continue;
						
						visCP.graphics.beginFill(0xFFFFFF,1);
						visCP.graphics.drawCircle(cp.x, cp.y, 3);
						visCP.graphics.endFill();
						
						visControlPoints.addChild(visCP);
					}
				}
				
				_displayContainer.addChild(visControlPoints);
				
			}
			
	
	}// end of class
}