package cytoscape.layout;

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

//---------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.border.*;
import javax.swing.JFormattedTextField;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.text.*;
import java.util.*;

import y.layout.hierarchic.*;

//------------------------------------------------------------/
public class HierarchicalLayoutDialog extends JDialog {

    JFormattedTextField nodeDistanceField;
    JFormattedTextField layerDistanceField;
    JFormattedTextField edgeDistanceField;
    JComboBox           layererList;
    JComboBox           routingList;
    JComboBox           layoutList;
    Hashtable           layererHash;
    Hashtable           routingHash;
    Hashtable           layoutHash;
    JCheckBox           usePortsBox;
    JCheckBox           removeFalseCrossingsBox;

    HierarchicLayouter layouter;

//--------------------------------------------------------------------------------------
public HierarchicalLayoutDialog (Frame parentFrame) {

  super (parentFrame, true);
  setTitle ("Hierarchical Layout Options");

  // initialize layerer- this is the core object that is configured and returned
  layouter = new HierarchicLayouter();  

  // set component layout
  JPanel mainPanel = new JPanel ();
  GridLayout grid = new GridLayout(0,2); 
  mainPanel.setLayout (grid);

  // build double value fields
  layerDistanceField = addDoubleField(mainPanel, "Minimum Layer Distance", 100.0);
  nodeDistanceField = addDoubleField(mainPanel, "Minimum Node Distance", 60.0);
  edgeDistanceField = addDoubleField(mainPanel, "Minimum Edge Distance", 40.0);
  
  // build combo box fields
  JLabel layererLabel = new JLabel("Layering Algorithm");  
  String  [] layererStrings = {"None", "BFS", "Topological"};
  Layerer [] layererClasses = { new AsIsLayerer(), new BFSLayerer(),
				new TopologicalLayerer() };
  layererHash = new Hashtable();
  for (int i=0; i<layererStrings.length; i++) 
      layererHash.put(layererStrings[i], layererClasses[i]);
  layererList = new JComboBox(layererStrings);
  layererList.setSelectedIndex(0);
  mainPanel.add(layererLabel);
  mainPanel.add(layererList);

  JLabel routingLabel = new JLabel("Routing Style");  
  String [] routingStrings = {"Orthogonal", "Polyline"};
  Byte   [] routingBytes   = { new Byte (HierarchicLayouter.ROUTE_ORTHOGONAL),
			       new Byte (HierarchicLayouter.ROUTE_POLYLINE)  };
  routingHash = new Hashtable();
  for (int i=0; i<routingStrings.length; i++) 
      routingHash.put(routingStrings[i], routingBytes[i]);
  routingList = new JComboBox(routingStrings);
  routingList.setSelectedIndex(0);
  mainPanel.add(routingLabel);
  mainPanel.add(routingList);

  JLabel layoutLabel = new JLabel("Layout Style");  
  String [] layoutStrings = {"Linear Segments", "Pendulum", "Polyline"};
  Byte   [] layoutBytes   = { new Byte (HierarchicLayouter.LINEAR_SEGMENTS),
			      new Byte (HierarchicLayouter.PENDULUM),
                              new Byte (HierarchicLayouter.POLYLINE) };
  layoutHash = new Hashtable();
  for (int i=0; i<layoutStrings.length; i++) 
      layoutHash.put(layoutStrings[i], layoutBytes[i]);
  layoutList = new JComboBox(layoutStrings);
  layoutList.setSelectedIndex(0);
  mainPanel.add(layoutLabel);
  mainPanel.add(layoutList);

  // checkboxes
  //usePortsBox = new JCheckBox("Use Ports");
  //mainPanel.add(usePortsBox);
  removeFalseCrossingsBox = new JCheckBox("Remove False Crossings");
  mainPanel.add(removeFalseCrossingsBox);

  // ok and cancel buttons
  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  mainPanel.add (applyButton);
  JButton cancelButton = new JButton ("Cancel");
  cancelButton.addActionListener (new CancelAction ());
  mainPanel.add (cancelButton);
  setContentPane (mainPanel);
} // PopupDialog ctor

//------------------------------------------------------------/

private JFormattedTextField addDoubleField (JPanel panel, String label, double value) {

    // set up decimal formatter to handle numerical entry for many fields
    DecimalFormat decimalFormat = new DecimalFormat("###.#");
    NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
    textFormatter.setValueClass(Double.class);
    textFormatter.setOverwriteMode(true);
    textFormatter.setAllowsInvalid(false);

    // set up label
    JLabel myLabel = new JLabel(label);  
    panel.add(myLabel);

    // set up field itself
    JFormattedTextField myField = new JFormattedTextField(textFormatter);
    myField.setValue( new Double (value));
    myField.setPreferredSize(new Dimension (100,20));
    panel.add(myField);
    return myField;
}

//------------------------------------------------------------/

public HierarchicLayouter getLayouter () {
    return layouter;
}

//------------------------------------------------------------/

public class ApplyAction extends AbstractAction {
    
    String item;

    ApplyAction () { super (""); }
    public void actionPerformed (ActionEvent e) {
	item = (String) layererList.getSelectedItem();
	layouter.setLayerer( (Layerer) layererHash.get(item) );
	layouter.setMinimalNodeDistance(
           ( (Double) nodeDistanceField.getValue() ).doubleValue());
	layouter.setMinimalEdgeDistance(
           ( (Double) edgeDistanceField.getValue() ).doubleValue());
	layouter.setMinimalLayerDistance(
           ( (Double) layerDistanceField.getValue() ).doubleValue());
	item = (String) routingList.getSelectedItem();
	layouter.setRoutingStyle(( (Byte) routingHash.get(item)).byteValue());
	item = (String) layoutList.getSelectedItem();
	layouter.setLayoutStyle(( (Byte) layoutHash.get(item)).byteValue());
	layouter.setRemoveFalseCrossings( removeFalseCrossingsBox.isSelected() );
	// layouter.setUsePorts( usePortsBox.isSelected() );
	HierarchicalLayoutDialog.this.dispose ();
    }
} // ApplyAction

//------------------------------------------------------------/
    
public class CancelAction extends AbstractAction {
    CancelAction () { super (""); }
    public void actionPerformed (ActionEvent e) {
	HierarchicalLayoutDialog.this.dispose ();
    }
} // CancelAction
    
//------------------------------------------------------------/

} // class VisualPropertiesDialog




