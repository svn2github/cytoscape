/**
 * A dialog that allows users to set a new mode's inequalities, direction information,
 * and the visual attributes of the edges that represents it.
 *
 * @author Iliana Avila-Campillo
 */
package phenotypeGenetics.ui;

import phenotypeGenetics.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import cytoscape.*;

public class ModeEditor extends JDialog {
  
  /**
   * The return value of static method <code>showModeEditor()</code>
   */
  private int returnValue;

  /**
   * The modes that are to be edited
   */
  private Mode [] modes;
  
  /**
   * Constructor
   *
   * @param modes the modes to edit
   */
  private ModeEditor (Mode[] modes){
    setTitle("Mode Editor");
    setModal(true);
    setModes(modes);
    create();
  }//ModeEditor
  
  /**
   * Sets the modes being edited
   *
   * @param modes the Modes that are being edited
   */
  private void setModes (Mode [] modes){
    this.modes = modes;
  }//setModes

  /**
   * Creates the dialog
   */
  private void create (){
    JPanel mainPanel = new JPanel(new BorderLayout());
    setContentPane(mainPanel);
  
    //mainPanel.add(createTitlePanel(), BorderLayout.NORTH);
    mainPanel.add(createModeTable(), BorderLayout.CENTER);
    mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
  }//create
  
  private JPanel createTitlePanel (){
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label = new JLabel("<html>Edit mode and then press \"Apply\":</html>" );
    panel.add(label);
    return panel;
  }//createTitlePanel
  
  private JPanel createModeTable (){
    JPanel tablePanel = new JPanel();
    ModeEditorTable table = new ModeEditorTable(this.modes);
    JScrollPane scrollPane = new JScrollPane(table);
    tablePanel.add(scrollPane);
    return tablePanel;
  }//createModeTable
  
  private JPanel createButtonPanel (){
    JPanel panel = new JPanel();
    JButton applyButton = new JButton("OK");
    applyButton.addActionListener(
        new AbstractAction (){
          public void actionPerformed (ActionEvent event){
            onApply();
          }//actionPerformed
        }//AbstractAction
        );
    //JButton cancelButton = new JButton("Cancel");
    //cancelButton.addActionListener(
    //  new AbstractAction(){ 
    //    public void actionPerformed (ActionEvent event){
    //      onCancel();
    //    }//actionPerformed
    //  }//AbstractAction
    //  );
    panel.add(applyButton);
    //panel.add(cancelButton);
    return panel;
  }//createButtonPanel

  /**
   * Gets called when the "Apply" button is pressed
   */
  private void onApply (){
    Mode mode = modeDirectionsAreConsistent();
    if(mode != null){
      JOptionPane.showMessageDialog( 
                                    ModeEditor.this, 
                                    "<html>Mode \"" + mode.getName() + 
                                    "\" contains non-directional and directional inequalities.<br>Please assign only directional or non-directional inequalities to each mode.</html>",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
    }else{
      this.returnValue = 1;
      // since some attribute mappings changed, redraw graph
      Cytoscape.getCurrentNetworkView().redrawGraph(false,true);//layout, vizmap
      dispose();
    }
  }//onApply
  
  /**
   * Iterates over all Modes. If there is a Mode that contains inequalities
   * with direction and no direction, return it, otherwise return null.
   */
  protected Mode modeDirectionsAreConsistent (){
    
    for(int i = 0; i < this.modes.length; i++){
      Iterator it = this.modes[i].getPhenotypeInequalities().iterator();
      boolean notDirectional = false;
      boolean directional = false;
      while(it.hasNext()){
        DiscretePhenoValueInequality ineq = (DiscretePhenoValueInequality)it.next();
        if(ineq.getDirection().equals(DiscretePhenoValueInequality.NOT_DIRECTIONAL)){
          notDirectional = true;
          if(notDirectional && directional){
            return this.modes[i];
          }
        }else{
          directional = true;
          if(directional && notDirectional){
            return this.modes[i];
          }
        }
      }//while it
    }//for i
    return null;
  }//modeDirectionsAreConsistent
  
  /**
   * Gets called when the "Cancel" button gets pressed
   */
  private void onCancel (){
    this.returnValue = 0;
    dispose();
  }//onCancel

  /**
   * @return the return value that showModeEditor should return
   */
  private int getReturnValue (){
    return this.returnValue;
  }//getReturnValue
  
  /**
   * Displays a dialog that contains options to edit the given Mode
   *
   * @param parent the parent component of this dialog
   * @param mode the Mode for which options will be shown and that will
   * be modified after the user edits the options
   */
  public static int showModeEditor (Component parent, Mode [] modes){
    ModeEditor modeEditor = new ModeEditor(modes);
    // the calling thread will block here until the user closes the dialog
    modeEditor.setLocationRelativeTo(parent);
    modeEditor.pack();
    modeEditor.setVisible(true);
    return modeEditor.getReturnValue();
  }//showModeEditor
  
}//class ModeEditor
