<?php
    require_once("php/content/documentation/function.php");
    require_once("php/content/documentation/api.php");
    
    $version = "";
    $date = "";
    
    foreach($apis as $ver_name => $api){
        if($api->version > $version){
        	$version = $api->version;
        	$date = $api->date;
        }
    }

    include_js("/js/cytoscape_web/json2.min.js");
    include_js("/js/cytoscape_web/AC_OETags.min.js");
    include_js("/js/cytoscape_web/cytoscapeweb.min.js");
    include_js("/js/cytoscape_web/cytoscapeweb-styles-demo.js");
?>