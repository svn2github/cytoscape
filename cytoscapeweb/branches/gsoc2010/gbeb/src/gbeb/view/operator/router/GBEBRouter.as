/*
This file is part of Cytoscape Web.
Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

The Cytoscape Consortium is:
- Agilent Technologies
- Institut Pasteur
- Institute for Systems Biology
- Memorial Sloan-Kettering Cancer Center
- National Center for Integrative Biomedical Informatics
- Unilever
- University of California San Diego
- University of California San Francisco
- University of Toronto

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA

Additional Remarks: 
Intersection of 2 lines is taken from Source: http://keith-hair.net/blog/2008/08/04/find-intersection-point-of-two-lines-in-as3

*/

package gbeb.view.operator.router {
    import flare.animate.Transitioner;
    import flare.util.Shapes;
    import flare.vis.Visualization;
    import flare.vis.data.Data;
    import flare.vis.data.EdgeSprite;
    import flare.vis.data.NodeSprite;
    import flare.vis.operator.Operator;
    
    import flash.display.Graphics;
    import flash.display.Sprite;
    import flash.events.MouseEvent;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    import flash.text.TextField;
    
    import gbeb.util.GBEBInterfaceUtil;
    import gbeb.util.GeometryUtil;
    import gbeb.util.Pathfinder;
    import gbeb.util.delaunay.Delaunay;
    import gbeb.util.delaunay.ITriangle;
    import gbeb.util.delaunay.XYZ;
    import gbeb.CodeArchive.DataDisplay;
		
		/**
     * Inspired by Paper "Geometry-Based Edge Clustering for Graph Visualization",
     * by Wewei Cui et al: http://www.cs.ust.hk/~zhouhong/articles/infovis08_weiwei.pdf
     * 
     * @author Tomithy Too
     */
		
    public class GBEBRouter extends Operator {
        
        /** Determines how "tight" the edges are bundled. At 0, all edges are
         *  unbundled straight lines. At 1, the edges bundle together tightly.
         *  The default is 0.85. */
        public var bundling:Number = 0.85;
				/** Mesh contains a collection of nodes, edges, and control points that are calculated based
				 * on the orginal graph layout using the Geometry-based Edge Bundling Algorithm.  */
				public var _mesh:Object = { nodes: [], edges: [], CP: [] }; 
				private var _bounds:Rectangle; //stores the bounds of thei visualization
        private var _meshEdgeArray:Array; //Stores the array of meshEdges for processing
        private var _meshResolution:int = 100; //Stores the resolution of the Mesh. defined as number of meshnodes.
        private var nonRedundantShapeIndexArray:Array = new Array(); // Stores Shape that actually contain meshEdges and have a general direction
        private var _grid:Array;
				private var _pathfinder:Pathfinder = new Pathfinder();
        private var _dataDisplay:DataDisplay;

				//parameters for setting up GBEB
				private var gridSize:int = 20; // size of initial bounding grid, in pixel
				private var numGridsX:int = -1;
				private var numGridsY:int = -1;
				private var angleResolution:int = 15; //stores the angle resolution needed to resolve grid joining.
				private var _gridResolution:int = 40; //default resolution is 256
				private var _meshNodesMinDistance:Number = 30; //Defines the minimum distnance between any 2 nodes in the mesh
				private var _meshNodesMaxDisplacementDistance:int = 15; //Define the maximum distance nodes can be displaced when shifted by mergeNodes_Pairwise
				
        //parameters or checking and debug purposes
        private var cycles:int = 0;
				private var mouseMoveCounter:int = 0;
				private var ipCounter:int = 0; //counts the number of intersectionPoints
				private var mergeNodes_PairwiseCounter:int = 0;
				private var _textFieldMouseTracker:TextField = new TextField();
				private var _displayContainer:Sprite = new Sprite();
				private var _visCentroid:Sprite = new Sprite();
				private var _visEdgesContainer:Sprite = new Sprite();
				private var _visShapeContainer:Sprite = new Sprite();

				// ========[ CONSTRUCTOR ]==================================================================
				/**
				 * Creates a new Geometry Based Edge Bundling router. 
				 * Note: GBEB must be used on top of another graph layout (force-based, radial,circular, static).
				 * Bounds: Refers to the original visualisation bounds of the graph.
				 */
				public function GBEBRouter(bounds:Rectangle, gridResolution:int = 64, bundling:Number=0.85) {
					this._gridResolution = ( gridResolution < 10 || gridResolution > 100 ? 64 : Math.round(gridResolution));
					this.bundling = bundling;
					this.bounds = bounds;
				}
				
				// ========[ PUBLIC METHODS ]===============================================================
				/**
				 * Sets up the GBEB Router 
				 */
				public override function setup():void {
					try {
						var data:Data = visualization.data;
						
						_meshResolution = int(Math.sqrt(data.edges.length + data.nodes.length));
						//                updateMesh();
					} catch (err:Error) {
						trace(err.getStackTrace());
						throw err;
					}
				}
				
				/** @inheritDoc */
				public override function operate(t:Transitioner=null):void {
					
					updateMesh();
					
					// Visual Debug Tools
					//drawMesh();
					//addDataDisplay();
					//trace("GBEBRouter: Finished Running");
				}
        
				/** Returns the boundary of this visualisation */
        public function get bounds():Rectangle {
            if (_bounds == null && visualization != null) {
                // TODO: better way of creating default bounds:
                _bounds = new Rectangle(0, 0, visualization.width, visualization.height);
            }
            
            return _bounds;
        }
        
				/** Sets the boundary of this visualisation */
        public function set bounds(b:Rectangle):void {
            _bounds = b;
            //updateMesh();
        }
				
				/** Returns the boundary of this visualisation */
				public function get gridResolution():int { return _gridResolution; }

				// ========[ PRIVATE METHODS ]==============================================================
				
        /**
         * The Algorithm follows the steps below to generate Geometry Based Edge Bundling :
         * 
         * 1. Generate a uniform grid of 20px per grid for the visualisation entire bound
         * 2. For each grid, detect what data edges are inside and store them in shape.
         * 3. For each shape
				 * 		3a. Calculate the primary direction (polar angle) of each shape by using the
         * 		kernel density estimator
         * 		3b. Merge shape w/ neighbour if resulting primarily difference of this.shape.primaryAngle
         *    and neighbour.shape.primaryAngle is < 15, 
         * 		3c. Repeat until 3a and 3b are exhausted for all Shapes
         * 4. Obtain meshEdges (1 for each Shape), and their respective source/target nodes.
         *    4a. Merge meshNodes that are too close together. ( < x pix )
				 * 		4b. Use Delaunay Triangulation add more meshEdges
         * 5. For each dataEdge, check for their intersection with mesh's edge and record these 
         * intersection points in a control point array of each dataEdge
				 * 		5b. The intersection points are a kmeans function of all the intersection point on a meshEdges*
         *		5c. A Pathfinder algorithm is called to find the path lesser bends and more common routes for dataEdges,
				 * 				which are now curved through controlPoints
				 * 
				 * * For more information, refer to the paper by Wewei Cui for more details.
         */	
        protected function updateMesh():void {
            if (visualization != null) {
                
								var data:Data = visualization.data;
							
								generateGrid(); // Step 1
                
								assignDataToGrid(); // Step 2
  
                mergeShapeUsingPrimaryDirections(); // Step 3a and 3b
                
								generateMeshEdges(); //Step 4
								mergeNodes_All(); //Step 4a
								triangulateMesh(); //Step 4b
								
								data.edges.visit(addControlPoints); //Step 5
								//addCPDebugTrace(); //Debug
								KmeansClustering(); //Step 5b
								
								_pathfinder.pathfind(visualization.data, _mesh, visualization.bounds); 
                       
                //displayGrids(); //Visual Debug
											
								trace("GBEBRouter has finished running. numNodes: " + _mesh.nodes.length + " | numEdges: " + _mesh.edges.length);
            }
        }

        //1. Generate a uniform grid of 20px per grid for the visualisation entire bound   
        protected function generateGrid():void {        
						var b:Rectangle = bounds;
            var _x:int = b.x, _y:int = b.y;
            var _height:int = b.height, _width:int = b.width;
            var _counterX:int = 0, _counterY:int = 0, numGrids:int = 0;
            var _newGrid:Array = new Array();
     
						gridSize = Math.ceil(_bounds.width / _gridResolution);
						numGridsX = Math.floor(b.width /gridSize);
						numGridsY = Math.floor(b.height /gridSize);
						
            trace("GBEBRouter: numGridsX: " + numGridsX + "  | numGridsY: " + numGridsY);

            while (_counterY <= numGridsY)  {
                while (_counterX <= numGridsX) {
                    var _shape:Shape = new Shape();
                    var __grid:Rectangle = new Rectangle();
                    var point:Point = new Point(_counterX, _counterY);
                    
                    __grid.x = _x + _counterX * gridSize;
                    __grid.y = _y + _counterY * gridSize;
                    
                    __grid.width = ( _counterX != numGridsX ? gridSize : (b.width - _counterX * gridSize  ) );
                    __grid.height = ( _counterY != numGridsY ? gridSize : (b.height - _counterY * gridSize ) );

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
        }

				//Step 2. For each grid, detect what data edges are inside and store them in shape.
        protected function assignDataToGrid():void {
            if (_grid.length == 0) return;
            var data:Data = visualization.data;
            var shape:Shape;
                        
            // A quadraply nested loop of O(gridsXedges) runtime
            // It tests if there is an intersection between the grids on the edges. If so, the edges are added into the shape. 
            for each (var edge:EdgeSprite in data.edges) {
            	
							// Reset control points
            	edge.props.$controlPointsArray = [];
            	
              edge.x1 = edge.source.x; edge.y1 = edge.source.y;
							edge.x2 = edge.target.x; edge.y2 = edge.target.y;

                for each (shape in _grid) {
                    for each (var __grid:Rectangle in shape.storedGrids) {
                        if (testIntersection(__grid, new Point(edge.x1, edge.y1), new Point(edge.x2, edge.y2))) {
                            shape.storedDataEdges.push(edge);
                        }
                    }
                }
            }
                        
            for each (shape in _grid) {
                shape.computeDirection(); //calculates the primary direction of each shape
            } 
        } 
        
        // Step 2.1: Function to compute intersection of any line (represented by 2 endpoints of the line) and a grid. Returns true if there is any intersection. 
        //  More: Makes use of the the fact that for the intersection of a line with vertical or horizontal lines of a grid, 
        //  the x/y of the ver/hor lines are fixed, thus there is no need to solve simultaneous eqn. Uses basic geometric algebra
        //  saves computational time as not all intersections have to be computed.
        private function testIntersection(rec:Rectangle, p1:Point, p2:Point):Boolean
        {
            var intersects:Boolean = false;
            
            if(intersectsVertical(rec.topLeft, bottomLeft(rec), p1, p2) != null) return true; //checks against left vertical
            if(intersectsVertical(topRight(rec), rec.bottomRight, p1, p2) != null) return true; //checks against right vertical
            if(intersectsHorizontal(rec.topLeft, topRight(rec), p1, p2)!= null) return true; //checks against top horizontal
            if(intersectsHorizontal(bottomLeft(rec), rec.bottomRight, p1, p2)!= null) return true; //checks against bottom horizontal    
            return false;
        }

				
        // Step 3a and b: function merge shape if they are neighouring ( both shapes have at least 1
        //  common vertical or horizontal edge) and if their angle difference is 
        // less than angleResolution
        protected function mergeShapeUsingPrimaryDirections():Array {
						var s1:Shape;
						var repeat:Boolean = true; //boolean indication if the alogorithm should run through all the grids again
            var iterationIndexArray:Array = new Array(); //this array stores the index of the shapes that 
            																						 //needs to be iterated through by the merged Shapes function
   
            for each (s1 in _grid) {
                if(s1.direction != -1) {
                    iterationIndexArray.push(s1.gridIndex[0]); //pushing the first index automatically refers to the whole shape
                    //trace("Mesh: mergeShapeUsingPrimaryDirections: Grid " + (s1.gridIndex[0] as Point).toString() + " is stored");
                }
            }    
            trace("Mesh: mergeShapeUsingPrimaryDirections is running...Shapes to be merged: " + iterationIndexArray.length);
            
            // while there might be shapes that needs to be merged
            while(iterationIndexArray[0] != null) {
                s1 = returnShapeFromIndex(iterationIndexArray.shift()); //assigns s1 to the shape referenced by the point at the beginning of the array
                
                var neigbourShapes:Array = new Array();
                var neigboursIndex:Array = getNeighboursIndex(s1);
                var hasMerged:Boolean = false; //Stores whether the shape has merged with its immedidate neighbours

                while (neigboursIndex.length != 0) {
                    neigbourShapes.push(returnShapeFromIndex(neigboursIndex.pop()));
                }
                
                if(s1.direction == -1) continue;
                
                for each (var s2:Shape in neigbourShapes) {
                    if (s2 == null) continue;
                    if (s2.direction == -1) continue;
                    //check if neighbouring shape also shares the same direction. If so the shapes can be merged. 
                    if (Math.abs(s1.direction - s2.direction) <= angleResolution){
                        mergeShape(s1, s2);
                        hasMerged = true;
                    }
                }
                
                if(hasMerged) iterationIndexArray.push(s1.gridIndex[0]); //adds the shape back to the Iteration array since it is changed.
            }     
            return null;
        } 

        //Step 3a.1 function to get the neighbours of a particular Shape given its index
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
										
										//new:: adding neightbous from 4 diagonal positions, in the order of topLeft, topRight, bottomLeft, bottomRight
										//will cause shapes to have out of bounce edges 
										if( (p.x - 1) >= 0 && (p.y - 1) >= 0 && returnShapeFromIndex(new Point (p.x-1, p.y-1)) !== shape )		
											neigboursIndexArray.push(new Point(p.x-1, p.y-1));
										
										if( (p.x + 1) <= numGridsX && (p.y - 1) >= 0 && returnShapeFromIndex(new Point (p.x+1, p.y-1)) !== shape )		
											neigboursIndexArray.push(new Point(p.x+1, p.y-1));
										
										if( (p.x - 1) >= 0 && (p.y + 1) <= numGridsY && returnShapeFromIndex(new Point (p.x-1, p.y+1)) !== shape )		
											neigboursIndexArray.push(new Point(p.x-1, p.y+1));
										
										if( (p.x + 1) <= numGridsX && (p.y + 1) <= numGridsY && returnShapeFromIndex(new Point (p.x+1, p.y+1)) !== shape )		
											neigboursIndexArray.push(new Point(p.x+1, p.y+1));   
            }
                     
            return neigboursIndexArray;
        }
                   
        //Step 3b Merges 2 shapes s1 and s2 together. When the shapes are merged together, only 1 shape will remain. 
				//The grids in the mesh will all reference to the remaining shape
        private function mergeShape(s1:Shape, s2:Shape):Shape //s2 will destroyed
        {
            var e2:EdgeSprite;
            
            if(s1 == null && s2 == null) return null;
            if(s2 == null) return s1;
            if(s1 == null) return s2;
   
            while(s2.storedDataEdges.length != 0) {
                //transfers the edges from s2 to s1
                e2 = s2.storedDataEdges.pop();
                if(s1.storedDataEdges.indexOf(e2) == -1) s1.storedDataEdges.push(e2);
            }
            
            //transfer storedGrid Index
            s1.computeDirection(); 

            for each (var p:Point in s2.gridIndex) {
                _grid[indexFromPoint(p)] = s1;
								if(s1.gridIndex.indexOf(p) == -1) s1.gridIndex.push(p);
                //trace("Mesh: MergeShape: Point "+ p.toString() + " is added to " + s1.gridIndex[0]);
            }
            return s1;
        }
                

    // Step 4 Obtain _meshEdges (1 for each Shape), and their respective nodes. 
    // The direction variable in each shape with a majority direction gives the gradient of the mesh edge passing through the particular shape.
    // The Mesh edge needs an intersection point within the shape and this intersection point is precisely the 
    // centroid of the shape. 
    public function generateMeshEdges():void {
        if (_grid == null) return;
        
        var gradient:Number = 1;
        var currentGrid:Point = new Point(-1,-1); //stores the index of the grid that is currently being tested for intersection 
        
        _meshEdgeArray = new Array();        
        generateNonRedundantShapeIndexArray();
        
        for each (var p1:Point in nonRedundantShapeIndexArray) {
            var s1:Shape = returnShapeFromIndex(p1);
            var angleOfLine:Number = 0;
            
            if (s1.direction == -1) continue;
            
						//only shapes with a main direction will continue until here. 
            //trace("Mesh: GenerateMeshEdges: Gradient Calculation: for " + s1.direction + " gradient = " + Math.tan((s1.direction / 180) * Math.PI));
            gradient = -1 / Math.tan((s1.direction / 180) * Math.PI);   
            
            //angleOfLine = Math.atan(gradient) * 180 / Math.PI;
            //trace("Mesh: Testing Gradient: Edge " + cycles + " | direction " + s1.direction + " | Gradient:" + gradient + " |Angle: " + angleOfLine);
		     	 s1.centroid = findCentroid(s1);
		    	 var meshEdge:MeshEdge = generateLineFromPointAndGradient(s1.centroid, gradient);
				 
						 // BUG FIX: 
						 // ########################################################################################################
						 // It is noted that at for vertical lines and for lines of gradient = 1, there will be a bug that cause the
						 // meshEdge to span the length of the visualisation. As such, and offset is used to move the line away from the corners
						 // of the grid and prevent this bug from happening. 
						 if( Math.abs(gradient) > 500 || Math.abs(gradient) == Number.POSITIVE_INFINITY || ( Math.abs(gradient) > 0.99 && Math.abs(gradient) < 1.01))
						 {
							 var offset:Number = 0.2;
							 if(s1.centroid != null)
							 {
								 meshEdge.x1 += offset; meshEdge.x2 += offset; s1.centroid.x += offset;
							 }
							 //trace("GBEBRouter: BugFix: Super long mesh Edge removed!");
						 }
						 // #########################################################################################################
            
            //trace("Mesh: Generate meshEdges: " + returnIndexFromXY(s1.centroid.x, s1.centroid.y).toString() + " | " + s1.centroid.toString());
            
            currentGrid = returnIndexFromXY(s1.centroid.x, s1.centroid.y);
            
            if (currentGrid.x < 0 || currentGrid.y < 0 || currentGrid.x > numGridsX || currentGrid.y > numGridsY) continue; 
            
            meshEdge.source = new MeshNode(); meshEdge.source.x = meshEdge.x1; meshEdge.source.y = meshEdge.y1;
            meshEdge.target = new MeshNode(); meshEdge.target.x = meshEdge.x2; meshEdge.target.y = meshEdge.y2;
            meshEdge.target.name = meshEdge.source.name = (cycles).toString(); //debug
            meshEdge.name = (cycles++).toString(); //debug
            
            getNodesForMeshEdge(returnIndexFromXY(s1.centroid.x, s1.centroid.y), meshEdge, meshEdge.source, "None");
            
						if(GBEBInterfaceUtil.calculateDistanceBetweenNodes(meshEdge.source, meshEdge.target) > 300) trace("GBEBRouter: meshEdge " + meshEdge.name + 
							" is too long " + gradient);
            
            s1.meshEdge = meshEdge;
            
            if(meshEdge != null)
            {
                _meshEdgeArray.push(meshEdge);
                _mesh.nodes.push(meshEdge.source);
                _mesh.nodes.push(meshEdge.target);
                _mesh.edges.push(meshEdge);
 
            }
        }

    }
    
    //Step 4a.1 :: Recurrsive function that takes in the grid index and an edge sprite to return an edge that is assigned with nodes
    //the nodes are the found at the boundary of each Shape. It uses the last prevDir variable to pass on the information on which direction the 
    //edge came from, in order not to get a double hit. (For example, if the edge passes through the top of the prev grid, it would definitely pass
    //through the bottom of the top adjoining grid, but that is not the next intersection that we are interested to find). This function also handles
    //the special case where the gradient is 0 or infinity. 
    private function getNodesForMeshEdge(currentGridIndex:Point, edge:MeshEdge, node:MeshNode, prevDir:String):MeshNode {
        //trace("Mesh: getNodesForMeshEdge is running! EdgeNo :" + edge.name + " | CurrentGridIndex: " + currentGridIndex, "| source", edge.x1,
        //  edge.y1, "| target", edge.x2, edge.y2);
        
				var currentGridXY:Point = returnXYFromIndex(currentGridIndex);
        var currentShape:Shape = returnShapeFromIndex(currentGridIndex);
        var newGrid:Point = new Point(0,0); 
        var grid:Rectangle = new Rectangle(currentGridXY.x, currentGridXY.y, gridSize, gridSize);
				var intersectionPoint:Point; 
				var p1:Point = new Point(edge.x1, edge.y1); //stores the x,y coordinates of the source of edgeNode temporarily.
				var p2:Point = new Point(edge.x2, edge.y2); //stores the x,y coordinates of the target of edgeNode temporarily.

        intersectionPoint = intersectsHorizontal(grid.topLeft, topRight(grid), p1, p2); //check if the edge intersects with the top
			    
        if (intersectionPoint != null && prevDir != "Bottom") {                   
            newGrid.x = currentGridIndex.x; newGrid.y = currentGridIndex.y - 1;
            node.x = intersectionPoint.x, node.y = intersectionPoint.y;
            
            if (searchShapeForIndex(currentShape, newGrid)) {
                node = getNodesForMeshEdge(newGrid, edge, node, "Top");
            } else if (prevDir != "None") {
                return node;
            } 
        } 

        //This huge recurrsive function handles both the source and target nodes together. It basically checks if
        //the function is called for the first time for an edge. If it is, and that if edge.source has been moved to an intersection point
        //it will continue to work with the edge.target as node. 
        if (prevDir == "None" && isSourceNodeAssigned(edge)) {
            node = edge.target;
        }
				
        //check if the edge intersects with the bottomEdge
        intersectionPoint = intersectsHorizontal(bottomLeft(grid), grid.bottomRight, p1, p2); 

        if (intersectionPoint != null && prevDir != "Top") {
            newGrid.x = currentGridIndex.x; newGrid.y = currentGridIndex.y + 1;
            node.x = intersectionPoint.x, node.y = intersectionPoint.y;
            
            if (searchShapeForIndex(currentShape, newGrid)) {
                node = getNodesForMeshEdge(newGrid, edge, node,"Bottom");
            } else if (prevDir != "None") {
                return node;
            } 
        } 

        if (prevDir == "None" && isSourceNodeAssigned(edge)) {
            node = edge.target;
        }

        //check if the edge intersects with the leftEdge
        intersectionPoint = intersectsVertical(grid.topLeft, bottomLeft(grid), p1, p2);

        if (intersectionPoint != null && prevDir != "Right" ) {
            newGrid.x = currentGridIndex.x - 1; newGrid.y = currentGridIndex.y;
            node.x = intersectionPoint.x, node.y = intersectionPoint.y;
            
            if (searchShapeForIndex(currentShape, newGrid)) {   
                node = getNodesForMeshEdge(newGrid, edge, node, "Left");    
            } else if (prevDir != "None") {
                return node;
            } 
        } 

        //The statement below is included for consistency. Technically, it can do without the right portion after "&&")
        //as each grid is guranteed to have 2 intersections points, so by the 4th assignment must belong to the edge. 
        if (prevDir == "None" && isSourceNodeAssigned(edge)) {
            node = edge.target;
        }

        //check if the edge intersects with the rightEdge
        intersectionPoint = intersectsVertical(topRight(grid), grid.bottomRight, p1, p2);

        if (intersectionPoint != null && prevDir != "Left") {
            newGrid.x = currentGridIndex.x + 1; newGrid.y = currentGridIndex.y;
            node.x = intersectionPoint.x, node.y = intersectionPoint.y;
            
            if(searchShapeForIndex(currentShape, newGrid)) {   
                node = getNodesForMeshEdge(newGrid, edge, node, "Right");
            } else if (prevDir != "None") {
                return node;
            } 
        }
        //trace("Mesh: getNodesForMeshEdge: " +  centroid.toString() + "(It is only suppose to fall through once per edge!)"); //since each centroid is unique
        return node;
    }

    
    //Step 4a.2 :: Support function checks if the previous the intersections for the "source Node" has been found. 
    private function isSourceNodeAssigned(edge:MeshEdge):Boolean {                   
        return ! (Math.abs(edge.source.x - edge.x1) < 0.5 && Math.abs(edge.source.y - edge.y1) < 0.5)
    }
		
    //Step 4a.3 :: Clean up function which generates an array of nonRedundant index of Unique shapes that have
    //a major direction which helps in further processesing of these edges
    private function generateNonRedundantShapeIndexArray():Array {
        nonRedundantShapeIndexArray = new Array();
        
        for each (var s1:Shape in _grid) {
            if (s1.direction != -1 && nonRedundantShapeIndexArray.indexOf(s1.gridIndex[0]) == -1) {
                nonRedundantShapeIndexArray.push(s1.gridIndex[0]);
            }
        }
        return nonRedundantShapeIndexArray;
    }

    //Step 4a.4 :: Finds the Centroid of a Shape
    private function findCentroid(s:Shape):Point
    {
        if(s == null) return null;
        
        var numPoints:int = 0; 
        var xCoor:Number = 0;
        var yCoor:Number = 0;
        var xyCoor:Point = new Point(0,0);
								var centroid:Point;
        
        for each (var p:Point in s.gridIndex)
        {
            xyCoor = returnXYFromIndex(p);  
            xCoor += (xyCoor.x + 0.5 * gridSize);
            yCoor += (xyCoor.y + 0.5 * gridSize);
            numPoints++;
        }
        
        if(numPoints == 0) return null; 
        
				centroid = new Point( (xCoor / numPoints), ( yCoor / numPoints) );
						
				//to display centroid slightly if it happens to line of the intersection of the grid lines.
				if((centroid.x % gridSize) < 0.005)
				{
					if((centroid.y % gridSize) < 0.005) 
					{
						centroid.x += gridSize / 2;
						//trace("GBEBRouter: " + centroid.toString() + " is laying in the intersection");
					}
				}				
	      return centroid;							
    }
    
    //Step 4a.5 Takes in the point in which the line passes through, and the gradient of the lines
    //and return a straight EdgeSprite that is defined by the the bottom left (source coordinates) to the top-right (target corrdinates),
    //where the source and target are the intersection of the line with the boundies of the graph
    private function generateLineFromPointAndGradient(p:Point, gradient:Number):MeshEdge {
        var meshEdge:MeshEdge = new MeshEdge();
        var pointsArray:Array = new Array(); //temp storage for the target and source
        var isSourceAssigned:Boolean = false;
        
        //trace("Mesh: generateLineFromPoint: Checking for gradient :" + gradient + " at " + p.toString());
        
        //boundary conditions
        if (gradient > 500 || gradient < -500) {
						//if gradient is a large number
            if (Math.abs(p.x - bounds.x) <= 0.01) {
                pointsArray.push(new Point(bounds.x, bounds.y));
                pointsArray.push(new Point((bounds.width), (bounds.height)));
            } else {
                pointsArray.push(new Point(p.x, bounds.y));
                pointsArray.push(new Point(p.x, bounds.height));
            }
        } else if (gradient > -0.01 && gradient < 0.01) {
            //if gradient is nearing 0 
            if (Math.abs(p.y - bounds.y) <= 0.01) {
                pointsArray.push(new Point(bounds.x, bounds.y));
                pointsArray.push(new Point((bounds.width), bounds.y));
            } else {
                pointsArray.push(new Point(bounds.x, p.y));
                pointsArray.push(new Point(bounds.width, p.y));
            }
        } else {
            // gradient = -1 /gradient;
            pointsArray.push(intersectionWithVertical(bounds.x, p.x, p.y, gradient)); //with left boundary
            pointsArray.push(intersectionWithVertical(bounds.width + bounds.x, p.x, p.y,  gradient)); //with right boundary
            pointsArray.push(intersectionWithHorizontal(bounds.y, p.y, p.x, gradient)); //with top boundary
            pointsArray.push(intersectionWithHorizontal(bounds.height + bounds.y, p.y, p.x, gradient)); //with bottom boundary
        }
        
        for each (var p:Point in pointsArray) {
            if (p != null) {   
                if (!isSourceAssigned) {   
                    meshEdge.x1 = p.x;
                    meshEdge.y1 = p.y;
                    isSourceAssigned = true;
                } else {
                    meshEdge.x2 = p.x;
                    meshEdge.y2 = p.y;
                }
            } 
        }
        return meshEdge;
    }
                        
    //Step 4a.5.1 Checks if the edge generated intersects with the vertical boundary of the _mesh/bounds boundary. If it does,
    // it returns the intersection point. 
    private function intersectionWithVertical(xBoundary:Number, x1:Number, y1:Number, gradient:Number):Point
    {
        var yCoor:Number = (gradient * ( xBoundary - x1)) + y1; //stores the y coordinate of the intersection point with the vertical boundary
        
        var diff:Number = yCoor - y1;//Patch to reflect the gradient about the y -axis; necessary due to unknown bug
        yCoor -= 2 * diff;					 //Patch to reflect the gradient about the y -axis; necessary due to unknown bug
        
        if( yCoor <= bounds.y + bounds.height && yCoor >= bounds.y)  //if intersects directly at the corner, only the vertial function will return the points
        {
            return new Point(xBoundary, yCoor);
        }       
        return null;
    }
    
   	//Step 4a.5.1 Checks if the edge generated intersects with the horizontal boundary of the _mesh/bounds boundary. If it does,
		// it returns the intersection point.  
    private function intersectionWithHorizontal(yBoundary:Number, y1:Number, x1:Number, gradient:Number):Point
    {               
        gradient = 1/gradient;
        
        var xCoor:Number = (gradient  * (yBoundary - y1)) + x1;
        
        var diff:Number = xCoor - x1;//Patch to reflect the gradient about the y -axis; necessary due to unknown bug
        xCoor -= 2 * diff;						//Patch to reflect the gradient about the y -axis; necessary due to unknown bug
        
        if( xCoor < bounds.x + bounds.width && xCoor > bounds.x)  //if intersects directly at the corner, the only vertial function above will return the points
        {
            return new Point(xCoor, yBoundary);
        }                       
        return null;
    }
                                
    //Step 4b. Merge nodes that are too close together. ( < x pix )
    //Since the nodes itself doesnt not contain any reference to the edges, edges are input instead,
    //so the edge's parameteres can be altered. It does a pairwise comparison with all nodes on the mesh
		//the new position of the nodes are not allowed to travel more than x px from the original direction. max(x) = floor(gridSize / 2)
    private function mergeNodes_All():void {
        var currEdge:MeshEdge;
        var currNode:MeshNode;
				var nodeMoved:Boolean;
							
				for each (var e:MeshEdge in _meshEdgeArray)
				{
					e.sourceOriPos = new Point(e.source.x, e.source.y);
					e.targetOriPos = new Point(e.target.x, e.target.y);
				}
    
        while(_meshEdgeArray.length != 0) {
            nodeMoved = false;
						currEdge = _meshEdgeArray.pop();			
            currNode = currEdge.source;
						//trace("GBEBRouter: mergeNodes_All: Tracing Source Node: " + (currNode != null) + " | Target Node: " + (currEdge.target != null));
                            
            for each (var edge2:MeshEdge in _meshEdgeArray) {
                if (GBEBInterfaceUtil.calculateDistanceBetweenNodes(currNode, edge2.source) < _meshNodesMinDistance) {
                    mergeNodes_Pairwise(currEdge, "source", edge2, "source");
													nodeMoved = true;
                    continue;
                } else if  (GBEBInterfaceUtil.calculateDistanceBetweenNodes(currNode, edge2.target) < _meshNodesMinDistance) {
                    mergeNodes_Pairwise(currEdge, "source", edge2, "target");
													nodeMoved = true;
                    continue;
                }

                currNode = currEdge.target;
                                
                if (GBEBInterfaceUtil.calculateDistanceBetweenNodes(currNode, edge2.source) < _meshNodesMinDistance) {   
                    mergeNodes_Pairwise(currEdge, "target", edge2, "source");
													nodeMoved = true;
                    continue;
                } else if (GBEBInterfaceUtil.calculateDistanceBetweenNodes(currNode, edge2.target) < _meshNodesMinDistance) {
                    mergeNodes_Pairwise(currEdge, "target", edge2, "target");
													nodeMoved = true;
                    continue;
                }
            }
        }								
				trace("GBEBRouter: mergeNodes_Pairwise has ran " + mergeNodes_PairwiseCounter + " times");                      
    } 
                    
    //Step 4b.1 this function creates a new node in the intersection between the extension of the 2 edges
    private function mergeNodes_Pairwise(edge1:MeshEdge, s1:String, edge2:MeshEdge, s2:String):void {
        if (edge1 == null || s1 == null || edge2 == null || s2 == null) return;

        var a:Point = new Point(edge1.x1, edge1.y1);
        var b:Point = new Point(edge1.x2, edge1.y2);
        var e:Point = new Point(edge2.x1, edge2.y1);
        var f:Point = new Point(edge2.x2, edge2.y2);      
        var ip:Point = GeometryUtil.lineIntersectLine(a, b, e, f); //stores the intersectionPoint

        if (ip == null) return;
											
				//trace("GBEBRouter: mergeNodes_Pairwise: " + (edge2[s1 + "OriPos"] as Point).toString(), "|", (edge2[s2] as MeshNode).x);
				
				//if the edge nodes has to be moved more than x = floor(gridSize/2) from its original position, it will not be moved
				if( Point.distance(ip, edge1[s1 + "OriPos"]) > _meshNodesMaxDisplacementDistance ||
					Point.distance(ip, edge2[s2 + "OriPos"]) > _meshNodesMaxDisplacementDistance) 
				{
					//trace(edge1[s1 + "OriPos"].toString(), edge2[s2 + "OriPos"].toString());
					//trace(GeometryUtil.calculateDistanceBetweenPoints(ip, edge1[s1 + "OriPos"]), GeometryUtil.calculateDistanceBetweenPoints(ip, edge2[s2 + "OriPos"]), _meshNodesMaxDisplacementDistance); 
					//trace("GBEBRouter: " + edge1.name + " and " + edge2.name + " is not moved. ");
					return;
				} 
        
        /*trace("Mesh: Clustering Neigbouring Nodes: A new intersectionPoint has been created at " + ip.toString() 
            + "\nfrom points (" + edge1[s1].x + "," + edge1[s1].y + ") " + s1 + " of Edge: " + edge1.name + " and (" 
            + edge2[s2].x + "," + edge2[s2].y + ") " + s2 + " of Edge: " + edge2.name ); */

        //below checks for the particular source/target node of edge2, which will be moved to the intersection Point
        //then it assigns the particular source/target node of edge1 to the previously moved node. This results in the removal of
        //nodes in the graph and reduces its node density, by "clustering" nodes		
											
        (edge2[s2] as MeshNode).x = ip.x; (edge2[s2] as MeshNode).y = ip.y;
        edge1[s1] = edge2[s2];
											
				_meshEdgeArray.splice(_meshEdgeArray.indexOf(edge2), 1);
				mergeNodes_PairwiseCounter++; //debug
    }
				
		// Step 4c. Delanay's triangulation;
		private function triangulateMesh():void
		{	
			//renderOriginalMeshEdges();//debug;
			trace("GBEBRouter: Triangulation: _mesh.nodes.length: " + _mesh.nodes.length + " | _mesh.edges.length " + _mesh.edges.length);
			
			if(_mesh.nodes.length < 3) return; //there must be at least 3 nodes to triangulate	
			var pointsArray:Array = [];
			
			for each (var edge:MeshEdge in _mesh.edges)
			{
				//there are redundant nodes in the array: Does it matter?
				pointsArray.push(new XYZ(edge.source.x, edge.source.y));
				pointsArray.push(new XYZ(edge.target.x, edge.target.y));
			}			
			var triangles:Array = Delaunay.triangulate(pointsArray);
			//GBEBInterfaceUtil.drawDelaunay(triangles, pointsArray, visualization);
			
			_mesh.edges = GBEBInterfaceUtil.convertToMeshEdges(triangles, pointsArray, _mesh.nodes);
		}

				
		// Step 5. For each edge in _data and each edge in _mesh we are going to calculate their intersection and
		// assign control points
		private function addControlPoints(dataEdge:EdgeSprite):void
		{
			var intersectionPointsArray:Array = new Array();
			var intersectionPoint:Point;
			var cp:Point; 
			var dataEdgeDirection:int;
			var deiPair:*; //stands for dataEdge-Intersection Pair 
			var a:Point, b:Point; //a,b stores the end points of the meshEdge of each shape
			var e:Point, f:Point; //e,f stores the end points of the each dataEdge					
			
			for each (var meshEdge:MeshEdge in _mesh.edges) {
				a = new Point(meshEdge.x1, meshEdge.y1);
				b = new Point(meshEdge.x2, meshEdge.y2);
				e = new Point(dataEdge.source.x, dataEdge.source.y);
				f = new Point(dataEdge.target.x, dataEdge.target.y);
				
				intersectionPoint = GeometryUtil.lineIntersectLine(a, b, e, f);
				
				if (intersectionPoint != null) {
					deiPair = new Object();
					deiPair.dataEdge = dataEdge;		
					deiPair.ip = intersectionPoint;
					meshEdge.dataEdgeIntersectionPairs.push(deiPair);					
					ipCounter++; //debug
				}
			}					
		}
				
				
				
		//5a.1 Apply kmeans clustering to all the intersection points on each meshEdge
		private function KmeansClustering():void
		{
			trace("GEBEBRouter: Kmeans clustering is running...");
				
			for each (var e:MeshEdge in _mesh.edges)
			{
				var ipArray:Array = [];
				var clusters:Array; //stores the results of kmeans clustering
				for each (var pair:* in e.dataEdgeIntersectionPairs)
				{
					ipArray.push(pair.ip);
				}
						
				if(ipArray.length == 0) continue;
				
				clusters = GeometryUtil.kmeans(ipArray);
				for each(var cluster:Array in clusters)
				{
					var centroid:Point = GeometryUtil.findCentroidFromPoints(cluster);
					for each(var p:Point in cluster)
					{
						var pair:* = e.dataEdgeIntersectionPairs[ipArray.indexOf(p)]; //assumes that the index of ipArray and dataEdgeIntersectionPair is the same
						var dataEdge:EdgeSprite = pair.dataEdge as EdgeSprite; 
		
						var ctrl:Array = dataEdge.props.$controlPointsArray;
						if (ctrl == null) dataEdge.props.$controlPointsArray = ctrl = [];
						ctrl.push(centroid);
						
						//dataEdge.lineWidth = dataEdge.lineWidth /2 ; //lower width gives better visual quality
						dataEdge.shape = Shapes.BSPLINE; //Here to change curve type
						dataEdge.lineWidth *= 0.5;
						dataEdge.lineAlpha = 0.5;
						
					}
					_mesh.CP.push(centroid);							
				}
			}
		}
				
		//Debug function:: used to check if the control points have been added correctly to the dataEdges
		private function addCPDebugTrace():void
		{
			trace("GBEBRouter: addControlPoints: " + ipCounter + " intersectionPoints have been added.");
			
			for each (var e:MeshEdge in _mesh.edges)
			{
				if (e.dataEdgeIntersectionPairs == null || e.dataEdgeIntersectionPairs.length == 0) continue;
				for each (var deiPair:* in e.dataEdgeIntersectionPairs)
				{
					var pair:* = deiPair; 
					var dataEdge:EdgeSprite;
					var ip:Point; //intersectionPoint
					
					if (pair == null) continue;
					dataEdge = pair.dataEdge as EdgeSprite; 
					ip = pair.ip as Point;		
					if (dataEdge == null || ip == null) continue;		
					
					trace("GBEBRouter: addCPDebugTrace: " + dataEdge.source.data["name"] + " --> " + dataEdge.target.data["name"] + " intersects " + e.name 
						+ " at " + ip.toString());
				}
			}
		}
			
		//5a.2 Sorts the control points of the shape by ascending distance from source. This prevents the edges
		//from forming loops 
		private function sortCPByDistance(e:EdgeSprite):void
		{
		var ctrl:Array = e.props.$controlPointsArray;
		if(ctrl == null) return;	
		
		var sourceNode:Point = new Point(e.source.x, e.source.y); //casting source node as mesh modes
		var targetNode:Point = new Point(e.target.x, e.target.y);
		var swapArray:Array = [];
		var disSourceTarget:Number = Point.distance(sourceNode, targetNode);
		
		//trace("GBEBRouter: Bubble sorting CP by Distance...", e.name);
		for each (var p:Point in ctrl)
		{
			if( p == null){
				ctrl.splice(ctrl.indexOf(p), 1); //trace("A null node has been spliced");
			} 
		}
		ctrl = bubbleSortPointsArray(ctrl, sourceNode);
		
		//trace("GBEBRouter: BubbleSort - Array trace: " + distance );//+ distance, e.source.data["name"], e.target.data["name"]);
		
		for(var i:int = 0; i < ctrl.length; i++)
		{
			
			var disTargetP:Number = Point.distance(targetNode, ctrl[i]);
			if(disTargetP > disSourceTarget)
			{
				swapArray.push(ctrl[i]);
				ctrl.splice(ctrl.indexOf(i), 1);
			}
		}
		
		swapArray = bubbleSortPointsArray(swapArray, targetNode, false);
		
		for each (var p:Point in swapArray) 
		{
			ctrl.unshift(swapArray.shift()); 
		}
		//trace("GBEBRouter: BubbleSort - Swap Array trace: " + distance, e.source.data["name"], e.target.data["name"]);
		
		}
		
		// 5a.2.1 takes in an array and result a sorted arraying in increasing distance away from target point.
		private function bubbleSortPointsArray(a:Array, targetPoint:Point, increasing:Boolean = true):Array
		{
			var currDist:Number; var nextDist:Number; var temp:*;
			for (var i:int = 0; i < a.length; i++)
			{
				for (var j:int = 0; j < a.length - i - 1; j++)
				{
					currDist = Point.distance(targetPoint,a[j]);
					nextDist = Point.distance(targetPoint,a[j + 1]);
					if(increasing)
					{
						if(currDist > nextDist)
						{
							temp = a[j+1]
							a[j+1] = a[j];
							a[j] = temp;
						}
					} else {
						if(currDist < nextDist)
						{
							temp = a[j+1]
							a[j+1] = a[j];
							a[j] = temp;
						}
					}
				}
			}
			return a;
		}

// TODO: Some of these functions might be provided by Flare or Cytoscape Web!
// ##############################################################################
    
		// ========[ Helper Functions ]================================================================
		
		//Notes: These intersection functions only check for eqaulity in the vertical intersection (meaning checking for corners)
    // Hence, it prevents any multiple counting
        
		private function intersectsVertical(vp1:Point, vp2:Point, p1:Point, p2:Point):Point {
		    var _x:Number = vp1.x;
		    if( (_x >= p1.x && _x <= p2.x) || (_x >= p2.x && _x <= p1.x)) //checks if the x-coor is within the interval of the line
		    {
		        var _y:Number = (( (p2.y - p1.y) / (p2.x - p1.x)    ) * ( _x - p1.x)) + p1.y;    
		        if( (_y >= vp1.y && _y <= vp2.y) || (_y >= vp2.y && _y <= vp1.y)) //checks if the calculated y-coor is within the interval of the vertical line 
		        {   //trace("GBEB: IntersectsVertical: " + p1, p2 + " intersects with " + vp1 + " at " + new Point(_x, _y));
		            return new Point(_x, _y);
		        }
		    }   
		    return null;
		}
        
    private function intersectsHorizontal(vp1:Point, vp2:Point, p1:Point, p2:Point):Point {
        var _y:Number = vp1.y; 
        if( (_y >= p1.y && _y <= p2.y) || (_y >= p2.y && _y <= p1.y)) //checks if the y-coor is within the interval of the line
        {                       
            var _x:Number = (( (p2.x - p1.x) / (p2.y - p1.y)    ) * ( _y - p1.y)) + p1.x; 
            
            if( (_x > vp1.x && _x < vp2.x) || (_x > vp2.x && _x < vp1.x)) //checks if the calculated x-coor is within the interval of the horizontal line   
            {  // trace("GBEB: IntersectsHorizontal: " + p1, p2 + " intersects with " + vp1 + " at " + new Point(_x, _y));
                return new Point(_x, _y);
            }
        }   
        return null;
    }
        
    //flash.geom.rec does not have a refernce readily available
    private function bottomLeft(rec:Rectangle):Point {
        return new Point(rec.left, rec.top + rec.height);
    }
    
    private function topRight(rec:Rectangle):Point {
        return new Point(rec.left + rec.width, rec.top);
    }

		
        
    private function returnShape(x:int, y:int):Shape {
        if (numGridsX == -1 || numGridsY == -1) return null;
        
        var gridX:int = Math.floor((x - bounds.x) / gridSize);
        var gridY:int = Math.floor((y - bounds.y) / gridSize);
        
        return _grid[gridY * (numGridsX - (-1))+ gridX] as Shape; // remove the (10-1) after trials 
    }
    
    private function returnShapeFromIndex(p:Point):Shape {
        return _grid[p.y * (numGridsX + 1 )+ p.x] as Shape; // remove the (10-1) after trials
    }
    
    private function indexFromPoint(p:Point):int {
        return p.x + p.y * (numGridsX + 1 ); // remove the (10-1) after trials
    }
    
    private function returnIndexFromXY(x:Number, y:Number):Point {
        x = Math.floor((x - bounds.x) / gridSize);
        y = Math.floor((y - bounds.y) / gridSize);
        
        return new Point(x, y);
    }

    // returns the actual (x,y) coordinate from index, returns the top-left point of the grid. 
    // May return any intermediate value if the Point p given is not any integer. 
    private function returnXYFromIndex(p:Point):Point {
        return new Point( (p.x * gridSize) + bounds.x , (p.y * gridSize) + bounds.y );
    }

    // searches the GridIndex array of a given Shape for an index. Returns true, if the index exist in the Grid Array
    private function searchShapeForIndex(s:Shape, index:Point):Boolean {
        if (s == null) return false;
        
        for each (var p:Point in s.gridIndex) {
            if (p.x == index.x && p.y == index.y) return true; 
        } 
        return false;
    }
		
  } // end of class BundledEdgeRouter
}