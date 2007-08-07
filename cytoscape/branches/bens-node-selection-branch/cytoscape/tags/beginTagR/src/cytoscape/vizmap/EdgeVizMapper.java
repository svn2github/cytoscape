// EdgeVizMapper.java: data associated with edges controls their visual attributes
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.vizmap;
//---------------------------------------------------------------------------------------
import java.awt.Color;
import java.util.*;
import cytoscape.GraphObjAttributes;
//---------------------------------------------------------------------------------------
/**
 *
 * Control the visual attributes of a graph edge by the various data attributes which
 * may be associated with it. The visual attributes of a graph edge are:
 * <p>
 * <ol>
 *   <li> line color
 *   <li> line style [dashed, solid]
 *   <li> line thickness [1-?]
 *   <li> source decoration [none, arrow]
 *   <li> target decoration [none, arrow]
 * </ol>
 * <p>
 * The data types which may be associated with an edge are virtually unlimited, but
 * for now (2002/02/28, pshannon) we restrict them to double precision floating point
 * numbers,  and enumerated sets of strings.  For example:
 * <p>
 * <ol>
 *   <li> numerical attribute: the evidence (expressed as a probablity between 0 and 1)
 *        for protein-protein binding between the two nodes which are joined by the edge
 *   <li> string attribute: the category of the relationship between the joined nodes, "pp"
 *        for a protein-protein binding; "pd" for a protein-DNA binding.
 * </ol>
 * <p>
 * Many possible attributes of each type (both numerical and string) may be 
 * present for each edge at any one time.  So a crucial responsibility of this
 * class is to allow <em>any</em> among those data attributes to control any 
 * visual attributes, at the whim of the user. For example,
 * <ul>
 *   <li> the interaction category controls line color (blue for "pp"; yellow for "pd")
 *   <li> the evidence probablity number controls line thickness
 *   <li> the interaction category also controls source and target decoration:
 *      <ul>
 *         <li> pd is inherently directional: draw an arrow at the target end of the edge
 *         <li> pp is non-directional:  use no decoration at either end
 *      </ul>
 * </ul>
 * <p>
 * Thus the first responsibility of this class is to allow the user to
 * assign which data attribute controls which visual attribute or attributes.
 * The second responsibility is to specify how, for each visual attribute, the
 * relevant data attribute values map onto the possible visual attribute values.
 * <p>
 * In addition, sensible defaults are provided for each of the visual attributes:
 * <ul>
 *   <li> line color:  black
 *   <li> line style:  solid
 *   <li> line thickness:  1
 *   <li> target &amp; source decoration: none
 * </ul>
 */
public class EdgeVizMapper extends VizMapper {

  public static final Integer COLOR = new Integer (1);
  public static final Integer LINE_STYLE = new Integer (2);
  public static final Integer THICKNESS = new Integer (3);
  public static final Integer SOURCE_DECORATION = new Integer (4);
  public static final Integer TARGET_DECORATION = new Integer (5);

  protected Integer [] allAttributes = {COLOR,
                                        LINE_STYLE,
                                        THICKNESS,
                                        SOURCE_DECORATION,
                                        TARGET_DECORATION};

  Color defaultEdgeColor = Color.black;
  String defaultLineStyle = "solid";
  int defaultLineThickness = 1;
  String defaultSourceDecoration = "none";
  String defaultTargetDecoration = "none";

    // these are the lookup tables in which key-value pairs are stored.
    // they are used in two ways:  for discrete data, in the colorMap (by example)
    //
    //    pp -> blue
    //    pd -> red
    // 
    // for continuous data, these maps also store key-value pairs, but the
    // keys are boundary tokens (like 'min' or 'max') and the value
    // 'DomainBoundary' objects, used like this:
    //
    //    min -> [0.0, Color (0,0,0)]
    //    max -> [100.0, Color (255, 0, 0)]
    //
    // todo (pshannon, 2002/03/12): this scheme must soon evolve to handle
    //      more complicated semantics, like this:
    //
    //    range -> [0.0, red]          (all values between 0-10 are red)
    //    range -> [10.0, blue]        (                  10-20 are blue)
    //    range -> [20.0, green]       (                  >= 20 are green
    //
    // and
    //
    //    spectrum -> [-2.0, green] (shades of green,  dark-to-light, from -2.0 to 0)
    //    spectrum -> [0.0,    red] (shades of red, dark-to-light, for all values >0)
    // 
    
