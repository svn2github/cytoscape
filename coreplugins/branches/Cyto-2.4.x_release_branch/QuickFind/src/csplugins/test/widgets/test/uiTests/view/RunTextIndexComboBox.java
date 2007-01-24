package csplugins.test.widgets.test.uiTests.view;

import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.test.widgets.test.unitTests.view.TestTextIndexComboBox;
import csplugins.widgets.autocomplete.view.ComboBoxFactory;
import csplugins.widgets.autocomplete.view.TextIndexComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Used to Test TextIndexComboBox from the command line.
 *
 * @author Ethan Cerami
 */
public class RunTextIndexComboBox {
    private static JFrame frame;

    /**
     * Creates and shows GUI.
     */
    private static void createAndShowGUI() throws IllegalAccessException,
            UnsupportedLookAndFeelException, ClassNotFoundException,
            InstantiationException {
        //  Create Text Index, and populate with sample data.
        TextIndex textIndex = TestTextIndexComboBox.createSampleTextIndex();

        TextIndexComboBox comboBox =
                ComboBoxFactory.createTextIndexComboBox(textIndex, 1.2);
        comboBox.setPrototypeDisplayValue("0123456790");

        comboBox.addFinalSelectionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TextIndexComboBox box = (TextIndexComboBox) e.getSource();
                Object item = box.getSelectedItem();
                JOptionPane.showMessageDialog(frame, "Final Selection:  "
                        + item.toString());
            }
        });

        // create and show a window containing the combo box
        frame = new JFrame();
        frame.setDefaultCloseOperation(3);
        frame.getContentPane().add(comboBox, BorderLayout.NORTH);
        frame.setSize(200, 200);
        frame.setVisible(true);

    }

    /**
     * Main method, invoked for testing purposes only.
     *
     * @param args Command line arguments (none expected).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    RunTextIndexComboBox.createAndShowGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
