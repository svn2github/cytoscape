package csplugins.isb.dreiss.httpdata;
import java.io.*;

/**
 * Class <code>Homolog</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class Homolog implements Serializable {
  String sourceSequenceName = "";
  String sourceSequenceCommonName = "";
  String sourceSpecies = "";
  String targetSpecies = "";
  String sourceSequence = "";

  String rawDefLine = "";  
  String species = "";
  int giNumber = -1;
  String commonName = "";
  String refSeqID = "";
  int locusLinkID = -1;
  String hitName = "";
  String product = ""; 

  int hitLength = -1;
  double eValue = -1.0;
  double score = -1.0;
  String hspInfo = "";
  int iPercent = -1;
  int pPercent = -1;

  int identitiesMatch = -1;
  int identitiesTotal = -1;
  int positivesMatch = -1;
  int positivesTotal = -1;
  int gapsMatch = -1;
  int gapsTotal = -1;

//----------------------------------------------------------------------------------------------------
public Homolog (String sourceSequenceName, String sourceSpecies, String sourceSequence, 
                String targetSpecies)
{
  this.sourceSequenceName = sourceSequenceName;
  this.sourceSpecies = sourceSpecies;
  this.targetSpecies = targetSpecies;
  this.sourceSequence = sourceSequence;
  this.species = targetSpecies;

}
//----------------------------------------------------------------------------------------------------
public void setSourceSequenceCommonName (String newValue)
{
  sourceSequenceCommonName = newValue;
}
//----------------------------------------------------------------------------------------------------
public String getSourceSequenceCommonName ()
{
  return sourceSequenceCommonName;
}
//----------------------------------------------------------------------------------------------------
public void setTargetSpecies (String newValue)
{
  targetSpecies = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setGiNumber (int newValue)
{
  giNumber = newValue;
}
//----------------------------------------------------------------------------------------------------
public int getGiNumber ()
{
  return giNumber;
}
//----------------------------------------------------------------------------------------------------
public void setRawDefLine (String newValue)
{
  rawDefLine = newValue;
}
//----------------------------------------------------------------------------------------------------
public String getRawDefLine ()
{
  return rawDefLine;
}
//----------------------------------------------------------------------------------------------------
public void setHitLength (int newValue)
{
  hitLength = newValue;
}
//----------------------------------------------------------------------------------------------------
public int getHitLength ()
{
  return hitLength;
}
//----------------------------------------------------------------------------------------------------
public void setCommonName (String newValue)
{
  commonName = newValue;
}
//----------------------------------------------------------------------------------------------------
public String getCommonName ()
{
  return commonName;
}
//----------------------------------------------------------------------------------------------------
public void setRefSeqID (String newValue)
{
  refSeqID = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setLocusLinkID (int newValue)
{
  locusLinkID = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setHitName (String newValue)
{
  hitName = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setProduct (String newValue)
{
  product = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setEValue (double newValue)
{
  eValue = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setScore (double newValue)
{
  score = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setHspInfo (String newValue)
{
  hspInfo = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setIPercent (int newValue)
{
  iPercent = newValue;
}
//----------------------------------------------------------------------------------------------------
public void setPPercent (int newValue)
{
  pPercent = newValue;
}
//----------------------------------------------------------------------------------------------------
public String getRefSeqID ()
{
  return refSeqID;
}
//----------------------------------------------------------------------------------------------------
public int getLocusLinkID ()
{
  return locusLinkID;
}
//----------------------------------------------------------------------------------------------------
public String getHitName ()
{
  return hitName;
}
//----------------------------------------------------------------------------------------------------
public String getProduct ()
{
  return product;
}
//----------------------------------------------------------------------------------------------------
public String getSourceSequenceName ()
{
  return sourceSequenceName;
}
//----------------------------------------------------------------------------------------------------
public String getSourceSpecies ()
{
  return sourceSpecies;
}
//----------------------------------------------------------------------------------------------------
public String getTargetSpecies ()
{
  return targetSpecies;
}
//----------------------------------------------------------------------------------------------------
public String getSourceSequence ()
{
  return sourceSequence;
}
//----------------------------------------------------------------------------------------------------
public double getEValue ()
{
  return eValue;
}
//----------------------------------------------------------------------------------------------------
public double getScore ()
{
  return score;
}
//----------------------------------------------------------------------------------------------------
public String getHspInfo ()
{
  return hspInfo;
}
//----------------------------------------------------------------------------------------------------
public int getIdentitiesMatch ()
{
  return identitiesMatch;
}
//----------------------------------------------------------------------------------------------------
public int getIdentitiesTotal ()
{
  return identitiesTotal;
}
//----------------------------------------------------------------------------------------------------
public int getPositivesMatch ()
{
  return positivesMatch;
}
//----------------------------------------------------------------------------------------------------
public int getPositivesTotal ()
{
  return positivesTotal;
}
//----------------------------------------------------------------------------------------------------
public int getGapsMatch ()
{
  return gapsMatch;
}
//----------------------------------------------------------------------------------------------------
public int getGapsTotal ()
{
  return gapsTotal;
}
//----------------------------------------------------------------------------------------------------
public int getIdentitiesPercentage ()
{
  return asPercentage (identitiesMatch, identitiesTotal);
}
//----------------------------------------------------------------------------------------------------
public int getPositivesPercentage ()
{
  return asPercentage (positivesMatch, positivesTotal);
}
//----------------------------------------------------------------------------------------------------
public int getGapsPercentage ()
{
  return asPercentage (gapsMatch, gapsTotal);
}
//----------------------------------------------------------------------------------------------------
protected int asPercentage (int numerator, int denominator)
{
  if (numerator == -1 || denominator == -1 || denominator == 0)
    return 0;
  else {
    double percentage = 100.0 * numerator / denominator;
    percentage += 0.5;
    int result = (new Double (percentage)).intValue ();
    return result;
    }

} // asPercentage
//----------------------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("sourceSequenceName: " + sourceSequenceName);
  sb.append ("   (" + sourceSpecies + ")");
  sb.append ("\n");

  sb.append ("source sequence: " + sourceSequence.substring (0,12) + "...\n");
  
  sb.append ("         target species: " + species + "\n");
  sb.append ("           raw def line: " + rawDefLine + "\n");
  sb.append ("              GI Number: " + giNumber + "\n");
  sb.append ("                 refSeq: " + refSeqID + "\n");
  sb.append ("              locusLink: " + locusLinkID + "\n");
  sb.append ("               hit name: " + hitName + "\n");
  sb.append ("                product: " + product + "\n");
  sb.append ("                e value: " + eValue + "\n");
  sb.append ("                  score: " + score + "\n");
  sb.append ("            identifed %: " + iPercent + "\n");
  sb.append ("            positives %: " + pPercent + "\n");
      //  sb.append ("                hspInfo: " + hspInfo + "\n");

  return sb.toString ();

} // toString
//----------------------------------------------------------------------------------------------------
public String toBriefString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append (hitName + " ");
  sb.append (eValue + ", ");
  sb.append (score + ", ");
  sb.append (iPercent + ", ");
  sb.append (pPercent);
  return sb.toString ();
}
//----------------------------------------------------------------------------------------------------
} // class Homolog