  HashMap colorMap = new HashMap ();
  HashMap sourceDecorationMap = new HashMap ();
  HashMap targetDecorationMap = new HashMap ();

//---------------------------------------------------------------------------------------
public EdgeVizMapper ()
{
  super ();
 
} // ctor
//---------------------------------------------------------------------------------------
public void setDiscreteAttribute (Integer attribute, String key, Object value)
{
  if (attribute.equals (COLOR))
    colorMap.put (key, value);
  else if (attribute.equals (SOURCE_DECORATION))
    sourceDecorationMap.put (key, value);
  else if (attribute.equals (TARGET_DECORATION))
    targetDecorationMap.put (key, value);
   
}
//--------------------------------------------------------------------------------------
public void setContinuousAttributeControls (Integer attribute,
                                            Double min, Object minObject,
                                            Double max, Object maxObject)
{
  if (attribute.equals (COLOR)) {
    colorMap.put ("min", new DomainBoundary (min, minObject));
    colorMap.put ("max", new DomainBoundary (max, maxObject));
    }
  else {
    System.err.println ("EdgeVizMapper.setContinuousAttributeControls error:  no support yet for ");
    System.err.println ("the control of edge attributes by continuous variables, except ");
    System.err.println ("for edge line color");
    }

} // setContinuousAttributeControls
//--------------------------------------------------------------------------------------
public Color getEdgeColor (HashMap edgeBundle)
{
  String controllingAttribute = getAttributeController (EdgeVizMapper.COLOR);

  if (controllingAttribute == null)
    return defaultEdgeColor;

  if (edgeBundle == null || edgeBundle.size () == 0)
    return defaultEdgeColor;

  if (!edgeBundle.containsKey (controllingAttribute))
    return defaultEdgeColor;

  String [] attributeNames = (String []) edgeBundle.keySet().toArray (new String [0]);

  for (int i=0; i < attributeNames.length; i++) {
    String attribute = attributeNames [i];
    if (controllingAttribute.equals (attribute)) {
      //System.out.println (" -- about to call calculateColor");
      return calculateColor (edgeBundle.get (attribute), EdgeVizMapper.COLOR, colorMap, 
                             defaultEdgeColor);
      }
    } // for i

  return defaultEdgeColor;

} // getEdgeColor
//--------------------------------------------------------------------------------------
public String getLineStyle (HashMap edgeBundle)
{
  return defaultLineStyle;
}
//--------------------------------------------------------------------------------------
public int getLineThickness (HashMap edgeBundle)
{
  return defaultLineThickness;
}
//--------------------------------------------------------------------------------------
public String getSourceDecoration (HashMap edgeBundle)
{
  DomainInfo domainInfo = (DomainInfo) attributeControllers.get (SOURCE_DECORATION);
  if (domainInfo == null)
    return defaultSourceDecoration;
 
  String controllingAttribute = domainInfo.controller;

  if (controllingAttribute == null)
    return defaultSourceDecoration;

  if (edgeBundle == null || edgeBundle.size () == 0)
    return defaultSourceDecoration;;

  String key = (String) edgeBundle.get (controllingAttribute);
  if (key == null)
    return defaultSourceDecoration;

  if (domainInfo.continuousData) {
    System.err.print   ("EdgeVizMapper.getSourceDecoration error:  no support yet for ");
    System.err.println ("the control of edge sourceDecoration by continuous variables");
    return defaultSourceDecoration;
    }

    // having gotten this far, we can now do a simple table lookup on the value in
    // <edgeBundle> for <controllingAttribute>

  String decoration = (String) sourceDecorationMap.get (key);
  if (decoration == null)
    decoration = defaultSourceDecoration;

  return decoration;

} // getSourceDecoration
//--------------------------------------------------------------------------------------
/**
 * 
 */
public String getTargetDecoration (HashMap edgeBundle)
{
  DomainInfo domainInfo = (DomainInfo) attributeControllers.get (TARGET_DECORATION);
  if (domainInfo == null)
    return defaultTargetDecoration;

  String controllingAttribute = domainInfo.controller;

  if (controllingAttribute == null)
    return defaultTargetDecoration;

  if (edgeBundle == null || edgeBundle.size () == 0)
    return defaultTargetDecoration;;

  String key = (String) edgeBundle.get (controllingAttribute);
  if (key == null)
    return defaultTargetDecoration;

  if (domainInfo.continuousData) {
    System.err.print   ("EdgeVizMapper.getTargetDecoration error:  no support yet for ");
    System.err.println ("the control of edge targetDecoration by continuous variables");
    return defaultTargetDecoration;
    }

    // having gotten this far, we can now do a simple table lookup on the value in
    // <edgeBundle> for <controllingAttribute>

  String decoration = (String) targetDecorationMap.get (key);
  if (decoration == null)
    decoration = defaultSourceDecoration;

  return decoration;

} // getTargetDecoration
//--------------------------------------------------------------------------------------
/** what is the java class of the data which controlls the specified attribute?
 * this will usually be either java.lang.String or java.lang.Double, but
 * more interesting classes may also appear
 */
public Class getDataClassOfAttribute (Integer attribute)
{
  DomainInfo domainInfo = (DomainInfo) attributeControllers.get (attribute);
  if (domainInfo != null) 
    return domainInfo.dataClass;
  else
    return null;

} // getDataClassOfAttribute
//--------------------------------------------------------------------------------------
public Color getDefaultEdgeColor ()
{
  return defaultEdgeColor;
}
//--------------------------------------------------------------------------------------
public String getDefaultLineStyle ()
{
  return defaultLineStyle;
}
//--------------------------------------------------------------------------------------
public int getDefaultLineThickness ()
{
  return defaultLineThickness;
}
//--------------------------------------------------------------------------------------
public String  getDefaultSourceDecoration ()
{
  return defaultSourceDecoration;
}
//--------------------------------------------------------------------------------------
public String  getDefaultTargetDecoration ()
{
  return defaultTargetDecoration;
}
//--------------------------------------------------------------------------------------
/** Are the data for the specified attribute CONTINUOUS or DISCRETE?
 */
public Integer getDataDomainType (Integer attribute)
{
  if (getDataClassOfAttribute (attribute) == "string".getClass ())
    return DISCRETE;
  else
    return CONTINUOUS;
}
//--------------------------------------------------------------------------------------
/** provide a readable version of the enumerated types provided by this class
 */
public String getName (Integer enumeratedValue)
{
  int value = enumeratedValue.intValue ();
  switch (value) {
    case 1:
      return "COLOR";
    case 2:
      return "LINE_STYLE";
    case 3:
      return "THICKNESS";
    case 4:
      return "SOURCE_DECORATION";
    case 5:
      return "TARGET_DECORATION";
    case 6:
      return "DISCRETE";
    case 7:
      return "CONTINUOUS";
    } // switch on value

  return "UNKNOWN_VALUE";

} // getName (Integer)
//--------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("---------------------------- EdgeVizMapper\n\n");

