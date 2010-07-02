package GBEB
{
	import flare.vis.data.Data;
	import flare.vis.data.DataSprite;
	import flare.vis.data.EdgeSprite;
	
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;

	////////////////////////////////////////////////////////////////
	// Helper class to generate and manipulate mesh needed for GBEB 
	// Model
	////////////////////////////////////////////////////////////////
	public class Mesh extends Sprite
	{		
		public var _grid:Array; 			  //stores the array of Shapes
		public var gridSize:int = 20; // size of initial bounding grid, in pixel
		private var _bounds:Rectangle = new Rectangle(); // stores the bound of the visualisation
		private var _data:Data;
		
		
		private var angleResolution:int = 15; //stores the angle resolution needed to resolve grid joining. 
		private var edgeCounter:int = 1;
		
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
			_grid = new Array();
		}
		
		
		// Follows the step below to generate a Mesh
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
			
			mergeShapeUsingPrimaryDirections();
			//_grid = mergeShape(angleResolution); 
			
			
			//testing grounds for functions
			//_data.edges.visit(updateEdges);
			//_data.edges.visit(checkEdges, checkIfAdam);
			//trace("Mesh: Testing testIntersection: " + testIntersection(new Rectangle(-1,-1,5,5), new Point(2,2), new Point(10,5))); //testing the intersection of glancing contact
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
				while( _counterX <= numGridsX - 10)// -20
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
			
			
			_grid = _newGrid;

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
							
			if(_grid.length == 0) return;
								
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

				for each(var _shape:Shape in _grid) 
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
						
			for each( var _shape:Shape in _grid)
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
			
			for each (var s1:Shape in _grid)
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
		
		
			return null; // to be edited. 
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
					
					if(s1 == null && s2 == null) return null;
					if(s2 == null) return s1;
					if(s1 == null) return s2;
					
					
					while(s2.storedDataEdges.length != 0)
					{
						//transfers the edges from s2 to s1
						s1.storedDataEdges.push(s2.storedDataEdges.pop());
					}
					
					//transfer storedGrid Index
					
					s1.computeDirection(); //Warning the resulting shape might not be strongly clustered!!!!!
					
					
					for each (var p:Point in s2.gridIndex)
					{
						_grid[indexFromPoint(p)] = s1;
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
					
					
					
					////////////////////////////////
					// Helper functions
					////////////////////////////////
					//the grid index is unique. 
					
					public function returnShape(x:int, y:int):Shape
					{ //checked
						if(numGridsX == -1 || numGridsY == -1) return null;
						
						var gridX:int = Math.floor(( x - _bounds.x) / gridSize );
						var gridY:int = Math.floor(( y - _bounds.y) / gridSize );
						
						return (_grid[gridY * (numGridsX - ( 10 - 1) )+ gridX] as Shape); //remove the -20 after trials
					}
					
					public function returnShapeFromIndex(p:Point):Shape
					{
						return (_grid[p.y * (numGridsX - 9 )+ p.x] as Shape); //remove the -20 after trials
					}
					
					public function indexFromPoint(p:Point):int
					{
						return p.x + p.y * (numGridsX - 9 );
					}
					
					//returns the actual (x,y) coordinate from index, returns the top-left point of the grid. 
					//May return any intermediate value if the Point p given is not any integer. 
					public function returnXYFromIndex(p:Point):Point 
					{
						return new Point( (p.x * gridSize) + _bounds.x , (p.y * gridSize) + _bounds.y );
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