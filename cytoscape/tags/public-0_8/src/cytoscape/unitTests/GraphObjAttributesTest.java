// GraphObjAttributesTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.HashMap;

import cytoscape.GraphObjAttributes;
//------------------------------------------------------------------------------
public class GraphObjAttributesTest extends TestCase {


//------------------------------------------------------------------------------
public GraphObjAttributesTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
public void testCtor () throws Exception
{ 
  System.out.println ("testCtor");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.size () == 0);

} // testAllArgs
//-------------------------------------------------------------------------
public void testAdd () throws Exception
{ 
  System.out.println ("testAdd");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.size () == 0);

  attributes.add ("expressionLevel", "GAL4", 1.8);
  assertTrue (attributes.size () == 1);
  attributes.add ("expressionLevel", "GAL80", 0.01);
  assertTrue (attributes.size () == 1);

  attributes.add ("foo", "GAL4", 321.23);
  assertTrue (attributes.size () == 2);

} // testAllArgs
//-------------------------------------------------------------------------
public void testHasAttribute () throws Exception
{
  System.out.println ("testHasAttribute");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  attributes.add ("expressionLevel", "GAL4", 1.8);
  attributes.add ("expressionLevel", "GAL80", 0.01);
  attributes.add ("foo", "GAL4", 321.23);
  assertTrue (attributes.hasAttribute ("expressionLevel"));
  assertTrue (attributes.hasAttribute ("foo"));
  assertTrue (!attributes.hasAttribute ("bar"));

  String [] names = attributes.getAttributeNames ();
  assertTrue (names.length == 2);
  
} // testHasAttribute
//-------------------------------------------------------------------------
public void testGetAttributeNames () throws Exception
{
  System.out.println ("testGetAttributeNames");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  attributes.add ("expressionLevel", "GAL4", 1.8);
  attributes.add ("expressionLevel", "GAL80", 0.01);
  attributes.add ("foo", "GAL4", 321.23);
  assertTrue (attributes.size () == 2);

  String [] names = attributes.getAttributeNames ();
  assertTrue (names.length == 2);
  
} // testGetAttributeNames
//-------------------------------------------------------------------------
public void testGetAttributeByName () throws Exception
{
  System.out.println ("testGetAttributeByName");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  attributes.add ("expressionLevel", "GAL4", 1.8);
  attributes.add ("expressionLevel", "GAL80", 0.01);
  attributes.add ("foo", "GAL4", 321.23);
  assertTrue (attributes.size () == 2);

  String [] names = attributes.getAttributeNames ();
  assertTrue (names.length == 2);

  HashMap expressionLevels = attributes.getAttribute ("expressionLevel");
  assertTrue (expressionLevels != null);
  assertTrue (expressionLevels.size () == 2);
  
  HashMap foo = attributes.getAttribute ("foo");
  assertTrue (foo != null);
  assertTrue (foo.size () == 1);
  
  HashMap bar = attributes.getAttribute ("bar");
  assertTrue (bar == null);
  
} // testGetAttributeByName
//-------------------------------------------------------------------------
public void testGetOneGeneAttribute () throws Exception
{
  System.out.println ("testGetOneGeneAttribute");
  GraphObjAttributes attributes = new GraphObjAttributes ();

  double gal4_exp = 1.8;
  double gal80_exp = 0.01;

  attributes.add ("expressionLevel", "GAL4", gal4_exp);
  attributes.add ("expressionLevel", "GAL80", gal80_exp);

  Double gal4_foo = new Double (321.23);
  attributes.add ("foo", "GAL4", gal4_foo);

  assertTrue (attributes.size () == 2);

  Double actual = attributes.getDoubleValue ("expressionLevel", "GAL4");
  assertTrue (actual.compareTo (new Double (gal4_exp)) == 0);

  actual = attributes.getDoubleValue ("expressionLevel", "GAL80");
  assertTrue (actual.compareTo (new Double (gal80_exp)) == 0);

  actual = attributes.getDoubleValue ("foo", "GAL4");
  assertTrue (actual.compareTo(gal4_foo) == 0);

  actual = attributes.getDoubleValue ("phoo", "GAL4");
  assertTrue (actual == null);

  actual = attributes.getDoubleValue ("foo", "GUY4");
  assertTrue (actual == null);
  
} // testGetOneGeneAttribute
//-------------------------------------------------------------------------
public void testTextFileReaderOnNodeAttributeData () throws Exception
{
  System.out.println ("testTextFileReaderOnNodeAttributeData");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.size () == 0);
  String attributeName = "fooB";
  attributes.readAttributesFromFile (new File ("../testData/noLabels.fooB"));
  assertTrue (attributes.size () == 1);
  HashMap fooB = attributes.getAttribute ("fooB");
  assertTrue (fooB.size () == 333);

} // testTextFileReaderOnNodeAttributeData
//-------------------------------------------------------------------------
public void testTextFileReaderOnEdgeAttributeData () throws Exception
{
  System.out.println ("testTextFileReaderOnEdgeAttributeData");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.size () == 0);
  File file = new File ("../testData/yeastSmall.edgeAttr.0");
  attributes.readAttributesFromFile (file);
  assertTrue (attributes.size () == 1);

  String [] attributeNames = attributes.getAttributeNames ();
  assertTrue (attributeNames.length == 1);

  HashMap edgeAttribute = attributes.getAttribute (attributeNames [0]);
  assertTrue (edgeAttribute.size () == 27);

} // testTextFileReaderOnEdgeAttributeData
//-------------------------------------------------------------------------
public void testAddAttributeHash () throws Exception
// can we combine two GraphObjAttributes, by simply adding the second
// to the first?
{
  System.out.println ("testAddAttributeHash");

    // first: read in and add fooB
  GraphObjAttributes firstSet = new GraphObjAttributes ();
  assertTrue (firstSet.size () == 0);
  String attributeName = "fooB";
  firstSet.readAttributesFromFile (new File ("../testData/noLabels.fooB"));
  assertTrue (firstSet.size () == 1);
  HashMap fooB = firstSet.getAttribute ("fooB");
  assertTrue (fooB.size () == 333);

     // second: read in and add edge attributes 0
  File file = new File ("../testData/yeastSmall.edgeAttr.0");
  GraphObjAttributes secondSet = new GraphObjAttributes ();
  secondSet.readAttributesFromFile (file);
  assertTrue (secondSet.size () == 1);
  String [] attributeNames = secondSet.getAttributeNames ();
  HashMap edgeAttribute = secondSet.getAttribute (attributeNames [0]);

  firstSet.add (secondSet);

  assertTrue (firstSet.size () == 2);
  attributeNames = firstSet.getAttributeNames ();
  assertTrue (attributeNames.length == 2);
  fooB = firstSet.getAttribute ("fooB");
  assertTrue (fooB.size () == 333);
  HashMap edgeAttribute0 = firstSet.getAttribute ("edge_attribute_0");
  assertTrue (edgeAttribute0.size () == 27);

} // testAddAttributeHash
//-------------------------------------------------------------------------
public void testNodeToNameMapping () throws Exception
// an application program often deals primarily in nodes and edges,
// rather than the canonical name of nodes and edges; since those
// names are the primary keys of all the attributes, we need a
// convenient way to map from the program's objects (nodes and edges)
// to the canonical name.  test that here.
{
  System.out.println ("testNodeToNameMapping");

    // set up a single attribute 'fooB', with 333 node-value pairs
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  assertTrue (nodeAttributes.size () == 0);
  String attributeName = "fooB";
  nodeAttributes.readAttributesFromFile (new File ("../testData/noLabels.fooB"));
  assertTrue (nodeAttributes.size () == 1);
  HashMap fooB = nodeAttributes.getAttribute (attributeName);
  assertTrue (fooB.size () == 333);

    // the objects in the canonicalName/Object map will typically be
    // graph nodes or graph edges.  but any old object will do.
  Object obj1 = new Integer (1);
  Object obj2 = new Integer (2);

     // choose two nodeNames at random
  String [] nodeNames = nodeAttributes.getObjectNames (attributeName);
  int index1 = nodeNames.length / 2;
  int index2 = nodeNames.length / 3;
  String name1 = nodeNames [nodeNames.length/2];
  String name2 = nodeNames [nodeNames.length/3];
  assertTrue (name1 != null);
  assertTrue (name2 != null);
  assertTrue (name1.length () > 0);
  assertTrue (name2.length () > 0);

    // ask for mapping from nameN to ObjectN
  nodeAttributes.addNameMapping (name1, obj1);
  nodeAttributes.addNameMapping (name2, obj2);

  String canonicalName1 = nodeAttributes.getCanonicalName (obj1);
  assertTrue (canonicalName1.equals (name1));

  String canonicalName2 = nodeAttributes.getCanonicalName (obj2);
  assertTrue (canonicalName2.equals (name2));

  String intentionalError = nodeAttributes.getCanonicalName (new Double (99999.9999));
  assertTrue (intentionalError == null);

} // testNodeToNameMapping
//-------------------------------------------------------------------------
/**
 * client programs may need a hashmap of attribute/attributeValue pairs
 * for each graphObj (each node or edge).  
 * test that here.
 */
