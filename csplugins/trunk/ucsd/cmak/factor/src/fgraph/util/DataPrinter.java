package fgraph.util;

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

import fgraph.Submodel;

import cern.colt.list.IntArrayList;

/**
 * A class that centralizes functionality for printing
 * internal data structures for debugging purposes.
 *
 */
public class DataPrinter
{
        
    public static  void printSubmodels(String filename, List submodels)
    {
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream(filename));
            for(int x=0; x < submodels.size(); x++)
            {
                Submodel s = (Submodel) submodels.get(x);
                
                IntArrayList vars = s.getVars();
                StringBuffer b = new StringBuffer("model ");
                b.append(s.getId());
                b.append(" [");
                for(int v=0; v < vars.size(); v++)
                {
                    b.append(vars.get(v) + " ");
                }
                b.append("]");
                
                out.println(b.toString());
            }

            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


}
