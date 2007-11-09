package org.mskcc.pathway_commons.filters;

import org.mskcc.pathway_commons.schemas.summary_response.DataSourceType;
import org.mskcc.pathway_commons.schemas.summary_response.RecordType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Data Source Filter.
 *
 * @author Ethan Cerami
 */
public class DataSourceFilter implements Filter {
    Set<String> dataSourceSet;

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
     * @param recordList List of RecordType Objects.
     * @return List of RecordType Objects.
     */
    public List<RecordType> filter(List<RecordType> recordList) {
        ArrayList<RecordType> passedList = new ArrayList<RecordType>();
        for (RecordType record : recordList) {
            DataSourceType dataSource = record.getDataSource();
            if (dataSource != null) {
                String dataSourceName = dataSource.getName();
                if (dataSourceName != null) {
                    if (dataSourceSet.contains(dataSourceName)) {
                        passedList.add(record);
                    }
                }
            }
        }
        return passedList;
    }
}
