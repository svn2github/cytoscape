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
	options.network = createObjectData();
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
					'<key id="label" for="edge" attr.name="label" attr.type="string"/>' + 
					'<key id="weight" for="edge" attr.name="weight" attr.type="double"/>' + 
					'<graph edgedefault="undirected">' +
						'<node id="n2">' +
							'<data key="label">n2</data>' +
						'</node>' +
						'<node id="n1">' +
							'<data key="label">n1</data>' +
							'<graph>' +
								'<node id="n12">' +
									'<data key="label">n12</data>' +
								'</node>' +
								'<node id="n11">' +
									'<data key="label">n11</data>' +
								'</node>' +
								'<node id="n13">' +
									'<data key="label">n13</data>' +
									'<graph>' +
										'<node id="n131">' +
											'<data key="label">n131</data>' +
										'</node>' +
									'</graph>' +
								'</node>' +
								'<edge id="e12" target="n12" source="n11">' +
									'<data key="label">e12</data>' +
									'<data key="weight">1.2</data>' +
								'</edge>' +
							'</graph>' +
						'</node>' +
						'<node id="n3">' +
							'<data key="label">n3</data>' +
							'<graph/>' +
						'</node>' + 
						'<edge id="e1" target="n2" source="n1">' +
							'<data key="label">e1</data>' +
							'<data key="weight">1.1</data>' +
						'</edge>' +
						'<edge id="e2" target="n12" source="n2">' +
							'<data key="label">e2</data>' +
							'<data key="weight">1.6</data>' +							
						'</edge>' +
					'</graph>' +
				'</graphml>';
	
	return data;
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
	
	/*
	vis.addContextMenuItem("Import", function(evt) {
		//createOpen();
	});
	*/
	
}

function initToolbar()
{
	/*
	$("#load-file").click(function(evt) {
		//new org.cytoscapeweb.demo.Importer("file_importer", null);
		createOpen();
    });
    */
	
	$("#layout").click(function(evt) {
        vis.layout("CoSE");
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

function createOpen(){
    var opts = {
            //swfPath: path("Importer"),
            //flashInstallerPath: path("playerProductInstall"),
            data: function(data){
    			var network = data.string;
				var new_graph_options = {
					network: network,
				    name: data.metadata.name,
				    description: "",
				    visualStyle: options.visualStyle,
				    //visualStyle: GRAPH_STYLES["Default"],
				    nodeLabelsVisible: true
				};

				openGraph(new_graph_options);
			},
			
            ready: function(){
                $("#open_file").trigger("available");
            },
            
            typeFilter: function(){
                return "*.graphml;*.xgmml;*.xml;*.sif";
            },
            binary: function(metadata){
            	return false; // to return data.string and not data.bytes
            	// TODO: if CYS support, check metadata.name.indexOf(".cys")
            }
        };
        
    new org.cytoscapeweb.demo.Importer("file_importer", opts);
    //new org.cytoscapeweb.demo.Importer("open-file", opts);
}

function openGraph(opt)
{
	return;
}

function createMenu(){
    $("#menu").children().remove(); // remove old menu if needed (we don't want two after a redraw)
    $("#menu").append(      '<ul>\
                                <li id="save_file"><label>Save file</label></li>\
                                <li id="open_file"><label>Open file</label><span id="file_importer"></span></li>\
                                \
                                <li><label>Style</label>\
                                    <ul>\
                                        <li id="merge_edges" class="ui-menu-checkable"><label>Merge edges</label></li>\
                                        <li id="show_node_labels" class="ui-menu-checkable"><label>Show node labels</label></li>\
                                        <li id="show_edge_labels" class="ui-menu-checkable"><label>Show edge labels</label></li>\
                                        <li>\
                                            <label>Visualisation</label>\
                                            <ul id="visual_style" class="ui-menu-one-checkable">\
                                            </ul>\
                                        </li>\
                                    </ul>\
                                </li>\
                                \
                                <li><label>Layout</label>\
                                    <ul>\
                                        <li id="recalculate_layout"><label>Recalculate layout</label></li>\
                                        <li>\
                                            <label>Mechanism</label>\
                                            <ul id="layout_style" class="ui-menu-one-checkable">\
                                            </ul>\
                                        </li>\
                                        <li id="layout_settings"><label>Settings...</label></li>\
                                    </ul>\
                                </li>\
                            </ul>\
                            ');

    $("#menu").menu({
    	menuItemMaxWidth: 180,
    });
    
    /*                        
    $("#save_file").click(function(){
        show_save_menu();
    });
    */
                            
    // menu should not span
    var last_top_lvl_item = $("#menu > ul > li:last");
    $("#menu").css( "min-width", last_top_lvl_item.offset().left + last_top_lvl_item.outerWidth(true) );
}
