// OntologyTerm.java

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

//-----------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data.annotation;
//-----------------------------------------------------------------------------
import java.io.*;
import java.util.Vector;
//-----------------------------------------------------------------------------
/**
 *  Represents one node in an ontology, which is a hierarchical classification
 *  of entities using a controlled vocabulary.  Each term has a name, a unique
 *  integer identifier, a list of parent terms, and a list of containers.
 *  Parent terms capture the "is-child-of" relationship for this term.
 *  Container terms capture the "is-part-of" relationship.  This data model
 *  was inspired by the three ontologies (biological process, molecular function,
 *  cellular component) of the Gene Ontology project.  Other ontologies should
 *  work nicely with this class as well.
 */
public class OntologyTerm implements Serializable {
  String name;
  int id;
  Vector parents;
  Vector containers;
//-----------------------------------------------------------------------------
public OntologyTerm (String name, int id)
{
  this.name = name;
  this.id = id;
  parents = new Vector ();
  containers = new Vector ();

} // ctor
//-----------------------------------------------------------------------------
public String getName ()
{
  return name;
}
//-----------------------------------------------------------------------------
public int getId ()
{
  return id;
}
//-----------------------------------------------------------------------------
public void addParent (int newParent)
{
  parents.addElement (new Integer (newParent));
}
//-----------------------------------------------------------------------------
public void addContainer (int newContainer)
{
  containers.addElement (new Integer (newContainer));
}
//-----------------------------------------------------------------------------
public int numberOfParentsAndContainers ()
{
  return numberOfParents () + numberOfContainers ();
}
//-----------------------------------------------------------------------------
public int numberOfParents ()
{
  return parents.size ();
}
//-----------------------------------------------------------------------------
public int numberOfContainers ()
{
  return containers.size ();
}
//-----------------------------------------------------------------------------
public int [] getParents ()
{
  int size = numberOfParents ();
  int [] result = new int [size];
  for (int i=0; i < size; i++) {
    Integer tmp = (Integer) parents.elementAt (i);
    result [i] = tmp.intValue ();
    }

   return result;

} // getParents
//-----------------------------------------------------------------------------
public int [] getContainers ()
{
  int size = numberOfContainers ();
  int [] result = new int [size];
  for (int i=0; i < size; i++) {
    Integer tmp = (Integer) containers.elementAt (i);
    result [i] = tmp.intValue ();
    }

   return result;

} // getContainers
//-----------------------------------------------------------------------------
public int [] getParentsAndContainers ()
{
  int size = numberOfParents () + numberOfContainers ();

  int [] result = new int [size];
  int [] parents = getParents ();
  int [] containers = getContainers ();

  for (int i=0; i < parents.length; i++)
   result [i]= parents [i];  

  for (int i=0; i < containers.length; i++)
    result [i + parents.length] = containers [i];

  return result;

} // getParentsAndContainers
//-----------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("name: ");
  sb.append (name);
  sb.append ("\n");

  sb.append ("id: ");
  sb.append (id);
  sb.append ("\n");

  int [] parents = getParents ();

  sb.append ("parents: ");
  for (int i=0; i < parents.length; i++) {
    sb.append (parents [i]);
    sb.append (" ");
    }
  sb.append ("\n");

  int [] containers = getContainers ();

  sb.append ("containers: ");
  for (int i=0; i < containers.length; i++) {
    sb.append (containers [i]);
    sb.append (" ");
    }
  sb.append ("\n");
  
  return sb.toString ();

} // toString
//-----------------------------------------------------------------------------
} // OntologyTerm


