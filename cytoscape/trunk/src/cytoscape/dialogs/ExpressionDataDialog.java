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
import cytoscape.data.*;
import cytoscape.vizmap.*;
import cytoscape.dialogs.NewSlider;
import cytoscape.GraphObjAttributes;

//--------------------------------------------------------------------------------------
public class ExpressionDataDialog extends JDialog {

    int expressionConditionNumber;
    AttributeMapper aMapper;
    Node[] nodes;
    ExpressionData eData;
    GraphObjAttributes nAttrib;
    String[] condNames;
    Color nColor;
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

  JPanel mainPanel = new JPanel ();
  GridBagLayout gridbag = new GridBagLayout();   // see note below
  GridBagConstraints c = new GridBagConstraints();  // see note below
  mainPanel.setLayout (gridbag);


  condNames = eData.getConditionNames();

  ButtonGroup buttonGroup = new ButtonGroup ();
  int condNum;
  for(condNum=0; condNum<condNames.length; condNum++) {
      JRadioButton condButton = new JRadioButton (condNames[condNum]);
      condButton.addActionListener(new CondAction (condNum));
      buttonGroup.add (condButton);
      c.gridx = 0;
      c.gridy = condNum+1;
      gridbag.setConstraints(condButton,c);
      mainPanel.add (condButton);
  }


  JButton applyButton = new JButton ("Apply");
  applyButton.addActionListener (new ApplyAction ());
  c.gridx=0;
  c.gridy=0;
  gridbag.setConstraints(applyButton,c);
  mainPanel.add (applyButton);

  setContentPane (mainPanel);
} // PopupDialog ctor

//--------------------------------------------------------------------------------------
public class ApplyAction extends AbstractAction {
  ApplyAction () {
      super ("");
  }

  public void actionPerformed (ActionEvent e) {

      String condName = condNames[expressionConditionNumber];

      for (int i=0; i < nodes.length; i++) {
	  Node node = nodes [i];
	  String canName = nAttrib.getCanonicalName (node);
	  mRNAMeasurement mm =  eData.getMeasurement(canName,condName);
	  if(mm!=null)
	      nAttrib.add("expression",canName,mm.getSignificance());
	  //System.out.println(canName + " " + mm);
      }

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
	Color tempColor = JColorChooser.showDialog(ExpressionDataDialog.this,
						   "Choose Color",
						   nColor);
	if (tempColor != null)
	    nColor = tempColor;
    }
}

class BGColorDialogListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
	//Color tempColor = JColorChooser.showDialog(ExpressionDataDialog.this,
	//						   "Choose Color",
	//						   bgColor);
	//	if (tempColor != null)
	    //bgColor = tempColor;
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

} // class ExpressionDataDialog

