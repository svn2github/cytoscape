import junit.framework.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

import java.util.*;

public class fnTest extends AbstractNodeTest
{
    private double ep1 = PathFactorNode.ep1;
    private double ep2 = PathFactorNode.ep2;

    private static final String S = "^(\\d+)\\s+";
    private static final String P = "\\s+(0?\\.\\d+)";
    private static final String E = "\\s*$";
    private static final String D = "\\s+(\\Q+1\\E|\\Q-1\\E)";


    private static boolean debug = false;
    
    private String file;
    
    PathFactorNode f;

    List messages;
    List tests;

    Map targetMap;

    static List errors;
    static List failures;
    
    public fnTest(String file, String s)
    {
        super(s);
        this.file = file;
        targetMap = new HashMap();
    }
    
    public static void main(String[] args)
    {
        if(args.length > 1) debug = true;

        errors = new ArrayList();
        failures = new ArrayList();
        
        long start = System.currentTimeMillis();
        fnTest c = new fnTest(args[0], "testAll");
        TestResult r = c.run();

        for(int x=0; x < errors.size(); x++)
        {
            r.addError(c, (Throwable) errors.get(x));
        }

        for(int x=0; x < failures.size(); x++)
        {
            r.addFailure(c, (AssertionFailedError) failures.get(x));
        }

        
        System.out.println("\n" + c.tests.size()
                           + " tests. Errors=" + errors.size()
                           + " Failures=" + failures.size());
        //printFailures(r.errors());
        //printFailures(r.failures());
    }

    protected static void printFailures(Enumeration e)
    {
        while(e.hasMoreElements())
        {
            System.out.println(((TestFailure) e.nextElement()).trace());
        }
    }

    public void testAll() throws Exception
    {
        printList(messages);
        printList(tests);
        
        for(int x=0; x < tests.size(); x++)
        {
            Test t = (Test) tests.get(x);

            System.out.println(">>> Running test: " + t);
            
            VariableNode n = (VariableNode) targetMap.get(t.index);
            ProbTable pt = f.maxProduct(messages, t.index.intValue(), n);

            try
            {
                t.checkResult(pt);
                System.out.println(">>> ok\n");
            }
            catch(AssertionFailedError f)
            {
                failures.add(f);
                failed(t.type, t, pt);
            }
            catch(Throwable e)
            {
                errors.add(e);
                failed(t.type, t, pt);
            }


        }
    }

    private void failed(String name, Test t, ProbTable results)
    {
        StringBuffer b = new StringBuffer("### failed ");
        b.append(name);
        b.append(" ");
        b.append(t.toString());
        b.append("\n   ");
        b.append(results.toString());
        b.append("\n");
        
        System.out.println(b.toString());
        
    }

    /**
     * Parse the input file.  Creating EdgeMessages and Test.
     */
    protected void setUp() throws Exception
    {
        f = PathFactorNode.getInstance();

        BufferedReader in = new BufferedReader(new FileReader(file));

        messages = new ArrayList();
        tests = new ArrayList();

        List matchers = new ArrayList();

        matchers.add(new EdgeMatcher());
        matchers.add(new DirMatcher());
        matchers.add(new SignMatcher());
        matchers.add(new PathMatcher());
        matchers.add(new KOMatcher());
        matchers.add(new OneZeroMatcher());
        matchers.add(new PMMatcher());
        matchers.add(new PMZMatcher());
        
        String line;
                
        while((line = in.readLine()) != null)
        {
            for(int m=0; m < matchers.size(); m++)
            {
                LineMatcher lm = (LineMatcher) matchers.get(m);
                if(lm.match(line))
                {
                    break;
                }
            }
        }

    }

    abstract class LineMatcher
    {
        Matcher m;

        LineMatcher(String regex)
        {
            m = Pattern.compile(regex).matcher("");
        }
        
        boolean match(String s)
        {
            boolean ok = m.reset(s).lookingAt();

            if(ok)
            {
                debugMatch();
                process();
            }

            return ok;
        }

        protected void debugMatch()
        {
            if(!debug) return;
            
            StringBuffer b = new StringBuffer(m.group(1));
            for(int x=1; x <= m.groupCount(); x++)
            {
                b.append(" g");
                b.append(x);
                b.append("=");
                b.append(m.group(x));
            }
            
            System.out.println(b.toString());
        }
                           
        
        abstract void process();
    }

    class EdgeMatcher extends LineMatcher
    {
        EdgeMatcher() {  super(S+"(x)" + P + P + E);  }

        void process()
        {
            Integer i = Integer.valueOf(m.group(1));
            targetMap.put(i, VariableNode.createEdge(i.intValue()));
            
            messages.add(pt2em(createEdge( Double.parseDouble(m.group(3)),
                                           Double.parseDouble(m.group(4))))
                         );
        }
    }

