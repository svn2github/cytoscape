import java.io.*;
import java.util.*;
import java.util.logging.*;

import antlr.CommonAST;

import cern.colt.map.*;
import cern.colt.list.*;

class ParseMain
{
    private static Logger logger = Logger.getLogger(ParseMain.class.getName());

    /**
     *  @param an list containing size 2 arrays of LinkedHashMaps.
     * In each array:
     * <p>
     * The first map contains the variable2factor messages in the same order
     * that they appear in the input file.
     * <p>
     * The second map contains the factor2variable messages in the
     * same order that they appear in the input file.
     * <p>
     * For each map the map key is a String of the "from"
     * node concatenated with the "to" node for the message.
     * This allows you to associate var2fac and fac2var messages sent between
     * pairs of nodes.
     */
    protected static List processMessages(MessageBlock maps)
    {
        List edgeMessages = new ArrayList();
        // convert data into EdgeMessages

        LinkedHashMap vMap = maps.getV2f(); // var2factor messages
        LinkedHashMap fMap = maps.getF2v(); // factor2var messages
        
        if(vMap.size() != fMap.size())
        {
            logger.warning("different # of incoming and outgoing messages. #v="
                           + vMap.size() + " f=" + fMap.size());
            return new ArrayList();
        }
        
        for(Iterator it = vMap.values().iterator(); it.hasNext();)
        {
            TestMessage v2f = (TestMessage) it.next();
            String key = v2f.getTo() + v2f.getFrom();
            TestMessage f2v = (TestMessage) fMap.get(key);
            if(f2v == null)
            {
                logger.warning("no f2v message found for: " + key);
                continue;
            }
            
            logger.fine(" adding: " + v2f);
            logger.fine(" adding: " + f2v);
            
            EdgeMessage em;
            
            if(v2f.getDir() != null)
            {
                em = new EdgeMessage(v2f.getType(),
                                     nodeId(v2f.getFrom()),
                                     nodeId(v2f.getTo()),
                                     v2f.getDir());
            }
            else
            {
                em = new EdgeMessage(v2f.getType(),
                                     nodeId(v2f.getFrom()),
                                     nodeId(v2f.getTo()));
            }
            em.v2f(v2f.getProbTable());
            em.f2v(f2v.getProbTable());
            
            edgeMessages.add(em);
        }
        
        logger.fine("### edge messages size: " + edgeMessages.size());

        return edgeMessages;
        
    }

    protected static int nodeId(String s)
    {
        if(s.startsWith("-"))
        {
            return Integer.parseInt(s.substring(1));
        }
        else
        {
            return Integer.parseInt(s);
        }
    }
    
    private static class Failure
    {
        int index;
        ProbTable computed;
        ProbTable expected;
        boolean sameMax;

        Failure(int i, ProbTable c, ProbTable e, boolean sameMax)
        {
            index = i;
            computed = c;
            expected = e;
            this.sameMax = sameMax;
        }
    }
    
    /**
     * Verify the factor to variable messages using the MaxProduct algorithm
     * @param edgeMessages a list of EdgeMessage objects
     */
    protected static void testMaxProduct(FactorNode factor, List edgeMessages)
    {
        OpenIntObjectHashMap failures = new OpenIntObjectHashMap();
        int pass = 0;
        int fail = 0;
        int diffmax = 0;
        for(int x=0; x < edgeMessages.size() ;x++)
        {
            try
            {
                EdgeMessage em = (EdgeMessage) edgeMessages.get(x);
                
                ProbTable pt = factor.maxProduct(edgeMessages, x);
                ProbTable expected = em.f2v();
                logger.fine("   maxProduct: " + pt);
                
                if(pt.equals(expected, 1e-4))
                {
                    pass++;
                    logger.fine(" OK");
                }
                else
                {
                    fail++;
                    // b = do the prob tables have the same max?
                    boolean b = (pt.hasUniqueMax() == expected.hasUniqueMax());
                    b = b && pt.hasUniqueMax()
                        && (pt.maxState() == expected.maxState());

                    if(!b)
                    {
                        diffmax++;
                    }
                    
                    failures.put(x, new Failure(x, pt, expected, b));

                    logger.fine(" FAIL");
                }
            }
            catch(AlgorithmException e)
            {
                logger.warning("### AlgorithmException");
                e.printStackTrace();
            }
        }

        logger.info(" ");
        logger.info(pass + " passed. " + fail + " failed. "
                    + diffmax + " had different max.");
        
        if(fail > 0)
        {

            for(int x=0; x < edgeMessages.size() ;x++)
            {
                EdgeMessage em = (EdgeMessage) edgeMessages.get(x);

                if(failures.containsKey(x))
                {
                    Failure f = (Failure) failures.get(x);
                    logger.info(em.toString()
                                + " FAIL f2v=" + f.computed
                                + " SAME_MAX=" + f.sameMax);
                }
                else
                {
                    logger.info(em.toString());
                }
            }
            logger.info(" ");
        }
    }
    

    /**
     * Expect a file format:
     * ( node path_factor {
     *   v2f ...
     *   v2f ...
     *   f2v ...
     *   f2v...
     *  }
     * )+
     *
     * Where each block of v2f and f2v messages correspond to
     * the messages sent to and from a single path_factor node.
     */
    public static void main(String[] args) {
        try {
            L lexer = new L(new DataInputStream(System.in));
            P parser = new P(lexer);
            List data = parser.parseMessages();

            List emList = new ArrayList();
            List typeList = new ArrayList();
            for(int x=0; x < data.size(); x++)
            {
                emList.add(processMessages((MessageBlock) data.get(x)));
                typeList.add(((MessageBlock) data.get(x)).getType());
            }

            for(int x=0; x < emList.size(); x++)
            {
                FactorNode fn;
                NodeType type = (NodeType) typeList.get(x);
                if(type == NodeType.PATH_FACTOR)
                {
                    fn = PathFactorNode.getInstance();
                }
                else
                {
                    fn = OrFactorNode.getInstance();
                }

                logger.info("### " + x + " testing edge msg list");
                testMaxProduct(fn, (List) emList.get(x));
            }
        } catch(Exception e) {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }
    }
}

