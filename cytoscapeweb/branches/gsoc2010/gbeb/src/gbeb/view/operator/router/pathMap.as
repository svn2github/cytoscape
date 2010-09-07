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

package gbeb.view.operator.router
{
	import flash.geom.Point;
	
	/**
	 * This class is used to keep track of the paths that are formed between any 2 pathing nodes.
	 */
	
	public class pathMap
	{
		private var _map:Array = new Array();
		private var _mapIndex:Array = new Array();
		
		// ========[ CONSTRUCTOR ]==================================================================
		
		public function pathMap(nodes:Array):void
		{	
			for each(var p1:Point in nodes)
			{
				var array:Array = new Array();		
				for each (var p2:Point in nodes)
				{
					var count:int = 0;
					array.push(count);			
				}
				_map.push(array);
				_mapIndex.push(p1);
			}
		}
		
		// ========[ PUBLIC METHODS ]===============================================================
		
		/**
		 * Returns the number of path that exist between source and target thus far */
		public function getPathCount(source:Point, target:Point):int
		{
			var array:Array = _map[_mapIndex.indexOf(source)];
			return array[array.indexOf(target)];
		}
		
		/**
		 * Increases the path count between source & target*/
		public function insertPath(source:Point, target:Point):void
		{
			if(source == target) return;
			if(_mapIndex.indexOf(source) == -1 || _mapIndex.indexOf(target) == -1) return;
			
			var array:Array = _map[_mapIndex.indexOf(source)];
			array[_mapIndex.indexOf(target)] += 1;
			
			array = _map[_mapIndex.indexOf(target)];
			array[_mapIndex.indexOf(source)] += 1;
		}
	} //end of Class
}