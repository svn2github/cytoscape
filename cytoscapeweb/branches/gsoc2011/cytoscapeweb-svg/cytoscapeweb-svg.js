(function(window){
	/*
	 * Cytoscape Web SVG prototype
	 * By Marek Zaluski
	 *
	 * Google Summer of Code 2011
	 */

	var SvgTool = {
		createSvgElement: function(tag, parent, attributes) {
			attributes = attributes || {};
			var elem = document.createElementNS("http://www.w3.org/2000/svg", tag);
			this.setElementAttributes(elem, attributes);
			if (parent) parent.appendChild(elem);
			return elem;
		},
		
		setElementAttributes: function(elem, attributeMap) {
			for (var attribute in attributeMap) {
				var value = attributeMap[attribute];
				if (value != null)
				elem.setAttribute(attribute, value);
			}
		}
	};
	 
	 
	/** Drag & click events */
	function MouseEventHandler() {
		this._onMouseup = function(event) {

			if (this._dragging) {
				this._onDragEnd();
			} else {
				this._onClick();
			}
			document.documentElement.removeEventListener("mousemove", this._mousemoveListenerRef, false);
			document.documentElement.removeEventListener("mouseup", this._mouseupListenerRef, false);
			this._dragging = false;
			this._justDropped = true;
		};
					

		this._onMousedown = function(event) {
			event.preventDefault();
			event.stopPropagation();

			this._dragOriginX = event.clientX + document.documentElement.scrollLeft;
			this._dragOriginY = event.clientY + document.documentElement.scrollTop;

			// A reference to the listener must be kept in order to be able to remove it later.
			this._mousemoveListenerRef = Util.delegate(this, "_onMousemove");
			document.documentElement.addEventListener("mousemove", this._mousemoveListenerRef, false);
			this._mouseupListenerRef = Util.delegate(this, "_onMouseup");		
			document.documentElement.addEventListener("mouseup", this._mouseupListenerRef, false);
			return false;
		};
					
		this._onMousemove = function(event) {
		
			var dx = event.clientX + document.documentElement.scrollLeft - this._dragOriginX;
			var dy = event.clientY + document.documentElement.scrollTop - this._dragOriginY ;
			
			
			if (!this._dragging) {
				this._onDragStart();
			}
			
			this._onDragMove(dx, dy);


			this._dragging = true;

		};
		
			this._onClick = function () {
			message("Clicked on node " + this._id);
		};
		
		this._onDragStart = function() {};
		
		this._onDragMove = function(dx, dy) {};
		
		this._onDragEnd = function() {};
		
		this._enableMouseEvents = function(elem) {
			elem.addEventListener("mousedown", Util.delegate(this, "_onMousedown"), false);
		};
		
	 };
	/**
	 * Network visualization instance.
	 * Represents an instance of the network visualization.
	 */
	function Visualization(container, height, width) {

		this._width = height;
		this._height = width;
		//this._canvas = Raphael(containerId, 640, 480);
		
		// -------- SVG --------
		this._containerElem = typeof container == "string" ? document.getElementById(container) : container;


		
		this._svgElem = SvgTool.createSvgElement("svg", this._containerElem, {
			"xmlns": "http://www.w3.org/2000/svg",
			"version": "1.1",
			"height": height,
			"width": width
		});
		
		this._svgMainGroup = SvgTool.createSvgElement("g", this._svgElem, {"transform": "scale(0.5) translate(200, 200)"});
		
		this._svgEdgeGroup = SvgTool.createSvgElement("g", this._svgMainGroup);
		this._svgNodeGroup = SvgTool.createSvgElement("g", this._svgMainGroup);	
		this._svgLabelGroup = SvgTool.createSvgElement("g", this._svgMainGroup);
		
		this._createLabelElement = function () {
			return SvgTool.createSvgElement("text", this._svgLabelGroup);
		};
		
		this._createEdgePathElement = function() {
			return SvgTool.createSvgElement("path", this._svgEdgeGroup);
		};
		
		this._createNodePathElement = function() {
			return SvgTool.createSvgElement("path", this._svgNodeGroup);
		};
		
		this._removeNodePathElement = function(elem) {
			this._svgNodeGroup.removeChild(elem);
		};
		
		this._removeEdgePathElement = function(elem) {
			this._svgEdgeGroup.removeChild(elem);
		};
		
		this._removeLabelElement = function(elem) {
			this._svgLabelGroup.removeChild(elem);
		};
		// --------

		this._edges = {};
		this._nodes = {};
		
		this._dragX = 0;
		this._dragY = 0;
		
		// Viewport offset
		this._offsetX = 0;
		this._offsetY = 0;
		this._zoom = 1;
		
		// FDL simulation
		this._simulating = false;
		this._particleSystem = null;
		this._autoStoppedSimulating = false;
		
		this._clearSimulation = function() {
			this._simulating = false;
			if (this._particleSystem) {
				this._particleSystem.stop();
				this._particleSystem = null;
			}
		};
		
		this._startSimulating = function(repulsion, stiffness, friction) {
			this._simulating = true;
			if (!this._particleSystem) {
				this._particleSystem = this._particleSystem = arbor.ParticleSystem();
				var nodes = vis.getNodes();
				for (var i = 0; i < nodes.length; i++) {
					this._particleSystem.addNode(nodes[i]._id); 
				}
			
				var edges = vis.getEdges();
				for (var i = 0; i < edges.length; i++) {
					this._particleSystem.addEdge(edges[i]._source._id, edges[i]._target._id); 
				}
			}
			
			this._particleSystem.screenSize(this._width, this._height);
			this._particleSystem.screenPadding(25);
			this._particleSystem.parameters({
				repulsion: repulsion || 1000,
				stiffness: stiffness || 300,
				fricton: friction || 0.3,	
				gravity: false
			});
			this._particleSystem.renderer = {
				init: function() {},
				redraw: Util.delegate(this, "_arborRedraw")
			};

			this._particleSystem.start();
		};
		
		this._stopSimulating = function() {
			this._simulating = false;
			this._particleSystem.stop();
		};
		
		this._autoStartSimulating = function() {
			if (this._autoStoppedSimulating) {
				this._startSimulating();
			}
		};
		
		this._autoStopSimulating = function() {
			if (this._simulating) {
				this._autoStoppedSimulating = true;
				this._stopSimulating();
			}
		};
		
		this._arborRedraw = function() {
			var t = this;
			this._particleSystem.eachNode(function(node, pt) {
				var n = t.getNode(node.name);
				n._x = pt.x;
				n._y = pt.y;
				pt.x = pt.x;
				pt.y = pt.y;
			});
			this.draw();
		};
		
		
		this.changeZoom = function(i) {
			this._zoom += i/100;
			if (this._zoom < 0.01) this._zoom = 0.01;
			this.draw();
		};
		
		// Dragging 
		this._dragElem = null;
		this._dragging = false;
		this._justDropped = false;
		this._holding = false;
		this._dragOriginX = 0;
		this._dragOriginY = 0;
		
		this._panningOriginX = 0;
		this._panningOriginY = 0;
		this._mousemoveListenerRef = null;

		this._onDragStart = function() {
			this._panningOriginX = this._offsetX;
			this._panningOriginY = this._offsetY;
		};

		this._onDragMove = function(dx, dy) {
			this._offsetX = this._panningOriginX + dx;
			this._offsetY = this._panningOriginY + dy;
			this.draw();
		};

		this._onDragEnd = function() {

		};

		this._onClick = function() {
			message("You clicked on the visualisation");
		};

		this._enableMouseEvents(this._svgElem);


		this._getTransform = function(offsetX, offsetY, scale) {
			return "translate(" + offsetX + "," + offsetY + ") scale(" + scale + ")";
		};

		this._styleDefaults = {
			global: {},
			node: {},
			edge: {}
		};

		// Style defaults
		this._style = {
			global: {
				backgroundColor: "#eeeeee"
			},

			node: {
				"shape": "ELLIPSE",
				"ratio": 1,
				"size": 18,
				"color": "#e0e0e0",
				"borderColor": "#666666",
				"borderWidth": 1,
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
				"labelDisplay": false,
				"labelSize": 10,
				"labelColor": "#000000"
			},

			edge: {
				"color": "#404040",
				"width": 3,
				"opacity": 1,
				"style": "SOLID",
				"labelDisplay": false,
				"targetArrowShape": "DELTA",
				"targetArrowColor": "black",
				"sourceArrowShape": "T",
				"sourceArrowColor": "black"
			}
		};
		

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
			
			SvgTool.setElementAttributes(this._svgMainGroup, {
				"transform": this._getTransform(this._offsetX, this._offsetY, this._zoom),
			});
			
			SvgTool.setElementAttributes(this._svgElem, {
				"viewport-fill": "#ff00f0" // Why does viewport-fill not work?
			});
			//this._bg.attr("fill", this._style.global.backgroundColor).toBack();

			return this;
		};

		

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
				node._data = nodes[i].data || {};
			}
			
			for (var j = 0, len = edges.length; j < len; j++) {
				var source = this.getNode(edges[j].source);
				var target = this.getNode(edges[j].target);
				var edge = this.addEdge(edges[j].id, source, target);
				edge.setStyles(edges[j].style);
				edge._data = edges[i].data || {};
			}
		};
		
		this._dragStart = function(x, y) {
			this._dragX = this._offsetX;
			this._dragY = this._offsetY;
		};
		
		this._dragMove = function(x, y) {
			this._offsetX = this._dragX + x;
			this._offsetY = this._dragY + y;
			this.draw();
		};
		
		this._dragEnd = function() {

		};
	}
	Visualization.prototype = new MouseEventHandler();


	/**
	 * Paths are in SVG PathData format. See http://www.w3.org/TR/SVG/paths.html#PathData
	 */
	var Paths = {
		// Node shapes
		ellipse: 	"M 0,-10 a 10,10 0 1 1 -0.01,0 z", // This is the path equivalent of a <circle> element
		diamond: 	"M -10,0 0,-10 10,0 0,10 z",
		rectangle: 	"M -10,-10 -10,10 10,10 10,-10 z",
		// Arrow shapes
		delta: 		"M 0,0 -10,-5, -10,5 z",
		t: 		"M 0,-5 0,5 -2,5 -2,-5 z"

	};

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
				var prop = this._style[property];
			} else if (property in this._visualization._style[this._group]) {
				var prop = this._visualization._style[this._group][property];
			} else {
				throw "No such property: " + property;
			}
			if (typeof prop == "function") {
				prop = prop(this._data);
			}
			return prop;
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
		
		this.data = function(field, value) {
			if (arguments.length >= 2) {
				this._data[field] = value;
			};
			if (arguments.length == 1) {
				return this._data[field];
			}
		};

	};
	Element.prototype = new MouseEventHandler();

	/**
	 * Node object
	 *
	 */
	var Node = function(vis) {

		this._visualization = vis;
		this._elem = null;
		this._labelElem = null;
		this._group = "node";
		this._data = {};
		this._listeners = {};
		this._eventTypes = ["dragStart", "dragEnd", "hoverStart", "hoverEnd", "click", "select", "deselect"];
		this._label = function() { return this._id; };
		
		this._hover = false;
		this._selected = false;

		this._justDragged = false;

		this._edges = [];
		this._edgeSpacing = 40;
		
		this._onClick = function () {
			message("Clicked on node " + this._id);
		};
		
		this._onDragStart = function() {
			if (this._visualization._simulating) {
				this._visualization._particleSystem.getNode(this._id).fixed = true;
				this._visualization._particleSystem.screenStep(0);	
			}
			this._dragStartX = this._x;
			this._dragStartY = this._y;
			//this._visualization._autoStopSimulating();
		};
		
		this._onDragMove = function(dx, dy) {
			dx /= this._visualization._zoom;
			dy /= this._visualization._zoom;
			this.setPosition(this._dragStartX + dx, this._dragStartY + dy);
			this._visualization._particleSystem.start();
		};
		
		this._onDragEnd = function() {
			if (this._visualization._simulating) {
				this._visualization._particleSystem.getNode(this._id).fixed = false;
				this._visualization._particleSystem.screenStep(0.01);
			}
		};
		

		
		this._getRenderedPosition = function() {
			return {x: this._x, y: this._y};
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
					radius = (this.getRenderedStyle("size") + this.getRenderedStyle("borderWidth"));
					break;
				case "RECTANGLE":
				default:
					radius = this.getRenderedStyle("size") * 1.3;
					break;
			}
			return radius;
		}
		
		/**
		 * Generate the string for the svg transform attribute. Performs a translate and scale of the source path.
		 */
		this._getTransform = function() {
			return "translate(" + this._x + "," + this._y + ")" + "scale(" + (this.getRenderedStyle("size")/10) + ")" ;
		};
		
		this._getPath = function() {
			var path;
		
			switch (this.getRenderedStyle("shape")) {
			
				case "ELLIPSE":
					path = Paths.ellipse;
					break;
					
				case "RECTANGLE":
					path = Paths.rectangle;
					break;
				
				case "DIAMOND":
					path = Paths.diamond
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
			if (this._visualization._simulating) {
				var node = this._visualization._particleSystem.getNode(this._id);
				var p =  this._visualization._particleSystem.fromScreen(arbor.Point(x, y));
				node.p = p;
			}
			this._draw();
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
				this._elem = this._visualization._createNodePathElement();
				this._enableMouseEvents(this._elem);
				this._labelElem = this._visualization._createLabelElement();
				SvgTool.setElementAttributes(this._labelElem, {
					"pointer-events": "none"
				});
			}

			
			var attrMap = {
				"fill": "color",
				"stroke": "borderColor",
				"stroke-width": "borderWidth",
				"opacity": "opacity"
			};

			var attr = {
				"d": this._getPath(),
				"transform": this._getTransform()
			};

			for (var attrName in attrMap) {
				attr[attrName] =  this.getRenderedStyle(attrMap[attrName]);
			}
			
			for (var i = 0, len = this._edges.length; i < len; i++) {
				this._edges[i]._draw();
			}
			
			SvgTool.setElementAttributes(this._elem, attr);
			
			this._labelElem.textContent = this.getLabel();
			SvgTool.setElementAttributes(this._labelElem, {
				"visibility": this.getRenderedStyle("labelDisplay") ? "visible" : "hidden",
				"x": this._x,
				"y": this._y,
				"dominant-baseline": "middle",
				"text-anchor": "middle"});
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
			if (this._elem) {
				this._visualization._removeNodePathElement(this._elem);
				if (this._labelElem) this._visualization._removeLabelElement(this._labelElem);
				
			}
			
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
		this._labelElem = null;
		this._targetArrowElem = null;
		this._sourceArrowElem = null;
		this._group = "edge";
		this._offset = 0; // Offset used for drawing multiple edges between two nodes
		this._label = function() {return this._id};
		this._data = {};
		this._listeners = {};
		
		// Necessary values
		this._id = null;
		this._source = null;
		this._target = null;
		

		// Containers
		this._data = {};
		this._style = {};
		
		this.remove = function() {
			if (this._elem) {
				this._visualization._removeEdgePathElement(this._elem);
			}
			if (this._targetArrowElem) {
				this._visualization._removeEdgePathElement(this._targetArrowElem);
			}
			if (this._sourceArrowElem) {
				this._visualization._removeEdgePathElement(this._sourceArrowElem);
			}
			if (this._labelElem) {
				this._visualization._removeLabelElement(this._labelElem);
			}
			this._source = null;
			this._target = null;
		};
		

		this._dashArrays = {
			"SOLID": "",
			"DASH": "5 5",
			"DOT": "1 5",
			"LONG_DASH": "10 5"
		};
		
		this._arrowShapes = {
			"DELTA": Paths.delta,
			"T": Paths.t
		};


		this._draw = function() {

			if (this._source == null || this._target == null) throw "Invalid edge members";
			if (this._elem == null) {
					this._elem = this._visualization._createEdgePathElement();
					//this._elem = this._visualization._canvas.path(this._getSvgPath()).toBack().click(Util.delegate(this, "_onClick")).attr("cursor", "pointer");
					this._targetArrowElem = this._visualization._createEdgePathElement();
					this._sourceArrowElem = this._visualization._createEdgePathElement();
					this._labelElem = this._visualization._createLabelElement();
			}


			if (this._offset == 0) {
				var points = this._getStraightPath();	
				var path = ["M", points[0], points[1], "L", points[4], points[5]].join(" ");
			} else {
				var points = this._getCurvedPath(this._offset);
				var path = ["M", points[0], points[1], "Q", points[2], points[3], points[4], points[5]].join(" "); 
				
			}
			
			
			var attr = {
				"d": path,
				"stroke-dasharray" : this._dashArrays[this.getRenderedStyle("style")],
				"fill": "none",
				"stroke-linecap": "square"
			};

		
			// Mapping of SVG attribute names to visual style property names.
			var attrMap = {
				"stroke": "color",
				"stroke-width": "width",
				"stroke-opacity": "opacity"
			};


			for (var attrName in attrMap) {
				attr[attrName] =  this.getRenderedStyle(attrMap[attrName]);
			}

			SvgTool.setElementAttributes(this._elem, attr);
			
			SvgTool.setElementAttributes(this._labelElem, {
				"visibility": this.getRenderedStyle("labelDisplay") ? "visible" : "hidden",
				"x": points[8],
				"y": points[9],
				"text-anchor": "middle",
				"dominant-baseline": "middle"
			});
			this._labelElem.textContent = this.getLabel();
			
			// Arrow head attributes
			var targetArrowPath = this._arrowShapes[this.getRenderedStyle("targetArrowShape")] || "none";
			var targetArrowAttr = {
				"fill": attr["stroke"],
				"stroke": "none",
				"opacity": attr["stroke-opacity"],
				"d": targetArrowPath,
				"transform": [ "rotate(", points[7] * 180 / Math.PI, points[4], points[5],")", "translate(", points[4], points[5], ")", "scale(", this.getRenderedStyle("width")/2, ")",].join(" ")
			};
			SvgTool.setElementAttributes(this._targetArrowElem, targetArrowAttr);
			
			var sourceArrowPath = this._arrowShapes[this.getRenderedStyle("sourceArrowShape")] || "none";
			var sourceArrowAttr = {
				"fill": attr["stroke"],
				"stroke": "none",
				"opacity": attr["stroke-opacity"],
				"d": sourceArrowPath,
				"transform": [ "rotate(", points[6] * 180 / Math.PI, points[0], points[1],")", "translate(", points[0], points[1], ")", "scale(", this.getRenderedStyle("width")/2, ")",].join(" ")
			}
			SvgTool.setElementAttributes(this._sourceArrowElem, sourceArrowAttr);

			
		};
		
		this._onClick = function() {
			message("Clicked edge '" + this._id + "' between '" + this._source._id + "' and '" + this._target._id + "'");
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

		this.getLabel = function(value) {
			if (typeof this._label == "function") {
				return this._label();
			} else {
				return this._label;
			}
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
					
					// Obtain midpoint
					var mpx = a.x + dx / 2;
					var mpy = a.y + dy / 2;				
					
					// Normalize
					dx /= length;
					dy /= length;
					
					// Move the endpoints by the size of the radius
					var nax = a.x + dx * ar
					var nay = a.y + dy * ar;

					var nbx = b.x - dx * br;
					var nby = b.y - dy * br;

					var angle = Math.atan2(dy, dx);
					return [nax, nay, null, null, nbx, nby, Math.atan2(-dy, -dx), Math.atan2(dy, dx), mpx, mpy];
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

					// Obtain the midpoint of the curve
					var mdx = hdx + (pdx * offset) / 2;
					var mdy = hdy + (pdy * offset) / 2;
					var mpx = a.x + mdx;
					var mpy = a.y + mdy;				

					// Format of result array:
					// [point1-x, point1-y, control-point-x, control-point-y, point2-x, point2-y, angle1, angle2, midpoint-x, midpoint-y]
					return [apx, apy, cx, cy, bpx, bpy, Math.atan2(-acy, -acx), Math.atan2(-bcy, -bcx), mpx, mpy];
		};

		
	}
	Edge.prototype = new Element();


	function ContinuousVisualMapper(fieldName, inputMin, inputMax, outputMin, outputMax) {
		return function(data) {
			var input = data[fieldName];
			var p = (input - inputMin)/inputMax;
			if (p < 0) p = 0;
			if (p > 1) p = 1; 

			return (p * outputMax) + outputMin;

		};
	}

	window.Visualization = Visualization;
	window.ContinuousVisualMapper = ContinuousVisualMapper;
	
})(window);