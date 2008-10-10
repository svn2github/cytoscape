package src;

/*
 * Steve Federowicz 
 * Google Summer of Code
 * 
 * This set of classes contains all of the code necessary to generate and make the table portion of the 
 * GUI interactive. 
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import cytoscape.Cytoscape;

import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;




public class CriteriaTablePanel implements ActionListener, ListSelectionListener {
	private JPanel tablePanel;
	private JPanel tableButtons;
    private JTable table;
	private BooleanTableModel dataModel;
    private int listCount = 0;
    private ColorMapper mapper;
    private AttributeManager attManager;
    private ColorEditor colorEditor;
    
    public CriteriaTablePanel() {
    	dataModel = new BooleanTableModel();
    	mapper = new ColorMapper();
    	attManager = new AttributeManager();
    	colorEditor = new ColorEditor();
    	initializeTable();
    	
    }
    
   
    public JPanel getTablePanel(){
    	
    	
		return this.tablePanel;
    }
   
    /*
     * Initializes the table, tables buttons and adds it all to a JPanel which can be returned by the method
     * above to the construction of the larger GUI in BooleanSettingsDialog.
     */
    public void initializeTable(){
    	
    	tablePanel = new JPanel();
    	tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
    	table = new JTable(dataModel);
    	
    	/*
    	 * This is the code that causes to color editor to only pop up on a double
    	 * click of the color cell in the table.
    	 */
    	table.addMouseListener(new MouseAdapter() {
    		   public void mouseClicked(MouseEvent e) {
    		      if (e.getClickCount() == 2) {
    		         JTable target = (JTable)e.getSource();
    		         int row = target.getSelectedRow();
    		         int column = target.getSelectedColumn();
    		         if(column == 2){
    		        	 System.out.println("mouse");
    		        	 JColorChooser colorChooser = new JColorChooser();
    					 JButton button = new JButton();
    				     button.setActionCommand("edit");
    				     //button.addActionListener(this);
    				     button.setBorderPainted(true);
    					 JDialog dialog = JColorChooser.createDialog(button,
    		                    "Pick a Color",
    		                    true,  //modal
    		                    colorChooser,
    		                    null,  //OK button handler
    		                    null); //no CANCEL button handler
    					dialog.add(button);
    					dialog.setLocation(2,Cytoscape.getDesktop().getHeight()-385);
    					dialog.setVisible(true);
    					Color currentColor = colorChooser.getColor();
    					colorEditor.currentColor = currentColor;
    					initializeTable();
    		         }
    		         }
    		   }
    		});
        table.setPreferredScrollableViewportSize(new Dimension(265, 80));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        
        ListSelectionModel listSelectionModel = table.getSelectionModel();
        
        
        listSelectionModel.addListSelectionListener(this);
    	table.setSelectionModel(listSelectionModel);

        //Set up renderer and editor for the Favorite Color column.
      
        table.getColumn("Color").setCellRenderer(new ColorRenderer(true));
        //scrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        JScrollPane scrollPane = new JScrollPane(table);
        //table.setDefaultRenderer(Color.class, new ColorRenderer(true));
        table.setDefaultEditor(Color.class, colorEditor);
        table.getColumn("Color").setCellEditor(colorEditor);
        //Add the scroll pane to this panel.
     
        tablePanel.add(scrollPane);
    
        
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
		
		tablePanel.add(tableButtons);
		//scrollPane.setAlignmentY(TOP_ALIGNMENT);
		//tableButtons.setAlignmentY(BOTTOM_ALIGNMENT); 
    }
    
    
    /*
     * Handles all of the action events in this case the move up, move down, and delete buttons for altering the table.
     */
    public void actionPerformed(ActionEvent e){
    	
    	String command = e.getActionCommand();
    	
    	if(command.equals("delete")){
			
			int[] row = table.getSelectedRows();
			//data[row][0] = "";
			
			if(row.length == 1){
			try{
				attManager.removeColorAttribute(getCell(row[0], 1)+"");
			}catch (Exception failedDelete){
				System.out.println(failedDelete.getMessage());
			}
			dataModel.setValueAt("", row[0], 0);
			dataModel.setValueAt("", row[0], 1);
			dataModel.setValueAt(Color.white, row[0], 2);
			if(dataModel.rowCount > 5){
				dataModel.rowCount--;
				//dataModel.createNewDataObject(dataModel.rowCount, 3);
			}
			
			if(row[0] != 0){
				while(getCell(row[0]-1, 0) == ""){
					row[0]--;
				}
			}
			listCount = row[0];
			
			}else{
				for(int i=0; i<row.length; i++){
					setCell(row[i], 0, "");
					setCell(row[i], 1, "");
					setCell(row[i], 2, Color.white+"");
				}
			}
			
			//initializeTable();
			
			
			
		}
		
		if (command.equals("moveUp")){
			int selected = table.getSelectedRow();
			
			
			if(selected != -1 && !(getCell(selected, 0) == "") && !(selected < 1)){
				 
				moveRowUp(table.getSelectedRow());
				table.setRowSelectionInterval(selected-1, selected-1);
			}
		}
		if (command.equals("moveDown")){
			int selected = table.getSelectedRow();
			if(selected != -1 && !(getCell(selected, 0) == "") && selected >= 0 && selected < dataModel.getRowCount()-1){
				 
				moveRowDown(table.getSelectedRow());
				table.setRowSelectionInterval(selected+1, selected+1);
			}
		}
    }
    
    
    public Color[] getColorArray(int[] indices){
    	Color[] temp = new Color[indices.length];
    	for(int i=0; i<indices.length; i++){
    		String colorString = getCell(indices[i],2)+"";
    		temp[i] = stringToColor(colorString);
    	}
    	return temp;
    }
    
    public String[] getLabelArray(int[] indices){
    	String[] temp = new String[indices.length];
    	ArrayList<String> stemp = new ArrayList<String>();
    	for(int i=0; i<indices.length; i++){
    		//if(getCell(indices[i],1).equals("")){ || getCell(indices[i],1) == null || getCell(indices[i],1).equals("null")){ continue; }
    		
    		//String check = getCell(indices[i],1)+"";
    		//stemp.add(getCell(indices[i],1)+"");
    		temp[i] = getCell(indices[i],1)+"";
    		
    	}
    	//return stemp.toArray(String[] tem);
    	return temp;
    }
    
    public String getCompositeLabel(String[] labels){
    	String compositeLabel = labels[0];
    	for(int i=1; i<labels.length; i++){
    		if(labels[i] == null || labels[i].equals("")){ return compositeLabel+""; }
    		compositeLabel = compositeLabel + ":" + labels[i];
    	}
    	return compositeLabel+"";
    }
    
    
    /*
     * This method handles all of the list and table selection events.  If you select one
     * row of the table it calls createDiscreteMapping from ColorMapper.java. If you select more than one
     * row it calls setComposite attribute from AttributeManager.java to set a composite attribute.
     * It then calls createCompositeMapping from ColorMapper.java and uses the composite attribute created
     * to map all of the highlighted rows to colors.  The composite attribute is stored as a boolean attribute
     * for cytoscape and accessed by a colon separated string of the labels in the order of their selection.
     */
    public void valueChanged(ListSelectionEvent e){
        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        
        
        
        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        boolean isAdjusting = e.getValueIsAdjusting(); 
        
        
        if (lsm.isSelectionEmpty()) {
            System.out.println(" <none>");
        } else {
            // Find out which indexes are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            int last = -1;
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                	
                		int[] temp = table.getSelectedRows();
                		if(temp.length == 1){
                			System.out.println("Selected Index: " + i);
                            String colorString = getCell(i,2)+"";
                            //System.out.println("temp: "+temp);
                            if(getCell(i,0).equals("")){ return; }
                            Color c = stringToColor(colorString);
                            System.out.println("Color String: "+colorString);
                            
                            mapper.createDiscreteMapping(getCell(i,1)+"_discrete", (String)getCell(i,1), c);
                		}else{    
                			
                			String[] labels = getLabelArray(temp);
                			Color[] colors = getColorArray(temp);
                			
                			for(int p=0; p<labels.length; p++){
                				System.out.println("LABEL: "+labels[p]);
                			}
                			String compositeLabel = getCompositeLabel(labels);
                			System.out.println("COMPOSITE LABEL: "+compositeLabel);
                			if(labels.length == 1){
                				mapper.createDiscreteMapping(labels[0]+"_discrete", labels[0], colors[0]);
                				break;
                			}
                			if(labels.length == 2 &&(labels[0].equals("") || labels[1].equals(""))){
                				
                			}
                			if(attManager.isCompositeAttribute(compositeLabel)){
                				attManager.removeCompositeAttribute(compositeLabel);
                			}
                			try{
                				attManager.setCompositeAttribute(labels);
                			}catch (Exception setAttFailure){
                				System.out.println("NO"+setAttFailure.getMessage()+"WAY");
                			}
                			mapper.createCompositeMapping(compositeLabel+"_discrete", compositeLabel, colors);
                		}
                	
                }
            }
        }
    }
    public Color stringToColor(String value){
    	Pattern p = Pattern.compile("java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
		Matcher m = p.matcher(value);
		if(m.matches()){
			//System.out.println(m.group(1)+" "+m.group(2)+" "+m.group(3));
			Color temp = new Color(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),Integer.parseInt(m.group(3)));
			return temp;
		}
		return Color.white;
    }
    
    
    //Wrapper methods for the inner class BooleanTableModel
    public Object getCell(int row, int col){
    	return dataModel.getValueAt(row, col);
    }
    
    
    public void setCell(int row, int col, String value){
    	if(col == 2){
    		//java.awt.Color[r=0,g=0,b=255]
    		Color temp = stringToColor(value);
    		dataModel.setValueAt(temp, row, col);
    		System.out.println("set values");
    		return;
    	}
    	dataModel.setValueAt(value, row, col);
    }
    
    public void clearTable(){
    	for(int i=0; i<dataModel.rowCount; i++){
    		for(int j=0; j<dataModel.colCount; j++){
    			if(j==2){
    				dataModel.setValueAt(Color.WHITE, i, j);
    			}else{
    				dataModel.setValueAt("", i, j);
    			}
    		}
    	}
    }
    
    public int getDataLength(){
    	return dataModel.getRowCount();
    }
    
   
    
	public void populateList(String criteria, String label, Color currentColor){
		
		
		//data = new Object[listCount+1][3];
		//table.
		
		for(int i=0; i<dataModel.getRowCount(); i++){
			System.out.println("i: "+i);
			if(getCell(i,0) == null || getCell(i,0).equals((""))){
				
				System.out.println(i);
				listCount = i;
				break;
			}
			
		}	
			
			
	
		
		if(listCount < 5){
			System.out.println("populate List: " + criteria+listCount);
			dataModel.setValueAt(criteria, listCount, 0);
			dataModel.setValueAt(label, listCount, 1);
			dataModel.setValueAt(currentColor, listCount, 2);
			//initializeTable();
		
			listCount++;
		}else{
			//dataModel.
			//listCount++;
			System.out.println("LIST COUNT: "+listCount);
			dataModel.setRowCount(listCount+1);
			dataModel.createNewDataObject();
			
			
			dataModel.setValueAt(criteria, listCount, 0);
			dataModel.setValueAt(label, listCount, 1);
			dataModel.setValueAt(currentColor, listCount, 2);
			//
			listCount++;
			initializeTable();
		}
	}
	
	
	
	public void moveRowUp(int rowNumber){
		if(rowNumber != 0){
			
			
			
			Object criteriaTemp = dataModel.data[rowNumber][0];
			Object labelTemp = dataModel.data[rowNumber][1];
			Object colorTemp = dataModel.data[rowNumber][2];
			
			dataModel.setValueAt(dataModel.data[rowNumber-1][0], rowNumber, 0);
			dataModel.setValueAt(dataModel.data[rowNumber-1][1], rowNumber, 1);
			dataModel.setValueAt(dataModel.data[rowNumber-1][2], rowNumber, 2);
			
			dataModel.setValueAt(criteriaTemp, rowNumber-1, 0);
			dataModel.setValueAt(labelTemp, rowNumber-1, 1);
			dataModel.setValueAt(colorTemp, rowNumber-1, 2);
			
		}
	}
	
	public void moveRowDown(int rowNumber){
		if(rowNumber != 5){
			
			
			
			Object criteriaTemp = dataModel.data[rowNumber][0];
			Object labelTemp = dataModel.data[rowNumber][1];
			Object colorTemp = dataModel.data[rowNumber][2];
			
			dataModel.setValueAt(dataModel.data[rowNumber+1][0], rowNumber, 0);
			dataModel.setValueAt(dataModel.data[rowNumber+1][1], rowNumber, 1);
			dataModel.setValueAt(dataModel.data[rowNumber+1][2], rowNumber, 2);
			
			dataModel.setValueAt(criteriaTemp, rowNumber+1, 0);
			dataModel.setValueAt(labelTemp, rowNumber+1, 1);
			dataModel.setValueAt(colorTemp, rowNumber+1, 2);
			
			//initializeTable();
		}
	}
    
    class BooleanTableModel extends AbstractTableModel {

		//TableModel dataModel = new AbstractTableModel() {
    	int rowCount = 5;
    	int colCount = 3;
		
		String[] columnNames = { "Criteria", "Label", "Color" };
		Object[][] data = new Object[rowCount][colCount];
		
		
		public void createNewDataObject(){ 
			Object[][] temp = data;
			System.out.println("cndo rowCount: "+ rowCount);
			this.data = new Object[rowCount][colCount];
			
			for(int i=0; i<rowCount-1; i++){
				for(int j=0; j<colCount; j++){
					System.out.println("temp ij: "+temp[i][j]);
					//if(i<temp.length){
					this.data[i][j] = temp[i][j];
					//}
					fireTableCellUpdated(i, j);
				}
			}
		}
		public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col < 2) {
	            return false;
	        } else {
	            return true;
	        }
	    }

		//public Class getColumnClass(int c) {
          //  return getValueAt(0, c).getClass();
        //}

		public String getColumnName(int i){ return columnNames[i]; }
		public void setColumnCount(int count) { colCount = count; }
		public void setRowCount(int count) { rowCount = count; }
		public int getColumnCount() { return colCount; }
		public int getRowCount() { return rowCount; }
		public Object getValueAt(int row, int col) { 
			if(data[row][col] != null){ 
				return data[row][col]; 
			}else{
				return "";
			}
		}
		public void setValueAt(Object value, int row, int col) {
			data[row][col] = value;
			fireTableCellUpdated(row, col);
		}
	}
}

