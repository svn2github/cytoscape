// DomainInfo: describes the nature of data associated with a node or edge
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.vizmap;
//---------------------------------------------------------------------------------------
/**
 * describes the nature of data associated with a node or an edge:
 * it's name, class, and category  (either continuous or discrete).
 *
 * every attribute maps from a domain value to a range value.  domain values include
 * edge attribuetes like "interaction type" or "homology score".  range values include
 * visual attributes like "line color" or "line thickness" or "targetDecoration" -- an
 * arrow on an edge indicating the direction of the edge.
 * 
 * domain values may be either continuous or discrete:  interaction type is typically
 * a discrete string (such as "pp" -- protein-protein, or "pd" -- protein-DNA).
 * homology between two genes or proteins is typically a continuously varying floating point 
 * number.
 *
 * EdgeViz provides the mapping from these 'domain' values to range values:
 * visual attributes like color, line width, and line style.  
 *
 * this inner class, DomainInfo, describes the nature of the domain data.  
 * RangeInfo is implicit in the visual data types:  edge color, edge thickness, edge 
 * line style, ...
 */
class DomainInfo {

  String controller;
  Class dataClass;
  boolean continuousData;

//---------------------------------------------------------------------------------------
DomainInfo (String controller, Class dataClass, boolean continuousData) 
{
  this.controller = controller;
  this.dataClass = dataClass;
  this.continuousData = continuousData;
}
//---------------------------------------------------------------------------------------
public String toString () 
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("controller: " + controller + "\n");
  sb.append ("dataClass:  " + dataClass.getName () + "\n");
  sb.append ("data category: " + (continuousData? "continuous" : "discrete") + "\n");
  return sb.toString ();
}
//---------------------------------------------------------------------------------------
} // class DomainInfo
