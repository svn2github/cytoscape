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
<h2>Cytoscape Plugin Tutorial</h2>
</center>


Cytoscape allows programmers to easily write plugins that access the core
data structures and windows of Cytoscape to do a wide variety of interesting
operations. This tutorial explains how to write a plugin and how to get
Cytoscape to load and use your plugin.<p>

It's assumed that the reader is familiar with the basics of the Java
programming language and has some kind of programming environment available.
See
<a href="http://java.sun.com/docs/books/tutorial/index.html">the Java Tutorial</a> 
for an excellent introduction and handy reference guide for Java.
You'll also want to check out the
<a href="http://www.cbio.mskcc.org/cytoscape/alpha/javadoc/index.html">Cytoscape core API documentation</a>.
</p>

<p>
<a href="#HelloWorld">'Hello World' as a Cytoscape plugin</a><br>
<a href="#CytoscapePlugin">The CytoscapePlugin class</a><br>
<a href="#SamplePlugin">A simple data analysis plugin</a><br>
<a href="#NetworkLinker">A plugin illustrating multiple networks and event handling</a><br></p><p>
Each section includes the Java source code and a jar file containing
both the source and the compiled classes. To run Cytoscape via Java Web
Start with all of these plugins automatically loaded, click here: 
<a href="pluginTutorial/webStart/cy.jnlp">WEB START</a>. 
(See the <a href="http://cytoscape.systemsbiology.net/Cytoscape2.0/user/JavaWebStart/index.html">
 Cytoscape Web Start documentation</a> for information on running Cytoscape via Java Web Start).
</p><hr>

<h3><a name="HelloWorld">'Hello World' as a Cytoscape plugin</a></h3>

Here's a simple 'Hello, world' example as a Cytoscape plugin (we've
used a Swing utility class to display the message in a dialog window):
<p>

</p>
<pre><code>
import javax.swing.JOptionPane;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.Cytoscape;

public class HelloWorldPlugin extends CytoscapePlugin {

    public HelloWorldPlugin () {
        String message = "Hello World!";
        System.out.println(message);
        // use the CytoscapeDesktop as parent for a Swing dialog
        JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
    }
}
</code></pre><p>

</p><h4>Looking at the Plugin:</h4>

<b>Imports:</b><br>
<code></code><pre>
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
</pre><p>

</p><ol>
<li>All plugins must extend the class <code>cytoscape.plugin.CytoscapePlugin</code>. 
The specific class that extends <code>CytoscapePlugin</code>
doesn't need to stand alone; it can reference any number of other
classes and libraries. This "main" class is simply Cytoscape's entry
point to your code.
<p>
</p></li><li>All plugins must have a default constructor (that is, a
constructor with no arguments). Cytoscape will call this constructor to
create an instance of your plugin class.<p>
</p></li><li>The static class <code>cytoscape.Cytoscape</code> provides
access the data objects and utilities of the Cytoscape core. Here, the
plugin gets a reference to a window created by Cytoscape for a parent
of a new dialog window.
</li></ol><p>

<a href="pluginTutorial/java/HelloWorldPlugin.java">HelloWorldPlugin.java</a> <br>
<a href="pluginTutorial/java/HelloWorldPlugin.jar">HelloWorldPlugin.jar</a></p><p>To
load this plugin into Cytoscape, first save the jar file at the above
link to your local disk. Then start Cytoscape and select the 
<code>Plugin-&gt;Load Plugin from Jar File</code>
menu option. Use the file browser to find and select this jar file;
Cytoscape will then load the plugin from the jar. You should see the
"Hello, world" dialog appear above your main Cytoscape window.</p><p>
If you run Cytoscape using one of the scripts that comes with the
standard Cytoscape distribution, you can place the jar file in the
plugins directory of the cytoscape distribution, and Cytoscape will find it
automatically. In this case you won't need to load the plugin via the
menu.</p><p>

</p><hr>

<h3><a name="CytoscapePlugin">The CytoscapePlugin class</a></h3>

The CytoscapePlugin class is very simple. It defines a default constructor and 
one instance method: <code>describe</code>,
which can be overridden to describe what the plugin does. (A static
method also exists that is used by Cytoscape to load plugins). Since
the constructor takes no arguments, it is not necessary to explicitly
call a parent constructor from your plugin's constructor. The only
requirement is that your plugin must have a default (no arguments)
constructor, as Cytoscape will call this constructor to instantiate
your plugin.<p>

