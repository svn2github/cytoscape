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

import y.view.LineType;

import cytoscape.GraphObjAttributes;
import cytoscape.data.*;
import cytoscape.vizmap.*;
import cytoscape.dialogs.NewSlider;
import cytoscape.util.MutableColor;
import cytoscape.util.MutableString;
import cytoscape.dialogs.GeneralColorDialogListener;
//--------------------------------------------------------------------------------------
public class VisualPropertiesDialog extends JDialog {

    ShapePopupButton shapeDefault;
    IntegerEntryField sizeDefault;
    LineTypePopupButton lineTypeDefault;
    AttributeMapper aMapper;
    MutableColor nColor;
    MutableColor bColor;
    MutableColor ppColor;
    MutableColor pdColor;
    MutableColor bgColor;
    MutableString localNodeLabelKey;
    MutableString parentNodeLabelKey;
//--------------------------------------------------------------------------------------
public VisualPropertiesDialog (Frame parentFrame,
			       String title,
			       AttributeMapper mapper,
			       GraphObjAttributes nodeAttribs,
			       MutableString nodeLabelKey)
{
  super (parentFrame, true);
  setTitle (title);

  aMapper = mapper;
  nColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_FILL_COLOR));
  bColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_BORDER_COLOR));
  ppColor = new MutableColor(getDMColor(VizMapperCategories.EDGE_COLOR, "pp"));
  pdColor = new MutableColor(getDMColor(VizMapperCategories.EDGE_COLOR, "pd"));
  bgColor = new MutableColor(getBasicColor(VizMapperCategories.BG_COLOR));
  localNodeLabelKey = new MutableString(nodeLabelKey.getString());
  parentNodeLabelKey = nodeLabelKey;

  JPanel mainPanel = new JPanel ();
  GridBagLayout gridbag = new GridBagLayout(); 
  GridBagConstraints c = new GridBagConstraints();
  mainPanel.setLayout (gridbag);

  JButton edgePPButton = new JButton ("Edge Coloring: PP");
  edgePPButton.addActionListener (new GeneralColorDialogListener(this,ppColor,"Choose a P-P Edge Color"));
  c.gridx=0;
  c.gridy=0;
  gridbag.setConstraints(edgePPButton,c);
  mainPanel.add (edgePPButton);

  JButton edgePDButton = new JButton ("Edge Coloring: PD");
  edgePDButton.addActionListener (new GeneralColorDialogListener(this,pdColor,"Choose a P-D Edge Color"));
  c.gridx=1;
  c.gridy=0;
  gridbag.setConstraints(edgePDButton,c);
  mainPanel.add (edgePDButton);
  
  JButton colorButton = new JButton("Choose Node Color");
  colorButton.addActionListener(new GeneralColorDialogListener(this,nColor,"Choose a Node Color"));
  c.gridx=0;
  c.gridy=1;
  gridbag.setConstraints(colorButton,c);
  mainPanel.add(colorButton);

  JButton borderColorButton = new JButton("Choose Node Border Color");
  borderColorButton.addActionListener(new GeneralColorDialogListener(this,bColor,"Choose a Node Border Color"));
  c.gridx=0;
  c.gridy=2;
  gridbag.setConstraints(borderColorButton,c);
  mainPanel.add(borderColorButton);

  JButton bgColorButton
      = new JButton("Choose Background Color");
  bgColorButton.addActionListener(new GeneralColorDialogListener(this,bgColor,"Choose a Background Color"));
  c.gridx=0;
  c.gridy=3;
  gridbag.setConstraints(bgColorButton,c);
  mainPanel.add(bgColorButton);

  JPanel labelTextPanel
      = new LabelTextPanel(nodeAttribs,localNodeLabelKey);
  c.gridx=0;
  c.gridy=4;
  gridbag.setConstraints(labelTextPanel,c);
  mainPanel.add(labelTextPanel);
  
  shapeDefault = 
      new ShapePopupButton
	  ("Default Node Shape",
	   ((Byte)aMapper.getDefaultValue(VizMapperCategories.NODE_SHAPE)).byteValue(),
	   this);
  c.gridx=0;
  c.gridy=5;
  gridbag.setConstraints(shapeDefault,c);
  mainPanel.add(shapeDefault);

  lineTypeDefault = 
      new LineTypePopupButton
	  ("Default Line Type",
	   (LineType)aMapper.getDefaultValue(VizMapperCategories.EDGE_LINETYPE),
	   this);
  c.gridx=0;
  c.gridy=6;
  gridbag.setConstraints(lineTypeDefault,c);
  mainPanel.add(lineTypeDefault);

  sizeDefault = 
      new IntegerEntryField
	  ("Default Node Size",
	   ((Integer)aMapper.getDefaultValue(VizMapperCategories.NODE_HEIGHT)).intValue(),
	   500);
  c.gridx=0;
  c.gridy=7;
  gridbag.setConstraints(sizeDefault,c);
  mainPanel.add(sizeDefault);


  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  c.gridx=0;
  c.gridy=8;
  gridbag.setConstraints(applyButton,c);
  mainPanel.add (applyButton);

  JButton cancelButton = new JButton ("Cancel");
  cancelButton.addActionListener (new CancelAction ());
  c.gridx=1;
  c.gridy=8;
  gridbag.setConstraints(cancelButton,c);
  mainPanel.add (cancelButton);


  setContentPane (mainPanel);
} // PopupDialog ctor


public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {

      Object o1 = aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR, nColor.getColor());
      Object o2 = aMapper.setDefaultValue(VizMapperCategories.BG_COLOR, bgColor.getColor());
      Object o3 = aMapper.setDefaultValue(VizMapperCategories.NODE_SHAPE, shapeDefault.getShapeByte());
      Object o4 = aMapper.setDefaultValue(VizMapperCategories.EDGE_LINETYPE, lineTypeDefault.getLineType());
      Object o5 = aMapper.setDefaultValue(VizMapperCategories.NODE_HEIGHT, sizeDefault.getInteger());
      Object o6 = aMapper.setDefaultValue(VizMapperCategories.NODE_WIDTH, sizeDefault.getInteger());
      Object o7 = aMapper.setDefaultValue(VizMapperCategories.NODE_BORDER_COLOR, bColor.getColor());

      EdgeArrowColor.removeThenAddEdgeColor(aMapper,"pp",ppColor.getColor());
      EdgeArrowColor.removeThenAddEdgeColor(aMapper,"pd",pdColor.getColor());

      //System.out.println(localNodeLabelKey.getString());
      parentNodeLabelKey.setString(localNodeLabelKey.getString());
      VisualPropertiesDialog.this.dispose ();
  }

} // ApplyAction

public class CancelAction extends AbstractAction {
  CancelAction () { super (""); }
  public void actionPerformed (ActionEvent e) {
      VisualPropertiesDialog.this.dispose ();
  }
} // CancelAction


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


