// ActivePathsParametersPopupDialog
//-----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-----------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
//-----------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.JSlider; 
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.AbstractAction;
import java.awt.BorderLayout;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.text.NumberFormat;

import cytoscape.data.*;
import cytoscape.dialogs.NewSlider;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
//-----------------------------------------------------------------------------
public class ActivePathsParametersPopupDialog extends JDialog {
    ActivePathsParametersPopupDialogListener listener;  
    JTextField readout;
    
    ActivePathFinderParameters apfParams;
    static String NONRANDOM = "Non-Random Starting Graph";
    static String ANNEAL = "Anneal";
    static String SEARCH = "Search";

    JPanel tStartPanel;
    JPanel tEndPanel;
    JTextField startNum;
    JTextField endNum;
    JLabel tempLabelStart;
    JLabel tempLabelEnd;

    JPanel intervalPanel;
    JTextField intervalNum;
    JLabel intervalLabel;

    JPanel pathPanel;
    JTextField pathNum;
    JLabel pathLabel;

    JPanel iterPanel;
    JTextField iterNum;
    JLabel iterLabel;

    JPanel annealExtPanel;
    JPanel generalExtPanel;
    JPanel hfcPanel;
    JCheckBox quenchCheck;
    JCheckBox edgesCheck;
    JCheckBox hubBox;
    JTextField hubNum;

    // hub adjustment
    JPanel hAdjPanel;
    JCheckBox hubAdjustmentBox;
    JTextField hubAdjustmentNum;

    // monte carlo: on/off
    JCheckBox mcBox;

    // regional scoring: on/off
    JCheckBox regionalBox;

    // greedy search rather than annealing
    JPanel searchPanel;
    JTextField searchDepth;
    JCheckBox searchFromNodesBox;
    JCheckBox maxBox;
    JTextField maxDepth;

