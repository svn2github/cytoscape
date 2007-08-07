// NodeVizMapperPropertiesAdapter.java: 
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
public class NodeVizMapperPropertiesAdapter extends VizMapperPropertiesAdapter {
  NodeVizMapper nodeVizMapper;
//---------------------------------------------------------------------------------------
public NodeVizMapperPropertiesAdapter (Properties props)
{
  super (props);
  this.nodeVizMapper = new NodeVizMapper ();

} // ctor
//---------------------------------------------------------------------------------------
public NodeVizMapper createNodeVizMapper ()
{
  parseColorFillMapFromProperties ();
  // parseSizeMapFromProperties ();
  return nodeVizMapper;

} // createNodeVizMapper
//---------------------------------------------------------------------------------------
/**
 * parse node color assignments from properties.  a properties file that 
 * reads like this:
 *
 *   node.fillcolor.controller=interaction
 *   node.fillcolor.controller.type=(discrete|continuous)
 *   node.fillcolor.interaction.map.pp=0,0,255
 *   node.fillcolor.interaction.map.pd=255,255,0
 *
 *  or
 *
 *   node.fillcolor.controller=expression
 *   node.fillcolor.expression.type=continuous
 *   node.fillcolor.expression.min.value=0.0
 *   node.fillcolor.expression.min.color=0,255,0
 *   node.fillcolor.expression.max.value=100.0
 *   node.fillcolor.expression.max.color=255,0,0
 *   
 *
 * the first example specifies that
 *
 *   node fill color is controlled by the node attribute called 'interaction'
 *   interaction is a discrete variable (which usually means: a set of strings)
 *   when the 'interaction' attribute is 'pp', the node is blue
 *   when it is 'pd', the node is yellow
 *
 * the second specifies that
 *
 *   expression controls fill color
 *   expression is a continuous variable
 *   the minimum value is 0.0, to be portrayed in green
 *   the maximum value is 100.0, to be portrayed in red
 */
protected void parseColorFillMapFromProperties ()
{
  String baseKey = "node.fillcolor";
  String controllerKey = baseKey + ".controller";

  if (props.containsKey (controllerKey)) {
    String controller = props.getProperty (controllerKey);
    String typeKey = baseKey + "." + controller + ".type";
    String type = props.getProperty (typeKey);
    if (type == null) {
      System.err.println (" error in EdgeVizPropertiesAdapter.parseColorMapFromProperties");
      System.err.println ("    no property matching: " + typeKey);
      return;
      }
    if (type.equals ("discrete")) 
      parseColorMapControlledByDiscreteValues (NodeVizMapper.FILL_COLOR, baseKey, controller, 
                                               props, nodeVizMapper);
    else if (type.equals ("continuous"))
      parseColorMapControlledByContinuousValues (NodeVizMapper.FILL_COLOR, baseKey, controller, 
                                                props, nodeVizMapper);
    else {
      System.err.println (" error in EdgeVizPropertiesAdapter.parseColorMapFromProperties");
      System.err.println ("    " + typeKey + " value must be 'discrete' or 'continuous'");
      return;
      }
    } // if node.fillcolor .controller is present

} // parseColorFillMapFromProperties
//---------------------------------------------------------------------------------------
/***********************************
protected void parseNodeSizeMapFromProperties ()
{
  String baseKey = "node.size";
  String controllerKey = baseKey + ".controller";

  if (props.containsKey (controllerKey)) {
    String controller = props.getProperty (controllerKey);
    String typeKey = baseKey + "." + controller + ".type";
    String type = props.getProperty (typeKey);
    if (type == null) {
      System.err.println (" error in EdgeVizPropertiesAdapter.parseNodeSizeMapFromProperties");
      System.err.println ("    no property matching: " + typeKey);
      return;
      }
    if (type.equals ("discrete")) 
      parseSizeMapControlledByDiscreteValues (NodeVizMapper.SIZE, baseKey, controller, 
                                               props, nodeVizMapper);
    else if (type.equals ("continuous"))
      parseSizeMapControlledByContinuousValues (NodeVizMapper.SIZE, baseKey, controller, 
                                                props, nodeVizMapper);
    else {
      System.err.println (" error in EdgeVizPropertiesAdapter.parseSizeMapFromProperties");
      System.err.println ("    " + typeKey + " value must be 'discrete' or 'continuous'");
      return;
      }
    } // if node.size.controller is present

} // parseNodeSizeMapFromProperties
//---------------------------------------------------------------------------------------
***********************************/
} // class NodeVizMapperPropertiesAdapter
