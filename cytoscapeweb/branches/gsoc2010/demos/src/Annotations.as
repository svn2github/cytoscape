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

Next:
Version 0.7 - allow dynamic adjusting of resolution by merging Shapes by the factor of 2
						- include edge quality calculation
						- implement another renderer for meshEdges to obtain better graphical control that default Bezier does not offer. 

Comments: Indent layout is rather slow to compute. Perhaps because of the huge centre shape. 

Known issues: 1) If the mesh edge is vertical or horizontal and it lies directly above the grid, its source/traget nodes cannot be retrieve. 
								Eg. Edge 9 of "social network.xml" -
								Function responsible: intersectsVertical and getNodesForMeshEdge
							2) MergeNodes_All() - Is acting weirdly, so it is disabled for now. 
							3) Edges cannot be added when nodes are not added
							4) Indent Layout does not work. 
							5) Sample 2 does not work as some nodes are not assigned any location, and are hence out of bounce. 

*/