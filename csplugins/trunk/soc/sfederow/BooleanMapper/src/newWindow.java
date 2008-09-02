package src;

import java.beans.PropertyChangeSupport;
import java.util.*;


import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.logger.CyLogger;

public class newWindow implements BooleanAlgorithm, ActionListener, TunableListener, FocusListener {
	protected BooleanProperties booleanProperties = null;
	protected PropertyChangeSupport pcs;
	CyLogger logger = null;
	ArrayList attributeList = new ArrayList();
	
	String[] attributeArray;
	String[] typeArray = {"node", "edge", "network"};
	String[] opArray = { "", "=", "<", ">", ">=", "<=", "AND", "OR", "NOT" };
	String[] criteriaArray = { "set1", "set2", "set3" };
	//String[] colors = {"red","green","yellow","blue"};
	
	String attribute, operation, criteria, value, criteriaString = "";
	
	boolean mapColor, debug, off = false;
	
	int listCount = 2;

	
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
		
		String[] attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object) attributeArray);
		
		return booleanProperties.getTunablePanel();

	}

	protected void initializeProperties() {
		

		/**
		 * Tuning values w/extension
		 */
		
		/*
		booleanProperties.add(new Tunable("criteriaSetGroup","Criteria Set Name", Tunable.GROUP,
				new Integer(1)));

		Tunable criteriaName = new Tunable("criteriaSetName", "", Tunable.LIST,
				new Integer(0), (Object)criteriaArray, (Object) null, 0);
		criteriaName.addTunableValueListener(this);
		booleanProperties.add(criteriaName);
		*/
		
		booleanProperties.add(new Tunable("attributeOperationsGroup",
				"Choose Criteria", Tunable.GROUP, new Integer(2)));

		/*
		Tunable typeChooser = new Tunable("attributeTypeChooser" ,"",Tunable.LIST,
				new Integer(0), (Object) typeArray, (Object) null, 0);
		typeChooser.addTunableValueListener(this);
		booleanProperties.add(typeChooser);
		*/
		
		//String[] attributeArray = getAllAttributes();
		Tunable attList = new Tunable("attributeList", "Attributes",
				Tunable.LIST, "", (Object) attributeArray, (Object) null,
				Tunable.MULTISELECT);
		attList.addTunableValueListener(this);
		booleanProperties.add(attList);
		
		
		
		Tunable opList = new Tunable("operationsList", "Operations", Tunable.LIST,
				new Integer(0),(Object) opArray,(Object) null, 0);
 
		
		
		opList.addTunableValueListener(this);
		booleanProperties.add(opList);
		
		

		booleanProperties.add(new Tunable("criteriaGroup", "Edit Criteria", Tunable.GROUP,
				new Integer(3)));

		Tunable legField = new Tunable("legendField", "Label", Tunable.STRING,
				new String(),this,null,0);
		legField.addTunableValueListener(this);		
		booleanProperties.add(legField);
		
		Tunable critField = new Tunable("criteriaField", "Criteria", Tunable.STRING, new String(),this,null,0);
		critField.addTunableValueListener(this);
		booleanProperties.add(critField);
		
		
		
		Tunable clear = new Tunable("clearButton", "", Tunable.BUTTON,
				new String("Clear"), this, null, 0);
		clear.addTunableValueListener(this);
		booleanProperties.add(clear);

		//booleanProperties.add(new Tunable("addButton", "", Tunable.BUTTON,
		//		new String("Add")));

		/*booleanProperties.add(new Tunable("colorGroup", "Choose Color",Tunable.BUTTON,
				new String("Choose Color")         ));

		Tunable color = new Tunable("mapColor", "Map to node Color",Tunable.LIST,
				new Integer(0),(Object) colors ,(Object) null ,0 );
		booleanProperties.add(color);
		*/
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

	public void tunableChanged(Tunable t){
		//System.out.println(t.getName()  + t.getValue());
		
		
		//booleanProperties.get("criteriaField").;
		
		criteriaString = (String)booleanProperties.get("criteriaField").getValue();
		
		if	(t.getName().equals("attributeList") || t.getName().equals("operationsList")){ 
			criteriaString = (String)booleanProperties.get("criteriaField").getValue();
			listCount++; 
			//booleanProperties.get("criteriaField").getPanel().requestFocus();
		}
		if (listCount%2 == 0 && t.getName().equals("attributeList") && !t.getValue().equals((Object)"")){
			
			
			
			System.out.println("criteriaString: "+criteriaString);
			String[] attributes = getAllAttributes();
			value = booleanProperties.getValue("attributeList");
			
			if(value.contains(",")){
				String[] indice = value.split(",");
				for(int i=0;i<indice.length;i++){
					
						criteriaString = criteriaString + " "+attributes[Integer.parseInt(value)]+" ";
					
				}	
				booleanProperties.get("criteriaField").setValue((Object)criteriaString);
				
			}else{
				if(criteriaString.equals("")){	
					criteriaString = criteriaString + " "+attributes[Integer.parseInt(value)]+ " ";
					booleanProperties.get("criteriaField").setValue((Object)criteriaString);
				}else{
				
						criteriaString = criteriaString + " "+attributes[Integer.parseInt(value)]+ " ";
					
					booleanProperties.get("criteriaField").setValue((Object)criteriaString);
				}	
				
			}	
			//booleanProperties.get("attributeList").setValue(0);
		//flag = false;
		} else {
			
		if(t.getName().equals("operationsList")){
			criteriaString = (String)booleanProperties.get("criteriaField").getValue();
			
			value = booleanProperties.getValue("operationsList");
			//if(!criteriaString.contains(opArray[Integer.parseInt(value)])){
				criteriaString = criteriaString + " " + opArray[Integer.parseInt(value)] + " ";
			//}
			booleanProperties.get("criteriaField").setValue((Object)criteriaString);
			ItemEvent e = null;
			booleanProperties.get("operationsList").setValue(0);
			
		}
		}
		
		
		
		
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("clearButton")){
			booleanProperties.get("criteriaField").setValue((Object)"");
			booleanProperties.get("legendField").setValue((Object)"");
			
			criteriaString = "";
		}
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
			operation = t.getValue().toString();
		}
		t = booleanProperties.get("criteriaField");
		if ((t != null) && (t.valueChanged() || force)) {
			criteria = (String) t.getValue();

			t = booleanProperties.get("mapColor");
			if ((t != null) && (t.valueChanged() || force)) {
				String colored = t.getValue().toString();
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
		String[] str = (String[])attributeList.toArray(new String[attributeList.size()]);
		attributeList.clear();
		return str;

	}

	public void focusLost(FocusEvent e){
		
	}
	public void focusGained(FocusEvent e){
		
	}
	public void getAttributesList(ArrayList attributeList,
			CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING
					 || attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER || attributes.getType(names[i]) == CyAttributes.TYPE_BOOLEAN) {
				if(names[i].contains(" ")){
					names[i].replace(" " ,"-");
					/*for(int j = 0; j < names[i].length(); j++){
						String temp = names[i].charAt(j) + "";
						if(temp.matches(" ")){
							names[i] = names[i].substring(0,j) + "-" + names[i].substring(j+2, names[i].length());
						}
					}*/
				}
				attributeList.add(prefix + names[i]);
			}
		}
	}

}
