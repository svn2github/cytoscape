// GoTerm.java
//-----------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data;
//-----------------------------------------------------------------------------
import java.io.*;
import java.util.Vector;
//-----------------------------------------------------------------------------
public class GoTerm implements Serializable {
  String name;
  int id;
  Vector parents;
  Vector containers;
//-----------------------------------------------------------------------------
public GoTerm (String name, int id)
{
  this.name = name;
  this.id = id;
  parents = new Vector ();
  containers = new Vector ();

}
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
} // GoTerm
