// ColorNodesFromFileDialog

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//-------------------------------------------------------------
/**
 * A dialog that allows the user to color the nodes given in a
 * text file (list of ORFs). 
 * @author iliana
 * @version %I%, %G%
 */
//-------------------------------------------------------------
package cytoscape.dialogs;
//-------------------------------------------------------------
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.AbstractAction;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.File;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.awt.Dimension;
import javax.swing.Box;



import java.util.*;
import java.lang.Integer;

import cytoscape.CytoscapeWindow;
import cytoscape.GraphObjAttributes;
import cytoscape.util.MutableColor;
import cytoscape.dialogs.MiscGB;
import cytoscape.dialogs.MiscDialog;
import cytoscape.util.Misc;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.calculators.*;

import y.base.Node;
//-------------------------------------------------------------
public class ColorNodesFromFileDialog extends JDialog{
  protected CytoscapeWindow cytoscapeWindow;
  static final int FIELD_WIDTH = 30;
    
  JPanel mainPanel;
    
  JPanel filePanel;
  JLabel fileNameLabel;
  JTextField fileNameField;
  JButton browseButton;
    
  JPanel colorPanel;
  JButton fColorButton;
  JLabel fColorLabel;
  MutableColor fColor;
        
  JPanel buttonPanel;
  JButton dismissButton;
  JButton applyButton;

  Border paneEdge;
    
  File currentDirectory;
  
  public ColorNodesFromFileDialog(CytoscapeWindow cytoscapeWindow){
    super(cytoscapeWindow.getMainFrame(), false);
    this.cytoscapeWindow = cytoscapeWindow;
    this.currentDirectory = new File (System.getProperty("user.dir"));
    setTitle("Color Nodes From File");
    createUI();
  }//ColorNodesFromFile
  
  // TODO: Remove. This method is no longer used.
  protected void setColorCalculator (){
    
    VisualMappingManager vmManager = cytoscapeWindow.getVizMapManager();
    NodeAppearanceCalculator nodeAppCalc = vmManager.getVisualStyle().getNodeAppearanceCalculator();
    NodeColorCalculator nfc = nodeAppCalc.getNodeFillColorCalculator();
    GenericNodeColorCalculator gncc = (GenericNodeColorCalculator)nfc; //cast to known type
    ObjectMapping objectMapping = gncc.getMapping();
    DiscreteMapping colorMapping;
    if(!(objectMapping instanceof DiscreteMapping)){
      colorMapping = new DiscreteMapping( Color.WHITE,
                                          ObjectMapping.NODE_MAPPING);
    }else{
      colorMapping = (DiscreteMapping)objectMapping;
    }
    colorMapping.setControllingAttributeName("nodeInFile",
                                             vmManager.getNetwork(),
                                             true);
    colorMapping.put(this.fColor.getColor().toString(),this.fColor.getColor());
    GenericNodeColorCalculator colorCalculator = new GenericNodeColorCalculator("Color Nodes From File",
                                                                                colorMapping);
    nodeAppCalc.setNodeFillColorCalculator(colorCalculator);
    
    vmManager.applyAppearances();
    
  }//setColorCalculator
  
