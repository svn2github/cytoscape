package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.data.webservice.ui.WebServiceClientManagerDialog;
import cytoscape.util.CytoscapeAction;

public class WebServiceManagerAction extends CytoscapeAction {

	public WebServiceManagerAction() {
		super("Open Web Service Client Manager...");
		setPreferredMenu("File");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		WebServiceClientManagerDialog.showDialog();
	}

}
