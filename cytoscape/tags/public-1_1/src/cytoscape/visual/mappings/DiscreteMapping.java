//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;
//----------------------------------------------------------------------------
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import cytoscape.GraphObjAttributes;
import cytoscape.dialogs.MiscGB;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.visual.Network;
import cytoscape.visual.ui.ValueDisplayer;
import cytoscape.visual.parsers.ValueParser;
import cytoscape.visual.parsers.ObjectToString;
//----------------------------------------------------------------------------
/**
 * Implements a lookup table mapping data to values of a particular class.
 * The data value is extracted from a bundle of attributes by using a
 * specified data attribute name.
 */
public class DiscreteMapping extends TreeMap implements ObjectMapping {
    
    Object defaultObj; //the default value held by this mapping
    Class rangeClass; // the valid range class for this mapping
    String attrName;  //the name of the controlling data attribute
    protected byte mapType; //node or edge; specifies which attributes to use
    protected HashSet mappedKeys;
    protected JScrollPane listScrollPane;
    protected JDialog parentDialog;

    /** keep track of interested UI classes */
    protected Vector changeListeners = new Vector(1,1);
    /**
     * Only one <code>ChangeEvent</code> is needed per mapping instance
     * since the event's only state is the source property.
     */
    protected transient ChangeEvent changeEvent;

    public DiscreteMapping(Object defObj, byte mapType) throws IllegalArgumentException {
	this(defObj, null, mapType);
    }
    public DiscreteMapping(Object defObj, String attrName, byte mapType) {
        this.defaultObj = defObj;
	this.rangeClass = defObj.getClass();
	if (mapType != ObjectMapping.EDGE_MAPPING &&
	    mapType != ObjectMapping.NODE_MAPPING) {
	    throw new IllegalArgumentException("Unknown mapping type " + mapType);
	}
	this.mapType = mapType;
        if (attrName != null)
	    setControllingAttributeName(attrName, null, false);
	//this.internalPanel = new JPanel();
	//this.internalPanel.setLayout(new BoxLayout(this.internalPanel, BoxLayout.Y_AXIS));
    }


    public Class getRangeClass() {return rangeClass;}
    
    public Class[] getAcceptedDataClasses() {
	// only strings supported
	Class[] ret = {String.class, Number.class};
	return ret;
    }

    public String getControllingAttributeName() {return attrName;}

    /**
     * Call whenever the controlling attribute changes. If preserveMapping
     * is true, all the currently stored mappings are unchanged; otherwise
     * all the mappings are cleared. In either case, this method calls
     * {@link #getUI} to rebuild the UI for this mapping, which in turn calls
     * {@link #loadKeys} to load the current data values for the new attribute.
     * <p>
     * Called by event handler from AbstractCalculator
     * {@link AbstractCalculator#AttributeSelectorListener}.
     *
     * @param	attrName	The name of the new attribute to map to
     */
    public void setControllingAttributeName(String attrName, Network n, boolean preserveMapping) {
        this.attrName = attrName;
	if (!preserveMapping) {
            this.clear();
            this.mappedKeys = new HashSet();
        }
    }
    
    /**
     * This method grabs all the data values for the current controlling
     * attribute from the appropriate GraphObjAttributes member of the
     * supplied network. Any data value that is not already a key in this
     * mapping is added with a null visual attribute value.
     * This method is called by the {@link #getUI} method before building
     * the UI.
     */
    public void loadKeys(Network network) {
        if (network == null) {return;}
	GraphObjAttributes attrs;
	if (mapType == ObjectMapping.EDGE_MAPPING)
	    attrs = network.getEdgeAttributes();
	else
	    attrs = network.getNodeAttributes();

	HashMap mapAttrs = attrs.getAttribute(attrName);
	if (mapAttrs == null) { // no attribute found <sob>
	    return;
	}

	Collection keys = mapAttrs.values();
	// get the set of keys being mapped from
	Iterator keyIter = keys.iterator();

	// add keys to the map, with null mappings
	while (keyIter.hasNext()) {
	    Object o = keyIter.next();
	    // handle vector data (from GO)
	    // add all values from the vector
	    if (o instanceof List) {
		List list = (List) o;
		for (int i = 0; i < list.size(); i++) {
		    Object vo = list.get(i);
		    if (!mappedKeys.contains(vo))
			mappedKeys.add(vo);
		}
	    }
	    else {
		if (!mappedKeys.contains(o))
		    mappedKeys.add(o);
	    }
	}
    }

