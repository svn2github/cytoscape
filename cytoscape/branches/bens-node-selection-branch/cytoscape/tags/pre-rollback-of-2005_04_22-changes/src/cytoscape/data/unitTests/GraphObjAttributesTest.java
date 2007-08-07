

// $Revision$
// $Date$
// $Author$


package cytoscape.data.unitTests;


import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.*;

import cytoscape.unitTests.AllTests;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CytoscapeDataImpl;


public class GraphObjAttributesTest extends TestCase {




  public GraphObjAttributesTest (String name) {
    super (name);
  }


  public void setUp () throws Exception {
  }


  public void tearDown () throws Exception {
  }


  public void testCtor () throws Exception { 
    AllTests.standardOut("testCtor");
    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
    assertTrue (attributes.numberOfAttributes () == 0);

  } // testAllArgs
  
  public void testSet () throws Exception { 
    AllTests.standardOut("testSet");
    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
    assertTrue (attributes.numberOfAttributes () == 0);

    attributes.set ("expressionLevel", "GAL4", 1.8);
    assertTrue (attributes.numberOfAttributes () == 1);
    attributes.set ("expressionLevel", "GAL80", 0.01);
    assertTrue (attributes.numberOfAttributes () == 1);

    attributes.set ("foo", "GAL4", 321.23);
    assertTrue (attributes.numberOfAttributes () == 2);

  } // testSet
  
  public void testGetSingleStringValueFromVector () throws Exception { 
    AllTests.standardOut ("testGetSingleStringValueFromVector");
    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
    assertTrue (attributes.numberOfAttributes () == 0);

    String firstSynonym = "synonym 1";
    String secondSynonym = "synonym 2";

    attributes.append ("synonym", "GAL4", firstSynonym);
    attributes.append ("synonym", "GAL4", secondSynonym);
    assertTrue (attributes.numberOfAttributes () == 1);

    String synonym = attributes.getStringValue ("synonym", "GAL4");
    assertTrue (synonym.equals (firstSynonym));;

  } // testGetSingleStringValueFromVector 
  