public void testGetAttributesBundle () throws Exception
{
  System.out.println ("testGetAttributesBundle");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magic = "abracadabra";
  
  String nodeName = "GAL4";

  attributes.add ("homology", nodeName, homology);
  attributes.add ("count", nodeName, count);
  attributes.add ("magic", nodeName, magic);

  HashMap bundle = attributes.getAttributes (nodeName);

  assertTrue (bundle.size () == 3);

  Object homologyResult = bundle.get ("homology");
  assertTrue (homologyResult.getClass() == (new Double (0)).getClass ());
  Double h = (Double) homologyResult;
  assertTrue (h.equals (homology));

  Object countResult = bundle.get ("count");
  assertTrue (countResult.getClass() == (new Integer (0)).getClass ());
  Integer c = (Integer) countResult;
  assertTrue (c.equals (count));

  Object magicResult = bundle.get ("magic");
  assertTrue (magicResult.getClass() == "".getClass ());
  String s = (String) magicResult;
  assertTrue (s.equals (magic));

} // testGetAttributesBundle
//-------------------------------------------------------------------------
/**
 * client programs may need to supply a hashmap of attribute/attributeValue 
 * for a new or existing node or edge.
 * test that here.
 */
public void testAddAttributesBundle () throws Exception
{
  System.out.println ("testAddAttributesBundle");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magic = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  magic);

  attributes.add (nodeName, bundle);

  HashMap bundleRetrieved = attributes.getAttributes (nodeName);

  assertTrue (bundleRetrieved.size () == 3);

  Object homologyResult = bundleRetrieved.get ("homology");
  assertTrue (homologyResult.getClass() == (new Double (0)).getClass ());
  Double h = (Double) homologyResult;
  assertTrue (h.equals (homology));

  Object countResult = bundleRetrieved.get ("count");
  assertTrue (countResult.getClass() == (new Integer (0)).getClass ());
  Integer c = (Integer) countResult;
  assertTrue (c.equals (count));

  Object magicResult = bundleRetrieved.get ("magic");
  assertTrue (magicResult.getClass() == "".getClass ());
  String s = (String) magicResult;
  assertTrue (s.equals (magic));

} // testAddAttributesBundle
//-------------------------------------------------------------------------
/**
 *  multiple GraphObj's (edges in particular) may have the same name; this method
 *  counts names which begin with the same string.  for instance
 *  there may be two edges between the same pair of nodes:
 * 
 *    VNG0382G phylogeneticPattern VNG1230G
 *    VNG0382G geneFusion          VNG1232G
 *
 * the first pair encountered may be give the name
 *  
 *    VNG0382G -> VNG1230G
 * 
 * we may wish to give the second pair the name
 *
 *    VNG0382G -> VNG1230G_1
 * 
 */
