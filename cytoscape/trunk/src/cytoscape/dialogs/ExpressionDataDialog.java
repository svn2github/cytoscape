// ExpressionDataDialog.java
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

import y.base.Node;

import javax.swing.colorchooser.*;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.Iterator;

import cytoscape.data.*;
import cytoscape.vizmap.*;
import cytoscape.dialogs.NewSlider;
import cytoscape.GraphObjAttributes;

//--------------------------------------------------------------------------------------
public class ExpressionDataDialog extends JDialog {

    public static final int RSNA_R = 0;
    public static final int RSNA_S = 1;
    public static final int RSNA_N = 2;

    int expressionConditionNumber;
    int rsnState;
    AttributeMapper aMapper;
    Node[] nodes;
    ExpressionData eData;
    GraphObjAttributes nAttrib;
    String[] condNames;

    Color pt0ColorLT;
    Color pt0Color;
    Color pt1Color;
    Color pt2Color;
    Color pt2ColorGT;

    JLabel maxGTColor, maxColor, midColor, minColor, minLTColor;
    JButton minLTColorButton, minColorButton;
    JButton midColorButton;
    JButton maxColorButton, maxGTColorButton;
    JTextField minPtText, midPtText, maxPtText;
    double minPtNum, midPtNum, maxPtNum;

//--------------------------------------------------------------------------------------
public ExpressionDataDialog (Frame parentFrame,
			     String title,
			     ExpressionData expressionData,
			     Node [] incomingNodes,
			     AttributeMapper mapper,
			     GraphObjAttributes nodeAttributes)
{
  super (parentFrame, true);
  setTitle (title);
  aMapper = mapper;
  nodes = incomingNodes;
  eData = expressionData;
  nAttrib = nodeAttributes;
  initializeColors();

  JPanel mainPanel = new JPanel ();
  GridBagLayout gridbag = new GridBagLayout();
  GridBagConstraints c = new GridBagConstraints();
  mainPanel.setLayout (gridbag);

  /**
   * condPanel;  rsnPanel = ratio / significance / none Panel;
   * colorPanel; adPanel = apply / dismiss Panel;
   */
  JPanel condPanel = createCondPanel ();
  JPanel rsnPanel = createRSNPanel ();
  JPanel colorPanel = createColorPanel ();
  JPanel adPanel = createADPanel ();

  c.gridx=0;
  c.gridy=0;
  c.gridheight = 3;
  gridbag.setConstraints(condPanel,c);
  mainPanel.add (condPanel);

  c.gridx=1;
  c.gridy=0;
  c.gridheight = 1;
  gridbag.setConstraints(rsnPanel,c);
  mainPanel.add (rsnPanel);

  c.gridx=1;
  c.gridy=1;
  c.gridheight = 1;
  gridbag.setConstraints(colorPanel,c);
  mainPanel.add (colorPanel);

  c.gridx=1;
  c.gridy=2;
  c.gridheight = 1;
  gridbag.setConstraints(adPanel,c);
  mainPanel.add (adPanel);
  

  setContentPane (mainPanel);
} // PopupDialog ctor


    private void initializeColors() {
	pt0ColorLT = getMinLTColor();
	pt0Color = getMinColor();
	pt1Color = getMidColor();
	pt2Color = getMaxColor();
	pt2ColorGT = getMaxGTColor();
    }

//--------------------------------------------------------------------------------------
public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {


      /**
       * updating color mapper
       */
      ContinuousMapper cm =
	  (ContinuousMapper)
	  aMapper.getValueMapper(VizMapperCategories.NODE_FILL_COLOR);

      SortedMap valueMap = cm.getBoundaryRangeValuesMap();
      Iterator it = valueMap.keySet().iterator();

      Double doubleBVal = (Double)it.next();
      BoundaryRangeValues bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
      bvObj.lesserValue = pt0ColorLT;
      bvObj.equalValue = pt0Color;
      bvObj.greaterValue = pt0Color;
      valueMap.put(doubleBVal,bvObj);

      doubleBVal = (Double)it.next();
      bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
      bvObj.lesserValue = pt1Color;
      bvObj.equalValue = pt1Color;
      bvObj.greaterValue = pt1Color;
      valueMap.put(doubleBVal,bvObj);

      doubleBVal = (Double)it.next();
      bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
      bvObj.lesserValue = pt2Color;
      bvObj.equalValue = pt2Color;
      bvObj.greaterValue = pt2ColorGT;
      valueMap.put(doubleBVal,bvObj);

      /**
       * updating which condition is displayed on the nodes' "expression"
       */
      String condName = condNames[expressionConditionNumber];

      for (int i=0; i < nodes.length; i++) {
	  Node node = nodes [i];
	  String canName = nAttrib.getCanonicalName (node);
	  mRNAMeasurement mm =  eData.getMeasurement(canName,condName);
	  if(mm!=null) {
	      if(rsnState==RSNA_R)
		  nAttrib.add("expression",canName,mm.getRatio());
	      else if (rsnState==RSNA_S) 
		  nAttrib.add("expression",canName,mm.getSignificance());
	      //else
		  // this would be ideal: nAttrib.remove("expression",canName);
	  }
      }

      ExpressionDataDialog.this.dispose ();

  }

} // QuitAction
//--------------------------------------------------------------------------------------
public class DismissAction extends AbstractAction {
  DismissAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {
      ExpressionDataDialog.this.dispose ();
  }

} // QuitAction
//-----------------------------------------------------------------------------

