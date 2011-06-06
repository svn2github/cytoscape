
/**
 * Network visualisation object.
 *
 */
function Visualization(containerId) {

	this._canvas = Raphael(containerId);
	
	this._edges = {};
	this._nodes = {};
	
	// These are the VisualStyle defaults
	this._visualStyle = {
		global: {
			backgroundColor: "#f0f0f0",
		},
		
		nodes: {
			//"shape": "ELLIPSE",
			"size": 18,
			"color": "#f5f5f5",
			"borderColor": "#666666",
			"borderWidth": 3,
			"opacity": 1,
			"hoverColor": null,
			"hoverBorderColor": null,
			"hoverOpacity": null
		},
		
		edges: {
			"color": "#999999",
			"width": 3,
			"opacity": 0.8,
			//"style": "SOLID",
		}
	};
	
	
	
	this._visualStyleBypass = {};
	
	this._dataSchema = {nodes:[], edges: []};
	
	function dereferenceNode(nodeReference) {
		if (typeof(nodeReference) == "string") {
			return this.node(nodeReference);
		} else {
			return nodeReference;
		}
	}
	
	/**
	 * Get or set the data schema.
	 */
	this.dataSchema = function(value) {
		if (value === undefined) {
			return this._dataSchema;
		} else {
			this._dataSchema = value;
		}
	}
	
	/**
	 * Return the network model as an object.
	 * @return The network model as a JavaScript object.
	 */
	this.networkModel = function(value) {
		if (value === undefined) {
			var nodeList = [];
			for (nodeId in this._nodes) {
				nodeList.push(this._nodes[nodeId].getModel());
			}
			var edgeList = [];
			for (edgeId in this._edges) {
				edgeList.push(this._edges[edgeId].getModel());
			}
			// This is just the data portion, not the data schema
			return {nodes: nodeList, edges: edgeList};
		}
	}
	
	// This is for merging one mapping into another.
	function updateMapping(a, b) {
		for (field in b) {
			if (field in a) a[field] = b[field];
		}
	}
	
	/**
	 * Get or update the VisualStyle. 
	 */
	this.visualStyle = function(value) {
		if (value === undefined) {
			return this._visualStyle;
		} else {
			for (field in value) {
				if (field in this._visualStyle) {
					updateMapping(this._visualStyle[field], value[field]);
				}
			}
			this.draw();
		}
	}
	
	/**
	 * Return a node by id.
	 * @return {Node} node
	 */
	this.node = function(id) {
		return id in this._nodes ? this._nodes[id] : null;
	}
	
	/**
	 * Return an edge by id.
	 * @return {Edge} edge
	 */
	this.edge = function(id) {
		return id in this._edges ? this._edges[id] : null;
	}
	
	/**
	 * Draw the network visualization.
	 */
	this.draw = function() {
	
		document.getElementById(containerId).style.backgroundColor = this._visualStyle.global.backgroundColor;
	
		for (nodeId in this._nodes) {
			this._nodes[nodeId].draw();
		}
		for (edgeId in this._edges) {
			this._edges[edgeId].draw();
		}
	}
	
	/**
	 * Get all the edges from the network.
	 * @return {array} List of edges.
	 */
	this.edges = function() {
		var result = [];
		for (id in this._edges) {
			result.push(this._edges[id]);
		}
		return result;
	}
	
	/**
	 * Get all the nodes from the network.
	 * @return {array} List of nodes.
	 */
	this.nodes = function() {
		var result = [];
		for (id in this._nodes) {
			result.push(this._nodes[id]);
		}
		return result;
	}
	
	/**
	 * Create a new node and add it to the network view.
	 * @param {string} new node id
	 * @return {Node} new node
	 */
	this.addNode = function(id) {
		var newNode = new Node(this);
		newNode.id = id;
		this._nodes[id] = newNode;
		return newNode;
	}
	
	/**
	 * Create a new edge linking two nodes and add it to the network view.
	 * @param {string} new edge id
	 * @return {Edge} new edge
	 */
	this.addEdge = function(id, source, target) {
		var newEdge = new Edge(this);
		newEdge.id = id;
		newEdge.source = source;
		newEdge.target = target;
		newEdge.source._edges.push(newEdge);
		newEdge.target._edges.push(newEdge);
		this._edges[id] = newEdge;
		return newEdge;
	}
}

