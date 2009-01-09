//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.ui.*;
//----------------------------------------------------------------------------
/**
 * Stores various types of Calculators from data attributes to an attribute of a
 * specified type. Also keeps track of available mappings. Notifies interested
 * classes of changes to the underlying datasets.
 */
public class CalculatorCatalog {
    
    Map nodeColorCalculators = new HashMap();
    HashSet nodeColorCalculatorNames = new HashSet();
    Vector nodeColorListeners = new Vector(2, 1);

    Map nodeLineTypeCalculators = new HashMap();
    HashSet nodeLineTypeCalculatorNames = new HashSet();
    Vector nodeLineTypeListeners = new Vector(1, 1);
    
    Map nodeShapeCalculators = new HashMap();
    HashSet nodeShapeCalculatorNames = new HashSet();
    Vector nodeShapeListeners = new Vector(1, 1);

    Map nodeSizeCalculators = new HashMap();
    HashSet nodeSizeCalculatorNames = new HashSet();
    Vector nodeSizeListeners = new Vector(2, 1);

    Map nodeLabelCalculators = new HashMap();
    HashSet nodeLabelCalculatorNames = new HashSet();
    Vector nodeLabelListeners = new Vector(1, 1);
    
    Map nodeToolTipCalculators = new HashMap();
    HashSet nodeToolTipCalculatorNames = new HashSet();
    Vector nodeToolTipListeners = new Vector(1, 1);

    Map nodeFontFaceCalculators = new HashMap();
    HashSet nodeFontFaceCalculatorNames = new HashSet();
    Vector nodeFontFaceListeners = new Vector(1, 1);

    Map nodeFontSizeCalculators = new HashMap();
    HashSet nodeFontSizeCalculatorNames = new HashSet();
    Vector nodeFontSizeListeners = new Vector(1, 1);

    Map edgeColorCalculators = new HashMap();
    HashSet edgeColorCalculatorNames = new HashSet();
    Vector edgeColorListeners = new Vector(1, 1);

    Map edgeLineTypeCalculators = new HashMap();
    HashSet edgeLineTypeCalculatorNames = new HashSet();
    Vector edgeLineTypeListeners = new Vector(1, 1);

    Map edgeArrowCalculators = new HashMap();
    HashSet edgeArrowCalculatorNames = new HashSet();
    Vector edgeArrowListeners = new Vector(2, 1);

    Map edgeLabelCalculators = new HashMap();
    HashSet edgeLabelCalculatorNames = new HashSet();
    Vector edgeLabelListeners = new Vector(1, 1);

    Map edgeToolTipCalculators = new HashMap();
    HashSet edgeToolTipCalculatorNames = new HashSet();
    Vector edgeToolTipListeners = new Vector(1, 1);

    Map edgeFontFaceCalculators = new HashMap();
    HashSet edgeFontFaceCalculatorNames = new HashSet();
    Vector edgeFontFaceListeners = new Vector(1, 1);

    Map edgeFontSizeCalculators = new HashMap();
    HashSet edgeFontSizeCalculatorNames = new HashSet();
    Vector edgeFontSizeListeners = new Vector(1, 1);
    
    Map nodeAppearanceCalculators = new HashMap();
    Vector nodeAppearanceListeners = new Vector(1, 1);

    Map edgeAppearanceCalculators = new HashMap();
    Vector edgeAppearanceListeners = new Vector(1, 1);

    // mapping database
    Map mappers = new HashMap();
    HashSet mapperNames = new HashSet();

    /**
     * Only one <code>ChangeEvent</code> is needed per catalog
     * instance since the
     * event's only state is the source property.  The source of events
     * generated is always "this".
     */
    protected transient ChangeEvent changeEvent;

    public CalculatorCatalog() {}
    public CalculatorCatalog(Properties props) {
        //should read calculators from their description in the properties object
    }

