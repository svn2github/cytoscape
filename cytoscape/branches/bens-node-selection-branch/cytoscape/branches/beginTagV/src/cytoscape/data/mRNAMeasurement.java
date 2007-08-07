// mRNAMeasurement:  encapsulate the ratio/signficance pair
//-------------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data;
//-------------------------------------------------------------------------------------------
public class mRNAMeasurement {
  private double expressionRatio;
  private double significance;
//-------------------------------------------------------------------------------------------
public mRNAMeasurement (String ratioString, String significanceString)
{
  expressionRatio = -99999.9;
  try {
    expressionRatio = Double.parseDouble (ratioString);
    }
  catch (NumberFormatException ignore) {;}

  significance = -99999.9;
  try {
    significance = Double.parseDouble (significanceString);
    }
  catch (NumberFormatException ignore) {;}

}
//-------------------------------------------------------------------------------------------
public double getRatio ()
{
  return expressionRatio;
}
//-------------------------------------------------------------------------------------------
public double getSignificance ()
{
  return significance;
}
//-------------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append (expressionRatio);
  sb.append (",  ");
  sb.append (significance);
  return sb.toString ();

} // toString
//-------------------------------------------------------------------------------------------
} // mRNAMeasurement
