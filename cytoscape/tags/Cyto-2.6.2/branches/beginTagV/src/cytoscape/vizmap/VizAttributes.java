// VizAttributes.java:  encapsulate the visual attributes of nodes, edges & background
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.vizmap;
//---------------------------------------------------------------------------------------
import java.awt.Color;
import java.util.*;
import java.text.DecimalFormat;
import java.lang.Math;
import y.view.LineType;

import cytoscape.GraphObjAttributes;
//---------------------------------------------------------------------------------------
public class VizAttributes {

      // Default Variables
  protected Color defaultBackgroundColor = new Color (230, 230, 205);
  protected Color defaultNodeColor       = new Color (204, 255, 255);
  protected Color defaultBorderColor     = new Color (0,0,0);
  //protected Color defaultEdgeColor       = new Color (0,0,0);
  protected  int  defaultBorderThickness = 1;
  //protected  int  defaultEdgeThickness   = 1;

    // Node Variables for Expression Settings
  protected double[][] extremeValues;
  protected int expressionRatioBinValue = 10;
  protected int expressionSigBinValue   = 10;
  protected double expressionRatioMinValue, expressionRatioZeroValue,expressionRatioMaxValue;
  protected double expressionSigMinValue,   expressionSigZeroValue,  expressionSigMaxValue;

  protected Color  expressionRatioMinColor,expressionRatioZeroColor,expressionRatioMaxColor;
  protected Color  expressionSigMinColor,  expressionSigZeroColor,  expressionSigMaxColor;

    // Node Variables for VizChooser
  protected String nodeAttributeWhichControlsFillColor = "none";
  protected double minAttributeValue = 0.0;
  protected double maxAttributeValue = 0.0;
  protected double lowClamp, zeroClamp, highClamp;
  protected Color nodeMinColor, nodeZeroColor, nodeMaxColor;
  protected int nodeColorBins = 10;    

    // Border Variables for VizChooser
  protected String borderAttributeWhichControlsFillColor = "none";
  protected double borderMinAttributeValue = 0.0;
  protected double borderMaxAttributeValue = 0.0; 
  protected double borderLowClamp, borderZeroClamp, borderHighClamp;
  protected Color borderMinColor, borderZeroColor, borderMaxColor;
  protected int borderColorBins = 10;
  protected LineType borderThickness = LineType.LINE_1;  

