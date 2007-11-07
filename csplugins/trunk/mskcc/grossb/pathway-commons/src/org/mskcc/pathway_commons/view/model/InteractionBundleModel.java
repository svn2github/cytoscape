package org.mskcc.pathway_commons.view.model;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Interaction Bundle Model.
 *
 * @author Ethan Cerami
 */
public class InteractionBundleModel extends Observable {
    private int numInteractions;

    /**
     * Constructor.
     */
    public InteractionBundleModel() {}

    public int getNumInteractions() {
        return numInteractions;
    }

    public void setNumInteractions(int numInteractions) {
        this.numInteractions = numInteractions;
        this.setChanged();
        this.notifyObservers();
    }
}
