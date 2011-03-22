package org.cytoscape.tableimport.internal;

import java.io.IOException;
import javax.swing.JPanel;
import javax.xml.bind.JAXBException;
import org.cytoscape.tableimport.internal.ui.ImportTablePanel;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesGUI;
import org.cytoscape.work.TaskMonitor;

public class ImportNetworkTableTask extends AbstractTask {

	private ImportTablePanel importDialog = null;
	
	public ImportNetworkTableTask(){	
	}

	@ProvidesGUI
	public JPanel getGUI() { 
		
		JPanel myPanel = new JPanel();

		try {
			importDialog = new ImportTablePanel(true, ImportTablePanel.NETWORK_IMPORT, null, null);
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
