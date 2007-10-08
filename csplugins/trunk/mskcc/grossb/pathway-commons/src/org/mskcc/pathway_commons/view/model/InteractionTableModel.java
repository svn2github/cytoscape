package org.mskcc.pathway_commons.view.model;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * Interaction Table Model.
 *
 * @author Ethan Cerami
 */
public class InteractionTableModel extends DefaultTableModel {

    /**
     * Constructor.
     */
    public InteractionTableModel() {
        super();
        Vector columnNames = new Vector();
        columnNames.add("Data Source");
        columnNames.add("Num Interactions");
        //columnNames.add("Select");
        this.setColumnIdentifiers(columnNames);
    }

    /**
     * Is the specified cell editable?
     *
     * @param row row index.
     * @param col col index.
     * @return true or false.
     */
    public boolean isCellEditable(int row, int col) {
        return false;
//        if (col == 2) {
//            return true;
//        } else {
//            return false;
//        }
    }

    /**
     * Gets the column class.
     *
     * @param columnIndex column index.
     * @return Class.
     */
    public Class getColumnClass(int columnIndex) {
        return String.class;
//        if (columnIndex == 2) {
//            return Boolean.class;
//        } else {
//            return String.class;
//        }
    }
}
