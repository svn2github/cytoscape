import java.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import cern.colt.list.IntArrayList;

public class MaxProduct
{
    String _interaction;
    String _expressionData;
    String _edgeData;
    double _thresh;

    InteractionGraph _ig;

    int MAX_PATH_LEN = 3;

    long start;

    static final String usage =
        "Usage: <max path len> \n" +
        "       <interaction sif file>\n" +
        "       <expression pvals>\n" +
        "       <expression pval threshold>\n" +
        "       <Cytoscape-format edge attribute file>\n" +
        "       <protein-DNA pvalue threshold: -1 to use all>\n" +
        "       <output directory>\n" +
        "       <output base filename>\n" +
        "       [true <optional if factor graph should be written to a file>]\n";
    
    public static void main(String[] args)
    {
        if(args.length < 7)
        {
            System.err.println(usage);
            System.exit(1);
        }
        
        int pathLength = Integer.parseInt(args[0]);

        String i = args[1];
        String exp = args[2];
        double expThresh = Double.parseDouble(args[3]);
        String edge = args[4];
        double edgeThresh = Double.parseDouble(args[5]);
        String outdir = args[6];
        String outfile = args[7];

        boolean printFactorGraph = false;
        if(args[8] != null)
        {
            printFactorGraph = Boolean.valueOf(args[8]).booleanValue();
        }
        
        try
        {
            MaxProduct mp = new MaxProduct(i);
            mp.setMaxPathLength(pathLength);
            mp.setExpressionFile(exp, expThresh);
            mp.setEdgeFile(edge, edgeThresh);
            mp.run(outdir, outfile, printFactorGraph);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public MaxProduct(String interaction) throws Exception
    {
        _interaction = interaction;

        System.out.println("Reading interaction file: " + _interaction);
        _ig = InteractionGraph.createFromSif(_interaction);
    }

    public void setProteinDNAThreshold(double pval)
    {

    }
    
    public void setMaxPathLength(int i)
    {
        if( i > 0)
        {
            MAX_PATH_LEN = i;
        }
        else
        {
            System.out.println("Warning: MAX_PATH_LENGTH [" + i + "] < 0.");
        }
    }
    
    public void setExpressionFile(String e, double pvalThreshold)
        throws FileNotFoundException
    {
        _expressionData = e;
        _thresh = pvalThreshold;

        _ig.loadExpressionData(_expressionData);
        _ig.setExpressionPvalThreshold(_thresh);
    }


    public void setEdgeFile(String e, double pval)
        throws FileNotFoundException
    {
        _edgeData = e;
        
        _ig.loadEdgeData(_edgeData);

        if(pval > 0)
        {
            _ig.setProteinDNAThreshold(pval);
        }
    }

    
    public void run(String outputDir, String outputFile, boolean printFactorGraph)
        throws IOException, AlgorithmException
    {
        //System.out.println(_ig.toString());
        start = System.currentTimeMillis();
        
        String[] conds = _ig.getConditionNames();
        log("conditions: " + Arrays.asList(conds));
        
        IntArrayList ko = new IntArrayList(conds.length);
        
        for(int x=0; x < conds.length; x++)
        {
            if(_ig.containsNode(conds[x]))
            {
                ko.add(_ig.name2Node(conds[x]));
            }
        }
        ko.trimToSize();
        
        log("Interaction Graph contains " + ko.size() + " of " +
            conds.length+ " knocked out genes: "
            + ko);

        log("Finding paths on graph, MAX PATH LENGTH: " + MAX_PATH_LEN);

        
        DFSPath d = new DFSPath(_ig);

        PathResult paths = d.findPaths(ko.elements(), MAX_PATH_LEN);

        log("Found paths: " + paths.getPathCount());
        //paths.print(_ig);

        log("Creating factor graph. Print graph =" + printFactorGraph);

        String fname = outputDir + File.separator + outputFile;
        
        FactorGraph fg;
        if(printFactorGraph)
        {
            fg = PrintableFactorGraph.createPrintable(_ig, paths);
            
            log("Writing factor graph sif file: " + fname);
            
            ((PrintableFactorGraph) fg).writeSif(fname);
            
            ((PrintableFactorGraph) fg).writeNodeProbs(System.out);
        }
        else
        {
            fg = FactorGraph.create(_ig, paths); 
        }
        
        log("Running max product");
        fg.runMaxProduct();

        //log(fg.printAdj());

        if(printFactorGraph)
        {
            ((PrintableFactorGraph) fg).printMaxConfig();
        }
        
        log("Updating interaction graph");
        fg.updateInteractionGraph();

        log("Writing interaction graph sif file: " + fname);
        _ig.writeGraph(fname);

        log("Done. ");
    }

    private void log(String s)
    {
        double t = (System.currentTimeMillis() - start)/1000d;
        System.out.println(s + ". [" + t + "]");
    }
}
