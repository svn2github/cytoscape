//CorrelationFinderDialog.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui.actions;
//------------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import csplugins.isb.pshannon.dataMatrix.*;
import csplugins.isb.pshannon.dataMatrix.gui.*;

//------------------------------------------------------------------------------
/**
 * This class provides visualization of the correlation of gene expression vectors
 * with a sample expression vector. The average of the normalized expression vectors
 * of the current selection is the target vector against which all other genes are
 * compared. Correlation values range from +1 (correlated) to 0 (uncorrelated) to
 * -1 (anti-correlated).
 *
 * @author Andrew Markiel
 * @date 07/15/02
 */
//-------------------------------------------------------------------------------------------------
public class CorrelationFinderDialog extends JDialog implements ActionListener {

  protected DataMatrixBrowser parentBrowser;

  private DataMatrixLens lens;
  private CorrelationFinder finder;
  private double currentCorrelationThreshold = 1.0;
  private ArrayList namesOfCorrelatedNodes = new ArrayList ();
  //private String [] namesOfCorrelatedNodes = new String [0];
  private String [] namesOfReferenceNodes = new String [0];   // the nodes to correlate -to-

  JTextField correlatedCountTextField;
  JTextField thresholdTextField;
  String correlationMode = "Positive";

//-------------------------------------------------------------------------------------------------
public CorrelationFinderDialog (DataMatrixBrowser parent) // , DataMatrixLens lens)
{ 
  super ();
  setTitle ("Correlation Finder");
  this.parentBrowser = parent;
  this.lens = parentBrowser.getCurrentLens ();
  createGui ();
  
}
//-------------------------------------------------------------------------------------------------
public void createGui ()
{
  int selectedRowCount = lens.getSelectedRowCount ();
  
  if (selectedRowCount < 1) {
    String msg = "Please select one or more rows and try again.";
    JOptionPane.showMessageDialog (parentBrowser, msg);
    return;
    }

  namesOfReferenceNodes = lens.getSelectedRowTitles ();  
   //   String [] selectedNames = 
  finder = new CorrelationFinder (lens);
  finder.buildCorrelationTable (namesOfReferenceNodes);
  Map table = finder.getCorrelationTable ();

     //----------- construct the gui proper
  
  JSlider slider = new JSlider (JSlider.HORIZONTAL, 0, 100, 100);
  slider.setBorder (BorderFactory.createEmptyBorder(0,0,10,0));
  slider.setMajorTickSpacing (10);
  slider.setMinorTickSpacing (1);
  slider.setPaintTicks (true);
  slider.setPaintLabels (true);
  slider.addChangeListener (new SliderListener ());

  JButton browserSelectButton = new JButton ("Select in Browser");
  browserSelectButton.addActionListener (new SelectNodesAboveThreshold ());
  
  Container contentPane = getContentPane ();
  contentPane.setLayout (new BorderLayout ());
  String lineSep = System.getProperty ("line.separator");

  JPanel topPanel = new JPanel ();

  topPanel.setBorder (BorderFactory.createCompoundBorder (
                        BorderFactory.createEmptyBorder (10,10,2,10),
                        BorderFactory.createCompoundBorder (
                          BorderFactory.createEtchedBorder (),
                          BorderFactory.createEmptyBorder (10,10,10,10))));

  JPanel middlePanel = new JPanel ();
  // middlePanel.setLayout (new GridLayout (2,1));
  JPanel readoutPanel = new JPanel ();
  middlePanel.add (readoutPanel);
  topPanel.add (middlePanel, BorderLayout.CENTER);
  readoutPanel.setLayout (new GridLayout (2, 2));
  
  correlatedCountTextField = new JTextField ("0", 6);

  thresholdTextField = new JTextField ("100", 6);

  readoutPanel.add (new JLabel ("Nodes meeting threshold "));// , BorderLayout.WEST);
  readoutPanel.add (correlatedCountTextField); //, BorderLayout.CENTER);
  readoutPanel.add (new JLabel ("Threshold "));
  readoutPanel.add (thresholdTextField);

  JPanel radioButtonPanel = new JPanel ();
  radioButtonPanel.setLayout (new GridLayout (1,3));

  JRadioButton positiveButton = new JRadioButton ("Positive", true);
  JRadioButton negativeButton = new JRadioButton ("Negative");
  JRadioButton bothButton = new JRadioButton ("Both");

  positiveButton.addActionListener (this);
  negativeButton.addActionListener (this);
  bothButton.addActionListener (this);

  radioButtonPanel.add (positiveButton);
  radioButtonPanel.add (negativeButton);
  radioButtonPanel.add (bothButton);


  ButtonGroup radioGroup = new ButtonGroup ();
  radioGroup.add (positiveButton);
  radioGroup.add (negativeButton);
  radioGroup.add (bothButton);

  //middlePanel.add (radioButtonPanel);


  JPanel sliderPanel = new JPanel ();
  sliderPanel.setLayout (new BorderLayout ());
  sliderPanel.setBorder (BorderFactory.createEmptyBorder (20,20,20,20));
  sliderPanel.add (radioButtonPanel, BorderLayout.NORTH);
  sliderPanel.add (slider, BorderLayout.CENTER);

  JPanel textAndSliderPanel = new JPanel ();
  textAndSliderPanel.setLayout (new BorderLayout ());
  contentPane.add (textAndSliderPanel,  BorderLayout.CENTER);
  textAndSliderPanel.add (topPanel, BorderLayout.CENTER);
  textAndSliderPanel.add (sliderPanel, BorderLayout.SOUTH);

  JPanel buttonPanel = new JPanel ();
  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction ());
  contentPane.add (buttonPanel, BorderLayout.SOUTH);
  buttonPanel.add (browserSelectButton);
  buttonPanel.add (dismissButton);

