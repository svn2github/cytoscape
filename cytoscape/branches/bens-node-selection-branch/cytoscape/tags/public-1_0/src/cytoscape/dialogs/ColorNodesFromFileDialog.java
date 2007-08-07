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
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
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

import java.util.*;

import cytoscape.CytoscapeWindow;
import cytoscape.GraphObjAttributes;
import cytoscape.util.MutableColor;
import cytoscape.dialogs.MiscGB;
import cytoscape.dialogs.MiscDialog;
import cytoscape.util.Misc;

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
	fColorButton = MiscGB.buttonAndColor(this,fColor,this.fColorLabel,"Fill Color");
	colorPanel.add(fColorButton);
	colorPanel.add(fColorLabel);
	mainPanel.add(colorPanel);

	buttonPanel = new JPanel();
	buttonPanel.setBorder(paneEdge);
	buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	applyButton = new JButton("Apply");
	applyButton.addActionListener(new ApplyAction());
	buttonPanel.add(applyButton);
	dismissButton = new JButton("Dismiss");
	dismissButton.addActionListener(new DismissAction());
	buttonPanel.add(dismissButton);
	mainPanel.add(buttonPanel);
		
	setContentPane(this.mainPanel);
	
	
    }//createUI
    //------------------------------------------------------
    public class ApplyAction extends AbstractAction{
	ApplyAction(){
	    super("");
	}

	public void actionPerformed(ActionEvent e){
	    String file = fileNameField.getText();
	    
	    try{
		FileReader fileReader = new FileReader(file);
		LineNumberReader lineReader = new LineNumberReader(fileReader);
		GraphObjAttributes nodeAttr = cytoscapeWindow.getNodeAttributes();
		Color fillC = fColor.getColor();
		String line = lineReader.readLine();
		String strC = Misc.getRGBText(fillC);
		while(line != null){
		    Node n = (Node)nodeAttr.getGraphObject(line);
		    if(n != null){
			nodeAttr.set("node.fillColor",line,strC);
		    }
		    line = lineReader.readLine();
		}//while
		cytoscapeWindow.redrawGraph();
		
	    }catch(Exception ex){
		JOptionPane.showMessageDialog(null,"Could not open or read file.\nCheck format and name.",
					      "Error", JOptionPane.ERROR_MESSAGE); 
	    }
	    
	}
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


