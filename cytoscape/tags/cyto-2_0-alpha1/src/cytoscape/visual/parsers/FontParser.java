//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.parsers;
//----------------------------------------------------------------------------
import java.awt.Font;
import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * Parses a String into a Font object.
 */
public class FontParser implements ValueParser {

    public Object parseStringValue(String value) {
        return parseFont(value);
    }
    public Font parseFont(String value) {
        //this algorithm could be moved into the Misc class with the
        //other parsing methods
        if (value == null) {return null;}
        //find index of first comma character
        int comma1 = value.indexOf(",");
        //return null if not found, or found at beginning or end of string
        if (comma1 < 1 || comma1 >= value.length()-1) {return null;}
        //find the second comma character
        int comma2 = value.indexOf(",", comma1+1);
        //return null if not found, or found immediately after the first
        //comma, or at end of string
        if (comma2 == -1 || comma2 == comma1+1 ||
        comma2 >= value.length()-1) {return null;}
        
        //extract the fields
        String name = value.substring(0,comma1);
        String typeString = value.substring(comma1+1,comma2);
        String sizeString = value.substring(comma2+1,value.length());
        //parse the strings
        int type = Font.PLAIN;
        if (typeString.equalsIgnoreCase("bold")) {
            type = Font.BOLD;
        } else if (typeString.equalsIgnoreCase("italic")) {
            type = Font.ITALIC;
        } else if (typeString.equalsIgnoreCase("bold|italic")) {
            type = Font.BOLD|Font.ITALIC;
        } else if (typeString.equalsIgnoreCase("italic|bold")) {
            type = Font.ITALIC|Font.BOLD;//presumably the same as above
        }
        int size = 0;
        try {
            size = Integer.parseInt(sizeString);
        } catch (NumberFormatException e) {
            return null;
        }
        Font f = new Font(name, type, size);
        return f;
    }
}

