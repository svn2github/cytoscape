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

package cytoscape.graph.dynamic.util;

final class NodeDepot
{

  private final Node m_head;

  NodeDepot()
  {
    m_head = new Node();
  }

  // Gimme a node, darnit!
  // Don't forget to initialize the node's member variables!
  // Node.nextNode is used internally and will point to some undefined node
  // in the returned Node.
  Node getNode()
  {
    final Node returnThis = m_head.nextNode;
    if (returnThis == null) { return new Node(); }
    m_head.nextNode = returnThis.nextNode;
    return returnThis;
  }

  // Deinitialize the object's members yourself if you need or want to.
  // node.nextNode is used internally and does not need to be deinitialized.
  void recycleNode(Node node)
  {
    node.nextNode = m_head.nextNode;
    m_head.nextNode = node;
  }

}
