package oiler.util;

import java.util.NoSuchElementException;

/**
 * A hash map where keys and values are <code>int</code>s.
 *
 * @author Samad Lotia
 */
public class LongIntHashMap
{
	protected final class Entry
	{
		public long key;
		public int value;
		public Entry next;

		public Entry() {}
		public Entry(final Entry other)
		{
			this.key = other.key;
			this.value = other.value;
		}
		
		public Entry(long key, int value)
		{
			this.key = key;
			this.value = value;
		}
	}

	/**
	 * Returned by <code>get()</code> or <code>put()</code> if the key
	 * is not in the table.
	 */
	public static final int KEY_NOT_FOUND = Integer.MIN_VALUE;

	/**
	 * Specifies the maximum load factor for the hash table.
	 * This value must be between 0, exclusive, and 1, inclusive.
	 * Memory usage is proportional to this value, and average
	 * lookup time is inversely proportional to this value.
	 * For example, reducing this value by 50% reduces
	 * memory usage by 50% and increases lookup time by 50%.
	 * A value of 1.00 has the worst memory usage but
	 * the best lookup times, and a value approaching 0.0
	 * has the best memory usage but the worst lookup times.
	 *
	 * <p>The load factor is the number (key,value) pairs
	 *    stored in the table divided by the capacity of the table.</p>
	 *
	 * <p>The table will ensure the load factor is never
	 *    greater than <code>MAX_LOAD_FACTOR</code>.</p>
	 */
	public static final float MAX_LOAD_FACTOR = 1.00f;

	/**
	 * Specifies the default capacity of the table.
	 */
	public static final int DEFAULT_CAPACITY = 2;
	
	protected Entry[] buckets;	// Buckets of the chained hash table
	protected int size;		// Number of (key,value) pairs stored

