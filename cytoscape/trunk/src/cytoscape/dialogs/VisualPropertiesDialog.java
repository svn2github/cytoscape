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
    Color nColor;
//--------------------------------------------------------------------------------------
public VisualPropertiesDialog (Frame parentFrame, String title, AttributeMapper mapper)
{
  super (parentFrame, true);
  setTitle (title);

  nColor = getNodeColor();

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
  
  JButton colorButton
      = new JButton("Choose Node Color");
  colorButton.addActionListener(new SpawnNodeColorDialogListener());
  c.gridx=0;
  c.gridy=2;
  gridbag.setConstraints(colorButton,c);
  mainPanel.add(colorButton);


  //JDialog ccdd = new ColorChooserDialog(this,"node color");
  //ccdd.pack ();
  //ccdd.setLocationRelativeTo (this);
  //ccdd.setVisible (true);

  ///////////////////////////////////////////

  setContentPane (mainPanel);
} // PopupDialog ctor

//--------------------------------------------------------------------------------------
public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {
      //Color c = new Color(0,0,0);
      Object o2 = aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR, nColor);

	  /*
	    setAttributeMapEntry(VizMapperCategories.NODE_FILL_COLOR,
			   String domainAttributeName,
			   mapper);
	  */

      VisualPropertiesDialog.this.dispose ();
  }

} // QuitAction
//-----------------------------------------------------------------------------



public class ColorChooserDialog extends JDialog {
    public ColorChooserDialog(JDialog parentDialog, String whatFor) {
        super(parentDialog, "Choose Color for " + whatFor);

        //Set up the banner at the top of the window
        final JLabel banner = new JLabel("Choose Color for " + whatFor,
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
		    //System.out.println("hello!");
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
    


    
    
class SpawnNodeColorDialogListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
	// Args are parent component, title, initial color
	nColor = JColorChooser.showDialog(VisualPropertiesDialog.this,
					  "Choose Color",
					  getNodeColor());
	if (nColor != null)
	    aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR,nColor);
    }
}
    
    private Color getNodeColor() {
	Color tempColor;
	try {
	    System.out.println("found real color");
	    tempColor = 
		(Color)aMapper.getDefaultValue(VizMapperCategories.NODE_FILL_COLOR);
	} catch (NullPointerException ex) {
	    System.out.println("made up color");
	    tempColor = new Color(255,255,255);
	}
	//if(tempColor != null)
	return tempColor;
	//else {
	//return
	//}
    }

} // class VisualPropertiesDialog

