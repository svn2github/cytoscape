package netan;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

import java.util.logging.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;

import fgraph.CytoscapeExpressionData;
import fgraph.ExpressionDataIF;
import fgraph.BadInputException;

import netan.parse.*;

public class DNADamage
{
    private static Logger logger = Logger.getLogger(DNADamage.class.getName());
    
    private static String[] inStudy = new String[] {"YLR131C", // ACE2
                                                    "YDR216W", // ADR1
                                                    "YDR423C", // CAD1
                                                    "YOR028C", // CIN5
                                                    "YPL049C", // DIG1
                                                    "YNL068C", // FKH2
                                                    "YGL254W", // FZF1
                                                    "YEL009C", // GCN4
                                                    "YGL073W", // HSF1
                                                    "YOL108C", // INO4
                                                    "YKL032C", // IXR1
                                                    "YMR043W", // MCM1
                                                    "YKL062W", // MSN4
                                                    "YOR372C", // NDD1
                                                    "YGL013C", // PDR1
                                                    "YNL216W", // RAP1
                                                    "YLR176C", // RFX1
                                                    "YHL027W", // RIM101
                                                    "YMR016C", // SOK2
                                                    "YER111C", // SWI4
                                                    "YDR146C", // SWI5
                                                    "YLR182W", // SWI6
                                                    "YML007W", // YAP1
                                                    "YIR018W", // YAP5
                                                    "YKL185W", // ASH1
                                                    "YIR023W", // DAL81
                                                    "YNL167C", // SKO1
                                                    "YDL170W", // UGA3
                                                    "YDL020C"}; // RPN4
    
    
    static String BUF_DIR = "/cellar/users/cmak/data/buffering/";
    static String LOC_DIR = "/cellar/users/cmak/data/location/";
    static String FISHER_COMBINED = BUF_DIR + "FisherCombinedPvalues_adjusted.logratios.pvalues";

    static String BUF_DATA = BUF_DIR + "TF_KOs_orf.logratios.pscores4.tab2";

    //static String SIF_DATA = LOC_DIR + "all+met-p0.02-27Feb05.sif";
    static String SIF_DATA_MMS = LOC_DIR + "plusMMS-27Feb05-p0.02.sif";
    static String SIF_DATA_YPD = LOC_DIR + "minusMMS-27Feb05-p0.02.sif";
    static String SIF_DATA = SIF_DATA_YPD;

    static double DEFAULT_EXP_VAL = 0.0001;
    static double DEFAULT_BUF_VAL = 0.78;

    private BioGraph _g;

    private Set _edgesToPrint;

    private Set _tfInStudy;
    
    public DNADamage() throws IOException, BadInputException
    {
        _g = readGraph();
        _edgesToPrint = new HashSet();
        HashSet s = new HashSet();
        s.addAll(Arrays.asList(inStudy));
        _tfInStudy = Collections.unmodifiableSet(s);
    }

    /**
     * Read Wild-type expression changes in response to DNA damage
     *
     * @return a set of gene names
     */
    public Set readWTExpression(double exp_cutoff)
    {
        ExpressionDataIF wtExpression =
            CytoscapeExpressionData.load(FISHER_COMBINED, exp_cutoff);
       
        String[] genes = wtExpression.getGeneNames();
        String wt = "WT";
        
        Set diffExp = new HashSet();
        for(int x=0; x < genes.length; x++)
        {
            if(wtExpression.isPvalueBelowThreshold(wt, genes[x]))
            {
                diffExp.add(genes[x]);
            }
        }


        return diffExp;
    }

    public Map readBuffering(double buf_cutoff)
    {
        ExpressionDataIF buf =
            CytoscapeExpressionData.load(BUF_DATA, buf_cutoff);
       
        String[] genes = buf.getGeneNames();
        String[] kos = buf.getConditionNames();
        

        Map buffered = new HashMap();

        for(int k=0; k < kos.length; k++)
        {
            if(!kos[k].equals("WT_MAY2004"))
            {
                Set targets = new HashSet();
                buffered.put(kos[k], targets);
                
                for(int x=0; x < genes.length; x++)
                {
                    if(buf.isPvalueBelowThreshold(kos[k], genes[x]))
                    {
                        targets.add(genes[x]);
                    }
                }
            }
        }

        return buffered;
    }
    

    public BioGraph readGraph() throws IOException, BadInputException
    {
        return new BioGraph(SIF_DATA);
    }

