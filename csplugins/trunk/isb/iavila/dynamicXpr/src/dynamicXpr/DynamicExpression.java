/**  Copyright (c) 2003 Institute for Systems Biology
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
 * A class that allows the user to see the changes in gene expression
 * by coloring nodes according to their expression values in different 
 * conditions. It colors the nodes sequentially through each condition
 * at a user set delay between conditions.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.net
 * @version %I%, %G%
 * @since 1.1
 */

//TODO: LEFT HERE Handle meta-nodes!!!!

package dynamicXpr;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.io.*;

import cytoscape.*;
import cytoscape.visual.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.calculators.*;
import cytoscape.data.*;

import dynamicXpr.dialogs.*;

public class DynamicExpression extends AbstractAction {
  
  /**
   * The node attribute name that is used to assign an expression
   * value to each node.
   */
  public static final String EXPRESSION_ATTR = "expression";

  /**
   * The name of the node color calculator used to color nodes
   * according to expression
   */
  public static final String NODE_COLOR_CALC_NAME = "dynamicXpr";
  
  protected Timer timer;
  protected DisplayFramesAction action;
  protected DynamicExpressionDialog dialog;
  protected int pause;
  protected File currentDirectory;
  protected NodeColorCalculator oldNodeColorCalculator;
  protected NodeColorCalculator dynamicXprCalculator;
  protected HashMap oldFillColorAttr;

  /**
   * Constructor.
   */
  public DynamicExpression (){
    super("Dynamic Expression...");
    this.currentDirectory = new File (System.getProperty("user.dir"));
    this.pause = -1;
  }
  /**
   * Constructor.
   *
   * @param parent_menu the JMenu to which this instance will be added
   * if null, the instance won't be added anywhere
   */
  public DynamicExpression (JMenu parent_menu) {
    this();
    if(parent_menu != null){
      parent_menu.add(this);
    }
  }//DynamicExpression

  /**
   * Fills <code>oldFillColorAttr</code> member variable with <code>CyNode</code> keys
   * and the values returned for the <code>NodeAppearanceCalculator.nodeFillColorBypass</code>
   * node attribute.
   */
  protected void saveFillColor (){
    CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
    if(this.oldFillColorAttr == null){
      this.oldFillColorAttr = new HashMap();
    }else{
      this.oldFillColorAttr.clear();
    }
    java.util.List nodeList = cyNetwork.nodesList();
    if(nodeList == null){
      System.err.println("DynamicExpression.saveFillColor(): CyNetwork has no nodes.");
      return;
    }
    CyNode [] nodes = (CyNode[])nodeList.toArray(new CyNode[nodeList.size()]);
    
    for(int i = 0; i < nodes.length; i++){
      Object value = 
        cyNetwork.getNodeAttributeValue(nodes[i],
                                        NodeAppearanceCalculator.nodeFillColorBypass);
      this.oldFillColorAttr.put(nodes[i],value);
    }//for i

  }//saveFillColor
  
  /**
   * Get ready for playing the movie.
   */
  protected void prepareBeforePlay (){
    CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();

    saveFillColor();
    cyNetwork.deleteNodeAttribute(NodeAppearanceCalculator.nodeFillColorBypass);
    
    VisualMappingManager manager = Cytoscape.getDesktop().getVizMapManager();
    NodeAppearanceCalculator nac = manager.getVisualStyle().getNodeAppearanceCalculator();
    this.oldNodeColorCalculator = nac.getNodeFillColorCalculator();
    if(this.dynamicXprCalculator == null){
      createCalculator();
    }
    nac.setNodeFillColorCalculator(this.dynamicXprCalculator);
    // Make sure that the catalog has one copy of this calculator
    NodeColorCalculator check =
      manager.getCalculatorCatalog().getNodeColorCalculator(NODE_COLOR_CALC_NAME);
    if(check == null){
      manager.getCalculatorCatalog().addNodeColorCalculator(this.dynamicXprCalculator);
    }
  }//prepareBeforePlay
  
  /**
   * Called when a new <code>ExpressionData</code> object is loaded.
   */
  public void adjustForNewExpressionData (){}
  
