//AttributeMapperPropertiesAdapter.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
import java.util.*;
//----------------------------------------------------------------------------
/**
 * This class configures an AttributeMapper object using a Properties object
 * that describes the mappings. Also needed is an AttributeMapperCategories
 * object that defines the known range attributes, the property names
 * associated with them, and methods for parsing the String value in
 * the Properties object into an object of the correct class.
 *
 * The Properties object is a Map structure with String keys and String
 * values. To parse this, each range atribute has an associated base key;
 * all properties related to that attribute start with the same base.
 * For example, "node.fill.color" is the base key for the visual attribute
 * of the fill color for a node.
 *
 * The following keys are recognized:
 *
 * <basekey>.default
 *              the matching value specifies the default value for this
 *              range attribute.
 * <basekey>.controller
 *              the matching value specifies the name of the domain attribute
 *              that controls this range attribute.
 * <basekey>.<domainname>.type
 *              the matching value specifies the type of mapping from the
 *              domain attribute called "domainname" to this range attribute.
 *              Valid values are "discrete" and "continuous" (sans quotes).
 *              This key is required if a controller key is present, and
 *              determines what other keys are expected.
 *
 * keys for discrete maps:
 *
 * <basekey>.<domainname>.map.<value>
 *              identifies a particular value for the named domain attribute;
 *              the substring corresponding to <value> will be stored as the
 *              key in a DiscreteMapper, and the matching value will be parsed
 *              into a range attribute value.
 *
 * keys for continuous maps:
 *
 * <basekey>.<domainname>.boundaryvalues
 *              The matching value specifies the number of boundary values
 *              for this map, which should be an integer value.
 *
 * <basekey>.<domainname>.bvX.domainvalue
 *              the matching value specifies a particular domain attribute
 *              value. The "X" should be replaced by an integer between
 *              0 and (boundaryValues-1)
 * <basekey>.<domainname>.bvX.lesser
 *              the matching value specifies a range attribute value to use
 *              when interpolating below the domain boundary value.
 * <basekey>.<domainname>.bvX.equal
 *              the matching value specifies a range attribute value to use
 *              for a domain value exactly equal to the boundary value
 * <basekey>.<domainname>.bvX.greater
 *              the matching value specifies a range attribute value to use
 *              when interpolating above the domain boundary value.
 *
 *  Examples:
 *
 *  node.fill.color.default=255,0,0   //red
 *
 *  node.fill.color.controller=someNumber       //a continuous number
 *  node.fill.color.someNumber.type=continuous
 *  node.fill.color.someNumber.boundaryvalues=2
 *  node.fill.color.someNumber.bv0.domainvalue=-100  //domain from -100 to 100
 *  node.fill.color.someNumber.bv0.lesser=255,0,255 //  special color for
 *  node.fill.color.someNumber.bv0.equal=255,0,255  //  extreme values
 *  node.fill.color.someNumber.bv0.greater=0,0,0       //black near -100
 *  node.fill.color.someNumber.bv1.domainvalue=100
 *  node.fill.color.someNumber.bv1.lesser=255,255,255  //white near 100
 *  node.fill.color.someNumber.bv1.equal=0,255,255   //special color for
 *  node.fill.color.someNumber.bv1.greater=0,255,255 //extreme values
 *
 *  edge.color.default=0,0,0               //black
 *  edge.color.controller=interaction      //discrete attribute
 *  edge.color.interaction.type=discrete
 *  edge.color.interaction.map.pp=0,0,255  //pp interactions are blue
 *  edge.color.interaction.map.pd=255,0,0  //pd interactions are red
 */
public class AttributeMapperPropertiesAdapter {

    private AttributeMapper mapper;
    private AttributeMapperCategories categories;


    public AttributeMapperPropertiesAdapter() {
	this.setAttributeMapper(null);
	this.setAttributeMapperCategories(null);
    }

    public AttributeMapperPropertiesAdapter(AttributeMapper mapper,
					    AttributeMapperCategories c) {
	this.setAttributeMapper(mapper);
	this.setAttributeMapperCategories(c);
    }


    //-------------------------------------------------------------------

    public AttributeMapper getAttributeMapper() {return mapper;}
    public void setAttributeMapper(AttributeMapper mapper) {
	this.mapper = mapper;
    }

    public AttributeMapperCategories getAttributeMapperCategories() {
	return categories;}
    public void setAttributeMapperCategories(AttributeMapperCategories c) {
	this.categories = c;
    }

