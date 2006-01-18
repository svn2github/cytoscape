// ConditionsVsPathwaysTable
//------------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
//---------------------------------------------------------------------------------------
import giny.model.Node;

import java.awt.BorderLayout;
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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import csplugins.jActiveModules.ActivePathViewer;
import csplugins.jActiveModules.Component;
import cytoscape.CyNetwork;

//---------------------------------------------------------------------------------------
public class ConditionsVsPathwaysTable extends JDialog {
	JTable table;
	//JPanel topTable, bottomTable;
	ActivePathViewer pathViewer;
	csplugins.jActiveModules.Component[] activePaths;
	String[] conditionNames;
	CyNetwork cyNetwork;

	// -----------------------------------------------------------------------------------------
	public ConditionsVsPathwaysTable(Frame parentFrame, CyNetwork cyNetwork,
			String[] conditionNames,
			csplugins.jActiveModules.Component[] activePaths,
			ActivePathViewer pathViewer) {
		super(parentFrame, false);
		this.cyNetwork = cyNetwork;
		this.activePaths = activePaths;
		this.conditionNames = conditionNames;
		this.pathViewer = pathViewer;
		init(parentFrame, pathViewer);
	} // ConditionsVsPathwaysTable ctor

	private void init(Frame parentFrame, ActivePathViewer pathViewer) {

		setTitle("Conditions vs. Pathways");

		JPanel mainPanel = new JPanel();
		 table = new JTable(new ActivePathsTableModel(activePaths,
				conditionNames));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Boolean.class, new ColorRenderer());
		JScrollPane scrollpane = new JScrollPane(table);
		//mainPanel.add(scrollpane);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				System.err.println("Mouse pressed");
				ConditionsVsPathwaysTable.this.pathViewer.displayPath(
						activePaths[table.getSelectedRow()], "");
			}
		});
		mainPanel.setLayout(new BorderLayout());
//		JPanel tablePanel = new JPanel();
//		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
//
//		topTable = new ConditionsVsPathwaysTopTable(activePaths);
//		tablePanel.add(topTable);
//		bottomTable = new ConditionsVsPathwaysBottomTable(conditionNames,
//				activePaths, pathViewer);
//		tablePanel.add(bottomTable);
//		JScrollPane scrollPane = new JScrollPane(tablePanel);
		mainPanel.add(scrollpane, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ConditionsVsPathwaysTable.this.saveState();
			}
		});
		JButton dismissButton = new JButton("Dismiss");
		dismissButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				ConditionsVsPathwaysTable.this.hide();
			}
		});
		buttonPanel.add(saveButton, BorderLayout.CENTER);
		buttonPanel.add(dismissButton, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		setContentPane(mainPanel);
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

class ActivePathsTableModel extends AbstractTableModel {
	Component[] activePaths;
	String[] columnNames;
	Boolean[][] data;
	protected static int HEADER_COLUMNS = 3;
	public ActivePathsTableModel(Component[] activePaths, String[] conditions) {
		this.activePaths = activePaths;
		columnNames = new String[HEADER_COLUMNS + conditions.length];
		columnNames[0] = "Network";
		columnNames[1] = "Size";
		columnNames[2] = "Score";
		for (int idx = 0; idx < conditions.length; idx++) {
			columnNames[HEADER_COLUMNS + idx] = conditions[idx];
		}

		data = new Boolean[activePaths.length][conditions.length];

		for (int column = 0; column < conditions.length; column++) {
			for (int row = 0; row < activePaths.length; row++) {
				csplugins.jActiveModules.Component path = activePaths[row];
				String[] conditionsForThisPath = path.getConditions();
				boolean matchedCondition = false;
				for (int cond = 0; cond < conditionsForThisPath.length; cond++) {
					String condition = conditionsForThisPath[cond];
					if (conditions[column].equalsIgnoreCase(condition)) {
						matchedCondition = true;
						break;
					}
				}
				if (matchedCondition)
					data[row][column] = new Boolean(true);
				else
					data[row][column] = new Boolean(false);
			} // for column
		}
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
		return activePaths.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		switch (columnIndex) {
			case 0 :
				return new Integer(rowIndex + 1);
			case 1 :
				return new Integer(activePaths[rowIndex].getDisplayNodes()
						.size());
			case 2 :
				return new Double(activePaths[rowIndex].getScore());
			default :
				return data[rowIndex][columnIndex - HEADER_COLUMNS];
		// return new
		// Double(activePaths[rowIndex].getDisplayScores()[columnIndex-HEADER_COLUMNS]);
		}
	}

}

class ColorRenderer extends JLabel implements TableCellRenderer {
	public ColorRenderer() {
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