<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta http-equiv="content-type"
 content="text/html; charset=ISO-8859-1">
  <title>Cytoscape Links</title>
  <link rel="stylesheet" type="text/css" media="screen"
 href="css/cytoscape.css">
  <link rel="shortcut icon" href="images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<table id="feature" summary="" border="0" cellpadding="0"
 cellspacing="0">
  <tbody>
    <tr>
      <td width="10"> &nbsp; </td>
      <td valign="center">
      <h1> Cytoscape Developer Documentation </h1>
      </td>
    </tr>
  </tbody>
</table>
<? include "nav.php"; ?>
<table id="indent" style="width: 145px; height: 125px;">
  <tbody>
    <tr>
      <td>
      <div class="roundbox blue"><big>Quick Links</big>
      <p><a href="concepts.php">Concepts Document</a>
      </p>
      <p><a href="pluginTutorial.php"> Plugin Tutorial </a>
      </p>
      <p><a href="http://cbio.mskcc.org/cytoscape/javadoc/">Cytoscape
API</a>
      </p>
      </div>
      </td>
    </tr>
  </tbody>
</table>
<div id="indent">
<p>
<big><b>Getting Started</b></big>
</p>
<p> Developing Cytoscape means programming in <a
 href="http://java.sun.com/">Java</a>. There are plugins that make it
possible to instead use Python/Perl/R, but these plugins only allow a
small amount of the opertaions that Cytoscape provides. In order to
assist new Developers in writing plugings we have a variety of
resources avalailable. We provide a <a href="concepts.php">Concepts
Document</a> that goes through what the key classes are in Cytoscape
that are used by plugins. In addtion we have tried to make our <a
 href="http://cbio.mskcc.org/cytoscape/javadoc/">API</a> as complete as
possible. Remember that you need not be limited to the API, you can use
the API from any plugin in your plugin as well. Notably, Filters are
only available as a plugin. If you need a jump start, there is a simple
"Hello World" style plugin <a href="pluginTutorial.php">
tutorial </a> available.
</p>
<p> Also, possibly the best source of information is the Cytoscape <a
 href="community.php">Community</a>. By signing up for our discussion
email list, you will have access to the worldwide community of
Cytoscape developers. Thanks for developing for Cytoscape!
</p>
<p></p>
<p><big><b>Species Specific Context Menus</b></big>
</p>
<p> One of the top questions is how to make ones own context menus. It
does require some programming, but it should be fairly obvious how it
is done. All context menus are loaded as a Plugin. What that means is
that once you have built your new menus, and made them into a Java Jar
file. Putting them in your Cytoscape plugins directory will have them
automatically loaded next time you start Cytoscape.
</p>
<p> Before proceeding, please read through the <a href="concepts.php">Concepts
Document</a>, and dowload the <a
 href="ftp://baker.systemsbiology.net/pub/xmas/plugins/yeast-context.tar.bz2">refercence
code</a>. The way that a context menu works is that each menu item is
added to a NetworkView, and when clicked on, is run via an abstract
static class. </p>
<p> First look at src/csplugins/contextmenu/yeast/YeastPlugin.java.
Each menu item is added via code that looks like:</p>
<p>
<table style="width: 434px; height: 124px;">
  <tbody>
    <tr>
      <td>
      <div class="codebox lightyellow">view.addContextMethod( "class
phoebe.PNodeView",<br>
"csplugins.contextmenu.yeast.NodeAction",<br>
"openWebInfo",<br>
new Object[] { view } ,<br>
JarLoader.getLoader() );<br>
      </div>
      </td>
    </tr>
  </tbody>
</table>
</p>
<p> Breaking this down: </p>
<div id="code">class phoebe.PNodeView</div>
refers to the viewable element that will trigger this Context menu. If
there are any questions, please use the discussion list to figure out
which class you should be using. Generally, PNodeView, and PEdgeView
should cover most Nodes and Edges. The next line
<p> </p>
<div id="code">csplugins.contextmenu.yeast.NodeAction</div>
is the class where your custom menui item is found. The third line is
the name of the method that returns a menu item, in this case:
<p></p>
<div id="code">openWebInfo</div>
The fourth line are the arguments that can be passed to the method at
invocation time. The final line is needed so that the menu item can be
loaded properly.
<p> The code for NodeAction.openWebInfo is where the actaul menu item
is defined:
<table style="width: 706px; height: 464px;">
  <tbody>
    <tr>
      <td>
      <div class="codebox lightyellow"> /**<br>
* This will open an web page that will give you more info.<br>
*/<br>
public static JMenuItem openWebInfo ( Object[] args, PNode node ) {<br>
      <br>
final PNode nv = node;<br>
      <br>
JMenu web_menu = new JMenu( "Web Info" );<br>
      <br>
web_menu.add( new JMenuItem( new AbstractAction( "SGD <small><i>yeast
only</i></small>" ) {<br>
public void actionPerformed ( ActionEvent e ) {<br>
// Do this in the GUI Event Dispatch thread...<br>
SwingUtilities.invokeLater( new Runnable() {<br>
public void run() {<br>
String gene = null;<br>
if ( nv instanceof PNodeView ) {<br>
gene = ( ( PNodeView ) nv).getLabel().getText();<br>
}<br>
if ( gene == null ) {<br>
gene = ( String )nv.getClientProperty("tooltip");<br>
}<br>
OpenBrowser.openURL(
"http://db.yeastgenome.org/cgi-bin/SGD/locus.pl?locus="+gene );<br>
      <br>
} } ); } } ) );<br>
return web_menu;<br>
}<br>
      </div>
      </td>
    </tr>
  </tbody>
</table>
</p>
<p> Simply replacing the URL with your own, is enough to add support
for a new organism. Please look at the rest of this class, and use the
discussion list if you have any problems.
</p>
<p></p>
</div>
<br>
<br>
<? include "footer.php"; ?>
</div>
</body>
</html>
