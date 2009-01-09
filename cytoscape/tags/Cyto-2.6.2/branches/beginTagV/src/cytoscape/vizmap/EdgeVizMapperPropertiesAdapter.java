// EdgeVizMapperPropertiesAdapter.java: 
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.vizmap;
//---------------------------------------------------------------------------------------
import java.awt.Color;
import java.util.*;
import cytoscape.util.Misc;
//---------------------------------------------------------------------------------------
public class EdgeVizMapperPropertiesAdapter extends VizMapperPropertiesAdapter {
  EdgeVizMapper edgeVizMapper;
//---------------------------------------------------------------------------------------
public EdgeVizMapperPropertiesAdapter (Properties props)
{
  super (props);
  this.edgeVizMapper = new EdgeVizMapper ();

} // ctor
//---------------------------------------------------------------------------------------
public EdgeVizMapper createEdgeVizMapper ()
{
  parseColorMapFromProperties ();
  parseSourceDecorationMapFromProperties ();
  parseTargetDecorationMapFromProperties ();

  return edgeVizMapper;

} // createEdgeVizMapper
//---------------------------------------------------------------------------------------
/**
 * parse edge color assignments from properties.  a properties file that 
 * reads like this:
 *
 *   edge.color.controller=interaction
 *   edge.color.controller.type=(discrete|continuous)
 *   edge.color.interaction.map.pp=0,0,255
 *   edge.color.interaction.map.pd=255,255,0
 *
 * specifies that
 *   edge color is controlled by the edge attribute called 'interaction'
 *   interaction is a discrete variable (which usually means: a set of strings)
 *   when the 'interaction' attribute is 'pp', the edge is blue
 *   when it is 'pd', the edge is yellow
 *
 *   edge.color.controller=homology
 *   edge.color.homology.type=continuous
 *   edge.color.homology.min.value=0.0
 *   edge.color.homology.min.color=0,0,0
 *   edge.color.homology.max.value=100.0
 *   edge.color.homology.max.color=255,255,255
 */
protected void parseColorMapFromProperties ()
{
  String baseKey = "edge.color";
  String controllerKey = baseKey + ".controller";

  if (props.containsKey (controllerKey)) {
    String controller = props.getProperty (controllerKey);
    String typeKey = baseKey + "." + controller + ".type";
    String type = props.getProperty (typeKey);
    if (type == null) {
      System.err.println (" error in EdgeVizMapperPropertiesAdapter.parseColorMapFromProperties");
      System.err.println ("    no property matching: " + typeKey);
      return;
      }
    if (type.equals ("discrete")) 
      parseColorMapControlledByDiscreteValues (EdgeVizMapper.COLOR, baseKey, controller, 
                                               props, edgeVizMapper);
    else
      parseColorMapControlledByContinuousValues (EdgeVizMapper.COLOR, baseKey, controller, 
                                                 props, edgeVizMapper);
    } // if edge.color.controller is present

} // parseColorMapFromProperties
//---------------------------------------------------------------------------------------
/*******************************************
protected void parseColorMapControlledByDiscreteValues (String baseKey, String controller,
                                                        Properties props, EdgeVizMapper edgeVizMapper)
{
  edgeVizMapper.setAttributeController (EdgeVizMapper.COLOR, controller, "string".getClass(),
                                  EdgeVizMapper.DISCRETE);

  String colorSpecifyingKey = baseKey + "." + controller + ".map";

  Enumeration eProps = props.propertyNames ();
  while (eProps.hasMoreElements()) {
    String key = (String) eProps.nextElement ();
    if (key.startsWith (colorSpecifyingKey)) {
      String value = props.getProperty (key);
      String interactionType = key.substring (colorSpecifyingKey.length () + 1);
      edgeVizMapper.setDiscreteAttribute (EdgeVizMapper.COLOR, interactionType, Misc.parseRGBText (value));
      } // if
    } // while

} // parseColorMapControlledByDiscreteValues
//---------------------------------------------------------------------------------------
protected void parseColorMapControlledByContinuousValues (String baseKey, String controller,
                                                          Properties props, EdgeVizMapper edgeVizMapper)
{
  edgeVizMapper.setAttributeController (EdgeVizMapper.COLOR, controller, "string".getClass(),
                                  EdgeVizMapper.CONTINUOUS);

  String minKey = baseKey + "." + controller + ".min";

  Double minValue = new Double (0.0);
  Color minColor = new Color (0,0,0);
  Double maxValue = new Double (0.0);
  Color maxColor = new Color (0,0,0);

  String minValueKey = baseKey + "." + controller + ".min.value";
  String minValueString = props.getProperty (minValueKey);

  String minColorKey = baseKey + "." + controller + ".min.color";
  String minColorString = props.getProperty (minColorKey);

  String maxValueKey = baseKey + "." + controller + ".max.value";
  String maxValueString = props.getProperty (maxValueKey);

  String maxColorKey = baseKey + "." + controller + ".max.color";
  String maxColorString = props.getProperty (maxColorKey);

  boolean missingProperty = false;
  if (minValueString == null) {
    System.err.println ("  NodeViz properties warning: no 'min.value' for " + baseKey);
    missingProperty = true;
    }

  if (minColorString == null) {
    System.err.println ("  NodeViz properties warning: no 'min.color' for " + baseKey);
    missingProperty = true;
    }

  if (maxValueString == null) {
    System.err.println ("  NodeViz properties warning: no 'max.value' for " + baseKey);
    missingProperty = true;
    }

  if (maxColorString == null) {
    System.err.println ("  NodeViz properties warning: no 'max.color' for " + baseKey);
    missingProperty = true;
    }

  if (missingProperty) 
    return;

  try {
    minValue = new Double (minValueString);
    }
  catch (NumberFormatException nfe) {
    System.err.println (" illegal double precision number in " + 
                        minValueKey + ": " + minValueString);
    }
  minColor = Misc.parseRGBText (minColorString);

  try {
    maxValue = new Double (maxValueString);
    }
  catch (NumberFormatException nfe) {
    System.err.println (" illegal double precision number in " + 
                        maxValueKey + ": " + maxValueString);
    }
  maxColor = Misc.parseRGBText (maxColorString);


  edgeVizMapper.setContinuousAttributeControls (EdgeVizMapper.COLOR, minValue, minColor, 
                                                         maxValue, maxColor);

} // parseColorMapControlledByDiscreteValues
*******************************************/
//---------------------------------------------------------------------------------------
protected void parseSourceDecorationMapFromProperties ()
{
  String baseKey = "edge.sourceDecoration";
  String controllerKey = baseKey + ".controller";

  if (props.containsKey (controllerKey)) {
    String controller = props.getProperty (controllerKey);
    edgeVizMapper.setAttributeController (EdgeVizMapper.SOURCE_DECORATION, controller, 
                                    "string".getClass(), EdgeVizMapper.DISCRETE);

    String decorationSpecifyingKey = baseKey + "." + controller + ".map";

    Enumeration eProps = props.propertyNames ();
    while (eProps.hasMoreElements()) {
      String key = (String) eProps.nextElement();
      if (key.startsWith (decorationSpecifyingKey)) {
        String value = props.getProperty (key);
        String interactionType = key.substring (decorationSpecifyingKey.length () + 1);
        edgeVizMapper.setDiscreteAttribute (EdgeVizMapper.SOURCE_DECORATION, interactionType, value);
        } // if
      } // while
   } //  if

} // parseSourceDecorationMapFromProperties
//---------------------------------------------------------------------------------------
protected void parseTargetDecorationMapFromProperties ()
{
  String baseKey = "edge.targetDecoration";
  String controllerKey = baseKey + ".controller";

  if (props.containsKey (controllerKey)) {
    String controller = props.getProperty (controllerKey);
    edgeVizMapper.setAttributeController (EdgeVizMapper.TARGET_DECORATION, controller, 
                                    "string".getClass(), EdgeVizMapper.DISCRETE);

    String decorationSpecifyingKey = baseKey + "." + controller + ".map";

    Enumeration eProps = props.propertyNames ();
    while (eProps.hasMoreElements()) {
      String key = (String) eProps.nextElement();
      if (key.startsWith (decorationSpecifyingKey)) {
        String value = props.getProperty (key);
        String interactionType = key.substring (decorationSpecifyingKey.length () + 1);
        edgeVizMapper.setDiscreteAttribute (EdgeVizMapper.TARGET_DECORATION, interactionType, value);
        } // if
      } // while
   } //  if

} // parseTargetDecorationMapFromProperties
//---------------------------------------------------------------------------------------
} // class EdgeVizMapperPropertiesAdapter
