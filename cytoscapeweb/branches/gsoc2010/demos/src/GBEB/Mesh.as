package GBEB
{
	import flare.vis.data.Data;
	import flare.vis.data.DataSprite;
	import flare.vis.data.EdgeSprite;
	import flare.vis.data.NodeSprite;
	
	import flash.display.Sprite;
	import flash.geom.Point;
	import flash.geom.Rectangle;

	////////////////////////////////////////////////////////////////
	// Helper class to generate and manipulate mesh needed for GBEB 
	// Model
	////////////////////////////////////////////////////////////////
	public class Mesh extends Sprite
	{		
		public var grid:Array; 			  //stores the array of Shapes
		public var gridSize:int = 20; // size of initial bounding grid, in pixel
		public var _mesh:Data = new Data(); //stores the actual edges and nodes of the points for the mesh
		public var nonRedundantShapeIndexArray:Array = new Array(); // Stores Shape that actually contain meshEdges and have a general direction
		private var _data:Data;	
		private var _meshEdgeArray:Array; //Stores the array of meshEdges for processing
		private var _bounds:Rectangle = new Rectangle(); // stores the bound of the visualisation
		private var _originalGrid:Array; //stores the array of Shapes at maximum resolution
		
		
		private var angleResolution:int = 15; //stores the angle resolution needed to resolve grid joining. 
		private var edgeCounter:int = 1;
		
		private const _meshNodesMinDistance:Number = 2.0; //Defines the minimum distnance between any 2 nodes in the mesh
		
		public var numGridsX:int = -1;
		public var numGridsY:int = -1;
		
		//for checking purposes
		private var areaHits:int = 0;
		private const numEdgesFromData:int = 47;
		private var cycles:int = 0;
		private var breakpointCounter:int = 0;
		
		public function Mesh (data:Data):void
		{
			_data = data;
			grid = new Array();
		}
		
		public function cleanup():void
		{
			grid = null;
			nonRedundantShapeIndexArray = null;
			
			cleanupMeshEdges();
			
			_data = null;
			_mesh = null;
			_meshEdgeArray = null;
			_bounds = null;
			_originalGrid = null;
			
		}
		
				private function cleanupMeshEdges():void
				{
					for each (var edge:EdgeSprite in _data.edges)
					{
						var props:* = edge.props.GBEBProperties;
						if(props != null)	props = null;
					}
				}
		
		
		// Follows the step below to generate a Mesh
		// 0. Assign GBEBProperty to all edges
		// 1. Generate a uniform grid of 20pix per grid for the entire bound
		// 2. For each grid, detect what edges are inside and store them in shape | There is no need to
		// store nodes at all. 
		// 2b. Calculate the primary direction (polar angle) of each shape. Note: Instead of using 
		// kernel density estimator, I would be using a more empirical method
		// 3. For each shape
		// 3, Merge shape w/ neighbour if resulting primarily difference of this.shape.primaryAngle
		//and neighbour.shape.primaryAngle is < 15 
		//How to detect neighbour? - Sharing rectangular coordinates
		// Repeat until 3a and 3b are exhausted for all Shapes
		// 4. Obtain _meshEdges (1 for each Shape), and their respective nodes. 
		//4b. Merge nodes that are too close together. ( < x pix )
		//4c. Add additional nodes if necessary. (?depends on check by eqn smooth eqn) - necessary?
		// 5. For each edge in _data, check for their intersection with mesh's edge and record these 
		// intersection points as CPand their CP - this is actually quite a challenge. 
		//warning: potential problems, nodes lying on edges.
		public function generateMesh(resolution:int, bounds:Rectangle):void //to return Data
		{
			_bounds = bounds;
			
			generateGrid();
			
			assignDataToGrid();
			
			//changeresolution();
			
			_data.edges.visit(assignProps);
			
			mergeShapeUsingPrimaryDirections();
			
			generateMeshEdges();
			
			addControlPointsToAll();
			//_grid = mergeShape(angleResolution); 
			
			
			//testing grounds for functions
			//_data.edges.visit(updateEdges);
			//_data.edges.visit(checkEdges, checkIfAdam);
			//trace("Mesh: Testing testIntersection: " + testIntersection(new Rectangle(-1,-1,5,5), new Point(2,2), new Point(10,5))); //testing the intersection of glancing contact
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		//Step 0: function to assign GBEB Properties to each mesh Edge that is used for controling them
		/////////////////////////////////////////////////////////////////////////////////////////////
		public function assignProps(e:EdgeSprite):void
		{
			var prop:GBEBProperty = e.props.GBEBProperty;		
			prop = new GBEBProperty();
			e.props.GBEBProperty = prop;
		}
		
		//////////////////////////////////////////////
		//Step 1: function to generate the grid from scratch	
		//////////////////////////////////////////////
		
		public function generateGrid():void
		{
			if(_bounds == new Rectangle() ) return void;
			
			var _x:int = _bounds.x, _y:int = _bounds.y;
			var _height:int = _bounds.height, _width:int = _bounds.width;
			numGridsX = Math.floor(_bounds.width /gridSize);
			numGridsY = Math.floor(_bounds.height /gridSize);
			var _counterX:int = 0, _counterY:int = 0, numGrids:int = 0;
			
			var _newGrid:Array = new Array();
			
			trace("Mesh: numGridsX: " + numGridsX + "  | numGridsY: " + numGridsY);
			
		
			while( _counterY <= numGridsY ) //-22
			{
				while( _counterX <= numGridsX )// -20
				{
					
					var _shape:Shape = new Shape();
					var __grid:Rectangle = new Rectangle();
					var point:Point = new Point(_counterX, _counterY);
					
					__grid.x = _x + _counterX * gridSize;
					__grid.y = _y + _counterY * gridSize;
					
					__grid.width = ( _counterX != numGridsX ? gridSize : (_bounds.width - _counterX * gridSize  ) );
					__grid.height = ( _counterY != numGridsY ? gridSize : (_bounds.height - _counterY * gridSize ) );
					
					
					_shape.gridIndex.push(point);
					_shape.storedGrids.push(__grid);
					_newGrid.push(_shape);
					
					//trace("Mesh: Grid created. _counterY = " + _counterY + " | _counterX = " + _counterX + " |numGrids: " + ++numGrids);
					
					_counterX++;
				}
				_counterX = 0; 
				_counterY++;
				
			} 
			
			
			grid = _newGrid;

			return void;
		}
		
		
		//////////////////////////////////////////////
		///Step 2.0: this group of functions are used to handle the assignment of data to shapes)
		//! Assigns of coordinates of start and end (flare reference x1, y1, x2, y2)for each EdgeSprite.
		//! must be called when display is change in order to get the updated location of the edges. 
		// There is no need to maniplulate nodes. 
		//////////////////////////////////////////////
		public function assignDataToGrid(d:Data = null):void
		{
			if(d != null) _data = d;
							
			if(grid.length == 0) return;
								
			//data.edges.sortBy({"x", "y"});
						
			// this is a quadraply nested loop of O(gridsXedges) runtime
			// It tests if there is an intersection between the grids on the edges. If so, the edges are added into the shape. 
			for each (var edge:EdgeSprite in _data.edges)
			{
				//included just in case (x1,y1), (x2, y2) have not been assigned
				edge.x1 = edge.source.x;
				edge.x2 = edge.target.x;
				edge.y1 = edge.source.y;
				edge.y2 = edge.target.y;
				
				trace((edge.source.data["name"]), " (" + edge.x1 + "," +  edge.y1 + ") ", " | " 
					+ edge.target.data["name"] + " ( " + edge.x2 + "," + edge.y2 + ")"); 

				for each(var _shape:Shape in grid) 
				{
					
					for each (var __grid:Rectangle in _shape.storedGrids)
					{
						
						//new trial function 
						if(testIntersection(__grid, new Point(edge.x1, edge.y1), new Point(edge.x2, edge.y2)))
						{
							_shape.storedDataEdges.push(edge);
						}
					}
					
					//trace("Mesh: Shapes: Checking _grids: " + _shape.gridIndex[0] + " has " + _shape.storedDataEdges.length + " edges" );
				
					/*if(_shape.storedDataEdges.length > 0)
					{
						trace("Mesh: " + _shape.gridIndex + " Edge Decteced " + _shape.storedDataEdges.length, _shape.storedGrids[0].x, _shape.storedGrids[0].y);
					} */
					
					//trace("Mesh: " + _shape.gridIndex );
					
				}
				
			}// end of huge for loop
						
			for each( var _shape:Shape in grid)
			{
				_shape.computeDirection();
			}
						
			// Do I need to pop in the nodes? Apparently, it seems to serve no function as of now. 
		
			
		} 
		
					
					///////////////////////////////////////////////////////
					// Step 2.1: Function to compute intersection of any line (represented by 2 endpoints of the line) and a grid. Returns true if there is any intersection. 
					//	More: Makes use of the the fact that for the intersection of a line with vertical or horizontal lines of a grid, 
					//  the x/y of the ver/hor lines are fixed, thus there is no need to solve simultaneous eqn. Uses basic geometric algebra
					// saves computational time as not all intersections have to be computed.
					////////////////////////////////////////////////////////
					private function testIntersection(rec:Rectangle, p1:Point, p2:Point):Boolean
					{
						var intersects:Boolean = false;
						
						//trace("Mesh: testIntersection: Rec :", rec.topLeft, rec.bottomRight, " | Points ",p1, p2);
						
						if(intersectsVertical(rec.topLeft, bottomLeft(rec), p1, p2) != null) return true; //checks against left vertical
						if(intersectsVertical(topRight(rec), rec.bottomRight, p1, p2) != null) return true; //checks against right vertical
						if(intersectsHorizontal(rec.topLeft, topRight(rec), p1, p2)!= null) return true; //checks against top horizontal
						if(intersectsHorizontal(bottomLeft(rec), rec.bottomRight, p1, p2)!= null) return true; //checks against bottom horizontal
						
						//are there special cases in which there is glancing contact?
						
						return false;
					}
					
					//Notes: These intersection functions only check for eqaulity in the vertical intersection (meaning checking for corners). Hence, it prevents any multiple counting
					private function intersectsVertical(vp1:Point, vp2:Point, p1:Point, p2:Point):Point
					{
						var _x:Number = vp1.x;
						if( (_x >= p1.x && _x <= p2.x) || (_x >= p2.x && _x <= p1.x)) //checks if the x-coor is within the interval of the line
						{
							var _y:Number = (( (p2.y - p1.y) / (p2.x - p1.x)	) * ( _x - p1.x)) + p1.y;	 
							if( (_y >= vp1.y && _y <= vp2.y) || (_y >= vp2.y && _y <= vp1.y)) //checks if the calculated y-coor is within the interval of the vertical line	
							{	//trace("Mesh: IntersectsVertical: " + p1, p2 + " intersects with " + vp1 + " at " + new Point(_x, _y));
								return new Point(_x, _y);
							}
						}	
						return null;
					}
					private function intersectsHorizontal(vp1:Point, vp2:Point, p1:Point, p2:Point):Point
					{
						var _y:Number = vp1.y; 
						if( (_y >= p1.y && _y <= p2.y) || (_y >= p2.y && _y <= p1.y)) //checks if the y-coor is within the interval of the line
						{						
							var _x:Number = (( (p2.x - p1.x) / (p2.y - p1.y)	) * ( _y - p1.y)) + p1.x; 
							
							if( (_x > vp1.x && _x < vp2.x) || (_x > vp2.x && _x < vp1.x)) //checks if the calculated x-coor is within the interval of the horizontal line	
							{	//trace("Mesh: IntersectsHorizontal: " + p1, p2 + " intersects with " + vp1 + " at " + new Point(_x, _y));
								return new Point(_x, _y);
							}
						}	
						return null;
					}
					
					//I have to do this because flash.geom.rec does not have a refernce readily available;
					private function bottomLeft(rec:Rectangle):Point
					{
						return new Point(rec.left, rec.top + rec.height);
					}
					private function topRight(rec:Rectangle):Point
					{
						return new Point(rec.left + rec.width, rec.top);
					}
					
					
					
		//////////////////////////////////////////////
		/// Step 3a: function merge shape if they are neighouring ( both shapes have at least 1
		///  common vertical or horizontal edge) and if their angle difference is 
		/// less than angleResolution
		/// I should probably write this under Shape
		//////////////////////////////////////////////	
		
		public function mergeShapeUsingPrimaryDirections():Array
		{
			
			var repeat:Boolean = true; //boolean indication if the program should run through all the grids again
			
			var iterationIndexArray:Array = new Array(); //this array stores the index of the shapes that 
			//needs to be iterated through by the merged Shapes function
			
			for each (var s1:Shape in grid)
			{
				if(s1.direction != -1)
				{
					iterationIndexArray.push(s1.gridIndex[0]); //pushing the first index automatically refers to the whole shape
					//trace("Mesh: mergeShapeUsingPrimaryDirections: Grid " + (s1.gridIndex[0] as Point).toString() + " is stored");
				}
			}
			
			trace("Mesh: mergeShapeUsingPrimaryDirections is running...Iterative Index: " + iterationIndexArray.length);
			
			while(iterationIndexArray[0] != null) //while there might be shapes that needs to be merged
			{		
				//trace("Mesh: mergeShapeUsingPrimaryDirections: iterationArray.length(Before): " + iterationIndexArray.length);
				var s1:Shape = returnShapeFromIndex(iterationIndexArray.shift()); //assigns s1 to the shape referenced by the point at the beginning of the array
				//continue if shape has no major direction 
				//trace("Mesh: mergeShapeUsingPrimaryDirections: iterationArray.length: " + iterationIndexArray.length);
				
				//trace("Mesh: mergeShapeUsingPrimaryDirections: Getting neigbours of " + s1.gridIndex[0]);
				
				var neigbourShapes:Array = new Array();
				var neigboursIndex:Array = getNeighboursIndex(s1);
				var hasMerged:Boolean = false; //Stores whether the shape has merged with its immedidate neighbours
				
		
				/*for each (var p:Point in neigboursIndex)
				{
					trace("Mesh: Neighours = " + p.toString());
					
				} */	
				//trace("Mesh: mergeShapeUsingPrimaryDirections next...");
				//trace("Mesh: mergeShapeUsingPrimaryDirections " + neigboursIndex.length);
				
				while(neigboursIndex.length != 0)
				{
					neigbourShapes.push(returnShapeFromIndex(neigboursIndex.pop()));
				}
				
				if(s1.direction == -1) continue;
				
				for each (var s2:Shape in neigbourShapes)
				{
					//if(s2 === s1) continue; //get neighbours could mess up?
					
					if(s2 == null) continue;
					
					if(s2.direction == -1) continue;
					
					//check if neighbouring shape also shares the same direction. If so the shapes can be merged. 
					if (Math.abs(s1.direction - s2.direction) <= angleResolution)
					{
						mergeShape(s1, s2);
						hasMerged = true;
					}

				}
				
				//trace("Mesh: " + cycles++);
				
				if(hasMerged) iterationIndexArray.push(s1.gridIndex[0]); //adds the shape back to the Iteration array since it is changed.
				
				//trace("Mesh: mergeShapeUsingPrimaryDirections: iterationArray.length (after) : " + iterationIndexArray.length);
				
				//break clause
				//if (breakpointCounter++ >= 300) return null;
				//if(iterationIndexArray.length > 65) return null;
				
				//need to settle flagging problem to optimise runtime. (=
			}
		
		
			return null;
		} 
		
		
				//////////////////////////////////////////////
				//Step 3a.1 function to get the neighbours of a particular Shape given its index
				//////////////////////////////////////////////
				private function getNeighboursIndex(shape:Shape):Array
				{
					var neigboursIndexArray:Array = new Array();
					
					for each (var p:Point in shape.gridIndex)
					{
						if( (p.x - 1) >= 0 && returnShapeFromIndex(new Point (p.x-1, p.y)) !== shape) 
							neigboursIndexArray.push(new Point(p.x-1, p.y));
						
						if((p.y - 1) >= 0 && returnShapeFromIndex(new Point (p.x, p.y - 1)) !== shape) 
								neigboursIndexArray.push(new Point(p.x, p.y-1));
						
						if((p.x + 1) <= numGridsX && returnShapeFromIndex(new Point (p.x+1, p.y)) !== shape) 
								neigboursIndexArray.push(new Point(p.x+1, p.y));
						
						if((p.y + 1) <= numGridsY && returnShapeFromIndex(new Point (p.x, p.y +1)) !== shape) 
								neigboursIndexArray.push(new Point(p.x, p.y+1));
					}
					
					/*for each (var p:Point in neigboursIndexArray)
					{
						trace("Mesh: Neighours = " + p.toString());
						
					} */
					
					return neigboursIndexArray;
				}
				
				//////////////////////////////////////////////
				//Step 3b When the shapes are merged together, only 1 shape will remain. The grids in the mesh will all reference
				//to the remaining shape
				//////////////////////////////////////////////
				private function mergeShape(s1:Shape, s2:Shape):Shape //s2 will destroyed
				{
				//	trace("Mesh: MergeShape: is called!");
					
					var e2:EdgeSprite;
					
					if(s1 == null && s2 == null) return null;
					if(s2 == null) return s1;
					if(s1 == null) return s2;
					
					
					while(s2.storedDataEdges.length != 0)
					{
						//transfers the edges from s2 to s1
						e2 = s2.storedDataEdges.pop();
						
						if(s1.storedDataEdges.indexOf(e2) == -1) s1.storedDataEdges.push(e2);
					}
					
					//transfer storedGrid Index
					
					s1.computeDirection(); //Warning the resulting shape might not be strongly clustered!!!!!
					
					
					for each (var p:Point in s2.gridIndex)
					{
						grid[indexFromPoint(p)] = s1;
						s1.gridIndex.push(p);
						//trace("Mesh: MergeShape: Point "+ p.toString() + " is added to " + s1.gridIndex[0]);
					}
					/*
					trace("Mesh: MergeShape: Now " + s1.gridIndex[0] + " contains: ");
					for each (var p:Point in s1.gridIndex)
					{
						trace(p.toString());
						
					} */
					
					//trace("Mesh: MergeShape: is called!");
					
					return s1;
				}
				
				
	//////////////////////////////////////////////
	// Step 4a. Obtain _meshEdges (1 for each Shape), and their respective nodes. 
	// The direction variable in each shape with a majority direction gives the gradient of the mesh edge passing through the particular shape.
	// The Mesh edge needs an intersection point within the shape and this intersection point is precisely the 
	// centroid of the shape. 
	//////////////////////////////////////////////
				
	public function generateMeshEdges():void
	{
		if(grid == null) return;
		
		var gradient:Number = 1;
		var meshEdge:EdgeSprite = new EdgeSprite();
		var currentGrid:Point = new Point(-1,-1); //stores the index of the grid that is currently being tested for intersection 
		
		_meshEdgeArray = new Array();
		
		trace("Mesh: Generate meshEdges: running!");
		
		generateNonRedundantShapeIndexArray();
		
		for each (var p1:Point in nonRedundantShapeIndexArray)
		{
			var s1:Shape = returnShapeFromIndex(p1);
			var angleOfLine:Number = 0;
			
			if(s1.direction == -1) continue;
			//only shapes with a main direction will continue until here. 
			
			//trace("Mesh: GenerateMeshEdges: Gradient Calculation: for " + s1.direction + " gradient = " + Math.tan((s1.direction / 180) * Math.PI));
			gradient = -1 / Math.tan((s1.direction / 180) * Math.PI);	
			
			//angleOfLine = Math.atan(gradient) * 180 / Math.PI;
			//trace("Mesh: Testing Gradient: Edge " + cycles + " | direction " + s1.direction + " | Gradient:" + gradient + " |Angle: " + angleOfLine);
			
			s1.centroid = findCentroid(s1);
			
			//trace("Mesh: Generate meshEdges: " + returnIndexFromXY(s1.centroid.x, s1.centroid.y).toString() + " | " + s1.centroid.toString());
			meshEdge = generateLineFromPointAndGradient(s1.centroid, gradient);
			
			currentGrid = returnIndexFromXY(s1.centroid.x, s1.centroid.y);
			
			if( currentGrid.x < 0 || currentGrid.y < 0 || currentGrid.x > numGridsX || currentGrid.y > numGridsY ) continue; 
			
			meshEdge.source = new NodeSprite(); meshEdge.source.x = meshEdge.x1; meshEdge.source.y = meshEdge.y1;
			
			meshEdge.target = new NodeSprite(); meshEdge.target.x = meshEdge.x2; meshEdge.target.y = meshEdge.y2;
			
			meshEdge.target.name = meshEdge.source.name = (cycles).toString();
			meshEdge.name = (cycles++).toString();
			
			getNodesForMeshEdge(returnIndexFromXY(s1.centroid.x, s1.centroid.y), meshEdge, meshEdge.source, "None");
			
			
			//trace("Mesh: GenerateMeshEdges: Assigning names: " + meshEdge.x1);
			
			s1.meshEdge = meshEdge;
			
			if(meshEdge != null)
			{
				_meshEdgeArray.push(meshEdge);
				_mesh.addNode(meshEdge.source);
				_mesh.addNode(meshEdge.target);
				_mesh.addEdge(meshEdge);
				
				//_mesh.edges.add(meshEdge); //adding the mesh edge to datalist; technically, I can use the _mesh.edge.visit function to runs
				//mergeNodes_All(), but it might be quite messy as I have to visit the list of edges in the fashion of bubble sort. 
			}
		}
		//trace("Mesh: GenerateMeshEdges: " + _mesh.edges);
		trace("Mesh: GenerateMeshEdes: No. of meshEdges generated = " + _meshEdgeArray.length + " | Nodes " + _mesh.nodes.length + " | Edges" + _mesh.edges.length);
		 
		
		
		//mergeNodes_All(); //huge sub-routine to merge all neigbouring nodes. 
		
		
		return;
	}
	
				//This will cause the edge to shift. 
				//Step 4a.1 :: Recurrsive function. Takes in the grid index and an edge sprite to return an edge that is assigned with nodes
				//the nodes are the found at the boundary of each Shape. It uses the last prevDir variable to pass on the information on which direction the 
				//edge came from, in order not to get a double hit. (For example, if the edge passes through the top of the prev grid, it would definitely pass
				//through the bottom of the top adjoining grid, but that is not the next intersection that we are interested to find). This function also handles
				//the special case where the gradient is 0 or infinity. 
				private function getNodesForMeshEdge(currentGridIndex:Point, edge:EdgeSprite, node:NodeSprite, prevDir:String):NodeSprite
				{
					//trace("Mesh: getNodesForMeshEdge is running! EdgeNo :" + edge.name + " | CurrentGridIndex: " + currentGridIndex, "| source", edge.x1,
					//	edge.y1, "| target", edge.x2, edge.y2);
					
					var currentGridXY:Point = returnXYFromIndex(currentGridIndex);
					var currentShape:Shape = returnShapeFromIndex(currentGridIndex);
					var newGrid:Point = new Point(0,0); 
					var grid:Rectangle = new Rectangle(currentGridXY.x, currentGridXY.y, gridSize, gridSize);
		
					var intersectionPoint:Point; 
		
					var p1:Point = new Point(edge.x1, edge.y1); //stores the x,y coordinates of the source of edgeNode temporarily.
					var p2:Point = new Point(edge.x2, edge.y2); //stores the x,y coordinates of the target of edgeNode temporarily.
		
					intersectionPoint = intersectsHorizontal(grid.topLeft, topRight(grid), p1, p2); //check if the edge intersects with the top
					
					if(intersectionPoint != null && prevDir != "Bottom" )
					{					
						newGrid.x = currentGridIndex.x; newGrid.y = currentGridIndex.y - 1;
						node.x = intersectionPoint.x, node.y = intersectionPoint.y;
						if(searchShapeForIndex(currentShape, newGrid))
						{
							node = getNodesForMeshEdge(newGrid, edge, node, "Top");
						} else if (prevDir != "None") {
							return node;
						} 
					} 
		
				
					
					//I am overloading this huge recurrsive to handle both the source and target nodes together. This function basically ask if
					//this is the first time the function is called for an edge. If it is, and that if edge.source has been moved to an intersection point
					//it will continue to work with the edge.target as node. 
					//Notes: It doesnt matter that the variable node is coninuously assigned to edge.target.
					if(prevDir == "None" && isSourceNodeAssigned(edge))  
					{
						node = edge.target;
					}
		
					//check if the edge intersects with the bottomEdge
					intersectionPoint = intersectsHorizontal(bottomLeft(grid), grid.bottomRight, p1, p2); 
					
					//trace("Mesh: getNodesForMeshEdge: IntersectionPoint: " + intersectionPoint.toString());
		
					if(intersectionPoint != null && prevDir != "Top" )
					{
						newGrid.x = currentGridIndex.x; newGrid.y = currentGridIndex.y + 1;
						node.x = intersectionPoint.x, node.y = intersectionPoint.y;
						if(searchShapeForIndex(currentShape, newGrid))
						{
							node = getNodesForMeshEdge(newGrid, edge, node,"Bottom");
						} else if (prevDir != "None") {
							return node;
						} 
					} 
		
		
			
					if(prevDir == "None" && isSourceNodeAssigned(edge))  
					{
						node = edge.target;
					}
		
					//check if the edge intersects with the leftEdge
					intersectionPoint = intersectsVertical(grid.topLeft, bottomLeft(grid), p1, p2);

					
					if(intersectionPoint != null && prevDir != "Right" )
					{
						newGrid.x = currentGridIndex.x - 1; newGrid.y = currentGridIndex.y;
						node.x = intersectionPoint.x, node.y = intersectionPoint.y;
						if(searchShapeForIndex(currentShape, newGrid))
						{	
							node = getNodesForMeshEdge(newGrid, edge, node, "Left");			
						} else if (prevDir != "None") {
							return node;
						} 
					} 
		
					//I have included the whole statement for consistency. Technically, if can do without the right portion after "&&")
					//as each grid is guranteed to have 2 intersections points, so by the 4th assignment must belong to the edge. 
					if(prevDir == "None" && isSourceNodeAssigned(edge))  
					{
						node = edge.target;
					}
		
					//check if the edge intersects with the rightEdge
					intersectionPoint = intersectsVertical(topRight(grid), grid.bottomRight, p1, p2);

					if(intersectionPoint != null && prevDir != "Left" )
					{
						newGrid.x = currentGridIndex.x + 1; newGrid.y = currentGridIndex.y;
						node.x = intersectionPoint.x, node.y = intersectionPoint.y;
						if(searchShapeForIndex(currentShape, newGrid))
						{	
							node = getNodesForMeshEdge(newGrid, edge, node, "Right");
						} else if (prevDir != "None") {
							return node;
						} 
					} 
		
						//trace("Mesh: getNodesForMeshEdge: " +  centroid.toString() + "(It is only suppose to fall through once per edge!)"); //since each centroid is unique
						return node;
					}
	
				
					//Step 4a.1	:: Support. This function is basically asking if the previous
					// the intersections for the "source Node" has been found. 
					//Worry about flash arithmetic? (what if the change is very small? what if "==" does not
					private function isSourceNodeAssigned(edge:EdgeSprite):Boolean
					{					
						if( Math.abs(edge.source.x - edge.x1) < 0.5 && Math.abs(edge.source.y - edge.y1) < 0.5)
						{
							return false;
						}
					
						return true;
					}
	
	
					//Step 4a.2 ::This function is a clean up function which generates an array of nonRedundant index of Unique shapes that have
					//a major direction which helps in further processesing of these edges
					private function generateNonRedundantShapeIndexArray():Array
					{
						nonRedundantShapeIndexArray = new Array();
						
						for each (var s1:Shape in grid)
						{
							if(s1.direction != -1 && nonRedundantShapeIndexArray.indexOf(s1.gridIndex[0]) == -1)
							{
									nonRedundantShapeIndexArray.push(s1.gridIndex[0]);
							}
						}

						return nonRedundantShapeIndexArray;
					}
	
					//Step 4a.3 ::this function is usually very complication if the Shapes are irregular. However, since I have 
					//define the primitive shape as a square a very special case of shapes, this function becomes 
					//quite simple. (=
					private function findCentroid(s:Shape):Point
					{
						if(s == null) return null;
						
						var numPoints:int = 0; 
						var xCoor:Number = 0;
						var yCoor:Number = 0;
						var xyCoor:Point = new Point(0,0);
						
						for each (var p:Point in s.gridIndex)
						{
							xyCoor = returnXYFromIndex(p);
							
							xCoor += (xyCoor.x + 0.5 * gridSize);
							yCoor += (xyCoor.y + 0.5 * gridSize);
							
							numPoints++;
						}
						
						if(numPoints == 0) return null; 
						
						return new Point( (xCoor / numPoints), ( yCoor / numPoints) );
						
						//check if centroid lies within the Area of the shape?
						//yes, I have have to. Should I assign the closet grid?
						
						//return returnXYFromIndex( new Point( (xIndex / numPoints), (yIndex / numPoints) ));
					}
					
					//Step 4a.4 This function takes in the point in which the line passes through, and the gradient of the lines
					//and return a straight EdgeSprite that is defined by the the bottom left (source coordinates) to the top-right (target corrdinates),
					//where the source and target are the intersection of the line with the boundies of the graph
					private function generateLineFromPointAndGradient(p:Point, gradient:Number): EdgeSprite
					{
						var pointsArray:Array = new Array(); //temp storage for the target and source
						var edgeSprite:EdgeSprite = new EdgeSprite();
						var isSourceAssigned:Boolean = false;
						
						//trace("Mesh: generateLineFromPoint: Checking for gradient :" + gradient + " at " + p.toString());
						
						//boundary conditions
						//if (gradient == Number.POSITIVE_INFINITY || gradient == Number.NEGATIVE_INFINITY)
						if( gradient > 500 || gradient < -500)
						{
							if(Math.abs(p.x - _bounds.x) <= 0.01)
							{
								pointsArray.push(new Point(_bounds.x, _bounds.y));
								pointsArray.push(new Point((_bounds.width), (_bounds.height)));
							} else {
								pointsArray.push(new Point(p.x, _bounds.y));
								pointsArray.push(new Point(p.x, _bounds.height));
							}
							
							//trace("Mesh: generateLineFromPoint: Checking for gradient = infinity");
						} else if ( gradient > -0.01 && gradient < 0.01)
						{
							//if gradient is nearing 0 
							if(Math.abs(p.y - _bounds.y) <= 0.01)
							{
								pointsArray.push(new Point(_bounds.x, _bounds.y));
								pointsArray.push(new Point((_bounds.width), _bounds.y));
							} else {
								pointsArray.push(new Point(_bounds.x, p.y));
								pointsArray.push(new Point(_bounds.width, p.y));
							}
							
							//trace("Mesh: generateLineFromPoint: Checking for gradient = 0");
						} else {
							
							//gradient = -1 /gradient;
							
							pointsArray.push(intersectionWithVertical(_bounds.x, p.x, p.y, gradient)); //with left boundary
							pointsArray.push(intersectionWithVertical(_bounds.width + _bounds.x, p.x, p.y,  gradient)); //with right boundary
							pointsArray.push(intersectionWithHorizontal(_bounds.y, p.y, p.x, gradient)); //with top boundary
							pointsArray.push(intersectionWithHorizontal(_bounds.height + _bounds.y, p.y, p.x, gradient)); //with bottom boundary
							
							//trace("Mesh: generateLineFromPoint: Checking for normal gradients...");
						}
						
						for each ( var p:Point in pointsArray)
						{
							if( p != null)
							{	
								if(!isSourceAssigned)
								{	
									edgeSprite.x1 = p.x;
									edgeSprite.y1 = p.y;
									isSourceAssigned = true;
								} else {
									edgeSprite.x2 = p.x;
									edgeSprite.y2 = p.y;
								}
								
								//trace("Mesh: generateLineFromPoint: pointsArray can be assigned " + edgeSprite.x1, edgeSprite.y1, " | ", edgeSprite.x2, edgeSprite.y2 );
								
								//for tracing
								//var angle:Number = (edgeSprite.y2 - edgeSprite.y1) / (edgeSprite.x2 - edgeSprite.x1);
								//angle = Math.atan(angle) / Math.PI * 180;
								//trace("Mesh: generateLineFromPoint:" + cycles + " | angle " + angle);
							}else {
								//trace("Mesh: MeshEdge Generator: PointsArray.length: " + pointsArray.length + " | NULL!!!!!");
							}
						}
						//trace("Mesh: generateLineFromPoint: " + edgeSprite.x1 + "!!!!!");
						
						return edgeSprite;
					}
							
								//Step 4a.4.1 This function checks if the edge generated intersects with the vertical boundary of the _mesh/_bounds boundary. If it does,
								// it returns the intersection point. Vice versa for 4a.4.2, except that it checks against the horizontal boundary. 
								//Does it handle the case where gradient = 0 or infinity?
								private function intersectionWithVertical(xBoundary:Number, x1:Number, y1:Number, gradient:Number):Point
								{
									var yCoor:Number = (gradient * ( xBoundary - x1)) + y1; //stores the y coordinate of the intersection point with the vertical boundary
									
									var diff:Number = yCoor - y1;//Patch to reflect the gradient about the y -axis; necessary due to unknown bug
									yCoor -= 2 * diff;//Patch to reflect the gradient about the y -axis; necessary due to unknown bug
									
									if( yCoor <= _bounds.y + _bounds.height && yCoor >= _bounds.y)  //if intersects directly at the corner, the vertial function will return the points
									{
										return new Point(xBoundary, yCoor);
									}		
									return null;
								}
								
								//Step 4a.4.2
								private function intersectionWithHorizontal(yBoundary:Number, y1:Number, x1:Number, gradient:Number):Point
								{				
									gradient = 1/gradient;
									
									var xCoor:Number = (gradient  * (yBoundary - y1)) + x1;
									
									var diff:Number = xCoor - x1;//Patch to reflect the gradient about the y -axis; necessary due to unknown bug
									xCoor -= 2 * diff;//Patch to reflect the gradient about the y -axis; necessary due to unknown bug
									
									if( xCoor < _bounds.x + _bounds.width && xCoor > _bounds.x)  //if intersects directly at the corner, the vertial function will return the points
									{
										return new Point(xCoor, yBoundary);
									}						
									return null;
								}
								
								
				//Step 4b. Merge nodes that are too close together. ( < x pix )
				//Since the nodes itself doesnt not contain any reference to the edges, edges are input instead,
				//so he edge's parameteres can be altered. It does a pairwise comparison with all nodes on the mesh
				public function mergeNodes_All():void
				{
					var currEdge:EdgeSprite;
					var currNode:NodeSprite;
									
					trace("Mesh: MergeNodes is running.");	
				
					while(_meshEdgeArray.length != 0)
					{
						currEdge = _meshEdgeArray.pop();
						currNode = currEdge.source;
										
						for each (var edge2:EdgeSprite in _meshEdgeArray)
						{
							if(calculateDistanceBetweenNodes(currNode, edge2.source) < _meshNodesMinDistance)
							{
								mergeNodes_Pairwise(currEdge, "source", edge2, "source");
								continue;
							} else if  (calculateDistanceBetweenNodes(currNode, edge2.target) < _meshNodesMinDistance)
							{
								mergeNodes_Pairwise(currEdge, "source", edge2, "target");
								continue;
							}
											
							currNode = currEdge.target;
											
							if(calculateDistanceBetweenNodes(currNode, edge2.source) < _meshNodesMinDistance)
							{	
								mergeNodes_Pairwise(currEdge, "target", edge2, "source");
								continue;
							} else if  (calculateDistanceBetweenNodes(currNode, edge2.target) < _meshNodesMinDistance)
							{
								mergeNodes_Pairwise(currEdge, "target", edge2, "target");
								continue;
							}
						}
					}
									
						}
						
								//Step 4b.1 This function checks if 2 nodeSprites are too close together. The minimum distance is
								//defined by the const _meshNodesMinDistance
								private function calculateDistanceBetweenNodes(node1:NodeSprite, node2:NodeSprite):Number
								{
									var distance:Number = 0;
							
									//this is Pythogoras' Theorem
									distance = Math.sqrt( Math.pow((node1.x - node2.x), 2) + Math.pow((node1.y - node2.y), 2));
									if (distance < _meshNodesMinDistance) trace("Mesh: CalculateDistanceBetweenNodes: " + distance,  "|", 
										node1.x, node1.y, "||", node2.x, node2.y);
								
										return distance;
								}
							
								//Step 4b.2 this function creates a new node in the intersection between the extension of the 2 edges
								private function mergeNodes_Pairwise(edge1:EdgeSprite, s1:String, edge2:EdgeSprite, s2:String):void
								{
									if(edge1 == null || s1 == null || edge2 == null || s2 == null) return;
							
									var a:Point = new Point(edge1.x1, edge1.y1);
									var b:Point = new Point(edge1.x2, edge1.y2);
									var e:Point = new Point(edge2.x1, edge2.y1);
									var f:Point = new Point(edge2.x2, edge2.y2);
							
									var ip:Point =  GBEB.GeometryUtil.lineIntersectLine( a, b, e, f); //stores the intersectionPoint
							
									if( ip == null ) return;
									
									trace("Mesh: Clustering Neigbouring Nodes: A new intersectionPoint has been created at " + ip.toString() 
										+ "\nfrom points (" + edge1[s1].x + "," + edge1[s1].y + ") " + s1 + " of Edge: " + edge1.name + " and (" 
										+ edge2[s2].x + "," + edge2[s2].y + ") " + s2 + " of Edge: " + edge2.name );
							
									//below checks for the particular source/target node of edge2, which will be moved to the intersection Point
									//then it assigns the particular source/target node of edge1 to the previously moved node. This results in the removal of
									//nodes in the graph and reduces its node density, by "clustering" nodes
							
									(edge2[s2] as NodeSprite).x = ip.x; (edge2[s2] as NodeSprite).y = ip.y
							
									edge1[s1] = edge2[s2];
							
									return;
								}
					
								
		// Step 5. For each edge in _data, check for their intersection with _mesh.edge and record these 
		// intersection points as CPand their CP - this is actually quite a challenge
		public function addControlPointsToAll():void
		{			
			for each (var p:Point in nonRedundantShapeIndexArray)
			{
				var s:Shape = returnShapeFromIndex(p);
				s.addControlPoint();
			}						
		}			
					
					////////////////////////////////
					// Helper functions
					////////////////////////////////
					//the grid index is unique. 
					
					public function returnShape(x:int, y:int):Shape
					{ //checked
						if(numGridsX == -1 || numGridsY == -1) return null;
						
						var gridX:int = Math.floor(( x - _bounds.x) / gridSize );
						var gridY:int = Math.floor(( y - _bounds.y) / gridSize );
						
						return (grid[gridY * (numGridsX - (- 1) )+ gridX] as Shape); //remove the (10-1) after trials 
					}
					
					public function returnShapeFromIndex(p:Point):Shape
					{
						return (grid[p.y * (numGridsX + 1 )+ p.x] as Shape); //remove the (10-1) after trials
					}
					
					public function indexFromPoint(p:Point):int
					{
						return p.x + p.y * (numGridsX + 1 ); //remove the (10-1) after trials
					}
					
					public function returnIndexFromXY(x:Number, y:Number):Point
					{
						x = Math.floor( (x - _bounds.x) / gridSize);
						y = Math.floor( (y - _bounds.y) / gridSize);
						
						return new Point(x, y);
					}

					//returns the actual (x,y) coordinate from index, returns the top-left point of the grid. 
					//May return any intermediate value if the Point p given is not any integer. 
					public function returnXYFromIndex(p:Point):Point 
					{
						return new Point( (p.x * gridSize) + _bounds.x , (p.y * gridSize) + _bounds.y );
					}
	
					//searches the GridIndex array of a given Shape for an index. Returns true, if the index exist in the Grid Array
					public function searchShapeForIndex(s:Shape, index:Point):Boolean
					{
						if(s == null) return false;
						
						for each(var p:Point in s.gridIndex)
						{
							if(p.x == index.x && p.y == index.y) return true; 
						}
						
						return false;
					}
					
					////////////////////////////////
					// functions that I am testing out
					////////////////////////////////			
					
					private function updateEdges(e:EdgeSprite):void
					{
						e.props.name = new String("Hi Everyone, I am edgeSprite " + edgeCounter++);
					}
					private function checkEdges(e:EdgeSprite):void
					{
						trace(e.props.name as String);
					}
					private function checkIfAdam(e:EdgeSprite):Boolean
					{
						if(e.target.data["name"] == "Adam") return true;
						return false;
					}
		
	}// end of class
}