    // Edge Variables for VizChooser
  /****************
  protected String edgeAttributeWhichControlsFillColor = "none";
  protected double edgeMinAttributeValue = 0.0;
  protected double edgeMaxAttributeValue = 0.0;
  protected double edgeLowClamp, edgeZeroClamp, edgeHighClamp;
  protected Color edgeMinColor, edgeZeroColor, edgeMaxColor;
  protected int edgeColorBins = 10;
  protected LineType edgeThickness = LineType.LINE_1;
  ****************/
  protected Properties props;  
  
//---------------------------------------------------------------------------------------
public VizAttributes (Properties props)
{
  this.props = props;

  nodeMinColor = nodeZeroColor = nodeMaxColor = defaultNodeColor;
  borderMinColor = borderZeroColor = borderMaxColor = defaultBorderColor;
  //edgeMinColor = edgeZeroColor = edgeMaxColor = defaultEdgeColor;
    
  expressionRatioMinValue = expressionSigMinValue = 0;
  expressionRatioZeroValue= expressionSigZeroValue = 0.5;
  expressionRatioMaxValue = expressionSigMaxValue = 1;

  //lowClamp = borderLowClamp = edgeLowClamp = 0;
  //zeroClamp= borderZeroClamp = edgeZeroClamp = 0.5;
  //highClamp= borderHighClamp = edgeHighClamp = 1;

  lowClamp = borderLowClamp =  0;
  zeroClamp= borderZeroClamp = 0.5;
  highClamp= borderHighClamp = 1;

  parseProperties (props);

} // ctor
//---------------------------------------------------------------------------------------
protected void parseProperties (Properties props)
{
 String key = "background.color";
 if (props.containsKey (key))
   defaultBackgroundColor = parseRGBText (props.getProperty (key));

 key = "node.fill.color";
 if (props.containsKey (key))
   defaultNodeColor = parseRGBText (props.getProperty (key));

 key = "node.border.color";
 if (props.containsKey (key)) {
   //System.out.println (key + " -> " + props.getProperty (key));
   defaultBorderColor = parseRGBText (props.getProperty (key));
   }


}
//---------------------------------------------------------------------------------------
public void setVizChooserExpressionValues (double[][] extremes)
{
  expressionRatioMinValue = extremes[0][0];
  expressionRatioZeroValue = 0; //VThorsson thinks 0 might be more useful than average
  expressionRatioMaxValue = extremes[0][1];
  expressionSigMinValue = extremes[1][0];
  expressionSigZeroValue= (extremes[1][0]+extremes[1][1])/4;
  expressionSigMaxValue = extremes[1][1];

  expressionRatioBinValue = 20;
  expressionRatioMinColor = Color.red;
  expressionRatioZeroColor= Color.white;
  expressionRatioMaxColor = Color.green;
  expressionSigBinValue = 20;
  expressionSigMinColor = Color.red;
  expressionSigZeroColor= Color.white;
  expressionSigMaxColor = Color.green;

}
//---------------------------------------------------------------------------------------
//public LineType getEdgeThickness()
//{
//    return edgeThickness;
//}
//---------------------------------------------------------------------------------------
//public int getDefaultEdgeThickness()
//{
//    return defaultEdgeThickness;
//}
//---------------------------------------------------------------------------------------
public LineType getBorderThickness()
{
    return borderThickness;
}
//---------------------------------------------------------------------------------------
public int getDefaultBorderThickness()
{
    return defaultBorderThickness;
}
//---------------------------------------------------------------------------------------
public Color getBorderMaxColor()
{
    return borderMaxColor;
}
//---------------------------------------------------------------------------------------
public Color getBorderZeroColor()
{
    return borderZeroColor;
}
//---------------------------------------------------------------------------------------
public Color getBorderMinColor()
{
    return borderMinColor;
}
//---------------------------------------------------------------------------------------
public Color getNodeMaxColor()
{
    return nodeMaxColor;
}
//---------------------------------------------------------------------------------------
public Color getNodeZeroColor()
{
    return nodeZeroColor;
}
//---------------------------------------------------------------------------------------
public Color getNodeMinColor()
{
    return nodeMinColor;
}
//---------------------------------------------------------------------------------------
//public Color getEdgeMaxColor()
//{
//    return edgeMaxColor;
//}
//---------------------------------------------------------------------------------------
//public Color getEdgeZeroColor()
//{
//    return edgeZeroColor;
//}
//---------------------------------------------------------------------------------------
//public Color getEdgeMinColor()
//{
//    return edgeMinColor;
//}
//---------------------------------------------------------------------------------------
public Color getExpressionSigMaxColor()
{
    return expressionSigMaxColor;
}
//---------------------------------------------------------------------------------------
public Color getExpressionSigZeroColor()
{
    return expressionSigZeroColor;
}
//---------------------------------------------------------------------------------------
public Color getExpressionSigMinColor()
{
    return expressionSigMinColor;
}
//---------------------------------------------------------------------------------------
public double getExpressionSigMaxValue()
{
    return expressionSigMaxValue;
}
//---------------------------------------------------------------------------------------
public double getExpressionSigZeroValue()
{
    return expressionSigZeroValue;
}
//---------------------------------------------------------------------------------------
public double getExpressionSigMinValue()
{
    return expressionSigMinValue;
}
//---------------------------------------------------------------------------------------
public int getExpressionSigBinValue()
{
    return expressionSigBinValue;
}
//---------------------------------------------------------------------------------------
public Color getExpressionRatioMaxColor()
{
    return expressionRatioMaxColor;
}
//---------------------------------------------------------------------------------------
public Color getExpressionRatioZeroColor()
{
    return expressionRatioZeroColor;
}
//---------------------------------------------------------------------------------------
public Color getExpressionRatioMinColor()
{
    return expressionRatioMinColor;
}
//---------------------------------------------------------------------------------------
public double getExpressionRatioMaxValue()
{
    return expressionRatioMaxValue;
}
//---------------------------------------------------------------------------------------
public double getExpressionRatioZeroValue()
{
    return expressionRatioZeroValue;
}
//---------------------------------------------------------------------------------------
public double getExpressionRatioMinValue()
{
    return expressionRatioMinValue;
}
//---------------------------------------------------------------------------------------
public int getExpressionRatioBinValue()
{
    return expressionRatioBinValue;
}
//---------------------------------------------------------------------------------------
//public String getEdgeAttributeWhichControlsFillColor()
//{
//    return edgeAttributeWhichControlsFillColor;
//}
//---------------------------------------------------------------------------------------
public String getBorderAttributeWhichControlsFillColor()
{
    return borderAttributeWhichControlsFillColor;
}
//---------------------------------------------------------------------------------------
public String getNodeAttributeWhichControlsFillColor()
{
    return nodeAttributeWhichControlsFillColor;
}
//---------------------------------------------------------------------------------------
public Color getDefaultBackgroundColor()
{
    return defaultBackgroundColor;
}
//---------------------------------------------------------------------------------------
//public Color getDefaultEdgeColor()
//{
//    return defaultEdgeColor;
//}
//---------------------------------------------------------------------------------------
public Color getDefaultBorderColor()
{
    return defaultBorderColor;
}
//---------------------------------------------------------------------------------------
public Color getDefaultNodeColor()
{
    return defaultNodeColor;
}
//---------------------------------------------------------------------------------------
//public void setEdgeAttributeWhichControlsFillColor(String condition)
//{
//    edgeAttributeWhichControlsFillColor = condition;
//}
//---------------------------------------------------------------------------------------
public void setBorderAttributeWhichControlsFillColor(String condition)
{
    borderAttributeWhichControlsFillColor = condition;
}
//---------------------------------------------------------------------------------------
public void setNodeAttributeWhichControlsFillColor(String condition)
{
    nodeAttributeWhichControlsFillColor = condition;
}
//---------------------------------------------------------------------------------------
public double getExpressionLowClamp()
{
    return expressionRatioMinValue;
}
//---------------------------------------------------------------------------------------
public double getExpressionHighClamp()
{
    return expressionRatioMaxValue;
}
//---------------------------------------------------------------------------------------
public double getExtremeMaxRatioValue()
{
    return extremeValues[0][1];
}
//---------------------------------------------------------------------------------------
public double getExtremeMinRatioValue()
{
    return extremeValues[0][0];
}

//---------------------------------------------------------------------------------------
public double getExtremeMinSigValue()
{
    return extremeValues[1][0];
}
//---------------------------------------------------------------------------------------
public double getExtremeMaxSigValue()
{
    return extremeValues[1][1];
}
//---------------------------------------------------------------------------------------
public void setExtremeValues(double[][] extremes)
{
    extremeValues = extremes;
}
//---------------------------------------------------------------------------------------
public double[][] getExtremeValues()
{
    return extremeValues;
}
//---------------------------------------------------------------------------------------
public void setDefaultNodeColor(Color c)
{
    defaultNodeColor = c;
}
//---------------------------------------------------------------------------------------
//public void setDefaultEdgeColor(Color c)
//{
//    defaultEdgeColor = c;
//}
//---------------------------------------------------------------------------------------
public void setDefaultBackgroundColor(Color c)
{
    defaultBackgroundColor = c;
}
//---------------------------------------------------------------------------------------
public void setDefaultSettings(Color nodeColor, Color borderColor,
                               // Color edgeColor, 
                               Color backgroundColor)
{
    nodeAttributeWhichControlsFillColor = "default";
    borderAttributeWhichControlsFillColor = "default";
    defaultNodeColor = nodeColor;
    defaultBorderColor = borderColor;
    // defaultEdgeColor = edgeColor;
    defaultBackgroundColor = backgroundColor;
}
//---------------------------------------------------------------------------------------

public void setExpressionInformation(int ratioBin,
                                     double ratioMin, double ratioZero,
                                     double ratioMax, Color ratioMinC,
                                     Color ratioZeroC,Color ratioMaxC,
                                     int sigBin,
                                     double sigMin, double sigZero,
                                     double sigMax, Color sigMinC,
                                     Color sigZeroC,Color sigMaxC,
                                     int thickness)
{
    expressionRatioBinValue = ratioBin;
    expressionRatioMinValue = ratioMin;
    expressionRatioZeroValue= ratioZero;
    expressionRatioMaxValue = ratioMax;
    expressionRatioMinColor = ratioMinC;
    expressionRatioZeroColor= ratioZeroC;
    expressionRatioMaxColor = ratioMaxC;

    expressionSigBinValue = sigBin;
    expressionSigMinValue = sigMin;
    expressionSigZeroValue= sigZero;
    expressionSigMaxValue = sigMax;
    expressionSigMinColor = sigMinC;
    expressionSigZeroColor= sigZeroC;
    expressionSigMaxColor = sigMaxC;
    
    borderThickness = getLineType(thickness);
}


//---------------------------------------------------------------------------------------


public void setNodeColorCalculationFactors (String attributeName,
                                            Color minColor, Color zeroColor,
                                            Color maxColor, int numberOfBins, 
                                            double attributeMin, double attributeMax,
                                            double lowClamp, double zeroClamp,
                                            double highClamp)
{
    nodeMinColor = minColor;
    nodeZeroColor= zeroColor;
    nodeMaxColor = maxColor;
    nodeColorBins = numberOfBins;
    this.lowClamp  = lowClamp;
    this.zeroClamp = zeroClamp;
    this.highClamp = highClamp;
    minAttributeValue = attributeMin;
    maxAttributeValue = attributeMax;
}
//---------------------------------------------------------------------------------------
public void setBorderColorCalculationFactors (String attributeName,
                                            Color minColor, Color zeroColor,
                                            Color maxColor, int numberOfBins, 
                                            double attributeMin, double attributeMax,
                                            double lowClamp, double zeroClamp,
                                            double highClamp, int borderThick)
{
    borderAttributeWhichControlsFillColor = attributeName;
    borderMinColor = minColor;
    borderZeroColor= zeroColor;
    borderMaxColor = maxColor;
    borderColorBins = numberOfBins;
    borderMinAttributeValue = attributeMin;
    borderMaxAttributeValue = attributeMax;
    borderLowClamp  = lowClamp;
    borderZeroClamp = zeroClamp;
    borderHighClamp = highClamp;
    borderThickness = getLineType(borderThick);
    
}
//---------------------------------------------------------------------------------------
/***************
public void setEdgeColorCalculationFactors (String attributeName,
                                            Color minColor, Color zeroColor,
                                            Color maxColor, int numberOfBins, 
                                            double attributeMin, double attributeMax,
                                            double lowClamp, double zeroClamp,
                                            double highClamp, int borderThick)
{
    edgeAttributeWhichControlsFillColor = attributeName;
    edgeMinColor = minColor;
    edgeZeroColor= zeroColor;
    edgeMaxColor = maxColor;
    edgeColorBins = numberOfBins;
    edgeMinAttributeValue = attributeMin;
    edgeMaxAttributeValue = attributeMax;
    edgeLowClamp  = lowClamp;
    edgeZeroClamp = zeroClamp;
    edgeHighClamp = highClamp;
    edgeThickness = getLineType(borderThick);
    
}
***********************/
//---------------------------------------------------------------------------------------
public LineType getLineType(int thick)
{
    switch(thick){
    case 1:
        return LineType.LINE_1;
    case 2:
        return LineType.LINE_2;
    case 3:
        return LineType.LINE_3;
    case 4:
        return LineType.LINE_4;
    case 5:
        return LineType.LINE_5;
    case 6:
        return LineType.LINE_6;
    case 7:
        return LineType.LINE_7;
    default:
        System.out.println("default line type");
    }
    return LineType.LINE_2;//should only get here by default
}
//---------------------------------------------------------------------------------------
//mj- Do we need this?
public void setNodeColorCalculationFactors (String attributeName,
                                            Color minColor, Color zeroColor,
                                            Color maxColor, int numberOfBins, 
                                            double attributeMin, double attributeMax)
{
    
  double lowClamp  = attributeMin;
  double zeroClamp = (attributeMin+attributeMax)/2;
  double highClamp = attributeMax;
  setNodeColorCalculationFactors (attributeName, 
                                  minColor, zeroColor,
                                  maxColor, numberOfBins, 
                                  attributeMin,  attributeMax, 
                                  lowClamp, zeroClamp,
                                  highClamp);


} 
//---------------------------------------------------------------------------------------
//mj- Do we need this?
public void setBorderColorCalculationFactors (String attributeName,
                                              Color minColor, Color zeroColor,
                                              Color maxColor, int numberOfBins, 
                                              double attributeMin, double attributeMax)
{
    
  double lowClamp  = attributeMin;
  double zeroClamp = (attributeMin+attributeMax)/2;
  double highClamp = attributeMax;
  setBorderColorCalculationFactors (attributeName, 
                                    minColor, zeroColor,
                                    maxColor, numberOfBins,  
                                    attributeMin,  attributeMax, 
                                    lowClamp, zeroClamp,
                                    highClamp, defaultBorderThickness);


}
//---------------------------------------------------------------------------------------
public double[][] getExpressionColorBins(){
    return calculateColorBins(expressionRatioMinColor, expressionRatioZeroColor,
                              expressionRatioMaxColor, expressionRatioMinValue,
                              expressionRatioZeroValue, expressionRatioMaxValue,
                              expressionRatioBinValue, extremeValues[0][1],
                              extremeValues[0][0]);
}
//---------------------------------------------------------------------------------------
public double[][] getSignificanceColorBins(){
    return calculateColorBins(expressionSigMinColor, expressionSigZeroColor,
                              expressionSigMaxColor, expressionSigMinValue,
                              expressionSigZeroValue, expressionSigMaxValue,
                              expressionSigBinValue, extremeValues[1][1],
                              extremeValues[1][0]);
}
//---------------------------------------------------------------------------------------
public double[][] getNodeColorBins(){
  return calculateColorBins(nodeMinColor, nodeZeroColor,
                            nodeMaxColor, lowClamp,
                            zeroClamp, highClamp,
                            nodeColorBins, maxAttributeValue,
                            minAttributeValue);
}
//---------------------------------------------------------------------------------------
public double[][] getBorderColorBins(){
  return calculateColorBins(borderMinColor, borderZeroColor,
                            borderMaxColor, borderLowClamp,
                            borderZeroClamp, borderHighClamp,
                            borderColorBins, borderMaxAttributeValue,
                            borderMinAttributeValue);
}//---------------------------------------------------------------------------------------
//public double[][] getEdgeColorBins(){
//  return calculateColorBins(edgeMinColor, edgeZeroColor,
//                            edgeMaxColor, edgeLowClamp,
//                            edgeZeroClamp, edgeHighClamp,
//                            edgeColorBins, edgeMaxAttributeValue,
//                            edgeMinAttributeValue);
//}
//---------------------------------------------------------------------------------------
public double[][] getDefaultNodeColorBins(){
    return calculateColorBins(defaultNodeColor, defaultNodeColor,
                              defaultNodeColor, 0,
                              0.5, 1,
                              5, 1,
                              0);
}
//---------------------------------------------------------------------------------------
public double[][] getDefaultBorderColorBins(){
    return calculateColorBins(defaultBorderColor, defaultBorderColor,
                              defaultBorderColor, 0,
                              0.5, 1,
                              5, 1,
                              0);
}
//---------------------------------------------------------------------------------------
//public double[][] getDefaultEdgeColorBins(){
//    return calculateColorBins(defaultEdgeColor, defaultEdgeColor,
//                              defaultEdgeColor, 0,
//                              0.5, 1,
//                              5, 1,
//                              0);
//}
//---------------------------------------------------------------------------------------

