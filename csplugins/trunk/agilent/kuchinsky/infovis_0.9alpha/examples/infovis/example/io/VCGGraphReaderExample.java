/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.io;
import infovis.Graph;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphMLWriter;
import infovis.graph.io.VCGGraphReader;

import java.io.*;

/**
 * Class DOTGraphReaderExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class VCGGraphReaderExample {
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String fname = args[i];
            try {
                FileReader fin = new FileReader(fname);
                BufferedReader in = new BufferedReader(fin);
                Graph graph = new DefaultGraph();
                int offset1 = fname.lastIndexOf('/') + 1;
                int offset2 = fname.indexOf('.', offset1);
                String name;
                if (offset2 == -1)
                    name = fname.substring(offset1);
                else {
                    name = fname.substring(offset1, offset2).toLowerCase();
                }
                VCGGraphReader reader = new VCGGraphReader(in, name, graph);
                System.out.println(name);
                if (reader.load()) {
                    Writer out;
                    try {
                        FileWriter fout = new FileWriter(name+".xml");

                        out = new BufferedWriter(fout);
                    }
                    catch (IOException e) {
                        out = new OutputStreamWriter(System.out);
                    }
                    
                    GraphMLWriter writer = new GraphMLWriter(out, graph);
                    writer.write();
                }
            }
            catch (FileNotFoundException e) {
            }
        }
    }
}

