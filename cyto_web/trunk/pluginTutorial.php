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

Cytoscape allows programmers to write plugins that access the core
data structures and windows of Cytoscape to do a wide variety of 
operations. This tutorial explains how to write a plugin and how to get
Cytoscape to load and use your plugin.

<p>
It is assumed that the reader is familiar with the basics of the Java
programming language and has some kind of programming environment available.
See
<a href="http://java.sun.com/docs/books/tutorial/index.html">the Java Tutorial</a> 
for an excellent introduction and handy reference guide for Java.
You will also want to check out the
<a href="http://www.cbio.mskcc.org/cytoscape/alpha/javadoc/index.html">Cytoscape core API documentation</a>.
</p>


<p>
Each plugin tutorial includes the Java source code and a jar file containing
the compiled classes. To run Cytoscape via Java WebStart with all of these 
plugins automatically loaded, click here: 
<a href="pluginTutorial/webStart/cy.jnlp">WEB START</a>. 
(See the <a href="http://cytoscape.systemsbiology.net/Cytoscape2.0/user/JavaWebStart/index.html">
 Cytoscape Web Start documentation</a> for information on running Cytoscape via Java Web Start).
</p>

<hr>

<p>

<h3><a name="CytoscapePlugin">The CytoscapePlugin class</a></h3>

The CytoscapePlugin class is very simple. It defines a default constructor and 
one instance method: <code>describe</code>,
which can be overridden to describe what the plugin does. A static
method also exists that is used by Cytoscape to load plugins. Since
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

</p>

<!-- <p>This page last modified August 25, 2004.</p> -->

    </div>
    <? include "footer.php"; ?>
  </body>
</html>
