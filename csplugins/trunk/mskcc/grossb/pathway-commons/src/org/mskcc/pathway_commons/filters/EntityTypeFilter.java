package org.mskcc.pathway_commons.filters;

import org.mskcc.pathway_commons.schemas.summary_response.RecordType;
import org.mskcc.pathway_commons.util.BioPaxEntityTypeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * EntityType Filter.
 *
 * @author Ethan Cerami
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
     * @param recordList List of RecordType Objects.
     * @return List of RecordType Objects.
     */
    public List<RecordType> filter(List<RecordType> recordList) {
        BioPaxEntityTypeMap bpMap = BioPaxEntityTypeMap.getInstance();
        ArrayList<RecordType> passedList = new ArrayList<RecordType>();
        for (RecordType record : recordList) {
            String type = record.getType();
            if (type != null) {
                if (bpMap.containsKey(type)) {
                    type = (String) bpMap.get(type);
                }
                if (entityTypeSet.contains(type)) {
                    passedList.add(record);
                }
            }
        }
        return passedList;
    }
}