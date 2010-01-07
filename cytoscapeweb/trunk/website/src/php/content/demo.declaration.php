<?php

    $content_style="half_and_half";
    
    // include css
    $handle = @fopen("php/content/demo/css_includes.txt", "r");
    if ($handle) {
        while (!feof($handle)) {
            $line = trim( fgets($handle) );
            
            if($line != ""){
                include_css($line);
            }
        }
        fclose($handle);
    }
    
    // include js
    $handle = @fopen("php/content/demo/js_includes.txt", "r");
    if ($handle) {
        while (!feof($handle)) {
            $line = trim( fgets($handle) );
            
            if($line != ""){
                include_js($line);
            }
        }
        fclose($handle);
    }

?>