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
<p>* <strong>Continuous-to-Discrete</strong> mappers are not supported yet (e.g. all values below 0 are mapped to square nodes, 
and all values above 0 are mapped to circular nodes).</p>
<p>** Only numerical attributes and colors can be mapped with continuous mappers. For example,
there is no way to smoothly morph between circular nodes and square nodes.</p>
<p>*** The mapping algorithm uses a linear interpolation to calculate the values.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "var sizeMapper = { attrName: \"weight\",  minValue: 12, maxValue: 36 };"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.DiscreteMapper";
							
						
							 
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