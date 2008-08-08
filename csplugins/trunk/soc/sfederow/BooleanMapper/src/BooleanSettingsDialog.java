

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


import javax.swing.WindowConstants.*;
import javax.swing.border.*;
import javax.swing.text.Position;





public class BooleanSettingsDialog extends JDialog implements ActionListener, FocusListener, TableModelListener {

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
	private JPanel tableButtons;
	private JComboBox algorithmSelector; // Which algorithm we're using
	private JPanel algorithmPanel; // The panel this algorithm uses
	private JPanel tablePanel;
	private JTable table;
	private JTable table2;
	JTextField field;
	private JPanel panel;
	private JList list;
	
	BooleanTableModel dataModel;
	
	String[] columnNames = {"Criteria",
            "Label",
            "Color",
            };
	
	
	
	//String[][] data = new String[6][3];
	//
	
	/*{
		    {"Mary", "Campione", "Snowboarding"},
		    {"Alison", "Huml", "Rowing"},
		    {"Kathy", "Walrath", "Knitting"},
		};*/	
	
		
	
	
	
	public BooleanSettingsDialog(BooleanAlgorithm algorithm) {
		super(Cytoscape.getDesktop(), algorithm.getName(), false);
		currentAlgorithm = algorithm;
		
		for(int b=0;b<4;b++){
			for(int c=0;c<3;c++){
				//data[b][c] = "";
			}
		}
		
		
		dataModel = new BooleanTableModel();
	
		initialize(); // Initialize the components we only do once
		calculator = new BooleanCalculator();
		//System.out.println(Cytoscape.getDesktop().getHeight());
		setLocation(2,Cytoscape.getDesktop().getHeight()-557);
		
		
		//setLocationRelativeTo(Cytoscape.getDeskto
		//this.setLocation(0, this.getHeight());
		//System.out.println("height: "+this.getHeight()+" width: "+this.getWidth());
		//init();
	}
	
