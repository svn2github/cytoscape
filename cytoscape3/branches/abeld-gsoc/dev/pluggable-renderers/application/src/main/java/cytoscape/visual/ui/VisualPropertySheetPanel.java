package cytoscape.visual.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.cytoscape.GraphObject;
import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesUtils;
import org.cytoscape.view.DiscreteValue;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.VisualPropertyCatalog;
import org.cytoscape.view.VisualPropertyIcon;
import org.cytoscape.vizmap.CalculatorCatalog;
import org.cytoscape.vizmap.LabelPosition;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.vizmap.calculators.BasicCalculator;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.mappings.ContinuousMapping;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;
import org.cytoscape.vizmap.mappings.PassThroughMapping;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.NetworkPanel;
import cytoscape.visual.ui.BrightnessListener;
import cytoscape.visual.ui.GenerateSeriesListener;
import cytoscape.visual.ui.GenerateValueListener;
import cytoscape.visual.ui.MultiHashMapListenerAdapter;
import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel;
import cytoscape.visual.ui.editors.discrete.CyColorCellRenderer;
import cytoscape.visual.ui.editors.discrete.CyColorPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyComboBoxPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyDoublePropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyFontPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyLabelPositionPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyStringPropertyEditor;
import cytoscape.visual.ui.editors.discrete.FontCellRenderer;
import cytoscape.visual.ui.editors.discrete.LabelPositionCellRenderer;
import cytoscape.visual.ui.editors.discrete.ShapeCellRenderer;

public class VisualPropertySheetPanel implements PropertyChangeListener, PopupMenuListener {

	private static final long serialVersionUID = 1L;
	private VizMapperMainPanel vmmp;
	private PropertySheetPanel propertySheetPanel;
	private PropertyRendererRegistry rendReg = new PropertyRendererRegistry();
	private PropertyEditorRegistry editorReg = new PropertyEditorRegistry();
	private Map<VisualProperty, JDialog> editorWindowManager = new HashMap<VisualProperty, JDialog>();
	// Keeps current discrete mappings:
	private final Map<String, Map<Object, Object>> discMapBuffer = new HashMap<String, Map<Object, Object>>();

	private static JPopupMenu menu;
	private static JMenuItem delete; // FIXME: if these menuItems are only used in initWidgets() method, move them there
	private static JMenuItem rainbow1;
	private static JMenuItem rainbow2;
	private static JMenuItem randomize;
	private static JMenuItem series;
	private static JMenuItem editAll;
	private static JMenu generateValues;
	private static JMenu modifyValues;
	private static JMenuItem brighter;
	private static JMenuItem darker;

	/*
	 * Renderer and Editors for the cells
	 */

	// For general values (string & number)
	private DefaultTableCellRenderer defCellRenderer = new DefaultTableCellRenderer();

	// For String values
	private CyStringPropertyEditor stringCellEditor = new CyStringPropertyEditor();

	// For colors
	private CyColorCellRenderer colorCellRenderer = new CyColorCellRenderer();
	private CyColorPropertyEditor colorCellEditor = new CyColorPropertyEditor();
	
	// For sizes
	private CyDoublePropertyEditor numberCellEditor;

	// For font faces
	private CyFontPropertyEditor fontCellEditor = new CyFontPropertyEditor();
	private FontCellRenderer fontCellRenderer = new FontCellRenderer();

	// For label positions
	private LabelPositionCellRenderer labelPositionRenderer = new LabelPositionCellRenderer();
	private CyLabelPositionPropertyEditor labelPositionEditor = new CyLabelPositionPropertyEditor();

	// Others
	private DefaultTableCellRenderer emptyBoxRenderer = new DefaultTableCellRenderer();
	private DefaultTableCellRenderer filledBoxRenderer = new DefaultTableCellRenderer();
	private DefaultTableCellRenderer continuousRenderer = new DefaultTableCellRenderer();
	private DefaultTableCellRenderer discreteRenderer = new DefaultTableCellRenderer();

	/*
	 * Controlling attr selector
	 */
	private CyComboBoxPropertyEditor nodeAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor edgeAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor nodeNumericalAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor edgeNumericalAttrEditor = new CyComboBoxPropertyEditor();

	// For mapping types.
	private CyComboBoxPropertyEditor mappingTypeEditor = new CyComboBoxPropertyEditor();

	/** store the Table state for each VisualStyle in this map, by storing the TableModel*/
	private Map<VisualStyle, TableModel> vsToModelMap;

	private static final String CATEGORY_UNUSED = "Unused Properties";
	private static final String GRAPHICAL_MAP_VIEW = "Graphical View";
	private static final String NODE_VISUAL_MAPPING = "Node Visual Mapping";
	private static final String EDGE_VISUAL_MAPPING = "Edge Visual Mapping";
	
	public VisualPropertySheetPanel(VizMapperMainPanel panel){
		this.vmmp = panel;
		vsToModelMap = new HashMap<VisualStyle, TableModel>();
		initWidgets();
		adaptToVisualStyleChanged();
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);

		// This can't be here because it would create an inifinite loop since this constructor gets called
		// when there is no CytoscapeDesktop object yet, and Cytoscape.getDesktop() would cann that constr
		// again, which would call this constr. etc.
		// as a workaround, hook up this listener in Cytoscape.getDesktop() 
		//Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 *            DOCUMENT ME!
	 */
	public void popupMenuCanceled(PopupMenuEvent arg0) {
		disableAllPopup();
	}

