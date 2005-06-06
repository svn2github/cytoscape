package cytoscape.test.cytopanel.buttons;

import junit.framework.TestCase;
import cytoscape.cytopanel.buttons.CustomButton;
import cytoscape.cytopanel.buttons.VTextIcon;

/**
 * Tests the CustomButton Object.
 *
 * @author Ethan Cerami
 */
public class TestCustomButton extends TestCase {

    /**
     * Tests the Custom Button Constructor.
     */
    public void testConstructor() {

        //  Should work, no problem
        CustomButton button1 = new CustomButton ("Test1",
                VTextIcon.ROTATE_RIGHT);

        //  Should trigger an exception
        try {
            CustomButton button2 = new CustomButton ("Test2", 99);
            fail ("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            assertTrue (msg.startsWith("Invalid Rotation Parameter."));
        }
    }
}