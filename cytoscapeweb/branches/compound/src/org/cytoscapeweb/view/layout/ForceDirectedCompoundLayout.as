package org.cytoscapeweb.view.layout
{
	import flare.vis.data.EdgeSprite;
	import flare.vis.data.NodeSprite;
	import flare.vis.operator.layout.Layout;
	
	import flash.geom.Rectangle;
	import flash.net.getClassByAlias;
	import flash.utils.Timer;
	import flash.utils.getTimer;
	
	import org.cytoscapeweb.util.Groups;
	import org.cytoscapeweb.view.layout.ivis.layout.LEdge;
	import org.cytoscapeweb.view.layout.ivis.layout.LGraph;
	import org.cytoscapeweb.view.layout.ivis.layout.LGraphManager;
	import org.cytoscapeweb.view.layout.ivis.layout.LNode;
	import org.cytoscapeweb.view.layout.ivis.layout.Layout;
	import org.cytoscapeweb.view.layout.ivis.layout.cose.CoSELayout;
	import org.cytoscapeweb.vis.data.CompoundNodeSprite;

	/**
	 * This layout uses a ported version of CoSE algorithm. CoSE is a part of
	 * chiLay (Chisio) project which is developed by i-Vis Research Group
	 * of Bilkent University.
	 *  
	 * Original algorithm, which is written in Java programming language, can be 
	 * found on the project webpage http://sourceforge.net/projects/chisio/
	 */
	public class ForceDirectedCompoundLayout extends flare.vis.operator.layout.Layout
	{
		protected var _ivisLayout:org.cytoscapeweb.view.layout.ivis.layout.Layout;
		protected var _cwToLayout:Object;
		protected var _layoutToCw:Object;
		
		public function ForceDirectedCompoundLayout()
		{
			this._cwToLayout = new Object();
			this._layoutToCw = new Object();
			
			this._ivisLayout = new CoSELayout();
		}
		
		override protected function layout():void
		{
			// create topology for chiLay
			this.createTopology();
			
			// DEBUG: print topology
			this._ivisLayout.getGraphManager().printTopology();
			
			// DEBUG: print initial values
			//visualization.data.nodes.visit(this.updateNode);
			
			var timer:Timer = new Timer(1000, 0);

			timer.start();
			
			trace("before layout:" + getTimer());
			
			// run layout
			this._ivisLayout.runLayout();
			
			trace("after layout:" + getTimer());
			
			// update sprites
			//visualization.data.nodes.visit(this.updateNode);
			for each (var ns:NodeSprite in visualization.data.nodes)
			{
				updateNode(ns);
			}
			
			trace("after position update:" + getTimer());
			
			timer.stop();
		}
		
		protected function updateNode(ns:NodeSprite):void
		{	
			var node:LNode = this._cwToLayout[ns];
			
			ns.x = node.getCenterX();
			ns.y = node.getCenterY();
		}
		
		/**
		 * Creates l-level topology of the graph from the given compound model.
		 */
		protected function createTopology():void
		{	
			// create initial topology: a graph manager associated with the layout,
			// containing an empty root graph as its only graph
			
			var gm:LGraphManager = this._ivisLayout.getGraphManager();
			var lroot:LGraph = gm.addRoot();
			//lroot.vGraphObject = this.root;
			lroot.label = "root"; // for debugging purposes
			
			// for each CompoundNodeSprite at the root level (i.e. parentless)
			// in the data set, create an LNode
			
			for each (var ns:NodeSprite in visualization.data.nodes)
			{
				var cns:CompoundNodeSprite;
				
				if (ns is CompoundNodeSprite)
				{
					cns = ns as CompoundNodeSprite;
					
					if (cns.parentId == null)
					{
						this.createNode(cns,
							null,
							this._ivisLayout);
					}
				}
			}
			
			// for each EdgeSprite in the data set, create an LEdge
			
			for each (var es:EdgeSprite in
				visualization.data.group(Groups.REGULAR_EDGES))
			{
				this.createEdge(es, this._ivisLayout);
			}
			
			gm.updateBounds();
		}
		
		/**
		 * Creates an LNode for the given NodeModel object.
		 * 
		 * @param node		NodeSprite representing the node
		 * @param parent	parent node of the given node
		 * @param layout	layout of the graph
		 */
		protected function createNode(node:CompoundNodeSprite,
			parent:CompoundNodeSprite,
			layout:org.cytoscapeweb.view.layout.ivis.layout.Layout):void
		{
			var lNode:LNode = layout.newNode(null/*node*/);
			
			// TODO [refactor] for debugging purposes
			lNode.label = node.data.id;
			//trace("vNode [" + node.data.id + "]" + "x: " + node.x +
			//	" y: " + node.y +
			//	" w: " + node.width +
			//	" h: " + node.height);
			
			//trace ("vNode [" + node.data.id + "]" + "(" + node.x + "," + node.y + ")");
			
			var rootGraph:LGraph = layout.getGraphManager().getRoot(); 
			
			this._cwToLayout[node] = lNode;
			this._layoutToCw[lNode] = node;
			
			// if the node has a parent add the l-node as a child of the parent
			// l-node. Otherwise add the node to the root graph.
			
			if (parent != null)
			{
				var parentLNode:LNode = this._cwToLayout[parent] as LNode;
				//function parentLNode.getChild():assert != null : 
				//"Parent node doesn't have child graph.";
				parentLNode.getChild().addNode(lNode);
			}
			else
			{
				rootGraph.addNode(lNode);
			}
			
			// copy geometry
			
			lNode.setLocation(node.x, node.y);
			
			// TODO copy cluster ID (zero means unclustered)
			
			/*
			var clusterID:int = node.getClusterID();
			
			if (clusterID != 0)
			{
				//assert clusterID > 0;
				lNode.setClusterID(Integer.toString(clusterID));
			}
			*/
			
			// if node is a compound, recursively create child nodes
			
			if (node.isInitialized())
			{
				//var nodeIter:Iterator = compoundNode.getChildren().iterator();
				
				// add new LGraph to the graph manager for the compound node
				layout.getGraphManager().addGraph(layout.newGraph(null), lNode);
				
				// for each NodeModel in the node set create an LNode
				//while (nodeIter.hasNext())
				for each (var cns:CompoundNodeSprite in node.getNodes())
				{
					this.createNode(cns,
						node,
						layout);
				}
				
				lNode.updateBounds();
			}
			else
			{
				
				lNode.setWidth(node.width);
				lNode.setHeight(node.height);
				//lNode.setWidth(40);
				//lNode.setHeight(40);
			}
		}
		
		/**
		 * Creates an LEdge for the given EdgeSprite.
		 * 
		 * @param edge		source edge 
		 * @param layout	layout of the graph
		 */
		protected function createEdge(edge:EdgeSprite,
			layout:org.cytoscapeweb.view.layout.ivis.layout.Layout):void
		{
			var lEdge:LEdge = layout.newEdge(null/*edge*/);
			lEdge.label = edge.data.id; // for debugging purposes
			
			var sourceLNode:LNode = this._cwToLayout[edge.source] as LNode;
			var targetLNode:LNode = this._cwToLayout[edge.target] as LNode;
			
			layout.getGraphManager().addEdge(lEdge, sourceLNode, targetLNode);
			
			//var bendPoints:List= edge.getBendpoints();
		}
		
	}
}