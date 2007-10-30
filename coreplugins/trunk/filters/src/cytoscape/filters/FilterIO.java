package cytoscape.filters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import cytoscape.filters.util.FilterUtil;

public class FilterIO {

	// Read the filter property file and construct the filter objects
	// based on the string representation of each filter
	public Vector<CompositeFilter> getFilterVectFromPropFile(File pPropFile) {
		Vector<CompositeFilter> retVect = new Vector<CompositeFilter>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(pPropFile));

			String oneLine = in.readLine();

			if (oneLine == null) {
				return null;
			}
			double filterVersion = 0.0;
			if (oneLine.trim().startsWith("FilterVersion")) {
				String versionStr = oneLine.trim().substring(14);
				filterVersion = Double.valueOf(versionStr);
			}
			
			// Ignore filters from the old version
			if (filterVersion <0.2) {
				return null;
			}			
			
			while (oneLine != null) {
				// ignore comment, empty line or the version line
				if (oneLine.startsWith("#") || oneLine.trim().equals("")||oneLine.startsWith("FilterVersion")) {
					oneLine = in.readLine();
					continue;
				}

				if (oneLine.trim().startsWith("<Composite>")) {
					Vector<String> filterStrVect = new Vector<String>();
					while ((oneLine = in.readLine()) != null) {
						if (oneLine.trim().startsWith("</Composite>")) {
							break;
						}
						filterStrVect.add(oneLine);
					} // inner while loop

					CompositeFilter aFilter = getFilterFromStrVect(filterStrVect); 
	
					if (aFilter != null) {
						retVect.add(aFilter);
					}
					
					//System.out.println("aFilter = \n" + aFilter.toString());
				}
				
				oneLine = in.readLine();
			} // while loop