public void testCountDuplicateNamesForAttribute () throws Exception
{
  System.out.println ("testCountDuplicateNamesForAttribute");

  GraphObjAttributes attributes = new GraphObjAttributes ();
  //attributes.initCountMap();

  assertTrue (attributes.countIdentical ("A") == 0); 
  assertTrue (attributes.countIdentical ("B") == 0);
  assertTrue (attributes.countIdentical ("A") == 1);
  assertTrue (attributes.countIdentical ("A") == 2);
  assertTrue (attributes.countIdentical ("B") == 1);
  /*
  assertTrue (attributes.countIdentical ("interaction", "VNG0382G -> VNG1230G") == 0);

  attributes.add ("interaction", "VNG0382G -> VNG1230G", "phylogeneticPattern");
  assertTrue (attributes.countIdentical ("interaction", "VNG0382G -> VNG1230G") == 1);

  attributes.add ("interaction", "VNG0382G -> VNG1230G_1", "phylogeneticPattern");
  assertTrue (attributes.countIdentical ("interaction", "VNG0382G -> VNG1230G") == 2);
  */
  // attributes.finalCountMap();
} // testCountDuplicateNamesForAttribute
//-------------------------------------------------------------------------
/**
 * in some cases we need to get the name map, and add it to another.
 * (one place this comes up is in the reading of successive edge attributes)
 * make sure we can get a name map; add a name map to another one, and get the
 * combined map back
 */
