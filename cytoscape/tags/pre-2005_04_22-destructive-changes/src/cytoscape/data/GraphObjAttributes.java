// GraphObjAttributes.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// andrew's suggestions (15 aug 2002)
//    attributes.set ()   -> replace current Object with new object
//    attributes.add ()   -> extend current attribute
//    attributes.get<BaseType><Array><Value> 
//
// explore using a List rather than a vector as the underlying implementation --
//  what flexiblity will this buy us?
//--------------------------------------------------------------------------------
// Revision: 1.12 
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data;
//--------------------------------------------------------------------------------
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import cytoscape.data.readers.*;
import cytoscape.data.servers.*;
import cytoscape.util.Misc;
import cytoscape.task.TaskMonitor;
//--------------------------------------------------------------------------------
/**
 * Store multiple attributes of multiple "graph objects" (usually graph nodes
 * or edges) for easy and flexible retrieval.  There are typically two
 * instances of this class associated with every cytoscape graph:  one set of
 * attributes for all of the nodes, and another set of attributes for all the
 * edges.
 * <p>
 * It may be useful to understand the implementation of this data structure, at
 * least at a high level, in order to use it effectively.  At the top level
 * there is a hashtable, with one entry for each attribute; the attribute's
 * name (a String) is the key to this top level hashtable.  The <em> value
 * </em> associated with each of these keys is another hashtable, in which are
 * stored possibly many entries whose keys are node or edge names, and whose
 * values are any kind of java object.
 * <p>
 * We make an important distinction between the semantics of 'set' and
 * 'append'. For set, the specified java object becomes the sole and single
 * value of that attribute for the named graph object.  In the example just
 * below, a java Double with value 1.8 becomes the sole value of 'mRNA ratio'
 * for the graph object named 'GAL4'.
 * <p>
 * The append semantics are different: in this case, a java.util.Vector is
 * created, and every appended object is added to that vector.
 * <p>
 * For example: <br>
 * <pre>
 *   GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
 *
 *   nodeAttributes.set ("mRNA ratio",        "GAL4",  1.8);
 *   nodeAttributes.set ("mRNA ratio",        "GAL80", 0.01);
 *
 *   nodeAttributes.set ("mRNA significance", "GAL4",  0.4);
 *   nodeAttributes.set ("mRNA significance", "GAL80", 0.8);
 *
 *   nodeAttributes.set ("gene product",      "GAL4",  "zinc finger transcription factor");
 *   nodeAttributes.set ("gene product",      "GAL80", "transcriptional regulator");
 *
 *   nodeAttributes.append ("SGD summary",       "GAL4",
 *                       "http://genome-www4.stanford.edu/cgi-bin/SGD/locus.pl?locus=gal4");
 *   nodeAttributes.append ("SGD summary",       "GAL4",
 *                       "http://genome-www4.stanford.edu/cgi-bin/SGD/locus.pl?locus=gal80");
 *
 * </pre>
 * <p>
 * These nine lines of code create 4 entries (and therefor 4 keys) in the top
 * level hash: <i>mRNA ratio, mRNA significance, gene product, and SGD
 * summary</i>.  The values of each of these keys in the top level hash are
 * each themselves hashtables, which have, in turn, just two entries (GAL4 and
 * GAL80); the values of each of these second-level hashes are java objects, as
 * follows:
 * <ol>
 *   <li> <b>mRNA ratio</b>: &nbsp; java.lang.Double
 *   <li> <b>mRNA significance</b>: &nbsp; java.lang.Double
 *   <li> <b>gene product</b>: &nbsp; java.lang.String
 *   <li> <b>SGD summary</b>:  &nbsp; java.net.URL
 * </ol>
 *
 * These classes (Double, String, URL) are deduced from the data, but you may
 * also assign attribute classes explicitly:
 * <pre>
 *   nodeAttributes.setClass ("SGD summary", Class.forName ("java.net.URL"));
 * </pre>
 *
 * Each attribute also has a category, which by default is 'unknown', but which
 * may be given any String value.  There is as yet no controlled vocabulary
 * for category names, but here are some suggestions which may be useful:
 * <ul>
 *   <li> <b>annotation</b>: &nbsp; useful if you want to do layout based on
 *        this attribute.  Annotation is an exception to the general rule about
 *        category names:  it is a privileged category, recognized elsewhere
 *        in cytoscape; for example, the Annotation Layout option uses it
 *        to recognize which of the current node attributes on nodes
 *   <li> <b>data</b>
 *   <li> <b>categorizer</b>
 *   <li> <b>static web page</b>
 * </ul>
 * <p>
 *  You may use cytoscape.props to specify attribute categories which
 *   are ignored by the NodeBrowser.
 * <p>
 * Here are some of the more common operations use in retrieving data from
 * an instance of GraphObjAttributes, presented in no particular order:
 * <pre>
 *   if (nodeAttributes.hasAttribute ("mRNA ratio")) ...
 *   int genesWithSGDLinks = nodeAttributes.getObjectCount ("SGD summary");
 *   String [] allAttributeNames = nodeAttributes.getAttributeNames ();
 *   int attributeCount = nodeAttributes.numberOfAttributes ();
 *   Vector gal4mRNAList = attributes.getList ("mRNA ratio", "GAL4")
 *     // though all attribute values are really lists, those lists
 *     // often have just one member; if you know in advance that
 *     // just a single mRNA ratio was stored for GAL4, and that it is
 *     // a Double, you can use this convenience method:
 *   Double gal4mRNA = attributes.getDoubleValue ("mRNA ratio", "GAL4");
 *     //
 *   Object [] values = attributes.getArrayValues ("gene product", "GAL4");
 *   String [] strings = attributes.getStringArrayValues ("gene product", "GAL4");
 *   String gal4Product = attributes.getStringValue ("geneProduct", "GAL4");
 * </pre>
 *
 * <h3> An Introduction to Attribute Files </h3>
 *
 * It is very common to associate attributes with nodes and edges of a graph
 * by reading them in on the cytoscape command line:
 * <pre>
 *   java cytoscape.cytoscape -i yeastGalactose.sif -n ratios.attr -n signif.attr -n products.attr -n sgdLinks.attr
 * </pre>
 *
 * Attribute files have the following form:
 * <ol>
 *   <li> a header line, containing at least an attribute name, and optionally
 *        class and category specifications
 *   <li> one or more data lines, of the form &lt;object name&gt; =
 *        &lt;object value&gt;; whitespace may be used for either the name or
 *        the value
 *   <li> &lt;object value&gt; may be a scalar (which, however, can include
 *        whitespace for strings) or a list, surrounded by parentheses, with
 *        each scalar delimited by "::"
 *   <li> other tokens for list and token delmiters can be specified in your
 *        cytoscape.props
 * </ol>
 *
 * <h4> Attribute File Example 1 </h4>
 * Create a String attribute named "animal species" of unknown category, for
 * four nodes
 * <p>
 * <pre>
 * animal species
 * a = dog
 * b = cat
 * c = yak
 * d = three-headed wolf
 * </pre>
 *
 * <p>
 * <h4> Attribute File Example 2 </h4>
 * Create a URL attribute named "locusLink" of category annotation, for three
 * nodes
 * <p>
 * <pre>
 * locusLink (category=annotation) (class=java.net.URL)
 * NM_000196 = http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3291
 * NM_000353 = http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=6898
 * NM_001128 = http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=164
 * </pre>
 *
 * <p>
 *
 * <p>
 * <h4> Attribute File Example 3 </h4>
 * Create a String attribute named "GO molecular function, level 4" of category
 * annotation, for four genes; some of the attribute values are multiple
 * <p>
 * <pre>
 * GO molecular function, level 4 (category=annotation)
 * AP1G1 = (intracellular::clathrin adaptor::intracellular transporter)
 * HSD17B2 = (membrane::intracellular)
 * E2F4 = (DNA binding)
 * CDH3 = cell adhesion molecule
 * </pre>
 *
 * <p>
 * <h3> todo </h3>
 * <ol>
 *    <li> create an example file and good documentation for an edge attributes
 *         file. how do we specify edge names these days?
 *         YPR065W (pp) YPR065W = 2232
 * </ol>
 *
 */
