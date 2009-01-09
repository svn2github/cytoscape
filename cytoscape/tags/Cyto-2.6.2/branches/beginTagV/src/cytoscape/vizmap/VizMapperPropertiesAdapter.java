// VizMapperPropertiesAdapter.java: 
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
public class VizMapperPropertiesAdapter {
  Properties props;
//---------------------------------------------------------------------------------------
public VizMapperPropertiesAdapter (Properties props)
{
  this.props = props;

} // ctor
//---------------------------------------------------------------------------------------
protected void parseColorMapControlledByDiscreteValues (Integer attribute, String baseKey, 
                                                        String controller,
                                                        Properties props, VizMapper vizMapper)
{
  vizMapper.setAttributeController (attribute, controller, "string".getClass(), VizMapper.DISCRETE);

  String colorSpecifyingKey = baseKey + "." + controller + ".map";

  Enumeration eProps = props.propertyNames ();
  while (eProps.hasMoreElements()) {
    String key = (String) eProps.nextElement ();
    if (key.startsWith (colorSpecifyingKey)) {
      String value = props.getProperty (key);
      String interactionType = key.substring (colorSpecifyingKey.length () + 1);
      vizMapper.setDiscreteAttribute (attribute, interactionType, 
                                      Misc.parseRGBText (value));
      } // if
    } // while

} // parseColorMapControlledByDiscreteValues
//---------------------------------------------------------------------------------------
protected void parseColorMapControlledByContinuousValues (Integer attribute, String baseKey, 
                                                          String controller,
                                                          Properties props, VizMapper vizMapper)
{
  //System.out.println ("VizMapper PA, parse continuous");
  //System.out.println (" -- attribute: " + attribute);
  //System.out.println (" -- baseKey: " + baseKey);
  //System.out.println (" -- controller: " + controller);
  //System.out.println (" -- props: " + props);
  //System.out.println (" -- vizMapper: " + vizMapper);

  vizMapper.setAttributeController (attribute, controller, "string".getClass(), VizMapper.CONTINUOUS);

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
    System.err.println ("  VizMapper properties warning: no 'min.value' for " + baseKey);
    missingProperty = true;
    }

  if (minColorString == null) {
    System.err.println ("  VizMapper properties warning: no 'min.color' for " + baseKey);
    missingProperty = true;
    }

  if (maxValueString == null) {
    System.err.println ("  VizMapper properties warning: no 'max.value' for " + baseKey);
    missingProperty = true;
    }

  if (maxColorString == null) {
    System.err.println ("  VizMapper properties warning: no 'max.color' for " + baseKey);
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


  vizMapper.setContinuousAttributeControls (attribute, minValue, minColor, 
                                                              maxValue, maxColor);
  //System.out.println (" -- leaving parse continusous, vizMapper: " + vizMapper);



} // parseColorMapControlledByContinuousValues
//---------------------------------------------------------------------------------------
} // abstract class VizMapperPropertiesAdapter
