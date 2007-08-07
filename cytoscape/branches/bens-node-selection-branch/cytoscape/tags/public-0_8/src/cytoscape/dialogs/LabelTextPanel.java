// LabelTextPanel.java
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import cytoscape.GraphObjAttributes;
import cytoscape.util.MutableString;
//--------------------------------------------------------------------------------------
public class LabelTextPanel extends JPanel {

    String [] attributeNames;
    MutableString nodeLabelKey;
    JComboBox theBox;
//--------------------------------------------------------------------------------------
public LabelTextPanel (GraphObjAttributes nodeAttribs,
		       MutableString writeHere)
{
  super ();
  attributeNames = nodeAttribs.getAttributeNames ();
  nodeLabelKey = writeHere;

  GridBagLayout gridbag = new GridBagLayout(); 
  GridBagConstraints c = new GridBagConstraints();
  this.setLayout (gridbag);

  // using Model so we can manually add "canonicalName" at the end.
  DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
  for (int i=0; i < attributeNames.length; i++)
      boxModel.addElement(new String(attributeNames [i]));
  boxModel.addElement(new String("canonicalName"));

  theBox = new JComboBox(boxModel);
  theBox.setSelectedItem(nodeLabelKey.getString());
  theBox.addActionListener(new BoxAction());

  JLabel label = new JLabel("Node Label: ");
  c.gridx=0;
  c.gridy=0;
  gridbag.setConstraints(label,c);
  this.add(label);
  c.gridx=1;
  c.gridy=0;
  gridbag.setConstraints(theBox,c);
  this.add(theBox);
  
} // LabelTextPanel ctor

public class BoxAction extends AbstractAction {
    public void actionPerformed (ActionEvent e) {
	JComboBox jcb = (JComboBox)e.getSource();
	nodeLabelKey.setString((String)jcb.getSelectedItem());
    }
}

} // class LabelTextPanel


