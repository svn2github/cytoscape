<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.Layout";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.Layout";
				
				    $fn->description = "<p>This object represents available network layouts. In actuality, it is just a string.</p>
<p>Its value must be one of:</p>
<ul class=\"options\"><li><code>ForceDirected</code>
    <li><code>Circle</code></li>
    <li><code>CircleTree</code></li>
    <li><code>Radial</code></li>
    <li><code>Tree</code></li>
    <li><code>Preset</code>: This layout is only available when the network was loaded from an 
                             <a href=\"http://www.cs.rpi.edu/~puninj/XGMML/\" target=\"_blank\">XGMML</a> data format, whose 
                             <code><a href=\"http://www.cs.rpi.edu/~puninj/XGMML/draft-xgmml-20010628.html#NodeE\" target=\"_blank\">node</a></code>
                             elements contain
                             <code><a href=\"http://www.cs.rpi.edu/~puninj/XGMML/draft-xgmml-20010628.html#GraphicsA\" target=\"_blank\">graphics</a></code>
                             tags with defined <code>x</code> and <code>y</code> attributes. In this case, by reapplying the \"Preset\" layout, you can reset
                             the nodes position according to the original x/y values.</li></ul>";
				
				    $fn->is_constructor = true;
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#layout";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>