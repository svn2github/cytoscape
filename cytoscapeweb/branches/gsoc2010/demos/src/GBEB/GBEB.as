package GBEB
{
	import flare.vis.data.Data;
	
	import flash.display.Sprite;
	import flash.geom.Rectangle;
	//import flare.

	//no Resize Event done yet
	public class GBEB extends Sprite
	{
		private var _meshResolution:int = 100; //Stores the resolution of the Mesh. defined as number of meshnodes.
		private var _meshData:Data;// Using Flare data class to store edges and control points (CP) of mesh and nodes of the mesh. 
		
		public var _mesh:Mesh;
		private var _dataDisplay:DataDisplay = new DataDisplay();
		
		private var bounds:Rectangle = new Rectangle(0,0, 700, 500);
		
		public function GBEB(d:Data, _mainBounds:Rectangle = null) 
		{
			
			_meshResolution = int(Math.sqrt(d.edges.length + d.nodes.length)); 
			
			_mesh = new Mesh(d); 
			
			addChild(_mesh);
			addChild(_dataDisplay);
			
			
			
			trace("Mesh Resolution = " + _meshResolution);
		}	
		
		public function updateBounds(passedInBounds:Rectangle):void
		{
			bounds = passedInBounds;
			_mesh.generateMesh(_meshResolution, bounds);
			_dataDisplay.updateBounds(bounds);
			_dataDisplay.updateMesh(_mesh);
			_dataDisplay.displayMesh();
			
			trace("GBEB : bounds.width:" + bounds.width + " | bounds.height: " + bounds.height);

		}
		
		
		
	}
}