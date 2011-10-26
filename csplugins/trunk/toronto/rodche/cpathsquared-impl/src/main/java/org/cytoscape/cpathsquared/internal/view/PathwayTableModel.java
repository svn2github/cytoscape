package org.cytoscape.cpathsquared.internal.view;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.ArrayList;

/**
 * Pathway Table Model.
 *
 * @author Ethan Cerami
 */
public class PathwayTableModel extends DefaultTableModel {
    ArrayList<String> internalIdList = new ArrayList<String>();

    /**
     * Constructor.
     */
    public PathwayTableModel() {
        super();
        Vector columnNames = new Vector();
        columnNames.add("Pathway");
        columnNames.add("Data Source");
        this.setColumnIdentifiers(columnNames);
    }

    public void resetInternalIds (int size) {
        internalIdList = new ArrayList<String>(size);
    }

    public void setInternalId (int index, String internalId) {
        internalIdList.add(index, internalId);
    }

    public String getInternalId (int index) {
        return internalIdList.get(index);
    }
}