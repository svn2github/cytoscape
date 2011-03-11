package org.cytoscape.tableimport.internal;

import java.io.IOException;
import javax.swing.JPanel;
import javax.xml.bind.JAXBException;
import org.cytoscape.tableimport.internal.ui.ImportTextTableDialog;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesGUI;
import org.cytoscape.work.TaskMonitor;

public class ImportAttributeTableTask extends AbstractTask {

	private ImportTextTableDialog importDialog = null;
	
	public ImportAttributeTableTask(){
		
	}

	@ProvidesGUI
	public JPanel getGUI() { 
		
		JPanel myPanel = new JPanel();

		try {
			this.importDialog = new ImportTextTableDialog(true, ImportTextTableDialog.SIMPLE_ATTRIBUTE_IMPORT);
			
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (importDialog != null){
			myPanel.add(importDialog);
		}
		
		return myPanel; 
	}
	
	
	public void run(TaskMonitor e) {
		
		try {
			this.importDialog.importButtonActionPerformed();
			if (this.importDialog.getLoadTask() != null){
				insertTasksAfterCurrentTask(this.importDialog.getLoadTask());				
			}
		}
		catch (Exception ex){
		}
	}	
}
