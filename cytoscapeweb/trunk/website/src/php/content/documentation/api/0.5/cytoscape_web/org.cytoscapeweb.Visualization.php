<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.Visualization";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			
				
            

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.Visualization";
				
				    $fn->description = "<p>Initialise Cytoscape Web. It does not draw the network yet.</p>
<p>The {@link org.cytoscapeweb.Visualization#draw} method must be called when 
you want the network to be displayed.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "&lt;html&gt;
&lt;head&gt; &lt;/head&gt;
&lt;body&gt;

&lt;h1&gt;Sample&lt;/h1&gt;
&lt;div id=\"cytoWebContent\" style=\"width: 600px;height: 400px;\"&gt;&lt;/div&gt;

&lt;script type=\"text/javascript\"&gt;
    var options = { swfPath: \"path/to/swf/CytoscapeWeb\",
                    flashInstallerPath: \"path/to/swf/playerProductInstall\",
                    flashAlternateContent: \"Le Flash Player est n&eacute;cessaire.\" };
                    
    var vis = new org.cytoscapeweb.Visualization(\"cytoWebContent\", options);
    
    vis.draw({ network: '&lt;graphml&gt;...&lt;/graphml&gt;' });
&lt;/script&gt;

&lt;/body&gt;
&lt;html&gt;"; 
				
				
				
				
                
                    
                        
                        
                            
                            $param = new param();
                            
                            $param->name = "containerId";
                            
                            
                                $param->type = "String";
                            
                            
                            
                            
                            
                            
                            $param->description = "The id of the HTML element (containing your alternative content)
                            you would like to have replaced by the Flash object.";
                            
                            // add parameter to function
                            $fn->params[$param->name] = $param;
                        
                        
                    
                        
                        
                            
                            $param = new param();
                            
                            $param->name = "options";
                            
                            
                                $param->type = "Object";
                            
                            
                            
                                $param->optional = true;
                            
                            
                            
                            
                            $param->description = "Cytoscape Web parameters:
               <ul class=\"options\">
                   <li><code>swfPath</code>: The path of the compiled Cytoscape Web SWF file, but without the
                                             <code>.swf</code> extension. If you use the provided <code>CytoscapeWeb.swf</code>
                                             file and put it in the root path of the web application, this option does not need
                                             to be specified. But, for example, if you deploy the swf file at <code>/plugin/flash</code>,
                                             the <code>swfPath</code> value must be \"/plugin/flash/CytoscapeWeb\".</li>
                   <li><code>flashInstallerPath</code>: The path to the compiled Flash video that should be displayed in case
                                                        the browser does not have the Flash Player version required by Cytoscape Web.
                                                        The default value is \"playerProductInstall\" and, if this option is not changed,
                                                        the <code>playerProductInstall.swf</code> file must be deployed in the
                                                        web site's root path. Otherwise, just specify the new path without the
                                                        <code>.swf</code> extension.</li>
                   <li><code>flashAlternateContent</code>: The text message that should be displayed if the browser does not have
                                                           the Flash Player plugin. If none is provided, Cytoscape Web will show
                                                           a default message and a link to the \"Get Flash\" page.</li>
                   <li><code>resourceBundleUrl</code>: An optional resource bundle path. Usually a <code>.properties</code> file
                                                       that redefines the default labels and messages used by Cytoscape Web.
                                                       Example of a valid file with all the available keys:
<pre>
global.wait = Please wait...
error.title = Error
pan.tooltip = Grab to pan
pan.up.tooltip = Pan up
pan.down.tooltip = Pan down
pan.left.tooltip = Pan left
pan.right.tooltip = Pan right
zoom.out.tooltip = Zoom out
zoom.in.tooltip = Zoom in
zoom.fit.tooltip = Fit to screen
zoom.slider.tooltip = {0}%
</pre></li>
                   <li><code>idToken</code>: A string used to create the embedded Flash video id
                                             (usually an HTML <code>embed</code> or <code>object</code> tag).
                                             The default token is \"cytoscapeWeb\" and the final id will be the token followed
                                             by a number, so if the application has two instances of the Visualization in the same page,
                                             their id's will be \"cytoscapeWeb1\" and \"cytoscapeWeb2\".
                                             This token does not usually need to be changed.</li>
               </ul>";
                            
                            // add parameter to function
                            $fn->params[$param->name] = $param;
                        
                        
                    
                
					
					
					
					
					
					
						
						    
						        $ret = new param();
						        
						        
					                $ret->type = "org.cytoscapeweb.Visualization";
					            
					            
					            $ret->description = "The Visualization instance.";
					            
					            // add return to function 
					            $fn->return_value = $ret;
						    
						
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#draw";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#ready";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			

				

                     
                    
                        $fn = new func();
                        $fn->name = "addContextMenuItem";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Adds a custom menu item to the right-click context menu.</p>
<p>This method can only be used after a network has been drawn, so it is better to use it after the
<code>ready</code> callback function is called (see {@link org.cytoscapeweb.Visualization#ready}).</p>
<p>If an item with the same label has already been set to the same group, it will not add another
callback function to that menu item. In that case, the previous function will be replaced by
the new one and only one menu item will be displayed.</p>
<p>It is possible to add more than one menu item with the same label, but only if they are added to
different groups.</p>"; 
						
					
					
					
					     $fn->examples[] = "// We will use the context menu to select the first neighbors of the
// right-clicked node.

// 1. Assuming that you have created a visualization object:
var vis = new org.cytoscapeweb.Visualization(\"container-id\");

// 2. Add a context menu item any time after the network is ready:
vis.ready(function () {
    vis.addContextMenuItem(\"Select first neighbors\", \"nodes\", 
        function (evt) {
            // Get the right-clicked node:
            var rootNode = evt.target;
        
            // Get the first neighbors of that node:
            var fNeighbors = vis.firstNeighbors([rootNode]);
            var neighborNodes = fNeighbors.neighbors;
        
            // Select the root node and its neighbors:
            vis.select([rootNode]).select(neighborNodes);
        }
    );
});";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "lbl";
								        
								        
								            $param->type = "String";
								        
								        
								        
								        
								        $param->description = "The context menu item label to be displayed.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements the menu item will be assigned to.
                                      If <code>\"nodes\"</code>, the menu item will be visible only on right-clicks
                                      when the cursor is over a node. If <code>\"edges\"</code>, only when its over an edge.
                                      If <code>\"none\"</code> or no group is provided, the menu item will be available after a right-click
                                      over any network element, including the canvas background.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "fn";
								        
								        
								            $param->type = "Function";
								        
								        
								        
								        
								        $param->description = "The callback function that is invoked after the user selects the injected menu item.
                     That function always receives an event object as argument. The event type is always <code>\"contextmenu\"</code>.
                     If the context menu was added to the <code>nodes</code> or <code>edges</code> group, you might want to
                     get the right-clicked node or edge object by using the event's <code>target</code> property.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeContextMenuItem";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeContextMenuItem");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeAllContextMenuItems";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeAllContextMenuItems");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "addDataField";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Add a custom attribute definition to the current node or edge data schema.</p>
<p>If an attribute with the same name is already defined for the same group,
the attribute will not be added again or replace the previous definitions.</p>"; 
						
					
					
					
					     $fn->examples[] = "// 1: Add the same new field to nodes and edges data:
var field = { name: \"url\", type: \"string\", defValue: \"http://cytoscapeweb.cytoscape.org/\" };
vis.addDataField(field);

// 2: Add new field to nodes only:
var field = { name: \"score\", type: \"number\", defValue: 0.15 };
vis.addDataField(\"nodes\", field);";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements. If no group is passed,
                                     Cytoscape Web will try to add the new field to both nodes and edges data schema.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "dataField";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								        
								        $param->description = "An object that contains the attribute definitions:
                          <ul class=\"options\">
                              <li><code>name</code>: The name of the new data attribute.</li>
                              <li><code>type</code>: The data type of the attribute. One of:
                                                     <code>\"string\"</code>, <code>\"boolean\"</code>, 
                                                     <code>\"number\"</code>, <code>\"int\"</code>, <code>\"object\"</code>.</li>
                              <li><code>defValue</code>: An optional default value.</li>
                          </ul>";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeDataField";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeDataField");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#updateData";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#updateData");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "addEdge";
                    

					
					     $fn->type = "org.cytoscapeweb.Edge"; 
					
						
				     $fn->description = "<p>Create a new edge linking two nodes and add it to the network view.<p>
<p>If the edge <code>id</code> is not specified, Cytoscape Web creates a new one automatically.</p>
<p>Throws exception if missing <code>source</code> or <code>target</code>.</p>
<p>If you try to add data attributes that have not been previously defined,
Cytoscape Web will automatically add the necessary field definitions, although it might be safer to always add the
fields to the schema first, by calling {@link org.cytoscapeweb.Visualization#addDataField}.</p>"; 
						
					
					
					
					     $fn->examples[] = "var data = { id: \"e10\",
             source: \"n1\",
             target: \"n4\",
             directed: false,
             label: \"Co-expression\",
             weight: 0.88 };

var edge = vis.addEdge(data, true);";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "data";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								        
								        $param->description = "The object that contains the edge attributes.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "updateVisualMappers";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "It tells Cytoscape Web to update and reapply the visual mappers
                                       to the network view after adding the edge.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Edge";
							        
							        
							        $ret->description = "The new created edge object.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addNode";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addNode");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeElements";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeElements");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "addListener";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Appends an event listener to the network.</p>
<p>Listeners can be added or removed at any time, even before the graph is rendered, which means that you do not
need to wait for the {@link org.cytoscapeweb.Visualization#ready} function to be called.</p>"; 
						
					
					
					
					     $fn->examples[] = "// 1. Create the visualization instance:
var vis = new org.cytoscapeweb.Visualization(\"container-id\");

// 2. Add listeners at any time:
vis.addListener(\"zoom\", function(evt) {
    var zoom = evt.value;
    alert(\"New zoom value is \" + (zoom * 100) + \"%\");
})
.addListener(\"click\", \"edges\", function(evt) {
    var edge = evt.target;
    alert(\"Edge \" + edge.data.id + \" was clicked\");
})
.addListener(\"select\", \"nodes\", function(evt) {
    var nodes = evt.target;
    alert(nodes.length + \" node(s) selected\");
});

// 3. Draw the network:
vis.draw({ network: '&lt;graphml&gt;...&lt;/graphml&gt;' });";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "evt";
								        
								        
								            $param->type = "org.cytoscapeweb.EventType";
								        
								        
								        
								        
								        $param->description = "The event type.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements to assign the listener to (optional for some events).";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "fn";
								        
								        
								            $param->type = "Function";
								        
								        
								        
								        
								        $param->description = "The callback function the event invokes.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Event";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Event");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#hasListener";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#hasListener");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeListener";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeListener");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "addNode";
                    

					
					     $fn->type = "org.cytoscapeweb.Node"; 
					
						
				     $fn->description = "<p>Create a new node and add it to the network view.<p>
<p>If the node <code>id</code> is not specified, Cytoscape Web creates a new one automatically.</p>
<p>If you try to add data attributes that have not been previously defined,
Cytoscape Web will automatically add the necessary field definitions, although it might be safer to always add the
fields to the schema first, by calling {@link org.cytoscapeweb.Visualization#addDataField}.</p>"; 
						
					
					
					
					     $fn->examples[] = "var data = { id: \"n4\",
             label: \"MYO2 (Yeast)\",
             weight: 0.54 };

var node = vis.addNode(240, 360, data, true);";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "x";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								        
								        $param->description = "The horizontal coordinate of the node.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "y";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								        
								        $param->description = "The vertical coordinate of the node.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "data";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The object that contains the node attributes.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "updateVisualMappers";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "It tells Cytoscape Web to update and reapply the visual mappers
                                       to the network view after adding the node.
                                       The default value is <code>false</code>.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Node";
							        
							        
							        $ret->description = "The new created node object.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addEdge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addEdge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeElements";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeElements");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "deselect";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Deselect the indicated nodes and edges, if they are selected.</p>
<p>The same method can also be used to deselect all nodes/edges.
To do that, just omit the <code>items</code> argument and specify the group of elements to be deselected.</p>
<p>If you send repeated or invalid elements, they will be ignored.</p>"; 
						
					
					
					
					     $fn->examples[] = "// a) Deselect edges by id:
var ids = [4,6,21];
vis.deselect(\"edges\", ids);

// b) Deselect one edge only:
// Notice that the group parameter (\"edges\") is optional here,
// because it's sending an edge object and not only its id.
var e = vis.selected(\"edges\")[0]; // assuming there is at least one selected edge!
vis.deselect([e]);

// c) Deselect nodes and edges at the same time:
var n = vis.selected(\"nodes\")[0];
var e = vis.selected(\"edges\")[0];
vis.deselect([n,e]);

// d) Deselect all nodes:
vis.deselect(\"nodes\");

// e) Deselect all edges:
vis.deselect(\"edges\");

// f) Deselect all nodes and all edges:
vis.deselect();";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements.
                                     If not specified, it will try to deselect elements from both <code>node</code>
                                     and <code>edge</code> groups.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "items";
								        
								        
								            $param->type = "Array";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The items to be deselected. The array can contain node/edge objects or only
                       their <code>id</code> values. Notice however that, if you specify only the id
                       and do not pass the group argument, and if an edge and a node have the same id value,
                       both will be deselected.<br>
                       If this argument is <code>null</code>, <code>undefined</code> 
                       or omitted, it will deselect all selected items that belong to the indicated group.<br>
                       If you send an empty array, no action will be performed.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#select";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#select");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#selected";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#selected");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "draw";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Start Cytoscape Web by drawing the network.
At least the <code>network</code> option must be specified.</p>
<p>Just remember that you probably want to register a callback function with {@link org.cytoscapeweb.Visualization#ready}
before calling <code>draw()</code>.</p>"; 
						
					
					
					
					     $fn->examples[] = "var vis = new org.cytoscapeweb.Visualization(\"container-id\");
vis.draw({ network: '&lt;graphml&gt;...&lt;/graphml&gt;',
           edgeLabelsVisible: false,
           layout: 'Circle',
           visualStyle: {
               global: {
                   backgroundColor: \"#000033\",
                   nodeSelectionColor: \"#ffce81\"
               },
               nodes: {
                   shape: \"diamond\"
               },
               edges: {
                   width: 2
               }
           }
        });";
					
					     $fn->examples[] = "var vis = new org.cytoscapeweb.Visualization(\"container-id\");
vis.ready(function () {
    // Start interaction with the network here...
});
vis.draw({ network: '&lt;graphml&gt;...&lt;/graphml&gt;' });";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "options";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								        
								        $param->description = "<ul class=\"options\">
                   <li><code>network</code>: The string that describes the network. Cytoscape supports one of the following formats:
                                             <a href=\"http://graphml.graphdrawing.org/primer/graphml-primer.html\" target=\"_blank\">GraphML</a>,
                                             <a href=\"http://www.cs.rpi.edu/~puninj/XGMML/\" target=\"_blank\">XGMML</a> or
                                             <a href=\"http://cytoscape.wodaklab.org/wiki/Cytoscape_User_Manual/Network_Formats/\" target=\"_blank\">SIF</a>.
                                             Only this option is mandatory.</li>
                   <li><code>visualStyle</code>: an optional {@link org.cytoscapeweb.VisualStyle} object to be applied on this network.</li>
                   <li><code>layout</code>: an optional {@link org.cytoscapeweb.Layout} object, or just the layout name.
                                            The default is \"ForceDirected\", unless the network data is an 
                                            <a href=\"http://www.cs.rpi.edu/~puninj/XGMML/\" target=\"_blank\">XGMML</a>, whose 
                                            <code><a href=\"http://www.cs.rpi.edu/~puninj/XGMML/draft-xgmml-20010628.html#NodeE\" target=\"_blank\">node</a></code>
                                            elements contain
                                            <code><a href=\"http://www.cs.rpi.edu/~puninj/XGMML/draft-xgmml-20010628.html#GraphicsA\" target=\"_blank\">graphics</a></code>
                                            tags with defined <code>x</code> and <code>y</code> attributes. In that case, the \"Preset\" layout is applied by default.</li>
                   <li><code>nodeLabelsVisible</code>: Boolean that defines whether or not the node labels will be visible.
                                                       The default value is <code>true</code>.
                                                       You can call {@link org.cytoscapeweb.Visualization#nodeLabelsVisible} 
                                                       later (after the network is ready) to change it.</li>
                   <li><code>edgeLabelsVisible</code>: Boolean that defines whether or not the edge labels will be visible.
                                                       The default value is <code>false</code>.
                                                       You can use {@link org.cytoscapeweb.Visualization#edgeLabelsVisible} later to change it.</li>
                   <li><code>nodeTooltipsEnabled</code>: Boolean value that enables or disables the node tooltips.
                                                         The default value is <code>true</code>.
                                                         You can call {@link org.cytoscapeweb.Visualization#nodeTooltipsEnabled} later to change it.</li>
                   <li><code>edgeTooltipsEnabled</code>: Boolean that enables or disables the edge tooltips.
                                                         The default value is <code>true</code>.
                                                         You can use {@link org.cytoscapeweb.Visualization#edgeTooltipsEnabled} later to change it.</li>
                   <li><code>edgesMerged</code>: Boolean that defines whether or not the network will be initially
                                                 rendered with merged edges. The default value is <code>false</code>.
                                                 You can call {@link org.cytoscapeweb.Visualization#edgesMerged} after the network is ready to change it.</li>
                   <li><code>panZoomControlVisible</code>: Boolean value that sets whether or not the built-in control
                                                           will be visible. The default value is <code>true</code>.
                                                           The visibility of the control can be changed later with
                                                           {@link org.cytoscapeweb.Visualization#panZoomControlVisible}.</li>
               </ul>";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#ready";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#ready");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.VisualStyle";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.VisualStyle");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Layout";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Layout");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "edge";
                    

					
					     $fn->type = "org.cytoscapeweb.Edge"; 
					
						
				     $fn->description = "<p>Get one edge, including any merged edge, by its unique ID.</p>"; 
						
					
					
					
					     $fn->examples[] = "var edge = vis.edge(\"e10\");";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "id";
								        
								        
								            $param->type = "String";
								        
								        
								        
								        
								        $param->description = "The edge id.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Edge";
							        
							        
							        $ret->description = "The edge object or <code>null</code>, if there is no edge with the specified id.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#node";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#node");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#edges";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#edges");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "edgeLabelsVisible";
                    

					
						
				     $fn->description = "<p>If the boolean argument is passed, it shows or hides all the edge labels and returns the Visualization object.</p>
<p>If not, it returns a boolean value indicating whether or not the edge labels are visible.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "visible";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "true to show the labels or false to hide them.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>A boolean value for <code>edgeLabelsVisible()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>edgeLabelsVisible({Boolean})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "edges";
                    

					
					     $fn->type = "Array"; 
					
						
				     $fn->description = "<p>Get all the regular edges from the network. Merged edges are not included.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "Array";
							        
							        
							        $ret->description = "List of edges.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#mergedEdges";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#mergedEdges");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#nodes";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#nodes");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#edge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#edge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Edge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Edge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "edgesMerged";
                    

					
						
				     $fn->description = "<p>If the boolean argument is passed, it merges or unmerge all the edges and returns the Visualization object.</p>
<p>If not, it returns a boolean value indicating whether or not the edges are merged.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "merged";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "true to merge the edges or false to unmerge them.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>A boolean value for <code>edgesMerged()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>edgesMerged({Boolean})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "edgeTooltipsEnabled";
                    

					
						
				     $fn->description = "<p>If the boolean argument is passed, it enables or disables the edge tooltips.</p>
<p>If not, it returns a boolean value indicating whether or not the edge tooltips are enabled.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "enabled";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "true to enable the tooltips or false to disable them.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>A boolean value for <code>edgeTooltipsEnabled()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>edgeTooltipsEnabled({Boolean})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "embedSWF";
                    

					
						
				     $fn->description = "<p>Redefine this function if you want to use another method to detect the Flash Player version
and embed the SWF file (e.g. SWFObject).</p>
<p>By default, Adobe's <a href=\"http://www.adobe.com/products/flashplayer/download/detection_kit/\" target=\"_blank\">Flash Player Detection Kit</a>
is used.</p>"; 
						
					
					
					
						
						
						
						
						
						
							
							
							    
							        $fn->preconditions[] = "<code>AC_OETags.js</code> and <code>playerProductInstall.swf</code>";
							    
							

						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "exportNetwork";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Export the network to a URL.
It's useful when you want to download the network as an image or xml, for example.</p>
<p>This method requires a server-side part (e.g. Java, PHP, etc.) to receive the raw data from Cytoscape Web.
That server-side code should send the data back to the browser.</p>"; 
						
					
					
					
					     $fn->examples[] = "// The JavaScript code
vis.exportNetwork('xgmml', 'export.php?type=xml');";
					
					     $fn->examples[] = "&lt;?php
    # ##### The server-side code in PHP ####

    # Type sent as part of the URL:
    &#36;type = &#36;_GET['type'];
    # Get the raw POST data:
    &#36;data = file_get_contents('php://input');

    # Set the content type accordingly:
    if (&#36;type == 'png') {
        header('Content-type: image/png');
    } elseif (&#36;type == 'pdf') {
        header('Content-type: application/pdf');
    } elseif (&#36;type == 'xml') {
        header('Content-type: text/xml');
    }

    # To force the browser to download the file:
    header('Content-disposition: attachment; filename=\"network.' . &#36;type . '\"');
    # Send the data to the browser:
    print &#36;data;
?&gt;";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "format";
								        
								        
								            $param->type = "String";
								        
								        
								        
								        
								        $param->description = "One of: <code>\"png\"</code>, <code>\"pdf\"</code>, <code>\"xgmml\"</code>, <code>\"graphml\"</code>, <code>\"sif\"</code>.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "url";
								        
								        
								            $param->type = "String";
								        
								        
								        
								        
								        $param->description = "The url that will receive the exported image (bytes) or xml (text).";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "options";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "Additional options:
                             <ul class=\"options\"><li><code>width</code>:</strong> The desired width of the image in pixels (only for 'pdf' format).</li>
                                 <li><code>height</code>:</strong> The desired height of the image in pixels (only for 'pdf' format).</li>
                                 <li><code>window</code>:</strong> The browser window or HTML frame in which to display the exported image or xml.
                                                 You can enter the name of a specific window or use one of the following values:
                                                 <ul><li><code>_self</code>: the current frame in the current window.</li>
                                                     <li><code>_blank</code>: a new window.</li>
                                                     <li><code>_parent</code>: the parent of the current frame.</li>
                                                     <li><code>_top</code>: the top-level frame in the current window.</li></ul>
                                                 The default is <code>_self</code>.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#png";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#png");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#pdf";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#pdf");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#sif";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#sif");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#graphml";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#graphml");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#xgmml";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#xgmml");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "filter";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Filter nodes or edges. The filtered out elements will be hidden.</p>"; 
						
					
					
					
					     $fn->examples[] = "// Hide all edges that have a weight that is lower than 0.4:
vis.filter(\"edges\", function(edge) {
    return edge.data.weight >= 0.4;
});";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements to filter.
                                      If <code>null</code>, filter both nodes and edges.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "fn";
								        
								        
								            $param->type = "Function";
								        
								        
								        
								        
								        $param->description = "The filter function. It will receive a node or edge as argument and must
                     return a boolean value indicating the visibility of that element.
                     So, if it returns false, that node or edge will be hidden.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "updateVisualMappers";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "It tells Cytoscape Web to update and reapply the visual mappers
                                       to the network view after the filtering action is done.
                                       Remember that continuous mappers ignore filtered out elements
                                       when interpolating the results.
                                       The default value is <code>false</code>.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeFilter";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeFilter");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.ContinuousMapper";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.ContinuousMapper");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "firstNeighbors";
                    

					
						
				     $fn->description = "<p>Return the first neighbors of one or more nodes.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "nodes";
								        
								        
								            $param->type = "Array";
								        
								        
								        
								        
								        $param->description = "Array of node objects or node IDs.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "ignoreFilteredOut";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "If <code>true</code>, the algorithm will ignore any filtered out node and edge.
                                     The default value is <code>false</code>.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "An object that contains the following properties: 
        <ul class=\"options\"><li><code>rootNodes</code> {Array}: the node objects that were passed as the function parameter.</li>
            <li><code>neighbors</code> {Array}: the node objects that are neighbors of the root ones.</li>
            <li><code>edges</code> {Array}: the edge objects that connects the root and the neighbor nodes.</li>
            <li><code>mergedEdges</code> {Array}: the merged edge objects that connect the returned nodes.</li></ul>.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "graphml";
                    

					
					     $fn->type = "String"; 
					
						
				     $fn->description = "<p>Return the network data as <a href=\"http://graphml.graphdrawing.org/primer/graphml-primer.html\" target=\"_blank\">GraphML</a>.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "String";
							        
							        
							        $ret->description = "The XML text.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#xgmml";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#xgmml");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#sif";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#sif");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "hasListener";
                    

					
					     $fn->type = "Boolean"; 
					
						
				     $fn->description = "<p>Tells whether or not there are listeners to an event type.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "evt";
								        
								        
								            $param->type = "org.cytoscapeweb.EventType";
								        
								        
								        
								        
								        $param->description = "The event type.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements the listener was assigned to (optional for some events).";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "Boolean";
							        
							        
							        $ret->description = "True if there is at least one listener to the event, false otherwise.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Event";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Event");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addListener";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addListener");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeListener";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeListener");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "layout";
                    

					
						
				     $fn->description = "<p>If the <code>layout</code> argument is passed, it applies the layout to the network.
Otherwise it just returns the the current layout object.</p>
<p>In order to set a layout, you can send a layout object or just the layout name, 
if you want to use the default options.</p>
<p>See {@link org.cytoscapeweb.Layout} for the available options.</p>"; 
						
					
					
					
					     $fn->examples[] = "// 1. Initialise Cytoscape Web with a Circle layout (default layout options):
var vis = new org.cytoscapeweb.Visualization(\"container-id\");
vis.draw({ network: '&lt;graphml&gt;...&lt;/graphml&gt;', layout: 'Circle' });

// 2. Get the current layout:
var layout = vis.layout(); // returns: { name: 'Circle', options: { angleWidth: 360 } };

// 3. Apply a new layout, using default options:
vis.layout('ForceDirected');

// 4. Apply a new layout with custom options:
var options = { 
     drag:          0.2,
     gravitation:   -200,
     minDistance:   1,
     maxDistance:   400,
     mass:          2,
     tension:       0.2,
     restLength:    100,
     iterations:    200,
     maxTime:       10000,
     autoStabilize: false
};
vis.layout({ name: 'ForceDirected', options: options });";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "layout";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The {@link org.cytoscapeweb.Layout} object or the layout name.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>The current {@link org.cytoscapeweb.Layout} object for <code>layout()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>layout({Object})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Layout";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Layout");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "mergedEdges";
                    

					
					     $fn->type = "Array"; 
					
						
				     $fn->description = "<p>Get all merged edges from the network.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "Array";
							        
							        
							        $ret->description = "List of merged edges.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#edges";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#edges");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Edge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Edge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "node";
                    

					
					     $fn->type = "org.cytoscapeweb.Node"; 
					
						
				     $fn->description = "<p>Get one node by its unique ID.</p>"; 
						
					
					
					
					     $fn->examples[] = "var node = vis.node(\"n4\");";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "id";
								        
								        
								            $param->type = "String";
								        
								        
								        
								        
								        $param->description = "The node id.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Node";
							        
							        
							        $ret->description = "The node object or <code>null</code>, if there is no node with the specified id.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#edge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#edge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#nodes";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#nodes");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "nodeLabelsVisible";
                    

					
						
				     $fn->description = "<p>If the boolean argument is passed, it shows or hides all the node labels and returns the Visualization object.</p>
<p>If not, it returns a boolean value indicating whether or not the node labels are visible.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "visible";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "true to show the labels or false to hide them.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>A boolean value for <code>nodeLabelsVisible()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>nodeLabelsVisible({Boolean})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "nodes";
                    

					
					     $fn->type = "Array"; 
					
						
				     $fn->description = "<p>Get all nodes from the network.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "Array";
							        
							        
							        $ret->description = "List of nodes.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#edges";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#edges");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#node";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#node");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "nodeTooltipsEnabled";
                    

					
						
				     $fn->description = "<p>If the boolean argument is passed, it enables or disables the node tooltips.</p>
<p>If not, it returns a boolean value indicating whether or not the node tooltips are enabled.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "enabled";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "true to enable the tooltips or false to disable them.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>A boolean value for <code>nodeTooltipsEnabled()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>nodeTooltipsEnabled({Boolean})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "panBy";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Pan the \"camera\" by the specified amount, in pixels.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "amountX";
								        
								        
								            $param->type = "Number";
								        
								        
								        
								        
								        $param->description = "If negative, pan left (the network moves to the right side).";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "amountY";
								        
								        
								            $param->type = "Number";
								        
								        
								        
								        
								        $param->description = "If negative, pan up (the network moves down).";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#panEnabled";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#panEnabled");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#panToCenter";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#panToCenter");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "panEnabled";
                    

					
						
				     $fn->description = "<p>If the boolean argument is passed, it enables or disables the \"grab to pan\" mode.
It's like clicking the \"grab to pan\" button in the pan-zoom control.</p>
<p>If no argument is passed, it returns a boolean value indicating whether or not the pan mode is enabled.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "enabled";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "If <code>true</code>, clicking and dragging the background will pan the network.
                           If <code>false</code>, the pan mode is turned off - clicking and dragging the background
                           will start a drag-selection action.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>A boolean value for <code>panEnabled()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>panEnabled({Boolean})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#panBy";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#panBy");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#panToCenter";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#panToCenter");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "panToCenter";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Center the network in the canvas area.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#panEnabled";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#panEnabled");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#panBy";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#panBy");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "panZoomControlVisible";
                    

					
						
				     $fn->description = "<p>If the boolean argument is passed, it shows or hides the built-in pan-zoom control.</p>
<p>If not, it just returns a boolean value indicating whether or not the control is visible.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "visible";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "true to show it and false to hide it.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>A boolean value for <code>panZoomControlVisible()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>panZoomControlVisible({Boolean})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "pdf";
                    

					
					     $fn->type = "String"; 
					
						
				     $fn->description = "<p>Return a PDF with the network vector image.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "options";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "Additional options:
                          <ul class=\"options\">
                              <li><code>width</code>:</strong> The desired width of the image in pixels.</li>
                              <li><code>height</code>:</strong> The desired height of the image in pixels.</li>
                          </ul>";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "String";
							        
							        
							        $ret->description = "The PDF binary data encoded to a Base64 string.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "png";
                    

					
					     $fn->type = "String"; 
					
						
				     $fn->description = "<p>Return the network as a PNG image.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "String";
							        
							        
							        $ret->description = "The PNG binary data encoded to a Base64 string.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "ready";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Register a function to be called after a {@link org.cytoscapeweb.Visualization#draw} method is executed and the visualization
is ready to receive requests, such as getting or selecting nodes, zooming, etc.</p>
<p>If the application wants to interact with the rendered network, this function must be used
before calling the <code>draw</code> method.</p>"; 
						
					
					
					
					     $fn->examples[] = "// 1. Create the visualization instance:
var vis = new org.cytoscapeweb.Visualization(\"container-id\");

// 2. Register a callback function for the ready event:
vis.ready(function () {
    // Write code to interact with Cytoscape Web, e.g:
    var nodes = vis.nodes();
    // and so on...
});

// 3. And then call draw:
vis.draw({ network: '&lt;graphml&gt;...&lt;/graphml&gt;' });";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "fn";
								        
								        
								            $param->type = "Function";
								        
								        
								        
								        
								        $param->description = "The callback function that will be invoked after the network has been drawn
                     and the visualization is ready.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#draw";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#draw");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "removeAllContextMenuItems";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Removes all preset menu items from the right-click context menu.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addContextMenuItem";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addContextMenuItem");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeContextMenuItem";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeContextMenuItem");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "removeContextMenuItem";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Removes a menu item from the right-click context menu.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "lbl";
								        
								        
								            $param->type = "String";
								        
								        
								        
								        
								        $param->description = "The menu item label.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "<p>The related group. If <code>null</code>, and there is a menu item with the same label
                                       associated with a <code>\"nodes\"</code> or <code>\"edges\"</code> group, that item will not be removed.
                                       In that case, you need to call this function again with the other groups.</p>
                                       </p>For example, <code>removeContextMenuItem(\"Select\")</code> does not remove the menu item
                                       added with <code>addContextMenuItem(\"Select\", \"edge\")</code>, but only the the one added with
                                       <code>addContextMenuItem(\"Select\")</code>.<p>";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addContextMenuItem";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addContextMenuItem");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeAllContextMenuItems";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeAllContextMenuItems");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "removeDataField";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Remove a custom attribute definition from the data schema.</p>
<p>Remember that only custom metadata can be removed. Any attempt to remove the following data fields will be ignored:</p>
<ul>
    <li><code>id</code> (nodes and edges)</li>
    <li><code>source</code> (edges)</li>
    <li><code>target</code> (edges)</li>
    <li><code>directed</code> (edges)</li>
</ul>"; 
						
					
					
					
					     $fn->examples[] = "// 1: Remove a data field from nodes and edges:
vis.removeDataField(\"url\");

// 2: Remove a data field from edges only:
vis.removeDataField(\"edges\", \"url\");";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements. If no group is passed,
                                     Cytoscape Web will try to remove the field from both nodes and edges data schema.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "name";
								        
								        
								            $param->type = "String";
								        
								        
								        
								        
								        $param->description = "The name of the custom data field that will be removed.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addDataField";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addDataField");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#updateData";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#updateData");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "removeEdge";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Permanently delete the specified edge from the network.</p>
<p>If the specified edge is a merged one, all of its \"regular\" edges are deleted as well.</p>"; 
						
					
					
					
					     $fn->examples[] = "// 1. Pass the whole Edge object:
var edge = vis.edges()[0];
vis.removeEdge(edge);

// 2. Or just pass the edge id:
vis.removeEdge(\"e101\");";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "edge";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								        
								        $param->description = "The edge to be removed from the network. It can be an {@link org.cytoscapeweb.Edge}
                     object or just its <code>id</code> (String).";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "updateVisualMappers";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "It tells Cytoscape Web to reapply the visual mappers
                                       to the network view after removing the element.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeNode";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeNode");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeElements";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeElements");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addNode";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addNode");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addEdge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addEdge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "removeElements";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Permanently delete nodes and/or edges from the network.</p>
<p>If a node is deleted, all of its connected edges will be removed as well.</p>"; 
						
					
					
					
					     $fn->examples[] = "// 1. Remove edges by ID:
vis.removeElements(\"edges\", [\"1\", \"2\", \"5\"]);

// 2. Remove edges and nodes altogether, by passing the objects to be deleted:
var nodes = vis.nodes();
var edges = vis.edges();
vis.removeElements([nodes[0], nodes[1], edges[0]], true);

// 3. Remove all edges:
vis.removeElements(\"edges\");

// 4. Remove everything (nodes and edges):
vis.removeElements();";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "items";
								        
								        
								            $param->type = "Array";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The items to be removed from the network. The array can contain node/edge objects or only
                       their <code>id</code> values. Remember that, if you pass only the id
                       and do not pass the group argument, if an edge and a node have the same id value,
                       both will be removed.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "updateVisualMappers";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "It tells Cytoscape Web to reapply the visual mappers
                                       to the network view after removing the elements.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeNode";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeNode");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeEdge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeEdge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addNode";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addNode");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addEdge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addEdge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "removeFilter";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Remove a nodes or edges filter.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements to remove the filter from.
                                      If <code>null</code>, remove any existing filters from both nodes and edges.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "updateVisualMappers";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "It tells Cytoscape Web to update and reapply the visual mappers
                                       to the network view after the filtering action is done.
                                       Remember that continuous mappers ignore filtered out elements
                                       when interpolating the results.
                                       The default value is <code>false</code>.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#filter";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#filter");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.ContinuousMapper";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.ContinuousMapper");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "removeListener";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Removes an event listener.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "evt";
								        
								        
								            $param->type = "org.cytoscapeweb.EventType";
								        
								        
								        
								        
								        $param->description = "The event type.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements to assign the listener to (optional for some events).";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "fn";
								        
								        
								            $param->type = "Function";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The function the event invokes. If undefined, all registered functions
                       for the specified event are removed.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Event";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Event");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addListener";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addListener");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#hasListener";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#hasListener");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "removeNode";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Permanently delete the specified node and its associated edges from the network.</p>
<p>If a node is deleted, all of its connected edges will be removed as well.</p>"; 
						
					
					
					
					     $fn->examples[] = "// 1. Pass the whole Node object:
var node = vis.nodes()[0];
vis.removeNode(node);

// 2. Or just specify the node id:
vis.removeNode(\"n3\");";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "node";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								        
								        $param->description = "The node to be removed from the network. It can be a {@link org.cytoscapeweb.Node}
                     object or just its <code>id</code> (String).";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "updateVisualMappers";
								        
								        
								            $param->type = "Boolean";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "It tells Cytoscape Web to reapply the visual mappers
                                       to the network view after removing the element.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeEdge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeEdge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeElements";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeElements");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addNode";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addNode");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addEdge";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addEdge");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "select";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Select the indicated nodes and edges.</p>
<p>The same method can also be used to select all nodes/edges.
To do that, just omit the <code>items</code> argument and specify the group of elements to be selected.</p>
<p>If you send repeated or invalid elements, they will be ignored.</p>"; 
						
					
					
					
					     $fn->examples[] = "// a) Select nodes by id:
var ids = [1,3,5,10];
vis.select(\"nodes\", ids);

// b) Select one node:
// Notice that the group parameter (\"nodes\") is optional here,
// because it's sending a node object and not only its id.
var n = vis.nodes()[0];
vis.select([n]);

// c) Select nodes and edges at the same time:
var n = vis.nodes()[0];
var e = vis.edges()[0];
vis.select([n,e]);

// d) Select all nodes:
vis.select(\"nodes\");

// e) Select all edges:
vis.select(\"edges\");

// f) Select all nodes and all edges:
vis.select();";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "items";
								        
								        
								            $param->type = "Array";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The items to be selected. The array can contain node/edge objects or only
                       their <code>id</code> values. Notice however that, if you specify only the id
                       and do not pass the group argument, if an edge and a node have the same id value,
                       both will be selected.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#deselect";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#deselect");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#selected";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#selected");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "selected";
                    

					
					     $fn->type = "Array"; 
					
						
				     $fn->description = "<p>Get all selected nodes or edges from the network.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "Array";
							        
							        
							        $ret->description = "List of node or edge objects. If the group is not passed or is <code>null</code>,
                the returned array may contain both nodes and edges.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#select";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#select");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#deselect";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#deselect");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "sif";
                    

					
					     $fn->type = "String"; 
					
						
				     $fn->description = "<p>Return the network data as <a href=\"http://cytoscape.wodaklab.org/wiki/Cytoscape_User_Manual/Network_Formats/\" target=\"_blank\">Simple Interaction Format (SIF)</a>.</p>
<p>Cytoscape Web uses tab characters to delimit the fields, because the node and interaction names may contain spaces.</p>
<p>The node name in the SIF text is taken from the node's <code>data.id</code> attribute.</p>
<p>Cytoscape Web tries to get the interaction name from the edge's <code>data.interaction</code> attribute.
You can choose any other edge attribute to be the interaction name by passing an <code>interactionAttr</code> parameter.
If the edge data does not have the defined interaction field, Cytoscape Web just uses the edge <code>id</code>.</p>"; 
						
					
					
					
					     $fn->examples[] = "var xml = '&lt;graphml&gt;' +
              // Create a custom \"type\" attribute:
              '&lt;key id=\"type\" for=\"edge\" attr.name=\"type\" attr.type=\"string\"/&gt;' +
              '&lt;graph&gt;' +
                  '&lt;node id=\"1\"/&gt;' +
                  '&lt;node id=\"2\"/&gt;' +
                  '&lt;edge source=\"1\" target=\"2\"&gt;' +
                      '&lt;data key=\"type\"&gt;co-expression&lt;/data&gt;' +
                  '&lt;/edge&gt;' +
                  '&lt;edge source=\"2\" target=\"1\"&gt;' +
                      '&lt;data key=\"type\"&gt;co-localization&lt;/data&gt;' +
                  '&lt;/edge&gt;' +
              '&lt;/graph&gt;' +
          '&lt;/graphml&gt;';
          
var vis = new org.cytoscapeweb.Visualization(\"container_id\");

vis.ready(function() {
    // Export to SIF, using the \"type\" attribute as edge interaction:
    var text = vis.sif('type');
    
    // text ==  '1\\tco-expression\\t2\\n' +
    //          '2\\tco-localization\\t1\\n'
});

vis.draw({ network: xml });";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "interactionAttr";
								        
								        
								            $param->type = "String";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "Optional edge attribute name to be used as the SIF interaction name.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "String";
							        
							        
							        $ret->description = "The SIF text.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#graphml";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#graphml");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#xgmml";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#xgmml");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "swf";
                    

					
					     $fn->type = "Object"; 
					
						
				     $fn->description = "<p>Get Cytoscape Web's Flash object.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "Object";
							        
							        
							        $ret->description = "The appropriate reference to the Flash object.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "updateData";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>This method updates nodes and edges <code>data</code> attributes. You can use it to
change the value of any existing data attribute, except:</p>
<ul>
    <li><code>id</code> (nodes and edges)</li>
    <li><code>source</code> (edges)</li>
    <li><code>target</code> (edges)</li>
</ul>
<p>You can only update <code>data</code> attributes. Visual properties such as <code>color</code>
and <code>width</code> cannot be updated with this method. In order to change visual properties,
use {@link org.cytoscapeweb.Visualization#visualStyle} or {@link org.cytoscapeweb.Visualization#visualStyleBypass}.</p>
<p>If you try to change an attribute that has not been previously defined, the request will be ignored.
In this case, you have to add the attribute definition first, by calling {@link org.cytoscapeweb.Visualization#addDataField}.</p>
<p>Another important thing to remember is that you cannot directly change merged edges attributes.</p>
<p>Finally, all the continuous and custom mappers - defined by the current visual style - will be automatically recomputed after
updating the data.</p>"; 
						
					
					
					
					     $fn->examples[] = "// 1: Update only one node or edge:
var n = vis.nodes()[0];
n.data.label = \"New Label...\";
n.data.weight *= 2;
vis.updateData([n]);

// 2: Update more than one object at once:
var nodes = vis.nodes();
var n1 = nodes[0];
var n2 = nodes[1];
n1.data.label = \"New Label for N1\";
n2.data.label = \"New Label for N2\";

var e = vis.edges[0];
e.data.weight = 0.8;

vis.updateData([n1, n2, e]);

// 3: Update more than one object from the same group at once,
//    setting the same values to all of them:
var edge_ids = [\"1\",\"3\",\"7\"];
var data = { weight: 0.5, interaction: \"pp\" };
vis.updateData(\"edges\", edge_ids, data);

// 4: Update more than one node and edge at once,
//    setting the same values to all of them:
var ids = [\"n1\",\"n2\",\"e7\",\"e10\"];
var data = { weight: 0 };
vis.updateData(ids, data);

// 5: Update all nodes and edges with the same attribute values:
var data = { weight: 0 };
vis.updateData(data);";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "gr";
								        
								        
								            $param->type = "org.cytoscapeweb.Group";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The group of network elements.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "items";
								        
								        
								            $param->type = "Array";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The items to be updated. The array can contain node/edge objects or only
                       their <code>id</code> values. Notice however that, if you specify only the id
                       and do not pass the group argument, if an edge and a node have the same id value,
                       both will be updated.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
								    
								        $param = new param();
								        
								        $param->name = "data";
								        
								        
								            $param->type = "Object";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "The data object that contains the attributes with the new values to be applied
                       to all the elements of the specified group or to the passed <code>items</code> only.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#addDataField";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#addDataField");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#removeDataField";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#removeDataField");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "visualStyle";
                    

					
						
				     $fn->description = "<p>If the <code>style</code> argument is passed, it applies that visual style to the network.
Otherwise it just returns the current visual style object.</p>"; 
						
					
					
					
					     $fn->examples[] = "var style = {
        global: {
            backgroundColor: \"#000000\"
        },
        nodes: {
            color: \"#ffffff\",
            size: 40
        }
};

vis.visualStyle(style);";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "style";
								        
								        
								            $param->type = "org.cytoscapeweb.VisualStyle";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "An object that contains the desired visual properties and attribute mappings.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>The {@link org.cytoscapeweb.VisualStyle} object for <code>visualStyle()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>visualStyle({Object})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.VisualStyle";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.VisualStyle");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#visualStyleBypass";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#visualStyleBypass");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "visualStyleBypass";
                    

					
						
				     $fn->description = "<p>If the <code>bypass</code> argument is passed, it sets a visual style bypass on top of the regular styles.
Otherwise it just returns the current bypass object.</p>
<p>It allows you to override the visual styles (including the ones set by mappers) for individual nodes and edges,
which is very useful when the default visual style mechanism is not enough to create the desired effect.</p>"; 
						
					
					
					
					     $fn->examples[] = "// Change the labels of selected nodes and edges:
var selected = vis.selected();

var bypass = { nodes: { }, edges: { } };
var props = { 
        labelFontSize: 16,
        labelFontColor: \"#ff0000\",
        labelFontWeight: \"bold\"
};

for (var i=0; i < selected.length; i++) {
    var obj = selected[i];
    
    // obj.group is either \"nodes\" or \"edges\"...
    bypass[obj.group][obj.data.id] = props;
}

vis.visualStyleBypass(bypass);";
					
					     $fn->examples[] = "// To remove a bypass, just set <code>null</code> or an empty object:
vis.visualStyleBypass(null);";
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "bypass";
								        
								        
								            $param->type = "org.cytoscapeweb.VisualStyleBypass";
								        
								        
								        
								        
								        $param->description = "The visual properties for nodes and edges. Must be a map that has nodes/edges
                                                   ids as keys and the desired visual properties as values.
                                                   The visual properties are the same ones used by the VisualStyle objects, except that
                                                   <code>global</code> properties cannot be bypassed and are just ignored. Another difference is that you
                                                   cannot set visual mappers, but only static values.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>The {@link org.cytoscapeweb.VisualStyleBypass} object for <code>visualStyleBypass()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} instance for <code>visualStyleBypass({Object})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.VisualStyleBypass";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.VisualStyleBypass");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#visualStyle";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#visualStyle");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "xgmml";
                    

					
					     $fn->type = "String"; 
					
						
				     $fn->description = "<p>Return the network data as <a href=\"http://www.cs.rpi.edu/~puninj/XGMML/\" target=\"_blank\">XGMML</a>.</p>"; 
						
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "String";
							        
							        
							        $ret->description = "The XML text.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#graphml";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#graphml");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#sif";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#sif");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "zoom";
                    

					
						
				     $fn->description = "<p>If the scale argument is passed, it changes the zoom level of the network.
Otherwise it gets the current zoom value.</p>"; 
						
					
					
					
						
							
								    
								        $param = new param();
								        
								        $param->name = "scale";
								        
								        
								            $param->type = "Number";
								        
								        
								        
								            $param->optional = true;
								        
								        
								        $param->description = "Value between 0 and 1.";
								        
								        // add param to function
								        $fn->params[$param->name] = $param;
								    
									
							
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							        
							        $ret->description = "<ul><li>A number for <code>zoom()</code>.</li>
            <li>The {@link org.cytoscapeweb.Visualization} object for <code>zoom({Number})</code>.</li></ul>";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#zoomToFit";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#zoomToFit");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				

                     
                    
                        $fn = new func();
                        $fn->name = "zoomToFit";
                    

					
					     $fn->type = "org.cytoscapeweb.Visualization"; 
					
						
				     $fn->description = "<p>Change the scale of the network until it fits the screen.</p>
<p>If the network scale is or reaches 1 (100%) and it's not cropped, it is not zoomed in to more than that.
It also centers the network, even if the scale was not changed.</p>
<p>It does not return the result scale.
If you want to get the applied zoom level, add an event listener before calling <code>zoomToFit</code>.</p>"; 
						
					
					
					
					     $fn->examples[] = "var scale;
vis.addListener(\"zoom\", function(evt) {
    scale = evt.value;
});
vis.zoomToFit();";
					
					
					
						
						
						
						
						
							
							    
							        $ret = new param();
							        
							        
							            $ret->type = "org.cytoscapeweb.Visualization";
							        
							        
							        $ret->description = "The Visualization instance.";
							        
							        // add return to function
							        $fn->return_value = $ret;
							    
								
							
						
						
						
							
							
								    
								    $fn->see[] = "org.cytoscapeweb.Visualization#zoom";
								    
								    /*
							        $see = preg_split("/(\s)+/", "org.cytoscapeweb.Visualization#zoom");
							        foreach($see as $tag){
							            $fn->tags[] = $tag;
							            $cls_info->function_tags[] = $tag;
							        }
							        */
							    
							

						

                    
                        // add function to class
                        $cls_info->funcs[$fn->name] = $fn;
                    
				
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>