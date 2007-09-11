package oiler.util;

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

public class IntArrayTest
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(IntArrayTest.class);
	}

	IntArray array;

	@Before
	public void setUp()
	{
		array = new IntArray();
	}

	@Test
	public void testcontructor()
	{
		assertTrue(array.size() == 0);
		assertFalse(array.indexOf(0) != -1);
	}

	@Test
	public void testadd()
	{
		assertTrue(array.size() == 0);
		assertFalse(array.indexOf('b') != -1);
		array.add('b');
		assertTrue(array.indexOf('b') != -1);

		assertTrue(array.size() == 1);
		assertFalse(array.indexOf('a') != -1);
		array.add('a');
		assertTrue(array.indexOf('a') != -1);

		assertTrue(array.size() == 2);
		assertFalse(array.indexOf('l') != -1);
		array.add('l');
		assertTrue(array.indexOf('l') != -1);

		assertTrue(array.size() == 3);
		assertFalse(array.indexOf('q') != -1);
		array.add('q');
		assertTrue(array.indexOf('q') != -1);

		assertTrue(array.size() == 4);
		assertFalse(array.indexOf('n') != -1);
		array.add('n');
		assertTrue(array.indexOf('n') != -1);

		assertTrue(array.size() == 5);
		assertTrue(array.indexOf('a') != -1);
		array.add('a');
		assertTrue(array.indexOf('a') != -1);
		assertTrue(array.size() == 6);
	}

	@Test
	public void testadd2()
	{
		array.add(0, 'b');
		array.add(5, 'a');
		assertTrue(array.indexOf('b') == 0);
		assertTrue(array.indexOf('a') == 5);
		assertTrue(array.indexOf(0) == 1);
		assertTrue(array.lastIndexOf(0) == 4);
	}

	@Test
	public void testremove()
	{
		array.add('b');
		array.add('a');
		array.add('l');
		array.add('q');
		array.add('n');
		array.add('a');

		assertTrue(array.removeAtIndex(0) == 'b');
		assertTrue(array.indexOf('a') == 0);
		assertTrue(array.lastIndexOf('a') == 4);
		assertTrue(array.size() == 5);

		assertTrue(array.removeAtIndex(4) == 'a');
		assertTrue(array.indexOf('a') == 0);
		assertTrue(array.lastIndexOf('a') == 0);
		assertTrue(array.size() == 4);

		assertTrue(array.removeAtIndex(0) == 'a');
		assertTrue(array.indexOf('a') == -1);
		assertTrue(array.lastIndexOf('a') == -1);
		assertTrue(array.size() == 3);
		
		assertTrue(array.removeAtIndex(2) == 'n');
		assertTrue(array.indexOf('n') == -1);
		assertTrue(array.lastIndexOf('n') == -1);
		assertTrue(array.size() == 2);

		assertTrue(array.removeAtIndex(0) == 'l');
		assertTrue(array.indexOf('l') == -1);
		assertTrue(array.lastIndexOf('l') == -1);
		assertTrue(array.size() == 1);

		assertTrue(array.removeAtIndex(0) == 'q');
		assertTrue(array.indexOf('q') == -1);
		assertTrue(array.lastIndexOf('q') == -1);
		assertTrue(array.size() == 0);
	}

	@Test
	public void testgetAndSet()
	{
		array.add(0);
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);
		array.add(5);

		assertTrue(array.get(0) == 0);
		assertTrue(array.get(1) == 1);
		assertTrue(array.get(2) == 2);
		assertTrue(array.get(3) == 3);
		assertTrue(array.get(4) == 4);
		assertTrue(array.get(5) == 5);

		array.set(0, 'b');
		array.set(1, 'a');
		array.set(2, 'l');
		array.set(3, 'q');
		array.set(4, 'n');
		array.set(5, 'a');

		assertTrue(array.get(0) == 'b');
		assertTrue(array.get(1) == 'a');
		assertTrue(array.get(2) == 'l');
		assertTrue(array.get(3) == 'q');
		assertTrue(array.get(4) == 'n');
		assertTrue(array.get(5) == 'a');
	}

	@Test
	public void testiterator()
	{
		array.add('b');
		array.add('a');
		array.add('l');
		array.add('q');
		array.add('n');
		array.add('a');

		IntIterator intIterator = array.iterator();

		assertTrue(intIterator.hasNext());
		assertTrue(intIterator.numRemaining() == 6);
		assertTrue(intIterator.next() == 'b');

		assertTrue(intIterator.hasNext());
		assertTrue(intIterator.numRemaining() == 5);
		assertTrue(intIterator.next() == 'a');

		assertTrue(intIterator.hasNext());
		assertTrue(intIterator.numRemaining() == 4);
		assertTrue(intIterator.next() == 'l');

		assertTrue(intIterator.hasNext());
		assertTrue(intIterator.numRemaining() == 3);
		assertTrue(intIterator.next() == 'q');

		assertTrue(intIterator.hasNext());
		assertTrue(intIterator.numRemaining() == 2);
		assertTrue(intIterator.next() == 'n');

		assertTrue(intIterator.hasNext());
		assertTrue(intIterator.numRemaining() == 1);
		assertTrue(intIterator.next() == 'a');
		
		assertFalse(intIterator.hasNext());
		assertTrue(intIterator.numRemaining() == 0);
	}

	@Test
	public void testtoArray()
	{
		array.add('b');
		array.add('a');
		array.add('l');
		array.add('q');
		array.add('n');
		array.add('a');

		int[] intArray = array.toArray();
		assertTrue(intArray.length == array.size());
		assertTrue(intArray[0] == 'b');
		assertTrue(intArray[1] == 'a');
		assertTrue(intArray[2] == 'l');
		assertTrue(intArray[3] == 'q');
		assertTrue(intArray[4] == 'n');
		assertTrue(intArray[5] == 'a');
	}
}
