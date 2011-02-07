<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.NetworkModel";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.NetworkModel";
				
				    $fn->description = "<p>This object represents a NetworkModel type, but is actually just an untyped object.</p>
<p>It defines the raw data (nodes and edges data values) and the data schema for a network.
It is important to notice that the network model does <b>not</b> contain {@link org.cytoscapeweb.Node} and {@link org.cytoscapeweb.Edge} objects, 
as it is not supposed to describe visual attributes such as colors, shapes and x/y coordinates.
Visual styles must be defined separately, through {@link org.cytoscapeweb.VisualStyle} or {@link org.cytoscapeweb.VisualStyleBypass}.
Nodes positioning are done by {@link org.cytoscapeweb.Layout} objects.</p>
<p>A NetworkModel object has only two fields:</p>
<ul class=\"options\">
    <li><code>dataSchema</code> {{@link org.cytoscapeweb.DataSchema}}: It defines the nodes/edges data fields.
                                You do not need to specify these essential fields: 
                                <code>id</code> (nodes or edges), <code>source</code> (edges), <code>target</code> (edges), <code>directed</code> (edges).
                                Actually, trying to modify these fields in the schema might throw an {@link org.cytoscapeweb.Error}.</li>
    <li><code>data</code> {Object}: The actual nodes/edges data values used to create {@link org.cytoscapeweb.Node} and {@link org.cytoscapeweb.Edge} elements.
                                    It contains two fields (<code>nodes</code> and <code>edges</code>), which are arrays of nodes/edges data objects.
                                    Note: data attributes of type <code>int</code> or <code>boolean</code> (see {@link org.cytoscapeweb.DataField}) 
                                    do NOT accept <code>null</code> values.</li>
</ul>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "var network = {

    dataSchema: {
        nodes: [ { name: \"label\", type: \"string\" },
                 { name: \"score\", type: \"number\" } ],
                 
        edges: [ { name: \"label\", type: \"string\" },
                 { name: \"weight\", type: \"number\" },
                 { name: \"directed\", type: \"boolean\", defValue: true} ]
    },
    
    data: {
        nodes: [ { id: \"n1\", label: \"Node 1\", score: 1.0 },
                 { id: \"n2\", label: \"Node 2\", score: 2.2 },
                 { id: \"n3\", label: \"Node 3\", score: 3.5 } ],
                 
        edges: [ { id: \"e1\", label: \"Edge 1\", weight: 1.1, source: \"n1\", target: \"n3\" },
                 { id: \"e2\", label: \"Edge 2\", weight: 3.3, source:\"n2\", target:\"n1\"} ]
    }
};"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#draw";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#networkModel";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.Visualization#dataSchema";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.DataSchema";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>