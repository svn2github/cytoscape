// FlattenIntVectors.java:  given a vector which nests other vectors, each resolving

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
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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



