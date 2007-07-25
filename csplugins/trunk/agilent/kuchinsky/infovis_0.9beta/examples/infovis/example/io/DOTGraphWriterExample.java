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
import infovis.graph.io.DOTGraphWriter;
import infovis.graph.io.GraphReaderFactory;
import infovis.io.AbstractReader;

/**
 * Class DOTGraphWriterExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DOTGraphWriterExample {
    public static void main(String args[]) {
        Graph graph = new DefaultGraph();
        AbstractReader reader =
            GraphReaderFactory.createGraphReader(args[0], graph);
        if (reader != null && reader.load()) {
            DOTGraphWriter writer = new DOTGraphWriter(System.out, graph);
            writer.write();
        }
        else {
            System.out.println("Couldn't load graph " + args[0]);
        }
    }
}
