/**
 * A dialog that allows the user to load XML files with phenotype data, XML files with
 * mode settings, and that contains buttons to perform Phenotype Genetics analysis.
 *
 * @author Iliana Avila-Campillo
 * @version 2.0
 */

package phenotypeGenetics.ui;

import phenotypeGenetics.*;
import phenotypeGenetics.action.*;
import phenotypeGenetics.xml.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import cytoscape.*;
import cytoscape.data.servers.BioDataServer;
import cytoscape.util.*;
import javax.swing.filechooser.FileFilter;

public class PGDialog extends JDialog{
  
  protected JFileChooser fileChooser;
  
  protected JButton distributionButton;
  protected JButton neighborsButton;
  protected JButton statementsButton;
  protected JRadioButton createNewRadioButton;

  protected ModeManagerDialog modeManagerDialog;
  protected MutualInfoDialog mutualInformationDialog;
  protected StatementDialog statementDialog;
  protected DiscretePhenotypeDialog discretePhenoDialog;
  
  // A map from XML file names to their full paths
  // so that we only display their short names in the dialog
  // instead of their full paths, but can get their full paths
  // when they are selected
  protected Map xmlFilePaths;
  protected JList xmlFileList;
  
  /**
   * Sole constructor
   *
   * @param phenotype_genetics the main phenotype genetics object that
   * will perform all the main actions
   */
  public PGDialog (){
    setTitle("Phenotype Genetics Analysis");
    create();
  }//PGDialog

