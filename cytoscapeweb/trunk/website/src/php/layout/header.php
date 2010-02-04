<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html>
    <head>
        <?php
            $title_text = $site_title;
            
            if( $navigation_link != "" && is_array($navigation_links) && array_key_exists($navigation_link, $navigation_links) ){
                $title_text = $title_text . " " . $title_separator . " " . $navigation_links[$navigation_link];
            }
            
            if( $page_link != "" && is_array($page_links) && array_key_exists($page_link, $page_links) ){
                $title_text = $title_text . " " . $title_separator . " " . $page_links[$page_link];
            }
        ?>
    
        <title><?php echo $title_text; ?></title>
        
        <link rel="shortcut icon" href="/img/layout/favicon.png" />
        
        <?php 
            foreach ( $css_includes as $inc ) {
                echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"" . $inc . "\" />\n";
            }
        ?>
        
        <!--[if IE]>
        <?php 
            foreach ( $ie_css_includes as $inc ) {
                echo "<link rel=\"stylesheet\" type=\"text/css\" href=\"" . $inc . "\" />\n";
            }
        ?>
        <![endif]-->
        
        <?php 
            foreach ( $js_includes as $inc ) {
                echo "<script type=\"text/javascript\" src=\"" . $inc . "\"></script>\n";
            }
        ?>
        
        <!--[if IE]>
        <?php 
            foreach ( $ie_js_includes as $inc ) {
                echo "<script type=\"text/javascript\" src=\"" . $inc . "\"></script>\n";
            }
        ?>
        <![endif]-->

        
        <?php
        foreach( $rss_includes as $inc => $name ) {
           echo "<link href=\"$inc\" type=\"application/rss+xml\" rel=\"alternate\" title=\"$name\" />";
        }
        ?>
	</head>
	
    <body>
	
        <div id="header" class="slice">
            <a href="/"><div id="logo"></div></a>
            
            <div id="navigation_links">
                <ul>
                    <?php
                        foreach( $navigation_links as $link => $name) {
                            echo "<li" . ( ($navigation_link == $link) ? (" class=\"selected\" ") : ("") ) . "><a href=\"" . get_link($link) . "\">$name</a></li>\n";   
                        }
                    ?>
                </ul>
            </div>
            
            <form action="http://www.google.com/cse" id="cse-search-box">
                <input type="hidden" name="cx" value="016016542103382164689:dyea8plfovk" />
                <input type="hidden" name="ie" value="UTF-8" />
                <div id="search">
                        <div id="search_input">
                            <input type="text" name="q" size="25" value="Search this site via Google" />
                        </div>
                        <button id="search_button" name="sa"></button>
                </div>
            </form>
            
            <div id="copyright"> &copy;
                <?php
                    $year = date("Y");
                    
                    if( $year == $first_year_of_project_release ){
                        echo $year;
                    } else {
                        echo $first_year_of_project_release . '&ndash;' . $year;
                    }
                ?>
            </div>
            
        </div>
		  
        <div id="page" class="slice">
            
            <?php if( count($page_links) > 0 ) { ?>
            <div id="page_navigation">
                <ul>
                    <?php
                        foreach ( $page_links as $link => $name ) {
                            echo "<li" . ( ($link == $page_link) ? (" class=\"selected\" ") : ("") ) . "><a href=\"" . get_link($navigation_link, $link) . "\">" . $name . "</a></li> ";   
                        }
                    ?>
                   </ul>
            </div>
            <?php } ?>
            
            <!-- begin page content -->
            <div id="content" class="<?php echo $content_style; ?>">
            
            <?php
            
                if( $content_style =="side" ) {
                    echo "<div class=\"right\">";
                        echo "<div class=\"nav\"></div>";
                    echo "</div>";
                }
            
            ?>