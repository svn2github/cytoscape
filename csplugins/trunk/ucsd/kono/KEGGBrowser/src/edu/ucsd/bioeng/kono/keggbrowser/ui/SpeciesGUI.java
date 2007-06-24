package edu.ucsd.bioeng.kono.keggbrowser.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import cytoscape.Cytoscape;

public class SpeciesGUI extends JDialog implements ActionListener , HyperlinkListener {

	private static SpeciesGUI dialog = new SpeciesGUI();
	
	private JEditorPane html;

	private static String targetURL;
	
	public SpeciesGUI() {
		super(Cytoscape.getDesktop(), true);
		try {
			initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initialize() throws IOException {
		this.setTitle("Please select an organism");
		html = new JEditorPane(
				"http://www.genome.jp/kegg/catalog/org_list.html");
		
		html.addHyperlinkListener(this);
		html.setEditable(false);
		this.getContentPane().add(new JScrollPane(html));
		this.setSize(900, 500);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
			return;
		targetURL = e.getURL().toString();
		
		System.out.println("URL EVENT: " + targetURL);
		dispose();
	}

	public static String showDialog() {
		dialog.setVisible(true);
		
		return targetURL;
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
