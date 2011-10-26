package org.cytoscape.cpathsquared.internal.view;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.cytoscape.cpathsquared.internal.webservice.CPathProperties;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Graphical User Interface (GUI) Utiltities.
 *
 * @author Ethan Cerami
 */
public class GuiUtils {

    /**
     * Creates a Titled Border with appropriate font settings.
     * @param title Title.
     * @return TitledBorder Object.
     */
    public static TitledBorder createTitledBorder (String title) {
        TitledBorder border = new TitledBorder(title);
        Font font = border.getTitleFont();
        Font newFont = new Font (font.getFamily(), Font.BOLD, font.getSize()+2);
        border.setTitleFont(newFont);
        border.setTitleColor(new Color(102,51,51));
        return border;
    }
    
    public static JScrollPane createConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setBorder(new TitledBorder("Retrieval Options"));
        configPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        final JRadioButton button1 = new JRadioButton("Full Model");

        JTextArea textArea1 = new JTextArea();
        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        textArea1.setEditable(false);
        textArea1.setOpaque(false);
        Font font = textArea1.getFont();
        Font smallerFont = new Font(font.getFamily(), font.getStyle(), font.getSize() - 2);
        textArea1.setFont(smallerFont);
        textArea1.setText("Retrieve the full model, as stored in the original BioPAX "
                + "representation.  In this representation, nodes within a network can "
                + "refer to physical entities and interactions.");
        textArea1.setBorder(new EmptyBorder(5, 20, 0, 0));

        JTextArea textArea2 = new JTextArea(3, 20);
        textArea2.setLineWrap(true);
        textArea2.setWrapStyleWord(true);
        textArea2.setEditable(false);
        textArea2.setOpaque(false);
        textArea2.setFont(smallerFont);
        textArea2.setText("Retrieve a simplified binary network, as inferred from the original "
                + "BioPAX representation.  In this representation, nodes within a network refer "
                + "to physical entities only, and edges refer to inferred interactions.");
        textArea2.setBorder(new EmptyBorder(5, 20, 0, 0));


        final JRadioButton button2 = new JRadioButton("Simplified Binary Model");
        button2.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        configPanel.add(button2, c);

        c.gridy = 1;
        configPanel.add(textArea2, c);

        c.gridy = 2;
        configPanel.add(button1, c);

        c.gridy = 3;
        configPanel.add(textArea1, c);

        //  Add invisible filler to take up all remaining space
        c.gridy = 4;
        c.weighty = 1.0;
        JPanel panel = new JPanel();
        configPanel.add(panel, c);

        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPathProperties.downloadMode = CPathProperties.DOWNLOAD_FULL_BIOPAX;
            }
        });
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPathProperties.downloadMode = CPathProperties.DOWNLOAD_REDUCED_BINARY_SIF;
            }
        });
        JScrollPane scrollPane = new JScrollPane(configPanel);
        return scrollPane;
    }
}
