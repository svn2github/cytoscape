// WebReader.java
//---------------------------------------------------------------------------
package csplugins.isb.pshannon.py;
//----------------------------------------------------------------------------------------
import java.io.*;
import java.net.*;
import java.util.Date;
//---------------------------------------------------------------------------
public class WebReader {
//---------------------------------------------------------------------------
//public WebReader ()
//{
//}
//---------------------------------------------------------------------------
public String read (String urlString)
{
  try {
    int characterCount = 0;
    StringBuffer result = new StringBuffer ();
    URL url = new URL (urlString);
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection ();
    BufferedReader theHTML = new BufferedReader 
                     (new InputStreamReader (urlConnection.getInputStream ()));
    String thisLine;
    while ((thisLine = theHTML.readLine ()) != null) {
      result.append (thisLine);
      result.append ("\n");

      }
    return result.toString ();
    } 
  catch (Exception e) {
    return ("exception when reading " + urlString + ": " + e.getMessage ());
    }

} // read
//-------------------------------------------------------------------------
} // class WebReader
