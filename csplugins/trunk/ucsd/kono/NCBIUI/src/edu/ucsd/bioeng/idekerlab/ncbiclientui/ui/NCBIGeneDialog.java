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
		setTitle("NCBI Entrez Gene");
		
		try {
			JPanel panel = new NCBIGenePanel();
			
			add(new NCBIGenePanel());
			
			panel.addPropertyChangeListener(this);
			add(panel);
			pack();
			setLocationRelativeTo(Cytoscape.getDesktop());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("CLOSE")) {
			dispose();
		}
		
	}

}
