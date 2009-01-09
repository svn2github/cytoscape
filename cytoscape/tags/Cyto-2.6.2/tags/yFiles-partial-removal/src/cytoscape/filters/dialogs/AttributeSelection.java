// AttributeSelection.java
//------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------
package cytoscape.filters.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import cytoscape.GraphObjAttributes;

public class AttributeSelection extends JPanel {

    public static final String noneString = "noneSelected";

    String [] attributeNames;
    String attributeSelected;
    JComboBox theBox;

    public AttributeSelection (GraphObjAttributes nodeAttribs)
    {
	super ();
	attributeNames = nodeAttribs.getAttributeNames ();
	attributeSelected = null;
	
	// layout
	GridBagLayout gridbag = new GridBagLayout(); 
	GridBagConstraints c = new GridBagConstraints();
	this.setLayout (gridbag);
	
	// combo box for the selection
	DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
	boxModel.addElement(new String (AttributeSelection.noneString));
	for (int i=0; i < attributeNames.length; i++)
	    boxModel.addElement(new String(attributeNames [i]));
	theBox = new JComboBox(boxModel);
	theBox.setSelectedItem(AttributeSelection.noneString);
	theBox.addActionListener(new BoxAction());

	// add combo box to panel
	c.gridx=1;
	c.gridy=0;
	gridbag.setConstraints(theBox,c);
	this.add(theBox);
    } 

    public void setEnabled(boolean b) {
	theBox.setEnabled(b);
    }

    public class BoxAction extends AbstractAction {
	public void actionPerformed (ActionEvent e) {
	    JComboBox jcb = (JComboBox)e.getSource();
	    attributeSelected = (String)jcb.getSelectedItem();
	}
    }
    public String getAttributeSelected() {
	if(attributeSelected==AttributeSelection.noneString) return null;
	else return attributeSelected;
    }

} // class AttributeSelection