    //-------------------------------------------------------------------

    /**
     * Applies the mappings from the supplied Properties argument for
     * every range attribute defined by the current AttributeMapperCategories
     * object associated with this object.
     */
    public void applyAllRangeProperties(Properties props) {
	if (props == null || mapper == null || categories == null) {return;}

	Iterator i = categories.getPropertyNamesMap().keySet().iterator();
	for ( ; i.hasNext(); ) {
	    Integer rangeAttribute = (Integer)i.next();
	    this.applyRangeProperties(rangeAttribute,props);
	}
    }

    /**
     * Given a Properties object, applies only the mappings associated
     * with the specified range attribute.
     */
    public void applyRangeProperties(Integer rangeAttribute,
				     Properties props) {
	if (rangeAttribute == null || props == null 
	    || mapper == null || categories == null) {return;}

	String baseKey =
	    (String)categories.getPropertyNamesMap().get(rangeAttribute);
	if (baseKey == null) {//hmm, don't know how to parse this attribute
	    System.err.println("Error parsing attributeMap properties:");
	    System.err.println("    don't know how to parse range attribute "
			       + rangeAttribute.toString() );
	    return;
	}
	String defaultKey = baseKey + ".default";
	if ( props.containsKey(defaultKey) ) {
	    String defaultValString = props.getProperty(defaultKey);
	    Object defaultVal =
		categories.parseRangeAttributeValue(rangeAttribute,
						    defaultValString);
	    mapper.setDefaultValue(rangeAttribute,defaultVal);
	}

	String domainAttrKey = baseKey + ".controller";

	if ( props.containsKey(domainAttrKey) ) {
	    String domainAttrName = props.getProperty(domainAttrKey);
	    String attrBase = baseKey + "." + domainAttrName;
	    String typeKey = attrBase + ".type";
	    String type = props.getProperty(typeKey);
	    if (type == null) {
		System.err.println("Error parsing attributeMap properties:");
		System.err.println("    no property matching: " + typeKey);
		return;
	    } else if ( type.equals("discrete") ) {
		Map valueMap = this.parseDiscreteMap(props,attrBase);
		this.parseDiscreteRangeMap(rangeAttribute, valueMap);
		DiscreteMapper dm = new DiscreteMapper(valueMap);
		this.mapper.setAttributeMapEntry( rangeAttribute,
						  domainAttrName, dm );
		return;
	    } else if ( type.equals("continuous") ) {
		SortedMap valueMap = this.parseContinuousMap(props,attrBase);
		SortedMap parsedMap =
		    this.parseContinuousRangeMap(rangeAttribute, valueMap);
		Interpolator fInt =
		    categories.getInterpolator(rangeAttribute);
		ContinuousMapper cm = new ContinuousMapper(parsedMap, fInt);
		this.mapper.setAttributeMapEntry( rangeAttribute,
						  domainAttrName, cm);
		return;
	    } else {
		System.err.println("Error parsing attributeMap properties:");
		System.err.println("    Unrecognized property value: " + type);
		System.err.println("    for key: " + typeKey);
		System.err.println("    (Expected 'discrete' or 'continuous')");
		return;
	    }
	}
    }

    //-------------------------------------------------------------------

    /**
     * Given a String which forms the base key for a discrete mapping
     * in a Properties object, parses the mapping. The last subunit of the
     * key is extracted as a String representing a domain attribute value,
     * and the matching value in the Properties object is extracted as a
     * String representing the associated range attribute value. These
     * Strings are not parsed here.
     */
    protected Map parseDiscreteMap(Properties props, String baseKey) {
	Map valueMap = new HashMap();
	String mapKey = baseKey + ".map";
	Enumeration eProps = props.propertyNames();
	while (eProps.hasMoreElements()) {
	    String key = (String)eProps.nextElement();
	    if (key.startsWith(mapKey)) {
		String value = props.getProperty(key);
		String domainVal = key.substring(mapKey.length() + 1);
		valueMap.put(domainVal,value);
	    }
	}
	return valueMap;
    }

