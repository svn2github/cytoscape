<html>

    <head>
        <title> $title$ </title>

        <script type="text/javascript" src="/js/json2.min.js"></script>
        <script type="text/javascript" src="/js/AC_OETags.min.js"></script>
        <script type="text/javascript" src="/js/cytoscapeweb.min.js"></script>
	<script type="text/javascript" src="/js/jquery-1.6.min.js"></script>

	<script>
			\$(document).ready(function(){

				var div_id = "cytoscapeweb";

				var options = {
                    swfPath: "/swf/CytoscapeWeb",
                    flashInstallerPath: "/swf/playerProductInstall"
                };

				var vis = new org.cytoscapeweb.Visualization(div_id);
				var url = "files" + $network$;

                \$.get(url, function(data) {
                    if (typeof data !== "string") {
                        if (window.ActiveXObject) {
                            data = data.xml;
                        } else {
                            data = (new XMLSerializer()).serializeToString(data);
                        }
                    }
                vis.draw({ network: data });

			}
		</script>

        <body>
        <h1>$title$</h1>
	<div id="cytoscapeweb" style="width:600px;height:400px;border:solid;border-width:1px;"></div>
        </body>

</html>
		