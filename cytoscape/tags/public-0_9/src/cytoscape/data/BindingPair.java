// BindingPair.java
//-----------------------------------------------------------------------------
// $Revision$  $Date$
//------------------------------------------------------------------------------
package cytoscape.data;
//-----------------------------------------------------------------------------
import java.io.*;
//-----------------------------------------------------------------------------
public class BindingPair implements Serializable {
  String a, b, species;
//-----------------------------------------------------------------------------
public BindingPair (String a, String b, String species)
{
  this.a = a;
  this.b = b;
  this.species = species;

}
//-----------------------------------------------------------------------------
public String getA ()
{
  return a;
}
//-----------------------------------------------------------------------------
public String getB ()
{
  return b;
}
//-----------------------------------------------------------------------------
public String getSpecies ()
{
  return species;
}
//-----------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append (a);
  sb.append (" -> ");
  sb.append (b);
  
  return sb.toString ();

} // toString
//-----------------------------------------------------------------------------
public String toXml ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("<bindingPair>\n");
  sb.append ("  <a> " + a + " </a>\n");
  sb.append ("  <b> " + b + " </b>\n");
  sb.append ("  <species> " + species + " </species>\n");
  sb.append ("</bindingPair>\n");
  
  return sb.toString ();

} // toXml
//-----------------------------------------------------------------------------
} // BindingPair
