package fgraph;

import java.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import cern.colt.list.IntArrayList;

public class PrintFGMaxProduct extends MaxProduct
{
    protected PrintFGMaxProduct() {}
    
    public static MaxProduct create()
    {
        System.out.println("### Creating PrintFGMaxProduct");
        return new PrintFGMaxProduct();
    }
    
    protected void _run(PathResult paths, InteractionGraph ig,
                        String outputDir, String outputFile)
        throws IOException, AlgorithmException
    {
        //System.out.println(ig.toString());
        
        log("Creating factor graph");

        String fname = outputDir + File.separator + outputFile;
        
        PrintableFactorGraph fg = PrintableFactorGraph.createPrintable(ig, paths);
        
        log("Writing factor graph sif file: " + fname);
        
        fg.writeSif(fname);
        
        log("Running max product");
        fg.runMaxProductAndDecompose();
        
        log("Printing max config");
        fg.printMaxConfig();

        log("Updating interaction graph");
        fg.updateInteractionGraph();

        log("Writing interaction graph sif file: " + fname);
        ig.writeGraph(fname);

        log("Done. ");
    }


}
