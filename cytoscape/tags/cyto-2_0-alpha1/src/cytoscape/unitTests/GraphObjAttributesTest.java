// GraphObjAttributesTest.java

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

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.unitTests;
//------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.*;

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
  AllTests.standardOut("testCtor");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.numberOfAttributes () == 0);

} // testAllArgs
//-------------------------------------------------------------------------
public void testSet () throws Exception
{ 
  AllTests.standardOut("testSet");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.numberOfAttributes () == 0);

  attributes.set ("expressionLevel", "GAL4", 1.8);
  assertTrue (attributes.numberOfAttributes () == 1);
  attributes.set ("expressionLevel", "GAL80", 0.01);
  assertTrue (attributes.numberOfAttributes () == 1);

  attributes.set ("foo", "GAL4", 321.23);
  assertTrue (attributes.numberOfAttributes () == 2);

} // testSet
//-------------------------------------------------------------------------
public void testGetSingleStringValueFromVector () throws Exception
{ 
  AllTests.standardOut ("testGetSingleStringValueFromVector");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.numberOfAttributes () == 0);

  String firstSynonym = "synonym 1";
  String secondSynonym = "synonym 2";

  attributes.append ("synonym", "GAL4", firstSynonym);
  attributes.append ("synonym", "GAL4", secondSynonym);
  assertTrue (attributes.numberOfAttributes () == 1);

  String synonym = attributes.getStringValue ("synonym", "GAL4");
  assertTrue (synonym.equals (firstSynonym));;

} // testGetSingleStringValueFromVector 
//-------------------------------------------------------------------------
public void testGetSingleDoubleValueFromVector () throws Exception
{ 
  AllTests.standardOut ("testGetSingleDoubleValueFromVector");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.numberOfAttributes () == 0);

  Double d0 = new Double (1.0);
  Double d1 = new Double (2.0);

  attributes.append ("score", "GAL4", d0);
  attributes.append ("score", "GAL4", d1);
  assertTrue (attributes.numberOfAttributes () == 1);

  Double retrievedValue = attributes.getDoubleValue ("score", "GAL4");
  assertTrue (retrievedValue.equals (d0));

  Object [] objs = attributes.getArrayValues ("score", "GAL4");
  assertTrue (objs.length == 2);

  Double retrieved0 = (Double) objs [0];
  Double retrieved1 = (Double) objs [1];
  assertTrue (retrieved0.equals (d0));
  assertTrue (retrieved1.equals (d1));


} // testGetSingleDoubleValueFromVector 
//-------------------------------------------------------------------------
/**
 *  four overloaded 'add' methods are now deprected, in favor of 'set'
 *  methods
 */ 
public void testAdd () throws Exception
{ 
    AllTests.standardOut("testAdd");

    //-----------------------------------------------------------------------
    // test the basic form:  add (attributeName, nodeName, value)
    //-----------------------------------------------------------------------

  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.numberOfAttributes () == 0);

  attributes.add ("expressionLevel", "GAL4", 1.8);
  assertTrue (attributes.numberOfAttributes () == 1);
  attributes.add ("expressionLevel", "GAL80", 0.01);
  assertTrue (attributes.numberOfAttributes () == 1);

  attributes.add ("foo", "GAL4", 321.23);
  assertTrue (attributes.numberOfAttributes () == 2);

  GraphObjAttributes copy = (GraphObjAttributes) attributes.clone ();
  assertTrue (copy.numberOfAttributes () == 2);

  attributes.deleteAttribute ("expressionLevel");
  attributes.deleteAttribute ("foo");
  assertTrue (copy.numberOfAttributes () == 2);
     
    //-----------------------------------------------------------------------
    // test attributes.add (<previously existing attributes>)
    //-----------------------------------------------------------------------

  GraphObjAttributes v2 = new GraphObjAttributes ();
  assertTrue (v2.numberOfAttributes () == 0);
  v2.add (copy);
  assertTrue (v2.numberOfAttributes () == 2);

    //-----------------------------------------------------------------------
    // test attributes.add (HashMap bundle)
    //-----------------------------------------------------------------------

  HashMap bundle = v2.getAttributes ("GAL4");
  assertTrue (bundle.size () == 2);

  v2.deleteAttribute ("expressionLevel");
  v2.deleteAttribute ("foo");
  assertTrue (v2.numberOfAttributes () == 0);

  v2.add ("GAL4", bundle);
  assertTrue (v2.numberOfAttributes () == 2);
  
    //-----------------------------------------------------------------------
    // test attributes.add (attributeName, graphObjectName, double value)
    //-----------------------------------------------------------------------

   v2.add ("whimsical", "GAL4", 101.101);
   HashMap gal4Bundle = v2.getAttributes ("GAL4");
   assertTrue (gal4Bundle.size () == 3);
   Double whimsicalRetrieved = (Double) gal4Bundle.get ("whimsical");
   assertTrue (whimsicalRetrieved.equals (new Double (101.101)));


} // testAdd
//-------------------------------------------------------------------------
/**
 *  make sure that everything is done properly when we call
 *  GraphObjAttributes.set (GraphObjAttributes attributes)
 *  where that method is explained:
 *
 *  copy all attributes in the supplied GraphObjAttributes object into this
 *  GraphObjAttributes.  any pre-existing attributes survive intact as long
 *  as they do not have the same attribute name as the attributes passed in
 */
