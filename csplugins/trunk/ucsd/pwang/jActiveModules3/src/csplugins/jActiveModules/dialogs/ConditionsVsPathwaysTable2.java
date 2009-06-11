// ConditionsVsPathwaysTable
//------------------------------------------------------------------------------------------
// $Revision: 14177 $
// $Date: 2008-06-10 15:16:57 -0700 (Tue, 10 Jun 2008) $
// $Author: rmkelley $
//---------------------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
//---------------------------------------------------------------------------------------
import giny.model.Node;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import csplugins.jActiveModules.ActivePathViewer;
import csplugins.jActiveModules.ActivePaths;
import csplugins.jActiveModules.Component;
import csplugins.jActiveModules.ActiveModulesUI;
import cytoscape.CyNetwork;
import cytoscape.util.CyNetworkNaming;
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.groups.CyGroup;


//---------------------------------------------------------------------------------------
public class ConditionsVsPathwaysTable2 extends JPanel {
	JTable table;
	//JPanel topTable, bottomTable;
	ActivePathViewer pathViewer;
	csplugins.jActiveModules.Component[] activePaths;
	String[] conditionNames;
	CyNetwork cyNetwork;
	ActiveModulesUI parentUI;
	Vector tableData;

	// -----------------------------------------------------------------------------------------
	public ConditionsVsPathwaysTable2(CyNetwork cyNetwork,
			String[] conditionNames,
			Vector tableData,
			ActivePathViewer pathViewer,
			ActiveModulesUI parentUI) 

	{
		this.cyNetwork = cyNetwork;
		this.activePaths = activePaths;
		this.conditionNames = conditionNames;
		this.pathViewer = pathViewer;
		this.parentUI = parentUI;
		this.tableData = tableData;
		init();
	} // ConditionsVsPathwaysTable ctor

	private String[] getTableColuimnNames() {
		String[] columnNames = 	new String[3+conditionNames.length];
		
		columnNames[0] = "Network";
		columnNames[1] = "Size";
		columnNames[2] = "Score";
		
		for (int i=0; i<conditionNames.length; i++ ){
			columnNames[3+i] = conditionNames[i];
		}
		return columnNames;
	}

	private void init() {

		String[] columnNames= getTableColuimnNames();
		
		table = new JTable(new ActivePathsTableModel2(tableData,
				columnNames));
		table.setDefaultRenderer(Boolean.class, new ColorRenderer2());
		JScrollPane scrollpane = new JScrollPane(table);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
//		JPanel tablePanel = new JPanel();
//		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
//
//		topTable = new ConditionsVsPathwaysTopTable(activePaths);
//		tablePanel.add(topTable);
//		bottomTable = new ConditionsVsPathwaysBottomTable(conditionNames,
//				activePaths, pathViewer);
//		tablePanel.add(bottomTable);
//		JScrollPane scrollPane = new JScrollPane(tablePanel);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;		c.gridy = 0;
		c.weightx = 1.0;	c.weighty = 1.0;
		add(scrollpane, c);

		JPanel buttonPanel = new JPanel();
		
		
		final CyNetwork parentNetwork = Cytoscape.getCurrentNetwork();

		final JButton createNetworkButton = new JButton(new AbstractAction("Create Network")
		{
			public void actionPerformed(ActionEvent e)
			{
				for (int i=0; i< table.getSelectedRowCount(); i++) {
					int[] rows = table.getSelectedRows();

					parentNetwork.unselectAllNodes();
					parentNetwork.unselectAllEdges();

					ConditionsVsPathwaysTable2.this.pathViewer.displayPath(activePaths[rows[i]], "");

					Set nodes = parentNetwork.getSelectedNodes();
					Set edges = new HashSet();
					Iterator iterator = parentNetwork.edgesIterator();
					while (iterator.hasNext())
					{
						CyEdge edge = (CyEdge) iterator.next();
						if (nodes.contains(edge.getSource()) && nodes.contains(edge.getTarget()))
							edges.add(edge);
					}
					CyNetwork newNetwork = Cytoscape.createNetwork(nodes, edges,
						CyNetworkNaming.getSuggestedSubnetworkTitle(parentNetwork),
						parentNetwork);
					Cytoscape.createNetworkView(newNetwork, " results");
					Cytoscape.getNetworkView(newNetwork.getIdentifier())
						.setVisualStyle(Cytoscape.getNetworkView(parentNetwork.getIdentifier()).getVisualStyle().getName());					
				}
			}			
		});
		createNetworkButton.setEnabled(false);
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				//System.err.println("Mouse pressed");
				if (table.getSelectedRowCount() ==0) {
					return;
				}
				ConditionsVsPathwaysTable2.this.pathViewer.displayPath(
						activePaths[table.getSelectedRow()], "");
				createNetworkButton.setEnabled(table.getSelectedRow() != -1);
			}
		});
		JButton dismissButton = new JButton("Close");
		dismissButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
				cytoPanel.remove(ConditionsVsPathwaysTable2.this);
			}
		});

		final JButton saveButton = new JButton("Save All Results");
		saveButton.setEnabled(true);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ConditionsVsPathwaysTable2.this.saveState();
			}
		});
		

		JButton saveScoreDistributions = new JButton(new AbstractAction("Save Score Distributions")
		{
			public void actionPerformed(ActionEvent e)
			{
				parentUI.startRandomizeAndRun(cyNetwork);
			}
		});
		buttonPanel.add(dismissButton, BorderLayout.CENTER);
		buttonPanel.add(createNetworkButton, BorderLayout.CENTER);
		buttonPanel.add(saveButton, BorderLayout.CENTER);
		buttonPanel.add(saveScoreDistributions, BorderLayout.CENTER);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;		c.gridy = 1;
		c.weightx = 1.0;	c.weighty = 0.0;
		add(buttonPanel, c);
		//mainPanel.setPreferredSize(new Dimension(500, 500));
		//setSize(800, 600);

	}

	public csplugins.jActiveModules.Component[] getActivePaths() {
		return activePaths;
	}

	
	public void saveState(String filename) {
		try {
			FileWriter fileWriter = new FileWriter(filename);
			for (int i = 0; i < activePaths.length; i++) {
				StringBuffer sb = new StringBuffer();
				sb.append("#Subnetwork " + i + "\n");
				csplugins.jActiveModules.Component ap = activePaths[i];
				List nodeNames = ap.getDisplayNodes();
				String[] condNames = ap.getConditions();
				double score = ap.getScore();
				sb.append("#Score\n");
				sb.append(score + "\n");
				sb.append("#Nodes\n");
				for (int j = 0; j < nodeNames.size(); j++)
					sb.append(((Node) nodeNames.get(j)).getIdentifier() + "\n");
				sb.append("#Conditions\n");
				for (int j = 0; j < condNames.length; j++)
					sb.append(condNames[j] + "\n");
				sb.append("\n");
				fileWriter.write(sb.toString());
			}
			fileWriter.close();
		} catch (IOException ioe) {
			System.err.println("Error while writing " + filename);
			ioe.printStackTrace();
		} // catch
	} // saveState

	// --------------------------------------------------------------------------------

	public void saveState() {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showSaveDialog(null) == chooser.APPROVE_OPTION) {
			String name = chooser.getSelectedFile().toString();
			saveState(name);
		}
	}
	

} // class ConditionsVsPathwaysTable

