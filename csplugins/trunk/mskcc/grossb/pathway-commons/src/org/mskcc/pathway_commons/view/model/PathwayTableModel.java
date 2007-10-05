package org.mskcc.pathway_commons.view.model;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * Pathway Table Model.
 *
 * @author Ethan Cerami
 */
public class PathwayTableModel extends DefaultTableModel {

    /**
     * Constructor.
     */
    public PathwayTableModel () {
        super ();
        Vector columnNames = new Vector();
        columnNames.add("Data Source");
        columnNames.add("Pathway");
        columnNames.add("Select");
        this.setColumnIdentifiers(columnNames);
    }

    /**
     * Is the specified cell editable?
     * @param row row index.
     * @param col col index.
     * @return true or false.
     */
    public boolean isCellEditable(int row, int col) {
        if (col == 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the column class.
     * @param columnIndex column index.
     * @return Class.
     */
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 2) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }
}