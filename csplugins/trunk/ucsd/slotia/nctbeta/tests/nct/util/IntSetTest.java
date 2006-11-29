package nct.util;

import junit.framework.*;
import java.util.*;

public class IntSetTest extends TestCase
{
	IntSet set;

	protected void setUp()
	{
		set = new IntSet();
	}

	public void testconstructor()
	{
		assertTrue(set.size() == 0);
		assertTrue(set.isEmpty());
	}

	public void testaddAndRemove()
	{
		set.add(1);
		assertTrue(set.size() == 1);
		assertFalse(set.isEmpty());
		assertTrue(set.contains(1));

		set.add(2);
		assertTrue(set.size() == 2);
		assertFalse(set.isEmpty());
		assertTrue(set.contains(1));
		assertTrue(set.contains(2));

		assertFalse(set.remove(3));
		assertTrue(set.size() == 2);
		assertTrue(set.remove(1));
		assertTrue(set.size() == 1);
		assertFalse(set.isEmpty());
		assertFalse(set.contains(1));
		assertTrue(set.contains(2));

		assertFalse(set.remove(1));
		assertTrue(set.size() == 1);
		assertFalse(set.isEmpty());
		assertTrue(set.remove(2));
		assertTrue(set.size() == 0);
		assertTrue(set.isEmpty());
		assertFalse(set.contains(1));
		assertFalse(set.contains(2));
	}

	public void testclear()
	{
		set.add(3);
		assertFalse(set.isEmpty());
		assertTrue(set.size() == 1);

		set.clear();
		assertTrue(set.isEmpty());
		assertTrue(set.size() == 0);
	}

	public void testintIterator()
	{
		set.add(1);
		set.add(2);
		set.add(3);

		IntIterator intIterator = set.intIterator();
		assertTrue(intIterator != null);

		assertTrue(intIterator.numRemaining() == 3);
		assertTrue(intIterator.hasNext());

		intIterator.next();
		assertTrue(intIterator.numRemaining() == 2);
		assertTrue(intIterator.hasNext());

		intIterator.next();
		assertTrue(intIterator.numRemaining() == 1);
		assertTrue(intIterator.hasNext());

		intIterator.next();
		assertTrue(intIterator.numRemaining() == 0);
		assertFalse(intIterator.hasNext());




		assertTrue(set.remove(2));
		assertFalse(set.remove(2));
		
		intIterator = set.intIterator();
		assertTrue(intIterator != null);

		assertTrue(intIterator.numRemaining() == 2);
		assertTrue(intIterator.hasNext());

		intIterator.next();
		assertTrue(intIterator.numRemaining() == 1);
		assertTrue(intIterator.hasNext());

		intIterator.next();
		assertTrue(intIterator.numRemaining() == 0);
		assertFalse(intIterator.hasNext());
	}

	public void testtoArray()
	{
		set.add(1);
		set.add(2);
		set.add(3);

		int[] array = set.toArray();
		assertTrue(array.length == 3);
		assertFalse(indexOf(array, 1) == -1);
		assertFalse(indexOf(array, 2) == -1);
		assertFalse(indexOf(array, 3) == -1);
		assertTrue(indexOf(array, 0) == -1);
	}

	public void testcompareTo(IntSet that)
	{
		set.add(1);
		set.add(3);

		IntSet other = new IntSet();
		other.add(1);
		other.add(2);
		
		assertFalse(set.compareTo(other) == 0);
		assertFalse(other.compareTo(set) == 0);

		set.add(2);
		assertFalse(set.compareTo(other) == 0);
		assertFalse(other.compareTo(set) == 0);

		other.add(3);
		assertTrue(set.compareTo(other) == 0);
		assertTrue(other.compareTo(set) == 0);
	}

	private int indexOf(int[] array, int element)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i] == element)
				return i;

		return -1;
	}

	public void testtoSet()
	{
		set.add(1);
		set.add(2);
		set.add(3);

		Set<Integer> genericSet = set.toSet();
		assertTrue(genericSet.size() == 3);
		assertTrue(set.contains(new Integer(1)));
		assertTrue(set.contains(new Integer(2)));
		assertTrue(set.contains(new Integer(3)));
		assertFalse(set.contains(new Integer(0)));
		
	}
}
