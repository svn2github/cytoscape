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

import javax.swing.colorchooser.*;

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

  JDialog ccdd = new ColorChooserDemoDialog(this);
  ccdd.pack ();
  ccdd.setLocationRelativeTo (this);
  ccdd.setVisible (true);

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



public class ColorChooserDemoDialog extends JDialog {
    public ColorChooserDemoDialog(JDialog parentDialog) {
        super(parentDialog, "ColorChooserDemoDialog");

        //Set up the banner at the top of the window
        final JLabel banner = new JLabel("Welcome to the Tutorial Zone!",
                                         JLabel.CENTER);
        banner.setForeground(Color.yellow);
        banner.setBackground(Color.blue);
        banner.setOpaque(true);
        banner.setFont(new Font("SansSerif", Font.BOLD, 24));
        banner.setPreferredSize(new Dimension(100, 65));

        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.add(banner, BorderLayout.CENTER);
        bannerPanel.setBorder(BorderFactory.createTitledBorder("Banner"));

        //Set up color chooser for setting text color
        final JColorChooser tcc = new JColorChooser(banner.getForeground());
        tcc.getSelectionModel().addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    Color newColor = tcc.getColor();
		    System.out.println("hello!");
                    banner.setForeground(newColor);
                }
            }
        );
        tcc.setBorder(BorderFactory.createTitledBorder(
                                             "Choose Text Color"));

        //Add the components to the demo frame
        Container contentPane = getContentPane();
        contentPane.add(bannerPanel, BorderLayout.CENTER);
        contentPane.add(tcc, BorderLayout.SOUTH);
    }

}


} // class VisualPropertiesDialog

