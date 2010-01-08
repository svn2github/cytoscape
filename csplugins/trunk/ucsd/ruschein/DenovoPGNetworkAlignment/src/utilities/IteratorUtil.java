package utilities;

import oiler.util.IntIterator;

import org.apache.commons.collections.primitives.ArrayIntList;

public class IteratorUtil {

	public static org.apache.commons.collections.primitives.IntIterator copyIterator(IntIterator ii)
	{
		ArrayIntList ii2 = new ArrayIntList(ii.numRemaining());
		while (ii.hasNext())
			ii2.add(ii.next());
		
		return ii2.iterator();
	}
	
}
