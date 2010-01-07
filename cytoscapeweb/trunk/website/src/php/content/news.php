<div class="left">
    
    <h1>Subscription</h1>
    
    <p>The news feed contains updates related to Cytoscape Web as posted on <a href="<?php echo $rss_home_link; ?>">the Cytoscape Web Google group</a>.  The latest entries are posted here.</p>
    
    <p>Subscribing to the news feed allows you to receive automatic email updates whenever a new entry is posted.</p>
    
    <form action="http://groups.google.com/group/cytoscapeweb-announce/boxsubscribe">
        <label>Email</label>
        <input type="text" size="30" name="email">
        <button type="submit" name="sub">Subscribe</button>
    </form>
    
    <?php
    
	
	$rss = fetch_rss( $rss_news_feed_url );
	
	$displayed = 0;
	foreach ($rss->items as $item) {
		$href = $item['link'];
		$title = $item['title'];
		$author = $item['author'];
		$link = $item['link'];
		$summary = $item['summary'];
		$date_raw = $item['pubdate'];
		list($usable_date, $extra) = split("UT", $date_raw);
		$time_stamp = strtotime("$usable_date");
		$date_formatted = date("l, j F Y \\a\\t H:i", $time_stamp);
		
		echo "<h1>$title</h1>\n";
		echo "<label class=\"date\">$date_formatted</label>";
		echo "<label class=\"author\">By $author</label>";
		echo "<div class=\"entry\">$summary ...</div>";
		echo "<div class=\"read_more\"><a href=\"$link\">Read full news posting</a></div>";
		
		$displayed++;
		if( $displayed >= $no_of_rss_to_display ) {
		    break;
        }
    }
	echo "</ul>";
    
    ?>
    
</div>