package netan;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;

import fgraph.CytoscapeExpressionData;
import fgraph.ExpressionDataIF;
import fgraph.BadInputException;

import netan.parse.*;

public class DNADamage
{
    static String BUF_DIR = "/cellar/users/cmak/data/buffering/";
    static String LOC_DIR = "/cellar/users/cmak/data/location/";
    static String FISHER_COMBINED = BUF_DIR + "FisherCombinedPvalues_adjusted.logratios.pvalues";

    static String BUF_DATA = BUF_DIR + "TF_KOs_orf.logratios.pscores4.tab2";

    static String SIF_DATA = BUF_DIR + "all-p0.02-27Feb05.sif";
    
    static double EXP_VAL = 0.0001;
    static double BUF_VAL = 0.78;

    private BioGraph _g;

    private Set _edgesToPrint;
    
    public DNADamage() throws IOException, BadInputException
    {
        _g = readGraph();
        _edgesToPrint = new HashSet();
    }

    /**
     * Read Wild-type expression changes in response to DNA damage
     *
     * @return a set of gene names
     */
    public Set readWTExpression()
    {
        ExpressionDataIF wtExpression =
            CytoscapeExpressionData.load(FISHER_COMBINED, EXP_VAL);
       
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

    public Map readBuffering()
    {
        ExpressionDataIF buf =
            CytoscapeExpressionData.load(BUF_DATA, BUF_VAL);
       
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

    private void countBound(Set buffered)
    {
        int BB = 0;
        
        for(Iterator it=buffered.iterator(); it.hasNext();)
        {
            String node = (String) it.next();

            Set tfs = _g.incomingNeighbors(EdgeType.PD, node); 
            int b = tfs.size();

            System.out.println(GeneNameMap.getName(node)
                               + " (" + node + ") "
                               + " is bound by " + b + " TFs");

            if(b > 0) { BB += 1; }

            for(Iterator t=tfs.iterator(); t.hasNext();)
            {
                String tf = (String) t.next();

                recordEdge(tf, node, EdgeType.PD);
            }
        }

        System.out.println("Buf and bound = " + BB + " of " + buffered.size());

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
            
            System.out.println(GeneNameMap.getName(ko) +
                               "buffers numTargets= " + targets.size());
        }
        
        System.out.println("Total buffered genes: " + union.size());
        System.out.println("Total buffering effects: " + sum);

        return union;
    }

    private void countTriangles(Set kos, Set candidateGenes, String edgeFilter,
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


    public static void main(String[] args) throws Exception
    {
        DNADamage dd = new DNADamage();

        Set diffExpressed = dd.readWTExpression();
        Map buffered = dd.readBuffering();
        Set kos = buffered.keySet();
        
        System.out.println("Diff Exp: " + diffExpressed.size()
                           + " genes at p < " + EXP_VAL);


        System.out.println("Buffering value = " + BUF_VAL);

        Set bufGenes = dd.countBuffered(buffered);
        dd.countBound(bufGenes);

        dd.countTriangles(kos, diffExpressed, EdgeType.PP, false);

        PrintStream out = System.out;
        if(args.length > 0)
        {
            out = new PrintStream(new FileOutputStream(args[0]));
        }

        dd.printEdges(out);
        
    }
}
