// EdgeArrowColor.java:  miscellaneous static utility

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.vizmap;
//--------------------------------------------------------------------------------------
import java.io.*;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.*;

import y.view.Arrow;
import y.view.LineType;
//------------------------------------------------------------------------------
public class EdgeArrowColor {

    static Polygon p;

    public static void init() {
	p = new Polygon();
	p.addPoint(0,0);
	p.addPoint(-40,20);
	p.addPoint(-30,0);
	p.addPoint(-40,-20);
	Arrow.addCustomArrow("BigDelta",p,new Color(255,128,0));
    }
//------------------------------------------------------------------------------
public static void removeThenAddEdgeColor(AttributeMapper aMapper, String key, Color c) {
    //System.out.println(key);
    DiscreteMapper dmColor =
	(DiscreteMapper)
	aMapper.getValueMapper(VizMapperCategories.EDGE_COLOR);
    
    Map valueMapColor = dmColor.getValueMap();
    valueMapColor.remove(key);
    valueMapColor.put(key,c);
    
    DiscreteMapper dmSourceDec =
	(DiscreteMapper)
	aMapper.getValueMapper(VizMapperCategories.EDGE_SOURCE_DECORATION);
    if(dmSourceDec!=null) {
	Map valueMapSourceDec = dmSourceDec.getValueMap();
	Arrow sourceArrow =	(Arrow)valueMapSourceDec.get(key);
	if(sourceArrow!=null) {
	    String sourceArrowName = sourceArrow.getCustomName();
	    if(sourceArrowName!=null) {
		//System.out.println("fixing source arrow");
		Shape sourceShape = sourceArrow.getShape();
		Arrow sourceArrowNew =
		    Arrow.addCustomArrow(sourceArrowName,sourceShape,c);
		valueMapSourceDec.remove(key);
		valueMapSourceDec.put(key,sourceArrowNew);
	    }
	}
    }
    
    DiscreteMapper dmTargetDec =
	(DiscreteMapper)
	aMapper.getValueMapper(VizMapperCategories.EDGE_TARGET_DECORATION);
    if(dmTargetDec!=null) {
	Map valueMapTargetDec = dmTargetDec.getValueMap();
	Arrow targetArrow =	(Arrow)valueMapTargetDec.get(key);
	if(targetArrow!=null) {
	    String targetArrowName = targetArrow.getCustomName();
	    if(targetArrowName!=null) {
		//System.out.println("fixing target arrow");
		Shape targetShape = targetArrow.getShape();
		Arrow targetArrowNew =
		    Arrow.addCustomArrow(targetArrowName,targetShape,c);
		valueMapTargetDec.remove(key);
		valueMapTargetDec.put(key,targetArrowNew);

	    }
	}
    }
} // removeThenAddEdgeColor

}


