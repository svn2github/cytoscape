package eqb;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import antlr.CommonAST;

class ParseTest
{
    private static Logger logger = Logger.getLogger(ParseTest.class.getName());

    /**
     * Expect a file format:
     * NODE (NODE, VALUE)*
     */
    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            System.err.println("missing input file");
            System.exit(1);
        }
        
        try
        {
            EqtlLexer lexer = new EqtlLexer(new DataInputStream(new FileInputStream(args[0])));
            EqtlParser parser = new EqtlParser(lexer);
            Map data = parser.parseEqtl();

            System.out.println("Parsed " + data.size() + " eqtl");
            
            printEqtls(data);
        }
        catch(Exception e)
        {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }
    }

    private static void printEqtls(Map m)
    {
        for(Iterator it = m.keySet().iterator(); it.hasNext();)
        {
            Eqtl e = (Eqtl) m.get(it.next());

            System.err.println(e);
        }
    }
}