  protected void createUI(){
    if(mainPanel != null){
	    mainPanel.removeAll();
    }

    mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    paneEdge = BorderFactory.createEmptyBorder(5,5,5,5);
    mainPanel.setBorder(paneEdge);
	
    paneEdge = BorderFactory.createEmptyBorder(3,3,3,3);

    filePanel = new JPanel();
    filePanel.setBorder(paneEdge);
    filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
    fileNameLabel = new JLabel("File:");
    filePanel.add(fileNameLabel);
    fileNameField = new JTextField(FIELD_WIDTH);
    filePanel.add(Box.createRigidArea(new Dimension(8,0)));
    filePanel.add(fileNameField);
    browseButton = new JButton("Browse");
    browseButton.addActionListener(new BrowseButtonAction(fileNameField));
    filePanel.add(Box.createRigidArea(new Dimension(8,0)));
    filePanel.add(browseButton);
    mainPanel.add(filePanel);

    colorPanel = new JPanel();
    colorPanel.setBorder(paneEdge);
    colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
    fColor = new MutableColor(Color.CYAN);
    fColorLabel = MiscGB.createColorLabel(Color.CYAN);
    fColorLabel.addPropertyChangeListener("background",new UpdateFillColorListener());
    fColorButton = MiscGB.buttonAndColor(this,fColor,this.fColorLabel,"Choose Fill Color");
    colorPanel.add(Box.createRigidArea(new Dimension(32,15)));
    colorPanel.add(fColorButton);
    colorPanel.add(Box.createRigidArea(new Dimension (15, 15)));
    colorPanel.add(fColorLabel);
    

    //buttonPanel = new JPanel();
    //buttonPanel.setBorder(paneEdge);
    //buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    applyButton = new JButton("Apply");
    applyButton.addActionListener(new ApplyAction());
    //buttonPanel.add(applyButton);
    colorPanel.add(Box.createRigidArea(new Dimension(75,15)));
    colorPanel.add(applyButton);
    dismissButton = new JButton("Dismiss");
    dismissButton.addActionListener(new DismissAction());
    //buttonPanel.add(dismissButton);
    colorPanel.add(Box.createRigidArea(new Dimension (15, 15)));
    colorPanel.add(dismissButton);
    //mainPanel.add(buttonPanel);
		mainPanel.add(colorPanel);
    
    setContentPane(this.mainPanel);
	
	
  }//createUI
  //------------------------------------------------------
  public class ApplyAction extends AbstractAction{
    ApplyAction(){
	    super("");
    }

    public void actionPerformed(ActionEvent e){
	    String file = fileNameField.getText();
	    if(file.length() == 0){
        return;
      }
	    try{
        FileReader fileReader = new FileReader(file);
        LineNumberReader lineReader = new LineNumberReader(fileReader);
        GraphObjAttributes nodeAttr = cytoscapeWindow.getNodeAttributes();
        Color fillC = fColor.getColor();
        String line = lineReader.readLine();
        int numLines = 0;
        
        while(line != null){
          Node n = (Node)nodeAttr.getGraphObject(line);
          if(n != null){
            nodeAttr.set(NodeAppearanceCalculator.nodeFillColorBypass,line,fillC);
            numLines++;
          }
          line = lineReader.readLine();
        }//while
        if(numLines > 0){
          cytoscapeWindow.redrawGraph(false,true);
        }
      }catch(Exception ex){
        System.err.println(ex);
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,"Could not open or read file.\nCheck format and name.",
                                      "Error", JOptionPane.ERROR_MESSAGE); 
	    }
	    
    }//actionPerformed
  }//ApplyAction
  //------------------------------------------------------
  public class BrowseButtonAction extends AbstractAction{
	
    JTextField field;
	
    BrowseButtonAction (JTextField field) {
	    super ("");
	    this.field = field;
    }
	
	
    public void actionPerformed (ActionEvent e) {
	    
	    JFileChooser chooser = new JFileChooser(currentDirectory);
	    if(chooser.showOpenDialog(ColorNodesFromFileDialog.this) == chooser.APPROVE_OPTION){
        currentDirectory = chooser.getCurrentDirectory();
        String name = chooser.getSelectedFile ().toString ();
        this.field.setText(name);
	    }
    }
  }// BrowseButtonAction class
  //------------------------------------------------------
  public class DismissAction extends AbstractAction{
    DismissAction(){super("");}//cons
	
    public void actionPerformed(ActionEvent e){
	    ColorNodesFromFileDialog.this.dispose();
    }//actionPerformed
	
  }//DismissAction
    
  //------------------------------------------------------
  public class UpdateFillColorListener implements PropertyChangeListener{
    UpdateFillColorListener(){}//cons
	
    public void propertyChange(PropertyChangeEvent event){
	    String property = event.getPropertyName();
	    if(property.equals("background")){
        Color fillColor = fColor.getColor();
	    }
    }//propertyChange
	
  }//UpdateFillColorListener
    
    
}//ColorNodesFromFileDialog


