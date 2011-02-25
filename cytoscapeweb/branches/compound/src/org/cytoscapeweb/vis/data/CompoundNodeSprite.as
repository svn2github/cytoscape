package org.cytoscapeweb.vis.data
{
	import flare.vis.data.NodeSprite;
	
	import flash.geom.Rectangle;
	
	import org.cytoscapeweb.util.Anchors;
	import org.cytoscapeweb.util.NodeShapes;

	/**
	 * This class represents a Compound Node with its child nodes, bounds and
	 * margin values. A compound node can contain any other node (both simple
	 * node and compound node) as its child.
	 * 
	 * @author Selcuk Onur Sumer
	 */ 
	public class CompoundNodeSprite extends NodeSprite
	{
		// ==================== [ PRIVATE PROPERTIES ] =========================
		
		/**
		 * Contains child nodes of this compound node as a map of NodeSprite
		 * objects.
		 */
		private var _nodesMap:Object;
		
		private var _bounds:Rectangle;
		private var _leftMargin:Number;
		private var _rightMargin:Number;
		private var _topMargin:Number;
		private var _bottomMargin:Number;
		
		// ===================== [ PUBLIC PROPERTIES ] =========================

		/**
		 * Bounds enclosing children of the compound node.
		 */
		public function get bounds():Rectangle
		{
			return _bounds;
		}
		
		/**
		 * Width of the right margin of the compound node
		 */
		public function get rightMargin():Number
		{
			return _rightMargin;
		}

		public function set rightMargin(value:Number):void
		{
			_rightMargin = value;
		}

		/**
		 * Height of the margin of the compound node
		 */
		public function get topMargin():Number
		{
			return _topMargin;
		}

		public function set topMargin(value:Number):void
		{
			_topMargin = value;
		}

		/**
		 * Height of the bottom margin of the compound node
		 */
		public function get bottomMargin():Number
		{
			return _bottomMargin;
		}

		public function set bottomMargin(value:Number):void
		{
			_bottomMargin = value;
		}

		/**
		 * Width of the left margin of the compound node
		 */
		public function get leftMargin():Number
		{
			return _leftMargin;
		}

		public function set leftMargin(value:Number):void
		{
			_leftMargin = value;
		}

		
		
		
		
		// ========================= [ CONSTRUCTOR ] ===========================
		
		public function CompoundNodeSprite()
		{
			this._nodesMap = new Object();
			this._bounds = null;
		}
		
		// ====================== [ PUBLIC FUNCTIONS ] =========================
		
		/**
		 * Adds the given node sprite to the child list of the compound node.
		 * This function assumes that the given node sprite has an id in its
		 * data field.
		 * 
		 * @param ns	child node sprite to be added
		 */
		public function addNode(ns:NodeSprite) : void
		{
			// add the node to the child node list of this node
			this._nodesMap[ns.data.id] = ns;
			
			// set the parent id of the added node
			ns.data.parentId = this.data.id;
		}
		
		/**
		 * Removes the given node sprite from the child list of the compound
		 * node.
		 * 
		 * @param ns	child node sprite to be removed
		 */ 
		public function removeNode(ns:NodeSprite) : void
		{
			// check if given node is a child of this compound
			if (ns.data.parentId == this.data.id)
			{
				// reset the parent id of the removed node
				ns.data.parentId = null;
			
				// remove the node from the list of child nodes 
				delete this._nodesMap[ns.data.id];
			}
		}
		
		/**
		 * Returns (one-level) child nodes of this compound node. 
		 */
		public function getNodes() : Array
		{
			var nodeList:Array = new Array();
			
			for each (var ns:NodeSprite in this._nodesMap)
			{
				nodeList.push(ns);
			}
			
			return nodeList;
		}
		
		public function updateBounds(bounds:Rectangle) : void
		{
			// extend bounds by adding margin width & height
			bounds.x -= this.leftMargin;
			bounds.y -= this.topMargin;
			bounds.height += this.topMargin + this.bottomMargin;
			bounds.width += this.leftMargin + this.rightMargin;
			
			// set bounds
			_bounds = bounds;
			
			// also update x & y coordinates of the compound node by using
			// the new bounds
			this.x = bounds.x + (bounds.width / 2);
			this.y = bounds.y + (bounds.height / 2);
		}
		
		public function resetBounds() : void
		{
			_bounds = null;
		}
		
		// ====================== [ PRIVATE FUNCTIONS ] ========================
	}
}