<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.Event";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
				
            
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.Event";
				
				    $fn->description = "<p>This object represents an Event.</p>
<p>Events are objects passed as arguments to listeners when an event occurs.</p>
<p>All event objects have at least the following fields:</p>
   <ul><li><code>type</code></li><li><code>group</code></li></ul>
<p>The following tables lists the possible properties for each event type.</p>
<p><label><strong>click:</strong></label> Fired when the user clicks an element that belongs to the <code>group</code> you registered. 
If you don't specify any group or if the group is <code>none</code>, the event will be fired when the background of the network visualization is clicked.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>nodes</code></td><td>{@link org.cytoscapeweb.Node}</td><td><code>undefined</code></td></tr>
    <tr><td><code>edges</code></td><td>{@link org.cytoscapeweb.Edge}</td><td><code>undefined</code></td></tr>
    <tr><td><code>none</code>: clicking the visualization background</td><td><code>undefined</code></td><td><code>undefined</code></td></tr>
</Table>
<p><label><strong>dblclick:</strong></label> Fired when the user double clicks an element that belongs to the <code>group</code> you registered. 
If you don't specify any group or if the group is <code>none</code>, the event will be fired when the background of the network visualization is double-clicked.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>nodes</code></td><td>{@link org.cytoscapeweb.Node}</td><td><code>undefined</code></td></tr>
    <tr><td><code>edges</code></td><td>{@link org.cytoscapeweb.Edge}</td><td><code>undefined</code></td></tr>
    <tr><td><code>none</code>: double-clicking the visualization background</td><td><code>undefined</code></td><td><code>undefined</code></td></tr>
</Table>
<p><label><strong>mouseover:</strong></label> Fired when the user moves the mouse over an element that belongs to the <code>group</code> you registered. 
If you don't specify any group or if the group is <code>none</code>, the event will be fired any time the cursor enters the visualization rectangle.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>nodes</code></td><td>{@link org.cytoscapeweb.Node}</td><td><code>undefined</code></td></tr>
    <tr><td><code>edges</code></td><td>{@link org.cytoscapeweb.Edge}</td><td><code>undefined</code></td></tr>
    <tr><td><code>none</code>: mouse enters the visualization area</td><td><code>undefined</code></td><td><code>undefined</code></td></tr>
</Table>
<p><label><strong>mouseout:</strong></label> Fired when the user moves the mouse out of an element that belongs to the <code>group</code> you registered. 
If you don't specify any group or if the group is <code>none</code>, the event will be fired when the cursor leaves the visualization area.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>nodes</code></td><td>{@link org.cytoscapeweb.Node}</td><td><code>undefined</code></td></tr>
    <tr><td><code>edges</code></td><td>{@link org.cytoscapeweb.Edge}</td><td><code>undefined</code></td></tr>
    <tr><td><code>none</code>: mouse leaves the visualization area</td><td><code>undefined</code></td><td><code>undefined</code></td></tr>
