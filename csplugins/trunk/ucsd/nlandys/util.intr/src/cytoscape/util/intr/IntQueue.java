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
 * A first-in, first-out container of 32 bit integers.  In the underlying
 * implementation, the memory consumed by an instance of this class may
 * increase, but does never decrease.<p>
 * While other container classes in this package are able to hold up
 * to Integer.MAX_VALUE elements, this class is only able to hold
 * Integer.MAX_VALUE-1 elements.
 * @deprecated This implementation has not undergone adequate testing yet;
 *   deprecation will go away once this class is adequately tested.
 */
public final class IntQueue
{

  // This must be a positive (non-zero) integer.
  private static final int DEFAULT_CAPACITY = 12;

  private int[] m_queue;

  /**
   * Creates a new queue of integers.
   */
  public IntQueue()
  {
    m_queue = new int[DEFAULT_CAPACITY];
  }

  /**
   * Removes all integers from this queue.  This operation has constant time
   * complexity.
   */
  public final void empty()
  {
  }

  /**
   * Returns the number of integers that are currently in this queue.
   */
  public final int size()
  {
    return -1;
  }

  /**
   * Inserts a new integer into this queue.
   */
  public final void enqueue(int value)
  {
  }

  /**
   * A non-mutating operation that retrieves the next integer in this queue.<p>
   * It is considered an error to call this method if there are no integers
   * currently in this queue.  If size() returns zero immediately before
   * this method is called, the results of this operation are undefined.
   */
  public final int peek()
  {
    return -1;
  }

  /**
   * Removes and returns the next integer in this queue.<p>
   * It is considered an error to call this method if there are no integers
   * currently in this queue.  If size() return zero immediately before
   * this method is called, the results of this operation are undefined.
   */
  public final int dequeue()
  {
    return -1;
  }

}