function delegate(object, methodName) {
	return function() {
		object[methodName].apply(object, arguments);
	}
}

/**
 * Node object
 *
 */
var Node = function(vis) {

	this._visualization = vis;
	this._elem = null;

	// Necessary values
	this.id = null;
	this.x = 0;
	this.y = 0;
	
	// Temporary vars for dragging
	this._dragOriginX = null;
	this._dragOriginY = null; 
	
	this._edges = [];

	this.getEdges = function() {
		return this._edges.slice(0);
	}
	
	this._getNeighbors = function() {
		throw "Not Implemented";
	};

	this.setPosition = function(x, y) {
		this.x = x;
		this.y = y;
		this.draw();
	};
	
	this._dragStart = function() {
		this._dragOriginX = this.x;
		this._dragOriginY = this.y;
		this._elem.toFront();		
	};

	this._dragEnd = function() {
	
	};
	
	this._dragMove = function(x, y) {
		var newX = this._dragOriginX + x;
		var newY = this._dragOriginY + y;
		
		// Clamp coordinates to canvas size (hardcoded for this test demo)
		if (newX < 0) newX = 0;
		if (newX > 400) newX = 400;
		if (newY < 0) newY = 0;
		if (newY > 400) newY = 400;
		
		this.x = newX;
		this.y = newY;
		this.draw();
	};
	
	this._hoverStart = function() {
		this._elem.attr({
			//"fill": this._visualization._visualStyle.nodes.hoverColor,
			//"stroke": this._visualization._visualStyle.nodes.hoverBorderColor,
			//"opacity": this._visualization._visualStyle.nodes.hoverOpacity
		});
	};
	
	this._hoverEnd = function () {
		this._elem.attr({
			"fill": this._visualization._visualStyle.nodes.color,
			"stroke": this._visualization._visualStyle.nodes.borderColor,
			"opacity": this._visualization._visualStyle.nodes.opacity,
			
		});
	};
	
	this.draw = function() {
		if (this._elem == null) {
				this._elem = this._visualization._canvas.circle(this.x, this.y, this._visualization._visualStyle.nodes.size)
					.attr({"cursor":"pointer"})
					.hover(delegate(this, "_hoverStart"), delegate(this, "_hoverEnd"))
					.drag(delegate(this, "_dragMove"), delegate(this, "_dragStart"), delegate(this, "_dragEnd"));
		}
		var attr = {
			// Update position
			"cx": this.x,
			"cy": this.y,
			
	
			// Update styles
			"cr": this._visualization._visualStyle.nodes.size,
			"fill": this._visualization._visualStyle.nodes.color,
			"stroke": this._visualization._visualStyle.nodes.borderColor,
			"stroke-width": this._visualization._visualStyle.nodes.borderWidth,
			"opacity": this._visualization._visualStyle.nodes.opacity
		};
		
		for (var i = 0, len = this._edges.length; i < len; i++) {
			this._edges[i].draw();
		}
		this._elem.attr(attr);
	};

	this.getModel = function() {
		return {
			id: this.id,
		};
	}
}


/**
 * Edge object
 *
 */
var Edge = function (vis) {

	this._visualization = vis;
	this._elem = null;

	// Necessary values
	this.id = null;
	this.source = null;
	this.target = null;
	
	this.getSvgPath = function() {
		return populateString("M %,% L %,%", [this.source.x, this.source.y, this.target.x, this.target.y]);
	}
	
	this.draw = function() {

		if (this.source == null || this.target == null) return;
		if (this._elem == null) {
				this._elem = this._visualization._canvas.path(this.getSvgPath()).toBack();
		}
		
		var attr = {
			// Update position
			"path": this.getSvgPath(),
			// Update styles
			"stroke": this._visualization._visualStyle.edges.color,
			"stroke-width": this._visualization._visualStyle.edges.width,
			
		};
		
		this._elem.attr(attr);
		
	};
	
	this.getModel = function() {
		return {
			id: this.id,
			source: this.source.id,
			target: this.target.id
		};
	};
} 

/**
 * Replace each instance of the placeholder token with a successive item from a list.
 */
function populateString(string, items) {
	var i = 0;
	return string.replace(/%/g, function() {
		return items[i++];
	}); 
}
