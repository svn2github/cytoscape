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
import y.view.Arrow;

import cytoscape.GraphObjAttributes;
import cytoscape.data.*;
import cytoscape.vizmap.*;
import cytoscape.dialogs.NewSlider;
import cytoscape.util.MutableColor;
import cytoscape.util.MutableString;
import cytoscape.dialogs.GeneralColorDialogListener;
//--------------------------------------------------------------------------------------
public class VisualPropertiesDialog extends JDialog {

    IconPopupButton shapeDefault;
    IconPopupButton lineTypeDefault;
    IconPopupButton arrowDefault;
    IntegerEntryField sizeDefault;
    AttributeMapper aMapper;
    Frame parentFrame;
    MutableColor nColor;
    MutableColor bColor;
    MutableColor bgColor;
    MutableString localNodeLabelKey;
    MutableString parentNodeLabelKey;
    MutableString localEdgeKey;
    EdgeTextPanel edgeTextPanel;
//--------------------------------------------------------------------------------------
public VisualPropertiesDialog (Frame parentFrame,
			       String title,
			       AttributeMapper mapper,
			       GraphObjAttributes nodeAttribs,
			       GraphObjAttributes edgeAttribs,
			       MutableString nodeLabelKey)
{
  super (parentFrame, true);
  setTitle (title);
  this.parentFrame = parentFrame;

  aMapper = mapper;
  nColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_FILL_COLOR));
  bColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_BORDER_COLOR));
  bgColor = new MutableColor(getBasicColor(VizMapperCategories.BG_COLOR));
  localNodeLabelKey = new MutableString(nodeLabelKey.getString());
  parentNodeLabelKey = nodeLabelKey;

  JPanel mainPanel = new JPanel ();
  GridBagLayout gridbag = new GridBagLayout(); 
  GridBagConstraints c = new GridBagConstraints();
  mainPanel.setLayout (gridbag);


  JButton colorButton = new JButton("Node Color");
  JLabel colorLabel = new JLabel("    ");
  colorLabel.setOpaque(true);
  colorLabel.setBackground(nColor.getColor());
  colorButton.addActionListener(new
      GeneralColorDialogListener(this,nColor,colorLabel,
				 "Choose a Node Color"));
  colorButton.setSize(new Dimension(400,100));
  c.gridx=1;
  c.gridy=1;
  c.fill=GridBagConstraints.NONE;
  gridbag.setConstraints(colorLabel,c);
  mainPanel.add(colorLabel);

  c.gridx=0;
  c.gridy=1;
  c.fill=GridBagConstraints.HORIZONTAL;
  gridbag.setConstraints(colorButton,c);
  mainPanel.add(colorButton);

  JButton borderColorButton = new JButton("Node Border Color");
  JLabel borderColorLabel = new JLabel("    ");
  borderColorLabel.setOpaque(true);
  borderColorLabel.setBackground(bColor.getColor());
  borderColorButton.addActionListener(new
      GeneralColorDialogListener(this,bColor,borderColorLabel,
				 "Choose a Node Border Color"));
  c.gridx=1;
  c.gridy=2;
  c.fill=GridBagConstraints.NONE;
  gridbag.setConstraints(borderColorLabel,c);
  mainPanel.add(borderColorLabel);

  c.gridx=0;
  c.gridy=2;
  c.fill=GridBagConstraints.HORIZONTAL;
  gridbag.setConstraints(borderColorButton,c);
  mainPanel.add(borderColorButton);


  JButton bgColorButton
      = new JButton("Background Color");
  JLabel bgColorLabel = new JLabel("    ");
  bgColorLabel.setOpaque(true);
  bgColorLabel.setBackground(bgColor.getColor());
  bgColorButton.addActionListener(new
      GeneralColorDialogListener(this,bgColor,bgColorLabel,
				 "Choose a Background Color"));
  c.gridx=1;
  c.gridy=3;
  c.fill=GridBagConstraints.NONE;
  gridbag.setConstraints(bgColorLabel,c);
  mainPanel.add(bgColorLabel);


  c.gridx=0;
  c.gridy=3;
  c.fill=GridBagConstraints.HORIZONTAL;
  gridbag.setConstraints(bgColorButton,c);
  mainPanel.add(bgColorButton);

  c.gridheight=1;
  c.fill=GridBagConstraints.NONE;

  JPanel labelTextPanel
      = new LabelTextPanel(nodeAttribs,localNodeLabelKey);
  c.gridx=0;
  c.gridy=4;
  gridbag.setConstraints(labelTextPanel,c);
  mainPanel.add(labelTextPanel);
  
  initializeShapeDefault();
  c.gridx=0;
  c.gridy=5;
  gridbag.setConstraints(shapeDefault,c);
  mainPanel.add(shapeDefault);

  initializeLineTypeDefault();
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

  initializeArrowDefault();
  c.gridx=0;
  c.gridy=8;
  gridbag.setConstraints(arrowDefault,c);
  mainPanel.add(arrowDefault);

  //////////////////////////////////////////////
  JPanel complexSubPanel = new JPanel();
  GridBagLayout complexSubPanelGridbag = new GridBagLayout(); 
  GridBagConstraints complexSubPanelConstraints = new GridBagConstraints();
  complexSubPanel.setLayout (complexSubPanelGridbag);

  Border complexSubPanelBorder = BorderFactory.createLineBorder (Color.black);
  Border complexSubPanelTitledBorder = 
      BorderFactory.createTitledBorder (complexSubPanelBorder,
					"Edge Color Mapping", 
					TitledBorder.CENTER, 
					TitledBorder.DEFAULT_POSITION);
  complexSubPanel.setBorder (complexSubPanelTitledBorder);

  //complexSubPanelConstraints.gridx=0;
  //complexSubPanelConstraints.gridy=0;
  //JLabel tempLabel = new JLabel("Define Mapping:");
  //complexSubPanelGridbag.setConstraints(tempLabel,complexSubPanelConstraints);
  //complexSubPanel.add(tempLabel);

  if(localEdgeKey==null) localEdgeKey = new MutableString("temp");
  edgeTextPanel
      = new EdgeTextPanel(edgeAttribs,aMapper,parentFrame,localEdgeKey);
  complexSubPanelConstraints.gridx=0;
  complexSubPanelConstraints.gridy=0;
  complexSubPanelGridbag.setConstraints(edgeTextPanel,complexSubPanelConstraints);
  complexSubPanel.add(edgeTextPanel);

  

  //////////////////////////////////////////////
  c.gridwidth = 2;
  c.gridx=0;
  c.gridy=9;
  gridbag.setConstraints(complexSubPanel,c);
  mainPanel.add(complexSubPanel);


  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  c.gridwidth = 1;
  c.gridx=0;
  c.gridy=10;
  gridbag.setConstraints(applyButton,c);
  mainPanel.add (applyButton);

  JButton cancelButton = new JButton ("Cancel");
  cancelButton.addActionListener (new CancelAction ());
  c.gridx=1;
  c.gridy=10;
  gridbag.setConstraints(cancelButton,c);
  mainPanel.add (cancelButton);


  setContentPane (mainPanel);
} // PopupDialog ctor