public void testAddGraphObjAttributes () throws Exception
{
  AllTests.standardOut ("testAddGraphObjAttributes");
  GraphObjAttributes original = new GraphObjAttributes ();
  original.set ("expressionLevel", "GAL4", 1.8);
  original.set ("expressionLevel", "GAL80", 0.01);
  original.set ("foo", "GAL4", 321.23);
  original.set ("bar", "GAL4", "The Columbia City Ale House");

  assertTrue (original.hasAttribute ("expressionLevel"));
  assertTrue (original.hasAttribute ("foo"));
  assertTrue (original.hasAttribute ("bar"));
  assertTrue (!original.hasAttribute ("BAR"));

  assertTrue (original.getClass ("expressionLevel") == (new Double (0.0)).getClass ());
  assertTrue (original.getClass ("foo") == (new Double (0.0)).getClass ());
  assertTrue (original.getClass ("bar") == "string".getClass ());

  assertTrue (original.getDoubleValue ("expressionLevel", "GAL4").equals (new Double (1.8)));
  assertTrue (original.getDoubleValue ("expressionLevel", "GAL80").equals (new Double (0.01)));
  assertTrue (original.getDoubleValue ("foo", "GAL4").equals (new Double (321.23)));
  assertTrue (original.getStringValue ("bar", "GAL4").equals ("The Columbia City Ale House"));

  GraphObjAttributes additional = new GraphObjAttributes ();
  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magic = "abracadabra";
  String nodeName = "GAL4";

  additional.set ("homology", nodeName, homology);
  additional.set ("count", nodeName, count);
  additional.set ("magic", nodeName, magic);

  assertTrue (additional.getClass ("homology") == (new Double (0.0)).getClass ());
  assertTrue (additional.getClass ("count") == (new Integer (0)).getClass ());
  assertTrue (additional.getClass ("magic") == "string".getClass ());

  assertTrue (additional.getDoubleValue ("homology", nodeName).equals (homology));
  assertTrue (additional.getIntegerValue ("count", nodeName).equals (count));
  assertTrue (additional.getStringValue ("magic", nodeName).equals (magic));

  original.add (additional);
  assertTrue (original.hasAttribute ("expressionLevel"));
  assertTrue (original.hasAttribute ("foo"));
  assertTrue (original.hasAttribute ("bar"));
  assertTrue (!original.hasAttribute ("BAR"));

  assertTrue (original.getClass ("expressionLevel") == (new Double (0.0)).getClass ());
  assertTrue (original.getClass ("foo") == (new Double (0.0)).getClass ());
  assertTrue (original.getClass ("bar") == "string".getClass ());

  assertTrue (original.getDoubleValue ("expressionLevel", "GAL4").equals (new Double (1.8)));
  assertTrue (original.getDoubleValue ("expressionLevel", "GAL80").equals (new Double (0.01)));
  assertTrue (original.getDoubleValue ("foo", "GAL4").equals (new Double (321.23)));
  assertTrue (original.getStringValue ("bar", "GAL4").equals ("The Columbia City Ale House"));

  assertTrue (additional.getClass ("homology") == (new Double (0.0)).getClass ());
  assertTrue (additional.getClass ("count") == (new Integer (0)).getClass ());
  assertTrue (additional.getClass ("magic") == "string".getClass ());

  assertTrue (additional.getDoubleValue ("homology", nodeName).equals (homology));
  assertTrue (additional.getIntegerValue ("count", nodeName).equals (count));
  assertTrue (additional.getStringValue ("magic", nodeName).equals (magic));



} // testAddGraphObjAttributes
//-------------------------------------------------------------------------
public void testHasAttribute () throws Exception
{
  AllTests.standardOut ("testHasAttribute");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  attributes.set ("expressionLevel", "GAL4", 1.8);
  attributes.set ("expressionLevel", "GAL80", 0.01);
  attributes.set ("foo", "GAL4", 321.23);
  assertTrue (attributes.hasAttribute ("expressionLevel"));
  assertTrue (attributes.hasAttribute ("foo"));
  assertTrue (!attributes.hasAttribute ("bar"));

  assertTrue (attributes.getObjectCount ("expressionLevel") == 2);
  assertTrue (attributes.getObjectCount ("foo") == 1);
  assertTrue (attributes.getObjectCount ("bar") == 0);

  String [] names = attributes.getAttributeNames ();
  assertTrue (names.length == 2);

  assertTrue (attributes.hasAttribute ("expressionLevel", "GAL4"));
  assertTrue (attributes.hasAttribute ("expressionLevel", "GAL80"));
  assertTrue (attributes.hasAttribute ("foo", "GAL4"));
  assertTrue (!attributes.hasAttribute ("foo", "GAL4bogus"));
  
} // testHasAttribute
//-------------------------------------------------------------------------
public void testGetAttributeNames () throws Exception
{
  AllTests.standardOut ("testGetAttributeNames");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  attributes.set ("expressionLevel", "GAL4", 1.8);
  attributes.set ("expressionLevel", "GAL80", 0.01);
  attributes.set ("foo", "GAL4", 321.23);
  assertTrue (attributes.numberOfAttributes () == 2);

  String [] names = attributes.getAttributeNames ();
  assertTrue (names.length == 2);
  
} // testGetAttributeNames
//-------------------------------------------------------------------------
public void testGetAttributeByName () throws Exception
{
  AllTests.standardOut ("testGetAttributeByName");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  attributes.set ("expressionLevel", "GAL4", 1.8);
  attributes.set ("expressionLevel", "GAL80", 0.01);
  attributes.set ("foo", "GAL4", 321.23);
  assertTrue (attributes.numberOfAttributes () == 2);

  String [] names = attributes.getAttributeNames ();
  assertTrue (names.length == 2);

  HashMap expressionLevels = attributes.getAttribute ("expressionLevel");
  assertTrue (expressionLevels != null);
  assertTrue (expressionLevels.size () == 2);
  assertTrue (attributes.getClass ("expressionLevel") == Class.forName ("java.lang.Double"));
  Object obj = expressionLevels.get ("GAL4");
  assertTrue (obj.getClass() == Class.forName ("java.lang.Double"));
  
  HashMap foo = attributes.getAttribute ("foo");
  assertTrue (foo != null);
  assertTrue (foo.size () == 1);
  
  HashMap bar = attributes.getAttribute ("bar");
  assertTrue (bar == null);
  
} // testGetAttributeByName
//-------------------------------------------------------------------------
public void testGetOneGeneAttribute () throws Exception
{
  AllTests.standardOut ("testGetOneGeneAttribute");
  GraphObjAttributes attributes = new GraphObjAttributes ();

  double gal4_exp = 1.8;
  double gal80_exp = 0.01;

  attributes.set ("expressionLevel", "GAL4", gal4_exp);
  attributes.set ("expressionLevel", "GAL80", gal80_exp);

  Double gal4_foo = new Double (321.23);
  attributes.set ("foo", "GAL4", gal4_foo);

  assertTrue (attributes.numberOfAttributes () == 2);

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
  AllTests.standardOut ("testTextFileReaderOnNodeAttributeData");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.numberOfAttributes () == 0);
  String attributeName = "fooB";
  attributes.readAttributesFromFile (new File ("testData/noLabels.fooB"));
  assertTrue (attributes.numberOfAttributes () == 1);
  HashMap fooB = attributes.getAttribute ("fooB");
  assertTrue (fooB.size () == 333);

} // testTextFileReaderOnNodeAttributeData
//-------------------------------------------------------------------------
public void testTextFileReaderOnEdgeAttributeData () throws Exception
{
  AllTests.standardOut ("testTextFileReaderOnEdgeAttributeData");
  GraphObjAttributes attributes = new GraphObjAttributes ();
  assertTrue (attributes.numberOfAttributes () == 0);
  File file = new File ("testData/yeastSmall.edgeAttr.0");
  attributes.readAttributesFromFile (file);
  assertTrue (attributes.numberOfAttributes () == 1);

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
  AllTests.standardOut ("testAddAttributeHash");

    // first: read in and add fooB
  GraphObjAttributes firstSet = new GraphObjAttributes ();
  assertTrue (firstSet.numberOfAttributes () == 0);
  String attributeName = "fooB";
  firstSet.readAttributesFromFile (new File ("testData/noLabels.fooB"));
  assertTrue (firstSet.numberOfAttributes () == 1);
  HashMap fooB = firstSet.getAttribute ("fooB");
  assertTrue (fooB.size () == 333);

     // second: read in and add edge attributes 0
  File file = new File ("testData/yeastSmall.edgeAttr.0");
  GraphObjAttributes secondSet = new GraphObjAttributes ();
  secondSet.readAttributesFromFile (file);
  assertTrue (secondSet.numberOfAttributes () == 1);
  String [] attributeNames = secondSet.getAttributeNames ();
  HashMap edgeAttribute = secondSet.getAttribute (attributeNames [0]);

  firstSet.set (secondSet);

  assertTrue (firstSet.numberOfAttributes () == 2);
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
  AllTests.standardOut ("testNodeToNameMapping");

    // set up a single attribute 'fooB', with 333 node-value pairs
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  assertTrue (nodeAttributes.numberOfAttributes () == 0);
  String attributeName = "fooB";
  nodeAttributes.readAttributesFromFile (new File ("testData/noLabels.fooB"));
  assertTrue (nodeAttributes.numberOfAttributes () == 1);
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
 * for each graphObj (each node or edge).   test that here.
 * 
 */
public void testGetAttributesBundle () throws Exception
{
  AllTests.standardOut ("testGetAttributesBundle");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magic = "abracadabra";
  
  String nodeName = "GAL4";

  attributes.set ("homology", nodeName, homology);
  attributes.set ("count", nodeName, count);
  attributes.set ("magic", nodeName, magic);

  HashMap bundle = attributes.getAttributes (nodeName);
  assertTrue (bundle.size () == 3);

  Double homologyRetrieved = (Double) bundle.get ("homology");
  assertTrue (homologyRetrieved.equals (homology));

  Integer countRetrieved = (Integer) bundle.get ("count");
  assertTrue (countRetrieved.equals (count));

  String magicRetrieved = (String) bundle.get ("magic");
  assertTrue (magicRetrieved.equals (magic));

} // testGetAttributesBundle
//-------------------------------------------------------------------------
/**
 * client programs may need to supply a hashmap of attribute/attributeValue 
 * for a new or existing node or edge.
 * test that here.
 */
public void testAddAttributesBundle () throws Exception
{
  AllTests.standardOut ("testAddAttributesBundle");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magic = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  magic);

  attributes.set (nodeName, bundle);

  HashMap bundleRetrieved = attributes.getAttributes (nodeName);

  assertTrue (bundleRetrieved.size () == 3);

  Double homologyRetrieved = (Double) bundleRetrieved.get ("homology");
  assertTrue (homologyRetrieved.equals (homology));

  Integer countRetrieved = (Integer) bundleRetrieved.get ("count");
  assertTrue (countRetrieved.equals (count));

  String magicRetrieved = (String) bundleRetrieved.get ("magic");
  assertTrue (magicRetrieved.equals (magic));

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
  AllTests.standardOut ("testCountDuplicateNamesForAttribute");

  GraphObjAttributes attributes = new GraphObjAttributes ();
  //attributes.initCountMap();

  assertTrue (attributes.countIdentical ("A") == 0); 
  assertTrue (attributes.countIdentical ("B") == 0);
  assertTrue (attributes.countIdentical ("A") == 1);
  assertTrue (attributes.countIdentical ("A") == 2);
  assertTrue (attributes.countIdentical ("B") == 1);
  /*
  assertTrue (attributes.countIdentical ("interaction", "VNG0382G -> VNG1230G") == 0);

  attributes.set ("interaction", "VNG0382G -> VNG1230G", "phylogeneticPattern");
  assertTrue (attributes.countIdentical ("interaction", "VNG0382G -> VNG1230G") == 1);

  attributes.set ("interaction", "VNG0382G -> VNG1230G_1", "phylogeneticPattern");
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
  AllTests.standardOut ("testGetAndAddNameMapping");
    // set up a single attribute 'fooB', with 333 node-value pairs
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  assertTrue (nodeAttributes.numberOfAttributes () == 0);
  String attributeName = "fooB";
  nodeAttributes.readAttributesFromFile (new File ("testData/noLabels.fooB"));
  assertTrue (nodeAttributes.numberOfAttributes () == 1);
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
 * can we get back exactly the java class of an attribute?
 */
public void testGetAttributeClass () throws Exception
{
  AllTests.standardOut ("testGetAttributeClass");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magic = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  magic);

  attributes.set (nodeName, bundle);
  attributes.setClass ("homology", homology.getClass ());
  attributes.setClass ("count", count.getClass ());
  attributes.setClass ("magic", magic.getClass ());

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
  AllTests.standardOut ("testCloning");

  GraphObjAttributes original = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String originalMagicWord = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  originalMagicWord);

  original.set (nodeName, bundle);

  GraphObjAttributes clone = (GraphObjAttributes) original.clone ();
  assertTrue (original != clone);

  String magicWordRetrievedFromOriginal = (String) original.getValue ("magic", "GAL4");
  String magicWordRetrievedFromClone  = (String) clone.getValue ("magic", "GAL4");

  assertTrue (magicWordRetrievedFromOriginal.equals (originalMagicWord));
  assertTrue (magicWordRetrievedFromClone.equals (originalMagicWord));

   // now change magic word in the clone.  is the original affected?

  String newMagicWord = "shazam!";

  clone.set ("magic", "GAL4", newMagicWord);

  String magicWordFromClone = (String) clone.get ("magic", "GAL4");
  assertTrue (magicWordFromClone.equals (newMagicWord));

  String magicWordFromOriginal = (String) original.get ("magic", "GAL4");
  assertTrue (magicWordFromOriginal.equals (originalMagicWord));

} // testCloning
//-------------------------------------------------------------------------
/**
 *  can we delete an attribute by name?
 */
