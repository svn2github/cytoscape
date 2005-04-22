package cytoscape.data;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import cytoscape.data.readers.*;
import cytoscape.util.Misc;
import cytoscape.task.TaskMonitor;

public interface GraphObjAttributes {

  /**
   * Sets a TaskMonitor for tracking loading of node attribute files.
   * @param taskMonitor
   */
  public void setTaskMonitor ( TaskMonitor taskMonitor );
  
  /**
   *  establish mapping between a java object (a graph node or edge) and
   *  its canonical (standard) name.
   *  <ul>
   *    <li> clients of this class (CyWindow, or cytoscape plugins) usually
   *         deal with graph nodes and edges, and only secondarily with names;
   *    <li> attributes are stored and retrieved by canonical name
   *    <li> the client must be able to translate from the node or edge object
   *         to the name, in order to get at the attributes
   *    <li> this method allows a new mapping between object and canonical name.
   *
   * @see #getCanonicalName
   */
  public void addNameMapping ( String canonicalName, Object graphObject );

  /**
   * removes a mapping between a java object (a graph node or edge) and
   * its canonical (standard) name
   */
  public void removeNameMapping ( String canonicalName );

  /**
   * removes a mapping between a canonical name and its graph object
   */
  public void removeObjectMapping ( Object graphObj );
 
  /**
   *  remove all entries from the nameMap
   */
  public void clearNameMap ();

  /**
   * remove all entries in the canonicalToGraphObject map
   */
  public void clearObjectMap ();

  public HashMap getNameMap ();

  public HashMap getClassMap ();

  public void addClassMap ( HashMap newClassMap );

  public HashMap getObjectMap ();

  /**
   *  a wholesale addition of all entries in a <graphObject> -> <canonicalName>
   *  HashMap.
   */
  public void addNameMap ( HashMap nameMapping );

  /**
   * add all entries in the given HashMap (entry: <canonicalName> -> <graphObject>)
   * to the canonicalToGraphObject HashMap.
   */
  public void addObjectMap ( HashMap objectMapping );

  /**
   *  return the canonical name associated with this graphObject (a node or edge,
   *  previously stored with a call to addNameMapping).  if no mapping exists, null
   *  is returned.
   */
  public String getCanonicalName ( Object graphObject );

  /**
   * return the graph object (node or edge) associated with this canonical name,
   * previously stored with a call to addNameMapping. if no mapping exists, null
   * is returned.
   */
  public Object getGraphObject ( String canonicalName );

  /**
   *  copy all attributes in the supplied GraphObjAttributes object into this
   *  GraphObjAttributes.  any pre-existing attributes survive intact as long
   *  as they do not have the same attribute name as the attributes passed
   *  in
   */
  public void set ( GraphObjAttributes attributes );

 
  /**
   *  set an attribute of the specified name to the graph object with the specified name.
   *  the attribute may be any java type, or an array of any java type; it can be neither
   *  a primitive value nor an array of primitves.  if this attribute has not previously
   *  been assigned a class, either explicitly or implicitly, then the class is deduced from
   *  the object (or the first element in the array of objects).  once a class has been
   *  assigned for this attribute, every subsequent addition must be
   *  an object of that same class.
   *
   * @param attributeName    eg, "expression", "GO molecular function level 4", "tissue count"
   * @param graphObjectName  the canonical name of a node or edge
   * @param obj              the value of this attribute, either a java object or an array of java
   *                         objects
   *
   * @throws IllegalArgumentException   if the class of obj (or of obj [0], if obj is an array)
   *                                    does not match the already assigned or deduced java class
   *                                    for this attribute
   */
  public boolean set ( String attributeName, String graphObjectName, Object obj );
  

  public boolean append ( String attributeName, String graphObjectName, Object obj );
  
  /**
   *  a convenience method; value will be promoted to Double
   */
  public boolean set  (String attributeName, String graphObjectName, double value );

  /**
   *  a convenience method allowing the addition of multiple different attributes for
   *  one graphObject at the same time.
   */
  public boolean set ( String graphObjectName, HashMap bundle );
  
  /**
   * the number of different attributes currently registered
   *
   */
  public int numberOfAttributes ();

  /**
   * get the names of all of the attributes
   *
   */
  public String[] getAttributeNames ();

  /**
   * return the canonical names of all objects with a given attribute.
   */
  public String[] getObjectNames ( String attributeName );

  /**
   * return the unique values among the values of all objects with a given attribute.
   */
  public Object[] getUniqueValues ( String attributeName );

  /**
   * return the unique Strings among the values of all objects with a given attribute.
   */
  public String[] getUniqueStringValues ( String attributeName ); 

  /**
   * return the number of graph objects with the specified attribute.
   */
  public int getObjectCount ( String attributeName );
 
  /**
   *
   */
  public boolean hasAttribute ( String attributeName );
 
  /**
   *
   */
  public boolean hasAttribute ( String attributeName, String graphObjName );
 
  /**
   *  return a hash whose keys are graphObjectName Strings, and whose values are
   *  a Vector of java objects the class of these objects, and
   *  the category of the attribute (annotation, data, URL) may be learned
   *  by calling getClass and getCategory
   *
   *  @return   a HashMap whose keys are graph object names (typically canonical names
   *            for nodes and edges) and whose values are Vectors of java objects.
   *
   *  @see #getClass
   *  @see #getCategory
   *
   */
  public HashMap getAttribute ( String attributeName );
 
