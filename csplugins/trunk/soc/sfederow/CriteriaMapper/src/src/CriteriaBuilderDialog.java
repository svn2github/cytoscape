package src;

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
import java.awt.Image;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.colorchooser.*;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

import src.BooleanSettingsDialog.getOperationSelection;



public class CriteriaBuilderDialog extends JDialog implements ActionListener, ListSelectionListener{
	
	private JFrame mainFrame;
	private JDialog mainDialog;
	private JPanel mainPanel;
	private JButton colorButton;
	private JColorChooser colorChooser;
	
	JTextField criteriaField;
	JTextField labelField;
	private JDialog dialog;
	private JList attList;
	private JList opList;
	JComboBox mapToBox;
	
	CriteriaTablePanel panelPointer;
	
	Color currentColor = Color.WHITE; //keeps track of the color displayed by the color button
	String label = ""; 
	String criteria = "";
	String mapTo = "";
	private ArrayList<String> allCurrentLabels = new ArrayList<String>();
	private ArrayList<String> attributeList = new ArrayList<String>(); //List which holds all of the attributes
	private String[] opArray = {"=", "<", ">", ">=", "<=", "AND", "OR", "NOT"}; 
	private String[] attributesArray;
	
	int currentRow;
	
	
	public CriteriaBuilderDialog(CriteriaTablePanel panel){
		
		panelPointer = panel;
		
	}
	
	
	public void initialize(int row){
		
		currentRow = row;
		mainDialog = new JDialog();
		mainPanel = new JPanel();
		
		/*JPanel labelPanel = new JPanel(new BorderLayout(0, 2));
		JLabel label = new JLabel("Label"); 
		labelField = new JTextField();
		labelField.setPreferredSize(new Dimension(200, 20));
		labelPanel.add(label, BorderLayout.LINE_START);
		labelPanel.add(labelField, BorderLayout.LINE_END);
		labelField.setHorizontalAlignment(JTextField.LEFT);
		*/
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		//mainPanel.setPreferredSize(new Dimension(Cytoscape.getDesktop().getWidth(), 450));
		
		//mainPanel.add(labelPanel);
		//JPanel pan = getListPanel();
		//pan.setSize(new Dimension(300,100));
		//mainPanel.add(getListPanel());
		mainPanel.add(getCriteriaChooserPanel());
		mainPanel.add(getListPanel());
		
		Image builderIcon = new ImageIcon(CriteriaBuilderDialog.class.getResource("img/stock_form-properties.png")).getImage();
        //if (builderIconURL != null) {
          //  builderIcon = new ImageIcon(builderIconURL).getImage();
        //}
        mainFrame = new JFrame();
        mainFrame.setIconImage(builderIcon);
        //mainDialog = new JDialog(mainFrame);
		
		
		
		mainDialog.setContentPane(mainPanel);
		mainDialog.setVisible(true);
		mainDialog.setLocation(360,Cytoscape.getDesktop().getHeight()-250);
		mainDialog.setSize(new Dimension(300,256));
        
		//mainDialog.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e){
    	
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
			colorButton.setBackground(currentColor);
		    System.out.println(currentColor);
    	}
		
    	if(command.equals("mapToListChanged")){
			
			
		}
    	if(command.equals("CBsave")){
    		label = labelField.getText();
    		criteria = criteriaField.getText();
    		mapTo = (String)mapToBox.getSelectedItem();
    		mainDialog.setVisible(false);
    		
    	}
        
