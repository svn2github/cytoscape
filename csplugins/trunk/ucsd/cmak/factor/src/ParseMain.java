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
     * The second map contains the factor2variable messages in the se same order
     * that they appear in the input file.
     * <p>
     * For each map the map key is a String of the "from"
     * node concatenated with the "to" node for the message.
     * This allows you to associate var2fac and fac2var messages sent between
     * pairs of nodes.
     */
    protected static List processMessages(LinkedHashMap[] maps)
    {
        List edgeMessages = new ArrayList();
        // convert data into EdgeMessages

        LinkedHashMap vMap = maps[0]; // var2factor messages
        LinkedHashMap fMap = maps[1]; // factor2var messages
        
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
                em = new EdgeMessage(v2f.getType(), 0, 0, v2f.getDir());
            }
            else
            {
                em = new EdgeMessage(v2f.getType(), 0, 0);
            }
            em.v2f(v2f.getProbTable());
            em.f2v(f2v.getProbTable());
            
            edgeMessages.add(em);
        }
        
        logger.fine("### edge messages size: " + edgeMessages.size());

        return edgeMessages;
        
    }

    protected static void testMaxProduct(List edgeMessages)
    {
        PathFactorNode pf = PathFactorNode.getInstance();
        
        for(int x=0; x < edgeMessages.size() ;x++)
        {
            try
            {
                EdgeMessage em = (EdgeMessage) edgeMessages.get(x);
                
                ProbTable pt = pf.maxProduct(edgeMessages, x);
                
                logger.info("   maxProduct: " + pt);
                
                if(pt.equals(em.f2v(), 1e-5))
                {
                    logger.info(" OK");
                }
                else
                {
                    logger.info(" FAIL");
                }
            }
            catch(AlgorithmException e)
            {
                logger.warning("### AlgorithmException");
                e.printStackTrace();
            }
        }
    }
    
    
    public static void main(String[] args) {
        try {
            L lexer = new L(new DataInputStream(System.in));
            P parser = new P(lexer);
            List data = parser.parseMessages();

            List emList = new ArrayList();
            for(int x=0; x < data.size(); x++)
            {
                emList.add(processMessages((LinkedHashMap[]) data.get(x)));
            }

            for(int x=0; x < emList.size(); x++)
            {
                testMaxProduct((List) emList.get(x));
            }
        } catch(Exception e) {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }
    }
}

