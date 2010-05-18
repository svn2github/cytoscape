<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.DiscreteMapper";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.DiscreteMapper";
				
				    $fn->description = "<p>This object represents a Discrete Mapper type, but is just an untyped object.</p>
<p>Discrete network attributes are mapped to discrete visual attributes.</p>
<p>For example, a discrete mapper can map node colors to gene annotations.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "// Create the mapper:
var colorMapper = {
        attrName: \"molecular_function\",
        entries: [ { attrValue: \"catalytic\", value: \"#ff0000\" },
                   { attrValue: \"transporter\", value: \"#00ff00\" },
                   { attrValue: \"binding\", value: \"#0000ff\" } ]
};

// Set the mapper to a Visual Style;
var style = {
        nodes: {
            color: { discreteMapper: colorMapper }
        }
};

// Set the new style to the Visualization:
vis.visualStyle(style);

// Now, if ( node.data[\"molecular_function\"] == \"binding\" ),
// then the node will be blue"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.ContinuousMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.PassthroughMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.CustomMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.VisualStyle";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>