  /**
   * Returns true if the node attributes has an attribute called 
   * <code>DynamicExpression.EXPRESSION_ATTR</code>.
   */
  public boolean expressionAttributeExists () {
    CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
    String [] attNames = cyNetwork.getNodeAttributesList();
    for(int i = 0; i < attNames.length; i++){
      if(attNames[i].equals(EXPRESSION_ATTR)){
        return true;
      }
    }
    return false;
  }//expressionAttributesExists
  
  /**
   * @return the node color calculator used for coloring nodes according 
   * to their <code>EXPRESSION_ATTR</code> values.
   */
  public NodeColorCalculator getDynamicXprColorCalculator (){
    return this.dynamicXprCalculator;
  }//getDynamicXprColorCalculator
  
  /**
   * Creates the <code>NodeColorCalculator</code> that will be used to calculate the fill color
   * of nodes.
   */
  protected NodeColorCalculator createCalculator (){

    ContinuousMapping contMapping = 
      new ContinuousMapping( 
                            new Color(204,204,204),
                            ObjectMapping.NODE_MAPPING
                            );
    contMapping.setControllingAttributeName(EXPRESSION_ATTR, 
                                            Cytoscape.getCurrentNetwork(),
                                            true);
    BoundaryRangeValues brVals;
    brVals = new BoundaryRangeValues();
    brVals.lesserValue = new Color(0,0,255);
    brVals.equalValue = new Color(0,0,255);
    brVals.greaterValue = new Color(0,0,255);
    contMapping.addPoint(-1, brVals);
    
    brVals = new BoundaryRangeValues();
    brVals.lesserValue = new Color(255,255,255);
    brVals.equalValue = new Color(255,255,255);
    brVals.greaterValue = new Color(255,255,255);
    contMapping.addPoint(0.0,brVals);
    
    brVals = new BoundaryRangeValues();
    brVals.lesserValue = new Color(255,0,0);
    brVals.equalValue = new Color(255,0,0);
    brVals.greaterValue = new Color(255,0,0);
    contMapping.addPoint(1.0,brVals);
    
    this.dynamicXprCalculator =
      new GenericNodeColorCalculator(
                                     NODE_COLOR_CALC_NAME,
                                     contMapping
                                     );
    return this.dynamicXprCalculator;
  }//createCalculator
    
  /**
   * Pops up the <code>DynamicExpressionDialog</code>.
   */
  public void actionPerformed(ActionEvent event){
    if(dialog == null){
      dialog = new DynamicExpressionDialog(this,"Dynamic Expression"); 
    }
    prepareBeforePlay();
    dialog.pack();
    dialog.setLocationRelativeTo(Cytoscape.getDesktop());
    //dialog.setResizable(false);
    dialog.setVisible (true);
  }//actionPerformed

  /**
   * Restores the old node color calculator.
   */
  public void restoreOldNodeColorCalculator (){
    VisualMappingManager manager = Cytoscape.getDesktop().getVizMapManager();
    NodeAppearanceCalculator nac = manager.getVisualStyle().getNodeAppearanceCalculator();
    nac.setNodeFillColorCalculator(this.oldNodeColorCalculator);
    if(this.oldFillColorAttr != null){
      CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
      Set keyset = this.oldFillColorAttr.keySet();
      CyNode [] nodes = (CyNode[])keyset.toArray(new CyNode[keyset.size()]);
      for(int i = 0; i < nodes.length; i++){
        Object value = this.oldFillColorAttr.get(nodes[i]);
        cyNetwork.setNodeAttributeValue(nodes[i],
                                        NodeAppearanceCalculator.nodeFillColorBypass,
                                        value);
      }//for i
    }
    Cytoscape.getCurrentNetworkView().redrawGraph(false,true);
    //Cytoscape.getDesktop().redrawGraph(false,true);// don't do layout, do apply vizmaps
  }//restoreOldNodeColorCalculator
  
