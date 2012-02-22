package org.cytoscape.cpathsquared.internal.filters;

import java.util.List;

import cpath.service.jaxb.SearchHit;

/**
 * Filter interface.
 */
public interface Filter {

    /**
     * Filters the record list.  Those items which pass the filter
     * are included in the returned list.
     *
     * @param recordList List of SearchHit Objects.
     * @return
     */
    public List<SearchHit> filter (List<SearchHit> recordList);
}