   /**
   * Creates the dialog.
   */
  protected void create (){
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    
    JPanel loadFilePanel = new JPanel();
    loadFilePanel.setLayout(new BoxLayout(loadFilePanel, BoxLayout.Y_AXIS));
    Border titledBorder = 
      BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Input");
    loadFilePanel.setBorder(titledBorder);
    
    JPanel flPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel loadFileLabel = new JLabel("Select one or more phenotype values files:");
    flPanel.add(loadFileLabel);
    DefaultListModel listModel = new DefaultListModel();
    this.xmlFileList = new JList(listModel);
    this.xmlFileList.setVisibleRowCount(5);
    this.xmlFileList.setFixedCellWidth(150);
    JScrollPane scrollPane = new JScrollPane();
    String [] existingFiles = PhenotypeGeneticsPlugIn.xmlProjectFiles;
    this.xmlFilePaths = new HashMap();
    
    for(int i = 0; i < existingFiles.length; i++){
      
      int index = existingFiles[i].indexOf("jar://");
      
      if(index != -1){
        String shortName = existingFiles[i].substring(index + 6);
        this.xmlFilePaths.put(shortName, existingFiles[i]);
        listModel.addElement(shortName);
        continue;
      }
      
      String fileSeparator = System.getProperty("file.separator");

      index = existingFiles[i].lastIndexOf(fileSeparator); 
      
      if(index != -1){
        String shortName = existingFiles[i].substring(index + 1);
        this.xmlFilePaths.put(shortName, existingFiles[i]);
        listModel.addElement(shortName);
        continue;
      }
      
      // Not a jar, not full path description, assume it is in the current directory
      this.xmlFilePaths.put(existingFiles[i], existingFiles[i]);
      listModel.addElement(existingFiles[i]);
    
    }//for i

    scrollPane.getViewport().setView(this.xmlFileList);
    
    JPanel bPanel = new JPanel();
    JButton browseFilesButton = new JButton("Browse");
    bPanel.add(browseFilesButton);
    JButton clearFilesButton = new JButton("Remove Selected Files");
    bPanel.add(clearFilesButton);
    
    clearFilesButton.addActionListener(
       new AbstractAction (){
         
         public void actionPerformed (ActionEvent event){

           int [] selected = xmlFileList.getSelectedIndices();
           for(int i = 0; i < selected.length; i++){
             Object removed = ((DefaultListModel)xmlFileList.getModel()).remove(selected[i]);
             xmlFilePaths.remove(removed);
           }//for i
           
         }//actionPerformed
         
       }//AbstractAction
       
       );//addActionListener


    this.fileChooser = new JFileChooser();
    XmlFileFilter filter = new XmlFileFilter();
    fileChooser.setFileFilter(filter);
    browseFilesButton.addActionListener(
     new AbstractAction (){
       
        public void actionPerformed (ActionEvent event){

          int returnVal = fileChooser.showOpenDialog(PGDialog.this);
          if(returnVal == JFileChooser.APPROVE_OPTION){
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            String fileSeparator = System.getProperty("file.separator");
            int index = filePath.lastIndexOf(fileSeparator); 
            
            if(index != -1){
              String shortName = filePath.substring(index + 1);
              xmlFilePaths.put(shortName, filePath);
              ((DefaultListModel)xmlFileList.getModel()).addElement(shortName);
            }else{
              //should not get here!
              throw new IllegalStateException("Somehow, a FileChooser returned a file " + 
                                              "with no path");
            }// else
            
            PhenotypeGeneticsPlugIn.addXmlFile(filePath);
            
          }// if APPROVE_OPTION
          
        }//actionPerformed
       
     }//AbstractAction
     );//addActionListener
    
    loadFilePanel.add(flPanel);
    loadFilePanel.add(scrollPane);
    loadFilePanel.add(bPanel);

    // Specify the modes to use
    JPanel modesPanel = new JPanel();
    modesPanel.setLayout(new BoxLayout(modesPanel, BoxLayout.Y_AXIS));
    // Get any command line argument for the modes
    String xmlModeFile = PhenotypeGeneticsPlugIn.modeXmlFile;
    Mode [] modes = null;
    if(xmlModeFile != null){
      try{
        // read and create modes so that they can be set in the ModeManagerDialog!
        modes = ModeXMLFileWriter.read(xmlModeFile);
      }catch (Exception e){
        e.printStackTrace();
      }
    }
    final Mode [] commandLineModes = modes;
    
    JButton modeManagerButton = new JButton("Manage genetic interaction modes...");
    modeManagerButton.addActionListener(
       new AbstractAction (){

         public void actionPerformed (ActionEvent event){
           if(modeManagerDialog == null){
             modeManagerDialog = new ModeManagerDialog(PGDialog.this);
             if(commandLineModes != null){
               modeManagerDialog.setModes(commandLineModes);
             }
           }
           modeManagerDialog.pack();
           modeManagerDialog.setLocationRelativeTo(PGDialog.this);
           modeManagerDialog.setVisible(true);
         }//actionPerformed
         
       }//AbstractAction
       );//addActionListener
    modesPanel.add(modeManagerButton);
    
    loadFilePanel.add(modesPanel);
    
    JPanel modeManagerPanel = new JPanel();
    
    modeManagerPanel.add(modeManagerButton);
    loadFilePanel.add(modeManagerPanel);
    
    
    mainPanel.add(loadFilePanel);
    
    
    JPanel raPanel = new JPanel();
    raPanel.setLayout(new BoxLayout(raPanel, BoxLayout.Y_AXIS));
    Border titledBorder2 = 
      BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Operations");
    raPanel.setBorder(titledBorder2);
     
    JPanel createPanel = new JPanel();
    this.createNewRadioButton = 
      new JRadioButton("Create a new graph for genetic interactions",
                       true);
    createPanel.add(this.createNewRadioButton);
    raPanel.add(createPanel);
    
    JPanel actionsPanel = new JPanel(new GridLayout(2,2)); //rows, cols


    JButton analyzeButton =
      new JButton("<html>Calculate genetic<br>interactions from<br>selected files</html>");

    analyzeButton.addActionListener(
     new AbstractAction(){
                                      
       public void actionPerformed (ActionEvent event){
         
         int numXmlFiles = getNumXmlFiles();
         String [] selectedXml = null;
         
         if(numXmlFiles == 0){
           JOptionPane.showMessageDialog(PGDialog.this,
                                         "<html>Please load at least one XML file<br>"+
                                         "by pressing the Browse button.</html>", 
                                         "Error",
                                         JOptionPane.ERROR_MESSAGE);
           return;
         }else if(numXmlFiles == 1){
           // this is *not* the full path:
           String selectedFile = (String)xmlFileList.getModel().getElementAt(0);
           selectedXml = new String[1];
           // this is the full path:
           selectedXml[0] = (String)xmlFilePaths.get(selectedFile);
           
         }else{
         
           selectedXml = getSelectedXmlFiles();
           
           if(selectedXml.length == 0){
             JOptionPane.showMessageDialog(PGDialog.this,
                                           "<html>Please select at least one XML file<br>"
                                           + "from the list (use your mouse).</html>",
                                           "Error",
                                           JOptionPane.ERROR_MESSAGE);
             return;
           }
         }
         
         final String [] finalXmlFiles = selectedXml;
         
         SwingWorker worker = 
           new SwingWorker (){
             public Object construct(){
               IndeterminateProgressBar pbar = 
                 new IndeterminateProgressBar(Cytoscape.getDesktop(),
                                              "Progress", 
                                              "Reading XML...");
               pbar.pack();
               pbar.setLocationRelativeTo(Cytoscape.getDesktop());
               pbar.setVisible(true);
    
               // Read the selected phenotype data and create a Project
               Project project = null;
               try{
                 project = ProjectXmlReader.readProject(finalXmlFiles);
               }catch(Exception ex){
                 JOptionPane.showMessageDialog(PGDialog.this,
                                               ex.getMessage());
               }finally{
                 pbar.setVisible(false);
               }
               
               // If the Project contains phenotypes with non-numerical discrete
               // values, then we need to ask the user to order them
               DiscretePhenotypeRanking discreteRanks = project.getDiscretePhenotypeRanks();
               if( discreteRanks.getNumPhenotypes() > 0 ){
                 // Pop-up ordering dialog!
                 discretePhenoDialog = 
                   new DiscretePhenotypeDialog(discreteRanks);
                 discretePhenoDialog.pack();
                 discretePhenoDialog.setLocationRelativeTo(Cytoscape.getDesktop());
                 discretePhenoDialog.setVisible(true);
                 // This dialog is modal, so if we get to this line it means that
                 // the user already closed it (and set the relations)
                 discreteRanks = discretePhenoDialog.getUpdatedDiscreteRanking();
                 project.setDiscretePhenotypeRanks(discreteRanks);
               }//if discrete phenotype values
               
               CalculateGeneticInteractionsTask calcTask = 
                 new CalculateGeneticInteractionsTask(project,
                                                      createNewRadioButton.isSelected());
               
               CytoscapeProgressMonitor monitor = 
                 new CytoscapeProgressMonitor(calcTask,
                                              Cytoscape.getDesktop());
               
               monitor.startMonitor(true); // wait until done
               
               setButtonsEnabled(true);

               JOptionPane.showMessageDialog(PGDialog.this,
                                             "Done calculating interactions.",
                                             "Information",
                                             JOptionPane.INFORMATION_MESSAGE);
               return null;
             }//construct
           };//SwingWorker
         
         worker.start();
         
       }//actionPerformed
     }//AbstractAction
     );//addActionListener
    
    this.distributionButton =  
      new JButton("<html>Show distribution of<br>interaction classes<br>by allele</html>");
    this.distributionButton.setEnabled(false);
    this.distributionButton.addActionListener(
        new AbstractAction(){
          public void actionPerformed (ActionEvent event){

            SwingWorker worker = 
              new SwingWorker (){
                public Object construct(){
                  
                  IndeterminateProgressBar pbar = 
                    new IndeterminateProgressBar(Cytoscape.getDesktop(),
                                                 "Progress", 
                                                 "Calculating distribution...");
                  pbar.pack();
                  pbar.setLocationRelativeTo(Cytoscape.getDesktop());
                  pbar.setVisible(true);
                  
                  CyNetwork currentNet = Cytoscape.getCurrentNetwork();
                  HashMap dist = 
                    NodalDistributionAnalyzer.calculateNodeDistribution(currentNet,true);
                  
                  TableDialog distTable = new TableDialog(dist);
                  
                  pbar.dispose();
                  
                  distTable.pack();
                  distTable.setLocationRelativeTo(Cytoscape.getDesktop());
                  distTable.setVisible(true);
                  
                  return null;
                }
              };
            

            worker.start();
            
          }//actionPerformed
          
        }//AbstractAction
        
        );

    this.neighborsButton = new JButton("<html>Find Mutual<br>Information Pairs...</html>");
    this.neighborsButton.setEnabled(false);
    this.neighborsButton.addActionListener(
        new AbstractAction (){
          public void actionPerformed (ActionEvent event){
            if(mutualInformationDialog == null){
              mutualInformationDialog = new MutualInfoDialog();
            }
            mutualInformationDialog.pack();
            mutualInformationDialog.setLocationRelativeTo(Cytoscape.getDesktop());
            mutualInformationDialog.setVisible(true);
          }//actionPerformed
        }//AbstractAction
        );
    
    this.statementsButton = new JButton("<html>Make Biological<br>Statements...</html>");
    this.statementsButton.setEnabled(false);
    this.statementsButton.addActionListener(
        new AbstractAction (){
          public void actionPerformed (ActionEvent event){
            BioDataServer bioDataServer = Cytoscape.getCytoscapeObj().getBioDataServer();
            if(bioDataServer == null){
              System.out.println("The bioDataServer is null.");
              JOptionPane.showMessageDialog(PGDialog.this,
                                            "No annotations loaded.", 
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE);
              
              return;
            }
            if(statementDialog == null){
              statementDialog = new StatementDialog(bioDataServer);
            }
            statementDialog.pack();
            statementDialog.setLocationRelativeTo(Cytoscape.getDesktop());
            statementDialog.setVisible(true);
          }//actionPerformed
        }//AbstractAction
        );
    
    actionsPanel.add(analyzeButton);
    actionsPanel.add(distributionButton);
    actionsPanel.add(statementsButton);
    actionsPanel.add(neighborsButton); 
    
    raPanel.add(actionsPanel);
    mainPanel.add(raPanel);
    
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(
     new AbstractAction (){
       public void actionPerformed (ActionEvent event){
         PGDialog.this.dispose();
       }//actionPerformed
     }//AbstractAction
     );
    buttonsPanel.add(closeButton);
    
    mainPanel.add(buttonsPanel);
    
    setContentPane(mainPanel);
    
    
  }//create

