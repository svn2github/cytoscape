// DomainBoundary: describes the nature of data associated with a node or edge
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.vizmap;
//---------------------------------------------------------------------------------------
/**
 * an attribute controlled by a continuous variable (i.e., edge color controlled by
 * homology scores of the two connected nodes) uses this class to store the values
 * associated with the various subranges of that continuous variable. A simple
 * use of this class is for line color:  one DomainBoundary will be placed at the miniumum
 * value in the domain (say 0.0), with a corresponding color of red; a second DomainBoundary
 * is placed at the maximum value (say 100.0), with a color of blue.  Intermediate values
 * may be a linear (or other) function of the two endpoint values.  
 *
 * <p><b>todo <i>(pshannon, 2002/03/12)</i></b>: for full control, many Domain Boundaries
 * may be specified for a continuously varying attribute, along with functions that
 * say how to interpolate for intermediate values.  for example, and continuing with the
 * example above, the user may wish to assign a different color for each subrange of
 * length 10:  0-10: black   10-20: green  30-40: blue ..., interpolating with color
 * intensity across each subrange, or simply using a constant intensity and color across
 * each subrange.  this class, and its use, will have to get more sophisticated to support
 * this.
 *
 */
//--------------------------------------------------------------------------------------
class DomainBoundary {

  Double number;
  Object object;

//--------------------------------------------------------------------------------------
DomainBoundary (Double number, Object object) 
{
  this.number = number;
  this.object = object;
}
//--------------------------------------------------------------------------------------
public String toString () 
{
  return number.toString () + " -> " + object;
}
//--------------------------------------------------------------------------------------
} // class DomainBoundary
