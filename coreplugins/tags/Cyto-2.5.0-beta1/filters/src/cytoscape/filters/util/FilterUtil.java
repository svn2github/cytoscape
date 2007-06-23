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
	public static void applyFilter(CompositeFilter pFilter) {
		
		
	} //applyFilter()
	
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
	
	

	public static Vector<CompositeFilter> getFilterVectFromStr() {
		return new Vector<CompositeFilter>();
		/*
		Vector<CompositeFilter> retVect1 = getFilterVectFromSession();
		Vector<CompositeFilter> retVect2 = getFilterVectFromProp();
						
		if ((retVect1 == null)&&(retVect2 == null)) {
			return null;
		}
		
		if (retVect1 == null)
			return retVect2;
		
		if (retVect2 == null)
			return retVect1;
		
		retVect1.addAll(retVect2);
		return retVect1;
		*/
	}
	

	public static Vector<CompositeFilter> getFilterVectFromSession() {
		
		Vector<CompositeFilter> retVect = new Vector<CompositeFilter>();
		
		return null;
		/*
		CompositeFilter filter1 = new CompositeFilter();
		filter1.setName("My first filter");
		filter1.addAtomicFilter(new StringFilter("AttNameAAA","searchStrAAA"));
		filter1.addAtomicFilter(new StringFilter("AttNameBBB","searchStrBBB"));
		
		CompositeFilter filter2 = new CompositeFilter();
		filter2.addAtomicFilter(new NumericFilter("AttNameCCC",1.5,3.8));
		filter2.addAtomicFilter(new StringFilter("AttNameDDD","SearchStrDDD"));
		filter2.setName("My second filter");

		CompositeFilter filter3 = new CompositeFilter();
		filter3.addAtomicFilter(new NumericFilter("AttNameEEE",1.5,3.8));
		filter3.addAtomicFilter(new StringFilter("AttNameFFF","SearchStrFFF"));
		filter3.setName("My third filter");

		retVect.add(filter1);
		retVect.add(filter2);
		retVect.add(filter3);
				
		return retVect;
		*/
	}


	
	public static CompositeFilter createFilterFromString(Vector pFilterStrVect) {
		if (pFilterStrVect==null || pFilterStrVect.size() == 0) {
			return null;
		}
		
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

		for (int i=8; i < pFilterStrVect.size(); i++) {
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




/*
public void initialize() {
	Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);

	try {
		File filter_file = CytoscapeInit.getConfigFile("filter.props");
		BufferedReader in = new BufferedReader(new FileReader(filter_file));
		String oneLine = in.readLine();

		while (oneLine != null) {
			if (oneLine.startsWith("#")) {
				// comment
			} else {
				FilterManager.defaultManager().createFilterFromString(oneLine);
			}

			oneLine = in.readLine();
		}

		in.close();
	} catch (Exception ex) {
		System.out.println("Filter Read error");
		ex.printStackTrace();
	}

	// create icons
	ImageIcon icon = new ImageIcon(getClass().getResource("/stock_filter-data-by-criteria.png"));
	ImageIcon icon2 = new ImageIcon(getClass()
	                                    .getResource("/stock_filter-data-by-criteria-16.png"));

	// 
	//FilterPlugin action = new FilterPlugin(icon, this);
	FilterMenuItem menu_action = new FilterMenuItem(icon2, this);
	//Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( ( CytoscapeAction )action );
	Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menu_action);

	//CytoscapeDesktop desktop = Cytoscape.getDesktop();
	//CyMenus cyMenus = desktop.getCyMenus();
	//CytoscapeToolBar toolBar = cyMenus.getToolBar();
	//JButton button = new JButton(icon);
	//button.addActionListener(action);
	//button.setToolTipText("Create and apply filters");
	//button.setBorderPainted(false);
	//toolBar.add(button);

	FilterEditorManager.defaultManager().addEditor(new NumericAttributeFilterEditor());
	FilterEditorManager.defaultManager().addEditor(new StringPatternFilterEditor());
	FilterEditorManager.defaultManager().addEditor(new NodeTopologyFilterEditor());
	FilterEditorManager.defaultManager().addEditor(new BooleanMetaFilterEditor());
	FilterEditorManager.defaultManager().addEditor(new NodeInteractionFilterEditor());
	FilterEditorManager.defaultManager().addEditor(new EdgeInteractionFilterEditor());
}
*/


