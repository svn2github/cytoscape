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
<h2>Neighbor Node Selection Cytoscape Plugin</h2>
</center>

<p>
This example plugin uses the Cytoscape network and attributes 
data structures to perform a simple node selection operation. 
When the user selects one or more nodes in the graph and
then activates the plugin, it will iterate over each selected node and
additionally select all neighbors of that node.
</p>
<p>
To run the plugin, save the jar file below to your local
disk. Then run Cytoscape and load in a sample yeast network (for
example, <code>galFiltered.sif</code>
in the testData directory of the public Cytoscape distribution). Then,
load the plugin as before. Select one or more nodes, then activate the
plugin via the <code>Plugins-&gt;SamplePlugin</code> menu option.</p><p>


<a href="pluginTutorial/java/NeighborNodeSelection.java">NeighborNodeSelection.java</a><br>
<a href="pluginTutorial/java/NeighborNodeSelection.jar">NeighborNodeSelection.jar</a></p><p>

</p>

<h3>Looking at the Plugin</h3>

<pre><code>
    public NeighborNodeSelection() {
        NeighborNodeSelectionAction action = new NeighborNodeSelectionAction();
        action.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
    }
 </code></pre>

<p>
The plugin constructor adds an entry to the menus of the window. This is a
very common operation, giving the user the ability to execute the plugin on
command. A plugin could add more than one menu entry, if it provided more than
one possible operation. What actually happens is that the plugin defines an
extension of the CytoscapeAction class, which is an extension of the Swing
AbstractAction class. The code in this extension class gets called whenever
the user selects the item that appears in the menu. (See the
<a href="http://java.sun.com/docs/books/tutorial/uiswing/index.html">Swing section</a> 
of the Java tutorial and the
<a href="http://java.sun.com/j2se/1.4.2/docs/api/index.html">Java API</a>
for more information).
</p>
<p>
The plugin works with several core objects. The CyNetwork object is a
graph and also contains the associated data. The plugin uses the
network to find the neighbors of the currently selected nodes.
The CyNetworkView contains information on what node views are 
currently selected.
</p>

<p>
At the end of the algorithm, the plugin calls the <code>redrawGraph</code>
method on the window. This is required to let Cytoscape know that it should
redraw the graph (for example, to update the appearance of newly selected
nodes).
</p>

<!-- <p> This page last modified August 25, 2004. </p> -->


    </div>
    <? include "footer.php"; ?>
  </body>
</html>
