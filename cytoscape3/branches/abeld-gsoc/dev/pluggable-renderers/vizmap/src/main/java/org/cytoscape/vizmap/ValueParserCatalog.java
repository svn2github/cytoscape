package org.cytoscape.vizmap;

import java.awt.Color;
import java.awt.Font;

import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.renderers.NodeRenderer;
import org.cytoscape.vizmap.parsers.ColorParser;
import org.cytoscape.vizmap.parsers.DoubleParser;
import org.cytoscape.vizmap.parsers.FontParser;
import org.cytoscape.vizmap.parsers.LabelPositionParser;
import org.cytoscape.vizmap.parsers.NodeRendererParser;
import org.cytoscape.vizmap.parsers.StringParser;

public abstract class ValueParserCatalog {
	public static ValueParser getValueParser(VisualProperty vp){
		Class c = vp.getDataType();
		if (c.isAssignableFrom(Color.class)){
			return new ColorParser();
		} else if (c.isAssignableFrom(Number.class)){
			return new DoubleParser(); // FIXME: or FloatParser? (which to use and when?)
		} else if (c.isAssignableFrom(Font.class)){
			return new FontParser();
		} else if (c.isAssignableFrom(LabelPosition.class)){
			return new LabelPositionParser();
		} else if (c.isAssignableFrom(NodeRenderer.class)){
			return new NodeRendererParser();
		} else if (c.isAssignableFrom(String.class)){
			return new StringParser();
		} else {
			System.out.println("unknown DataType!");
			return null;
		}
	}
}
