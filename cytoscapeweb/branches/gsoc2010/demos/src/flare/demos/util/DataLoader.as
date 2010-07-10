package flare.demos.util
{
	import flare.data.DataSet;
	import flare.data.DataSource;
	import flare.demos.Layouts;
	import flare.vis.data.Data;
	import flare.vis.data.NodeSprite;
	
	import flash.events.*;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	
	

	public class DataLoader extends EventDispatcher
	{
		public static var ACTION:String = "action";
		
		private var _data:Data;
		private var _dataSource:DataSource;
		private var _dataSet:DataSet;
		private var _loader:URLLoader;
		
		private var _callBack:Function;
		
		//for "GraphML files
		//private var _urlGML:String = "/Users/Tomithy/Desktop/GSOC/Datasets/socialnet.xml";
		
		//private var _urlGML:String = "/Users/Tomithy/Desktop/GSOC/Datasets/simplegraph.xml";
		//private var _urlGML:String = "/Users/Tomithy/Desktop/GSOC/Datasets/sample1.graphml";	
		private var _urlGML:String = "/Users/Tomithy/Desktop/GSOC/Datasets/sample3.graphml";
		
		//for "tab" files
		private var _urlTab:String = "/Users/Tomithy/Desktop/GSOC/Datasets/Family.txt";
	
		public function DataLoader(passedInCallBack:Function)
		{
			_callBack = passedInCallBack;
			//var urlLoader:URLLoader = new URLLoader(new URLRequest(_url));
			//dataFromTab();
			dataFromGraphML();
			
		}
		
		private function dataLoaded(e:Event):void
		{
			
		}
		
		private function dataFromTab():void
		{
			_dataSource = new DataSource(_urlTab, "tab");
		  _loader = _dataSource.load();
			
			_loader.addEventListener(Event.COMPLETE, onComplete);
			
		}
		
		private function dataFromGraphML():void
		{
			_dataSource = new DataSource(_urlGML, "graphml");
			_loader = _dataSource.load();
			
			_loader.addEventListener(Event.COMPLETE, onComplete);
			
			trace("DataLoader.graphml is loaded");
		}
		
		//
		private function onComplete(e:Event):void
		{
			_dataSet = _loader.data as DataSet;
			_data = Data.fromDataSet(_dataSet);
			
			//trace("DataLoader: Data Initialised");
			//trace("DataLoader.ds : numNodes = " + _data.nodes.length + " | numEdges = " + _data.edges["length"]);
							
			//testing data access
			if(_dataSource.format === "tab")
			{
				for each (var n:NodeSprite in _data.nodes)
				{
					trace("DataLoader.ds.node : name = " + n.data["Name"] + " | Age = " + n.data["Age"]);	
				}
			}		
			
			/*if(_dataSource.format === "graphml")
			{
				for each ( var n:NodeSprite in _data.nodes)
				{	
					trace("DataLoader.ds.node : name = " + n.data["name"]);
				}
			} */
			
			//callback to signal that data has been loaded asynchronously. 
			_callBack(_data);
		
		}
		
		
		
		
		
	}// end of class
}