public class CondAction extends AbstractAction {
    private int condNum;
    CondAction (int cNum) {
	super ("");
	condNum = cNum;
    }
    public void actionPerformed (ActionEvent e) {
	expressionConditionNumber = condNum;
    }
}


public class RSNAction extends AbstractAction {
    private int rsnNum;
    RSNAction (int num) {
	super ("");
	rsnNum = num;
    }
    public void actionPerformed (ActionEvent e) {
	rsnState = rsnNum;
    }
}

    

class SpawnMinLTColorDialogListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
	// Args are parent component, title, initial color
	Color tempColor = JColorChooser.showDialog(ExpressionDataDialog.this,
						   "Choose Sub-Min Color",
						   pt0ColorLT);
	if (tempColor != null) {
	    pt0ColorLT = tempColor;
	    minLTColor.setBackground(pt0ColorLT);
	}
    }
}

class SpawnMinColorDialogListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
	// Args are parent component, title, initial color
	Color tempColor = JColorChooser.showDialog(ExpressionDataDialog.this,
						   "Choose Min Color",
						   pt0Color);
	if (tempColor != null) {
	    pt0Color = tempColor;
	    minColor.setBackground(pt0Color);
	}
    }
}

class SpawnMidColorDialogListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
	// Args are parent component, title, initial color
	Color tempColor = JColorChooser.showDialog(ExpressionDataDialog.this,
						   "Choose Mid Color",
						   pt1Color);
	if (tempColor != null) {
	    pt1Color = tempColor;
	    midColor.setBackground(pt1Color);
	}
    }
}

class SpawnMaxColorDialogListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
	// Args are parent component, title, initial color
	Color tempColor = JColorChooser.showDialog(ExpressionDataDialog.this,
						   "Choose Sub-Max Color",
						   pt2Color);
	if (tempColor != null) {
	    pt2Color = tempColor;
	    maxColor.setBackground(pt2Color);
	}
    }
}

class SpawnMaxGTColorDialogListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
	// Args are parent component, title, initial color
	Color tempColor = JColorChooser.showDialog(ExpressionDataDialog.this,
						   "Choose Max Color",
						   pt2ColorGT);
	if (tempColor != null) {
	    pt2ColorGT = tempColor;
	    maxGTColor.setBackground(pt2ColorGT);
	}
    }
}

private JPanel createCondPanel() {

  JPanel condPanel = new JPanel ();
  GridBagLayout condGridbag = new GridBagLayout();
  GridBagConstraints condConstraints = new GridBagConstraints();
  condPanel.setLayout (condGridbag);

  condNames = eData.getConditionNames();

  ButtonGroup buttonGroup = new ButtonGroup ();
  int condNum;
  for(condNum=0; condNum<condNames.length; condNum++) {
      JRadioButton condButton = new JRadioButton (condNames[condNum]);
      condButton.addActionListener(new CondAction (condNum));
      buttonGroup.add (condButton);
      condConstraints.gridx = 0;
      condConstraints.gridy = condNum+1;
      condGridbag.setConstraints(condButton,condConstraints);
      condPanel.add (condButton);
  }
  return condPanel;

}