public void testDeleteAttribute () throws Exception
{
  AllTests.standardOut ("testDeleteAttribute");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magicWord = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  magicWord);

  attributes.set (nodeName, bundle);
  assertTrue (attributes.numberOfAttributes () == 3);

  attributes.deleteAttribute ("homology");
  assertTrue (attributes.numberOfAttributes () == 2);
  assertTrue (attributes.hasAttribute ("homology") == false);

  attributes.deleteAttribute ("count");
  assertTrue (attributes.numberOfAttributes () == 1);
  assertTrue (attributes.hasAttribute ("count") == false);

  attributes.deleteAttribute ("magic");
  assertTrue (attributes.numberOfAttributes () == 0);
  assertTrue (attributes.hasAttribute ("magic") == false);

} // testDeleteAttribute
//-------------------------------------------------------------------------
/**
 *  can we delete an attribute by name?
 */
public void testDeleteAttributeForOneGraphObject () throws Exception
{
  AllTests.standardOut ("testDeleteAttributeForOneGraphObject");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  Integer count = new Integer (33);
  String magicWord = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("count",  count);
  bundle.put ("magic",  magicWord);

  attributes.set (nodeName, bundle);
  assertTrue (attributes.numberOfAttributes () == 3);
  attributes.append ("homology", "GAL80", new Double (888.88));

  assertTrue (attributes.hasAttribute ("homology", "GAL80"));
  assertTrue (attributes.hasAttribute ("homology", "GAL4"));
  assertTrue (attributes.hasAttribute ("count", "GAL4"));

  attributes.deleteAttribute ("homology", "GAL4");

  assertTrue (!attributes.hasAttribute ("homology", "GAL4"));
  assertTrue (attributes.hasAttribute ("homology", "GAL80"));
  assertTrue (attributes.hasAttribute ("count", "GAL4"));

} // testDeleteAttributeForOneGraphObject
//-------------------------------------------------------------------------
/**
 *  can we delete a specific value from a named attribute of a graphObj?
 *  the full attribute, say "homolog" of a node may be a list of protein names.
 *  this test ensures that we can delete exactly one of those protein names
 *  from the list
 */
