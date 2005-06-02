/**
 * A dialog that allows the user to manage modes (create new modes, delete modes,
 * edit modes, save modes, and load modes).
 *
 * @author Iliana Avila-Campillo
 */

package phenotypeGenetics.ui;

import phenotypeGenetics.*;
import phenotypeGenetics.xml.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ModeManagerDialog extends JDialog {

  public static final String DIALOG_TITLE = "Mode Manager";
  protected JList modeList;
  protected JFileChooser fileChooser;
  
  /**
   * Constructor
   *
   * @param owner the owner Dialog of this dialog
   */
  public ModeManagerDialog (Dialog owner){
    super(owner, DIALOG_TITLE);
    create();
  }//constructor

  /**
   * Constructor
   *
   * @param owner the owner Frame of this dialog
   */
  public ModeManagerDialog (Frame owner){
    super(owner, DIALOG_TITLE);
    create();
  }//constructor

  /**
   * Creates the dialog
   */
  protected void create (){
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    
    JPanel modePanel = new JPanel();
    modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
    
    JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel modeLabel = new JLabel("Existing Modes:");
    labelPanel.add(modeLabel);
    modePanel.add(labelPanel);

    JPanel modeBoxPanel = new JPanel();
    modeBoxPanel.setLayout(new BoxLayout(modeBoxPanel, BoxLayout.X_AXIS));
    
    // the mode list always has an "unassigned" mode
    
    Mode [] modes = {DiscretePhenoValueInequality.UNASSIGNED_MODE};
    this.modeList = new JList(modes);
    modeList.setVisibleRowCount(10);
    JScrollPane scrollPane = new JScrollPane(this.modeList);
    modeBoxPanel.add(scrollPane);
       
    JPanel rbuttonPanel = new JPanel(new GridLayout(3,1));//rows, columns
    JButton newButton = new JButton("New...");
    newButton.addActionListener(
     new AbstractAction (){
       public void actionPerformed (ActionEvent actionEvent){
         String modeName = askForModeName();
         if(modeName != null){
           Mode newMode = new Mode(modeName);
           addNewMode(newMode);
         }
       }//actionPerformed
     }//AbstractAction
     );
    JButton editButton = new JButton("Edit...");
    editButton.addActionListener(
     new AbstractAction (){
       public void actionPerformed (ActionEvent actionEvent){
         showModeEditor(getModes());
       }
     }
     );
    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(
      new AbstractAction (){
        public void actionPerformed (ActionEvent actionEvent){
          deleteSelectedModes();
        }
      }
      );
    rbuttonPanel.add(newButton);
    rbuttonPanel.add(editButton);
    rbuttonPanel.add(deleteButton);
    modeBoxPanel.add(rbuttonPanel);
    
    modePanel.add(modeBoxPanel);
    
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
    centerPanel.add(modePanel);
        
    mainPanel.add(centerPanel);

    this.fileChooser = new JFileChooser();
    XmlFileFilter filter = new XmlFileFilter();
    this.fileChooser.setFileFilter(filter);
    JPanel bottomPanel = new JPanel(new GridLayout(1,3)); // rows, columns
    JButton loadButton = new JButton("Load XML file...");
    loadButton.addActionListener(
      new AbstractAction (){
        public void actionPerformed (ActionEvent event){
          int returnVal = fileChooser.showOpenDialog(ModeManagerDialog.this);
          if(returnVal == JFileChooser.APPROVE_OPTION){
            try{
              Mode [] modes = 
                ModeXMLFileWriter.read(fileChooser.getSelectedFile().getAbsolutePath());
              for(int i = 0; i < modes.length; i++){
                addNewMode(modes[i]);
              }
            }catch (Exception exception){
              JOptionPane.showMessageDialog(
                                            ModeManagerDialog.this, 
                                            "Error parsing XML file",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                           );
            }//catch
          }// if APPROVE_OPTION
        }//actionPerformed
      }
      );
    JButton saveButton = new JButton("Save to XML file...");
    saveButton.addActionListener(
      new AbstractAction (){
        public void actionPerformed (ActionEvent event){
          int returnVal = fileChooser.showSaveDialog(ModeManagerDialog.this);
          if(returnVal == JFileChooser.APPROVE_OPTION){
            ModeXMLFileWriter.write(getModes(), fileChooser.getSelectedFile());
          }
        }//actionPerformed
      }//AbstractAction
      );
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(
     new AbstractAction (){
       public void actionPerformed (ActionEvent event){
         ModeManagerDialog.this.dispose();
       }//actionPerformed
     }//AbstractAction
     );//addActionListener
    
    bottomPanel.add(loadButton);
    bottomPanel.add(saveButton);
    bottomPanel.add(closeButton);
    
    mainPanel.add(bottomPanel);
    
    setContentPane(mainPanel);
  }//create

  
  /**
   * @return the Modes that are managed by this manager
   */
  public Mode [] getModes (){
    ListModel lModel = this.modeList.getModel();
    ArrayList modes = new ArrayList();
    for(int i = 0; i < lModel.getSize(); i++){
      Mode m = (Mode)lModel.getElementAt(i);
      modes.add(m);
    }//for i
    return (Mode[])modes.toArray(new Mode[modes.size()]);
  }//getModes

  /**
   * Sets the modes that this manager contains
   */
  public void setModes (Mode [] modes){
    this.modeList.setListData(modes);
    for(int i = 0; i < modes.length; i++){
      if(modes[i] == DiscretePhenoValueInequality.UNASSIGNED_MODE){
        return;
      }
    }// for i
    // The UNASSIGNED_MODE should always be an option!
    addNewMode(DiscretePhenoValueInequality.UNASSIGNED_MODE);
  }//setModes

  /**
   * Pops up a dialog asking for a mode name, checks that this mode name is unique, and
   * returns it.
   * 
   * @return the mode name
   */
  //TODO: Check that it does not already exist
  protected String askForModeName (){
    
    String modeName = 
      JOptionPane.showInputDialog(ModeManagerDialog.this, "Enter new mode's name:"); 
    
    ListModel lModel = this.modeList.getModel();
    for(int i = 0; i < lModel.getSize(); i++){
      Mode otherMode = (Mode)lModel.getElementAt(i);
      if(otherMode.getName().equals(modeName)){
        JOptionPane.showMessageDialog(ModeManagerDialog.this, 
                                      "That mode already exists.",
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
        return null;
      }
    }//for i
    return modeName;
    
  }//askForModeName

  /**
   * Deletes the selected Modes from the list and does necessary bookkeepping
   */
  protected void deleteSelectedModes (){
    
    Object [] selectedVals = this.modeList.getSelectedValues();
    HashSet modesToDelete = new HashSet();
        
    for(int i = 0; i < selectedVals.length; i++){
    
      if(!(selectedVals[i] instanceof Mode)){
        continue;
      }// not a Mode
      
      Mode deleteMode = (Mode)selectedVals[i];
      if(deleteMode == DiscretePhenoValueInequality.UNASSIGNED_MODE){
        // Do not delete this one!
        continue;
      }
      Iterator it = deleteMode.getPhenotypeInequalities().iterator();
      // For all the inequalities that have this Mode, set it to "unassigned"
      while(it.hasNext()){
        DiscretePhenoValueInequality ineq = (DiscretePhenoValueInequality)it.next();
        ineq.setMode(DiscretePhenoValueInequality.UNASSIGNED_MODE);
      }//it
      
      modesToDelete.add(deleteMode);
            
    }//for i
    
    ListModel listModel = this.modeList.getModel();
    Vector newModeList = new Vector();
    for(int i = 0; i < listModel.getSize(); i++){
      Mode existingMode = (Mode)listModel.getElementAt(i);
      if(!modesToDelete.contains(existingMode)){
        newModeList.add(existingMode);
      }
    }//for i
    
    this.modeList.setListData(newModeList);

  }//deleteSelectedModes

  /**
   * Adds a new Mode to the box of Modes, if a  Mode with that name already exists,
   * the old one gets replaced
   */
  protected void addNewMode (Mode newMode){
    ListModel listModel = this.modeList.getModel();
    Vector data = new Vector();
    for(int i = 0; i < listModel.getSize(); i++){
      Mode existingMode = (Mode)listModel.getElementAt(i);
      if(!existingMode.equals(newMode)){
        data.add(existingMode);
      }
    }//for i
    data.add(newMode);
    this.modeList.setListData(data);
  }//addMode
  
  /**
   * Pops up a dialog to edit the existing modes and their settings.
   *
   * @param new_mode_name the name of the new mode being created 
   */
  protected void showModeEditor (Mode [] modes){
    int returnVal = ModeEditor.showModeEditor(this,modes);
    System.out.println("Return value == " + returnVal);
  }//showModeEditor
  
  /**
   * A FileFilter that only accepts XML files
   */
  protected class XmlFileFilter extends FileFilter {
    
    public XmlFileFilter (){}

    /**
     * Whether the given file is accepted by this filter.
     */
    public boolean accept (File f){
      
      if(f.isDirectory()){
        return true;
      }
      
      String name = f.getName();
      if(name.endsWith("xml")){
        return true;
      }
      return false;
    
    }//accept
    
    /**
     * The description of this filter.
     */
    public String getDescription(){
      return ".xml ending";
    }
    
  }//XmlFileFilter
  
}//class ModeManagerDialog
