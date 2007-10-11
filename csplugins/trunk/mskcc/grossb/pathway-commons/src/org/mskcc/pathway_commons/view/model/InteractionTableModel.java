package org.mskcc.pathway_commons.view.model;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.ArrayList;

/**
 * Interaction Table Model.
 *
 * @author Ethan Cerami
 */
public class InteractionTableModel extends PathwayTableModel {

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
}