public void testDeleteAttributeValueForOneGraphObject () throws Exception
{
  AllTests.standardOut ("testDeleteAttributeValueForOneGraphObject");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  attributes.append ("homolog", "GAL4", "h0");
  attributes.append ("homolog", "GAL4", "h1");
  attributes.append ("homolog", "GAL4", "h2");

  assertTrue (attributes.numberOfAttributes () == 1);
  String [] homologNames = attributes.getStringArrayValues ("homolog", "GAL4");
  assertTrue (homologNames.length == 3);

     // delete the value "h0"
  attributes.deleteAttributeValue ("homolog", "GAL4", "h0");
  assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 2);

    // do it again.  this should have no effect
  attributes.deleteAttributeValue ("homolog", "GAL4", "h0");
  assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 2);

    // do some bogus deletes.  this, too, should change nothing
  attributes.deleteAttributeValue ("homolog", "GAL4", "hohoho");
  assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 2);
  attributes.deleteAttributeValue ("homolog", "GAL4", "hobo");
  assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 2);

  attributes.deleteAttributeValue ("homolog", "GAL5", "hobo");
  attributes.deleteAttributeValue ("homologue", "GAL4", "hobo");
  assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 2);


    // now delete "h1".  this should leave only "h2"
  attributes.deleteAttributeValue ("homolog", "GAL4", "h1");
  assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 1);
  assertTrue (attributes.getStringArrayValues ("homolog", "GAL4")[0].equals ("h2"));

    // now delete "h2"
  attributes.deleteAttributeValue ("homolog", "GAL4", "h2");
  assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 0);


} // testDeleteAttributeValueForOneGraphObject
//-------------------------------------------------------------------------
/**
 *  can we set and get attribute category?  numerical, annotation, categorizer, temporary, ...
 */