private JPanel createRSNPanel() {
  JPanel rsnPanel = new JPanel ();
  GridBagLayout rsnGridbag = new GridBagLayout();
  GridBagConstraints rsnConstraints = new GridBagConstraints();
  rsnPanel.setLayout (rsnGridbag);

  ButtonGroup buttonGroup = new ButtonGroup ();
  JRadioButton rButton = new JRadioButton ("Expression");
  rButton.addActionListener(new RSNAction (RSNA_R));
  JRadioButton sButton = new JRadioButton ("Significance");
  sButton.addActionListener(new RSNAction (RSNA_S));
  JRadioButton nButton = new JRadioButton ("None");
  nButton.addActionListener(new RSNAction (RSNA_N));

  buttonGroup.add (rButton);
  buttonGroup.add (sButton);
  buttonGroup.add (nButton);

  rsnConstraints.gridx = 0;
  rsnConstraints.gridy = 0;
  rsnGridbag.setConstraints(rButton,rsnConstraints);
  rsnPanel.add (rButton);

  rsnConstraints.gridx = 1;
  rsnConstraints.gridy = 0;
  rsnGridbag.setConstraints(sButton,rsnConstraints);
  rsnPanel.add (sButton);

  rsnConstraints.gridx = 2;
  rsnConstraints.gridy = 0;
  rsnGridbag.setConstraints(nButton,rsnConstraints);
  rsnPanel.add (nButton);
  
  return rsnPanel;
}




private JPanel createColorPanel() {
  JPanel colorPanel = new JPanel ();
  GridBagLayout gridbag = new GridBagLayout();
  GridBagConstraints constraints = new GridBagConstraints();
  colorPanel.setLayout (gridbag);

  initColorButtonsAndColors();

  int yPos=0;
  insertColorItem(yPos, gridbag, constraints,
		  colorPanel, minLTColor, minLTColorButton);
  yPos++;
  insertColorItem(yPos, gridbag, constraints,
		  colorPanel, minColor, minColorButton);
  yPos++;
  insertColorItem(yPos, gridbag, constraints,
		  colorPanel, midColor, midColorButton);
  yPos++;
  insertColorItem(yPos, gridbag, constraints,
		  colorPanel, maxColor, maxColorButton);
  yPos++;
  insertColorItem(yPos, gridbag, constraints,
		  colorPanel, maxGTColor, maxGTColorButton);
  
  return colorPanel;
}

private void insertColorItem(int yPos,
			     GridBagLayout gridbag,
			     GridBagConstraints constraints,
			     JPanel colorPanel,
			     JLabel colorLabel,
			     JButton colorButton) {
  constraints.gridx=0;
  constraints.gridy=yPos;
  gridbag.setConstraints(colorButton,constraints);
  colorPanel.add (colorButton);
  // the label
  constraints.gridx=1;
  constraints.gridy=yPos;
  gridbag.setConstraints(colorLabel,constraints);
  colorPanel.add (colorLabel);
    
}
private void initColorButtonsAndColors() {

    minLTColorButton = new JButton ("min LT Color");
    minLTColorButton.addActionListener (new SpawnMinLTColorDialogListener ());
    minLTColor = new JLabel("    ");
    minLTColor.setOpaque(true);
    minLTColor.setBackground(pt0ColorLT);
  
    minColorButton = new JButton ("min GT Color");
    minColorButton.addActionListener (new SpawnMinColorDialogListener ());
    minColor = new JLabel("    ");
    minColor.setOpaque(true);
    minColor.setBackground(pt0Color);

    midColorButton = new JButton ("mid Color");
    midColorButton.addActionListener (new SpawnMidColorDialogListener ());
    midColor = new JLabel("    ");
    midColor.setOpaque(true);
    midColor.setBackground(pt1Color);

    maxColorButton = new JButton ("max LT Color");
    maxColorButton.addActionListener (new SpawnMaxColorDialogListener ());
    maxColor = new JLabel("    ");
    maxColor.setOpaque(true);
    maxColor.setBackground(pt2Color);

    maxGTColorButton = new JButton ("max GT Color");
    maxGTColorButton.addActionListener (new SpawnMaxGTColorDialogListener ());
    maxGTColor = new JLabel("    ");
    maxGTColor.setOpaque(true);
    maxGTColor.setBackground(pt2ColorGT);
}

