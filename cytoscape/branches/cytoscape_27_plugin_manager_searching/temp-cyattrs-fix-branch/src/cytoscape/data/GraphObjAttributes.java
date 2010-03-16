package cytoscape.data;

import cytoscape.task.TaskMonitor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * GraphObjAttributes provides access to node and edge attributes within
 * Cytoscape.
 * <P>
 * GraphObjAttributes will be replaced by {@link CyAttributes}, and
 * will be officially removed from the Cytoscape core in
 * September, 2006.
 *
 * @deprecated Use {@link CyAttributes} instead.
 *
 */
public interface GraphObjAttributes {

    //**********************************************************************
    //  The following set of methods have clear equivalent methods and/or
    //  functionality in CyAttributes.
    //  For each method, the @deprecated tag indicates which method in
    //  CyAttributes to use instead.
    //**********************************************************************

    /**
     * Set an attribute of the specified name to the graph object with the
     * specified id.  The attribute may be any java type, or an array of any
     * java type; it can be neither a primitive value nor an array of primitves.
     * If this attribute has not previously been assigned a class, either
     * explicitly or implicitly, then the class is deduced from
     * the object (or the first element in the array of objects).  once a
     * class has been assigned for this attribute, every subsequent addition
     * must be an object of that same class.
     *
     * @param attributeName   Attribute Name.
     * @param id              Unique Identifier.
     * @param value           The value of this attribute, either a java object
     *                        or an array of java objects
     * @return                true indicates that the attribute was set.
     * @throws IllegalArgumentException if the class of obj (or of obj [0],
     *                                  if obj is an array) does not match the
     *                                  already assigned or deduced java class
     *                                  for this attribute.
     * @deprecated Storing arbitrary Java objects as attribute values will
     * no longer be supported in CyAttributes.  However, you will be able
     * to store arbitrarily complex trees of Boolean, Integer, Double,
     * and String Objects.  For details, refer to {@link CyAttributes}.
     */
    public boolean set(String attributeName, String id, Object value);

    /**
     * Appends to specified object to an implicit list of attribute values.
     *
     * @param attributeName     Attribute Name.
     * @param id                Unique Identifier.
     * @param value             The value of this attribute.
     * @return                true indicates that the attribute was set.
     * @deprecated Use
     * {@link CyAttributes#setAttributeList(String, String, java.util.List)}
     * for equivalent functionality.
    */
    public boolean append(String attributeName, String id, Object value);

    /**
     * Sets an id/attributeName pair of type double
     * <P>Value will be promoted to Double.
     *
     * @param attributeName     Attribute Name.
     * @param id                Unique Identifier.
     * @param value             Attribute double value.
     * @return true indicates that the attribute was set.
     * @deprecated  Use
     * {@link CyAttributes#setAttribute(String, String, Double)} instead.
     */
    public boolean set(String attributeName, String id, double value);

    /**
     * Sets a TaskMonitor for tracking loading of node attribute files.
     *
     * @param taskMonitor
     */
    public void setTaskMonitor(TaskMonitor taskMonitor);

    /**
     * Gets the number of different attributes currently registered.
     * @deprecated Use {@link CyAttributes#getAttributeNames()}.length instead.
     */
    public int numberOfAttributes();

    /**
     * Get the names of all registered attributes
     * @deprecated Use {@link CyAttributes#getAttributeNames()} instead.
     */
    public String[] getAttributeNames();

    /**
     * Determines if the specified attribute has been set/registered.
     *
     * @param attributeName     Attribute Name.
     * @return  true indicates that the attribute has been set/registered.
     * @deprecated Use {@link CyAttributes#getType(String)} instead.
     */
    public boolean hasAttribute(String attributeName);

    /**
     * Determines if the specified attributeName/id has been set.
     * @param attributeName     Attribute Name.
     * @param id                Unique Identifier.
     * @return true indicates that the attributeName/id has been set.
     * @deprecated Use {@link CyAttributes#hasAttribute(String, String)} instead.
     */
    public boolean hasAttribute(String attributeName, String id);

