package src;

/*
 * Steve Federowicz 
 * Google Summer of Code
 * 
 * This is the main class in the entire program as it serves as the background JDialog window 
 * upon which everything else is added to and displayed.  This class contains all of the
 * code which handles buttons and action events.  It also contains all of the java Swing code necessary to 
 * display the components.  The initialize method calls all of the other methods which
 * create swing components and return a JPanel which are then added to the main dialog window.
 * 
 */

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.visual.*;
import cytoscape.task.util.TaskManager;
import cytoscape.data.CyAttributes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.*;

import java.util.regex.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.awt.BorderLayout;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.colorchooser.*;
import javax.swing.JColorChooser;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.AbstractCellEditor;



import javax.swing.WindowConstants.*;
import javax.swing.border.*;
import javax.swing.text.Position;


public class BooleanSettingsDialog extends JDialog implements ActionListener, FocusListener, ListSelectionListener,
                                                               java.beans.PropertyChangeListener{
	
	//Declarations of classes used by BooleanMapper
	private BooleanAlgorithm currentAlgorithm = null;
	private BooleanCalculator calculator = null;
	private BooleanScanner scan = null; //Not currently used
	private AttributeManager attributeManager;
	private ColorMapper colorMapper; 
	private CriteriaTablePanel criteriaTable;
	private CriteriaCalculator calculate = new CriteriaCalculator(); //Not currently used
	
	//All of the Java Swing objects
	private JButton colorButton;
	private JColorChooser colorChooser;
	private JComboBox nameBox;
	private JDialog dialog;
	private JList attList;
	private JList opList;
	private JPanel criteriaChooserPanel;
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JPanel setNamePanel;
	private JPanel tablePanel;
	private JPanel colorPanel;
	private JTable table;
	private JTextField criteriaField;
	private JTextField labelField;
	private JTextField colorField;
	
	//All of the other miscellaneous variables
	private Color currentColor = Color.BLUE; //keeps track of the color displayed by the color button
	private String label = ""; 
	private String criteria = "";
	private ArrayList<String> attributeList = new ArrayList<String>(); //List which holds all of the attributes
	private String[] opArray = {"=", "<", ">", ">=", "<=", "AND", "OR", "NOT"}; 
	private String[] attributesArray;
	private String[] nameBoxArray; //Array holding the names of the Criteria Sets
	
	
	public BooleanSettingsDialog(BooleanAlgorithm algorithm) {
		super(Cytoscape.getDesktop(), algorithm.getName(), false);
		
		Cytoscape.getSwingPropertyChangeSupport().
		addPropertyChangeListener(this);
		// add as listener to CytoscapeDesktop
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().
		addPropertyChangeListener(this);
		

		
		currentAlgorithm = algorithm;
		colorMapper = new ColorMapper();
		attributeManager = new AttributeManager();
		calculator = new BooleanCalculator();
		criteriaTable = new CriteriaTablePanel();
		scan = new BooleanScanner();
		initialize(); 
	
	}
	
	/*
	 * Creates or calls on other methods to create all of the JPanels and
	 * swing components in the GUI.
	 */
	private void initialize() {
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		setNamePanel = getCriteriaSetPanel(); //new CriteriaSetPanel();
		mainPanel.add(setNamePanel);
		
		// Create a panel for algorithm's content
		//this.algorithmPanel = currentAlgorithm.getSettingsPanel();

		//mainPanel.add(algorithmPanel);
		mainPanel.addFocusListener(this);
		
		mainPanel.add(getListPanel());
		
		mainPanel.add(getCriteriaChooserPanel());
				
		//Create a panel for our button box
		this.buttonBox = new JPanel();
		
		//Create buttons
		JButton addButton = new JButton("Add");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);

		JButton applyButton = new JButton("Apply");
		applyButton.setActionCommand("apply");
		applyButton.addActionListener(this);

		JButton saveButton = new JButton("Save");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);

		JButton exitButton = new JButton("Exit");
		exitButton.setActionCommand("exit");
		exitButton.addActionListener(this);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		
		//buttonBox.add(addButton);		
		buttonBox.add(saveButton);
		buttonBox.add(exitButton);
		buttonBox.add(applyButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonBox.addFocusListener(this);
		
		
		tablePanel = criteriaTable.getTablePanel();
		tablePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	
		
		//mainPanel.add(colorPanel);
		mainPanel.add(buttonBox);
		mainPanel.add(tablePanel);
	
		setContentPane(mainPanel);
		
		mainPanel.setLocation(Cytoscape.getDesktop().getWidth(), Cytoscape.getDesktop().getHeight());
		setLocation(2,Cytoscape.getDesktop().getHeight()-557);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		// Are we the source of the event?
		//System.out.println("action");
		
		String command = e.getActionCommand();
		
		if(command.equals("chooseColor")){
			colorChooser = new JColorChooser();
			 JButton button = new JButton();
		     button.setActionCommand("edit");
		     button.addActionListener(this);
		     button.setBorderPainted(true);
			dialog = JColorChooser.createDialog(button,
                    "Pick a Color",
                    true,  //modal
                    colorChooser,
                    this,  //OK button handler
                    null); //no CANCEL button handler
			dialog.add(button);
			dialog.setLocation(2,Cytoscape.getDesktop().getHeight()-385);
			dialog.setVisible(true);
			currentColor = colorChooser.getColor();
		}
		
		if(command.equals("OK")){
			
			System.out.println(colorChooser.getColor());
			currentColor = colorChooser.getColor();
			colorButton.setBackground(colorChooser.getColor());
		}
		
		if(command.equals("edit")){
			System.out.println("found it");
		}
		
		if (command.equals("add")){
			
			criteria = criteriaField.getText();
			if(criteria.equals("")){
				JOptionPane.showMessageDialog(new JPanel(), "Must include Criteria");
				criteriaField.requestFocus();
				return;
			}
			label = labelField.getText();
			if(label.equals("")){
				JOptionPane.showMessageDialog(new JPanel(), "Must include Label");	
				criteriaField.requestFocus();
				return;
			}
			
			
			if(calculator.parse2(criteria) != null){ 
				//ArrayList<Token> temp = new ArrayList<Token>();
				
				CriteriaCalculator calculate = new CriteriaCalculator();
				
				try{
					calculate.parse(criteria);
				}catch (Exception p) {
					System.out.println(p.getMessage());
				}
				 calculate.printTokens();
				
				System.out.println(criteria);
				criteriaTable.populateList(criteria, label, currentColor);
				//initialize();
				
			}else{
				JOptionPane.showMessageDialog(new JPanel(), "Invalid Criteria");
			}
			
		}
		
		if(command.equals("listChanged")){
			String value = (String)nameBox.getSelectedItem();
			if(value.equals("")){
				JOptionPane.showMessageDialog(new JPanel(), "Criteria Set must be named");
				return;
			}
			System.out.println(value);
			if(value.equals("New...")){
				nameBox.setSelectedIndex(0);
				nameBox.setEditable(true);
				criteriaTable.clearTable();
			}else{
				loadSettings(value);
			}
		}
		if(command.equals("clear")){
			criteriaField.setText("");
			labelField.setText("");
			criteriaBuild = "";
			criteriaField.requestFocus();
		}
		
		if (command.equals("apply")) {
	
			applyCriteria();
			
		}
		
		if (command.equals("save")) {
			System.out.println("save: "+nameBox.getSelectedItem());
			String value = nameBox.getSelectedItem()+"";
			if(value.equals("")){
				JOptionPane.showMessageDialog(new JPanel(), "Criteria Set must be named");
				return;
			}
			
			saveSettings();
			
			initialize();	
		}
		
		if (command.equals("exit")) {
			// Call revertSettings for each
			//revertAllSettings();
			setVisible(false);
		} else {
			// OK, initialize and display
			//initialize();
			pack();
			setVisible(true);
		}
	}
	
	public void propertyChange ( PropertyChangeEvent e ) {
		if(e.getPropertyName().equals("ATTRIBUTES_CHANGED")){
			System.out.println("wooooord");
			initialize();
			setVisible(true);
			
		}
	}
	
	
	
	String criteriaBuild = "";
	int last = -1;
	/*
	 * Handles list selection on attributes.  Further below their is a
	 * separate class, getOperationSelection which does the exact same thing as this method 
	 * but on the other list.  Since their were two lists, one for the attributes, and one for
	 * operations I found that their was no good way to tell apart ListSelectionEvents from two
	 * separate lists.  I thus was forced to create the other class to handle the operation
	 * selection.
	 */
	public void valueChanged(ListSelectionEvent e){

		ListSelectionModel lsm = (ListSelectionModel)e.getSource();

		int firstIndex = e.getFirstIndex();
		int lastIndex = e.getLastIndex();
		boolean isAdjusting = e.getValueIsAdjusting(); 
		//e.getSource().getClass().

		if (lsm.isSelectionEmpty()) {
			System.out.println(" <none>");
		} else {
			// Find out which indexes are selected.
			int minIndex = lsm.getMinSelectionIndex();
			int maxIndex = lsm.getMaxSelectionIndex();


			for (int i = minIndex; i <= maxIndex; i++) {
				//if (lsm.isSelectedIndex(i) && last != i) {
				criteriaBuild = criteriaField.getText();
				criteriaBuild = criteriaBuild +" "+ attributesArray[i]+" ";
				criteriaField.setText(criteriaBuild);
				System.out.println("Selected Index: "+i);
				//}
				last = i;
			}
		}
		attList.clearSelection();
		criteriaField.requestFocus();
		criteriaField.setHorizontalAlignment(JTextField.LEFT);

	}
	
	
	/*
	 * Saves all of the settings.
	 */
	public void saveSettings(){

		//System.out.println(nameBox.getSelectedItem());
		String newName = (String)nameBox.getSelectedItem();
		if(newName.equals("New...")){ return; }

		attributeManager.addNamesAttribute(Cytoscape.getCurrentNetwork(), newName);

		String[] criteriaLabels = new String[criteriaTable.getDataLength()];	
		for(int k=0; k<criteriaLabels.length; k++){
			String temp = criteriaTable.getCell(k, 0)+":"+criteriaTable.getCell(k, 1)+":"+criteriaTable.getCell(k, 2);

			if(!temp.equals(null)){
				criteriaLabels[k] = temp;
			}
			//attributeManager.setColorAttribute(label, color, nodeID)
			System.out.println(criteriaLabels.length+"AAA"+temp);
		}
		attributeManager.setValuesAttribute(newName, criteriaLabels);

	}
	
	public void loadSettings(String setName){
		String[] criteria = attributeManager.getValuesAttribute(Cytoscape.getCurrentNetwork(), setName);
		criteriaTable.clearTable();
		
		for(int i=0; i<criteria.length;i++){
			String[] temp = criteria[i].split(":");
			criteriaTable.populateList(temp[0], temp[1], criteriaTable.stringToColor(temp[2]));

		}
	}
	
	
	/*
	 * Important method which extracts the appropriate data from the table through CriteriaTablePanel
	 * and then sends it off first to the calculator class to get evaluated and then to the colorMapper
	 * to create the color mappings.
	 */
	public void applyCriteria(){

		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Color> colors = new ArrayList<Color>();
		String compositeLabel = "";
		String[] nameLabels = new String[criteriaTable.getDataLength()];
		for(int i=0; i<criteriaTable.getDataLength(); i++){
			String current = (String)criteriaTable.getCell(i,0); 
			if(current != null && !current.equals("")){

				try{

					calculate.parse(current);
				}catch (Exception p) {
					System.out.println(p.getMessage());
				}


				ArrayList<String>[] attsAndOps = calculator.parse2(current);
				//calculator.clearList();
				if(attsAndOps != null){
					String label = (String)criteriaTable.getCell(i, 1);
					if(i == 0){ compositeLabel = label; 
					}else{
						if(!label.equals("") && label != null){
							compositeLabel = compositeLabel + ":" + label;
						}
					}
					calculator.evaluate(label, attsAndOps[0], attsAndOps[1]);
					//calculate.evaluateLeftToRight(label);
					labels.add(label);
				}


				Color c = criteriaTable.stringToColor(criteriaTable.getCell(i,2)+"");
				colors.add(c);

			}
		}

		String[] labelsA = new String[labels.size()];
		for(int h=0; h<labels.size(); h++){
			labelsA[h] = labels.get(h);
		}

		try{
			attributeManager.setCompositeAttribute(labelsA);
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		
		Color[] colorsA = new Color[labels.size()];
		for(int g=0; g<labels.size(); g++){
			colorsA[g] = colors.get(g);
		}
		
		System.out.println("compositeLabel: "+compositeLabel);
		if(labels.size() == 1){

			colorMapper.createDiscreteMapping(labelsA[0]+"_discrete", labelsA[0], colorsA[0]);
		}else{
			colorMapper.createCompositeMapping(compositeLabel+"_discrete", compositeLabel, colorsA);
		}
		
		//System.out.println("current: "+ current);
		/*try{
			scan.parse(current);
			}catch (IOException e) {
				System.out.println(e.getMessage());
			}
		 */

		//}
		//parsedCriteria = calculator.parseCriteria(criteria);
		//calculator.evaluateCriteria(parsedCriteria);
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
			attributeList.add(internalAttributes.get(i));
		}
	}


	public void focusGained(FocusEvent e){
		System.out.println(e.toString());
	}
	
	public void focusLost(FocusEvent e){
		System.out.println(e.toString());
	}
	
	private void updateAllSettings() {
		currentAlgorithm.updateSettings();
	}
	
	private void updateAllSettings(boolean force) {
		currentAlgorithm.updateSettings(force);
	}

	private void revertAllSettings() {
		currentAlgorithm.revertSettings();
	}
	
	
	/*
	 * Creates the Criteria Set Panel which has two JComboBoxes for the name and map to
	 * fields.
	 */
	public JPanel getCriteriaSetPanel(){
		//JPanel setPanel = new JPanel(new BorderLayout(0, 2));
		
		nameBoxArray = attributeManager.getNamesAttribute(Cytoscape.getCurrentNetwork());
		
		JPanel setPanel = new JPanel();
		BoxLayout box = new BoxLayout(setPanel, BoxLayout.Y_AXIS);
		setPanel.setLayout(box);
		
		Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, "Criteria Set");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		setPanel.setBorder(titleBorder);
		
		String labelLocation = BorderLayout.LINE_START;
		String fieldLocation = BorderLayout.LINE_END;
		
		JPanel namePanel = new JPanel(new BorderLayout(0, 2));
		JLabel setLabel = new JLabel("Name"); 
		System.out.println(Cytoscape.getCurrentNetwork().getIdentifier());
		nameBox = new JComboBox(nameBoxArray);
		nameBox.setEditable(false);
		nameBox.setPreferredSize(new Dimension(200,20));
		nameBox.setActionCommand("listChanged");
		nameBox.addActionListener(this);
		namePanel.add(setLabel, labelLocation);
		namePanel.add(nameBox, fieldLocation);
		
		JPanel mapPanel = new JPanel(new BorderLayout(0, 2));
		JLabel mapLabel = new JLabel("Map To");
		JComboBox mapToBox = new JComboBox(new String[] {"Node Color", "Node Border Color", "None" });
		mapPanel.add(mapLabel, labelLocation);
		mapPanel.add(mapToBox, fieldLocation);
		
		setPanel.add(namePanel);
		setPanel.add(mapPanel);
		
		return setPanel;
	}
	
	
	
	/*
	 * Creates the criteria and label text fields, along with the color chooser, add, 
	 * and clear buttons.
	 */
	private JPanel getCriteriaChooserPanel(){
		JPanel fieldPanel = new JPanel();
		
		BoxLayout box = new BoxLayout(fieldPanel, BoxLayout.Y_AXIS);
		fieldPanel.setLayout(box);
		
		Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, "");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		fieldPanel.setBorder(titleBorder);
		
		
		String labelLocation = BorderLayout.LINE_START;
		String fieldLocation = BorderLayout.LINE_END;
		
		JPanel labelPanel = new JPanel(new BorderLayout(0, 2));
		JLabel label = new JLabel("Label"); 
		labelField = new JTextField();
		labelField.setPreferredSize(new Dimension(200, 20));
		labelPanel.add(label, labelLocation);
		labelPanel.add(labelField, fieldLocation);
		labelField.setHorizontalAlignment(JTextField.LEFT);
		//labelField.s
		
		
		JPanel criteriaPanel = new JPanel(new BorderLayout(0, 2));
		JLabel criteriaLabel = new JLabel("Criteria");
		criteriaField = new JTextField();
		
		criteriaField.setPreferredSize(new Dimension(200, 20));
		criteriaPanel.add(criteriaLabel, labelLocation);
		criteriaPanel.add(criteriaField, fieldLocation);
		criteriaField.setHorizontalAlignment(JTextField.LEFT);
		
		
		//Make JPanel for colorPanel and button box panel
		JPanel colorAndButtonPanel = new JPanel();
		BoxLayout Xbox = new BoxLayout(colorAndButtonPanel, BoxLayout.X_AXIS);
		colorAndButtonPanel.setLayout(Xbox);
		
		//Make colorPanel for Color Label and color chooser button
		JPanel colorPanel = new JPanel(new BorderLayout(0, 2));
		//colorPanel.setPreferredSize(new Dimension(180,10));
		JLabel colorLabel = new JLabel("Color");
		colorButton = new JButton("");
		colorButton.setBackground(currentColor);
		colorButton.setPreferredSize(new Dimension(90, 10));
		colorButton.setActionCommand("chooseColor");
		colorButton.addActionListener(this);
		colorButton.setBorder(null);
        colorButton.setBorderPainted(false);
        colorButton.setBackground(currentColor);
        
		colorPanel.add(colorLabel, labelLocation);
		colorPanel.add(colorButton, fieldLocation);
		
		//Make button box JPanel for Add and Clear Buttons
		JPanel buttonBox = new JPanel();//new BorderLayout(0,2));
		JButton addButton = new JButton("Add");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		JButton clearButton = new JButton("Clear");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);
		
		buttonBox.add(addButton);//, labelLocation);
		buttonBox.add(clearButton);//, fieldLocation);
		
		colorAndButtonPanel.add(colorPanel);
		colorAndButtonPanel.add(buttonBox);
		
		
		fieldPanel.add(labelPanel);
		fieldPanel.add(criteriaPanel);
		fieldPanel.add(colorAndButtonPanel);
		
		
		return fieldPanel;
	}
	
	/*
	 * Creates the attribute list and operation list.
	 */
	private JPanel getListPanel(){
		JPanel bigPanel = new JPanel();
		
		BoxLayout bigBox = new BoxLayout(bigPanel, BoxLayout.Y_AXIS);
		bigPanel.setLayout(bigBox);
		
		
		Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, "Build Criteria");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		bigPanel.setBorder(titleBorder);
		
		//make label panel
		JPanel labelPanel = new JPanel();
		BoxLayout labelBox = new BoxLayout(labelPanel, BoxLayout.X_AXIS);
		labelPanel.setLayout(labelBox);
		
		
		JPanel attPanel = new JPanel(new BorderLayout(0,2));
		JLabel attLabel = new JLabel("    Attributes");
		attPanel.add(attLabel, BorderLayout.LINE_START);
		JPanel opPanel = new JPanel(new BorderLayout(0,2));
		JLabel opLabel = new JLabel("Operations       ");
		
		//opPanel.add(spaceLabel, BorderLayout.LINE_START);
		opPanel.add(opLabel, BorderLayout.LINE_END);
		
		labelPanel.add(attPanel);
		labelPanel.add(opPanel);
		
		JPanel listPanel = new JPanel();
		BoxLayout listBox = new BoxLayout(listPanel, BoxLayout.X_AXIS);
		listPanel.setLayout(listBox);
		
		JPanel attListPanel = new JPanel(new BorderLayout(0,2));
		attributesArray = getAllAttributes(); 
		attList = new JList();
		attList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = attributesArray;
            public String getName() { return "attList"; }
            public int getSize() { 
            	if(strings.length == 8){ return 9; }
            	else{ return strings.length; }
            }
            public Object getElementAt(int i) { return strings[i]; }
        });
		ListSelectionModel listSelectModel = attList.getSelectionModel();        
        listSelectModel.addListSelectionListener(this);
        
    	attList.setSelectionModel(listSelectModel);
    	
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(attList);
		pane.setPreferredSize(new Dimension(150,150));
		attListPanel.add(pane, BorderLayout.LINE_START);
		
		JPanel opListPanel = new JPanel(new BorderLayout(0,2));
		opList = new JList(opArray);
		ListSelectionModel listSelectionModel = opList.getSelectionModel(); 
		getOperationSelection opSelection = new getOperationSelection();
        listSelectionModel.addListSelectionListener(opSelection);
    	opList.setSelectionModel(listSelectionModel);
    	
		opListPanel.add(opList, BorderLayout.LINE_START);
		
		listPanel.add(attListPanel);
		listPanel.add(opListPanel);
		
		bigPanel.add(labelPanel);
		bigPanel.add(listPanel);
			
		return bigPanel;
	}
	
	/*
	 * Class which handles the list selection for the operation List.
	 * I had to create this class because I could not find a good way of
	 * distinguishing between ListSelectionEvents coming from different 
	 * lists that are registered to the same listener.
	 */
	class getOperationSelection implements ListSelectionListener{

		public getOperationSelection(){

		}
		public void valueChanged(ListSelectionEvent e){
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();

			//System.out.println("maddeeee it");


			int firstIndex = e.getFirstIndex();
			int lastIndex = e.getLastIndex();
			boolean isAdjusting = e.getValueIsAdjusting(); 
			//e.getSource().getClass().

			if (lsm.isSelectionEmpty()) {
				System.out.println(" <none>");
			} else {
				// Find out which indexes are selected.
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();


				for (int i = minIndex; i <= maxIndex; i++) {

					// if (lsm.isSelectedIndex(i) && last != i) {
					criteriaBuild = criteriaField.getText();
					criteriaBuild = criteriaBuild +" "+ opArray[i]+" ";
					criteriaField.setText(criteriaBuild);
					System.out.println("Selected Index: "+i);

					//}
					last = i;
				}
			}
			opList.clearSelection();
			criteriaField.requestFocus();
			criteriaField.setHorizontalAlignment(JTextField.LEFT);

		}
	}


}

