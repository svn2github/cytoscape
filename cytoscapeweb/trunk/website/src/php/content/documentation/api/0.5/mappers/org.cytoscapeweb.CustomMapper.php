<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.CustomMapper";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.CustomMapper";
				
				    $fn->description = "<p>This is a special type of mapper that allows you to register a callback function
that will be called for each associated element (nodes or edges). 
The function will then be responsible for returning the desired property value.</p>
<p>The callback function should expect a <code>data</code> object as argument.</p>
<p>You could, for example, use a custom mapper to create a better tooltip text.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "// 1. First, create a function and add it to the Visualization object.
vis[\"customTooltip\"] = function (data) {
    var value = Math.round(100 * data[\"weight\"]) + \"%\";
    return 'The confidence level of this link is: ' +
           '&lt;font color=\"#000099\" face=\"Courier\" size=\"14\"&gt;' + value + '&lt;/font&gt;';
};

// 2. Now create a new visual style (or get the current one) and register
//    the custom mapper to one or more visual properties:
var style = vis.visualStyle();
style.edges.tooltipText = { customMapper: { functionName: \"customTooltip\" } },

// 3. Finally set the visual style again:
vis.visualStyle(style);"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.ContinuousMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.DiscreteMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.PassthroughMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.VisualStyle";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>