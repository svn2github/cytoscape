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

import cytoscape.GraphObjAttributes;
import cytoscape.util.MutableString;
import cytoscape.util.MutableColor;
import cytoscape.vizmap.*;
//--------------------------------------------------------------------------------------
public class EdgeTextPanel extends JPanel {

    String [] attributeNames;
    MutableString edgeKey;
    MutableString attribKey;
    JList theList;
    AttributeMapper aMapper;
    Frame parentFrame;
    Map theMap;
    String theMapKey;
    boolean useThisMap;

//--------------------------------------------------------------------------------------
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

  GridBagLayout gridbag = new GridBagLayout(); 
  GridBagConstraints c = new GridBagConstraints();
  this.setLayout (gridbag);

  DefaultListModel listModel = new DefaultListModel();
  for (int i=0; i < attributeNames.length; i++)
      listModel.addElement(attributeNames [i]);
  theList = new JList(listModel);

  theList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  theList.addListSelectionListener(new SharedListSelectionHandler(edgeKey));
  theList.setSelectedValue(edgeKey.getString(),true);
  /*
    for (int i=0; i < attributeNames.length; i++) {
    String attributeName = attributeNames [i];
    System.out.println(attributeName);
    }
  */

  c.gridx=0;
  c.gridy=0;
  gridbag.setConstraints(theList,c);
  this.add(theList);

  JButton pickThisAttribute = new JButton ("OK");
  pickThisAttribute.addActionListener (new ColorToDiscreteListener());
  c.gridx=0;
  c.gridy=1;
  gridbag.setConstraints(pickThisAttribute,c);
  this.add(pickThisAttribute);

  
  
} // EdgeTextPanel ctor

public Map getMap() {
    return theMap;
}

public boolean getWhetherToUseTheMap() {
    return useThisMap;
}

public class SharedListSelectionHandler implements ListSelectionListener {
    MutableString ms;
    public SharedListSelectionHandler(MutableString ms) {
	super();
	this.ms = ms;
    }
    public void valueChanged(ListSelectionEvent e) {

	JList jl = (JList)e.getSource();
	ms.setString(jl.getSelectedValue().toString());
    }
}

public class ColorToDiscreteListener extends AbstractAction {
    public void actionPerformed (ActionEvent e) {
	//System.out.println(edgeKey.getString());
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

	    /*
	    boolean needToCreate = true;
	    if(theMapKey != null) {
		if(theMapKey.equals(edgeKey.getString())) {
		    System.out.println("Not in controller, but already created");
		    needToCreate = false;
		}
	    }
	    if(needToCreate) {
		System.out.println("Not in controller, creating");
		theMap = new HashMap();
		theMapKey = edgeKey.getString();
	    }
	    */
	}

  
	//valueMapColor.remove(key);
	//valueMapColor.put(key,c);

	JPanel popupPanel = new JPanel();
	
	GridBagLayout gridbag = new GridBagLayout(); 
	GridBagConstraints c = new GridBagConstraints();
	popupPanel.setLayout (gridbag);

	JScrollPane listScrollPane = new JScrollPane(intScrollPanel,
						     ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
						     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	listScrollPane.setPreferredSize(new Dimension(150,150));
	extScrollPanel = new JPanel(new GridLayout(1,1));
	extScrollPanel.add(listScrollPane);
	c.gridx=0;
	c.gridy=0;
	//c.gridheight=2;
	gridbag.setConstraints(extScrollPanel,c);
	popupPanel.add(extScrollPanel);


	JButton editButton = new JButton ("Edit");
	editButton.addActionListener (new EditAction ());

	c.gridx=1;
	c.gridy=0;
	//c.gridheight=1;
	gridbag.setConstraints(editButton,c);
	popupPanel.add(editButton);

	/*
	JButton newButton = new JButton ("New");
	newButton.addActionListener (new NewAction ());
	
	c.gridx=1;
	c.gridy=1;
	gridbag.setConstraints(newButton,c);
	popupPanel.add(newButton);

	JButton removeButton = new JButton ("Remove");
	removeButton.addActionListener (new RemoveAction ());

	c.gridx=1;
	c.gridy=2;
	gridbag.setConstraints(removeButton,c);
	popupPanel.add(removeButton);
	*/


	JButton cancelButton = new JButton ("Cancel");
	cancelButton.addActionListener (new CancelAction ());

	c.gridx=0;
	c.gridy=3;
	//c.gridy=1;
	gridbag.setConstraints(cancelButton,c);
	popupPanel.add(cancelButton);

	JButton applyButton = new JButton ("Apply");
	applyButton.addActionListener (new ApplyAction ());

	c.gridx=1;
	c.gridy=3;
	//c.gridy=1;
	gridbag.setConstraints(applyButton,c);
	popupPanel.add(applyButton);
	
	setContentPane(popupPanel);
	pack ();
	setLocationRelativeTo (EdgeTextPanel.this);
	setVisible (true);
	

	/*	
	DiscreteMapper dmSourceDec =
	    (DiscreteMapper)
	    aMapper.getValueMapper(VizMapperCategories.EDGE_SOURCE_DECORATION);
	if(dmSourceDec!=null) {
	    Map valueMapSourceDec = dmSourceDec.getValueMap();
	    Arrow sourceArrow =	(Arrow)valueMapSourceDec.get(key);
	    if(sourceArrow!=null) {
		String sourceArrowName = sourceArrow.getCustomName();
		if(sourceArrowName!=null) {
		    //System.out.println("fixing source arrow");
		    Shape sourceShape = sourceArrow.getShape();
		    Arrow sourceArrowNew =
			Arrow.addCustomArrow(sourceArrowName,sourceShape,c);
		    valueMapSourceDec.remove(key);
		    valueMapSourceDec.put(key,sourceArrowNew);
		}
	    }
	}

	*/
	
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

    // note: this class never quite worked. 
    // it would have had to redraw the internal panel.
    public class NewAction extends AbstractAction {
	NewAction () { super (""); }
	public void actionPerformed (ActionEvent e) {
	    NewStringPopupDialog nspd =
		new NewStringPopupDialog(parentFrame,"Mapping to be added");
	    String newString = nspd.getString();
	    if(newString!=null) {
		if(newString.length()>0) {
		    //System.out.println(newString);
		    JButton tempButton = new JButton(newString);
		    JLabel tempLabel = new JLabel("     ");
		    tempLabel.setOpaque(true);
		    tempLabel.setBackground(Color.WHITE);
		    theMap.put(newString,Color.WHITE);
		    String tempTitle = edgeKey.getString() + " / " + newString;
		    tempButton.addActionListener(new
			MapStringListener(ColorToDiscreteDialog.this,
					  theMap,
					  newString,
					  tempLabel,
					  tempTitle));
		    intScrollPanel.add(tempButton);
		    intScrollPanel.add(tempLabel);
		}
	    }
	    
	    //ColorToDiscreteDialog.this.dispose ();
	}
    } // NewAction

    // note: this class never quite worked;
    // it would have had to have access to the buttons and labels,
    // perhaps through a HashMap.
    public class RemoveAction extends AbstractAction {
	RemoveAction () { super (""); }
	public void actionPerformed (ActionEvent e) {
	    //
	    ColorToDiscreteDialog.this.dispose ();
	}
    } // RemoveAction

    public void spawnNeedToSelectErrorMessage() {
    }

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


