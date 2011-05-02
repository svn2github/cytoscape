package org.cytoscapeweb.vis.data
{
	import flare.vis.data.NodeSprite;
	
	import flash.geom.Rectangle;
	
	import org.cytoscapeweb.util.Anchors;
	import org.cytoscapeweb.util.NodeShapes;

	/**
	 * This class represents a Compound Node with its child nodes, bounds and
	 * padding values. A compound node can contain any other node (both simple
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
		private var _paddingLeft:Number;
		private var _paddingRight:Number;
		private var _paddingTop:Number;
		private var _paddingBottom:Number;
		
		// ===================== [ PUBLIC PROPERTIES ] =========================

		/**
		 * Bounds enclosing children of the compound node.
		 */
		public function get bounds():Rectangle
		{
			return _bounds;
		}
		
		public function set bounds(rect:Rectangle):void
		{
			_bounds = rect;
		}
		
		/**
		 * Width of the right padding of the compound node
		 */
		public function get paddingRight():Number
		{
			return _paddingRight;
		}

		public function set paddingRight(value:Number):void
		{
			_paddingRight = value;
		}

		/**
		 * Height of the top padding of the compound node
		 */
		public function get paddingTop():Number
		{
			return _paddingTop;
		}

		public function set paddingTop(value:Number):void
		{
			_paddingTop = value;
		}

		/**
		 * Height of the bottom padding of the compound node
		 */
		public function get paddingBottom():Number
		{
			return _paddingBottom;
		}

		public function set paddingBottom(value:Number):void
		{
			_paddingBottom = value;
		}

		/**
		 * Width of the left padding of the compound node
		 */
		public function get paddingLeft():Number
		{
			return _paddingLeft;
		}

		public function set paddingLeft(value:Number):void
		{
			_paddingLeft = value;
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
			// extend bounds by adding padding width & height
			bounds.x -= this.paddingLeft;
			bounds.y -= this.paddingTop;
			bounds.height += this.paddingTop + this.paddingBottom;
			bounds.width += this.paddingLeft + this.paddingRight;
			
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