public class GraphObjAttributes implements Cloneable,Serializable {
    //  Used to Monitor Loading of Attribute Files.
    private TaskMonitor taskMonitor;

    // the main data object, a hash of hashes: (attributeName, hash (objName, objValue)).
    HashMap map;
    // map from a graphObject (a node or an edge) to its canonical name
    transient HashMap nameFinder;
    // map from a canonical name to a graphObject (node or edge) --iliana
    transient HashMap canonicalToGraphObject;
    transient HashMap countIdMap;
    // what kind of attribute is this?  staticWebPage, annotation, numerical, categorizer, ...
    // these types are -not- from a controlled vocabulary:  they are up to the whim
    // of the user (though standard usages may evolve)
    HashMap categoryMap;
    // keep track of the java class of each of these attributes
    HashMap classMap;
    public final static String DEFAULT_CATEGORY = "unknown";
//--------------------------------------------------------------------------------
public GraphObjAttributes ()
{
  map = new HashMap ();
  nameFinder = new HashMap ();
  canonicalToGraphObject = new HashMap();
  countIdMap = new HashMap ();
  categoryMap = new HashMap ();
  classMap = new HashMap ();
}

/**
 * Sets a TaskMonitor for tracking loading of node attribute files.
 * @param taskMonitor
 */
public void setTaskMonitor(TaskMonitor taskMonitor) {
    this.taskMonitor = taskMonitor;
}

//--------------------------------------------------------------------------------
/**
 *  create an identical and unique copy, so that any subsequent changes to
 *  the clone will not affect the original, and vice versa.
 */
public Object clone ()
// cloning can be a little tricky.  for a discussion, see
//
//   http://java.sun.com/docs/books/tutorial/java/javaOO/objectclass.html
//
// for cloning to work, every container data structure (HashMap's in our case)
// must be clone 'all the way down'.  in particular, the core data structure
// of this class -- 'map' -- is a HashMap of HashMap's.  for a true clone, then,
// each one of those second-level HashMaps must be extracted, cloned, and added
// to the toplevel HashMap, which itself must be cloned.
{
  GraphObjAttributes attributesClone = null;

  try {
    attributesClone =  (GraphObjAttributes) super.clone ();
    attributesClone.map = (HashMap) map.clone ();
    String [] keys = (String []) map.keySet().toArray(new String [0]);
    for (int i=0; i < keys.length; i++) {
      HashMap singleAttributeHash = (HashMap) map.get (keys [i]);
      HashMap singleAttributeHashClone = (HashMap) singleAttributeHash.clone ();
      attributesClone.map.put (keys [i], singleAttributeHashClone);
      String [] graphObjectNames = (String []) singleAttributeHash.keySet().toArray (new String [0]);
      for (int j=0; j < graphObjectNames.length; j++) {
        Object obj = singleAttributeHash.get (graphObjectNames [j]);
          // todo:  other container objects need special treatment too
          // todo:  when there is time, generalize this test, and the special treatment
          // todo:  to handle HashMaps, Sets, ....
        if (obj.getClass () == Class.forName ("java.util.Vector")) {
          Vector list = (Vector) obj;
          Vector clonedList = (Vector) list.clone ();
          singleAttributeHashClone.put (graphObjectNames [j], clonedList);
          } // if
        } // for j
      } // for i
    attributesClone.nameFinder  = (HashMap) nameFinder.clone ();
    attributesClone.canonicalToGraphObject = (HashMap) canonicalToGraphObject.clone();
    attributesClone.countIdMap  = (HashMap) countIdMap.clone ();
    attributesClone.categoryMap = (HashMap) categoryMap.clone ();
    attributesClone.classMap = (HashMap) classMap.clone ();
    }
  catch (Exception e) {
    System.err.println (" --- error in GraphObjAttributes.clone");
    e.printStackTrace ();
    }
  return attributesClone;

} // clone
//--------------------------------------------------------------------------------
/**
 * This method copies all information from the argument to this object, overwriting
 * any duplicated mappings but preserving any information that is not overwritten.
 * This includes both the attributes and the mappings of names to objects.
 * Does nothing if the argument is null.
 */
public void inputAll(GraphObjAttributes newAttributes) {
    if (newAttributes == null) {return;}
    addNameMap( newAttributes.getNameMap() );
    addObjectMap( newAttributes.getObjectMap() );
    addClassMap( newAttributes.getClassMap() );
    set(newAttributes);
}
//--------------------------------------------------------------------------------
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
public void addNameMapping (String canonicalName, Object graphObject)
{
    nameFinder.put (graphObject, canonicalName);
    canonicalToGraphObject.put(canonicalName, graphObject);
    set("canonicalName",canonicalName,canonicalName);
}
//--------------------------------------------------------------------------------
/**
 * removes a mapping between a java object (a graph node or edge) and
 * its canonical (standard) name
 */
public void removeNameMapping (String canonicalName)
{
    Object graphObject = canonicalToGraphObject.remove (canonicalName);
    nameFinder.remove (graphObject);
    // update the counter as well
    if(countIdMap != null){
	Integer numIdentical = (Integer)countIdMap.get(canonicalName);
	if(numIdentical != null && numIdentical.intValue() > 0){
	    countIdMap.put (canonicalName, new Integer (numIdentical.intValue() - 1));
	}
    }
}
//--------------------------------------------------------------------------------
/**
 * removes a mapping between a canonical name and its graph object
 */
public void removeObjectMapping(Object graphObj)
{
    String canonical = (String)nameFinder.remove (graphObj);
    canonicalToGraphObject.remove (canonical);
}

//--------------------------------------------------------------------------------
/**
 *  remove all entries from the nameMap
 */
public void clearNameMap ()
{
  nameFinder = new HashMap ();
  clearObjectMap();
}
//--------------------------------------------------------------------------------
/**
 * remove all entries in the canonicalToGraphObject map
 */
public void clearObjectMap()
{
    canonicalToGraphObject = new HashMap();
}
//--------------------------------------------------------------------------------
public HashMap getNameMap ()
{
  return nameFinder;
}
//--------------------------------------------------------------------------------
public HashMap getClassMap ()
{
  return classMap;
}
//--------------------------------------------------------------------------------
public void addClassMap (HashMap newClassMap)
{
  classMap.putAll (newClassMap);
}
//--------------------------------------------------------------------------------
public HashMap getObjectMap()
{
    return canonicalToGraphObject;
}
//--------------------------------------------------------------------------------
/**
 *  a wholesale addition of all entries in a <graphObject> -> <canonicalName>
 *  HashMap.
 */
public void addNameMap (HashMap nameMapping)
{

  nameFinder.putAll (nameMapping);


  Set keySet = nameMapping.keySet();
  Iterator it = keySet.iterator();
  HashMap objectMap = new HashMap();
  while(it.hasNext()){
      Object graphObj = it.next();
      String canonical = (String)nameFinder.get(graphObj);
      objectMap.put(canonical,graphObj);
  }
  addObjectMap(objectMap);

}
//--------------------------------------------------------------------------------
/**
 * add all entries in the given HashMap (entry: <canonicalName> -> <graphObject>)
 * to the canonicalToGraphObject HashMap.
 */
public void addObjectMap(HashMap objectMapping)
{
    canonicalToGraphObject.putAll(objectMapping);
}

//--------------------------------------------------------------------------------
/**
 *  return the canonical name associated with this graphObject (a node or edge,
 *  previously stored with a call to addNameMapping).  if no mapping exists, null
 *  is returned.
 */
public String getCanonicalName (Object graphObject)
{
  if(nameFinder == null){
    System.out.println("oh oh, nameFinder is NULL !!!!!!!!!!!!!!");
  }
  return (String) nameFinder.get (graphObject);
}
//--------------------------------------------------------------------------------
/**
 * return the graph object (node or edge) associated with this canonical name,
 * previously stored with a call to addNameMapping. if no mapping exists, null
 * is returned.
 */
public Object getGraphObject(String canonicalName)
{
    return canonicalToGraphObject.get(canonicalName);
}

//--------------------------------------------------------------------------------
/**
 *  copy all attributes in the supplied GraphObjAttributes object into this
 *  GraphObjAttributes.  any pre-existing attributes survive intact as long
 *  as they do not have the same attribute name as the attributes passed
 *  in
 */
public void set (GraphObjAttributes attributes)
{
  String [] newAttributeNames = attributes.getAttributeNames ();

  for (int i=0; i < newAttributeNames.length; i++) {
    String name =  newAttributeNames [i];
    HashMap hash = attributes.getAttribute (newAttributeNames [i]);
    map.put (name, hash);
    }

} // add
//--------------------------------------------------------------------------------
/**
 *  when a new attribute is assigned, some preliminary set up may need to be done:
 *  <ul>
 *    <li> if the attribute is previously unknown, then a new HashMap must be
 *         created for it
 *    <li> if the class of the attribute has not yet been set, then it can
 *         be deduced from the specified value ('obj').  if 'obj' is an array,
 *         then the deduced class is that of first element in the array
 *    <li> if the class of the attribute has already been set, then the
 *         class of the new 'obj' (or its first element, if it is an array)
 *         must be the same as that of the class; we test that here, and throw
 *         an exception is thrown if they do not agree
 *  </ul>
 */
protected void initializeAttributeAsRequired (String attributeName,
                                              String graphObjectName,
                                              Object obj)
{

  if (!map.containsKey (attributeName)) {
    // System.out.println (" --- map does not contain key " + attributeName);
    map.put (attributeName, new HashMap ());
    if (getClass (attributeName) == null) {
      Class deducedClass = obj.getClass ();
      //System.out.println ("\nObject: " + obj + " attribute: " +attributeName +
      //                    " deduced class: " + deducedClass);
      //System.out.flush();
      if (obj.getClass().isArray())
        deducedClass = ((Object []) obj)[0].getClass ();
      setClass (attributeName, deducedClass);
      } // if no class assigned yet
    } // if new attribute
  else {
    ; // System.out.println (" +++ map does contain key " + attributeName);
    }

  Class expectedClass = getClass (attributeName);
  Class actualClass = obj.getClass ();

  if (obj.getClass().isArray()) {
    Object [] objAsArray = (Object []) obj;
    Object first = objAsArray [0];
    actualClass = first.getClass ();
    }

  if (actualClass != expectedClass)
    throw new IllegalArgumentException ("class mismatch during set for attribute " +
                                         attributeName + ",\n object: " + graphObjectName +
                                        "\n expected " + expectedClass +
                                        "\n got " + actualClass);

} // initializeAttributeAsRequired
//--------------------------------------------------------------------------------
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
public boolean set (String attributeName, String graphObjectName, Object obj)
{
  // if this is a new attribute, it needs special treatment:
  //
  //   - create a new HashMap (which is the value of the top-level hashmap, and
  //     which will hold <String graphObjectName> -> <Object obj> mappings
  //
  //   - sometimes people will explicitly assign the java class of this attribute, but
  //     other times we must deduce the class from the <obj>, allowing for the
  //     possiblity that it may be an array of objects, in which case we check the
  //     class of the first object in the array

    if (obj == null) return false;
    if(!implementsSerializable(obj.getClass())){
	throw new IllegalArgumentException("The class " + obj.getClass().getName()+ " of the object that represents the value for the attribute \""
					   + attributeName + "\" must implement java.io.Serializable.");
    }
    initializeAttributeAsRequired (attributeName, graphObjectName, obj);
    HashMap attributeMap = (HashMap) map.get (attributeName);
    attributeMap.put (graphObjectName, obj);

    return true;

} // set
//--------------------------------------------------------------------------------
public boolean append (String attributeName, String graphObjectName, Object obj)
{
  // if this is a new attribute, it needs special treatment:
  //
  //   - create a new HashMap (which is the value of the top-level hashmap, and
  //     which will hold <String graphObjectName> -> <Object obj> mappings
  //
  //   - sometimes people will explicitly assign the java class of this attribute, but
  //     other times we must deduce the class from the <obj>, allowing for the
  //     possiblity that it may be an array of objects, in which case we check the
  //     class of the first object in the array

    if (obj == null) return false;

    if(!implementsSerializable(obj.getClass())){
	throw new IllegalArgumentException("The class " + obj.getClass().getName()+ " of the object that represents the value for the attribute \""
					   + attributeName + "\" must implement java.io.Serializable.");
    }
  initializeAttributeAsRequired (attributeName, graphObjectName, obj);
  HashMap attributeMap = (HashMap) map.get (attributeName);

  Vector list = null;
  if (attributeMap.containsKey (graphObjectName)) {
    list = (Vector) attributeMap.get (graphObjectName);
    }
  else {
    list = new Vector ();
    }

  if (obj.getClass().isArray ()) {
    Object [] objAsArray = (Object []) obj;
    for (int i=0; i < objAsArray.length; i++)
      // if (!list.contains (objAsArray [i]))
         list.add (objAsArray [i]);
    }
  else { //if (!list.contains (obj)) {
    list.add (obj);
    }

  attributeMap.put (graphObjectName, list);

  return true;

} // append
//--------------------------------------------------------------------------------
/**
 *  a convenience method; value will be promoted to Double
 */
public boolean set (String attributeName, String graphObjectName, double value)
{
  return set (attributeName, graphObjectName, new Double (value));

} // set
//--------------------------------------------------------------------------------
/**
 *  a convenience method allowing the addition of multiple different attributes for
 *  one graphObject at the same time.
 */
public boolean set (String graphObjectName, HashMap bundle)
{
  String [] keys = (String []) bundle.keySet().toArray (new String [0]);
  boolean success = true;
  for (int i=0; i < keys.length; i++) {
    String attributeName = keys [i];
    Object value = bundle.get (attributeName);
    if(!set (attributeName, graphObjectName, value)){
	success = false;
    }
  }

  return success;

} // set
//--------------------------------------------------------------------------------
 /**
  * @deprecated  use set instead
  * @see #set (GraphObjAttributes)
  */
public void add (GraphObjAttributes attributes)
{
  set (attributes);
}
//--------------------------------------------------------------------------------
 /**
  * @deprecated  use set instead
  * @see #set (String, GraphObjAttributes, Object)
  */
//--------------------------------------------------------------------------------
public boolean add (String attributeName, String graphObjectName, Object obj)
{
    return set (attributeName, graphObjectName, obj);

}
//--------------------------------------------------------------------------------
 /**
  * @deprecated  use set instead
  * @see #set (String, String, double)
  */
public boolean add (String attributeName, String graphObjectName, double value)
{
  return set (attributeName, graphObjectName, value);
}
//--------------------------------------------------------------------------------
 /**
  * @deprecated  use set instead
  * @see #set (String, HashMap)
  */
public boolean add (String graphObjectName, HashMap bundle)
{
  return set (graphObjectName, bundle);
}
//--------------------------------------------------------------------------------
/**
 * the number of different attributes currently registered
 *
 * @deprecated  use numberOfAttributes instead
 * @see #numberOfAttributes
 */
public int size ()
{
  return numberOfAttributes ();
}
//--------------------------------------------------------------------------------
/**
 * the number of different attributes currently registered
 *
 */
public int numberOfAttributes ()
{
  return map.size ();
}
//--------------------------------------------------------------------------------
/**
 * get the names of all of the attributes
 *
 */
public String [] getAttributeNames ()
{
  return (String []) map.keySet().toArray (new String [0]);
}
//--------------------------------------------------------------------------------
/**
 * return the canonical names of all objects with a given attribute.
 */
public String [] getObjectNames (String attributeName)
{
  HashMap attributeMap = getAttribute (attributeName);
  if (attributeMap == null)
    return new String [0];

  return (String []) attributeMap.keySet().toArray (new String [0]);

} // getObjectNames
//--------------------------------------------------------------------------------
/**
 * return the unique values among the values of all objects with a given attribute.
 */
public Object [] getUniqueValues (String attributeName)
{
  HashMap hash = getAttribute (attributeName);
 if (hash == null)
    return null;
  Object [] allValues = (Object []) hash.values().toArray (new Object [0]);
  Vector nonredundantList = new Vector ();
  for (int i=0; i < allValues.length; i++)
    if (allValues [i].getClass() == (nonredundantList.getClass ())) {
      Object [] allValuesI = (Object []) ((Vector) allValues [i]).toArray (new Object [0]);
      for (int j=0; j < allValuesI.length; j++)
        if (!nonredundantList.contains (allValuesI [j]))
          nonredundantList.add (allValuesI [j]);
        }
    else if (allValues [i].getClass().isArray ()) {
      Object [] allValuesI = (Object []) allValues [i];
      for (int j=0; j < allValuesI.length; j++)
        if (!nonredundantList.contains (allValuesI [j]))
          nonredundantList.add (allValuesI [j]);
      }
    else { // allValues [i] is -not- a Vector
      if (!nonredundantList.contains (allValues [i]))
        nonredundantList.add (allValues [i]);
      }

  return (Object []) nonredundantList.toArray (new Object [0]);

} // getUniqueValues
//--------------------------------------------------------------------------------
/**
 * return the unique Strings among the values of all objects with a given attribute.
 */
public String [] getUniqueStringValues (String attributeName)
{
  Object [] objs = getUniqueValues (attributeName);
  if (objs == null)
    return new String [0];

  String [] result = new String [objs.length];
  for (int i=0; i < objs.length; i++)
    result [i] = (String) objs [i];

  return result;

} // getUniqueValues
//--------------------------------------------------------------------------------
/**
 * return the number of graph objects with the specified attribute.
 */
public int getObjectCount (String attributeName)
{
  HashMap attributeMap = getAttribute (attributeName);
  if (attributeMap == null)
    return 0;

  return attributeMap.size ();

} // getObjectCount
//--------------------------------------------------------------------------------
/**
 *  assign an arbitrary category name to the specified attribute
 */
public void setCategory (String attributeName, String newValue)
{
  categoryMap.put (attributeName, newValue);
}
//--------------------------------------------------------------------------------
/**
 *
 */
public String getCategory (String attributeName)
{
  return (String) categoryMap.get (attributeName);
}
//--------------------------------------------------------------------------------
/**
 *
 */
public boolean hasAttribute (String attributeName)
{
  return map.containsKey (attributeName);
}
//--------------------------------------------------------------------------------
/**
 *
 */
public boolean hasAttribute (String attributeName, String graphObjName)
{
  HashMap attributeMap = (HashMap) map.get (attributeName);
  if (attributeMap == null)
    return false;

  return attributeMap.containsKey (graphObjName);

}
//--------------------------------------------------------------------------------
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
public HashMap getAttribute (String attributeName)
{
  return (HashMap) map.get (attributeName);
}
//--------------------------------------------------------------------------------
/**
 *  remove the entire second level Hashmap whose key is the specified attributeName
 */
public void deleteAttribute (String attributeName)
{
  if (hasAttribute (attributeName))
    map.remove (attributeName);

}
//--------------------------------------------------------------------------------
/**
 *  remove the specified attribute from the specified node or edge
 */
public void deleteAttribute (String attributeName, String graphObjectName)
{
  if (!hasAttribute (attributeName)) {
    return;
  }
  HashMap oneAttributeMap = getAttribute (attributeName);
  oneAttributeMap.remove (graphObjectName);

}
//--------------------------------------------------------------------------------
/**
 *  remove the specified attribute value from the specified node or edge.
 *  there may be multiple values associated with the attribute, so search through
 *  the list, and if it is found, remove it
 */
public void deleteAttributeValue (String attributeName, String graphObjectName, Object value)
{
  if (!hasAttribute (attributeName)) return;
  Vector list = (Vector) getList (attributeName, graphObjectName);

  if (list.contains (value))
     list.remove (value);

}
//--------------------------------------------------------------------------------
/**
 *  specify the class of this attribute.  all subsequently added attribute
 *  values must be of the exactly this class
 */
public boolean setClass (String attributeName, Class attributeClass)
{
    if(implementsSerializable(attributeClass) || attributeClass == null){
	classMap.put(attributeName,attributeClass);
	return true;
    }else{
	throw new IllegalArgumentException("Attribute class " + attributeClass.toString() + " must implement java.io.Serializable");
    }

}
//--------------------------------------------------------------------------------
/**
 *  all attributes are lists (java.lang.Vector) sharing the same base type; discover
 *  and return that here
 */
public Class getClass (String attributeName)
{
  return (Class) classMap.get (attributeName);

} // getClass
//--------------------------------------------------------------------------------
/**
 *  get all values associated with this graphObjectName and this attributeName
 *
 *  @return a java.util.Vector of size zero or more, containing java objects
 *          whose types may be learned via a call to getType
 *
 *  @see #getCategory
 */
public Vector getList (String attributeName, String graphObjectName)
{
  HashMap attributeMap = (HashMap) map.get (attributeName);
  if (attributeMap == null)
    return new Vector ();

  if (!attributeMap.containsKey (graphObjectName))
    return new Vector ();

   Object obj = attributeMap.get (graphObjectName);
   Vector tmp = new Vector ();
   if (obj.getClass() != tmp.getClass ()) {
     tmp.add (obj);
     return tmp;
     }
   else
     return (Vector) attributeMap.get (graphObjectName);

} // getList
//--------------------------------------------------------------------------------
/**
 *  for backwards compatibility:  the value of an attribute used to be
 *  strictly a single scalar; now -- even though attributes are all lists
 *  of scalars -- we support the old idiom by retrieving the first scalar
 *  from the list.
 */
public Object getValue (String attributeName, String graphObjectName)
{
 return get (attributeName, graphObjectName);
}
public Object get (String attributeName, String graphObjectName)
{

  HashMap attributeMap = (HashMap) map.get (attributeName);
  if (attributeMap == null)
    return null;

  if (!attributeMap.containsKey (graphObjectName))
    return null;

   return attributeMap.get (graphObjectName);

} // getValue
//--------------------------------------------------------------------------------
/**
 *  a convenience method:  convert the Vector of objects into an array
 */
public Object [] getArrayValues (String attributeName, String graphObjectName)
{
  Vector list = (Vector) getList (attributeName, graphObjectName);
  if (list == null)
     return new Object [0];

  Object [] result = (Object []) list.toArray (new Object [0]);
  return result;
}
//--------------------------------------------------------------------------------
/**
 *  a convenience method, useful if you are certain that the attribute stores
 *  Strings; convert the Vector of Objects into an array of Strings
 */
public String [] getStringArrayValues (String attributeName, String graphObjectName)
{
  Vector list = (Vector) getList (attributeName, graphObjectName);
  if (list == null)
     return new String [0];

  String [] result = (String []) list.toArray (new String [0]);
  return result;

}
//--------------------------------------------------------------------------------
/**
 *  construe the possibly multiple values of the attribute as a scalar Double,
 *  if possible
 */
public Double getDoubleValue (String attributeName, String graphObjectName)
{
  Object object = getValue (attributeName, graphObjectName);
  if (object == null)
    return null;

  try {
    if (object.getClass() == Class.forName ("java.util.Vector")) {
      Vector tmp = (Vector) object;
      if (tmp.size () < 1)
        object = null;
      else
        object = tmp.get (0);
      }
    }
   catch (ClassNotFoundException shouldNeverOccur) {;}

   return (Double) object;

} // getDoubleValue
//--------------------------------------------------------------------------------
/**
 *  construe the possibly multiple values of the attribute as a scalar Integer,
 *  if possible
 */
public Integer getIntegerValue (String attributeName, String graphObjectName)
{
  Object object = getValue (attributeName, graphObjectName);
  if (object == null)
    return null;

  try {
    if (object.getClass() == Class.forName ("java.util.Vector")) {
      Vector tmp = (Vector) object;
      if (tmp.size () < 1)
        object = null;
      else
        object = tmp.get (0);
      }
    }
   catch (ClassNotFoundException shouldNeverOccur) {;}

   return (Integer) object;

} // getIntegerValue
//--------------------------------------------------------------------------------
/**
 *  construe the possibly multiple values of the attribute as a scalar String
 */
public String getStringValue (String attributeName, String graphObjectName)
{
  Object object = getValue (attributeName, graphObjectName);
  // added by iliana - it is useful to know that this attribute has not been assigned a value
  // plus if object is null this crashes
  if(object == null){return null;}
  try {
    if (object.getClass() == Class.forName ("java.util.Vector")) {
      Vector tmp = (Vector) object;
      if (tmp.size () < 1)
        object = null;
      else
        object = tmp.get (0);
      }
    }
   catch (ClassNotFoundException shouldNeverOccur) {;}

  return object.toString ();
}
//--------------------------------------------------------------------------------
/**
 *  deduce attribute name, category, and java class from the first
 *  line of the attributes file.   the form of the first line is
 *  <pre>
 *  attribute name  (category=xxxx) (class=yyyy)
 *  </pre>
 *  category and class are optional; if absent, class will be inferred
 *  (see deduceClass), and category set to DEFAULT_CATEGORY
 */
public String processFileHeader (String text)
// every attribute file must have, at minimum, the name attribute in the first line,
// possibly with embedded spaces
// in addition, the first line may have category and class information, as in
//     homologene  (category=staticWebPage) (class=java.net.URL)
// the present method extracts the mandatory attribute name, and the optional
// category and class information.
// 
// note: category and class information, if present, are not only parsed here:  the
//       information is also stored as appropriate in the current class data members
{
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

} // processFileHeader
//--------------------------------------------------------------------------------
public void readAttributesFromFile (String filename)
   throws IOException, IllegalArgumentException, NumberFormatException
{
  readAttributesFromFile (null, "unknown",  filename, true);
}
//--------------------------------------------------------------------------------
public void readAttributesFromFile (File file)
   throws IOException, IllegalArgumentException, NumberFormatException
{
  readAttributesFromFile (null, "unknown",  file.getPath (), true);
}
//--------------------------------------------------------------------------------
//public void readAttributesFromFile(BioDataServer dataServer, String species, String filename)
//   throws FileNotFoundException, IllegalArgumentException, NumberFormatException
//{
//  readAttributesFromFile (dataServer, species, new File (filename));
//}
//--------------------------------------------------------------------------------
/**
 *  determine (heuristically) the most-specialized class instance which can be
 *  constructed from the supplied string.
 */
static public Class deduceClass (String string)
{
  String [] classNames = {"java.net.URL",
                          "java.lang.Integer",    // using this breaks the vizmapper, see below
                          "java.lang.Double",
                          "java.lang.String"};

  /** vizmapper error:
   * Exception in thread "main" java.lang.ClassCastException: java.lang.Double
   *  at java.lang.Integer.compareTo(Integer.java:913)
   * at cytoscape.vizmap.ContinuousMapper.getRangeValue(ContinuousMapper.java:78)
   */

  for (int i=0; i < classNames.length; i++) {
    try {
      Object obj = createInstanceFromString (Class.forName (classNames [i]), string);
      return obj.getClass ();
      }
    catch (Exception e) {
      ; // try the next class
      }
    } // for i

  return null;

} // deduceClass
//--------------------------------------------------------------------------------
/**
 *  given a string and a class, dynamically create an instance of that class from
 *  the string
 */
static public Object createInstanceFromString (Class requestedClass, String ctorArg)
   throws Exception

{
  Class [] ctorArgsClasses = new Class [1];
  ctorArgsClasses [0] =  Class.forName ("java.lang.String");
  Object [] ctorArgs = new Object [1];
  ctorArgs [0] = new String (ctorArg);
  Constructor ctor = requestedClass.getConstructor (ctorArgsClasses);
  return ctor.newInstance (ctorArgs);

} // createInstanceFromString

//--------------------------------------------------------------------------------
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
public void readAttributesFromFile(BioDataServer dataServer, String species,
        String filename, boolean canonicalize) throws IOException,
        IllegalArgumentException, NumberFormatException {

   if (taskMonitor != null) {
      taskMonitor.setStatus("Importing Attributes...");
   }

   String rawText;
   if (filename.trim().startsWith("jar://")) {
      TextJarReader reader = new TextJarReader(filename);
      reader.read();
      rawText = reader.getText();
   } else if ( filename.trim().startsWith("http://") || filename.trim().startsWith( "file://") ) {
      try {
	 TextHttpReader reader = new TextHttpReader( filename );
	 rawText = reader.getText();
      } catch ( Exception e ) {
	 throw new IOException( e.getMessage() );
      } // end of try-catch
       
   } else {
      TextFileReader reader = new TextFileReader(filename);
      reader.read();
      rawText = reader.getText();
   }

   StringTokenizer lineTokenizer = new StringTokenizer(rawText, "\n");

   int lineNumber = 0;
   if (lineTokenizer.countTokens() < 2) {
      throw new IllegalArgumentException
	 (filename + " must have at least 2 lines");
   }

   String attributeName = processFileHeader
      (lineTokenizer.nextToken().trim());
   boolean extractingFirstValue = true;

   int numTokens = lineTokenizer.countTokens();

   while (lineTokenizer.hasMoreElements()) {
      String newLine = (String) lineTokenizer.nextElement();

      //  Track Progress
      if (taskMonitor != null) {
	 double percent = ((double) lineNumber / numTokens) * 100.0;
	 taskMonitor.setPercentCompleted((int) percent);
      }

      if (newLine.trim().startsWith("#")) continue;
      lineNumber++;
      StringTokenizer strtok2 = new StringTokenizer(newLine, "=");
      if (strtok2.countTokens() < 2) {
	 throw new IOException
	    ("Cannot parse line number " + lineNumber
	     + ":\n\t" + newLine + ".  This may not be a valid "
	     + "attributes file.");
      }
      String graphObjectName = strtok2.nextToken().trim();
      if (canonicalize && dataServer != null) {
	 graphObjectName = dataServer.getCanonicalName
	    (species, graphObjectName);
      }

      String rawString = newLine.substring(newLine.indexOf("=") + 1).trim();
      String[] rawList;
      boolean isList = false;
      if (Misc.isList(rawString, "(", ")", "::")) {
	 rawList = Misc.parseList(rawString, "(", ")", "::");
	 isList = true;
      } else {
	 rawList = new String[1];
	 rawList[0] = rawString;
      }
      if (extractingFirstValue && getClass(attributeName) == null) {
	 extractingFirstValue = false;  // henceforth
	 Class deducedClass = deduceClass(rawList[0]);
	 setClass(attributeName, deducedClass); // ***** Could fail ******* //
      }
      Object[] objs = new Object[rawList.length];
      Class stringClass = (new String()).getClass();

      if (getClass(attributeName).equals(stringClass)) {
	 for (int i = 0; i < rawList.length; i++) {
	    rawList[i] = rawList[i].replaceAll("\\\\n", "\n");
	 }
      }

      for (int i = 0; i < rawList.length; i++) {
	 try {
	    objs[i] = createInstanceFromString
	       (getClass(attributeName), rawList[i]);
	    if (isList) {
	       append(attributeName, graphObjectName, objs[i]);
	    } else {
	       set(attributeName, graphObjectName, objs[i]);
	    }
	 } catch (Exception e) {
	    throw new IllegalArgumentException
	       ("Could not create an instance of " +
		getClass(attributeName) + " from " + rawList[i]);
	 }
      }
   }

   //  Inform User of What Just Happened.
   if (taskMonitor != null) {
      File  file = new File (filename);
      taskMonitor.setPercentCompleted (100);
      StringBuffer sb = new StringBuffer();
      sb.append("Succesfully loaded attributes from:  "
                + file.getName());
      sb.append("\n\nAttribute Name:  " + attributeName);
      sb.append("\n\nNumber of Attributes:  " + lineNumber);
      taskMonitor.setStatus(sb.toString());
   }
}

//--------------------------------------------------------------------------------
/**
 *  return attributeName/attributeClass pairs, for every known attribute
 */
public HashMap getSummary ()
{
  HashMap result = new HashMap ();
  String [] attributeNames = getAttributeNames ();

  for (int i=0; i < attributeNames.length; i++) {
    String attributeName = attributeNames [i];
    String firstObjectName = getObjectNames (attributeName)[0];
    Object firstValue = getValue (attributeName, firstObjectName);
    result.put (attributeName, firstValue.getClass ());
    } // for i

  return result;

} // getSummary
//--------------------------------------------------------------------------------
/**
 *  for the graphObject named by canonicalName, extract and return all attributes
 *
 *  @see #getValue
 */
public HashMap getAttributes  (String canonicalName)
{
  HashMap bundle = new HashMap ();
  String [] allAttributes = getAttributeNames ();
  for (int i=0; i < allAttributes.length; i++) {
    String attributeName = allAttributes [i];
    Object value = getValue (attributeName, canonicalName);
    if (value != null)
       bundle.put (attributeName, value);
    } // for i

  return bundle;

} // getAttributes
//--------------------------------------------------------------------------------
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
public int countIdentical (String graphObjectName)
{
    if(countIdMap == null){
	countIdMap = new HashMap();
    }
    Integer count = (Integer) countIdMap.get (graphObjectName);
    if (count == null)
	count = new Integer(0);

    // update the counter as well
    countIdMap.put (graphObjectName, new Integer (count.intValue() + 1));
    return count.intValue();
}
//--------------------------------------------------------------------------------
/**
 *  create a human readable version.
 */
public String toString ()
{
  StringBuffer sb = new StringBuffer ();

  sb.append ("\n-- canonicalNames: " + nameFinder.size ());
  String nameFinderString = nameFinder.toString ();
  StringTokenizer strtok = new StringTokenizer (nameFinderString, ",");
  while (strtok.hasMoreElements ()) {
    sb.append ("\n");
    sb.append (strtok.nextToken ());
    }

  String [] names = getAttributeNames ();
  sb.append ("\n-- attributes: " + names.length + "\n");
  for (int i=0; i < names.length; i++) {
    sb.append ("attribute " + i + ": " + names [i] + "  ");
    Class attributeClass = getClass (names [i]);
    String category = getCategory (names [i]);
    sb.append ("(class:" + attributeClass + ") ");
    sb.append ("(category: " + category + ")");
    sb.append ("\n");
    String [] keys = getObjectNames (names [i]);
    for (int j=0; j < keys.length; j++) {
      //Object value = getValue (names [i], keys [j]);
      Object [] arrayValue = getArrayValues (names [i], keys [j]);
      sb.append ("   " + keys [j] + " -> (");
      for (int k=0; k < arrayValue.length; k++) {
        sb.append (arrayValue [k]);
        if (k < arrayValue.length - 1)
          sb.append (", ");
        } // for k
      sb.append (")\n");
      } // for j
    } // for i

  return sb.toString ();

} // toString


/**
 * Whether or not the given class implements java.io.Serializable
 */
protected boolean implementsSerializable(Class objClass){

    if(objClass == null){
	return false;
    }

    Class [] interfaces = objClass.getInterfaces();
    Class serializable = null;
    try{
	serializable = Class.forName("java.io.Serializable");
    }catch(ClassNotFoundException e){;}

    for(int i = 0; i < interfaces.length; i++){
	if(serializable.isAssignableFrom(interfaces[i])){
	    return true;
	}
    }// for

    // if we got here, that means that this class does not implement Serializable, but maybe its parent does
    return implementsSerializable(objClass.getSuperclass());

}//implementsSerializable
//--------------------------------------------------------------------------------
public static String [] unpackPossiblyCompoundStringAttributeValue (Object value)
{
  String [] result = new String [0];
  try {
    if (value.getClass () == Class.forName ("java.lang.String")) {
      result = new String [1];
      result [0] = (String) value;
      }
    else if (value.getClass () == Class.forName ("[Ljava.lang.String;")) {
      result = (String []) value;
      }
    else if (value.getClass () == Class.forName ("java.util.Vector")) {
      Vector tmp = (Vector) value;
      result = (String []) tmp.toArray (new String [0]);
      }
    else {
      String msg = "AnnotationGui.unpackPossiblyCompoundAttributeValue, unrecognized class: " +
                   value.getClass ();
      System.err.println (msg);
      }
    } // try
  catch (ClassNotFoundException ignore) {
    ignore.printStackTrace ();
    }

  return result;

} // unpackPossiblyCompoundAttributeValue
//----------------------------------------------------------------------------------------
private void writeObject(ObjectOutputStream out) throws Exception{
    // super.writeObject gets called automatically
    System.out.println("Writing GraphObjAttributes...");
    System.out.flush();
    out.defaultWriteObject();
    System.out.println("Wrote GraphObjAttributes");
    System.out.flush();
}//writeObject
//--------------------------------------------------------------------------------
} // class GraphObjAttributes




