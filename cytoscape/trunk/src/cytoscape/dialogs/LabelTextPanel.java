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
    JList theList;
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

  theList = new JList(attributeNames);
  theList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  theList.addListSelectionListener(new SharedListSelectionHandler());

  /*
    for (int i=0; i < attributeNames.length; i++) {
    String attributeName = attributeNames [i];
    System.out.println(attributeName);
    }
  */

  c.gridx=0;
  c.gridy=0;
  gridbag.setConstraints(theList,c);
  this.add(theList);
  
} // LabelTextPanel ctor

public class SharedListSelectionHandler implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {

	JList jl = (JList)e.getSource();
	nodeLabelKey.setString(jl.getSelectedValue().toString());
    }
}


} // class LabelTextPanel


