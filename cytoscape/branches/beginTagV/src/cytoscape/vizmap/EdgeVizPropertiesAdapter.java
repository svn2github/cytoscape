// EdgeVizPropertiesAdapter.java: 
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
public class EdgeVizPropertiesAdapter {
  EdgeViz edgeViz;
  Properties props;
//---------------------------------------------------------------------------------------
public EdgeVizPropertiesAdapter (Properties props)
{
  this.edgeViz = new EdgeViz ();
  this.props = props;

} // ctor
//---------------------------------------------------------------------------------------
public EdgeViz createEdgeViz ()
{
  parseColorMapFromProperties ();
  parseSourceDecorationMapFromProperties ();
  parseTargetDecorationMapFromProperties ();

  return edgeViz;

} // createEdgeViz
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
 */
protected void parseColorMapFromProperties ()
{
  String baseKey = "edge.color";
  String controllerKey = baseKey + ".controller";

  if (props.containsKey (controllerKey)) {
    String controller = props.getProperty (controllerKey);
    edgeViz.setAttributeController (EdgeViz.COLOR, controller, "string".getClass(), EdgeViz.DISCRETE);

    String colorSpecifyingKey = baseKey + "." + controller + ".map";

    Enumeration eProps = props.propertyNames ();
    while (eProps.hasMoreElements()) {
      String key = (String) eProps.nextElement ();
      if (key.startsWith (colorSpecifyingKey)) {
        String value = props.getProperty (key);
        String interactionType = key.substring (colorSpecifyingKey.length () + 1);
        edgeViz.setDiscreteAttribute (EdgeViz.COLOR, interactionType, Misc.parseRGBText (value));
        } // if
      } // while
   } //  if


} // parseColorMapFromProperties
//---------------------------------------------------------------------------------------
protected void parseSourceDecorationMapFromProperties ()
{
  String baseKey = "edge.sourceDecoration";
  String controllerKey = baseKey + ".controller";

  if (props.containsKey (controllerKey)) {
    String controller = props.getProperty (controllerKey);
    edgeViz.setAttributeController (EdgeViz.SOURCE_DECORATION, controller, 
                                    "string".getClass(), EdgeViz.DISCRETE);

    String decorationSpecifyingKey = baseKey + "." + controller + ".map";

    Enumeration eProps = props.propertyNames ();
    while (eProps.hasMoreElements()) {
      String key = (String) eProps.nextElement();
      if (key.startsWith (decorationSpecifyingKey)) {
        String value = props.getProperty (key);
        String interactionType = key.substring (decorationSpecifyingKey.length () + 1);
        edgeViz.setDiscreteAttribute (EdgeViz.SOURCE_DECORATION, interactionType, value);
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
    edgeViz.setAttributeController (EdgeViz.TARGET_DECORATION, controller, 
                                    "string".getClass(), EdgeViz.DISCRETE);

    String decorationSpecifyingKey = baseKey + "." + controller + ".map";

    Enumeration eProps = props.propertyNames ();
    while (eProps.hasMoreElements()) {
      String key = (String) eProps.nextElement();
      if (key.startsWith (decorationSpecifyingKey)) {
        String value = props.getProperty (key);
        String interactionType = key.substring (decorationSpecifyingKey.length () + 1);
        edgeViz.setDiscreteAttribute (EdgeViz.TARGET_DECORATION, interactionType, value);
        } // if
      } // while
   } //  if

} // parseTargetDecorationMapFromProperties
//---------------------------------------------------------------------------------------
} // class EdgeVizPropertiesAdapter
