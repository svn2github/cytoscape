package pingo.ui;

import java.awt.Dialog;
import java.awt.Window;
import java.io.IOException;
import java.net.URL;

import javax.swing.JDialog;

public class TermSearchGUI extends JDialog {

	private static final long serialVersionUID = -4801907830314023426L;
	
	private GOTreePanel panel;
	
	public TermSearchGUI(final Window parent, URL source) {
		super(parent, "Search Ontology Term",
				Dialog.ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		try {
			panel = new GOTreePanel(this, source);
		} catch (IOException e) {
			e.printStackTrace();
		}
		add(panel);
		pack();
	}

	public String getTerm() {
		return panel.getTerms();
	}

}
