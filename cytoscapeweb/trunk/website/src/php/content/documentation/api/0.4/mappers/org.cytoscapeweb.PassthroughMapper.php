<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.PassthroughMapper";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.PassthroughMapper";
				
				    $fn->description = "<p>This is an untyped object that represents a Passthrough Mapper type.</p>
<p>The values of network attributes are passed directly through to visual attributes.</p>
<p>The most common use case is using this mapper to specify node/edge labels.
For example, a passthrough mapper can label all nodes with their gene symbols.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "// Create the mapper and set it to a Visual Style's nodes.label property;
var style = {
        nodes: {
            label: { passthroughMapper: { attrName: \"symbol\" } }
        }
};

// Set the new style to the Visualization:
vis.visualStyle(style);"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.ContinuousMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.DiscreteMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.CustomMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.VisualStyle";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>