/**  Copyright (c) 2005 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
/**
 * A <code>javax.swing.JDialog</code> that accepts user parameters for the
 * <code>MutualInfoCalculator</code> class.
 *
 * @author Greg Carter
 * @author Iliana Avila (transfered to C2.0)
 */
package phenotypeGenetics;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import cytoscape.*;
import javax.swing.border.*;

public class MutualInfoDialog extends JDialog{

  protected static final double WRONG_SCORE = -1;
  protected static final int	WRONG_REPS = -1;
  
  /**
   * The <code>MutualInfoCalculator</code> to which
   * this <code>MutualInfoDialog</code> interfaces to.
   */
  protected MutualInfoCalculator  theInfo;
  
  /**
   * The actions that will be added to buttons that will 
   * be added to <code>MutualInfoCalculator</code>
   * objects that pop-up when the "Calculate" button is pressed.
   */
  protected AbstractAction [] tableActions;

  /**
   * The input field for score threshold.
   */
  protected JTextField scoreField;

  protected JTextField repsField;

  /**
   * Constructor.
   * 
   * @param overlap the <code>CytoscapeWindow</code> to which this dialog 
   *                  interfaces to.
   */
  public MutualInfoDialog(MutualInfoCalculator info){

    this.tableActions = new AbstractAction[0];
    this.setTitle("Mutual Information");
    setMutualInfoCalculator(info);
    create();
  }

  /**
   * Sets the <code>MutualInfoCalculator</code> to which this dialog
   * interfaces to.
   * @param overlap
   */
  public void setMutualInfoCalculator (MutualInfoCalculator info){
    this.theInfo = info;
  }//setMutualInfoCalculator

  /**
   * Sets the array of <code>JButton</code> objects that
   * will get added to the bottom of the <code>MutualInfoTable</code>
   * objects that pop-up when the user presses the "Calculate" button.
   */
  public void setActionsForTable (AbstractAction [] button_actions){
    this.tableActions = button_actions;
  }//setActionsForTable

  /**
   * Creates the dialog.
   */
  protected void create (){
    
    JPanel mainPanel  = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createTitledBorder("Mutual Information Parameters"));

    Border panelPadding = BorderFactory.createEmptyBorder(10,10,10,10); 
    Component padding = Box.createRigidArea(new Dimension(20,0));

    JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new GridLayout(2, 1));
    JLabel scoreLabel = new JLabel("Minimum -Log(p):"); 
    labelPanel.add(scoreLabel);
    JLabel repsLabel = new JLabel("Number of Randomizations:  "); 
    labelPanel.add(repsLabel);

    JPanel fieldPanel = new JPanel();
    fieldPanel.setLayout(new GridLayout(2, 1));

    double minScore = this.theInfo.getMinScore();
    this.scoreField = new JTextField(String.valueOf(minScore), 5);
    this.scoreField.addActionListener(new ScoreFieldListener());
    fieldPanel.add(this.scoreField);
        
    int numRandomReps = this.theInfo.getNumRandomReps();
    this.repsField = new JTextField(String.valueOf(numRandomReps), 5);
    this.repsField.addActionListener(new RepsFieldListener());
    fieldPanel.add(this.repsField);
        
    //Put the panels in another panel, labels on left,
    //text fields on right
    JPanel contentPane = new JPanel();
    contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10,
                                                          10, 10));
    contentPane.setLayout(new BorderLayout());
    contentPane.add(labelPanel, BorderLayout.CENTER);
    contentPane.add(fieldPanel, BorderLayout.EAST);
    
    mainPanel.add(contentPane);
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setBorder(panelPadding);
    JButton calculateButton = new JButton("Calculate");
    calculateButton.addActionListener(new CalculateButtonListener());
    buttonPanel.add(calculateButton);
    JButton okButton = new JButton("Dismiss");
    okButton.addActionListener(new OKButtonListener());
    buttonPanel.add(padding);
    buttonPanel.add(okButton);

    mainPanel.add(buttonPanel);
    setContentPane(mainPanel);
  }//create

  /**
   * Checks for correct score input, if correct it returns it.
   */
  protected double checkScoreInput (String text){
    double score;
    try{
      score = Double.parseDouble(text);
    }catch (NumberFormatException ex){
      // Tell the user that he has to enter a double
      JOptionPane.showMessageDialog( this,
                                     "Please enter a positive decimal number",
                                     "Input Error",
                                     JOptionPane.ERROR_MESSAGE);
      
      return WRONG_SCORE;
      
    }
    if(score < 0){
      
      // Tell the user that he has to enter a double
      JOptionPane.showMessageDialog(this,
                                    "Please enter a positive decimal number",
                                    "Input Error",
                                    JOptionPane.ERROR_MESSAGE);
      
      return WRONG_SCORE;
    }
    return score;
  }//checkPValueInput


  /**
   * Checks for correct reps input, if correct it returns it.
   */
  protected int checkRepsInput (String text){
    int reps;
    try{
      reps = Integer.parseInt(text);
    }catch (NumberFormatException ex){
      // Tell the user that he has to enter an int 
      JOptionPane.showMessageDialog( this,
                                     "Please enter a positive integer of 5 or greater",
                                     "Input Error",
                                     JOptionPane.ERROR_MESSAGE);
      
      return WRONG_REPS;
      
    }
    if(reps < 5){
      
      // Tell the user that he has to enter an int
      JOptionPane.showMessageDialog(this,
                                    "Please enter a positive integer of 5 or greater",
                                    "Input Error",
                                    JOptionPane.ERROR_MESSAGE);
      
      return WRONG_REPS;
    }
    return reps;
  }//


  //------------------- Listeners ----------------------//
  protected class RepsFieldListener extends AbstractAction{
    RepsFieldListener (){
      super("");
    }//RepsFieldListener
    
    public void actionPerformed (ActionEvent e){
      Object source = e.getSource();
      JTextField tf;
      if(source instanceof JTextField){
        tf = (JTextField)source;
      }else{
        return;
      }
      // Validate input
      String input = tf.getText();
      int reps = MutualInfoDialog.this.checkRepsInput(input);
      if(reps != MutualInfoDialog.WRONG_REPS){
        MutualInfoDialog.this.theInfo.setNumRandomReps(reps);
      }
    }//actionPerformed
  }//class RepsFieldListener


  protected class ScoreFieldListener extends AbstractAction{
    ScoreFieldListener (){
      super("");
    }//ScoreFieldListener
    
    public void actionPerformed (ActionEvent e){
      Object source = e.getSource();
      JTextField tf;
      if(source instanceof JTextField){
        tf = (JTextField)source;
      }else{
        return;
      }
      // Validate input
      String input = tf.getText();
      double score = MutualInfoDialog.this.checkScoreInput(input);
      if(score != MutualInfoDialog.WRONG_SCORE){
        MutualInfoDialog.this.theInfo.setMinScore(score);
      }
    }//actionPerformed
  }//class ScoreFieldListener

  protected class OKButtonListener extends AbstractAction {

    OKButtonListener (){
      super("");
    }//OKButtonListener

    public void actionPerformed (ActionEvent e){
      MutualInfoDialog.this.dispose();
    }//actionPerformed
  }//class OKButtonListener

  protected class CalculateButtonListener extends AbstractAction {
    CalculateButtonListener (){
      super("");
    }//CalculateButtonListener

    public void actionPerformed (ActionEvent e){
      
      String input = MutualInfoDialog.this.scoreField.getText();
      double score = MutualInfoDialog.this.checkScoreInput(input);
      if(score != MutualInfoDialog.WRONG_SCORE){
        MutualInfoDialog.this.theInfo.setMinScore(score);
      }else{
        return;
      }

      input = MutualInfoDialog.this.repsField.getText();
      int reps = MutualInfoDialog.this.checkRepsInput(input);
      if(reps != MutualInfoDialog.WRONG_REPS){
        MutualInfoDialog.this.theInfo.setNumRandomReps(reps);
      }else{
        return;
      }

      //  Now tell the MutualInfoCalculator to do its business
      MutualInfo[] pairs = MutualInfoDialog.this.theInfo.miFinder();

      //  Bring up a table dialog for selection
      if ( pairs.length > 0 ) {
        String title = "Mutual Information Pairs with -Log(p) > "+theInfo.getMinScore()+
		" ("+pairs.length+" pairs), "+theInfo.getNumRandomReps()+" randomizations" ;
        MutualInfoTable table = new MutualInfoTable( theInfo, pairs, title ); 
        table.pack();
        table.setLocationRelativeTo(MutualInfoDialog.this);
        table.setVisible( true );
      }
    }//actionPerformed
  }//class CalculateButtonListener

}//class  MutualInfoDialog