    class DirMatcher extends LineMatcher
    {
        DirMatcher()  { super(S+"(d)" + P + P + D + E); }

        void process()
        {
            Integer i = Integer.valueOf(m.group(1));
            targetMap.put(i, VariableNode.createDirection(i.intValue()));
            
            State st = m.group(5).equals("+1") ? State.PLUS : State.MINUS;
            
            messages.add(pt2em(createDir( Double.parseDouble(m.group(3)),
                                          Double.parseDouble(m.group(4))),
                               st)
                         );
        }
    }

    class SignMatcher extends LineMatcher
    {
        SignMatcher()  { super(S+"(s)" + P + P + E); }

        void process()
        {
            Integer i = Integer.valueOf(m.group(1));
            targetMap.put(i, VariableNode.createSign(i.intValue()));
            
            messages.add(pt2em(createSign( Double.parseDouble(m.group(3)),
                                    Double.parseDouble(m.group(4))))
                      );
        }
    }
    

    class PathMatcher extends LineMatcher
    {
       PathMatcher()  { super(S+"(p)" + P + P + E); }

        void process()
        {
            Integer i = Integer.valueOf(m.group(1));
            targetMap.put(i, VariableNode.createPathActive(i.intValue()));
            
            messages.add(pt2em(createPathActive( Double.parseDouble(m.group(3)),
                                                 Double.parseDouble(m.group(4))))
                         );
        }
    }

    
    class KOMatcher extends LineMatcher
    {
        KOMatcher()  { super(S+"(k)" + P + P + P + E); }

        void process()
        {
            Integer i = Integer.valueOf(m.group(1));
            targetMap.put(i, VariableNode.createKO(1, i.intValue()));
            
            messages.add(pt2em(createKO( Double.parseDouble(m.group(3)),
                                         Double.parseDouble(m.group(4)),
                                         Double.parseDouble(m.group(5))))
                         );
        }
    }

    class OneZeroMatcher extends LineMatcher
    {
        OneZeroMatcher()  { super("^(tx|tp)\\s+(\\d+)" + P + P + E); }

        void process()
        {
            tests.add(new OneZeroTest(m.group(1),
                                      new Integer(m.group(2)),
                                      new double[] {
                                          Double.parseDouble(m.group(3)),
                                          Double.parseDouble(m.group(4))}
                                      )
                      );
        }
    }

    
    class PMMatcher extends LineMatcher
    {
        PMMatcher()  { super("^(td|ts)\\s+(\\d+)" + P + P + E); }

        void process()
        {
            tests.add(new PMTest(m.group(1),
                                 new Integer(m.group(2)),
                                 new double[] {
                                     Double.parseDouble(m.group(3)),
                                     Double.parseDouble(m.group(4))}
                                 )
                      );
        }
    }

    
    class PMZMatcher extends LineMatcher
    {
        PMZMatcher()  { super("^(tk)\\s+(\\d+)" + P + P + P + E); }

        void process()
        {
            tests.add(new KOTest(m.group(1),
                                 new Integer(m.group(2)),
                                 new double[] {
                                     Double.parseDouble(m.group(3)),
                                     Double.parseDouble(m.group(4)),
                                     Double.parseDouble(m.group(5))}
                                 )
                        );
        }
    }

    
    abstract class Test
    {
        Integer index;
        double[] probs;
        String type;
        
        Test(String type, Integer i, double[] pr)
        {
            this.type = type;
            index = i;
            probs = pr;
        }

        abstract void checkResult(ProbTable pt);
        
        public String toString()
        {
            StringBuffer b = new StringBuffer(type);
            b.append(" index[");
            b.append(index);
            b.append("] ");

            for(int x=0; x < probs.length; x++)
            {
                b.append(probs[x]);
                b.append(" ");
            }

            return b.toString();
        }
    }

    class KOTest extends Test
    {
        KOTest(String type, Integer i, double[] pr)
        {
            super(type, i, pr);
        }
        
        void checkResult(ProbTable pt)
        {
            checkProbsPMZ(pt, probs[0], probs[1], probs[2]);
        }
    }

    class PMTest extends Test
    {
        PMTest(String type, Integer i, double[] pr)
        {
            super(type, i, pr);
        }

        void checkResult(ProbTable pt)
        {
            checkProbsPM(pt, probs[0], probs[1]);
        }
    }

    class OneZeroTest extends Test
    {
        OneZeroTest(String type, Integer i, double[] pr)
        {
            super(type, i, pr);
        }

        void checkResult(ProbTable pt)
        {
            checkProbs10(pt, probs[0], probs[1]);
        }
    }

    
    
    private void printList(List l)
    {
        System.out.println(l);
    }
}
