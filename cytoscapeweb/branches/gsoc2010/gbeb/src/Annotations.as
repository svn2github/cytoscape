// ActionScript file

/* Version control file.

Version 0.3
- Mesh is able to merge shape; 
- DataDisplay shows different Shapes in Mesh;
- Able to calculate Mesh for different Layouts.

Version 0.4 
- cleanup function is written to free up resources
Mesh: - shapes no longer contain repeated edges
- Patch is used to reflect the gradient around the point, as there is some unknown bug causing the gradient to be reflected about the y-axis
- Functions implemented: 
4a) generateMeshEdges; getNodesForMeshEdge; isSourceNodeAssigned, generateNonRedundantShapeIndexArray, findCentroid,
generateLineFromPointAndGradient, intersectionWithVertical, intersectionWithHorizontal
4b) mergeNodes_All(), mergeNodes_Pairwise, calculateDistanceBetweenNodes - disabled as there are some bugs
Class GeometryUtil is added to store lineIntersectline Math function
DataDisplay:  - displays the centroid of Shapes, 
- displays the mesh itself, which is accurate except for occurance of vertical straight lines due to known issue (1)

Version 0.5
Shape/ DataDisplay: - control points are set up. Initially they are added via GBEB properties and mirrored in edge.points
- control points are successfully added to all dataEdges that intersects with meshEdges - the control point is the avg of the intersection points of 
the meshEdge with the dataEdges passing through it. 

Version 0.6
Bundler added - it extends from edge render. Used Shapes.BEZIER for rendering, the effect is only satisfactory in general, but sometimes confusing for lines with > 1 control points,
due to BEZIER's mathemathical property
Comments: It doesnt work very well on simple graphs and the curves thens to complicate.
Messy graphs like circle or radial can be improved if the resolution is set higher at the expense of runtime. 

Version 0.61
DataDisplay: - now removes highlights/meshEdges when the moouse moves away to other region. 
ControlPanel - new class is added to provide runtime control of the GBEB configurations.
Functions include:

Version 0.7:
GBEB is restructured into an operator called GBEBRouter, it uses self-definied EdgeSprite and NodeSprite classes instead of that of 
Flare.
Debug - multiple runnings of the operator has been resolved by adding it once to a stable display.

Version 0.71:
Data display has been added to the main class for debug; it retains all the previous functionality. 
GeometryUtil: Added a control point calculator (CP) to find CP' from original CP, such that when curveTo accepts CP' as input,
the curve is forced to pass through CP.

Version 0.72: 
Added functionality to set different gridResolution for GBEB. Notes: I didnt not add a functionality for gridSize adjustments as 
such a function would incur a huge memory cost as the whole operator has to be cloned in order to save on computational speed.
Debugging the problem which causes excessively long edges. 
GBEBRouter: - MergeShape: All grid Index is now unique
						- Known issue 1 is fixed
KDE: If there is < 2 edges, it would not be strongly clustered.

Version 0.73:
GBEBRouter: - tweaked the mergeNodes_All() such that it is consistent with new GBEBRouter class, also modified code such
that when merging the edges extends but do not move away from control points. 
						- Added a function (displayUnmergedMesh) to display the original mesh
						- Changed mergeShape to also merge neighbouring Shapes: Decreased the occurance of "Ladders" - known Issue 6. - but
								this in turn creates a situation where the mesh edge might not lie within the Shape.
						- fixed problem of meshEdge intersecting with Edges that are in the shape but does not agree with general directions
						- fixed curve makeing loops
						- adjusted the quadratic curve for eqn for EdgeRenderer such that it passes through the CP

Version 0.8:
GBEBRouter: - Curve drawing mechanism has been changed to farmcodes's Bezier Point implementation
						- Fixed mergeNodes_All() using a distance constrain to the adjustnment so the meshEdge would not travel all about the place
						- Fixed Othering for control points via bubble sort bug that forms loops
						- fixed issue of extremely long mesh edge when centroid due to joining of ladder edges by shifting them a tad
						- Delaunay's triangulation 
						- removed direction check in shape, as it is no longer necessary an asthetic adjustment in the bundling process
						- written Kmeans clustering method from scratch, tested against randomly generated variable:works 
						
Version 0.9:
Cleaned up GeomUtil and created new GeomUtilInterface class to abstract methods

Version 0.95:
Bundling Success~! - A Pathfinder class is written to bundle the edges. Utilises kmeans to cluster and finds the centroids
						of the clusters of CP and thread the edges which have CP in the clusters through the CP.
						- Fixed minor bug in pathfinder which is causing loops
GeomUtil: - Fixed NaN bug for Kmeans
					- removed calculateDistanceBetween points function as flash has already provided this method
BundledRenderer: Redundant Class: Fixed Problem of running renderer twice





Comments: Indent layout is rather slow to compute. Perhaps because of the huge centre shape. 

Known issues: 1) If the mesh edge is vertical or has gradient = 1 and it intersects with the corner of the grid, its nodes will not
be readjusted to the lines of the grids. Instead, it will be very LONG and intersects with the bounds. 
Function responsible: intersectsVertical and getNodesForMeshEdge
2) MergeNodes_All() - Is acting weirdly, so it is disabled for now. 
3) Edges cannot be added when nodes are not added
4) Indent Layout does not work. 
5) Sample 2 does not work as some nodes are not assigned any location, and are hence out of bounce. 
6) Ladder problem - Parallel meshEdges occur in the form of a ladder because the shape diagonally away is not merged. 
Problem becomes more appararent with smaller shapes

*/