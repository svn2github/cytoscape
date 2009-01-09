// MapperAndAttribute.java
//------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------
//package cytoscape.dialogs;
package cytoscape.visual.ui;
//------------------------------------------------------------------------
import java.io.*;
import java.awt.Color;
import java.awt.Polygon;
import java.util.*;
import java.net.URL;

import javax.swing.*;

import y.view.Arrow;
import y.view.LineType;
import y.view.ShapeNodeRealizer;

import cytoscape.util.Misc;
import cytoscape.vizmap.*;
import cytoscape.util.MutableString;

//------------------------------------------------------------------------
public class MapperAndAttribute {
    Object mapper;
    MutableString attribute;

    public MapperAndAttribute() {
    }

    public MapperAndAttribute(Object newMapper, MutableString newAttribute) {
	this.mapper = newMapper;
	this.attribute = newAttribute;
    }

}
