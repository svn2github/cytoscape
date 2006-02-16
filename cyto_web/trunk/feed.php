<table width=100%>
<TR><TD>
<div class="roundbox blue">
<?
require_once 'magpie/rss_fetch.inc';

$url = 'http://groups.google.com/group/cytoscape-announce/feed/rss_v2_0_msgs.xml';
$rss = fetch_rss($url);

echo "<A HREF='http://groups.google.com/group/cytoscape-announce'>";
echo "Cytoscape Announcements:</A></B><P>\n";
#  Only show the three most recent items.
$counter = 0;
foreach ($rss->items as $item ) {
	if ($counter < 3) {
		$name = $item["author_name"];
		$summary = $item[summary];
		$summary2 = str_replace("<br>", "\n", $summary);
		$title = $item[title];
		$url   = $item[link];

		// Parse and format the date directly;
		// strtotime() does not work
		$date = ("$item[updated]");
		list($usable_date, $extra) = split("T", $date);
		list($year, $month, $day) = split ("-", $usable_date);
		$time_stamp = strtotime("$month/$day/$year");
		$date_formatted = date("F j, Y", $time_stamp);

		echo "<a href=$url>$title</a></li>.&nbsp;&nbsp;$date_formatted<BR>\n";
		echo " <UL><LI>$summary2 [cont.]</UL>";

		#print_r(array_keys($item));
		echo "<BR>";
	}
	$counter++;
}
echo "<A HREF='http://groups-beta.google.com/group/cytoscape-announce'>";
echo "View All Announcements</A></B><P>\n";
?>
<P>
	<P>
	<form action="http://groups-beta.google.com/group/cytoscape-announce/boxsubscribe">
		<div class="item">
		Subscribe to cytoscape-announce:
		<P>
		Email: <input type=text name=email>
       		<input type=submit name="sub" value="Subscribe">
	</form>
</div>
</TD>
</TR>
</TABLE>