        //setVisible(true);
    	
	}
	
	private JPanel getListPanel(){
		JPanel bigPanel = new JPanel();
		
		BoxLayout bigBox = new BoxLayout(bigPanel, BoxLayout.Y_AXIS);
		bigPanel.setLayout(bigBox);
		
		
		Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, "");
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
            	//if(strings.length == 8){ return 9; }
            	//else{ 
            		return strings.length; //}
            }
            public Object getElementAt(int i) { return strings[i]; }
        });
		ListSelectionModel listSelectModel = attList.getSelectionModel();        
        listSelectModel.addListSelectionListener(this);
        
    	attList.setSelectionModel(listSelectModel);
    	//attList.setMaximumSize(new Dimension(60,50));
    	//attListPanel.add(attList, BorderLayout.LINE_START);
		
    	JScrollPane attpane = new JScrollPane();
		attpane.setViewportView(attList);
		attpane.setPreferredSize(new Dimension(125,100));
		attListPanel.add(attpane, BorderLayout.LINE_START);
		
    	
		JPanel opListPanel = new JPanel(new BorderLayout(0,2));
		opList = new JList(opArray);
		ListSelectionModel listSelectionModel = opList.getSelectionModel(); 
		getOperationSelection opSelection = new getOperationSelection();
        listSelectionModel.addListSelectionListener(opSelection);
    	opList.setSelectionModel(listSelectionModel);
    	
    	JScrollPane oppane = new JScrollPane();
		oppane.setViewportView(opList);
		oppane.setPreferredSize(new Dimension(125,100));
		opListPanel.add(oppane, BorderLayout.LINE_START);
		
    	//opListPanel.add(opList, BorderLayout.LINE_START);
		JPanel buttonBox = new JPanel();//new BorderLayout(0,2));
		
		JButton addButton = new JButton("Add");
		addButton.setActionCommand("CBadd");
		//addButton.addActionListener(this);
		addButton.addActionListener(panelPointer);
		
		JButton clearButton = new JButton("Clear");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);
		
		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("CBsave");
		addButton.addActionListener(this);
		addButton.addActionListener(panelPointer);
		
		buttonBox.add(addButton);//, labelLocation);
		buttonBox.add(clearButton);//, fieldLocation);
		buttonBox.add(doneButton);
		
		//attListPanel.setPreferredSize(new Dimension(60,50));
		listPanel.add(attListPanel);
		listPanel.add(opListPanel);
		
		
		bigPanel.add(labelPanel);
		bigPanel.add(listPanel);
		bigPanel.add(buttonBox);
		bigPanel.setMaximumSize(new Dimension(300,100));
		
		return bigPanel;
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
		TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, "Build Criteria");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		fieldPanel.setBorder(titleBorder);
		
		
		
		String labelLocation = BorderLayout.LINE_START;
		String fieldLocation = BorderLayout.LINE_END;
		
		
		JPanel labelPanel = new JPanel(new BorderLayout(0, 2));
		JLabel label = new JLabel("Label"); 
		labelField = new JTextField();
		labelField.setPreferredSize(new Dimension(215, 20));
		labelPanel.add(label, labelLocation);
		labelPanel.add(labelField, fieldLocation);
		labelField.setHorizontalAlignment(JTextField.LEFT);
		
		
		JPanel criteriaPanel = new JPanel(new BorderLayout(0, 2));
		JLabel criteriaLabel = new JLabel("Criteria");
		criteriaField = new JTextField();
		
		criteriaField.setPreferredSize(new Dimension(215, 20));
		criteriaField.setAutoscrolls(true);
		//criteriaField.setMaximumSize(new Dimension(1000, 20));
		criteriaPanel.add(criteriaLabel, labelLocation);
		criteriaPanel.add(criteriaField, fieldLocation);
		criteriaField.setHorizontalAlignment(JTextField.LEFT);
		
		JPanel mapToValuePanel = new JPanel();
		BoxLayout mvXbox = new BoxLayout(mapToValuePanel, BoxLayout.X_AXIS);
		mapToValuePanel.setLayout(mvXbox);
		
		JPanel mapToPanel = new JPanel(new BorderLayout(0,2));
		JLabel mtLabel = new JLabel("Map To");
		mapToPanel.add(mtLabel, labelLocation);
		
		String[] mappableAttributes = {"Node Color", "Node Size", "Node Shape"};
		mapToBox = new JComboBox(mappableAttributes);
		
		mapToBox.setEditable(false);
		mapToBox.setPreferredSize(new Dimension(100,20));
		mapToBox.setActionCommand("mapToListChanged");
		mapToBox.addActionListener(this);
		
		mapToPanel.add(mapToBox, fieldLocation);		
		
		mapToValuePanel.add(mapToPanel);
		
		
		
		/*
		//Make JPanel for colorPanel and button box panel
		JPanel colorAndButtonPanel = new JPanel();
		BoxLayout Xbox = new BoxLayout(colorAndButtonPanel, BoxLayout.X_AXIS);
		colorAndButtonPanel.setLayout(Xbox);
		*/
		
		//Make colorPanel for Color Label and color chooser button
		JPanel colorPanel = new JPanel(new BorderLayout(0, 2));
		//colorPanel.setPreferredSize(new Dimension(180,10));
		JLabel colorLabel = new JLabel(" Value");
		colorButton = new JButton("");
		colorButton.setBackground(currentColor);
		colorButton.setPreferredSize(new Dimension(60, 10));
		colorButton.setActionCommand("chooseColor");
		colorButton.addActionListener(this);
		colorButton.setBorder(null);
        colorButton.setBorderPainted(false);
        colorButton.setBackground(currentColor);
        
		colorPanel.add(colorLabel, labelLocation);
		colorPanel.add(colorButton, fieldLocation);
		mapToValuePanel.add(colorPanel);
		
		//Make button box JPanel for Add and Clear Buttons
		JPanel buttonBox = new JPanel();//new BorderLayout(0,2));
		JButton addButton = new JButton("Save");
		addButton.setActionCommand("save");
		addButton.addActionListener(this);
		JButton clearButton = new JButton("Clear");
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);
		
		buttonBox.add(addButton);//, labelLocation);
		buttonBox.add(clearButton);//, fieldLocation);
		
		
		
		
		fieldPanel.add(labelPanel);
		fieldPanel.add(criteriaPanel);
		fieldPanel.add(mapToValuePanel);
		//fieldPanel.add(buttonBox);
		//fieldPanel.add(colorAndButtonPanel);
		
		
		return fieldPanel;
	}
	
	/*
	 * Creates the attribute list and operation list.
	 */
	
	
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
			//System.out.println(" <none>");
		} else {
			// Find out which indexes are selected.
			int minIndex = lsm.getMinSelectionIndex();
			int maxIndex = lsm.getMaxSelectionIndex();


			for (int i = minIndex; i <= maxIndex; i++) {
				//if (lsm.isSelectedIndex(i) && last != i) {
				criteriaBuild = criteriaField.getText();
				if(attributesArray[i].contains(" ")){
					criteriaBuild = criteriaBuild +" \""+ attributesArray[i]+"\" ";
				}else{
					criteriaBuild = criteriaBuild +" "+ attributesArray[i]+" ";
				}
				criteriaField.setText(criteriaBuild);
				//System.out.println("Selected Index: "+i);
				//}
				last = i;
			}
		}
		attList.clearSelection();
		criteriaField.requestFocus();
		criteriaField.setHorizontalAlignment(JTextField.LEFT);

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
				//System.out.println(" <none>");
			} else {
				// Find out which indexes are selected.
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();


				for (int i = minIndex; i <= maxIndex; i++) {

					// if (lsm.isSelectedIndex(i) && last != i) {
					criteriaBuild = criteriaField.getText();
					criteriaBuild = criteriaBuild +" "+ opArray[i]+" ";
					criteriaField.setText(criteriaBuild);
					//System.out.println("Selected Index: "+i);

					//}
					last = i;
				}
			}
			opList.clearSelection();
			criteriaField.requestFocus();
			criteriaField.setHorizontalAlignment(JTextField.LEFT);

		}
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
			//attributeList.add(internalAttributes.get(i));
		}
	}
	
}
