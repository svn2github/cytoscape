// GraphObjAttributes.java:  represent attributes of graph nodes, or graph edges
// implementation:  a hash of hashes:
//     (attributeName, hash (objName, objValue))
//
// for now, <objValues> are not dynamically discovered:  they must be
// of type Double, or String
//--------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//--------------------------------------------------------------------------------
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.*;

import cytoscape.data.*;
import cytoscape.data.readers.*;
//--------------------------------------------------------------------------------
public class GraphObjAttributes implements Cloneable {
  HashMap map;
  HashMap nameFinder;
//--------------------------------------------------------------------------------
public GraphObjAttributes ()
{
  map = new HashMap ();
  nameFinder = new HashMap ();
}
//--------------------------------------------------------------------------------
public Object clone ()
{
  GraphObjAttributes attributesClone = null;

  try {
   attributesClone =  (GraphObjAttributes) super.clone ();
    }
  catch (CloneNotSupportedException cnse) {
    System.err.println (" --- error in GraphObjAttributes.clone");
    cnse.printStackTrace ();
    }
  return attributesClone;

} // clone
//--------------------------------------------------------------------------------
public void addNameMapping (String canonicalName, Object applicationObject)
{
  nameFinder.put (applicationObject, canonicalName);
}
//--------------------------------------------------------------------------------
public void clearNameMap ()
{
  nameFinder = new HashMap ();
}
//--------------------------------------------------------------------------------
public HashMap getNameMap ()
{
  return nameFinder;
}
//--------------------------------------------------------------------------------
public void addNameMap (HashMap nameMapping)
{
  nameFinder.putAll (nameMapping);  
}
//--------------------------------------------------------------------------------
public String getCanonicalName (Object applicationObject)
{ 
  String name = (String) nameFinder.get (applicationObject);

  assert name != null;

  return name;
}
//--------------------------------------------------------------------------------
public void add (GraphObjAttributes attributes)
{
  String [] newAttributeNames = attributes.getAttributeNames ();

  for (int i=0; i < newAttributeNames.length; i++) {
    String name =  newAttributeNames [i];
    HashMap hash = attributes.getAttribute (newAttributeNames [i]);
    map.put (name, hash);
    }

} // add
//--------------------------------------------------------------------------------
public boolean add (String attributeName, String objectName, Object value)
{
  if (!map.containsKey (attributeName))
    map.put (attributeName, new HashMap ());

  HashMap attributeMap = (HashMap) map.get (attributeName);
  attributeMap.put (objectName, value);

  return true;

} // add
//--------------------------------------------------------------------------------
public boolean add (String attributeName, String objectName, Double value)
{
  return add (attributeName, objectName, (Object) value);

} // add
//--------------------------------------------------------------------------------
public boolean add (String attributeName, String objectName, double value)
{
  return add (attributeName, objectName, new Double (value));

} // add
//--------------------------------------------------------------------------------
public boolean add (String attributeName, String objectName, String value)
{
  return add (attributeName, objectName, (Object) value);

} // add
//--------------------------------------------------------------------------------
public int size ()
{
  return map.size ();
}
//--------------------------------------------------------------------------------
public String [] getAttributeNames ()
{
  return (String []) map.keySet().toArray (new String [0]);
}
//--------------------------------------------------------------------------------
public String [] getObjectNames (String attributeName)
{
  HashMap attributeMap = getAttribute (attributeName);
  if (attributeMap == null)
    return new String [0];
  
  return (String []) attributeMap.keySet().toArray (new String [0]);

} // getObjectNames
//--------------------------------------------------------------------------------
public boolean hasAttribute (String attributeName)
{
  return map.containsKey (attributeName);
}
//--------------------------------------------------------------------------------
public HashMap getAttribute (String attributeName)
{
  return (HashMap) map.get (attributeName);
}
//--------------------------------------------------------------------------------
public Object getValue (String attribute, String objectName)
{
  HashMap attributeMap = (HashMap) map.get (attribute);
  if (attributeMap == null)
    return null;

  if (!attributeMap.containsKey (objectName))
    return null;
 
   return attributeMap.get (objectName);
  
} // getValue
//--------------------------------------------------------------------------------
public Double getDoubleValue (String attribute, String objectName)
{
  HashMap attributeMap = (HashMap) map.get (attribute);
  if (attributeMap == null)
    return null;

  if (!attributeMap.containsKey (objectName))
    return null;
 
   return (Double) attributeMap.get (objectName);
  
} // getDoubleValue
//--------------------------------------------------------------------------------
public String getStringValue (String attribute, String objectName)
{
  HashMap attributeMap = (HashMap) map.get (attribute);
  if (attributeMap == null)
    return null;

  if (!attributeMap.containsKey (objectName))
    return null;
 
   return (String) attributeMap.get (objectName);
  
} // getStringValue
//--------------------------------------------------------------------------------
public void readFloatAttributesFromFile (String filename)
   throws FileNotFoundException, IllegalArgumentException, NumberFormatException
{
  readFloatAttributesFromFile (new File (filename));
}
//--------------------------------------------------------------------------------
public void readFloatAttributesFromFile (File file)
   throws FileNotFoundException, IllegalArgumentException, NumberFormatException
{

  TextFileReader reader = new TextFileReader (file.getPath ());
  reader.read ();
  String rawText = reader.getText ();
  StringTokenizer strtok = new StringTokenizer (rawText, "\n");

  int lineNumber = 0;
  if (strtok.countTokens () < 2) 
    throw new IllegalArgumentException (file.getPath () + " must have at least 2 lines");

  String attributeName = strtok.nextToken().trim ();
  if (attributeName.indexOf (" ") >= 0)
    throw new IllegalArgumentException ("attribute name: '" + attributeName + 
                                        "' must have no embedded spaces");

  while (strtok.hasMoreElements ()) {
    String newLine = (String) strtok.nextElement ();
    lineNumber++;
    StringTokenizer strtok2 = new StringTokenizer (newLine);
    if (strtok2.countTokens () != 2)
      throw new IllegalArgumentException ("cannot parse line number " + lineNumber +
                                          ": " + newLine);
    String objectName = strtok2.nextToken ();
    String doubleString = strtok2.nextToken ();
    try {
      Double value = new Double (doubleString);
      add (attributeName, objectName, value);
      }
    catch (NumberFormatException nfe) {
      throw new IllegalArgumentException (
          "cannot parse double on line number " + lineNumber +
                                          ": " + newLine);
       }
    } // while strtok finds new lines

} // readFloatAttributesFromFile
//--------------------------------------------------------------------------------
public HashMap getAttributes (String canonicalName)
{
  HashMap bundle = new HashMap ();
  String [] allAttributes = getAttributeNames ();
  for (int i=0; i < allAttributes.length; i++) {
    String attribute = allAttributes [i];
    Object value = getValue (attribute, canonicalName);
    if (value != null)
       bundle.put (attribute, value);
    } // for i

  return bundle;

} // getAttributes
//--------------------------------------------------------------------------------
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
 * this method provides a count of matches based on 
 * String.startsWith ("VNG0382G -> VNG1230G") which solves the problem
 * of all subsequent duplicates simply append a number to the base name.
 */
public int countIdentical (String attribute, String objectName) 
{
  String [] knownNames = getObjectNames (attribute);

  int matches = 0;

  for (int i=0; i < knownNames.length; i++) {
    if (knownNames [i].startsWith (objectName))
      matches++;
    } // for i

  return matches;
    
} // countIdentical
//--------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("-- canonicalNames\n" + nameFinder + "\n");
  sb.append ("-- atributes\n");
  String [] names = getAttributeNames ();
  for (int i=0; i < names.length; i++) {
    sb.append ("attribute " + i + ": " + names [i] + "\n");
    String [] keys = getObjectNames (names [i]);
    for (int j=0; j < keys.length; j++) {
      Object value = getValue (names [i], keys [j]);
      sb.append ("   " + keys [j] + " -> " + value + "\n");
      } // for j
    } // for i

  return sb.toString ();

} // toString
//--------------------------------------------------------------------------------
} // class GraphObjAttributes


