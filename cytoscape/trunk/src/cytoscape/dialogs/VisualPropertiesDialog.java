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
import java.util.HashMap;
import java.util.Map;
import cytoscape.data.*;
import cytoscape.vizmap.*;
import cytoscape.dialogs.NewSlider;
//import csplugins.activePathsNew.data.ActivePathFinderParameters;
//--------------------------------------------------------------------------------------
public class VisualPropertiesDialog extends JDialog {

    JTextField readout;
    AttributeMapper aMapper;
    Color nColor;
    Color ppColor;
    Color pdColor;
    Color bgColor;
//--------------------------------------------------------------------------------------
public VisualPropertiesDialog (Frame parentFrame,
			       String title,
			       AttributeMapper mapper)
{
  super (parentFrame, true);
  setTitle (title);

  aMapper = mapper;
  nColor = getNodeColor();
  //ppColor = new Color(0,0,255);
  //pdColor = new Color(255,0,0);
  ppColor = getPPColor();
  pdColor = getPDColor();
  bgColor = getBGColor();

  JPanel mainPanel = new JPanel ();
  GridBagLayout gridbag = new GridBagLayout();   // see note below
  GridBagConstraints c = new GridBagConstraints();  // see note below
  mainPanel.setLayout (gridbag);

  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  c.gridx=0;
  c.gridy=0;
  gridbag.setConstraints(applyButton,c);
  mainPanel.add (applyButton);

  JButton edgePPButton = new JButton ("Edge Coloring: PP");
  edgePPButton.addActionListener (new EdgePPAction ());
  c.gridx=0;
  c.gridy=1;
  gridbag.setConstraints(edgePPButton,c);
  mainPanel.add (edgePPButton);

  JButton edgePDButton = new JButton ("Edge Coloring: PD");
  edgePDButton.addActionListener (new EdgePDAction ());
  c.gridx=1;
  c.gridy=1;
  gridbag.setConstraints(edgePDButton,c);
  mainPanel.add (edgePDButton);
  
  JButton colorButton
      = new JButton("Choose Node Color");
  colorButton.addActionListener(new SpawnNodeColorDialogListener());
  c.gridx=0;
  c.gridy=2;
  gridbag.setConstraints(colorButton,c);
  mainPanel.add(colorButton);

  JButton bgColorButton
      = new JButton("Choose Background Color");
  bgColorButton.addActionListener(new BGColorDialogListener());
  c.gridx=0;
  c.gridy=3;
  gridbag.setConstraints(bgColorButton,c);
  mainPanel.add(bgColorButton);

  ///////////////////////////////////////////

  //ColorChooserDialog ccd = new ColorChooserDialog(this,"whatever");
  //ccd.pack();
  //ccd.setVisible(true);

  setContentPane (mainPanel);
} // PopupDialog ctor

//--------------------------------------------------------------------------------------
public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {
      //Color c = new Color(0,0,0);
      Object o1 = aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR, nColor);
      Object o2 = aMapper.setDefaultValue(VizMapperCategories.BG_COLOR, bgColor);

	  /*
	    setAttributeMapEntry(VizMapperCategories.NODE_FILL_COLOR,
			   String domainAttributeName,
			   mapper);
	  */

      DiscreteMapper dm =
	  (DiscreteMapper)
	  aMapper.getValueMapper(VizMapperCategories.EDGE_COLOR);

      Map valueMap = dm.getValueMap();
      valueMap.remove("pp");
      valueMap.remove("pd");
      valueMap.put("pp",ppColor);
      valueMap.put("pd",pdColor);

      //DiscreteMapper dm = new DiscreteMapper(valueMap);
      //aMapper.setAttributeMapEntry(VizMapperCategories.EDGE_COLOR,
      //				   "interaction",
      //				   dm);

      VisualPropertiesDialog.this.dispose ();
  }

} // QuitAction
//-----------------------------------------------------------------------------

public class EdgePPAction extends AbstractAction {
  EdgePPAction () {
      super ("");
  }
  public void actionPerformed (ActionEvent e) {
      Color tempColor = JColorChooser.showDialog(VisualPropertiesDialog.this,
						 "Choose Color for PP",
						 ppColor);
      if (tempColor != null)
	  ppColor = tempColor;
  }
    /*
  public void actionPerformed (ActionEvent e) {
      Map valueMap = new HashMap();
      valueMap.put("pp",new Color(255,0,0));
      valueMap.put("pd",new Color(0,0,255));

      DiscreteMapper dm = new DiscreteMapper(valueMap);
      aMapper.setAttributeMapEntry(VizMapperCategories.EDGE_COLOR,
				   "interaction",
				   dm);
  }
    */
}

public class EdgePDAction extends AbstractAction {
  EdgePDAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {
      Color tempColor = JColorChooser.showDialog(VisualPropertiesDialog.this,
						 "Choose Color for PD",
						 pdColor);
      if (tempColor != null)
	  pdColor = tempColor;
  }
}

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
	Color tempColor = JColorChooser.showDialog(VisualPropertiesDialog.this,
						   "Choose Color",
						   nColor);
	if (tempColor != null)
	    nColor = tempColor;
    }
}

class BGColorDialogListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
	Color tempColor = JColorChooser.showDialog(VisualPropertiesDialog.this,
						   "Choose Color",
						   bgColor);
	if (tempColor != null)
	    bgColor = tempColor;
    }
}


    private Color getNodeColor() {
	Color tempColor;
	try {
	    tempColor = 
		(Color)aMapper.getDefaultValue(VizMapperCategories.NODE_FILL_COLOR);
	    //System.out.println("get: found real color");
	} catch (NullPointerException ex) {
	    System.out.println("get: made up color");
	    tempColor = new Color(255,255,255);
	}
	//if(tempColor != null)
	return tempColor;
	//else {
	//return
	//}
    }
    private Color getBGColor() {
	Color tempColor;
	try {
	    tempColor = 
		(Color)aMapper.getDefaultValue(VizMapperCategories.BG_COLOR);
	} catch (NullPointerException ex) {
	    tempColor = new Color(255,255,255);
	}
	if(tempColor != null)
	    return tempColor;
	else {
	    return new Color(255,255,255);
	}
    }

    private Color getPPColor() {
	Color tempColor;

	DiscreteMapper dm =
	    (DiscreteMapper)
	    aMapper.getValueMapper(VizMapperCategories.EDGE_COLOR);

	Map valueMap = dm.getValueMap();
	tempColor = (Color)valueMap.get("pp");

	if(tempColor != null)
	    return tempColor;
	else {
	    return new Color(0,0,255);
	}
    }

    private Color getPDColor() {
	Color tempColor;

	DiscreteMapper dm =
	    (DiscreteMapper)
	    aMapper.getValueMapper(VizMapperCategories.EDGE_COLOR);

	Map valueMap = dm.getValueMap();
	tempColor = (Color)valueMap.get("pd");

	if(tempColor != null)
	    return tempColor;
	else {
	    return new Color(255,255,0);
	}
    }

} // class VisualPropertiesDialog

