package fgraph;

import java.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import cern.colt.list.IntArrayList;

public class ProfileMaxProduct extends MaxProduct
{
    protected ProfileMaxProduct() {}
    
    public static MaxProduct create()
    {
        System.out.println("### Creating ProfileProduct");
        return new ProfileMaxProduct();
    }

    protected void _run(PathResult paths, InteractionGraph ig,
                        String outputDir, String outputFile)
        throws IOException, AlgorithmException
    {
        log("Creating instrumented factor graph");

        String fname = outputDir + File.separator + outputFile;
        
        FactorGraph fg =  InstrumentedFactorGraph.create(ig, paths, fname); 

        log("Running max product");
        fg.runMaxProduct();

        log("Updating interaction graph");
        fg.updateInteractionGraph();

        log("Writing interaction graph sif file: " + fname);
        ig.writeGraph(fname);

        log("Done. ");
    }
}
