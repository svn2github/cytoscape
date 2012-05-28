package org.cytoscape.cpathsquared.internal.filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cpath.service.jaxb.SearchHit;

public final class OrganismFilter implements Filter {
    final Set<String> organismsSet;

    
    public OrganismFilter(Set<String> organismsSet) {
        this.organismsSet = organismsSet;
    }


	public List<SearchHit> filter(List<SearchHit> recordList) {
		ArrayList<SearchHit> passedList = new ArrayList<SearchHit>();
		for (SearchHit record : recordList) {
			if (!record.getOrganism().isEmpty()) 
			{
				//copy organisms to a new set
				Set<String> o = new HashSet<String>(record.getOrganism());
				o.retainAll(organismsSet); //keep two sets intersection
				if (!o.isEmpty()) {
					passedList.add(record);
				}
			} else {
				passedList.add(record);
			}
		}
		return passedList;
	}
}