private JPanel createADPanel() {
  JPanel adPanel = new JPanel ();
  GridBagLayout adGridbag = new GridBagLayout();
  GridBagConstraints adConstraints = new GridBagConstraints();
  adPanel.setLayout (adGridbag);

  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  adConstraints.gridx=0;
  adConstraints.gridy=0;
  adGridbag.setConstraints(applyButton,adConstraints);
  adPanel.add (applyButton);

  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction ());
  adConstraints.gridx=1;
  adConstraints.gridy=0;
  adGridbag.setConstraints(dismissButton,adConstraints);
  adPanel.add (dismissButton);

  return adPanel;
}





    private Color getMinLTColor() {
	Color tempColor;

	ContinuousMapper cm =
	    (ContinuousMapper)
	    aMapper.getValueMapper(VizMapperCategories.NODE_FILL_COLOR);

	//Map valueMap = cm.getValueMap();
	SortedMap valueMap = cm.getBoundaryRangeValuesMap();
	Iterator i = valueMap.keySet().iterator();

	//for ( ; i.hasNext(); ) {
	Double doubleBVal = (Double)i.next();
	//}
	
	BoundaryRangeValues bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
	if(bvObj!=null) {
	    tempColor = (Color)bvObj.lesserValue;
	    
	    if(tempColor != null)
		return tempColor;
	    else {
		return new Color(255,255,0);
	    }
	}
	else {
	    return new Color(255,255,0);
	}
    }

    private Color getMinColor() {
	Color tempColor;

	ContinuousMapper cm =
	    (ContinuousMapper)
	    aMapper.getValueMapper(VizMapperCategories.NODE_FILL_COLOR);

	//Map valueMap = cm.getValueMap();
	SortedMap valueMap = cm.getBoundaryRangeValuesMap();
	Iterator i = valueMap.keySet().iterator();

	//for ( ; i.hasNext(); ) {
	Double doubleBVal = (Double)i.next();
	//}
	
	BoundaryRangeValues bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
	if(bvObj!=null) {
	    tempColor = (Color)bvObj.greaterValue;
	    
	    if(tempColor != null)
		return tempColor;
	    else {
		return new Color(255,255,0);
	    }
	}
	else {
	    return new Color(255,255,0);
	}
    }


    private Color getMidColor() {
	Color tempColor;

	ContinuousMapper cm =
	    (ContinuousMapper)
	    aMapper.getValueMapper(VizMapperCategories.NODE_FILL_COLOR);

	//Map valueMap = cm.getValueMap();
	SortedMap valueMap = cm.getBoundaryRangeValuesMap();
	Iterator i = valueMap.keySet().iterator();

	//for ( ; i.hasNext(); ) {
	Double doubleBVal = (Double)i.next();
	doubleBVal = (Double)i.next();
	//}
	
	BoundaryRangeValues bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
	if(bvObj!=null) {
	    tempColor = (Color)bvObj.greaterValue;
	    
	    if(tempColor != null)
		return tempColor;
	    else {
		return new Color(255,255,0);
	    }
	}
	else {
	    return new Color(255,255,0);
	}
    }

    private Color getMaxColor() {
	Color tempColor;

	ContinuousMapper cm =
	    (ContinuousMapper)
	    aMapper.getValueMapper(VizMapperCategories.NODE_FILL_COLOR);

	//Map valueMap = cm.getValueMap();
	SortedMap valueMap = cm.getBoundaryRangeValuesMap();
	Iterator i = valueMap.keySet().iterator();

	//for ( ; i.hasNext(); ) {
	Double doubleBVal = (Double)i.next();
	doubleBVal = (Double)i.next();
	doubleBVal = (Double)i.next();
	//}
	
	BoundaryRangeValues bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
	if(bvObj!=null) {
	    tempColor = (Color)bvObj.lesserValue;
	    
	    if(tempColor != null)
		return tempColor;
	    else {
		return new Color(255,255,0);
	    }
	}
	else {
	    return new Color(255,255,0);
	}
    }

    private Color getMaxGTColor() {
	Color tempColor;

	ContinuousMapper cm =
	    (ContinuousMapper)
	    aMapper.getValueMapper(VizMapperCategories.NODE_FILL_COLOR);

	//Map valueMap = cm.getValueMap();
	SortedMap valueMap = cm.getBoundaryRangeValuesMap();
	Iterator i = valueMap.keySet().iterator();

	//for ( ; i.hasNext(); ) {
	Double doubleBVal = (Double)i.next();
	doubleBVal = (Double)i.next();
	doubleBVal = (Double)i.next();
	//}
	
	BoundaryRangeValues bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
	if(bvObj!=null) {
	    tempColor = (Color)bvObj.greaterValue;
	    
	    if(tempColor != null)
		return tempColor;
	    else {
		return new Color(255,255,0);
	    }
	}
	else {
	    return new Color(255,255,0);
	}
    }



} // class ExpressionDataDialog

