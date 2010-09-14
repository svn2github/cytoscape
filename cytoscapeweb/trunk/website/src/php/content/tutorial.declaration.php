<?php

    $content_style="side";
    
    // code formatting    
    include_js("/js/jquery/plugins/chili/jquery.chili-2.2.js");
    include_js("/js/jquery/plugins/chili/recipes.js");
    
    function escape_html($str){
        $without_lt = preg_replace( '/</', '&lt;', $str );
        $without_gt = preg_replace( '/>/', '&gt;', $without_lt );
        
        return $without_gt;
    }
    
    function print_code($file){       
        echo '<div class="code"><pre class="ln-"><code class="html">';
        
        $fh = fopen($file, "r");
        
        if( $fh ){
            while( $line = fgets($fh) ){
                echo escape_html($line);
            }
            fclose($fh);
        }
        
        echo '</code></pre></div>';
    }
    
    function embed_code($file){
        echo '<iframe src=/' . $file . '></iframe>';
    }

?>