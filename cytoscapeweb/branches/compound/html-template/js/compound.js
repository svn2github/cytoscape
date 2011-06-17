/*
 * Script file for compound node support. This script is used for
 * compound.html
 */

var vis;
var autoLabel = 1;

//options used for Cytoscape Web
var options = {
    nodeTooltipsEnabled: true,
    edgeTooltipsEnabled: true,
    nodeTooltipsEnabled: true,
    edgesMerged: false,
    //mouseDownToDragDelay: -1,
    network: { }, // initial empty network
    layout: {
    	name: "CoSE",
    	options: { }
    },
    visualStyle: {
        global: {
            backgroundColor: "#fefefe",
            tooltipDelay: 1000
        },
        /*
        nodes: {
            shape: "ELLIPSE",
            color: { defaultValue: "#cccccc", continuousMapper: { attrName: "weight", minValue: "#ffffff", maxValue: "#0b94b1" } },
            opacity: 0.9,
            size: { defaultValue: 20, continuousMapper: { attrName: "weight",  minValue: 20, maxValue: 40 } },
            borderWidth: 2,
            borderColor: "#707070",
            image: "http://chart.apis.google.com/chart?chs=300x300&cht=p&chd=e0:U-gh..bR",
            labelFontSize: { defaultValue: 12, continuousMapper: { attrName: "weight",  minValue: 10, maxValue: 24 } },
            tooltipText: { customMapper: { functionName: "onNodeTooltip" } },
            selectionGlowOpacity: 0,
            selectionBorderColor: "ff0000",
            hoverBorderWidth: 4
        },
        edges: {
        	color: "#0b94b1",
            width: { defaultValue: 2, continuousMapper: { attrName: "weight",  minValue: 2, maxValue: 8 } },
            mergeWidth: { defaultValue: 2, continuousMapper: { attrName: "weight",  minValue: 2, maxValue: 8 } },
            mergeColor: "#0b94b1",
            opacity: 0.7,
            labelFontSize: 10,
            labelFontWeight: "bold",
            selectionGlowOpacity: 0,
            selectionColor: "ff0000",
            tooltipText: "${weight}"
         },
        */
        
        nodes: {
        	// regular nodes
        	size: 40,
        	color: "#8a1b0b",
        	image: "http://www.cs.bilkent.edu.tr/~ssumer/cw/cw-logo.png",
        	// compound nodes
        	/*
        	compoundPaddingLeft: 20,
        	compoundPaddingRight: 8,
        	compoundPaddingTop: 12,
        	compoundPaddingBottom: 22,
        	compoundColor: "#9ed1dc",
        	compoundShape: "RECTANGLE",
        	compoundLabelFontSize: 15,
        	compoundLabelVerticalAnchor: "bottom"
        	*/
        	compoundImage: "http://www.cs.bilkent.edu.tr/~ivis/images/ivis-logo.png",
        	compoundPaddingLeft: 10,
        	compoundPaddingRight: 10,
        	compoundPaddingTop: 10,
        	compoundPaddingBottom: 10,
        	compoundColor: "#9ed1dc",
        	compoundShape: "RECTANGLE",
        	compoundLabelFontSize: 14,
        	compoundLabelVerticalAnchor: "top"
        },
        edges: {
        	
        }
        /*
        compoundNodes: {
        	color: "#9ed1dc",
        	shape: "RECTANGLE",        	
        	//labelFontSize: {defaultValue: 12},
        	leftMargin: 20,
        	rightMargin: 8,
        	topMargin: 12,
        	bottomMargin: 22,
        	labelFontSize: 15,
        	labelVerticalAnchor: "bottom"
        }
        */
    }
};

window.onload = function()
{
	// id of Cytoscape Web container div
	var cw_div_id = "cytoscapeweb"
	
	// init and draw
	vis = new org.cytoscapeweb.Visualization(cw_div_id);   

	initToolbar();
	vis.ready(initContextMenu);
	
	//createMenu();
	//options.network = createObjectData();
	options.network = createGraphmlData();
	//options.network = createRandomMesh(5);
	vis.draw(options);
};

/**
 * Creates a sample graphML data containing both simple and compound nodes.
 *
 */
