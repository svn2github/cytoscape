package csplugins.isb.dreiss.httpdata;
import java.io.*;
import java.util.*;

/**
 * Class <code>HomologSet</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class HomologSet implements Serializable {
  String sourceSequenceName;
  String sourceSequenceCommonName;
  String sourceSpecies;
  String targetSequenceFileName;
  String sourceSequence;
  static public final int DEFAULT_MAX_HITS = 100;
  int maxHits = DEFAULT_MAX_HITS;
  Vector hits = new Vector ();
  String status = "unassigned";
  Date submissionTime;
  Date startExecutionTime;
  Date completionTime;
//----------------------------------------------------------------------------------------------------
public HomologSet (String sourceSequenceName, String sourceSpecies, 
                   String sourceSequence, String targetSequenceFileName,
                   int maxHits)
{
  this.sourceSequenceName = sourceSequenceName;
  this.sourceSpecies = sourceSpecies;
  this.targetSequenceFileName = targetSequenceFileName;
  this.sourceSequence = sourceSequence;
  this.maxHits = maxHits;
  setStartExecutionTime ();
}
//----------------------------------------------------------------------------------------------------
public HomologSet (String sourceSequenceName, String sourceSpecies, 
                   String sourceSequence, String targetSequenceFileName)
{
  this (sourceSequenceName, sourceSpecies, sourceSequence, targetSequenceFileName, DEFAULT_MAX_HITS);
}
//----------------------------------------------------------------------------------------------------
public String getSourceSequenceName ()
{
  return sourceSequenceName;
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
public String getSourceSpecies ()
{
  return sourceSpecies;
}
//----------------------------------------------------------------------------------------------------
public String getTargetSequenceFileName ()
{
  return targetSequenceFileName;
}
//----------------------------------------------------------------------------------------------------
public String getTargetSpecies ()
{
  if (size () == 0)
    return "unknown species";
  else {
    return (get (0).getTargetSpecies ());
    }
}
//----------------------------------------------------------------------------------------------------
public String getSourceSequence ()
{
  return sourceSequence;
}
//----------------------------------------------------------------------------------------------------
public int getMaxHits ()
{
  return maxHits;
}
//----------------------------------------------------------------------------------------------------
public int size ()
{
  return hits.size ();
}
//----------------------------------------------------------------------------------------------------
public void addHit (Homolog sequence)
{
  hits.add (sequence);
}
//----------------------------------------------------------------------------------------------------
public Homolog [] getAll ()
{
   return (Homolog []) hits.toArray (new Homolog [0]);
}
//----------------------------------------------------------------------------------------------------
public Homolog get (int index)
{
  if (hits.size () >= index) 
    return  (Homolog) hits.get (index);
  else
    throw new IllegalArgumentException ("no Homolog #" + index + " in HomologSet " +
                                        " length = " + size ());
     
}
//----------------------------------------------------------------------------------------------------
public void setStatus (String newValue)
{
  status = newValue;
}
//----------------------------------------------------------------------------------------------------
public String getStatus ()
{
  return status;
}
//----------------------------------------------------------------------------------------------------
public void setSubmissionTime ()
{
  submissionTime = new Date ();
}
//----------------------------------------------------------------------------------------------------
public void setStartExecutionTime ()
{
  startExecutionTime = new Date ();
}
//----------------------------------------------------------------------------------------------------
public void setCompletionTime ()
{
  completionTime = new Date ();
}
//----------------------------------------------------------------------------------------------------
public Date getSubmissionTime ()
{
  return submissionTime;
}
//----------------------------------------------------------------------------------------------------
public Date getStartExecutionTime ()
{
  return startExecutionTime;
}
//----------------------------------------------------------------------------------------------------
public Date getCompletionTime ()
{
  return completionTime;
}
//----------------------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("\n");
  sb.append ("             sourceSequenceName: " + sourceSequenceName);
  sb.append ("   (" + sourceSpecies + ")");
  sb.append ("\n");

  sb.append ("        source sequence: " + sourceSequence.substring (0,12) + "...");
  sb.append ("\n");
  
  sb.append ("         target species: " + targetSequenceFileName);
  sb.append ("\n");
  sb.append ("               max hits: " + maxHits);
  sb.append ("\n");
  sb.append ("              hit count: " + hits.size ());
  sb.append ("\n");
  sb.append ("                 status: " + status);
  sb.append ("\n");
  sb.append ("                  times: " + submissionTime + ", " + startExecutionTime + ", " +
                                          completionTime);
  sb.append ("\n");

  return sb.toString ();

} // toString
//----------------------------------------------------------------------------------------------------
}
