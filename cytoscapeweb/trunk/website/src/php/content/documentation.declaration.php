<?php
    require_once("php/content/documentation/api.php"); // get api versions
    
    $latest_api = null;
    foreach($apis as $version => $api){
        $latest_api = $api;
        break;
    }
    
    $page_links = array(
        "tutorial" => "Tutorial"
    );
    
    if($page_link != "tutorial" && $page_link != "") { 
        $include = "php/content/documentation/list_functions.php";
        //include_js("/js/content/api.js");
        //include_js("/js/jquery/jquery-ui-1.7.2.custom/jquery-ui-1.7.2.custom.min.js");
    }
    
    // use half_and_half if api.js enabled for demos of each function
    $content_style = "side";
    //$content_style = "half_and_half";

    // code formatting    
    include_js("/js/jquery/plugins/chili/jquery.chili-2.2.js");
    include_js("/js/jquery/plugins/chili/recipes.js");

    // make page links based on tags (but make them nice with spaces instead of _ and capitalise the
    // first letter, etc
    foreach($latest_api->categories as $category_name => $category){
        $formatted_cat = strtoupper( substr($category_name, 0, 1) ) . strtolower( substr($category_name, 1) );
        $formatted_cat = str_replace("_", " ", $formatted_cat);
        
        if( $formatted_cat == "Cytoscape web" ){
            $formatted_cat = "Cytoscape Web";
        }
        
        if( !array_key_exists($category_name, $page_links) ){
            $page_links[$category_name] = $formatted_cat;
        }
    }


?>