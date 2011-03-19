package org.cytoscape.tableimport.internal;

import java.io.IOException;
import org.cytoscape.tableimport.internal.ui.ImportTextTableDialog;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesGUI;
import org.cytoscape.work.TaskMonitor;


import javax.swing.JPanel;
import javax.xml.bind.JAXBException;


public class ImportAttributesTask extends AbstractTask {
	
	private ImportTextTableDialog importDialog = null;
	
	public ImportAttributesTask(){
		System.out.println("Entering ImportAttributesTask constructor ...");
	}

	@ProvidesGUI
	public JPanel getGUI() { 
		
		System.out.println("Entering ImportAttributeTableTask.getGUI() ...");
		
		JPanel myPanel = new JPanel();

		try {
			this.importDialog = new ImportTextTableDialog(true, ImportTextTableDialog.SIMPLE_ATTRIBUTE_IMPORT);
			
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (importDialog != null){
			myPanel.add(importDialog);
		}
		
		return myPanel; 
	}
	
	
	public void run(TaskMonitor taskMonitor) {
		System.out.println("Entering ImportAttributeTableTask.run()...");
		try {
			this.importDialog.importButtonActionPerformed();
			if (this.importDialog.getLoadTask() != null){
				insertTasksAfterCurrentTask(this.importDialog.getLoadTask());				
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
}

