package cytoscape.data.readers;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;

public class CyAttributesReader
{

  public static void loadAttributes(CyAttributes cyAttrs,
                                    Reader fileIn) throws IOException
  {
    final BufferedReader reader;
    if (fileIn instanceof BufferedReader) { reader = (BufferedReader) fileIn; }
    else { reader = new BufferedReader(fileIn); }
    final String attributeName;
    byte type = -1;
    {
      final String firstLine = reader.readLine();
      if (firstLine == null) { return; }
      final StringTokenizer tokens = new StringTokenizer(firstLine);
      attributeName = tokens.nextToken();
      if (tokens.hasMoreTokens()) {
        final String foo = tokens.nextToken();
        final String searchStr = "class=";
        final int inx = foo.indexOf(searchStr);
        if (inx >= 0) {
          String className = foo.substring(inx + searchStr.length()).trim();
          if (className.endsWith(")")) {
            className = className.substring(0, className.length() - 1); }
          if (className.equalsIgnoreCase("java.lang.String")) {
            type = MultiHashMapDefinition.TYPE_STRING; }
          else if (className.equalsIgnoreCase("java.lang.Boolean")) {
            type = MultiHashMapDefinition.TYPE_BOOLEAN; }
          else if (className.equalsIgnoreCase("java.lang.Integer")) {
            type = MultiHashMapDefinition.TYPE_INTEGER; }
          else if (className.equalsIgnoreCase("java.lang.Double")) {
            type = MultiHashMapDefinition.TYPE_FLOATING_POINT; } } }
    }
  }

}
