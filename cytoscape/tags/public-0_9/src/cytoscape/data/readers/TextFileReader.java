// TextFileReader.java
//---------------------------------------------------------------------------
//  $Revision$ $Date$
//---------------------------------------------------------------------------
package cytoscape.data.readers;
//------------------------------------------------------------------------------
import java.io.*;
//---------------------------------------------------------------------------
public class TextFileReader {
  String filename;
  BufferedReader bufferedReader;
  StringBuffer strbuf;
//---------------------------------------------------------------------------
public TextFileReader (String filename)
{
  this.filename = filename;
  try {
    //reader = new FileReader (filename);
    //bufferedReader = new BufferedReader (reader);
    bufferedReader = new BufferedReader (new FileReader (filename));
    }
  catch (IOException e) {
    e.printStackTrace ();
    return;
    }
 
  strbuf = new StringBuffer ();

} // ctor
//---------------------------------------------------------------------------
public int read ()
{
  String newLineOfText;
 
  try {
    while ((newLineOfText = bufferedReader.readLine()) != null) {
      strbuf.append (newLineOfText + "\n");
      }
    }
  catch (IOException e) {
    e.printStackTrace ();
    return -1;
    }

  return (strbuf.length ());

} // read
//---------------------------------------------------------------------------
public String getText ()
{
  return (new String (strbuf));

} // read
//---------------------------------------------------------------------------
public static void main (String argv[])
{
  String fileToRead;

  int argCount = argv.length;
  if (argCount == 0) 
    fileToRead = "TextFileReader.java";
  else
    fileToRead = argv [0];
  
  TextFileReader reader = new TextFileReader (fileToRead);
  int size = reader.read ();
  System.out.println ("size of text block: " + size);
  System.out.println (reader.getText ());

}// main
//---------------------------------------------------------------------------
}
