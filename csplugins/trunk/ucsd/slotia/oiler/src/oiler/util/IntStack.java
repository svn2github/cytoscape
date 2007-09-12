package oiler.util;

import java.util.NoSuchElementException;

/**
 * Stack with <code>int</code>s for elements.
 * @author Samad Lotia
 */
public class IntStack implements IntIterable
{
	protected int pointer = -1;
	protected IntArray array;

	/**
	 * Initialize a stack with no initial capacity.
	 */
	public IntStack()
	{
		array = new IntArray();
	}

	/**
	 * Initialize a stack with the specified capacity.
	 */
	public IntStack(final int initialCapacity)
	{
		if (initialCapacity < 0)
			throw new IllegalArgumentException("initialCapacity < 0");
		array = new IntArray(initialCapacity);
	}

	/**
	 * Returns the number of elements in the stack.
	 */
	public int size()
	{
		return pointer + 1;
	}

	/**
	 * Returns true if the stack is empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return pointer < 0;
	}

	/**
	 * Returns the element on top of the stack.
	 */
	public int peek()
	{
		if (pointer < 0)
			throw new NoSuchElementException();
		return array.get(pointer);
	}

	/**
	 * Adds an element to the top of the stack.
	 */
	public void push(final int value)
	{
		if (pointer + 1 == array.size())
		{
			array.add(value);
			pointer++;
		}
		else
			array.set(++pointer, value);
	}

	/**
	 * Removes the element on top of the stack and
	 * returns it.
	 */
	public int pop()
	{
		if (pointer < 0)
			throw new NoSuchElementException();
		return array.get(pointer--);
	}

	public IntIterator iterator()
	{
		return array.iterator();
	}

	/**
	 * Returns the distance of <code>element</code> to
	 * the top of the stack.
	 * If the stack contains more than one <code>element</code>,
	 * it will return the element whose distance is the smallest.
	 */
	public int search(final int element)
	{
		final int index = array.lastIndexOf(element);
		if (index < 0)
			return -1;
		else
			return array.size() - index - 1;
	}

	/**
	 * Returns true if the element is in the stack.
	 */
	public boolean contains(final int element)
	{
		return (array.lastIndexOf(element) != -1);
	}
}
