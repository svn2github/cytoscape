package oiler.util;

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

import java.util.*;

public class IntHashSetTest
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(IntHashSetTest.class);
	}
	
	IntHashSet set;

	@Before
	public void setUp()
	{
		set = new IntHashSet();
	}

	@Test
	public void testconstructor()
	{
		assertTrue(set.size() == 0);
		assertTrue(set.isEmpty());
	}

	@Test
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

	@Test
	public void testclear()
	{
		set.add(3);
		assertFalse(set.isEmpty());
		assertTrue(set.size() == 1);

		set.clear();
		assertTrue(set.isEmpty());
		assertTrue(set.size() == 0);
	}

	@Test
	public void testintIterator()
	{
		set.add(1);
		set.add(2);
		set.add(3);

		IntIterator intIterator = set.iterator();
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
		
		intIterator = set.iterator();
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

	@Test
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

	@Test
	public void testequals()
	{
		set.add(1);
		set.add(3);

		IntHashSet other = new IntHashSet();
		other.add(1);
		other.add(2);
		
		assertFalse(set.equals(other));
		assertFalse(other.equals(set));

		set.add(2);
		assertFalse(set.equals(other));
		assertFalse(other.equals(set));

		other.add(3);
		assertTrue(set.equals(other));
		assertTrue(other.equals(set));
	}

	private int indexOf(int[] array, int element)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i] == element)
				return i;

		return -1;
	}

	@Test
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

	@Test
	public void testCopyConstructor()
	{
		set.add(0);
		set.remove(0);
		set.add(1);
		set.remove(1);
		set.add(2);
		set.remove(2);
		set.add(90);
		set.add(40);
		set.add(4);
		set.add(83);
		set.add(77);
		set.add(68);

		IntHashSet newSet = new IntHashSet(set);
		assertTrue(newSet.contains(90));
		assertTrue(newSet.contains(40));
		assertTrue(newSet.contains(4));
		assertTrue(newSet.contains(83));
		assertTrue(newSet.contains(77));
		assertTrue(newSet.contains(68));
		assertFalse(newSet.contains(0));
		assertFalse(newSet.contains(1));
		assertFalse(newSet.contains(2));
	}
}
