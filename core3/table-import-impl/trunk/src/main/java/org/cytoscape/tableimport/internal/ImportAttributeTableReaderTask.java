package org.cytoscape.tableimport.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JPanel;
import javax.xml.bind.JAXBException;
import org.cytoscape.tableimport.internal.ui.ImportTextTableDialog;
import org.cytoscape.work.ProvidesGUI;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.model.CyTable;

public class ImportAttributeTableReaderTask implements CyTableReader {

	private ImportTextTableDialog importDialog = null;
	private TaskIterator taskIterator = new TaskIterator();
	
	public ImportAttributeTableReaderTask(InputStream is){
		System.out.println("\n\nEntering ImportAttributeTableReaderTask constructor ...\n");
	}

	@ProvidesGUI
	public JPanel getGUI() { 
		
		System.out.println("\tEntering ImportAttributeTableTask.getGUI() ...");
		
		JPanel myPanel = new JPanel();

		try {
			if (this.importDialog == null){
				this.importDialog = new ImportTextTableDialog(true, ImportTextTableDialog.SIMPLE_ATTRIBUTE_IMPORT);				
			}
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
	
	
	public void run(TaskMonitor e) {
		System.out.println("\tEntering ImportAttributeTableTask.run()...");
		try {
			this.importDialog.importButtonActionPerformed();
			if (this.importDialog.getLoadTask() != null){
				//insertTasksAfterCurrentTask(this.importDialog.getLoadTask());
				taskIterator.insertTasksAfter(this, this.importDialog.getLoadTask());
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public void cancel(){
	}
	
	public CyTable[] getCyTables(){
		return null;
	}
}