	private void disableAllPopup() {
		rainbow1.setEnabled(false);
		rainbow2.setEnabled(false);
		randomize.setEnabled(false);
		series.setEnabled(false);
		brighter.setEnabled(false);
		darker.setEnabled(false);
		delete.setEnabled(false);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	/**
	 * Check the selected VPT and enable/disable menu items.
	 *
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		//System.out.println("popupMenuWillBecomeVisible"+e);
		final VizMapperProperty prop = getSelectedProperty();

		if (prop == null)
			return;

		if (prop.getHiddenObject() instanceof VisualProperty
		    && (prop.getDisplayName().contains("Mapping Type") == false)
		    && (prop.getValue() != null)
		    && (prop.getValue().toString().startsWith("Please select") == false)) {
			// Enble delete menu
			delete.setEnabled(true);

			Property[] children = prop.getSubProperties();

			for (Property p : children) {
				if ((p.getDisplayName() != null) && p.getDisplayName().contains("Mapping Type")) {
					if ((p.getValue() == null)
					    || (p.getValue().equals("Discrete Mapping") == false)) {
						return;
					}
				}
			}
			VisualProperty type = ((VisualProperty) prop.getHiddenObject());
			Class dataType = type.getDataType();

			if (dataType == Color.class) {
				rainbow1.setEnabled(true);
				rainbow2.setEnabled(true);
				randomize.setEnabled(true);
				brighter.setEnabled(true);
				darker.setEnabled(true);
			} else if (dataType == Number.class) {
				randomize.setEnabled(true);
				series.setEnabled(true);
			}
		}
		return;
	}

	public void propertyChange(PropertyChangeEvent e) {
		/*
		 * Managing editor windows.
		 */
		try {
		//System.out.println("VPSP got event:"+e);
		if (e.getPropertyName().equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_OPENED)
		    || e.getPropertyName().equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED)) {
			manageWindow(e.getPropertyName(), (VisualProperty) e.getNewValue(), e.getSource());

			if (e.getPropertyName().equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED))
				editorWindowManager.remove((VisualProperty) e.getNewValue());

		} else if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			adaptToVisualStyleChanged();  // FIXME: is this needed? why?
		} else if (e.getPropertyName().equals(Cytoscape.SESSION_LOADED)
		           || e.getPropertyName().equals(Cytoscape.VIZMAP_LOADED)) {
			adaptToVisualStyleChanged(); // FIXME: is this needed?
		} else if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUS)
		           && (e.getSource().getClass() == NetworkPanel.class)) { //FIXME: why do we have to check source?
			adaptToVisualStyleChanged(); // FIXME: is this needed?
			setPropertyTable();
		} else if (e.getPropertyName().equals(Cytoscape.VISUALSTYLE_MODIFIED)){
			//System.out.println("got VISUALSTYLE_MODIFIED!");
		} else if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)
		           || e.getPropertyName().equals(Cytoscape.NETWORK_LOADED)) {
			setAttrComboBox();
		}

		/***********************************************************************
		 * Below this line, accept only cell editor events.
		 **********************************************************************/
		//System.out.println("cell editor event.");
		if (e.getPropertyName().equalsIgnoreCase("value") == false)	return;

		if (e.getNewValue().equals(e.getOldValue())) return;

		VizMapperProperty prop = getSelectedProperty(); 
		if (prop == null) return;

		VisualProperty type = null;
		VizMapperProperty typeRootProp = null;
		if ((prop.getParentProperty() == null) && e.getNewValue() instanceof String) {
			//System.out.println("This is a controlling attr name change signal.");
			/*
			 * This is a controlling attr name change signal.
			 */
			//System.out.println("This is a controlling attr name change signal.");
			typeRootProp = (VizMapperProperty) prop;
			type = (VisualProperty) ((VizMapperProperty) prop).getHiddenObject();
		} else if ((prop.getParentProperty() == null) && (e.getNewValue() == null)) {
			/*
			 * Empty cell selected. no need to change anything.
			 */
			//System.out.println("Empty cell selected. no need to change anything.");
			return;
		} else {
			typeRootProp = (VizMapperProperty) prop.getParentProperty();

			if (prop.getParentProperty() == null) return;

			type = (VisualProperty) ((VizMapperProperty) prop.getParentProperty())
			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             .getHiddenObject();
		}

		/*
		 * Mapping type changed
		 */
		if (prop.getHiddenObject() instanceof ObjectMapping || prop.getDisplayName().equals("Mapping Type")) {
			//System.out.println("Mapping type changed: " + prop.getHiddenObject());

			final Object parentValue = prop.getParentProperty().getValue();
			if (parentValue == null) return;

			String ctrAttrName = parentValue.toString();
			final Class dataClass;

			if (type.isNodeProp()) {
				dataClass = CyAttributesUtils.getClass(ctrAttrName, Cytoscape.getNodeAttributes());
			} else {
				dataClass = CyAttributesUtils.getClass(ctrAttrName, Cytoscape.getEdgeAttributes());
			}

			if (e.getNewValue() == null) return;
			if (e.getNewValue().equals("Continuous Mapper")
					&& ((dataClass != Integer.class) && (dataClass != Double.class) && (dataClass != Float.class))) {
				JOptionPane.showMessageDialog(propertySheetPanel,
						"Continuous Mapper can be used with Numbers only.",
						"Incompatible Mapping Type!",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			if (e.getNewValue().toString().endsWith("Mapper")){
				switchMapping(prop, e.getNewValue().toString(), prop.getParentProperty().getValue());
				// restore expanded props:
				expandLastSelectedItem(type.getName());
				updateTableView();
			}

			return;
			
		}

		/*
		 * Extract calculator
		 */
		final Calculator curCalc = vmmp.getCurrentlyEditedVS().getCalculator(type);
		if (curCalc == null) return;

		ObjectMapping mapping = curCalc.getMapping(0);

		/*
		 * Controlling Attribute has been changed.
		 */
		if ((prop.getParentProperty() == null) && (e.getNewValue() instanceof String)) {
			String ctrAttrName = (String) e.getNewValue();

			// If same, do nothing.
			if (ctrAttrName.equals(mapping.getControllingAttributeName()))
				return;

			// This part is for Continuous Mapping.
			if (mapping instanceof ContinuousMapping) {
				final Byte dataType;
				if (type.isNodeProp()) {
					dataType = Cytoscape.getNodeAttributes().getType(ctrAttrName);
				} else {
					dataType = Cytoscape.getEdgeAttributes().getType(ctrAttrName);
				}
				
				if ((dataType == CyAttributes.TYPE_FLOATING) || (dataType == CyAttributes.TYPE_INTEGER)) {
					// Do nothing
				} else {
					JOptionPane.showMessageDialog(propertySheetPanel,
					                              "Continuous Mapper can be used with Numbers only.\nPlease select numerical attributes.",
					                              "Incompatible Mapping Type!",
					                              JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			}
			GraphView currentView = vmmp.getCurrentView();
			// Buffer current discrete mapping
			if (mapping instanceof DiscreteMapping) {
				final String curMappingName = curCalc.toString() + "-" + mapping.getControllingAttributeName();
				final String newMappingName = curCalc.toString() + "-" + ctrAttrName;
				final Map saved = discMapBuffer.get(newMappingName);

				if (saved == null) {
					discMapBuffer.put(curMappingName, ((DiscreteMapping) mapping).getAll());
					mapping.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);
				} else if (saved != null) {
					// Mapping exists
					discMapBuffer.put(curMappingName, ((DiscreteMapping) mapping).getAll());
					mapping.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);
					((DiscreteMapping) mapping).putAll(saved);
				}
			} else {
				mapping.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);
			}

			propertySheetPanel.removeProperty(typeRootProp);

			final VizMapperProperty newRootProp = new VizMapperProperty();
			
			buildProperty(vmmp.getCurrentlyEditedVS().getCalculator(type), newRootProp);
			
			expandLastSelectedItem(type.getName());
			updateTableView();

			// Finally, update graph view and focus.
			if (currentView != null) Cytoscape.redrawGraph(currentView);

			return;
		}

		// Return if not a Discrete Mapping.
		if (mapping instanceof ContinuousMapping || mapping instanceof PassThroughMapping) return;

		Object key = null;

		if ((type.getDataType() == Number.class) || (type.getDataType() == String.class)) {
			key = e.getOldValue();

			if (type.getDataType() == Number.class) {
				numberCellEditor = new CyDoublePropertyEditor(this);
				numberCellEditor.addPropertyChangeListener(this);
				editorReg.registerEditor(prop, numberCellEditor);
			}
		} else {
			key = getSelectedProperty().getDisplayName();
		}

		/*
		 * Need to convert this string to proper data types.
		 */
		String ctrAttrName = mapping.getControllingAttributeName();
		Byte attrType; 
		if (type.isNodeProp()) {
			attrType = Cytoscape.getNodeAttributes().getType(ctrAttrName);
		} else {
			attrType = Cytoscape.getEdgeAttributes().getType(ctrAttrName);
		}

		if (attrType != CyAttributes.TYPE_STRING) {
			switch (attrType) {
				case CyAttributes.TYPE_BOOLEAN:
					key = Boolean.valueOf((String) key);
					break;
				case CyAttributes.TYPE_INTEGER:
					key = Integer.valueOf((String) key);
					break;
				case CyAttributes.TYPE_FLOATING:
					key = Double.valueOf((String) key);
					break;
				default:
					break;
			}
		}

		Object newValue = e.getNewValue();

		if (type.getDataType() == Number.class) {
			if ((((Number) newValue).doubleValue() == 0)
			    || (newValue instanceof Number && type.toString().endsWith("OPACITY")
			       && (((Number) newValue).doubleValue() > 255))) {
				PropertySheetTable table = propertySheetPanel.getTable();
				for (int i = 0; i < table.getRowCount(); i++) {
					Property p = ((Item) table.getValueAt(i, 0)).getProperty();

					if (p != null) {
						Object val = p.getDisplayName();

						if ((val != null) && val.equals(key.toString())) {
							p.setValue(((DiscreteMapping) mapping).getMapValue(key));
							return;
						}
					}
				}
				return;
			}
		}

		((DiscreteMapping) mapping).putMapValue(key, newValue);

		/*
		 * Update table and current network view.
		 */
		updateTableView();

		propertySheetPanel.repaint();
		} catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
	public PropertySheetTable getTable(){
		return propertySheetPanel.getTable();
	}
	public PropertySheetPanel getPSP(){
		return propertySheetPanel;
	}
	public VizMapperMainPanel getVMMP(){
		return vmmp;
	}
	/** Return VizMapperProperty of selected row. */
	public VizMapperProperty getSelectedProperty(){
		final int selectedRow = getTable().getSelectedRow();

		if (selectedRow < 0)
			return null;

		final Item item = (Item) getTable().getValueAt(selectedRow, 0);
		return (VizMapperProperty) item.getProperty();
	}
		
	private void initWidgets(){
		/* custom PropertySheetTable needed so that we can customize tooltip */
		PropertySheetTable table = new PropertySheetTable() {
			private final static long serialVersionUID = 1213748836812161L;
			public String getToolTipText(MouseEvent me) {
				final Point pt = me.getPoint();
				final int row = rowAtPoint(pt);

				if (row < 0)
					return null;
				else {
					final Property prop = ((Item) getValueAt(row, 0)).getProperty();
					if (prop == null) return null;

					final Color fontColor;
					if ((prop.getValue() != null) && (prop.getValue().getClass() == Color.class))
						fontColor = (Color) prop.getValue();
					else
						fontColor = Color.DARK_GRAY;
					final String colorString = Integer.toHexString(fontColor.getRGB());

					if (prop.getDisplayName().equals(GRAPHICAL_MAP_VIEW))
						return "Click to edit this mapping...";

					if ((prop.getDisplayName() == "Controlling Attribute") || (prop.getDisplayName() == "Mapping Type"))
						return "<html><Body BgColor=\"white\"><font Size=\"4\" Color=\"#"
					           + colorString.substring(2, 8) + "\"><strong>"
							   + prop.getDisplayName() + " = " + prop.getValue()
							   + "</font></strong></body></html>";
					else if ((prop.getSubProperties() == null) || (prop.getSubProperties().length == 0))
						return "<html><Body BgColor=\"white\"><font Size=\"4\" Color=\"#"
						    	+ colorString.substring(2, 8) + "\"><strong>"
							    + prop.getDisplayName() + "</font></strong></body></html>";
					return null;
				}
			}
		};

		propertySheetPanel = new PropertySheetPanel(table);
		propertySheetPanel.setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);
		propertySheetPanel.setRendererFactory(rendReg);
		propertySheetPanel.setEditorFactory(editorReg);
		table = propertySheetPanel.getTable();

		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			public void columnAdded(TableColumnModelEvent arg0) {
				// TODO Auto-generated method stub
			}

			public void columnMarginChanged(ChangeEvent e) {
				updateTableView();
			}

			public void columnMoved(TableColumnModelEvent e) {
				// TODO Auto-generated method stub
			}

			public void columnRemoved(TableColumnModelEvent e) {
				// TODO Auto-generated method stub
			}

			public void columnSelectionChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		menu = new JPopupMenu();
		table.setComponentPopupMenu(menu);
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				processMouseClick(e);
			}
		});

		table.setRowHeight(25);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCategoryBackground(new Color(10, 10, 50, 20));
		table.setCategoryForeground(Color.black);
		table.setSelectionBackground(Color.white);
		table.setSelectionForeground(Color.blue);

		generateValues = new JMenu("Generate Discrete Values");
		generateValues.setIcon(new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_filters-16.png")));
		modifyValues = new JMenu("Modify Discrete Values");
		delete = new JMenuItem("Delete mapping");

		final Font italicMenu = new Font("SansSerif", Font.ITALIC, 14);
		rainbow1 = new JMenuItem("Rainbow 1");
		rainbow2 = new JMenuItem("Rainbow 2 (w/modulations)");
		randomize = new JMenuItem("Randomize");
		rainbow1.setFont(italicMenu);
		rainbow2.setFont(italicMenu);

		series = new JMenuItem("Series (Number Only)");
		brighter = new JMenuItem("Brighter");
		darker = new JMenuItem("Darker");
		editAll = new JMenuItem("Edit selected values at once...");

		delete.setIcon(new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_delete-16.png")));
		editAll.setIcon(new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_edit-16.png")));
		rainbow1.addActionListener(new GenerateValueListener(this, GenerateValueListener.RAINBOW1));
		rainbow2.addActionListener(new GenerateValueListener(this, GenerateValueListener.RAINBOW2));
		randomize.addActionListener(new GenerateValueListener(this, GenerateValueListener.RANDOM));
		series.addActionListener(new GenerateSeriesListener(this));
		brighter.addActionListener(new BrightnessListener(this, BrightnessListener.BRIGHTER));
		darker.addActionListener(new BrightnessListener(this, BrightnessListener.DARKER));

		delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removeMapping();
				}
			});
		editAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						editSelectedCells();
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		
		generateValues.add(rainbow1);
		generateValues.add(rainbow2);
		generateValues.add(randomize);
		generateValues.add(series);

		modifyValues.add(brighter);
		modifyValues.add(darker);

		rainbow1.setEnabled(false);
		rainbow2.setEnabled(false);
		randomize.setEnabled(false);
		series.setEnabled(false);

		brighter.setEnabled(false);
		darker.setEnabled(false);

		menu.add(delete);
		menu.add(new JSeparator());
		menu.add(generateValues);
		menu.add(modifyValues);
		menu.add(editAll);
		menu.add(new JSeparator());

		delete.setEnabled(false);
		menu.addPopupMenuListener(this);
		
		/*
		 * Set editors
		 */
		colorCellRenderer.setForeground(Color.DARK_GRAY);
		colorCellRenderer.setOddBackgroundColor(new Color(150, 150, 150, 20));
		colorCellRenderer.setEvenBackgroundColor(Color.white);

		emptyBoxRenderer.setHorizontalTextPosition(SwingConstants.CENTER);
		emptyBoxRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		emptyBoxRenderer.setBackground(new Color(0, 200, 255, 20));
		emptyBoxRenderer.setForeground(Color.red);
		emptyBoxRenderer.setFont(new Font("SansSerif", Font.BOLD, 12));

		filledBoxRenderer.setBackground(Color.white);
		filledBoxRenderer.setForeground(Color.blue);
		
		numberCellEditor = new CyDoublePropertyEditor(this);
		
		nodeAttrEditor.addPropertyChangeListener(this);
		edgeAttrEditor.addPropertyChangeListener(this);
		mappingTypeEditor.addPropertyChangeListener(this);
		colorCellEditor.addPropertyChangeListener(this);
		fontCellEditor.addPropertyChangeListener(this);
		numberCellEditor.addPropertyChangeListener(this);
		stringCellEditor.addPropertyChangeListener(this);
		labelPositionEditor.addPropertyChangeListener(this);
		
		Cytoscape.getNodeAttributes().getMultiHashMap().addDataListener(new MultiHashMapListenerAdapter(propertySheetPanel,
																		Cytoscape.getNodeAttributes(),
																		nodeAttrEditor, nodeNumericalAttrEditor));
		Cytoscape.getEdgeAttributes().getMultiHashMap().addDataListener(new MultiHashMapListenerAdapter(propertySheetPanel,
																		Cytoscape.getEdgeAttributes(),
																		edgeAttrEditor, edgeNumericalAttrEditor));
		Cytoscape.getNetworkAttributes().getMultiHashMap().addDataListener(new MultiHashMapListenerAdapter(propertySheetPanel,
																		Cytoscape.getNetworkAttributes(),
																		null, null));
	}
	
	/** set given TableModel as model for PropertySheetPanel widget;
	 * model _must_ be a PropertySheetTableModel since it is cast to one somwhere.
	 */
	private void setModel(TableModel model){
		propertySheetPanel.getTable().setModel(model);
		
		// workaround the fact that PropertySheetPanel caches the TableModel and thus would ignore the model change.
		propertySheetPanel.setTable(propertySheetPanel.getTable());
		// set other stuff that PropertySheetPanel proxies to the model:
		propertySheetPanel.setSorting(true);
		propertySheetPanel.setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);
	}

	private void setAttrComboBox() {
		Class dataClass;
		final List<String> names = new ArrayList<String>();
		
		CyAttributes attr = Cytoscape.getNodeAttributes();
		String[] nameArray = attr.getAttributeNames();
		Arrays.sort(nameArray);
		names.add("ID");
		for (String name : nameArray) {
			if (attr.getUserVisible(name) && (attr.getType(name) != CyAttributes.TYPE_UNDEFINED)
			    && (attr.getType(name) != CyAttributes.TYPE_COMPLEX)) {
				names.add(name);
			}
		}
		nodeAttrEditor.setAvailableValues(names.toArray());

		names.clear();
		for (String name : nameArray) {
			dataClass = CyAttributesUtils.getClass(name, attr);

			if ((dataClass == Integer.class) || (dataClass == Double.class)
			    || (dataClass == Float.class))
				names.add(name);
		}
		nodeNumericalAttrEditor.setAvailableValues(names.toArray());

		names.clear();
		attr = Cytoscape.getEdgeAttributes();
		nameArray = attr.getAttributeNames();
		Arrays.sort(nameArray);
		names.add("ID");

		for (String name : nameArray) {
			if (attr.getUserVisible(name) && (attr.getType(name) != CyAttributes.TYPE_UNDEFINED)
			    && (attr.getType(name) != CyAttributes.TYPE_COMPLEX)) {
				names.add(name);
			}
		}
		edgeAttrEditor.setAvailableValues(names.toArray());

		names.clear();
		for (String name : nameArray) {
			dataClass = CyAttributesUtils.getClass(name, attr);
			if ((dataClass == Integer.class) || (dataClass == Double.class)
			    || (dataClass == Float.class))
				names.add(name);
		}
		edgeNumericalAttrEditor.setAvailableValues(names.toArray());
	}

	
	public void adaptToVisualStyleChanged(){
		VisualStyle currentlyEditedVS = vmmp.getCurrentlyEditedVS();
		if (vsToModelMap.containsKey(currentlyEditedVS)){
			//System.out.println("swapping in exsisting TableModel:");
			setModel(vsToModelMap.get(currentlyEditedVS));
		} else {
			//System.out.println("no TableModel yet for this VisualStyle, have to create it:");
			PropertySheetTableModel model = new PropertySheetTableModel();
			setModel(model);
			setPropertyTable();
			vsToModelMap.put(currentlyEditedVS, model);
		}
		
		// Close editor windows. was in closeEditorWindow() method 
		for (VisualProperty vpt : editorWindowManager.keySet()) {
			JDialog window = editorWindowManager.get(vpt);
			manageWindow(ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED, vpt, null);
			window.dispose();
			editorWindowManager.remove(vpt);
		}
		setAttrComboBox();
		mappingTypeEditor.setAvailableValues(Cytoscape.getVisualMappingManager().getCalculatorCatalog().getMappingNames().toArray());
	}
	
	/** remove the widgets cached for visualStyle */
	public void removeWidgetsFor(VisualStyle visualStyle){
		vsToModelMap.remove(visualStyle);
	}
	
	private void processMouseClick(MouseEvent e) {
		//System.out.println("processMouseClick");
		int selectedRow = propertySheetPanel.getTable().getSelectedRow();
		/*
		 * Adjust height if it's an legend icon.
		 */
		updateTableView();

		if (SwingUtilities.isLeftMouseButton(e) && (0 <= selectedRow)) {
			//System.out.println("handling selection");

			final Item item = (Item) propertySheetPanel.getTable().getValueAt(selectedRow, 0);
			final Property curProp = item.getProperty();

			if (curProp == null)
				return;

			/*
			 * Create new mapping if double-click on unused val.
			 */
			String category = curProp.getCategory();

			if ((e.getClickCount() == 2) && (category != null) && category.equalsIgnoreCase("Unused Properties")) {
				((VizMapperProperty) curProp).setEditable(true); // FIXME: is this needed if it is going to be removed anyway?

				VisualProperty vp = (VisualProperty) ((VizMapperProperty) curProp).getHiddenObject();
				propertySheetPanel.removeProperty(curProp);

				final VizMapperProperty newProp = new VizMapperProperty();
				final VizMapperProperty mapProp = new VizMapperProperty();

				newProp.setDisplayName(vp.getName());
				newProp.setHiddenObject(vp);
				newProp.setValue("Please select a value!");
				if (vp.isNodeProp()) {newProp.setCategory(NODE_VISUAL_MAPPING);
				} else { newProp.setCategory(EDGE_VISUAL_MAPPING); }
				
				addSubProperty(newProp, mapProp, "Mapping Type", null, "Please select a mapping type!");
				propertySheetPanel.addProperty(newProp);

				if (vp.isNodeProp()) {
					editorReg.registerEditor(newProp, nodeAttrEditor);
				} else {
					editorReg.registerEditor(newProp, edgeAttrEditor);
				}

				editorReg.registerEditor(mapProp, mappingTypeEditor);

				expandLastSelectedItem(vp.getName());

				propertySheetPanel.getTable().scrollRectToVisible(new Rectangle(0, 0, 10, 10));
			} else if ((e.getClickCount() == 1) && (category == null)) {
				/*
				 * Single left-click
				 */
				VisualProperty type = null;

				if ((curProp.getParentProperty() == null) && ((VizMapperProperty) curProp).getHiddenObject() instanceof VisualProperty)
					type = (VisualProperty) ((VizMapperProperty) curProp).getHiddenObject();
				else if (curProp.getParentProperty() != null)
					type = (VisualProperty) ((VizMapperProperty) curProp.getParentProperty()).getHiddenObject();
				else
					return;

				Calculator calc = vmmp.getCurrentlyEditedVS().getCalculator(type);

				if ((calc != null) && calc.getMapping(0) instanceof ContinuousMapping) {
					/*
					 * Need to check other windows.
					 */
					if (editorWindowManager.containsKey(type)) {
						// This means editor is already on display.
						editorWindowManager.get(type).requestFocus();

					} else {
						try {
							((JDialog) EditorFactory.showContinuousEditor(type)).addPropertyChangeListener(this);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void manageWindow(final String status, VisualProperty vpt, Object source) {
		if (status.equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_OPENED)) {
			this.editorWindowManager.put(vpt, (JDialog) source);
		} else if (status.equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED)) {
			/*
			 * Update icon
			 */
			final Property[] props = propertySheetPanel.getProperties();
			VizMapperProperty vprop = null;

			for (Property prop : props) {
				vprop = (VizMapperProperty) prop;

				if ((vprop.getHiddenObject() != null) && (vpt == vprop.getHiddenObject())) {
					vprop = (VizMapperProperty) prop;

					break;
				}
			}

			final Property[] subProps = vprop.getSubProperties();
			vprop = null;

			String name = null;

			for (Property prop : subProps) {
				name = prop.getName();
				if ((name != null) && name.equals(vpt.getName())) {
					vprop = (VizMapperProperty) prop;
					break;
				}
			}

			final DefaultTableCellRenderer cRenderer = new DefaultTableCellRenderer();
			final int width = getTable().getCellRect(0, 1, true).width;
			cRenderer.setIcon(ContinuousMappingEditorPanel.getIcon(width, 70, vpt));
			rendReg.registerRenderer(vprop, cRenderer);
		}
	}
	
	/**
	 * Edit all selected cells at once.
	 *
	 * This is for Discrete Mapping only.
	 *
	 */
	private void editSelectedCells() throws Exception {
		final PropertySheetTable table = getTable();
		final int[] selected = table.getSelectedRows();

		// If nothing selected, return.
		if ((selected == null) || (selected.length == 0)) return;

		// Test with the first selected item
		Item item = (Item) table.getValueAt(selected[0], 0);
		VizMapperProperty prop = (VizMapperProperty) item.getProperty();

		if ((prop == null) || (prop.getParentProperty() == null)) return;
		
		final VisualProperty type = (VisualProperty) ((VizMapperProperty) prop.getParentProperty()).getHiddenObject();

		ObjectMapping mapping = vmmp.getCurrentlyEditedVS().getCalculator(type).getMapping(0);
		if (mapping instanceof ContinuousMapping || mapping instanceof PassThroughMapping) return;
		DiscreteMapping dm = (DiscreteMapping) mapping;
		Object newValue = EditorFactory.showDiscreteEditor(type);

		if (newValue == null) return;
		
		Class keyClass = null;
		if (type.isNodeProp()) {
			keyClass = CyAttributesUtils.getClass(mapping.getControllingAttributeName(), Cytoscape.getNodeAttributes());
		} else {
			keyClass = CyAttributesUtils.getClass(mapping.getControllingAttributeName(), Cytoscape.getEdgeAttributes());
		}
		for (int i = 0; i < selected.length; i++) {
			Property p = ((Item) table.getValueAt(selected[i], 0)).getProperty();
			// First, update property sheet:
			p.setValue(newValue);
			// Then update backend:
			Object key = p.getDisplayName();

			if (keyClass == Integer.class) {
				key = Integer.valueOf((String) key);
			} else if (keyClass == Double.class) {
				key = Double.valueOf((String) key);
			} else if (keyClass == Boolean.class) {
				key = Boolean.valueOf((String) key);
			}
			dm.putMapValue(key, newValue);
		}
	}
	
	public void expandLastSelectedItem(String name) {
		final PropertySheetTable table = propertySheetPanel.getTable();
		
		for (int i = 0; i < table.getRowCount(); i++) {
			Item item = (Item) table.getValueAt(i, 0);

			Property curProp = item.getProperty();

			if ((curProp != null) && (curProp.getDisplayName().equals(name))) {
				table.setRowSelectionInterval(i, i); // FIXME: is this needed?

				if (item.isVisible() == false) {
					item.toggle();
				}
				return;
			}
		}
	}

	/** Remove a mapping from current visual style.	 */
	private void removeMapping() {
		VizMapperProperty curProp = getSelectedProperty();
		if ( curProp!= null ){
			final VisualProperty type = (VisualProperty) ((VizMapperProperty) curProp).getHiddenObject();

			if (type == null)
				return;

			String[] message = {"The Mapping for " + type.getName() + " will be removed.","Proceed?"};
			int value = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), message,
					"Remove Mapping",
					JOptionPane.YES_NO_OPTION);

			if (value == JOptionPane.YES_OPTION) {
				// If Continuous Mapper is displayed, kill it.
				if (editorWindowManager.get(type) != null) {
					JDialog editor = editorWindowManager.get(type);
					editor.dispose();
					editorWindowManager.remove(type);
				}

				vmmp.getCurrentlyEditedVS().removeCalculator(type);

				// Finally, move the visual property to "unused list"
				VizMapperProperty prop = new VizMapperProperty();
				prop.setCategory(CATEGORY_UNUSED);
				prop.setDisplayName(type.getName());
				prop.setHiddenObject(type);
				prop.setValue("Double-Click to create...");
				propertySheetPanel.addProperty(prop);
				propertySheetPanel.removeProperty(curProp);
				propertySheetPanel.repaint();
			}
		}
	}
	/**
	 * Switch between mapppings. Each calcs has 3 mappings. The first one
	 * (getMapping(0)) is the current mapping used by calculator.
	 *
	 */
	private void switchMapping(VizMapperProperty prop, String newMapName, Object attrName) {
		if (attrName == null) {
			return;
		}
		VisualStyle currentlyEditedVS = vmmp.getCurrentlyEditedVS();
		CalculatorCatalog calculatorCatalog = vmmp.getCalculatorCatalog();
		final VisualProperty type = (VisualProperty) ((VizMapperProperty) prop .getParentProperty()).getHiddenObject();
		final String newCalcName = currentlyEditedVS.getName() + "-" + type.getName() + "-"+ newMapName;
		// Extract target calculator
		Calculator newCalc = calculatorCatalog.getCalculator(type, newCalcName);
		Calculator oldCalc = null;

		oldCalc = currentlyEditedVS.getCalculator(type);

		/*
		 * If not exist, create new one.
		 */
		if (newCalc == null) {
			newCalc = getNewCalculator(type, newMapName, newCalcName);
			newCalc.getMapping(0).setControllingAttributeName((String) attrName, null, true);
			calculatorCatalog.addCalculator(newCalc);
		}
		newCalc.getMapping(0).setControllingAttributeName((String) attrName, null, true);

		currentlyEditedVS.setCalculator(newCalc);
		/*
		 * If old calc is not standard name, rename it.
		 */
		if (oldCalc != null) {
			final String oldMappingTypeName;

			if (oldCalc.getMapping(0) instanceof DiscreteMapping)
				oldMappingTypeName = "Discrete Mapper";
			else if (oldCalc.getMapping(0) instanceof ContinuousMapping)
				oldMappingTypeName = "Continuous Mapper";
			else if (oldCalc.getMapping(0) instanceof PassThroughMapping)
				oldMappingTypeName = "Passthrough Mapper";
			else
				oldMappingTypeName = null;
			final String oldCalcName = type.getName() + "-" + oldMappingTypeName;
			if (calculatorCatalog.getCalculator(type, oldCalcName) == null) {
				final Calculator newC = getNewCalculator(type, oldMappingTypeName, oldCalcName);
				newC.getMapping(0).setControllingAttributeName((String) attrName, null, false);
				calculatorCatalog.addCalculator(newC);
			}
		}
		propertySheetPanel.removeProperty(prop.getParentProperty());
		final VizMapperProperty newRootProp = new VizMapperProperty();

		buildProperty(currentlyEditedVS.getCalculator(type), newRootProp);
		
		expandLastSelectedItem(type.getName());

		// vmm.getNetworkView().redrawGraph(false, true);
		Cytoscape.firePropertyChange(Cytoscape.VISUALSTYLE_MODIFIED, currentlyEditedVS, null);
	}

	private Calculator getNewCalculator(final VisualProperty type, final String newMappingName,
	                                    final String newCalcName) {

		final CalculatorCatalog catalog = vmmp.getCalculatorCatalog();

		Class mapperClass = catalog.getMapping(newMappingName);

		if (mapperClass == null) {
			return null;
		}

		// create the selected mapper
		Class[] conTypes = { Object.class, byte.class };
		Constructor mapperCon;

		try {
			mapperCon = mapperClass.getConstructor(conTypes);
		} catch (NoSuchMethodException exc) {
			// Should not happen...
			System.err.println("Invalid mapper " + mapperClass.getName());

			return null;
		}

		// create the mapper
		final byte mapType; // node or edge calculator

		if (type.isNodeProp())
			mapType = ObjectMapping.NODE_MAPPING;
		else
			mapType = ObjectMapping.EDGE_MAPPING;

		final Object defaultObj = vmmp.getCurrentlyEditedVS().getDefaultValue(type);

		System.out.println("defobj = " + defaultObj.getClass() + ", Type = " + type.getName());

		final Object[] invokeArgs = { defaultObj, new Byte(mapType) };
		ObjectMapping mapper = null;

		try {
			mapper = (ObjectMapping) mapperCon.newInstance(invokeArgs);
		} catch (Exception exc) {
			System.err.println("Error creating mapping");
			exc.printStackTrace();
			return null;
		}

		return new BasicCalculator(newCalcName, mapper, type);
	}

	
	/*
	 * Set property sheet panel.
	 */
	private void setPropertyTable() {
		// Clean up sheet -- FIXME: this shouldn't be needed since propertySheetPanel is supposed to be empty (?) 
		for (Property item : propertySheetPanel.getProperties())
			propertySheetPanel.removeProperty(item);
		//System.out.println("cleared PropertyTable, num properties:"+(propertySheetPanel.getProperties()).length);
		VisualStyle currentlyEditedVS = vmmp.getCurrentlyEditedVS();
		final List<Calculator> ncList = currentlyEditedVS.getNodeCalculators();
		final List<Calculator> ecList = currentlyEditedVS.getEdgeCalculators();

		editorReg.registerDefaults(); // FIXME ezzel valamit kezdeni
		//System.out.println("ncList length:"+ncList.size());
		/* Add properties to the property sheet. */
		setPropertyFromCalculator(ncList);
		//System.out.println("node calculator properties added, num properties:"+(propertySheetPanel.getProperties()).length);
		setPropertyFromCalculator(ecList);
		
		//System.out.println("in-use properties added, num properties:"+(propertySheetPanel.getProperties()).length);
		/* Finally, add unused visual properties (as VizMapperProperties) to propList */
		for (VisualProperty vp : byNameSortedVisualProperties(VisualPropertyCatalog.collectionOfVisualProperties(vmmp.getCurrentView()))) { //FIXME: assumes currentView
			if (currentlyEditedVS.getCalculator(vp) == null) {
				VizMapperProperty prop = new VizMapperProperty();
				prop.setCategory(CATEGORY_UNUSED);
				prop.setDisplayName(vp.getName());
				prop.setHiddenObject(vp);
				prop.setValue("Double-Click to create...");
				propertySheetPanel.addProperty(prop);
			}
		}
		//System.out.println("setPropertyTable done, num properties:"+(propertySheetPanel.getProperties()).length);
	}
	
	private void setPropertyFromCalculator(List<Calculator> calcList) {
		for (Calculator calc : calcList) {
			final VizMapperProperty calculatorTypeProp = new VizMapperProperty();

			buildProperty(calc, calculatorTypeProp);

			PropertyEditor editor = editorReg.getEditor(calculatorTypeProp);

			if ((editor == null) && (calculatorTypeProp.getCategory().equals("Unused Properties") == false)) {
				VisualProperty type = (VisualProperty) calculatorTypeProp.getHiddenObject();

				if (type.isNodeProp()) {
					editorReg.registerEditor(calculatorTypeProp, nodeAttrEditor);
				} else {
					editorReg.registerEditor(calculatorTypeProp, edgeAttrEditor);
				}
			}
		}
	}
	/*
	 * Build one property for one visual property, and add it to the PropertySheet
	 */
	public final void buildProperty(Calculator calc, VizMapperProperty calculatorTypeProp) {
		final VisualProperty type = calc.getVisualProperty();
		// Set one calculator
		if (type.isNodeProp()){
			calculatorTypeProp.setCategory(NODE_VISUAL_MAPPING);
		} else {
			calculatorTypeProp.setCategory(EDGE_VISUAL_MAPPING);
		}
		calculatorTypeProp.setDisplayName(type.getName());
		calculatorTypeProp.setHiddenObject(type);
		//System.out.println("Build one property for one visual property.");
		// Mapping 0 is always currently used mapping.
		final ObjectMapping firstMap = calc.getMapping(0);
		String attrName;

		if (firstMap != null) {
			attrName = firstMap.getControllingAttributeName();

			if (attrName == null) {
				calculatorTypeProp.setValue("Select Value");
				rendReg.registerRenderer(calculatorTypeProp, emptyBoxRenderer);
			} else {
				calculatorTypeProp.setValue(attrName);
				rendReg.registerRenderer(calculatorTypeProp, filledBoxRenderer);
			}

			final VizMapperProperty mappingHeader = new VizMapperProperty();
			if (firstMap.getClass() == DiscreteMapping.class)
				addSubProperty(calculatorTypeProp, mappingHeader, "Mapping Type", null, "Discrete Mapping");
			else if (firstMap.getClass() == ContinuousMapping.class)
				addSubProperty(calculatorTypeProp, mappingHeader, "Mapping Type", null, "Continuous Mapping");
			else
				addSubProperty(calculatorTypeProp, mappingHeader, "Mapping Type", null, "Passthrough Mapping");
			mappingHeader.setHiddenObject(firstMap);
			
			editorReg.registerEditor(mappingHeader, mappingTypeEditor);

			final CyAttributes attr;

			if (calc.getVisualProperty().isNodeProp()) {
				attr = Cytoscape.getNodeAttributes();
				editorReg.registerEditor(calculatorTypeProp, nodeAttrEditor);
			} else {
				attr = Cytoscape.getEdgeAttributes();
				editorReg.registerEditor(calculatorTypeProp, edgeAttrEditor);
			}

			if ((firstMap.getClass() == DiscreteMapping.class) && (attrName != null)) {
				final Map discMapping = ((DiscreteMapping) firstMap).getAll();
				final Set<Object> attrSet = loadKeys(attrName, attr, firstMap, calc.getVisualProperty().isNodeProp());
				Class<?> dataTypeClass = type.getDataType();

				if (dataTypeClass.isAssignableFrom(DiscreteValue.class)){ // FIXME: these should be Enum-like instead!! (or Discrete or something)
					setDiscreteProps(type, discMapping, attrSet, buildCellEditor(type), new ShapeCellRenderer(type), calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(LabelPosition.class)){
					setDiscreteProps(type, discMapping, attrSet, labelPositionEditor, labelPositionRenderer, calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(Number.class)){
					setDiscreteProps(type, discMapping, attrSet, numberCellEditor, defCellRenderer, calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(Font.class)){
					setDiscreteProps(type, discMapping, attrSet, fontCellEditor, fontCellRenderer, calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(String.class)){
					setDiscreteProps(type, discMapping, attrSet, stringCellEditor, defCellRenderer, calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(Color.class)){
					setDiscreteProps(type, discMapping, attrSet, colorCellEditor, colorCellRenderer, calculatorTypeProp);
				} else {
					System.out.println("unknown datatype:"+dataTypeClass);
				}
			} else if ((firstMap.getClass() == ContinuousMapping.class) && (attrName != null)) {
				int wi = propertySheetPanel.getTable().getCellRect(0, 1, true).width;

				VizMapperProperty graphicalView = new VizMapperProperty();
				graphicalView.setName(type.getName());
				addSubProperty(calculatorTypeProp, graphicalView, GRAPHICAL_MAP_VIEW, null, null);

				final Class dataType = type.getDataType();
				final ImageIcon icon = ContinuousMappingEditorPanel.getIcon(wi, 70, (VisualProperty) type);

				if (dataType == Color.class) {
					final DefaultTableCellRenderer gradientRenderer = new DefaultTableCellRenderer();
					gradientRenderer.setIcon(icon);
					rendReg.registerRenderer(graphicalView, gradientRenderer);
				} else if (dataType == Number.class) {
					continuousRenderer.setIcon(icon);
					rendReg.registerRenderer(graphicalView, continuousRenderer);
				} else {
					discreteRenderer.setIcon(icon);
					rendReg.registerRenderer(graphicalView, discreteRenderer);
				}
			} else if ((firstMap.getClass() == PassThroughMapping.class) && (attrName != null)) {
				if (attr.getType(attrName) == CyAttributes.TYPE_STRING) {
					final Iterator it;
					if (calc.getVisualProperty().isNodeProp()) {
						it = vmmp.getCurrentView().getNetwork().nodesIterator();
					} else {
						it = vmmp.getCurrentView().getNetwork().edgesIterator();
					}

					while (it.hasNext()) {
						String id = ((GraphObject) it.next()).getIdentifier();

						VizMapperProperty oneProperty = new VizMapperProperty();

						if (attrName.equals("ID"))
							addSubProperty(calculatorTypeProp, oneProperty, id, String.class, id);
						else
							addSubProperty(calculatorTypeProp, oneProperty, id, String.class, attr.getStringAttribute(id, attrName));

						oneProperty.setEditable(false); // can't edit Passthrough mapping
					}
				}
			}
		}
		propertySheetPanel.addProperty(calculatorTypeProp);
	}
	/** Add given Property as subProperty to given Property, and set some values on the subProperty*/
	private void addSubProperty(VizMapperProperty parentProp, VizMapperProperty subProp, String displayName, Class klass, Object value){
		subProp.setParentProperty(parentProp);
		subProp.setDisplayName(displayName);
		if (klass != null) subProp.setType(klass);
		if (value != null) subProp.setValue(value);
		parentProp.addSubProperty(subProp);
	}
	
	/*
	 * Set value, title, and renderer for each property in the category.
	 */
	private final void setDiscreteProps(VisualProperty type, Map discMapping, Set<Object> attrKeys, PropertyEditor editor,
	                                    TableCellRenderer rend, VizMapperProperty parent) {
		if (attrKeys == null) return;

		for (Object key : attrKeys) {
			VizMapperProperty  valProp = new VizMapperProperty();
			String strVal = key.toString();
			valProp.setName(strVal + "-" + type.toString());

			Object val = null;
			try {
				val = discMapping.get(key);
			} catch (Exception e) {
				System.out.println("------- Map = " + discMapping.getClass() + ", class = " + key.getClass() + ", err = " + e.getMessage());
				System.out.println("------- Key = " + key + ", val = " + val + ", disp = " + strVal);
			}

			Class c;
			if (val != null){
				c = val.getClass();
			} else {
				c = null;
			}
			addSubProperty(parent, valProp, strVal, c, val);
			
			rendReg.registerRenderer(valProp, rend);
			editorReg.registerEditor(valProp, editor);
		}
	}

	private void updateTableView() {
		final PropertySheetTable table = propertySheetPanel.getTable();
		Property shownProp = null;
		final DefaultTableCellRenderer empRenderer = new DefaultTableCellRenderer();

		for (int i = 0; i < table.getRowCount(); i++) {
			shownProp = ((Item) table.getValueAt(i, 0)).getProperty();

			if ((shownProp != null) && (shownProp.getParentProperty() != null)
			    && shownProp.getParentProperty().getDisplayName()
			                .equals("NODE_LABEL_POSITION")) {
				// This is label position cell. Need larger cell.
				table.setRowHeight(i, 50);
			} else if ((shownProp != null) && shownProp.getDisplayName().equals(GRAPHICAL_MAP_VIEW)) {
				// This is a Continuous Icon cell.
				final Property parent = shownProp.getParentProperty();
				final Object type = ((VizMapperProperty) parent).getHiddenObject();

				if (type instanceof VisualProperty) {
					ObjectMapping mapping;
					VisualProperty vp = (VisualProperty) type;

					mapping = vmmp.getCurrentlyEditedVS().getCalculator(vp).getMapping(0);

					if (mapping instanceof ContinuousMapping) {
						table.setRowHeight(i, 80);

						int wi = table.getCellRect(0, 1, true).width;
						final ImageIcon icon = ContinuousMappingEditorPanel.getIcon(wi, 70, vp);
						final Class dataType = vp.getDataType();

						if (dataType == Color.class) {
							final DefaultTableCellRenderer gradientRenderer = new DefaultTableCellRenderer();
							gradientRenderer.setIcon(icon);
							rendReg.registerRenderer(shownProp, gradientRenderer);
						} else if (dataType == Number.class) {
							final DefaultTableCellRenderer cRenderer = new DefaultTableCellRenderer();
							cRenderer.setIcon(icon);
							rendReg.registerRenderer(shownProp, cRenderer);
						} else {
							final DefaultTableCellRenderer dRenderer = new DefaultTableCellRenderer();
							dRenderer.setIcon(icon);
							rendReg.registerRenderer(shownProp, dRenderer);
						}
					}
				}
			} else if ((shownProp != null) && (shownProp.getCategory() != null)
			           && shownProp.getCategory().equals(CATEGORY_UNUSED)) {
				empRenderer.setForeground(new Color(100, 100, 100, 50)); // UNUSED_COLOR
				rendReg.registerRenderer(shownProp, empRenderer);
			}
		}
	}
	
	private CyComboBoxPropertyEditor buildCellEditor(VisualProperty vp){
		final List<Icon> iconList = new ArrayList<Icon>();
		final List<Object> values = new ArrayList<Object>();
		final Map<Object, VisualPropertyIcon> iconSet = vp.getIconSet();
		//System.out.println("building discrete editor for:"+vp.getName());
		for (Object val: iconSet.keySet()){
			iconList.add(iconSet.get(val));
			values.add(val);
		}
		CyComboBoxPropertyEditor editor = new CyComboBoxPropertyEditor();
		editor.setAvailableValues(values.toArray());
		editor.setAvailableIcons(iconList.toArray(new Icon[0]));
		return editor;
	}

	/* Returns a sorted list of the visual properties
	 * Needed because VisualProperties themselves are not Comparable,
	 * (forcing them to be comparable would bloat the API and) 
	 */
	private List<VisualProperty> byNameSortedVisualProperties(Collection <VisualProperty>input){
		// FIXME: I bet this could be done more java-style (i.e. better)
		List <String> vpNames = new ArrayList<String>();
		for (VisualProperty vp:input){
			vpNames.add(vp.getName());
		}
		Collections.sort(vpNames);
		List <VisualProperty> result = new ArrayList<VisualProperty>(input.size());
		for (String name: vpNames){
			result.add(VisualPropertyCatalog.getVisualProperty(name));
		}
		return result;
	}
// FIXME: methods below could go into CyAttributesUtils instead?
	/**
	 * <p>
	 * If user selects ID as controlling attributes name, create list of IDs
	 * from actual list of nodes/edges.
	 * </p>
	 *
	 * @return
	 */
	private Set<Object> loadID(final boolean forNodes) {
		Set<Object> ids = new TreeSet<Object>();

		List<? extends GraphObject> obj;

		GraphView view = vmmp.getCurrentView();
		 // if there is no network open we can't do anything but return empty result.
		if (view == null || view == Cytoscape.getNullNetworkView()) return ids;

		if (forNodes) {
			obj = view.getGraphPerspective().nodesList();
		} else {
			obj = view.getGraphPerspective().edgesList();
		}

		for (GraphObject o : obj) {
			ids.add( o.getIdentifier() );
		}

		return ids;
	}

	public Set<Object> loadKeys(final String attrName, final CyAttributes attrs,
	                             final ObjectMapping mapping, final boolean forNodes) {
		if (attrName.equals("ID")) {
			return loadID(forNodes);
		}

		Map mapAttrs;
		mapAttrs = CyAttributesUtils.getAttribute(attrName, attrs);

		if ((mapAttrs == null) || (mapAttrs.size() == 0))
			return new TreeSet<Object>();

		List acceptedClasses = Arrays.asList(mapping.getAcceptedDataClasses());
		Class mapAttrClass = CyAttributesUtils.getClass(attrName, attrs);

		if ((mapAttrClass == null) || !(acceptedClasses.contains(mapAttrClass)))
			return new TreeSet<Object>(); // Return empty set.

		return loadKeySet(mapAttrs);
	}

	/**
	 * Loads the Key Set.
	 */
	private Set<Object> loadKeySet(final Map mapAttrs) {
		final Set<Object> mappedKeys = new TreeSet<Object>();

		final Iterator keyIter = mapAttrs.values().iterator();

		Object o = null;

		while (keyIter.hasNext()) {
			o = keyIter.next();

			if (o instanceof List) {
				List list = (List) o;

				for (int i = 0; i < list.size(); i++) {
					Object vo = list.get(i);

					if (!mappedKeys.contains(vo))
						mappedKeys.add(vo);
				}
			} else {
				if (!mappedKeys.contains(o))
					mappedKeys.add(o);
			}
		}
		return mappedKeys;
	}
}
