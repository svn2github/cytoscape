// ConditionsVsPathwaysTable
//------------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.event.*;
import javax.swing.border.Border;

import java.io.*;
import java.util.*;

import csplugins.jActiveModules.Component;
import csplugins.jActiveModules.ActivePathViewer;
import cytoscape.view.*;
import cytoscape.data.readers.TextJarReader;
//---------------------------------------------------------------------------------------
public class ConditionsVsPathwaysTable extends JDialog {

    ConditionsVsPathwaysTable self;
    JPanel topTable, bottomTable;
    csplugins.jActiveModules.Component [] activePaths;
    String [] conditionNames;
    CyWindow cytoscapeWindow;

//-----------------------------------------------------------------------------------------
public ConditionsVsPathwaysTable (Frame parentFrame, CyWindow cw,
				  String [] conditionNames,
                                  Component [] activePaths, ActivePathViewer pathViewer)
{
  super (parentFrame, false);  
  this.cytoscapeWindow = cw;
  this.activePaths = activePaths;
  this.conditionNames = conditionNames;
  init (parentFrame, pathViewer);

} // ConditionsVsPathwaysTable ctor

private void init (Frame parentFrame, ActivePathViewer pathViewer) {

  setTitle ("Conditions vs. Pathways");
  self = this;
  
  JPanel mainPanel = new JPanel ();
  mainPanel.setLayout (new BorderLayout ());
  JPanel tablePanel = new JPanel ();
  tablePanel.setLayout (new BoxLayout (tablePanel, BoxLayout.Y_AXIS));

  topTable = new ConditionsVsPathwaysTopTable (activePaths);
  tablePanel.add (topTable);
  bottomTable = new ConditionsVsPathwaysBottomTable (conditionNames, activePaths, pathViewer);
  tablePanel.add (bottomTable);
  JScrollPane scrollPane = new JScrollPane (tablePanel);
  mainPanel.add (scrollPane, BorderLayout.CENTER);

  JPanel buttonPanel = new JPanel ();
  JButton saveButton = new JButton ("Save");
  saveButton.addActionListener (new SaveAction ());
  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction ());
  buttonPanel.add (saveButton, BorderLayout.CENTER);
  buttonPanel.add (dismissButton, BorderLayout.CENTER);
  mainPanel.add (buttonPanel, BorderLayout.SOUTH);
  setContentPane (mainPanel);
  mainPanel.setPreferredSize (new Dimension (500, 500));
  setSize (800,600);

}

//-----------------------------------------------------------------------------------------

public class DismissAction extends AbstractAction {

  DismissAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    self.hide ();
  }

} // DismissAction
//--------------------------------------------------------------------------------

public class SaveAction extends AbstractAction {
    
    SaveAction () {super ("");}
    
    public void actionPerformed (ActionEvent e) {
	saveState();
    }  // actionPerformed
    
} // SaveAction
//--------------------------------------------------------------------------------

public csplugins.jActiveModules.Component [] getActivePaths() { return activePaths; }

//--------------------------------------------------------------------------------

public void saveState(String filename) {
    try {
	FileWriter fileWriter = new FileWriter (filename);
	for (int i=0; i < activePaths.length; i++) {
	    StringBuffer sb = new StringBuffer ();
	    sb.append("#Subnetwork " + i + "\n");
	    csplugins.jActiveModules.Component ap = activePaths[i];
	    String [] nodeNames = ap.getNodeNames();
	    String [] condNames = ap.getConditions();
	    double score = ap.getScore();
	    sb.append("#Score\n");
	    sb.append(score + "\n");
	    sb.append("#Nodes\n");
	    for (int j=0; j < nodeNames.length; j++)
		sb.append(nodeNames[j] + "\n");
	    sb.append("#Conditions\n");
	    for (int j=0; j < condNames.length; j++)
		sb.append(condNames[j] + "\n");
	    sb.append("\n");
	    fileWriter.write (sb.toString () );
	}
	fileWriter.close ();
    } catch (IOException ioe) {
	System.err.println ("Error while writing " + filename);
	ioe.printStackTrace ();
    } // catch
} // saveState

//--------------------------------------------------------------------------------

public void saveState() {
    JFileChooser chooser = new JFileChooser (cytoscapeWindow.getCytoscapeObj().getCurrentDirectory());
    if (chooser.showSaveDialog (null) == chooser.APPROVE_OPTION) {
	String name = chooser.getSelectedFile ().toString ();
	saveState(name);
    }
}



} // class ConditionsVsPathwaysTable
