
import infovis.Column;
import infovis.Table;
import infovis.column.ColumnFactory;
import infovis.column.IntColumn;
import infovis.table.DefaultTable;
import infovis.utils.RowIterator;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/

/**
 * Class TableTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class TableTest extends TestCase {
    public TableTest(String name) {
        super(name);
    }

    public void testCreation() {
        testCreation(new DefaultTable());
    }
    public static boolean testEquals(Object o1, Object o2) {
        return (o1 == null && o2 == null)
            || (o1.equals(o2));
    }
    
    public static boolean columnEquals(Column c1, Column c2) {
        if (!testEquals(c1.getName(), c2.getName())) return false;
        if (!testEquals(c1.getMetadata(), c2.getMetadata())) return false;
        if (!testEquals(c1.getClientPropery(), c2.getClientPropery())) return false;
        if (c1.size() != c2.size()) return false;
        for (int r = 0; r < c1.size(); r++) {
            if (c1.isValueUndefined(r) && ! c2.isValueUndefined(r)) return false;
            else if (c2.isValueUndefined(r)) return false;
            else if (! testEquals(c1.getValueAt(r), c2.getValueAt(r))) return false;
        }
        return true;
    }
    
    public static boolean tableEquals(Table t1, Table t2) {
        if (t1 == t2) {
            return true;
        }
        if (!testEquals(t1.getName(),t2.getName())) return false;
        if (!testEquals(t1.getMetadata(), t2.getMetadata())) return false;
        if (!testEquals(t1.getClientPropery(), t2.getClientPropery())) return false;
        if (t1.getColumnCount() != t2.getColumnCount()) return false;
        for (int c = 0; c < t1.getColumnCount(); c++) {
            if (!columnEquals(t1.getColumnAt(c), t2.getColumnAt(c))) return false;
        }
        return true;
    }

    public static void testCreation(Table table) {
        int i;

        assertNull(table.getName());
        assertEquals(table.getColumnCount(), 0);
        for (i = -1; i < 10; i++) {
            assertNull(table.getColumnAt(i));
        }
        assertEquals(table.getRowCount(), 0);
        RowIterator iter = table.iterator();
        assertTrue(!iter.hasNext());
        iter = table.reverseIterator();
        assertTrue(!iter.hasNext());
        assertEquals(table, table.getTable());
        for (i = -1; i < 10; i++) {
            assertTrue(!table.isRowValid(i));
        }
    }

    public void testInvariants() {
        DefaultTable table = new DefaultTable();
        testInvariants(table);
        testClear(table);
    }

    public void testSerializable() {
        DefaultTable table = new DefaultTable();
        randomFillTable(table);

        try {
            FileOutputStream fo = new FileOutputStream("cde.tmp");
            ObjectOutputStream so = new ObjectOutputStream(fo);
            so.writeObject(table);
            so.flush();
            so.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        DefaultTable tableNew = null;
        // Deserialize in to new class object
        try {
            FileInputStream fi = new FileInputStream("cde.tmp");
            ObjectInputStream si = new ObjectInputStream(fi);
            tableNew = (DefaultTable) si.readObject();
            si.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("Serialized tables not equal", tableEquals(table, tableNew));
    }

    public static void randomFillTable(Table t) {
        Random rand = new Random();

        ArrayList colTypes = new ArrayList();
        for (Iterator iter = ColumnFactory.getInstance().iterator(); iter
                .hasNext();) {
            colTypes.add(iter.next());
        }

        t.setName("name" + rand.nextInt(1000));
        int ncol = rand.nextInt(100);
        for (int col = 0; col < ncol; col++) {
            String typeName = (String) colTypes.get(rand
                    .nextInt(colTypes.size()));
            String colName = "column" + col;
            Column c = ColumnFactory.createColumn(typeName, colName);
            t.addColumn(c);
        }
    }

    public static void testInvariants(Table table) {
        int i;
        final String randomName = "randomName";
        String savedName = table.getName();
        WeakHashMap colMap = new WeakHashMap();

        table.setName(randomName);
        assertEquals(table.getName(), randomName);
        table.setName(savedName);
        assertEquals(table.getName(), savedName);

        int count = table.getColumnCount();
        int rows = table.getRowCount();
        Column[] cols = new Column[10];
        for (i = 0; i < 10; i++) {
            Column c = new IntColumn(randomName + i);
            cols[i] = c;
            table.addColumn(c);
            colMap.put(c, c.getName());
        }
        assertEquals(table.getColumnCount(), count + 10);
        for (i = 0; i < 10; i++) {
            assertEquals(table.getColumnAt(count + i), cols[i]);
            assertEquals(table.getColumn(randomName + i), cols[i]);
            assertEquals(table.indexOf(cols[i]), count + i);
            assertEquals(table.indexOf(randomName + i), count + i);
        }
        assertEquals(table.getRowCount(), rows);
        for (i = 0; i < (rows + 10); i++) {
            cols[0].addValueOrNull("" + i);
            assertTrue(!cols[0].isValueUndefined(i));
        }
        assertEquals(table.getRowCount(), rows + 10);

        table.removeColumn(cols[3]);
        assertEquals(table.getColumnCount(), count + 9);

        for (i = 0; i < 10; i++) {
            assertEquals(table.removeColumn(cols[i]), (i != 3));
        }
        cols = null;
        System.gc();
        for (Iterator iter = colMap.keySet().iterator(); iter.hasNext();) {
            Column c = (Column) iter.next();
            System.out.println("Column " + c.getName()
                    + " not reclaimed");
        }
    }

    private static void testClear(Table table) {
        int count = table.getColumnCount();
        table.clear();
        assertEquals(table.getRowCount(), 0);
        assertEquals(table.getColumnCount(), count);
    }
}