// EdgeTextPanel.java
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.ScrollPaneConstants;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import cytoscape.GraphObjAttributes;
import cytoscape.util.MutableString;
import cytoscape.util.MutableColor;
import cytoscape.vizmap.*;
import cytoscape.dialogs.MiscGB;
//----------------------------------------------------------------------------
public class EdgeTextPanel extends JPanel {

    String [] attributeNames;
    MutableString edgeKey;
    MutableString attribKey;
    JComboBox theBox;
    AttributeMapper aMapper;
    Frame parentFrame;
    Map theMap;
    String theMapKey;
    boolean useThisMap;

    public EdgeTextPanel (GraphObjAttributes edgeAttribs,
			  AttributeMapper aMapper,
			  Frame parentFrame,
			  MutableString writeHere)
    {
	super ();
	this.aMapper = aMapper;
	this.parentFrame = parentFrame;
	attributeNames = edgeAttribs.getAttributeNames ();
	edgeKey = writeHere;
	attribKey = new MutableString("");
	theMap = null;
	useThisMap=false;
	
	this.setLayout (new GridLayout(1,2,10,10));
	
	DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
	for (int i=0; i < attributeNames.length; i++)
	    boxModel.addElement(new String(attributeNames [i]));
	theBox = new JComboBox(boxModel);
	
	theBox.addActionListener(new BoxAction(edgeKey));
	if(boxModel.getSize()==1) {
	    theBox.setSelectedIndex(0);
	    edgeKey.setString((String)boxModel.getElementAt(0));
	}
	else
	    theBox.setSelectedItem(edgeKey.getString());
	
	this.add(theBox);
	
	JButton pickThisAttribute = new JButton ("Define Mapping");
	pickThisAttribute.addActionListener (new ColorToDiscreteListener());
	this.add(pickThisAttribute);
	
    }
    /** returns the map. */
    public Map getMap() {
	return theMap;
    }
    /** updates the colors on scalable arrows using EdgeArrowColor. */
    public void updateMapperScalableArrows() {
	/** first loop through the map, making a copy. */
	TreeMap tempSM = new TreeMap();
	Set allKeys = theMap.keySet();
	Iterator keyIter = allKeys.iterator();
	for(;keyIter.hasNext();) {
	    String keyString = (String)keyIter.next();
	    Color c = (Color)theMap.get(keyString);
	    tempSM.put(keyString,c);
	}
	/** then loop through the copy, using the EdgeArrowColor method. */
	allKeys = tempSM.keySet();
	keyIter = allKeys.iterator();
	for(;keyIter.hasNext();) {
	    String keyString = (String)keyIter.next();
	    Color c = (Color)theMap.get(keyString);
	    EdgeArrowColor.removeThenAddEdgeColor(aMapper,keyString,c);
	}
    }
    public boolean getWhetherToUseTheMap() {
	return useThisMap;
    }
    public void setWhetherToUseTheMap(boolean b) {
	useThisMap = b;
    }
    public class BoxAction extends AbstractAction {
	MutableString ms;
	public BoxAction(MutableString ms) {
	    super();
	    this.ms = ms;
	}
	public void actionPerformed(ActionEvent e) {
	    JComboBox jcb = (JComboBox)e.getSource();
	    ms.setString((String)jcb.getSelectedItem());
	    //System.out.println(ms.getString());
	}
    }
    
    public class ColorToDiscreteListener extends AbstractAction {
	public void actionPerformed (ActionEvent e) {
	    //System.out.println("define " + edgeKey.getString());
	    ColorToDiscreteDialog ctdDialog =
		new ColorToDiscreteDialog();
	}
    }
    
    public class MapStringListener extends AbstractAction {
	JLabel label;
	Map theMap;
	Component component;
	String key;
	String title;
	public MapStringListener(Component component,
				 Map theMap, String key,
				 JLabel label, String title) {
	    this.label = label;
	    this.theMap = theMap;
	    this.key = key;
	    this.component = component;
	    this.title = title;
	}
	public void actionPerformed (ActionEvent e) {
	    MutableColor mc = new MutableColor((Color)theMap.get(key));
	    GeneralColorDialogListener gcdl =
		new GeneralColorDialogListener(component,
					       mc,
					       label,
					       title);
	    gcdl.popup();
	    theMap.put(key,mc.getColor());
	}
    }
    
