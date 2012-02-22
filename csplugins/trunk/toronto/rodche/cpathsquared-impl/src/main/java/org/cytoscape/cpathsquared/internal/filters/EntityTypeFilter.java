package org.cytoscape.cpathsquared.internal.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cpath.service.jaxb.SearchHit;

/**
 * EntityType Filter.
 *
 */
public class EntityTypeFilter implements Filter {
    Set<String> entityTypeSet;

    /**
     * Constructor.
     *
     * @param entityTypeSet Set of Entity Types we want to keep.
     */
    public EntityTypeFilter(Set<String> entityTypeSet) {
        this.entityTypeSet = entityTypeSet;
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
            String type = record.getBiopaxClass();
            if (type != null) {
                if (entityTypeSet.contains(type)) {
                    passedList.add(record);
                }
            }
        }
        return passedList;
    }
}