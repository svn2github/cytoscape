// VisualPropertiesDialog.java
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import cytoscape.data.*;
import cytoscape.vizmap.*;
import cytoscape.dialogs.NewSlider;
import cytoscape.util.MutableColor;
import cytoscape.dialogs.GeneralColorDialogListener;
//--------------------------------------------------------------------------------------
public class VisualPropertiesDialog extends JDialog {

    JTextField readout;
    AttributeMapper aMapper;
    MutableColor nColor;
    MutableColor ppColor;
    MutableColor pdColor;
    MutableColor bgColor;
//--------------------------------------------------------------------------------------
public VisualPropertiesDialog (Frame parentFrame,
			       String title,
			       AttributeMapper mapper)
{
  super (parentFrame, true);
  setTitle (title);

  aMapper = mapper;
  nColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_FILL_COLOR));
  ppColor = new MutableColor(getDMColor(VizMapperCategories.EDGE_COLOR, "pp"));
  pdColor = new MutableColor(getDMColor(VizMapperCategories.EDGE_COLOR, "pd"));
  bgColor = new MutableColor(getBasicColor(VizMapperCategories.BG_COLOR));

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
  edgePPButton.addActionListener (new GeneralColorDialogListener(this,ppColor,"Choose a P-P Edge Color"));
  c.gridx=0;
  c.gridy=1;
  gridbag.setConstraints(edgePPButton,c);
  mainPanel.add (edgePPButton);

  JButton edgePDButton = new JButton ("Edge Coloring: PD");
  edgePDButton.addActionListener (new GeneralColorDialogListener(this,pdColor,"Choose a P-D Edge Color"));
  c.gridx=1;
  c.gridy=1;
  gridbag.setConstraints(edgePDButton,c);
  mainPanel.add (edgePDButton);
  
  JButton colorButton = new JButton("Choose Node Color");
  colorButton.addActionListener(new GeneralColorDialogListener(this,nColor,"Choose a Node Color"));
  c.gridx=0;
  c.gridy=2;
  gridbag.setConstraints(colorButton,c);
  mainPanel.add(colorButton);

  JButton bgColorButton
      = new JButton("Choose Background Color");
  bgColorButton.addActionListener(new GeneralColorDialogListener(this,bgColor,"Choose a Background Color"));
  c.gridx=0;
  c.gridy=3;
  gridbag.setConstraints(bgColorButton,c);
  mainPanel.add(bgColorButton);

  setContentPane (mainPanel);
} // PopupDialog ctor


public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {

      Object o1 = aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR, nColor.getColor());
      Object o2 = aMapper.setDefaultValue(VizMapperCategories.BG_COLOR, bgColor.getColor());

      EdgeArrowColor.removeThenAddEdgeColor(aMapper,"pp",ppColor.getColor());
      EdgeArrowColor.removeThenAddEdgeColor(aMapper,"pd",pdColor.getColor());

      VisualPropertiesDialog.this.dispose ();
  }

} // ApplyAction


    private Color getBasicColor(Integer category) {
	Color tempColor;
	try {
	    tempColor = 
		(Color)aMapper.getDefaultValue(category);
	} catch (NullPointerException ex) {
	    tempColor = new Color(255,255,255);
	}
	if(tempColor != null)
	    return tempColor;
	else {
	    return new Color(255,255,255);
	}
    }

    private Color getDMColor(Integer category, String key) {
	Color tempColor;

	DiscreteMapper dm =
	    (DiscreteMapper)
	    aMapper.getValueMapper(category);

	Map valueMap = dm.getValueMap();
	tempColor = (Color)valueMap.get(key);

	if(tempColor != null)
	    return tempColor;
	else {
	    return new Color(255,255,255);
	}
    }

} // class VisualPropertiesDialog