public void testAttributeCategories () throws Exception
{
  AllTests.standardOut ("testAttributeCategories");

  GraphObjAttributes attributes = new GraphObjAttributes ();

  Double homology = new Double (99.32);
  String biologicalProcess = "amino acid synthesis";
  String magicWord = "abracadabra";
  
  String nodeName = "GAL4";

  HashMap bundle = new HashMap ();
  bundle.put ("homology", homology);
  bundle.put ("biological process",  biologicalProcess);
  bundle.put ("magic",  magicWord);

  attributes.set (nodeName, bundle);
  assertTrue (attributes.numberOfAttributes () == 3);

  attributes.setCategory ("homology", "numerical");
  attributes.setCategory ("biological process", "annotation");

  assertTrue (attributes.getCategory ("homology").equals ("numerical"));
  assertTrue (attributes.getCategory ("magic") == null);
  assertTrue (attributes.getCategory ("biological process").equals ("annotation"));

  assertTrue (attributes.getCategory ("nonexistent") == null);


} // testAttributeCategories
//-------------------------------------------------------------------------
/**
 *  can we handle the several possible varieties of header lines?
 *
 *    SNP Count
 *    SNP Count (category=data)
 *    SNP Count (class=java.lang.Integer)
 *    SNP Count (category=data) (class=java.lang.Integer)
 *
 */
