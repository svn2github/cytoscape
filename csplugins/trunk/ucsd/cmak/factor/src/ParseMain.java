import java.io.*;
import java.util.*;
import antlr.CommonAST;

import cern.colt.map.*;
import cern.colt.list.*;

class ParseMain {
    public static void main(String[] args) {
        try {
            L lexer = new L(new DataInputStream(System.in));
            P parser = new P(lexer);
            OpenIntObjectHashMap[] maps = parser.parseMessages();
            OpenIntObjectHashMap mMap = maps[0];
            OpenIntObjectHashMap rMap = maps[1];
            IntArrayList km = mMap.keys();
            IntArrayList kr = rMap.keys();

            List l = new ArrayList();
            if(km.size() != kr.size())
            {

            }
            else
            {

                for(int x=0; x < km.size(); x++)
                {
                    TestMessage m = (TestMessage) mMap.get(km.get(x));
                    TestMessage r = (TestMessage) rMap.get(km.get(x));

                    EdgeMessage em;

                    if(m.getDir() != null)
                    {
                        em = new EdgeMessage(m.getType(), 0, 0, m.getDir());
                    }
                    else
                    {
                        em = new EdgeMessage(m.getType(), 0, 0);
                    }
                    em.v2f(m.getProbTable());
                    em.f2v(r.getProbTable());

                    l.add(em);
                }
            }

            PathFactorNode pf = PathFactorNode.getInstance();

            for(int x=0; x < l.size() ;x++)
            {
                System.out.println(l.get(x));
                EdgeMessage em = (EdgeMessage) l.get(x);

                ProbTable pt = pf.maxProduct(l, x);

                System.out.print("   maxProduct: " + pt);

                if(pt.equals(em.f2v(), 1e-5))
                {
                    System.out.println(" OK");
                }
                else
                {
                    System.out.println(" FAIL");
                }
            }

        } catch(Exception e) {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }
    }
}