function createGraphmlData()
{
	var data = '<graphml xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd" xmlns="http://graphml.graphdrawing.org/xmlns" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
	'<key id="label" for="node" attr.name="label" attr.type="string"/>' +
	'<key id="weight" for="node" attr.name="weight" attr.type="double"/>' +
	'<key id="name" for="node" attr.name="name" attr.type="string"/>' +
	'<key id="" for="node" attr.name="" attr.type="string"/>' +
	'<key id="label" for="edge" attr.name="label" attr.type="string"/>' +
	'<key id="weight" for="edge" attr.name="weight" attr.type="double"/>' +
	'<graph edgedefault="undirected">' +
	'<node id="n2">' +
	'<data key="label">n2</data>' +
	'</node>' +
	'<node id="n3">' +
	'<data key="label">n3</data>' +
	'</node>' +
	'<node id="n4">' +
	'<data key="label">n4</data>' +
	'</node>' +
	'<node id="n16">' +
	'<data key="label">n16</data>' +
	'</node>' +
	'<node id="n17">' +
	'<data key="label">n17</data>' +
	'</node>' +
	'<node id="n18">' +
	'<data key="label">n18</data>' +
	'</node>' +
	'<node id="n19">' +
	'<data key="label">n19</data>' +
	'</node>' +
	'<node id="n5">' +
	'<data key="label">n5</data>' +
	'<data key=""></data>' +
	'<graph edgedefault="undirected">' +
	'<node id="n22">' +
	'<data key="label">n22</data>' +
	'</node>' +
	'<node id="n21">' +
	'<data key="label">n21</data>' +
	'</node>' +
	'<node id="n23">' +
	'<data key="label">n23</data>' +
	'</node>' +
	'<edge target="n21" source="n23" id="e15">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n22" source="n23" id="e16">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n21" source="n22" id="e17">' +
	'<data key="label"></data>' +
	'</edge>' +
	'</graph>' +
	'</node>' +
	'<node id="n1">' +
	'<data key="label">n1</data>' +
	'<data key=""></data>' +
	'<graph edgedefault="undirected">' +
	'<node id="n20">' +
	'<data key="label">n20</data>' +
	'</node>' +
	'<node id="n11">' +
	'<data key="label">n11</data>' +
	'</node>' +
	'<node id="n12">' +
	'<data key="label">n12</data>' +
	'</node>' +
	'<node id="n13">' +
	'<data key="label">n13</data>' +
	'<data key=""></data>' +
	'<graph edgedefault="undirected">' +
	'<node id="n131">' +
	'<data key="label">n131</data>' +
	'</node>' +
	'</graph>' +
	'</node>' +
	'<edge target="n12" source="n11" id="e12">' +
	'<data key="label">e12</data>' +
	'<data key="weight">1.2</data>' +
	'</edge>' +
	'<edge target="n11" source="n20" id="e13">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n11" source="n13" id="e20">' +
	'<data key="label"></data>' +
	'</edge>' +
	'</graph>' +
	'</node>' +
	'<node id="n6">' +
	'<data key="label">n6</data>' +
	'<data key=""></data>' +
	'<graph edgedefault="undirected">' +
	'<node id="n10">' +
	'<data key="label">n10</data>' +
	'</node>' +
	'<node id="n8">' +
	'<data key="label">n8</data>' +
	'</node>' +
	'<node id="n7">' +
	'<data key="label">n7</data>' +
	'</node>' +
	'<node id="n9">' +
	'<data key="label">n9</data>' +
	'<data key=""></data>' +
	'<graph edgedefault="undirected">' +
	'<node id="n14">' +
	'<data key="label">n14</data>' +
	'</node>' +
	'<node id="n15">' +
	'<data key="label">n15</data>' +
	'</node>' +
	'<edge target="n14" source="n15" id="e18">' +
	'<data key="label"></data>' +
	'</edge>' +
	'</graph>' +
	'</node>' +
	'<edge target="n10" source="n8" id="e5">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n10" source="n7" id="e6">' +
	'<data key="label"></data>' +
	'</edge>' +
	'</graph>' +
	'</node>' +
	'<edge target="n2" source="n1" id="e1">' +
	'<data key="label">e1</data>' +
	'<data key="weight">1.1</data>' +
	'</edge>' +
	'<edge target="n12" source="n2" id="e2">' +
	'<data key="label">e2</data>' +
	'<data key="weight">1.6</data>' +
	'</edge>' +
	'<edge target="n3" source="n4" id="e3">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n3" source="n5" id="e4">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n10" source="n3" id="e7">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n19" source="n18" id="e8">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n17" source="n19" id="e9">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n16" source="n17" id="e10">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n1" source="n16" id="e11">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n18" source="n17" id="e14">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n7" source="n15" id="e19">' +
	'<data key="label"></data>' +
	'</edge>' +
	'<edge target="n4" source="n2" id="e21">' +
	'<data key="label"></data>' +
	'</edge>' +
	'</graph>' +
	'</graphml>';
	
	return data;
}

