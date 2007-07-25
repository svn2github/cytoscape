package infovis.example.io;
import infovis.Graph;
import infovis.graph.DefaultGraph;
import infovis.graph.io.DOTGraphReader;
import infovis.graph.io.GraphMLWriter;

import java.io.*;

/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class DOTGraphReaderExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DOTGraphReaderExample {

    public static void main(String[] args) {
        String fname = args[0];
        try {
            InputStream in = new FileInputStream(fname);
            Graph graph = new DefaultGraph();
            int offset1 = fname.lastIndexOf('/') + 1;
            int offset2 = fname.indexOf('.', offset1);
            String name;
            if (offset2 == -1)
                name = fname.substring(offset1);
            else {
                if ((offset2 - offset1) > 4)
                    offset1 += 4;

                name = fname.substring(offset1, offset2).toLowerCase();
            }
            DOTGraphReader reader = new DOTGraphReader(in, name, graph);
            if (reader.load()) {
                OutputStream out = System.out;
                if (args.length == 2) {
                    try {
                        FileOutputStream fout = new FileOutputStream(args[1]);

                        out = new BufferedOutputStream(fout);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                GraphMLWriter writer = new GraphMLWriter(out, graph);
                writer.write();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