public void testProcessFileHeader () throws Exception
{
  AllTests.standardOut ("testProcessFileHeader");
  String s0 = "SNP Count";
  String s1 = "SNP Count (category=data)";
  String s2 = "SNP Count (class=java.lang.Integer)";
  String s3 = "SNP Count (category=data) (class=java.lang.Integer)";

  GraphObjAttributes a = new GraphObjAttributes ();
  a.processFileHeader (s0);
  a.processFileHeader (s1);
  a.processFileHeader (s2);
  a.processFileHeader (s3);

} // testProcessFileHeader
//-------------------------------------------------------------------------
/**
 *  can we create objects of a requested type from an appropriate string argument?
 *
 *   java.lang.Integer, 32
 *   java.lang.Double, 32.23
 *   java.net.URL, http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3291
 *
 */
public void testClassDeduction () throws Exception
{
  AllTests.standardOut ("testClassDeduction");

  String integerString = "32";
  String doubleString = "32.23";
  String urlString = "http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3294";
  String string = "a very fine day in Havanna";

      // --  Integer attribute classes confuse cytoscape.vizmap.ContinuousMapper
      /** vizmapper error:
       * Exception in thread "main" java.lang.ClassCastException: java.lang.Double
       *  at java.lang.Integer.compareTo(Integer.java:913)
       * at cytoscape.vizmap.ContinuousMapper.getRangeValue(ContinuousMapper.java:78)
       */
  //Class deducedClass = GraphObjAttributes.deduceClass (integerString);
  //assertTrue (deducedClass == Class.forName ("java.lang.Integer"));

  Class deducedClass = GraphObjAttributes.deduceClass (doubleString);
  assertTrue (deducedClass == Class.forName ("java.lang.Double"));

  deducedClass = GraphObjAttributes.deduceClass (urlString);
  assertTrue (deducedClass == Class.forName ("java.net.URL"));

  deducedClass = GraphObjAttributes.deduceClass (string);
  assertTrue (deducedClass == Class.forName ("java.lang.String"));

} // testObjectCreation
//-------------------------------------------------------------------------
/**
 *  can we create objects of a requested type from an appropriate string argument?
 *
 *   java.lang.Integer, 32
 *   java.lang.Double, 32.23
 *   java.net.URL, http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3291
 *
 */