  /**
   * Dynamically displays the gene expression data contained in CyWindow
   * See loadExpressionData() in CyWindow
   * 
   * @param delay  how long (in milliseconds) should it take for frames to change
   *               (each frame being one condition)
   */
  public void play (int delay) {
    //prepareBeforePlay();
    ExpressionData expressionData = Cytoscape.getCurrentNetwork().getExpressionData();
    if(expressionData == null){ return;}
    if(this.action == null){
	    this.action = new DisplayFramesAction(expressionData.getConditionNames());
    }
    if(this.timer != null){
      this.timer.stop();
    }
    this.timer = new Timer(delay, this.action);
    this.timer.setInitialDelay(0);
    this.timer.start();
    this.pause = 0;
  }//display
  
  /**
   * Sets the delay in milliseconds for the next frame to appear
   */
  public void setTimerDelay (int delay){
    if(this.timer == null){return;}
    this.timer.setDelay(delay);
  }//setTimerDelay
  
  /**
   * Displays the given condition in the current CyNetwork
   */
  static public void displayCondition (String conditionName){
    CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
    ExpressionData expressionData = cyNetwork.getExpressionData();
    java.util.List nList = cyNetwork.nodesList();
    CyNode [] nodes = (CyNode[])nList.toArray( new CyNode[nList.size()]);
        
    for(int i = 0; i < nodes.length; i++){
      String uid = (String)cyNetwork.getNodeAttributeValue(nodes[i],Semantics.CANONICAL_NAME);
      if(uid == null){
        System.err.println("DynamicExpression.displayCondition, no canonical name for node " +
                           nodes[i]);
        continue;
      }
      mRNAMeasurement measurement = expressionData.getMeasurement(uid,conditionName);
      if(measurement == null){continue;}
	    double ratio = measurement.getRatio();
      cyNetwork.setNodeAttributeValue(nodes[i],EXPRESSION_ATTR,new Double(ratio)); 
    }//for c
    VisualMappingManager vmManager = Cytoscape.getDesktop().getVizMapManager();
    vmManager.applyNodeAppearances();
    Cytoscape.getCurrentNetworkView().redrawGraph(false,false);
    //Cytoscape.getDesktop().redrawGraph(false,false); // does not do layout
  }//displayCondition
  
  /**
   * Stops the current animation
   */
  public void stop () {
    this.timer.stop();
    this.action.reset();
    this.pause = -1;
  }//stop

  /**
   * Pauses the current animation
   */
  public void pause () {
    if(this.pause == 1){
      // we were in pause, so user wants to continue play
      this.timer.start();
      this.pause = 0;
    }else if(this.pause == 0){
      // we were not in pause, so user wants to pause play
      
      this.timer.stop();
      this.pause = 1;
    }
  }//pause

  /**
   * Returns whether or not the display is currently paused, or if the animation is not playing.
   * 0 : not paused and playing
   * 1 : paused
   *-1 : not playing
  */
  public int isPaused () {
    return this.pause;
  }//isPaused
    
  // --------- internal classes ----------
  class DisplayFramesAction implements ActionListener {
	
    String [] conditions;
    int currCond;
	
    DisplayFramesAction (String [] conditions) {
	    this.conditions = conditions;
	    this.currCond = 0;
    }//DisplayFramesAction

    public void actionPerformed(ActionEvent evt) {
      
      if(currCond < conditions.length){
        dialog.updateConditionsSlider(currCond,conditions[currCond]);
        DynamicExpression.displayCondition(conditions[currCond]);
        currCond++;
      }else{
        DynamicExpression.this.stop();
        dialog.conditionsSliderEnabled(true);
      }
      //SwingWorker worker = new SwingWorker () {
      
      //    public Object construct () {
      //    if(currCond < conditions.length){
      //      DynamicExpression.this.displayCondition(conditions[currCond]);
      //      dialog.updateConditionsSlider(currCond,conditions[currCond]);
      //      currCond++;
      //    }else{
      //      DynamicExpression.this.stop();
      //      dialog.conditionsSliderEnabled(true);
      //    }
      //    return null;
      //  }
      //};
      //worker.start();
    }

    public void reset () {
	    currCond = 0;
    }
  }//DisplayFramesAction

}//DynamicExpression
