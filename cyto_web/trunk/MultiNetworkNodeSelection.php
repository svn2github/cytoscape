<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content=
    "text/html; charset=ISO-8859-1">
    <title>
    Cytoscape Online Plugin Tutorial
    </title>
    <link rel="stylesheet" type="text/css" media="screen" href=
    "css/cytoscape.css">
    <link rel="shortcut icon" href="images/cyto.ico">
  </head>
  <body bgcolor="#FFFFFF">
    <table id="feature" border="0" cellpadding="0" cellspacing=
    "0" summary="">
      <tr>
        <td width="10">

        </td>
        <td valign="center">
          <h1>Cytoscape Online Plugin Tutorial</h1>
        </td>
      </tr>
    </table>
    <? include "nav.php"; ?>
    <? include "nav_plugintut.php"; ?>
    <div id="indent">

<center>
<h2>Multi-Network Node Selection Cytoscape Plugin</h2>
</center>

<p>
This plugin works with multiple networks. When active, it links all
Cytoscape networks so that any node or edge that is flagged in one
network is flagged in all networks. In Cytoscape, the flagged state of
an object in a network is synchronized with the selection state of the
corresponding object in any view of that network. A graph object can
also be flagged in a network that has no view (useful for marking
objects as interesting to other plugins). Thus, this plugin allows the
user or a plugin to flag or select nodes in one network or view and
have them automatically flagged in all networks and selected in all
views that also have those graph objects.
<p>
This plugin illustrates three different types of Cytoscape event
handling. It captures events fired by any network's flagger to update
the flagged state in all networks. It catches graph change events to
handle nodes or edges that are hidden. Finally, it catches network
creation events to synchronize the new network with the flag/selection
state of all the other networks.
</p>
<p>
To see the plugin in action, run Cytoscape and load the plugin and a
network. Create a subnetwork of the first network by selecting some
nodes and using the <code>Select-&gt;To New Window-&gt;Selected Nodes, 
All Edges</code> menu option, then create a view on this new network with 
the <code>Edit-&gt;Create View</code> menu option. You should see that 
objects selected in one view are also selected in the other view if 
they exist in both views.
</p>

<p>
<a href="pluginTutorial/java/MultiNetworkNodeSelection.java">MultiNetworkNodeSelection.java</a><br>
<a href="pluginTutorial/java/MultiNetworkNodeSelection.jar">MultiNetworkNodeSelection.jar</a>
</p>

<!-- <p>This page last modified August 25, 2004.</p> -->

    </div>
    <? include "footer.php"; ?>
  </body>
</html>
