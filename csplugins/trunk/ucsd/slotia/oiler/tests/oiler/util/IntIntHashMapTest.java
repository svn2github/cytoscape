package oiler.util;

import java.util.HashSet;
import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

public class IntIntHashMapTest
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(IntIntHashMapTest.class);
	}

	IntIntHashMap hash;

	@Before
	public void setUp()
	{
		hash = new IntIntHashMap();
	}

	@Test
	public void testconstructor()
	{
		assertTrue(hash.size() == 0);
		assertTrue(hash.isEmpty());
		assertTrue(hash.get(0) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(hash.get(1) == IntIntHashMap.KEY_NOT_FOUND);
		assertTrue(hash.get(2) == IntIntHashMap.KEY_NOT_FOUND);
	}

	@Test
	public void testgetPutAndSize()
	{
		int size = 12;
		
		int[] keys = new int[size];
		int[] values = new int[size];

		for (int i = 0; i < size; i++)
		{
			keys[i] = i;
			values[i] = i * 4;
		}

		for (int i = 0; i < size; i++)
			hash.put(keys[i], values[i]);

		for (int i = 0; i < size; i++)
		{
			int result = hash.get(keys[i]);
			assertTrue(result == values[i]);
			assertTrue(hash.containsKey(keys[i]));
		}

		assertTrue(hash.size() == size);
	}

	@Test
	public void testsizeAndClear()
	{
		assertTrue(hash.size() == 0);
		hash.put(0, 0);
		assertTrue(hash.size() == 1);
		hash.put(0, 3);
		assertTrue(hash.size() == 1);
		hash.put(1, 7);
		assertTrue(hash.size() == 2);

		hash.clear();
		assertTrue(hash.size() == 0);
		assertFalse(hash.containsKey(0));
		assertFalse(hash.containsKey(1));
		assertFalse(hash.containsKey(2));
	}

	@Test
	public void testCopyConstructor()
	{
		hash.put(90, 83);
		hash.put(40, 77);
		hash.put(4, 68);

		IntIntHashMap newHash = new IntIntHashMap(hash);

		assertTrue(newHash.containsKey(90));
		assertTrue(newHash.get(90) == 83);
		assertTrue(newHash.containsKey(40));
		assertTrue(newHash.get(40) == 77);
		assertTrue(newHash.containsKey(4));
		assertTrue(newHash.get(4) == 68);
		assertTrue(newHash.size() == 3);
		
		assertFalse(newHash.containsKey(0));
		assertFalse(newHash.containsKey(1));
		assertFalse(newHash.containsKey(2));
	}

	@Test
	public void testKeysIterator()
	{
		hash.put(90, 83);
		hash.put(40, 77);
		hash.put(4, 68);

		IntIterator keysIterator = hash.keysIterator();
		HashSet<Integer> keys = new HashSet<Integer>();
		while (keysIterator.hasNext())
			keys.add(keysIterator.next());
		assertTrue(keys.contains(90));
		assertTrue(keys.contains(40));
		assertTrue(keys.contains(4));
		assertTrue(keys.size() == 3);
	}

	@Test
	public void testValuesIterator()
	{
		hash.put(90, 83);
		hash.put(40, 77);
		hash.put(4, 68);

		IntIterator valuesIterator = hash.valuesIterator();
		HashSet<Integer> values = new HashSet<Integer>();
		while (valuesIterator.hasNext())
			values.add(valuesIterator.next());
		assertTrue(values.contains(83));
		assertTrue(values.contains(77));
		assertTrue(values.contains(68));
		assertTrue(values.size() == 3);
	}

	@Test
	public void testRemove()
	{
		hash.put(90, 83);
		hash.put(40, 77);
		hash.put(4, 68);

		assertTrue(hash.size() == 3);
		assertTrue(hash.remove(90) == 83);
		assertFalse(hash.containsKey(90));
		assertFalse(hash.containsValue(83));
		assertTrue(hash.get(90) == IntIntHashMap.KEY_NOT_FOUND);

		assertTrue(hash.size() == 2);
		assertTrue(hash.remove(40) == 77);
		assertFalse(hash.containsKey(40));
		assertFalse(hash.containsValue(77));
		assertTrue(hash.get(40) == IntIntHashMap.KEY_NOT_FOUND);

		assertTrue(hash.size() == 1);
		assertTrue(hash.remove(4) == 68);
		assertFalse(hash.containsKey(4));
		assertFalse(hash.containsValue(68));
		assertTrue(hash.get(4) == IntIntHashMap.KEY_NOT_FOUND);

		assertTrue(hash.size() == 0);

		hash.put(90, 83);
		hash.put(40, 77);
		hash.put(4, 68);

		assertTrue(hash.size() == 3);
		assertTrue(hash.containsKey(90));
		assertTrue(hash.containsKey(40));
		assertTrue(hash.containsKey(4));
	}
}
