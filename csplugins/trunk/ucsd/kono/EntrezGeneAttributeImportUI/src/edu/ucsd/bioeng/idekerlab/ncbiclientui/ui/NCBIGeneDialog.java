package edu.ucsd.bioeng.idekerlab.ncbiclientui.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JPanel;

import cytoscape.Cytoscape;

public class NCBIGeneDialog extends JDialog implements PropertyChangeListener {
	
	public NCBIGeneDialog() {
		super(Cytoscape.getDesktop(), false);
		setTitle("NCBI Web Service Client");
		
		try {
			JPanel panel = new NCBIGenePanel();
			
			add(new NCBIGenePanel());
			
			panel.addPropertyChangeListener(this);
			add(panel);
			pack();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

}
