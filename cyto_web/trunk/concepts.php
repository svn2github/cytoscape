<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content=
    "text/html; charset=ISO-8859-1">
    <title>
      Cytoscape Links
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
          <h1>
            Cytoscape Developer Documentation
          </h1>
        </td>
      </tr>
    </table>
    <? include "nav.php"; ?>
    <div id="indent">

<P>
Cytoscape Developer Documentation
<P>
<hr>
<big><b>Concept 1: Networks</big></b>
<P>
	Cytoscape is written using the Java programming language, and therefore uses the 
Object-Oriented paradigm.  The central object in Cytoscape is a CyNetwork.  The 
CyNetwork is a combination of a graph, consisting of Nodes and Edges, and the data that is 
associated with individual graph elements, or the graph itself.  
<P>
	CyNetworks inherit from the GraphPerspective which is part of the underlying 
GINY graph library.  All CyNetworks operate on the same set of Nodes and Edges, 
although not all Nodes and Edges will be in every CyNetwork.  This means that if you 
create a Node in one CyNetwork, it can be added to another CyNetwork, and will have all of 
its data and connections associated with it.
<P>
	The following diagram shows the classes and their relationships that will be used 
most.
 <P>
<img src="images/image001.gif">
<p>
<hr>
<big><b>Concept 2: Creating and Modifying and Destroying Networks</big></b>
<P>
	The creation of CyNetworks is handled by factory methods found in the 
cytoscape.Cytoscape class.  Cytoscape is an abstract static class and cannot be instantiated. 
It does however handle all of the creation methods and fires the appropriate events to its 
listeners.  CyNetworks can be created in a variety of ways, but they all work in a similar 
fashion.  What they will do is, given a set of nodes and edges, will create the nodes and 
edges if necessary in the shared set of nodes and edges that all CyNetworks use, and then 
return a CyNetwork that only has the requested nodes and edges.  The following are the 
methods for creating a CyNetwork:
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// return an empty network<br>
CyNetwork network = Cytoscape.createNetwork();<br>
<br>
// return a new Network from a SIF file<br>
CyNetwork network = Cytoscape.createNetwork( file_name, <br>
Cytoscape.FILE_SIF, canonicalize, biodataserver, species );<br>
<br>
// return a new Network from a GML file<br>
CyNetwork network = Cytoscape.createNetwork( file_name, <br>
Cytoscape.FILE_GML, canonicalize, biodataserver, species );<br>
<br>
// return a new Network from a file, with a  standard<br>
// suffix, using the default values<br>
CyNetwork network = Cytoscape.createNetwork( file_name );<br>
<br>
// return a new Network with a subset of Nodes and Edges<br>
// from an existing Network, and make the new Network a<br>
// a child of the old Network in the Network Panel<br>
CyNetwork new_network = Cytoscape.createNetwork( nodes, <br>
edges, child_title, parent_network );<br>
</div>
</TD>
</TR>
</TABLE>
<P>

	When modifying a CyNetwork, it is necessary to operate directly on that 
CyNetwork, rather than using the Cytoscape class.  There are two types of removal that can 
be done.  Using the "removeNode/Edge" method Cytoscape will attempt to completely 
remove the Node/Edge from both the CyNetwork and the shared set of Nodes and Edges 
that all CyNetworks have access to.  A Node/Edge will not be removed completely if 
another CyNetwork has a reference to it.  The removal can be forced by passing a Boolean 
"true" to force the removal.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// remove the node, unless used by another CyNetwork<br>
network.removeNode( node, false );<br>
<br>
// remove the node, even if used by another CyNetwork<br>
network.removeNode( node, true );<br>
</div>
</TD>
</TR>
</TABLE>
<P>
	If you want the Node/Edge only removed the current CyNetwork, but still available 
for other CyNetworks ( or even the one it was removed from ) at a later time.  The "hide" 
methods are used.  These methods are part of the GINY API, but are linked in the JavaDoc.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// remove the node from this CyNetwork, but allow it to<br>
// be re-used later.<br>
network.hideNode( node )<br>
</div>
</TD>
</TR>
</TABLE>
<P>
	Creating Nodes/Edges can happen in several ways.  Since there is a set of 
Nodes/Edges that are available to all CyNetworks, there is nothing wrong with creating 
Nodes/Edges that are not a part of any CyNetwork.  This is done using the static Cytoscape 
class, just like CyNetwork creation.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// create a CyNode, Boolean passed so the "create" flag<br>
// is enabled<br>
CyNode node = Cytoscape.getCyNode( identifier, true )<br>
</div>
</TD>
</TR>
</TABLE>
<P>
	The same function can also be used to query if a CyNode exists for a particular 
