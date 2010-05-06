package cytoscape.visual.ui;

import giny.view.ObjectPosition;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.ui.editors.discrete.CyColorPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyComboBoxPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyCustomGraphicsEditor;
import cytoscape.visual.ui.editors.discrete.CyDoublePropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyFontPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyObjectPositionPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyStringPropertyEditor;
import cytoscape.visual.ui.icon.ArrowIcon;
import cytoscape.visual.ui.icon.NodeIcon;
import cytoscape.visual.ui.icon.VisualPropertyIcon;

public class CellEditorFactory {

	private final Map<Object, Icon> nodeShapeIcons = NodeShape.getIconSet();
	private final Map<Object, Icon> arrowShapeIcons = ArrowShape.getIconSet();
	private final Map<Object, Icon> lineTypeIcons = LineStyle.getIconSet();

	private final Map<Class<?>, PropertyEditor> editorMap;
	private final PropertyChangeListener pcl;

	CellEditorFactory(final PropertyChangeListener pcl) {
		this.editorMap = new HashMap<Class<?>, PropertyEditor>();
		this.pcl = pcl;
		registerDefaultEditors();
		addListener();
	}

	/**
	 * Register editord supported by default.
	 */
	private void registerDefaultEditors() {
		this.editorMap.put(String.class, new CyStringPropertyEditor());
		this.editorMap.put(Color.class, new CyColorPropertyEditor());
		this.editorMap.put(Font.class, new CyFontPropertyEditor());
		this.editorMap.put(Number.class, new CyDoublePropertyEditor());
		this.editorMap.put(ObjectPosition.class,
				new CyObjectPositionPropertyEditor(
						VisualPropertyType.NODE_LABEL_POSITION));
		this.editorMap.put(CyCustomGraphics.class, new CyCustomGraphicsEditor());

		// Register combo-box type editors. They needs special customization.
		initializeShapeSelectors();
	}

	private void initializeShapeSelectors() {
		VisualPropertyIcon newIcon;
		final CyComboBoxPropertyEditor shapeCellEditor = new CyComboBoxPropertyEditor();

		List<Icon> iconList = new ArrayList<Icon>();
		final List<NodeShape> nodeShapes = new ArrayList<NodeShape>();

		for (Object key : nodeShapeIcons.keySet()) {
			NodeShape shape = (NodeShape) key;

			if (shape.isSupported()) {
				iconList.add(nodeShapeIcons.get(key));
				nodeShapes.add(shape);
			}
		}

		Icon[] iconArray = new Icon[iconList.size()];
		String[] shapeNames = new String[iconList.size()];

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = ((NodeIcon) iconList.get(i)).clone();
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(16);
			iconArray[i] = newIcon;
			shapeNames[i] = nodeShapes.get(i).getShapeName();
		}

		shapeCellEditor.setAvailableValues(nodeShapes.toArray());
		shapeCellEditor.setAvailableIcons(iconArray);

		iconList.clear();
		iconList.addAll(arrowShapeIcons.values());
		iconArray = new Icon[iconList.size()];

		String[] arrowNames = new String[iconList.size()];
		Set<Object> arrowShapes = arrowShapeIcons.keySet();

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = ((ArrowIcon) iconList.get(i));
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(40);
			newIcon.setBottomPadding(-9);
			iconArray[i] = newIcon;
			arrowNames[i] = newIcon.getName();
		}

		final CyComboBoxPropertyEditor arrowCellEditor = new CyComboBoxPropertyEditor();
		arrowCellEditor.setAvailableValues(arrowShapes.toArray());
		arrowCellEditor.setAvailableIcons(iconArray);

		iconList = new ArrayList<Icon>();
		iconList.addAll(lineTypeIcons.values());
		iconArray = new Icon[iconList.size()];
		shapeNames = new String[iconList.size()];

		Set<Object> lineTypes = lineTypeIcons.keySet();

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = (VisualPropertyIcon) (iconList.get(i));
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(16);
			iconArray[i] = newIcon;
			shapeNames[i] = newIcon.getName();
		}

		final CyComboBoxPropertyEditor lineCellEditor = new CyComboBoxPropertyEditor();
		lineCellEditor.setAvailableValues(lineTypes.toArray());
		lineCellEditor.setAvailableIcons(iconArray);
		
		// Boolean editor
		final CyComboBoxPropertyEditor booleanCellEditor = new CyComboBoxPropertyEditor();
		booleanCellEditor.setAvailableValues(new Boolean[] {true, false});

		// Register editors
		this.editorMap.put(NodeShape.class, shapeCellEditor);
		this.editorMap.put(ArrowShape.class, arrowCellEditor);
		this.editorMap.put(LineStyle.class, lineCellEditor);
		this.editorMap.put(Boolean.class, booleanCellEditor);
	}

	private void addListener() {
		for (final PropertyEditor editor : editorMap.values())
			editor.addPropertyChangeListener(pcl);
	}

	public PropertyEditor getPropertyEditor(final Class<?> type) {
		final PropertyEditor editor = this.editorMap.get(type);
		if (editor == null) {
			System.out.println("Could not find editor for >>>>>>>>>>>>>>>> "
					+ type);
			return null;
		} else
			return editor;
	}
}
