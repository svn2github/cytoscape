/**
 * Visualization class and network object model
 * 
 * Classes:
 *   Visualization (created with "new Visualization(raphaelCanvas)")
 *   Node (created with visualization.addNode)
 *   Edge (created with visualization.addEdge)
 *
 * This draft doesn't yet take into account VisualStyle objects.
 */

var Visualization = function(/* Raphael instance: */ canvas) {

	this.canvas = canvas;
	this.edges = [];
	this.nodes = [];

	
	this.addNode = function (attr) {
		var node = new Node(this.canvas);
		for (a in attr) {
			node[a] = attr[a];
		}
		node.update();
		return node;
		
	};
	
	this.addEdge = function(nodeA, nodeB, attr) {
		var edge = new Edge(this.canvas);
		edge.origin = nodeA;
		edge.target = nodeB;
		nodeA.edges.push(edge);
		nodeB.edges.push(edge);
		for (a in attr) {
			edge[a] = attr[a];
		}
		edge.update();
		
	}

};

var Edge = function (canvas) {
	this.id = "";
	this.styleClasses = [];
	
	this.raphaelCanvas = canvas;
	this.raphaelElement = null;
	
	this.color = "#995555";
	this.width = 2;
	
	this.origin = null;
	this.target = null;
	
	this.getSvgPath = function() {
		return populateString("M %,% L %,%", [this.origin.x, this.origin.y, this.target.x, this.target.y]);
	}
	
	this.update = function() {
		if (this.origin == null || this.target == null) return;
		if (this.raphaelElement == null) {
				this.raphaelElement = this.raphaelCanvas.path(this.getSvgPath()).toBack();
		}
		
		var attr = {
			// Update position
			"path": this.getSvgPath(),
			// Update styles
			"stroke": this.color,
			"stroke-width": this.width
		};
		
		
		this.raphaelElement.attr(attr);
		
	};

} 

var Node = function (canvas) {
	var self = this;
	this.id = "";

	this.raphaelCanvas = canvas;
	this.raphaelElement = null;
	
	this.x = 0;
	this.y = 0;
	
	// In this draft the objects themselves contain style attributes.
	// The styling data will likely be moved elsewhere.
	this.size = 16;
	this.color = "#ddd";
	this.strokeColor =  "#999";
	this.strokeWidth = 3;
	this.shape = "circle";
	
	this.edges = [];

	this.getNeighbors = function () { return [] };

	this.setPosition = function(x, y) {
		this.x = x;
		this.y = y;
		this.draw();
	};
	
	this.dragStart = function() {
		this.ox = this.x;
		this.oy = this.y;
		this.raphaelElement.toFront();
		
	};
	
	this.dragMove = function(x, y) {
		var newX = this.ox + x;
		var newY = this.oy + y;
		
		// Clamp coordinates to canvas size (hardcoded for this test demo)
		if (newX < 0) newX = 0;
		if (newX > 400) newX = 400;
		if (newY < 0) newY = 0;
		if (newY > 400) newY = 400;
		
		this.x = newX;
		this.y = newY;
		this.update();
	};
	
	this.dragEnd = function() {
	
	};
	
	this.update = function() {
		if (this.raphaelElement == null) {
				this.raphaelElement = this.raphaelCanvas.circleNode()
				.drag(function(x,y) {self.dragMove(x,y)}, function() {self.dragStart()}, function() {self.dragEnd()});
		}
		var attr = {
		// Update layout
			"cx": this.x,
			"cy": this.y,
			"r": this.size,
	
		// Update styles
			"fill": this.color,
			"stroke": this.strokeColor,
			"stroke-width": this.strokeWidth
		};
		
		for (var i = 0, len = this.edges.length; i < len; i++) {
			this.edges[i].update();
		}
		
		this.raphaelElement.attr(attr);
		
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




Raphael.fn.circleNode = function(x, y, size) {
		size = size || 18;
		var node = this.circle(x, y, size).attr({"stroke-width": 4, "cursor":"pointer"}).hover(function() {
			this.attr("fill", "#eee");
		}, function() {
			this.attr("fill", "#ddd");
		});
		node.x = x;
		node.y = y;
		return node;
}