function createXgmmlData()
{
	var data = '<graph label="Cytoscape Web" directed="0" Graphic="1" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:cy="http://www.cytoscape.org" xmlns="http://www.cs.rpi.edu/XGMML">' +
	'<att name="documentVersion" value="0.1"/>' +
	'<att type="string" name="backgroundColor" value="#ffffff"/>' +
	'<att type="real" name="GRAPH_VIEW_ZOOM" value="1"/>' +
	'<att type="real" name="GRAPH_VIEW_CENTER_X" value="463.23749999999995"/>' +
	'<att type="real" name="GRAPH_VIEW_CENTER_Y" value="368.59999999999997"/>' +
	'<node id="n2" label="n2">' +
	'<graphics x="304.75" y="303.3" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n3" label="n3">' +
	'<graphics x="465.95" y="177.95" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n4" label="n4">' +
	'<graphics x="355.75" y="239.6" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n16" label="n16">' +
	'<graphics x="317.3" y="580.8" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n17" label="n17">' +
	'<graphics x="414.85" y="614.45" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n18" label="n18">' +
	'<graphics x="560.75" y="618.5" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n19" label="n19">' +
	'<graphics x="475.7" y="549.8" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n5" label="n5">' +
	'<graphics x="457.3" y="400.85" cy:nodeLabelFont="Arial-0-11" type="RECTANGLE" labelanchor="s" fill="#f5f5f5" outline="#666666" cy:nodeTransparency="0.8" width="1" w="128.25" h="125.2"/>' +
	'<att>' +
	'<graph>' +
	'<node id="n22" label="n22">' +
	'<graphics x="445.8" y="438.35" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n21" label="n21">' +
	'<graphics x="418.3" y="363.1" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n23" label="n23">' +
	'<graphics x="496.55" y="370.6" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<edge target="n21" source="n23" id="e15" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n22" source="n23" id="e16" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n21" source="n22" id="e17" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'</graph>' +
	'</att>' +
	'</node>' +
	'<node id="n1" label="n1">' +
	'<graphics x="158.25" y="429.1" cy:nodeLabelFont="Arial-0-11" type="RECTANGLE" labelanchor="s" fill="#f5f5f5" outline="#666666" cy:nodeTransparency="0.8" width="1" w="168.1" h="257.3"/>' +
	'<att>' +
	'<graph>' +
	'<node id="n20" label="n20">' +
	'<graphics x="99.2" y="434.7" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n11" label="n11">' +
	'<graphics x="178.05" y="407.4" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n12" label="n12">' +
	'<graphics x="217.3" y="325.45" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n13" label="n13">' +
	'<graphics x="177.9" y="501.3" cy:nodeLabelFont="Arial-0-11" type="RECTANGLE" labelanchor="s" fill="#f5f5f5" outline="#666666" cy:nodeTransparency="0.8" width="1" w="53.95" h="50"/>' +
	'<att>' +
	'<graph>' +
	'<node id="n131" label="n131">' +
	'<graphics x="177.9" y="501.3" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'</graph>' +
	'</att>' +
	'</node>' +
	'<edge target="n12" source="n11" id="e12" label="e12" directed="false">' +
	'<att name="weight" type="real" value="1.2"/>' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n11" source="n20" id="e13" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n11" source="n13" id="e20" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'</graph>' +
	'</att>' +
	'</node>' +
	'<node id="n6" label="n6">' +
	'<graphics x="729.95" y="248.05" cy:nodeLabelFont="Arial-0-11" type="RECTANGLE" labelanchor="s" fill="#f5f5f5" outline="#666666" cy:nodeTransparency="0.8" width="1" w="203.55" h="333.85"/>' +
	'<att>' +
	'<graph>' +
	'<node id="n10" label="n10">' +
	'<graphics x="653" y="390.55" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n8" label="n8">' +
	'<graphics x="733.45" y="369.8" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n7" label="n7">' +
	'<graphics x="686.25" y="299.05" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n9" label="n9">' +
	'<graphics x="771.2" y="157.3" cy:nodeLabelFont="Arial-0-11" type="RECTANGLE" labelanchor="s" fill="#f5f5f5" outline="#666666" cy:nodeTransparency="0.8" width="1" w="96" h="127.2"/>' +
	'<att>' +
	'<graph>' +
	'<node id="n14" label="n14">' +
	'<graphics x="794.45" y="118.7" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<node id="n15" label="n15">' +
	'<graphics x="748.2" y="195.9" cy:nodeLabelFont="Arial-0-11" type="ELLIPSE" labelanchor="c" cy:nodeTransparency="0.8" w="24" fill="#f5f5f5" outline="#666666" h="24" width="1"/>' +
	'</node>' +
	'<edge target="n14" source="n15" id="e18" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'</graph>' +
	'</att>' +
	'</node>' +
	'<edge target="n10" source="n8" id="e5" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n10" source="n7" id="e6" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'</graph>' +
	'</att>' +
	'</node>' +
	'<edge target="n2" source="n1" id="e1" label="e1" directed="false">' +
	'<att name="weight" type="real" value="1.1"/>' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n12" source="n2" id="e2" label="e2" directed="false">' +
	'<att name="weight" type="real" value="1.6"/>' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n3" source="n4" id="e3" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n3" source="n5" id="e4" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n10" source="n3" id="e7" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n19" source="n18" id="e8" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n17" source="n19" id="e9" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n16" source="n17" id="e10" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n1" source="n16" id="e11" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n18" source="n17" id="e14" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n7" source="n15" id="e19" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'<edge target="n4" source="n2" id="e21" label="" directed="false">' +
	'<graphics cy:edgeLineType="SOLID" cy:targetArrow="0" cy:sourceArrowColor="#000000" fill="#999999" cy:targetArrowColor="#000000" cy:sourceArrow="0" width="1"/>' +
	'</edge>' +
	'</graph>';
	
	return data;
}

