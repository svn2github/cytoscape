// VisualPropertiesDialog.java
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
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

import cytoscape.data.*;
import cytoscape.vizmap.*;
import cytoscape.dialogs.NewSlider;
//import csplugins.activePathsNew.data.ActivePathFinderParameters;
//--------------------------------------------------------------------------------------
public class VisualPropertiesDialog extends JDialog {

    JTextField readout;
    AttributeMapper aMapper;
//--------------------------------------------------------------------------------------
public VisualPropertiesDialog (Frame parentFrame, String title, AttributeMapper mapper)
{
  super (parentFrame, true);
  setTitle (title);

  aMapper = mapper;
  JPanel mainPanel = new JPanel ();
  GridBagLayout gridbag = new GridBagLayout();   // see note below
  GridBagConstraints c = new GridBagConstraints();  // see note below
  mainPanel.setLayout (gridbag);

  readout = new JTextField(new String("this is the readout."));
  c.gridx=0;
  c.gridy=0;
  gridbag.setConstraints(readout,c);
  mainPanel.add (readout);

  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  c.gridx=0;
  c.gridy=1;
  gridbag.setConstraints(applyButton,c);
  mainPanel.add (applyButton);

  ///////////////////////////////////////////

  setContentPane (mainPanel);
} // PopupDialog ctor

//--------------------------------------------------------------------------------------
public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {
      Color c = new Color(0,0,0);
      Object o2 = aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR, c);

	  /*
	    setAttributeMapEntry(VizMapperCategories.NODE_FILL_COLOR,
			   String domainAttributeName,
			   mapper);
	  */

      VisualPropertiesDialog.this.dispose ();
  }

} // QuitAction
//-----------------------------------------------------------------------------
} // class VisualPropertiesDialog
