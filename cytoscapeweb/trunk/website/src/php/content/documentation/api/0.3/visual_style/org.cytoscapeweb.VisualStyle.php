<?php
                

                

                


 /*<!-- ============================== class summary ========================== -->	*/ 		
            
             
                $cls_info = new cls();
                $cls_info->name = "org.cytoscapeweb.VisualStyle";
             
            
            
                        
             $cls_info->description = ""; 
            
            
                 $cls_info->file = "../cytoscapeweb/bin/js/cytoscapeweb.js"; 
            

 /*<!-- ============================== properties summary ===================== -->*/ 
			
				
            
            
 /*<!-- ============================== methods summary ======================== -->*/ 
			

 /*<!-- ============================== events summary ======================== -->*/ 
			

 /*<!-- ============================== constructor details ==================== -->	*/ 	
			
				
				    $fn = new func();
                    
                    $fn->is_constructor = true;
                    
				    $fn->name = "org.cytoscapeweb.VisualStyle";
				
				    $fn->description = "<p>This object represents a Visual Style type, but it is actually just an untyped object.</p>
<p>A visual style may have three attributes:</p>
<ul class=\"options\">
    <li><code>global</code></li>
    <li><code>nodes</code></li>
    <li><code>edges</code></li></ul>
<p>Each one is an object that defines a set of visual properties.</p>

<p>For each visual property, you can specify a default value or define a dynamic visual mapping.
Cytoscape Web currently supports four different types of visual mappers:</p>
<ul class=\"options\">
    <li><code>continuousMapper</code></li>
    <li><code>discreteMapper</code></li>
    <li><code>passthroughMapper</code></li>
    <li><code>customMapper</code></li></ul> 

