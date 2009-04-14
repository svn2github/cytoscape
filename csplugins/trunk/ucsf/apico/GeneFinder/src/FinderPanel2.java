package browser;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.FlagEvent;
import cytoscape.data.FlagEventListener;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import filter.model.Filter;
import filter.model.FilterManager;

public class FinderPanel2 extends JPanel implements PropertyChangeListener,
		ActionListener, FlagEventListener {
	public static int NODES = 0;
	public static int EDGES = 1;
	int graphObjectType;
	JComboBox mycomboBox;
	JButton mybutton;
	DataTableModel2 tableModel;
	Map titleIdMap;

	CyNetwork current_network;

	public FinderPanel2(DataTableModel2 tableModel, int graphObjectType) {

		this.tableModel = tableModel;
		this.graphObjectType = graphObjectType;

		titleIdMap = new HashMap();
		String [] items = { "Entrez Gene", "Ensembl", "Affy", "UniProt", "Other" };
		mycomboBox = new JComboBox(items);
		mycomboBox.setEditable(true);
		
		mybutton = new JButton("Search");
	    button.addActionListener(new ActionListener(  )) {
	      public void actionPerformed(ActionEvent ae) {
	    	  System.out.println("Searching for Gene in Database");
	      }
	        
	      }

		setBorder(new TitledBorder("Gene Finder"));
		add(new JLabel("Gene ID System: "));
		add(mycomboBox);
		add(mybutton);
		
		mycomboBox.addActionListener(this);

	}
	
	public void onFlagEvent(FlagEvent event) {
		System.out.println("Searching for Gene in Database");
	}


	public void actionPerformed(ActionEvent e) {
		System.out.println("Searching for Gene in Database");
	}



	public void propertyChange(PropertyChangeEvent e) {
		System.out.println("Searching for Gene in Database");
		}



}