    /**
     * Add a ChangeListener to the catalog. Depending on the passed-in type,
     * the catalog will add the ChangeListener to the appropriate listener vector
     * for the associated set of calculators. When the catalog's database of
     * calculators changes, the ChangeListener will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the catalog.
     *
     * @param	l	ChangeListener to add
     * @param	type	type of calculator to add to, one of {@link VizMapUI}'s constants
     * @throws IllegalArgumentException if unknown type passed in
     */
    public void addChangeListener(ChangeListener l, byte type) throws IllegalArgumentException{
	switch (type) {
	case VizMapUI.NODE_COLOR:
	case VizMapUI.NODE_BORDER_COLOR:
	    nodeColorListeners.add(l);
	    break;
	case VizMapUI.NODE_LINETYPE:
	    nodeLineTypeListeners.add(l);
	    break;
	case VizMapUI.NODE_SHAPE:
	    nodeShapeListeners.add(l);
	    break;
	case VizMapUI.NODE_HEIGHT:
	case VizMapUI.NODE_WIDTH:
	case VizMapUI.NODE_SIZE:
	    nodeSizeListeners.add(l);
	    break;
	case VizMapUI.NODE_LABEL:
	    nodeLabelListeners.add(l);
	    break;
	case VizMapUI.NODE_TOOLTIP:
	    nodeToolTipListeners.add(l);
	    break;
	case VizMapUI.EDGE_COLOR:
	    edgeColorListeners.add(l);
	    break;
	case VizMapUI.EDGE_LINETYPE:
	    edgeLineTypeListeners.add(l);
	    break;
	case VizMapUI.EDGE_SRCARROW:
	case VizMapUI.EDGE_TGTARROW:
	    edgeArrowListeners.add(l);
	    break;
	case VizMapUI.EDGE_LABEL:
	    edgeLabelListeners.add(l);
	    break;
	case VizMapUI.EDGE_TOOLTIP:
	    edgeToolTipListeners.add(l);
	    break;
	case VizMapUI.NODE_FONT_FACE:
	    nodeFontFaceListeners.add(l);
	    break;
	case VizMapUI.NODE_FONT_SIZE:
	    nodeFontSizeListeners.add(l);
	    break;
	case VizMapUI.EDGE_FONT_FACE:
	    edgeFontFaceListeners.add(l);
	    break;
	case VizMapUI.EDGE_FONT_SIZE:
	    edgeFontSizeListeners.add(l);
	    break;
	default:
	    throw new IllegalArgumentException("Unknown type " + type);
	}
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created.
     *
     * Note that fireStateChanged is only triggered by calling
     * {@link #addCalculator}, {@link #renameCalculator}, or
     * {@link #removeCalculator}.
     * Manipulating each type explicitly
     * does not trigger ChangeEvents to be fired. This is because the UI classes
     * only use the more general methods.
     *
     * However, this behavior does not permit "hidden" calculators. Upon the next
     * refresh, all calculators contained will be visible.
     *
     * @param	type	one of VizMapUI constants, which set of listeners to notify
     * @throws IllegalArgumentException if type is unknown
     */
    protected void fireStateChanged(byte type) throws IllegalArgumentException{
	Vector notifyEvents;

	switch (type) {
	case VizMapUI.NODE_COLOR:
	case VizMapUI.NODE_BORDER_COLOR:
	    notifyEvents = nodeColorListeners;
	    break;
	case VizMapUI.NODE_LINETYPE:
	    notifyEvents = nodeLineTypeListeners;
	    break;
	case VizMapUI.NODE_SHAPE:
	    notifyEvents = nodeShapeListeners;
	    break;
	case VizMapUI.NODE_HEIGHT:
	case VizMapUI.NODE_WIDTH:
	case VizMapUI.NODE_SIZE:
	    notifyEvents = nodeSizeListeners;
	    break;
	case VizMapUI.NODE_LABEL:
	    notifyEvents = nodeLabelListeners;
	    break;
	case VizMapUI.NODE_FONT_FACE:
	    notifyEvents = nodeFontFaceListeners;
	    break;
	case VizMapUI.NODE_FONT_SIZE:
	    notifyEvents = nodeFontSizeListeners;
	    break;
	case VizMapUI.NODE_TOOLTIP:
	    notifyEvents = nodeToolTipListeners;
	    break;
	case VizMapUI.EDGE_COLOR:
	    notifyEvents = edgeColorListeners;
	    break;
	case VizMapUI.EDGE_LINETYPE:
	    notifyEvents = edgeLineTypeListeners;
	    break;
	case VizMapUI.EDGE_SRCARROW:
	case VizMapUI.EDGE_TGTARROW:
	    notifyEvents = edgeArrowListeners;
	    break;
	case VizMapUI.EDGE_LABEL:
	    notifyEvents = edgeLabelListeners;
	    break;
	case VizMapUI.EDGE_TOOLTIP:
	    notifyEvents = edgeToolTipListeners;
	    break;
	case VizMapUI.EDGE_FONT_FACE:
	    notifyEvents = edgeFontFaceListeners;
	    break;
	case VizMapUI.EDGE_FONT_SIZE:
	    notifyEvents = edgeFontSizeListeners;
	    break;
	default:
	    throw new IllegalArgumentException("Unknown type " + type);
	}
	    
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = notifyEvents.size() - 1; i>=0; i--) {
	    ChangeListener listener = (ChangeListener) notifyEvents.get(i);
	    // Lazily create the event:
	    if (changeEvent == null)
		changeEvent = new ChangeEvent(this);
	    listener.stateChanged(changeEvent);
        }
    }   
    
    /** 
     * Add any calculator to the catalog. Automatically checks type. Calculator
     * is added according to its name as reported by the toString() method.
     * @param	dupe	Calculator to add
     * @throws DuplicateCalculatorNameException if calculator's name is a duplicate with valid name as detail message
     * @throws IllegalArgumentException if calculator is of an unknown type
     */
    public void addCalculator(Calculator dupe) throws DuplicateCalculatorNameException, IllegalArgumentException {
	byte calcType;
	if (dupe instanceof NodeColorCalculator) {
	    addNodeColorCalculator((NodeColorCalculator) dupe);
	    calcType = VizMapUI.NODE_COLOR;
	}
	else if (dupe instanceof NodeLineTypeCalculator) {
	    addNodeLineTypeCalculator((NodeLineTypeCalculator) dupe);
	    calcType = VizMapUI.NODE_LINETYPE;
	}
	else if (dupe instanceof NodeShapeCalculator) {
	    addNodeShapeCalculator((NodeShapeCalculator) dupe);
	    calcType = VizMapUI.NODE_SHAPE;
	}
	else if (dupe instanceof NodeSizeCalculator) {
	    addNodeSizeCalculator((NodeSizeCalculator) dupe);
	    calcType = VizMapUI.NODE_SIZE;
	}
	else if (dupe instanceof NodeLabelCalculator) {
	    addNodeLabelCalculator((NodeLabelCalculator) dupe);
	    calcType = VizMapUI.NODE_LABEL;
	}
	else if (dupe instanceof NodeToolTipCalculator) {
	    addNodeToolTipCalculator((NodeToolTipCalculator) dupe);
	    calcType = VizMapUI.NODE_TOOLTIP;
	}
	else if (dupe instanceof EdgeColorCalculator) {
	    addEdgeColorCalculator((EdgeColorCalculator) dupe);
	    calcType = VizMapUI.EDGE_COLOR;
	}
	else if (dupe instanceof EdgeLineTypeCalculator) {
	    addEdgeLineTypeCalculator((EdgeLineTypeCalculator) dupe);
	    calcType = VizMapUI.EDGE_LINETYPE;
	}
	else if (dupe instanceof EdgeArrowCalculator) {
	    addEdgeArrowCalculator((EdgeArrowCalculator) dupe);
	    calcType = VizMapUI.EDGE_SRCARROW;
	}
	else if (dupe instanceof EdgeLabelCalculator) {
	    addEdgeLabelCalculator((EdgeLabelCalculator) dupe);
	    calcType = VizMapUI.EDGE_LABEL;
	}
	else if (dupe instanceof EdgeToolTipCalculator) {
	    addEdgeToolTipCalculator((EdgeToolTipCalculator) dupe);
	    calcType = VizMapUI.EDGE_TOOLTIP;
	}
	else if (dupe instanceof EdgeFontFaceCalculator) {
	    addEdgeFontFaceCalculator((EdgeFontFaceCalculator) dupe);
	    calcType = VizMapUI.EDGE_FONT_FACE;
	}
	else if (dupe instanceof EdgeFontSizeCalculator) {
	    addEdgeFontSizeCalculator((EdgeFontSizeCalculator) dupe);
	    calcType = VizMapUI.EDGE_FONT_SIZE;
	}	
	else if (dupe instanceof NodeFontFaceCalculator) {
	    addNodeFontFaceCalculator((NodeFontFaceCalculator) dupe);
	    calcType = VizMapUI.NODE_FONT_FACE;
	}
	else if (dupe instanceof NodeFontSizeCalculator) {
	    addNodeFontSizeCalculator((NodeFontSizeCalculator) dupe);
	    calcType = VizMapUI.NODE_FONT_SIZE;
	}	
	else {
	    throw new IllegalArgumentException("Unknown calculator type");
	}
	
	// throw event listeners
	fireStateChanged(calcType);
    }

    /** Checks whether a name for a calculator is valid
     *	@param	calcName	Name to check
     *	@param	calcType	Type of calculator {@link cytoscape.visual.ui.VizMapUI}
     *
     *	@return	a valid name for the calculator. If the given name was not valid,
     *		numbers are appended until a valid name is found; this valid name
     *		is returned to the caller.
     */
    public String checkCalculatorName(String calcName, byte calcType) {
	if (calcName == null)
	    return null;

	String name = new String(calcName);

	switch(calcType) {
	case VizMapUI.NODE_COLOR:
	case VizMapUI.NODE_BORDER_COLOR:
	    return checkNodeColorCalculatorName(name);
	case VizMapUI.NODE_LINETYPE:
	    return checkNodeLineTypeCalculatorName(name);
	case VizMapUI.NODE_SHAPE:
	    return checkNodeShapeCalculatorName(name);
	case VizMapUI.NODE_HEIGHT:
	case VizMapUI.NODE_WIDTH:
	case VizMapUI.NODE_SIZE:
	    return checkNodeSizeCalculatorName(name);
	case VizMapUI.NODE_LABEL:
	    return checkNodeLabelCalculatorName(name);
	case VizMapUI.NODE_TOOLTIP:
	    return checkNodeToolTipCalculatorName(name);
	case VizMapUI.EDGE_COLOR:
	    return checkEdgeColorCalculatorName(name);
	case VizMapUI.EDGE_LINETYPE:
	    return checkEdgeLineTypeCalculatorName(name);
	case VizMapUI.EDGE_SRCARROW:
	case VizMapUI.EDGE_TGTARROW:
	    return checkEdgeArrowCalculatorName(name);
	case VizMapUI.EDGE_LABEL:
	    return checkEdgeLabelCalculatorName(name);
	case VizMapUI.EDGE_TOOLTIP:
	    return checkEdgeToolTipCalculatorName(name);
	case VizMapUI.NODE_FONT_FACE:
	    return checkNodeFontFaceCalculatorName(name);
	case VizMapUI.NODE_FONT_SIZE:
	    return checkNodeFontSizeCalculatorName(name);	    
	case VizMapUI.EDGE_FONT_FACE:
	    return checkEdgeFontFaceCalculatorName(name);
	case VizMapUI.EDGE_FONT_SIZE:
	    return checkEdgeFontSizeCalculatorName(name);
	default:
	    return null;
	}
    }

    /** Renames a calculator.
     *  @param	c	Calculator to rename
     *	@param	name New name for calculator
     *	@throws	DuplicateCalculatorNameException if name is a duplicate with valid name as detail message
     *  @throws IllegalArgumentException if c is of an unknown type
     */
    public void renameCalculator(Calculator c, String name) throws DuplicateCalculatorNameException, IllegalArgumentException {
	String newName;
	byte calcType;
	if (c instanceof NodeColorCalculator) {
	    if ((newName = checkNodeColorCalculatorName(name)).equals(name)) {
		removeNodeColorCalculator(c.toString());
		c.setName(name);
		addNodeColorCalculator((NodeColorCalculator) c);
		calcType = VizMapUI.NODE_COLOR;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof NodeLineTypeCalculator) {
	    if ((newName = checkNodeLineTypeCalculatorName(name)).equals(name)) {
		removeNodeLineTypeCalculator(c.toString());
		c.setName(name);
		addNodeLineTypeCalculator((NodeLineTypeCalculator) c);
		calcType = VizMapUI.NODE_LINETYPE;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof NodeShapeCalculator) {
	    if ((newName = checkNodeShapeCalculatorName(name)).equals(name)) {
		removeNodeShapeCalculator(c.toString());
		c.setName(name);
		addNodeShapeCalculator((NodeShapeCalculator) c);
		calcType = VizMapUI.NODE_SHAPE;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof NodeSizeCalculator) {
	    if ((newName = checkNodeSizeCalculatorName(name)).equals(name)) {
		removeNodeSizeCalculator(c.toString());
		c.setName(name);
		addNodeSizeCalculator((NodeSizeCalculator) c);
		calcType = VizMapUI.NODE_SIZE;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof NodeLabelCalculator) {
	    if ((newName = checkNodeLabelCalculatorName(name)).equals(name)) {
		removeNodeLabelCalculator(c.toString());
		c.setName(name);
		addNodeLabelCalculator((NodeLabelCalculator) c);
		calcType = VizMapUI.NODE_LABEL;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof NodeToolTipCalculator) {
	    if ((newName = checkNodeToolTipCalculatorName(name)).equals(name)) {
		removeNodeToolTipCalculator(c.toString());
		c.setName(name);
		addNodeToolTipCalculator((NodeToolTipCalculator) c);
		calcType = VizMapUI.NODE_TOOLTIP;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof EdgeColorCalculator) {
	    if ((newName = checkEdgeColorCalculatorName(name)).equals(name)) {
		removeEdgeColorCalculator(c.toString());
		c.setName(name);
		addEdgeColorCalculator((EdgeColorCalculator) c);
		calcType = VizMapUI.EDGE_COLOR;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof EdgeLineTypeCalculator) {
	    if ((newName = checkEdgeLineTypeCalculatorName(name)).equals(name)) {
		removeEdgeLineTypeCalculator(c.toString());
		c.setName(name);
		addEdgeLineTypeCalculator((EdgeLineTypeCalculator) c);
		calcType = VizMapUI.EDGE_LINETYPE;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof EdgeArrowCalculator) {
	    if ((newName = checkEdgeArrowCalculatorName(name)).equals(name)) {
		removeEdgeArrowCalculator(c.toString());
		c.setName(name);
		addEdgeArrowCalculator((EdgeArrowCalculator) c);
		calcType = VizMapUI.EDGE_SRCARROW;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof EdgeLabelCalculator) {
	    if ((newName = checkEdgeLabelCalculatorName(name)).equals(name)) {
		removeEdgeLabelCalculator(c.toString());
		c.setName(name);
		addEdgeLabelCalculator((EdgeLabelCalculator) c);
		calcType = VizMapUI.EDGE_LABEL;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof EdgeToolTipCalculator) {
	    if ((newName = checkEdgeToolTipCalculatorName(name)).equals(name)) {
		removeEdgeToolTipCalculator(c.toString());
		c.setName(name);
		addEdgeToolTipCalculator((EdgeToolTipCalculator) c);
		calcType = VizMapUI.EDGE_TOOLTIP;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof EdgeFontFaceCalculator) {
	    if ((newName = checkEdgeFontFaceCalculatorName(name)).equals(name)) {
		removeEdgeFontFaceCalculator(c.toString());
		c.setName(name);
		addEdgeFontFaceCalculator((EdgeFontFaceCalculator) c);
		calcType = VizMapUI.EDGE_FONT_FACE;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof EdgeFontSizeCalculator) {
	    if ((newName = checkEdgeFontSizeCalculatorName(name)).equals(name)) {
		removeEdgeFontSizeCalculator(c.toString());
		c.setName(name);
		addEdgeFontSizeCalculator((EdgeFontSizeCalculator) c);
		calcType = VizMapUI.EDGE_FONT_SIZE;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof NodeFontFaceCalculator) {
	    if ((newName = checkNodeFontFaceCalculatorName(name)).equals(name)) {
		removeNodeFontFaceCalculator(c.toString());
		c.setName(name);
		addNodeFontFaceCalculator((NodeFontFaceCalculator) c);
		calcType = VizMapUI.NODE_FONT_FACE;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else if (c instanceof NodeFontSizeCalculator) {
	    if ((newName = checkNodeFontSizeCalculatorName(name)).equals(name)) {
		removeNodeFontSizeCalculator(c.toString());
		c.setName(name);
		addNodeFontSizeCalculator((NodeFontSizeCalculator) c);
		calcType = VizMapUI.NODE_FONT_SIZE;
	    }
	    else
		throw new DuplicateCalculatorNameException(newName);
	}
	else {
	    throw new IllegalArgumentException("Unknown calculator type");
	}

	// alert event listeners
	fireStateChanged(calcType);
    }

    /**
     * Remove a calculator.
     * @param	c	Calculator to remove
     * @throws IllegalArgumentException if c is of an unknown calculator type
     */
    public void removeCalculator(Calculator c) throws IllegalArgumentException {
	byte calcType;
	if (c instanceof NodeColorCalculator) {
	    removeNodeColorCalculator(c.toString());
	    calcType = VizMapUI.NODE_COLOR;	    
	}
	else if (c instanceof NodeLineTypeCalculator) {
	    removeNodeLineTypeCalculator(c.toString());
	    calcType = VizMapUI.NODE_LINETYPE;
	}
	else if (c instanceof NodeShapeCalculator) {
	    removeNodeShapeCalculator(c.toString());
	    calcType = VizMapUI.NODE_SHAPE;
	}
	else if (c instanceof NodeSizeCalculator) {
	    removeNodeSizeCalculator(c.toString());
	    calcType = VizMapUI.NODE_SIZE;
	}
	else if (c instanceof NodeLabelCalculator) {
	    removeNodeLabelCalculator(c.toString());
	    calcType = VizMapUI.NODE_LABEL;
	}
	else if (c instanceof NodeToolTipCalculator) {
	    removeNodeToolTipCalculator(c.toString());
	    calcType = VizMapUI.NODE_TOOLTIP;
	}
	else if (c instanceof EdgeColorCalculator) {
	    removeEdgeColorCalculator(c.toString());
	    calcType = VizMapUI.EDGE_COLOR;
	}
	else if (c instanceof EdgeLineTypeCalculator) {
	    removeEdgeLineTypeCalculator(c.toString());
	    calcType = VizMapUI.EDGE_LINETYPE;
	}
	else if (c instanceof EdgeArrowCalculator) {
	    removeEdgeArrowCalculator(c.toString());
	    calcType = VizMapUI.EDGE_SRCARROW;
	}
	else if (c instanceof EdgeLabelCalculator) {
	    removeEdgeLabelCalculator(c.toString());
	    calcType = VizMapUI.EDGE_LABEL;
	}
	else if (c instanceof EdgeToolTipCalculator) {
	    removeEdgeToolTipCalculator(c.toString());
	    calcType = VizMapUI.EDGE_TOOLTIP;
	}
	else if (c instanceof EdgeFontFaceCalculator) {
	    removeEdgeFontFaceCalculator(c.toString());
	    calcType = VizMapUI.EDGE_FONT_FACE;
	}
	else if (c instanceof EdgeFontSizeCalculator) {
	    removeEdgeFontSizeCalculator(c.toString());
	    calcType = VizMapUI.EDGE_FONT_SIZE;
	}
	else if (c instanceof NodeFontFaceCalculator) {
	    removeNodeFontFaceCalculator(c.toString());
	    calcType = VizMapUI.NODE_FONT_FACE;
	}
	else if (c instanceof NodeFontSizeCalculator) {
	    removeNodeFontSizeCalculator(c.toString());
	    calcType = VizMapUI.NODE_FONT_SIZE;
	}	
	else {
	    throw new IllegalArgumentException("Unknown calculator type");
	}

	// fire event
	fireStateChanged(calcType);
    }	
	

    /**
     * Returns the HashMap of mappers
     */
    public Set getMappingNames() {
	return mappers.keySet();
    }
    /**
     * Add a mapping to the database of available mappings. Because mappings are
     * instantiated for each calculator, only class types are stored.
     *
     * @param	name	Name of the mapping
     * @param	m	Class of the mapping
     * @throws DuplicateCalculatorNameException if the given name is already taken
     * @throws IllegalArgumentException if the given class is not in the mapping hierarchy
     */
    public void addMapping(String name, Class m) throws DuplicateCalculatorNameException, IllegalArgumentException {
	// verify that the class is in the mapping hierarchy
	if (!ObjectMapping.class.isAssignableFrom(m))
	    throw new IllegalArgumentException("Class " + m.getName() + " is not an ObjectMapper!");

	// check for duplicate names
	if (mapperNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate mapper name " + name);
	mapperNames.add(name);
	mappers.put(name, m);
    }
    public Class removeMapping(String name) {
	mapperNames.remove(name);
	return (Class) mappers.remove(name);
    }
    public Class getMapping(String name) {
	return (Class) mappers.get(name);
    }
    public String checkMappingName(String name) {
	String newName = name;
	int nameApp = 2;
	while (mapperNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }

    public Set getNodeAppearanceCalculatorNames() {
        return nodeAppearanceCalculators.keySet();
    }   
    public Collection getNodeAppearanceCalculators() {
        return nodeAppearanceCalculators.values();
    }
    public void addNodeAppearanceCalculator(String name, NodeAppearanceCalculator c) {
        nodeAppearanceCalculators.put(name, c);
	NodeColorCalculator ncc1 = c.getNodeFillColorCalculator();
	if( ncc1!=null && !nodeColorCalculators.values().contains(ncc1) ) {
            addNodeColorCalculator(ncc1);
        }
	NodeColorCalculator ncc2 = c.getNodeBorderColorCalculator();
	if(ncc2!=null && !nodeColorCalculators.values().contains(ncc2) ) {
            addNodeColorCalculator(ncc2);
        }
	NodeLineTypeCalculator nltc = c.getNodeLineTypeCalculator();
	if(nltc!=null && !nodeLineTypeCalculators.values().contains(nltc) ) {
            addNodeLineTypeCalculator(nltc);
        }
	NodeShapeCalculator nsc = c.getNodeShapeCalculator();
	if(nsc!=null && !nodeShapeCalculators.values().contains(nsc) ) {
            addNodeShapeCalculator(nsc);
        }
	NodeSizeCalculator nsc1 = c.getNodeHeightCalculator();
	if(nsc1!=null && !nodeSizeCalculators.values().contains(nsc1) ) {
            addNodeSizeCalculator(nsc1);
        }
	NodeSizeCalculator nsc2 = c.getNodeWidthCalculator();
	if(nsc2!=null && !nodeSizeCalculators.values().contains(nsc2) ) {
            addNodeSizeCalculator(nsc2);
        }
	NodeLabelCalculator nlc = c.getNodeLabelCalculator();
	if(nlc!=null && !nodeLabelCalculators.values().contains(nlc) ) {
            addNodeLabelCalculator(nlc);
        }
	NodeToolTipCalculator nttc = c.getNodeToolTipCalculator();
	if(nttc!=null && !nodeToolTipCalculators.values().contains(nttc) ) {
            addNodeToolTipCalculator(nttc);
        }
        NodeFontFaceCalculator nffc = c.getNodeFontFaceCalculator();
        if (nffc != null && !nodeFontFaceCalculators.values().contains(nffc) ) {
            addNodeFontFaceCalculator(nffc);
        }
        NodeFontSizeCalculator nfsc = c.getNodeFontSizeCalculator();
        if (nfsc != null && !nodeFontSizeCalculators.values().contains(nfsc) ) {
            addNodeFontSizeCalculator(nfsc);
        }
    }
    public NodeAppearanceCalculator removeNodeAppearanceCalculator(String name) {
        return (NodeAppearanceCalculator)nodeAppearanceCalculators.remove(name);
    }

    public NodeAppearanceCalculator getNodeAppearanceCalculator(String name) {
        return (NodeAppearanceCalculator)nodeAppearanceCalculators.get(name);
    }
    
    public Set getEdgeAppearanceCalculatorNames() {
        return edgeAppearanceCalculators.keySet();
    }
    public Collection getEdgeAppearanceCalculators() {
        return edgeAppearanceCalculators.values();
    }
    public void addEdgeAppearanceCalculator(String name, EdgeAppearanceCalculator c) {
        edgeAppearanceCalculators.put(name, c);
	EdgeColorCalculator ecc = c.getEdgeColorCalculator();
	if(ecc!=null && !edgeColorCalculators.values().contains(ecc) ) {
            addEdgeColorCalculator(ecc);
        }
	EdgeLineTypeCalculator eltc = c.getEdgeLineTypeCalculator();
	if(eltc!=null && !edgeLineTypeCalculators.values().contains(eltc) ) {
            addEdgeLineTypeCalculator(eltc);
        }
	EdgeArrowCalculator eac1 = c.getEdgeSourceArrowCalculator();
	if(eac1!=null && !edgeArrowCalculators.values().contains(eac1) ) {
            addEdgeArrowCalculator(eac1);
        }
	EdgeArrowCalculator eac2 = c.getEdgeTargetArrowCalculator();
	if(eac2!=null && !edgeArrowCalculators.values().contains(eac2) ) {
            addEdgeArrowCalculator(eac2);
        }
        EdgeLabelCalculator elc = c.getEdgeLabelCalculator();
	if(elc!=null && !edgeLabelCalculators.values().contains(elc) ) {
            addEdgeLabelCalculator(elc);
        }
	EdgeToolTipCalculator ettc = c.getEdgeToolTipCalculator();
	if(ettc!=null && !edgeToolTipCalculators.values().contains(ettc) ) {
            addEdgeToolTipCalculator(ettc);
        }
        EdgeFontFaceCalculator effc = c.getEdgeFontFaceCalculator();
        if (effc != null && !edgeFontFaceCalculators.values().contains(effc) ) {
            addEdgeFontFaceCalculator(effc);
        }
        EdgeFontSizeCalculator efsc = c.getEdgeFontSizeCalculator();
        if (efsc != null && !edgeFontSizeCalculators.values().contains(efsc) ) {
            addEdgeFontSizeCalculator(efsc);
        }
    }
    public EdgeAppearanceCalculator removeEdgeAppearanceCalculator(String name) {
	return (EdgeAppearanceCalculator)edgeAppearanceCalculators.remove(name);
    }
    public EdgeAppearanceCalculator getEdgeAppearanceCalculator(String name) {
        return (EdgeAppearanceCalculator)edgeAppearanceCalculators.get(name);
    }
    
    public Collection getNodeColorCalculators() {return nodeColorCalculators.values();}
    public void addNodeColorCalculator(NodeColorCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
	if (nodeColorCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	nodeColorCalculatorNames.add(name);
        nodeColorCalculators.put(name, c);
    }
    public NodeColorCalculator removeNodeColorCalculator(String name) {
	nodeColorCalculatorNames.remove(name);
        return (NodeColorCalculator)nodeColorCalculators.remove(name);
    }
    public NodeColorCalculator getNodeColorCalculator(String name) {
        return (NodeColorCalculator)nodeColorCalculators.get(name);
    }
    public String checkNodeColorCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (nodeColorCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
	
    public Collection getNodeLineTypeCalculators() {return nodeLineTypeCalculators.values();}
    public void addNodeLineTypeCalculator(NodeLineTypeCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
	if (nodeLineTypeCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	nodeLineTypeCalculatorNames.add(name);
	nodeLineTypeCalculators.put(name, c);
    }
    public NodeLineTypeCalculator removeNodeLineTypeCalculator(String name) {
	nodeLineTypeCalculatorNames.remove(name);
        return (NodeLineTypeCalculator)nodeLineTypeCalculators.remove(name);
    }
    public NodeLineTypeCalculator getNodeLineTypeCalculator(String name) {
        return (NodeLineTypeCalculator)nodeLineTypeCalculators.get(name);
    }
    public String checkNodeLineTypeCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (nodeLineTypeCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
        
    public Collection getNodeShapeCalculators() {return nodeShapeCalculators.values();}
    public void addNodeShapeCalculator(NodeShapeCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
	if (nodeShapeCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	nodeShapeCalculatorNames.add(name);
	nodeShapeCalculators.put(name, c);
    }
    public NodeShapeCalculator removeNodeShapeCalculator(String name) {
	nodeShapeCalculatorNames.remove(name);
        return (NodeShapeCalculator)nodeShapeCalculators.remove(name);
    }
    public NodeShapeCalculator getNodeShapeCalculator(String name) {
        return (NodeShapeCalculator)nodeShapeCalculators.get(name);
    }
    public String checkNodeShapeCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (nodeShapeCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
    
    public Collection getNodeSizeCalculators() {return nodeSizeCalculators.values();}
    public void addNodeSizeCalculator(NodeSizeCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (nodeSizeCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	nodeSizeCalculatorNames.add(name);
        nodeSizeCalculators.put(name, c);
    }
    public NodeSizeCalculator removeNodeSizeCalculator(String name) {
        nodeSizeCalculatorNames.remove(name);
        return (NodeSizeCalculator)nodeSizeCalculators.remove(name);
    }
    public NodeSizeCalculator getNodeSizeCalculator(String name) {
        return (NodeSizeCalculator)nodeSizeCalculators.get(name);
    }
    public String checkNodeSizeCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (nodeSizeCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
    
    public Collection getNodeLabelCalculators() {return nodeLabelCalculators.values();}
    public void addNodeLabelCalculator(NodeLabelCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (nodeLabelCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	nodeLabelCalculatorNames.add(name);
        nodeLabelCalculators.put(name, c);
    }
    public NodeLabelCalculator removeNodeLabelCalculator(String name) {
        nodeLabelCalculatorNames.remove(name);
        return (NodeLabelCalculator)nodeLabelCalculators.remove(name);
    }
    public NodeLabelCalculator getNodeLabelCalculator(String name) {
        return (NodeLabelCalculator)nodeLabelCalculators.get(name);
    }
    public String checkNodeLabelCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (nodeLabelCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
    
    public Collection getNodeToolTipCalculators() {return nodeToolTipCalculators.values();}
    public void addNodeToolTipCalculator(NodeToolTipCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (nodeToolTipCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	nodeToolTipCalculatorNames.add(name);
        nodeToolTipCalculators.put(name, c);
    }
    public NodeToolTipCalculator removeNodeToolTipCalculator(String name) {
        nodeToolTipCalculatorNames.remove(name);
        return (NodeToolTipCalculator)nodeToolTipCalculators.remove(name);
    }
    public NodeToolTipCalculator getNodeToolTipCalculator(String name) {
        return (NodeToolTipCalculator)nodeToolTipCalculators.get(name);
    }
    public String checkNodeToolTipCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (nodeToolTipCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
    
    public Collection getEdgeColorCalculators() {return edgeColorCalculators.values();}
    public void addEdgeColorCalculator(EdgeColorCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (edgeColorCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	edgeColorCalculatorNames.add(name);
        edgeColorCalculators.put(name, c);
    }
    public EdgeColorCalculator removeEdgeColorCalculator(String name) {
        edgeColorCalculatorNames.remove(name);
        return (EdgeColorCalculator)edgeColorCalculators.remove(name);
    }
    public EdgeColorCalculator getEdgeColorCalculator(String name) {
        return (EdgeColorCalculator)edgeColorCalculators.get(name);
    }
    public String checkEdgeColorCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (edgeColorCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
    
    public Collection getEdgeLineTypeCalculators() {return edgeLineTypeCalculators.values();}
    public void addEdgeLineTypeCalculator(EdgeLineTypeCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (edgeLineTypeCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	edgeLineTypeCalculatorNames.add(name);
        edgeLineTypeCalculators.put(name, c);
    }
    public EdgeLineTypeCalculator removeEdgeLineTypeCalculator(String name) {
        edgeLineTypeCalculatorNames.remove(name);
        return (EdgeLineTypeCalculator)edgeLineTypeCalculators.remove(name);
    }
    public EdgeLineTypeCalculator getEdgeLineTypeCalculator(String name) {
        return (EdgeLineTypeCalculator)edgeLineTypeCalculators.get(name);
    }
    public String checkEdgeLineTypeCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (edgeLineTypeCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
    
    public Collection getEdgeArrowCalculators() {return edgeArrowCalculators.values();}
    public void addEdgeArrowCalculator(EdgeArrowCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (edgeArrowCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	edgeArrowCalculatorNames.add(name);
        edgeArrowCalculators.put(name, c);
    }
    public EdgeArrowCalculator removeEdgeArrowCalculator(String name) {
        edgeArrowCalculatorNames.remove(name);
        return (EdgeArrowCalculator)edgeArrowCalculators.remove(name);
    }
    public EdgeArrowCalculator getEdgeArrowCalculator(String name) {
        return (EdgeArrowCalculator)edgeArrowCalculators.get(name);
    }
    public String checkEdgeArrowCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (edgeArrowCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
    
    public Collection getEdgeLabelCalculators() {return edgeLabelCalculators.values();}
    public void addEdgeLabelCalculator(EdgeLabelCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (edgeLabelCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	edgeLabelCalculatorNames.add(name);
        edgeLabelCalculators.put(name, c);
    }
    public EdgeLabelCalculator removeEdgeLabelCalculator(String name) {
        edgeLabelCalculatorNames.remove(name);
        return (EdgeLabelCalculator)edgeLabelCalculators.remove(name);
    }
    public EdgeLabelCalculator getEdgeLabelCalculator(String name) {
        return (EdgeLabelCalculator)edgeLabelCalculators.get(name);
    }
    public String checkEdgeLabelCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (edgeLabelCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
    
    public Collection getEdgeToolTipCalculators() {return edgeToolTipCalculators.values();}
    public void addEdgeToolTipCalculator(EdgeToolTipCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (edgeToolTipCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	edgeToolTipCalculatorNames.add(name);
        edgeToolTipCalculators.put(name, c);
    }
    public EdgeToolTipCalculator removeEdgeToolTipCalculator(String name) {
        edgeToolTipCalculatorNames.remove(name);
        return (EdgeToolTipCalculator)edgeToolTipCalculators.remove(name);
    }
    public EdgeToolTipCalculator getEdgeToolTipCalculator(String name) {
        return (EdgeToolTipCalculator)edgeToolTipCalculators.get(name);
    }
    public String checkEdgeToolTipCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (edgeToolTipCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }

    public Collection getEdgeFontFaceCalculators() {return edgeFontFaceCalculators.values();}
    public void addEdgeFontFaceCalculator(EdgeFontFaceCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (edgeFontFaceCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	edgeFontFaceCalculatorNames.add(name);
        edgeFontFaceCalculators.put(name, c);
    }
    public EdgeFontFaceCalculator removeEdgeFontFaceCalculator(String name) {
        edgeFontFaceCalculatorNames.remove(name);
        return (EdgeFontFaceCalculator)edgeFontFaceCalculators.remove(name);
    }
    public EdgeFontFaceCalculator getEdgeFontFaceCalculator(String name) {
        return (EdgeFontFaceCalculator)edgeFontFaceCalculators.get(name);
    }
    public String checkEdgeFontFaceCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (edgeFontFaceCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }

    public Collection getEdgeFontSizeCalculators() {return edgeFontSizeCalculators.values();}
    public void addEdgeFontSizeCalculator(EdgeFontSizeCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (edgeFontSizeCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	edgeFontSizeCalculatorNames.add(name);
        edgeFontSizeCalculators.put(name, c);
    }
    public EdgeFontSizeCalculator removeEdgeFontSizeCalculator(String name) {
        edgeFontSizeCalculatorNames.remove(name);
        return (EdgeFontSizeCalculator)edgeFontSizeCalculators.remove(name);
    }
    public EdgeFontSizeCalculator getEdgeFontSizeCalculator(String name) {
        return (EdgeFontSizeCalculator)edgeFontSizeCalculators.get(name);
    }
    public String checkEdgeFontSizeCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (edgeFontSizeCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }

    public Collection getNodeFontFaceCalculators() {return nodeFontFaceCalculators.values();}
    public void addNodeFontFaceCalculator(NodeFontFaceCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (nodeFontFaceCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	nodeFontFaceCalculatorNames.add(name);
        nodeFontFaceCalculators.put(name, c);
    }
    public NodeFontFaceCalculator removeNodeFontFaceCalculator(String name) {
        nodeFontFaceCalculatorNames.remove(name);
        return (NodeFontFaceCalculator)nodeFontFaceCalculators.remove(name);
    }
    public NodeFontFaceCalculator getNodeFontFaceCalculator(String name) {
        return (NodeFontFaceCalculator)nodeFontFaceCalculators.get(name);
    }
    public String checkNodeFontFaceCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (nodeFontFaceCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }

    public Collection getNodeFontSizeCalculators() {return nodeFontSizeCalculators.values();}
    public void addNodeFontSizeCalculator(NodeFontSizeCalculator c)
	throws DuplicateCalculatorNameException {
	String name = c.toString();
	// check for duplicate names
        if (nodeFontSizeCalculatorNames.contains(name))
	    throw new DuplicateCalculatorNameException("Duplicate calculator name " + name);
	nodeFontSizeCalculatorNames.add(name);
        nodeFontSizeCalculators.put(name, c);
    }
    public NodeFontSizeCalculator removeNodeFontSizeCalculator(String name) {
        nodeFontSizeCalculatorNames.remove(name);
        return (NodeFontSizeCalculator)nodeFontSizeCalculators.remove(name);
    }
    public NodeFontSizeCalculator getNodeFontSizeCalculator(String name) {
        return (NodeFontSizeCalculator)nodeFontSizeCalculators.get(name);
    }
    public String checkNodeFontSizeCalculatorName(String name) {
	String newName = name;
	int nameApp = 2;
	while (nodeFontSizeCalculatorNames.contains(newName)) {
	    newName = name + nameApp;
	    nameApp++;
	}
	return newName;
    }
}