alias. This is the default behavior, or can be explicitly used by passing a "false" value for 
the create flag.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// Return a CyNode if one exists, or null if not.<br>
CyNode node = Cytoscape.getCyNode( identifier );<br>
if ( node == null ) <br>
	// there is no node for this identifier<br>
</div>
</TD>
</TR>
</TABLE>
<P>
	Destroying a CyNetwork is also done via the static Cytoscape class using the 
"destroyNetwork" methods.  When a CyNetwork is destroyed all of the Nodes/Edges are 
hidden, so they are still available for other CyNetworks.  Destroying a CyNetwork will also 
destroy any CyNetworkView that is was created to view this CyNetwork.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// Destroy a CyNetwork<br>
Cytoscape.destroyNetwork( network );<br>
<br>
// Destroy a CyNetwork using its "getIdentifier" method<br>
Cytoscape.destroyNetwork( network.getIdentifer() );<br>
</div>
</TD>
</TR>
</TABLE>
<hr>
<P>
<big><b>Concept 3: NetworkView Creation, Modifying and Destruction</big></b>
<P>
	While CyNetworks represent the unification of the graph and data, the 
CyNetworkView is the Visualization of the CyNetwork.  Working with CyNetworkViews is 
very similar to working with CyNetworks.  As with the CyNetwork, a CyNetworkView is 
created using the static Cytoscape class, and destroyed in the same way.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// create a CyNetworkView from a CyNetwork<br>
CyNetworkView view = Cytoscape.createNetworkView( network );<br>
<br>
// ways to destroy a CyNetworkView, given:<br>
CyNetwork network;<br>
CyNetworkView view;<br>
<br>
Cytoscape.destroyNetworkView( view );<br>
Cytoscape.destroyNetworkView( view.getIdentifier );<br>
Cytoscape.destroyNetworkView( network );<br>
<br>
// implicit destruction by destroying the network<br>
// it is based on<br>
Cytoscape.destroyNetwork( network );<br>
Cytoscape.destroyNetwork( network.getIdentifer() );<br>
</div>
</TD>
</TR>
</TABLE>
<P>

	Modification of a CyNetworkView is not directly possible.  It is a WYSIWYG view 
on a CyNetwork, so hiding a NodeView in a CyNetworkView only makes that NodeView 
temporarily invisible.  To actually remove a NodeView from a CyNetworkView, the Node 
that it is a view on must be hidden/removed from the CyNetwork.  This behavior can be 
changed, but is beyond the scope of this document.
<P>
<hr>
<big><b>Concept 4: CyNetwork(View) Client Data</big></b>
<P>
	CyNetworks and CyNetworkViews will be used by a variety of plugins, and plugins 
will use a variety of CyNetworks and CyNetworkViews.  Therefore, the concept of client 
data allows a plugin to store information that it needs about a particular CyNetwork(View), 
with that Object.<P>
	An example of this is a plugin that calculates an APSP matrix, and runs an analysis 
on it.  Obviously each CyNetwork has a different APSP matrix, so if a new CyNetwork is 
being used, then a new matrix needs to be calculated.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// Sample method for a plugin performing an action<br>
CyNetwork network = Cytoscape.getCurrentNetwork();<br>
APSP matrix;<br>
Object apsp = network.getClientData( "APSP" );<br>
if ( apsp == null ) {<br>
	// there is no apsp matrix available, create one<br>
	matrix = new APSP( network );<br>
	// store it for later use<br>
	network.putClientData( "APSP", matrix );<br>
}<br>
// use the matrix we already had, or the one we just made<br>
doSomething( matrix );<br>
		</div>
</TD>
</TR>
</TABLE>
<P>


<hr>
<big><b>Concept 5: Loading, and Using Data</big></b>
<P>
	Data can be thought of in many different ways.  Usually we refer to data as specific 
attributes loaded by the user, that refer to one specific Node or Edge.  Common attributes 
are Expression data from Microarray experiments, other laboratory measurements, and 
ontology information. <P> 
	Data can be loaded in a variety of ways, from a variety of sources.  We will only 
discuss loading from files, and from a calculation.  Database conections and other things are 
handled via other methods such as the Data Servives Plugin from MSKCC.
	The most common way to load Node and Edge attributes is from Node and Edge 
attributes files, and from expression "matrix" files.  Both are possible using the static 
Cytoscape class.  <P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// load Node and Edge Attributes from a set of files.<br>
String[] node_atts = new String{ "file1", "file2" };<br>
String[] edge_atts = new String{ "file3", "file4" };<br>
Cytoscape.loadAttributes( node_atts, edge_atts );<br>
<br>
// load Expression Data<br>
Cytoscape.laodExpressionData( file_name, true );<br>
<br>
// right now setting individual attributes is a little<br>
// hacky, since we just use the old GraphObjAttributes <br>
class.<br>
// @deprecated DO NOT USE!!!! ( unless you have to )<br>
Cytoscape.getNodeNetworkData().set( attribute, node, value <br>
);<br>
</TD>
</TR>
</TABLE>
<P>
The more supported way for adding attributes for individual Nodes and Edges (e.g. a 
calcualted value from a Plugin ), is to use the method provided by CyNetwork.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// set a value for a node, for a given attribute<br>
String attribute;<br>
Object value;<br>
CyNode node;<br>
	// note that the method name is the argument order<br>
