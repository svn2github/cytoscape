package src;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.visual.*;
import cytoscape.task.util.TaskManager;

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
	String test = "test";
	
	private JButton colorButton;
	private JColorChooser colorChooser;
	private JDialog dialog;
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JPanel tableButtons;
	private JPanel algorithmPanel; // The panel this algorithm uses
	private JPanel tablePanel;
	private JPanel colorPanel;
	private JTable table;
	
	
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
			if(calculator.parse2(criteria)){ 
				
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
			
			
			applyCriteria();
			//String 
			//calculator.evaluate(parsedCriteria);
			//System.out.println(currentAlgorithm.getSettings().get("operationsList").valueChanged());
			
			
			//System.out.println(attributes[Integer.parseInt(currentAlgorithm.getSettings().getValue("attributeList"))]);
			
			
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
			setVisible(true);
		}
	}
	
	public void tableChanged(TableModelEvent e){
		int row = e.getFirstRow();
		System.out.println(row + e.getLastRow());
	}
	
	
	private void initialize() {
		
		 
	     /*able2 = new JTable(dataModel);
	     table2.setPreferredScrollableViewportSize(new Dimension(110, 80));
	     table2.setFillsViewportHeight(true);
	     //table2.setDefaultRenderer(Color.class, new ColorRenderer(true));
	     table2.getColumn("Color").setCellRenderer(new ColorRenderer(true));
	     
	      JScrollPane scrollpane = new JScrollPane(table2);
	     */
	     
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		// Create a panel for algorithm's content
		this.algorithmPanel = currentAlgorithm.getSettingsPanel();

		mainPanel.add(algorithmPanel);
		mainPanel.addFocusListener(this);
		
		//Panel for color Button
		this.colorPanel = new JPanel();
		
		colorButton = new JButton("Choose Color");
		colorButton.setBackground(currentColor);
		colorButton.setActionCommand("chooseColor");
		colorButton.addActionListener(this);
		
		colorPanel.add(colorButton);
		colorPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		//Create a panel for our button box
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
		
		
				
		
		
		JColorChooser chooser = new JColorChooser();
		AbstractColorChooserPanel cpane = null;
		
		Object a = "a";
		byte b = 0;
		JDialog dialog = new JDialog();
		CyNetwork work = Cytoscape.getCurrentNetwork();
		cytoscape.visual.mappings.ContinuousMapping mapping = new cytoscape.visual.mappings.ContinuousMapping(a,b);
		//mainPanel.add(mapping.getUI(dialog, work));
		//cytoscape.visual.mappings.
		//lorChooserComponentFactory factory = null;
		//faultColorSelectionModel mod = new DefaultColorSelectionModel();
		
		 // JComponent newContentPane = new CriteriaTablePanel();
	       // newContentPane.setOpaque(true); //content panes must be opaque
	        //setContentPane(newContentPane);


		
		
		tablePanel = criteriaTable.returnTablePanel();
		tablePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		
		//mainPanel.add();
		mainPanel.add(colorPanel);
		mainPanel.add(buttonBox);
		//mainPanel.add(tablePanel);
		
		mainPanel.add(tablePanel);
	//mainPanel.add(tableButtons);
		//mainPanel.add(chooser);
		setContentPane(mainPanel);
		//System.out.println("made window");
		mainPanel.setLocation(Cytoscape.getDesktop().getWidth(), Cytoscape.getDesktop().getHeight());
		setLocation(2,Cytoscape.getDesktop().getHeight()-557);
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
	
	
	
	/*public String appendValue(String criteria){
		String cleanCriteria = "";
		String[] temp = calculator.parseCriteria(criteria);
		for(int i=0;i<temp.length;i++){
			//System.out.println("PARSER: "+parsedCriteria[i]);
			cleanCriteria = cleanCriteria + " "+temp[i];
		}
		return cleanCriteria;
	}*/
	
	public void applyCriteria(){
		//for(int i=1;i<data.length;i++){
			BooleanScanner scan = new BooleanScanner();
		    //int[] rowIndexes = table.getSelectedRows();
		    //for(int i = 0; i<rowIndexes.length;i++){
		    	//System.out.println("row index: "+rowIndexes[i]);
		    //}
			String current = (String)criteriaTable.returnCellValue(0,0); 
			//System.out.println("current: "+ current);
			/*try{
			scan.parse(current);
			}catch (IOException e) {
				System.out.println(e.getMessage());
			}
			*/
			calculator.parse2(current);
			calculator.clearList();
			calculator.evaluate();
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

	
	
	