    JRadioButton annealButton;
    JRadioButton searchButton;
    JPanel annealSearchControlPanel;
    JPanel annealSearchContentPanel;
    JPanel annealContentPanel;
    JPanel searchContentPanel;
    JPanel currentContentPanel;

//-----------------------------------------------------------------------------
public ActivePathsParametersPopupDialog 
                         (ActivePathsParametersPopupDialogListener listener, 
                          Frame parentFrame, String title, 
			  ActivePathFinderParameters incomingApfParams)
{
  super (parentFrame, true);
  setTitle (title);
  this.listener = listener;

  // uses copy constructor so that changes aren't committed if you dismiss.
  apfParams = new ActivePathFinderParameters(incomingApfParams);
  
  readout = new JTextField(new String("seed: "+apfParams.getRandomSeed()));
  RandomSeedTextListener readoutListener = new RandomSeedTextListener();
  readout.addFocusListener(readoutListener);

  JPanel mainPanel = new JPanel ();
  GridBagLayout gridbag = new GridBagLayout();
  GridBagConstraints c = new GridBagConstraints();
  mainPanel.setLayout (gridbag);

  createExtsController();
  createAnnealContentPanel();
  createSearchContentPanel();

  c.fill = GridBagConstraints.NONE;
  c.weightx = 1.0;
  c.ipadx = 10;
  c.ipady = 10;

  c.gridx=0;
  c.gridy=6;
  c.anchor=GridBagConstraints.NORTH;
  gridbag.setConstraints(generalExtPanel,c);
  mainPanel.add (generalExtPanel);

  createAnnealSearchController();
  c.gridx=1;
  c.gridy=6;
  c.gridwidth = GridBagConstraints.REMAINDER; //end row
  c.anchor=GridBagConstraints.CENTER;
  gridbag.setConstraints(annealSearchControlPanel,c);
  mainPanel.add (annealSearchControlPanel);
  c.gridx=0;
  c.gridy=10;
  c.gridwidth = GridBagConstraints.REMAINDER; //end row
  c.anchor=GridBagConstraints.CENTER;
  gridbag.setConstraints(annealSearchContentPanel,c);
  mainPanel.add (annealSearchContentPanel);
  
  
  ///////////////////////////////////////////
  JPanel buttonPanel = new JPanel ();
  JButton applyButton = new JButton ("Apply");
  JButton saveButton = new JButton ("Save Params");
  JButton dismissButton= new JButton ("Dismiss");

  applyButton.addActionListener (new ApplyAction ());
  saveButton.addActionListener (new SaveAction ());
  dismissButton.addActionListener (new DismissAction());
  
  buttonPanel.add (applyButton, BorderLayout.WEST);
  buttonPanel.add (saveButton, BorderLayout.CENTER);
  buttonPanel.add (dismissButton, BorderLayout.EAST);

  c.gridx=0;
  c.gridy=11;
  c.gridwidth = GridBagConstraints.REMAINDER; //end row
  c.anchor=GridBagConstraints.CENTER;
  gridbag.setConstraints(buttonPanel,c);
  mainPanel.add (buttonPanel);

  ///////////////////////////////////////////

  setContentPane (mainPanel);
} // PopupDialog ctor


private void createAnnealContentPanel() {
    annealContentPanel = new JPanel ();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    annealContentPanel.setLayout (gridbag);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 1.0;
    c.ipadx = 10;
    c.ipady = 10;
    ///////////////////////////////////////////

    c.gridx=0;
    c.gridy=0;
    createIterationsController();
    gridbag.setConstraints(iterPanel,c);
    annealContentPanel.add (iterPanel);
    
        c.gridx=1;
    c.gridy=0;
    createPathsController();
    gridbag.setConstraints(pathPanel,c);
    annealContentPanel.add (pathPanel);

    /////////////////////////////////////////////

    TempController tc = new TempController();

    c.gridx=0;
    c.gridy=2;
    gridbag.setConstraints(tStartPanel,c);
    annealContentPanel.add (tStartPanel);
    
    c.gridx=1;
    c.gridy=2;
    gridbag.setConstraints(tEndPanel,c);
    annealContentPanel.add (tEndPanel);

    ///////////////////////////////////////////

    c.gridx=0;
    c.gridy=4;
    createIntervalController();
    gridbag.setConstraints(intervalPanel,c);
    //annealContentPanel.add(intervalPanel);

    ///////////////////////////////////////////

    c.gridx=0;
    c.gridy=6;
    c.anchor=GridBagConstraints.NORTH;
    //createExtsController();
    gridbag.setConstraints(annealExtPanel,c);
    annealContentPanel.add (annealExtPanel);

    c.gridx=1;
    c.gridy=6;
    c.anchor=GridBagConstraints.NORTH;
    JPanel rsPanel = createRandomSeedController ();
    gridbag.setConstraints(rsPanel,c);
    annealContentPanel.add (rsPanel);

    Border border = BorderFactory.createLineBorder (Color.black);
    Border titledBorder = 
	BorderFactory.createTitledBorder (border, "Annealing Parameters", 
					  TitledBorder.CENTER, 
					  TitledBorder.DEFAULT_POSITION);
    annealContentPanel.setBorder (titledBorder);
}

private void createSearchContentPanel() {
    searchContentPanel = new JPanel ();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    searchContentPanel.setLayout (gridbag);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 1.0;
    c.ipadx = 10;
    c.ipady = 10;
    ///////////////////////////////////////////

    c.gridx=0;
    c.gridy=8;
    c.gridwidth = GridBagConstraints.REMAINDER; //end row
    c.anchor=GridBagConstraints.CENTER;
    createSearchController();
    gridbag.setConstraints(searchPanel,c);
    searchContentPanel.add (searchPanel);

    Border border = BorderFactory.createLineBorder (Color.black);
    Border titledBorder = 
	BorderFactory.createTitledBorder (border, "Searching Parameters", 
					  TitledBorder.CENTER, 
					  TitledBorder.DEFAULT_POSITION);
    searchContentPanel.setBorder (titledBorder);
}

//-----------------------------------------------------------------------------
private void createExtsController ()
{
  annealExtPanel = new JPanel ();
  annealExtPanel.setLayout (new GridLayout (0,1));

  generalExtPanel = new JPanel ();
  generalExtPanel.setLayout (new GridLayout(0,1));

  createHubfindingController ();
  createHubAdjustmentController ();
  createMontecarloController();

  quenchCheck = new JCheckBox("Quenching",apfParams.getToQuench());
  QuenchCheckListener qcListener = new QuenchCheckListener();
  quenchCheck.addItemListener(qcListener);

  edgesCheck = new JCheckBox("Edges (not Nodes)",apfParams.getEdgesNotNodes());
  //temporarily disabled while option not available
  edgesCheck.setEnabled(false);
  EdgesCheckListener ecListener = new EdgesCheckListener();
  edgesCheck.addItemListener(ecListener);

  Border annealBorder = BorderFactory.createLineBorder (Color.black);
  Border annealTitledBorder = 
      BorderFactory.createTitledBorder (annealBorder, "Annealing Extensions", 
					TitledBorder.CENTER, 
					TitledBorder.DEFAULT_POSITION);
  annealExtPanel.setBorder (annealTitledBorder);

  Border generalBorder = BorderFactory.createLineBorder (Color.black);
  Border generalTitledBorder = 
      BorderFactory.createTitledBorder (generalBorder, "General Parameters", 
					TitledBorder.CENTER, 
					TitledBorder.DEFAULT_POSITION);
  generalExtPanel.setBorder (generalTitledBorder);
    
  annealExtPanel.add (quenchCheck); //, BorderLayout.CENTER);
  //annealExtPanel.add (edgesCheck); //, BorderLayout.CENTER);
  annealExtPanel.add (hfcPanel); //, BorderLayout.CENTER);

  createRegionalScoringController();
  //createPathsController();
  //generalExtPanel.add (pathPanel);
  generalExtPanel.add(mcBox);
  //generalExtPanel.add (hAdjPanel);
  generalExtPanel.add (regionalBox);

  return;
 
}
 
private void createAnnealSearchController() {
    annealSearchControlPanel = new JPanel();
    annealSearchContentPanel = new JPanel();
    annealButton = new JRadioButton(ANNEAL);
    searchButton = new JRadioButton(SEARCH);
    //temporarily disabled while option unavailable
    //searchButton.setEnabled(false);
    ButtonGroup annealSearchGroup = new ButtonGroup ();
    annealSearchGroup.add(annealButton);
    annealSearchGroup.add(searchButton);

    Border ascBorder = BorderFactory.createLineBorder (Color.black);
    Border ascTitledBorder = 
	BorderFactory.createTitledBorder (ascBorder, "Strategy", 
					  TitledBorder.CENTER, 
					  TitledBorder.DEFAULT_POSITION);
    annealSearchControlPanel.setBorder (ascTitledBorder);

    annealSearchControlPanel.setLayout (new GridLayout(2,0));
    annealSearchControlPanel.add(annealButton);
    annealSearchControlPanel.add(searchButton);
    AnnealSearchSwitchListener switchListener =
	new AnnealSearchSwitchListener();
    annealButton.addActionListener(switchListener);
    searchButton.addActionListener(switchListener);

    boolean sInit  = (apfParams.getSearchDepth()==0)?false:true;
    if(sInit) {
	currentContentPanel = searchContentPanel;
	searchButton.setSelected (true);
    }
    else {
	currentContentPanel = annealContentPanel;
	annealButton.setSelected (true);
    }
    annealSearchContentPanel.add(currentContentPanel);
}

//-----------------------------------------------------------------------------
private JPanel createRandomSeedController ()
{
  JPanel panel = new JPanel ();
  panel.setLayout (new GridLayout (0,1));
  JRadioButton smallPrimeNumberSeedButton = new JRadioButton (NONRANDOM);
  smallPrimeNumberSeedButton.setSelected (true);
  JRadioButton dateBasedSeedButton = new JRadioButton ("Random Based on Current Time");
 
  ButtonGroup buttonGroup = new ButtonGroup ();
  buttonGroup.add (smallPrimeNumberSeedButton);
  buttonGroup.add (dateBasedSeedButton);

  panel.add (smallPrimeNumberSeedButton);
  panel.add (dateBasedSeedButton);

  RandomSeedListener listener = new RandomSeedListener ();
  smallPrimeNumberSeedButton.addActionListener (listener);
  dateBasedSeedButton.addActionListener (listener);

  Border border = BorderFactory.createLineBorder (Color.black);
  Border titledBorder = 
     BorderFactory.createTitledBorder (border, "Seed Graph Options", 
                                       TitledBorder.CENTER, 
                                       TitledBorder.DEFAULT_POSITION);
  panel.setBorder (titledBorder);
    
  panel.add (smallPrimeNumberSeedButton); //, BorderLayout.CENTER);
  panel.add (dateBasedSeedButton); //, BorderLayout.CENTER);
  panel.add(readout);
  return  panel;

} // createRandomSeedController 

//-----------------------------------------------------------------------------
private void createHubfindingController ()
{
  hfcPanel = new JPanel ();
  hfcPanel.setLayout (new GridLayout (1,2));
  HFListener listener = new HFListener ();
  boolean hfInit  = (apfParams.getMinHubSize()==0)?false:true;
  hubBox = new JCheckBox ("Hubfinding", hfInit);
  //temp change while option not available
  //hubBox.setEnabled(false);
  hubNum = new JTextField (Integer.toString(apfParams.getMinHubSize()));

  if(!hfInit) {
      hubNum.setText("10");
      hubNum.setEnabled(false);
      hubBox.setSelected(false);
      apfParams.setMinHubSize(0);
  }
  hubBox.addItemListener (listener);
  hubNum.addFocusListener (listener);

  hfcPanel.add(hubBox);
  hfcPanel.add(hubNum);

  return;

} // createHubfindingController

private void createHubAdjustmentController ()
{
  hAdjPanel = new JPanel ();
  hAdjPanel.setLayout (new GridLayout (1,2));
  HAListener listener = new HAListener ();
  boolean haInit  = (apfParams.getHubAdjustment()==0)?false:true;
  hubAdjustmentBox = new JCheckBox ("Hub Penalty", haInit);
  //temporarily disabled while option not available
  hubAdjustmentBox.setEnabled(false);
  hubAdjustmentNum = new JTextField (Double.toString(apfParams.getHubAdjustment()));

  if(!haInit) {
      hubAdjustmentNum.setText("0.406");
      hubAdjustmentNum.setEnabled(false);
      hubAdjustmentBox.setSelected(false);
      apfParams.setHubAdjustment(0);
  }
  hubAdjustmentBox.addItemListener (listener);
  hubAdjustmentNum.addFocusListener (listener);

  hAdjPanel.add(hubAdjustmentBox);
  hAdjPanel.add(hubAdjustmentNum);

  return;

} // createHubAdjustmentController

private void createSearchController ()
{
  searchPanel = new JPanel ();
  searchPanel.setLayout (new GridLayout (3,2));
  AnnealSearchSwitchListener listener =
      new AnnealSearchSwitchListener ();
  boolean sInit  = (apfParams.getSearchDepth()==0)?false:true;

  if(sInit)
      searchDepth = new JTextField (Integer.toString(apfParams.getSearchDepth()));
  else
      searchDepth = new JTextField (Integer.toString(1));
  searchDepth.setEnabled(sInit);
  searchFromNodesBox = new JCheckBox ("Search from selected nodes?",
				      apfParams.getSearchFromNodes());
  boolean mInit  = (apfParams.getMaxDepth()==0)?false:true;
  maxBox = new JCheckBox("Max depth from start nodes:",mInit);
  if(mInit)
      maxDepth = new JTextField (Integer.toString(apfParams.getMaxDepth()));
  else
      maxDepth = new JTextField (Integer.toString(1));
  MListener mListener = new MListener ();
  maxDepth.setEnabled(mInit);
  maxBox.addItemListener (mListener);
  maxDepth.addFocusListener (mListener);

  searchDepth.addFocusListener (listener);
  searchFromNodesBox.addItemListener (new SFNListener());

  searchPanel.add(new JLabel("Search depth: "));
  searchPanel.add(searchDepth);
  searchPanel.add(searchFromNodesBox);
  searchPanel.add(new JLabel(""));
  searchPanel.add(maxBox);
  searchPanel.add(maxDepth);

  return;

} // createSearchController

//-----------------------------------------------------------------------------
private void createIterationsController ()
{
  iterPanel = new JPanel ();
  iterPanel.setLayout (new GridLayout (0,1));
  IterListener listener = new IterListener ();
  iterLabel = new JLabel("Iterations (0-10^8)");
  iterNum = new JTextField (Integer.toString(apfParams.getTotalIterations()));

  iterNum.addFocusListener (listener);

  iterPanel.add(iterLabel);
  iterPanel.add(iterNum);

  return;

} // createIterationsController

//-----------------------------------------------------------------------------
private void createPathsController ()
{
  pathPanel = new JPanel ();
  pathPanel.setLayout (new GridLayout (0,1));
  PathListener listener = new PathListener ();
  pathLabel = new JLabel("Number of Paths (1-1000)");
  pathNum = new JTextField (Integer.toString(apfParams.getNumberOfPaths()));

  pathNum.addFocusListener (listener);

  pathPanel.add(pathLabel);
  pathPanel.add(pathNum);

} // createPathsController

//-----------------------------------------------------------------------------

class TempController {
    
