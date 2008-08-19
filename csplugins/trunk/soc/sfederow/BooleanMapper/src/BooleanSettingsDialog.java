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





public class BooleanSettingsDialog extends JDialog implements ActionListener, FocusListener, TableModelListener {

	private BooleanAlgorithm currentAlgorithm = null;
	private BooleanCalculator calculator = null;
	private Color currentColor = Color.GRAY;
	private String value, criteria = "";
	String[] parsedCriteria;
	String[] setNames;
	String test = "test";
	
	private JButton colorButton;
	private JColorChooser colorChooser;
	private JComboBox nameBox;
	private JDialog dialog;
	private JTextField colorField;
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JPanel tableButtons;
	private JPanel algorithmPanel; // The panel this algorithm uses
	private JPanel tablePanel;
	private JPanel colorPanel;
	private JTable table;
	
	AttributeManager attributeManager;
	//ColorMapper cmapper = new ColorMapper();
	CriteriaTablePanel criteriaTable;
		
	
	public BooleanSettingsDialog(BooleanAlgorithm algorithm) {
		super(Cytoscape.getDesktop(), algorithm.getName(), false);
		
		currentAlgorithm = algorithm;
		calculator = new BooleanCalculator();
		criteriaTable = new CriteriaTablePanel();
		
		initialize(); 
		
		
		
		
		
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
		        button.setBorderPainted(false);

			
			dialog = JColorChooser.createDialog(button,
                    "Pick a Color",
                    true,  //modal
                    colorChooser,
                    this,  //OK button handler
                    null); //no CANCEL button handler
			dialog.add(button);
			dialog.setVisible(true);

		}
		if(command.equals("OK")){
			
			System.out.println(colorChooser.getColor());
			currentColor = colorChooser.getColor();
			//colorButton.setBackground(colorChooser.getColor());
		}
		
		if(command.equals("edit")){
			System.out.println("found it");
		}
		
