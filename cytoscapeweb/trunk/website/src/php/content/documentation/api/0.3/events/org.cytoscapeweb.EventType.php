<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.EventType";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.EventType";
				
				    $fn->description = "<p>This object represents an event type. In actuality, it is a string.</p>
<p>All of them, but <code>\"contextmenu\"</code> can be used with the listener methods 
({@link org.cytoscapeweb.Visualization#addListener}, {@link org.cytoscapeweb.Visualization#hasListener} and
{@link org.cytoscapeweb.Visualization#removeListener}).</p>
<p>Its value must be one of:</p>
    <ul class=\"options\"><li><code>click</code>:</strong> For mouse click events on nodes, edges or the visualization background.</li>
        <li><code>dblclick</code>:</strong> For double-click events on nodes, edges or the visualization background.</li>
        <li><code>mouseover</code>:</strong> For mouse-over events on nodes, edges or the visualization background.</li>
        <li><code>mouseout</code>:</strong> For mouse-out events on nodes, edges or the visualization background.</li>
        <li><code>select</code>:</strong> For events dispatched after nodes or edges are selected (e.g. by direct mouse clicking or by drag-selecting).</li>
        <li><code>deselect</code>:</strong> For events dispatched after nodes or edges are unselected.</li>
        <li><code>filter</code>:</strong> For events dispatched after nodes or edges are filtered.</li>
        <li><code>zoom</code>:</strong> For events dispatched after the network is rescaled.</li>
        <li><code>layout</code>:</strong> For events dispatched after a new layout is applied or the current one is recomputed.</li>
        <li><code>visualstyle</code>:</strong> For events dispatched after a new visual style is set.</li>
        <li><code>contextmenu</code>:</strong> For events dispatched after a right-click context menu item is selected.
                                                       You cannot use this type with the listener methods (e.g. {@link org.cytoscapeweb.Visualization#addListener}).
                                                       Events of this type are only dispatched to the callback functions that are registered with
                                                       {@link org.cytoscapeweb.Visualization#addContextMenuItem}.</li>
        <li><code>error</code>:</strong> For events dispatched when an internal error or exception occurs.</li></ul>";
				
				    $fn->is_constructor = true;
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#addListener";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#hasListener";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#removeListener";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#addContextMenuItem";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>