import java.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import cern.colt.list.IntArrayList;

public class MaxProduct
{
    String _interaction;
    String _candidateGenes;
    String _expressionData;
    String _edgeData;
    double _thresh;

    private InteractionGraph _ig;

    private int MAX_PATH_LEN = 3;

    private long start;

    public MaxProduct()
    {

    }

    public void setInteractionFile(String interaction) throws Exception
    {
        _interaction = interaction;

        System.out.println("Reading interaction file: " + _interaction);
        _ig = InteractionGraphFactory.createFromSif(_interaction);
    }

    public void setInteractionFile(String interaction, String candidateGenes)
       throws Exception
    {
        _interaction = interaction;
        _candidateGenes = candidateGenes;

        System.out.println("Reading interaction file: " + _interaction);
        System.out.println("Candidate gene file: " + _candidateGenes);
        _ig = InteractionGraphFactory.createFromSif(_interaction, _candidateGenes);
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
        
        InteractionGraphFactory.loadEdgeData(_ig, _edgeData);

        if(pval > 0)
        {
            _ig.setProteinDNAThreshold(pval);
        }
    }

    
    public void run(String outputDir, String outputFile)
        throws IOException, AlgorithmException
    {
        //System.out.println(_ig.toString());
        start = System.currentTimeMillis();

        PathResult paths = findPaths();
        log("Found paths: " + paths.getPathCount());

        //log(paths.toString(_ig));
        
        _run(paths, _ig, outputDir, outputFile);
    }


    protected void _run(PathResult paths, InteractionGraph ig,
                        String outputDir, String outputFile)
        throws IOException, AlgorithmException
    {
        log("Creating factor graph");

        String fname = outputDir + File.separator + outputFile;
        
        FactorGraph fg =  FactorGraph.create(ig, paths); 

        
        log("Running max product and decompose");
        fg.runMaxProductAndDecompose();
        
        /*
        log("Running max product");
        fg.runMaxProduct();
        */
        
        log("Updating interaction graph");
        fg.updateInteractionGraph();

        log("Writing interaction graph sif file: " + fname);
        ig.writeGraphAsSubmodels(fname);

        log("Done. ");
    }

    protected PathResult findPaths()
    {
        String[] conds = _ig.getConditionNames();
        log("conditions: " + Arrays.asList(conds));
        
        IntArrayList ko = new IntArrayList(conds.length);
        
        for(int x=0; x < conds.length; x++)
        {
            if(_ig.containsNode(conds[x]))
            {
                int i = _ig.name2Node(conds[x]); 
                ko.add(i);
            }
        }
        ko.trimToSize();
        
        log("Interaction Graph contains " + ko.size() + " of " +
            conds.length+ " knocked out genes: "
            + ko);

        log("Finding paths on graph, MAX PATH LENGTH: " + MAX_PATH_LEN);

        
        DFSPath d = new DFSPath(_ig);

        PathResult paths = d.findPaths(ko.elements(), MAX_PATH_LEN);

        _ig.setPaths(paths);
        
        return paths;
    }

    protected void log(String s)
    {
        double t = (System.currentTimeMillis() - start)/1000d;
        System.out.println(s + ". [" + t + "]");
    }
}
