package org.cytoscape.application.action;

import java.awt.event.ActionEvent;

import org.cytoscape.application.dialog.webservice.WebServiceClientManagerDialog;



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