    public Object calculateRangeValue(Map attrBundle) {
        if (attrBundle == null || attrName == null) {return null;}
        //extract the data value for our controlling attribute name
        Object attrValue = attrBundle.get(attrName);

        if (attrValue == null) {return null;}
        //from here we have to catch ClassCastExceptions that will be
        //thrown if the data value is not of a type comparable to the keys
        //in this SortedMap
        try {
            //if the attrValue is a List, search for an object in the List
            //that maps to a non-null value, and return the matching value
            if(attrValue instanceof List) {
                Iterator attrValueIt = ((List)attrValue).iterator();
                while(attrValueIt.hasNext()) {
                    Object attrSubValue = attrValueIt.next();
                    if(get(attrSubValue)!=null) {
                        return get(attrSubValue);
                    }
                }
                //if not found, return null
                return null;
            } else {
                //OK, try the attrValue itself as a key
                return get(attrValue); //returns null if not found
            }
        } catch (ClassCastException e) {
            return null;
        }
    }
    
    /**
     * Redefine the put method to only accept values of the correct type.
     * Note that, since this is a SortedMap, a ClassCastException will be
     * thrown if the new key is not comparable to the existing keys.
     */
    public Object put(Object key, Object value) {
        if ( rangeClass.isInstance(value) ) {
            return super.put(key, value);
        } else {
            String s = "Invalid map entry: Expected class " + rangeClass.toString()
                    + ", got class " + value.getClass().toString();
            throw new ClassCastException(s);
        }
    }
    
    /**
     * Redefine the putAll method to only accept values of the correct type.
     */
    public void putAll(Map m) {
        if (m == null) {return;}
        for (Iterator si = m.keySet().iterator(); si.hasNext(); ) {
            Object key = si.next();
            put( key, m.get(key) );
        }
    }
    
    /**
     * Customize this object by applying mapping defintions described by the
     * supplied Properties argument.
     */
    public void applyProperties(Properties props, String baseKey, ValueParser parser) {
        String contKey = baseKey + ".controller";
        String contValue = props.getProperty(contKey);
        if (contValue != null) {setControllingAttributeName(contValue, null, false);}
        
        String mapKey = baseKey + ".map.";
	Enumeration eProps = props.propertyNames();
	while (eProps.hasMoreElements()) {
	    String key = (String)eProps.nextElement();
	    if (key.startsWith(mapKey)) {
		String value = props.getProperty(key);
		String domainVal = key.substring(mapKey.length());
                Object parsedVal = parser.parseStringValue(value);
		put(domainVal,parsedVal);
	    }
	}
    }
    
    /**
     * Return a Properties object with entries suitable for customizing this
     * object via the applyProperties method.
     */
    public Properties getProperties(String baseKey) {
        Properties newProps = new Properties();
        String contKey = baseKey + ".controller";
        String contValue = getControllingAttributeName();
        newProps.setProperty(contKey, contValue);
        
        String mapKey = baseKey + ".map.";
        for (Iterator si = this.keySet().iterator(); si.hasNext(); ) {
            String key = (String)si.next();
            Object value = this.get(key);
            String stringValue = ObjectToString.getStringValue(value);
            newProps.setProperty(mapKey + key, stringValue);
        }
        return newProps;
    }
    
    public Object clone() {
	DiscreteMapping miniMe = (DiscreteMapping) super.clone();
	// defaultObj doesn't need to be cloned since it is only assigned on construction by the
	// creator of the mapping
	// rangeClass doesn't need to be cloned since the cloned calculator has the same type
	// as the original
	// only clone if mapping initialized
	if (attrName != null) {
	    miniMe.attrName = new String(attrName);
	    miniMe.mappedKeys = (HashSet) mappedKeys.clone();
	}
	return miniMe;
    }

    public void addChangeListener(ChangeListener l) {
        this.changeListeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        this.changeListeners.remove(l);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created.
     *
     * UI classes should attach themselves with a listener to the mapping to be
     * notified about changes in the underlying data structures that require the UI
     * classes to fetch a new copy of the UI and display it.
     *
     */
    protected void fireStateChanged() {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = this.changeListeners.size() - 1; i>=0; i--) {
	    ChangeListener listener = (ChangeListener) this.changeListeners.get(i);
	    // Lazily create the event:
	    if (this.changeEvent == null)
		this.changeEvent = new ChangeEvent(this);
	    listener.stateChanged(this.changeEvent);
        }
    }

