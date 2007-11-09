package org.mskcc.pathway_commons.filters;

import org.mskcc.pathway_commons.schemas.summary_response.RecordType;

import java.util.List;

/**
 * Filter interface.
 *
 * @author Ethan Cerami
 */
public interface Filter {

    /**
     * Filters the record list.  Those items which pass the filter
     * are included in the returned list.
     *
     * @param recordList List of RecordType Objects.
     * @return List of RecordType Objects. 
     */
    public List<RecordType> filter (List<RecordType> recordList);
}