  sb.append ("      defaults\n\n");
  sb.append ("         edge color: " + defaultEdgeColor);
  sb.append ("\n");
  sb.append ("         line style: " + defaultLineStyle);
  sb.append ("\n");
  sb.append ("     line thickness: " + defaultLineThickness);
  sb.append ("\n");
  sb.append ("  source decoration: " + defaultSourceDecoration);
  sb.append ("\n");
  sb.append ("  target decoration: " + defaultTargetDecoration);
  sb.append ("\n\n\n");

  Integer [] keys = (Integer []) attributeControllers.keySet().toArray (new Integer [0]);
  sb.append ("      controllers\n\n");
  sb.append ("edge attribute       controlled by           of class\n");
  sb.append ("--------------       -------------      --------------------\n");

  for (int i=0; i < allAttributes.length; i++) {
    String attributeName = getName (allAttributes [i]);
    DomainInfo domainInfo = (DomainInfo) attributeControllers.get (allAttributes [i]);
    sb.append (attributeName);
    if (domainInfo != null) {
      String controller = domainInfo.controller;
      if (controller == null) controller = "null";
      Class classOfController = domainInfo.dataClass;
      int fill = 22 - attributeName.length ();
      for (int f=0; f < fill; f++) sb.append (" ");
      sb.append (controller);
      sb.append ("         ");
      if (classOfController != null)
        sb.append (classOfController.getName ());
      }
    sb.append ("\n");
    }

  return sb.toString ();

} // toString
//--------------------------------------------------------------------------------------
} // class EdgeVizMapper
