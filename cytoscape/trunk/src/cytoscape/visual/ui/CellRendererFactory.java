package cytoscape.visual.ui;

import giny.view.ObjectPosition;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.ui.editors.discrete.CustomGraphicsCellRenderer;
import cytoscape.visual.ui.editors.discrete.CyColorCellRenderer;
import cytoscape.visual.ui.editors.discrete.FontCellRenderer;
import cytoscape.visual.ui.editors.discrete.ObjectPositionCellRenderer;
import cytoscape.visual.ui.editors.discrete.ShapeCellRenderer;

public class CellRendererFactory {
	
	private final Map<Class<?>, TableCellRenderer> rendererMap;
	
	protected final static TableCellRenderer DEF_RENDERER = new DefaultTableCellRenderer();
	
	CellRendererFactory() {
		rendererMap = new HashMap<Class<?>, TableCellRenderer>();
		
		registerDefaultEditors();
	}
	
	private void registerDefaultEditors() {
		rendererMap.put(Color.class, new CyColorCellRenderer());
		rendererMap.put(NodeShape.class, new ShapeCellRenderer(VisualPropertyType.NODE_SHAPE));
		rendererMap.put(LineStyle.class, new ShapeCellRenderer(VisualPropertyType.EDGE_LINE_STYLE));
		rendererMap.put(ArrowShape.class, new ShapeCellRenderer(VisualPropertyType.EDGE_TGTARROW_SHAPE));
		rendererMap.put(Font.class, new FontCellRenderer());
		rendererMap.put(ObjectPosition.class, new ObjectPositionCellRenderer());
		rendererMap.put(CyCustomGraphics.class, new CustomGraphicsCellRenderer());
		rendererMap.put(String.class, DEF_RENDERER);
		rendererMap.put(Number.class, DEF_RENDERER);
		
	}
	
	protected TableCellRenderer getCellRenderer(final Class<?> type) {
		final TableCellRenderer rend = this.rendererMap.get(type);
		if(rend == null)
			return DEF_RENDERER;
		else
			return rend; 
	}
	
	protected void register(final Class<?> type, final TableCellRenderer renderer) {
		this.rendererMap.put(type, renderer);
	}
}
