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

    public static final int LESSER_RANGE = 0;
    public static final int EQUAL_RANGE = 1;
    public static final int GREATER_RANGE = 2;

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
  initializePoints();

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
	pt0ColorLT = getPointColor(0,LESSER_RANGE);
	pt0Color = getPointColor(0,GREATER_RANGE);
	pt1Color = getPointColor(1,GREATER_RANGE);
	pt2Color = getPointColor(2,LESSER_RANGE);
	pt2ColorGT = getPointColor(2,GREATER_RANGE);
    }

    private void initializePoints() {
	ContinuousMapper cm =
	    (ContinuousMapper)
	    aMapper.getValueMapper(VizMapperCategories.NODE_FILL_COLOR);
	
	SortedMap valueMap = cm.getBoundaryRangeValuesMap();
	Iterator i = valueMap.keySet().iterator();

	Double doubleBVal = (Double)i.next();
	minPtNum = doubleBVal.doubleValue();
	doubleBVal = (Double)i.next();
	midPtNum = doubleBVal.doubleValue();
	doubleBVal = (Double)i.next();
	maxPtNum = doubleBVal.doubleValue();
	
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

      Double doubleBVal0 = (Double)it.next();
      BoundaryRangeValues bvObj0 = (BoundaryRangeValues)valueMap.get(doubleBVal0);

      Double doubleBVal1 = (Double)it.next();
      BoundaryRangeValues bvObj1 = (BoundaryRangeValues)valueMap.get(doubleBVal1);

      Double doubleBVal2 = (Double)it.next();
      BoundaryRangeValues bvObj2 = (BoundaryRangeValues)valueMap.get(doubleBVal2);

      Double newDoubleBVal0 = new Double(minPtNum);
      bvObj0.lesserValue = pt0ColorLT;
      bvObj0.equalValue = pt0Color;
      bvObj0.greaterValue = pt0Color;

      Double newDoubleBVal1 = new Double(midPtNum);	
      bvObj1.lesserValue = pt1Color;
      bvObj1.equalValue = pt1Color;
      bvObj1.greaterValue = pt1Color;

      Double newDoubleBVal2 = new Double(maxPtNum);
      bvObj2.lesserValue = pt2Color;
      bvObj2.equalValue = pt2Color;
      bvObj2.greaterValue = pt2ColorGT;

      valueMap.remove(doubleBVal0);
      valueMap.remove(doubleBVal1);
      valueMap.remove(doubleBVal2);
      valueMap.put(newDoubleBVal0,bvObj0);
      valueMap.put(newDoubleBVal1,bvObj1);
      valueMap.put(newDoubleBVal2,bvObj2);

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
  initPointText();

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

  insertAllTextItems(gridbag, constraints, colorPanel);

  return colorPanel;
}

private void insertColorItem(int yPos,
			     GridBagLayout gridbag,
			     GridBagConstraints constraints,
			     JPanel colorPanel,
			     JLabel colorLabel,
			     JButton colorButton) {
  constraints.gridx=1;
  constraints.gridy=yPos;
  gridbag.setConstraints(colorButton,constraints);
  colorPanel.add (colorButton);
  // the label
  constraints.gridx=2;
  constraints.gridy=yPos;
  gridbag.setConstraints(colorLabel,constraints);
  colorPanel.add (colorLabel);
    
}

private void insertAllTextItems(GridBagLayout gridbag,
				GridBagConstraints constraints,
				JPanel colorPanel) {
  constraints.gridx=0;
  constraints.gridy=0;
  constraints.gridheight = 2;
  gridbag.setConstraints(minPtText,constraints);
  colorPanel.add (minPtText);

  constraints.gridx=0;
  constraints.gridy=2;
  constraints.gridheight = 1;
  gridbag.setConstraints(midPtText,constraints);
  colorPanel.add (midPtText);

  constraints.gridx=0;
  constraints.gridy=3;
  constraints.gridheight = 2;
  gridbag.setConstraints(maxPtText,constraints);
  colorPanel.add (maxPtText);

}
private void initPointText() {
     minPtText = new JTextField(Double.toString(minPtNum),6);
     midPtText = new JTextField(Double.toString(midPtNum),6);
     maxPtText = new JTextField(Double.toString(maxPtNum),6);

     PtListener listener = new PtListener ();

     minPtText.addFocusListener (listener);
     midPtText.addFocusListener (listener);
     maxPtText.addFocusListener (listener);
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

    private Color getPointColor(int point, int whichBoundaryRange) {
	Color tempColor;

	ContinuousMapper cm =
	    (ContinuousMapper)
	    aMapper.getValueMapper(VizMapperCategories.NODE_FILL_COLOR);

	//Map valueMap = cm.getValueMap();
	SortedMap valueMap = cm.getBoundaryRangeValuesMap();
	Iterator i = valueMap.keySet().iterator();

	Double doubleBVal = (Double)i.next();
	for (int j = 0; j<point; j++)
	    doubleBVal = (Double)i.next();
	
	BoundaryRangeValues bvObj = (BoundaryRangeValues)valueMap.get(doubleBVal);
	
	if(bvObj!=null) {

	    tempColor=null;
	    if(whichBoundaryRange == LESSER_RANGE)
		tempColor = (Color)bvObj.lesserValue;
	    else if(whichBoundaryRange == EQUAL_RANGE)
		tempColor = (Color)bvObj.equalValue;
	    else if(whichBoundaryRange == GREATER_RANGE)
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




class PtListener implements FocusListener { 
  public void focusGained (FocusEvent e) {
      validate();
  }
  public void focusLost (FocusEvent e) {
      validate();
  }
  private void validate() {
      String pt0t = minPtText.getText();
      String pt1t = midPtText.getText();
      String pt2t = maxPtText.getText();

      String pt0t2 = pt0t.replaceAll("[^0-9+.-]",""); // ditch all non-numeric
      String pt1t2 = pt1t.replaceAll("[^0-9+.-]",""); // ditch all non-numeric
      String pt2t2 = pt2t.replaceAll("[^0-9+.-]",""); // ditch all non-numeric

      if(pt0t2.length()==0) {
	  pt0t2 = Double.toString(minPtNum);
	  minPtText.setText(pt0t2);
      }
      if(pt1t2.length()==0) {
	  pt1t2 = Double.toString(midPtNum);
	  midPtText.setText(pt1t2);
      }
      if(pt2t2.length()==0) {
	  pt2t2 = Double.toString(maxPtNum);
	  maxPtText.setText(pt2t2);
      }

      try {
	  double newPt0 = Double.parseDouble(pt0t2);
	  if(newPt0>midPtNum) {
	      minPtNum = midPtNum;
	  }
	  else {
	      minPtNum = newPt0;
	  }
      }
      catch (NumberFormatException nfe) {
	  System.out.println("Not a double: " + pt0t2);
      }
      minPtText.setText(Double.toString(minPtNum));

      try {
	  double newPt1 = Double.parseDouble(pt1t2);
	  if(newPt1>maxPtNum) {
	      midPtNum = maxPtNum;
	  }
	  else {
	      midPtNum = newPt1;
	  }
      }
      catch (NumberFormatException nfe) {
	  System.out.println("Not a double: " + pt1t2);
      }
      midPtText.setText(Double.toString(midPtNum));

      try {
	  double newPt2 = Double.parseDouble(pt2t2);
	  maxPtNum = newPt2;
      }
      catch (NumberFormatException nfe) {
	  System.out.println("Not a double: " + pt2t2);
      }
      maxPtText.setText(Double.toString(maxPtNum));
  }

} // PtListener



} // class ExpressionDataDialog

