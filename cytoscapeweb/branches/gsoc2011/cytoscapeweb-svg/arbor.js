//
//  arbor.js - version 0.91
//  a graph vizualization toolkit
//
//  Copyright (c) 2011 Samizdat Drafting Co.
//  Physics code derived from springy.js, copyright (c) 2010 Dennis Hotson
// 
//  Permission is hereby granted, free of charge, to any person
//  obtaining a copy of this software and associated documentation
//  files (the "Software"), to deal in the Software without
//  restriction, including without limitation the rights to use,
//  copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the
//  Software is furnished to do so, subject to the following
//  conditions:
// 
//  The above copyright notice and this permission notice shall be
//  included in all copies or substantial portions of the Software.
// 
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
//  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
//  OTHER DEALINGS IN THE SOFTWARE.
//

// file from
// https://raw.github.com/gr3dman/arbor/df6b8c8b351763b94c9dcc7f3cb66a463d53deeb/lib/arbor.js

// changes to arbor here (https://github.com/gr3dman)
// https://github.com/samizdatco/arbor/pull/11/files

(function($){

  /*        etc.js */  var trace=function(msg){if(typeof(window)=="undefined"||!window.console){return}var len=arguments.length;var args=[];for(var i=0;i<len;i++){args.push("arguments["+i+"]")}eval("console.log("+args.join(",")+")")};var dirname=function(a){var b=a.replace(/^\/?(.*?)\/?$/,"$1").split("/");b.pop();return"/"+b.join("/")};var basename=function(b){var c=b.replace(/^\/?(.*?)\/?$/,"$1").split("/");var a=c.pop();if(a==""){return null}else{return a}};var _ordinalize_re=/(\d)(?=(\d\d\d)+(?!\d))/g;var ordinalize=function(a){var b=""+a;if(a<11000){b=(""+a).replace(_ordinalize_re,"$1,")}else{if(a<1000000){b=Math.floor(a/1000)+"k"}else{if(a<1000000000){b=(""+Math.floor(a/1000)).replace(_ordinalize_re,"$1,")+"m"}}}return b};var nano=function(a,b){return a.replace(/\{([\w\-\.]*)}/g,function(f,c){var d=c.split("."),e=b[d.shift()];$.each(d,function(){if(e.hasOwnProperty(this)){e=e[this]}else{e=f}});return e})};var objcopy=function(a){if(a===undefined){return undefined}if(a===null){return null}if(a.parentNode){return a}switch(typeof a){case"string":return a.substring(0);break;case"number":return a+0;break;case"boolean":return a===true;break}var b=($.isArray(a))?[]:{};$.each(a,function(d,c){b[d]=objcopy(c)});return b};var objmerge=function(d,b){d=d||{};b=b||{};var c=objcopy(d);for(var a in b){c[a]=b[a]}return c};var objcmp=function(e,c,d){if(!e||!c){return e===c}if(typeof e!=typeof c){return false}if(typeof e!="object"){return e===c}else{if($.isArray(e)){if(!($.isArray(c))){return false}if(e.length!=c.length){return false}}else{var h=[];for(var f in e){if(e.hasOwnProperty(f)){h.push(f)}}var g=[];for(var f in c){if(c.hasOwnProperty(f)){g.push(f)}}if(!d){h.sort();g.sort()}if(h.join(",")!==g.join(",")){return false}}var i=true;$.each(e,function(a){var b=objcmp(e[a],c[a]);i=i&&b;if(!i){return false}});return i}};var objkeys=function(b){var a=[];$.each(b,function(d,c){if(b.hasOwnProperty(d)){a.push(d)}});return a};var objcontains=function(c){if(!c||typeof c!="object"){return false}for(var b=1,a=arguments.length;b<a;b++){if(c.hasOwnProperty(arguments[b])){return true}}return false};var uniq=function(b){var a=b.length;var d={};for(var c=0;c<a;c++){d[b[c]]=true}return objkeys(d)};var arbor_path=function(){var a=$("script").map(function(b){var c=$(this).attr("src");if(!c){return}if(c.match(/arbor[^\/\.]*.js|dev.js/)){return c.match(/.*\//)||"/"}});if(a.length>0){return a[0]}else{return null}};
  /*     kernel.js */  var Kernel=function(b){var k=window.location.protocol=="file:"&&navigator.userAgent.toLowerCase().indexOf("chrome")>-1;var a=(window.Worker!==undefined&&!k);var i=null;var c=null;var f=[];f.last=new Date();var l=null;var e=null;var d=null;var h=null;var g=false;var j={system:b,tween:null,nodes:{},init:function(){if(typeof(Tween)!="undefined"){c=Tween()}else{if(typeof(arbor.Tween)!="undefined"){c=arbor.Tween()}else{c={busy:function(){return false},tick:function(){return true},to:function(){trace("Please include arbor-tween.js to enable tweens");c.to=function(){};return}}}}j.tween=c;var m=b.parameters();if(a){trace("using web workers");l=setInterval(j.screenUpdate,m.timeout);i=new Worker(arbor_path()+"arbor.js");i.onmessage=j.workerMsg;i.onerror=function(n){trace("physics:",n)};i.postMessage({type:"physics",physics:objmerge(m,{timeout:Math.ceil(m.timeout)})})}else{trace("couldn't use web workers, be careful...");i=Physics(m.dt,m.stiffness,m.repulsion,m.friction,j.system._updateGeometry);j.start()}return j},graphChanged:function(m){if(a){i.postMessage({type:"changes",changes:m})}else{i._update(m)}j.start()},particleModified:function(n,m){if(a){i.postMessage({type:"modify",id:n,mods:m})}else{i.modifyNode(n,m)}j.start()},physicsModified:function(m){if(!isNaN(m.timeout)){if(a){clearInterval(l);l=setInterval(j.screenUpdate,m.timeout)}else{clearInterval(d);d=null}}if(a){i.postMessage({type:"sys",param:m})}else{i.modifyPhysics(m)}j.start()},workerMsg:function(n){var m=n.data.type;if(m=="geometry"){j.workerUpdate(n.data)}else{trace("physics:",n.data)}},_lastPositions:null,workerUpdate:function(m){j._lastPositions=m;j._lastBounds=m.bounds},_lastFrametime:new Date().valueOf(),_lastBounds:null,_currentRenderer:null,screenUpdate:function(){var n=new Date().valueOf();var m=false;if(j._lastPositions!==null){j.system._updateGeometry(j._lastPositions);j._lastPositions=null;m=true}if(c&&c.busy()){m=true}if(j.system._updateBounds(j._lastBounds)){m=true}if(m){var o=j.system.renderer;if(o!==undefined){if(o!==e){o.init(j.system);e=o}if(c){c.tick()}o.redraw();var p=f.last;f.last=new Date();f.push(f.last-p);if(f.length>50){f.shift()}}}},physicsUpdate:function(){if(c){c.tick()}i.tick();var n=j.system._updateBounds();if(c&&c.busy()){n=true}var o=j.system.renderer;var m=new Date();var o=j.system.renderer;if(o!==undefined){if(o!==e){o.init(j.system);e=o}o.redraw({timestamp:m})}var q=f.last;f.last=m;f.push(f.last-q);if(f.length>50){f.shift()}var p=i.systemEnergy();if((p.mean+p.max)/2<0.05){if(h===null){h=new Date().valueOf()}if(new Date().valueOf()-h>1000){clearInterval(d);d=null}else{}}else{h=null}},fps:function(n){if(n!==undefined){var q=1000/Math.max(1,targetFps);j.physicsModified({timeout:q})}var r=0;for(var p=0,o=f.length;p<o;p++){r+=f[p]}var m=r/Math.max(1,f.length);if(!isNaN(m)){return Math.round(1000/m)}else{return 0}},start:function(m){if(d!==null){return}if(g&&!m){return}g=false;if(a){i.postMessage({type:"start"})}else{h=null;d=setInterval(j.physicsUpdate,j.system.parameters().timeout)}},stop:function(){g=true;if(a){i.postMessage({type:"stop"})}else{if(d!==null){clearInterval(d);d=null}}}};return j.init()};
  /*      atoms.js */  var Node=function(a){this._id=_nextNodeId++;this.data=a||{};this._mass=(a.mass!==undefined)?a.mass:1;this._fixed=(a.fixed===true)?true:false;this._p=new Point((typeof(a.x)=="number")?a.x:null,(typeof(a.y)=="number")?a.y:null);delete this.data.x;delete this.data.y;delete this.data.mass;delete this.data.fixed};var _nextNodeId=1;var Edge=function(b,c,a){this._id=_nextEdgeId--;this.source=b;this.target=c;this.length=(a.length!==undefined)?a.length:1;this.data=(a!==undefined)?a:{};delete this.data.length};var _nextEdgeId=-1;var Particle=function(a,b){this.p=a;this.m=b;this.v=new Point(0,0);this.f=new Point(0,0)};Particle.prototype.applyForce=function(a){this.f=this.f.add(a.divide(this.m))};var Spring=function(c,b,d,a){this.point1=c;this.point2=b;this.length=d;this.k=a};Spring.prototype.distanceToParticle=function(a){var c=that.point2.p.subtract(that.point1.p).normalize().normal();var b=a.p.subtract(that.point1.p);return Math.abs(b.x*c.x+b.y*c.y)};var Point=function(a,b){if(a&&a.hasOwnProperty("y")){b=a.y;a=a.x}this.x=a;this.y=b};Point.random=function(a){a=(a!==undefined)?a:5;return new Point(2*a*(Math.random()-0.5),2*a*(Math.random()-0.5))};Point.prototype={exploded:function(){return(isNaN(this.x)||isNaN(this.y))},add:function(a){return new Point(this.x+a.x,this.y+a.y)},subtract:function(a){return new Point(this.x-a.x,this.y-a.y)},multiply:function(a){return new Point(this.x*a,this.y*a)},divide:function(a){return new Point(this.x/a,this.y/a)},magnitude:function(){return Math.sqrt(this.x*this.x+this.y*this.y)},normal:function(){return new Point(-this.y,this.x)},normalize:function(){return this.divide(this.magnitude())}};
 
  //
//system.js
//
//the main controller object for creating/modifying graphs 
//

 var ParticleSystem = function(repulsion, stiffness, friction, centerGravity, targetFps, dt, precision){
 // also callable with ({stiffness:, repulsion:, friction:, timestep:, fps:, dt:, gravity:})
   
   var _changes=[]
   var _notification=null
   var _epoch = 0

   var _screenSize = null
   var _screenStep = .04
   var _screenPadding = [20,20,20,20]
   var _bounds = null
   var _boundsTarget = null

   if (typeof stiffness=='object'){
     var _p = stiffness
     friction = _p.friction
     repulsion = _p.repulsion
     targetFps = _p.fps
     dt = _p.dt
     stiffness = _p.stiffness
     centerGravity = _p.gravity
     precision = _p.precision
   }

   friction = isNaN(friction) ? .5 : friction
   repulsion = isNaN(repulsion) ? 1000 : repulsion
   targetFps = isNaN(targetFps) ? 55 : targetFps
   stiffness = isNaN(stiffness) ? 600 : stiffness
   dt = isNaN(dt) ? 0.02 : dt
   precision = isNaN(precision) ? .6 : precision
   centerGravity = (centerGravity===true)
   var _systemTimeout = (targetFps!==undefined) ? 1000/targetFps : 1000/50
   var _parameters = {repulsion:repulsion, stiffness:stiffness, friction:friction, dt:dt, gravity:centerGravity, precision:precision, timeout:_systemTimeout}
   var _energy

   var state = {
     renderer:null, // this is set by the library user
     tween:null, // gets filled in by the Kernel
     nodes:{}, // lookup based on node _id's from the worker
     edges:{}, // likewise
     adjacency:{}, // {name1:{name2:{}, name3:{}}}
     names:{}, // lookup table based on 'name' field in data objects
     kernel: null
   }

   var that={
     parameters:function(newParams){
       if (newParams!==undefined){
         if (!isNaN(newParams.precision)){
           newParams.precision = Math.max(0, Math.min(1, newParams.precision))
         }
         $.each(_parameters, function(p, v){
           if (newParams[p]!==undefined) _parameters[p] = newParams[p]
         })
         state.kernel.physicsModified(newParams)
       }
       return _parameters
     },

     fps:function(newFPS){
       if (newFPS===undefined) return state.kernel.fps()
       else that.parameters({timeout:1000/(newFPS||50)})
     },


     start:function(){
       state.kernel.start()
     },
     stop:function(){
       state.kernel.stop()
     },

     addNode:function(name, data){
       data = data || {}
       var priorNode = state.names[name]
       if (priorNode){
         priorNode.data = data
         return priorNode
       }else if (name!=undefined){
         // the data object has a few magic fields that are actually used
         // by the simulation:
         //   'mass' overrides the default of 1
         //   'fixed' overrides the default of false
         //   'x' & 'y' will set a starting position rather than 
         //             defaulting to random placement
         var x = (data.x!=undefined) ? data.x : null
         var y = (data.y!=undefined) ? data.y : null
         var fixed = (data.fixed) ? 1 : 0

         var node = new Node(data)
         node.name = name
         state.names[name] = node
         state.nodes[node._id] = node;

         _changes.push({t:"addNode", id:node._id, m:node.mass, x:x, y:y, f:fixed})
         that._notify();
         return node;

       }
     },

     // remove a node and its associated edges from the graph
     pruneNode:function(nodeOrName) {
       var node = that.getNode(nodeOrName)
       
       if (typeof(state.nodes[node._id]) !== 'undefined'){
         delete state.nodes[node._id]
         delete state.names[node.name]
       }


       $.each(state.edges, function(id, e){
         if (e.source._id === node._id || e.target._id === node._id){
           that.pruneEdge(e);
         }
       })

       _changes.push({t:"dropNode", id:node._id})
       that._notify();
     },

     getNode:function(nodeOrName){
       if (nodeOrName._id!==undefined){
         return nodeOrName
       }else if (typeof nodeOrName=='string' || typeof nodeOrName=='number'){
         return state.names[nodeOrName]
       }
       // otherwise let it return undefined
     },

     eachNode:function(callback){
       // callback should accept two arguments: Node, Point
       $.each(state.nodes, function(id, n){
         if (n._p.x==null || n._p.y==null) return
         var pt = (_screenSize!==null) ? that.toScreen(n._p) : n._p
         callback.call(that, n, pt);
       })
     },

     addEdge:function(source, target, data){
       source = that.getNode(source) || that.addNode(source)
       target = that.getNode(target) || that.addNode(target)
       data = data || {}
       var edge = new Edge(source, target, data);

       var src = source._id
       var dst = target._id
       state.adjacency[src] = state.adjacency[src] || {}
       state.adjacency[src][dst] = state.adjacency[src][dst] || []

       var exists = (state.adjacency[src][dst].length > 0)
       if (exists){
         // probably shouldn't allow multiple edges in same direction
         // between same nodes? for now just overwriting the data...
         $.extend(state.adjacency[src][dst].data, edge.data)
         return
       }else{
         state.edges[edge._id] = edge
         state.adjacency[src][dst].push(edge)
         var len = (edge.length!==undefined) ? edge.length : 1
         _changes.push({t:"addSpring", id:edge._id, fm:src, to:dst, l:len})
         that._notify()
       }

       return edge;

     },

     // remove an edge and its associated lookup entries
     pruneEdge:function(edge) {

       _changes.push({t:"dropSpring", id:edge._id})
       delete state.edges[edge._id]
       
       for (var x in state.adjacency){
         for (var y in state.adjacency[x]){
           var edges = state.adjacency[x][y];

           for (var j=edges.length - 1; j>=0; j--)  {
             if (state.adjacency[x][y][j]._id === edge._id){
               state.adjacency[x][y].splice(j, 1);
             }
           }
         }
       }

       that._notify();
     },

     // find the edges from node1 to node2
     getEdges:function(node1, node2) {
       node1 = that.getNode(node1)
       node2 = that.getNode(node2)
       if (!node1 || !node2) return []
       
       if (typeof(state.adjacency[node1._id]) !== 'undefined'
         && typeof(state.adjacency[node1._id][node2._id]) !== 'undefined'){
         return state.adjacency[node1._id][node2._id];
       }

       return [];
     },

     getEdgesFrom:function(node) {
       node = that.getNode(node)
       if (!node) return []
       
       if (typeof(state.adjacency[node._id]) !== 'undefined'){
         var nodeEdges = []
         $.each(state.adjacency[node._id], function(id, subEdges){
           nodeEdges = nodeEdges.concat(subEdges)
         })
         return nodeEdges
       }

       return [];
     },

     getEdgesTo:function(node) {
       node = that.getNode(node)
       if (!node) return []

       var nodeEdges = []
       $.each(state.edges, function(edgeId, edge){
         if (edge.target == node) nodeEdges.push(edge)
       })
       
       return nodeEdges;
     },

     eachEdge:function(callback){
       // callback should accept two arguments: Edge, Point
       $.each(state.edges, function(id, e){
         var p1 = state.nodes[e.source._id]._p
         var p2 = state.nodes[e.target._id]._p


         if (p1.x==null || p2.x==null) return
         
         p1 = (_screenSize!==null) ? that.toScreen(p1) : p1
         p2 = (_screenSize!==null) ? that.toScreen(p2) : p2
         
         if (p1 && p2) callback.call(that, e, p1, p2);
       })
     },


     prune:function(callback){
       // callback should be of the form Ä(node, {from:[],to:[]})

       var changes = {dropped:{nodes:[], edges:[]}}
       if (callback===undefined){
         $.each(state.nodes, function(id, node){
           changes.dropped.nodes.push(node)
           that.pruneNode(node)
         })
       }else{
         that.eachNode(function(node){
           var drop = callback.call(that, node, {from:that.getEdgesFrom(node), to:that.getEdgesTo(node)})
           if (drop){
             changes.dropped.nodes.push(node)
             that.pruneNode(node)
           }
         })
       }
       // trace('prune', changes.dropped)
       return changes
     },
     
     graft:function(branch){
       // branch is of the form: { nodes:{name1:{d}, name2:{d},...}, 
       //                          edges:{fromNm:{toNm1:{d}, toNm2:{d}}, ...} }

       var changes = {added:{nodes:[], edges:[]}}
       if (branch.nodes) $.each(branch.nodes, function(name, nodeData){
         var oldNode = that.getNode(name)
         // should probably merge any x/y/m data as well...
         // if (oldNode) $.extend(oldNode.data, nodeData)
         
         if (oldNode) oldNode.data = nodeData
         else changes.added.nodes.push( that.addNode(name, nodeData) )
         
         state.kernel.start()
       })
       
       if (branch.edges) $.each(branch.edges, function(src, dsts){
         var srcNode = that.getNode(src)
         if (!srcNode) changes.added.nodes.push( that.addNode(src, {}) )

         $.each(dsts, function(dst, edgeData){

           // should probably merge any x/y/m data as well...
           // if (srcNode) $.extend(srcNode.data, nodeData)


           // i wonder if it should spawn any non-existant nodes that are part
           // of one of these edge requests...
           var dstNode = that.getNode(dst)
           if (!dstNode) changes.added.nodes.push( that.addNode(dst, {}) )

           var oldEdges = that.getEdges(src, dst)
           if (oldEdges.length>0){
             // trace("update",src,dst)
             oldEdges[0].data = edgeData
           }else{
           // trace("new ->",src,dst)
             changes.added.edges.push( that.addEdge(src, dst, edgeData) )
           }
         })
       })

       // trace('graft', changes.added)
       return changes
     },

     merge:function(branch){
       var changes = {added:{nodes:[], edges:[]}, dropped:{nodes:[], edges:[]}}

       $.each(state.edges, function(id, edge){
         // if ((branch.edges[edge.source.name]===undefined || branch.edges[edge.source.name][edge.target.name]===undefined) &&
         //     (branch.edges[edge.target.name]===undefined || branch.edges[edge.target.name][edge.source.name]===undefined)){
         if ((branch.edges[edge.source.name]===undefined || branch.edges[edge.source.name][edge.target.name]===undefined)){
               that.pruneEdge(edge)
               changes.dropped.edges.push(edge)
             }
       })
       
       var prune_changes = that.prune(function(node, edges){
         if (branch.nodes[node.name] === undefined){
           changes.dropped.nodes.push(node)
           return true
         }
       })
       var graft_changes = that.graft(branch)        
       changes.added.nodes = changes.added.nodes.concat(graft_changes.added.nodes)
       changes.added.edges = changes.added.edges.concat(graft_changes.added.edges)
       changes.dropped.nodes = changes.dropped.nodes.concat(prune_changes.dropped.nodes)
       changes.dropped.edges = changes.dropped.edges.concat(prune_changes.dropped.edges)
       
       // trace('changes', changes)
       return changes
     },

     
     tweenNode:function(nodeOrName, dur, to){
       var node = that.getNode(nodeOrName)
       if (node) state.tween.to(node, dur, to)
     },

     tweenEdge:function(a,b,c,d){
       if (d===undefined){
         // called with (edge, dur, to)
         that._tweenEdge(a,b,c)
       }else{
         // called with (node1, node2, dur, to)
         var edges = that.getEdges(a,b)
         $.each(edges, function(i, edge){
           that._tweenEdge(edge, c, d)    
         })
       }
     },

     _tweenEdge:function(edge, dur, to){
       if (edge && edge._id!==undefined) state.tween.to(edge, dur, to)
     },

     _updateGeometry:function(e){
       if (e != undefined){          
         var stale = (e.epoch<_epoch)

         _energy = e.energy
         var pts = e.geometry // an array of the form [id1,x1,y1, id2,x2,y2, ...]
         if (pts!==undefined){
           for (var i=0, j=pts.length/3; i<j; i++){
             var id = pts[3*i]
                           
             // canary silencer...
             if (stale && state.nodes[id]==undefined) continue
             
             state.nodes[id]._p.x = pts[3*i + 1]
             state.nodes[id]._p.y = pts[3*i + 2]
           }
         }          
       }
     },
     
     // convert to/from screen coordinates
     screen:function(opts){
       if (opts == undefined) return {size:(_screenSize)? objcopy(_screenSize) : undefined, 
                                      padding:_screenPadding.concat(), 
                                      step:_screenStep}
       if (opts.size!==undefined) that.screenSize(opts.size.width, opts.size.height)
       if (!isNaN(opts.step)) that.screenStep(opts.step)
       if (opts.padding!==undefined) that.screenPadding(opts.padding)
     },
     
     screenSize:function(canvasWidth, canvasHeight){
       _screenSize = {width:canvasWidth,height:canvasHeight}
       that._updateBounds()
     },

     screenPadding:function(t,r,b,l){
       if ($.isArray(t)) trbl = t
       else trbl = [t,r,b,l]

       var top = trbl[0]
       var right = trbl[1]
       var bot = trbl[2]
       if (right===undefined) trbl = [top,top,top,top]
       else if (bot==undefined) trbl = [top,right,top,right]
       
       _screenPadding = trbl
     },

     screenStep:function(stepsize){
       _screenStep = stepsize
     },

     toScreen:function(p) {
       if (!_bounds || !_screenSize) return
       // trace(p.x, p.y)

       var _padding = _screenPadding || [0,0,0,0]
       var size = _bounds.bottomright.subtract(_bounds.topleft)
       var sx = _padding[3] + p.subtract(_bounds.topleft).divide(size.x).x * (_screenSize.width - (_padding[1] + _padding[3]))
       var sy = _padding[0] + p.subtract(_bounds.topleft).divide(size.y).y * (_screenSize.height - (_padding[0] + _padding[2]))

       // return arbor.Point(Math.floor(sx), Math.floor(sy))
       return arbor.Point(sx, sy)
     },
     
     fromScreen:function(s) {
       if (!_bounds || !_screenSize) return

       var _padding = _screenPadding || [0,0,0,0]
       var size = _bounds.bottomright.subtract(_bounds.topleft)
       var px = (s.x-_padding[3]) / (_screenSize.width-(_padding[1]+_padding[3]))  * size.x + _bounds.topleft.x
       var py = (s.y-_padding[0]) / (_screenSize.height-(_padding[0]+_padding[2])) * size.y + _bounds.topleft.y

       return arbor.Point(px, py);
     },

     _updateBounds:function(newBounds){
       // step the renderer's current bounding box closer to the true box containing all
       // the nodes. if _screenStep is set to 1 there will be no lag. if _screenStep is
       // set to 0 the bounding box will remain stationary after being initially set 
       if (_screenSize===null) return
       
       if (newBounds) _boundsTarget = newBounds
       else _boundsTarget = that.bounds()
       
       // _boundsTarget = newBounds || that.bounds()
       // _boundsTarget.topleft = new Point(_boundsTarget.topleft.x,_boundsTarget.topleft.y)
       // _boundsTarget.bottomright = new Point(_boundsTarget.bottomright.x,_boundsTarget.bottomright.y)

       var bottomright = new Point(_boundsTarget.bottomright.x, _boundsTarget.bottomright.y)
       var topleft = new Point(_boundsTarget.topleft.x, _boundsTarget.topleft.y)
       var dims = bottomright.subtract(topleft)
       var center = topleft.add(dims.divide(2))


       var MINSIZE = 4                                   // perfect-fit scaling
       // MINSIZE = Math.max(Math.max(MINSIZE,dims.y), dims.x) // proportional scaling

       var size = new Point(Math.max(dims.x,MINSIZE), Math.max(dims.y,MINSIZE))
       _boundsTarget.topleft = center.subtract(size.divide(2))
       _boundsTarget.bottomright = center.add(size.divide(2))

       if (!_bounds){
         if ($.isEmptyObject(state.nodes)) return false
         _bounds = _boundsTarget
         return true
       }
       
       // var stepSize = (Math.max(dims.x,dims.y)<MINSIZE) ? .2 : _screenStep
       var stepSize = _screenStep
       _newBounds = {
         bottomright: _bounds.bottomright.add( _boundsTarget.bottomright.subtract(_bounds.bottomright).multiply(stepSize) ),
         topleft: _bounds.topleft.add( _boundsTarget.topleft.subtract(_bounds.topleft).multiply(stepSize) )
       }
       
       // return true if we're still approaching the target, false if we're Ôclose enoughÕ
       var diff = new Point(_bounds.topleft.subtract(_newBounds.topleft).magnitude(), _bounds.bottomright.subtract(_newBounds.bottomright).magnitude())        
       if (diff.x*_screenSize.width>1 || diff.y*_screenSize.height>1){
         _bounds = _newBounds
         return true
       }else{
        return false        
       }
     },

     energy:function(){
       return _energy
     },

     bounds:function(){
       //  TL   -1
       //     -1   1
       //        1   BR
       var bottomright = null
       var topleft = null

       // find the true x/y range of the nodes
       $.each(state.nodes, function(id, node){
         if (!bottomright){
           bottomright = new Point(node._p)
           topleft = new Point(node._p)
           return
         }
       
         var point = node._p
         if (point.x===null || point.y===null) return
         if (point.x > bottomright.x) bottomright.x = point.x;
         if (point.y > bottomright.y) bottomright.y = point.y;          
         if   (point.x < topleft.x)   topleft.x = point.x;
         if   (point.y < topleft.y)   topleft.y = point.y;
       })


       // return the true range then let to/fromScreen handle the padding
       if (bottomright && topleft){
         return {bottomright: bottomright, topleft: topleft}
       }else{
         return {topleft: new Point(-1,-1), bottomright: new Point(1,1)};
       }
     },

     // Find the nearest node to a particular position
     nearest:function(pos){
       if (_screenSize!==null) pos = that.fromScreen(pos)
       // if screen size has been specified, presume pos is in screen pixel
       // units and convert it back to the particle system coordinates
       
       var min = {node: null, point: null, distance: null};
       var t = that;
       
       $.each(state.nodes, function(id, node){
         var pt = node._p
         if (pt.x===null || pt.y===null) return
         var distance = pt.subtract(pos).magnitude();
         if (min.distance === null || distance < min.distance){
           min = {node: node, point: pt, distance: distance};
           if (_screenSize!==null) min.screenPoint = that.toScreen(pt)
         }
       })
       
       if (min.node){
         if (_screenSize!==null) min.distance = that.toScreen(min.node.p).subtract(that.toScreen(pos)).magnitude()
          return min
       }else{
          return null
       }
     },

     _notify:function() {
       // pass on graph changes to the physics object in the worker thread
       // (using a short timeout to batch changes)
       if (_notification===null) _epoch++
       else clearTimeout(_notification)
       
       _notification = setTimeout(that._synchronize,20)
       // that._synchronize()
     },
     _synchronize:function(){
       if (_changes.length>0){
         state.kernel.graphChanged(_changes)
         _changes = []
         _notification = null
       }
     }
   }    
   
   state.kernel = Kernel(that)
   state.tween = state.kernel.tween || null



   // some magic attrs to make the Node objects phone-home their physics-relevant changes

   var defineProperty = (window.__defineGetter__ == null || window.__defineSetter__ == null) ? 
	  function (obj, name, desc){
        if(!obj.hasOwnProperty(name)){
	      Object.defineProperty(obj, name, desc);
        }
      }
        :
      function (obj, name, desc) {
        if (desc.get)
          obj.__defineGetter__(name, desc.get)
        if (desc.set)
          obj.__defineSetter__(name, desc.set)
      };

   var RoboPoint = function (n) {
     this._n = n;
   }
   RoboPoint.prototype = new Point();
   defineProperty(RoboPoint.prototype, "x", {
     get: function(){ return this._n._p.x; },
     set: function(newX){ state.kernel.particleModified(this._n._id, {x:newX}) }
   })
   defineProperty(RoboPoint.prototype, "y", {
     get: function(){ return this._n._p.y; },
     set: function(newY){ state.kernel.particleModified(this._n._id, {y:newY}) }
   })

   defineProperty(Node.prototype, "p", {
     get: function() { 
       return new RoboPoint(this)
     },
     set: function(newP) { 
       this._p.x = newP.x
       this._p.y = newP.y
       state.kernel.particleModified(this._id, {x:newP.x, y:newP.y})
     }
   })
   
   defineProperty(Node.prototype, "mass", {
     get: function() { return this._mass; },
     set: function(newM) { 
       this._mass = newM
       state.kernel.particleModified(this._id, {m:newM})
     }
   })

   defineProperty(Node.prototype, "tempMass", {
     set: function(newM) { 
       state.kernel.particleModified(this._id, {_m:newM})
     }
   })

   defineProperty(Node.prototype, "fixed", {
     get: function() { return this._fixed; },
     set:function(isFixed) { 
       this._fixed = isFixed
       state.kernel.particleModified(this._id, {f:isFixed?1:0})
     }
   })
   
   return that
 }
 
  
  /* barnes-hut.js */  var BarnesHutTree=function(){var b=[];var a=0;var e=null;var d=0.5;var c={init:function(g,h,f){d=f;a=0;e=c._newBranch();e.origin=g;e.size=h.subtract(g)},insert:function(j){var f=e;var g=[j];while(g.length){var h=g.shift();var m=h._m||h.m;var p=c._whichQuad(h,f);if(f[p]===undefined){f[p]=h;f.mass+=m;if(f.p){f.p=f.p.add(h.p.multiply(m))}else{f.p=h.p.multiply(m)}}else{if("origin" in f[p]){f.mass+=(m);if(f.p){f.p=f.p.add(h.p.multiply(m))}else{f.p=h.p.multiply(m)}f=f[p];g.unshift(h)}else{var l=f.size.divide(2);var n=new Point(f.origin);if(p[0]=="s"){n.y+=l.y}if(p[1]=="e"){n.x+=l.x}var o=f[p];f[p]=c._newBranch();f[p].origin=n;f[p].size=l;f.mass=m;f.p=h.p.multiply(m);f=f[p];if(o.p.x===h.p.x&&o.p.y===h.p.y){var k=l.x*0.08;var i=l.y*0.08;o.p.x=Math.min(n.x+l.x,Math.max(n.x,o.p.x-k/2+Math.random()*k));o.p.y=Math.min(n.y+l.y,Math.max(n.y,o.p.y-i/2+Math.random()*i))}g.push(o);g.unshift(h)}}}},applyForces:function(m,g){var f=[e];while(f.length){node=f.shift();if(node===undefined){continue}if(m===node){continue}if("f" in node){var k=m.p.subtract(node.p);var l=Math.max(1,k.magnitude());var i=((k.magnitude()>0)?k:Point.random(1)).normalize();m.applyForce(i.multiply(g*(node._m||node.m)).divide(l*l))}else{var j=m.p.subtract(node.p.divide(node.mass)).magnitude();var h=Math.sqrt(node.size.x*node.size.y);if(h/j>d){f.push(node.ne);f.push(node.nw);f.push(node.se);f.push(node.sw)}else{var k=m.p.subtract(node.p.divide(node.mass));var l=Math.max(1,k.magnitude());var i=((k.magnitude()>0)?k:Point.random(1)).normalize();m.applyForce(i.multiply(g*(node.mass)).divide(l*l))}}}},_whichQuad:function(i,f){if(i.p.exploded()){return null}var h=i.p.subtract(f.origin);var g=f.size.divide(2);if(h.y<g.y){if(h.x<g.x){return"nw"}else{return"ne"}}else{if(h.x<g.x){return"sw"}else{return"se"}}},_newBranch:function(){if(b[a]){var f=b[a];f.ne=f.nw=f.se=f.sw=undefined;f.mass=0;delete f.p}else{f={origin:null,size:null,nw:undefined,ne:undefined,sw:undefined,se:undefined,mass:0};b[a]=f}a++;return f}};return c};
  /*    physics.js */  var Physics=function(a,m,n,e,h){var f=BarnesHutTree();var c={particles:{},springs:{}};var l={particles:{}};var o=[];var k=[];var d=0;var b={sum:0,max:0,mean:0};var g={topleft:new Point(-1,-1),bottomright:new Point(1,1)};var j=1000;var i={stiffness:(m!==undefined)?m:1000,repulsion:(n!==undefined)?n:600,friction:(e!==undefined)?e:0.3,gravity:false,dt:(a!==undefined)?a:0.02,theta:0.4,init:function(){return i},modifyPhysics:function(p){$.each(["stiffness","repulsion","friction","gravity","dt","precision"],function(r,s){if(p[s]!==undefined){if(s=="precision"){i.theta=1-p[s];return}i[s]=p[s];if(s=="stiffness"){var q=p[s];$.each(c.springs,function(u,t){t.k=q})}}})},addNode:function(u){var t=u.id;var q=u.m;var p=g.bottomright.x-g.topleft.x;var s=g.bottomright.y-g.topleft.y;var r=new Point((u.x!=null)?u.x:g.topleft.x+p*Math.random(),(u.y!=null)?u.y:g.topleft.y+s*Math.random());c.particles[t]=new Particle(r,q);c.particles[t].connections=0;c.particles[t].fixed=(u.f===1);l.particles[t]=c.particles[t];o.push(c.particles[t])},dropNode:function(s){var r=s.id;var q=c.particles[r];var p=$.inArray(q,o);if(p>-1){o.splice(p,1)}delete c.particles[r];delete l.particles[r]},modifyNode:function(r,p){if(r in c.particles){var q=c.particles[r];if("x" in p){q.p.x=p.x}if("y" in p){q.p.y=p.y}if("m" in p){q.m=p.m}if("f" in p){q.fixed=(p.f===1)}if("_m" in p){if(q._m===undefined){q._m=q.m}q.m=p._m}}},addSpring:function(t){var s=t.id;var p=t.l;var r=c.particles[t.fm];var q=c.particles[t.to];if(r!==undefined&&q!==undefined){c.springs[s]=new Spring(r,q,p,i.stiffness);k.push(c.springs[s]);r.connections++;q.connections++;delete l.particles[t.fm];delete l.particles[t.to]}},dropSpring:function(s){var r=s.id;var q=c.springs[r];q.point1.connections--;q.point2.connections--;var p=$.inArray(q,k);if(p>-1){k.splice(p,1)}delete c.springs[r]},_update:function(p){d++;$.each(p,function(q,r){if(r.t in i){i[r.t](r)}});return d},tick:function(){i.tendParticles();i.eulerIntegrator(i.dt);i.tock()},tock:function(){var p=[];$.each(c.particles,function(r,q){p.push(r);p.push(q.p.x);p.push(q.p.y)});if(h){h({geometry:p,epoch:d,energy:b,bounds:g})}},tendParticles:function(){$.each(c.particles,function(q,p){if(p._m!==undefined){if(Math.abs(p.m-p._m)<1){p.m=p._m;delete p._m}else{p.m*=0.98}}p.v.x=p.v.y=0})},eulerIntegrator:function(p){if(i.repulsion>0){if(i.theta>0){i.applyBarnesHutRepulsion()}else{i.applyBruteForceRepulsion()}}if(i.stiffness>0){i.applySprings()}i.applyCenterDrift();if(i.gravity){i.applyCenterGravity()}i.updateVelocity(p);i.updatePosition(p)},applyBruteForceRepulsion:function(){$.each(c.particles,function(q,p){$.each(c.particles,function(s,r){if(p!==r){var u=p.p.subtract(r.p);var v=Math.max(1,u.magnitude());var t=((u.magnitude()>0)?u:Point.random(1)).normalize();p.applyForce(t.multiply(i.repulsion*(r._m||r.m)*0.5).divide(v*v*0.5));r.applyForce(t.multiply(i.repulsion*(p._m||p.m)*0.5).divide(v*v*-0.5))}})})},applyBarnesHutRepulsion:function(){if(!g.topleft||!g.bottomright){return}var q=new Point(g.bottomright);var p=new Point(g.topleft);f.init(p,q,i.theta);$.each(c.particles,function(s,r){f.insert(r)});$.each(c.particles,function(s,r){f.applyForces(r,i.repulsion)})},applySprings:function(){$.each(c.springs,function(t,p){var s=p.point2.p.subtract(p.point1.p);var q=p.length-s.magnitude();var r=((s.magnitude()>0)?s:Point.random(1)).normalize();p.point1.applyForce(r.multiply(p.k*q*-0.5));p.point2.applyForce(r.multiply(p.k*q*0.5))})},applyCenterDrift:function(){var q=0;var r=new Point(0,0);$.each(c.particles,function(t,s){r.add(s.p);q++});if(q==0){return}var p=r.divide(-q);$.each(c.particles,function(t,s){s.applyForce(p)})},applyCenterGravity:function(){$.each(c.particles,function(r,p){var q=p.p.multiply(-1);p.applyForce(q.multiply(i.repulsion/100))})},updateVelocity:function(p){$.each(c.particles,function(t,q){if(q.fixed){q.v=new Point(0,0);q.f=new Point(0,0);return}var s=q.v.magnitude();q.v=q.v.add(q.f.multiply(p)).multiply(1-i.friction);q.f.x=q.f.y=0;var r=q.v.magnitude();if(r>j){q.v=q.v.divide(r*r)}})},updatePosition:function(q){var r=0,p=0,u=0;var t=null;var s=null;$.each(c.particles,function(w,v){v.p=v.p.add(v.v.multiply(q));var x=v.v.magnitude();var z=x*x;r+=z;p=Math.max(z,p);u++;if(!t){t=new Point(v.p.x,v.p.y);s=new Point(v.p.x,v.p.y);return}var y=v.p;if(y.x===null||y.y===null){return}if(y.x>t.x){t.x=y.x}if(y.y>t.y){t.y=y.y}if(y.x<s.x){s.x=y.x}if(y.y<s.y){s.y=y.y}});b={sum:r,max:p,mean:r/u,n:u};g={topleft:s||new Point(-1,-1),bottomright:t||new Point(1,1)}},systemEnergy:function(p){return b}};return i.init()};var _nearParticle=function(b,c){var c=c||0;var a=b.x;var f=b.y;var e=c*2;return new Point(a-c+Math.random()*e,f-c+Math.random()*e)};

  // if called as a worker thread, set up a run loop for the Physics object and bail out
  if (typeof(window)=='undefined') return (function(){
  /* hermetic.js */  $={each:function(d,e){if($.isArray(d)){for(var c=0,b=d.length;c<b;c++){e(c,d[c])}}else{for(var a in d){e(a,d[a])}}},map:function(a,c){var b=[];$.each(a,function(f,e){var d=c(e);if(d!==undefined){b.push(d)}});return b},extend:function(c,b){if(typeof b!="object"){return c}for(var a in b){if(b.hasOwnProperty(a)){c[a]=b[a]}}return c},isArray:function(a){if(!a){return false}return(a.constructor.toString().indexOf("Array")!=-1)},inArray:function(c,a){for(var d=0,b=a.length;d<b;d++){if(a[d]===c){return d}}return -1},isEmptyObject:function(a){if(typeof a!=="object"){return false}var b=true;$.each(a,function(c,d){b=false});return b},};
  /*     worker.js */  var PhysicsWorker=function(){var b=20;var a=null;var d=null;var c=null;var g=[];var f=new Date().valueOf();var e={init:function(h){e.timeout(h.timeout);a=Physics(h.dt,h.stiffness,h.repulsion,h.friction,e.tock);return e},timeout:function(h){if(h!=b){b=h;if(d!==null){e.stop();e.go()}}},go:function(){if(d!==null){return}c=null;d=setInterval(e.tick,b)},stop:function(){if(d===null){return}clearInterval(d);d=null},tick:function(){a.tick();var h=a.systemEnergy();if((h.mean+h.max)/2<0.05){if(c===null){c=new Date().valueOf()}if(new Date().valueOf()-c>1000){e.stop()}else{}}else{c=null}},tock:function(h){h.type="geometry";postMessage(h)},modifyNode:function(i,h){a.modifyNode(i,h);e.go()},modifyPhysics:function(h){a.modifyPhysics(h)},update:function(h){var i=a._update(h)}};return e};var physics=PhysicsWorker();onmessage=function(a){if(!a.data.type){postMessage("ÀkŽrnl?");return}if(a.data.type=="physics"){var b=a.data.physics;physics.init(a.data.physics);return}switch(a.data.type){case"modify":physics.modifyNode(a.data.id,a.data.mods);break;case"changes":physics.update(a.data.changes);physics.go();break;case"start":physics.go();break;case"stop":physics.stop();break;case"sys":var b=a.data.param||{};if(!isNaN(b.timeout)){physics.timeout(b.timeout)}physics.modifyPhysics(b);physics.go();break}};
  })()


  arbor = (typeof(arbor)!=='undefined') ? arbor : {}
  $.extend(arbor, {
    // object constructors (don't use ÔnewÕ, just call them)
    ParticleSystem:ParticleSystem,
    Point:function(x, y){ return new Point(x, y) },

    // immutable object with useful methods
    etc:{      
      trace:trace,              // Ä(msg) -> safe console logging
      dirname:dirname,          // Ä(path) -> leading part of path
      basename:basename,        // Ä(path) -> trailing part of path
      ordinalize:ordinalize,    // Ä(num) -> abbrev integers (and add commas)
      objcopy:objcopy,          // Ä(old) -> clone an object
      objcmp:objcmp,            // Ä(a, b, strict_ordering) -> t/f comparison
      objkeys:objkeys,          // Ä(obj) -> array of all keys in obj
      objmerge:objmerge,        // Ä(dst, src) -> like $.extend but non-destructive
      uniq:uniq,                // Ä(arr) -> array of unique items in arr
      arbor_path:arbor_path,    // Ä() -> guess the directory of the lib code
    }
  })
  
})(this.jQuery)