			in.close();
		} catch (Exception ex) {
			System.out.println("Filter Read error");
			ex.printStackTrace();
		}
		
		return retVect;
	}

	private AdvancedSetting getAdvancedSettingFromStrVect(Vector<String> pAdvSettingStrVect) {
		AdvancedSetting advSetting = new AdvancedSetting();
		String line = null;
		for (int i=0; i<pAdvSettingStrVect.size(); i++ ) {
			line = pAdvSettingStrVect.elementAt(i);
			if (line.startsWith("scope.global=true")) {
				advSetting.setGlobal(true);
			}
			if (line.startsWith("scope.global=false")) {
				advSetting.setGlobal(false);
			}
			if (line.startsWith("scope.session=true")) {
				advSetting.setSession(true);
			}
			if (line.startsWith("scope.session=false")) {
				advSetting.setSession(false);
			}
			if (line.startsWith("selection.node=true")) {
				advSetting.setNode(true);
			}
			if (line.startsWith("selection.node=false")) {
				advSetting.setNode(false);
			}
			if (line.startsWith("selection.edge=true")) {
				advSetting.setEdge(true);
			}
			if (line.startsWith("selection.edge=false")) {
				advSetting.setEdge(false);
			}
			if (line.startsWith("edge.source=true")) {
				advSetting.setSource(true);
			}
			if (line.startsWith("edge.source=false")) {
				advSetting.setSource(false);
			}
			if (line.startsWith("edge.target=true")) {
				advSetting.setTarget(true);
			}
			if (line.startsWith("edge.target=false")) {
				advSetting.setTarget(false);
			}
			if (line.startsWith("Relation=AND")) {
				advSetting.setRelation(Relation.AND);
			}
			if (line.startsWith("Relation=OR")) {
				advSetting.setRelation(Relation.OR);
			}
		}

		return advSetting;
	}
	
	
	private CompositeFilter getFilterFromStrVect(Vector<String> pFilterStrVect){

		Vector<String> advSettingStrVect = new Vector<String>();
		Vector<String> filterStrVect = new Vector<String>();

		String line = null;
		// Seperate AdvancedSetting from the rest
		int startIndex = -1, endIndex = -1;
		for (int i=0; i< pFilterStrVect.size(); i++) {
			line = (String) pFilterStrVect.elementAt(i);

			if (line.startsWith("<AdvancedSetting>")) {
				startIndex = i;
			}
			if (line.startsWith("</AdvancedSetting>")) {
				endIndex = i;
				break;
			}
		}
		advSettingStrVect.addAll(pFilterStrVect.subList(startIndex+1, endIndex));

		filterStrVect.addAll(pFilterStrVect.subList(0, startIndex));
		filterStrVect.addAll(pFilterStrVect.subList(endIndex+1, pFilterStrVect.size()));
				
		CompositeFilter retFilter = new CompositeFilter();
		retFilter.setAdvancedSetting(getAdvancedSettingFromStrVect(advSettingStrVect));
		
		for (int i=0; i<filterStrVect.size(); i++ ) {
			line = filterStrVect.elementAt(i) ;

			if (line.startsWith("name=")) {
				String name =line.substring(5);
				retFilter.setName(name);
			}
			if (line.startsWith("Negation=true")) {
				retFilter.setNegation(true);
			}
			if (line.startsWith("Negation=false")) {
				retFilter.setNegation(false);
			}
			if (line.startsWith("StringFilter=")) {
				String[] _values = line.substring(13).split(":");
				//controllingAttribute+":" + negation+ ":"+searchStr+":"+index_type;
				StringFilter _strFilter = new StringFilter();
				_strFilter.setParent(retFilter);
				_strFilter.setControllingAttribute(_values[0]);
				_strFilter.setNegation((new Boolean(_values[1])).booleanValue());				
				_strFilter.setSearchStr(_values[2]);
				_strFilter.setIndexType((new Integer(_values[3])).intValue());
				retFilter.addChild(_strFilter);
			}

			if (line.startsWith("NumericFilter=")) {
				String[] _values = line.substring(14).split(":");
				//controllingAttribute + ":" + negation+ ":"+lowBound+":" + highBound+ ":"+index_type;

				// Determine data type of the attribute
				String dataType = "int";
				if (_values[2].indexOf(".")>=0 ||_values[2].indexOf("E")>=0 ||
						_values[3].indexOf(".")>=0 ||_values[3].indexOf("E")>=0) {
					dataType = "double";
				}
				
				if (dataType.equalsIgnoreCase("double")) {
					NumericFilter<Double> _numFilter = new NumericFilter<Double>();
					_numFilter.setParent(retFilter);					
					_numFilter.setControllingAttribute(_values[0]);
					_numFilter.setNegation((new Boolean(_values[1])).booleanValue());
					_numFilter.setLowBound(Double.valueOf(_values[2]));
					_numFilter.setHighBound(Double.valueOf(_values[3]));
					_numFilter.setIndexType((new Integer(_values[4])).intValue());					
					retFilter.addChild(_numFilter);
				}
				else { // dataType = "int"
					NumericFilter<Integer> _numFilter = new NumericFilter<Integer>();
					_numFilter.setParent(retFilter);
					_numFilter.setControllingAttribute(_values[0]);
					_numFilter.setNegation((new Boolean(_values[1])).booleanValue());
					_numFilter.setLowBound(Integer.valueOf(_values[2]));
					_numFilter.setHighBound(Integer.valueOf(_values[3]));
					_numFilter.setIndexType((new Integer(_values[4])).intValue());
					retFilter.addChild(_numFilter);
				}
			}
			if (line.startsWith("CompositeFilter=")) {
				//e.g. CompositeFilter=AAA:true
				String[] _values = line.substring(16).split(":");

				String name = _values[0].trim();
				String notValue = _values[1].trim();
				
				// get the reference CompositeFilter
				CompositeFilter cmpFilter = null;
				for (int j=0; j< FilterPlugin.getAllFilterVect().size(); j++) {
					if (FilterPlugin.getAllFilterVect().elementAt(j).getName().equalsIgnoreCase(name)) {
						cmpFilter = FilterPlugin.getAllFilterVect().elementAt(j);
						break;
					}
				}
				if (cmpFilter !=null) {
					retFilter.addChild(cmpFilter, (new Boolean(notValue)).booleanValue());					
				}
			}
			if (line.startsWith("TopologyFilter=")) {
				//e.g. TopologyFilter=AAA:true
				String[] _values = line.substring(15).split(":");

				String name = _values[0].trim();
				String notValue = _values[1].trim();
				
				// get the reference TopologyFilter
				TopologyFilter topoFilter = null;
				for (int j=0; j< FilterPlugin.getAllFilterVect().size(); j++) {
					if (FilterPlugin.getAllFilterVect().elementAt(j).getName().equalsIgnoreCase(name)) {
						topoFilter = (TopologyFilter) FilterPlugin.getAllFilterVect().elementAt(j);
						break;
					}
				}
				if (topoFilter !=null) {
					retFilter.addChild(topoFilter, (new Boolean(notValue)).booleanValue());					
				}
			}
		}
		
		return retFilter;
	}
	
	
	public void saveGlobalPropFile(File pPropFile, Vector<CompositeFilter> pAllFilterVect) {
		System.out.println("FilterIO.saveGlobalPropFile() ...");
		System.out.println("\tpAllFilterVect.size() =" + pAllFilterVect.size());
		
		// Because one filter may depend on the other, CompositeFilters must 
		// be sorted in the order of depthLevel before save
		Object [] sortedFilters = getSortedCompositeFilter(pAllFilterVect);
		Object[] globalFilters = getGlobalFilters(sortedFilters);
		
		if (globalFilters == null || globalFilters.length == 0) {
			return;
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(pPropFile));
			writer.write("FilterVersion=0.2\n");			
			
			for (int i = 0; i < globalFilters.length; i++) {
				CompositeFilter theFilter = (CompositeFilter) globalFilters[i];
				writer.write(theFilter.toString());
				writer.newLine();
			}
			writer.close();
		} catch (Exception ex) {
			System.out.println("Global filter Write error");
			ex.printStackTrace();
		}
	}
	
	
	private Object[] getGlobalFilters(Object[] pFilters) {
		if (pFilters == null || pFilters.length == 0) {
			return null;
		}
		ArrayList<CompositeFilter> globalFilterList = new ArrayList<CompositeFilter>();
		for (int i = 0; i < pFilters.length; i++) {
			CompositeFilter theFilter = (CompositeFilter) pFilters[i];
			AdvancedSetting advSetting = theFilter.getAdvancedSetting();
			if (advSetting.isGlobalChecked()) {
				globalFilterList.add(theFilter);
			}
		}
		return globalFilterList.toArray();
	}
	
	public void saveSessionStateFiles(List<File> pFileList, Vector<CompositeFilter> pAllFilterVect){
		// Create an empty file on system temp directory
		String tmpDir = System.getProperty("java.io.tmpdir");
		System.out.println("java.io.tmpdir: [" + tmpDir + "]");

		File session_filter_file = new File(tmpDir, "session_filters.props");

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					session_filter_file));

			for (int i = 0; i < pAllFilterVect.size(); i++) {
				CompositeFilter theFilter = (CompositeFilter) pAllFilterVect.elementAt(i);
				AdvancedSetting advSetting = theFilter.getAdvancedSetting();

				if (advSetting.isSessionChecked()) {
					writer.write(theFilter.toString());
					writer.newLine();
				}
			}

			writer.close();
		} catch (Exception ex) {
			System.out.println("Session filter Write error");
			ex.printStackTrace();
		}

		pFileList.add(session_filter_file);

	}
	
	
	public void restoreSessionState(List<File> pStateFileList, Vector<CompositeFilter> pAllFilterVect) {
		if ((pStateFileList == null) || (pStateFileList.size() == 0)) {
			System.out.println("\tNo previous filter state to restore.");

			return;
		}

		try {
			File session_filter_file = pStateFileList.get(0);

			Vector<CompositeFilter> sessionFilterVect = getFilterVectFromPropFile(session_filter_file);

			System.out.println("\tLoad " + sessionFilterVect.size()
					+ " session filters");

			int currentFilterCount = pAllFilterVect.size();
			System.out.println("\tcurrentFilterCount=" + currentFilterCount);

			for (int i = 0; i < sessionFilterVect.size(); i++) {
				// Exclude duplicated filter
				boolean isDuplicated = false;

				for (int j = 0; j < currentFilterCount; j++) {
					if (sessionFilterVect.elementAt(i).toString()
							.equalsIgnoreCase(
									pAllFilterVect.elementAt(j).toString())) {
						isDuplicated = true;

						break;
					}
				}

				if (isDuplicated) {
					continue;
				}

				pAllFilterVect.add(sessionFilterVect.elementAt(i));
			}

			System.out.println("\t"
							+ ((currentFilterCount + sessionFilterVect.size()) - pAllFilterVect.size())
							+ " duplicated filters are not added");
		} catch (Throwable ee) {
			System.out.println("Failed to restore Filters from session!");
		}

	}

	// Determine the nest level of the given CompositeFilter
	private int getTreeDepth(CompositeFilter pFilter, int pDepthLevel){
		List<CyFilter> childrenList = pFilter.getChildren();
		List<CompositeFilter> theList = new ArrayList<CompositeFilter>();
		
		// Find all the child compositeFilter
		for (int i=0; i<childrenList.size(); i++) {
			if (childrenList.get(i) instanceof CompositeFilter) {
				theList.add((CompositeFilter)childrenList.get(i));
			}
		}
		
		if (theList.size() == 0) {
			return pDepthLevel;
		}
		
		int [] depths = new int[theList.size()];
		for (int j=0; j<theList.size(); j++) {
			depths[j] = getTreeDepth((CompositeFilter)theList.get(j), pDepthLevel+1);
		}
		
		java.util.Arrays.sort(depths);
		
		return depths[depths.length-1];		
	}
	
	public Object [] getSortedCompositeFilter(Vector pAllFilterVect) {
		if (pAllFilterVect == null || pAllFilterVect.size() == 0) {
			return null;
		}
		
		Object[] sortedFilters = pAllFilterVect.toArray();
		Arrays.sort(sortedFilters, (new CompositeFilterCmp<CompositeFilter>()));
		return sortedFilters;
	}
	
	class CompositeFilterCmp<T> implements Comparator {
		
		public int compare(Object o1, Object o2){
			int depth1 = getTreeDepth((CompositeFilter) o1, 0);
			int depth2 = getTreeDepth((CompositeFilter) o2, 0);

			if (depth1 > depth2) {
				return 1;
			}
			if (depth1 == depth2) {
				return 0;
			}
			return -1;//depth1 < depth2
		}
		
		public boolean equals(Object obj) {
			return false;
		}
		 
	}
}

