import infovis.Column;
import infovis.column.*;
import infovis.column.format.CategoricalFormat;

import java.io.*;
import java.text.ParseException;

import junit.framework.TestCase;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.17 $
 */
public class ColumnsTest extends TestCase {

    public ColumnsTest(String name) {
        super(name);
    }
    

    public void testColumnProperties(Column column) {
        column.clear();
        assertEquals(column.size(), 0);
        assertEquals(column.isEmpty(), true);
        String name = column.getName();
        column.setName("#_12_");
        assertEquals(column.getName(), "#_12_");
        assertEquals(column.isInternal(), true);
        column.setName(name);
        assertEquals(column.getName(), name);
        assertEquals(column.isValueUndefined(10), true);
        try {
            column.setValueAt(10, null);
            assertEquals(11, column.size());
        }
        catch (ParseException e) {
            assertEquals(0, column.size());
        }

        int i;

        for (i = 0; i < 10; i++) {
            assertEquals(column.isValueUndefined(i), true);
        }
        try {
            column.setValueAt(2, "2");
            assertEquals(column.isValueUndefined(2), false);
        }
        catch (ParseException e) {
            assertEquals(column.isValueUndefined(2), true);
        }
        
        testSerializeColumn(column);
        
        column.clear();
        
    }
    
    public void testSerializeColumn(Column column) {
        try {
        FileOutputStream f = new FileOutputStream("tmp");
        ObjectOutput sout = new ObjectOutputStream(f);
        sout.writeObject(column);
        sout.flush();
        sout.close();
        
        FileInputStream in = new FileInputStream("tmp");
        ObjectInputStream s = new ObjectInputStream(in);
        Column c2 = (Column)s.readObject();
        assertTrue("Serialized object differs from original", column.equals(c2));
        }
        catch(Exception e) {
            assertTrue(
                    "Exception while writing or readeing serialized object", 
                    false);
        }
    }

    public void testNumberColumnProperties(NumberColumn column) {
        testColumnProperties(column);

    }

    public void testIntColumn() {
        IntColumn column = new IntColumn("IntColumn");
        testNumberColumnProperties(column);
        try {
            column.setValueAt(2, "32");
            assertEquals(column.get(2), 32);
        }
        catch (ParseException e) {
            assertTrue("Parse error for IntColumn", false);
        }
        try {
            column.setValueAt(2, "32.5");
            assertEquals(column.get(2), 32);
            assertTrue("Too lax parsing for IntColumn", false);
        }
        catch (ParseException e) {
            assertTrue("Parse error for IntColumn", true);
        }
        try {
            column.setValueAt(3, "");
            assertTrue(
                "Invalid empty value parsed for IntColumn",
                false);
        }
        catch (ParseException e) {
            assertTrue(
                "Row 3 should be undefined",
                column.isValueUndefined(3));
        }

        assertTrue(!column.isValueUndefined(2));
        assertEquals(32, column.getMin());
        assertEquals(32, column.getMax());

        column.set(1, 54);
        column.setExtend(3, 12);

        assertEquals(12,column.getMin());
        assertEquals(54, column.getMax());
        column.setValueUndefined(1, true);

        assertEquals(12, column.getMin());
        assertEquals(32, column.getMax());

        column.setExtend(100, 123);
        assertEquals(column.getMin(), 12);
        assertEquals(column.getMax(), 123);
        assertEquals(column.size(), 101);
    }

    public void testIntSparseColumn() {
        IntSparseColumn column = new IntSparseColumn("IntColumn");
        testNumberColumnProperties(column);
        try {
            column.setValueAt(2, "32");
            assertEquals(column.get(2), 32);
        }
        catch (ParseException e) {
            assertTrue("Parse error for IntColumn", false);
        }
        try {
            column.setValueAt(2, "32.5");
            assertEquals(column.get(2), 32);
            assertTrue("Too lax parsing for IntColumn", false);
        }
        catch (ParseException e) {
            assertTrue("Parse error for IntColumn", true);
        }
        try {
            column.setValueAt(3, "");
            assertTrue(
                "Invalid empty value parsed for IntColumn",
                false);
        }
        catch (ParseException e) {
            assertTrue(
                "Row 3 should be undefined",
                column.isValueUndefined(3));
        }

        assertTrue(!column.isValueUndefined(2));
        assertEquals(32, column.getMin());
        assertEquals(32, column.getMax());

        column.set(1, 54);
        column.setExtend(3, 12);

        assertEquals(12,column.getMin());
        assertEquals(54, column.getMax());
        column.setValueUndefined(1, true);

        assertEquals(12, column.getMin());
        assertEquals(32, column.getMax());

        column.setExtend(100, 123);
        assertEquals(column.getMin(), 12);
        assertEquals(column.getMax(), 123);
        assertEquals(column.size(), 101);
    }

