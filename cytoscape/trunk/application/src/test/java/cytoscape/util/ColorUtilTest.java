package cytoscape.util;

import java.awt.Color;

import junit.framework.TestCase;

/**
 * Test Color Utility class.
 * 
 * @author kono
 *
 */
public class ColorUtilTest extends TestCase {

	public void testParseColorText() {
		String black = "black";
		String darkGray = "darkgray";
		String darkGrayRGB = "169, 169, 169";
		Color darkGrayColor = ColorUtil.parseColorText(darkGray);
		Color darkGrayColor2 = ColorUtil.parseColorText(darkGrayRGB);
		
		String seaShell = "  SeaShell    ";
		Color seaShellColor = Color.decode("#FFF5EE");
		
		String mediumTurquoise = "mediumturquoise";
		Color mediumTurquoiseColor = ColorUtil.parseColorText("72, 209,204 ");
		
		String nonsense = "dkajshfls/>-d13opr[wepq-83=";
		
		assertEquals(Color.black, ColorUtil.parseColorText(black));
		assertEquals(Color.decode("#A9A9A9"), darkGrayColor);
		assertEquals(darkGrayColor, darkGrayColor2);
		
		assertEquals(seaShellColor, ColorUtil.parseColorText(seaShell));
		assertEquals(mediumTurquoiseColor, ColorUtil.parseColorText(mediumTurquoise));
		
		assertNull(ColorUtil.parseColorText(nonsense));
	}

	public void testGetColorAsText() {
		assertNotNull(ColorUtil.getColorAsText(Color.white));
		assertEquals("0,0,0", ColorUtil.getColorAsText(Color.black));
	}

}
