<?php

    // TODO get this region from the local machine
    date_default_timezone_set("America/Toronto");

    define(DEBUG_MESSAGES_ENABLED, false);

    function debug_warning($msg){        
        if( DEBUG_MESSAGES_ENABLED ) {
            trigger_error($msg, E_USER_NOTICE);
            debug_print_backtrace();
        }
    }
    
    function debug_error($msg){
        global $enable_debug_messages;
        
        if( DEBUG_MESSAGES_ENABLED ) {
            trigger_error($msg, E_USER_ERROR);
            debug_print_backtrace();
        }
    }
    
    function get_link($nav="", $page="") {
        if( $page == "" ) {
            return "/$nav";
        } else {
            return "/$nav/$page";
        }
    }
    
    function has_ie_in_name($file){
        return preg_match('/\.ie([0-9])*\./', $file);
    }
    
    function push_js($file){
        global $js_includes;
        global $ie_js_includes;
        
        if( has_ie_in_name($file) ){
            $ie_js_includes[] = $file;
        } else {
            $js_includes[] = $file;
        }
    }
    
    function include_js($file, $backup=""){
        
        // fall back on backup if external file does not exist
        if( preg_match('/^([a-z|A-Z])+:\/\/(.)+$/', $file) ) {
        
            if( $fp = @fopen($file, 'r') ){
                push_js($file, $browser);
                fclose($fp);
            } else if( file_exists( substr($backup, 1) ) ) {
                push_js($backup, $browser);
            } else {
                debug_warning("JS include ($file) does not exist and backup ($backup) does not either");
            }
            
            
        } else {
            if( file_exists( substr($file, 1) ) ) {
                push_js($file, $browser);
            } else {
                debug_warning("JS file ($file) does not exist");
            }
        }
        
    }
    
   
    
    function push_css($file){
        global $css_includes;
        global $ie_css_includes;
        
        if( has_ie_in_name($file) ){
            $ie_css_includes[] = $file;
        } else {
            $css_includes[] = $file;
        }
    }
    
    function include_css($file, $browser=""){
        if( file_exists( substr($file, 1) ) ) {
            push_css($file, $browser);
        } else {
            debug_warning("CSS file ($file) does not exist");
        }
    }
    
    function include_rss($file, $description){
        global $rss_includes;
        $rss_includes[$file]=$description;
    }
    
    function get_download_url($version){
        return "/file/lib/cytoscapeweb_v$version.zip";
    }
    
?>