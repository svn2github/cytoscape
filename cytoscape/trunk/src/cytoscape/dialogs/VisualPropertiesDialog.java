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
  gbcPad(c,5,5);
  mainPanel.setLayout (gridbag);

  BorderedPanel defaultBP = new BorderedPanel("Defaults");
  JPanel defaultPanel = defaultBP.getPanel();
  GridBagLayout defaultLayout = defaultBP.getLayout();
  GridBagConstraints defaultC = defaultBP.getConstraints();
  gbcPad(defaultC,5,5);


  JButton colorButton = new JButton("Node Color");
  JLabel colorLabel = new JLabel("    ");
  colorLabel.setOpaque(true);
  colorLabel.setBackground(nColor.getColor());
  colorButton.addActionListener(new
      GeneralColorDialogListener(this,nColor,colorLabel,
				 "Choose a Node Color"));
  gbcSet(defaultC,0,0,1,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(defaultPanel,colorButton,defaultLayout,defaultC);
  gbcSet(defaultC,1,0);
  gblPanelInsert(defaultPanel,colorLabel,defaultLayout,defaultC);

  JButton borderColorButton = new JButton("Node Border Color");
  JLabel borderColorLabel = new JLabel("    ");
  borderColorLabel.setOpaque(true);
  borderColorLabel.setBackground(bColor.getColor());
  borderColorButton.addActionListener(new
      GeneralColorDialogListener(this,bColor,borderColorLabel,
				 "Choose a Node Border Color"));
  gbcSet(defaultC,1,2);
  gblPanelInsert(defaultPanel,borderColorLabel,defaultLayout,defaultC);

  gbcSet(defaultC,0,2,1,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(defaultPanel,borderColorButton,defaultLayout,defaultC);

  JButton bgColorButton
      = new JButton("Background Color");
  JLabel bgColorLabel = new JLabel("    ");
  bgColorLabel.setOpaque(true);
  bgColorLabel.setBackground(bgColor.getColor());
  bgColorButton.addActionListener(new
      GeneralColorDialogListener(this,bgColor,bgColorLabel,
				 "Choose a Background Color"));
  gbcSet(defaultC,1,3);
  gblPanelInsert(defaultPanel,bgColorLabel,defaultLayout,defaultC);

  gbcSet(defaultC,0,3,1,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(defaultPanel,bgColorButton,defaultLayout,defaultC);

  sizeDefault = 
      new IntegerEntryField
	  ("Node Size",
	   ((Integer)aMapper.getDefaultValue(VizMapperCategories.NODE_HEIGHT)).intValue(),
	   500);
  gbcSet(defaultC,0,4);
  gblPanelInsert(defaultPanel,sizeDefault.getLabel(),defaultLayout,defaultC);
  gbcSet(defaultC,1,4);
  gblPanelInsert(defaultPanel,sizeDefault.getField(),defaultLayout,defaultC);

  initializeShapeDefault();
  gbcSet(defaultC,0,5,1,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(defaultPanel,shapeDefault.getButton(),defaultLayout,defaultC);
  gbcSet(defaultC,1,5);
  gblPanelInsert(defaultPanel,shapeDefault.getLabel(),defaultLayout,defaultC);

  initializeLineTypeDefault();
  gbcSet(defaultC,0,6,1,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(defaultPanel,lineTypeDefault.getButton(),defaultLayout,defaultC);
  gbcSet(defaultC,1,6);
  gblPanelInsert(defaultPanel,lineTypeDefault.getLabel(),defaultLayout,defaultC);

  initializeArrowDefault();
  gbcSet(defaultC,0,7,1,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(defaultPanel,arrowDefault.getButton(),defaultLayout,defaultC);
  gbcSet(defaultC,1,7);
  gblPanelInsert(defaultPanel,arrowDefault.getLabel(),defaultLayout,defaultC);

  //////////////////////////////////////////////
  gbcSet(c,0,0,2,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(mainPanel,defaultPanel,gridbag,c);

  //////////////////////////////////////////////
  BorderedPanel labelBP = new BorderedPanel("Node Label Mapping");
  JPanel labelPanel = labelBP.getPanel();
  GridBagLayout labelLayout = labelBP.getLayout();
  GridBagConstraints labelConstraints = labelBP.getConstraints();

  JPanel labelTextPanel
      = new LabelTextPanel(nodeAttribs,localNodeLabelKey);
  gbcSet(labelConstraints,0,0);
  gblPanelInsert(labelPanel,labelTextPanel,labelLayout,labelConstraints);
  gbcSet(c,0,8,2,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(mainPanel,labelPanel,gridbag,c);

  //////////////////////////////////////////////
  BorderedPanel edgeBP = new BorderedPanel("Edge Color Mapping");
  JPanel edgePanel = edgeBP.getPanel();
  GridBagLayout edgeLayout = edgeBP.getLayout();
  GridBagConstraints edgeConstraints = edgeBP.getConstraints();
  
  if(localEdgeKey==null) localEdgeKey = new MutableString("temp");
  edgeTextPanel
      = new EdgeTextPanel(edgeAttribs,aMapper,parentFrame,localEdgeKey);
  gbcSet(edgeConstraints,0,0);
  gblPanelInsert(edgePanel,edgeTextPanel,edgeLayout,edgeConstraints);
  gbcSet(c,0,9,2,1,GridBagConstraints.HORIZONTAL);
  gblPanelInsert(mainPanel,edgePanel,gridbag,c);

  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  gbcSet(c,0,10);
  gblPanelInsert(mainPanel,applyButton,gridbag,c);

  JButton cancelButton = new JButton ("Cancel");
  cancelButton.addActionListener (new CancelAction ());
  gbcSet(c,1,10);
  gblPanelInsert(mainPanel,cancelButton,gridbag,c);

  setContentPane (mainPanel);
} // PopupDialog ctor

    // sets GridBagConstraints.
    private void gbcPad(GridBagConstraints c, int padx, int pady) {
	c.ipadx = padx;	c.ipady = pady;
    }
    private void gbcSet(GridBagConstraints c, int x, int y, int w, int h, int f) {
	c.gridx = x;	c.gridy = y;
	c.gridwidth = w;	c.gridheight = h;
	c.fill = f;
    }
    private void gbcSet(GridBagConstraints c, int x, int y) {
	gbcSet(c,x,y,1,1,GridBagConstraints.NONE);
    }

    // inserts a component into a panel with a GridBagLayout.
    private void gblPanelInsert (JPanel panel,
				 Component comp,
				 GridBagLayout bag,
				 GridBagConstraints c) {
	if(bag==null) System.out.println("bag is null");
	if(comp==null) System.out.println("comp is null");
	if(c==null) System.out.println("c is null");
	if(panel==null) System.out.println("panel is null");
	bag.setConstraints(comp,c);
	panel.add(comp);
    }

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

public class BorderedPanel {
    JPanel panel;
    GridBagLayout layout;
    GridBagConstraints constraints;
    BorderedPanel(String title) {
	panel = new JPanel();
	layout = new GridBagLayout(); 
	constraints = new GridBagConstraints();
	panel.setLayout (layout);
  
	Border border = BorderFactory.createLineBorder (Color.black);
	Border titledBorder = 
	    BorderFactory.createTitledBorder (border,
					      title,
					      TitledBorder.CENTER, 
					      TitledBorder.DEFAULT_POSITION);
	panel.setBorder (titledBorder);
    }
    public JPanel getPanel() { return panel; }
    public GridBagLayout getLayout() { return layout; }
    public GridBagConstraints getConstraints() { return constraints; }
}

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
	    new IconPopupButton ("Arrow",
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
	    new IconPopupButton ("Node Shape",
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
	    new IconPopupButton ("Line Type",
				 "Line Type",
				 lineTypeToString,
				 stringToLineType,
				 icons,
				 currentLineType,
				 this);
    }


} // class VisualPropertiesDialog