  /**
   *  remove the entire second level Hashmap whose key is the specified attributeName
   */
  public void deleteAttribute ( String attributeName );

  /**
   *  remove the specified attribute from the specified node or edge
   */
  public void deleteAttribute ( String attributeName, String graphObjectName );

  /**
   *  remove the specified attribute value from the specified node or edge.
   *  there may be multiple values associated with the attribute, so search through
   *  the list, and if it is found, remove it
   */
  public void deleteAttributeValue ( String attributeName, String graphObjectName, Object value );

  /**
   *  specify the class of this attribute.  all subsequently added attribute
   *  values must be of the exactly this class
   */
  public boolean setClass ( String attributeName, Class attributeClass );

  /**
   *  all attributes are lists (java.lang.Vector) sharing the same base type; discover
   *  and return that here
   */
  public Class getClass ( String attributeName );

  /**
   *  get all values associated with this graphObjectName and this attributeName
   *
   *  @return a java.util.Vector of size zero or more, containing java objects
   *          whose types may be learned via a call to getType
   *
   *  @see #getCategory
   */
  public List getList ( String attributeName, 
                        String graphObjectName );

  /**
   *  for backwards compatibility:  the value of an attribute used to be
   *  strictly a single scalar; now -- even though attributes are all lists
   *  of scalars -- we support the old idiom by retrieving the first scalar
   *  from the list.
   */
  public Object getValue ( String attributeName, 
                           String graphObjectName );
  
  public Object get ( String attributeName, 
                      String graphObjectName );

  /**
   *  a convenience method:  convert the Vector of objects into an array
   */
  public Object[] getArrayValues ( String attributeName, 
                                   String graphObjectName );

  /**
   *  a convenience method, useful if you are certain that the attribute stores
   *  Strings; convert the Vector of Objects into an array of Strings
   */
  public String [] getStringArrayValues ( String attributeName, 
                                          String graphObjectName );

  /**
   *  construe the possibly multiple values of the attribute as a scalar Double,
   *  if possible
   */
  public Double getDoubleValue ( String attributeName, 
                                 String graphObjectName );

  /**
   *  construe the possibly multiple values of the attribute as a scalar Integer,
   *  if possible
   */
  public Integer getIntegerValue ( String attributeName, 
                                   String graphObjectName );

  /**
   *  construe the possibly multiple values of the attribute as a scalar String
   */
  public String getStringValue ( String attributeName, 
                                 String graphObjectName );

  /**
   *  deduce attribute name, category, and java class from the first
   *  line of the attributes file.   the form of the first line is
   *  <pre>
   *  attribute name  (category=xxxx) (class=yyyy)
   *  </pre>
   *  category and class are optional; if absent, class will be inferred
   *  (see deduceClass), and category set to DEFAULT_CATEGORY
  * every attribute file must have, at minimum, the name attribute in the first line,
    * possibly with embedded spaces
    * in addition, the first line may have category and class information, as in
    *     homologene  (category=staticWebPage) (class=java.net.URL)
    * the present method extracts the mandatory attribute name, and the optional
    * category and class information.
    * 
    * note: category and class information, if present, are not only parsed here:  the
    *       information is also stored as appropriate in the current class data members 
  */
  public String processFileHeader ( String text );
    
  
  public void readAttributesFromFile ( File file );

  
  /**
   *  Reads attributes from a file.  There is one basic format for attribute
   *  files, but a few aspects of the format are flexible.
   *
   *  The simplest form looks like this:
   *  <pre>
   *  expresssion ratio
   *  geneA = 0.1
   *  geneB = 8.9
   *  ...
   *  geneZ = 23.2
   *  </pre>
   */
  public void readAttributesFromFile ( String filename );

  /**
   *  return attributeName/attributeClass pairs, for every known attribute
   */
  public HashMap getSummary ();

  /**
   *  for the graphObject named by canonicalName, extract and return all attributes
   *
   *  @see #getValue
   */
  public HashMap getAttributes ( String canonicalName );

  /**
   *  multiple GraphObj's (edges in particular) may have the same name; this method
   *  counts names which begin with the same string.  for instance
   *  there may be two edges between the same pair of nodes:
   *  <pre>
   *    VNG0382G (geneFusion) VNG1230G
   *    VNG0382G (geneFusion) VNG1232G
   *  </pre>
   * the first pair encountered may be give the name
   *
   *  <pre>
   *    VNG0382G (geneFusion) VNG1230G
   *  </pre>
   * we may wish to give the second pair the name
   *
   *  <pre>
   *    VNG0382G (geneFusion) VNG1230G_1
   *  </pre>
   * this method provides a count of matches based on
   * String.startsWith ("VNG0382G (geneFusion) VNG1230G") which solves the problem
   * of all subsequent duplicates simply append a number to the base name.
   * <p>
   * whoever calls this method must construct the new object's name -first-
   * and then is expected to append "_N" where N is the value returned here
   * (and of course, if the result is 0, there is no need to append '_0'
   */
  public int countIdentical ( String graphObjectName );
  
  /**
   *  create a human readable version.
   */
  public String toString ();


} 



