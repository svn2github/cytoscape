// LabelTextPanel.java

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




