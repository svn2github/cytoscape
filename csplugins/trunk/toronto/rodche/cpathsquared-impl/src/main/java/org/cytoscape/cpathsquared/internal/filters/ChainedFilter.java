package org.cytoscape.cpathsquared.internal.filters;

import java.util.ArrayList;
import java.util.List;

import cpath.service.jaxb.SearchHit;

/**
 * Chained Filter.
 */
public class ChainedFilter implements Filter {
    private ArrayList<Filter> filterList = new ArrayList<Filter>();

    /**
     * Adds a new filter.
     * @param filter Filter Object.
     */
    public void addFilter (Filter filter) {
        filterList.add(filter);
    }

    /**
     * Filters the record list.  Those items which pass the filter
     * are included in the returned list.
     *
     * @param recordList
     * @return
     */    
    public List<SearchHit> filter(List<SearchHit> recordList) {
        for (Filter filter:  filterList) {
            recordList = filter.filter(recordList);
        }
        return recordList;
    }
}