Java only allows a class to inherit from one parent. Since every plugin
must extend CytoscapePlugin, this seems to be a severe limitation. The way
around this is to define your Cytoscape plugin class as a simple wrapper
around your real code. For example:</p><p>

</p><pre><code>
public class PluginWrapper extends CytoscapePlugin {

    public PluginWrapper() {
        RealPlugin plugin = new RealPlugin();
    }
}
</code></pre><p>

This scheme can also be used to link to more complicated services, like a
web server, or to connect to code written in other languages via the JNI
mechanism (see the
<a href="http://java.sun.com/docs/books/tutorial/native1.1/index.html">JNI</a>
section of the Java tutorial).</p><p>

</p><hr>

<h3><a name="SamplePlugin">A simple data analysis plugin</a></h3>

The HelloWorld plugin doesn't do much. Here's a more interesting plugin that
uses Cytoscape's network and attributes data structures to perform a very
simple operation. When the user selects one or more nodes in the graph and
then activates the plugin, it will iterate over each selected node and
additionally select all neighbors of that node whose name ends with the same
letter. For yeast genes, whose names are of the form 'YOR167C', this selects
genes that are on the same DNA strand. While this is a very simple (and
probably not very useful) operation, it illustrates the use of Cytoscape's
core data structures to implement an analysis routine.<p>
To see the plugin in action, save the jar file below to your local
disk. Then run Cytoscape and load in a sample yeast network (for
example, <code>galFiltered.sif</code>
in the testData directory of the public Cytoscape distribution). Then,
load the plugin as before. Select one or more nodes, then activate the
plugin via the <code>Plugins-&gt;SamplePlugin</code> menu option.</p><p>


<a href="pluginTutorial/java/SamplePlugin.java">SamplePlugin.java</a><br>
<a href="pluginTutorial/java/SamplePlugin.jar">SamplePlugin.jar</a></p><p>

</p><h4>Looking at the Plugin:</h4>

<pre><code>
    public SamplePlugin() {
        SamplePluginAction action = new SamplePluginAction();
        action.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
    }
</code></pre><p>

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
for more information).</p><p>
The plugin works with several core objects. The CyNetwork object is a
graph and also contains the associated data. The plugin uses the
network to find the neighbors of the currently selected nodes and to
get the names of the nodes for comparison. The CyNetworkView contains
information on what node views are currently selected.</p><p>

At the end of the algorithm, the plugin calls the <code>redrawGraph</code>
method on the window. This is required to let Cytoscape know that it should
redraw the graph (for example, to update the appearance of newly selected
nodes).</p><p>

</p><hr>

<h3><a name="NetworkLinker">A sample plugin linking multiple networks</a></h3>
This plugin works with multiple networks. When active, it links all
Cytoscape networks so that any node or edge that is flagged in one
network is flagged in all networks. In Cytoscape, the flagged state of
an object in a network is synchronized with the selection state of the
corresponding object in any view of that network. A graph object can
also be flagged in a network that has no view (useful for marking
objects as interesting to other plugins). Thus, this plugin allows the
user or a plugin to flag or select nodes in one network or view and
have them automatically flagged in all networks and selected in all
views that also have those graph objects.<p>
This plugin illustrates three different types of Cytoscape event
handling. It captures events fired by any network's flagger to update
the flagged state in all networks. It catches graph change events to
handle nodes or edges that are hidden. Finally, it catches network
creation events to synchronize the new network with the flag/selection
state of all the other networks.</p><p>
To see the plugin in action, run Cytoscape and load the plugin and a
network. Create a subnetwork of the first network by selecting some
nodes and using the <code>Select-&gt;To New Window-&gt;Selected Nodes, 
All Edges</code> menu option, then create a view on this new network with 
the <code>Edit-&gt;Create View</code> menu option. You should see that 
objects selected in one view are also selected in the other view if 
they exist in both views.</p><p>


<a href="pluginTutorial/java/NetworkLinkerPlugin.java">NetworkLinkerPlugin.java</a><br>
<a href="pluginTutorial/java/NetworkLinkerPlugin.jar">NetworkLinkerPlugin.jar</a></p><p>

This page last modified August 25, 2004.
</p>

    </div>
    <? include "footer.php"; ?>
  </body>
</html>
