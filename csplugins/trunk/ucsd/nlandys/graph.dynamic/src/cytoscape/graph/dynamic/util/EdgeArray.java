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

// Valid indices: [0, Integer.MAX_VALUE - 1].
class EdgeArray
{

  private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

  private Edge[] m_edgeArr;

  EdgeArray()
  {
    m_edgeArr = new Edge[INITIAL_CAPACITY];
  }

  // Understand that this method will not increase the size of the underlying
  // array, no matter what.
  // Throws ArrayIndexOutOfBoundsException if index is negative.
  // The package-level agreement for this class is that Integer.MAX_VALUE
  // will never be passed to this method.
  Edge getEdgeAtIndex(int index)
  {
    if (index >= m_edgeArr.length) return null;
    return m_edgeArr[index];
  }

  // Understand that this method will potentially increase the size of the
  // underlying array, but only if two conditions hold:
  //   1. edge is not null and
  //   2. index is greater than or equal to the length of the array.
  // Throws ArrayIndexOutOfBoundsException if index is negative.
  // The package-level agreement for this class is that Integer.MAX_VALUE
  // will never be passed to this method.
  void setEdgeAtIndex(Edge edge, int index)
  {
    if (index >= m_edgeArr.length && edge == null) return;
    try { m_edgeArr[index] = edge; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0) throw e;
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 Math.max(((long) m_edgeArr.length) * 2l + 1l,
                          ((long) index) + 1l + (long) INITIAL_CAPACITY));
      Edge[] newArr = new Edge[newArrSize];
      System.arraycopy(m_edgeArr, 0, newArr, 0, m_edgeArr.length);
      m_edgeArr = newArr;
      m_edgeArr[index] = edge;
    }
  }

}