<p>In order to create a visual style, just create an object with the expected fields.</p>
<p>Never do:</p>
<p><code>var style = new org.cytoscapeweb.VisualStyle(); // Wrong!!!</code></p>";
				
				    $fn->is_constructor = true;
				
				
				
				     $fn->examples[] = "var style = {
        global: {
            backgroundColor: \"#ffffff\",
            tooltipDelay: 1000
        },
        nodes: {
            shape: \"ELLIPSE\",
            color: \"#333333\",
            opacity: 1,
            size: { defaultValue: 12, 
                    continuousMapper: { attrName: \"weight\", 
                                        minValue: 12, 
                                        maxValue: 36 } },
            borderColor: \"#000000\",
            tooltipText: \"&lt;b&gt;&#36{label}&lt;/b&gt;: &#36{weight}\"
        },
        edges: {
            color: \"#999999\",
            width: 2,
            mergeWidth: 2,
            opacity: 1,
            label: { passthroughMapper: { attrName: \"id\" } },
            labelFontSize: 10,
            labelFontWeight: \"bold\"
         }
};"; 
				
				
				
				
                
					
					
					
					
					
					
					
					
					
					
						
							 
							    $fn->see[] = "org.cytoscapeweb.ContinuousMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.DiscreteMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.PassthroughMapper";
							
						
							 
							    $fn->see[] = "org.cytoscapeweb.CustomMapper";
							
						
					
					
					
					    // add function to class
					    $cls_info->funcs[$fn->name] = $fn;
					    $cls_info->constructor = $fn;
					
			

 /*<!-- ============================== field details ========================== -->		*/ 
			

				
						
                        $field = new param();
                        
                        
                            $field->type = "Object";
                        
                        
                        $field->name = "edges";
						$field->description = "<p>An object that defines visual styles for edges.</p>
<p>The possible edge properties are:</p>
<ul class=\"options\"><li><code>color</code> {String}: Color of edges. The default value is \"#999999\".</li>
    <li><code>width</code> {Number}: Line width of edges. The default value is 1 pixel.</li>
    <li><code>opacity</code> {Number}: The edge opacity (0 to 1). The default value is 0.8.</li>
    <li><code>mergeColor</code> {String}: Line color for merged edges. The default value is \"#666666\".</li>
    <li><code>mergeWidth</code> {Number}: Line width for merged edges. The default value is 1 pixel.</li>
    <li><code>mergeOpacity</code> {Number}: Opacity of merged edges (0 to 1). The default value is 0.8.</li>
    <li><code>selectionColor</code> {String}: The fill color of selected edges.
                                                               The default value is the same one set to <code>color</code>
                                                               (or <code>mergeColor</code>, when edges are merged).</li>
    <li><code>selectionOpacity</code> {Number}: The opacity of selected edges (0 to 1).
                                                                 The default value is the same one set to <code>opacity</code>.</li>
    <li><code>selectionLineWidth</code> {Number}: The border width of selected edges (0 to 1).
                                                                   The default value is the same one set to <code>width</code>.</li>
    <li><code>selectionGlowColor</code> {String}: The glow color of selected edges.The default value is \"#ffff33\".</li>
    <li><code>selectionGlowOpacity</code> {Number}: The glow transparency of selected edges. Valid values are 0 to 1.
                                                                     The default value is 0.6 (60% opaque).</li>
    <li><code>selectionGlowBlur</code> {Number}: The amount of blur for the selection glow. Valid values are 0 to 255 (floating point).
                                                                  The default value is 4. Values that are a power of 2 (such as 2, 4, 8, 16, and 32) 
                                                                  are optimized to render more quickly.</li>
    <li><code>selectionGlowStrength</code> {Number}: The strength of the glow color imprint or spread when the edge is selected.
                                                                      The higher the value, the more color is imprinted and the stronger the contrast
                                                                      between the glow and the background.
                                                                      Valid values are 0 to 255. The default is 10.</li>
    <li><code>hoverOpacity</code> {Number}: The opacity of the edge when the mouse is over it (0 to 1).
                                                             The default value is the same one set to <code>opacity</code>.</li>
    <li><code>curvature</code> {Number}: The curvature amount of curved edges. The default value is 18.</li>
    <li><code>sourceArrowShape</code> {{@link org.cytoscapeweb.ArrowShape}}: Shape name of source arrows. The default value is \"NONE\".</li>
    <li><code>targetArrowShape</code> {{@link org.cytoscapeweb.ArrowShape}}: Shape name of target arrows.
                                                                                               For directed edges, the default value is \"DELTA\".
                                                                                               For undirected ones, the default value is \"NONE\".</li>
    <li><code>sourceArrowColor</code> {String}: Color code of source arrows.
                                                                 The default value is the same one set to the edge <code>color</code> property.</li>
    <li><code>targetArrowColor</code> {String}: Color code of target arrows.
                                                                 The default value is the same one set to the edge <code>color</code> property.</li>
    <li><code>label</code> {String}: The text to be displayed as edge label. There is no default value or mapper for edge labels.</li>
    <li><code>labelFontName</code> {String}: Font name of edge labels. The default is \"Arial\".</li>
    <li><code>labelFontSize</code> {Number}: The point size of edge labels. The default size is 11.</li>
    <li><code>labelFontColor</code> {String}: Font color of edge labels. The default value \"#000000\".</li>
    <li><code>labelFontWeight</code> {String}: <code>normal</code> or <code>bold</code>. The default is \"normal\".</li>
    <li><code>labelFontStyle</code> {String}: <code>normal</code> or <code>italic</code>. The default is \"normal\".</li>
    <li><code>labelGlowColor</code> {String}: The color of the label glow. The default value is \"#ffffff\".</li>
    <li><code>labelGlowOpacity</code> {Number}: The alpha transparency of the label glow. Valid values are 0 to 1.
                                                                 The default value is 0 (totally transparent).</li>
    <li><code>labelGlowBlur</code> {Number}: The amount of blur for the label glow. Valid values are 0 to 255 (floating point).
                                                              The default value is 2. Values that are a power of 2 (such as 2, 4, 8, 16, and 32) 
                                                              are optimized to render more quickly.</li>
    <li><code>labelGlowStrength</code> {Number}: The strength of the imprint or spread. The higher the value, the more color 
                                                                  is imprinted and the stronger the contrast between the glow and the background.
                                                                  Valid values are 0 to 255. The default is 20.</li>
    <li><code>tooltipText</code> {String}: Static text or a text formatter for regular edge tool tips. 
                                                            A list with all the edge <code>data</code> attributes is displayed by default.</li>
    <li><code>mergeTooltipText</code> {String}: Static text or a text formatter for merged edge tool tips.
                                                                 A list with all the merged edge <code>data</code> attributes is displayed by default.</li>
    <li><code>tooltipFont</code> {String}: Font name of edge tool tips. The default font is \"Arial\".</li>
    <li><code>tooltipFontSize</code> {Number}: The point size of edge tool tips. The default value is 11.</li>
    <li><code>tooltipFontColor</code> {String}: Font color of edge tool tips. The default value is \"#000000\".</li>
    <li><code>tooltipBackgroundColor</code> {String}: Background color of edge tool tips. The default value is \"#f5f5cc\".</li>
    <li><code>tooltipBorderColor</code> {String}: Border color of edge tool tips. The default value is \"#000000\".</li></ul>";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Object";
                        
                        
                        $field->name = "global";
						$field->description = "<p>An object that defines global visual properties.</p>
<p>Remember that global properties do not accept visual mappers, because they cannot be associated with nodes/edges data attributes.
If you try to set a mapper to a global property, the mapper is simply ignored.</p>
<p>The possible global properties are:</p>
<ul class=\"options\"><li><code>backgroundColor</code> {String}: Background color of the network view (hexadecimal code).
                                                                The default value is \"#ffffff\".</li>
    <li><code>tooltipDelay</code>  {Number}: Number of milliseconds to delay before displaying the tooltip, when the cursor is over a node or edge.
                                                              The default value is 800 milliseconds.</li>
    <li><code>selectionFillColor</code> {String}: Fill color of the drag-selection rectangle. The default value is \"#8888ff\".</li>
    <li><code>selectionLineColor</code> {String}: Line color of the drag-selection border. The default value is \"#8888ff\".</li>
    <li><code>selectionFillOpacity</code> {Number}: Fill opacity of the drag-selection rectangle (0 to 1). The default value is 0.1.</li>
    <li><code>selectionLineOpacity</code> {Number}: Line opacity of the drag-selection border (0 to 1). The default value is 0.8.</li>
    <li><code>selectionLineWidth</code> {Number}: Line width of the drag-selection border. The default value is 1.</li></ul>";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
						
                        $field = new param();
                        
                        
                            $field->type = "Object";
                        
                        
                        $field->name = "nodes";
						$field->description = "<p>An object that defines visual styles for nodes.</p>
<p>The possible node properties are:</p>
<ul class=\"options\"><li><code>shape</code> {{@link org.cytoscapeweb.NodeShape}}: Node shape name. The default value is \"ELLIPSE\".</li>
    <li><code>size</code> {Number}: Node size, in pixels. The default value is 24.</li>
    <li><code>color</code> {String}: Fill color code of nodes. The default value is \"#f5f5f5\".</li>
    <li><code>borderColor</code> {String}: Border color of nodes. The default value is \"#666666\".</li>
    <li><code>borderWidth</code> {Number}: Border width of nodes. The default value is 1.</li>
    <li><code>opacity</code> {Number}: The node opacity (0 to 1). The default value is 0.8.</li>
    <li><code>selectionColor</code> {String}: The fill color of selected nodes.
                                                               The default value is the same one set to <code>color</code>.</li>
    <li><code>selectionBorderColor</code> {String}: The border color of selected nodes.
                                                                     The default value is the same one set to <code>borderColor</code>.</li>
    <li><code>selectionOpacity</code> {Number}: The opacity of selected nodes (0 to 1).
                                                                 The default value is the same one set to <code>opacity</code>.</li>
    <li><code>selectionBorderWidth</code> {Number}: The border width of selected nodes (0 to 1).
                                                                     The default value is the same one set to <code>borderWidth</code>.</li>
    <li><code>selectionGlowColor</code> {String}: The glow color of selected nodes.The default value is \"#ffff33\".</li>
    <li><code>selectionGlowOpacity</code> {Number}: The glow transparency of selected nodes. Valid values are 0 to 1.
                                                                     The default value is 0.6 (60% opaque).</li>
    <li><code>selectionGlowBlur</code> {Number}: The amount of blur for the selection glow. Valid values are 0 to 255 (floating point).
                                                                  The default value is 8. Values that are a power of 2 (such as 2, 4, 8, 16, and 32) 
                                                                  are optimized to render more quickly.</li>
    <li><code>selectionGlowStrength</code> {Number}: The strength of the glow color imprint or spread when the node is selected.
                                                                      The higher the value, the more color is imprinted and the stronger the contrast
                                                                      between the glow and the background.
                                                                      Valid values are 0 to 255. The default is 6.</li>
    <li><code>hoverOpacity</code> {Number}: The opacity of the node when the mouse is over it (0 to 1).
                                                             The default value is the same one set to <code>opacity</code>.</li>
    <li><code>hoverBorderColor</code> {String}: The border color when the mouse is over a node.
                                                                 The default value is the same one set to <code>borderColor</code>.</li>
    <li><code>hoverBorderWidth</code> {Number}: The node border width on mouse over.
                                                                 The default value is the same one set to <code>borderWidth</code>.</li>
    <li><code>hoverGlowColor</code> {String}: The node glow color on mouse over.
                                                               The default value is \"#aae6ff\".</li>
    <li><code>hoverGlowOpacity</code> {Number}: The node glow opacity on mouse over (0 to 1).
                                                                 The default value is 0, which means that there is no visible glow on mouse over.</li>
    <li><code>hoverGlowBlur</code> {Number}: The amount of blur for the mouse over glow. Valid values are 0 to 255 (floating point).
                                                              The default value is 8. Values that are a power of 2 (such as 2, 4, 8, 16, and 32) 
                                                              are optimized to render more quickly.</li>
    <li><code>hoverGlowStrength</code> {Number}: The strength of the glow color imprint or spread on mouse over.
                                                                  The higher the value, the more color is imprinted and the stronger the contrast
                                                                  between the glow and the background.
                                                                  Valid values are 0 to 255. The default is 6.</li>
    <li><code>label</code> {String}: The text to be displayed as node label. A Passthrough Mapper is created by default, 
                                                      and it displays the node <code>data.label</code> attribute value.</li>
    <li><code>labelFontName</code> {String}: Font name of node labels. The default is \"Arial\".</li>
    <li><code>labelFontSize</code> {Number}: The point size of node labels. The default size is 11.</li>
    <li><code>labelFontColor</code> {String}: Font color of node labels. The default value \"#000000\".</li>
    <li><code>labelFontWeight</code> {String}: <code>normal</code> or <code>bold</code>. The default is \"normal\".</li>
    <li><code>labelFontStyle</code> {String}: <code>normal</code> or <code>italic</code>. The default is \"normal\".</li>
    <li><code>labelHorizontalAnchor</code> {String}: The horizontal label anchor: 
                                                                      <code>left</code>, <code>center</code> or <code>right</code></li>
    <li><code>labelVerticalAnchor</code> {String}: The vertical label anchor: 
                                                                    <code>top</code>, <code>middle</code> or <code>bottom</code></li>
    <li><code>labelXOffset</code> {Number}: Horizontal distance of the label from the node border. 
                                                             If <code>labelHorizontalAnchor</code> is \"right\",
                                                             the distance is measured from the left side of the node, and
                                                             a negative offset displaces the label towards left.</li>
    <li><code>labelYOffset</code> {Number}: Vertical distance of the label from the node border. 
                                                             If <code>labelVerticalAnchor</code> is \"bottom\", 
                                                             the distance is measured from the top side of the node, and
                                                             a negative offset moves the label upper.</li>
    <li><code>labelGlowColor</code> {String}: The color of the label glow. The default value is \"#ffffff\".</li>
    <li><code>labelGlowOpacity</code> {Number}: The alpha transparency of the label glow. Valid values are 0 to 1.
                                                                 The default value is 0 (totally transparent).</li>
    <li><code>labelGlowBlur</code> {Number}: The amount of blur for the label glow. Valid values are 0 to 255 (floating point).
                                                              The default value is 8. Values that are a power of 2 (such as 2, 4, 8, 16, and 32) 
                                                              are optimized to render more quickly.</li>
    <li><code>labelGlowStrength</code> {Number}: The strength of the imprint or spread. The higher the value, the more color 
                                                                  is imprinted and the stronger the contrast between the glow and the background.
                                                                  Valid values are 0 to 255. The default is 20.</li>
    <li><code>tooltipText</code> {String}: Static text or a text formatter for node tool tips. 
                                                            A list with all the node <code>data</code> attributes is displayed by default.</li>
    <li><code>tooltipFont</code> {String}: Font name of node tool tips. The default font is \"Arial\".</li>
    <li><code>tooltipFontSize</code> {Number}: The point size of node tool tips. The default value is 11.</li>
    <li><code>tooltipFontColor</code> {String}: Font color of node tool tips. The default value is \"#000000\".</li>
    <li><code>tooltipBackgroundColor</code> {String}: Background color of node tool tips. The default value is \"#f5f5cc\".</li>
    <li><code>tooltipBorderColor</code> {String}: Border color of node tool tips. The default value is \"#000000\".</li></ul>";
					
					
					
					

						
						
						
						
						
			        
					    // add field x to class
					    $cls_info->fields[$field->name] = $field;
					
				
			

 /*<!-- ============================== method details ========================= -->	*/ 	
			
			
 /*<!-- ============================== event details ========================= -->		*/ 
			
?>