  public void testGetSingleDoubleValueFromVector () throws Exception { 
    AllTests.standardOut ("testGetSingleDoubleValueFromVector");
    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
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
  
  /**
   *  four overloaded 'add' methods are now deprected, in favor of 'set'
   *  methods
   */ 
  public void testAdd () throws Exception { 
    AllTests.standardOut("testAdd");

    //-----------------------------------------------------------------------
    // test the basic form:  add (attributeName, nodeName, value)
    //-----------------------------------------------------------------------

    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
    assertTrue (attributes.numberOfAttributes () == 0);

    attributes.set ("expressionLevel", "GAL4", 1.8);
    assertTrue (attributes.numberOfAttributes () == 1);
    attributes.set ("expressionLevel", "GAL80", 0.01);
    assertTrue (attributes.numberOfAttributes () == 1);

    attributes.set ("foo", "GAL4", 321.23);
    assertTrue (attributes.numberOfAttributes () == 2);

    attributes.deleteAttribute ("expressionLevel");
    attributes.deleteAttribute ("foo");
   
       


  } // testAdd
  
  /**
   *  make sure that everything is done properly when we call
   *  GraphObjAttributes.set (GraphObjAttributes attributes)
   *  where that method is explained:
   *
   *  copy all attributes in the supplied GraphObjAttributes object into this
   *  GraphObjAttributes.  any pre-existing attributes survive intact as long
   *  as they do not have the same attribute name as the attributes passed in
   */
  public void testAddGraphObjAttributes () throws Exception {
    AllTests.standardOut ("testAddGraphObjAttributes");
    GraphObjAttributes original = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
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

    GraphObjAttributes additional = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
    Double homology = new Double (99.32);
    Integer count = new Integer (33);
    String magic = "abracadabra";
    String nodeName = "GAL4";

    additional.set ("homology", nodeName, homology);
    additional.set ("count", nodeName, count);
    additional.set ("magic", nodeName, magic);

    assertTrue (additional.getClass ("homology") == (new Double (0.0)).getClass ());
    assertTrue (additional.getClass ("count") == (new Double (0)).getClass ());
    assertTrue (additional.getClass ("magic") == "string".getClass ());

    assertTrue (additional.getDoubleValue ("homology", nodeName).equals (homology));
    //assertTrue (additional.getDoubleValue ("count", nodeName).equals (count));
    assertTrue (additional.getStringValue ("magic", nodeName).equals (magic));

    original.set (additional);
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
    //assertTrue (additional.getClass ("count") == (new Double (0)).getClass ());
    assertTrue (additional.getClass ("magic") == "string".getClass ());

    assertTrue (additional.getDoubleValue ("homology", nodeName).equals (homology));
    ///assertTrue (additional.getDoubleValue ("count", nodeName).equals (count));
    assertTrue (additional.getStringValue ("magic", nodeName).equals (magic));



  } // testAddGraphObjAttributes
  
  public void testHasAttribute () throws Exception {
    AllTests.standardOut ("testHasAttribute");
    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
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
    //assertTrue (!attributes.hasAttribute ("foo", "GAL4bogus"));
  
  } // testHasAttribute
  
  public void testGetAttributeNames () throws Exception {
    AllTests.standardOut ("testGetAttributeNames");
    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
    attributes.set ("expressionLevel", "GAL4", 1.8);
    attributes.set ("expressionLevel", "GAL80", 0.01);
    attributes.set ("foo", "GAL4", 321.23);
    assertTrue (attributes.numberOfAttributes () == 2);

    String [] names = attributes.getAttributeNames ();
    assertTrue (names.length == 2);
  
  } // testGetAttributeNames
  
  public void testGetAttributeByName () throws Exception {
    AllTests.standardOut ("testGetAttributeByName");
    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);
    attributes.set ("expressionLevel", "GAL4", 1.8);
    attributes.set ("expressionLevel", "GAL80", 0.01);
    attributes.set ("foo", "GAL4", 321.23);
    assertTrue (attributes.numberOfAttributes () == 2);

    String [] names = attributes.getAttributeNames ();
    assertTrue (names.length == 2);

    // HashMap expressionLevels = attributes.getAttribute ("expressionLevel");
//     assertTrue (expressionLevels != null);
//     assertTrue (expressionLevels.size () == 2);
//     assertTrue (attributes.getClass ("expressionLevel") == Class.forName ("java.lang.Double"));
//     Object obj = expressionLevels.get ("GAL4");
//     assertTrue (obj.getClass() == Class.forName ("java.lang.Double"));
  
    //HashMap foo = attributes.getAttribute ("foo");
    //assertTrue (foo != null);
    //assertTrue (foo.size () == 1);
  
    //HashMap bar = attributes.getAttribute ("bar");
    //assertTrue (bar == null);
  
  } // testGetAttributeByName
  
  public void testGetOneGeneAttribute () throws Exception {
    AllTests.standardOut ("testGetOneGeneAttribute");
    GraphObjAttributes attributes = new CytoscapeDataImpl (CytoscapeDataImpl.NODES);

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

    //actual = attributes.getDoubleValue ("phoo", "GAL4");
    //assertTrue (actual == null);

    //actual = attributes.getDoubleValue ("foo", "GUY4");
    //assertTrue (actual == null);
  
  } // testGetOneGeneAttribute
  
  public void testTextFileReaderOnNodeAttributeData () throws Exception {
    AllTests.standardOut ("testTextFileReaderOnNodeAttributeData");
    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    assertTrue (attributes.numberOfAttributes () == 0);
    String attributeName = "fooB";
    attributes.readAttributesFromFile (new File ("testData/noLabels.fooB"));
    assertTrue (attributes.numberOfAttributes () == 1);
    //HashMap fooB = attributes.getAttribute ("fooB");
    //assertTrue (fooB.size () == 333);

  } // testTextFileReaderOnNodeAttributeData
  
  public void testTextFileReaderOnEdgeAttributeData () throws Exception {
    AllTests.standardOut ("testTextFileReaderOnEdgeAttributeData");
    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    assertTrue (attributes.numberOfAttributes () == 0);
    File file = new File ("testData/yeastSmall.edgeAttr.0");
    attributes.readAttributesFromFile (file);
    assertTrue (attributes.numberOfAttributes () == 1);

    String [] attributeNames = attributes.getAttributeNames ();
    assertTrue (attributeNames.length == 1);

    //HashMap edgeAttribute = attributes.getAttribute (attributeNames [0]);
    //assertTrue (edgeAttribute.size () == 27);

  } // testTextFileReaderOnEdgeAttributeData
  
   
  /**
   * client programs may need a hashmap of attribute/attributeValue pairs
   * for each graphObj (each node or edge).   test that here.
   * 
   */
  public void testGetAttributesBundle () throws Exception {
    AllTests.standardOut ("testGetAttributesBundle");

    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);

