// EdgeArrowColor.java:  miscellaneous static utility
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
