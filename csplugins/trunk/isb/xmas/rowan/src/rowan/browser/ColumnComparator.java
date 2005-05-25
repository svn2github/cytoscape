/*
=====================================================================

  ColumnComparator.java

  Created by Claude Duguay
  Copyright (c) 2002

=====================================================================
*/
package rowan.browser;

import java.util.Comparator;
import java.util.Vector;

public class ColumnComparator implements Comparator {
	protected int index;
	protected boolean ascending;

	public ColumnComparator(int index, boolean ascending) {
		this.index = index;
		this.ascending = ascending;
	}

	public int compare(Object one, Object two) {

    

		if (one instanceof Vector && two instanceof Vector) {
			Vector vOne = (Vector) one;
			Vector vTwo = (Vector) two;
			Object oOne = vOne.elementAt(index);
			Object oTwo = vTwo.elementAt(index);
		
      if (oOne instanceof Comparable && oTwo instanceof Comparable) {

				if (oOne instanceof String && oTwo instanceof String) {
					String sOne = (String) oOne;
					String sTwo = (String) oTwo;
					if (StringUtil.firstStringSortsBeforeSecond(sOne, sTwo, ascending)) {
						return -1;
					}
					else {
						return 1;
					}
				}
				else {
					Comparable cOne = (Comparable) oOne;
					Comparable cTwo = (Comparable) oTwo;
					if (ascending) {
						return cTwo.compareTo(cOne);
					}
					else {
						return cOne.compareTo(cTwo);
					}
				}
			}
		}
		return 1;
	}
}
