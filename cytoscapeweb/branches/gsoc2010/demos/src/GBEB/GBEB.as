package GBEB
{
	import flare.vis.data.Data;
	import flare.vis.data.EdgeSprite;
	import flare.vis.data.NodeSprite;
	
	import flash.display.Sprite;
	import flash.geom.Rectangle;
	import flash.system.SecurityDomain;

	//import flare.

	//no Resize Event done yet
	public class GBEB extends Sprite
	{
		private var _meshResolution:int = 100; //Stores the resolution of the Mesh. defined as number of meshnodes.
		private var _meshData:Data;// Using Flare data class to store edges and control points (CP) of mesh and nodes of the mesh. 
		private var _data:Data; //stores the actual data of the graph
		private var _cloneData:Data; //stores the cloned data for Edges used for visual comparsion of the algorithm
		
		public var _mesh:Mesh;
		private var _dataDisplay:DataDisplay;
	
		private var _GBEBContainer:Sprite = new Sprite();
		private var _bundler:Bundler = new Bundler();
		
		private var bounds:Rectangle = new Rectangle(0,0, 700, 500);
		
		public function GBEB(d:Data, _mainBounds:Rectangle = null) 
		{
			_data = d;
			
			_meshResolution = int(Math.sqrt(d.edges.length + d.nodes.length)); 
			_mesh = new Mesh(d); 
			//addChild(_mesh);
			
			_dataDisplay = new DataDisplay(d);
			addChild(_dataDisplay);
			
			addChild(_GBEBContainer);
				
			trace("Mesh Resolution = " + _meshResolution);
		}	
		
		public function updateBounds(passedInBounds:Rectangle):void
		{
			bounds = passedInBounds;
			_mesh.generateMesh(_meshResolution, bounds);
			_dataDisplay.updateBounds(bounds);
			_dataDisplay.updateMesh(_mesh);
			
			
			trace("GBEB: Bundling!");
			
			//_cloneData = new Data();
			//data.nodes.visit(addNodes);
			_data.edges.visit(bundle);
			_data.edges.visit(addEdgesToVis);
			
			//_data.edges.visit(cloneEdges);			
			//_cloneData.edges.visit(bundle);
			//_cloneData.edges.visit(addEdgesToVis);
			
			trace("GBEB : bounds.width:" + bounds.width + " | bounds.height: " + bounds.height);

		}
					//these indented functions are use to create a visualisation for GBEB
					//by cloning an overlay is created, allowing the inspection of the quality of the graph
					private function bundle(e:EdgeSprite):void
					{
						_bundler.render(e);
					}
					
					private function addNodes(n:NodeSprite):void
					{
						_cloneData.addNode(n);
					}
					
					private function cloneEdges(e:EdgeSprite):void
					{
						var newEdge:EdgeSprite = new EdgeSprite(e.source,e.target,e.directed);				
						newEdge.props.GBEBProperty = e.props.GBEBProperty;
						newEdge.x1 = e.x1; newEdge.y1 = e.y1; newEdge.x2 = e.x2; newEdge.y2 = e.y2;
						newEdge.data = e.data;
						newEdge.lineColor = e.lineColor;			
						_cloneData.addEdge(newEdge);
						trace("GBEB: Cloning Edges! _cloneData.length: " + _cloneData.edges.length);
					}
					
					private function addEdgesToVis(e:EdgeSprite):void
					{
						_GBEBContainer.addChild(e);
					}
		
		public function cleanup():void
		{
			_dataDisplay.cleanup();
			removeChild(_dataDisplay);
			_dataDisplay = null;
			
			_mesh.cleanup();
			//removeChild(_mesh);
			_mesh = null;
			
			_meshData = null;
		}
		
		public function redrawMesh():void
		{
			if(_data == null) throw new Error("Data is null!"); 
			
			_mesh.cleanup();
			//removeChild(_mesh);
		
			_mesh = new Mesh(_data);
			//addChild(_mesh);
		}
		
		
		
	}
}