    /**
     * Given a String which forms the base key for a continuous mapping
     * in a Properties object, parses the mapping. The domain and range
     * values for the boundary values are extracted as Strings and stored
     * in a SortedMap where the keys are Strings (representing the domain
     * values) and the values are BoundaryRangeValues objects that contain
     * the 3 strings representing the range values. These Strings are
     * not parsed into the correct objects here.
     */
    protected SortedMap parseContinuousMap(Properties props, String baseKey) {
	SortedMap valueMap = new TreeMap();

	String bvNumKey = baseKey + ".boundaryvalues";
	String bvNumString = props.getProperty(bvNumKey);
	if (bvNumString == null) {
	    System.err.println("Warning: while parsing attributeMap properties:");
	    System.err.println("    no boundary values specified for");
	    System.err.println("continuous key: " + baseKey);
	    return valueMap;
	}
	int numBV;
	try {
	    numBV = Integer.parseInt(bvNumString);
	} catch (NumberFormatException e) {
	    System.err.println("Error parsing attributeMap properties:");
	    System.err.println("    Expected number value for key: "
			       + bvNumString);
	    return valueMap;
	}
	for (int i=0; i<numBV; i++) {
	    String bvBase = baseKey + ".bv" + Integer.toString(i);
	    String dvKey = bvBase + ".domainvalue";
	    String dvString = props.getProperty(dvKey);
	    if (dvString == null) {
		System.err.println("Error parsing attributeMap properties:");
		System.err.println("    expected numerical value for key: "
				   + dvKey);
		continue;
	    }
	    BoundaryRangeValues bv = new BoundaryRangeValues();
	    String lKey = bvBase + ".lesser";
	    String lString = props.getProperty(lKey);
	    bv.lesserValue = lString;
	    String eKey = bvBase + ".equal";
	    String eString = props.getProperty(eKey);
	    bv.equalValue = eString;
	    String gKey = bvBase + ".greater";
	    String gString = props.getProperty(gKey);
	    bv.greaterValue = gString;

	    valueMap.put(dvString,bv);
	}

	return valueMap;
    }

    //-------------------------------------------------------------------

    /**
     * Given a Map representing a discrete mapping, as created by the
     * parseDiscreteMap method, replaces the String values with the parsed
     * object returned by the parseRangeAttributeValue method of the
     * AttributemapperCategories object associated with this object.
     */
    protected void parseDiscreteRangeMap(Integer rangeAttribute,
					 Map valueMap) {
	Iterator i = valueMap.entrySet().iterator();
	for ( ; i.hasNext(); ) {
	    Map.Entry e = (Map.Entry)i.next();
	    String value = (String)e.getValue();
	    Object newValue =
		categories.parseRangeAttributeValue(rangeAttribute,value);
	    if (newValue == null) {
		i.remove();
	    } else {
		e.setValue(newValue);
	    }
	}
    }

    /**
     * Given a SortedMap representing a continuous mapping, as created by
     * the parseContinuousMap method, returns a new map in which the
     * keys have been parsed into Double objects and the member fields in
     * the BoundaryRangeValues objects have been parsed by calling the
     * parseRangeAttributeValue method of the AttributeMapperCategories
     * object associated with this object.
     */
    protected SortedMap parseContinuousRangeMap(Integer rangeAttribute,
						SortedMap valueMap) {
	SortedMap parsedMap = new TreeMap();
	Iterator i = valueMap.keySet().iterator();
	for ( ; i.hasNext(); ) {
	    /* extract the info for this key and value */
	    String key = (String)i.next();
	    BoundaryRangeValues bv = (BoundaryRangeValues)valueMap.get(key);

	    /* key should be a number; parse into a Double */
	    Double dVal = null;
	    try {
		dVal = Double.valueOf(key);
	    } catch (NumberFormatException e) {
		System.err.println("Error parsing attributeMap properties:");
		System.err.println("    expected number value for key: "+ key);
		continue;
	    }

	    /* parse the Strings stored in the BoundaryRangeValues object */
	    String lString = (String)bv.lesserValue;
	    Object lVal =
		categories.parseRangeAttributeValue(rangeAttribute,lString);
	    if (lVal == null) {continue;} else {bv.lesserValue = lVal;}
	    String eString = (String)bv.equalValue;
	    Object eVal =
		categories.parseRangeAttributeValue(rangeAttribute,eString);
	    if (eVal == null) {continue;} else {bv.equalValue = eVal;}
	    String gString = (String)bv.greaterValue;
	    Object gVal =
		categories.parseRangeAttributeValue(rangeAttribute,gString);
	    if (gVal == null) {continue;} else {bv.greaterValue = gVal;}

	    /* the parsed key and value need to go into a new map, since
	     * we've changed the type of the key */
	    parsedMap.put(dVal,bv);
	}
	return parsedMap;
    }
}
