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
				
				    $fn->description = "<p>Layouts are just untyped objects.</p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "var layout = {
    name:    \"Radial\",
    options: { angleWidth: 180, radius: 80 }
};"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#layout";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

				
						
                        $field = new param();
                        
                        
                            $field->type = "String";
                        
                        
                        $field->name = "name";
						$field->description = "<p>The layout name. This field is mandatory and must be one of:</p>
<ul class=\"options\"><li><code>ForceDirected</code>
    <li><code>Circle</code></li>
    <li><code>Radial</code></li>
    <li><code>Tree</code></li>
    <li><code>Preset</code></li></ul>";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Object";
                        
                        
                        $field->name = "options";
						$field->description = "<p>The available options for each layout type are:</p>
<ol class=\"options\">
<li><b>ForceDirected:</b></li>
    <ul class=\"options\">
        <li><code>mass</code> {Number}: The default mass value for nodes.</li>
        <li><code>gravitation</code> {Number}: The gravitational attraction (or repulsion, for
                                               negative values) between nodes.</li>
        <li><code>tension</code> {Number}: The default spring tension for edges.</li>
        <li><code>restLength</code> {Number}: The default spring rest length for edges.</li>
        <li><code>drag</code> {Number}: The co-efficient for frictional drag forces.</li>
        <li><code>iterations</code> {Number}: The number of iterations to run the simulation.</li>
        <li><code>maxTime</code> {Number}: The maximum time to run the simulation, in milliseconds.</li>
        <li><code>minDistance</code> {Number}: The minimum effective distance over which forces are exerted.
                                               Any lesser distances will be treated as the minimum.</li>
        <li><code>maxDistance</code> {Number}: The maximum distance over which forces are exerted. 
                                               Any greater distances will be ignored.</li>
        <li><code>autoStabilize</code> {Boolean}: A common problem with force-directed layouts is that they can be highly unstable.
                                                  If this parameter is <code>true</code> and the edges are being stretched too much
                                                  between each iteration, Cytoscape Web automatically tries to stabilize 
                                                  the network. The stabilization attempt is executed after the determined number
                                                  of <code>iterations</code>, until each edge length seems constant or until the 
                                                  <code>maxTime</code> is reached. Set <code>false</code> if you think the results
                                                  look worse than expected, or if the layout is taking too long to execute.</li>
    </ul>
<li><b>Circle:</b></li>
    <ul class=\"options\">
        <li><code>angleWidth</code> {Number}: The angular width of the layout, in degrees.</li>
        <li><code>tree</code> {Boolean}: Flag indicating if any tree-structure in the data should be used to inform the layout. The default value is <code>false</code>.</li>
    </ul>
<li><b>Radial:</b></li>
    <ul class=\"options\">
        <li><code>angleWidth</code> {Number}: The angular width of the layout, in degrees.</li>
        <li><code>radius</code> {Number}: The radius increment between depth levels.</li>
    </ul>
<li><b>Tree:</b></li>
    <ul class=\"options\">
        <li><code>orientation</code> {String}: The orientation of the tree. One of: 
                                               <code>\"leftToRight\"</code>,
                                               <code>\"rightToLeft\"</code>,
                                               <code>\"topToBottom\"</code>,
                                               <code>\"bottomToTop\"</code>.</li>
        <li><code>depthSpace</code> {Number}: The space between depth levels in the tree.</li>
        <li><code>breadthSpace</code> {Number}: The space between siblings in the tree.</li>
        <li><code>subtreeSpace</code> {Number}: The space between different sub-trees.</li>
    </ul>
<li><b>Preset:</b></li>
    <ul class=\"options\">
        <li><code>fitToScreen</code> {Boolean}: If <code>true</code>, the network is centered, and can be zoomed out to fit the screen.</li>
        <li><code>points</code> {Array}: A list of plain objects containing the node <code>id</code> and the <code>x</code>/<code>y</code>
                                         coordinates. Example:<br>
<pre class=\"example ln-\"><code class=\"js\"
>var options = {
    fitToScreen: false,
    points: [ { id: \"1\", x:  10, y:  60 },
              { id: \"2\", x: -54, y:  32 },
              { id: \"3\", x: 120, y: -12 } ]
};</code></pre></li>
    </ul>
</ol>";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>