    public TempController() {

	tStartPanel = new JPanel();
	tEndPanel = new JPanel();
	startNum = new JTextField (Double.toString(apfParams.getInitialTemperature()));
	endNum = new JTextField (Double.toString(apfParams.getFinalTemperature()));

	TempListener listener = new TempListener ();
	tempLabelStart = new JLabel("Start Temp (0.0001 - 100)");
	tempLabelEnd = new JLabel("End Temp (0.0001 - Start)");
	
	tStartPanel.setLayout (new GridLayout (0,1));
	tStartPanel.add(tempLabelStart);
	tStartPanel.add(startNum);
	
	tEndPanel.setLayout (new GridLayout (0,1));
	tEndPanel.add(tempLabelEnd);
	tEndPanel.add(endNum);
	
	startNum.addFocusListener (listener);
	endNum.addFocusListener (listener);
    }

} // TempController

//-----------------------------------------------------------------------------
private void createIntervalController ()
{

  intervalNum = new JTextField (Integer.toString(apfParams.getDisplayInterval()));
  intervalNum.setEnabled(false);
  intervalLabel = new JLabel("Display Interval (1-1e05)");
  intervalPanel = new JPanel();

  intervalPanel.setLayout (new GridLayout (0,1));
  IntervalListener listener = new IntervalListener ();

  intervalNum.addFocusListener (listener);

  intervalPanel.add(intervalLabel);
  intervalPanel.add(intervalNum);

} // createIntervalController

private void createMontecarloController() {
    boolean mcInit  = apfParams.getMCboolean();
    mcBox = new JCheckBox ("Montecarlo adj?", mcInit);
    mcBox.addItemListener (new MCListener());
} // createMontecarloController

private void createRegionalScoringController() {
    boolean regionalInit  = apfParams.getRegionalBoolean();
    regionalBox = new JCheckBox ("Regional Scoring?", regionalInit);
    //temporarily disabled while option not available
    //regionalBox.setEnabled(false);
    regionalBox.addItemListener (new RSListener());
} // createRegionalScoringController

//-----------------------------------------------------------------------------
class RandomSeedListener implements ActionListener { 
  public void actionPerformed (ActionEvent e) {
    String setting = e.getActionCommand ();
    if (setting.equals (NONRANDOM))
      apfParams.setRandomSeed(17);
    else
      apfParams.setRandomSeed( Math.abs ((int) System.currentTimeMillis ()) );
    readout.setText ("seed: " + apfParams.getRandomSeed());
    }

} // RandomSeedListener

//-----------------------------------------------------------------------------
class AnnealSearchSwitchListener implements ActionListener, FocusListener { 
    public void focusGained (FocusEvent e) {
	validate();
    }
    public void focusLost (FocusEvent e) {
	validate();
    }
    public void actionPerformed (ActionEvent e) {
	searchDepth.setEnabled(searchButton.isSelected());
	String setting = e.getActionCommand ();
	if (setting.equals (ANNEAL)) {
	    validate();
	    switchToAnneal();
	}
	else if(setting.equals (SEARCH)) {
	    validate();
	    switchToSearch();
	}
    }
    private void switchToAnneal() {
	if (currentContentPanel!=annealContentPanel) {
	    annealSearchContentPanel.remove(currentContentPanel);
	    currentContentPanel = annealContentPanel;
	    annealSearchContentPanel.add(currentContentPanel);
	    ActivePathsParametersPopupDialog.this.pack();
	    ActivePathsParametersPopupDialog.this.validate();
	}
    }
    private void switchToSearch() {
	if (currentContentPanel!=searchContentPanel) {
	    annealSearchContentPanel.remove(currentContentPanel);
	    currentContentPanel = searchContentPanel;
	    annealSearchContentPanel.add(currentContentPanel);
	    ActivePathsParametersPopupDialog.this.pack();
	    ActivePathsParametersPopupDialog.this.validate();
	}
    }
    private void validate() {
	if(searchButton.isSelected()) {
	    String st = searchDepth.getText();
	    String st2 = st.replaceAll("[^0-9]",""); // ditch all non-numeric
	    if(st.length()>0) {
		try {
		    int si = Integer.parseInt(st2);
		    if(si<=0) {
			searchDepth.setText("1");
			searchDepth.setEnabled(false);
			//searchButton.setSelected(false);
			apfParams.setSearchDepth(0);
			annealButton.setSelected(true);
			switchToAnneal();
		    }
		    else if(si>10) {
			searchDepth.setText("10");
			apfParams.setSearchDepth(10);
		    }
		    else {
			searchDepth.setText(st2);
			apfParams.setSearchDepth(si);
		    }
		}
		catch (NumberFormatException nfe) {
		    System.out.println("Not an int: " + st2);
		    searchDepth.setText("1");
		    apfParams.setSearchDepth(1);
		}
	    }
	    else {
		searchDepth.setText("1");
		apfParams.setSearchDepth(1);
	    }
	}
	else
	    apfParams.setSearchDepth(0);
    }

} // AnnealSearchSwitchListener

//-----------------------------------------------------------------------------
class RandomSeedTextListener implements FocusListener {
  public void focusGained (FocusEvent e) {
      //System.out.println("gained");
      validate();
  }
  public void focusLost (FocusEvent e) {
      //System.out.println("lost");
      validate();
  }