	/**
	 * Construct LongIntHashMap with capacity <code>DEFAULT_CAPACITY</code>.
	 */
	public LongIntHashMap()
	{
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Construct LongIntHashMap with specified capacity.
	 * @param capacity Initial capacity for the table. Must be greater than 0.
	 *                 <b>Note:</b> <code>capacity</code> should be a power of 2.
	 *                 If it is not, the next higher number after <code>capacity</code>
	 *                 that is a power of 2 will be used. For example,
	 *                 if <code>capacity</code> is 4, the table will have
	 *                 a capacity of 4; if <code>capacity</code> is 5, the
	 *                 table will have a capacity of 8.
	 * @throws IllegalArgumentException Thrown if <code>capacity</code> is less than 1.
	 * @throws IllegalStateException Thrown if <code>capacity</code> is larger than <code>Integer.MAX_VALUE</code>.
	 */
	public LongIntHashMap(final int capacity)
	{
		buckets = new Entry[correctCapacity(capacity)];
		size = 0;
	}

	private final int correctCapacity(final int capacity)
	{
		if (capacity < 1)
			throw new IllegalArgumentException("capacity < 1");

		int correctCapacity = 1;
		while (correctCapacity < capacity)
		{
			final int doubleCapacity = correctCapacity << 1;

			// check for overflow--this occurs when multiplying by 2
			// results in a smaller number
			if (doubleCapacity < correctCapacity)
				throw new IllegalStateException("capacity is larger than Integer.MAX_VALUE");
				
			correctCapacity = doubleCapacity;
		}

		return correctCapacity;
	}

	/**
	 * Construct an LongIntHashMap will all the elements of <code>other</code>.
	 */
	public LongIntHashMap(final LongIntHashMap other)
	{
		this.size = other.size;
		buckets = new Entry[other.buckets.length];
		for (int i = 0; i < other.buckets.length; i++)
		{
			Entry otherEntry = other.buckets[i];
			if (otherEntry == null)
				continue;

			Entry entry = new Entry(otherEntry);
			buckets[i] = entry;

			while (otherEntry.next != null)
			{
				entry.next = new Entry(otherEntry.next);
				entry = entry.next;
				otherEntry = otherEntry.next;
			}
		}
	}

	/**
	 * Returns the capacity of the table.
	 */
	public int capacity()
	{
		return buckets.length;
	}

	/**
	 * Return the number of (key,value) pairs stored in the table.
	 */
	public int size()
	{
		return size;
	}

	/**
	 * Empty the table of all (key,value) pairs but maintain the
	 * same capacity.
	 */
	public void empty()
	{
		buckets = new Entry[buckets.length];
		size = 0;
	}

	protected final int hash(final long key)
	{
		// hash(key) == key % capacity, but since
		// capacity is always a power of 2, we can use
		// bit-wise operators to greatly enhance the performance.
		//
		// Here is a demonstration of how it works, but in 8 bits:
		// Given:  capacity = 00100000 (this is arbitrary)
		//       capacity-1 = 00011111
		// Given:       key = XXXXXXXX
		// ---------------------------
		// key & capacity-1 = 000XXXXX
		// Now 0 <= hash(key) < capacity

		final int capacity = buckets.length;
		return (int) (key & (capacity - 1));
	}

	// Find the Entry in the table with key, or return null if
	// key is not found
	protected final Entry findEntry(final long key)
	{
		final int bucketIndex = hash(key);
		Entry entry = buckets[bucketIndex];
		while (entry != null)
		{
			if (entry.key == key)
				return entry;
			entry = entry.next;
		}
		return null;
	}

	/**
	 * Determines if a key is in the table.
	 */
	public boolean containsKey(final long key)
	{
		return (findEntry(key) != null);
	}

	/**
	 * Determines if a value is in the table.
	 * Performance issues: this method is O(n).
	 */
	public boolean containsValue(final int value)
	{
		for (int bucketIndex = 0; bucketIndex < buckets.length; bucketIndex++)
		{
			Entry entry = buckets[bucketIndex];
			while (entry != null)
			{
				if (entry.value == value)
					return true;
				entry = entry.next;
			}
		}
		return false;
	}

	/**
	 * Returns the value associated with <code>key</code> or
	 *         <code>KEY_NOT_FOUND</code> if the key is not in the table.
	 */
	public int get(final long key)
	{
		final Entry entry = findEntry(key);

		if (entry == null)
			return KEY_NOT_FOUND;
		else
			return entry.value;
	}

	/**
	 * Return true if the table has no (key,value) pairs stored in it.
	 */
	public boolean isEmpty()
	{
		return (size == 0);
	}

	/**
	 * Inserts a (key,value) pair into the table.
	 * If <tt>key</tt> is already in the table, its value is overwritten
	 * with <tt>value</tt>.
	 *
	 * @return the old value associated with <code>key</code> if <code>key</code>
	 * was already in the table, or <code>KEY_NOT_FOUND</code> if <code>key</code>
	 * was not in the table.
	 */
	public int put(final long key, final int value)
	{
		final int bucketIndex = hash(key);
		
		// Check to see if we already have the key
		Entry entry = buckets[bucketIndex];
		while (entry != null)
		{
			if (entry.key == key)
			{
				final int oldValue = entry.value;
				entry.value = value;
				return oldValue;
			}
			entry = entry.next;
		}
		
		// If we reached here, we didn't find a key.
		// Insert a new Entry into the table
		final Entry newEntry = new Entry(key, value);
		newEntry.next = buckets[bucketIndex];
		buckets[bucketIndex] = newEntry;

		// Make sure inserting the element will not
		// cause the load factor to exceed MAX_LOAD_FACTOR
		size++;
		if (size > buckets.length * MAX_LOAD_FACTOR)
			increaseCapacity(buckets.length * 2);

		return KEY_NOT_FOUND;
	}

	/**
	 * Removes a (key,value) pair.
	 * @return true if the key was in the table
	 * and was removed, false if the key is not
	 * in the table.
	 */
	public boolean remove(final long key)
	{
		final int bucketIndex = hash(key);
		Entry entry = buckets[bucketIndex];

		if (entry == null)
			return false;
		else if (entry.key == key)
		{
			// Check our first entry. We do this because
			// the buckets array points
			// to the Entry, not another Entry object.
			buckets[bucketIndex] = entry.next;
			entry = null;
			size--;
			return true;
		}
		else
		{
			// Search the linked list of Entry's
			// and keep track of which Entry
			// points to the entry we're looking
			// for in order to update its next field.
			Entry prev = entry;
			entry = entry.next;
			while (entry != null)
			{
				if (entry.key == key)
				{
					prev.next = entry.next;
					entry = null;
					size--;
					return true;
				}
				prev = entry;
				entry = entry.next;
			}
		}

		return false;
	}

	/**
	 * Increase the capacity of the table.
	 * @param newCapacity New capacity for the table. Must be greater than the current capacity.
	 *                    <b>Note:</b> <tt>newCapacity</tt> should be a power of 2.
	 *                    If it is not, the next higher number after <tt>newCapacity</tt>
	 *                    that is a power of 2 will be used. For example,
	 *                    if <tt>newCapacity</tt> is 4, the table will have
	 *                    a capacity of 4; if <tt>newCapacity</tt> is 5, the
	 *                    table will have a capacity of 8.
	 * @throws IllegalArgumentException Thrown if <tt>newCapacity</tt> is less than the current capacity.
	 */
	public void increaseCapacity(final int newCapacity)
	{
		if (buckets.length > newCapacity)
			throw new IllegalArgumentException("capacity > newCapacity");
		
		// Save the old table
		final Entry[] oldBuckets = buckets;
		// Initialize buckets for new table
		buckets = new Entry[correctCapacity(newCapacity)];
		// Initialize size; let put() set size to the correct value
		size = 0;
		
		// Rehash all the old (key,value) pairs into the new table
		for (int oldBucketIndex = 0; oldBucketIndex < oldBuckets.length; oldBucketIndex++)
		{
			Entry oldEntry = oldBuckets[oldBucketIndex];
			while (oldEntry != null)
			{
				put(oldEntry.key, oldEntry.value);
				oldEntry = oldEntry.next;
			}
		}
	}

	/**
	 * Return an iterator for all the values in the table.
	 */
	public IntIterator valuesIterator()
	{
		return new ValuesIterator();
	}

	private class ValuesIterator implements IntIterator
	{
		private int remaining = size;
		private int bucketIndex = 0;
		private Entry entry = buckets[bucketIndex];

		public boolean hasNext()
		{
			return (remaining > 0);
		}

		public int next()
		{
			if (remaining <= 0)
				throw new NoSuchElementException();

			while(entry == null)
				entry = buckets[++bucketIndex];

			final int result = entry.value;
			entry = entry.next;
			remaining--;
			return result;
		}

		public int numRemaining()
		{
			return remaining;
		}
	}

}
