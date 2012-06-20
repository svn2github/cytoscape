package org.cytoscape.sample.internal;

import java.util.HashMap;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyTable;

public class TableAddedEvent implements SetCurrentNetworkListener{

	private MyCytoPanel myCytoPanel;
	private JTable table;
	private CyTable cytable;
	private static HashMap<String, Object> panelComponentMap;
	private JCheckBox[] checkBoxArray;
	private PanelComponents panelComponents;
	private int tableColumnCount;
	public static boolean networkDestroyed = false;
	
	TableAddedEvent(MyCytoPanel myCytoPanel){
		
		this.myCytoPanel = myCytoPanel;
		panelComponentMap = new HashMap<String, Object>();
	}

	
	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
		//If this method was called immediately following a network destroyed event, which it by default does,
		//then such a current network event should not be implemented because the pointer doesn't point to 
		//a particular network at that time.
		if(!networkDestroyed){	
			cytable = e.getNetwork().getDefaultNodeTable();
			if(cytable!=null)
			{
				if(panelComponentMap.containsKey(cytable.getTitle())) {	
					
					panelComponents = (PanelComponents) panelComponentMap.get(cytable.getTitle());
					table = panelComponents.getTable();
					checkBoxArray = panelComponents.getCheckBoxArray();
				} else {
					
					table = new JTable(new MyTableModel(cytable));
					panelComponents = new PanelComponents(table);
					checkBoxArray = panelComponents.initialiseCheckBoxArray();
					panelComponentMap.put(cytable.getTitle(), panelComponents);
				}	
			
				tableColumnCount = panelComponents.getTableColumnCount();
				myCytoPanel.initComponents(table, checkBoxArray, tableColumnCount);
			}
		} else {
			networkDestroyed = false;
		}
	}
	
	public static HashMap<String, Object> getPanelComponentMap(){
		return panelComponentMap;
	}
}
