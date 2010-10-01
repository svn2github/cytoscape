package org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComboBox;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.events.NetworkAddedEvent;
import org.cytoscape.session.events.NetworkAddedListener;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.gui.editor.ListEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds list of attributes. By default, three instances of this should be
 * created (for NODE, EDGE, and NETWORK).
 * 
 * Export this as OSGi service!
 * 
 * @author kono
 * 
 */
public class AttributeComboBoxPropertyEditor extends CyComboBoxPropertyEditor
		implements ColumnDeletedListener, ColumnCreatedListener,
		NetworkAddedListener, ListEditor {

	private static final Logger logger = LoggerFactory
			.getLogger(AttributeComboBoxPropertyEditor.class);

	private final String name;
	private final CyTableManager tableMgr;

	private final Set<CyTable> targetTables;

	private final SortedSet<String> attrNames;
	
	private VisualProperty<Visualizable> category;

	public AttributeComboBoxPropertyEditor(final String name,
			final CyTableManager tableMgr, final CyNetworkManager networkMgr) {

		// Validate
		if (!name.equals(CyTableEntry.NODE) && !name.equals(CyTableEntry.EDGE)
				&& !name.equals(CyTableEntry.NETWORK))
			throw new IllegalArgumentException(
					"Name should be NODE, EDGE, or NETWORK.");

		this.name = name;
		this.tableMgr = tableMgr;
		this.attrNames = new TreeSet<String>();

		// This set contains NODE, EDGE, or NETWORK tables.
		targetTables = new HashSet<CyTable>();

		final Set<CyNetwork> networkSet = networkMgr.getNetworkSet();
		for (final CyNetwork net : networkSet) {
			final Map<String, CyTable> tableMap = tableMgr.getTableMap(name,
					net);
			targetTables.addAll(tableMap.values());

			for (final CyTable table : tableMap.values())
				attrNames.addAll(table.getUniqueColumns());
		}

		final JComboBox box = (JComboBox) editor;
		for (String attrName : attrNames)
			box.addItem(attrName);
		
		assignCategory();
	}
	
	private void assignCategory() {
		if(name.equals(CyTableEntry.NODE))
			category = TwoDVisualLexicon.NODE;
		else if(name.equals(CyTableEntry.EDGE))
			category = TwoDVisualLexicon.EDGE;
		else if(name.equals(CyTableEntry.NETWORK))
			category = TwoDVisualLexicon.NETWORK;
	}
	
	public VisualProperty<Visualizable> getCategory() {
		return this.category;
	}

	@Override
	public void handleEvent(ColumnDeletedEvent e) {
		final CyTable table = e.getSource();
		if (!targetTables.contains(table))
			return;

		attrNames.remove(e.getColumnName());

		final JComboBox box = (JComboBox) editor;
		box.removeItem(e.getColumnName());
	}

	@Override
	public void handleEvent(ColumnCreatedEvent e) {

		final String newAttributeName = e.getColumnName();
		final CyTable table = e.getSource();

		if (!targetTables.contains(table))
			return;

		if (attrNames.contains(newAttributeName))
			return;

		attrNames.add(newAttributeName);

		updateComboBox();
	}

	@Override
	public String getTargetObjectName() {
		return name;
	}

	@Override
	public void handleEvent(NetworkAddedEvent e) {
		final Map<String, CyTable> tableMap = tableMgr.getTableMap(name,
				e.getNetwork());
		targetTables.addAll(tableMap.values());
		for (CyTable table : tableMap.values()) {
			attrNames.addAll(table.getColumnTypeMap().keySet());
		}

		updateComboBox();
	}

	private void updateComboBox() {
		final JComboBox box = (JComboBox) editor;
		final Object selected = box.getSelectedItem();
		box.removeAllItems();
		for (String attrName : attrNames)
			box.addItem(attrName);

		// Add new name if not in the list.
		box.setSelectedItem(selected);

		logger.debug(name + " attribute Combobox Updated: New Names = "
				+ attrNames);

	}
}
