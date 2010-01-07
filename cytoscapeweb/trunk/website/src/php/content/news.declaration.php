<?php
    require_once("php/lib/magpierss/rss_fetch.inc");
    
    $content_style = "side";

    $no_of_rss_to_display = 7;
    $rss_home_link = "http://groups.google.com/group/cytoscapeweb-announce";
    $rss_news_feed_url = "http://groups.google.com/group/cytoscapeweb-announce/feed/rss_v2_0_msgs.xml";
    include_rss($rss_news_feed_url, "RSS news feed");

?>