    private int countBound(Set genes)
    {
        int unique = 0;
        int numEffects = 0;
        
        boolean incBB = false;
        for(Iterator it=genes.iterator(); it.hasNext();)
        {
            String bGene = (String) it.next();

            Set tfs = _g.incomingNeighbors(EdgeType.PD, bGene); 
            Set intersect = SetUtils.intersect(tfs, _tfInStudy);

            numEffects += intersect.size();
            if(intersect.size() > 0)
            {
                unique += 1;
            }
        }
        
        logger.info("Binding found for: " + unique + " of " + genes.size());
        logger.info("TF-Gene pairs = " + numEffects);

        return unique;
    }

    /**
     * Count genes that are bound and buffered by the SAME tf.
     */
    private Set countBoundAndBuffered(Map bufData)
    {
        int sum = 0;

        Set all = new HashSet();
        for(Iterator it=_tfInStudy.iterator(); it.hasNext();)
        {
            String tf = (String) it.next();

            if(bufData.containsKey(tf))
            {
                Set buffered = (Set) bufData.get(tf);

                Set bound = _g.outgoingNeighbors(EdgeType.PD, tf); 

                StringBuffer b = new StringBuffer();
                b.append(GeneNameMap.getName(tf));
                b.append(" (" + tf + ") ");
                b.append(" binds and buffers targets: ");

                int Nbb = 0;
                for(Iterator t=bound.iterator(); t.hasNext();)
                {
                    String bb = (String) t.next();
                    
                    if(buffered.contains(bb))
                    {
                        all.add(makeEdge(tf, bb, EdgeType.PD));
                        recordEdge(tf, bb, EdgeType.PD);
                        b.append(GeneNameMap.getName(bb) + " ");
                        Nbb++;
                    }
                }
                
                b.append("(" + Nbb + ")");
                logger.info(b.toString());
            }
        }
        
        logger.info("Total buf and bound by same TF: " + all.size());

        return all;
    }

    /**
     *
     * @param buffered Buffering data
     * @return the set of genes that are buffered by one or more TFs
     */
    private Set countBuffered(Map buffered)
    {
   
        Set union = new HashSet();
        int sum = 0;
        for(Iterator kos=buffered.entrySet().iterator(); kos.hasNext();)
        {
            Map.Entry e = (Map.Entry) kos.next();
            String ko = (String) e.getKey();

            Set targets = (Set) e.getValue();

            union.addAll(targets);
            sum += targets.size();
            
            logger.info(GeneNameMap.getName(ko) +
                        " buffers " + targets.size());
        }
        
        logger.info("Total buffered genes: " + union.size());
        logger.info("Total buffering effects: " + sum);

        return union;
    }

    /**
     * Count the number of genes that are
     *
     * @param
     * @return a set of 2-element string arrays.
     * Where each element of the array is the name of a node
     * that is a vertext of the triangle.
     */
    private Set countTriangles(Set kos, Set candidateGenes,
                               String edgeFilter,
                               boolean directedFilter)
    {
        Set triangles = new HashSet();

        Set unique = new HashSet();
        
        for(Iterator kit = kos.iterator(); kit.hasNext();)
        {
            String ko = (String) kit.next();

            Set pdNeighbors = _g.outgoingNeighbors(EdgeType.PD, ko);

            Set pdCandidates = SetUtils.intersect(pdNeighbors, candidateGenes);

            String[] a = (String[]) pdCandidates.toArray(new String[0]);

            for(int x=0; x < a.length; x++)
            {
                for(int y=0; y < a.length; y++)
                {
                    if(x != y)
                    {
                        if(_g.edgeExists(a[x], a[y], edgeFilter))
                        {
                            if(!unique.contains(a[x] + a[y]))
                            {
                                triangles.add(new String[] {a[x], a[y]});
                                unique.add(a[x] + a[y]);

                                if(!directedFilter)
                                {
                                    unique.add(a[y] + a[x]);
                                }
                                
                                recordEdge(ko, a[x], EdgeType.PD);
                                recordEdge(ko, a[y], EdgeType.PD);
                                recordEdge(a[x], a[y], edgeFilter);
                                
                            }
                        }
                    }
                }
            }
        }

        /*
        for(Iterator it = triangles.iterator(); it.hasNext();)
        {
            String[] edge = (String[]) it.next();
            System.out.println(edge[0] + " " + edgeFilter + " " + edge[1]);
        }
        */

        return triangles;
    }


    private void recordEdge(String s, String t, String type)
    {
        _edgesToPrint.add(makeEdge(s, t, type));
    }

