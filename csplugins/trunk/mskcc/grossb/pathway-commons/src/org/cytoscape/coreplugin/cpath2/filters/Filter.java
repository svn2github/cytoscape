package org.cytoscape.coreplugin.cpath2.filters;

import org.cytoscape.coreplugin.cpath2.schemas.summary_response.BasicRecordType;

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
    public List<BasicRecordType> filter (List<BasicRecordType> recordList);
}