    public class ColorToDiscreteDialog extends JDialog {
	JPanel extScrollPanel;
	JPanel intScrollPanel;
	public ColorToDiscreteDialog() {
	    super(parentFrame, true);
	    setTitle("Colors for " + edgeKey.getString() + " types");
	    
	    intScrollPanel = new JPanel(new GridLayout(0,2));
	    String controllingAttribName =
		aMapper.getControllingDomainAttributeName(VizMapperCategories.EDGE_COLOR);
	    
	    //System.out.println(">" + edgeKey.getString() + "<  vs  >"
	    //		   + controllingAttribName + "<");
	    if(controllingAttribName.equals(edgeKey.getString())) {
		DiscreteMapper dmColor =
		    (DiscreteMapper)
		    aMapper.getValueMapper(VizMapperCategories.EDGE_COLOR);
		
		//Map valueMapColor = dmColor.getValueMap();
		// make a copy.
		//Map valueMapColor = new HashMap(dmColor.getValueMap());
		if(theMap==null || !theMapKey.equals(controllingAttribName)) {
		    System.out.println("In controller, creating");
		    theMap = new HashMap(dmColor.getValueMap());
		    theMapKey = controllingAttribName;
		}
		Set allKeys = theMap.keySet();
		Iterator keyIter = allKeys.iterator();
		
		for(;keyIter.hasNext();) {
		    Object keyObject = keyIter.next();
		    String keyString = (String)keyObject;
		    //System.out.println(" key: " + keyString);
		    JButton tempButton = new JButton(keyString);
		    JLabel tempLabel = new JLabel("     ");
		    tempLabel.setOpaque(true);
		    tempLabel.setBackground((Color)theMap.get(keyString));
		    String tempTitle = edgeKey.getString() + " / " + keyString;
		    tempButton.addActionListener(new
			MapStringListener(ColorToDiscreteDialog.this,
					  theMap,
					  keyString,
					  tempLabel,
					  tempTitle));
		    intScrollPanel.add(tempButton);
		    intScrollPanel.add(tempLabel);
		}
	    }
	    else {
		// this is so that we don't have to create mappings where none existed.
		return;
		
	    }
	    GridBagGroup popupGBG = new GridBagGroup();
	    JScrollPane listScrollPane =
		new JScrollPane(intScrollPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    listScrollPane.setPreferredSize(new Dimension(150,150));
	    extScrollPanel = new JPanel(new GridLayout(1,1));
	    extScrollPanel.add(listScrollPane);
	    MiscGB.insert(popupGBG,extScrollPanel,0,0,2,1);
	    
	    JButton cancelButton = new JButton ("Cancel");
	    cancelButton.addActionListener (new CancelAction ());
	    MiscGB.insert(popupGBG,cancelButton,0,3);
	    JButton applyButton = new JButton ("Apply");
	    applyButton.addActionListener (new ApplyAction ());
	    MiscGB.insert(popupGBG,applyButton,1,3);
	    
	    setContentPane(popupGBG.panel);
	    pack ();
	    setLocationRelativeTo (EdgeTextPanel.this);
	    setVisible (true);
	    
	}
	public class EditAction extends AbstractAction {
	    EditAction () { super (""); }
	    public void actionPerformed (ActionEvent e) {
		//
		MutableColor mc =
		    new MutableColor((Color)theMap.get(attribKey.getString()));
		GeneralColorDialogListener gcdListener =
		    new GeneralColorDialogListener (ColorToDiscreteDialog.this,
						    mc,
						    edgeKey.getString() +
						    "/" +
						    attribKey.getString());
		gcdListener.popup();
		theMap.remove(attribKey.getString());
		theMap.put(attribKey.getString(),mc.getColor());
		//ColorToDiscreteDialog.this.dispose ();
	    }
	} // EditAction
	
	public class CancelAction extends AbstractAction {
	    CancelAction () { super (""); }
	    public void actionPerformed (ActionEvent e) {
		ColorToDiscreteDialog.this.dispose ();
	    }
	} // CancelAction
	
	public class ApplyAction extends AbstractAction {
	    ApplyAction () { super (""); }
	    public void actionPerformed (ActionEvent e) {
		useThisMap=true;
		ColorToDiscreteDialog.this.dispose ();
	    }
	} // CancelAction
	
    } // class ColorToDiscreteDialog
    
} // class EdgeTextPanel
