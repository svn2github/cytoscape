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
 * Class TestRBTree
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class TestRBTree extends TestCase {

    public TestRBTree(String name) {
        super(name);
    }
    
    public void testRBTree() {
        RBTree rbtree = new RBTree();
        IntIntSortedMap sortedMap = new IntIntSortedMap();
        
        int i;
        Permutation perm = new Permutation(100);
        perm.shuffle(100);
        for (i = 0; i < 100; i++) {
            rbtree.put(perm.getDirect(i), perm.getDirect(i));
            sortedMap.put(perm.getDirect(i), perm.getDirect(i));
        }
        
        RowIterator rbi = rbtree.keyIterator();
        RowIterator smi = sortedMap.keyIterator();
        while (rbi.hasNext() && smi.hasNext()) {
            assertEquals(rbi.nextRow(), smi.nextRow());
        }
        assertEquals(rbi.hasNext(), smi.hasNext());
        
        for (i = 0; i < 10; i++) {
            rbtree.remove(perm.getDirect(i));
            sortedMap.remove(perm.getDirect(i));
        }
        rbi = rbtree.keyIterator();
        smi = sortedMap.keyIterator();
        while (rbi.hasNext() && smi.hasNext()) {
            assertEquals(rbi.nextRow(), smi.nextRow());
        }
        assertEquals(rbi.hasNext(), smi.hasNext());
        
        rbtree.clear();
        assertEquals(0, rbtree.size());
        rbtree.put(144,144);
        rbtree.put(145,145);
        RowIterator iter = rbtree.valueIterator(); 
        assertTrue(iter.hasNext());
        assertEquals(144, iter.nextRow());
        assertTrue(iter.hasNext());
        assertEquals(145, iter.nextRow());
        assertTrue(!iter.hasNext());
        rbtree.clear();
        for (i = 0; i < 100; i++) {
            rbtree.put(perm.getDirect(i), perm.getDirect(i));
        }
        i = 50;
        for (rbi = rbtree.keyIterator(50); rbi.hasNext(); i++) {
            int key = rbi.nextRow();
            assertEquals(i, key);
            rbi.remove();
        }
        assertEquals(50, rbtree.size());
    }

}
