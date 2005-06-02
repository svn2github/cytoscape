/**
 * A dialog that contains a tab for each discrete phenotype. Each tab
 * contains sliders for each possible value of a phenotype so that
 * the user can set up the ordering of these values.
 *
 * @author Iliana Avila
 * @version 2.0
 */
package phenotypeGenetics.ui;

import phenotypeGenetics.*;
import phenotypeGenetics.xml.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import cytoscape.*;

public class DiscretePhenotypeDialog extends JDialog {
  
  /**
   * An object that stores discrete phenotypes and their ranked values
   */
  protected DiscretePhenotypeRanking phenotypeRanking;
  /**
   * A list of PhenotypeDiscreteValsPanels, one per each phenotype-name
   */
  protected ArrayList tabs;
  /**
   * Help text
   */
  protected static final String HELP_HTML = 
    "<html>For each phenotype value move the notch on the slider left or right.<br>"+
    "The final relative positions of all the notches will determine the<br>"+
    "relationships (>,=,<) between every pair of values. For example, if<br>"+
    "the notch for phenotype-value A is to the left of phenotype value-B,<br>"+
    "then phenotype-value A is less than phenotype value-B.<br>" +
    "Phenotype-values can also be equal if their notches are aligned.</html>";
  
  /**
   * Constructor, creates a modal dialog
   */
  public DiscretePhenotypeDialog (DiscretePhenotypeRanking ranking){
    super(Cytoscape.getDesktop(), "Phenotype Relations", true);// modal
    this.phenotypeRanking = ranking;
    create();
  }//DiscretePhenotypeDialog

  /**
   * @return a Map from phenotype names (Strings) to ArrayLists.
   * Each mapped ArrayList contains ArrayLists itself. The values of
   * the 2nd level ArrayLists are Strings that represent phenotype-names.
   * The ordering of the 2nd level lists represent a "less" to "greater"
   * phenotype-value. Phenotype names in the same bin have the same "less" (or "greater")
   * value.
   */
  public Map getAllPhenotypeBins (){
    
    Map returnMap = new HashMap();
    
    for(int i = 0; i < this.tabs.size(); i++){
      PhenotypeDiscreteValsPanel panel = (PhenotypeDiscreteValsPanel)this.tabs.get(i);
      List bins = panel.getPhenotypeBins();
      returnMap.put(panel.getPhenotypeName(), bins);
    }//for i
    
    return returnMap;
  }//getAllPhenotypeBins

  /**
   * @return the DiscretePhenotypeRanking in this object according to the
   * ranks that the user gave in the dialog
   */
  public DiscretePhenotypeRanking getUpdatedDiscreteRanking (){
    Map bins = getAllPhenotypeBins();
    Set keySet = bins.keySet();
    String [] phenoNames = (String[])keySet.toArray(new String[0]);
    for(int i = 0; i < phenoNames.length; i++){
      List rankings = (List)bins.get(phenoNames[i]);
      this.phenotypeRanking.setPhenotypeRanking(phenoNames[i],rankings);
    }//for i
    return this.phenotypeRanking;
  }//getUpdatedDiscreteRanking
    
  /**
   * Creates the dialog
   */
  protected void create (){
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    JTabbedPane tabbedPane = new JTabbedPane();
    
    String [] phenotypeNames = this.phenotypeRanking.getPhenotypeNames();
    this.tabs = new ArrayList();
    
    for(int i = 0; i < phenotypeNames.length; i++){
      String key = phenotypeNames[i];
      PhenotypeDiscreteValsPanel valsPanel = null;
      if(this.phenotypeRanking.isPhenotypeRanked(key)){
        valsPanel = 
          new PhenotypeDiscreteValsPanel(key,
                                         this.phenotypeRanking.getPhenotypeRanking(key));
      }else{
        String [] valsArray = this.phenotypeRanking.getUnrankedValues(key);
        valsPanel = 
          new PhenotypeDiscreteValsPanel(key,valsArray);
      }
      
      this.tabs.add(valsPanel);
      tabbedPane.addTab(key,valsPanel.getPanel()); // key is title
    }//while it
    
    mainPanel.add(tabbedPane);
    
    JPanel buttonPanel = new JPanel();
        
    JButton ok = new JButton("OK");
    ok.addActionListener(
                         new AbstractAction (){
                           public void actionPerformed (ActionEvent event){
                             DiscretePhenotypeDialog.this.dispose();
                           }//actionPerformed
                         }//AbstractAction
                         );
    buttonPanel.add(ok);
    
    JButton save = new JButton("Save");
    save.addActionListener(
               new AbstractAction (){
                 public void actionPerformed (ActionEvent event){
                   DiscretePhenotypeRanking r =
                     getUpdatedDiscreteRanking();
                   PGDialog dialog = PhenotypeGeneticsPlugIn.getDialog();
                   String [] xfiles = dialog.getSelectedProjectFiles();
                   String message =  "<html>Saved discrete phenotype ranks to:<br>";
                   for(int i = 0; i < xfiles.length; i++){
                     try{
                       ProjectXmlReader.writeDiscretePhenotypeInfo(xfiles[i],r);
                     }catch(Exception ex){
                       JOptionPane.showMessageDialog(DiscretePhenotypeDialog.this,
                                                     ex.getMessage());
                     }
                     message = message + xfiles[i]+ "<br>";
                   }//for i
                   message = message + "</html>";
                   JOptionPane.showMessageDialog(DiscretePhenotypeDialog.this,message);
                 }//actionPerformed
               }
               );
    buttonPanel.add(save);

    JButton help = new JButton("Help");
    help.addActionListener(
                           new AbstractAction(){
                             public void actionPerformed (ActionEvent event){
                               JOptionPane.showMessageDialog(DiscretePhenotypeDialog.this, 
                                                             HELP_HTML); 
                             }//actionPerformed
                           }//AbstractAction
                           );
    buttonPanel.add(help);
   
    mainPanel.add(buttonPanel);
    
    setContentPane(mainPanel);
  }//create
  
}//DiscretePhenotypeDialog
