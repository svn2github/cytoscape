package legacy.util;

import legacy.IndexIterator;

import java.util.NoSuchElementException;

/**
 * <b>This class is in a very unfinished state; development effort on this
 * class has been suspended until further notice; don't use this class
 * unless you're the author.
 * </b><p>
 * Creates an <code>IndexIterator</code> object out of an array of integers.
 */
public class ArrayIterator implements IndexIterator {

    private final int[] m_indices;
    private final int m_last; // Exclusive (non-inclusive) last index.
    private int m_iter;       // Index of next item to be returned.

    /**
     * Defines an <code>IndexIterator</code> by returning values
     * <code>indices[0]</code>, <code>indices[1]</code>, ...
     * <nobr><code>indices[indices.length - 1]</code></nobr> as
     * sequential <code>next()</code> return values.
     * No copy of the <code>indices</code> array is made.
     *
     * @throws NullPointerException if <nobr><code>indices == null</code></nobr>.
     */
    public ArrayIterator(int[] indices) {
        this(indices, 0, indices.length);
    }

    /**
     * Defines an <code>IndexIterator</code> by returning values
     * <code>indices[beginIndex]</code>,
     * <nobr><code>indices[beginIndex + 1]</code></nobr>, ...
     * <nobr><code>indices[beginIndex + length - 1]</code></nobr> as
     * sequential <code>next()</code> return values.
     * No copy of the <code>indices</code> array is made.
     *
     * @throws NullPointerException           if <nobr><code>indices == null</code></nobr>.
     * @throws IllegalArgumentException       if <nobr><code>length < 0</code></nobr>.
     * @throws ArrayIndexOutOfBoundsException if <nobr><code>beginIndex < 0</code></nobr> or if
     *                                        <nobr><code>beginIndex + length > indices.length</code></nobr>.
     */
    public ArrayIterator(int[] indices, int beginIndex, int length) {
        if (indices == null) {
            throw new NullPointerException("indices is null");
        }
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (beginIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("beginIndex < 0");
        }
        if (beginIndex + length > indices.length) {
            throw new ArrayIndexOutOfBoundsException
                    ("beginIndex + length > indices.length");
        }
        m_indices = indices;
        m_last = beginIndex + length;
        m_iter = beginIndex;
    }

    public int numRemaining() {
        return m_last - m_iter;
    }

    public int next() {
        if (m_iter != m_last) {
            return m_indices[m_iter++];
        } else {
            throw new NoSuchElementException();
        }
    }

}