    public void testLongColumn() {
        LongColumn column = new LongColumn("LongColumn");
        testNumberColumnProperties(column);

        try {
            column.setValueAt(2, "32");
            assertEquals(column.get(2), 32);
        }
        catch (ParseException e) {
            assertTrue("Parse error", false);
        }
        try {
            column.setValueAt(2, "32.5");
            assertEquals(column.get(2), 32);
            assertTrue("Too lax parsing", false);
        }
        catch (ParseException e) {
            assertTrue("Parse error", true);
        }
        try {
            column.setValueAt(3, "");
            assertTrue("Invalid empty value parsed", false);
        }
        catch (ParseException e) {
            assertTrue(
                "Row 3 should be undefined",
                column.isValueUndefined(3));
        }

        assertTrue(!column.isValueUndefined(2));
        assertEquals(column.getMin(), 32);
        assertEquals(column.getMax(), 32);

        column.set(1, 54);
        column.setExtend(3, 12);

        assertEquals(column.getMin(), 12);
        assertEquals(column.getMax(), 54);
        column.setValueUndefined(1, true);

        assertEquals(column.getMin(), 12);
        assertEquals(column.getMax(), 32);

        column.setExtend(100, 12312312312312312L);
        assertEquals(column.getMin(), 12);
        assertEquals(column.getMax(), 12312312312312312L);
        assertEquals(column.size(), 101);

    }

    public void testFloatColumn() {
        FloatColumn column = new FloatColumn("FloatColumn");
        testNumberColumnProperties(column);

    }

    public void testDoubleColumn() {
        testDoubleColumn(new DoubleColumn("DoubleColumn"));
    }
    
//    public void testColtDoubleColumn() {
//        testDoubleColumn(new ColtDoubleColumn("COltDoubleColumn"));
//    }

    public void testDoubleColumn(NumberColumn column) {
        testNumberColumnProperties(column);

        try {
            column.setValueAt(2, "32");
            assertEquals(column.getDoubleAt(2), 32.0, 0);
        }
        catch (ParseException e) {
            assertTrue("Parse error", false);
        }
        try {
            column.setValueAt(2, "32.5");
            assertEquals(column.getDoubleAt(2), 32.5, 0);
            assertTrue("Good parsing", true);
        }
        catch (ParseException e) {
            assertTrue("Parse error", false);
        }
        try {
            column.setValueAt(3, "");
            assertTrue("Invalid empty value parsed", false);
        }
        catch (ParseException e) {
            assertTrue(
                "Row 3 should be undefined",
                column.isValueUndefined(3));
        }

        assertTrue(!column.isValueUndefined(2));
        assertEquals(column.getDoubleMin(), 32.5, 0);
        assertEquals(column.getDoubleMax(), 32.5, 0);

        column.setDoubleAt(1, 54);
        column.setDoubleAt(3, 12);

        assertEquals(column.getDoubleMin(), 12, 0);
        assertEquals(column.getDoubleMax(), 54, 0);
        column.setValueUndefined(1, true);

        assertEquals(column.getDoubleMin(), 12, 0);
        assertEquals(column.getDoubleMax(), 32.5, 0);

        column.setDoubleAt(100, 1231231231.2312312);
        assertEquals(column.getDoubleMin(), 12, 0);
        assertEquals(column.getDoubleMax(), 1231231231.2312312, 0);
        assertEquals(column.size(), 101);
    }

    public void testStringColumn() {
        StringColumn column = new StringColumn("StringColumn");
        testColumnProperties(column);

    }

    public void testObjectColumn() {
        ObjectColumn column = new ObjectColumn("ObjectColumn");
        testColumnProperties(column);

    }

    public void testCategoricalColumn() {
        //CategoricalColumn column = new CategoricalColumn("CategoricalColumn");
        IntColumn column = new IntColumn("CategoricalColumn");
        column.setFormat(new CategoricalFormat());
        testColumnProperties(column);

        column.clear();
        column.setFormat(new CategoricalFormat());
        try {
            column.addValue("one");
        }
        catch (ParseException e) {
        }
        assertEquals(column.size(), 1);
        assertEquals(column.get(0), 0);
        assertEquals(column.getValueAt(0), "one");
        try {
            column.addValue("two");
        }
        catch (ParseException e) {
        }
        assertEquals(column.size(), 2);
        assertEquals(column.get(1), 1);
        assertEquals(column.getValueAt(1), "two");
        try {
            column.addValue("three");
        }
        catch (ParseException e) {
        }
        assertEquals(column.size(), 3);
        assertEquals(column.get(2), 2);
        assertEquals(column.getValueAt(2), "three");
        try {
            column.addValue("two");
        }
        catch (ParseException e) {
        }
        assertEquals(column.size(), 4);
        assertEquals(column.get(3), 1);
        assertEquals(column.getValueAt(3), "two");
    }
}
