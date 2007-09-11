package oiler.util;

import java.util.NoSuchElementException;

/**
 * Queue with <code>int</code>s for elements.
 * @author Samad Lotia
 */
public class IntQueue
{
	protected IntArray array = new IntArray();

	public IntQueue()
	{
	}

	/**
	 * Retrieves the head of the queue.
	 * @throws NoSuchElementException if the queue is empty
	 */
	public int element()
	{
		if (array.size() == 0)
			throw new NoSuchElementException();
		return array.get(0);
	}

	/**
	 * Adds an element to the queue.
	 */
	public void add(final int value)
	{
		array.add(value);
	}

	/**
	 * Retrieves and removes the head of the queue.
	 * @throws NoSuchElementException if the queue is empty
	 */
	public int remove()
	{
		if (array.size() == 0)
			throw new NoSuchElementException();
		return array.removeAtIndex(0);
	}

	/**
	 * Clears all elements in the queue.
	 */
	public void clear()
	{
		array.clear();
	}

	/**
	 * Returns true if the queue is empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return (array.size() == 0);
	}

	/**
	 * Returns the number of elements in the queue.
	 */
	public int size()
	{
		return array.size();
	}
}