</Table>
<p><label><strong>select:</strong></label> Fired when an element that belongs to the <code>group</code> you registered is selected.
Nodes and edges can be selected by three possible ways:
directly clicking it; using the drag-rectangle (the select event is dispatched only after the the mouse button is released); programmatically, with {@link org.cytoscapeweb.Visualization#select}. 
If you don't specify any group or if the group is <code>none</code>, the event will be fired after selecting any nodes or edges.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>nodes</code></td><td>Array of selected {@link org.cytoscapeweb.Node} objects</td><td><code>undefined</code></td></tr>
    <tr><td><code>edges</code></td><td>Array of selected {@link org.cytoscapeweb.Edge} objects</td><td><code>undefined</code></td></tr>
    <tr><td><code>none</code></td><td>Array of selected {@link org.cytoscapeweb.Node} and {@link org.cytoscapeweb.Edge} objects</td><td><code>undefined</code></td></tr>
</Table>
<p><label><strong>deselect:</strong></label> Fired when an element that belongs to the <code>group</code> you registered is deselected.
Nodes and edges can be deselected by the user or programmatically, with {@link org.cytoscapeweb.Visualization#deselect}. 
If you don't specify any group or if the group is <code>none</code>, the event will be fired after deselecting any nodes or edges.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>nodes</code></td><td>Array of deselected {@link org.cytoscapeweb.Node} objects</td><td><code>undefined</code></td></tr>
    <tr><td><code>edges</code></td><td>Array of deselected {@link org.cytoscapeweb.Edge} objects</td><td><code>undefined</code></td></tr>
    <tr><td><code>none</code></td><td>Array of deselected {@link org.cytoscapeweb.Node} and {@link org.cytoscapeweb.Edge} objects</td><td><code>undefined</code></td></tr>
</Table>
<p><label><strong>filter:</strong></label> Fired when the <code>group</code> you registered is filtered.
Nodes and edges can be filtered with {@link org.cytoscapeweb.Visualization#filter}.  
If you don't specify any group or if the group is <code>none</code>, the event will be fired after filtering nodes or edges elements.
It is important to be aware that if no element of the specified <code>group</code> is filtered (no filter applied), 
the event's <code>target</code> property will be <code>null</code>.
But if all the elements of that <code>group</code> is filtered out, <code>target</code> will be an empty array.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>nodes</code></td><td>Array of filtered {@link org.cytoscapeweb.Node} objects or <code>null</code></td><td><code>undefined</code></td></tr>
    <tr><td><code>edges</code></td><td>Array of filtered {@link org.cytoscapeweb.Edge} objects or <code>null</code></td><td><code>undefined</code></td></tr>
    <tr><td><code>none</code></td><td>Array of filtered {@link org.cytoscapeweb.Node} and {@link org.cytoscapeweb.Edge} objects or <code>null</code></td><td><code>undefined</code></td></tr>
</Table>
<p><label><strong>layout:</strong></label> Fired after a layout is applied (see {@link org.cytoscapeweb#layout}.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>none</code></td><td><code>undefined</code></td><td><code>The applied layout name</code></td></tr>
</Table>
<p><label><strong>visualstyle:</strong></label> Fired after a visual style is applied (see {@link org.cytoscapeweb#visualStyle}.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>none</code></td><td><code>undefined</code></td><td>The applied {@link org.cytoscapeweb.VisualStyle} object</td></tr>
</Table>
<p><label><strong>zoom:</strong></label> Fired after the network is rescaled, either by calling {@link org.cytoscapeweb#zoom} or 
when the user interacts with the visualization's pan-zoom control.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>none</code></td><td><code>undefined</code></td><td>The zoom value (float number from 0 to 1)</td></tr>
</Table>
<p><label><strong>error:</strong></label> Fired after the network is rescaled, either by calling {@link org.cytoscapeweb#zoom} or 
when the user interacts with the visualization's pan-zoom control.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>none</code></td><td><code>undefined</code></td><td>The {@link org.cytoscapeweb.Error} object</td></tr>
</Table>
<p><label><strong>contextmenu:</strong></label> Events of this type are only passed to the callback functions that are registered with {@link org.cytoscapeweb.Visualization#addContextMenuItem}.
You cannot add listeners to this event.</p>
<table>
    <tr><th>group</th><th>target</th><th>value</th></tr>
    <tr><td><code>nodes</code></td><td>The related {@link org.cytoscapeweb.Node} object</td><td><code>undefined</code></td></tr>
    <tr><td><code>edges</code></td><td>The related  {@link org.cytoscapeweb.Edge} object</td><td><code>undefined</code></td></tr>
    <tr><td><code>none</code></td><td>The {@link org.cytoscapeweb.Node} or {@link org.cytoscapeweb.Edge} object, if a node or edge was right-clicked. Or <code>undefined</code>, if the right click was done on an empty background area.</td><td><code>undefined</code></td></tr>
</Table>";
				
				    $fn->is_constructor = true;
				
				
				
				
                
                    
                        
                        
                            
                            $param = new param();
                            
                            $param->name = "options";
                            
                            
                            
                            
                            
                            
                            
                            $param->description = "";
                            
                            // add parameter to function
                            $fn->params[$param->name] = $param;
                        
                        
                    
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.EventType";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#addListener";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#hasListener";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#removeListener";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

				
						
                        $field = new param();
                        
                        
                            $field->type = "org.cytoscapeweb.Group";
                        
                        
                        $field->name = "group";
						$field->description = "The group of network elements the event is related to.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Object";
                        
                        
                        $field->name = "target";
						$field->description = "The event target. For example, if one or more nodes are selected, the target of the 
<code>\"select\"</code> event will be an array of node objects.
But if a node is clicked, the target of the <code>\"click\"</code> event will be just a node object.
This property is available only for event types that are related to actions performed on nodes or edges.
For the other events it is <code>undefined</code>.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "org.cytoscapeweb.EventType";
                        
                        
                        $field->name = "type";
						$field->description = "The event type name.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                        
                        $field->name = "value";
						$field->description = "This property is a very generic one and is usually used to send back any important value that
is not defined as <code>target</code>. For example, for <code>\"zoom\"</code> events, value is
the new scale, but for <code>\"error\"</code> events it is an error object.";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>