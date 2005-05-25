package fgraph.test;

import fgraph.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import antlr.CommonAST;

import cern.colt.map.*;
import cern.colt.list.*;

class ParseSubmodel
{
    private static Logger logger = Logger.getLogger(ParseSubmodel.class.getName());


    /**
     * Expect a file format:
     * { decomposedmodel ## [
     *  var1
     *  var2
     *  var3
     *  ...
     *  varN
     *  ]
     * }+
     *
     */
    public static void main(String[] args)
    {
        if(args.length < 1)
        {
            System.err.println("missing input submodel file");
            System.exit(1);
        }
        
        try
        {
            ModelLexer lexer = new ModelLexer(new DataInputStream(new FileInputStream(args[0])));
            ModelParser parser = new ModelParser(lexer);
            List data = parser.parseSubmodels();

            System.out.println("Parsed " + data.size() + " submodels");
            
            if(args.length == 2)
            {
                Submodel model0 = (Submodel) data.remove(0);
                
                data = SubmodelAlgorithms.mergeSubmodelsByIndepVar(data);
                
                data.add(0, model0);
            }
            
            printModels(data);
        }
        catch(Exception e)
        {
            System.err.println("exception: "+e);
            e.printStackTrace();
        }
    }

    private static void printModels(List models)
    {
        String name = "model: ";
        for(int x=0; x < models.size(); x++)
        {
            Submodel m = (Submodel) models.get(x);
            if(x == 0)
            {
                System.out.print("model: invaraint");
            }
            else
            {
                System.out.print("model: " + x);
            }
            System.out.println(" indepvar=" + m.getIndependentVar()
                               +" size=" + m.size());
        }
    }
}

