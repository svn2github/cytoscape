package fgraph;

import java.util.Arrays;

import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import cern.colt.list.IntArrayList;

public class MaxProduct
{
    
    private static Logger logger = Logger.getLogger(MaxProduct.class.getName());
    String _interaction;
    String _candidateGenes;
    String _expressionData;
    String _edgeData;
    double _thresh;

    private InteractionGraph _ig;

    private int MAX_PATH_LEN = 3;
    private int KO_EXPLAIN_CUTOFF = 3;

    private boolean decomposeModel;
    
    private long start;

    public MaxProduct()
    {
        decomposeModel = true;
    }

    public InteractionGraph getInteractionGraph()
    {
        return _ig;
    }

    /**
     *
     * @param value If true, algorithm will return decomposed submodels.
     * Otherwise all paths and edges will be put into one model.
     */
    public void setDecomposeModel(boolean value)
    {
        decomposeModel = value;
    }
    
    public void setInteractionFile(String interaction) //throws Exception
    {
        _interaction = interaction;

        logger.info("Reading interaction file: " + _interaction);
        _ig = InteractionGraphFactory.createFromSif(_interaction);

    }

    public void setInteractionFile(String interaction, String candidateGenes)
        //throws Exception
    {
        _interaction = interaction;
        _candidateGenes = candidateGenes;

        logger.info("Reading interaction file: " + _interaction);
        logger.info("Candidate gene file: " + _candidateGenes);
        _ig = InteractionGraphFactory.createFromSif(_interaction, _candidateGenes);
    }

    
    public void setMaxPathLength(int i)
    {
        if( i > 0)
        {
            MAX_PATH_LEN = i;

            logger.info("Set max path len to: " + MAX_PATH_LEN);
        }
        else
        {
            logger.warning("Warning: MAX_PATH_LENGTH [" + i + "] < 0.");
        }
    }

    
    public void setKOExplainCutoff(int i)
    {
        if( i > 0)
        {
            KO_EXPLAIN_CUTOFF = i;

            logger.info("Set ko explain cutoff to: " + KO_EXPLAIN_CUTOFF);
        }
        else
        {
            logger.warning("Warning: KO_EXPLAIN_CUTOFF [" + i + "] <= 0.");
        }
    }

    
    public void setExpressionFile(String e, double pvalThreshold)
        throws FileNotFoundException
    {
        _expressionData = e;
        _thresh = pvalThreshold;

        
        _ig.loadExpressionData(_expressionData);
        _ig.setExpressionPvalThreshold(_thresh);

        //logger.info("  " + _ig.toString());
    }


    /**
     * @param pval the pvalue threshold to use to filter protein-DNA edges.
     * Or "0" to not use a threshold.
     */
    public void setEdgeFile(String e, double pval)
        throws FileNotFoundException
    {
        _edgeData = e;

        logger.info("Reading edge data file: " + _edgeData
                    + " protein-DNA pval threshold=" + pval);
        
        InteractionGraphFactory.loadEdgeData(_ig, _edgeData);

        if(pval > 0)
        {
            _ig.setProteinDNAThreshold(pval);
        }
    }


    public void run()
        throws IOException, AlgorithmException
    {
        run(null, null);
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
        
        FactorGraph fg =  FactorGraph.create(ig, paths); 

        if(decomposeModel)
        {
            log("Running recursive max product. Output decomposed models.");
            fg.runMaxProductAndDecompose();
        }
        else
        {
            log("Running recursive max product. Output single model.");
            fg.runMaxProduct();
        }
        
        log("Updating interaction graph");
        fg.updateInteractionGraph();

        if(outputDir != null && outputFile != null)
        {
            String fname = outputDir + File.separator + outputFile;
            
            log("Writing interaction graph sif file: " + fname);
            log("Filtering submodels that explain fewer than: " + KO_EXPLAIN_CUTOFF
                + " KO experiments");
            ig.writeGraphAsSubmodels(fname, KO_EXPLAIN_CUTOFF);
        }
        
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
        logger.info(s + ". [" + t + "]");
    }
}
