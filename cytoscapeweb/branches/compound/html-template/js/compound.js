/*
 * Script file for compound node support. This script is used for
 * compound.html
 */

var vis;

//options used for Cytoscape Web
var options = {
    nodeTooltipsEnabled: true,
    edgeTooltipsEnabled: true,
    nodeTooltipsEnabled: true,
    edgesMerged: false,
    //mouseDownToDragDelay: -1,
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
        	size: 30,
        	color: "#8a1b0b"
        },
        edges: {
        	
        },
        
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
    }   
};

window.onload = function()
{
	// id of Cytoscape Web container div
	var div_id = "cytoscapeweb";

	// init and draw
	vis = new org.cytoscapeweb.Visualization(div_id);   

	vis.ready(initContextMenu);
	
	vis.draw(options);
};

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
		var n = vis.addNode(evt.mouseX,
					evt.mouseY,
					{ weight: Math.random(),
					  label: "node"},
					true,
					evt.target);
	});

	vis.addContextMenuItem("Add new compound node", function(evt) {
		var n = vis.addCompoundNode(evt.mouseX,
				evt.mouseY,
				{ weight: Math.random(),
				  label: "cnode"},
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
