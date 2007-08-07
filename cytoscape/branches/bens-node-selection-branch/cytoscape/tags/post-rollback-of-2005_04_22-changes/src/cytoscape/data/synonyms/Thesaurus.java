// Thesaurus.java

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

//-----------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data.synonyms;
//-----------------------------------------------------------------------------
import java.io.*;
import java.util.*;
//-----------------------------------------------------------------------------
/**
 * todo (pshannon, 25 oct 2002): there may be multiple canonicalNames
 * for the same common name.  a good example is from the d16s3098
 * marker region studied in the ISB's JDRF project:
 *
 *      PHEMX -> NM_005705 
 *      PHEMX -> NM_139022
 *      PHEMX -> NM_139023
 *      PHEMX -> NM_139024
 *
 * these are (I believe) mRNA splice variants.   what to do?
 */
public class Thesaurus implements Serializable {

  String species;
  HashMap canonicalToCommon;       // String -> String
  HashMap commonToCanonical;       // String -> String
  HashMap alternatesToCanonical;   // String -> String
  HashMap canonicalToAll;          // String -> Vector of Strings

//-----------------------------------------------------------------------------
public Thesaurus (String species)
{
  this.species = species;
  canonicalToCommon = new HashMap ();
  commonToCanonical = new HashMap ();
  alternatesToCanonical = new HashMap ();
  canonicalToAll = new HashMap (); 
}
//-----------------------------------------------------------------------------
public String getSpecies ()
{
  return species;
}
//-----------------------------------------------------------------------------
public int canonicalNameCount ()
{
  return canonicalToAll.size ();
}
//-----------------------------------------------------------------------------
public void add (String canonicalName, String commonName)
{
  if (canonicalToCommon.containsKey (canonicalName) ) {
    addAlternateCommonName( canonicalName, commonName );
  }
  // throw new IllegalArgumentException ("duplicate canonicalName: " + canonicalName);

  if (commonToCanonical.containsKey (commonName)) {
    //System.out.println ("commonName " + commonName + " already has canonicalName " +
    //                    commonToCanonical.get (commonName) + " skipping new map: " +
    //                    commonName + " -> " + canonicalName);
  } else
    canonicalToCommon.put (canonicalName, commonName);

  commonToCanonical.put (commonName, canonicalName);
  storeAmongAllCommonNames (commonName, canonicalName);

} // add
//-----------------------------------------------------------------------------
public void remove (String canonicalName, String commonName)
{
  canonicalToCommon.remove (canonicalName);
  commonToCanonical.remove (commonName);
  canonicalToAll.remove (canonicalName);
}
//-----------------------------------------------------------------------------
public void addAlternateCommonName (String canonicalName, String alternateCommonName)
{
  alternatesToCanonical.put (alternateCommonName, canonicalName);
  storeAmongAllCommonNames (alternateCommonName, canonicalName);
}
//-----------------------------------------------------------------------------
protected void storeAmongAllCommonNames (String commonName, String canonicalName)
{
  Vector allCommonNames;
  if (canonicalToAll.containsKey (canonicalName))
    allCommonNames = (Vector) canonicalToAll.get (canonicalName);
  else
    allCommonNames = new Vector ();
 
  allCommonNames.add (commonName);
  canonicalToAll.put (canonicalName, allCommonNames);

} // storeAmongAllCommonNames
//-----------------------------------------------------------------------------
public String getCommonName (String canonicalName)
{
  return (String) canonicalToCommon.get (canonicalName);
}
//-----------------------------------------------------------------------------
public String getCanonicalName (String commonName)
{
  if (commonToCanonical.containsKey (commonName))
    return (String) commonToCanonical.get (commonName);
  else if (alternatesToCanonical.containsKey (commonName))
    return (String) alternatesToCanonical.get (commonName);
  else
    return null;

} // getCanonicalName
//-----------------------------------------------------------------------------
public String [] getAllCommonNames (String canonicalName)
{

  //  for ( Iterator i = canonicalToAll.keySet().iterator(); i.hasNext(); ) {
  //  System.out.println( "Key: "+i.next().toString() );
  // }

  if (canonicalToAll.containsKey (canonicalName)) {
    Vector vector = (Vector) canonicalToAll.get (canonicalName);
    return (String []) vector.toArray (new String [0]);
    }
  else
    return new String [0];

} // getAllCommonNames
//-----------------------------------------------------------------------------
public String [] getAlternateCommonNames (String canonicalName)
{
  if (canonicalToAll.containsKey (canonicalName)) {
    Vector vector = (Vector) canonicalToAll.get (canonicalName);
    vector.remove (getCommonName (canonicalName));
    return (String []) vector.toArray (new String [0]);
    }
  else
    return new String [0];

} // getAlternateCommonNames
//-----------------------------------------------------------------------------
public String toString ()
{
  int length = 0;
  if (canonicalToCommon != null)
    length = canonicalToCommon.size ();

  return species + ": " + length;
}
//-----------------------------------------------------------------------------
} // Thesaurus


