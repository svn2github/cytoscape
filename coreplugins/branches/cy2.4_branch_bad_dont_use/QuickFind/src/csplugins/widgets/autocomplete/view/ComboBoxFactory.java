package csplugins.widgets.autocomplete.view;

import csplugins.widgets.autocomplete.index.TextIndex;

import javax.swing.*;

/**
 * Factory for creating TextIndexComboBoxes.
 *
 * @author Ethan Cerami
 */
public class ComboBoxFactory {

    /**
     * Creates a new TextIndex Combo Box.
     *
     * @param textIndex               Text Index Object.
     * @param popupWindowSizeMultiple Indicates the size multiple used
     *                                to resize the popup window.
     * @return TextIndexComboBox Object.
     * @throws IllegalAccessException Could not set Cross Platform
     *                                Look and Feel.
     * @throws UnsupportedLookAndFeelException
     *                                Could not set Cross Platform
     *                                Look and Feel.
     * @throws InstantiationException Could not set Cross Platform
     *                                Look and Feel.
     * @throws ClassNotFoundException Could not set Cross Platform
     *                                Look and Feel.
     */
    public static TextIndexComboBox createTextIndexComboBox(TextIndex
            textIndex, double popupWindowSizeMultiple)
            throws IllegalAccessException,
            UnsupportedLookAndFeelException,
            InstantiationException, ClassNotFoundException {

        // Obtain the current L & F
        LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();

        //  Set to Default Java Cross Platform L & F
        //  UIManager.setLookAndFeel
        // ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        UIManager.setLookAndFeel
                (UIManager.getCrossPlatformLookAndFeelClassName());

        //  Create the ToDelete
        TextIndexComboBox comboBox = new TextIndexComboBox(textIndex,
                popupWindowSizeMultiple);

        //  Return to original L & F
        UIManager.setLookAndFeel(currentLookAndFeel);

        return comboBox;
    }
}
