// FlattenIntVectors.java:  given a vector which nests other vectors, each resolving
// eventually to a list of Integers, flatten it out into a simple 1-level-deep
// vector of Integer vectors.
//-------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.util.Vector;
//-------------------------------------------------------------------------------
public class FlattenIntVectors {
  Vector input;
  Vector result = new Vector ();
//-------------------------------------------------------------------------------
public FlattenIntVectors (Vector v)
{
  input = v;
  flatten (v);

}
//-------------------------------------------------------------------------------
private void flatten (Vector v)
{
  if (v == null)
    return;

  Object o = v.elementAt (0);
  String className = o.getClass().getName ();

  if (!className.equalsIgnoreCase ("java.util.Vector")) {
    // System.out.println (v);
    result.addElement (v);
    }
  else {
    for (int i=0; i < v.size (); i++) {
      Vector w = (Vector) v.elementAt (i);
      flatten (w);
      } // for i
    } // else
     
} // flatten
//-------------------------------------------------------------------------
public Vector getResult ()
{
  return result;
}
//-------------------------------------------------------------------------------
} // FlattenIntVectors