    private String[] makeEdge(String s, String t, String type)
    {
        return new String[] {s, t, type};
    }

    
    private void printEdges(PrintStream out)
    {
        printEdges(out, _edgesToPrint.iterator());
    }

    private void printEdges(PrintStream out, Iterator edges)
    {
        while( edges.hasNext() )
        {
            String[] e = (String[]) edges.next();

            out.println(e[0] + " " + e[2] + " " + e[1]);
        }
    }

    

    private void run(String outFile, double[] bufVals) throws Exception
    {
        logger.info("Reading expression data using p=" + DEFAULT_EXP_VAL);

        Set diffExpressed = readWTExpression(DEFAULT_EXP_VAL);

        logger.info("Diff Exp: " + diffExpressed.size()
                    + " genes at p < " + DEFAULT_EXP_VAL);

        PrintStream stats =
            new PrintStream(new FileOutputStream(outFile
                                                   + "-stats.csv"));

        stats.println(SIF_DATA);
        stats.println(BUF_DATA);
        stats.println(FISHER_COMBINED);
        stats.println(join(new String[] {"E-thr",
                                         "B-thr",
                                         "# PDedge",
                                         "# buf",
                                         "# buf^in-graph",
                                         "# DE",
                                         "# DE^Bound-either",
                                         "# Buf^Bound-either",
                                         "# pp-tri",
                                         "# rr-tri",
                                         "# explained"},
                                "\t"));

        for(int x=0; x < bufVals.length; x++)
        {
            double bufScore = bufVals[x];
            
            logger.info("Reading buffering data using score=" + bufScore);
            Map buffered = readBuffering(bufScore);
            Set kos = buffered.keySet();
            Set bufGenes = countBuffered(buffered);
            
            int numDiffExp = diffExpressed.size();
            int numBuffered = bufGenes.size();
            int bufAndInGraph = SetUtils.intersect(bufGenes, _g.nodeNames()).size();
            int pdEdges = _g.getEdgeCount(EdgeType.PD);

            Set BB = countBoundAndBuffered(buffered);
            int bufAndBound = BB.size();
            int deAndBound = countBound(diffExpressed);
            //int deAndBound = 0;

            Set ppTriangles = countTriangles(kos, diffExpressed, EdgeType.PP,
                                           false);

            Set rrTriangles = countTriangles(kos, diffExpressed, EdgeType.RR,
                                           false);

            int ppTriangle = countUniqueNodes(ppTriangles);
            int rrTriangle = countUniqueNodes(rrTriangles);

            Set explained = new HashSet();
            explained.addAll(BB);
            explained.addAll(ppTriangles);
            explained.addAll(rrTriangles);
            
            
            stats.println(join(new String[] {String.valueOf(DEFAULT_EXP_VAL),
                                             String.valueOf(bufScore),
                                             String.valueOf(pdEdges),
                                             String.valueOf(numBuffered),
                                             String.valueOf(bufAndInGraph),
                                             String.valueOf(numDiffExp),
                                             String.valueOf(deAndBound),
                                             String.valueOf(bufAndBound),
                                             String.valueOf(ppTriangle),
                                             String.valueOf(rrTriangle),
                                             String.valueOf(explained.size())},
                                    "\t"));
            
            
            PrintStream out = System.out;
            if(outFile != null)
            {
                out = new PrintStream(new FileOutputStream(outFile
                                                           + "-"
                                                           + bufScore));
            }
            
            printEdges(out, BB.iterator());
        }
    }

    private int countUniqueNodes(Set triangles)
    {
        Set u = new HashSet();
        for(Iterator it = triangles.iterator(); it.hasNext();)
        {
            String[] s = (String[]) it.next();
            u.add(s[0]);
            u.add(s[1]);
        }

        return u.size();
    }
    
    private String join(String[] s, String glue)
    {
        StringBuffer b = new StringBuffer();

        for(int x=0; x < s.length; x++)
        {
            b.append(s[x]);
            if(x < (s.length - 1))
            {
                b.append(glue);
            }
        }

        return b.toString();
     
    }
    
    public static void main(String[] args) throws Exception
    {
        DNADamage dd = new DNADamage();

        String outFile = null;

        if(args.length > 0)
        {
            outFile = args[0];
        }
        
        //dd.run(outFile, new double[] {0.01, 0.1, 0.5, 0.78});
        dd.run(outFile, new double[] {0.78});
    }
}
