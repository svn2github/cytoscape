
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.filters;

import java.util.Vector;
import cytoscape.filters.util.FilterUtil;;

/**
 * 
  */
public class CompositeFilter implements Cloneable {
	
	private String name;
	private String description;
	private AdvancedSetting advancedSetting = new AdvancedSetting();
	private Vector<AtomicFilter> atomicFilterVect = new Vector<AtomicFilter>();

	public CompositeFilter(AdvancedSetting pAdvancedSetting, Vector<AtomicFilter> pCustomFilterVect) {
		advancedSetting = pAdvancedSetting;
		atomicFilterVect = pCustomFilterVect;
	}

	//Create an empty CompositeFilter
	public CompositeFilter() {
		advancedSetting = new AdvancedSetting();
		atomicFilterVect = new Vector<AtomicFilter>();
	}

	//Create an empty CompositeFilter
	public CompositeFilter(String pName) {
		advancedSetting = new AdvancedSetting();
		atomicFilterVect = new Vector<AtomicFilter>();
		name = pName;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String pName) {
		name = pName;
	}
		
	public String getDescription() {
		return description;
	}

	public void setDescription(String pDescription) {
		description = pDescription;
	}

	public AdvancedSetting getAdvancedSetting() {
		return advancedSetting;
	}

	public void setAdvancedSetting(AdvancedSetting pAdvancedSetting) {
		advancedSetting = pAdvancedSetting;
	}

	
	public Vector<AtomicFilter> getAtomicFilterVect()
	{
		return atomicFilterVect;
	}
	
	public void addAtomicFilter(AtomicFilter pAtomicFilter)
	{
		atomicFilterVect.add(pAtomicFilter);
	}
	
	public void removeAtomicFilter(AtomicFilter pAtomicFilter)
	{
		atomicFilterVect.remove(pAtomicFilter);
	}

	public void removeAtomicFilterAt(int index)
	{
		atomicFilterVect.remove(index);
	}
	
	/**
	 * @return the string represention of this Filter.
	 */
	public String toString()
	{
		String retStr ="Filter_name = " + name + "\n";
		retStr       += "Description = " + description + "\n";
		retStr       += advancedSetting.toString();
		for (int i=0; i<atomicFilterVect.size(); i++ ) {
			AtomicFilter theAtomicFilter = atomicFilterVect.elementAt(i);
			if (theAtomicFilter instanceof StringFilter) {
				StringFilter theStringFilter = (StringFilter)theAtomicFilter;
				try {
					retStr += "\nCustomSetting.StringFilter = " + theStringFilter.toString();					
				}
				catch (Exception e) { 
					//If StringFilter is not initialized, ignore it
				}
			}
			else if (theAtomicFilter instanceof NumericFilter) {
				NumericFilter theNumericFilter = (NumericFilter)theAtomicFilter;
				try {
					retStr += "\nCustomSetting.NumericFilter = " + theNumericFilter.toString();									
				}
				catch (Exception e) { 
					//If NumericFilter is not initialized, ignore it
				}
			}
		}
		return retStr;
	}

	/**
	 */
	public boolean equals(Object other_object) {
		if (!(other_object instanceof CompositeFilter)) {
			return false;
		}
		CompositeFilter theOtherFilter = (CompositeFilter) other_object;
		
		if (theOtherFilter.toString().equalsIgnoreCase(this.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * CompositeFilter may be cloned.
	 */
	public Object clone() {
		String filterStr = this.toString();
		Vector<String> filterStrVect = new Vector<String>();
		String[] filterStrArray = filterStr.split("\n");
		for (int i=0; i<filterStrArray.length; i++ ) {
			filterStrVect.add(filterStrArray[i]);
		}
		return FilterUtil.createFilterFromString(filterStrVect);
	}	

} // COmpositeFilter