/*
 * ColorRenderer and ColorEditor are taken almost verbatim from the Java Trail's sun tutorial on using tables at
 * http://java.sun.com/docs/books/tutorial/uiswing/components/table.html 
 */

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
		Color newColor = stringToColor(color+"");
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

	public Color stringToColor(String value){
    	Pattern p = Pattern.compile("java.awt.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
		Matcher m = p.matcher(value);
		if(m.matches()){
			//System.out.println(m.group(1)+" "+m.group(2)+" "+m.group(3));
			Color temp = new Color(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),Integer.parseInt(m.group(3)));
			return temp;
		}
		return Color.white;
    }	
}



class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	Color currentColor;
	JButton button;
	JColorChooser colorChooser;
	JDialog dialog;


	public ColorEditor() {
//		Set up the editor (from the table's point of view),
//		which is a button.
//		This button brings up the color chooser dialog,
//		which is the editor from the user's point of view.
		button = new JButton();
		button.setActionCommand("edit");
		button.addActionListener(this);
		button.setBorderPainted(false);
		System.out.println("made editor");
//		Set up the dialog that the button brings up.
		colorChooser = new JColorChooser();
		dialog = JColorChooser.createDialog(button,
				"Pick a Color",
				true,  //modal
				colorChooser,
				this,  //OK button handler
				null); //no CANCEL button handler
	}

	/**
	 * Handles events from the editor button and from
	 * the dialog's OK button.
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println("made dialog");
		if (e.getActionCommand().equals("edit")) {
			
			button.setBackground(currentColor);
			colorChooser.setColor(currentColor);
			//dialog.setVisible(true);
			
			
			
//			Make the renderer reappear.
			fireEditingStopped();

		} else { //User pressed dialog's "OK" button.
			currentColor = colorChooser.getColor();
		}
	}

	
	
//	Implement the one CellEditor method that AbstractCellEditor doesn't.
	public Object getCellEditorValue() {
		return currentColor;
	}

//	Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		currentColor = (Color)value;
		return button;
	}
}