public void testGetAndAddNameMapping () throws Exception
{
  System.out.println ("testGetAndAddNameMapping");
    // set up a single attribute 'fooB', with 333 node-value pairs
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  assertTrue (nodeAttributes.size () == 0);
  String attributeName = "fooB";
  nodeAttributes.readAttributesFromFile (new File ("../testData/noLabels.fooB"));
  assertTrue (nodeAttributes.size () == 1);
  HashMap fooB = nodeAttributes.getAttribute (attributeName);
  assertTrue (fooB.size () == 333);

    // the objects in the canonicalName/Object map will typically be
    // graph nodes or graph edges.  but any old object will do.
  Object obj1 = new Integer (1);
  Object obj2 = new Integer (2);

     // choose two nodeNames at random
  String [] nodeNames = nodeAttributes.getObjectNames (attributeName);
  int index1 = nodeNames.length / 2;
  int index2 = nodeNames.length / 3;
  String name1 = nodeNames [index1];
  String name2 = nodeNames [index2];
  assertTrue (name1 != null);
  assertTrue (name2 != null);
  assertTrue (name1.length () > 0);
  assertTrue (name2.length () > 0);

    // ask for mapping from nameN to ObjectN
  nodeAttributes.addNameMapping (name1, obj1);
  nodeAttributes.addNameMapping (name2, obj2);

  String canonicalName1 = nodeAttributes.getCanonicalName (obj1);
  assertTrue (canonicalName1.equals (name1));

  String canonicalName2 = nodeAttributes.getCanonicalName (obj2);
  assertTrue (canonicalName2.equals (name2));

  String intentionalError = nodeAttributes.getCanonicalName (new Double (99999.9999));
  assertTrue (intentionalError == null);

  HashMap nameMap = nodeAttributes.getNameMap ();
  assertTrue (nameMap.size () == 2);

    // add this back; make sure there is no change:  these are all duplicates
  nodeAttributes.addNameMap (nameMap);
  assertTrue (nameMap.size () == 2);

  HashMap newMap = new HashMap ();
  Object obj3 = new Integer (3);
  Object obj4 = new Integer (4);
  int index3 = nodeNames.length / 4;
  int index4 = nodeNames.length / 5;
  String name3 = nodeNames [index3];
  String name4 = nodeNames [index4];
  newMap.put (obj3, name3);   
  newMap.put (obj4, name4);   

  nodeAttributes.addNameMap (newMap);
  assertTrue (nameMap.size () == 4);

  String canonicalName3 = nodeAttributes.getCanonicalName (obj3);

  assertTrue (canonicalName3.equals (name3));

  String canonicalName4 = nodeAttributes.getCanonicalName (obj4);
  assertTrue (canonicalName4.equals (name4));

  intentionalError = nodeAttributes.getCanonicalName (new Double (99999.9999));
  assertTrue (intentionalError == null);

} // testGetAndAddNameMapping
//-------------------------------------------------------------------------
/**
 * client programs may need a list of all attributes: their name, their
 * type, and -- maybe someday -- their range.
 * test that here.
 */
public void testGetAttributeSummary () throws Exception
{
  System.out.println ("testGetAttributeSummary");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magic = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  magic);

  attributes.add (nodeName, bundle);
  HashMap summary = attributes.getSummary ();

  assertTrue (summary.size () == 3);
  assertTrue (summary.get ("homology") == homology.getClass ());
  assertTrue (summary.get ("count") == count.getClass ());
  assertTrue (summary.get ("magic") == magic.getClass ());

} // testGetAttributeSummary
//-------------------------------------------------------------------------
/**
 * can we get back exactly the java class of an attribute?
 */
public void testGetAttributeClass () throws Exception
{
  System.out.println ("testGetAttributeClass");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magic = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  magic);

  attributes.add (nodeName, bundle);

  assertTrue (attributes.getClass ("homology") == (new Double (0.0)).getClass ());
  assertTrue (attributes.getClass ("count") == (new Integer (0)).getClass ());
  assertTrue (attributes.getClass ("magic") == "string".getClass ());

} // testGetAttributeClass
//-------------------------------------------------------------------------
/**
 * does the clone method return a true copy, with no real identity?
 * a simple way to check this is to change an attribute in either the
 * original or the clone, and then make sure that the other is unchanged.
 */
public void testCloning () throws Exception
{
  System.out.println ("testCloning");

  GraphObjAttributes original = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magicWord = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  magicWord);

  original.add (nodeName, bundle);

  GraphObjAttributes clone = (GraphObjAttributes) original.clone ();
  //System.out.println ("-- original: " + original);
  //System.out.println ("-- clone: " + clone);
  assertTrue (original != clone);

  String magicWordRetrievedFromOriginal = (String) original.getValue ("magic", "GAL4");
  String magicWordRetrievedFromClone  = (String) clone.getValue ("magic", "GAL4");

  assertTrue (magicWordRetrievedFromOriginal.equals (magicWord));
  assertTrue (magicWordRetrievedFromClone.equals (magicWord));

   // now change magic word in the clone.  is the original affected?

  String newMagicWord = "shazam!";

  clone.add ("magic", "GAL4", newMagicWord);
  magicWordRetrievedFromClone  = (String) clone.getValue ("magic", "GAL4");
  assertTrue (magicWordRetrievedFromClone.equals (newMagicWord));

  magicWordRetrievedFromOriginal = (String) original.getValue ("magic", "GAL4");
  assertTrue (!magicWordRetrievedFromOriginal.equals (newMagicWord));


} // testCloning
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (GraphObjAttributesTest.class));
}
//------------------------------------------------------------------------------
} // GraphObjAttributesTest
