<table width=100%>
<TR><TD>
<div class="roundbox blue">
<?
require_once 'magpie/rss_fetch.inc';

$url = 'http://groups-beta.google.com/group/cytoscape-announce/feed/msgs.xml';
$rss = fetch_rss($url);

echo "<A HREF='http://groups-beta.google.com/group/cytoscape-announce'>";
echo "Cytoscape Announcements:</A></B><P>\n";
foreach ($rss->items as $item ) {
	$name = $item["author_name"];
	$summary = $item[summary];
	$summary2 = str_replace("<BR>", "\n", $summary);
	$date = $item[issued];
	$title = $item[title];
	$url   = $item[link];
	echo "<a href=$url>$title</a></li><BR>\n";
	echo " <UL><LI>$summary2</UL>";
	
	#print_r(array_keys($item));
	echo "<BR>";
}
?>
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