    //[][0]=>data limit
    //[][1]=>red   component of RGB
    //[][2]=>green component of RGB
    //[][3]=>blue  component of RGB
public double [][] calculateColorBins (Color colorMin, Color colorZero,
                                       Color colorMax, double clampLow,
                                       double clampZero, double clampHigh,
                                       int binAmount, double attributeMax,
                                       double attributeMin)
{
    int keeper = 0;
    double ratioValue = 0;
    //--- get color data ---//
    int minRed   = colorMin.getRed ();
    int minGreen = colorMin.getGreen ();
    int minBlue  = colorMin.getBlue ();
    
    int zeroRed   = colorZero.getRed();
    int zeroGreen = colorZero.getGreen();
    int zeroBlue  = colorZero.getBlue();
    
    int maxRed   = colorMax.getRed();
    int maxGreen = colorMax.getGreen();
    int maxBlue  = colorMax.getBlue();
    
    //--- get data ranges ---//
    double upperRange = clampHigh - clampZero;
    double lowerRange = clampZero - clampLow;
    
    int lowBinCount  = binAmount;
    int highBinCount = binAmount;
       
    if (clampHigh < attributeMax)
        highBinCount++;
    if (clampLow > attributeMin)
        lowBinCount++;
    int totalBinCount = lowBinCount+highBinCount;
    
    double bins [][] = new double [totalBinCount][4];
    
    double upperBinSizeWithinClampedRegion = upperRange/highBinCount;
    double lowerBinSizeWithinClampedRegion = lowerRange/lowBinCount;
    
    //we should know the min bin and zero bin colors and values
    //mjbins [0][0] = attributeMin;
    bins [0][0] = clampLow;
    bins [0][1] = minRed;
    bins [0][2] = minGreen;
    bins [0][3] = minBlue;
    
    bins [lowBinCount][0] = clampZero;
    bins [lowBinCount][1] = zeroRed;
    bins [lowBinCount][2] = zeroGreen;
    bins [lowBinCount][3] = zeroBlue;
    
    //120301
    bins [totalBinCount-1][0] = clampHigh;
    bins [totalBinCount-1][1] = maxRed;
    bins [totalBinCount-1][2] = maxGreen;
    bins [totalBinCount-1][3] = maxBlue;

    for(int i=1;i<lowBinCount;i++){
        bins[i][0] = bins[i-1][0] + lowerBinSizeWithinClampedRegion;
        bins[i][1] = calculateRGBValue(minRed,   zeroRed,   clampLow, clampZero, bins[i][0]);
        bins[i][2] = calculateRGBValue(minGreen, zeroGreen, clampLow, clampZero, bins[i][0]);
        bins[i][3] = calculateRGBValue(minBlue,  zeroBlue,  clampLow, clampZero, bins[i][0]);
    }
    for(int i=lowBinCount+1;i<totalBinCount-1;i++){
        bins[i][0] = bins[i-1][0] + upperBinSizeWithinClampedRegion;
        bins[i][1] = calculateRGBValue(zeroRed,   maxRed,   clampZero, clampHigh, bins[i][0]);
        bins[i][2] = calculateRGBValue(zeroGreen, maxGreen, clampZero, clampHigh, bins[i][0]);
        bins[i][3] = calculateRGBValue(zeroBlue,  maxBlue,  clampZero, clampHigh, bins[i][0]);
    }
    return bins;
} // calculateNodeColorBins
//---------------------------------------------------------------------------------------
//---- note: the thoery behind calculateRGBValue is as follows:
//           we have a range of color values that are always non-negative and less than
//           255. We have our data range, which can span both positive and negative
//           values. To prevent complications, we will set the lowest data value to 0.
//           The result is that we know that every value in the range will be positive.
//           Also, the ratio used for scaling is simply the current value div. by the
//           the max value.  Since we're only concerned about the data ratio, this is all
//           we need.
public int calculateRGBValue(int lowerColorValue, int upperColorValue,
                             double lowerValue, double upperValue,
                             double currentValue){
    //    boolean isLowNegative = (lowerValue  <=0)?true:false;
    //    boolean isUpNegative  = (upperValue  <=0)?true:false;
    //    boolean isCurNegative = (currentValue<=0)?true:false;
    double ratio;
    double tempUpper, tempCurrent;
   
    tempCurrent = currentValue+(lowerValue*-1);
    tempUpper = upperValue+(lowerValue*-1);
    ratio = tempCurrent/tempUpper;
   
    int test = 0;
    test = (int)( (ratio*upperColorValue)+
                  ((1-ratio)*lowerColorValue) );
    if(test >=0 && test <=255)
        return test;
    else{
        //System.out.println("check calculateRGBValue in VizAttributes");
        return 100;
    }
}
    
//---------------------------------------------------------------------------------------
public Color getNodeColor (GraphObjAttributes nodeAttributes, String nodeName)
// the strategy: 
//   nodeAttributes is (at the top level) a hash with property names as keys.
//   the value associated with each of those keys is itself a hash, whose
//   keys are nodeNames, and whose values are (usually) double precision numbers
//   take these steps (bailing out, returning <defaultNodeColor> as necessary):
//
//      1.  from the top level hash, get the (String, double) hash associated
//          with <nodeAttributeWhichControlsFillColor>
//
//      2.  get the double value associated with <nodeName>; see which bin
//          it falls in; use the relative location of that bin to adjust the
//          intensity of the nodeBaseColor; return that adjusted color
{
    Color newColor;
  if (nodeAttributeWhichControlsFillColor == null ||
      nodeAttributeWhichControlsFillColor.trim().length () == 0)
    return defaultNodeColor;

  Double D = nodeAttributes.getDoubleValue (nodeAttributeWhichControlsFillColor, nodeName);
  double attributeValue =  0.0;
  if (D == null)
    return defaultNodeColor;

  attributeValue = D.doubleValue ();

  int selectedBin = 0;
  double [][] bins = calculateColorBins(nodeMinColor, nodeZeroColor,
                                        nodeMaxColor, lowClamp,
                                        zeroClamp, highClamp,
                                        nodeColorBins, maxAttributeValue,
                                        minAttributeValue);
  int binCount = bins.length;
  if(attributeValue>=maxAttributeValue || attributeValue >= highClamp)
      newColor = nodeMaxColor;
  if(attributeValue<=minAttributeValue || attributeValue <=lowClamp)
      newColor = nodeMinColor;
  else{
      for (int i=binCount-1; i >= 0; i--)
          if (attributeValue >= bins [i][0]) {
              selectedBin = i;
              break;//we know what bin to place it in
          } 
     Color tempColor = new Color((int)bins[selectedBin][1],
                                 (int)bins[selectedBin][2],
                                 (int)bins[selectedBin][3]);
     newColor = tempColor;
  }
  return newColor;
} // getNodeColor
//---------------------------------------------------------------------------------------
//public Color getEdgeColor (GraphObjAttributes edgeAttributes, String edgeName)
//{
//  Color newColor;
//  if (edgeAttributeWhichControlsFillColor == null ||
//     edgeAttributeWhichControlsFillColor.trim().length () == 0)
//    return defaultEdgeColor;
//
//  Double d = edgeAttributes.getDoubleValue (edgeAttributeWhichControlsFillColor, edgeName);
//  double attributeValue =  0.0;
//  if (d == null)
//    return defaultEdgeColor;
//
//  attributeValue = d.doubleValue ();
//
//  int selectedBin = 0;
//  double [][] bins = calculateColorBins(edgeMinColor, edgeZeroColor,
//                                        edgeMaxColor, edgeLowClamp,
//                                        edgeZeroClamp, edgeHighClamp,
//                                        edgeColorBins, edgeMaxAttributeValue,
//                                        edgeMinAttributeValue);
//  int binCount = bins.length;
//  if(attributeValue>=edgeMaxAttributeValue || attributeValue >= edgeHighClamp)
//      newColor = edgeMaxColor;
//  if(attributeValue<=edgeMinAttributeValue || attributeValue <=edgeLowClamp)
//      newColor = edgeMinColor;
//  else{
//      for (int i=binCount-1; i >= 0; i--)
//          if (attributeValue >= bins [i][0]) {
//              selectedBin = i;
//              break;//we know what bin to place it in
//          } 
//     Color tempColor = new Color((int)bins[selectedBin][1],
//                                 (int)bins[selectedBin][2],
//                                 (int)bins[selectedBin][3]);
//     newColor = tempColor;
//  }
//  return newColor;
//
//} // getNodeColor
//---------------------------------------------------------------------------------------
public Color getBorderColor (GraphObjAttributes nodeAttributes, String nodeName)
{
    Color newColor;
    if (borderAttributeWhichControlsFillColor == null ||
        borderAttributeWhichControlsFillColor.trim().length () == 0) {
        //System.out.println ("returning default: " + defaultBorderColor);
        return defaultBorderColor;
        }
    Double D = nodeAttributes.getDoubleValue (borderAttributeWhichControlsFillColor, 
                                              nodeName);
    double attributeValue =  0.0;
    if (D == null)
        return defaultBorderColor;
    
    attributeValue = D.doubleValue ();
    
    int selectedBin = 0;
    double [][] bins = calculateColorBins(borderMinColor, borderZeroColor,
                                          borderMaxColor, borderLowClamp,
                                          borderZeroClamp, borderHighClamp,
                                          borderColorBins, borderMaxAttributeValue,
                                          borderMinAttributeValue);
    int binCount = bins.length;
    
    if(attributeValue>=maxAttributeValue || attributeValue >= highClamp)
        newColor = borderMaxColor;
    if(attributeValue<=minAttributeValue || attributeValue <=lowClamp)
        newColor = borderMinColor;
    else{
        for (int i=binCount-1; i >= 0; i--)
            if (attributeValue >= bins [i][0]) {
                selectedBin = i;
                break;//we know what bin to place it in
            } 
        Color tempColor = new Color((int)bins[selectedBin][1],
                                    (int)bins[selectedBin][2],
                                    (int)bins[selectedBin][3]);
        newColor = tempColor;
    }
    return newColor;
}//getBorderColor
//---------------------------------------------------------------------------------------
Color mapAttributeValueToColor (double attributeValue)
{
    Color newColor;
    int selectedBin = 0;
    double [][] bins = calculateColorBins(nodeMinColor, nodeZeroColor,
                                          nodeMaxColor, lowClamp,
                                          zeroClamp, highClamp,
                                          nodeColorBins, maxAttributeValue,
                                          minAttributeValue);
    int binCount = bins.length;

    if(attributeValue>=maxAttributeValue || attributeValue >= highClamp)
        newColor = nodeMaxColor;
    if(attributeValue<=minAttributeValue || attributeValue <=lowClamp)
        newColor = nodeMinColor;
    else{
        for (int i=binCount-1; i >= 0; i--)
            if (attributeValue >= bins [i][0]) {
                selectedBin = i;
                break;//we know what bin to place it in
            } 
        Color tempColor = new Color((int)bins[selectedBin][1],
                                    (int)bins[selectedBin][2],
                                    (int)bins[selectedBin][3]);
        newColor = tempColor;
    }
    return newColor;

} // mapAttributeValueToColor
//---------------------------------------------------------------------------------------
public static Color parseRGBText (String text)
{
  StringTokenizer strtok = new StringTokenizer (text, ",");
  if (strtok.countTokens () != 3) {
    System.err.println ("illegal RGB string in VizAttributes.parseRGBText: " + text);
    return Color.black;
    }

  String red = strtok.nextToken ();
  String green = strtok.nextToken ();
  String blue = strtok.nextToken ();
  
  try {
    int r = Integer.parseInt (red);
    int g = Integer.parseInt (green);
    int b = Integer.parseInt (blue);
    return new Color (r,g,b);
    }
  catch (NumberFormatException e) {
    return Color.black;
    }  

} // parseRGBText
//--------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();

