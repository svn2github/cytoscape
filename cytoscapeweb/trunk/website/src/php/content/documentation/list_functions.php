<div class="left api">
    <?php
    
    // THIS IS THE ONLY FILE YOU NEED TO EDIT TO CHANGE HOW THE DOCUMENTATION IS PRINTED OUT
    // ALL THE INFORMATION FROM JSDOC IS ALREADY PUT IN THE cls_info STRUCT
    
    // output api functions given the selected catgory ($page_link) in $api
    
    $category_name = $page_link;
    $category = $latest_api->categories[$category_name];
    
    // get short class name for fake classes (don't want fakes to be fully qualified org.something)
    function short_cls_name($cls_name){
        global $api;
    
        if( array_key_exists($cls_name, $api->cls_name_to_cat_name) ){
            $cat = $api->categories[ $api->cls_name_to_cat_name[$cls_name] ];
            
            if( ! $cat->real_class ){
                $split = preg_split( '/\./', $cls_name );
                if( count($split) > 1 ){
                    return $split[ count($split) - 1 ];
                }
            }
        }
        
        return $cls_name;
    }
    
    function clsref_to_link($matches){
        $cls_name = $matches[1];
        
        global $api;
        
        if( array_key_exists($cls_name, $api->cls_name_to_cat_name) ){
            global $category_name;
            $cat = $api->cls_name_to_cat_name[$cls_name];
            
            if( $cat == $category_name ){
                return '<a class="class_link direct" href="#' . short_cls_name($cls_name) . '">' . short_cls_name($cls_name) . '</a>';
            } else {
                return '<a class="class_link" href="/documentation/' . $api->cls_name_to_cat_name[$cls_name] . '#' . short_cls_name($cls_name) . '">' . short_cls_name($cls_name) . '</a>';
            }
        } else {
            return short_cls_name($cls_name);
        }
    }
    
    // util for parse_links
    function fnref_to_link($matches){
        $cls_name = $matches[1];
        $fn_name = $matches[2];

        global $api;
        $cat = $api->cls_name_to_cat_name[$cls_name];
        
        global $category_name;
        if( $cat == $category_name ){
            return  '<a class="function_link direct" href="#' . $fn_name . '">' . $fn_name . '</a>';
        } else {
            return  '<a href="/documentation/' . $api->cls_name_to_cat_name[$cls_name] . '#' . $fn_name . '">' . short_cls_name($cls_name) . ' :: ' . $fn_name . '</a>';
        }
    }
    
    // {@link} => <a></a>
    function parse_links($str){
        $fn_links = preg_replace_callback( '/\{\@link\s*([a-z|A-Z|\.]+)\#([a-z|A-Z]+)\s*\}/i', "fnref_to_link", $str );
        
        $cls_links = preg_replace_callback( '/\{\@link\s*([a-z|A-Z|\.]+)\s*\}/i' , "clsref_to_link", $fn_links);
        
        return $cls_links;
    }
    
    // util for parse_links
    function parse_clsrefs($str){
        $matches = array();
        $matches[1] = $str;
        
        return clsref_to_link($matches);
    }
    
    // fix missing/unmatched <p> with ``Name (Type) : ''
    function parse_para($str, $pre=""){

        if( preg_match( '/<p>/i', $str ) > 0 ){
            return  '<p>' . $pre . preg_replace( '/<p>/i', '', $str, 1 );
        } else {
            return '<p>' . $pre . $str . '</p>';
        }
    }
    
    foreach($category->files as $file){
        
        // include $cls_info generating php file
        require($file);
        
        if( ! $category->real_class ){
            $cls_name = $cls_info->name;
            echo '<h1 id="' . short_cls_name($cls_name) . '" name="' . short_cls_name($cls_name) . '">' . short_cls_name($cls_name) . '</h1>';
        }
        
        $functions = $cls_info->funcs;
        if( count($functions) > 0 ){
            foreach($functions as $fn_name => $fn) {
                
                $parameters = $fn->params;
                
                if( $category->real_class ){ 
                    echo "<h1 id=\"$fn_name\" name=\"$fn_name\">" . $fn_name . "<span class=\"arguments\">&nbsp;( ";
                    
                        $num_params = count($parameters);
                    
                        $current_param_num = 1;
                        if( $num_params > 0 ) {
                            foreach($parameters as $param_name => $param) {
                                
                                echo ($param->optional ? "[&nbsp;" : "");
                                echo "$param_name";
                                echo ($param->optional ? "&nbsp;]" : "");
                                
                                if($current_param_num < $num_params) {
                                    echo ", ";
                                }
                                
                                $current_param_num++;
                            }
                        }
                    
                    echo "&nbsp;)</span></h1>";
                }
                
                $description = $fn->description;
                if( $description ) {
                    echo "<div class=\"description\">" . parse_para( parse_links($description) ) . "</div>";
                }
                
                echo "<div class=\"details\">";
                
                    if( $num_params > 0 ) {
                        echo "<label>Parameters</label>";
                        foreach($parameters as $param_name => $param) {
                            
                            $descr = "";
                            
                            echo "<div class=\"parameter\">";
                                $descr .= ($param->optional ? "[ " : "");
                                $descr .= "<em>$param->name</em>";
                                $descr .= ($param->optional ? " ]" : "");
                                $descr .= ($param->type ? " <span class=\"type\">{" . parse_clsrefs($param->type) . "}</span>" : "");
                                if ($param->description){
                                    $descr .= " : ";
                                }
                                echo parse_para( parse_links($param->description), $descr );
                            echo "</div>";
                                 
                        }
                    }
                    
                    if($category->real_class){
                    
                        $return_value = $fn->return_value;
                        echo "<label>Return value</label>";
                        if( $return_value ) {
                            $descr = ($return_value->type ? "{" . parse_clsrefs($return_value->type) . "} " : "");
                            
                            echo "<div class=\"return_value\">";
                            echo parse_para( parse_links($return_value->description), $descr ); 
                            echo "</div>";
                        } else {
                            echo "<div class=\"return_value\"><p>void</p></div>"; 
                        }
                    
                    }
                    
                    
                    $fields = $cls_info->fields;
                    if( $fn->is_constructor && count($fields) > 0 ){
                        echo "<label>Fields</label>";
                        echo "<div class=\"fields\">";
                        
                            foreach($fields as $field){
                                echo "<div class=\"field\">";
                                    $descr = "";
                                    $descr .= "<em>$field->name</em>";
                                    $descr .= ($field->type ? " <span class=\"type\">{" . parse_clsrefs($field->type) . "}</span>" : "");
                                    $descr .= ($field->description ? " : " : "");
                                    
                                    echo parse_para( parse_links($field->description), $descr );
                                echo "</div>";
                            }
                            
                        echo "</div>";
                    }
                    
                    $examples = $fn->examples;
                    if( count($examples) > 0 ) {
                        echo "<label>Examples</label>";
                        echo "<div class=\"examples\">";
                            
                            foreach($examples as $example){
                                if( preg_match('/\&lt\;\s*html\s*\&gt\;/', $example) ){
                                    echo "<pre class=\"example ln-\"><code class=\"html\">$example</code></pre>";
                                } elseif( preg_match('/\&lt\;\?php\s*/', $example) ){
                                    echo "<pre class=\"example ln-\"><code class=\"php\">$example</code></pre>";
                                } else {
                                    echo "<pre class=\"example ln-\"><code class=\"js\">$example</code></pre>";
                                }
                            }
                            
                        echo "</div>";
                    }
                    
                    $see_list = $fn->see;
                    if( count($see_list) > 0 ) {
                        echo "<label>See Also</label>";
                        echo "<ul class=\"see_also\">";
                            
                            foreach($see_list as $see){
                                echo "<li>" . parse_links("{@link ".$see."}") . "</li>";
                            }
                            
                        echo "</ul>";
                    }
                    
                echo "</div>";
                    
                
            
            } // for each function
        } // if fuctions
    } // for each file

    ?>
</div>

<?php if($content_style == "half_and_half"){ ?>
<div class="right">
    <div id="example"></div>
</div>
<?php } ?>