		if (command.equals("add")){
			
			//System.out.println(currentAlgorithm.getSettings().get("criteriaField").getValue().toString());
			criteria = currentAlgorithm.getSettings().get("criteriaField").getValue().toString();
			
			System.out.println("ADD CRITERIA: "+criteria);
			if(calculator.parse2(criteria) != null){ 
				
				value = criteria;
					//calculator.cleanCritera();
				//System.out.println("somewhwere"+value);
				criteriaTable.populateList(criteria, value, currentColor);
				initialize();
				
			}
			//calculator.clearList();
		}
		if (command.equals("exit")) {
			setVisible(false);
		} else if (command.equals("apply")) {
			updateAllSettings(true);
			//criteria = currentAlgorithm.getSettings().get("criteriaField").getValue().toString();
			
			//ColorMapper mapper = new ColorMapper("test", "continuous");
			applyCriteria();
			//String 
			//calculator.evaluate(parsedCriteria);
			//System.out.println(currentAlgorithm.getSettings().get("operationsList").valueChanged());
			
			
			//System.out.println(attributes[Integer.parseInt(currentAlgorithm.getSettings().getValue("attributeList"))]);
			
			
		} else if (command.equals("save")) {
			System.out.println(nameBox.getSelectedItem());
			String[] names = new String[nameBox.getItemCount()];
			for(int i=0; i<nameBox.getItemCount(); i++){
				names[i] = (String)nameBox.getItemAt(i);
			}
			
			
		} else if (command.equals("cancel")) {
			// Call revertSettings for each
			
			
			revertAllSettings();
			setVisible(false);
		} else {
			// OK, initialize and display
			//initialize();
			pack();
			setVisible(true);
		}
	}
	
	public void tableChanged(TableModelEvent e){
		int row = e.getFirstRow();
		System.out.println(row + e.getLastRow());
	}
	
	
	private void initialize() {
				
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		JPanel setNamePanel = getCriteriaSetPanel(); //new CriteriaSetPanel();
		mainPanel.add(setNamePanel);
		
		// Create a panel for algorithm's content
		this.algorithmPanel = currentAlgorithm.getSettingsPanel();

		mainPanel.add(algorithmPanel);
		mainPanel.addFocusListener(this);
		
		//Panel for color Button
		this.colorPanel = new JPanel(new BorderLayout(0,2));
		
		
		JLabel colorLabel = new JLabel();
		colorField = new JTextField();
		colorField.setPreferredSize(new Dimension(200, 20));
		colorField.setBackground(currentColor);
		colorField.setActionCommand("chooseColor");
		colorField.addActionListener(this);
		//colorButton = new JButton("Choose Color");
		//colorButton.setBackground(currentColor);
		//colorButton.setActionCommand("chooseColor");
		//colorButton.addActionListener(this);
		
		colorPanel.add(colorLabel, BorderLayout.LINE_START);
		colorPanel.add(colorField, BorderLayout.LINE_END);
		colorPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
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
		
		
		buttonBox.add(addButton);		
		buttonBox.add(saveButton);
		buttonBox.add(exitButton);
		buttonBox.add(applyButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonBox.addFocusListener(this);
		
		
		
		this.tableButtons = new JPanel();
		
		JButton moveUpButton = new JButton("Move Up");
		moveUpButton.setActionCommand("moveUp");
		moveUpButton.addActionListener(this);
		
		JButton moveDownButton = new JButton("Move Down");
		moveDownButton.setActionCommand("moveDown");
		moveDownButton.addActionListener(this);
		
		JButton deleteButton = new JButton("Delete");
		deleteButton.setActionCommand("delete");
		deleteButton.addActionListener(this);
		
		tableButtons.add(moveUpButton);
		tableButtons.add(moveDownButton);
		tableButtons.add(deleteButton);
		
		
		
		tablePanel = criteriaTable.getTablePanel();
		
		tablePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		
		
		mainPanel.add(colorPanel);
		mainPanel.add(buttonBox);
		mainPanel.add(tablePanel);
	
		setContentPane(mainPanel);
		
		mainPanel.setLocation(Cytoscape.getDesktop().getWidth(), Cytoscape.getDesktop().getHeight());
		setLocation(2,Cytoscape.getDesktop().getHeight()-557);
	}
	
	public JPanel getCriteriaPanel(){
		JPanel criteriaPanel = new JPanel();
		/* 
		BoxLayout box = new BoxLayout(criteriaPanel, BoxLayout.Y_AXIS);
		criteriaPanel.setLayout(box);
		
		Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, "Criteria");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		criteriaPanel.setBorder(titleBorder);
		
		String labelLocation = BorderLayout.LINE_START;
		String fieldLocation = BorderLayout.LINE_END;
		
		JPanel fieldPanel = new JPanel(new BorderLayout(0, 2));
		JLabel fieldLabel = new JLabel("Name"); 
		//JTextField
		nameBox.setEditable(true);
		nameBox.setPreferredSize(new Dimension(200,20));
		nameBox.setActionCommand("listChanged");
		nameBox.addActionListener(this);
		fieldPanel.add(setLabel, labelLocation);
		namePanel.add(nameBox, fieldLocation);
		
		JPanel mapPanel = new JPanel(new BorderLayout(0, 2));
		JLabel mapLabel = new JLabel("Map To");
		JComboBox mapToBox = new JComboBox(new String[] {"Node Color", "Node Border Color", "None" });
		mapPanel.add(mapLabel, labelLocation);
		mapPanel.add(mapToBox, fieldLocation);
		
		setPanel.add(namePanel);
		setPanel.add(mapPanel);
		*/
		return criteriaPanel;
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
	
	public JPanel getCriteriaSetPanel(){
		//JPanel setPanel = new JPanel(new BorderLayout(0, 2));
		
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
		nameBox = new JComboBox(new String[] {"amitabha buddha", "avalokiteshvara"});//getCriteriaSetNames());
		nameBox.setEditable(true);
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
	
	public String[] getCriteriaSetNames(){
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		if(networkAttributes.hasAttribute(Cytoscape.getCurrentNetwork().toString(), "Criteria")){
			List temp = networkAttributes.getListAttribute(Cytoscape.getCurrentNetwork().toString(), "Criteria");
			return (String[])temp.toArray();
		}
		return new String[] {"              "};
	}
	
	public void makeCriteriaSet(String[] names){
		
	}
	
	public void applyCriteria(){
		//for(int i=1;i<data.length;i++){
			BooleanScanner scan = new BooleanScanner();
		    //int[] rowIndexes = table.getSelectedRows();
		    //for(int i = 0; i<rowIndexes.length;i++){
		    	//System.out.println("row index: "+rowIndexes[i]);
		    //}
			String current = (String)criteriaTable.getCell(0,0); 
			//System.out.println("current: "+ current);
			/*try{
			scan.parse(current);
			}catch (IOException e) {
				System.out.println(e.getMessage());
			}
			*/
			ArrayList<String>[] temp = calculator.parse2(current);
			calculator.clearList();
			calculator.evaluate("label", temp[0], temp[1]);
		//}
		//parsedCriteria = calculator.parseCriteria(criteria);
		
		
		
		//calculator.evaluateCriteria(parsedCriteria);
	}
	
	public void focusGained(FocusEvent e){
		System.out.println(e.toString());
	}
	
	public void focusLost(FocusEvent e){
		System.out.println(e.toString());
	}



}

	
	
	


