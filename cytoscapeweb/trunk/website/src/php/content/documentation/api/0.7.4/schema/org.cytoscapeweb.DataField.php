<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.DataField";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.DataField";
				
				    $fn->description = "<p>This untyped object represents a Data Field, which is a node or edge attribute definition.</p>
<p>A data field object contains the following properties:</p>
<ul class=\"options\">
	   <li><code>name</code>: The name of the data attribute.</li>
    <li><code>type</code>: The data type of the attribute. One of:
        <code>\"string\"</code>, <code>\"boolean\"</code>, <code>\"number\"</code>, <code>\"int\"</code>, <code>\"object\"</code>.</li>
    <li><code>defValue</code>: An optional default value.</li>
</ul>";
				
				    $fn->is_constructor = true;
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#addDataField";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#removeDataField";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#dataSchema";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.DataSchema";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>