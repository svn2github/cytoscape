package org.cytoscape.tableimport.internal;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.cytoscape.tableimport.internal.ui.ImportTextTableDialog;
import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class ImportAttributeTableTask extends AbstractTask {

	public ImportAttributeTableTask(){
		
	}
	
	public void run(TaskMonitor e) {
		ImportTextTableDialog iad;

		try {
			iad = new ImportTextTableDialog(CytoscapeServices.desktop.getJFrame(), true, ImportTextTableDialog.SIMPLE_ATTRIBUTE_IMPORT);
			iad.pack();
			iad.setLocationRelativeTo(CytoscapeServices.desktop.getJFrame());
			iad.setVisible(true);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