    Double homology = new Double (99.32);
    Double count = new Double (33);
    String magic = "abracadabra";
  
    String nodeName = "GAL4";

    attributes.set ("homology", nodeName, homology);
    attributes.set ("count", nodeName, count);
    attributes.set ("magic", nodeName, magic);

    HashMap bundle = attributes.getAttributes (nodeName);
    assertTrue (bundle.size () == 3);

    Double homologyRetrieved = (Double) bundle.get ("homology");
    assertTrue (homologyRetrieved.equals (homology));

    Double countRetrieved = (Double) bundle.get ("count");
    assertTrue (countRetrieved.equals (count));

    String magicRetrieved = (String) bundle.get ("magic");
    assertTrue (magicRetrieved.equals (magic));

  } // testGetAttributesBundle
  
  /**
   * client programs may need to supply a hashmap of attribute/attributeValue 
   * for a new or existing node or edge.
   * test that here.
   */
  public void testAddAttributesBundle () throws Exception {
    AllTests.standardOut ("testAddAttributesBundle");

    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);

    Double homology = new Double (99.32);
    Double count = new Double (33);
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

    Double countRetrieved = (Double) bundleRetrieved.get ("count");
    assertTrue (countRetrieved.equals (count));

    String magicRetrieved = (String) bundleRetrieved.get ("magic");
    assertTrue (magicRetrieved.equals (magic));

  } // testAddAttributesBundle
  
 
  /**
   * can we get back exactly the java class of an attribute?
   */
  public void testGetAttributeClass () throws Exception {
    AllTests.standardOut ("testGetAttributeClass");

    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);

    Double homology = new Double (99.32);
    Double count = new Double (33);
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
    assertTrue (attributes.getClass ("count") == (new Double (0)).getClass ());
    assertTrue (attributes.getClass ("magic") == "string".getClass ());

  } // testGetAttributeClass
  

  
  /**
   *  can we delete an attribute by name?
   */
  public void testDeleteAttribute () throws Exception {
    AllTests.standardOut ("testDeleteAttribute");

    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);

    Double homology = new Double (99.32);
    Double count = new Double (33);
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
  
  /**
   *  can we delete an attribute by name?
   */
  public void testDeleteAttributeForOneGraphObject () throws Exception {
    AllTests.standardOut ("testDeleteAttributeForOneGraphObject");

    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);

    Double homology = new Double (99.32);
    Double count = new Double (33);
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

    //assertTrue (!attributes.hasAttribute ("homology", "GAL4"));
    assertTrue (attributes.hasAttribute ("homology", "GAL80"));
    assertTrue (attributes.hasAttribute ("count", "GAL4"));

  } // testDeleteAttributeForOneGraphObject
  
  /**
   *  can we delete a specific value from a named attribute of a graphObj?
   *  the full attribute, say "homolog" of a node may be a list of protein names.
   *  this test ensures that we can delete exactly one of those protein names
   *  from the list
   */
  public void testDeleteAttributeValueForOneGraphObject () throws Exception {
    AllTests.standardOut ("testDeleteAttributeValueForOneGraphObject");

    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);

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
    //attributes.deleteAttributeValue ("homolog", "GAL4", "h1");
    //assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 1);
    //assertTrue (attributes.getStringArrayValues ("homolog", "GAL4")[0].equals ("h2"));

    // now delete "h2"
    //attributes.deleteAttributeValue ("homolog", "GAL4", "h2");
    //assertTrue (attributes.getStringArrayValues ("homolog", "GAL4").length == 0);


  } // testDeleteAttributeValueForOneGraphObject
  
  /**
   *  can we set and get attribute category?  numerical, annotation, categorizer, temporary, ...
   */
  public void testAttributeCategories () throws Exception {
    AllTests.standardOut ("testAttributeCategories");

    GraphObjAttributes attributes = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);

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
 
    //   attributes.setCategory ("homology", "numerical");
    //   attributes.setCategory ("biological process", "annotation");

    //   assertTrue (attributes.getCategory ("homology").equals ("numerical"));
    //   assertTrue (attributes.getCategory ("magic") == null);
    //   assertTrue (attributes.getCategory ("biological process").equals ("annotation"));

    //   assertTrue (attributes.getCategory ("nonexistent") == null);


  } // testAttributeCategories
  
  /**
   *  can we handle the several possible varieties of header lines?
   *
   *    SNP Count
   *    SNP Count (category=data)
   *    SNP Count (class=java.lang.Double)
   *    SNP Count (category=data) (class=java.lang.Double)
   *
   */
  public void testProcessFileHeader () throws Exception {
    AllTests.standardOut ("testProcessFileHeader");
    String s0 = "SNP Count";
    String s1 = "SNP Count (category=data)";
    String s2 = "SNP Count (class=java.lang.Double)";
    String s3 = "SNP Count (category=data) (class=java.lang.Double)";

    GraphObjAttributes a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    a.processFileHeader (s0);
    a.processFileHeader (s1);
    a.processFileHeader (s2);
    a.processFileHeader (s3);

  } // testProcessFileHeader
  
  /**
   *  can we create objects of a requested type from an appropriate string argument?
   *
   *   java.lang.Double, 32
   *   java.lang.Double, 32.23
   *   java.net.URL, http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3291
   *
   */
  public void testClassDeduction () throws Exception {
    AllTests.standardOut ("testClassDeduction");

    String integerString = "32";
    String doubleString = "32.23";
    String urlString = "http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3294";
    String string = "a very fine day in Havanna";

    // --  Double attribute classes confuse cytoscape.vizmap.ContinuousMapper
    /** vizmapper error:
     * Exception in thread "main" java.lang.ClassCastException: java.lang.Double
     *  at java.lang.Double.compareTo(Double.java:913)
     * at cytoscape.vizmap.ContinuousMapper.getRangeValue(ContinuousMapper.java:78)
     */
    //Class deducedClass = CytoscapeDataImpl.deduceClass (integerString);
    //assertTrue (deducedClass == Class.forName ("java.lang.Double"));

    Class deducedClass = CytoscapeDataImpl.deduceClass (doubleString);
    assertTrue (deducedClass == Class.forName ("java.lang.Double"));

    deducedClass = CytoscapeDataImpl.deduceClass (urlString);
    assertTrue (deducedClass == Class.forName ("java.net.URL"));

    deducedClass = CytoscapeDataImpl.deduceClass (string);
    assertTrue (deducedClass == Class.forName ("java.lang.String"));

  } // testObjectCreation
  
  /**
   *  can we create objects of a requested type from an appropriate string argument?
   *
   *   java.lang.Double, 32
   *   java.lang.Double, 32.23
   *   java.net.URL, http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3291
   *
   */
  public void testObjectCreation () throws Exception {
    AllTests.standardOut ("testObjectCreation");
    Class integerClass = Class.forName ("java.lang.Integer");
    Class stringClass = Class.forName ("java.lang.String");
    Class doubleClass = Class.forName ("java.lang.Double");
    Class urlClass = Class.forName ("java.net.URL");

    String integerString = "32";
    String urlString = "http://www.ncbi.nlm.nih.gov/LocusLink/LocRpt.cgi?l=3294";

    //----------------------------------------------------------------
    // first, make sure we can create an Double, Double, and URL
    // successfully
    //----------------------------------------------------------------

    Object o = CytoscapeDataImpl.createInstanceFromString (integerClass, integerString);
    assertTrue (o.getClass () == integerClass);

    o = CytoscapeDataImpl.createInstanceFromString (stringClass, integerString);
    assertTrue (o.getClass () == stringClass);

    o = CytoscapeDataImpl.createInstanceFromString (doubleClass, integerString);
    assertTrue (o.getClass () == doubleClass);

    o = CytoscapeDataImpl.createInstanceFromString (urlClass, urlString);
    assertTrue (o.getClass () == urlClass);

    //-----------------------------------
    // now do some that will fail
    //-----------------------------------

    try {
      o = CytoscapeDataImpl.createInstanceFromString (urlClass, integerString);
      assertTrue (o.getClass () == urlClass);
    }
    catch (Exception e) {;}

    try {
      o = CytoscapeDataImpl.createInstanceFromString (doubleClass, urlString);
      assertTrue (o.getClass () == stringClass);
    }
    catch (Exception e) {;}


  } // testObjectCreation
  
  /**
   *  can we read (and/or infer) attribute category and class from some
   *  combination of the attribute file header, and the file contents?
   *
   *    SNP Count  
   *    SNP Count (category=data)
   *    SNP Count (class=java.lang.Double)
   *    SNP Count (category=data) (class=java.lang.Double)
   *
   */
  public void testAttributeCategoryAndClassDetection () throws Exception {
    AllTests.standardOut ("testAttributeCategoryAndClassDetection");

    GraphObjAttributes a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    a.readAttributesFromFile (new File ("testData/implicitStringNoCategory.attribute"));
    assertTrue (a.getClass ("sample zero") == "string".getClass ());
    //assertTrue (a.getCategory ("sample zero").equals (GraphObjAttributes.DEFAULT_CATEGORY));

    a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    a.readAttributesFromFile (new File ("testData/explicitStringNoCategory.attribute"));
    assertTrue (a.getClass ("sample zero") == "string".getClass ());
    //assertTrue (a.getCategory ("sample zero").equals (GraphObjAttributes.DEFAULT_CATEGORY));

    a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    a.readAttributesFromFile (new File ("testData/implicitStringWithCategory.attribute"));
    assertTrue (a.getClass ("sample zero") == "string".getClass ());
    //assertTrue (a.getCategory ("sample zero").equals ("annotation"));

    a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    a.readAttributesFromFile (new File ("testData/explicitStringWithCategory.attribute"));
    assertTrue (a.getClass ("sample zero") == "string".getClass ());
    //assertTrue (a.getCategory ("sample zero").equals ("annotation"));

    a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    a.readAttributesFromFile (new File ("testData/explicitUrlWithCategory.attribute"));
    //assertTrue (a.getClass ("locusLink") == Class.forName ("java.net.URL"));
    //assertTrue (a.getCategory ("locusLink").equals ("annotation"));

    // Double attribute classes confuse cytoscape.vizmap.ContinuousMapper
    // so disable this test for now
    //----------------------------------------
    //a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    //a.readAttributesFromFile (new File ("testData/implicitDouble.attribute"));
    //assertTrue (a.getClass ("SNP Count") == Class.forName ("java.lang.Double"));
    //assertTrue (a.getCategory ("SNP Count").equals (GraphObjAttributes.DEFAULT_CATEGORY));

    a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    a.readAttributesFromFile (new File ("testData/implicitDouble.attribute"));
    assertTrue (a.getClass ("Score") == Class.forName ("java.lang.Double"));
    //assertTrue (a.getCategory ("Score").equals (GraphObjAttributes.DEFAULT_CATEGORY));

    a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
    a.readAttributesFromFile (new File ("testData/implicitUrl.attribute"));
    //assertTrue (a.getClass ("Locus Link") == Class.forName ("java.net.URL"));
    //assertTrue (a.getCategory ("Locus Link").equals (GraphObjAttributes.DEFAULT_CATEGORY));

  
  } // testAttributeCategoryAndClassDetection
  
  /**
   *  can we read array attributes?
   *
   *   GO molecular function
   *   
   */
  public void testReadArrayAttributes () throws Exception {
    AllTests.standardOut ("testReadArrayAttributes");
    String name = "GO molecular function, level 4";
    GraphObjAttributes a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);
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


  /**
   *  can we get a simple array of unique object values for the specified attribute?
   */
  public void testGetUniqueValues () throws Exception {
    AllTests.standardOut ("testGetUniqueValues");
    String attributeName = "KEGG-2";
    GraphObjAttributes a = new CytoscapeDataImpl(CytoscapeDataImpl.NODES);

    // using 'append' ensures that each attribute value is a list,
    // which, for this test to work, must be unpacked
    a.append ("KEGG", "GAL4", "xxx");
    a.append ("KEGG", "GAL4", "yyy");
    a.append ("KEGG", "GAL4", "zzz");

    a.append ("KEGG", "GAL3", "xxx");
    a.append ("KEGG", "GAL3", "yyy");
    a.append ("KEGG", "GAL3", "QQQ");

    Object [] uniqueValues = a.getUniqueValues ("KEGG");
    assertTrue (uniqueValues.length == 4);

    String [] uniqueStrings = a.getUniqueStringValues ("KEGG");
    assertTrue (uniqueStrings.length == 4);

  } // testGetUniqueValues

  public static void main (String [] args) {
    junit.textui.TestRunner.run (new TestSuite (GraphObjAttributesTest.class));
  }


} // GraphObjAttributesTest
