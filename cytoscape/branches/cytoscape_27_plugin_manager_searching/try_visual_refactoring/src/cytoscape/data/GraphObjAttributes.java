
/*
  File: GraphObjAttributes.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.data;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextHttpReader;
import cytoscape.data.readers.TextJarReader;
import cytoscape.task.TaskMonitor;
import cytoscape.util.Misc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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
public class GraphObjAttributes
{

  private final HashMap m_localMap = new HashMap();
  private final CyAttributes m_cyAttrs;
  private TaskMonitor m_taskMonitor;

  /**
   * Constructor.
   * @param cyAttrs CyAttributes.
   */
  public GraphObjAttributes(CyAttributes cyAttrs)
  {
    m_cyAttrs = cyAttrs;
  }

  /**
   * Deduces a Class.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public static Class deduceClass(String string)
  {
    String[] classNames = { //"java.net.URL",
                            "java.lang.Integer",
                            "java.lang.Double",
                            "java.lang.String" };

    for (int i = 0; i < classNames.length; i++) {
      try {
        Object obj = createInstanceFromString
          (Class.forName(classNames[i]), string);
        return obj.getClass(); }
      catch (Exception e) {
        ;
      }
    }
    return null;
  }

  private static Object createInstanceFromString(Class requestedClass,
                                          String ctorArg) throws Exception
  {
    Class[] ctorArgsClasses = new Class[1];
    ctorArgsClasses[0] = Class.forName("java.lang.String");
    Object[] ctorArgs = new Object[1];
    ctorArgs[0] = new String(ctorArg);
    Constructor ctor = requestedClass.getConstructor(ctorArgsClasses);
    return ctor.newInstance(ctorArgs);
  }

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
  public boolean set(String attributeName, String id, Object value)
  {
    if (value instanceof Boolean) {
      m_cyAttrs.setAttribute(id, attributeName, (Boolean) value);
      return true; }
    else if (value instanceof Integer) {
      m_cyAttrs.setAttribute(id, attributeName, (Integer) value);
      return true; }
    else if (value instanceof Double) {
      m_cyAttrs.setAttribute(id, attributeName, (Double) value);
      return true; }
    else if (value instanceof String) {
      m_cyAttrs.setAttribute(id, attributeName, (String) value);
      return true; }
    HashMap stupidMap = (HashMap) m_stupidMaps.get(attributeName);
    if (stupidMap == null) {
      stupidMap = new HashMap();
      m_stupidMaps.put(attributeName, stupidMap); }
    stupidMap.put(id, value);
    return true;
  }

  private final HashMap m_stupidMaps = new HashMap();

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
  public boolean append(String attributeName, String id, Object value)
  {
    List l = m_cyAttrs.getAttributeList(id, attributeName);
    if (l == null) { l = new ArrayList(); }
    l.add(value);
    m_cyAttrs.setAttributeList(id, attributeName, l);
    return true;
  }

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
  public boolean set(String attributeName, String id, double value)
  {
    return set(attributeName, id, new Double(value));
  }

  /**
   * Sets a TaskMonitor for tracking loading of node attribute files.
   *
   * @param taskMonitor
   * @deprecated
   */
  public void setTaskMonitor(TaskMonitor taskMonitor)
  {
    m_taskMonitor = taskMonitor;
  }

  /**
   * Gets the number of different attributes currently registered.
   * @deprecated Use {@link CyAttributes#getAttributeNames()}.length instead.
   */
  public int numberOfAttributes()
  {
    return m_cyAttrs.getMultiHashMapDefinition().getDefinedAttributes().
      numRemaining();
  }

  /**
   * Get the names of all registered attributes
   * @deprecated Use {@link CyAttributes#getAttributeNames()} instead.
   */
  public String[] getAttributeNames()
  {
    return m_cyAttrs.getAttributeNames();
  }

  /**
   * Determines if the specified attribute has been set/registered.
   *
   * @param attributeName     Attribute Name.
   * @return  true indicates that the attribute has been set/registered.
   * @deprecated Use {@link CyAttributes#getType(String)} instead.
   */
  public boolean hasAttribute(String attributeName)
  {
    return m_cyAttrs.getType(attributeName) != CyAttributes.TYPE_UNDEFINED;
  }

  /**
   * Determines if the specified attributeName/id has been set.
   * @param attributeName     Attribute Name.
   * @param id                Unique Identifier.
   * @return true indicates that the attributeName/id has been set.
   * @deprecated Use {@link CyAttributes#hasAttribute(String, String)} instead.
   */
  public boolean hasAttribute(String attributeName, String id)
  {
    return m_cyAttrs.hasAttribute(id, attributeName);
  }

  /**
   * Remove the entire second level Hashmap whose key is the specified
   * attributeName.
   * @param attributeName     AttributeName
   * @deprecated Use {@link CyAttributes#deleteAttribute(String)} instead.
   */
  public void deleteAttribute(String attributeName)
  {
    m_cyAttrs.deleteAttribute(attributeName);
  }

  /**
   * Removes the specified attribute from the specified node or edge.
   * @param attributeName     AttributeName
   * @param id                Unique Identifier.
   * @deprecated Use {@link CyAttributes#deleteAttribute(String, String)}  instead.
   */
  public void deleteAttribute(String attributeName, String id)
  {
    m_cyAttrs.deleteAttribute(id, attributeName);
  }

  /**
   * All attributes are lists (java.lang.Vector) sharing the same base
   * type; discover and return that here.
   * @param attributeName     AttributeName
   * @return Class Object.
   * @deprecated Use {@link CyAttributesUtils#getClass(String,CyAttributes)} instead.
   */
  public Class getClass(String attributeName)
  {
    switch (m_cyAttrs.getMultiHashMapDefinition().
            getAttributeValueType(attributeName)) {
    case MultiHashMapDefinition.TYPE_BOOLEAN:
      return Boolean.class;
    case MultiHashMapDefinition.TYPE_INTEGER:
      return Integer.class;
    case MultiHashMapDefinition.TYPE_FLOATING_POINT:
      return Double.class;
    case MultiHashMapDefinition.TYPE_STRING:
      return String.class;
    default:
      return null; }
  }

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
  public List getList(String attributeName, String id)
  {
    return m_cyAttrs.getAttributeList(id, attributeName);
  }

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
  public Object getValue(String attributeName, String id)
  {
    switch (m_cyAttrs.getType(attributeName)) {
    case CyAttributes.TYPE_BOOLEAN:
      return m_cyAttrs.getBooleanAttribute(id, attributeName);
    case CyAttributes.TYPE_INTEGER:
      return m_cyAttrs.getIntegerAttribute(id, attributeName);
    case CyAttributes.TYPE_FLOATING:
      return m_cyAttrs.getDoubleAttribute(id, attributeName);
    case CyAttributes.TYPE_STRING:
      return m_cyAttrs.getStringAttribute(id, attributeName);
    case CyAttributes.TYPE_SIMPLE_LIST:
      List l = m_cyAttrs.getAttributeList(id, attributeName);
      if (l.size() > 0) return l.get(0);
      else return null;
    case CyAttributes.TYPE_SIMPLE_MAP:
      return m_cyAttrs.getAttributeMap(id, attributeName);
    default:
      return null; }
  }

  /**
   * Equivalent to getValue() method.
   * @param attributeName     Attribute Name.
   * @param id                Unique Identifier.
   * @return Java Object.
   * @deprecated Use {@link CyAttributes} instead.  It provides several
   * getter methods for retrieving Boolean, Integer, Double and String values.
   */
  public Object get(String attributeName, String id)
  {
    HashMap stupidMap;
    if ((stupidMap = (HashMap) m_stupidMaps.get(attributeName)) != null) {
      return stupidMap.get(id); }
    return getValue(attributeName, id);
  }

  /**
   * Gets an attribute Double value.
   * @param attributeName     Attribute Name.
   * @param id                Unique Identifier.
   * @return Double Object.
   * @deprecated Use
   * {@link CyAttributes#getDoubleAttribute(String, String)} instead.
   */
  public Double getDoubleValue(String attributeName, String id)
  {
    if (m_cyAttrs.getType(attributeName) == CyAttributes.TYPE_FLOATING) {
      return m_cyAttrs.getDoubleAttribute(id, attributeName); }
    else if (m_cyAttrs.getType(attributeName) ==
             CyAttributes.TYPE_SIMPLE_LIST &&
             m_cyAttrs.getMultiHashMapDefinition().
             getAttributeValueType(attributeName) ==
             MultiHashMapDefinition.TYPE_FLOATING_POINT) {
      List l = m_cyAttrs.getAttributeList(id, attributeName);
      if (l != null && l.size() > 0) {
        return (Double) l.get(0); } }
    return null;
  }

  /**
   * Gets an attribute Integer value.
   * @param attributeName     Attribute Name.
   * @param id                Unique Identifier.
   * @return Integer Object.
   * @deprecated Use {@link CyAttributes#getIntegerAttribute(String, String)}
   * instead.
   */
  public Integer getIntegerValue(String attributeName, String id)
  {
    if (m_cyAttrs.getType(attributeName) == CyAttributes.TYPE_INTEGER) {
      return m_cyAttrs.getIntegerAttribute(id, attributeName); }
    else if (m_cyAttrs.getType(attributeName) ==
             CyAttributes.TYPE_SIMPLE_LIST &&
             m_cyAttrs.getMultiHashMapDefinition().
             getAttributeValueType(attributeName) ==
             MultiHashMapDefinition.TYPE_INTEGER) {
      List l = m_cyAttrs.getAttributeList(id, attributeName);
      if (l != null && l.size() > 0) {
        return (Integer) l.get(0); } }
    return null;
  }

   /**
    * Gets an attribute String value.
    * @param attributeName     Attribute Name.
    * @param id                Unique Identifier.
    * @return Integer Object.
    * @deprecated Use {@link CyAttributes#getStringAttribute(String, String)}
    * instead.
   */
  public String getStringValue(String attributeName, String id)
  {
    if (m_cyAttrs.getType(attributeName) == CyAttributes.TYPE_STRING) {
      return m_cyAttrs.getStringAttribute(id, attributeName); }
    else if (m_cyAttrs.getType(attributeName) ==
             CyAttributes.TYPE_SIMPLE_LIST &&
             m_cyAttrs.getMultiHashMapDefinition().
             getAttributeValueType(attributeName) ==
             MultiHashMapDefinition.TYPE_STRING) {
      List l = m_cyAttrs.getAttributeList(id, attributeName);
      if (l != null && l.size() > 0) {
        return (String) l.get(0); } }
    return null;
  }

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
  public HashMap getAttribute(String attributeName)
  {
    final HashMap returnThis = new HashMap();
    final MultiHashMap mmap = m_cyAttrs.getMultiHashMap();
    final MultiHashMapDefinition mmapDef =
      m_cyAttrs.getMultiHashMapDefinition();
    if (mmapDef.getAttributeValueType(attributeName) != -1) {
      final Iterator objs = mmap.getObjectKeys(attributeName);
      while (objs.hasNext()) {
        final String obj = (String) objs.next();
        returnThis.put(obj, get(attributeName, obj)); } }
    return returnThis.size() == 0 ? null : returnThis;
  }

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
  public String[] getStringArrayValues(String attributeName, String id)
  {
    List l = m_cyAttrs.getAttributeList(id, attributeName);
    if (l == null) {
      final String strVal = m_cyAttrs.getStringAttribute(id, attributeName);
      if (strVal == null) return new String[0];
      else return new String[] { strVal }; }
    else {
      final String[] returnThis = new String[l.size()];
      final Object[] arr = l.toArray();
      System.arraycopy(arr, 0, returnThis, 0, arr.length);
      return returnThis; }
  }

  public String toString()
  {
    return "Greetings.  This is a human readable string.";
  }

  /**
   * A convenience method allowing the addition of multiple
   * different attributes for  one graphObject at the same time.
   *
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public boolean set(String graphObjName, HashMap bundle)
  {
    final Iterator keys = bundle.keySet().iterator();
    while (keys.hasNext()) {
      final String attrName = (String) keys.next();
      set(attrName, graphObjName, bundle.get(attrName)); }
    return true;
  }

  /**
   * Removes all entries from the nameMap.
   *
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public void clearNameMap()
  {
  }

  /**
   * Remove all entries in the canonicalToGraphObject map
   *
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public void clearObjectMap()
  {
  }

  /**
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public HashMap getClassMap()
  {
    return null;
  }

  /**
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public void addClassMap(HashMap newClassMap)
  {
  }

  /**
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public HashMap getObjectMap()
  {
    return null;
  }

  /**
   * A wholesale addition of all entries in a <graphObject> -> <canonicalName>
   * HashMap.
   *
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public void addNameMap(HashMap nameMapping)
  {
    Iterator keys = nameMapping.keySet().iterator();
    while (keys.hasNext()) {
      Object key = keys.next();
      addNameMapping((String) nameMapping.get(key), key); }
  }

  /**
   * add all entries in the given HashMap (entry: <canonicalName> -> <graphObject>)
   * to the canonicalToGraphObject HashMap.
   *
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public void addObjectMap(HashMap objectMapping)
  {
  }

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
  public void set(GraphObjAttributes attributes)
  {
  }

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
                                   String graphObjName, Object value)
  {
    List l = m_cyAttrs.getAttributeList(graphObjName, attributeName);
    if (l == null) return;
    l.remove(value);
    if (l.size() > 0) {
      m_cyAttrs.setAttributeList(graphObjName, attributeName, l); }
    else {
      m_cyAttrs.deleteAttribute(graphObjName, attributeName); }
  }

  /**
   * Deprecation note:  This method has 0 usages in the core.
   * @param file  File Object.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public void readAttributesFromFile(File file) throws IOException
  {
    readAttributesFromFile(file.getPath());
  }

  /**
   * Return attributeName/attributeClass pairs, for every known attribute
   *
   * Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public HashMap getSummary()
  {
    return null;
  }

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
  public int countIdentical(String graphObjName)
  {
    return 0;
  }

  /**
   * return the number of graph objects with the specified attribute.
   *
   * Deprecation note:  This method has 0 usage in the core.
   * @deprecated There is no one method in {@link CyAttributes} that provides
   * this functionality, but it can be recreated via several calls.
  */
  public int getObjectCount(String attributeName)
  {
    try {
      return m_cyAttrs.getMultiHashMap().
        getObjectKeys(attributeName).numRemaining(); }
    catch (RuntimeException e) { return 0; }
  }

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
  public String getCanonicalName(Object graphObj)
  {
//     return ((giny.model.GraphObject) graphObj).getIdentifier();

//     final Set entrySet = m_localMap.entrySet();
//     final Iterator setIter = entrySet.iterator();
//     Object key = null;
//     while (setIter.hasNext()) {
//       final Map.Entry entry = (Map.Entry) setIter.next();
//       if (entry.getValue() == graphObj) {
//         key = entry.getKey();
//         break; } }
//     return (String) key;

    return (String) m_localMap.get(graphObj);
  }

  /**
   * for the graphObject named by canonicalName, extract and return
   * all attributes.
   *
   * Deprecation note:  This method has 18 usages in the core. most
   * of which are in the VizMapper.
   *
   * @deprecated use {@link CyAttributeUtils.getAttributes(String,CyAttributes)}.
   */
  public HashMap getAttributes(String canonicalName)
  {
    // Populate hashmap with single-valued attributes.

    final HashMap returnThis = new HashMap();
    final String[] attrNames = m_cyAttrs.getAttributeNames();
    for (int i = 0; i < attrNames.length; i++) {
      final byte type = m_cyAttrs.getType(attrNames[i]);
      if (m_cyAttrs.hasAttribute(canonicalName, attrNames[i])) {
        if (type == CyAttributes.TYPE_SIMPLE_LIST) {
          List l = m_cyAttrs.getAttributeList(canonicalName, attrNames[i]);
          if (l != null && l.size() > 0) {
            returnThis.put(attrNames[i], l.get(0)); } }
        else if (type == CyAttributes.TYPE_BOOLEAN) {
          returnThis.put
            (attrNames[i],
             m_cyAttrs.getBooleanAttribute(canonicalName, attrNames[i])); }
        else if (type == CyAttributes.TYPE_INTEGER) {
          returnThis.put
            (attrNames[i],
             m_cyAttrs.getIntegerAttribute(canonicalName, attrNames[i])); }
        else if (type == CyAttributes.TYPE_FLOATING) {
          returnThis.put
            (attrNames[i],
             m_cyAttrs.getDoubleAttribute(canonicalName, attrNames[i])); }
        else if (type == CyAttributes.TYPE_STRING) {
          returnThis.put
            (attrNames[i],
             m_cyAttrs.getStringAttribute(canonicalName, attrNames[i])); } } }
    return returnThis;
  }

  /**
   * establish mapping between a java object (a graph node or edge) and
   * its canonical (standard) name.
   * <ul>
   * <li> clients of this class (view, or cytoscape plugins) usually
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
  public void addNameMapping(String canonicalName, Object graphObject)
  {
    m_localMap.put(graphObject, canonicalName);
  }

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
  public Object getGraphObject(String canonicalName)
  {
//     return m_localMap.get(canonicalName);

    final Set entrySet = m_localMap.entrySet();
    final Iterator setIter = entrySet.iterator();
    Object key = null;
    while (setIter.hasNext()) {
      final Map.Entry entry = (Map.Entry) setIter.next();
      if (entry.getValue().equals(canonicalName)) {
        key = entry.getKey();
        break; } }
    return key;
  }

  /**
   * return the canonical names of all objects with a given attribute.
   *
   * Deprecation note:  This method has 3 usages in the core.
   * @deprecated Use {@link cytoscape.data.attr.MultiHashMap#getObjectKeys(String)}
   * instead.
   */
  public String[] getObjectNames(String attributeName)
  {
    final MultiHashMap mmap = m_cyAttrs.getMultiHashMap();
    final CountedIterator keys = mmap.getObjectKeys(attributeName);
    final String[] returnThis = new String[keys.numRemaining()];
    int inx = 0;
    while (keys.hasNext()) {
      returnThis[inx++] = (String) keys.next(); }
    return returnThis;
  }

  /**
   * removes a mapping between a java object (a graph node or edge) and
   * its canonical (standard) name.
   *
   * Deprecation note:  This method has 2 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public void removeNameMapping(String canonicalName)
  {
//     m_localMap.remove(canonicalName);

    final Set entrySet = m_localMap.entrySet();
    final Iterator setIter = entrySet.iterator();
    Object key = null;
    while (setIter.hasNext()) {
      final Map.Entry entry = (Map.Entry) setIter.next();
      if (entry.getValue().equals(canonicalName)) {
        key = entry.getKey();
        break; } }
    if (key != null) m_localMap.remove(key);
  }

  /**
   * specify the class of this attribute.  all subsequently added attribute
   * values must be of the exactly this class.
   *
   * Deprecation note:  This method has 2 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public boolean setClass(String attributeName, Class attributeClass)
  {
    final MultiHashMapDefinition mmapDef =
      m_cyAttrs.getMultiHashMapDefinition();
    try {
      if (attributeClass.equals(Boolean.class)) {
        mmapDef.defineAttribute
          (attributeName,
           MultiHashMapDefinition.TYPE_BOOLEAN,
           new byte[] { MultiHashMapDefinition.TYPE_INTEGER }); }
      else if (attributeClass.equals(Integer.class)) {
        mmapDef.defineAttribute
          (attributeName,
           MultiHashMapDefinition.TYPE_INTEGER,
           new byte[] { MultiHashMapDefinition.TYPE_INTEGER }); }
      else if (attributeClass.equals(Double.class)) {
        mmapDef.defineAttribute
          (attributeName,
           MultiHashMapDefinition.TYPE_FLOATING_POINT,
           new byte[] { MultiHashMapDefinition.TYPE_INTEGER }); }
      else if (attributeClass.equals(String.class)) {
        mmapDef.defineAttribute
          (attributeName,
           MultiHashMapDefinition.TYPE_STRING,
           new byte[] { MultiHashMapDefinition.TYPE_INTEGER }); }
      else return false; }
    catch (Exception e) { return false; }
    return true;
  }

  /**
   * removes a mapping between a canonical name and its graph object
   *
   * Deprecation note:  This method has 2 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   * equivalent method will be provided.
   */
  public void removeObjectMapping(Object graphObj)
  {
//     final Set entrySet = m_localMap.entrySet();
//     final Iterator setIter = entrySet.iterator();
//     Object key = null;
//     while (setIter.hasNext()) {
//       final Map.Entry entry = (Map.Entry) setIter.next();
//       if (entry.getValue() == graphObj) {
//         key = entry.getKey();
//         break; } }
//     if (key != null) m_localMap.remove(key);

    m_localMap.remove(graphObj);
  }

  /**
   * a convenience method:  convert the Vector of objects into an array.
   *
   * Deprecation note:  This method has 2 usages in the core.
   * @deprecated There is no one method in {@link CyAttributes} that provides
   * this functionality, but it can be recreated via several calls.
  */
  public Object[] getArrayValues(String attributeName,
                                 String graphObjectName)
  {
    // If this attributeName is List, convert to array.
    // Otherwise return new Object[0].
    if (m_cyAttrs.getType(attributeName) != CyAttributes.TYPE_SIMPLE_LIST) {
      return new Object[0]; }
    return m_cyAttrs.getAttributeList(graphObjectName,
                                      attributeName).toArray();
  }

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
  public void readAttributesFromFile(String filename) throws IOException
  {
    CyAttributesReader.loadAttributes(m_cyAttrs, new FileReader(filename));
  }

  /**
   * Deprecation note:  This method has 1 usage in the core.
   * @deprecated Use {@link cytoscape.CyNetwork#nodesIterator()} or
   * {@link cytoscape.CyNetwork#edgesIterator()} instead.
   */
  public HashMap getNameMap()
  {
    // Returns reference to local obj->name hashmap.
    return m_localMap;
  }

  /**
   * return the unique values among the values of all objects with a
   * given attribute.
   *
   * Deprecation note:  This method has 1 usage in the core.
   * @deprecated There is no one method in {@link CyAttributes} that provides
   * this functionality, but it can be recreated via several calls.
  */
  public Object[] getUniqueValues(String attributeName)
  {
    final HashMap dupsFilter = new HashMap();
    final MultiHashMapDefinition mmapDef =
      m_cyAttrs.getMultiHashMapDefinition();
    final MultiHashMap mmap = m_cyAttrs.getMultiHashMap();
    final byte type = m_cyAttrs.getType(attributeName);
    if (type == CyAttributes.TYPE_SIMPLE_LIST) {
      final Iterator objs = mmap.getObjectKeys(attributeName);
      while (objs.hasNext()) {
        final String obj = (String) objs.next();
        final List l = m_cyAttrs.getAttributeList(obj, attributeName);
        final Iterator liter = l.iterator();
        while (liter.hasNext()) {
          final Object val = liter.next();
          dupsFilter.put(val, val); } } }
    else if (type == CyAttributes.TYPE_BOOLEAN ||
             type == CyAttributes.TYPE_FLOATING ||
             type == CyAttributes.TYPE_INTEGER ||
             type == CyAttributes.TYPE_STRING) {
      final Iterator objs = mmap.getObjectKeys(attributeName);
      while (objs.hasNext()) {
        final String obj = (String) objs.next();
        final Object val = mmap.getAttributeValue(obj, attributeName, null);
        dupsFilter.put(val, val); } }
    else { return new Object[0]; }
    final Object[] returnThis = new Object[dupsFilter.size()];
    Iterator uniqueIter = dupsFilter.keySet().iterator();
    int inx = 0;
    while (uniqueIter.hasNext()) { returnThis[inx++] = uniqueIter.next(); }
    return returnThis;
  }

  /**
   * return the unique Strings among the values of all objects with a given
   * attribute.
   *
   * Deprecation note:  This method has 1 usage in the core.
   * @deprecated There is no one method in {@link CyAttributes} that provides
   * this functionality, but it can be recreated via several calls.
  */
  public String[] getUniqueStringValues(String attributeName)
  {
    final HashMap dupsFilter = new HashMap();
    final MultiHashMapDefinition mmapDef =
      m_cyAttrs.getMultiHashMapDefinition();
    final MultiHashMap mmap = m_cyAttrs.getMultiHashMap();
    final byte type = m_cyAttrs.getType(attributeName);
    if (type == CyAttributes.TYPE_SIMPLE_LIST &&
        mmapDef.getAttributeValueType(attributeName) ==
        MultiHashMapDefinition.TYPE_STRING) {
      final Iterator objs = mmap.getObjectKeys(attributeName);
      while (objs.hasNext()) {
        final String obj = (String) objs.next();
        final List l = m_cyAttrs.getAttributeList(obj, attributeName);
        final Iterator liter = l.iterator();
        while (liter.hasNext()) {
          final Object val = liter.next();
          dupsFilter.put(val, val); } } }
    else if (type == CyAttributes.TYPE_STRING) {
      final Iterator objs = mmap.getObjectKeys(attributeName);
      while (objs.hasNext()) {
        final String obj = (String) objs.next();
        final Object val = mmap.getAttributeValue(obj, attributeName, null);
        dupsFilter.put(val, val); } }
    else { return new String[0]; }
    final String[] returnThis = new String[dupsFilter.size()];
    Iterator uniqueIter = dupsFilter.keySet().iterator();
    int inx = 0;
    while (uniqueIter.hasNext()) {
      returnThis[inx++] = (String) uniqueIter.next(); }
    return returnThis;
  }

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
  public String processFileHeader(String text)
  {
    // Copy old code from old GraphObjAttributes.
    String attributeName = "";
    String attributeCategory = DEFAULT_CATEGORY;
    Class  attributeClass = null;

    if (text.indexOf ("(") < 0)
      attributeName = text.trim ();
    else {
      StringTokenizer strtok = new StringTokenizer (text, "(");
      attributeName = strtok.nextToken ().trim();
      while (strtok.hasMoreElements ()) {
        String rawValuePair = strtok.nextToken().trim();
        if (!rawValuePair.endsWith (")")) continue;
        String valuePair = rawValuePair.substring (0,rawValuePair.length()-1);
        int locationOfEqualSign = valuePair.indexOf ("=");
        if (locationOfEqualSign < 0) continue;
        if (valuePair.endsWith ("=")) continue;
        StringTokenizer strtok2 = new StringTokenizer (valuePair, "=");
        String name = strtok2.nextToken ();
        String value = strtok2.nextToken ();
        if (name.equals ("category"))
          attributeCategory = value;
        if (name.equals ("class")) {
          try {
            attributeClass = Class.forName (value);
          }
          catch (ClassNotFoundException ignore) {;}
        } // if name == 'class'
      } // while strtok
    } // else: at least one (x=y) found
    setCategory (attributeName, attributeCategory);
    setClass (attributeName, attributeClass); // ******* Could fail *********
    return attributeName;
  }

  /**
   * Adds a set of attributes.
   *
   * @param attributes GraphObjAttributes Object.
   *                   Deprecation note:  This method has 0 usages in the core.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   *             equivalent method will be provided.
   */
  public void add(GraphObjAttributes attributes) {}

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
  public boolean add(String attributeName, String id, Object value)
  {
    return append(attributeName, id, value);
  }

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
  public boolean add(String attributeName, String id, double value)
  {
    return append(attributeName, id, new Double(value));
  }

  /**
   * A convenience method allowing the addition of multiple
   * different attributes for  one graphObject at the same time.
   * <p/>
   * Deprecation note:  This method has 0 usages in the core.
   *
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   *             equivalent method will be provided.
   */
  public boolean add(String graphObjectName, HashMap bundle)
  {
    return false;
  }

  private final HashMap m_categoryMap = new HashMap();

  /**
   * Assigns an arbitrary category name to the specified attribute.
   * @param attributeName     Attribute name
   * @param newValue          New value.
   * @deprecated Method is no longer needed in the Cytoscape core, and no
   *             equivalent method will be provided.
   */
  public void setCategory(String attributeName, String newValue)
  {
    m_categoryMap.put(attributeName, newValue);
  }

  /**
   * Gets a category for the specified attribute.
   * @param attributeName     Attribute name
   * @return Category name.
   */
  public String getCategory(String attributeName)
  {
    String returnThis =  (String) m_categoryMap.get(attributeName);
    if (returnThis != null) { return returnThis; }
    if (true) return null;
    if (m_cyAttrs.getType(attributeName) == CyAttributes.TYPE_UNDEFINED) {
      return null; }
    return GraphObjAttributes.DEFAULT_CATEGORY;
  }

  public final static String DEFAULT_CATEGORY = "unknown";    
}
