package legacy;

import java.util.NoSuchElementException;

/**
 * <b>This class is in a very unfinished state; development effort on this
 * class has been suspended until further notice; don't use this class
 * unless you're the author.
 * </b><p>
 * An iterator over a set of indices.
 */
public interface IndexIterator {

    /**
     * Returns the next index in the iteration.
     *
     * @return the next index in the iteration.
     * @throws NoSuchElementException if the iteration has no more
     *                                indices.
     */
    public int next();

    /**
     * Returns an integer I such that <code>next()</code> will successfully
     * return a value no more and no less that I times.  Negative values are
     * never returned.
     */
    public int numRemaining();

}
