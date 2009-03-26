<table width=100%>
  <TR>
    <TD><div class="roundbox blue">
        <?
require_once 'magpie/rss_fetch.inc';

#$url = 'http://groups.google.com/group/cytoscape-announce/feed/rss_v2_0_msgs.xml';
$url = 'http://cytoweb.ncibi.org/rss_v2_0_msgs.xml';
$rss = fetch_rss($url);

echo "<h2><A HREF='http://groups.google.com/group/cytoscape-announce'>";
echo "Cytoscape Announcements:</A></h2>\n";
#  Only show the three most recent items.
$counter = 0;
foreach ($rss->items as $item ) {
	$showNewsItem = true;
	if ($counter < 3) {
		$name = $item["author_name"];
		$summary = $item[summary];
		$summary2 = str_replace("<br>", "\n", $summary);
		$title = $item[title];
		$url   = $item[link];

		$date = $item[pubdate];

		list($usable_date, $extra) = split("T", $date);
		$time_stamp = strtotime("$usable_date");
		$date_formatted = date("F j, Y", $time_stamp);

		# Temporary Hack to Remove Non-Approved Items from the Home Page
		# Hack created by Ethan Cerami, April 6, 2006
		if ($title=="Re: Announcing BioNetBuilder BETA" 
			|| $title =="Douglas Selinger is out of the office.") {
			$showNewsItem = false;
		}

		if ($showNewsItem == true) {
			echo "<a href=$url>$title</a></li>.&nbsp;&nbsp;$date_formatted<BR>\n";
			echo " <UL><LI>$summary2 [cont.]</UL>";
			echo "<BR>";
		}
	}
	if ($showNewsItem == true) {
		$counter++;
	}
}
echo "<A HREF='http://groups-beta.google.com/group/cytoscape-announce'>";
echo "View All Announcements</A></B><P>\n";
?>
        <form action="http://groups-beta.google.com/group/cytoscape-announce/boxsubscribe">
          Subscribe to cytoscape-announce:
          <P> Email:
            <input type=text name=email>
            <input type=submit name="sub" value="Subscribe">
        </form>
      </div></TD>
  </TR>
</TABLE>