network.setNodeAttributeValue( node, attribute, value );<br>
</TD>
</TR>
</TABLE>
<P>
	CyNetwork also provides methods for getting attributes for nodes.  The methods are 
structured the same as for setting.
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// get a value for a node, for a given attribute<br>
String attribute;<br>
CyNode node;<br>
	// note that the method name is the argument order<br>
Object value = network.getNodeAttributeValue( node, <br>
attribute );<br>
</TD>
</TR>
</TABLE>
<P>
Several convience methods are also available, look at the API.  What methods are here are 
open for debate. Please let me know what you want/need/like/use.
<P>
<hr>
<big><b>Concept 6: Focus</big></b>
<P>
Since many CyNetworks can exist in one Cytoscape instance it is necessary to 
know which CyNetwork and CyNetworkView you are working with.  For a plugin that 
operates when a button/menu is selected, and wants to use whichever CyNetwork and 
CyNetworkView the user chose, then the following methods should be used:
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
//action method<br>
public void actionPerformed ( ActionEvent e ) {<br>
	CyNetwork network = Cytoscape.getCurrentNetwork();<br>
	CyNetwork view = Cytoscape.getCurrentNetworkView();<br>
}<br>
</TD>
</TR>
</TABLE>
<P>
Note that if a CyNetworkView was last focused then the current CyNetwork will always be 
the CyNetwork of that CyNetworkView.  However if a CyNetwork was last focused with no 
view, then the current CyNetworkView will return Cytoscape.nullNetworkView. 
<P>
<hr>
<big><b>Concept 7: CytoscapeDesktop</big></b>
<P>
	The CytoscapeDesktop is responsible for managing all of the CyNetworkViews and 
for updating the focus.  There is only one instance of CytoscapDesktop and it is available 
via:
<P>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
// get the CytoscapeDesktop<br>
Cytoscape.getDesktop();<br>
</TD>
</TR>
</TABLE>
<P>
The CytoscapeDesktop will not normally be used, but is useful for being the parent for 
Dialogs, since it is a JFrame.
<P>
<hr>
<big><b>Concept 8: Events</big></b>
<P>
	Events are a useful way to update an Object when a property changes. Any event 
listener needs to implements java.beans.PropertyChangeListener. Here are the available 
events:
<P>
<table  style="margin-left: 30;  border: dotted gray 1px;
		padding-left: 10px;font-size:small" width=20%>
<tr>
<td>Event</td>
<td>Firing Class</td><tr>
<td>CYTOSCAPE_EXIT</td>
<td>Cytoscape</td><tr>
<td>NETWORK_CREATED</td>
<td>Cytoscape</td><tr>
<td>NETWORK_DESTROYED</td>
<td>Cytoscape</td><tr>
<td>ATTRIBUTES_LOADED</td>
<td>Cytoscape</td><tr>
<td>NETWORK_VIEW_CREATED</td>
<td>CytoscapeDesktop</td><tr>
<td>NETWORK_VIEW_DESTROYED</td>
<td>CytoscapeDesktop</td><tr>
<td>NETWORK_VIEW_FOCUS</td>
<td>CytoscapeDesktop</td><tr>
<td>NETWORK_VIEW_FOCUSED</td>
<td>CytoscapeDesktop</td><tr>
<td>NETWORK_VIEW_FOCUSED</td>
<td>CytoscapeDesktop</td><tr>
</table>
<P>
This is an example implementation of a class that is a listener to both Cytoscape and CytoscapeDesktop.
<p>
<table width=50%>
<TR><TD>
<div class="codebox lightyellow">
	public class MyClass implements <br>
java.beans.PropertyChangeListener {<br>
<br>
  public MyClass () {<br>
    // add as listener to Cytoscape<br>
    Cytoscape.getSwingPropertyChangeSupport().<br>
                    addPropertyChangeListener(this);<br>
    // add as listener to CytoscapeDesktop<br>
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().<br>
                           addPropertyChangeListener(this);<br>
  }<br>
<br>
  public void propertyChanged ( PropertyChangedEvent e ) {<br>
    //respond<br>
  }<br>
}<br>
</TD>
</TR>
</TABLE>
<p>



<br><br>

    </div>
    <? include "footer.php"; ?>
  </body>
</html>