  private void validate() {
      String rt = readout.getText();
      String rt2 = rt.replaceAll("[^0-9]",""); // ditch all non-numeric
      if(rt2.length()>0) {
	  //System.out.println(" length " + rt2.length());
	  try {
	      int seed = Integer.parseInt(rt2);
	      apfParams.setRandomSeed(seed);
	      readout.setText ("seed: " + apfParams.getRandomSeed());
	  }
	  catch (NumberFormatException nfe) {
	      System.out.println("Not an integer: " + rt2);
	      apfParams.setRandomSeed(0);
	      readout.setText ("seed: " + apfParams.getRandomSeed());
	  }
      }  // if gt 0
      else {
	  apfParams.setRandomSeed(0);
	  readout.setText ("seed: " + apfParams.getRandomSeed());
      }  // if gt 0 (else)
  } // validate()
} // RandomSeedTextListener

//-----------------------------------------------------------------------------
class MCListener implements ItemListener {
  public void itemStateChanged (ItemEvent e) {
      apfParams.setMCboolean(mcBox.isSelected());
  }
}
class RSListener implements ItemListener {
  public void itemStateChanged (ItemEvent e) {
      apfParams.setRegionalBoolean(regionalBox.isSelected());
  }
}
class SFNListener implements ItemListener {
  public void itemStateChanged (ItemEvent e) {
      apfParams.setSearchFromNodes(searchFromNodesBox.isSelected());
  }
}
class QuenchCheckListener implements ItemListener {
  public void itemStateChanged (ItemEvent e) {
      JCheckBox jcb = (JCheckBox)e.getItem();
      apfParams.setToQuench(jcb.isSelected());
  }
}
class EdgesCheckListener implements ItemListener {
  public void itemStateChanged (ItemEvent e) {
      JCheckBox jcb = (JCheckBox)e.getItem();
      apfParams.setEdgesNotNodes(jcb.isSelected());
  }
}

//-----------------------------------------------------------------------------
class HFListener implements ItemListener, FocusListener { 
  public void focusGained (FocusEvent e) {
      //System.out.println("gained");
      validate();
  }
  public void focusLost (FocusEvent e) {
      //System.out.println("lost");
      validate();
  }
  public void itemStateChanged (ItemEvent e) {
    hubNum.setEnabled(hubBox.isSelected());
    validate();
  }

