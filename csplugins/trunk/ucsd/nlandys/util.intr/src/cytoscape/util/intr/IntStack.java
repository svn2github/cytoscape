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
 * A first-in, last-out container of 32 bit integers.  In the underlying
 * implementation, the memory consumed by an instance of this class may
 * increase, but does never decrease.
 */
public final class IntStack
{

  // This must be a non-negative integer.
  private static final int DEFAULT_CAPACITY = 11;

  private int[] m_stack;
  private int m_currentSize;

  /**
   * Creates a new stack of integers.
   */
  public IntStack()
  {
    m_stack = new int[DEFAULT_CAPACITY];
    m_currentSize = 0;
  }

  /**
   * Removes all integers from this stack.  This operation has constant time
   * complexity.
   */
  public final void empty()
  {
    m_currentSize = 0;
  }

  /**
   * Returns the number of integers that are currently on this stack.
   */
  public final int size()
  {
    return m_currentSize;
  }

  /**
   * Pushes a new integer onto this stack.  A successive peek() or pop()
   * call will return the specified value.
   */
  public final void push(int value)
  {
    try { m_stack[m_currentSize++] = value; }
    catch (ArrayIndexOutOfBoundsException e) {
      m_currentSize--;
      checkSize();
      m_stack[m_currentSize++] = value; }
  }

  /**
   * A non-mutating operation that retrieves the next integer on this stack.<p>
   * It is considered an error to call this method if there are no integers
   * currently on this stack.  If size() returns zero immediately before this
   * method is called, the results of this operation are undefined.
   */
  public final int peek()
  {
    return m_stack[m_currentSize - 1];
  }

  /**
   * Removes and returns the next integer on this stack.<p>
   * It is considered an error to call this method if there are no integers
   * currently on this stack.  If size() returns zero immediately before this
   * method is called, the results of this operation are undefined.
   */
  public final int pop()
  {
    try { return m_stack[--m_currentSize]; }
    catch (ArrayIndexOutOfBoundsException e) {
      m_currentSize++;
      throw e; }
  }

  private final void checkSize()
  {
    if (m_currentSize < m_stack.length) return;
    final int newStackSize = (int) Math.min((long) Integer.MAX_VALUE,
                                            ((long) m_stack.length) * 2l + 1l);
    if (newStackSize == m_stack.length)
      throw new IllegalStateException("cannot allocate large enough array");
    final int[] newStack = new int[(int) newStackSize];
    System.arraycopy(m_stack, 0, newStack, 0, m_stack.length);
    m_stack = newStack;
  }

}
