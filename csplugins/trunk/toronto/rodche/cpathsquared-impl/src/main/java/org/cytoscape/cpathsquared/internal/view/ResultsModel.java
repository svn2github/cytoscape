package org.cytoscape.cpathsquared.internal.view;

import java.util.Observable;

/**
 * Contains information regarding the currently selected set of interaction bundles.
 *
 */
public class ResultsModel extends Observable {
    private RecordList recordList;

    /**
     * Sets the SummaryResponse Object.
     * @param recordList Record List.
     */
    public void setRecordList (RecordList recordList) {
        this.recordList = recordList;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Gets the Record List.
     * @return RecordList Object.
     */
    public RecordList getRecordList() {
        return recordList;
    }

}