  /**
   * @return the number of XML file paths in this dialog 
   */
  protected int getNumXmlFiles (){
    if(this.xmlFileList == null){
      return 0;
    }
    return this.xmlFileList.getModel().getSize();
  }//getXmlFiles


  /**
   * @return an array of XML file paths of files the user selected from the GUI
   */
  protected String [] getSelectedXmlFiles (){

    Object [] selected = this.xmlFileList.getSelectedValues();
    ArrayList list = new ArrayList();
               
    for(int i = 0; i < selected.length; i++){
      String selectedFile = (String)selected[i];
      String fullPath = (String)this.xmlFilePaths.get(selectedFile);
      if(fullPath == null){
        throw new IllegalStateException("Selected file does not have a " +
                                        "full path in xmlFilePaths!");
      }
      list.add(fullPath);
    }//for i
               
    String [] selectedFiles = 
      (String[])list.toArray(new String[list.size()]);
  
    return selectedFiles;
  }//getSelectedXmlFiles
               
  
  /**
   * Calls setEnabled(enable) for each button in this dialog (except for the
   * one that calulates interactions, the Close button, and the Browse button, 
   * obviously)
   */
  protected void setButtonsEnabled (boolean enable){
    distributionButton.setEnabled(enable);
    neighborsButton.setEnabled(enable);
    statementsButton.setEnabled(enable);
  }//setButtonsEnabled
  
  /**
   * A FileFilter that only accepts XML files
   */
  protected class XmlFileFilter 
    extends FileFilter {
    
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

  /**
   * @return the currently selected XML project files
   */
  public String [] getSelectedProjectFiles (){
    Object [] objs = this.xmlFileList.getSelectedValues();
    String [] selectedFiles = new String[objs.length];
    for(int i = 0; i < objs.length; i++){
      String fileName = (String)objs[i];
      selectedFiles[i] = (String)xmlFilePaths.get(fileName);
    }//for i
    return selectedFiles;
  }//getSelectedProjectFiles
  
}//class PGDialog
