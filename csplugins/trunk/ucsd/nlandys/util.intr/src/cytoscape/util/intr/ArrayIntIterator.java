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

package cytoscape.util.intr;

/**
 * A utility class which conveniently converts an array of integers into
 * an IntIterator (an iteration of integers).
 */
public final class ArrayIntIterator implements IntIterator
{

  private final int[] m_elements;
  private int m_index;
  private final int m_end;

  /**
   * No copy of the elements array is made.  The contents of the array
   * are never modified by this object.
   */
  public ArrayIntIterator(int[] elements, int beginIndex, int length)
  {
    if (beginIndex < 0)
      throw new IllegalArgumentException("beginIndex is less than zero");
    if (length < 0)
      throw new IllegalArgumentException("length is less than zero");
    if (((long) beginIndex) + (long) length > (long) elements.length)
      throw new IllegalArgumentException
        ("combination of beginIndex and length exceed length of array");
    m_elements = elements;
    m_index = beginIndex;
    m_end = beginIndex + length;
  }

  public final boolean hasNext()
  {
    return m_index < m_end;
  }

  public final int nextInt()
  {
    return m_elements[m_index++];
  }

}
