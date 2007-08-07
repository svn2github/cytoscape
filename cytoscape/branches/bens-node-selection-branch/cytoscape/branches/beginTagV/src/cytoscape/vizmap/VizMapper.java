// VizMapper.java: abstract base class
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
abstract class VizMapper {

  public static final Integer DISCRETE = new Integer (-1);
  public static final Integer CONTINUOUS = new Integer (-2);

   // for each visual attribute, we want to know the name of the data attribute
   // which controls it, and the java class of that data.  use two hash maps 
   // for this.  by example:
   // 
   //    attributeControllers.put (NodeViz.COLOR, "reliability");
   //    attributeClass.put (NodeViz.COLOR, java.lang.Double);

  protected HashMap attributeControllers = new HashMap ();

//---------------------------------------------------------------------------------------
public VizMapper ()
{
 
} // ctor
//---------------------------------------------------------------------------------------
/** set the controller for the named attribute, specify its datatype, and whether
 * it is continuous or discrete data; all previous information is retained.
 */
public void setAttributeController (Integer attribute, String controller, 
                                    Class dataClass, 
                                    Integer continuousOrDiscrete)
{
  DomainInfo domainInfo = new DomainInfo (controller, dataClass, 
                                          continuousOrDiscrete.equals (CONTINUOUS));
  attributeControllers.put (attribute, domainInfo);

} // setAttributeController
//--------------------------------------------------------------------------------------
public String getAttributeController (Integer attribute)
{
  DomainInfo domainInfo = (DomainInfo) attributeControllers.get (attribute);
  if (domainInfo == null)
    return null;
  else
    return domainInfo.controller;
}
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
protected Color calculateColor (Object dataValue, Integer attribute, HashMap colorMap,
                                Color defaultColor)
{
  //System.out.println (" -- VizMapper.calculateColor");
  //System.out.println (" -- dataValue: " + dataValue);
  //System.out.println (" -- attribute: " + attribute);
  //System.out.println (" -- colorMap size: " + colorMap.size ());
  //System.out.println (" -- defaultColor: " + defaultColor);

  if (dataValue == null)
    return defaultColor;

  Class classType = dataValue.getClass ();
  DomainInfo domainInfo = (DomainInfo) attributeControllers.get (attribute);

  Color result = defaultColor;

  if (domainInfo.continuousData) {
    //System.out.println (" -- continuous data");
    DomainBoundary min = (DomainBoundary) colorMap.get ("min");
    DomainBoundary max = (DomainBoundary) colorMap.get ("max");
    Color minColor = (Color) min.object;
    Color maxColor = (Color) max.object;
    double minValue = ((Double) min.number).doubleValue ();
    double maxValue = ((Double) max.number).doubleValue ();
    double value = ((Double) dataValue).doubleValue ();
    double fraction = (value - minValue) / (maxValue - minValue);
    //System.out.println ("min: " + min + "    max: " + max +  "  value: " + value);
    //System.out.println ("fraction: " + fraction);

    int redRange = maxColor.getRed () - minColor.getRed ();
    int newRed = (int) (redRange * fraction) + minColor.getRed ();
    //System.out.println ("new red: " + newRed);

    int greenRange = maxColor.getGreen () - minColor.getGreen ();
    int newGreen = (int) (greenRange * fraction) + minColor.getGreen ();
    //System.out.println ("new green: " + newGreen);

    int blueRange = maxColor.getBlue () - minColor.getBlue ();
    int newBlue = (int) (blueRange * fraction) + minColor.getBlue ();
    //System.out.println ("new blue: " + newBlue);
    result = new Color (newRed, newGreen, newBlue);
    }
  else {
    //System.out.println (" -- discrete data");
    result = (Color) colorMap.get (dataValue);
    }

  //System.out.println ("VizMapper.calcualateColor, result: " + result);
  if (result == null)
    return defaultColor;
  else
    return result;

} // calculateColor
//--------------------------------------------------------------------------------------
abstract void setDiscreteAttribute (Integer attribute, String key, Object value);
abstract void setContinuousAttributeControls (Integer attribute,
                                              Double min, Object minObject,
                                              Double max, Object maxObject);

abstract String getName (Integer enumeratedValue);
//--------------------------------------------------------------------------------------
} // class VizMapper
