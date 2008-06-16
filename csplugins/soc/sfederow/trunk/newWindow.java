import java.beans.PropertyChangeSupport;
import java.util.*;

import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;


public class newWindow implements BooleanAlgorithm {
	
	
	protected BooleanProperties booleanProperties = null;
	protected PropertyChangeSupport pcs;
	boolean off = false;
	CyLogger logger = null;
	String[] attributeArray = new String[1];
	String[] opArray = {"=","<",">",">=","<="};
	ArrayList attributeList = new ArrayList();
	
	
	public newWindow(){
		pcs = new PropertyChangeSupport(new Object());
		booleanProperties = new BooleanProperties(getShortName());
		logger = CyLogger.getLogger(newWindow.class);
		initializeProperties();
	}
	
	public String getShortName(){
		return "Bmapper";
	}

	/**
 	 * Get the name of this algorithm
 	 *
 	 * @return name for algorithm
 	 */
	public String getName(){
		return "Boolean Mapper";
	}

	/**
 	 * Get the settings panel for this algorithm
 	 *
 	 * @return settings panel
 	 */
	public JPanel getSettingsPanel(){
		Tunable attributeTunable = booleanProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);

		return booleanProperties.getTunablePanel();
	
	}

	protected void initializeProperties() {
		booleanProperties.add(new Tunable("debug", "Enable debugging", 
                Tunable.BOOLEAN, new Boolean(false), 
            Tunable.NOINPUT));

		/**
		 * Tuning values
		 */

		
		 
		booleanProperties.add(new Tunable("attributeListGroup",
		                                  "Source for array data",
		                                  Tunable.GROUP, new Integer(2)));
 
		
		attributeArray = getAllAttributes();
		booleanProperties.add(new Tunable("attributeList",
		                                  "Array sources",
		                                  Tunable.LIST, "",
		                                  (Object)attributeArray, (Object)null, Tunable.MULTISELECT));

		booleanProperties.add(new Tunable("operationsList",
										  "operation",
										  Tunable.LIST,"",
										  (Object)opArray, new Integer(4), Tunable.MULTISELECT));

		
		booleanProperties.add(new Tunable("Enter Criteria",
										  "Enter Criteria",
										  Tunable.STRING, new String()));
		
		booleanProperties.add(new Tunable("clusterAttributes",
                "Map to node Color", 
                Tunable.BOOLEAN, new Boolean(false)));
		
		booleanProperties.add(new Tunable("test",
                                          "test",
                                          Tunable.NODEATTRIBUTE, new String()));
		
		booleanProperties.initializeProperties();
		updateSettings(true);
	}
	
	/**
	 * This method is used to ask the algorithm to revert its settings
	 * to some previous state.  It is called from the settings dialog
	 * when the user presses the "Cancel" button.
	 *
	 * NOTE: ClusterAlgorithmBase implements this on behalf of all its subclasses
	 * by using Java Preferences.
	 */
	public void revertSettings(){
		booleanProperties.revertProperties();
	}

  /**
	 * This method is used to ask the algorithm to get its settings
	 * from the settings dialog.  It is called from the settings dialog
	 * when the user presses the "Done" or the "Execute" buttons.
	 *
	 * NOTE: ClusterAlgorithmBase implements this on behalf of all its subclasses
	 * by using Java Preferences.
	 */
	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		booleanProperties.updateValues();
		/*super.updateSettings(force);

		Tunable t = booleanProperties.get("linkage");
		if ((t != null) && (t.valueChanged() || force))
			clusterMethod = linkageTypes[((Integer) t.getValue()).intValue()];

		t = booleanProperties.get("dMetric");
		if ((t != null) && (t.valueChanged() || force))
			distanceMetric = distanceTypes[((Integer) t.getValue()).intValue()];

		t = booleanProperties.get("clusterAttributes");
		if ((t != null) && (t.valueChanged() || force))
			clusterAttributes = ((Boolean) t.getValue()).booleanValue();

		t = booleanProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			dataAttributes = (String) t.getValue();
		}*/
	}

  /**
	 * This method is used to ask the algorithm to get all of its tunables
	 * and return them to the caller.
	 *
	 * @return the cluster properties for this algorithm
	 *
	 */
	public BooleanProperties getSettings(){
		return booleanProperties;
	}

	/**
	 * This method is used to signal a running cluster algorithm to stop
	 *
	 */
	public void halt(){ 
		off = true;
	}

	

	
	public PropertyChangeSupport getPropertyChangeSupport() {return pcs;}
	
	public String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single list
		
		
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"node.");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(),"edge.");
		String[] str = (String[])attributeList.toArray(new String[attributeList.size()]);
		return str;
		
		
		
	}
	
	public void getAttributesList(ArrayList attributeList, CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING ||
		       attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
			   attributeList.add(prefix+names[i]);
			}
		}
	}
	

}
