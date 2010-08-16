/*
  This file is part of Cytoscape Web.
  Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
    - Agilent Technologies
    - Institut Pasteur
    - Institute for Systems Biology
    - Memorial Sloan-Kettering Cancer Center
    - National Center for Integrative Biomedical Informatics
    - Unilever
    - University of California San Diego
    - University of California San Francisco
    - University of Toronto

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
*/
package gbeb.util.converters {
    import flare.data.DataField;
    import flare.data.DataSchema;
    import flare.data.DataSet;
    import flare.data.DataTable;
    import flare.data.DataUtil;
    import flare.data.converters.IDataConverter;
    
    import flash.geom.Point;
    import flash.utils.IDataInput;
    import flash.utils.IDataOutput;
    
    import mx.utils.StringUtil;


    /**
     * Converts data between XGMML markup and flare DataSet instances.
     * 
     * XGMML 1.0 Draft Specification:
     * 
     *   http://www.cs.rpi.edu/~puninj/XGMML/DOC/xgmml_schema.html
     *   http://www.cs.rpi.edu/~puninj/XGMML/draft-xgmml.html
     */
    public class XGMMLConverter implements IDataConverter {
    	
    	private namespace _defNamespace = "http://www.cs.rpi.edu/XGMML";
        use namespace _defNamespace;
    	
        // ========[ CONSTANTS ]====================================================================
        
        private static const VIZMAP_ATTR_PREFIX:String = "vizmap:";
        
        private static const NODE_ATTR:Object = {
            id: 1, label: "", weight: 1, name: ""
        }
        private static const EDGE_ATTR:Object = {
            id: 1, source: 1, target: 1, label: "", name: "", weight: 1, directed: false
        };
    
        // TODO: customizable!!!
        // **********************
        private static const DOCUMENT_VERSION:String = "0.1";
        private static const GRAPH_LABEL:String    = "Cytoscape Web";
//        private static const NETWORK_TITLE:String  = "Cytoscape Web";
//        private static const NETWORK_TYPE:String   = "Protein-Protein Interaction";
//        private static const NETWORK_SOURCE:String = "http://www.cytoscape.org/";
//        private static const NETWORK_ABOUT:String  = "http://www.cytoscape.org/";
//        private static const NETWORK_FORMAT:String = "Cytoscape-XGMML";
        // **********************
        
        public static const DEFAULT_NAMESPACE:String   = "http://www.cs.rpi.edu/XGMML";
        private static const DMCI_NAMESPACE:String      = "http://purl.org/dc/elements/1.1/";
        private static const XLINK_NAMESPACE:String     = "http://www.w3.org/1999/xlink";
        private static const RDF_NAMESPACE:String       = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        private static const CYTOSCAPE_NAMESPACE:String = "http://www.cytoscape.org";
    
        private static const ROOT:String = "<graph/>";       

        private static const GRAPH:String      = "graph";
        private static const DIRECTED:String   = "directed";
        private static const UNDIRECTED:String   = "undirected";
        private static const GRAPHIC_INFO:String = "Graphic";
        
        private static const ATTRIBUTE:String  = "att";
        private static const GRAPHICS:String   = "graphics";
        private static const DEFAULT:String    = "default";
        
        private static const NODE:String   = "node";
        private static const EDGE:String   = "edge";
        private static const ID:String     = "id";
        private static const LABEL:String  = "label";
        private static const SOURCE:String = "source";
        private static const TARGET:String = "target";
        private static const WEIGHT:String = "weight";
        private static const DATA:String   = "data";
        private static const TYPE:String   = "type";
        private static const NAME:String   = "name";
        private static const VALUE:String  = "value";
        
        private static const INTEGER:String = "integer";
        private static const REAL:String    = "real";
        private static const LIST:String    = "list";
        private static const STRING:String  = "string";
        private static const BOOLEAN:String  = "boolean";
        
        private static const TRUE:String  = "1";
        private static const FALSE:String = "0";
        
        // It has to specify the XGMML default namespace before getting nodes/edges:
        private static const NS:Namespace = new Namespace(DEFAULT_NAMESPACE);
        private static const CY:Namespace = new Namespace(CYTOSCAPE_NAMESPACE);
    	
        // ========[ PRIVATE PROPERTIES ]===========================================================
        
        private var _noGraphicInfo:Boolean = false;
        private var _points:Object;
        private var _minX:Number = Number.POSITIVE_INFINITY;
        private var _minY:Number = Number.POSITIVE_INFINITY;
        private var _maxX:Number = Number.NEGATIVE_INFINITY
        private var _maxY:Number = Number.NEGATIVE_INFINITY;
        
        // ========[ PUBLIC PROPERTIES ]============================================================

        
        public function get points():Object {
            return _points;
        }
   
        // ========[ CONSTRUCTOR ]==================================================================
        
        public function XGMMLConverter() {
        }
        
        // ========[ PUBLIC METHODS ]===============================================================
        
        // -- reader ----------------------------------------------------------
        
        /** @inheritDoc */
        public function read(input:IDataInput, schema:DataSchema=null):DataSet {
            var str:String = input.readUTFBytes(input.bytesAvailable);
            var idx:int = str.indexOf(ROOT);
            if (idx > 0) {
                str = str.substr(0, idx+ROOT.length) + str.substring(str.indexOf(">", idx));
            }
            return parse(XML(str), schema);
        }
        
        /**
         * Parses a XGMML object into a DataSet instance.
         * @param xgmml the XML object containing XGMML markup
         * @param schema a DataSchema (typically null, as XGMML contains
         *  schema information)
         * @return the parsed DataSet instance
         */
        public function parse(xgmml:XML, schema:DataSchema=null):DataSet {
            var lookup:Object = {};
            var nodes:Array = [], n:Object;
            var edges:Array = [], e:Object;
            var id:String, sid:String, tid:String;
            var def:Object, type:int;
            var group:String, attrName:String, attrType:String;
            
            // Does this XGMML model have graphical information?
            // Let's just check for explicit false statements, as if the default were true,
            // because Cytoscape never creates this attribute, but has graphics info.
            _noGraphicInfo = String(xgmml.@[GRAPHIC_INFO]) === FALSE;
            
            var nodeSchema:DataSchema = new DataSchema();
            var edgeSchema:DataSchema = new DataSchema();
            
            // set schema defaults
            nodeSchema.addField(new DataField(ID, DataUtil.STRING));
            nodeSchema.addField(new DataField(LABEL, DataUtil.STRING));
            nodeSchema.addField(new DataField(WEIGHT, DataUtil.NUMBER));
            nodeSchema.addField(new DataField(NAME, DataUtil.STRING));
            
            edgeSchema.addField(new DataField(ID, DataUtil.STRING));
            edgeSchema.addField(new DataField(SOURCE, DataUtil.STRING));
            edgeSchema.addField(new DataField(TARGET, DataUtil.STRING));
            edgeSchema.addField(new DataField(LABEL, DataUtil.STRING)); // Edge label cannot be an ID!
            edgeSchema.addField(new DataField(WEIGHT, DataUtil.NUMBER));
            var directed:Boolean = TRUE == xgmml.@[DIRECTED] ? true : false;
            edgeSchema.addField(new DataField(DIRECTED, DataUtil.BOOLEAN, directed));

            // Parse nodes
            // ------------------------------------------------------
            var nodesList:XMLList = xgmml.node;
            var node:XML;
         
            for each (node in nodesList) {
                id = StringUtil.trim("" + node.@[ID]);
                if (id === "") throw new Error("The 'id' attribute is mandatory for 'node' tags");
                lookup[id] = (n = parseData(node, nodeSchema));
                nodes.push(n);
                parsePoints(id, node);
            }
           
            // Parse edges
            // ------------------------------------------------------
            // Parse IDs first:
            var edgesIds:Object = {};
            var edgesList:XMLList = xgmml.edge;
            var edge:XML;
            
            for each (edge in edgesList) {
                id = StringUtil.trim("" + edge.@[ID]);
                if (id !== "") edgesIds[id] = true;
            }
            
            var count:int = 1;
            
            // Parse the attributes:
            for each (edge in xgmml.edge) {
                id  = edge.@[ID].toString();
                sid = edge.@[SOURCE].toString();
                tid = edge.@[TARGET].toString();

				if (StringUtil.trim(id) === "") {
	                while (edgesIds[count.toString()] === true) ++count;
	                id = count.toString();
	                edgesIds[id] = true;
	                edge.@[ID] = id;
	                count++;
				}
                
                // error checking
                if (!lookup.hasOwnProperty(sid))
                    error("Edge "+id+" references unknown node: "+sid);
                if (!lookup.hasOwnProperty(tid))
                    error("Edge "+id+" references unknown node: "+tid);
                                
                edges.push(e = parseData(edge, edgeSchema));
            }
            
            return new DataSet(
                new DataTable(nodes, nodeSchema),
                new DataTable(edges, edgeSchema)
            );
        }

        // -- writer ----------------------------------------------------------
        
        /** @inheritDoc */
        public function write(dtset:DataSet, output:IDataOutput=null):IDataOutput {
        	// TODO...
            return null;
        }
        
        // ========[ PRIVATE METHODS ]==============================================================
        
        private function parseData(tag:XML, schema:DataSchema):Object {
            var data:Object = {};
            var name:String, field:DataField, value:Object;
            
            // set default values
            for (var i:int = 0; i < schema.numFields; ++i) {
                field = schema.getFieldAt(i);
                data[field.name] = field.defaultValue;
            }
            
            // get attribute values
            for each (var attribute:XML in tag.@*) {
                name = attribute.name().toString();
                field = schema.getFieldByName(name);
                if (field != null)
                    data[name] = DataUtil.parseValue(attribute[0].toString(), field.type);
            }
            
            // get "att" tags:
            for each (var att:XML in tag.att) {
            	parseAtt(att, schema, data);
            }
            
            // TODO: get RDF (Resource Description Framework) ???
            
            return data;
        }
        
        private function parseAtt(att:XML, schema:DataSchema, data:Object):void {
            var field:DataField, value:Object;
            var name:String = att.@[NAME].toString();
            
            if (name == null) return;
            
            var type:int = toCW_Type(att.@[TYPE].toString());
            
            // Add the attribute definition to the schema:
            if (schema.getFieldById(name) == null) {
                schema.addField(new DataField(name, type));
            }
            
            // Add <att> tags data:
            if (type === DataUtil.OBJECT) {
                // If it is a list, add the nested <att> tags recursively:
                var arr:Array = [];
                for each (var innerAtt:XML in att.att) {
                    var innerData:*;             
                    if (innerAtt.@[NAME][0] !== undefined) {
                        innerData = {};
                        parseAtt(innerAtt, schema, innerData);
                    } else {
                        var innerType:int = toCW_Type(innerAtt.@[TYPE].toString());
                        innerData = DataUtil.parseValue(innerAtt.@[VALUE], innerType);
                    }
                    arr.push(innerData);
                }
                data[name] = arr;
            } else {
                // Otherwise, just add the single att data:
                data[name] = DataUtil.parseValue(att.@[VALUE], type);
            }
        }

        private function parsePoints(id:String, xml:XML):void {
            // Note Cytoscape does not set the "Graphic" attribute,
            // so we just check whether or not there is a graphics tag:
            var g:XML = xml[GRAPHICS][0];
            
            if (!(g == null || _noGraphicInfo)) {
                // Positioning (x,y):
                if (xml.localName() === NODE) {
                	var x:Number = g.@x[0]; var y:Number = g.@y[0];
                	if (!isNaN(x) && !isNaN(y)) {
                	    if (_points == null) _points = [];
                	    _points[id] = new Point(x, y);
                    }
                }
            }
        }
        
        // -- static helpers --------------------------------------------------
        
        private static function toString(o:Object, type:int):String {
            return o != null ? o.toString() : ""; // TODO: formatting control?
        }
        
        /**
         * Converts from XGMML data types to Flare types.
         * XGMML TYPES: list | string | integer | real
         */
        private static function toCW_Type(type:String):int {
            switch (type) {
                case INTEGER: return DataUtil.INT;
                case REAL:    return DataUtil.NUMBER;
                case LIST:    return DataUtil.OBJECT;
                case STRING:
                default:      return DataUtil.STRING;
            }
        }
        
        /**
         * Converts from Flare data types to XGMML types.
         */
        private static function fromCW_Type(type:int):String {        	
            switch (type) {
                case DataUtil.INT:      return INTEGER;
                case DataUtil.NUMBER:   return REAL;
                case DataUtil.OBJECT:   return LIST;
                case DataUtil.BOOLEAN:  return BOOLEAN;
                case DataUtil.DATE:
                case DataUtil.STRING:
                default:                return STRING;
            }
        }
        
        /**
         * @param hanchor the XGMML value for "labelanchor"
         *                (see http://www.cs.rpi.edu/~puninj/XGMML/draft-xgmml.html#GlobalA and 
         *                 http://www.inf.uni-konstanz.de/algo/lehre/ws04/pp/api/y/io/doc-files/gml-comments.html)
         */
        private static function toCW_HAnchor(labelanchor:String):String {
            if (labelanchor != null) labelanchor = labelanchor.toLowerCase();
            switch (labelanchor) {
                case "ne":
                case "se":
                case "e": return "left";
                case "nw":
                case "sw":
                case "w": return "right";
                default:  return "center";
            }
        }
        
        /**
         * @param vanchor the XGMML value for "labelanchor"
         *                (see http://www.cs.rpi.edu/~puninj/XGMML/draft-xgmml.html#GlobalA and 
         *                 http://www.inf.uni-konstanz.de/algo/lehre/ws04/pp/api/y/io/doc-files/gml-comments.html)
         */
        private static function toCW_VAnchor(labelanchor:String):String {
            if (labelanchor != null) labelanchor = labelanchor.toLowerCase();
            switch (labelanchor) {
                case "ne":
                case "nw":
                case "n": return "bottom";
                case "se":
                case "sw":
                case "s": return "top";
                default:  return "middle";
            }
        }
        
        private static function error(msg:String):void {
            throw new Error(msg);
        }
    }
}
