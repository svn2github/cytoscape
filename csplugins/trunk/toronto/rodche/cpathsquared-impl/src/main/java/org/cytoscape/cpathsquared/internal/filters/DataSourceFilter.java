package org.cytoscape.cpathsquared.internal.filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cpath.service.jaxb.SearchHit;

public final class DataSourceFilter implements Filter {
    final Set<String> dataSourceSet;

    
    public DataSourceFilter(Set<String> dataSourceSet) {
        this.dataSourceSet = dataSourceSet;
    }


	public List<SearchHit> filter(List<SearchHit> recordList) {
		ArrayList<SearchHit> passedList = new ArrayList<SearchHit>();
		for (SearchHit record : recordList) {
			if (!record.getDataSource().isEmpty()) 
			{
				//copy datasources to a new set
				Set<String> ds = new HashSet<String>(record.getDataSource());
				ds.retainAll(dataSourceSet); //keep two sets intersection
				if (!ds.isEmpty()) {
					passedList.add(record);
				}
			} else {
				passedList.add(record);
			}
		}
		return passedList;
	}
}
