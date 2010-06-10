package edu.ucsd.bioeng.idekerlab.ncbiclient.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import cytoscape.Cytoscape;

public class NCBIGeneDialog extends JDialog implements PropertyChangeListener {
	
	private static final long serialVersionUID = -2609215983943863094L;

	public NCBIGeneDialog() {
		super(Cytoscape.getDesktop(), false);
		setTitle("NCBI Entrez Gene");
		
		try {
			final JPanel panel = new NCBIGenePanel();
			panel.addPropertyChangeListener(this);
			add(panel);
			pack();
			setLocationRelativeTo(Cytoscape.getDesktop());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("CLOSE")) {
			dispose();
		}
		
	}

}
