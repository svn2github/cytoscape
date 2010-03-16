/**
 * 
 */
package cytoscape.data.servers.ui;

import javax.swing.table.DefaultTableModel;

/**
 * @author kono
 * 
 */
public class OntologyTableModel extends DefaultTableModel {

	// Define Column names
	private static String[] columnHeader = new String[] { "Ontology Name", "Data Source" };

	/**
	 * Constructor for the network metadata table model.
	 * 
	 * @param network
	 *            Metadata for this network will be edited.
	 */
	public OntologyTableModel() {
		super();
	}

	public void addEntry(String name, String url) {
		Object[] obj = { name, url };
		super.addRow(obj);
		fireTableDataChanged();
	}

	public String getColumnName(int col) {
		return columnHeader[col];
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnHeader.length;
	}

	/**
	 * Determine which cell is editible or not.
	 */
	public boolean isCellEditable(int row, int column) {
		return false;
	}

}
