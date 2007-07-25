import infovis.utils.*;
import junit.framework.TestCase;
/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class PermutationTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class PermutationTest extends TestCase {

    public PermutationTest(String name) {
        super(name);
    }
    
    public void testInvariant(Permutation perm) {
        IntIntSortedMap map = new IntIntSortedMap();
        int size = perm.getInverseSize();
        for (int i = 0; i < size; i++) {
            int inverse = perm.getInverse(i);
            if (!perm.isValueUndefined(i)) {
                assertEquals(i, perm.getDirect(inverse));
                map.put(inverse, i);
            }
            else {
                assertTrue("Index already defined", !map.containsKey(inverse));
                assertTrue("Index in bad range", inverse < size);
            }
        }
        assertEquals(map.size(), perm.getDirectCount());
        for (int i = 0; i < perm.getDirectCount(); i++) {
            int v = perm.getDirect(i);
            assertEquals(perm.getInverse(v), i);
        }
    }
    
    public void testPermutation() {
        Permutation perm = new Permutation();
        
        assertEquals(0, perm.getDirectCount());
        assertEquals(0, perm.getInverseSize());
        
        perm.fillPermutation(10);
        perm.sort(new RowComparator() {
            public boolean isValueUndefined(int row) {
                return false;
            }

            public int compare(int a, int b) {
                return a - b;
            }
        });
        assertEquals(10, perm.getDirectCount());
        assertEquals(10, perm.getInverseSize());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, perm.getDirect(i));
            assertEquals(i, perm.getInverse(i));
        }
        testInvariant(perm);
        
        perm.fillPermutation(10);
        perm.sort(new RowComparator() {
            public boolean isValueUndefined(int row) {
                return false;
            }

            public int compare(int a, int b) {
                return b - a;
            }

            public boolean equals(Object arg0) {
                return false;
            }
        });
        assertEquals(10, perm.getDirectCount());
        assertEquals(10, perm.getInverseSize());
        for (int i = 0; i < 10; i++) {
            assertEquals(9-i, perm.getDirect(i));
            assertEquals(9-i, perm.getInverse(i));
        }
        testInvariant(perm);
        
        RowComparator comp = new RowComparator() {
            public boolean isValueUndefined(int row) {
                return row == 5;
            }

            public int compare(int a, int b) {
                return b - a;
            }

            public boolean equals(Object arg0) {
                return false;
            }
        };
        perm.fillPermutation(10, comp);
        perm.sort(comp);
        assertEquals(9, perm.getDirectCount());
        assertEquals(10, perm.getInverseSize());
        for (int i = 0; i < 9; i++) {
            if (i < 4) {
                assertEquals(9-i, perm.getDirect(i));
            }
            else {
                assertEquals(8-i, perm.getDirect(i));
            }
        }
        testInvariant(perm);        
    }

}
