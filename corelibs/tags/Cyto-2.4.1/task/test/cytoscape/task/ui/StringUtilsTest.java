package cytoscape.task.ui;

import junit.framework.TestCase;

/**
 * JUnit Test for StringUtils.
 */
public class StringUtilsTest extends TestCase {

    /**
     * Tests the GetTimeString() static method.
     */
    public void testGetTimeString() {
        String timeStr = StringUtils.getTimeString(-1);
        assertEquals(StringUtils.NOT_AVAILABLE_STR, timeStr);

        timeStr = StringUtils.getTimeString(0L);
        assertEquals("00:00", timeStr);

        timeStr = StringUtils.getTimeString(3000);
        assertEquals("00:03", timeStr);

        timeStr = StringUtils.getTimeString(300000);
        assertEquals("05:00", timeStr);

        timeStr = StringUtils.getTimeString(302000);
        assertEquals("05:02", timeStr);

        timeStr = StringUtils.getTimeString(3601000);
        assertEquals("01:00:01", timeStr);
    }

    /**
     * Tests the PadString static method.
     */
    public void testPadString() {
        String str = StringUtils.truncateOrPadString("Testing");
        assertEquals(StringUtils.STR_LENGTH, str.length());
    }
}
