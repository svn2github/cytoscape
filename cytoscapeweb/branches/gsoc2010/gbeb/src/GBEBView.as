package 
{
    import com.adobe.serialization.json.JSON;
    
    import flare.animate.TransitionEvent;
    import flare.animate.Transitioner;
    import flare.data.DataSet;
    import flare.data.converters.GraphMLConverter;
    import flare.display.DirtySprite;
    import flare.scale.ScaleType;
    import flare.util.Shapes;
    import flare.util.palette.ColorPalette;
    import flare.vis.Visualization;
    import flare.vis.data.Data;
    import flare.vis.data.EdgeSprite;
    import flare.vis.data.NodeSprite;
    import flare.vis.data.Tree;
    import flare.vis.events.SelectionEvent;
    import flare.vis.operator.encoder.ColorEncoder;
    import flare.vis.operator.layout.CircleLayout;
    import flare.vis.operator.layout.RadialTreeLayout;
    
    import flash.display.Sprite;
    import flash.display.StageAlign;
    import flash.display.StageScaleMode;
    import flash.events.Event;
    import flash.events.MouseEvent;
    import flash.geom.Rectangle;
    import flash.net.URLLoader;
    import flash.net.URLRequest;
    import flash.text.TextFormat;
    import flash.utils.Dictionary;
    
    import gbeb.view.components.ProgressBar;
    import gbeb.view.operator.router.GBEBRouter;
    import gbeb.view.render.BundleRenderer;
    

    [SWF(width="800",height="600", backgroundColor="#ffffff", frameRate="30")]
    public class GBEBView extends Sprite
    {  
        //private var _url:String = "data/sample1.xml";
        private var _url:String = "http://flare.prefuse.org/data/flare.json.txt";
				//private var _url:String ="/Users/Tomithy/Desktop/GSOC/Datasets/flare.json.txt";
				//private var _url:String ="/Users/Tomithy/Desktop/GSOC/Datasets/flare_reduced.json.txt";
				//private var _url:String ="/Users/Tomithy/Desktop/GSOC/Datasets/socialnet.xml";
        private var _vis:Visualization;
        private var _bar:ProgressBar;
        private var _bounds:Rectangle;
        
        private var _fmt:TextFormat = new TextFormat("_sans", 7);
        private var _focus:NodeSprite;
        private var _appBounds:Rectangle;
				
		//testing Variables
		private var addEventCounter:int = 0;
        
        
        public function GBEBView() {
            addEventListener(Event.ADDED_TO_STAGE, onStageAdd);
        }
        
        protected function init():void {
            // create progress bar
            addChild(_bar = new ProgressBar());
						  
            // load data file
            // TODO: load Graphml:
						
            var ldr:URLLoader = new URLLoader(new URLRequest(_url));
            _bar.loadURL(
                ldr,
                function():void {
                    var obj:String = ldr.data as String;
                    var data:Data = buildData(obj); 
                    visualize(data);
                    _bar = null;
                });
        }
        
        private function visualize(data:Data):void {    	
			// prepare data with default settings
            data.nodes.setProperties({
                shape: Shapes.CIRCLE,
                alpha: 0.2,
                //visible: eq("childDegree",0), // only show leaf nodes
                buttonMode: true              // show hand cursor
            });
            data.edges.setProperties({
                lineWidth: 2,
                lineColor: 0xff0055cc,
                mouseEnabled: true,
                buttonMode: true,
//                visible: neq("source.parentNode","target.parentNode"),
                renderer: BundleRenderer.instance
            });
 
            _vis = new Visualization(data);
            //_vis.continuousUpdates = false;
            addChild(_vis);
 						
            if (_bounds) resize(_bounds);
						
						
						
						
						
 
                // place around circle by tree structure, radius mapped to depth
//                _vis.operators.add(new CircleLayout(null, null, true));
//                CircleLayout(_vis.operators.last).angleWidth = 2 * Math.PI;
												
//           	    _vis.operators.add(new RadialTreeLayout(80));
//                RadialTreeLayout(_vis.operators.last).autoScale = true;				
							
				_vis.operators.add(new CircleLayout());
				
				//_vis.operators.add(new NodeLinkTreeLayout("topToBottom", 50, 50, 50));
							
							
                // set the edge alpha values
                // longer edge, lighter alpha: 1/(2*numCtrlPoints)
//                _vis.operators.add(new PropertyEncoder({ alpha: edgeAlpha }, Data.EDGES));
           
                // TODO: replace by GBEB Router:
                // ##############################################################            
                // bundle edges to route along the tree structure
                //_vis.operators.add(new BundledEdgeRouter(0.95));
                
                //var bounds:Rectangle = new Rectangle(0, 0, width, height);
								//_vis.operators.add(new ColorEncoder("index", "edges", "lineColour"))
//				_vis.operators.add(new Labeler("data.name"));
                _vis.operators.add(new GBEBRouter(_bounds, 30 , 0.95));
                trace("GBEBView: No. of times View calls router: " + addEventCounter++);
                // ############################################################## 
				_vis.update();
				 
                // show all dependencies on single-click
//                var linkType:int = NodeSprite.OUT_LINKS;
//                _vis.controls.add(new ClickControl(NodeSprite, 1,
//                    function(evt:SelectionEvent):void {
//                        if (_focus && _focus != evt.node) {
//                            unhighlight(_focus);
//                            linkType = NodeSprite.OUT_LINKS;
//                        }
//                        _focus = evt.node;
//                        highlight(evt);
//                        showAllDeps(evt, linkType);
////                        _vis.controls.remove(hov);
//                        linkType = (linkType==NodeSprite.OUT_LINKS ?
//                            NodeSprite.IN_LINKS : NodeSprite.OUT_LINKS);
//                    },
//                    // show all edges and nodes as normal
//                    function(evt:SelectionEvent):void {
//                        if (_focus) unhighlight(_focus);
//                        _focus = null;
//                        _vis.data.edges["visible"] = 
//                            neq("source.parentNode","target.parentNode");
//                        _vis.data.nodes["alpha"] = 1;
////                        _vis.controls.add(hov);
//                        linkType = NodeSprite.OUT_LINKS;
//                    }
//                ));
                
                // add mouse-over highlight
//                var hov:HoverControl = new HoverControl(NodeSprite, HoverControl.DONT_MOVE, highlight, unhighlight);
//                _vis.controls.add(hov);

        }
        
        /** Add highlight to a node and connected edges/nodes */
        private function highlight(evt:SelectionEvent):void
        {
            // highlight links for classes that depend on the focus in green
            evt.node.visitEdges(function(e:EdgeSprite):void {
                e.alpha = 0.5;
                e.lineColor = 0xff00ff00;
                _vis.marks.setChildIndex(e, _vis.marks.numChildren-1);
            }, NodeSprite.IN_LINKS);
            // highlight links the focus depends on in red
            evt.node.visitEdges(function(e:EdgeSprite):void {
                e.alpha = 0.5;
                e.lineColor = 0xffff0000;
                _vis.marks.setChildIndex(e, _vis.marks.numChildren-1);
            }, NodeSprite.OUT_LINKS);
        }
        
        /** Remove highlight from a node and connected edges/nodes */
        private function unhighlight(n:*):void
        {
            var node:NodeSprite = n is NodeSprite ?
                NodeSprite(n) : SelectionEvent(n).node;
            // set everything back to normal
            node.setEdgeProperties({
                alpha: edgeAlpha,
                lineColor: 0xff0055cc
            }, NodeSprite.GRAPH_LINKS);
        }
        
        /** Traverse all dependencies for a given class */
        private function showAllDeps(evt:SelectionEvent, linkType:int):void
        {
            // first, do a breadth-first-search to compute closure
            var q:Array = evt.items.slice();
            var map:Dictionary = new Dictionary();
            while (q.length > 0) {
                var u:NodeSprite = q.shift();
                map[u] = true;
                u.visitNodes(function(v:NodeSprite):void {
                    if (!map[v]) q.push(v);
                }, linkType);
            }
            // now highlight nodes and edges in the closure
            _vis.data.edges.visit(function(e:EdgeSprite):void {
                e.visible = map[e.source] && map[e.target];
            });
            _vis.data.nodes.visit(function(n:NodeSprite):void {
                n.alpha = map[n] ? 1 : 0.4;
            });
        }
        
        public function resize(bounds:Rectangle):void {
            _bounds = bounds;
            if (_bar) {
                _bar.x = _bounds.width/2 - _bar.width/2;
                _bar.y = _bounds.height/2 - _bar.height/2;
            }
            if (_vis) {
                // compute the visualization bounds
                _vis.bounds.x = _bounds.x;
                _vis.bounds.y = _bounds.y + (0.06 * _bounds.height);
                _vis.bounds.width = _bounds.width;
                _vis.bounds.height = _bounds.height - (0.05 * _bounds.height);
                // update
                _vis.update();
                
                // forcibly render to eliminate partial update bug, as
                // the standard RENDER event routing can get delayed.
                // remove this line for faster but unsynchronized resizes
                DirtySprite.renderDirty();
            }
        }
        
        private function onStageAdd(evt:Event):void {
            removeEventListener(Event.ADDED_TO_STAGE, onStageAdd);
            initStage();
            init();
            onResize();
            stage.addEventListener(Event.RESIZE, onResize);
        }
        
        private function onResize(evt:Event=null):void {
            _appBounds = new Rectangle(0, 0, stage.stageWidth, stage.stageHeight);
            resize(_appBounds.clone());
        }
        
        protected function initStage():void {
            if (!stage) {
                throw new Error("Can't initialize Stage -- not yet added to stage");
            }
            stage.align = StageAlign.TOP_LEFT;
            stage.scaleMode = StageScaleMode.NO_SCALE;
        }
        
        private function edgeAlpha(e:EdgeSprite):Number {
            return e.points && e.points.length > 0 ? 1/e.points.length : 1;
        }
        
        // --------------------------------------------------------------------
        
        /**
         * Creates the visualized data.
         */
        public static function buildData(network:String):Data
        {
            var data:Data;
            var tree:Tree;
            var xml:XML = new XML(network as String);
            
            if (xml != null && xml.name() != null) {
                // convert from GraphML
                var ds:DataSet = new GraphMLConverter().parse(xml);
                data = Data.fromDataSet(ds);
                tree = data.tree;
            } else {
                // build from tuples
                var tuples:Array = JSON.decode(network) as Array;
                data = new Data();
                tree = new Tree();
                tree.root = data.addNode({name:"flare", size:0});
                var map:Object = {};
                map.flare = tree.root;
                
                var t:Object, u:NodeSprite, v:NodeSprite;
                var path:Array, p:String, pp:String, i:uint;
                
                // build data set and tree edges
                tuples.sortOn("name");
                for each (t in tuples) {
                    path = String(t.name).split(".");
                    for (i=0, p=""; i<path.length-1; ++i) {
                        pp = p;
                        p += (i?".":"") + path[i];
                        if (!map[p]) {
                            u = data.addNode({name:p, size:0});
                            tree.addChild(map[pp], u);
                            map[p] = u;
                        }
                    }
                    t["package"] = p;
                    u = data.addNode(t);
                    tree.addChild(map[p], u);
                    map[t.name] = u;
                }
                
                // create graph links
                for each (t in tuples) {
                    u = map[t.name];
                    
                    var count:int = 0;
                    for each (var name:String in t.imports) {
                        v = map[name];
                        if (v && count%4 === 0) data.addEdgeFor(u, v);
                        else trace ("Missing node: "+name);
                        count++;
                    }
                }
                
                // sort the list of children alphabetically by name
                for each (u in tree.nodes) {
                    u.sortEdgesBy(NodeSprite.CHILD_LINKS, "target.data.name");
                }
                
                data.tree = tree;
            }
            
            // DEBUG ******
            for each (var e:EdgeSprite in data.edges) {
            	e.addEventListener(MouseEvent.CLICK, function(evt:MouseEvent):void {
            		var clicked:EdgeSprite = evt.target as EdgeSprite;
            		trace("CLICK >> " + clicked.source.data.name + " - " + clicked.target.data.name);
//                    trace("CLICK >> " + clicked.source.data.id + " - " + clicked.target.data.id);
                    data.edges.setProperty("lineColor", clicked.lineColor);
                    data.edges.setProperty("props.$debug", false);
                    clicked.lineColor = 0xffff0000;
                    clicked.props.$debug = true;
            	});
            }
            // ************
            
            return data;
        }
        
    } // end of class DependencyGraph
}