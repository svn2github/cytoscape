import infovis.Tree;
import infovis.tree.DefaultTree;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class TreeTest extends TestCase {

    /**
     * Constructor for TreeTest.
     */
    public TreeTest(String name) {
        super(name);
    }

    public void testTree() {
        Tree tree = new DefaultTree();
        //TableTest.testInvariants(tree);

        
        for (int i = 1; i < 10; i++) {
            int node = tree.addNode(Tree.ROOT);
            assertEquals(i, node);
            assertEquals(Tree.ROOT, tree.getParent(node));
        }
        assertEquals(10, tree.getRowCount());
        assertEquals(10, tree.getNodeCount());
        for (int i = 1; i < 10; i++) {
            int node = tree.getChild(Tree.ROOT, i-1);
            assertEquals(i, node);
        }
        tree.removeNode(1);
        assertEquals(9, tree.getNodeCount());
        assertEquals(2, tree.getChild(Tree.ROOT, 0));
        assertEquals(1, tree.addNode(Tree.ROOT));
    }

//    private static void testClear(Tree tree) {
//        int count = tree.getColumnCount();
//        tree.clear();
//        assertEquals(tree.getRowCount(), 1);
//        assertEquals(tree.getColumnCount(), count);
//        assertEquals(0, tree.getNodeCount());
//    }
}