  private void validate() {
    if(hubBox.isSelected()) {
	String ht = hubNum.getText();
	String ht2 = ht.replaceAll("[^0-9]",""); // ditch all non-numeric
	if(ht.length()>0) {
	    //System.out.println(" length " + ht.length());
	    try {
		int hf = Integer.parseInt(ht2);
		if(hf<=0) {
		    hubNum.setText("10");
		    hubNum.setEnabled(false);
		    hubBox.setSelected(false);
		    apfParams.setMinHubSize(0);
		}
		else if(hf>10000) {
		    hubNum.setText("10000");
		    apfParams.setMinHubSize(10000);
		}
		else {
		    hubNum.setText(ht2);
		    apfParams.setMinHubSize(hf);
		}
		
	    }
	    catch (NumberFormatException nfe) {
		System.out.println("Not an integer: " + ht2);
		hubNum.setText("10");
		apfParams.setMinHubSize(10);
		//JOptionPane.showMessageDialog (mainPanel, "Not an integer: " + ht2);
	    }
	}
	else {
	    //System.out.println(" length " + ht.length());
	    //System.out.println(" going for 10.");
	    hubNum.setText("10");
	    apfParams.setMinHubSize(10);
	}
    }
    else
	apfParams.setMinHubSize(0);
  }

} // HFListener



class HAListener implements ItemListener, FocusListener { 
  public void focusGained (FocusEvent e) {
      //System.out.println("gained");
      validate();
  }
  public void focusLost (FocusEvent e) {
      //System.out.println("lost");
      validate();
  }
  public void itemStateChanged (ItemEvent e) {
    hubAdjustmentNum.setEnabled(hubAdjustmentBox.isSelected());
    validate();
  }

