import java.io.*;
import java.util.*;
import java.util.logging.*;

import antlr.CommonAST;

import cern.colt.map.*;
import cern.colt.list.*;

class ParseYeang extends ParseMain
{
    private static Logger logger = Logger.getLogger(ParseYeang.class.getName());

    // return a list of lists of edge messages
    protected static List processMessages(MessageBlock maps)
    {
        LinkedHashMap vMap = maps.getV2f(); // var2factor messages
        LinkedHashMap fMap = maps.getF2v(); // factor2var messages
        
        if(vMap.size() != fMap.size())
        {
            logger.warning("variable-to-factor and factor-to-variable maps are different sizes: v.size()="
                           + vMap.size()
                           + " f.size()=" + fMap.size());
            return new ArrayList();
        }
        
        List edgeMessages = new ArrayList();

        String curFactor = "";
        List curEm = new ArrayList();
        
        for(Iterator it = vMap.values().iterator(); it.hasNext(); )
        {
            TestMessage v2fm = (TestMessage) it.next();
            String factor = v2fm.getTo();

            if(!curFactor.equals(factor))
            {
                if(!curFactor.equals(""))
                {
                    edgeMessages.add(curEm);
                    logger.fine("adding em list size " + curEm.size()
                                + " for factor "
                                + curFactor);
                }
                curFactor = factor;
                curEm = new ArrayList();
            }
            
            logger.fine(v2fm.toString());
            
            EdgeMessage em = null;
            if(v2fm.getType() == NodeType.DIR)
            {
                em = new EdgeMessage(v2fm.getType(),
                                     nodeId(v2fm.getFrom()),
                                     nodeId(v2fm.getTo()),
                                     v2fm.getDir());
            }
            else
            {
                em = new EdgeMessage(v2fm.getType(),
                                     nodeId(v2fm.getFrom()),
                                     nodeId(v2fm.getTo()));
            }
            
            String key = v2fm.getTo() + v2fm.getFrom(); 
            TestMessage f2vm = (TestMessage) fMap.get(key);
            if(f2vm == null)
            {
                logger.warning("factor-to-var message for: " + key
                               + " is null");
                
            }
            em.v2f(v2fm.getProbTable());
            em.f2v(f2vm.getProbTable());
            
            logger.finer("   adding to em: " + key + " "
                        + v2fm.getProbTable());
            logger.finer("   adding to em: " + key + " "
                        + f2vm.getProbTable());
            
            curEm.add(em);
        }
        return edgeMessages;
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
     * the messages sent during one iteration of the MaxProduct
     * algorithm on a factor graph.  ie. processMessages must
     * group the messages into EdgeMessage lists for each factor node.
     */
    public static void main(String[] args) {
        try {
            L lexer = new L(new DataInputStream(System.in));
            P parser = new P(lexer);
            List data = parser.parseMessages();
            
            for(int y=0; y < data.size(); y++)
            {
                MessageBlock mb = (MessageBlock) data.get(y);
                List emList = processMessages(mb);
                NodeType type = (NodeType) mb.getType();

                FactorNode fn;
                if(type == NodeType.PATH_FACTOR)
                {
                    fn = PathFactorNode.getInstance();
                }
                else
                {
                    fn = OrFactorNode.getInstance();
                }
                
                for(int x=0; x < emList.size(); x++)
                {
                    logger.info("### loop=" + y
                                + ", em=" +x + " testing edge msg list");
                    testMaxProduct(fn, (List) emList.get(x));
                }
            }
        } catch(Exception e) {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }
    }
}

