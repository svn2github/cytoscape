package netan;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EdgeType
{
    public static String PD = "pd";
    public static String PP = "pp";
    
    // pattern for matching directed edges.
    private static Pattern directedPattern =
        Pattern.compile("ypd|mms|pd", Pattern.CASE_INSENSITIVE);

    /**
     * @return true if an edge of type "type" is directed, false otherwise
     */
    public static boolean isDirected(String type)
    {
        return directedPattern.matcher(type).matches();
    }
}
