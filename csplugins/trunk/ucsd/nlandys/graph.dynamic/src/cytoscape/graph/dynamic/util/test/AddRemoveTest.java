/*
  Copyright (c) 2005, Nerius Landys
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package cytoscape.graph.dynamic.util.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;

public class AddRemoveTest
{

  public static void main(String[] args)
  {
    final DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
    final int[][] nodesArr = new int[][] { new int[100000],
                                           new int[99980],
                                           new int[100010] };
    final int[] edges = new int[1000000];
    final int iterations = 10000;
    for (int foo = 0; foo < iterations; foo++)
    {
      boolean print = false;
      if (foo % 10 == 0) print = true;
      if (print) System.out.println("at add/remove iteration " + (foo + 1) +
                                    " of " + iterations);
      if (print) System.out.println("creating nodes");
      final int[] nodes = nodesArr[foo % nodesArr.length];
      for (int i = 0; i < nodes.length; i++) nodes[i] = graph.nodeCreate();
      if (print) System.out.println("creating edges");
      for (int i = 0; i < edges.length; i++)
        edges[i] = graph.edgeCreate(nodes[i % nodes.length],
                                    nodes[(i * 3) % nodes.length],
                                    true);
      if (print) System.out.println
                   ("in graph: " + graph.nodes().numRemaining() +
                    " nodes and " + graph.edges().numRemaining() + " edges");
      if (print) System.out.println();
      if (print) System.out.println("removing edges");
      for (int i = 0; i < edges.length; i++) graph.edgeRemove(edges[i]);
      if (print) System.out.println("removing nodes");
      for (int i = 0; i < nodes.length; i++) graph.nodeRemove(nodes[i]);
      if (print) System.out.println
                   ("in graph: " + graph.nodes().numRemaining() +
                    " nodes and " + graph.edges().numRemaining() + " edges");
      if (print) System.out.println();
    }
  }

}
