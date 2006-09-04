package csplugins.test.widgets.test.unitTests.text;

import junit.framework.TestCase;
import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.IndexFactory;

import java.util.List;

/**
 * Unit Tests for NumberIndexImpl.
 *
 * @author Ethan Cerami.
 */
public class TestNumberIndex extends TestCase {

    /**
     * Test Number Index with Integer Values.
     */
    public void testNumberIndexInteger() {
        NumberIndex numberIndex = IndexFactory.createDefaultNumberIndex();
        numberIndex.addToIndex(new Integer(5), "a");
        numberIndex.addToIndex(new Integer(2), "b");
        numberIndex.addToIndex(new Integer(50), "c");
        numberIndex.addToIndex(new Integer(15), "d");
        numberIndex.addToIndex(new Integer(3), "e");
        numberIndex.addToIndex(new Integer(5), "f");
        numberIndex.addToIndex(new Integer(2), "g");

        //  Test Min / Max values
        Number min = numberIndex.getMinimumValue();
        assertEquals(2, min);

        Number max = numberIndex.getMaximumValue();
        assertEquals(50, max);

        List list = numberIndex.getRange(10, 51);
        assertEquals(2, list.size());
        assertEquals("d", list.get(0));
        assertEquals("c", list.get(1));

        list = numberIndex.getRange(5, 6);
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("f", list.get(1));
    }

    /**
     * Test Number Index with Double values.
     */
    public void testNumberIndexDouble() {
        NumberIndex numberIndex = IndexFactory.createDefaultNumberIndex();
        numberIndex.addToIndex(new Double(0.1), "a");
        numberIndex.addToIndex(new Double(0.2), "b");
        numberIndex.addToIndex(new Double(0.8), "c");
        numberIndex.addToIndex(new Double(0.9), "d");
        numberIndex.addToIndex(new Double(0.99), "e");
        numberIndex.addToIndex(new Double(0.7), "f");
        numberIndex.addToIndex(new Double(0.88), "g");

        //  Test Min / Max values
        Number min = numberIndex.getMinimumValue();
        assertEquals(0.1, min);
        assertEquals (0.1, min.doubleValue(), 0.001);

        Number max = numberIndex.getMaximumValue();
        assertEquals(.99, max.doubleValue(), 0.001);

        List list = numberIndex.getRange(0.1, 0.3);
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));

        list = numberIndex.getRange(0.0, 1.0);
        assertEquals(7, list.size());
    }
}