public void testObjectCreation () throws Exception
{
  AllTests.standardOut ("testObjectCreation");
  Class integerClass = Class.forName ("java.lang.Integer");
  Class stringClass = Class.forName ("java.lang.String");
  Class doubleClass = Class.forName ("java.lang.Double");
  Class urlClass = Class.forName ("java.net.URL");

  String integerString = "32";
  String urlString = "http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3294";

    //----------------------------------------------------------------
    // first, make sure we can create an Integer, Double, and URL
    // successfully
    //----------------------------------------------------------------

  Object o = GraphObjAttributes.createInstanceFromString (integerClass, integerString);
  assertTrue (o.getClass () == integerClass);

  o = GraphObjAttributes.createInstanceFromString (stringClass, integerString);
  assertTrue (o.getClass () == stringClass);

  o = GraphObjAttributes.createInstanceFromString (doubleClass, integerString);
  assertTrue (o.getClass () == doubleClass);

  o = GraphObjAttributes.createInstanceFromString (urlClass, urlString);
  assertTrue (o.getClass () == urlClass);

    //-----------------------------------
    // now do some that will fail
    //-----------------------------------

  try {
    o = GraphObjAttributes.createInstanceFromString (urlClass, integerString);
    assertTrue (o.getClass () == urlClass);
    }
  catch (Exception e) {;}

  try {
    o = GraphObjAttributes.createInstanceFromString (doubleClass, urlString);
    assertTrue (o.getClass () == stringClass);
    }
  catch (Exception e) {;}


} // testObjectCreation
//-------------------------------------------------------------------------
/**
 *  can we read (and/or infer) attribute category and class from some
 *  combination of the attribute file header, and the file contents?
 *
 *    SNP Count  
 *    SNP Count (category=data)
 *    SNP Count (class=java.lang.Integer)
 *    SNP Count (category=data) (class=java.lang.Integer)
 *
 */
public void testAttributeCategoryAndClassDetection () throws Exception
{
  AllTests.standardOut ("testAttributeCategoryAndClassDetection");

  GraphObjAttributes a = new GraphObjAttributes ();
  a.readAttributesFromFile (new File ("testData/implicitStringNoCategory.attribute"));
  assertTrue (a.getClass ("sample zero") == "string".getClass ());
  assertTrue (a.getCategory ("sample zero").equals (GraphObjAttributes.DEFAULT_CATEGORY));

  a = new GraphObjAttributes ();
  a.readAttributesFromFile (new File ("testData/explicitStringNoCategory.attribute"));
  assertTrue (a.getClass ("sample zero") == "string".getClass ());
  assertTrue (a.getCategory ("sample zero").equals (GraphObjAttributes.DEFAULT_CATEGORY));

  a = new GraphObjAttributes ();
  a.readAttributesFromFile (new File ("testData/implicitStringWithCategory.attribute"));
  assertTrue (a.getClass ("sample zero") == "string".getClass ());
  assertTrue (a.getCategory ("sample zero").equals ("annotation"));

  a = new GraphObjAttributes ();
  a.readAttributesFromFile (new File ("testData/explicitStringWithCategory.attribute"));
  assertTrue (a.getClass ("sample zero") == "string".getClass ());
  assertTrue (a.getCategory ("sample zero").equals ("annotation"));

  a = new GraphObjAttributes ();
  a.readAttributesFromFile (new File ("testData/explicitUrlWithCategory.attribute"));
  assertTrue (a.getClass ("locusLink") == Class.forName ("java.net.URL"));
  assertTrue (a.getCategory ("locusLink").equals ("annotation"));

  // Integer attribute classes confuse cytoscape.vizmap.ContinuousMapper
  // so disable this test for now
  //----------------------------------------
  //a = new GraphObjAttributes ();
  //a.readAttributesFromFile (new File ("testData/implicitInteger.attribute"));
  //assertTrue (a.getClass ("SNP Count") == Class.forName ("java.lang.Integer"));
  //assertTrue (a.getCategory ("SNP Count").equals (GraphObjAttributes.DEFAULT_CATEGORY));

  a = new GraphObjAttributes ();
  a.readAttributesFromFile (new File ("testData/implicitDouble.attribute"));
  assertTrue (a.getClass ("Score") == Class.forName ("java.lang.Double"));
  assertTrue (a.getCategory ("Score").equals (GraphObjAttributes.DEFAULT_CATEGORY));

  a = new GraphObjAttributes ();
  a.readAttributesFromFile (new File ("testData/implicitUrl.attribute"));
  assertTrue (a.getClass ("Locus Link") == Class.forName ("java.net.URL"));
  assertTrue (a.getCategory ("Locus Link").equals (GraphObjAttributes.DEFAULT_CATEGORY));

  
} // testAttributeCategoryAndClassDetection
//-------------------------------------------------------------------------
/**
 *  can we read array attributes?
 *
 *   GO molecular function
 *   
 */