    /**
     * Remove the entire second level Hashmap whose key is the specified
     * attributeName.
     * @param attributeName     AttributeName
     * @deprecated Use {@link CyAttributes#deleteAttribute(String)} instead.
     */
    public void deleteAttribute(String attributeName);

    /**
     * Removes the specified attribute from the specified node or edge.
     * @param attributeName     AttributeName
     * @param id                Unique Identifier.
     * @deprecated Use {@link CyAttributes#deleteAttribute(String, String)}  instead.
     */
    public void deleteAttribute(String attributeName, String id);

    /**
     * All attributes are lists (java.lang.Vector) sharing the same base
     * type; discover and return that here.
     * @param attributeName     AttributeName
     * @return Class Object.
     * @deprecated Use {@link CyAttributesUtils#getClass(String,CyAttributes)} instead.
     */
    public Class getClass(String attributeName);

    /**
     * Gets all values associated with this graphObjectName and this
     * attributeName.
     *
     * @param attributeName AttributeName.
     * @param id            Unique Identifier.
     * @return a java.util.Vector of size zero or more, containing java objects
     *         whose types may be learned via a call to getType
     * @deprecated Use {@link CyAttributes#getAttributeList(String, String)}
     * instead.
     */
    public List getList(String attributeName, String id);

    /**
     * For backwards compatibility:  the value of an attribute used to be
     * strictly a single scalar; now -- even though attributes are all lists
     * of scalars -- we support the old idiom by retrieving the first scalar
     * from the list.
     * @param attributeName     AttributeName.
     * @param id                Unique Identifier.
     * @return Java Object.
     * @deprecated Use {@link CyAttributes} instead.  It provides several
     * getter methods for retrieving Boolean, Integer, Double and String values.
     */
    public Object getValue(String attributeName, String id);

    /**
     * Equivalent to getValue() method.
     * @param attributeName     Attribute Name.
     * @param id                Unique Identifier.
     * @return Java Object.
     * @deprecated Use {@link CyAttributes} instead.  It provides several
     * getter methods for retrieving Boolean, Integer, Double and String values.
     */
    public Object get(String attributeName, String id);

    /**
     * Gets an attribute Double value.
     * @param attributeName     Attribute Name.
     * @param id                Unique Identifier.
     * @return Double Object.
     * @deprecated Use
     * {@link CyAttributes#getDoubleAttribute(String, String)} instead.
     */
    public Double getDoubleValue(String attributeName, String id);

    /**
     * Gets an attribute Integer value.
     * @param attributeName     Attribute Name.
     * @param id                Unique Identifier.
     * @return Integer Object.
     * @deprecated Use {@link CyAttributes#getIntegerAttribute(String, String)}
     * instead.
     */
    public Integer getIntegerValue(String attributeName, String id);

     /**
      * Gets an attribute String value.
      * @param attributeName     Attribute Name.
      * @param id                Unique Identifier.
      * @return Integer Object.
      * @deprecated Use {@link CyAttributes#getStringAttribute(String, String)}
      * instead.
     */
    public String getStringValue(String attributeName, String id);

    /**
     * Return a hash whose keys are graphObjectName Strings, and whose values
     * are  a Vector of java objects the class of these objects, and
     * the category of the attribute (annotation, data, URL) may be learned
     * by calling getClass and getCategory.
     *
     * @param attributeName     Attribute Name.
     *
     * @return a HashMap whose keys are graph object names (typically canonical
     * names for nodes and edges) and whose values are Vectors of java objects.
     * @deprecated Use {@link CyAttributesUtils#getAttribute(String, CyAttributes)}
     * instead.
     */
    public HashMap getAttribute(String attributeName);

