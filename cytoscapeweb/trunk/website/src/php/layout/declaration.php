<?php 

    // common functions
    include_once("php/layout/functions.php");

    // page title
    $site_title = "Cytoscape Web";
    $title_separator = "&#187;";
    
    $first_year_of_project_release = 2009;
    
    // includes
    $css_includes = array();
    $ie_css_includes = array();
    $js_includes = array();
    $ie_js_includes = array();
    $rss_includes = array();
    $include = "";
    
    // navigation
    $navigation_link = ($_GET["id"] == "") ? ("home") : ($_GET["id"]);
	$navigation_links = array(
        "download" => "Download",
        "documentation" => "Documentation",
        "demo" => "Demo",
        "news" => "News",
        "about" => "About",
        "contact" => "Contact"
    );
    $page_link = $_GET["page"];
    
	// page content style
	$content_style = ($content_style == "") ? ("full") : ($content_style);
	
	// common js
	include_js("/js/jquery/jquery-1.3.2.min.js");
	include_js("/js/layout/layout.js");
	
	// common css
	include_css("/css/layout.css");
    include_css("/css/content.css");
	
	// navigation declaration
    $navigation_declaration = "php/content/$navigation_link.declaration.php";
    if( file_exists($navigation_declaration) ) {
        include_once($navigation_declaration);
    }
    
    // revise page
	if( count($page_links) > 0 && $page_link == "" && $include == "" ) {
	    reset( $page_links );
		$page_link = key( $page_links );
	}
	
	// page declaration
	$page_declaration = "php/content/$navigation_link/$page_link.declaration.php";
	if( file_exists($page_declaration) ) {
	    include_once($page_declaration);
	}
	
	// include
	if($include == "") {
	    $include = "php/content/$navigation_link" . (($page_link != "") ? ("/$page_link") : ("")) . ".php";
	}
	
	switch( $content_style ) {
	    case "side":
	        include_js("/js/layout/side.js");
	        break;
	}
	
	// automatic css and js includes
    include_css("/css/content/$navigation_link.css");
    include_css("/css/content/$navigation_link.ie.css");
    include_js("/js/content/$navigation_link.js");
    if( $page_link != "" ) {
        include_css("/css/content/$navigation_link/$page_link.css");
        include_css("/css/content/$navigation_link/$page_link.ie.css");
        include_js("/js/content/$navigation_link/$page_link.js");
        include_js("/js/content/$navigation_link/$page_link.ie.js");
    }
	
	// remove duplicates from css, js, rss includes
	$js_includes = array_unique($js_includes);
	$css_includes = array_unique($css_includes);
	$ie_js_includes = array_unique($ie_js_includes);
	$ie_css_includes = array_unique($ie_css_includes);
	$rss_includes = array_unique($rss_includes);
	
?>