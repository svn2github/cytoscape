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
		
		private var _parentId:String;
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

		/**
		 * ID of the parent node containing this node sprite
		 */
		public function get parentId():String
		{
			return _parentId;
		}
		
		public function set parentId(value:String):void
		{
			_parentId = value;
		}
		
		
		
		// ========================= [ CONSTRUCTOR ] ===========================
		
		public function CompoundNodeSprite()
		{
			this._nodesMap = null;
			this._bounds = null;
			this._parentId = null;
		}
		
		// ====================== [ PUBLIC FUNCTIONS ] =========================
		
		/**
		 * Initializes the map of children for this compound node.
		 */
		public function initialize() : void
		{
			this._nodesMap = new Object();
		}
		
		public function isInitialized() : Boolean
		{
			var initialized:Boolean;
			
			if (this._nodesMap == null)
			{
				initialized = false;
			}
			else
			{
				initialized = true;
			}
			
			return initialized;
		}
		
		
		/**
		 * Adds the given node sprite to the child map of the compound node.
		 * This function assumes that the given node sprite has an id in its
		 * data field.
		 * 
		 * @param ns	child node sprite to be added
		 */
		public function addNode(ns:NodeSprite) : void
		{
			// check if the node is initialized
			if (this._nodesMap != null)
			{
				// add the node to the child node list of this node
				this._nodesMap[ns.data.id] = ns;
			
				// set the parent id of the added node
				
				if (ns is CompoundNodeSprite)
				{
					// if a CompoundNodeSprite instance is added set the 
					// corresponding field for parent id.
					(ns as CompoundNodeSprite).parentId = this.data.id; 
				}
				else
				{
					// TODO what to do if a NodeSprite is added? 
					ns.data.parentId = this.data.id;
				}
				
			}
		}
		
		/**
		 * Removes the given node sprite from the child list of the compound
		 * node.
		 * 
		 * @param ns	child node sprite to be removed
		 */ 
		public function removeNode(ns:NodeSprite) : void
		{
			var parentId:String;
			
			if (ns is CompoundNodeSprite)
			{
				parentId = (ns as CompoundNodeSprite).parentId;
			}
			else
			{
				parentId = ns.data.id;
			}
			
			// check if given node is a child of this compound
			if (this._nodesMap != null &&
				parentId == this.data.id)
			{
				// reset the parent id of the removed node
				if (ns is CompoundNodeSprite)
				{
					(ns as CompoundNodeSprite).parentId = null;
				}
				else
				{
					ns.data.parentId = null;
				}
				
				// remove the node from the list of child nodes 
				delete this._nodesMap[ns.data.id];
			}
		}
		
		/**
		 * Returns (one-level) child nodes of this compound node. If the map
		 * of children is not initialized, then returns an empty array.
		 */
		public function getNodes() : Array
		{
			var nodeList:Array = new Array();
			
			if (this._nodesMap != null)
			{
				for each (var ns:NodeSprite in this._nodesMap)
				{
					nodeList.push(ns);
				}
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