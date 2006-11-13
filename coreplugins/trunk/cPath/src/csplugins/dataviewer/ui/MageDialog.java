/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.dataviewer.ui;

import csplugins.dataviewer.action.LoadSelectedExpressionFile;
import csplugins.dataviewer.mage.MageData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Dialog Box for Displaying MAGE-ML data and prompting user to load an expression data file.
 *
 * @author Ethan Cerami.
 */
public class MageDialog extends JFrame {

    /**
     * Constructor.
     *
     * @param mageData MAGE-ML Data, to be presented to the user.
     */
    public MageDialog(MageData mageData) {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //  Create Header
        JLabel label = new JLabel("Cytoscape MAGE-ML Reader");
        Font currentFont = label.getFont();
        label.setFont(new Font(currentFont.getFamily(), Font.BOLD, currentFont.getSize() + 5));
        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.add(label, BorderLayout.NORTH);

        //  Create Center Panel
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(2, 1));
        addTextArea(center, "Experimental Description(s)", mageData.getExperimentDescriptionList());
        addTextArea(center, "Organizational Contacts(s)", mageData.getOrganizationContactList());
        contentPane.add(center, BorderLayout.CENTER);

        //  Create Footer Panel
        JPanel footer = new JPanel();
        footer.setLayout(new GridLayout(1, 2));
        if (mageData.getFileList() != null && mageData.getFileList().size() > 0) {
            footer.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createTitledBorder("Select Expression Data File:"),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            JComboBox comboList = new JComboBox();
            for (int i = 0; i < mageData.getFileList().size(); i++) {
                comboList.addItem(mageData.getFileList().get(i));
            }
            footer.add(comboList);
            JButton button = new JButton("Load Data File");
            LoadSelectedExpressionFile listener = new LoadSelectedExpressionFile(mageData.getFile(),
                    comboList);
            button.addActionListener(listener);
            footer.add(button);
        } else {
            ArrayList list = new ArrayList();
            list.add("Failed to identify any valid external expression data files.  Please "
                    + "check the MAGE-ML file and try again.");
            addTextArea(footer, "Expression Data Files", list);
        }

        contentPane.add(footer, BorderLayout.SOUTH);
        this.setSize(300, 400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Adds Text Area.
     */
    private void addTextArea(JPanel center, String header, java.util.List contentList) {
        JTextArea textArea = new JTextArea();
        if (contentList != null) {
            for (int i = 0; i < contentList.size(); i++) {
                String content = (String) contentList.get(i);
                textArea.append(content + "\n");
            }
            Font currentFont = textArea.getFont();
            textArea.setFont(new Font(currentFont.getFamily(),
                    Font.PLAIN, currentFont.getSize() - 2));
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(null);
            JScrollPane areaScrollPane = new JScrollPane(textArea);
            areaScrollPane.setOpaque(false);
            areaScrollPane.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createTitledBorder(header),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            center.add(areaScrollPane);
        }
    }

    /**
     * Used for local testing purposes only.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        MageData data = new MageData();
        ArrayList expList = new ArrayList();
        expList.add(getIpsum());
        data.setExperimentDescriptionList(expList);
        data.setOrganizationContactList(expList);

        ArrayList fileList = new ArrayList();
        fileList.add("File 1 File 1 File 1 File 1 File 1");
        fileList.add("File 2");
        fileList.add("File 3");
        data.setFileList(fileList);

        MageDialog dialog = new MageDialog(data);
    }

    /**
     * Again, only used for local testing purposes.
     *
     * @return Lorem ipsum string.
     */
    private static String getIpsum() {
        return "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Mauris id pede. "
                + "Proin aliquam. In hac habitasse platea dictumst. Curabitur ut dui non enim "
                + "varius scelerisque. Quisque tincidunt eleifend arcu. Suspendisse lobortis "
                + "elit id augue. Quisque fermentum viverra neque. Aenean bibendum sodales metus. "
                + "Suspendisse vulputate, nisl quis consectetuer sollicitudin, ligula metus "
                + "viverra est, a semper nulla neque a ipsum. Sed elementum diam non lectus "
                + "eleifend molestie. ";
    }
}