  private void validate() {
    if(hubAdjustmentBox.isSelected()) {
	String ht = hubAdjustmentNum.getText();
	String ht2 = ht.replaceAll("[^0-9.]",""); // ditch all non-numeric
	if(ht.length()>0) {
	    //System.out.println(" length " + ht.length());
	    try {
		double hf = Double.parseDouble(ht2);
		if(hf<=0) {
		    hubAdjustmentNum.setText("0.406");
		    hubAdjustmentNum.setEnabled(false);
		    hubAdjustmentBox.setSelected(false);
		    apfParams.setHubAdjustment(0);
		}
		else if(hf>100) {
		    hubAdjustmentNum.setText("100");
		    apfParams.setHubAdjustment(100);
		}
		else {
		    hubAdjustmentNum.setText(ht2);
		    apfParams.setHubAdjustment(hf);
		}
		
	    }
	    catch (NumberFormatException nfe) {
		System.out.println("Not a double: " + ht2);
		hubAdjustmentNum.setText("0.406");
		apfParams.setHubAdjustment(0.406);
	    }
	}
	else {
	    hubAdjustmentNum.setText("0.406");
	    apfParams.setHubAdjustment(0.406);
	}
    }
    else
	apfParams.setHubAdjustment(0);
  }

} // HAListener


class MListener implements ItemListener, FocusListener { 
    public void focusGained (FocusEvent e) {
	//System.out.println("gained");
	validate();
    }
    public void focusLost (FocusEvent e) {
	//System.out.println("lost");
	validate();
    }
    public void itemStateChanged (ItemEvent e) {
	maxDepth.setEnabled(maxBox.isSelected());
	validate();
    }
    private void validate() {
	if(maxBox.isSelected()) {
	    String st = maxDepth.getText();
	    String st2 = st.replaceAll("[^0-9]",""); // ditch all non-numeric
	    if(st.length()>0) {
		try {
		    int si = Integer.parseInt(st2);
		    if(si<=0) {
			maxDepth.setText("1");
			maxDepth.setEnabled(false);
			maxBox.setSelected(false);
			apfParams.setMaxDepth(0);
		    }
		    else if(si>10) {
			maxDepth.setText("10");
			apfParams.setMaxDepth(10);
		    }
		    else {
			maxDepth.setText(st2);
			apfParams.setMaxDepth(si);
		    }
		}
		catch (NumberFormatException nfe) {
		    System.out.println("Not an int: " + st2);
		    maxDepth.setText("1");
		    apfParams.setMaxDepth(1);
		}
	    }
	    else {
		maxDepth.setText("1");
		apfParams.setMaxDepth(1);
	    }
	}
	else
	    apfParams.setMaxDepth(0);
    }
} // MListener


//-----------------------------------------------------------------------------
class IterListener implements FocusListener { 
  public void focusGained (FocusEvent e) {
      //System.out.println("gained");
      validate();
  }
  public void focusLost (FocusEvent e) {
      //System.out.println("lost");
      validate();
  }

