package org.cytoscape.tableimport.internal;

import java.io.IOException;
import java.io.InputStream;
import javax.swing.JPanel;
import javax.xml.bind.JAXBException;
import org.cytoscape.tableimport.internal.ui.ImportTablePanel;
import org.cytoscape.work.ProvidesGUI;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.model.CyTable;
import org.cytoscape.tableimport.internal.ui.ImportAttributeTableTask;

public class ImportAttributeTableReaderTask implements CyTableReader {

	private ImportTablePanel importDialog = null;
	private InputStream is= null;
	private String fileType = null;
	
	
	public ImportAttributeTableReaderTask(InputStream is, String fileType){
		this.is = is;
		this.fileType = fileType;
	}

	@ProvidesGUI
	public JPanel getGUI() { 

		JPanel myPanel = new JPanel();

		try {
			if (this.importDialog == null){
				this.importDialog = new ImportTablePanel(ImportTablePanel.SIMPLE_ATTRIBUTE_IMPORT, is, this.fileType);				
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
		try {
			this.importDialog.importButtonActionPerformed();
			if (this.importDialog.getLoadTask() != null){
				 this.importDialog.getLoadTask().run(e);
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public void cancel(){
	}
	
	public CyTable[] getCyTables(){
		if (this.importDialog.getLoadTask() instanceof ImportAttributeTableTask){
			ImportAttributeTableTask importTask = (ImportAttributeTableTask) this.importDialog.getLoadTask();
			return importTask.getCyTables();
		}
		return null;
	}
}
