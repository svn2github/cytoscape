package org.cytoscape.cpathsquared.internal.view.model;

import org.cytoscape.cpath.service.jaxb.*;

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
