<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.VisualStyleBypass";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.VisualStyleBypass";
				
				    $fn->description = "<p>This object represents a Visual Style Bypass type, but it is actually just an untyped object.</p>
<p>A visual style bypass may have two attributes:</p>
<ul class=\"options\">
    <li><code>nodes</code></li>
    <li><code>edges</code></li></ul>
<p>Each one is an object that redefines a set of visual properties. They are dictionaries
that have edges and nodes <code>id</code> values as keys, and objects that contain the visual styles as values.</p>
<p>Notice that you cannot bypass <code>global</code> properties, and it is not possible to set visual mappings either.</p>
<p>You can bypass any of the nodes or edges visual properties. Just use the same names listed at 
{@link org.cytoscapeweb.VisualStyle}.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "var bypass = {
        nodes: {
            \"1\": { color: \"#ff0000\", opacity: 0.5, size: 32 },
            \"3\": { color: \"#ffff00\", opacity: 0.9 },
            \"7\": { color: \"#ffff00\", opacity: 0.2 }
        },
        edges: {
            \"22\": { width: 4, opacity: 0.2 },
            \"23\": { width: 4, opacity: 0.2 }
         }
};"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.VisualStyle";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#visualStyleBypass";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>