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
    private String _interaction;
    private String _candidateGenes;
    private String _expressionDataFile;
    private String _edgeData;
    private double _thresh;

    private InteractionGraph _ig;

    private ExpressionDataIF _expressionData;
    
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
        logger.info("Decompose final model into submodel: " + value);
        decomposeModel = value;
    }
    
    public void setInteractionFile(String interaction)
        throws IOException, BadInputException
    {
        _interaction = interaction;

       logger.info("Reading interaction file: " + _interaction);
        _ig = InteractionGraphFactory.createFromSif(_interaction);

    }

    /*
    public void setInteractionFile(String interaction, String candidateGenes)
        //throws Exception
    {
        _interaction = interaction;
        _candidateGenes = candidateGenes;

        logger.info("Reading interaction file: " + _interaction);
        logger.info("Candidate gene file: " + _candidateGenes);
        _ig = InteractionGraphFactory.createFromSif(_interaction, _candidateGenes);
    }
    */
    
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
        throws FileNotFoundException, BadInputException
    {
        _expressionDataFile = e;
        _thresh = pvalThreshold;

        if(e.endsWith(".eda"))
        {
            _expressionData = EdgeExpressionData.load(e, pvalThreshold);
        }
        else
        {
            _expressionData = CytoscapeExpressionData.load(e, pvalThreshold);
        }
        
        _ig.setExpressionData(_expressionData);

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


    public SubmodelOutputFiles run()
        throws IOException, AlgorithmException
    {
        return run(null, null, false);
    }

    public SubmodelOutputFiles run(String outputDir, String outputFile)
        throws IOException, AlgorithmException
    {
        return run(outputDir, outputFile, false);
    }
    
    public SubmodelOutputFiles run(String outputDir, String outputFile,
                    boolean yeangDataFormat)
        throws IOException, AlgorithmException
    {
        //System.out.println(_ig.toString());
        start = System.currentTimeMillis();

        PathResult paths = findPaths();
        
        computePathStats(paths);
        
        //log(paths.toString(_ig));
        
        return _run(paths, _ig, outputDir, outputFile, yeangDataFormat);
    }

    private void computePathStats(PathResult paths)
    {
        IntArrayList kos = paths.getKOs();

        for(int n=0, N=kos.size(); n < N; n++)
        {
            int ko = kos.get(n);

            log(_ig.node2Name(ko) + " " +
                paths.getTarget2PathMap(ko).size());
        }


        log("Num KOs: " + paths.getKOs().size());
        log("Num KO pairs: " + paths.getNumKOPairs());
        log("Num KO with >0 targets: " + paths.countKOWithTargets());
        log("Found paths: " + paths.getPathCount());
        

    }
    
    /**
     * Create a FactorGraph and run the max product algorithm
     *
     * @param paths Candidate explanatory paths
     * @param ig the interaction graph
     * @param outputDir the directory where output files will be written
     * @param outputFile the base file name that will be pre-pended to all
     *        output files
     * @param yeangDataFormat if true, expect data files that were
     *        created/adapted from output of the Chen-Hsiang's
     *        c-implementation.  This is used for debugging purposes
     *        because I cannot decipher Chen-Hsiang's code to figure
     *        out how he is processing the raw data files.
     *
     * @return null if outputDir or outputFile are null.  Otherwise, return
     *         a data structure that contains the location and names
     *         of all of the output files.
     *
     * @throws IOException if there is an error writing the output
     *
     * @throws AlgorithmException if there is an error running the
     *         max product algorithm
     */
    protected SubmodelOutputFiles _run(PathResult paths, InteractionGraph ig,
                                       String outputDir, String outputFile,
                                       boolean yeangDataFormat)
        throws IOException, AlgorithmException
    {
        log("Creating factor graph");
        
        FactorGraph fg =  FactorGraph.create(ig, paths, yeangDataFormat); 

        MaxProductAlgorithm mpa = new MaxProductAlgorithm(fg);

        if(decomposeModel)
        {
            log("Running recursive max product. Output decomposed models.");
            mpa.runMaxProductAndDecompose();
        }
        else
        {
            log("Running recursive max product. Output single model.");
            mpa.runMaxProduct();
        }
        
        log("Updating interaction graph");
        fg.updateInteractionGraph();

        SubmodelOutputFiles output = null;
        if(outputDir != null && outputFile != null)
        {
            String fname = outputDir + File.separator + outputFile;
            
            log("Writing interaction graph sif file: " + fname);
            log("Filtering submodels that explain fewer than: " + KO_EXPLAIN_CUTOFF
                + " KO experiments");
            output = ig.writeGraphAsSubmodels(fname, KO_EXPLAIN_CUTOFF);
        }
        
        log("Done. ");

        return output;
    }

    protected PathResult findPaths()
    {
        String[] conds = _expressionData.getConditionNames();
        log("conditions: " + Arrays.asList(conds));

        int kos[] = _ig.getKOIndices();
        
        log("Interaction Graph contains " + kos.length + " of " +
            conds.length+ " knocked out genes: ");

        log("Finding paths on graph, MAX PATH LENGTH: " + MAX_PATH_LEN);

        DFSPath d = new DFSPath(_ig);

        PathResult paths = d.findPaths(kos, MAX_PATH_LEN);

        _ig.setPaths(paths);
        
        return paths;
    }

    protected void log(String s)
    {
        double t = (System.currentTimeMillis() - start)/1000d;
        logger.info(s + ". [" + t + "]");
    }
}
