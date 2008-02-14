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


<pre>
<code>
import javax.swing.JOptionPane;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

public class HelloWorld extends CytoscapePlugin {

    public HelloWorld() {
        String message = "Hello World!";
        System.out.println(message);
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
    }
}
</code>
</pre>


<p>
<a href="pluginTutorial/java/HelloWorld.java">HelloWorld.java</a> <br>
<a href="pluginTutorial/java/HelloWorld.jar">HelloWorld.jar</a>
</p>


<h3>Looking at the Plugin</h3>

<b>Imports:</b>

<pre><code>
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
</code></pre>


<ul>
<li>
All plugins must extend the class <code>cytoscape.plugin.CytoscapePlugin</code>.
The specific class that extends <code>CytoscapePlugin</code>
doesn't need to stand alone; it can reference any number of other
classes and libraries. This "main" class is simply Cytoscape's entry
point to your code.
</li>

<p></p>

<li>
The static class <code>cytoscape.Cytoscape</code> provides
access the data objects and utilities of the Cytoscape core. Here, the
plugin gets a reference to a window created by Cytoscape for a parent
of a new dialog window.
</li>
</ul>


<b>Methods:</b>
<pre><code>
      public HelloWorldPlugin ()
</code></pre>

<ul>
<li>
All plugins must have a default constructor (that is, a
constructor with no arguments). Cytoscape will call this constructor to
create an instance of your plugin class.
</li>
</ul>


</p>
<p>
To load this plugin into Cytoscape, save the jar file at the above
link to your local disk in the Cytoscape plugins folder. Then start Cytoscape
which will then load the plugin from the jar. You should see the
"Hello World" dialog appear above your main Cytoscape window.
</p>

<!-- <p> This page last modified $Date$. </p> -->

    </div>
    <? include "footer.php"; ?>
  </body>
</html>
