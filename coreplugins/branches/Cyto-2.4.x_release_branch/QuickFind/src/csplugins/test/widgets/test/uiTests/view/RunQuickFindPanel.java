package csplugins.test.widgets.test.uiTests.view;

import csplugins.quickfind.view.QuickFindPanel;
import csplugins.test.widgets.test.unitTests.text.TestNumberIndex;
import csplugins.test.widgets.test.unitTests.view.TestTextIndexComboBox;
import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.widgets.autocomplete.view.TextIndexComboBox;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import prefuse.util.ui.JRangeSlider;
import prefuse.data.query.NumberRangeModel;

/**
 * Used to Test QuickFindPanel from the command line.
 *
 * @author Ethan Cerami
 */
public class RunQuickFindPanel {
    private static JTextArea textArea;

    /**
     * Creates and shows GUI.
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame();
        final QuickFindPanel panel = new QuickFindPanel();
        panel.enableAllQuickFindButtons();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        //  Create Sample Indexes
        final TextIndex textIndex =
                TestTextIndexComboBox.createSampleTextIndex();
        final NumberIndex numberIndex =
                TestNumberIndex.createSampleNumberIndex();
        panel.setIndex(textIndex);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        final JComboBox box = new JComboBox();
        box.addItem("Text index");
        box.addItem("Number index");
        box.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selection = (String) box.getSelectedItem();
                if (selection.equals("Text index")) {
                    panel.setIndex(textIndex);
                } else {
                    panel.setIndex(numberIndex);
                }
            }
        });

        final TextIndexComboBox comboBox = panel.getTextIndexComboBox();
        comboBox.addFinalSelectionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.append("Select:  " + comboBox.getSelectedItem()
                    + "\n");
            }
        });

        final JRangeSlider slider = panel.getSlider();
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                NumberRangeModel model = (NumberRangeModel) slider.getModel();
                Number low = (Number) model.getLowValue();
                Number high = (Number) model.getHighValue();
                textArea.append("Select:  " + low + " - " + high + "\n");
            }
        });

        frame.getContentPane().add(box, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(3);
        frame.setSize(300, 200);
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
                    RunQuickFindPanel.createAndShowGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}