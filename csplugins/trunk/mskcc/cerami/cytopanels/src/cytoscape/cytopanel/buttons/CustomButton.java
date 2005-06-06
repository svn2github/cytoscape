package cytoscape.cytopanel.buttons;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A Custom Button used by the CytoPanel API.
 *
 * @author Ethan Cerami
 */
public class CustomButton extends JButton {
    /**
     * Internal Margin.
     */
    private static final int MARGIN = 2;

    /**
     * Constructor.
     * @param text      String Text.
     * @param rotation  Rotation Hint Integer Value.
     *                  Must be set to:
     *                  VTextIcon.ROTATE_RIGHT or
     *                  VTextIcon.ROTATE_LEFT.
     */
    public CustomButton(String text, int rotation) {

        //  Validate the Incoming Parameters
        if (rotation != VTextIcon.ROTATE_RIGHT
                && rotation != VTextIcon.ROTATE_LEFT) {
            throw new IllegalArgumentException ("Invalid Rotation Parameter. "
                + " Must be set to: VTextIcon.ROTATE_RIGHT or "
                + "VTextIcon.ROTATE_LEFT.");
        }

        //  Set Rotated Text
        initButton(text);
        setIcon (new VTextIcon(this, text, rotation));
    }

    /**
     * Constructor.
     * @param text String Text.
     */
    public CustomButton (String text) {
        initButton (text);
        this.setText(text);
    }

    /**
     * Initializes the Button.
     * @param text String text.
     */
    private void initButton(String text) {
        //  Set a Light Gray Border
        setBorderColor(Color.LIGHT_GRAY);

        //  Set Action Command
        setActionCommand(text);

        //  Set Gray Background Color
        Color color = new Color(240, 240, 240);
        setBackground(color);

        //  Attach Mouse Listener
        attachMouseListener();
    }

    /**
     * Sets the Border Color.
     * @param color Color Object.
     */
    private void setBorderColor(Color color) {
        LineBorder lineBorder = new LineBorder(color, 1, true);
        EmptyBorder emptyBorder = new EmptyBorder (MARGIN, MARGIN,
                MARGIN, MARGIN);
        Border compoundBorder = BorderFactory.createCompoundBorder
                (lineBorder, emptyBorder);
        setBorder (compoundBorder);
    }

    /**
     * Attaches a Mouse Listener, used for Rollovers.
     */
    private void attachMouseListener() {
        this.addMouseListener(new MouseAdapter() {

            /**
             * On Mouse Enter, change border color
             * @param e Mouse Event Object.
             */
            public void mouseEntered(MouseEvent e) {
                setBorderColor (Color.GRAY);
            }

            /**
             * On Mouse Exit, change border color.
             * @param e MouseEvent Object.
             */
            public void mouseExited(MouseEvent e) {
                setBorderColor (Color.LIGHT_GRAY);
            }
        });
    }
}