    /**
     * A convenience method, useful if you are certain that the attribute stores
     * Strings; convert the Vector of Objects into an array of Strings
     *
     * @param attributeName         Attribute Name.
     * @param id                    Unique Identifier.
     * @return Array of Strings.
     * @deprecated Use {@link CyAttributes#getAttributeList(String, String)}
     * instead.
     */
    public String[] getStringArrayValues(String attributeName, String id);

    /**
     * Creates a human readable version.
     * @return Human Readable String.
     */
    public String toString();

    //**********************************************************************
    //  The following set of methods do *not* (yet) have clear equivalent
    //  methods and/or functionality in CyAttributes.
    //  These methods also have 0 usages within the core.
    //**********************************************************************

    /**
     * A convenience method allowing the addition of multiple
     * different attributes for  one graphObject at the same time.
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public boolean set(String graphObjectName, HashMap bundle);

    /**
     * Removes all entries from the nameMap.
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void clearNameMap();

    /**
     * Remove all entries in the canonicalToGraphObject map
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void clearObjectMap();

    /**
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public HashMap getClassMap();

    /**
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void addClassMap(HashMap newClassMap);

    /**
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public HashMap getObjectMap();

    /**
     * A wholesale addition of all entries in a <graphObject> -> <canonicalName>
     * HashMap.
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void addNameMap(HashMap nameMapping);

    /**
     * add all entries in the given HashMap (entry: <canonicalName> -> <graphObject>)
     * to the canonicalToGraphObject HashMap.
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void addObjectMap(HashMap objectMapping);

    /**
     * copy all attributes in the supplied GraphObjAttributes object into this
     * GraphObjAttributes.  any pre-existing attributes survive intact as long
     * as they do not have the same attribute name as the attributes passed
     * in.
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void set(GraphObjAttributes attributes);

    /**
     * remove the specified attribute value from the specified node or edge.
     * there may be multiple values associated with the attribute, so search through
     * the list, and if it is found, remove it.
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void deleteAttributeValue(String attributeName,
            String graphObjectName, Object value);

    /**
     * Deprecation note:  This method has 0 usages in the core.
     * @param file  File Object.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void readAttributesFromFile(File file) throws IOException;

    /**
     * Return attributeName/attributeClass pairs, for every known attribute
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public HashMap getSummary();

    /**
     * multiple GraphObj's (edges in particular) may have the same name; this method
     * counts names which begin with the same string.  for instance
     * there may be two edges between the same pair of nodes:
     * <pre>
     *    VNG0382G (geneFusion) VNG1230G
     *    VNG0382G (geneFusion) VNG1232G
     *  </pre>
     * the first pair encountered may be give the name
     * <p/>
     * <pre>
     *    VNG0382G (geneFusion) VNG1230G
     *  </pre>
     * we may wish to give the second pair the name
     * <p/>
     * <pre>
     *    VNG0382G (geneFusion) VNG1230G_1
     *  </pre>
     * this method provides a count of matches based on
     * String.startsWith ("VNG0382G (geneFusion) VNG1230G") which solves the problem
     * of all subsequent duplicates simply append a number to the base name.
     * <p/>
     * whoever calls this method must construct the new object's name -first-
     * and then is expected to append "_N" where N is the value returned here
     * (and of course, if the result is 0, there is no need to append '_0'.
     *
     * Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public int countIdentical(String graphObjectName);

    /**
     * return the number of graph objects with the specified attribute.
     *
     * Deprecation note:  This method has 0 usage in the core.
     * @deprecated There is no one method in {@link CyAttributes} that provides
     * this functionality, but it can be recreated via several calls.
    */
    public int getObjectCount(String attributeName);

    //**********************************************************************
    //  The following set of methods do *not* (yet) have clear equivalent
    //  methods and/or functionality in CyAttributes.
    //  These method also have >0 usages within the core.
    //  The methods are ordered by usage, e.g. those with the greatest usage
    //  appear first.
    //**********************************************************************

