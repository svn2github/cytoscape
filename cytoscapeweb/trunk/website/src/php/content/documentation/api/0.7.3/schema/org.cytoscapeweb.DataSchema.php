<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.DataSchema";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.DataSchema";
				
				    $fn->description = "<p>This is an untyped object that represents a Data Schema type.</p>
<p>A data schema is automatically created when a network is loaded into Cytoscape Web,
   and cannot be created programatically through the API.
   However you can use the {@link org.cytoscapeweb.Visualization#addDataField} and
   {@link org.cytoscapeweb.Visualization#removeDataField} methods to change the current schema.</p>
<p>A data schema has two attributes:</p>
<ul class=\"options\">
    <li><code>nodes</code> {Array}</li>
    <li><code>edges</code> {Array}</li></ul>
<p>Those are arrays of {@link org.cytoscapeweb.DataField} objects:</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "var schema = {
    nodes: [
        { name: \"id\",    type: \"string\" },
        { name: \"label\", type: \"string\" }
    ],
    edges: [
        { name: \"id\",     type: \"string\" },
        { name: \"weight\", type: \"number\", defValue: 0.5 }
    ]
};"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#dataSchema";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.DataField";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>