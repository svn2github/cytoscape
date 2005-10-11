package cytoscape.data.unitTests;

import junit.framework.TestCase;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tests Implementation of CyAttributes.
 *
 * TODO:  Add to DataSuite
 * TODO:  Add Tests for Simple Maps
 */
public class CyAttributesTest extends TestCase {
    private CyAttributes cyAttributes;
    private static final String DUMMY_ID = "id_123";
    private static final String DUMMY_BOOLEAN_ATTRIBUTE = "attribute1";
    private static final String DUMMY_STRING_ATTRIBUTE = "attribute2";
    private static final String DUMMY_LIST_ATTRIBUTE = "attribute3";

    /**
     * Set things up.
     * @throws Exception All Exceptions.
     */
    protected void setUp() throws Exception {
        cyAttributes = new CyAttributesImpl();
    }

    /**
     * Tests Boolean Values.
     */
    public void testBooleanValue() {
        //  Verify that attribute does not (yet) exist
        boolean exists = cyAttributes.hasAttribute(DUMMY_ID,
                DUMMY_BOOLEAN_ATTRIBUTE);
        assertEquals (false, exists);

        //  First, try with a null ID;  should fail
        try {
            cyAttributes.setAttribute(null, DUMMY_BOOLEAN_ATTRIBUTE,
                    new Boolean(true));
            fail ("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertTrue (e != null);
        }

        //  Next, try with a null Attribute Name;  should fail
        try {
            cyAttributes.setAttribute(DUMMY_ID, null, new Boolean(true));
            fail ("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertTrue (e != null);
        }

        //  Set a Boolean Attribute Value;  should work
        cyAttributes.setAttribute(DUMMY_ID, DUMMY_BOOLEAN_ATTRIBUTE,
                new Boolean (true));

        //  Verify that attribute now exists
        exists = cyAttributes.hasAttribute(DUMMY_ID, DUMMY_BOOLEAN_ATTRIBUTE);
        assertEquals (true, exists);

        //  Verify Type
        byte type = cyAttributes.getType(DUMMY_BOOLEAN_ATTRIBUTE);
        assertEquals (CyAttributes.TYPE_BOOLEAN, type);

        //  Verify value stored
        Boolean value = cyAttributes.getBooleanAttribute(DUMMY_ID,
                DUMMY_BOOLEAN_ATTRIBUTE);
        assertEquals (true, value.booleanValue());

        //  Try getting it as an Integer value;  this should trigger
        //  a ClassCastException.
        try {
            Integer valueInt = cyAttributes.getIntegerAttribute
                (DUMMY_ID, DUMMY_BOOLEAN_ATTRIBUTE);
            fail ("ClassCastException should have been thrown.");
        } catch (ClassCastException e) {
            assertTrue (e != null);
        }

        //  Try setting an integer value;  this should trigger an
        //  IllegalArgumentException
        try {
            cyAttributes.setAttribute(DUMMY_ID, DUMMY_BOOLEAN_ATTRIBUTE,
                new Integer (5));
            fail ("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertTrue (e != null);
        }

        //  Check out the attribute names
        String attribs[] = cyAttributes.getAttributeNames();
        assertEquals (1, attribs.length);
        assertEquals (DUMMY_BOOLEAN_ATTRIBUTE, attribs[0]);

        //  Now delete the attribute
        boolean success = cyAttributes.deleteAttribute(DUMMY_ID,
                DUMMY_BOOLEAN_ATTRIBUTE);
        assertEquals (true, success);

        //  Verify that attribute no longer exists, but that its type remains
        exists = cyAttributes.hasAttribute(DUMMY_ID,
                DUMMY_BOOLEAN_ATTRIBUTE);
        assertEquals (false, exists);
        type = cyAttributes.getType(DUMMY_BOOLEAN_ATTRIBUTE);
                assertEquals (CyAttributes.TYPE_BOOLEAN, type);
        assertEquals (CyAttributes.TYPE_BOOLEAN, type);

        //  Now try deleting all attribute of this name
        success = cyAttributes.deleteAttribute(DUMMY_BOOLEAN_ATTRIBUTE);

        //  Verify that type is now UNDEFINED
        type = cyAttributes.getType(DUMMY_BOOLEAN_ATTRIBUTE);
                assertEquals (CyAttributes.TYPE_BOOLEAN, type);
        assertEquals (CyAttributes.TYPE_UNDEFINED, type);
    }

    /**
     * Tests String Values.
     */
    public void testStringValue() {
        //  Verify that attribute does not (yet) exist
        boolean exists = cyAttributes.hasAttribute(DUMMY_ID,
                DUMMY_STRING_ATTRIBUTE);
        assertEquals (false, exists);

        //  Set a String Attribute Value
        cyAttributes.setAttribute(DUMMY_ID, DUMMY_STRING_ATTRIBUTE,
                new String ("Cytoscape"));

        //  Verify that attribute now exists
        exists = cyAttributes.hasAttribute(DUMMY_ID, DUMMY_STRING_ATTRIBUTE);
        assertEquals (true, exists);

        //  Verify Type
        byte type = cyAttributes.getType(DUMMY_STRING_ATTRIBUTE);
        assertEquals (CyAttributes.TYPE_BOOLEAN, type);

        //  Verify value stored
        String value = cyAttributes.getStringAttribute(DUMMY_ID,
                DUMMY_STRING_ATTRIBUTE);
        assertEquals ("Cytoscape", value);

        //  Try getting it as an Integer value;  this should trigger
        //  a ClassCastException.
        try {
            Integer valueInt = cyAttributes.getIntegerAttribute
                (DUMMY_ID, DUMMY_STRING_ATTRIBUTE);
            fail ("ClassCastException should have been thrown.");
        } catch (ClassCastException e) {
            assertTrue (e != null);
        }

        //  Try setting an integer value;  this should trigger an
        //  IllegalArgumentException
        try {
            cyAttributes.setAttribute(DUMMY_ID, DUMMY_STRING_ATTRIBUTE,
                new Integer (5));
            fail ("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertTrue (e != null);
        }

        //  Check out the attribute names
        String attribs[] = cyAttributes.getAttributeNames();
        assertEquals (1, attribs.length);
        assertEquals (DUMMY_STRING_ATTRIBUTE, attribs[0]);
    }

    /**
     * Tests Simple Lists.
     */
    public void testSimpleLists() {
        //  First, try setting a not-so simple list
        List list = new ArrayList();
        list.add(new Integer (5));
        list.add(new String ("Cytoscape"));

        //  Try setting the list as null;  this should fail
        try {
            cyAttributes.setAttributeList(DUMMY_ID, DUMMY_LIST_ATTRIBUTE, null);
            fail ("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertTrue (e != null);
        }

        //  Try setting the list;  this should fail b/c we have mixed data types
        try {
            cyAttributes.setAttributeList(DUMMY_ID, DUMMY_LIST_ATTRIBUTE, list);
            fail ("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertTrue (e != null);
        }

        //  Try again with a valid list
        list = new ArrayList();
        list.add(new Integer (5));
        list.add(new Integer (6));
        cyAttributes.setAttributeList(DUMMY_ID, DUMMY_LIST_ATTRIBUTE, list);

        //  Verify type
        byte type = cyAttributes.getType(DUMMY_LIST_ATTRIBUTE);
        assertEquals (type, CyAttributes.TYPE_SIMPLE_LIST);

        //  Verify value exists
        boolean exists = cyAttributes.hasAttribute(DUMMY_ID,
                DUMMY_LIST_ATTRIBUTE);
        assertEquals (true, exists);

        //  Get Stored value as a Simple Map;  this should fail
        try {
            Map map = cyAttributes.getAttributeMap(DUMMY_ID,
                DUMMY_LIST_ATTRIBUTE);
            fail ("ClassCastException should have been thrown.");
        } catch (ClassCastException e) {
            assertTrue (e != null);
        }

        //  Get the list back, and verify its contents
        List storedList = cyAttributes.getAttributeList(DUMMY_ID,
                DUMMY_LIST_ATTRIBUTE);
        assertEquals (2, storedList.size());
        Integer int0 = (Integer) storedList.get(0);
        Integer int1 = (Integer) storedList.get(1);
        assertEquals (5, int0.intValue());
        assertEquals (6, int1.intValue());
    }
}