// NodeVizMapper.java: data associated with edges controls their visual attributes
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.vizmap;
//---------------------------------------------------------------------------------------
import java.awt.Color;
import java.util.*;
//---------------------------------------------------------------------------------------
public class NodeVizMapper extends VizMapper {

  public static final Integer FILL_COLOR = new Integer (1);
  public static final Integer BORDER_COLOR = new Integer (2);
  public static final Integer SIZE = new Integer (3);
  public static final Integer SHAPE = new Integer (4);

  protected Integer [] allAttributes = {FILL_COLOR,
                                        BORDER_COLOR,
                                        SIZE,
                                        SHAPE};
    
  protected Color defaultFillColor = new Color (204, 255, 255);
  protected int defaultSize = 100;
  protected Color defaultBorderColor = new Color (0, 0, 0);
  protected String defaultShape = "rectangle";

  HashMap colorFillMap = new HashMap ();

//---------------------------------------------------------------------------------------
public NodeVizMapper ()
{
  super ();
 
} // ctor
//---------------------------------------------------------------------------------------
public void setDiscreteAttribute (Integer attribute, String key, Object value)
{
  if (attribute.equals (FILL_COLOR))
    colorFillMap.put (key, value);
   
}
//--------------------------------------------------------------------------------------
public void setContinuousAttributeControls (Integer attribute,
                                            Double min, Object minObject,
                                            Double max, Object maxObject)
{
  if (attribute.equals (FILL_COLOR)) {
    colorFillMap.put ("min", new DomainBoundary (min, minObject));
    colorFillMap.put ("max", new DomainBoundary (max, maxObject));
    }
  else {
    System.err.println ("NodeVizMapper.setContinuousAttributeControls error:  no support yet for ");
    System.err.println ("the control of node attributes by continuous variables, except ");
    System.err.println ("for node fill color");
    }

} // setContinuousAttributeControls
//--------------------------------------------------------------------------------------
/****************************8
public int getSize (HashMap nodeBundle)
{
  String controllingAttribute = getAttributeController (NodeVizMapper.SIZE);

  if (controllingAttribute == null)
    return defaultSize;

  if (nodeBundle == null || nodeBundle.size () == 0)
    return defaultSize;

  if (!nodeBundle.containsKey (controllingAttribute))
    return defaultSize;

  String [] attributeNames = (String []) nodeBundle.keySet().toArray (new String [0]);

  for (int i=0; i < attributeNames.length; i++) {
    String attribute = attributeNames [i];
    if (controllingAttribute.equals (attribute))
      Object dataValue = nodeBundle.get (attribute);
      DomainInfo domainInfo = (DomainInfo) attributeControllers.get (attribute);
      System.out.println ("get node size, domainInfo: " + domainInfo);
      } // if
    } // for i

  return defaultSize;

} // getSize
****************************/
//--------------------------------------------------------------------------------------
public Color getNodeFillColor (HashMap nodeBundle)
{
  String controllingAttribute = getAttributeController (NodeVizMapper.FILL_COLOR);

  if (controllingAttribute == null)
    return defaultFillColor;

  if (nodeBundle == null || nodeBundle.size () == 0)
    return defaultFillColor;

  if (!nodeBundle.containsKey (controllingAttribute))
    return defaultFillColor;

  String [] attributeNames = (String []) nodeBundle.keySet().toArray (new String [0]);

  for (int i=0; i < attributeNames.length; i++) {
    String attribute = attributeNames [i];
    if (controllingAttribute.equals (attribute))
      return calculateColor (nodeBundle.get (attribute), NodeVizMapper.FILL_COLOR, 
                             colorFillMap, defaultFillColor);
    } // for i

  return defaultFillColor;

} // getNodeColor
//--------------------------------------------------------------------------------------
public Color getDefaultNodeFillColor ()
{
  return defaultFillColor;
}
//--------------------------------------------------------------------------------------
/** provide a readable version of the enumerated types provided by this class
 */
public String getName (Integer enumeratedValue)
{
  int value = enumeratedValue.intValue ();
  switch (value) {
    case 1:
      return "FILL_COLOR";
    case 2:
      return "BORDER_COLOR";
    case 3:
      return "SIZE";
    case 4:
      return "SHAPE";
    } // switch on value

  return "UNKNOWN_VALUE";

} // getName (Integer)
//--------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("---------------------------- NodeVizMapper\n\n");

  sb.append ("      defaults\n\n");
  sb.append ("         node color: " + defaultFillColor);
  sb.append ("\n");
  sb.append ("\n\n\n");

  Integer [] keys = (Integer []) attributeControllers.keySet().toArray (new Integer [0]);
  sb.append ("      controllers\n\n");
  sb.append ("node attribute       controlled by           of class\n");
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
} // class NodeVizMapper
