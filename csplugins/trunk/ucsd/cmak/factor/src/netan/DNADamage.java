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
    
    private static String[] inStudy = new String[] {"YLR131C",
                                                    "YKL185W",
                                                    "YGL254W",
                                                    "YOL108C",
                                                    "YDL020C",
                                                    "YDR146C",
                                                    "YDL170W",
                                                    "YDR423C",
                                                    "YOR028C",
                                                    "YIR023W",
                                                    "YPL049C",
                                                    "YLR228C",
                                                    "YNL068C",
                                                    "YEL009C",
                                                    "YKL032C",
                                                    "YKL062W",
                                                    "YGL013C",
                                                    "YLR176C",
                                                    "YHL027W",
                                                    "YBL103C",
                                                    "YNL167C",
                                                    "YMR016C",
                                                    "YER111C",
                                                    "YLR182W",
                                                    "YML007W",
                                                    "YIR018W"};

    
    static String BUF_DIR = "/cellar/users/cmak/data/buffering/";
    static String LOC_DIR = "/cellar/users/cmak/data/location/";
    static String FISHER_COMBINED = BUF_DIR + "FisherCombinedPvalues_adjusted.logratios.pvalues";

    static String BUF_DATA = BUF_DIR + "TF_KOs_orf.logratios.pscores4.tab2";

    static String SIF_DATA = LOC_DIR + "all-p0.02-27Feb05.sif";
    
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

    private int countBound(Set buffered)
    {
        int BB = 0;
        int numBufEffects = 0;
        
        boolean incBB = false;
        for(Iterator it=buffered.iterator(); it.hasNext();)
        {
            String bGene = (String) it.next();

            Set tfs = _g.incomingNeighbors(EdgeType.PD, bGene); 
            numBufEffects += tfs.size();

            StringBuffer b = new StringBuffer();
            b.append(GeneNameMap.getName(bGene));
            b.append(" (" + bGene + ") ");
            b.append(" is bound by " + b + " TF: ");

            incBB = false;
            for(Iterator t=tfs.iterator(); t.hasNext();)
            {
                String tf = (String) t.next();

                if(_tfInStudy.contains(tf))
                {
                    recordEdge(tf, bGene, EdgeType.PD);
                    incBB = true;
                    
                    b.append(GeneNameMap.getName(tf) + " ");
                }
            }

            // increment the number of Buffered and Bound
            // if at least one TF in the study binds the buffered gene
            if(incBB)
            {
                BB += 1;
            }
            
            logger.info(b.toString());
        }
        
        logger.info("Buf and bound = " + BB + " of " + buffered.size());
        logger.info("TF-BufGene pairs = " + numBufEffects);

        return BB;
    }

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
                        "buffers numTargets= " + targets.size());
        }
        
        logger.info("Total buffered genes: " + union.size());
        logger.info("Total buffering effects: " + sum);

        return union;
    }

    private int countTriangles(Set kos, Set candidateGenes, String edgeFilter,
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

        return triangles.size();
    }


    private void recordEdge(String s, String t, String type)
    {
        _edgesToPrint.add(new String[] {s, t, type});
    }

    private void printEdges(PrintStream out)
    {
        for(Iterator it = _edgesToPrint.iterator(); it.hasNext();)
        {
            String[] e = (String[]) it.next();

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
                                              "# DE",
                                              "# DE^Bound-either",
                                              "# Buf^Bound-either",
                                              "# pp-tri"},
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
            int pdEdges = _g.getEdgeCount(EdgeType.PD);
            int bufAndBound = countBound(bufGenes);
            int deAndBound = countBound(diffExpressed);
            int ppTriangle = countTriangles(kos, diffExpressed, EdgeType.PP,
                                            false);
            
            stats.println(join(new String[] {String.valueOf(DEFAULT_EXP_VAL),
                                                  String.valueOf(bufScore),
                                                  String.valueOf(pdEdges),
                                                  String.valueOf(numBuffered),
                                                  String.valueOf(numDiffExp),
                                                  String.valueOf(deAndBound),
                                                  String.valueOf(bufAndBound),
                                                  String.valueOf(ppTriangle)},
                                    "\t"));
            
            
            PrintStream out = System.out;
            if(outFile != null)
            {
                out = new PrintStream(new FileOutputStream(outFile
                                                           + "-"
                                                           + bufScore));
            }
            
            printEdges(out);
        }
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
        
        dd.run(outFile, new double[] {0.01, 0.1, 0.5, 0.78});
    }
}
