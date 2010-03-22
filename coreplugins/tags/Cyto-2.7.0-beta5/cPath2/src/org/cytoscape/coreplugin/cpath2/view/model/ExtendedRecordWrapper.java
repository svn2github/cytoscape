package org.cytoscape.coreplugin.cpath2.view.model;

import org.cytoscape.coreplugin.cpath2.schemas.search_response.ExtendedRecordType;

/**
 * Wrapper for ExtendedRecordType.
 *
 * @author Ethan Cerami.
 */
public class ExtendedRecordWrapper {
    private ExtendedRecordType record;

    public ExtendedRecordWrapper (ExtendedRecordType record) {
        this.record = record;
    }

    public ExtendedRecordType getRecord() {
        return this.record;
    }

    public String toString() {
        return record.getName();
    }
}
