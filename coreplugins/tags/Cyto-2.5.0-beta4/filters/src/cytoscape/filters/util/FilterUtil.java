package cytoscape.filters.util;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.filters.CompositeFilter;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import cytoscape.filters.StringFilter;
import cytoscape.filters.NumericFilter;
import cytoscape.filters.view.FilterMainPanel;
import cytoscape.quickfind.util.QuickFind;
import cytoscape.util.CytoscapeAction;
import cytoscape.data.CyAttributes;
import cytoscape.filters.AdvancedSetting;

public class FilterUtil {
	
	public static CyAttributes getCyAttributes(String pIndexType) {
		CyAttributes attributes = null;

		if (pIndexType.equalsIgnoreCase("node")) {
			attributes = Cytoscape.getNodeAttributes();
		} else if (pIndexType.equalsIgnoreCase("edge")) {
			attributes = Cytoscape.getEdgeAttributes();
		}
		else { //QuickFind.INDEX_ALL_ATTRIBUTES
			attributes = null;
		}

		return attributes;
	}
	
	public static boolean isFilterNameDuplicated(Vector<CompositeFilter> pFilterVect, String pFilterName) {
		
		if (pFilterVect == null || pFilterVect.size() == 0)
			return false;
		
		for (int i=0; i<pFilterVect.size(); i++) {
			CompositeFilter theFilter = (CompositeFilter) pFilterVect.elementAt(i);
			if (pFilterName.equalsIgnoreCase(theFilter.getName().trim())) {
				return true;
			}
		}
		return false;
	}
	
	
	public static CompositeFilter createFilterFromString(Vector pFilterStrVect) {
		if (pFilterStrVect==null || pFilterStrVect.size() == 0) {
			return null;
		}
		
		//System.out.println("createFilterFromString()\npFilterStrVect.toString() =\n" + pFilterStrVect.toString());
		
		CompositeFilter theFilter = new CompositeFilter();
		String tmpStr;
		String [] tmpStrArray = null;
		
		//name
		tmpStr = (String) pFilterStrVect.elementAt(0);		
		tmpStrArray = tmpStr.split("=");
		theFilter.setName(tmpStrArray[1].trim());
		//description
		tmpStr = (String) pFilterStrVect.elementAt(1);		
		tmpStrArray = tmpStr.split("=");
		if (tmpStrArray[1].trim().equalsIgnoreCase("null")){
			theFilter.setDescription(null);	
		}
		else {
			theFilter.setDescription(tmpStrArray[1].trim());			
		}
		
		//AdvancedSettiing
		AdvancedSetting advSetting = theFilter.getAdvancedSetting();

		//global
		tmpStr = (String) pFilterStrVect.elementAt(2);		
		tmpStrArray = tmpStr.split("=");	
		advSetting.setGlobal((new Boolean(tmpStrArray[1].trim())).booleanValue());			
		//session
		tmpStr = (String) pFilterStrVect.elementAt(3);		
		tmpStrArray = tmpStr.split("=");		
		advSetting.setSession((new Boolean(tmpStrArray[1].trim())).booleanValue());
		//node
		tmpStr = (String) pFilterStrVect.elementAt(4);		
		tmpStrArray = tmpStr.split("=");
		advSetting.setNode((new Boolean(tmpStrArray[1].trim())).booleanValue());
		//edge
		tmpStr = (String) pFilterStrVect.elementAt(5);		
		tmpStrArray = tmpStr.split("=");
		advSetting.setEdge((new Boolean(tmpStrArray[1].trim())).booleanValue());
		//source
		tmpStr = (String) pFilterStrVect.elementAt(6);		
		tmpStrArray = tmpStr.split("=");
		advSetting.setSource((new Boolean(tmpStrArray[1].trim())).booleanValue());
		//target
		tmpStr = (String) pFilterStrVect.elementAt(7);		
		tmpStrArray = tmpStr.split("=");
		advSetting.setTarget((new Boolean(tmpStrArray[1].trim())).booleanValue());

		//AND
		tmpStr = (String) pFilterStrVect.elementAt(8);		
		tmpStrArray = tmpStr.split("=");
		advSetting.setRelationAND((new Boolean(tmpStrArray[1].trim())).booleanValue());
		
		//OR
		tmpStr = (String) pFilterStrVect.elementAt(9);		
		tmpStrArray = tmpStr.split("=");
		advSetting.setRelationOR((new Boolean(tmpStrArray[1].trim())).booleanValue());
		
		for (int i=10; i < pFilterStrVect.size(); i++) {
			tmpStr = (String) pFilterStrVect.elementAt(i);
			tmpStrArray = tmpStr.split("=");
			if (tmpStrArray[0].trim().endsWith("StringFilter")) {
				tmpStrArray = tmpStrArray[1].split(",");
				String atomicFilterName = tmpStrArray[0].trim();
				String serarchStr = tmpStrArray[1].trim();
				StringFilter stringFilter = new StringFilter(atomicFilterName);
				stringFilter.setSearchStr(serarchStr);
				theFilter.getAtomicFilterVect().add(stringFilter);
			}
			else { // NumericFilter
				tmpStrArray = tmpStrArray[1].split(",");
				String atomicFilterName = tmpStrArray[0].trim();
				String[] serarchValues = new String[2];
				serarchValues[0] =tmpStrArray[1].trim();
				serarchValues[1] =tmpStrArray[2].trim();
				
				NumericFilter numericFilter = new NumericFilter(atomicFilterName);
				numericFilter.setSearchValues(serarchValues);
				theFilter.getAtomicFilterVect().add(numericFilter);
			}
		}
		
		return theFilter;
	}

}


