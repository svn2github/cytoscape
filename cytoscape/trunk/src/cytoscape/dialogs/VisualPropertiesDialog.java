// VisualPropertiesDialog.java

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
import cytoscape.util.MutableBool;
import cytoscape.dialogs.MiscGB;
import cytoscape.dialogs.EdgeTextPanel;
import cytoscape.dialogs.IntegerEntryField;
import cytoscape.dialogs.JointIntegerEntry;
//--------------------------------------------------------------------------------------
public class VisualPropertiesDialog extends JDialog {
    static Map backupMap=null;
    static String backupKey=null;
    static boolean useMapping=true;
    IconPopupButton shapeDefault;
    IconPopupButton lineTypeDefault;
    IconPopupButton arrowDefault;
    JointIntegerEntry sizeDefaults;
    AttributeMapper aMapper;
    Frame parentFrame;
    MutableColor nColor;
    MutableColor bColor;
    MutableColor bgColor;
    MutableColor eColor;
    MutableString localNodeLabelKey;
    MutableString parentNodeLabelKey;
    MutableString localEdgeKey;
    MutableBool applied;
    EdgeTextPanel edgeTextPanel;
//--------------------------------------------------------------------------------------
public VisualPropertiesDialog (Frame parentFrame,
			       String title,
			       AttributeMapper mapper,
			       GraphObjAttributes nodeAttribs,
			       GraphObjAttributes edgeAttribs,
			       MutableString nodeLabelKey,
			       MutableBool applied)
{
  super (parentFrame, true);
  setTitle (title);
  this.parentFrame = parentFrame;

  aMapper = mapper;
  nColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_FILL_COLOR));
  bColor = new MutableColor(getBasicColor(VizMapperCategories.NODE_BORDER_COLOR));
  bgColor = new MutableColor(getBasicColor(VizMapperCategories.BG_COLOR));
  eColor = new MutableColor(getBasicColor(VizMapperCategories.EDGE_COLOR));
  localNodeLabelKey = new MutableString(nodeLabelKey.getString());
  parentNodeLabelKey = nodeLabelKey;
  this.applied = applied;

  GridBagGroup gbg = new GridBagGroup();
  JPanel mainPanel = gbg.panel;
  MiscGB.pad(gbg.constraints,5,5);
  GridBagGroup defGBG = new GridBagGroup("Defaults");
  MiscGB.pad(defGBG.constraints,5,5);
  int yPos=0;
  int yDef=0;

  JLabel colorLabel = MiscGB.createColorLabel(nColor.getColor());
  JButton colorButton = MiscGB.buttonAndColor(this,nColor,colorLabel,"Node Color");
  MiscGB.insert(defGBG,colorButton,0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  MiscGB.insert(defGBG,colorLabel,1,yDef);
  yDef++;

  JLabel borderColorLabel = MiscGB.createColorLabel(bColor.getColor());
  JButton borderColorButton = MiscGB.buttonAndColor(this,bColor,borderColorLabel,
						    "Node Border Color");
  MiscGB.insert(defGBG,borderColorLabel,1,yDef);
  MiscGB.insert(defGBG,borderColorButton,0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  yDef++;

  JLabel bgColorLabel = MiscGB.createColorLabel(bgColor.getColor());
  JButton bgColorButton = MiscGB.buttonAndColor(this,bgColor,bgColorLabel,
						    "Background Color");
  MiscGB.insert(defGBG,bgColorLabel,1,yDef);
  MiscGB.insert(defGBG,bgColorButton,0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  yDef++;

  JLabel eColorLabel = MiscGB.createColorLabel(eColor.getColor());
  JButton eColorButton = MiscGB.buttonAndColor(this,eColor,eColorLabel,
						    "Edge Color");
  MiscGB.insert(defGBG,eColorLabel,1,yDef);
  MiscGB.insert(defGBG,eColorButton,0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  yDef++;

  sizeDefaults =
      new JointIntegerEntry
	  ("Node","Height","Width",
	   (int)(((Double)aMapper.getDefaultValue(VizMapperCategories.NODE_HEIGHT)).doubleValue()),
	   (int)(((Double)aMapper.getDefaultValue(VizMapperCategories.NODE_WIDTH)).doubleValue()),
	   500,500);
  //	   ((Integer)aMapper.getDefaultValue(VizMapperCategories.NODE_HEIGHT)).intValue(),
  //	   ((Integer)aMapper.getDefaultValue(VizMapperCategories.NODE_WIDTH)).intValue(),

  MiscGB.insert(defGBG,sizeDefaults.getConstraintLabel(),0,yDef);
  MiscGB.insert(defGBG,sizeDefaults.getConstraintBox(),1,yDef);
  yDef++;
  MiscGB.insert(defGBG,sizeDefaults.getLabel("Height"),0,yDef);
  MiscGB.insert(defGBG,sizeDefaults.getField("Height"),1,yDef);
  yDef++;
  MiscGB.insert(defGBG,sizeDefaults.getLabel("Width"),0,yDef);
  MiscGB.insert(defGBG,sizeDefaults.getField("Width"),1,yDef);
  yDef++;

  initializeShapeDefault();
  MiscGB.insert(defGBG,shapeDefault.getButton(),0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  MiscGB.insert(defGBG,shapeDefault.getLabel(),1,yDef);
  yDef++;

  initializeLineTypeDefault();
  MiscGB.insert(defGBG,lineTypeDefault.getButton(),0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  MiscGB.insert(defGBG,lineTypeDefault.getLabel(),1,yDef);
  yDef++;

  initializeArrowDefault();
  MiscGB.insert(defGBG,arrowDefault.getButton(),0,yDef,1,1,GridBagConstraints.HORIZONTAL);
  MiscGB.insert(defGBG,arrowDefault.getLabel(),1,yDef);
  yDef++;

  //////////////////////////////////////////////
  MiscGB.insert(gbg,defGBG.panel,0,yPos,2,1,GridBagConstraints.HORIZONTAL);
  yPos++;

  //////////////////////////////////////////////
  GridBagGroup labelGBG = new GridBagGroup("Node Label Mapping");
  JPanel labelTextPanel
      = new LabelTextPanel(nodeAttribs,localNodeLabelKey);
  MiscGB.insert(labelGBG,labelTextPanel,0,0);
  MiscGB.insert(gbg,labelGBG.panel,0,yPos,2,1,GridBagConstraints.HORIZONTAL);
  yPos++;

  //////////////////////////////////////////////
  GridBagGroup edgeGBG = new GridBagGroup("Edge Color Mapping");
  if(localEdgeKey==null) localEdgeKey = new MutableString("temp");
  edgeTextPanel
      = new EdgeTextPanel(edgeAttribs,aMapper,parentFrame,localEdgeKey,
			  backupMap, backupKey, useMapping);
  MiscGB.insert(edgeGBG,edgeTextPanel,0,0);
  MiscGB.insert(gbg,edgeGBG.panel,0,yPos,2,1,GridBagConstraints.HORIZONTAL);
  yPos++;

  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  MiscGB.insert(gbg,applyButton,0,yPos);
  JButton cancelButton = new JButton ("Cancel");
  cancelButton.addActionListener (new CancelAction ());
  MiscGB.insert(gbg,cancelButton,1,yPos);
  yPos++;

  setContentPane (mainPanel);
} // PopupDialog ctor

public class ApplyAction extends AbstractAction {
    ApplyAction () {
	super ("");
    }
    
    public void actionPerformed (ActionEvent event) {
	Object o;
	Color node = nColor.getColor();
	if(node != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_FILL_COLOR, node);
	Color bg = bgColor.getColor();
	if(bg != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.BG_COLOR, bg);
	Color ec = eColor.getColor();
	if(ec != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.EDGE_COLOR, ec);
	Byte shape = (Byte)shapeDefault.getIconObject();
	if(shape != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_SHAPE, shape);
	LineType line = (LineType)lineTypeDefault.getIconObject();
	if(line != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.EDGE_LINETYPE, line);
	Integer height = sizeDefaults.getInteger("Height");
	if(height != null) {
	    Double heightD = new Double(height.intValue());
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_HEIGHT, heightD);
	}
	Integer width = sizeDefaults.getInteger("Width");
	if(width != null) {
	    Double widthD = new Double(width.intValue());
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_WIDTH, widthD);
	}
	Color border = bColor.getColor();
	if(border != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.NODE_BORDER_COLOR, border);
	Arrow arrow = (Arrow)arrowDefault.getIconObject();
	if(arrow != null)
	    o = aMapper.setDefaultValue(VizMapperCategories.EDGE_TARGET_DECORATION, arrow);

	Map m = edgeTextPanel.getMap();
	if(m != null) {
	    backupMap = m;
	    backupKey = localEdgeKey.getString();
	}
	if(edgeTextPanel.didStateChange()) {
	    useMapping = edgeTextPanel.useMappingGenerally();
	    if(useMapping && edgeTextPanel.getWhetherToUseTheMap()) {
		if(m != null) {
		    aMapper.setAttributeMapEntry(VizMapperCategories.EDGE_COLOR,
						 localEdgeKey.getString(),
						 new DiscreteMapper(m));
		}
		edgeTextPanel.updateMapperScalableArrows();
	    }
	    else if(useMapping) {
		if(backupMap != null) {
		    aMapper.setAttributeMapEntry(VizMapperCategories.EDGE_COLOR,
						 backupKey,
						 new DiscreteMapper(backupMap));
		}
	    }
	    else {
		aMapper.removeAttributeMapEntry(VizMapperCategories.EDGE_COLOR);
	    }
	}

	parentNodeLabelKey.setString(localNodeLabelKey.getString());
	applied.setBool(true);
	VisualPropertiesDialog.this.dispose ();
    }
    
} // ApplyAction

public class CancelAction extends AbstractAction {
  CancelAction () { super (""); }
  public void actionPerformed (ActionEvent e) {
      applied.setBool(false);
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
	int ns = (int)((Double)aMapper.getDefaultValue(VizMapperCategories.NODE_HEIGHT)).doubleValue();
	Object currentArrow =  aMapper.getDefaultValue(VizMapperCategories.EDGE_TARGET_DECORATION);

	HashMap arrowToString = MiscDialog.getArrowToStringHashMap(ns);
	HashMap stringToArrow = MiscDialog.getStringToArrowHashMap(ns);

	MiscDialog iconLoader = new MiscDialog();
	ImageIcon [] icons = iconLoader.getArrowIcons();
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

	MiscDialog iconLoader = new MiscDialog();
	ImageIcon [] icons = iconLoader.getShapeIcons();
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

	MiscDialog iconLoader = new MiscDialog();
	ImageIcon [] icons = iconLoader.getLineTypeIcons();
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


