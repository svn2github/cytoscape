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
<h2>"Hello World" Cytoscape Plugin</h2>
</center>

<p>
Here is a simple "Hello World" example as a Cytoscape plugin where
a Java Swing utility class is used to display the message in a dialog window.
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
</code></pre>


<h4>Looking at the Plugin:</h4>

<b>Imports:</b>
<br>
<code></code><pre>
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
</pre>

<p>
<ol>
<li>All plugins must extend the class <code>cytoscape.plugin.CytoscapePlugin</code>. 
The specific class that extends <code>CytoscapePlugin</code>
doesn't need to stand alone; it can reference any number of other
classes and libraries. This "main" class is simply Cytoscape's entry
point to your code.
</p>
<p>
</li><li>All plugins must have a default constructor (that is, a
constructor with no arguments). Cytoscape will call this constructor to
create an instance of your plugin class.
</p>
<p>
</li><li>The static class <code>cytoscape.Cytoscape</code> provides
access the data objects and utilities of the Cytoscape core. Here, the
plugin gets a reference to a window created by Cytoscape for a parent
of a new dialog window.
</li></ol>
</p>

<a href="pluginTutorial/java/HelloWorld.java">HelloWorld.java</a> <br>
<a href="pluginTutorial/java/HelloWorld.jar">HelloWorld.jar</a>
</p>
<p>
To load this plugin into Cytoscape, first save the jar file at the above
link to your local disk. Then start Cytoscape and select the 
<code>Plugin-&gt;Load Plugin from Jar File</code>
menu option. Use the file browser to find and select this jar file;
Cytoscape will then load the plugin from the jar. You should see the
"Hello, world" dialog appear above your main Cytoscape window.
</p><p>
If you run Cytoscape using one of the scripts that comes with the
standard Cytoscape distribution, you can place the jar file in the
plugins directory of the cytoscape distribution, and Cytoscape will find it
automatically. In this case you won't need to load the plugin via the
menu.
</p><p>

This page last modified August 25, 2004.
</p>

    </div>
    <? include "footer.php"; ?>
  </body>
</html>