	public void actionPerformed(ActionEvent e) {
		// Are we the source of the event?
		//System.out.println("action");
		
		
		String command = e.getActionCommand();
		if(command.equals("delete")){
			
			int row = table2.getSelectedRow();
			//data[row][0] = "";
			dataModel.data[row][0] = "";
			dataModel.data[row][1] = "";
			if(dataModel.rowCount > 5){
				dataModel.rowCount--;
			}
			dataModel.createNewDataObject();
			listCount--;
			initialize();
			
		}
		
		if (command.equals("moveUp")){
			if(table.getSelectedRowCount() != 0){
				moveRowUp(table.getSelectedRow());
			}
		}
		if (command.equals("moveDown")){
			if(table.getSelectedRowCount() != 0){	
				moveRowDown(table.getSelectedRow());
			}
		}
		if (command.equals("add")){
			
			//System.out.println(currentAlgorithm.getSettings().get("criteriaField").getValue().toString());
			criteria = currentAlgorithm.getSettings().get("criteriaField").getValue().toString();
			
			System.out.println("ADD CRITERIA: "+criteria);
			if(calculator.parse2(criteria)){ 
				
				value = criteria;
					//calculator.cleanCritera();
				//System.out.println("somewhwere"+value);
				populateList(criteria, value);
				
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
			//initialize();
			pack();
			setLocation(2,Cytoscape.getDesktop().getHeight()-557);
			setVisible(true);
		}
	}
	
	public void tableChanged(TableModelEvent e){
		int row = e.getFirstRow();
		System.out.println(row + e.getLastRow());
	}
	
	
	private void initialize() {
		
		 
	     table2 = new JTable(dataModel);
	     table2.setPreferredScrollableViewportSize(new Dimension(110, 80));
	     table2.setFillsViewportHeight(true);
	     //table2.setDefaultRenderer(Color.class, new ColorRenderer(true));
	     table2.getColumn("Color").setCellRenderer(new ColorRenderer(true));
	     
	      JScrollPane scrollpane = new JScrollPane(table2);
	     //scrollpane.getC
	      //ColorRenderer renderer = new ColorRenderer(true);
	      //Component rendered = renderer.getTableCellRendererComponent(table2, Color.BLUE,
			//true, true,
			//0, 2);
	      //table2.getColumn("Color").setCellRenderer(renderer);
		//attributes = Cytoscape.getNodeAttributes().getAttributeNames();
		
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

		mainPanel.addFocusListener(this);
		
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
		
		
		buttonBox.add(saveButton);
		buttonBox.add(exitButton);
		buttonBox.add(applyButton);
		
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		
		buttonBox.addFocusListener(this);
		
		this.tablePanel = new JPanel();
		
		this.table = new JTable(dataModel.data, columnNames);
		//table.
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
		
		tableButtons.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JColorChooser chooser = new JColorChooser();
		AbstractColorChooserPanel cpane = null;
		
		Object a = "a";
		byte b = 0;
		JDialog dialog = new JDialog();
		CyNetwork work = Cytoscape.getCurrentNetwork();
		cytoscape.visual.mappings.ContinuousMapping mapping = new cytoscape.visual.mappings.ContinuousMapping(a,b);
		//mainPanel.add(mapping.getUI(dialog, work));
		//cytoscape.visual.mappings.
		ColorChooserComponentFactory factory = null;
		DefaultColorSelectionModel mod = new DefaultColorSelectionModel();
		
		
		JPanel tpanel = new JPanel();
		

		//scrollpane.setPreferredSize(new Dimension(110,102));
		tpanel.setLayout(new BorderLayout());
		tpanel.add(scrollpane);
		
		//mainPanel.add();
		mainPanel.add(buttonBox);
		//mainPanel.add(tablePanel);
		mainPanel.add(tpanel);
		mainPanel.add(tableButtons);
		//mainPanel.add(chooser);
		setContentPane(mainPanel);
		//System.out.println("made window");
		mainPanel.setLocation(Cytoscape.getDesktop().getWidth(), Cytoscape.getDesktop().getHeight());
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
		    int[] rowIndexes = table.getSelectedRows();
		    for(int i = 0; i<rowIndexes.length;i++){
		    	//System.out.println("row index: "+rowIndexes[i]);
		    }
			String current = (String)dataModel.data[0][0]; 
			//System.out.println("current: "+ current);
			try{
			scan.parse(current);
			}catch (IOException e) {
				System.out.println(e.getMessage());
			}
			//calculator.parse2(current);
			//calculator.clearList();
			//calculator.evaluate();
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
	public void populateList(String criteria, String label){
		
		//System.out.println("populate List");
		//data = new Object[listCount+1][3];
		//table.
		if(listCount < 5){
			dataModel.data[listCount][0] = criteria;
			dataModel.data[listCount][1] = label;
			dataModel.data[listCount][2] = Color.BLUE;
			initialize();
		
			listCount++;
		}else{
			//dataModel.
			listCount++;
			System.out.println("rows appending");
			dataModel.setRowCount(listCount);
			//dataModel.rowCount = listCount;
			
			dataModel.createNewDataObject();
			dataModel.data[listCount-1][0] = criteria;
			dataModel.data[listCount-1][1] = label;
			dataModel.data[listCount-1][2] = "";
			initialize();
			
		}
	}
	
	
	
	public void moveRowUp(int rowNumber){
		if(rowNumber != 0){
			
			
			/*
			String criteriaTemp = data[rowNumber][0];
			String labelTemp = data[rowNumber][1];
			String colorTemp = data[rowNumber][2];
			*/
			Object criteriaTemp = dataModel.data[rowNumber][0];
			Object labelTemp = dataModel.data[rowNumber][1];
			Object colorTemp = dataModel.data[rowNumber][2];
			
			/*
			table.getModel().setValueAt(data[rowNumber-1][0], rowNumber, 0);  
			table.getModel().setValueAt(data[rowNumber-1][1], rowNumber, 1);
			System.out.println("moving Something");
			table.getModel().setValueAt(criteriaTemp, rowNumber-1, 0);
			table.getModel().setValueAt(labelTemp, rowNumber-1, 0);
			
			table.setValueAt(data[rowNumber-1][0], rowNumber, 0);
			table.setValueAt(data[rowNumber-1][1], rowNumber, 1);
			System.out.println("moving Something");
			table.setValueAt(criteriaTemp, rowNumber-1, 0);
			table.setValueAt(labelTemp, rowNumber-1, 0);
			*/
			 
			dataModel.data[rowNumber][1] = dataModel.data[rowNumber-1][1];
			dataModel.data[rowNumber][2] = dataModel.data[rowNumber-1][2];
			
			dataModel.data[rowNumber][0] = dataModel.data[rowNumber-1][0];
			dataModel.data[rowNumber][1] = dataModel.data[rowNumber-1][1];
			dataModel.data[rowNumber][2] = dataModel.data[rowNumber-1][2];
			dataModel.data[rowNumber-1][0] = criteriaTemp;
			dataModel.data[rowNumber-1][1] = labelTemp;
			dataModel.data[rowNumber-1][2] = colorTemp;
			initialize();
			
		}
	}
	
	public void moveRowDown(int rowNumber){
		if(rowNumber != 5){
			
			
			
			Object criteriaTemp = dataModel.data[rowNumber][0];
			Object labelTemp = dataModel.data[rowNumber][1];
			Object colorTemp = dataModel.data[rowNumber][2];
			
			//System.out.println(rowNumber);
			//table.setValueAt(data[rowNumber+1][0], rowNumber, 0);
			//table.setValueAt(data[rowNumber+1][1], rowNumber, 1);
			//System.out.println("moving Something");
			//table.setValueAt("new value", rowNumber+1, 0);
			//table.setValueAt("new value", rowNumber+1, 0);
			
			
			dataModel.data[rowNumber][0] = dataModel.data[rowNumber+1][0];
			dataModel.data[rowNumber][1] = dataModel.data[rowNumber+1][1];
			dataModel.data[rowNumber][2] = dataModel.data[rowNumber+1][2];
			dataModel.data[rowNumber+1][0] = criteriaTemp;
			dataModel.data[rowNumber+1][1] = labelTemp;
			dataModel.data[rowNumber+1][2] = colorTemp;
			initialize();
			
		}
		
	}
	class BooleanTableModel extends AbstractTableModel {

		//TableModel dataModel = new AbstractTableModel() {
		int colCount = 3;
		int rowCount = 5;
		String[] columnNames = { "Criteria", "Label", "Color" };
		Object[][] data = new Object[rowCount][colCount];

		public void createNewDataObject(){ 
			Object[][] temp = data;
			this.data = new Object[rowCount][colCount];
			for(int i=0; i<rowCount-1; i++){
				for(int j=0; j<colCount; j++){
					this.data[i][j] = temp[i][j];
				}
			}
		}
		
		
		public String getColumnName(int i){ return columnNames[i]; }
		public void setColumnCount(int count) { colCount = count; }
		public void setRowCount(int count) { rowCount = count; }
		public int getColumnCount() { return colCount; }
		public int getRowCount() { return rowCount;}
		public Object getValueAt(int row, int col) { return data[row][col]; }
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}

}


	class ColorRenderer extends JLabel implements TableCellRenderer {
		Border unselectedBorder = null;
		Border selectedBorder = null;
		boolean isBordered = true;

		public ColorRenderer(boolean isBordered) {
			this.isBordered = isBordered;
			setOpaque(true); //MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Color newColor = (Color)color;
		setBackground(newColor);
		if (isBordered) {
			if (isSelected) {
				if (selectedBorder == null) {
					selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
							table.getSelectionBackground());
				}
				setBorder(selectedBorder);
			} else {
				if (unselectedBorder == null) {
					unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
							table.getBackground());
				}
				setBorder(unselectedBorder);
			}
		}

		//setToolTipText("RGB value: " + newColor.getRed() + ", "
			//	+ newColor.getGreen() + ", "
				//+ newColor.getBlue());
		return this;
	}
}


