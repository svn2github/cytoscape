<div class="left">

	<h1>Background</h1>
	
		<p class="question">What does Cytoscape Web do?</p>
		<p class="answer">Cytoscape Web is a network visualisation library that you can use to embed
		networks onto a webpage.  It is not a stand alone application for users; it is a tool for developers to
		display networks on the web.</p>
	
		<p class="question">What are the minimum system requirements for end users?</p>
		<p class="answer">End users need a modern browser with the Flash plugin installed.
		Cytoscape Web is tested on Chrome (OS X, Windows, Linux), Firefox (OS X, Windows, Linux),
		Safari (OS X), and Internet Explorer (Windows).</p>
		
		<p class="question">What is the license of Cytoscape Web?</p>
		<p class="answer">Cytoscape Web is an open source project under the 
		<a href="about/license">LGPL</a>.</p>
		
		<p class="question">How do I reference Cytoscape Web?</p>
		<p class="answer">You can find reference information and the publication itself on 
		<a href="http://www.ncbi.nlm.nih.gov/pubmed/20656902">Pubmed</a>.  If you want to link to
		Cytoscape web, please see the
		<a href="about#section/Linking_back_to_Cytoscape_Web">About section</a>.</p>
	
	<h1>Development background</h1>
	
		<p class="question">What development skills should I have to use Cytoscape Web?</p>
		<p class="answer">To use Cytoscape Web, you should be very familiar with HTML and Javascript.
		You do not need to program in Flash, but you should know Javascript and DOM manipulation
		very well if you plan on using the more advanced features of Cytoscape Web or you plan to
		build a web app around Cytoscape Web.</p>
		
		<p class="question">What technologies is Cytoscape Web made from?</p>
		<p class="answer">Cytoscape Web in a Flash component with a Javascript API.</p>
		
		<p class="question">Do I need to be a Flash developer to use Cytoscape Web?</p>
		<p class="answer">No, you do not need to know any Flash.  Cytoscape Web's API is all
		Javascript.</p>
	
	<h1>Troubleshooting</h1>
	
		<p class="question">I tried to open the Cytoscape Web SWF file.  Why doesn't it work?</p>
		<p class="answer">Cytoscape Web is a library, not an application.  You need to use the Javascript
		API to embed Cytoscape Web in a webpage.  You can not open the SWF file.</p>
		
		<p class="question">I copied the example from the tutorial and it doesn't work!  Why?</p>
		<p class="answer">Set your Flash security settings <a href="tutorial#section/Getting_started">properly</a>,
		or deploy the HTML file to a webserver, like Apache.</p>
	
	<h1>Loading networks</h1>
	
		<p class="question">What network formats does Cytoscape Web support?</p>
		<p class="answer">Cytoscape Web supports
		<a href="http://graphml.graphdrawing.org/primer/graphml-primer.html" >GraphML</a>,
		<a href="http://www.cs.rpi.edu/~puninj/XGMML/" >XGMML</a>, and
		<a href="http://cytoscape.wodaklab.org/wiki/Cytoscape_User_Manual/Network_Formats/" >SIF</a>.</p>
		
		<p class="question">My favourite network format isn't supported?  What can I do?</p>
		<p class="answer">You can either write some code that translates your networks into a
		supported format on-the-fly, or you could add it to Cytoscape Web itself.  Cytoscape Web
		is an open source project, so feel free to check out the <a href="download#section/Source_Code">source</a> and try things out!</p>
		
		<p class="question">Can I load a network from Javascript objects instead of a file?</p>
		<p class="answer">No, but the next question may interest you.</p>
		
		<p class="question">Can I modify the network programmatically?</p>
		<p class="answer">Yes, you can add and remove nodes and edges programmatically using 
		<a href="documentation#section/addEdge">addEdge</a>, <a href="documentation#section/addNode">addNode</a>,
		<a href="documentation#section/removeEdge">removeEdge</a>, and <a href="documentation#section/removeNode">removeNode</a>.
		Note that these functions are slow when applied in iteration on many elements.</p>
		
		<p class="question">Can I load a network from a URL?</p>
		<p class="answer">Cytoscape Web does not support this feature directly, but you can implement
		yourself easily.
		Use AJAX to pull the file you want into a string in Javascript, and then pass that string
		to Cytoscape Web.  <a href="http://jquery.com">jQuery</a> is a good library for this.</p>
	
	<h1>Interacting with Cytoscape Web</h1>
	
		<p class="question">I added a listener, changed the data schema, filtered, or called some function on Cytoscape Web.  Why isn't it working?</p>
		<p class="answer">You need to call new, ready, and draw in that order with interactions with Cytoscape Web within
		the ready callback.  All interaction with Cytoscape Web occurs within the callback function passed to the ready function.
		See the <a href="tutorial#section/Interacting_with_Cytoscape_Web">tutorial</a> for an example.</p>
	
		<p class="question">What formats can I export the network to?</p>
		<p class="answer">PDF, PNG, 
		<a href="http://graphml.graphdrawing.org/primer/graphml-primer.html" >GraphML</a>,
		<a href="http://www.cs.rpi.edu/~puninj/XGMML/" >XGMML</a>, and
		<a href="http://cytoscape.wodaklab.org/wiki/Cytoscape_User_Manual/Network_Formats/" >SIF</a> are supported.</p>
	
	<h1>Customizing visual styles</h1>
	
		<p class="question">What options do I have to change the visual style?</p>
		<p class="answer">See the <a href="documentation/visual_style">API reference</a>.</p>
		
		<p class="question">Can I change the visual style after the network has been drawn?</p>
		<p class="answer">Yes, use <a href="documentation#section/visualStyle">visualStyle</a>.</p>
		
		<p class="question">How do I set the visual style to be dependent on the network data?</p>
		<p class="answer">Use a <a href="documentation/mappers">mapper</a>.</p>
		
		<p class="question">Can I set the edge length to be dependent on an attribute?</p>
		<p class="answer">Not precisely.  You can use an edge weighted layout, but the lengths will
		only be approximate based on the weight attribute.</p>
	
	<h1>Modifying network data</h1>
		
		<p class="question">How do I change node or edge attribute values after the graph is drawn?</p>
		<p class="answer">Use <a href="documentation#section/updateData">updateData</a>.</p>
		
		<p class="question">How can I add a new node or edge attribute to an existing network?</p>
		<p class="answer">Use <a href="documentation#section/addDataField">addDataField</a>.</p>
		
		<p class="question">How can I delete a node or edge attribute?</p>
		<p class="answer">Use <a href="documentation#section/removeDataField">removeDataField</a>.</p>
		
		<p class="question">How can I add nodes and edges to an existing network?</p>
		<p class="answer">Use <a href="documentation#section/addEdge">addEdge</a> and <a href="documentation#section/addNode">addNode</a>.</p>
		
		<p class="question">How can I delete nodes and edges from a network?</p>
		<p class="answer">Use <a href="documentation#section/removeEdge">removeEdge</a> and and <a href="documentation#section/removeNode">removeNode</a>.</p>
	
	<h1>Layouts</h1>
	
		<p class="question">What layouts does Cytoscape Web support?</p>
		<p class="answer">Several; see the <a href="documentation/layout">API</a> for details.</p>
		
		<p class="question">Can I customize the layout parameters?</p>
		<p class="answer">Yes, see the <a href="documentation/layout">API</a> for details.</p>
		
		<p class="question">Why is the force-directed layout not animated?</p>
		<p class="answer">It's computationally expensive such that large networks become unresponsive
		even on modern computers.</p>
		
		<p class="question">Does the force-directed layout support an edge-weighted option?</p>
		<p class="answer">Yes, see weightAttr in the <a href="documentation/layout">API</a>.</p>
		
		<p class="question">Can I use my own custom layout algorithm?</p>
		<p class="answer">Yes, calculate your layout and pass it to Cytoscape Web as a preset layout.
		You can calculate it on the client side with Javascript or on the server side with Java, PHP, 
		and so on.</p>
</div>