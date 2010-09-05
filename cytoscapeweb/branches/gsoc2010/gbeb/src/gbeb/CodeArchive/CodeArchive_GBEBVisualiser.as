// ========[ Visual Debug Functions ]=================================================
// Copy and paste this to the end of the GBEBRouter class (within the class braces obviously)
// to use this as a debug tool

private function drawMesh():void {
	var vis:Visualization = visualization;
	var edges:Array = _mesh.edges;
	var edge:MeshEdge
	
	for each (edge in edges) {
		// display meshEdges
		vis.graphics.lineStyle(3, 0x000000);
		vis.graphics.moveTo(edge.source.x, edge.source.y);
		vis.graphics.lineTo(edge.target.x, edge.target.y);
		
		//vis.graphics.beginFill(0x42426F,0);
		vis.graphics.beginFill(0x222222, 0);
		vis.graphics.lineStyle(2, 0x42C0FB);
		vis.graphics.drawCircle(edge.source.x, edge.source.y, 2);
		vis.graphics.drawCircle(edge.target.x, edge.target.y, 2);
		vis.graphics.endFill();
	}
	
	// display control points
	var cpArray:Array;
	
	for each (edge in edges) {
		cpArray = edge.controlPoints;
		
		//trace("GBEB Router: Drawing Edge!");
		
		if (cpArray == null || cpArray.length <= 0) continue;
		trace("GBEB Router: Drawing Edge!");
		for each ( var cp:Point in cpArray) {
			if (cp == null) continue;
			trace("GBEB Router: Drawing Edge!");
			vis.graphics.beginFill(0x00FF00,1);
			vis.graphics.drawCircle(cp.x, cp.y, 5);
			vis.graphics.endFill();
		}
	}
}

// TODO: remove ?
// ##############################################################################
// DEBUG ONLY:

// to display meshEdges before Delaunay trigulation/for checking);
private function renderOriginalMeshEdges():void
{
	var vis:Visualization = visualization;
	var visEdgeContainer:Sprite = new Sprite(); 
	var visEdges:Graphics = visEdgeContainer.graphics;
	var edges:Array = _mesh.edges;
	var edge:MeshEdge;
	
	for each (edge in edges) {
		visEdges.lineStyle(1, 0xFF0000);
		visEdges.moveTo(edge.source.x, edge.source.y);
		visEdges.lineTo(edge.target.x, edge.target.y);
		
		visEdges.beginFill(0x222222, 0);
		visEdges.lineStyle(2, 0x42C0FB);
		visEdges.drawCircle(edge.source.x, edge.source.y, 2);
		visEdges.drawCircle(edge.target.x, edge.target.y, 2);
		visEdges.endFill();
	}
	visualization.addChild(visEdgeContainer);
}



private function displayGrids():void {
	
	var displayCentroid:Boolean = true;
	var displayGridLines:Boolean = false;
	
	if (visualization == null) return;
	var graphics:Graphics = visualization.graphics;
	graphics.clear();
	
	if (displayGridLines)
	{
		for each (var shape:Shape in _grid) {                   
			for each (var r:Rectangle in shape.storedGrids) {
				graphics.beginFill(0x000000, 0);
				graphics.lineStyle(0.1,0xFF0000,0.5);
				graphics.drawRect(r.x, r.y, r.width, r.height);
				graphics.endFill();
				
			}
		}        
		//trace("Mesh: Display Shape: Drawing..." + g.x);
		/*visualization.addEventListener(MouseEvent.MOUSE_MOVE, function mouseoverGrid(e:MouseEvent):void{
		
		trace("Hi! Mouse Moving..." + e.stageX, e.stageY, mouseMoveCounter++);
		
		//_displayContainer.removeChildAt(_displayContainer.numChildren - 1);
		
		var _textFieldGridTracker:TextField = new TextField();
		_textFieldGridTracker.x = e.stageX; _textFieldGridTracker.y = e.stageY + 20;
		_textFieldGridTracker.text = shape.gridIndex + " has " + shape.storedDataEdges.length + " edges";
		visualization.addChild(_textFieldGridTracker); 
		});          */    
		
		
	}
}


private function addDataDisplay():void
{
	var vis:Visualization = visualization;
	vis.addChild(_textFieldMouseTracker);
	vis.addChild(_displayContainer);
	
	vis.addEventListener(MouseEvent.MOUSE_MOVE, mouseTracker);
}

private function mouseTracker(e:MouseEvent):void
{
	displayMeshData(e.stageX, e.stageY);
	_textFieldMouseTracker.width = 180;  _textFieldMouseTracker.height = 150; 
	_textFieldMouseTracker.border = true; _textFieldMouseTracker.background = true;
	_textFieldMouseTracker.x = _bounds.width * 0.75 ;   _textFieldMouseTracker.y = _bounds.height * 0.75;
	_textFieldMouseTracker.text = e.stageX + " , "+ e.stageY + " | " + returnIndexFromXY(e.stageX, e.stageY).toString();
	
	_textFieldMouseTracker.text += displayMeshData(e.stageX, e.stageY);
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
	var highLightShape:Boolean = true;
	
	var shapeInfo:String;	
	
	var _currentShape:Shape = returnShape(mouseX, mouseY);
	
	if(_currentShape != null)
	{	
		
		shapeInfo = "\n" + "No. Edges: " + _currentShape.storedDataEdges.length;
		
		if(highlightShape)
		{
			//highlightShapeFxn(_currentShape);
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
		if(highlightShape)
		{
			highlightThisShape(_currentShape);
		}
	}
	
	return shapeInfo;
}

private function displayCentroidFxn(s:Shape):void
{	
	if(s.centroid == null) return;
	
	if(_displayContainer.contains(_visCentroid)) _displayContainer.removeChild(_visCentroid);
	
	_visCentroid = new Sprite();
	
	_visCentroid.graphics.beginFill( 0x121212);
	_visCentroid.graphics.drawCircle(s.centroid.x, s.centroid.y, 4);
	_visCentroid.graphics.endFill();
	
	_displayContainer.addChild(_visCentroid);
}

private function drawMeshEdges(s:Shape):String
{
	var e:MeshEdge = s.meshEdge;
	
	if (e == null) return "";
	
	while(_visEdgesContainer.numChildren > 0) {
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

//function to highlight the boundaries of the Shape that the mouse is over
private function highlightThisShape(s:Shape):void
{
	var lineWeight:int = (s.stronglyClustered == false ? 1 : 3);
	
	while(_visShapeContainer.numChildren > 0) {
		_visShapeContainer.removeChildAt(0);
	} 		
	for each (var p:Point in s.gridIndex)
	{										
		var visShape:Sprite = new Sprite();
		visShape.graphics.beginFill(0x555500, 0);
		visShape.graphics.lineStyle(lineWeight, 0x448866);
		visShape.graphics.drawRect(p.x * gridSize, p.y * gridSize, gridSize, gridSize);
		visShape.graphics.endFill();
		
		_visShapeContainer.addChild(visShape);
	}	
	_displayContainer.addChild(_visShapeContainer);
}