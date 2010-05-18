<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.Node";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
				
            
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.Node";
				
				    $fn->description = "<p>This object represents a Node type, but is actually just an untyped object.</p>
<p>So never do:</p>
<p><code>var node = new org.cytoscapeweb.Node(); // Wrong!!!</code></p>
<p>In order to create a node, just create an object with the expected fields.
Notice that the attribute <code>group</code> must always be <code>\"nodes\"</code>, 
because that is what really defines this type.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "var node = {
    group: \"nodes\",
    shape: \"TRIANGLE\",
    size: 20,
    color: \"0000ff\",
    // etc...
    data: {
        id: 1
    }
};"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "borderColor";
						$field->description = "The border color, in hexadecimal code (e.g. <code>\"#000000\"</code>).";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "borderWidth";
						$field->description = "The border width, in pixels.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "color";
						$field->description = "The node fill color, in hexadecimal code (e.g. <code>\"#ff3333\"</code>).";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Object";
                        
                        
                        $field->name = "data";
						$field->description = "The object that stores the custom node attributes.
It should have at least the <code>id</code> property.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "org.cytoscapeweb.Group";
                        
                        
                        $field->name = "group";
						$field->description = "The group name that defines this Data type (always <code>\"nodes\"</code>).";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "opacity";
						$field->description = "The node opacity, from <code>0</code> to <code>1.0</code> (100% opaque).";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "org.cytoscapeweb.NodeShape";
                        
                        
                        $field->name = "shape";
						$field->description = "The shape name.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "size";
						$field->description = "The absolute node height and width (in pixels), when the zoom level is 100%.
In Cytoscape Web, a node has the same value for both width and height.
Notice that this value is not scaled, so if you want its real visualized size, you need to multiply
this value by the current network scale, which is provided by {@link org.cytoscapeweb.Visualization#zoom}.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Boolean";
                        
                        
                        $field->name = "visible";
						$field->description = "A boolean value that indicates whether or not the node is set to visible.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "x";
						$field->description = "The x coordinate value that indicates where the center of the node is positioned in
the horizontal axis of the Visualization rectangle.
If <code>x == 0</code>, the middle point of the node is located exactly at the left border of the network view.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "y";
						$field->description = "The y coordinate value that indicates where the center of the node is positioned in
the vertical axis of the Visualization rectangle.
If <code>y == 0</code>, the middle point of the node is located exactly at the top border of the network view.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>