    /**
     * Returns the canonical name associated with this graphObject
     * (a node or edge, previously stored with a call to addNameMapping).
     * if no mapping exists, null is returned.
     *
     * Deprecation notes:  This method has _62_ usages in the core.
     * Canonical name is now the same thing as node/edge identifier.
     * Calling this method is therefore equivalent to calling:
     * node.getIdentifier();
     * edge.getIdentifier();
     * @deprecated Use {@link cytoscape.CyNode#getIdentifier()} or
     * {@link cytoscape.CyEdge#getIdentifier()} instead.
     */
    public String getCanonicalName(Object graphObject);

    /**
     * for the graphObject named by canonicalName, extract and return
     * all attributes.
     *
     * Deprecation note:  This method has 18 usages in the core. most
     * of which are in the VizMapper.
     *
     * @deprecated use {@link CyAttributeUtils.getAttributes(String,CyAttributes)}.
     */
    public HashMap getAttributes(String canonicalName);

    /**
     * establish mapping between a java object (a graph node or edge) and
     * its canonical (standard) name.
     * <ul>
     * <li> clients of this class (CyWindow, or cytoscape plugins) usually
     * deal with graph nodes and edges, and only secondarily with names;
     * <li> attributes are stored and retrieved by canonical name
     * <li> the client must be able to translate from the node or edge object
     * to the name, in order to get at the attributes
     * <li> this method allows a new mapping between object and canonical name.
     *
     * Deprecation note:  This method has 6 usages in the core.
     *
     * @see #getCanonicalName
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void addNameMapping(String canonicalName, Object graphObject);

    /**
     * return the graph object (node or edge) associated with this canonical name,
     * previously stored with a call to addNameMapping. if no mapping exists, null
     * is returned.
     *
     * Deprecation note:  This method has 3 usages in the core.
     * Canonical name is now the same thing as node/edge identifier.
     * This method is therefore equivalent to:
     * Cytoscape.getRootGraph().getNode( identifier );
     * Cytoscape.getRootGraph().getEdge( identifier );
     * See implementation in Rowan's CytocapeDataImpl for details.
     *
     * @deprecated Use {@link cytoscape.Cytoscape#getCyNode(String)} or
     * {@link cytoscape.Cytoscape#getCyEdge(String, String, String, String)} instead.
     */
    public Object getGraphObject(String canonicalName);

    /**
     * return the canonical names of all objects with a given attribute.
     *
     * Deprecation note:  This method has 3 usages in the core.
     * @deprecated Use {@link cytoscape.data.attr.MultiHashMap#getObjectKeys(String)}
     * instead.
     */
    public String[] getObjectNames(String attributeName);

    /**
     * removes a mapping between a java object (a graph node or edge) and
     * its canonical (standard) name.
     *
     * Deprecation note:  This method has 2 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void removeNameMapping(String canonicalName);

    /**
     * specify the class of this attribute.  all subsequently added attribute
     * values must be of the exactly this class.
     *
     * Deprecation note:  This method has 2 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public boolean setClass(String attributeName, Class attributeClass);

    /**
     * removes a mapping between a canonical name and its graph object
     *
     * Deprecation note:  This method has 2 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     * equivalent method will be provided.
     */
    public void removeObjectMapping(Object graphObj);

    /**
     * a convenience method:  convert the Vector of objects into an array.
     *
     * Deprecation note:  This method has 2 usages in the core.
     * @deprecated There is no one method in {@link CyAttributes} that provides
     * this functionality, but it can be recreated via several calls.
    */
    public Object[] getArrayValues(String attributeName,
            String graphObjectName);

    /**
     * Reads attributes from a file.  There is one basic format for attribute
     * files, but a few aspects of the format are flexible.
     * <p/>
     * The simplest form looks like this:
     * <pre>
     *  expresssion ratio
     *  geneA = 0.1
     *  geneB = 8.9
     *  ...
     *  geneZ = 23.2
     *  </pre>
     *
     *
     * Deprecation note:  This method has 2 usages in the core.
     * @deprecated Use {@link CyAttributesReader} instead.
     */
    public void readAttributesFromFile(String filename) throws IOException;

