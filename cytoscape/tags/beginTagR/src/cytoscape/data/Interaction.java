// Interaction.java:  protein-protein or protein-DNA: parse text file, encapsulate
//----------------------------------------------------------------------------------------
// RCS: $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data;
//----------------------------------------------------------------------------------------
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
//----------------------------------------------------------------------------------------
public class Interaction {
  private String source;
  private Vector targets = new Vector ();
  private String interactionType;
  private Vector allInteractions = new Vector ();
//----------------------------------------------------------------------------------------
public Interaction (String source, String target, String interactionType)
{
  this.source = source;
  this.interactionType = interactionType;
  this.targets.addElement (target);

} // ctor (3 args)
//----------------------------------------------------------------------------------------
public Interaction (String rawText)
{
  StringTokenizer strtok = new StringTokenizer (rawText);
  int counter = 0;
  while (strtok.hasMoreTokens ()) {
    if (counter == 0) 
      source = (String) strtok.nextToken ();
    else if (counter == 1)
      interactionType = (String) strtok.nextToken ();
    else {
      String newTarget = (String) strtok.nextToken ();
      targets.addElement (newTarget);
      }
    counter++;
    }
  
} // ctor (String)
//---------------------------------------------------------------------------------------
public String getSource ()
{
  return source;
}
//---------------------------------------------------------------------------------------
public String getType ()
{
  return interactionType;
}
//---------------------------------------------------------------------------------------
public int numberOfTargets ()
{
  return targets.size ();
}
//---------------------------------------------------------------------------------------
public String [] getTargets ()
{
  String [] result = new String [targets.size ()];
  for (int i=0; i < result.length; i++)
    result [i] = (String) targets.elementAt (i);

  return result;

} // getTargets
//---------------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append (interactionType);
  sb.append ("::");
  sb.append (source);
  sb.append ("::");
  for (int i=0; i < targets.size (); i++) {
    sb.append ((String) targets.elementAt (i));
    if (i < targets.size () - 1) sb.append (",");
    }
  return sb.toString ();
}
//---------------------------------------------------------------------------------------
} // Interaction
