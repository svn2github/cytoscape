<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.Edge";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
				
            
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.Edge";
				
				    $fn->description = "<p>This object represents an Edge type, but is just an untyped object.</p>
<p>So never do:</p>
<p><code>var edge = new org.cytoscapeweb.Edge(); // Wrong!!!</code></p>
<p>In order to create an edge, just create an object with the expected fields.
Notice that the attribute <code>group</code> must always be <code>\"edges\"</code>, 
because that is what really defines this type.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "var edge = {
    group: \"edges\",
    merged: false,
    opacity: 0.8,
    color: \"333333\",
    width: 2,
    // etc...
    data: {
        id: 1,
        source: 1,
        target: 3,
        weight: 0.5
    }
};"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "color";
						$field->description = "The edge color, in hexadecimal code (e.g. <code>\"#666666\"</code>).";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "curvature";
						$field->description = "The value that defines the curvature rate of curved edges. Higher values create more curved edges.";
					
					
					
					

						
						
						
						
						     $field->default_value = "18"; 
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Object";
                        
                        
                        $field->name = "data";
						$field->description = "The object that stores the custom edge attributes.
It should have at least the following properties:
<ul><li><code>id</code>: the edge id</li>
    <li><code>source</code>: the source node id</li>
    <li><code>target</code>: the target node id</li></ul>";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Boolean";
                        
                        
                        $field->name = "directed";
						$field->description = "Indicate whether or not the edge is directed. A directed edge has a default arrow pointed 
to the target node.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Array";
                        
                        
                        $field->name = "edges";
						$field->description = "If the edge is a merged one, this property provides the regular parallel edges that were merged together.
If the edge is already a regular non-merged type, this property is undefined.";
					
					
					
					

						
						
						
							
							        
							        $field->see[] = "org.cytoscapeweb.Edge#merged";

							
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "org.cytoscapeweb.Group";
                        
                        
                        $field->name = "group";
						$field->description = "The group name that defines this Data type (always <code>\"edges\"</code>).";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Boolean";
                        
                        
                        $field->name = "merged";
						$field->description = "Indicate whether or not the edge is a merged one. Merged edges are used to simplify the 
network visualization by just showing that two nodes are connected to each other, without 
displaying all the real edges that link them together.";
					
					
					
					

						
						
						
							
							        
							        $field->see[] = "org.cytoscapeweb.Visualization#edgesMerged";

							
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "opacity";
						$field->description = "The edge opacity, from <code>0</code> to <code>1.0</code> (100% opaque).";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "sourceArrowColor";
						$field->description = "The color code of the source arrow.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "org.cytoscapeweb.ArrowShape";
                        
                        
                        $field->name = "sourceArrowShape";
						$field->description = "The shape name of the edge's source arrow.";
					
					
					
					

						
						
						
						
						     $field->default_value = "<code>\"NONE\"</code>, unless the current visual style sets a different value."; 
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "targetArrowColor";
						$field->description = "The color code of the target arrow.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "org.cytoscapeweb.ArrowShape";
                        
                        
                        $field->name = "targetArrowShape";
						$field->description = "The shape name of the edge's target arrow.";
					
					
					
					

						
						
						
						
						     $field->default_value = "<ul><li><code>\"NONE\"</code>, if the edge is undirected</li>
             <li><code>\"DELTA\"</code>, if the edge is directed</li>"; 
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Boolean";
                        
                        
                        $field->name = "visible";
						$field->description = "A boolean value that indicates whether or not the edge is set to visible.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "width";
						$field->description = "The edge line width, in pixels.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>