function readDataFromFile(filename)
{
	var input = fopen(getScriptPath(filename), 0); // open file for reading
	var content;
	
	// if the file is successfully opened
	if(input != -1)
	{
		// read all file content
	    var length = flength(input);     
	    content = fread(input, length);	
	    fclose(input);
	    
	    alert("file opened!");
	}
	else
	{
		alert("wrong input!");
	}
	
	return content;
}

/**
 * Creates a sample network object model data containing both simple and
 * compound nodes.
 *
 */

function createObjectData()
{
	var data = {
	    	dataSchema: {
			nodes: [ { name: "label", type: "string" } ],       
			edges: [ { name: "label", type: "string" },
			         { name: "weight", type: "number" } ]
		},
		data: {
			nodes: [ { id: "n1", label: "n1", network: {
							nodes: [ { id: "n11", label: "n11"},
							         { id: "n12", label: "n12"},
							         { id: "n13", label: "n13", network: {
							        	 nodes: [{id: "n131", label: "n131"}]
							         	}
							         }],
						    edges: [ { id: "e12", label: "e12", weight: 1.2, source: "n11", target: "n12" } ]
						}
					},
			         { id: "n2", label: "n2"}/*,
			         { id: "n3", label: "n3", network: { } }*/ ],
			edges: [ { id: "e1", label: "e1", weight: 1.1, source: "n1", target: "n2" },
			         { id: "e2", label: "e2", weight: 1.6, source: "n2", target: "n12" }]
		}
	};
	
	return data;
}

function createRandomMesh(size)
{	
	var graphData = new Object();
	var data = new Object();
	var nodes = new Array();
	var edges = new Array();
	
	var dataSchema = {
		nodes: [ { name: "label", type: "string" } ],       
		edges: [ { name: "label", type: "string" },
		         { name: "weight", type: "number" } ]
	};
	
	var i, j;
	
	// create nodes
	for (i = 0; i < size*size; i++)
	{
		nodes[i] = { id: "n" + i, label: "n" + i};
	}
	
	j = 0;
	var edge;
	
	// create edges
	for (i = 0; i < size*size; i++)
	{
		if ((i+1) % size != 0)
		{
			edge = new Object();
			
			edge.id = "e" + i;
			edge.label = "e" + i;
			edge.weight = 1;
			edge.source = nodes[i].id;
			edge.target = nodes[i+1].id;
			
			edges[j] = edge;
			j++;
		}
	}
	
	for (i = 0; i < size*(size-1); i++)
	{
		edge = new Object();
		
		edge.id = "e" + i;
		edge.label = "e" + i;
		edge.weight = 1;
		edge.source = nodes[i].id;
		edge.target = nodes[i + size].id;
		
		edges[j] = edge;
		j++;
	}
	
	graphData.nodes = nodes;
	graphData.edges = edges;
	
	data.dataSchema = dataSchema;
	data.data = graphData;
	
	return data;
}

