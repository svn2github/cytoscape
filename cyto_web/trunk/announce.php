<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<? include "config.php"; ?>

<html>

	<head>
		<title>Cytoscape Announcements</title>
	</head>

	<body>
<div id="container">
<div id="rightbox">
<?
			if ($news_option == "atom") {
				include "feed.php";
			}
?> 
</div>
</div>
</body>

</html>