  private void validate() {
      String it = iterNum.getText();
      String it2 = it.replaceAll("[^0-9]",""); // ditch all non-numeric
      if(it2.length()>0) {
	  if(it2.length()>=9) {
	      iterNum.setText("100000000");
	      apfParams.setTotalIterations(100000000);
	  }
	  else {
	      //System.out.println(" length " + it.length());
	      try {
		  int iters = Integer.parseInt(it2);
		  if(iters<=0) {
		      iterNum.setText("0");
		      apfParams.setTotalIterations(0);
		  }
		  else if(iters>100000000) {
		      iterNum.setText("100000000");
		      apfParams.setTotalIterations(100000000);
		  }
		  else {
		      iterNum.setText(it2);
		      apfParams.setTotalIterations(iters);
		  }
	      }
	      catch (NumberFormatException nfe) {
		  System.out.println("Not an integer: " + it2);
		  iterNum.setText("0");
		  apfParams.setTotalIterations(0);
	      }
	  }  // if gt 9 (else)
      }  // if gt 0
      else {
	  iterNum.setText("0");
	  apfParams.setTotalIterations(0);
      }  // if gt 0 (else)
  }

} // IterListener

//-----------------------------------------------------------------------------
class PathListener implements FocusListener { 
  public void focusGained (FocusEvent e) {
      //System.out.println("gained");
      validate();
  }
  public void focusLost (FocusEvent e) {
      //System.out.println("lost");
      validate();
  }

  private void validate() {
      String pt = pathNum.getText();
      String pt2 = pt.replaceAll("[^0-9]",""); // ditch all non-numeric
      if(pt2.length()>0) {
	  if(pt2.length()>3) {
	      pathNum.setText("1000");
	      apfParams.setNumberOfPaths(1000);
	  }
	  else {
	      //System.out.println(" length " + pt.length());
	      try {
		  int paths = Integer.parseInt(pt2);
		  if(paths<=0) {
		      pathNum.setText("0");
		      apfParams.setNumberOfPaths(0);
		  }
		  else if(paths>1000) {
		      pathNum.setText("1000");
		      apfParams.setNumberOfPaths(1000);
		  }
		  else {
		      pathNum.setText(pt2);
		      apfParams.setNumberOfPaths(paths);
		  }
	      }
	      catch (NumberFormatException nfe) {
		  System.out.println("Not an integer: " + pt2);
		  pathNum.setText("0");
		  apfParams.setNumberOfPaths(0);
	      }
	  }  // if gt 3 (else)
      }  // if gt 0
      else {
	  pathNum.setText("0");
	  apfParams.setNumberOfPaths(0);
      }  // if gt 0 (else)
  }

} // PathListener


//-----------------------------------------------------------------------------
class TempListener implements FocusListener { 
  public void focusGained (FocusEvent e) {
      //System.out.println("gained");
      validate();
  }
  public void focusLost (FocusEvent e) {
      //System.out.println("lost");
      validate();
  }

