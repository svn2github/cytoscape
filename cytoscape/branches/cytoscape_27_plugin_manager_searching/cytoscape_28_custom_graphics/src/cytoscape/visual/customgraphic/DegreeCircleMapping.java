package cytoscape.visual.customgraphic;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import cytoscape.CyNetwork;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.NodeShape;
import cytoscape.visual.SubjectBase;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.discrete.DiscreteLegend;
import cytoscape.visual.mappings.discrete.DiscreteMappingReader;
import cytoscape.visual.mappings.discrete.DiscreteMappingWriter;
import cytoscape.visual.mappings.discrete.DiscreteRangeCalculator;
import cytoscape.visual.parsers.ValueParser;

public class DegreeCircleMapping extends SubjectBase implements ObjectMapping<CyCustomGraphics<CustomGraphic>> {

	private static final Class<?>[] ACCEPTED_CLASSES = { String.class,
			Number.class, Integer.class, Double.class, Float.class, Long.class,
			Short.class, NodeShape.class, List.class, Boolean.class };
	
	private CyCustomGraphics<CustomGraphic> defaultObj; // the default value held by this mapping
	private Class<?> rangeClass; // the valid range class for this mapping
	String attrName; // the name of the controlling data attribute
	protected byte mapType; // node or edge; specifies which attributes
	// to use.
	private TreeMap treeMap; // contains the actual map elements (sorted)
	private Object lastKey;

	/**
	 * Constructor.
	 * 
	 * @param defObj
	 *            Default Object.
	 * @param mapType
	 *            Map Type, ObjectMapping.EDGE_MAPPING or
	 *            ObjectMapping.NODE_MAPPING.
	 */
	public DegreeCircleMapping(CyCustomGraphics<CustomGraphic> defObj, byte mapType) {
		this(defObj, null, mapType);
	}

	/**
	 * Constructor.
	 * 
	 * @param defObj
	 *            Default Object.
	 * @param attrName
	 *            Controlling Attribute Name.
	 * @param mapType
	 *            Map Type, ObjectMapping.EDGE_MAPPING or
	 *            ObjectMapping.NODE_MAPPING.
	 */
	public DegreeCircleMapping(CyCustomGraphics<CustomGraphic> defObj, String attrName, byte mapType) {
		treeMap = new TreeMap();

		this.defaultObj = defObj;
		this.rangeClass = defObj.getClass();

		if ((mapType != ObjectMapping.EDGE_MAPPING)
				&& (mapType != ObjectMapping.NODE_MAPPING))
			throw new IllegalArgumentException("Unknown mapping type "
					+ mapType);

		this.mapType = mapType;

		if (attrName != null)
			setControllingAttributeName(attrName, null, false);
	}

	/**
	 * Clones the Object.
	 * 
	 * @return DiscreteMapping Object.
	 */
	public Object clone() {
		return null;
	}

	/**
	 * Gets Value for Specified Key.
	 * 
	 * @param key
	 *            String Key.
	 * @return Object.
	 */
	public Object getMapValue(Object key) {
		return treeMap.get(key);
	}

	/**
	 * Puts New Key/Value in Map.
	 * 
	 * @param key
	 *            Key Object.
	 * @param value
	 *            Value Object.
	 */
	public void putMapValue(Object key, Object value) {
		lastKey = key;

		treeMap.put(key, value);
		fireStateChanged();
	}

	/**
	 * Gets the Last Modified Key.
	 * 
	 * @return Key Object.
	 */
	public Object getLastKeyModified() {
		return lastKey;
	}

	/**
	 * Adds All Members of Specified Map.
	 * 
	 * @param map
	 *            Map.
	 */
	public void putAll(Map<Object, Object> map) {
		treeMap.putAll(map);
	}

	// AJK: 05/05/06 BEGIN
	/**
	 * gets all map values
	 * 
	 */
	public Map getAll() {
		return treeMap;
	}

	// AJK: 05/05/06 END

	/**
	 * Gets the Range Class. Required by the ObjectMapping interface.
	 * 
	 * @return Class object.
	 */
	public Class getRangeClass() {
		return rangeClass;
	}

	/**
	 * Gets Accepted Data Classes. Required by the ObjectMapping interface.
	 * 
	 * @return ArrayList of Class objects.
	 */
	public Class<?>[] getAcceptedDataClasses() {
		return ACCEPTED_CLASSES;
	}

	/**
	 * Gets the Name of the Controlling Attribute. Required by the ObjectMapping
	 * interface.
	 * 
	 * @return Attribue Name.
	 */
	public String getControllingAttributeName() {
		return attrName;
	}

	/**
	 * Call whenever the controlling attribute changes. If preserveMapping is
	 * true, all the currently stored mappings are unchanged; otherwise all the
	 * mappings are cleared. In either case, this method calls {@link #getUI} to
	 * rebuild the UI for this mapping, which in turn calls loadKeys to load the
	 * current data values for the new attribute.
	 * <p>
	 * Called by event handler from AbstractCalculator
	 * {@link cytoscape.visual.calculators.AbstractCalculator}.
	 * 
	 * @param attrName
	 *            The name of the new attribute to map to
	 */
	public void setControllingAttributeName(String attrName, CyNetwork n,
			boolean preserveMapping) {
		this.attrName = attrName;

		if (preserveMapping == false) {
			treeMap = new TreeMap();
		}
	}

	/**
	 * Customizes this object by applying mapping defintions described by the
	 * supplied Properties argument. Required by the ObjectMapping interface.
	 * 
	 * @param props
	 *            Properties Object.
	 * @param baseKey
	 *            Base Key for finding properties.
	 * @param parser
	 *            ValueParser Object.
	 */
	public void applyProperties(Properties props, String baseKey,
			ValueParser parser) {
		DiscreteMappingReader reader = new DiscreteMappingReader(props,
				baseKey, parser);
		String contValue = reader.getControllingAttributeName();

		if (contValue != null)
			setControllingAttributeName(contValue, null, false);

		this.treeMap = reader.getMap();
	}

	/**
	 * Returns a Properties object with entries suitable for customizing this
	 * object via the applyProperties method. Required by the ObjectMapping
	 * interface.
	 * 
	 * @param baseKey
	 *            Base Key for creating properties.
	 * @return Properties Object.
	 */
	public Properties getProperties(String baseKey) {
		DiscreteMappingWriter writer = new DiscreteMappingWriter(attrName,
				baseKey, treeMap);

		return writer.getProperties();
	}

	/**
	 * Calculates the Range Value. Required by the ObjectMapping interface.
	 * 
	 * @param attrBundle
	 *            A Bundle of Attributes.
	 * @return Mapping object.
	 */
	public CyCustomGraphics<CustomGraphic> calculateRangeValue(Map<String, Object> attrBundle) {
//		final DiscreteRangeCalculator calculator = new DiscreteRangeCalculator(
//				treeMap, attrName);
//
//		return calculator.calculateRangeValue(attrBundle);
		return null;
	}

	
	public JPanel getUI(JDialog parent, CyNetwork network) {
		return null;
	}

	
	public JPanel getLegend(VisualPropertyType vpt) {
		return null;
	}
}
