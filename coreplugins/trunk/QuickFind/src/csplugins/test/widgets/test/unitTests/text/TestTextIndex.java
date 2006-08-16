package csplugins.test.widgets.test.unitTests.text;

import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.widgets.autocomplete.index.TextIndexFactory;
import junit.framework.TestCase;

/**
 * Unit Tests for TextIndexImpl.
 *
 * @author Ethan Cerami.
 */
public class TestTextIndex extends TestCase {

    /**
     * First Round of Test Cases.
     */
    public void testTextIndex0() {
        TextIndex textIndex = TextIndexFactory.createDefaultTextIndex();
        textIndex.addToIndex("rain", new Integer(1));
        textIndex.addToIndex("rain", new Integer(2));
        textIndex.addToIndex("rainbow", new Integer(3));
        textIndex.addToIndex("rainbow trout", new Integer(4));
        textIndex.addToIndex("RABBIT", new Integer(5));

        assertEquals(4, textIndex.getNumKeys());

        //  Test with prefix:  "ra"
        Hit hits[] = textIndex.getHits("ra", Integer.MAX_VALUE);
        assertEquals(4, hits.length);
        assertEquals("rabbit", hits[0].getKeyword());
        assertEquals("rain", hits[1].getKeyword());
        assertEquals("rainbow", hits[2].getKeyword());
        assertEquals("rainbow trout", hits[3].getKeyword());

        //  Test with prefix "rain"
        hits = textIndex.getHits("rain", Integer.MAX_VALUE);
        assertEquals(3, hits.length);
        assertEquals("rain", hits[0].getKeyword());
        assertEquals("rainbow", hits[1].getKeyword());
        assertEquals("rainbow trout", hits[2].getKeyword());

        //  Test with prefix "RAIN".  Verifies that search is
        // case *insensitive*.
        hits = textIndex.getHits("RAIN", Integer.MAX_VALUE);
        assertEquals(3, hits.length);
        assertEquals("rain", hits[0].getKeyword());
        assertEquals("rainbow", hits[1].getKeyword());
        assertEquals("rainbow trout", hits[2].getKeyword());

        //  Test Existence of Embedded Objects
        hits = textIndex.getHits("rain", Integer.MAX_VALUE);
        assertEquals(2, hits[0].getAssociatedObjects().length);
        assertEquals("1", hits[0].getAssociatedObjects()[0].toString());
        assertEquals("2", hits[0].getAssociatedObjects()[1].toString());

        hits = textIndex.getHits("rainbow", Integer.MAX_VALUE);
        assertEquals(1, hits[0].getAssociatedObjects().length);
        assertEquals("3", hits[0].getAssociatedObjects()[0].toString());

        //  Try getting hits for an undefined key
        hits = textIndex.getHits("cytoscape", Integer.MAX_VALUE);
        assertEquals(0, hits.length);

        //  Now, try resetting the index
        textIndex.resetIndex();

        //  Verify that keys are no longer available
        hits = textIndex.getHits("rain", Integer.MAX_VALUE);
        assertEquals(0, hits.length);

        //  Verify toString() makes sense
        assertEquals("Text Index:  [Total number of keys:  0]",
                textIndex.toString());
    }
}