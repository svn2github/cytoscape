package netan;

import netan.parse.NodeAttrLexer;
import netan.parse.NodeAttrParser;

import fgraph.BadInputException;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import java.util.logging.Logger;

import antlr.ANTLRException;

public class GeneNameMap
{
    private static Logger logger = Logger.getLogger(GeneNameMap.class.getName());

    private static String data = "orf2name.noa";

    private static Map orf2nameMap;
    
    static
    {
        orf2nameMap = new HashMap();

        try
        {
            NodeAttrLexer lexer = new NodeAttrLexer(new FileInputStream(data));
            NodeAttrParser parser = new NodeAttrParser(lexer);

            // Expect a list of 2-element String arrays
            // a[0]: node
            // a[1]: attribute value

            List d = parser.parse();
            
            logger.info("Parsed " + d.size() + " data records from "
                        + data);
                        
            for(int x=0; x < d.size(); x++)
            {
                String[] t = (String[]) d.get(x);
                orf2nameMap.put(t[0], t[1]);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ANTLRException e)
        {
            e.printStackTrace();
        }
    }

    public static String getName(String orf)
    {
        if(orf2nameMap.containsKey(orf))
        {
            return (String) orf2nameMap.get(orf);
        }

        return orf;
    }
}
