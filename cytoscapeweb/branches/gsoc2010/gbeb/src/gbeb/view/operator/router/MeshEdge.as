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

package gbeb.view.operator.router {
	import flash.geom.Point;

    public class MeshEdge {
        public var x1:Number;
        public var y1:Number;
        public var x2:Number;
        public var y2:Number;
        public var source:MeshNode;
        public var target:MeshNode;
        public var name:String;
				public var sourceOriPos:Point;
				public var targetOriPos:Point;
				public var controlPoints:Array = [];
				public var dataEdgeIntersectionPairs:Array = []; //stores the dataEdge and intersection point pair, which is used in calculating the eventual 
																							// intersection of this edge with all the data curves that passes it
        
				// ========[ CONSTRUCTOR ]==================================================================
				
				/**
				 * This class is used to store variables for the GBEB mesh */
        
        public function MeshEdge():void {
                
        }
    }
}