public void testReadArrayAttributes () throws Exception
{
  AllTests.standardOut ("testReadArrayAttributes");
  String name = "GO molecular function, level 4";
  GraphObjAttributes a = new GraphObjAttributes ();
  a.readAttributesFromFile (new File ("testData/implicitStringArray.attribute"));

  String geneName = "HSD17B2";
  String [] geneFunctions = a.getStringArrayValues (name, geneName);
  assertTrue (geneFunctions.length == 2);
  assertTrue (geneFunctions[0].equals ("membrane"));
  assertTrue (geneFunctions[1].equals ("intracellular"));


  geneName = "CDH3";
  geneFunctions = a.getStringArrayValues (name, geneName);
  assertTrue (geneFunctions.length == 1);
  assertTrue (geneFunctions[0].equals ("cell adhesion molecule"));

  geneName = "AP1G1";
  geneFunctions = a.getStringArrayValues (name, geneName);
  assertTrue (geneFunctions.length == 3);
  assertTrue (geneFunctions[0].equals ("intracellular"));
  assertTrue (geneFunctions[1].equals ("clathrin adaptor"));
  assertTrue (geneFunctions[2].equals ("intracellular transporter"));

  geneName = "E2F4";
  geneFunctions = a.getStringArrayValues (name, geneName);
  assertTrue (geneFunctions.length == 1);
  assertTrue (geneFunctions[0].equals ("DNA binding"));

  //geneName = "ABC";
  //geneFunctions = a.getStringArrayValues (name, geneName);
  //assertTrue (geneFunctions.length == 3);
  //assertTrue (geneFunctions[0].equals ("DNA binding"));


} // testReadArrayAttributes
//------------------------------------------------------------------------------
/**
 *  can we get a simple array of unique object values for the specified attribute?
 */
public void testGetUniqueValues () throws Exception
{
  AllTests.standardOut ("testGetUniqueValues");
  String attributeName = "KEGG-2";
  GraphObjAttributes a = new GraphObjAttributes ();

    // using 'append' ensures that each attribute value is a list,
    // which, for this test to work, must be unpacked
  a.append ("KEGG", "GAL4", "xxx");
  a.append ("KEGG", "GAL4", "yyy");
  a.append ("KEGG", "GAL4", "zzz");

  a.append ("KEGG", "GAL3", "xxx");
  a.append ("KEGG", "GAL3", "yyy");
  a.append ("KEGG", "GAL3", "QQQ");

  Object [] uniqueValues = a.getUniqueValues ("KEGG");
  //for (int i=0; i < uniqueValues.length; i++)
  //  System.out.println ("  " + uniqueValues [i]);

  assertTrue (uniqueValues.length == 4);

  String [] uniqueStrings = a.getUniqueStringValues ("KEGG");
  assertTrue (uniqueStrings.length == 4);

} // testGetUniqueValues
//--------------------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (GraphObjAttributesTest.class));
}
//------------------------------------------------------------------------------
} // GraphObjAttributesTest