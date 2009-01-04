/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.vizmap.gui.internal;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.cytoscape.model.GraphObject;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.vizmap.gui.DefaultViewEditor;
import org.cytoscape.vizmap.gui.DefaultViewPanel;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.gui.event.VizMapEventHandler;
import org.cytoscape.vizmap.gui.event.VizMapEventHandlerManager;
import org.cytoscape.vizmap.gui.theme.ColorManager;
import org.cytoscape.vizmap.gui.theme.IconManager;
import org.cytoscape.vizmap.MappingCalculator;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonUI;

import cytoscape.CyNetworkManager;
import cytoscape.Cytoscape;
import cytoscape.view.CySwingApplication;

/**
 * New VizMapper UI main panel. Refactored for Cytoscape 3.
 * 
 * This panel consists of 3 panels:
 * <ul>
 * <li>Global Control Panel
 * <li>Default editor panel
 * <li>Visual Mapping Browser
 * </ul>
 * 
 * @version 0.8
 * @since Cytoscape 2.5
 * @author Keiichiro Ono
 * @param <syncronized>
 */
public class VizMapperMainPanel extends AbstractVizMapperPanel implements
		PropertyChangeListener, PopupMenuListener, ChangeListener {

	private final static long serialVersionUID = 1202339867854959L;

	private boolean ignore = false;
	private String lastVSName = null;

	/**
	 * Create new instance of VizMapperMainPanel object. GUI layout is handled
	 * by abstract class.
	 * 
	 * @param desktop
	 * @param dab
	 * @param iconMgr
	 * @param colorMgr
	 * @param vmm
	 * @param menuMgr
	 * @param editorFactory
	 */
	public VizMapperMainPanel(CySwingApplication desktop,
			DefaultViewEditor defViewEditor, IconManager iconMgr,
			ColorManager colorMgr, VisualMappingManager vmm,
			VizMapperMenuManager menuMgr, EditorFactory editorFactory,
			PropertySheetPanel propertySheetPanel,
			VizMapPropertySheetBuilder vizMapPropertySheetBuilder,
			VizMapEventHandlerManager vizMapEventHandlerManager,
			EditorWindowManager editorWindowManager, CyNetworkManager cyNetworkManager) {
		super(desktop, defViewEditor, iconMgr, colorMgr, vmm, menuMgr,
				editorFactory, propertySheetPanel, vizMapPropertySheetBuilder,
				vizMapEventHandlerManager, editorWindowManager, cyNetworkManager);

		initPanel();
	}

	private void initPanel() {
		vmm.addChangeListener(this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				new VizMapListener(vmm, cyNetworkManager));

		registerCellEditorListeners();
		addVisualStyleChangeAction();

		// By default, force to sort property by prop name.
		propertySheetPanel.setSorting(true);
		
		refreshUI();

		cytoscapeDesktop.getCytoPanel(SwingConstants.WEST).add(
				"VizMapper\u2122", this);
		cytoscapeDesktop.getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);
	}

	/*
	 * Register listeners for editors.
	 */
	private void registerCellEditorListeners() {
		for (PropertyEditor p : editorFactory.getCellEditors()) {
			p.addPropertyChangeListener(this);

			if (p instanceof PropertyChangeListener)
				spcs.addPropertyChangeListener((PropertyChangeListener) p);
		}
	}

	private void addVisualStyleChangeAction() {
		getVsNameComboBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final String vsName = (String) getVsNameComboBox()
						.getSelectedItem();
				if (vsName != null) {
					if (cyNetworkManager.getCurrentNetworkView() == null)
						switchVS(vsName, false);
					else
						switchVS(vsName, true);
				}
			}
		});
	}

	public void setLastVSName(final String newName) {
		this.lastVSName = newName;
	}

	public void switchVS(String vsName) {
		switchVS(vsName, true);
	}

	public void switchVS(String vsName, boolean redraw) {
		if (ignore)
			return;

		// If new VS name is the same, ignore.
		if (lastVSName == vsName)
			return;

		editorWindowManager.closeAllEditorWindows();

		//vmm.setNetworkView(cyNetworkManager.getCurrentNetworkView());
		//vmm.setVisualStyle(vsName);

		if (vizMapPropertySheetBuilder.getPropertyMap().containsKey(vsName)) {
			final List<Property> props = vizMapPropertySheetBuilder
					.getPropertyMap().get(vsName);
			final Map<String, Property> unused = new TreeMap<String, Property>();

			/*
			 * Remove currently shown property
			 */
			for (Property item : propertySheetPanel.getProperties())
				propertySheetPanel.removeProperty(item);

			/*
			 * Add properties to current property sheet.
			 */
			for (Property prop : props) {
				if (prop.getCategory().startsWith(CATEGORY_UNUSED) == false) {
					if (prop.getCategory().equals(NODE_VISUAL_MAPPING)) {
						propertySheetPanel.addProperty(0, prop);
					} else {
						propertySheetPanel.addProperty(prop);
					}
				} else {
					unused.put(prop.getDisplayName(), prop);
				}
			}

			final List<String> keys = new ArrayList<String>(unused.keySet());
			Collections.sort(keys);

			for (Object key : keys) {
				propertySheetPanel.addProperty(unused.get(key));
			}
		} else {
			vizMapPropertySheetBuilder.setPropertyTable();
			updateAttributeList();
		}

		vmm.setVisualStyleForView(cyNetworkManager.getCurrentNetworkView(), vmm.getVisualStyle(vsName));

		//if (redraw)
		//	Cytoscape.redrawGraph(cyNetworkManager.getCurrentNetworkView());

		/*
		 * Draw default view
		 */
		Image defImg = defaultImageManager.get(vsName);

		if (defImg == null) {
			// Default image is not available in the buffer. Create a new one.
			updateDefaultImage(vsName,
					(GraphView) ((DefaultViewPanel) defViewEditor
							.getDefaultView(vsName)).getView(),
					defaultViewImagePanel.getSize());
			defImg = defaultImageManager.get(vsName);
		}

		// Set the default view to the panel.
		setDefaultViewImagePanel(defImg);

		// Sync. lock state
		final boolean lockState = vmm.getVisualStyle()
				.getNodeAppearanceCalculator().getNodeSizeLocked();
		spcs.firePropertyChange("UPDATE_LOCK", null, lockState);
		propertySheetPanel.setSorting(true);

		// Cleanup desktop.
		// cytoscapeDesktop.repaint();
		getVsNameComboBox().setSelectedItem(vsName);
	}

	public void refreshUI() {
		List<String> vsNames = new ArrayList<String>(vmm.getCalculatorCatalog()
				.getVisualStyleNames());

		final VisualStyle style = vmm.getVisualStyle();

		// Disable action listeners
		final ActionListener[] li = getVsNameComboBox().getActionListeners();

		for (int i = 0; i < li.length; i++)
			getVsNameComboBox().removeActionListener(li[i]);

		getVsNameComboBox().removeAllItems();

		JPanel defPanel;

		final Dimension panelSize = defaultViewImagePanel.getSize();
		GraphView view;

		Collections.sort(vsNames);

		for (String name : vsNames) {
			getVsNameComboBox().addItem(name);
			defPanel = defViewEditor.getDefaultView(name);
			view = (GraphView) ((DefaultViewPanel) defPanel).getView();

			if (view != null)
				updateDefaultImage(name, view, panelSize);
		}

		//vmm.setNetworkView(cyNetworkManager.getCurrentNetworkView());

		// Switch back to the original style.
		switchVS(style.getName());

		// Sync check box and actual lock state
		spcs.firePropertyChange("UPDATE_LOCK", null, true);

		// switchNodeSizeLock(lockSize.isSelected());

		// Restore listeners
		for (int i = 0; i < li.length; i++)
			getVsNameComboBox().addActionListener(li[i]);
	}

	/**
	 * Create image of a default dummy network and save in a Map object.
	 * 
	 * @param vsName
	 * @param view
	 * @param size
	 */
	public void updateDefaultImage(String vsName, GraphView view, Dimension size) {
		Image image = defaultImageManager.remove(vsName);

		if (image != null) {
			image.flush();
			image = null;
		}

		defaultImageManager.put(vsName, view.createImage((int) size.getWidth(),
				(int) size.getHeight(), 0.9));
	}

	public void updateAttributeList() {
		vizMapPropertySheetBuilder.setAttrComboBox();
		final Set mappingTypes = vmm.getCalculatorCatalog().getMappingNames();

		// mappingTypeEditor.setAvailableValues(mappingTypes.toArray());
		spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "mappingTypeEditor",
				mappingTypes.toArray());
	}

	/*
	 * private Set<Object> loadKeys(final String attrName, final CyDataTable
	 * attrs, final MappingCalculator mapping, final int nOre) { if
	 * (attrName.equals("ID")) { return loadID(nOre); }
	 * 
	 * Map mapAttrs; mapAttrs = CyAttributesUtils.getAttribute(attrName, attrs);
	 * 
	 * if ((mapAttrs == null) || (mapAttrs.size() == 0)) return new
	 * TreeSet<Object>();
	 * 
	 * List acceptedClasses = Arrays.asList(mapping.getAcceptedDataClasses());
	 * Class mapAttrClass = CyAttributesUtils.getClass(attrName, attrs);
	 * 
	 * if ((mapAttrClass == null) || !(acceptedClasses.contains(mapAttrClass)))
	 * return new TreeSet<Object>(); // Return empty set.
	 * 
	 * return loadKeySet(mapAttrs); }
	 */

	/**
	 * Loads the Key Set. private Set<Object> loadKeySet(final Map mapAttrs) {
	 * final Set<Object> mappedKeys = new TreeSet<Object>();
	 * 
	 * final Iterator keyIter = mapAttrs.values().iterator();
	 * 
	 * Object o = null;
	 * 
	 * while (keyIter.hasNext()) { o = keyIter.next();
	 * 
	 * if (o instanceof List) { List list = (List) o;
	 * 
	 * for (int i = 0; i < list.size(); i++) { Object vo = list.get(i);
	 * 
	 * if (!mappedKeys.contains(vo)) mappedKeys.add(vo); } } else { if
	 * (!mappedKeys.contains(o)) mappedKeys.add(o); } }
	 * 
	 * return mappedKeys; }
	 */
	public void setDefaultViewImagePanel(final Image defImage) {
		if (defImage == null)
			return;

		defaultViewImagePanel.removeAll();

		final JButton defaultImageButton = new JButton();
		defaultImageButton.setUI(new BlueishButtonUI());
		defaultImageButton.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		defaultImageButton.setIcon(new ImageIcon(defImage));
		defaultViewImagePanel.add(defaultImageButton, BorderLayout.CENTER);
		defaultImageButton.addMouseListener(new DefaultViewMouseListener(vmm,
				this, defViewEditor, cyNetworkManager));
	}

	public JPanel getDefaultPanel() {
		return defaultViewImagePanel;
	}

	/**
	 * On/Off listeners. This is for performance.
	 * 
	 * @param on
	 *            DOCUMENT ME!
	 */
	private void enableListeners(boolean on) {
		if (on) {
			vmm.addChangeListener(this);
			syncStyleBox();
			ignore = false;
		} else {
			vmm.removeChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		// Set ignore flag.
		if (e.getPropertyName().equals(
				Integer.toString(Cytoscape.SESSION_OPENED))) {
			ignore = true;
			enableListeners(false);
		}

		if (ignore)
			return;

		final VizMapEventHandler handler = vizMapEventHandlerManager
				.getHandler(e.getPropertyName());
		if (handler != null)
			handler.processEvent(e);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param vsName
	 *            DOCUMENT ME!
	 */
	public void setCurrentVS(String vsName) {
		getVsNameComboBox().setSelectedItem(vsName);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg0
	 *            DOCUMENT ME!
	 */
	public void popupMenuCanceled(PopupMenuEvent arg0) {
		// TODO: replace this to firePropertyChange
		// disableAllPopup();
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
		// disableAllPopup();

		final int selected = propertySheetPanel.getTable().getSelectedRow();

		if (0 > selected) {
			return;
		}

		final Item item = (Item) propertySheetPanel.getTable().getValueAt(
				selected, 0);
		final Property curProp = item.getProperty();

		if (curProp == null)
			return;

		VizMapperProperty prop = ((VizMapperProperty) curProp);

		if (prop.getHiddenObject() instanceof VisualProperty
				&& (prop.getDisplayName().contains("Mapping Type") == false)
				&& (prop.getValue() != null)
				&& (prop.getValue().toString().startsWith("Please select") == false)) {
			// Enble delete menu
			// delete.setEnabled(true);
			Property[] children = prop.getSubProperties();

			for (Property p : children) {
				if ((p.getDisplayName() != null)
						&& p.getDisplayName().contains("Mapping Type")) {
					if ((p.getValue() == null)
							|| (p.getValue().equals("Discrete Mapping") == false)) {
						return;
					}
				}
			}

			VisualProperty type = ((VisualProperty) prop
					.getHiddenObject());

			Class dataType = type.getDataType();

			// if (dataType == Color.class) {
			// rainbow1.setEnabled(true);
			// rainbow2.setEnabled(true);
			// randomize.setEnabled(true);
			// brighter.setEnabled(true);
			// darker.setEnabled(true);
			// } else if (dataType == Number.class) {
			// randomize.setEnabled(true);
			// series.setEnabled(true);
			// }
			//
			// if ((type == VisualProperty.NODE_WIDTH)
			// || (type == VisualProperty.NODE_HEIGHT)) {
			// fit.setEnabled(true);
			// }
		}

		return;
	}

	/**
	 * <p>
	 * If user selects ID as controlling attributes name, cretate list of IDs
	 * from actual list of nodes/edges.
	 * </p>
	 * 
	 * @return
	 */
	private Set<Object> loadID(final int nOre) {
		Set<Object> ids = new TreeSet<Object>();

		List<? extends GraphObject> obj;

		if (nOre == MappingCalculator.NODE_MAPPING) {
			obj = cyNetworkManager.getCurrentNetworkView().getGraphPerspective().getNodeList();
		} else {
			obj = cyNetworkManager.getCurrentNetworkView().getGraphPerspective().getEdgeList();
		}

		for (GraphObject o : obj) {
			ids.add(o.attrs().get("name", String.class));
		}

		return ids;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void stateChanged(ChangeEvent e) {
		final String selectedName = (String) getVsNameComboBox()
				.getSelectedItem();
		final String currentName = vmm.getVisualStyle().getName();

		final GraphView curView = cyNetworkManager.getCurrentNetworkView();

		if (ignore)
			return;

		System.out.println("Got VMM Change event.  Cur VS in VMM: "
				+ vmm.getVisualStyle().getName());

		if ((selectedName == null) || (currentName == null)
				|| (curView == null) )
			return;

		// Update GUI based on CalcCatalog's state.
		if (!findVSName(currentName)) {
			syncStyleBox();
		} else {
			// Bug fix: 0001802: if VS already existed in combobox, select it
			for (int i = 0; i < getVsNameComboBox().getItemCount(); i++) {
				if (getVsNameComboBox().getItemAt(i).equals(currentName)) {
					getVsNameComboBox().setSelectedIndex(i);

					break;
				}
			}
		}

		// kono: should be placed here.
		// MLC 03/31/08 BEGIN:
		// Make fure we update the lastVSName based on anything that changes the
		// visual style:
		lastVSName = currentName;

		// MLC 03/31/08 END.
	}

	private void syncStyleBox() {
		String curStyleName = vmm.getVisualStyle().getName();

		String styleName;
		List<String> namesInBox = new ArrayList<String>();
		namesInBox.addAll(vmm.getCalculatorCatalog().getVisualStyleNames());

		for (int i = 0; i < getVsNameComboBox().getItemCount(); i++) {
			styleName = getVsNameComboBox().getItemAt(i).toString();

			if (vmm.getCalculatorCatalog().getVisualStyle(styleName) == null) {
				// No longer exists in the VMM. Remove.
				getVsNameComboBox().removeItem(styleName);
				defaultImageManager.remove(styleName);
				vizMapPropertySheetBuilder.getPropertyMap().remove(styleName);
			}
		}

		Collections.sort(namesInBox);

		// Reset combobox items.
		getVsNameComboBox().removeAllItems();

		for (String name : namesInBox)
			getVsNameComboBox().addItem(name);

		// Bug fix: 0001721:
		// Note: Because vsNameComboBox.removeAllItems() will fire unwanted
		// event,
		// vmm.getVisualStyle().getName() will not be the same as curStyleName
		if ((curStyleName == null) || curStyleName.trim().equals(""))
			switchVS(vmm.getVisualStyle().getName());
		else
			switchVS(curStyleName);
	}

	// return true iff 'match' is found as a name within the
	// vsNameComboBox.
	private boolean findVSName(String match) {
		for (int i = 0; i < getVsNameComboBox().getItemCount(); i++) {
			if (getVsNameComboBox().getItemAt(i).equals(match)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Object getSelectedItem() {
		final JTable table = propertySheetPanel.getTable();

		return table.getModel().getValueAt(table.getSelectedRow(), 0);
	}
}
