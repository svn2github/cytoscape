package org.cytoscape.sample.internal;

import java.io.Serializable;
import java.util.HashMap;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

public class TableEvents implements NetworkAboutToBeDestroyedListener, SetCurrentNetworkListener{

	private MyCytoPanel myCytoPanel;
	private JTable table;
	private CyTable cytable;
	private CreateTable createTable;
	private HashMap<String, Serializable> panelComponentMap;
	private JCheckBox[] checkBoxArray;
	private PanelComponents panelComponents;
	private int tableColumnCount;
	
	TableEvents(MyCytoPanel myCytoPanel){
		
		this.myCytoPanel = myCytoPanel;
		panelComponentMap = new HashMap<String, Serializable>();
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		panelComponentMap.remove(e.getNetwork().getDefaultNodeTable().getTitle());
	}

	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
		
		cytable = e.getNetwork().getDefaultNodeTable();
		if(cytable!=null)
		{
			if(panelComponentMap.containsKey(cytable.getTitle())){	
				panelComponents = (PanelComponents) panelComponentMap.get(cytable.getTitle());
				table = panelComponents.getTable();
				checkBoxArray = panelComponents.getCheckBoxArray();
			}else{
				createTable = new CreateTable(cytable);
				table = createTable.setTableValues(createTable.getColumnVector(), cytable.getRowCount());
				panelComponents = new PanelComponents(table);
				checkBoxArray = panelComponents.initialiseCheckBoxArray();
				panelComponentMap.put(cytable.getTitle(), panelComponents);
			}	
			
			tableColumnCount = panelComponents.getTableColumnCount();
			myCytoPanel.initComponents(table, checkBoxArray, tableColumnCount);
		}
		
	}
	
	
}
