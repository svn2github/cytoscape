// GoPathTerminatorTextReader.java:  
//------------------------------------------------------------------------------
// $Revision$  $Date$
//------------------------------------------------------------------------------
package cytoscape.data.readers;
//------------------------------------------------------------------------------
import java.io.*; 
import java.util.Vector;
import java.util.StringTokenizer;
//-------------------------------------------------------------------------
public class GoPathTerminatorTextReader { 
  String filename;

//-------------------------------------------------------------------------
public GoPathTerminatorTextReader (String filename)
{
   this.filename = filename;
}
//-------------------------------------------------------------------------
public Vector read () throws Exception
{
  TextFileReader reader = new TextFileReader (filename);
  Vector list = new Vector ();
  int size = reader.read ();

  StringTokenizer strtok = new StringTokenizer (reader.getText(), "\n");
  while (strtok.hasMoreTokens ()) {
    String newLine = strtok.nextToken ();
    if (newLine.startsWith ("#")) 
      continue;
    int goID = Integer.parseInt (newLine);
    list.addElement (new Integer (goID));
    } // while

  return list;

} // read
//-------------------------------------------------------------------------
} // class GoPathTerminatorTextReader
