<table width=100%>
<TR><TD>
<div class="roundbox blue">
<?
require_once 'magpie/rss_fetch.inc';

$url = 'http://groups-beta.google.com/group/Cytoscape/feed/msgs.xml';
$rss = fetch_rss($url);

echo "<B>" ,"<A HREF='http://groups-beta.google.com/group/Cytoscape'>";
echo "Cytoscape News:</A></B><P>\n";
foreach ($rss->items as $item ) {
	$name = $item["author_name"];
	$summary = $item[summary];
	$summary2 = str_replace("<BR>", "\n", $summary);
	$date = $item[issued];
	$title = $item[title];
	$url   = $item[link];
	echo "<a href=$url>$title</a></li> ($name, $date) <br>\n";
	echo " <UL><LI>$summary2</UL>";
	
	#print_r(array_keys($item));
	echo "<BR>";
}
?>
</div>
</TD>
</TR>
</TABLE>