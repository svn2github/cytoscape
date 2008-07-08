

import cytoscape.Cytoscape;

import cytoscape.task.util.TaskManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
import java.util.Set;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.*;

import javax.swing.WindowConstants.*;
import javax.swing.border.*;
import javax.swing.text.Position;





public class BooleanSettingsDialog extends JDialog implements ActionListener, TableModelListener {

	private BooleanAlgorithm currentAlgorithm = null;
	private BooleanCalculator calculator = null;
	private String value, criteria = "";
	private String[] attributes = {""};
	private int listCount = 0;
	String[] parsedCriteria;
	String test = "test";
	
	private JButton vizButton = null;
	private JLabel titleLabel; // Our titl
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JComboBox algorithmSelector; // Which algorithm we're using
	private JPanel algorithmPanel; // The panel this algorithm uses
	private JPanel tablePanel;
	private JTable table;
	JTextField field;
	private JPanel panel;
	private JList list;
	
	String[] columnNames = {"Label",
            "Criteria",
            "Color",
            };
	
	Object[][] data = new Object[4][3];
	/*{
		    {"Mary", "Campione", "Snowboarding"},
		    {"Alison", "Huml", "Rowing"},
		    {"Kathy", "Walrath", "Knitting"},
		};*/	
	
	
	public BooleanSettingsDialog(BooleanAlgorithm algorithm) {
		super(Cytoscape.getDesktop(), algorithm.getName(), false);
		currentAlgorithm = algorithm;
		calculator = new BooleanCalculator();
		initializeOnce(); // Initialize the components we only do once
		//init();
	}
	
	public void actionPerformed(ActionEvent e) {
		// Are we the source of the event?
		//System.out.println("action");
		
		
		String command = e.getActionCommand();
		if (command.equals("add")){
			//System.out.println(currentAlgorithm.getSettings().get("criteriaField").getValue().toString());
			criteria = currentAlgorithm.getSettings().get("criteriaField").getValue().toString();
			
			if(calculator.checkCriteria(criteria)){ 
				value = appendValue(criteria);
				populateList(criteria, value);
			}
		}
		if (command.equals("exit")) {
			setVisible(false);
		} else if (command.equals("apply")) {
			updateAllSettings(true);
			applyCriteria();
			
			//System.out.println(currentAlgorithm.getSettings().get("operationsList").valueChanged());
			
			
			//System.out.println(attributes[Integer.parseInt(currentAlgorithm.getSettings().getValue("attributeList"))]);
		} else if (command.equals("add")) {
			// Cluster using the current layout
			updateAllSettings();
			
			
		} else if (command.equals("save")) {
			updateAllSettings();
			//currentAlgorithm.getSettings().get("criteriaField").
		} else if (command.equals("cancel")) {
			// Call revertSettings for each
			
			
			revertAllSettings();
			setVisible(false);
		} else {
			// OK, initialize and display
			initialize();
			pack();
			setLocationRelativeTo(Cytoscape.getDesktop());
			setVisible(true);
		}
	}
	
	public void tableChanged(TableModelEvent e){
		int row = e.getFirstRow();
		//System.out.println(row + e.getLastRow());
	}
	
	private void init(){
		String labelLocation = BorderLayout.LINE_START;
		String fieldLocation = BorderLayout.LINE_END;
		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		list = new JList(Cytoscape.getNodeAttributes().getAttributeNames());
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(200,100));
		panel.add(listScroller, fieldLocation);
		
		
		ListSelectionModel model = null;
		model = list.getSelectionModel();
	    //model.addListSelectionListener(this);
		
	    field = new JTextField();
	    
	    //panel.add(list);
	    panel.add(field);
	    setContentPane(panel);
	    
	    
	    
	}
	
	
	private void initializeOnce() {
		
		attributes = Cytoscape.getNodeAttributes().getAttributeNames();
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		// Create a panel for algorithm's content
		this.algorithmPanel = currentAlgorithm.getSettingsPanel();

		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		//TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder,"Settings");
		//titleBorder.setTitlePosition(TitledBorder.LEFT);
		//titleBorder.setTitlePosition(TitledBorder.TOP);
		//algorithmPanel.setBorder(titleBorder);
		mainPanel.add(algorithmPanel);


		
		// Create a panel for our button box
		this.buttonBox = new JPanel();

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
		buttonBox.add(applyButton);
		
		buttonBox.add(saveButton);
		buttonBox.add(exitButton);
		
		
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		
		
		
		this.tablePanel = new JPanel();
		
		this.table = new JTable(data, columnNames);
		//System.out.println("made table");
		//table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		//JScrollPane scrollPane = new JScrollPane(table);
		//table.setFillsViewportHeight(true);
		//setPreferredSize(new Dimension(450, 110));
		
		//add(scrollPane, BorderLayout.CENTER);
		
		this.tablePanel.setLayout(new BorderLayout());
		this.tablePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		this.tablePanel.add(table, BorderLayout.CENTER);
		
		this.tablePanel.add(table);
		
		
		mainPanel.add(buttonBox);
		mainPanel.add(tablePanel);
		
		setContentPane(mainPanel);
		//System.out.println("made window");
		
	}

	private void initialize() {
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
	
	
	
	public String appendValue(String criteria){
		String cleanCriteria = "";
		String[] temp = calculator.parseCriteria(criteria);
		for(int i=0;i<temp.length;i++){
			//System.out.println("PARSER: "+parsedCriteria[i]);
			cleanCriteria = temp[i] + " ";
		}
		return cleanCriteria;
	}
	
	public void applyCriteria(){
		parsedCriteria = calculator.parseCriteria(criteria);
		calculator.evaluateCriteria(parsedCriteria);
	}
	
	public void populateList(String criteria, String label){
		
		//System.out.println("populate List");
		//data = new Object[listCount+1][3];
		data[listCount][0] = criteria;
		data[listCount][1] = label;
		data[listCount][2] = "";
		initializeOnce();
		listCount++;
	}
	

	
	public void removeListElement(){
	}
		
	public void removeRow(){
	}
	
	
	    
	}

	

