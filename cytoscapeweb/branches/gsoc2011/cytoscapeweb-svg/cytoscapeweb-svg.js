/*
 * Cytoscape Web SVG prototype
 * By Marek Zaluski
 *
 * Google Summer of Code 2011
 */
 
 
/**
 * Network visualization instance.
 * Represents an instance of the network visualization.
 */
function Visualization(containerId) {

	this._width = 640;
	this._height = 480;
	this._canvas = Raphael(containerId, 640, 480);

	this._edges = {};
	this._nodes = {};
	
	this._dragX = 0;
	this._dragY = 0;
	
	// Viewport offset
	this._offsetX = 186;
	this._offsetY = 100;

	// Style defaults
	this._style = {
		global: {
			backgroundColor: "#eeeeee"
		},

		node: {
			"shape": "ELLIPSE",
			"ratio": 1,
			"size": 18,
			"color": "#f5f5f5",
			"borderColor": "#666666",
			"borderWidth": 3,
			"opacity": 1,
			"hoverColor": "pink",
			"hoverBorderColor": null,
			"hoverOpacity": null,
			"dragColor": null,
			"dragBorderColor": null,
			"dragOpacity": null,
			"selectedColor": null,
			"selectedBorderColor": "red",
			"selectedOpacity": null,
			"labelSize": 10,
			"labelColor": "#000000"
		},

		edge: {
			"color": "#999999",
			"width": 3,
			"opacity": 0.8,
			"style": "SOLID",
			"forwardArrowShape": "",
			"forwardArrowSize": 10,
			"backwardArrowShape": "",
			"backwardArrowSize": 10
		}
	};
	
	this._bg = this._canvas.rect(0, 0, this._width, this._height).attr({"fill": this._style.global.backgroundColor, "stroke": "none", "cursor": "move"});
	this._bg.drag(Util.delegate(this, "_dragMove"), Util.delegate(this, "_dragStart"));

	this.deselect = function () {
		
		for (var nodeId in this._nodes) {
			this._nodes[nodeId].deselect();
		}
		
		for (var edgeId in this._edges) {
			this._edges[edgeId].deselect();
		}
	}

	/**
	 * Get a global style property.
	 * @param property Style property name
	 * @return value
	 */
	this.getGlobalStyle = function(property) {
		if (!(property in this._style.global)) throw "No such property: " + property;

		return this._style.global[property];
	};

	/**
	 * Set a global style property.
	 * @param property Style property name
	 * @param value Style value
	 */
	this.setGlobalStyle = function(property, value) {

		if (!(property in this._style.global)) throw "No such property: " + property;

		this._style.global[property] = value;

		return this;
	};

	/**
	 * Set global style properties from a mapping.
	 * @param obj Mapping of property names to values
	 */
	this.setGlobalStyles = function(obj) {
		for (var property in obj) {
			this.setGlobalStyle(property, obj[property]);
		}
		return this;
	};

	/**
	 * Get a node style property.
	 * @param property Style property name
	 */
	this.getNodeStyle = function(property) {
		if (!(property in this._style.node)) throw "No such property: " + property;

		return this._style.node[property];
	};

	/**
	 * Set a node style property.
	 * @param property Style property name
	 * @param value Style value
	 */
	this.setNodeStyle = function(property, value) {

		if (!(property in this._style.node)) throw "No such property: " + property;

		this._style.node[property] = value;

		return this;
	};

	/**
	 * Set node style properties from a mapping.
	 * @param obj Mapping of style property names to values
	 */
	this.setNodeStyles = function(obj) {
		for (var property in obj) {
			this.setNodeStyle(property, obj[property]);
		}
		return this;
	};

	/**
	 * Get edge style property.
	 * @param property Style property name
	 * @return value
	 */
	this.getEdgeStyle = function(property) {
		if (!(property in this._style.edge)) throw "No such property: " + property;

		return this._style.edge[property];
	};

	this.setEdgeStyle = function(property, value) {
		if (!(property in this._style.edge)) throw "No such property: " + property;

		this._style.edge[property] = value;

		return this;
	};

	this.setEdgeStyles = function(obj) {
		for (var property in obj) {
			this.setEdgeStyle(property, obj[property]);
		}
		return this;
	};
	
	/** 
	 * Load styles from a style mapping object.
	 *
	 * The object can have "nodes", "edges" and "global" fields.
	 */
	this.loadStyles = function(obj) {
		this.setNodeStyles(obj.nodes);
		this.setEdgeStyles(obj.edges);
		this.setGlobalStyles(obj.global);
	}


	this._dataSchema = {nodes:[], edges: []};


	/**
	 * Return the network model as an object.
	 * @return The network model as a JavaScript object.
	 */
	this.getModel = function() {
		var nodeList = [];
		for (var nodeId in this._nodes) {
			nodeList.push(this._nodes[nodeId].getModel());
		}
		var edgeList = [];
		for (var edgeId in this._edges) {
			edgeList.push(this._edges[edgeId].getModel());
		}

		return {nodes: nodeList, edges: edgeList};
	};

	/**
	 * Return a node by id.
	 * @return {Node} node
	 */
	this.getNode = function(id) {
		return id in this._nodes ? this._nodes[id] : null;
	};

	/**
	 * Return an edge by id.
	 * @return {Edge} edge
	 */
	this.getEdge = function(id) {
		return id in this._edges ? this._edges[id] : null;
	}
	
	/**
	 * Return a list of existing nodes.
	 */
	this.getNodes = function() {
		var result = [];
		for (var nodeId in this._nodes) {
			result.push(this._nodes[nodeId]);
		}
		return result;
	}
	
	/**
	 * Return a list of existing edges.
	 */
	this.getEdges = function() {
		var result = [];
		for (var edgeId in this._edges) {
			result.push(this._edges[edgeId]);
		}
		return result;
	}

	/**
	 * Draw the network visualization.
	 */
	this.draw = function() {

		for (var nodeId in this._nodes) {
			this._nodes[nodeId]._draw();
		}
		
		for (var edgeId in this._edges) {
			this._edges[edgeId]._draw();
		}
		
		this._bg.attr("fill", this._style.global.backgroundColor).toBack();

		this._canvas.safari();
		return this;
	};

	/**
	 * Get all the edges from the network.
	 * @return {array} List of edges.
	 */
	this.getEdges = function() {
		var result = [];
		for (var id in this._edges) {
			result.push(this._edges[id]);
		}
		return result;
	};

	/**
	 * Get all the nodes from the network.
	 * @return {array} List of nodes.
	 */
	this.getEdges = function() {
		var result = [];
		for (var id in this._nodes) {
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
		newNode._id = id;
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
		if (source == null || target == null) throw "addEdge: Source and target are required arguments; got "+ source + " and " + target;
		newEdge._id = id;
		newEdge._source = source;
		newEdge._target = target;
		newEdge._source._edges.push(newEdge);
		newEdge._target._edges.push(newEdge);

		this._edges[id] = newEdge;
		source._calculateEdgeOffsets(target);
		return newEdge;
	}
	
	/**
	 * Remove all elements (nodes and edges) from the visualisation.
	 */
	this.clear = function() {
		for (var nodeId in this._nodes) {
			this._nodes[nodeId].remove();
		}
		
		for (var edgeId in this._edges) {
			this._edges[edgeId].remove();
		}

		this._edges = {};
		this._nodes = {};
	}
	/**
	 * Load a graph from a data object.
	 */
	this.loadGraph = function(graph) {
		this.clear();
		var nodes = graph.nodes || [];
		var edges = graph.edges || [];
		
		for (var i = 0, len = nodes.length; i < len; i++) {
			var node = this.addNode(nodes[i].id);
			node.setStyles(nodes[i].style);
			node.setPosition(nodes[i].x, nodes[i].y);
			//node.setLabel(nodes[i].label);
		}
		
		for (var j = 0, len = edges.length; j < len; j++) {
			var source = this.getNode(edges[j].source);
			var target = this.getNode(edges[j].target);
			var edge = this.addEdge(edges[j].id, source, target);
			edge.setStyles(edges[j].style);
			//edge.setLabel(edges[j].label);	
		}
	};
	
	this._dragStart = function(x, y) {
		this._dragX = this._offsetX;
		this._dragY = this._offsetY;
	}
	
	this._dragMove = function(x, y) {
		this._offsetX = this._dragX + x;
		this._offsetY = this._dragY + y;
		this.draw();
	}
	
	
}

var Shapes = {};

Shapes.buildEllipse = function(x, y, w, h) {
	return populateString("M %,% a %,% % % % %,% z", [x, y - w, w, h, 0, 1, 1, -0.01, 0]);
}


Shapes.buildPolygon= function(x, y, points, angle, scale) {
	scale = scale || 1;
	angle = angle || 0;
	var cos = Math.cos(angle);
	var sin = Math.sin(angle);
	var pathPoints = [];
	for (var i = 0, len = points.length; i < len; i++) {
		pathPoints.push(((points[i][0] * cos - points[i][1] * sin) * scale) + x);
		pathPoints.push(((points[i][0] * sin + points[i][1] * cos) * scale) + y);
	}
	return "M " + pathPoints[0] + " " + pathPoints[1] + " L " + pathPoints.slice(2).join(" ") + " z";
};


Shapes.buildArrow = function(x, y, w, h, angle, scale) {
	return this.buildPolygon(x, y, [[0,0], [-h, -w/2], [-h, w/2]], angle, scale);
}

Shapes.buildDiamond = function(x, y, w, h) {
	return this.buildPolygon(x, y, [[-w, 0], [0, -h], [w, 0], [0, h]]);
}

Shapes.buildRectangle = function(x, y, w, h) {
	return this.buildPolygon(x, y, [[-w, -h], [-w, h], [w, h], [w, -h]]);
}

var Util = {};

/**
 * Create a delegate function, which calls a given method on the target object
 * @param {object} targetObject Object containing the method to be called.
 * @param {string} methodName Name of the method which is a member of the target object.
 * @return {function} Delegate function
 */
Util.delegate = function(object, methodName) {
	return function() {
		object[methodName].apply(object, arguments);
	}
}

/**
 * Element parent class for nodes and edges.
 */
var Element = function() {

	this._styleOverride = {
		hover: {
			"color": "hoverColor",
			"borderColor": "hoverBorderColor",
			"opacity": "hoverOpacity"
			},
		drag: {

			"color": "dragColor",
			"bordeRcolor": "dragBorderColor",
			"opacity": "dragOpacity"
		},
		selected: {
			"color" : "selectedColor",
			"borderColor": "selectedBorderColor",
			"opacity": "selectedOpacity"
		}
	};
	
	this.toggleSelected = function() {
		if (this._selected) {
			this.deselect();
		} else {
			this.select();
		}
	}
	
	this.select = function() {
		this._selected = true;
		this._triggerEvent("selected");
	}
	
	this.deselect = function() {
		if (this._selected) {
			
			this._selected = false;
			this._triggerEvent("deselected");
			this._draw();
		}
	}
	

	this.setStyle = function(property, value) {
		if (property in this._visualization._style[this._group]) {
			this._style[property] = value;
		} else {
			throw "No such property: " + property;
		}
		this._draw();
	};

	this.setStyles = function(obj) {
		for (var property in obj) {
			this.setStyle(property, obj[property]);
		}
	};

	this.getStyle = function(property) {
		if (property in this._style) {
			return this._style[property];
		} else {
			throw "No such property: " + property;
		}
	};

	this.getStyles = function() {
		var result = {};
		for (property in this._style) {
			result[property] = this._style[property];
		}
		return result;
	};

	this.getAppliedStyle = function(property) {
		if (property in this._style) {
			return this._style[property];
		} else if (property in this._visualization._style[this._group]) {
			return this._visualization._style[this._group][property];
		} else {
			throw "No such property: " + property;
		}
	};

	this.getAppliedStyles = function() {
		var result = {};
		for (property in this._visualization._style[this._group]) {
			result[property] = this.getAppliedStyle(property);
		}
		return result;
	};

	this.getRenderedStyle = function(property) {
		var value = this.getAppliedStyle(property);
		if (this._drag && property in this._styleOverride.drag) {
			value = this.getAppliedStyle(this._styleOverride.drag[property]) || value;
		}
		if (this._hover && property in this._styleOverride.hover) {
			value = this.getAppliedStyle(this._styleOverride.hover[property]) || value;
		}
		if (this._selected && property in this._styleOverride.selected) {
			var value = this.getAppliedStyle(this._styleOverride.selected[property]) || value;
		}
		return value;
	};

	this.getRenderedStyles = function(property) {
		var result = {};
		for (property in this._visualization._style[this._group]) {
			result[property] = this.getRenderedStyle(property);
		}
		return result;
	};


	this.addListener = function(event, callback) {
		var listeners = this._listeners[event];
		if (listeners) {
			listeners.push(callback);
		} else {
			if (this._eventTypes.indexOf(event) !== -1) {
				this._listeners[event] = [callback];
			} else {
				throw "Not a valid event: " + event;
			}
		}
	};

	this._triggerEvent = function(event, parameters) {

		var listeners = this._listeners[event];
		if (listeners) {
			for (var i = 0, len = listeners.length; i < len; i++) {
				listeners[i].apply(this, parameters);
			}
		}
	}
	
	this.setLabel = function(value) {
		this._label = value;
	};

	this.getLabel = function(value) {
		if (typeof this._label == "function") {
			return this._label();
		} else {
			return this._label;
		}
	};

};


/**
 * Node object
 *
 */
var Node = function(vis) {

	this._visualization = vis;
	this._elem = null;
	this._elemLabel = null;
	this._group = "node";
	this._listeners = {};
	this._eventTypes = ["dragStart", "dragEnd", "hoverStart", "hoverEnd", "click", "select", "deselect"];
	this._label = function() { return this._id; };
	
	this._hover = false;
	this._selected = false;

	this._justDragged = false;

	this._edges = [];
	this._edgeSpacing = 40;
	
	this._getRenderedPosition = function() {
		return {x: this._x + this._visualization._offsetX, y: this._y + this._visualization._offsetY};
	}
	
	this._calculateEdgeOffsets = function(otherNode) {
		var edges = [];
		var edgeOffset = 0;
		for (var i = 0, len = this._edges.length; i < len; i++) {
			var edge = this._edges[i];
			if (edge._source == otherNode || edge._target == otherNode)
			edges.push(edge);
		}
		var baseOffset = -this._edgeSpacing / 2;
		if (edges.length % 2 != 0) {

			edges[0]._offset = 0;
			edges = edges.slice(1);
			baseOffset = 0;
		}
		
		
		for (var j = 0, len = edges.length; j < len; j++) {
			var offset = baseOffset + (Math.floor(j/2)+1) * this._edgeSpacing;
			if (j % 2 == 0) offset *= -1;

			if (edges[j]._target === this) offset *= -1;
			edges[j]._offset = offset;

		}
		
	}
	
	
	this._getRadius = function() {
		var radius;
					
		switch (this.getRenderedStyle("shape")) {
			case "DIAMOND":
			case "ELLIPSE":
				radius = this.getRenderedStyle("size") + 3;
				break;
			case "RECTANGLE":
			default:
				radius = this.getRenderedStyle("size") * 1.3;
				break;
		}
		return radius;
	}
	
	this._getPath = function() {
		var path;
		var shape = this.getRenderedStyle("shape");
		var size = this.getRenderedStyle("size");
		var position = this._getRenderedPosition();
		switch (this.getRenderedStyle("shape")) {
			case "ELLIPSE":
				path = Shapes.buildEllipse(position.x, position.y, size, size * this.getRenderedStyle("ratio"));
				break;
				
			case "RECTANGLE":
				path = Shapes.buildRectangle(position.x, position.y, size, size * this.getRenderedStyle("ratio"));
				break;
			
			case "DIAMOND":
				path = Shapes.buildDiamond(position.x, position.y, size, size * this.getRenderedStyle("ratio"));
				break;
		}
		
		return path;
		
	};



	// Necessary values
	this._id = null;
	this._x = 0;
	this._y = 0;

	this._data = {};
	this._style = {};




	// Temporary vars for dragging
	this._dragOriginX = null;
	this._dragOriginY = null;

	//this._edges = [];

	this.getEdges = function() {
		return this._edges.slice(); // array.slice() make a (shallow) copy of the array
	}

	this.getNeighbors = function() {
		var neighbors = [];
		for (var i = 0, len = this._edges.length; i < len; i++) {
			var edge = this._edges[i];
			neighbors.push(edge._source === this ? edge._target : edge._source);
		}
		return neighbors;
	};
	
	this.getForwardNeighbors = function() {
		var neighbors = [];
		for (var i = 0, len = this._edges.length; i < len; i++) {
			var edge = this._edges[i];
			if (edge._source === this) {
				neighbors.push(edge);
			}
		}
		return neighbors;
	};
	
	this.getBackwardNeighbors = function() {
		var neighbors = [];
		for (var i = 0, len = this._edges.length; i < len; i++) {
			var edge = this._edges[i];
			if (edge._target === this) {
				neighbors.push(edge);
			}
		}
		return neighbors;
	};

	this.getPosition = function() {
		return {x: this._x, y: this._y};
	};

	this.setPosition = function(x, y) {
		this._x = x;
		this._y = y;
		this._draw();
	};
	


	this._dragStart = function() {
		this._dragOriginX = this._x;
		this._dragOriginY = this._y;
		this._elem.toFront();
		this._drag = true;
		this._triggerEvent("dragStart");
	};

	this._dragEnd = function() {
		this._drag = false;

		this._triggerEvent("dragEnd");
	};

	this._dragMove = function(x, y) {
		this._justDragged = true;
		var newX = this._dragOriginX + x;
		var newY = this._dragOriginY + y;

		/*
		// Clamp coordinates to canvas size (hardcoded for this test demo)
		if (newX < 0) newX = 0;
		if (newX > 400) newX = 400;
		if (newY < 0) newY = 0;
		if (newY > 400) newY = 400;
		*/
		this._x = newX;
		this._y = newY;
		this._draw();
	};

	this._onClick = function() {
		if (this._justDragged) {
			this._justDragged = false;
			return;
		}
		this._visualization.deselect();
		this.toggleSelected();
		this._draw();
		this._triggerEvent("click");
		this._hoverEnd();
	};


	this._hoverStart = function(e, f) {


		this._hover = true;
		this._draw();
		this._triggerEvent("hoverStart");
	};

	this._hoverEnd = function () {
		this._hover = false;
		this._draw();
		this._triggerEvent("hoverEnd");
	};

	this._draw = function() {
		if (this._elem == null) {
				this._elem = this._visualization._canvas.path("")
					.attr({"cursor":"pointer"})
					.hover(Util.delegate(this, "_hoverStart"), Util.delegate(this, "_hoverEnd"))
					.drag(Util.delegate(this, "_dragMove"), Util.delegate(this, "_dragStart"), Util.delegate(this, "_dragEnd"))
					.click(Util.delegate(this, "_onClick"));
		}
		
		var label = this.getLabel();
		if (label) {
			var labelSize = this.getRenderedStyle("labelSize");
			if (labelSize) {
				if (this._elemLabel == null) {
					this._elemLabel = this._visualization._canvas.text(0, 0, "").attr({"font-weight": "bold"});
					this._elemLabel.node.setAttribute("pointer-events", "none");
				}
				this._elemLabel.attr({
					"text": label,
					"font-size": this.getRenderedStyle("labelSize"),
					"fill": this.getRenderedStyle("labelColor"),
					"x": this._x + this._visualization._offsetX,
					"y": this._y + this._visualization._offsetY,
				}).toFront();
			}
		}
		
		
		var attrMap = {
			"fill": "color",
			"stroke": "borderColor",
			"stroke-width": "borderWidth",
			"opacity": "opacity"
		};

		var attrList = ["fill", "stroke", "stroke-width", "opacity"];

		var attr = {"path": this._getPath()};

		for (var attrName in attrMap) {
			attr[attrName] =  this.getRenderedStyle(attrMap[attrName]);
		}
		
		for (var i = 0, len = this._edges.length; i < len; i++) {
			this._edges[i]._draw();
		}

		this._elem.attr(attr);

	};
	

	this.getModel = function() {
		var model = {};
		for (field in this._data) {
			model[field] = this._data[field];
		}
		model.id = this._id;
		var pos = this.getPosition();
		model.x = pos.x;
		model.y = pos.y;
		var styles = this.getStyles();
		if (styles) model.styles = styles;
		return model;
	};

	
	this.remove = function() {
		if (this._elem) this._elem.remove();
		if (this._elemLabel) this._elemLabel.remove();
	}
}
Node.prototype = new Element();


/**
 * Edge object
 *
 */
var Edge = function(vis) { 

	this._visualization = vis;
	this._elem = null;
	this._elemLabel = null;
	this._elemArrowForward = null;
	this._elemArrowBackward = null;
	this._group = "edge";
	this._offset = 0; // Offset used for drawing multiple edges between two nodes
	this._label = "";
	this._listeners = {};
	
	// Necessary values
	this._id = null;
	this._source = null;
	this._target = null;
	

	// Containers
	this._data = {};
	this._style = {};
	
	this.remove = function() {
		if (this._elem) this._elem.remove();
		this._source = null;
		this._target = null;
	};
	

	this._getSvgPath = function() {

		if (this._offset == 0) {
			return this._getStraightPath();
		} else {
			return this._getCurvedPath(this._offset);
		}
	};

	this._draw = function() {

		if (this._source == null || this._target == null) throw "Invalid edge members";
		if (this._elem == null) {
				this._elem = this._visualization._canvas.path(this._getSvgPath()).toBack().click(Util.delegate(this, "_onClick")).attr("cursor", "pointer");
		}
		
		var dashArray = {
			"SOLID": "",
			"DASH": "-",
			"DOT": ".",
			"LONG_DASH": "--"
		}[this.getRenderedStyle("style")];


		var attr = {
			// Update position
			"path": this._getSvgPath(),
			"stroke-dasharray" : dashArray,
		};
		

		var attrMap = {
			"stroke": "color",
			//"fill": "color",
		
			"stroke-width": "width",
			"stroke-opacity": "opacity"
		};

		


		for (var attrName in attrMap) {
			attr[attrName] =  this.getRenderedStyle(attrMap[attrName]);
		}

		
		this._elem.attr(attr);
	};
	
	this._onClick = function() {
		alert("Clicked edge '" + this._id + "' between '" + this._source._id + "' and '" + this._target._id + "'");
	}

	this.getModel = function() {
		var model = {};
		for (field in this._data) {
			model[field] = this._data[field];
		}
		model.id = this._id;
		model.source = this._source._id;
		model.target = this._target._id;
		var styles = this.getStyles();
		if (styles) model.styles = styles;
		return model;
	};
	

			
	this._getStraightPath = function() {
				// first point
				var a = this._source._getRenderedPosition();
				var ar = this._source._getRadius();
				
				// second point
				var b = this._target._getRenderedPosition();
				var br = this._target._getRadius();
			
				// Get vector & length	
				var dx = b.x - a.x;
				var dy = b.y - a.y;
				var length = Math.sqrt(dx * dx + dy * dy);
				
				// Normalize
				dx /= length;
				dy /= length;
				
				// Move the endpoints by the size of the radius
				var nax = a.x + dx * ar
				var nay = a.y + dy * ar;

				var nbx = b.x - dx * br;
				var nby = b.y - dy * br;
	
				var path = populateString("M %,% L %,%", [nax, nay, nbx, nby]);
	
				if (this.getRenderedStyle("forwardArrowShape") == "DELTA") path += " " + Shapes.buildArrow(nax, nay, 6, 10, Math.atan2(-dy, -dx), 1);
				if (this.getRenderedStyle("backwardArrowShape") == "DELTA") path += " " + Shapes.buildArrow(nbx, nby, 6, 10, Math.atan2(dy, dx), 1);
				return path
	};
	
	this._getCurvedPath = function(offset) {
					
				var a = this._source._getRenderedPosition();
				var ar = this._source._getRadius();
				
				var b = this._target._getRenderedPosition();
				var br = this._target._getRadius();
					
				// Vector between two points
				var dx = b.x - a.x;
				var dy = b.y - a.y;
				
				// Halved vector
				var hdx = dx / 2;
				var hdy = dy / 2;
				
				// Perpendicular vector
				var pdx = -dy;
				var pdy = dx;
				
				// Normalized perpendicular vector
				var pdlength = Math.sqrt(pdx * pdx + pdy * pdy);
				pdx /= pdlength;
				pdy /= pdlength;
				
				// Obtain the control point vector A
				var acx = hdx + pdx * offset;
				var acy = hdy + pdy * offset;
				
				// Obtain the control point
				var cx = a.x + acx;
				var cy = a.y + acy;
				
				// Obtain the control point vector B
				var bcx = cx - b.x;
				var bcy = cy - b.y;
				
				// Obtain the A endpoint
				var aclength = Math.sqrt(acx * acx + acy * acy);
				acx *= ar / aclength;
				acy *= ar / aclength;
				var apx = a.x + acx;
				var apy = a.y + acy;
				
				// Obtain the B endpoint
				
				bcx *= br / aclength;
				bcy *= br / aclength;
				var bpx = b.x + bcx;
				var bpy = b.y + bcy;
				

				var path = populateString("M %,% Q %,% %,%", [apx, apy, cx, cy, bpx, bpy]);
				
				if (this.getRenderedStyle("forwardArrowShape") == "DELTA") path += " " + Shapes.buildArrow(apx, apy, 6, 10, Math.atan2(-acy, -acx), 1);
				if (this.getRenderedStyle("backwardArrowShape") == "DELTA") path += " " + Shapes.buildArrow(bpx, bpy, 6, 10, Math.atan2(-bcy, -bcx), 1);
				
				return path;
			
	};
			/*
	this._getCurvedPathCubic = function(theta) {
				
				var ax = this._source._x;
				var ay = this._source._y;
				var ar = this._source.getRenderedStyle("size");
				
				var bx = this._target._x;
				var by = this._target._y;
				var br = this._target.getRenderedStyle("size");
				
				// ax ay ar		Circle A
				// bx by br		Circle B
				// dx dy		Vector A -> B
				// arx ary		Rotated unit vector A
				// brx bry 		Rotated unit vector B
				// apx apy		Curve endpoint A
				// bpx bpy		Curve endpoint B
				// acx acy		Curve control point A
				// bcx bcy		Curve control point B
				
				// Margin
				ar += 3;
				br += 3;
				
				
				
				// Vector between two points
				var dx = bx - ax;
				var dy = by - ay;
				
				// Normalized
				var length = Math.sqrt(dx * dx + dy * dy);
				dx /= length;
				dy /= length;
				
				//var curvatureFactor = 30 + Math.abs(theta);
				var curvatureFactor = length / 10 + Math.abs(theta);
				
				// Rotate the vector
				theta = Raphael.rad(theta);
				var arx = dx * Math.cos(theta) - dy * Math.sin(theta);
				var ary = dx * Math.sin(theta) + dy * Math.cos(theta);
				
				var brx = (-dx) * Math.cos(-theta) - (-dy) * Math.sin(-theta);
				var bry = (-dx) * Math.sin(-theta) + (-dy) * Math.cos(-theta);
				
				var apx = ax + arx * ar;
				var apy = ay + ary * ar;
				var bpx = bx + brx * br;
				var bpy = by + bry * br;
				
				
				
				//ar = br = 1;
				var acx = ax + arx * (ar + curvatureFactor);
				var acy = ay + ary * (ar + curvatureFactor);
				var bcx = bx + brx * (br + curvatureFactor);
				var bcy = by + bry * (br + curvatureFactor);
				
				var thetaA = Math.atan(bry, brx);
				
				//arrowHead(apx, apy, Math.atan2(-ary, -arx), 1.5);
				//arrowHead(bpx, bpy, Math.atan2(-bry, -brx), 1.5);
				
				//paper.circle(acx, acy, 3);
				//paper.circle(bcx, bcy, 3);
				
				return populateString("M %,% C %,% %,% %,%", [apx, apy, acx, acy, bcx, bcy, bpx, bpy]);
				
			
			};
	*/
	
}
Edge.prototype = new Element();

/**
 * Replace each instance of the placeholder token with a successive item from a list.
 */
function populateString(string, items) {
	var i = 0;
	return string.replace(/%/g, function() {
		return items[i++];
	});
}

