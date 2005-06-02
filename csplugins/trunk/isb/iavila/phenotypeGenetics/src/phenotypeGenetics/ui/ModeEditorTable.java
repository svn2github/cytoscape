/**
 * A JTable that contains 5 columns: "Inequality", "Mode", "Edge Direction",
 * "Edge Color", and "Edge Type". The user can interact with it in order
 * to modify a mode's settings.
 *
 * @author Iliana Avila-Campillo
 */
package phenotypeGenetics.ui;
import phenotypeGenetics.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import cytoscape.visual.ui.*;

public class ModeEditorTable extends JTable {
  
  protected static final String [] edgeDirections = 
  {
    DiscretePhenoValueInequality.NOT_DIRECTIONAL,
    DiscretePhenoValueInequality.A_TO_B,
    DiscretePhenoValueInequality.B_TO_A
  };
  
  protected Mode [] modes;
  
  /**
   * Constructor
   *
   * @param modes the Modes for which the editor table is being displayed
   */
  public ModeEditorTable (Mode [] modes){
    super();
    // TODO: Remove
    //System.out.println("ModeEditorTable: There are " + modes.length + " modes");
    //for(int i = 0; i < modes.length; i++){
    //System.out.println(modes[i]);
    //}
    setModes(modes);
    create();
  }//constructor

  /**
   * Creates the table
   */
  private void create (){
    Object [][] data = initData();
    ModeEditorTableModel model = new ModeEditorTableModel(modes,data);
    ModeTableListener listener = new ModeTableListener();
    model.addTableModelListener(listener);
    setModel(model);
    
    JComboBox modesBox = new JComboBox(modes);
    TableColumn modeColumn = getColumnModel().getColumn(1);
    modeColumn.setCellEditor(new DefaultCellEditor(modesBox));
    //Set up tool tips for the cells
    DefaultTableCellRenderer renderer =
      new DefaultTableCellRenderer();
    renderer.setToolTipText("Click for options");
    modeColumn.setCellRenderer(renderer);
    
    JComboBox directionBox = new JComboBox(this.edgeDirections);
    TableColumn directionColumn = getColumnModel().getColumn(model.DIR_POSITION);
    directionColumn.setCellEditor(new DefaultCellEditor(directionBox));
    directionColumn.setCellRenderer(renderer);
    
    ImageIcon [] edgeTypeIcons = MiscDialog.getLineTypeIcons();
    JComboBox edgeTypeBox = new JComboBox(edgeTypeIcons);
    TableColumn typeColumn = getColumnModel().getColumn(model.TYPE_POSITION);
    typeColumn.setCellEditor(new DefaultCellEditor(edgeTypeBox));
        
    setDefaultRenderer(Color.class, new ColorRenderer(true));
    setDefaultEditor(Color.class, new ColorEditor());
    
    // Set size of columns
    TableColumn col = getColumnModel().getColumn(model.INEQ_POSITION);
    col.setPreferredWidth(80);
    col = getColumnModel().getColumn(model.MODE_POSITION);
    col.setPreferredWidth(100);
    col = getColumnModel().getColumn(model.DIR_POSITION);
    col.setPreferredWidth(25);
    col = getColumnModel().getColumn(model.COLOR_POSITION);
    col.setPreferredWidth(25);
    col = getColumnModel().getColumn(model.TYPE_POSITION);
    col.setPreferredWidth(25);
  }
  
  /**
   * Initializes the data for this table's model.
   */
  private Object[][] initData (){
    
    
    DiscretePhenoValueInequality [] ineqs = 
      DiscretePhenoValueInequality.getInequalitiesSet();
    //if(ineqs == null){
    //System.out.println("ineqs is null!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //}
    
    ImageIcon [] edgeTypeIcons = MiscDialog.getLineTypeIcons();
    //if(edgeTypeIcons == null){
    //System.out.println("edgeTypeIcons is null!!!!!!!!!!!!!!!!!!");
    //}
    // 44 = reduced set of inequalities
    Object [][] data = new Object[44][5]; 
    
    for(int i = 0; i < 44; i++){
      
      //if(ineqs[i] == null){
      //System.out.println("ineqs["+i+"] is null !!!!!!!!!!!!!!!!!!!");
      //}
      
      data[i][0] = ineqs[i];
      
      Mode mode = ineqs[i].getMode();
      if(mode == null){
        mode = modes[0];
      }
      
      data[i][1] = mode;
      
      data[i][2] = ineqs[i].getDirection();
      data[i][3] = ineqs[i].getColor();
      
      String edgeType = ineqs[i].getEdgeType();
      ImageIcon icon = null;
      for(int j = 0; j < edgeTypeIcons.length; j++){
        //if(edgeType == null){
        //System.out.println("edgeType for ineq " + i + " is null !!!!!!!!!!!");
        //}
        //if(edgeTypeIcons[j] == null){
        //System.out.println("edgeTypeIcons["+j+"] is null!!!!!!!!!!!!!!!!!");
        //}
        if(edgeType.equals(edgeTypeIcons[j].getDescription())){
          icon = edgeTypeIcons[j];
          break;
        }
      }//for j
      if(icon == null){
        icon = edgeTypeIcons[0];
      }
      data[i][4] = icon;
    }//for i
    return data;
  }//initData

