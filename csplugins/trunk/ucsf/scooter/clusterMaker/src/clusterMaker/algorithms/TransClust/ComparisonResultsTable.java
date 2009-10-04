package clusterMaker.algorithms.TransClust;

import java.util.Vector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

class ComparisonResultsTable extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	Vector<String> columnNames = new Vector<String>();
	Vector<Vector<String>> data = new Vector<Vector<String>>();
	JTable t;
	public ComparisonResultsTable(Vector<String> columnNames) {
		this.columnNames = columnNames;
	}

	public void addRow(Vector<String> row){
		data.add(row);
	}

	public void initializeTable(){


		t = new JTable(data, columnNames);
		//      t.setAutoCreateRowSorter(true);


		JButton clearButton = new JButton("Close Window");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);

		JPanel clearDummyPanel = new JPanel();
		clearDummyPanel.add(clearButton);

		JScrollPane scrollpane = new JScrollPane(t);

		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		this.add(scrollpane);
		this.add(clearDummyPanel);

	}

	public void actionPerformed(ActionEvent e) {

		String c = e.getActionCommand();

		if (c.equalsIgnoreCase("clear")) {

			CytoscapeDesktop desktop = Cytoscape.getDesktop();
			CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);

			cytoPanel.remove(this);
			if (cytoPanel.getCytoPanelComponentCount() == 0) {
				cytoPanel.setState(CytoPanelState.HIDE);
			}
		}
	}
}