  private void validate() {
      String st = startNum.getText();
      String st2 = st.replaceAll("[^0-9.]",""); // ditch all non-numeric
      String et = endNum.getText();
      String et2 = et.replaceAll("[^0-9.]",""); // ditch all non-numeric

      ////////////////////////////////////////////////
      // first handle start temp
      if(st2.length()>0) {
	  //System.out.println(" length " + st2.length());
	  try {
	      double startTemp = Double.parseDouble(st2);
	      if(startTemp<=0) {
		  startNum.setText("0.0001");
		  apfParams.setInitialTemperature(0.0001);
	      }
	      else if(startTemp>100.0) {
		  startNum.setText("100.0");
		  apfParams.setInitialTemperature(100.0);
	      }
	      else {
		  startNum.setText(st2);
		  apfParams.setInitialTemperature(startTemp);
	      }
	  }
	  catch (NumberFormatException nfe) {
	      System.out.println("Not a number: " + st2);
	      startNum.setText("1.000");
	      apfParams.setInitialTemperature(1.000);
	  }
      }  // if gt 0
      else {
	  startNum.setText("1.000");
	  apfParams.setInitialTemperature(1.000);
      }  // if gt 0 (else)

      ////////////////////////////////////////////////
      // then handle end temp
      if(et2.length()>0) {
	  //System.out.println(" length " + et2.length());
	  try {
	      double endTemp = Double.parseDouble(et2);
	      if(endTemp<=0) {
		  endNum.setText("0.0001");
		  apfParams.setFinalTemperature(0.0001);
	      }
	      else if(endTemp>apfParams.getInitialTemperature()) {
		  endNum.setText(startNum.getText());
		  apfParams.setFinalTemperature(apfParams.getInitialTemperature());
	      }
	      else {
		  endNum.setText(et2);
		  apfParams.setFinalTemperature(endTemp);
	      }
	  }
	  catch (NumberFormatException nfe) {
	      System.out.println("Not a number: " + et2);
	      endNum.setText("0.0001");
	      apfParams.setFinalTemperature(0.0001);
	  }
      }  // if gt 0
      else {
	  endNum.setText("0.0001");
	  apfParams.setFinalTemperature(0.0001);
      }  // if gt 0 (else)

  } // validate

} // TempListener


//-------------------------------------------------------------
class IntervalListener implements FocusListener { 
  public void focusGained (FocusEvent e) {
      //System.out.println("gained");
      validate();
  }
  public void focusLost (FocusEvent e) {
      //System.out.println("lost");
      validate();
  }

  private void validate() {
      String it = intervalNum.getText();
      String it2 = it.replaceAll("[^0-9]",""); // ditch all non-numeric
      if(it2.length()>0) {
	  if(it2.length()>=6) {
	      intervalNum.setText("100000");
	      apfParams.setDisplayInterval(100000);
	  }
	  else {
	      //System.out.println(" length " + it2.length());
	      try {
		  int intervals = Integer.parseInt(it2);
		  if(intervals<=0) {
		      intervalNum.setText("0");
		      apfParams.setDisplayInterval(0);
		  }
		  else if(intervals>100000) {
		      intervalNum.setText("100000");
		      apfParams.setDisplayInterval(100000);
		  }
		  else {
		      intervalNum.setText(it2);
		      apfParams.setDisplayInterval(intervals);
		  }
	      }
	      catch (NumberFormatException nfe) {
		  System.out.println("Not an integer: " + it2);
		  intervalNum.setText("0");
		  apfParams.setDisplayInterval(0);
	      }
	  }  // if gte 6 (else)
      }  // if gt 0
      else {
	  intervalNum.setText("0");
	  apfParams.setDisplayInterval(0);
      }  // if gt 0 (else)
  }

} // IntervalListener


//-----------------------------------------------------------------------------
public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {
      listener.setActivePathsParameters (apfParams);
      ActivePathsParametersPopupDialog.this.dispose ();
  }

} // QuitAction
//-----------------------------------------------------------------------------
public class SaveAction extends AbstractAction {
  SaveAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {
      listener.setActivePathsParameters (apfParams);
      Object[] options = { "OK"};
      JOptionPane.showOptionDialog(null,
				   "Parameters Successfully Saved.",
				   "Data Saved.",
				   JOptionPane.DEFAULT_OPTION,
				   JOptionPane.PLAIN_MESSAGE,
				   null, options, options[0]);
  }

} // QuitAction
//-----------------------------------------------------------------------------
public class DismissAction extends AbstractAction 
{

  DismissAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    listener.cancelActivePathsFinding ();
    ActivePathsParametersPopupDialog.this.dispose ();
  }

} // DismissAction
//-----------------------------------------------------------------------------
} // class ActivePathsParametersPopupDialog