  /**
   * Sets the Modes for which the editor table is being displayed
   */
  public void setModes (Mode [] modes){
    this.modes = modes;
  }//setModes

  /**
   * A listener of changes to the table model
   */
  protected class ModeTableListener 
    implements TableModelListener{
    
    /**
     * Constructor
     */
    public ModeTableListener (){}

    /**
     * Responds to a change in the table model
     */
    public void tableChanged (TableModelEvent e){
      if(e.getType() == TableModelEvent.UPDATE){
        ModeEditorTableModel model = (ModeEditorTableModel)e.getSource();
        
        int colIndex = e.getColumn();
        int rowIndex = e.getFirstRow();
          
        Mode changedMode = (Mode)model.getValueAt(rowIndex, model.MODE_POSITION);
        
        DiscretePhenoValueInequality ineq = 
          (DiscretePhenoValueInequality)model.getValueAt(rowIndex, model.INEQ_POSITION);
        
        // The table only displays the 44 non-redundant inequalities, but, we
        // have a total of 75 (equivalent if switching A and B tags) so we
        // need to take care of the rest inequalities as well:
        DiscretePhenoValueInequality equivalentIneq = 
          DiscretePhenoValueInequality.getEquivalentInequality(ineq);
        
        if(colIndex == model.MODE_POSITION){
          
          // The inequality was assigned to a new Mode
          Mode oldMode = ineq.getMode();
          if(oldMode != null){
            oldMode.removePhenotypeInequality(ineq);
            if(equivalentIneq != null){
              oldMode.removePhenotypeInequality(equivalentIneq);
            }
          }
          changedMode.addPhenotypeInequality(ineq);
          if(equivalentIneq != null){
            changedMode.addPhenotypeInequality(equivalentIneq);
          }
          ineq.setMode(changedMode);
          if(equivalentIneq != null){
            equivalentIneq.setMode(changedMode);
          }
          
        }else if(colIndex == model.DIR_POSITION){
          
          // The inequality was assigned a new direction
          String direction = (String)model.getValueAt(rowIndex, model.DIR_POSITION);
          
          if(rowIndex <= 25 && direction != DiscretePhenoValueInequality.NOT_DIRECTIONAL){
            // These inequalities are symmetric, which means that they are
            // not supposed to have a direction!
            JOptionPane.showMessageDialog( 
                  ModeEditorTable.this, 
                  "This inequality is symmetric, so it cannot have an edge direction!",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
            model.setValueAt(DiscretePhenoValueInequality.NOT_DIRECTIONAL,rowIndex,colIndex);
            return;
          }
          
          ineq.setDirection(direction);
          
          // The equivalent inequality needs to have the opposite direction
          if(equivalentIneq != null){
            if(direction == DiscretePhenoValueInequality.NOT_DIRECTIONAL){
              // Just use the same:
              equivalentIneq.setDirection(DiscretePhenoValueInequality.NOT_DIRECTIONAL);
            }else if(direction == DiscretePhenoValueInequality.A_TO_B){
              equivalentIneq.setDirection(DiscretePhenoValueInequality.B_TO_A);
            }else{
              equivalentIneq.setDirection(DiscretePhenoValueInequality.A_TO_B);
            }
          }

        }else if(colIndex == model.COLOR_POSITION){
        
          // The inequality was assigned a new Color
          Color color = (Color)model.getValueAt(rowIndex, model.COLOR_POSITION);
          ineq.setColor(color);
          if(equivalentIneq != null){
            equivalentIneq.setColor(color);
          }
          
        }else if(colIndex == model.TYPE_POSITION){
        
          // The inequality was assigned a new Edge Type
          ImageIcon icon = (ImageIcon)model.getValueAt(rowIndex, model.TYPE_POSITION);
          ineq.setEdgeType(icon.getDescription());
          if(equivalentIneq != null){
            equivalentIneq.setEdgeType(icon.getDescription());
          }
        
        }// if TYPE_POSITION
        
      }//if UPDATE
      
    }//tableChanged
    
  }//ModeTableListener

  /**
   * the TableModel for this table
   */
  protected class ModeEditorTableModel 
    extends AbstractTableModel{

    public final int INEQ_POSITION = 0;
    public final int MODE_POSITION = 1;
    public final int DIR_POSITION  = 2;
    public final int COLOR_POSITION = 3;
    public final int TYPE_POSITION  = 4;
    protected final String [] columnNames = {"Inequality",
                                             "Mode",
                                             "Direction",
                                             "Edge Color",
                                             "Edge Type"};
    protected Object [][] data;
    
    /**
     * Constructor
     */
    public ModeEditorTableModel (Mode [] modes, Object [][] data){
      this.data = data;
    }
            
    /**
     * @return the Class of the data value at index (0,c)
     */
    public Class getColumnClass (int columnIndex){
      return getValueAt(0, columnIndex).getClass();
    }
    
    /**
     * @return the number of columns in the model.
     */
    public int getColumnCount (){
      return this.columnNames.length;
    }
    
    /**
     * @return the name of the column at columnIndex.
     */
    public String getColumnName (int columnIndex){
      return this.columnNames[columnIndex];
    }
    
    /**
     * @return the number of rows in the model.
     */
    public int getRowCount(){
      return this.data.length;
    }
    
    /**
     * @return the value for the cell at columnIndex and rowIndex.
     */
    public Object getValueAt (int rowIndex, int columnIndex){
      return this.data[rowIndex][columnIndex];
    }
          
    /**
     * @returns true if the cell at rowIndex and columnIndex is editable.
     */
    public boolean isCellEditable(int rowIndex, int columnIndex){
      //TODO: Need to know which ones are symmetric and which ones arent'
      return true;
    }
    
    /**
     * Sets the value in the cell at columnIndex and rowIndex to aValue.
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
      this.data[rowIndex][columnIndex] = aValue;
      fireTableCellUpdated(rowIndex,columnIndex);
    }
              
  }//ModelEditorTableModel

  // -------------------------------------------------------------------- 
  //TODO: Move these classes to separate files

  public class ColorEditor extends AbstractCellEditor
    implements TableCellEditor, ActionListener {
    
    protected Color currentColor;
    protected JButton button;
    protected JColorChooser colorChooser;
    protected JDialog dialog;
    protected static final String EDIT = "edit";

    public ColorEditor() {
      button = new JButton();
      button.setActionCommand(EDIT);
      button.addActionListener(this);
      button.setBorderPainted(false);
      
      //Set up the dialog that the button brings up.
      colorChooser = new JColorChooser();
      dialog = JColorChooser.createDialog(button,
                                          "Pick a Color",
                                          true,  //modal
                                          colorChooser,
                                          this,  //OK button handler
                                          null); //no CANCEL button handler
    }
    
    public void actionPerformed(ActionEvent e) {
      if (EDIT.equals(e.getActionCommand())) {
        //The user has clicked the cell, so
        //bring up the dialog.
        button.setBackground(currentColor);
        colorChooser.setColor(currentColor);
        dialog.setVisible(true);
        
        fireEditingStopped(); //Make the renderer reappear.
         
      }else{ //User pressed dialog's "OK" button.
        currentColor = colorChooser.getColor();
      }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
      return currentColor;
    }
    
    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
      currentColor = (Color)value;
      return button;
    }
  }//ColorEditor

  protected class ColorRenderer extends JLabel
    implements TableCellRenderer {

    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public ColorRenderer(boolean isBordered) {
      this.isBordered = isBordered;
      setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(JTable table, Object color,
                                                   boolean isSelected, boolean hasFocus,
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
        
      setToolTipText("RGB value: " + newColor.getRed() + ", "
                     + newColor.getGreen() + ", "
                     + newColor.getBlue());
      return this;
    }
  }
  
}//ModeEditorTable