  pack ();
  placeInCenter ();
  setVisible (true);
  refreshSelection ();

} // createGui
//-------------------------------------------------------------------------------------------------
protected void refreshSelection ()
// called by the SliderListener, and the radio button action listener, so that
// any change (to either slider position, or mode -- positive correlations, negative, or both)
// causes a fresh calculation and display of nodes correlated to the initial selected node
// or nodes
{
  int count = findCorrelatedNodes ();
  String countAsString = (new Integer (count)).toString ();
  correlatedCountTextField.setText (countAsString);
}
//-------------------------------------------------------------------------------------------------
protected class SliderListener implements ChangeListener {
        
  public SliderListener () {;}

  public void stateChanged (ChangeEvent ce) {
    JSlider source = (JSlider) ce.getSource();
    if (!source.getValueIsAdjusting ()) {
      int currentValue = source.getValue ();
      thresholdTextField.setText ((new Integer (currentValue)).toString ());
      currentCorrelationThreshold  = currentValue / 100.0;
      refreshSelection ();
      } // if
    } // stateChanged

} // inner class SliderListener
//-------------------------------------------------------------------------------------------------
private int findCorrelatedNodes ()
{
  Map correlations = finder.getCorrelationTable ();
  String [] keys = (String []) correlations.keySet().toArray (new String [0]);
  namesOfCorrelatedNodes = new ArrayList ();

  for (int k=0; k < keys.length; k++) {
    String nodeName = keys [k];
    double correlation = ((Double) correlations.get (nodeName)).doubleValue ();

    boolean correlated = false;
    if (correlation >= currentCorrelationThreshold && 
       (correlationMode.equals ("Positive") || correlationMode.equals ("Both")))
      correlated = true;

    if ((-1.0 * correlation) >= currentCorrelationThreshold && 
       (correlationMode.equals ("Negative") || correlationMode.equals ("Both")))
      correlated = true;

    if (correlated)
      namesOfCorrelatedNodes.add (nodeName);
    } // for k

  //namesOfCorrelatedNodes = (String []) nodeList.toArray (new String [0]);
  //namesOfCorrelatedNodes = (String []) nodeList.toArray (new String [0]);
  return (namesOfCorrelatedNodes.size ());

} // findCorrelatedNodes
//-------------------------------------------------------------------------------------------------
class SelectNodesAboveThreshold extends AbstractAction  {

  SelectNodesAboveThreshold () {super ("");};
  
  public void actionPerformed (ActionEvent e) {
    parentBrowser.clearAllSelections ();
    for (int i=0; i < namesOfReferenceNodes.length; i++) 
      if (!namesOfCorrelatedNodes.contains (namesOfReferenceNodes [i]))
        namesOfCorrelatedNodes.add (namesOfReferenceNodes [i]);
    
    String [] names = (String []) namesOfCorrelatedNodes.toArray (new String [0]);
    parentBrowser.selectRowsByName (names);
    }

} // inner class SelectNodesAboveThreshold
//-------------------------------------------------------------------------------------------------
public void actionPerformed (ActionEvent e) 
{
  correlationMode = e.getActionCommand ();
  refreshSelection ();

} // actionPerformed  (radio button callback)
//-------------------------------------------------------------------------------------------------
public class DismissAction extends AbstractAction {

  DismissAction () {;}

  public void actionPerformed (ActionEvent e) {
    CorrelationFinderDialog.this.dispose ();
    }

} // DismissAction
//-----------------------------------------------------------------------------------
protected void placeInCenter ()
{
  GraphicsConfiguration gc = getGraphicsConfiguration ();
  int screenHeight = (int) gc.getBounds().getHeight ();
  int screenWidth = (int) gc.getBounds().getWidth ();
  int windowWidth = getWidth ();
  int windowHeight = getHeight ();
  setLocation ((screenWidth-windowWidth)/2, (screenHeight-windowHeight)/2);

} // placeInCenter
//------------------------------------------------------------------------------
}  // class CorrelationFinderDialog

