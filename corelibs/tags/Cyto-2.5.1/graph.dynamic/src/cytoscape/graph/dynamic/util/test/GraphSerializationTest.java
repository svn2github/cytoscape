
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.graph.dynamic.util.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class GraphSerializationTest {
	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 * @throws IllegalStateException DOCUMENT ME!
	 * @throws NullPointerException DOCUMENT ME!
	 */
	public static void main(String[] args) throws Exception {
		{
			DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(graph);
			objOut.flush();
			objOut.close();
			System.out.println("An empty graph takes " + byteOut.size()
			                   + " bytes in serialized form.");

			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream objIn = new ObjectInputStream(byteIn);
			graph = (DynamicGraph) objIn.readObject();
			objIn.close();

			if ((graph.nodes().numRemaining() != 0) || (graph.edges().numRemaining() != 0)) {
				throw new IllegalStateException("expected restored graph to be empty");
			}

			graph = DynamicGraphFactory.instantiateDynamicGraph();

			int[] nodes = new int[10];

			for (int i = 0; i < nodes.length; i++)
				nodes[i] = graph.nodeCreate();

			int[] edges = new int[20];

			for (int i = 0; i < edges.length; i++)
				edges[i] = graph.edgeCreate(nodes[i % nodes.length], nodes[(i * 3) % nodes.length],
				                            true);

			byteOut = new ByteArrayOutputStream();
			objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(graph);
			objOut.flush();
			objOut.close();
			System.out.println("A graph with " + nodes.length + " nodes and " + edges.length
			                   + " edges takes " + byteOut.size() + " bytes in serialized form.");
			byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			objIn = new ObjectInputStream(byteIn);
			graph = (DynamicGraph) objIn.readObject();
			objIn.close();

			if ((graph.nodes().numRemaining() != nodes.length)
			    || (graph.edges().numRemaining() != edges.length)) {
				throw new IllegalStateException("expected restored graph to have proper number of nodes and edges");
			}
		}

		{
			DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
			int[] nodes = new int[100000];
			int[] edges = new int[1000000];

			for (int i = 0; i < nodes.length; i++)
				nodes[i] = graph.nodeCreate();

			for (int i = 0; i < edges.length; i++)
				edges[i] = graph.edgeCreate(nodes[i % nodes.length], nodes[(i * 3) % nodes.length],
				                            true);

			for (int i = 0; i < nodes.length; i += 2)
				graph.nodeRemove(nodes[i]);

			System.out.println("graph has " + graph.nodes().numRemaining() + " nodes and "
			                   + graph.edges().numRemaining() + " edges");
			System.out.println("at one point graph had " + nodes.length + " nodes and "
			                   + edges.length + " edges");

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			long millisBegin = System.currentTimeMillis();
			objOut.writeObject(graph);
			objOut.flush();
			objOut.close();

			long millisEnd = System.currentTimeMillis();
			System.out.println("serializing graph took " + (millisEnd - millisBegin)
			                   + " milliseconds");

			byte[] serializedData = byteOut.toByteArray();
			System.out.println("in serialized form, graph takes " + serializedData.length
			                   + " bytes");

			ByteArrayInputStream byteIn = new ByteArrayInputStream(serializedData);
			millisBegin = System.currentTimeMillis();

			ObjectInputStream objIn = new ObjectInputStream(byteIn);
			graph = (DynamicGraph) objIn.readObject();
			objIn.close();
			millisEnd = System.currentTimeMillis();
			System.out.println("deserializeing graph took " + (millisEnd - millisBegin)
			                   + " milliseconds");
			System.out.println("deserialized graph has " + graph.nodes().numRemaining()
			                   + " nodes and " + graph.edges().numRemaining() + " edges");
		}

		{
			DynamicGraph[] graphs = new DynamicGraph[2];
			graphs[0] = DynamicGraphFactory.instantiateDynamicGraph();
			graphs[1] = graphs[0];

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			objOut.writeObject(graphs);
			objOut.flush();
			objOut.close();

			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream objIn = new ObjectInputStream(byteIn);
			graphs = (DynamicGraph[]) objIn.readObject();
			objIn.close();

			if (graphs.length != 2) {
				throw new IllegalStateException("graphs.length is not 2");
			}

			if ((graphs[0] == null) || (graphs[1] == null)) {
				throw new NullPointerException();
			}

			if (graphs[0] != graphs[1]) {
				throw new IllegalStateException("not the same reference");
			}
		}
	}
}
