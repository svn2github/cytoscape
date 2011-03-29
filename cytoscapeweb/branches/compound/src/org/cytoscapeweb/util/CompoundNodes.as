package org.cytoscapeweb.util
{
	import flare.util.IEvaluable;
	import flare.vis.data.NodeSprite;
	
	import flash.filters.GlowFilter;
	
	import org.cytoscapeweb.ApplicationFacade;
	import org.cytoscapeweb.model.ConfigProxy;
	import org.cytoscapeweb.model.data.VisualStyleBypassVO;
	import org.cytoscapeweb.model.data.VisualStyleVO;
	import org.cytoscapeweb.view.render.CompoundNodeRenderer;
	import org.cytoscapeweb.vis.data.CompoundNodeSprite;

	public class CompoundNodes
	{
		public static const ALL_CHILDREN:String = "all";
		public static const SELECTED_CHILDREN:String = "selected";
		public static const NON_SELECTED_CHILDREN:String = "non-selected";
		
		private static var _properties:Object;
		private static var _configProxy:ConfigProxy;
		
		private static function get configProxy() : ConfigProxy
		{
			if (_configProxy == null)
			{
				_configProxy = ApplicationFacade.getInstance().
					retrieveProxy(ConfigProxy.NAME) as ConfigProxy;
			}
			
			return _configProxy;
		}
		
		private static function get style() : VisualStyleVO
		{
			return configProxy.visualStyle;
		}
		
		private static function get bypass() : VisualStyleBypassVO
		{
			return configProxy.visualStyleBypass;
		}
		
		public function CompoundNodes()
		{
			throw new Error("This is an abstract class.");
		}
		
		/**
		 * This method returns visual style properties which are specific to 
		 * compound nodes.
		 */
		public static function get properties() : Object
		{
			if (CompoundNodes._properties == null)
			{
				CompoundNodes._properties =
				{
					shape: CompoundNodes.shape,
					size: CompoundNodes.size,
					paddingLeft: CompoundNodes.paddingLeft,
					paddingRight: CompoundNodes.paddingRight,
					paddingTop: CompoundNodes.paddingTop,
					paddingBottom: CompoundNodes.paddingBottom,
					fillColor: CompoundNodes.fillColor,
					lineColor: CompoundNodes.lineColor, 
					lineWidth: CompoundNodes.lineWidth,
					alpha: CompoundNodes.alpha,
					"props.imageUrl": Nodes.imageUrl,
					visible: Nodes.visible,
					buttonMode: true,
					filters: CompoundNodes.filters,
					renderer: CompoundNodeRenderer.instance
				};
			}
			
			return _properties;
		}
		
		public static function shape(n:NodeSprite) : String
		{
			var shape:String = CompoundNodes.style.getValue(
				VisualProperties.C_NODE_SHAPE, n.data);
			
			return NodeShapes.parse(shape);
		}
		
		public static function size(n:NodeSprite) : Number
		{
			// set size as double size of a simple node
			var size:Number = style.getValue(
				VisualProperties.NODE_SIZE, n.data) * 2;
			
			return size / _properties.renderer.defaultSize;
		}
		
		public static function fillColor(n:NodeSprite) : uint
		{
			var propName:String = VisualProperties.C_NODE_COLOR;
			
			if (n.props.$selected &&
				style.hasVisualProperty(
					VisualProperties.C_NODE_SELECTION_COLOR))
			{
				propName = VisualProperties.C_NODE_SELECTION_COLOR;
			}
			
			return style.getValue(propName, n.data);
		}
		
		public static function lineColor(n:NodeSprite) : uint
		{
			var propName:String = VisualProperties.C_NODE_LINE_COLOR;
			
			if (n.props.$hover &&
				style.hasVisualProperty(
					VisualProperties.C_NODE_HOVER_LINE_COLOR))
			{
				propName = VisualProperties.C_NODE_HOVER_LINE_COLOR;
			}
			else if (n.props.$selected &&
				style.hasVisualProperty(
					VisualProperties.C_NODE_SELECTION_LINE_COLOR))
			{
				propName = VisualProperties.C_NODE_SELECTION_LINE_COLOR;
			}
			
			return style.getValue(propName, n.data);
		}
		
		public static function lineWidth(n:NodeSprite) : Number
		{
			var propName:String = VisualProperties.C_NODE_LINE_WIDTH;
			
			if (n.props.$hover &&
				style.hasVisualProperty(
					VisualProperties.C_NODE_HOVER_LINE_WIDTH))
			{
				propName = VisualProperties.NODE_HOVER_LINE_WIDTH;
			}
			else if (n.props.$selected &&
				style.hasVisualProperty(
					VisualProperties.C_NODE_SELECTION_LINE_WIDTH))
			{
				propName = VisualProperties.NODE_SELECTION_LINE_WIDTH;
			}
			
			return style.getValue(propName, n.data);
		}
		
		public static function selectionLineWidth(n:NodeSprite) : Number
		{
			var propName:String = VisualProperties.C_NODE_LINE_WIDTH;
			
			if (style.hasVisualProperty(
				VisualProperties.C_NODE_SELECTION_LINE_WIDTH))
			{
				propName = VisualProperties.C_NODE_SELECTION_LINE_WIDTH;
			}
			else if (n.props.$hover &&
				style.hasVisualProperty(
					VisualProperties.C_NODE_HOVER_LINE_WIDTH))
			{
				propName = VisualProperties.C_NODE_HOVER_LINE_WIDTH;
			}
			
			return style.getValue(propName, n.data);
		}
		
		public static function alpha(n:NodeSprite) : Number
		{
			var propName:String = VisualProperties.C_NODE_ALPHA;
			
			if (n.props.$hover &&
				style.hasVisualProperty(
					VisualProperties.C_NODE_HOVER_ALPHA))
			{
				propName = VisualProperties.C_NODE_HOVER_ALPHA;
			}
			else if (n.props.$selected &&
				style.hasVisualProperty(
					VisualProperties.C_NODE_SELECTION_ALPHA))
			{
				propName = VisualProperties.C_NODE_SELECTION_ALPHA;
			}
			
			return style.getValue(propName, n.data);
		}
		
		public static function selectionAlpha(n:NodeSprite):Number
		{
			var propName:String = VisualProperties.C_NODE_ALPHA;
			
			if (style.hasVisualProperty(
				VisualProperties.C_NODE_SELECTION_ALPHA))
			{
				propName = VisualProperties.C_NODE_SELECTION_ALPHA;
			}
			
			return style.getValue(propName, n.data);
		}
		
		public static function filters(n:NodeSprite,
									   selectNow:Boolean=false) : Array
		{
			var filters:Array = [];
			
			var glow:GlowFilter = null;
			
			if (!selectNow && n.props.$hover)
			{
				glow = hoverGlow(n);
			}
			if (glow == null &&
				n.props.$selected)
			{
				glow = selectionGlow(n);
			}
			
			if (glow != null)
			{
				filters.push(glow);
			}
			
			return filters;
		}
		
		public static function selectionGlow(n:NodeSprite) : GlowFilter
		{
			var filter:GlowFilter = null;
			var alpha:Number = style.getDefaultValue(
				VisualProperties.C_NODE_SELECTION_GLOW_ALPHA);
			var blur:Number = style.getDefaultValue(
				VisualProperties.C_NODE_SELECTION_GLOW_BLUR);
			var strength:Number = style.getDefaultValue(
				VisualProperties.C_NODE_SELECTION_GLOW_STRENGTH);
			
			if (alpha > 0 &&
				blur > 0 &&
				strength > 0)
			{
				var color:uint = style.getDefaultValue(
					VisualProperties.C_NODE_SELECTION_GLOW_COLOR);           
				
				filter = new GlowFilter(color, alpha, blur, blur, strength);
			}
			
			return filter;
		}
		
		public static function hoverGlow(n:NodeSprite) : GlowFilter
		{
			var filter:GlowFilter = null;
			var alpha:Number = style.getDefaultValue(
				VisualProperties.C_NODE_HOVER_GLOW_ALPHA);
			var blur:Number = style.getDefaultValue(
				VisualProperties.C_NODE_HOVER_GLOW_BLUR);
			var strength:Number = style.getDefaultValue(
				VisualProperties.C_NODE_HOVER_GLOW_STRENGTH);
			
			if (alpha > 0 && blur > 0 && strength > 0)
			{
				var color:uint = style.getDefaultValue(
					VisualProperties.C_NODE_HOVER_GLOW_COLOR);
				
				filter = new GlowFilter(color, alpha, blur, blur, strength);
			}
			
			return filter;
		}
		
		public static function paddingLeft(n:NodeSprite) : Number
		{
			var margin:Number = style.getValue(
				VisualProperties.C_NODE_PADDING_LEFT, n.data);
			
			return margin;
		}
		
		public static function paddingRight(n:NodeSprite) : Number
		{
			var margin:Number = style.getValue(
				VisualProperties.C_NODE_PADDING_RIGHT, n.data);
			
			return margin;
		}
		
		public static function paddingTop(n:NodeSprite) : Number
		{
			var margin:Number = style.getValue(
				VisualProperties.C_NODE_PADDING_TOP, n.data);
			
			return margin;
		}
		
		public static function paddingBottom(n:NodeSprite) : Number
		{
			var margin:Number = style.getValue(
				VisualProperties.C_NODE_PADDING_BOTTOM, n.data);
			
			return margin;
		}
		
		/**
		 * Recursively populates an array of NodeSprite instances with the
		 * children of selected type for the given CompoundNodeSprite. All
		 * children are collected by default, type can be selected and
		 * non-selected children.
		 * 
		 * @param cns	compound node sprite whose children are collected 
		 */
		public static function getChildren(cns:CompoundNodeSprite,
			type:String = CompoundNodes.ALL_CHILDREN) : Array
		{
			var children:Array = new Array();
			var condition:Boolean;
			
			if (cns != null)
			{
				for each (var ns:NodeSprite in cns.getNodes())
				{
					if (type === CompoundNodes.SELECTED_CHILDREN)
					{
						if (ns.props.$selected)
						{
							condition = true;
						}
						else
						{
							condition = false;
						}
					}
					else if (type === CompoundNodes.NON_SELECTED_CHILDREN)
					{
						if (ns.props.$selected)
						{
							condition = false;
						}
						else
						{
							condition = true;
						}
					}
					else
					{
						// default case is all children (always true)
						condition = true;
					}
					
					// process the node if the condition meets
					if (condition)
					{
						// add current node to the list
						children.push(ns);
					}
					
					if (ns is CompoundNodeSprite)
					{
						// recursively collect child nodes
						children = children.concat(
							getChildren(ns as CompoundNodeSprite, type));
					}
				}
			}
			
			return children;
		}
		
	}
}