    public JPanel getUI(JDialog parent, Network n) {
        //construct a UI to view/edit this mapping. Mapping is contained in
	//superclass TreeMap map - maps are from attribute bundles to Objects.
	//The UI only contains a JScrollPane containing a bunch of JButtons and
	//ValueDisplayers to display objects for
	parentDialog = parent;

        //get current data values as possible keys
        loadKeys(n);

	JPanel myUI = new JPanel();
	JPanel internalPanel = new JPanel();
	internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.Y_AXIS));
	
	// check that there is a valid attribute set
	if (this.attrName == null || this.mappedKeys == null || this.mappedKeys.size() == 0) {
	    myUI.add(new JLabel("Unknown attribute set!"));
	    return myUI;
	}
	    
	Object[] keyArray = this.mappedKeys.toArray();
	//Iterator keyIter = this.mappedKeys.iterator();
	int numKeys = this.mappedKeys.size();
	
	// have to handle the case where number of keys*2 > GridBagLayout.MAXGRIDSIZE, which is 512
	// but has no accessor so 512 must be hard-coded.
	int numGridBags = (int) Math.ceil(((double) numKeys * 2) / 512);
	
	for (int gbgIndex = 0; gbgIndex < numGridBags; gbgIndex++) {
	    GridBagGroup gbgInternal = new GridBagGroup();
	    for (int arrIndex = gbgIndex * 256, yPos = 0; yPos < 256 && arrIndex < keyArray.length; yPos++, arrIndex++) {
		Object keyObject = keyArray[arrIndex];
		String keyString = keyObject.toString();
		// create button
		JButton mapButton = new JButton(keyString);
		// get current mapping
		Object currentMapping = get(keyObject);
		ValueDisplayer mapValue;
		if (currentMapping == null) {
		    // display default selection
		    mapValue = ValueDisplayer.getDisplayFor(parent, "Define Discrete Mapping", defaultObj);
		}
		else { // display current mapping
		    mapValue = ValueDisplayer.getDisplayFor(parent, "Define Discrete Mapping", get(keyObject));
		}
		
		mapValue.addItemListener(new ValueChangeListener(keyObject));
		mapButton.addActionListener(mapValue.getInputListener());
		
		// set constraints to help with layout
		if (yPos == numKeys - 1) {
		    MiscGB.insert(gbgInternal, mapButton, 0, yPos, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, GridBagConstraints.BOTH);
		    MiscGB.insert(gbgInternal, mapValue, 1, yPos, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, GridBagConstraints.BOTH);
		}
		else {
		    // dump into the panel
		    MiscGB.insert(gbgInternal, mapButton, 0, yPos, GridBagConstraints.RELATIVE, 1, GridBagConstraints.BOTH);
		    MiscGB.insert(gbgInternal, mapValue, 1, yPos, GridBagConstraints.REMAINDER, 1, GridBagConstraints.BOTH);
		}
	    }
	    internalPanel.add(gbgInternal.panel);
	}
	resetScrollPane(myUI, internalPanel);
	return myUI;
    }

    private void resetScrollPane(JPanel myUI, JPanel internalPanel) {
	// set up the scrollable pane
	listScrollPane = new
	    JScrollPane(internalPanel,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	myUI.add(listScrollPane);
	
	// set limits on size
	Dimension d = listScrollPane.getPreferredSize();
	int prefHeight = (int) d.getHeight();
	if (prefHeight > 200)
	    prefHeight = 200;
	listScrollPane.setPreferredSize(new Dimension((int) d.getWidth() + 10,
						      prefHeight));
	// because parentDialog is only passed in at getUI and not construction time
	if (parentDialog != null) {
	    parentDialog.pack();
	    parentDialog.validate();
	    parentDialog.repaint();
	}
    }
            
    /**
     * Listens to a ValueDisplayer and sets the underlying TreeMap when a new
     * selection is made. Construct with the key whose mapping the ValueDisplayer
     * is displaying.
     */
    protected class ValueChangeListener implements ItemListener {
	private Object key;
	
	/**
	 * Constructs a ValueChangeListener.
	 *
	 * @param	key	the key the attached ValueDisplayer is displaying.
	 */
	public ValueChangeListener(Object key) {
	    this.key = key;
	}
	    
	/**
	 *  The ValueDisplayer being reflected by this listener was changed.
	 *  Make the appropriate changes to the underlying data in the mapper
	 *  and notify interested listeners that state has changed.
	 */
	public void itemStateChanged (ItemEvent e) {
	    ValueDisplayer v = (ValueDisplayer) e.getItemSelectable();
	    put(key, v.getValue());
	    fireStateChanged();
	}
    }
}

