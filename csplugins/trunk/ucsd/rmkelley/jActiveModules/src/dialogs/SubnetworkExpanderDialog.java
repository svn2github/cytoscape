// SubnetworkExpanderPanel.java
//---------------------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
//---------------------------------------------------------------------------------------
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
import java.io.*;

//--------------------------------------------------------------------------------------
public class SubnetworkExpanderDialog extends JDialog {

    public File NODES_FILE;
    public int MAX_SEARCH_DEPTH;
    public boolean IS_DIRECTED_SEARCH;
    public boolean UPSTREAM_SEARCH;

    private JTextField nodesFileField;
    private JTextField maxSearchDepthField;
    private JCheckBox  directedSearchCheckBox;
    private JRadioButton upstreamButton;
    private JRadioButton downstreamButton;
    
    private boolean isApplyAction;
    private File currentDirectory;

    //--------------------------------------------------------------------------------------

    public SubnetworkExpanderDialog (Frame parentFrame, String title) {
	super (parentFrame, true);
	setTitle (title);
 
	// setup dialog layout
	JPanel mainPanel = new JPanel ();
	SpringLayout springLayout = new SpringLayout();
	Spring defaultSpring = Spring.constant(10);
	mainPanel.setLayout(springLayout);

	// setup main fields
	JLabel nodesFileLabel = new JLabel("File with list of nodes:", JLabel.LEFT);
	SpringLayout.Constraints cons = springLayout.getConstraints(nodesFileLabel);
	cons.setX(defaultSpring);
	cons.setY(defaultSpring);
	Spring xSpring = Spring.sum(defaultSpring, cons.getConstraint("East"));
	mainPanel.add(nodesFileLabel);

	nodesFileField = new JTextField();
	cons = springLayout.getConstraints(nodesFileField);
	cons.setX(xSpring);
	cons.setY(defaultSpring);
	cons.setWidth(Spring.constant(200));
	xSpring = Spring.sum(defaultSpring, cons.getConstraint("East"));
	mainPanel.add(nodesFileField);
	
	JButton fileButton = new JButton ("Browse...");
	fileButton.addActionListener (new BrowseAction ());
	cons = springLayout.getConstraints(fileButton);
	cons.setX(xSpring);
	cons.setY(defaultSpring);
	Spring maxX = Spring.sum(defaultSpring, cons.getConstraint("East"));
	Spring ySpring = Spring.sum(defaultSpring, cons.getConstraint("South"));
	mainPanel.add(fileButton);

	JLabel maxSearchDepthLabel = new JLabel("Max depth to search:", JLabel.LEFT);
	cons = springLayout.getConstraints(maxSearchDepthLabel);
	cons.setX(defaultSpring);
	cons.setY(ySpring);
	xSpring = Spring.sum(defaultSpring, cons.getConstraint("East"));
	mainPanel.add(maxSearchDepthLabel);

	maxSearchDepthField = new JTextField();
	cons = springLayout.getConstraints(maxSearchDepthField);
	cons.setX(xSpring);
	cons.setY(ySpring);
	cons.setWidth(Spring.constant(50));
	ySpring = Spring.sum(defaultSpring, cons.getConstraint("South"));
	mainPanel.add(maxSearchDepthField);

	directedSearchCheckBox = new JCheckBox("Use directed search");
	directedSearchCheckBox.addActionListener(new CheckBoxAction ());
	cons = springLayout.getConstraints(directedSearchCheckBox);
	cons.setX(defaultSpring);
	cons.setY(ySpring);
	xSpring = Spring.sum(defaultSpring, cons.getConstraint("East"));
	mainPanel.add(directedSearchCheckBox);

	upstreamButton = new JRadioButton("Upstream");
	downstreamButton = new JRadioButton("Downstream");
	ButtonGroup buttonGroup = new ButtonGroup();
	buttonGroup.add(upstreamButton);
	buttonGroup.add(downstreamButton);
	JPanel radioPanel = new JPanel();
	radioPanel.add(upstreamButton);
	radioPanel.add(downstreamButton);
	upstreamButton.setEnabled(false);
	downstreamButton.setEnabled(false);
	cons = springLayout.getConstraints(radioPanel);
	cons.setX(xSpring);
	cons.setY(ySpring);
	ySpring = Spring.sum(defaultSpring, cons.getConstraint("South"));
	mainPanel.add(radioPanel);

	// apply and dismiss buttons
	JButton applyButton = new JButton ("Apply");
	JButton dismissButton= new JButton ("Dismiss");
	applyButton.addActionListener   (new ApplyAction ());
	dismissButton.addActionListener (new DismissAction());
	cons = springLayout.getConstraints(applyButton);
	cons.setX(defaultSpring);
	cons.setY(ySpring);
	xSpring = Spring.sum(defaultSpring, cons.getConstraint("East"));
	cons = springLayout.getConstraints(dismissButton);
	cons.setX(xSpring);
	cons.setY(ySpring);
	xSpring = Spring.sum(defaultSpring, cons.getConstraint("East"));
	ySpring = Spring.sum(defaultSpring, cons.getConstraint("South"));
	mainPanel.add (applyButton);
	mainPanel.add (dismissButton);

	SpringLayout.Constraints pCons = springLayout.getConstraints(mainPanel);
	pCons.setConstraint("East", maxX);
	pCons.setConstraint("South", ySpring);
	setContentPane (mainPanel);
   }
    
    //--------------------------------------------------------------------------------------
    public class ApplyAction extends AbstractAction {
	ApplyAction () { super (""); }
	
	public void actionPerformed (ActionEvent e) {
	    MAX_SEARCH_DEPTH = Integer.parseInt(maxSearchDepthField.getText());
	    IS_DIRECTED_SEARCH = directedSearchCheckBox.isSelected();
	    UPSTREAM_SEARCH = upstreamButton.isSelected();
	    NODES_FILE = new File (nodesFileField.getText());
	    SubnetworkExpanderDialog.this.isApplyAction = true;
	    SubnetworkExpanderDialog.this.dispose ();
	}
    } // ApplyAction

    //--------------------------------------------------------------------------------------
    public boolean isApplyAction () { return isApplyAction; }

    //--------------------------------------------------------------------------------------
    public class DismissAction extends AbstractAction {
	DismissAction () {super ("");}
	
	public void actionPerformed (ActionEvent e) {
	    SubnetworkExpanderDialog.this.isApplyAction = false;
	    SubnetworkExpanderDialog.this.dispose ();
	}
    } // DismissAction

    //--------------------------------------------------------------------------------------
    public class BrowseAction extends AbstractAction {
	BrowseAction () {super ("");}
	
	public void actionPerformed (ActionEvent e) {
	    JFileChooser fChooser = new JFileChooser(currentDirectory);	
	    fChooser.setDialogTitle("Load Node List");
	    if (fChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		currentDirectory = fChooser.getCurrentDirectory();
		NODES_FILE = fChooser.getSelectedFile();
		nodesFileField.setText(NODES_FILE.getAbsolutePath());
	    }
	} 
    } // BrowseAction

    //--------------------------------------------------------------------------------------
    public class CheckBoxAction extends AbstractAction {
	CheckBoxAction () {super ("");}
	
	public void actionPerformed (ActionEvent e) {
	    if (directedSearchCheckBox.isSelected()) {
		upstreamButton.setEnabled(true);
		downstreamButton.setEnabled(true);
	    }
	    else {
		upstreamButton.setEnabled(false);
		downstreamButton.setEnabled(false);
	    }
	}
    } // CheckBoxAction

}

