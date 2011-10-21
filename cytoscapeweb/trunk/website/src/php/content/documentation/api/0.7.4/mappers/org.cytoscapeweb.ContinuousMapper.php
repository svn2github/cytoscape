<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.ContinuousMapper";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
				
            
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.ContinuousMapper";
				
				    $fn->description = "<p>This object represents a Continuous Mapper type, although it is just an untyped object.</p>
<p>Depending on the visual attribute, there are two kinds of continuous mappers:</p>
<ol><li><strong>Continuous-to-Continuous Mapper:</strong> for example, you can map a continuous numerical value to a node size.</li>
    <li><strong>Color Gradient Mapper:</strong> This is a special case of continuous-to-continuous mapping. 
        Continuous numerical values are mapped to a color gradient.</li></ol>
<p>Notice that:
<ul>
    <li><strong>Continuous-to-Discrete</strong> mappers are not supported yet (e.g. all values below 0 are mapped to square nodes, 
and all values above 0 are mapped to circular nodes).</li>
    <li>Only numerical attributes and colors can be mapped with continuous mappers. For example,
there is no way to smoothly morph between circular nodes and square nodes.</il>
    <li>The mapping algorithm uses a linear interpolation to calculate the values.</li>
    <li>Continuous mappers ignore filtered out elements.</li>
</ul>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "// A mapper that could be used to set the sizes of the nodes between 12 and 36 pixels:
var sizeMapper = { attrName: \"weight\",  minValue: 12, maxValue: 36 };

// This one could be used to create a color range from yellow to green:
var colorMapper = { attrName: \"score\",  minValue: \"#ffff00\", maxValue: \"#00ff00\" };

// This edge width mapper specifies the minimum and maximum data values for the scale.
// Weights lower than 0.1 are given a width of 1, and weights higher than 1.0 are given a width of 4.
var widthMapper = { attrName: \"weight\",  minValue: 1, maxValue: 4, minAttrValue: 0.1, maxAttrValue: 1.0 };"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.DiscreteMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.PassthroughMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.CustomMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.VisualStyle";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "attrName";
						$field->description = "The name of the data attribute that will be mapped to a visual style's property.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "maxAttrValue";
						$field->description = "An optional maximum value for the linear scale. If you don't specify it,
Cytoscape Web gets the highest attribute value from the rendered nodes or edges (filtered-out elements are ignored).
And if an element's data value is higher than the specified maximum, that element's visual property is simply scaled down to the maximum value.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                        
                        $field->name = "maxValue";
						$field->description = "The maximum value of the visual style's property. It is usually a number (e.g. edge width),
but accepts strings if the visual property is a color.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Number";
                        
                        
                        $field->name = "minAttrValue";
						$field->description = "An optional minimum value for the linear scale. If you don't specify it,
Cytoscape Web gets the lowest attribute value from the rendered nodes or edges (filtered-out elements are ignored).
And if an element's data value is lower than the specified minimum, that element's visual property is simply scaled up to the minimum value.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                        
                        $field->name = "minValue";
						$field->description = "The minimum value of the visual style's property. It is usually a number (e.g. edge width),
but accepts strings if the visual property is a color.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>