package src;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ImageIcon;

import javax.imageio.*;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

public class CriteriaMapperDialog extends JDialog implements ActionListener, FocusListener, ListSelectionListener,
java.beans.PropertyChangeListener{

	
	private BooleanCalculator calculator = null;
	private BooleanScanner scan = null; //Not currently used
	private AttributeManager attributeManager;
	private ColorMapper colorMapper; 
	private CriteriaTablePanel criteriaTable;
	private CriteriaCalculator calculate = new CriteriaCalculator(); //Not currently used
	
	private JButton newSet;
	private JButton saveSet;
	private JButton deleteSet;
	private JButton renameSet;
	private JPanel mainPanel;
	private JPanel namesPanel;
	private JPanel tableMapperPanel;
	private JPanel setButtonsPanel;
	

	String mapTo = "Node Color";
	
	public CriteriaMapperDialog()
	{
	   Cytoscape.getSwingPropertyChangeSupport().
	   addPropertyChangeListener(this);
	   //add as listener to CytoscapeDesktop
	   Cytoscape.getDesktop().getSwingPropertyChangeSupport().
	   addPropertyChangeListener(this);
	
	   //currentAlgorithm = algorithm;
	   colorMapper = new ColorMapper();
	   attributeManager = new AttributeManager();
	   calculator = new BooleanCalculator();
	   criteriaTable = new CriteriaTablePanel();
	   scan = new BooleanScanner();
	   initialize();
	}
	
	public void initialize()
	{
	   mainPanel = new JPanel();
	   
	   mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
	   mainPanel.setMaximumSize(new Dimension(Cytoscape.getDesktop().getWidth(), 150));
	   
	   JPanel setPanel = getCriteriaSetPanel();
	   
	   tableMapperPanel = criteriaTable.getTablePanel();
	   tableMapperPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	
	   
	   mainPanel.add(setPanel);
	   mainPanel.add(tableMapperPanel);

	   setContentPane(mainPanel);
	   setLocation(0,Cytoscape.getDesktop().getHeight()-250);
	}

	public void actionPerformed(ActionEvent e) {
		

		String command = e.getActionCommand();
		
	
		if(command.equals("listChanged")){
			String setName = (String)nameBox.getSelectedItem();
			criteriaTable.clearTable();	
			loadSettings(setName); 
			
		}
		
		if(command.equals("newSet")){
			nameBox.setSelectedIndex(0);
			nameBox.setEditable(true);
			criteriaTable.clearTable();
		}
		if(command.equals("saveSet")){
			String setName = (String)nameBox.getSelectedItem();
			saveSettings(setName);
			nameBox.setEditable(false);
			initialize();
		}
		
		
		pack();
		setVisible(true);
	}
	
	
	public void saveSettings(String nameValue){

		//System.out.println(nameBox.getSelectedItem());
		String newName = nameValue; //(String)nameBox.getSelectedItem();
		
		String mapTo = (String)mapToBox.getSelectedItem();
		
		attributeManager.addNamesAttribute(Cytoscape.getCurrentNetwork(), newName);

		String[] criteriaLabels = new String[criteriaTable.getDataLength()];	
		for(int k=0; k<criteriaLabels.length; k++){
			String temp = criteriaTable.getCell(k, 0)+":"+criteriaTable.getCell(k, 1)+":"+criteriaTable.getCell(k, 2);

			if(!temp.equals(null)){
				criteriaLabels[k] = temp;
			}
			//attributeManager.setColorAttribute(label, color, nodeID)
			//System.out.println(criteriaLabels.length+"AAA"+temp);
		}
		attributeManager.setValuesAttribute(newName, mapTo, criteriaLabels);
	}
	
	
	public void loadSettings(String setName){
		String[] criteria = attributeManager.getValuesAttribute(Cytoscape.getCurrentNetwork(), setName);
		
		criteriaTable.clearTable();
		if(criteria.length > 0){ mapTo = criteria[0]; }
		criteriaTable.mapTo = mapTo;
		
		for(int i=1; i<criteria.length;i++){
			String[] temp = criteria[i].split(":");
			if(temp.length != 3){ break; }
			criteriaTable.populateList(temp[0], temp[1], criteriaTable.stringToColor(temp[2]));

		}
	}
	

	
	public void valueChanged(ListSelectionEvent e){
	
	}
	
	public void focusGained(FocusEvent e){
		System.out.println(e.toString());
	}
	
	public void focusLost(FocusEvent e){
		System.out.println(e.toString());
	}
	
	public void propertyChange ( PropertyChangeEvent e ) {
		if(e.getPropertyName().equals("ATTRIBUTES_CHANGED")){
			initialize();
			setVisible(true);
		}
	}
	
	private JComboBox nameBox;
	private JComboBox mapToBox;
	private String[] nameBoxArray;
	
	public JPanel getCriteriaSetPanel(){
		//JPanel setPanel = new JPanel(new BorderLayout(0, 2));
		
		nameBoxArray = attributeManager.getNamesAttribute(Cytoscape.getCurrentNetwork());
		
		JPanel setPanel = new JPanel();
		BoxLayout box = new BoxLayout(setPanel, BoxLayout.Y_AXIS);
		setPanel.setLayout(box);
		
		String labelLocation = BorderLayout.LINE_START;
		String fieldLocation = BorderLayout.LINE_END;
		
		JPanel namePanel = new JPanel(new BorderLayout(0, 2));
		JLabel setLabel = new JLabel("Name"); 
		//System.out.println(Cytoscape.getCurrentNetwork().getIdentifier());
		nameBox = new JComboBox(nameBoxArray);
		nameBox.setEditable(false);
		nameBox.setPreferredSize(new Dimension(240,20));
		nameBox.setActionCommand("listChanged");
		nameBox.addActionListener(this);
		
		namePanel.add(setLabel,labelLocation);
		namePanel.add(nameBox,fieldLocation);
		
		JPanel nPanel = new JPanel();
		nPanel.add(namePanel);
		
		JPanel sPanel = new JPanel(new BorderLayout(0,2));
		JPanel setButtonsPanel = new JPanel();//new BorderLayout(0,2));
		newSet = new JButton("New");
		saveSet = new JButton("Save");
		deleteSet = new JButton("Delete");
		renameSet = new JButton("Rename");
		
		newSet.addActionListener(this);
		saveSet.addActionListener(this);
		deleteSet.addActionListener(this);
		renameSet.addActionListener(this);
		
		newSet.setActionCommand("newSet");
		saveSet.setActionCommand("saveSet");
		deleteSet.setActionCommand("deleteSet");
        renameSet.setActionCommand("renameSet");
		   
        setButtonsPanel.add(newSet); 
  	    setButtonsPanel.add(saveSet);
	    setButtonsPanel.add(deleteSet); 
        setButtonsPanel.add(renameSet);
		
        sPanel.add(setButtonsPanel, BorderLayout.CENTER);
		
		Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, "Criteria Set");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		setPanel.setBorder(titleBorder);
		
		
		
		JPanel mapPanel = new JPanel(new BorderLayout(0, 2));
		JLabel mapLabel = new JLabel("Map To");
		mapToBox = new JComboBox(new String[] {"Node Color", "Node Border Color", "None" });
		mapToBox.setActionCommand("mapToBoxChanged");
		mapToBox.addActionListener(this);
		
		mapPanel.add(mapLabel, labelLocation);
		mapPanel.add(mapToBox, fieldLocation);
		
		
		setPanel.add(nPanel);
		//setPanel.add(mapPanel);
		setPanel.add(sPanel);
		
		return setPanel;
	}
	
	
	
	public String[] getAllAttributes(ArrayList<String> attributeList) {
		// Create the list by combining node and edge attributes into a single
		// list

		getAttributesList(attributeList, Cytoscape.getNodeAttributes(), "");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(), "");
		
		String[] str = (String[])attributeList.toArray(new String[attributeList.size()]);
		attributeList.clear();
		return str;

	}

	
	public void getAttributesList(ArrayList<String> attributeList,
			CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		ArrayList<String> numericAttributes = new ArrayList<String>();
		//ArrayList<String> stringAttributes = new ArrayList<String>();
		ArrayList<String> booleanAttributes = new ArrayList<String>();
		ArrayList<String> internalAttributes = new ArrayList<String>();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING || attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER){
				if(names[i].contains(" ")){	names[i].replace(" " ,"-"); }	
				if(names[i].contains(":")){
					internalAttributes.add(names[i]);
				}else{
					numericAttributes.add(names[i]);
				}
			}	
			if(attributes.getType(names[i]) == CyAttributes.TYPE_BOOLEAN){
				if(names[i].contains(" ")){	names[i].replace(" " ,"-"); }	
				if(names[i].contains(":")){
					internalAttributes.add(names[i]);
				}else{
				   booleanAttributes.add(names[i]);
				}
			}
		}
		//attributeList.add("--Numeric Attributes--");
		for(int j=0; j<numericAttributes.size(); j++){
			attributeList.add(numericAttributes.get(j));
		}
		//attributeList.add("--Boolean Attributes--");
		for(int k=0; k<booleanAttributes.size(); k++){
			attributeList.add(booleanAttributes.get(k));
		}
		for(int i=0; i<internalAttributes.size(); i++){
			//attributeList.add(internalAttributes.get(i));
		}
	}
	
}

