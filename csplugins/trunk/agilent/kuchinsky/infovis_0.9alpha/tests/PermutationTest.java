import infovis.utils.Permutation;
import infovis.utils.RowComparator;
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
 * @version $Revision: 1.1 $
 */
public class PermutationTest extends TestCase {

    public PermutationTest(String name) {
        super(name);
    }
    
    public void testInvariant(Permutation perm) {
        for (int i = 0; i < perm.getDirectCount(); i++) {
            int inverse = perm.getInverse(i);
            if (inverse != -1) {
                assertEquals(i, perm.getDirect(inverse));
            }
        }
        
    }
    
    public void testPermutation() {
        Permutation perm = new Permutation();
        
        assertEquals(0, perm.getDirectCount());
        assertEquals(0, perm.getInverseCount());
        
        perm.sort(10, new RowComparator() {
            public boolean isValueUndefined(int row) {
                return false;
            }

            public int compare(int a, int b) {
                return a - b;
            }

            public boolean equals(Object arg0) {
                return false;
            }
        });
        assertEquals(10, perm.getDirectCount());
        assertEquals(10, perm.getInverseCount());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, perm.getDirect(i));
            assertEquals(i, perm.getInverse(i));
        }
        testInvariant(perm);
        
        perm.sort(10, new RowComparator() {
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
        assertEquals(10, perm.getInverseCount());
        for (int i = 0; i < 10; i++) {
            assertEquals(9-i, perm.getDirect(i));
            assertEquals(9-i, perm.getInverse(i));
        }
        testInvariant(perm);
        
        perm.sort(10, new RowComparator() {
            public boolean isValueUndefined(int row) {
                return row == 5;
            }

            public int compare(int a, int b) {
                return b - a;
            }

            public boolean equals(Object arg0) {
                return false;
            }
        });
        assertEquals(9, perm.getDirectCount());
        assertEquals(9, perm.getInverseCount());
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
