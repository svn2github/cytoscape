package org.cytoscape.view.vizmap.gui.internal;

import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.view.vizmap.gui.VisualPropertyDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DependencyTable extends JTable {
	
	private static final long serialVersionUID = -8052559216229363239L;
	
	private static final Logger logger = LoggerFactory.getLogger(DependencyTable.class);
	
	private final DefaultTableModel model;
	
	public DependencyTable() {
		model = new DefaultTableModel();
		buildModel();
		this.setModel(model);
	}
	
	
	private void buildModel() {
		model.addColumn("Set Dependency");
		model.addColumn("Description");
	}
	
	public void addDependency(final VisualPropertyDependency dep, Map props) {
		logger.debug("------------ New Dependency: " + dep.getDisplayName());
		
		
	}
	
	public void removeDependency(final VisualPropertyDependency dep, Map props) {
		
		
		
	}
	

}
