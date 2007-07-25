import cern.colt.list.IntArrayList;
import infovis.utils.*;
import infovis.utils.IntIntSortedMap;
import infovis.utils.RBTree;
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
 * @version $Revision: 1.1 $
 */
public class TestRBTree extends TestCase {

    public TestRBTree(String name) {
        super(name);
    }
    
    public void testRBTree() {
        RBTree rbtree = new RBTree();
        IntIntSortedMap sortedMap = new IntIntSortedMap();
        
        int i;
        IntArrayList ial = new IntArrayList(100);
        for (i = 0; i < 100; i++) {
            ial.add(i);
        }
        ial.shuffle();
        for (i = 0; i < 100; i++) {
            rbtree.put(ial.get(i), ial.get(i));
            sortedMap.put(ial.get(i), ial.get(i));
        }
        
        RowIterator rbi = rbtree.keyIterator();
        RowIterator smi = sortedMap.keyIterator();
        while (rbi.hasNext() && smi.hasNext()) {
            assertEquals(rbi.nextRow(), smi.nextRow());
        }
        assertEquals(rbi.hasNext(), smi.hasNext());
        
        for (i = 0; i < 10; i++) {
            rbtree.remove(ial.get(i));
            sortedMap.remove(ial.get(i));
        }
        rbi = rbtree.keyIterator();
        smi = sortedMap.keyIterator();
        while (rbi.hasNext() && smi.hasNext()) {
            assertEquals(rbi.nextRow(), smi.nextRow());
        }
        assertEquals(rbi.hasNext(), smi.hasNext());
    }

}
