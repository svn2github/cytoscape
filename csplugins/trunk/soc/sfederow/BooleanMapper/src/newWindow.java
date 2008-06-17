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

	boolean mapColor, debug = false;

	CyLogger logger = null;

	String[] attributeArray = new String[1];

	String[] opArray = { "=", "<", ">", ">=", "<=", "AND", "OR", "NOT" };

	String attribute, operation, criteria = "";

	int[] intarray = { 1, 2, 3, 4, 5 };

	int d = 4;

	String atts = "word";

	ArrayList attributeList = new ArrayList();

	public newWindow() {
		pcs = new PropertyChangeSupport(new Object());
		booleanProperties = new BooleanProperties(getShortName());
		logger = CyLogger.getLogger(newWindow.class);
		initializeProperties();
	}

	public String getShortName() {
		return "Bmapper";
	}

	/**
	 * Get the name of this algorithm
	 * 
	 * @return name for algorithm
	 */
	public String getName() {
		return "Boolean Mapper";
	}

	/**
	 * Get the settings panel for this algorithm
	 * 
	 * @return settings panel
	 */
	public JPanel getSettingsPanel() {
		Tunable attributeTunable = booleanProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object) attributeArray);

		return booleanProperties.getTunablePanel();

	}

	protected void initializeProperties() {
		booleanProperties.add(new Tunable("debug", "Enable debugging",
				Tunable.BOOLEAN, new Boolean(false), Tunable.NOINPUT));

		/**
		 * Tuning values
		 */

		booleanProperties.add(new Tunable("attributeOperationsGroup",
				"Choose Criteria", Tunable.GROUP, new Integer(2)));

		attributeArray = getAllAttributes();
		booleanProperties.add(new Tunable("attributeList", "Attributes",
				Tunable.LIST, "", (Object) attributeArray, (Object) null,
				Tunable.MULTISELECT));

		booleanProperties
				.add(new Tunable("operationsList", "Operations", Tunable.LIST,
						"", (Object) opArray, (Object) null, Tunable.LIST));

		booleanProperties.add(new Tunable("criteriaGroup", "View Criteria",
				Tunable.GROUP, new Integer(2)));

		booleanProperties.add(new Tunable("criteriaField", "", Tunable.STRING,
				new String()));

		booleanProperties.add(new Tunable("clearButton", "", Tunable.BUTTON,
				new String("Clear")));

		booleanProperties.add(new Tunable("colorGroup", "Choose Color",
				Tunable.GROUP, new Integer(1)));

		booleanProperties.add(new Tunable("mapColor", "Map to node Color",
				Tunable.BOOLEAN, new Boolean(false)));

		/*
		 * booleanProperties.add(new Tunable("test", "test", Tunable.INTEGER,
		 * new Integer(6),(Object)new Integer(0), (Object)new
		 * Integer(10),Tunable.USESLIDER));
		 */
		booleanProperties.initializeProperties();
		updateSettings(true);
	}

	/**
	 * This method is used to ask the algorithm to revert its settings to some
	 * previous state. It is called from the settings dialog when the user
	 * presses the "Cancel" button.
	 * 
	 * NOTE: ClusterAlgorithmBase implements this on behalf of all its
	 * subclasses by using Java Preferences.
	 */
	public void revertSettings() {
		booleanProperties.revertProperties();
	}

	/**
	 * This method is used to ask the algorithm to get its settings from the
	 * settings dialog. It is called from the settings dialog when the user
	 * presses the "Done" or the "Execute" buttons.
	 * 
	 * NOTE: ClusterAlgorithmBase implements this on behalf of all its
	 * subclasses by using Java Preferences.
	 */
	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		booleanProperties.updateValues();
		Tunable t = booleanProperties.get("debug");
		if ((t != null) && (t.valueChanged() || force)) {
			debug = ((Boolean) t.getValue()).booleanValue();
		}
		t = booleanProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			attribute = (String) t.getValue();
		}
		t = booleanProperties.get("operationsList");
		if ((t != null) && (t.valueChanged() || force)) {
			operation = (String) t.getValue();
		}
		t = booleanProperties.get("criteriaField");
		if ((t != null) && (t.valueChanged() || force)) {
			criteria = (String) t.getValue();

			t = booleanProperties.get("mapColor");
			if ((t != null) && (t.valueChanged() || force)) {
				mapColor = ((Boolean) t.getValue()).booleanValue();
			}

		}
	}

	/**
	 * This method is used to ask the algorithm to get all of its tunables and
	 * return them to the caller.
	 * 
	 * @return the cluster properties for this algorithm
	 * 
	 */
	public BooleanProperties getSettings() {
		return booleanProperties;
	}

	public String getValues() {
		return attribute;
	}

	/**
	 * This method is used to signal a running cluster algorithm to stop
	 * 
	 */
	public void halt() {
		off = true;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcs;
	}

	public String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single
		// list

		getAttributesList(attributeList, Cytoscape.getNodeAttributes(), "");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(), "");
		String[] str = (String[]) attributeList
				.toArray(new String[attributeList.size()]);
		return str;

	}

	public void getAttributesList(ArrayList attributeList,
			CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING
					|| attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
				attributeList.add(prefix + names[i]);
			}
		}
	}

}
