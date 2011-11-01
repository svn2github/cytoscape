package org.cytoscape.cpathsquared.internal.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cpath.service.jaxb.SearchHit;

/**
 * Data Source Filter.
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public class DataSourceFilter implements Filter {
    private final Set<String> dataSourceSet;

    /**
     * Constructor.
     *
     * @param dataSourceSet Set of Data Sources we want to keep.
     */
    public DataSourceFilter(Set<String> dataSourceSet) {
        this.dataSourceSet = dataSourceSet;
    }

    /**
     * Filters the record list.  Those items which pass the filter
     * are included in the returned list.
     *
     * @param recordList
     * @return
     */
    public List<SearchHit> filter(List<SearchHit> recordList) {
        ArrayList<SearchHit> passedList = new ArrayList<SearchHit>();
        for (SearchHit record : recordList) {
            List<String> dataSources = record.getDataSource();
            if (dataSources != null && !dataSources.isEmpty()) {
            	dataSources.retainAll(dataSourceSet); // intersection
                if (!dataSources.isEmpty()) {
                    passedList.add(record);
                }
           }
        }
        return passedList;
    }
}