    /**
     * Deprecation note:  This method has 1 usage in the core.
     * @deprecated Use {@link cytoscape.CyNetwork#nodesIterator()} or
     * {@link cytoscape.CyNetwork#edgesIterator()} instead.
     */
    public HashMap getNameMap();

    /**
     * return the unique values among the values of all objects with a
     * given attribute.
     *
     * Deprecation note:  This method has 1 usage in the core.
     * @deprecated There is no one method in {@link CyAttributes} that provides
     * this functionality, but it can be recreated via several calls.
    */
    public Object[] getUniqueValues(String attributeName);

    /**
     * return the unique Strings among the values of all objects with a given
     * attribute.
     *
     * Deprecation note:  This method has 1 usage in the core.
     * @deprecated There is no one method in {@link CyAttributes} that provides
     * this functionality, but it can be recreated via several calls.
    */
    public String[] getUniqueStringValues(String attributeName);

    /**
     * deduce attribute name, category, and java class from the first
     * line of the attributes file.   the form of the first line is
     * <pre>
     *  attribute name  (category=xxxx) (class=yyyy)
     *  </pre>
     * category and class are optional; if absent, class will be inferred
     * (see deduceClass), and category set to DEFAULT_CATEGORY
     * every attribute file must have, at minimum, the name attribute in the
     * first line, possibly with embedded spaces
     * in addition, the first line may have category and class information, as in
     * homologene  (category=staticWebPage) (class=java.net.URL)
     * the present method extracts the mandatory attribute name, and the optional
     * category and class information.
     * <p/>
     * note: category and class information, if present, are not only parsed here:
     * the information is also stored as appropriate in the current class data
     * members.
     *
     * Deprecation note:  This method has 1 usage in the core.
     * @deprecated Use {@link CyAttributesReader} instead.
     */
    public String processFileHeader(String text);


    /**
     * Adds a set of attributes.
     *
     * @param attributes GraphObjAttributes Object.
     *                   Deprecation note:  This method has 0 usages in the core.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     *             equivalent method will be provided.
     */
    public void add(GraphObjAttributes attributes);

    /**
     * Adds a new Attribute.
     *
     * @param attributeName Attribute Name
     * @param id            Unique Identifier
     * @param value         Value
     * @return true indicates success.
     * @deprecated Storing arbitrary Java objects as attribute values will
     *             no longer be supported in CyAttributes.  However, you will be able
     *             to store arbitrarily complex trees of Boolean, Integer, Double,
     *             and String Objects.  For details, refer to {@link CyAttributes}.
     */
    public boolean add(String attributeName, String id, Object value);

    /**
     * Sets an id/attributeName pair of type double
     * <P>Value will be promoted to Double.
     *
     * @param attributeName Attribute Name.
     * @param id            Unique Identifier.
     * @param value         Attribute double value.
     * @return true indicates that the attribute was set.
     * @deprecated Use
     * {@link CyAttributes#setAttribute(String, String, Double)} instead.
     */
    public boolean add(String attributeName, String id, double value);

    /**
     * A convenience method allowing the addition of multiple
     * different attributes for  one graphObject at the same time.
     * <p/>
     * Deprecation note:  This method has 0 usages in the core.
     *
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     *             equivalent method will be provided.
     */
    public boolean add(String graphObjectName, HashMap bundle);

    /**
     * Assigns an arbitrary category name to the specified attribute.
     * @param attributeName     Attribute name
     * @param newValue          New value.
     * @deprecated Method is no longer needed in the Cytoscape core, and no
     *             equivalent method will be provided.
     */
    public void setCategory(String attributeName, String newValue);

    /**
     * Gets a category for the specified attribute.
     * @param attributeName     Attribute name
     * @return Category name.
     */
    public String getCategory(String attributeName);
    
    public final static String DEFAULT_CATEGORY = "unknown";
}