public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

    public void actionPerformed (ActionEvent e) {
	
	Object o;
	
	Color node = nColor.getColor();
	if(node != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR, node);
	Color bg = bgColor.getColor();
	if(bg != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.BG_COLOR, bg);
	Byte shape = (Byte)shapeDefault.getIconObject();
	if(shape != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_SHAPE, shape);
	LineType line = (LineType)lineTypeDefault.getIconObject();
	if(line != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.EDGE_LINETYPE, null);
	Integer size = sizeDefault.getInteger();
	if(size != null) {
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_HEIGHT, size);
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_WIDTH, size);
	}
	Color border = bColor.getColor();
	if(border != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_BORDER_COLOR, border);
	Arrow arrow = (Arrow)arrowDefault.getIconObject();
	if(arrow != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.EDGE_TARGET_DECORATION, arrow);
	
      /*
      EdgeArrowColor.removeThenAddEdgeColor(aMapper,"pp",ppColor.getColor());
      EdgeArrowColor.removeThenAddEdgeColor(aMapper,"pd",pdColor.getColor());
      */

      if(edgeTextPanel.getWhetherToUseTheMap()) {
	  Map m = edgeTextPanel.getMap();
	  if(m != null) {
	      aMapper.setAttributeMapEntry(VizMapperCategories.EDGE_COLOR,
					   localEdgeKey.getString(),
					   new DiscreteMapper(m));

	  }
      }
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

    /**
     *  For each icon-list of attributes, there is an IconPopupButton,
     *  initialized through a function called
     *     initialize<Whatever>Default()
     *  (several instances of this sort of function follow this comment).
     *
     *  Construction of an IconPopupButton requires a title for the button,
     *  a name of the attribute in question, two hashes for going from
     *  Object to String and back again, a list of ImageIcon's, a current
     *  Object that is selected, and a reference to this, the parent dialog.
     *
     *  Each different icon-list of attributes also depends on a few
     *  functions from MiscDialog.java.  These functions create the
     *  HashMaps and the list of ImageIcons.
     *
     *  Finally, the value in each of these IconPopupButton's is taken and
     *  used during ApplyAction's actionPerformed(), above.
     *
     */

    private void initializeArrowDefault() {
	int ns = ((Integer)aMapper.getDefaultValue(VizMapperCategories.NODE_HEIGHT)).intValue();
	Object currentArrow =  aMapper.getDefaultValue(VizMapperCategories.EDGE_TARGET_DECORATION);
	HashMap arrowToString = MiscDialog.getArrowToStringHashMap(ns);
	HashMap stringToArrow = MiscDialog.getStringToArrowHashMap(ns);
	ImageIcon [] icons = MiscDialog.getArrowIcons();
	arrowDefault =
	    new IconPopupButton ("Default Arrow",
				 "Arrow",
				 arrowToString,
				 stringToArrow,
				 icons,
				 currentArrow,
				 this);
    }


    private void initializeShapeDefault() {
	Object currentShape =  aMapper.getDefaultValue(VizMapperCategories.NODE_SHAPE);
	HashMap shapeToString = MiscDialog.getShapeByteToStringHashMap();
	HashMap stringToShape = MiscDialog.getStringToShapeByteHashMap();
	ImageIcon [] icons = MiscDialog.getShapeIcons();
	shapeDefault =
	    new IconPopupButton ("Default Node Shape",
				 "Node Shape",
				 shapeToString,
				 stringToShape,
				 icons,
				 currentShape,
				 this);
    }


    private void initializeLineTypeDefault() {
	Object currentLineType =  aMapper.getDefaultValue(VizMapperCategories.EDGE_LINETYPE);
	HashMap lineTypeToString = MiscDialog.getLineTypeToStringHashMap();
	HashMap stringToLineType = MiscDialog.getStringToLineTypeHashMap();
	ImageIcon [] icons = MiscDialog.getLineTypeIcons();
	lineTypeDefault =
	    new IconPopupButton ("Default Line Type",
				 "Line Type",
				 lineTypeToString,
				 stringToLineType,
				 icons,
				 currentLineType,
				 this);
    }
    
} // class VisualPropertiesDialog