  sb.append ("  default background color: ");
  sb.append (defaultBackgroundColor);
  sb.append ("\n");

  sb.append ("        default node color: ");
  sb.append (defaultNodeColor);
  sb.append ("\n");

  sb.append ("  current background color: ");
  sb.append (defaultBackgroundColor);
  sb.append ("\n");

  sb.append ("   current node min color: ");
  sb.append (nodeMinColor);
  sb.append ("\n");

  sb.append ("   current node zero color: ");
  sb.append (nodeZeroColor);
  sb.append ("\n");

  sb.append ("   current node max color: ");
  sb.append (nodeMaxColor);
  sb.append ("\n");

  sb.append ("      node color attribute: ");
  sb.append (nodeAttributeWhichControlsFillColor);
  sb.append ("\n");

  sb.append ("           node color bins: ");
  sb.append (nodeColorBins);
  sb.append ("\n");

  sb.append ("         minAttributeValue: ");
  sb.append (minAttributeValue);
  sb.append ("\n");

  sb.append ("         maxAttributeValue: ");
  sb.append (maxAttributeValue);
  sb.append ("\n");

  sb.append ("                  lowClamp: ");
  sb.append (lowClamp);
  sb.append ("\n");

  sb.append ("                 highClamp: ");
  sb.append (highClamp);
  sb.append ("\n");

  double [][] bins = calculateColorBins ( nodeMinColor, nodeZeroColor,
                                          nodeMaxColor, lowClamp,
                                          zeroClamp, highClamp,
                                          nodeColorBins, maxAttributeValue,
                                          minAttributeValue);
  for (int i=0; i < bins.length; i++) {
    double nodeBinFloor = bins [i][0];
      // some horrible string manipulations....
    String format = "###0.00";
    DecimalFormat formatter = new DecimalFormat (format);
    String floorAsString = formatter.format (nodeBinFloor);
    int desiredLength = format.length ();
    int spacesNeeded = desiredLength - floorAsString.length ();
    for (int s=0; s < spacesNeeded; s++)
       floorAsString = " " + floorAsString;
    sb.append ("                     bin " + i + ": " + floorAsString);
    sb.append ("  ");
    sb.append (mapAttributeValueToColor (nodeBinFloor));
    sb.append ("\n");
    } 
  
  return sb.toString ();

} // toString
//---------------------------------------------------------------------------------------
} // class VizAttributes