/*
function createObjectData()
{
	var data = {
	    	dataSchema: {
			nodes: [ { name: "label", type: "string" } ],       
			edges: [ { name: "label", type: "string" },
			         { name: "weight", type: "number" } ]
		},
		data: {
			nodes: [ { id: "n0", label: "n0"},
			         { id: "n1", label: "n1"},
			         { id: "n2", label: "n2", network: { nodes: [ { id: "n2:n0", label: "n2:n0"} ]} }],			         
			edges: [ { id: "e0", label: "e0", weight: 1.1, source: "n1", target: "n0" },
			         { id: "e1", label: "e1", weight: 1.1, source: "n2:n0", target: "n0" },
			         { id: "e2", label: "e2", weight: 1.1, source: "n1", target: "n2" }]
		}
	};
	
	return data;
}
*/

/*
 *
 */
function clickNodeToAddEdge(evt)
{
    if (_srcId != null)
    {
    	vis.removeListener("click", "nodes", clickNodeToAddEdge);
    	var e = vis.addEdge({ source: _srcId,
    		target: evt.target.data.id,
    		weight: Math.random(),
    		label: "edge"},
    		true);
    	_srcId = null;
    }
}

/*
 * Add items to context menu
 */
function initContextMenu()
{
	vis.addContextMenuItem("Add new node", function(evt) {
		var label = "node " + autoLabel;
		autoLabel++;
		
		var n = vis.addNode(evt.mouseX,
					evt.mouseY,
					{ weight: Math.random(),
					  label: label},
					true,
					evt.target);
	});

	vis.addContextMenuItem("Toggle node labels", function(evt) {
		if (vis.nodeLabelsVisible())
		{
			vis.nodeLabelsVisible(false);
		}
		else
		{
			vis.nodeLabelsVisible(true);
		}
	});
	
	vis.addContextMenuItem("Toggle edge labels", function(evt) {
		if (vis.edgeLabelsVisible())
		{
			vis.edgeLabelsVisible(false);
		}
		else
		{
			vis.edgeLabelsVisible(true);
		}
	});

	vis.addContextMenuItem("Delete node", "nodes", function(evt) {
		vis.removeNode(evt.target.data.id, true);
	});
    
	vis.addContextMenuItem("Delete edge", "edges", function(evt) {
		vis.removeEdge(evt.target.data.id, true);
	});
    
	vis.addContextMenuItem("Add new edge", "nodes", function(evt) {
		_srcId = evt.target.data.id;
		vis.removeListener("click", "nodes", clickNodeToAddEdge);
		vis.addListener("click", "nodes", clickNodeToAddEdge);
	});
	
	vis.addContextMenuItem("Delete selected", function(evt) {
		var items = vis.selected();
		if (items.length > 0) { vis.removeElements(items, true); }
	});
}

function initToolbar()
{
	$("#load-file").click(function(evt) {
		//var network = readDataFromFile("C:\\temp\\test.xml");
        options.network = network;
        vis.draw(options);
    });
    
	
	$("#cose").click(function(evt) {
        vis.layout("CoSE");
    });
	
	$("#fd").click(function(evt) {
        vis.layout("ForceDirected");
    });
	
	$("#circle").click(function(evt) {
        vis.layout("Circle");
    });
	
	$("#radial").click(function(evt) {
        vis.layout("Radial");
    });
	
	$("#tree").click(function(evt) {
        vis.layout("Tree");
    });
	
	
	$("#in-object-model").click(function(evt) {
        var network = createObjectData();
        options.network = network;
        vis.draw(options);
    });
	
	$("#in-graphml").click(function(evt) {
        var network = createGraphmlData();
        options.network = network;
        vis.draw(options);
    });
	
	$("#in-xgmml").click(function(evt) {
        var network = createXgmmlData();
        options.network = network;
        vis.draw(options);
    });
	
	$("#create-mesh").click(function(evt) {
        var size = document.getElementById("mesh-size").value;
		var network = createRandomMesh(parseInt(size));
        options.network = network;
        options.layout = null;
        vis.draw(options);
    });
	
	$("#to-graphml").click(function(evt) {
		vis.exportNetwork('graphml', 'export.php?type=xml');
    });
	
	$("#to-xgmml").click(function(evt) {
		vis.exportNetwork('xgmml', 'export.php?type=xml');
    });

	$("#to-pdf").click(function(evt) {
		vis.exportNetwork('pdf', 'export.php?type=pdf');
    });

	$("#to-png").click(function(evt) {
		vis.exportNetwork('png', 'export.php?type=png');
    });

	$("#to-svg").click(function(evt) {
		vis.exportNetwork('svg', 'export.php?type=svg');
    });

	$("#to-sif").click(function(evt) {
		vis.exportNetwork('sif', 'export.php?type=xml');
    });
}

// TODO read from input file...