class ActivePathsTableModel2 extends AbstractTableModel {
	//Component[] activePaths;
	String[] columnNames;
	Vector data;
	protected static int HEADER_COLUMNS = 3;
	public ActivePathsTableModel2(Vector tableData,String[] columnNames) {
		this.data = tableData;
		this.columnNames = columnNames;
	}

	public Class getColumnClass(int column) {
		switch (column) {
			case 0: return Integer.class; 
			case 1: return Integer.class; 
			case 2: return Double.class; 
			default: return Boolean.class;
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}
	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		Vector groupVector = (Vector) data.elementAt(0);
		CyGroup theGroup = (CyGroup) groupVector.elementAt(rowIndex);

		Double[] scores = (Double[]) data.elementAt(1);
		Boolean[][] values = (Boolean[][])data.elementAt(2);
		
		switch (columnIndex) {
			case 0 :
				return new Integer(rowIndex + 1);
			case 1 :
				return new Integer(theGroup.getNodes().size());
			case 2 :
				return scores[rowIndex];
			default :
				return values[rowIndex][columnIndex-3];
		}
	}

}

class ColorRenderer2 extends JLabel implements TableCellRenderer {
	public ColorRenderer2() {
		setOpaque(true);
	}

	// public java.awt.Component getTableCellRendererComponent (JTable table,
	// Object value,
	// boolean isSelected, boolean hasFocus,
	// int row, int column) {
	// float cell_value = ((Double)value).floatValue();
	// float MAX = 1;
	// float MID = .5f;
	// float MIN = 0;
	// float green_fraction =
	// Math.min(1f,Math.max(0f,(cell_value-MID)/(MAX-MID)));
	// float red_fraction =
	// Math.min(1f,Math.max(0f,(MID-cell_value)/(MID-MIN)));
	// setBackground(new Color(red_fraction,green_fraction,0f));
	// setText(String.valueOf(cell_value));
	// setForeground(Color.WHITE);
	// return this;
	// } // getTableCellRendererComponent
	public java.awt.Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		boolean cell_value = ((Boolean) value).booleanValue();
		if (cell_value) {
			setBackground(Color.RED);
		} else {
			setBackground(Color.WHITE);
		}

		setForeground(Color.WHITE);
		return this;
	} // getTableCellRendererComponent
}
