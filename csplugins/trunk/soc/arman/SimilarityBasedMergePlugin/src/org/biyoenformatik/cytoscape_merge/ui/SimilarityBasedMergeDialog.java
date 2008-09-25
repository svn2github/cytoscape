/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape_merge.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 *
 * This file is part of SimilarityBasedMergePlugin.
 *
 *  SimilarityBasedMergePlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.biyoenformatik.cytoscape_merge.ui;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.text.DecimalFormat;

import org.biyoenformatik.cytoscape_merge.util.Criteria;
import org.biyoenformatik.cytoscape_merge.util.ComponentType;
import org.biyoenformatik.cytoscape_merge.util.MatchScore;
import org.biyoenformatik.cytoscape_merge.task.ScoreSimilarComponentsTask;
import org.biyoenformatik.cytoscape_merge.task.AssignAttributesTask;

public class SimilarityBasedMergeDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox networkCombo1;
    private JComboBox networkCombo2;
    private JComboBox componentTypeComboBox;
    private JComboBox attrComboBox1;
    private JComboBox attrComboBox2;
    private JSlider criteriaImportance;
    private JLabel importanceAsANumber;
    private JButton deleteCriteriaButton;
    private JButton addCriteriaButton;
    private JTable matchesTable;
    private JButton previewButton;
    private JTable criteriasTable;
    private JSpinner thresholdSpinner;

    private ArrayList<CyNetwork> networkList = new ArrayList<CyNetwork>();
    private ArrayList<Criteria> criteriaList = new ArrayList<Criteria>();
    private ArrayList<MatchScore> scoreList = new ArrayList<MatchScore>();
    private ArrayList<String> componentTypes = new ArrayList<String>();

    public static final int MAX_SCORE = 100, MIN_SCORE = 0;
    private int checkBoxColumn = 4;

    private boolean valuesChanged = false;
    private boolean hasResults = false;

    public SimilarityBasedMergeDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(previewButton);

        setTitle("Similarity Based Merge");

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        previewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPreview();
            }
        });

        String[] networkTitles = new String[Cytoscape.getNetworkSet().size()];
        int nCnt = 0;
        for (CyNetwork cynet : Cytoscape.getNetworkSet()) {
            networkList.add(cynet);
            networkTitles[nCnt++] = cynet.getTitle();
        }

        networkCombo1.setModel(new DefaultComboBoxModel(networkTitles));
        networkCombo2.setModel(new DefaultComboBoxModel(networkTitles));
        if (networkList.size() > 1)
            networkCombo2.setSelectedIndex(1);
        networkCombo1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetScoring();
            }
        });
        networkCombo2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetScoring();
            }
        });

        thresholdSpinner.setModel(new SpinnerNumberModel(MIN_SCORE, .0, MAX_SCORE, .1));
        criteriaImportance.setModel(new DefaultBoundedRangeModel(MAX_SCORE, 0, 0, MAX_SCORE));
        importanceAsANumber.setText(criteriaImportance.getValue() + "%");
        criteriaImportance.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                importanceAsANumber.setText(criteriaImportance.getValue() + "%");
            }
        });
        thresholdSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateMatchesTable();
            }
        });
        updateMatchesTable();

        for (ComponentType ct : ComponentType.values())
            componentTypes.add(ct.toString());
        componentTypeComboBox.setModel(new DefaultComboBoxModel(componentTypes.toArray()));
        componentTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAttributes();
            }
        });
        updateAttributes();

        addCriteriaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ComponentType ct = ComponentType.values()[componentTypeComboBox.getSelectedIndex()];
                CyAttributes attr = ct.getAttributes();
                criteriaList.add(new Criteria(ct,
                        attr.getAttributeNames()[attrComboBox1.getSelectedIndex()],
                        attr.getAttributeNames()[attrComboBox2.getSelectedIndex()],
                        ((double) criteriaImportance.getValue()) / MAX_SCORE));

                updateCriteriasTable();
            }
        });
        deleteCriteriaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Integer> selects = new ArrayList<Integer>();
                for (int i : criteriasTable.getSelectedRows())
                    selects.add(i);
                Collections.sort(selects);
                Collections.reverse(selects);
                for (Integer i : selects)
                    criteriaList.remove(i.intValue());

                updateCriteriasTable();
            }
        });
        updateCriteriasTable();

        matchesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    SimilarityDetailsDialog dialog =
                            new SimilarityDetailsDialog(scoreList.get(matchesTable.getSelectedRow()));
                    dialog.pack();
                    dialog.setVisible(true);
                }
            }
        });
    }

    private void resetScoring() {
        criteriaList.clear();
        updateCriteriasTable();

        scoreList.clear();
        updateMatchesTable();

        valuesChanged = false;
    }

    private void updateCriteriasTable() {
        String[] tableHeaders = {"Type", "Attribute 1", "Attribute 2", "Importance"};
        String[][] tableData = new String[criteriaList.size()][];
        DecimalFormat df = new DecimalFormat("###");

        if (criteriaList.isEmpty()) {
            tableHeaders = new String[]{""};
            tableData = new String[][]{{"No criterieas."}};
        } else {
            int count = 0;
            for (Criteria criteria : criteriaList) {
                String[] aRow = {criteria.type.toString(),
                        criteria.attribute1,
                        criteria.attribute2,
                        df.format(criteria.importance * MAX_SCORE) + "/" + MAX_SCORE};
                tableData[criteriaList.size() - ++count] = aRow;
            }
        }

        criteriasTable.setModel(new DefaultTableModel(tableData, tableHeaders) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        valuesChanged = true;
        updateMatchesTable();
    }

    private void updateAttributes() {
        CyAttributes attrs = ComponentType.values()[componentTypeComboBox.getSelectedIndex()].getAttributes();
        attrComboBox1.setModel(new DefaultComboBoxModel(attrs.getAttributeNames()));
        attrComboBox2.setModel(new DefaultComboBoxModel(attrs.getAttributeNames()));
    }

    public void updateMatchesTable() {
        ArrayList<MatchScore> copyScores = new ArrayList<MatchScore>();
        for (MatchScore ms : scoreList) {
            if (ms.score * MAX_SCORE > (Double) thresholdSpinner.getValue())
                copyScores.add(ms);
            else
                break;
        }

        if (copyScores.isEmpty()) {
            String[] tableHeaders = {""};
            String[][] tableData = {{"No results."}};

            matchesTable.setModel(new DefaultTableModel(tableData, tableHeaders) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });

            hasResults = false;
            return;
        }

        String[] tableHeaders = {"Score", "Type", "Component 1", "Component 2", "Merge?"};
        Object[][] tableData = new Object[copyScores.size()][];

        DecimalFormat df = new DecimalFormat("###.00");

        int count = 0;
        for (MatchScore ms : copyScores) {
            Object[] aRow = {"" + df.format(ms.score * MAX_SCORE),
                    ms.type.toString(),
                    ms.type.getAttributes().getAttribute(ms.id1, Semantics.CANONICAL_NAME),
                    ms.type.getAttributes().getAttribute(ms.id2, Semantics.CANONICAL_NAME),
                    true};
            tableData[count++] = aRow;
        }

        matchesTable.setModel(new DefaultTableModel(tableData, tableHeaders) {
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }

            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        });

        hasResults = true;
    }

    private void onPreview() {
        ScoreSimilarComponentsTask task = new ScoreSimilarComponentsTask(
                networkList.get(networkCombo1.getSelectedIndex()),
                networkList.get(networkCombo2.getSelectedIndex()),
                criteriaList,
                scoreList,
                this);

        JTaskConfig jTaskConfig = new JTaskConfig();
        jTaskConfig.setOwner(Cytoscape.getDesktop());
        jTaskConfig.displayCloseButton(true);
        jTaskConfig.displayStatus(true);
        jTaskConfig.setAutoDispose(true);

        TaskManager.executeTask(task, jTaskConfig);

        valuesChanged = false;
    }

    private void onOK() {
        if (valuesChanged)
            onPreview();

        ArrayList<MatchScore> selectedScoresList = new ArrayList<MatchScore>();

        if (hasResults)
            for (int rowCnt = 0; rowCnt < matchesTable.getRowCount(); rowCnt++)
                if ((Boolean) matchesTable.getValueAt(rowCnt, checkBoxColumn))
                    selectedScoresList.add(scoreList.get(rowCnt));

        AssignAttributesTask task = new AssignAttributesTask(selectedScoresList);

        JTaskConfig jTaskConfig = new JTaskConfig();
        jTaskConfig.setOwner(Cytoscape.getDesktop());
        jTaskConfig.displayCloseButton(true);
        jTaskConfig.displayStatus(true);
        jTaskConfig.setAutoDispose(false);

        dispose();
        TaskManager.executeTask(task, jTaskConfig);
    }

    private void onCancel() {
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        buttonOK = new JButton();
        buttonOK.setText("Finish");
        buttonOK.setMnemonic('F');
        buttonOK.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonOK, gbc);
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        buttonCancel.setMnemonic('C');
        buttonCancel.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonCancel, gbc);
        previewButton = new JButton();
        previewButton.setText("Preview");
        previewButton.setMnemonic('P');
        previewButton.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(previewButton, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        panel3.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel3, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel4.setAlignmentX(0.5f);
        panel4.setAlignmentY(0.5f);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel4, gbc);
        panel4.setBorder(BorderFactory.createTitledBorder("Networks to merge"));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(panel5, gbc);
        panel5.setBorder(BorderFactory.createTitledBorder("First network"));
        networkCombo1 = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(networkCombo1, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(panel6, gbc);
        panel6.setBorder(BorderFactory.createTitledBorder("Second network"));
        networkCombo2 = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(networkCombo2, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel7, gbc);
        panel7.setBorder(BorderFactory.createTitledBorder("Similarity Criterias"));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(panel8, gbc);
        panel8.setBorder(BorderFactory.createTitledBorder("Add a new criteria"));
        componentTypeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        componentTypeComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 3, 5);
        panel8.add(componentTypeComboBox, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel8.add(spacer1, gbc);
        attrComboBox1 = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 3, 5);
        panel8.add(attrComboBox1, gbc);
        attrComboBox2 = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 3, 5);
        panel8.add(attrComboBox2, gbc);
        criteriaImportance = new JSlider();
        criteriaImportance.setMajorTickSpacing(50);
        criteriaImportance.setMaximum(100);
        criteriaImportance.setMinimumSize(new Dimension(200, 21));
        criteriaImportance.setMinorTickSpacing(10);
        criteriaImportance.setPaintLabels(true);
        criteriaImportance.setPaintTicks(true);
        criteriaImportance.setPaintTrack(true);
        criteriaImportance.setSnapToTicks(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 3, 5);
        panel8.add(criteriaImportance, gbc);
        importanceAsANumber = new JLabel();
        importanceAsANumber.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel8.add(importanceAsANumber, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Edge or Node");
        label1.setDisplayedMnemonic('E');
        label1.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 3, 5);
        panel8.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("First Network Attribute");
        label2.setDisplayedMnemonic('F');
        label2.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 3, 5);
        panel8.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Second Network Attribute");
        label3.setDisplayedMnemonic('S');
        label3.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 3, 5);
        panel8.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Importance of match");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 5, 3, 5);
        panel8.add(label4, gbc);
        addCriteriaButton = new JButton();
        addCriteriaButton.setMaximumSize(new Dimension(120, 25));
        addCriteriaButton.setMinimumSize(new Dimension(120, 25));
        addCriteriaButton.setOpaque(false);
        addCriteriaButton.setPreferredSize(new Dimension(120, 25));
        addCriteriaButton.setText("Add Criteria");
        addCriteriaButton.setMnemonic('A');
        addCriteriaButton.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        panel8.add(addCriteriaButton, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        panel7.add(panel9, gbc);
        panel9.setBorder(BorderFactory.createTitledBorder("Criterias"));
        deleteCriteriaButton = new JButton();
        deleteCriteriaButton.setText("Delete Criteria");
        deleteCriteriaButton.setMnemonic('D');
        deleteCriteriaButton.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel9.add(deleteCriteriaButton, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(454, 20));
        scrollPane1.setVerticalScrollBarPolicy(22);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel9.add(scrollPane1, gbc);
        criteriasTable = new JTable();
        criteriasTable.setFillsViewportHeight(true);
        scrollPane1.setViewportView(criteriasTable);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        panel10.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel10, gbc);
        panel10.setBorder(BorderFactory.createTitledBorder("Preview of Matches"));
        final JScrollPane scrollPane2 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(scrollPane2, gbc);
        matchesTable = new JTable();
        scrollPane2.setViewportView(matchesTable);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(panel11, gbc);
        thresholdSpinner = new JSpinner();
        thresholdSpinner.setMinimumSize(new Dimension(50, 24));
        thresholdSpinner.setPreferredSize(new Dimension(50, 24));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel11.add(thresholdSpinner, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Score Threshold:");
        label5.setDisplayedMnemonic('T');
        label5.setDisplayedMnemonicIndex(6);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(3, 0, 0, 5);
        panel11.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setFocusable(true);
        label6.setFont(new Font(label6.getFont().getName(), label6.getFont().getStyle(), 10));
        label6.setText("Double click on a match to see the details.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel11.add(label6, gbc);
        label1.setLabelFor(componentTypeComboBox);
        label2.setLabelFor(attrComboBox1);
        label3.setLabelFor(attrComboBox2);
        label5.setLabelFor(thresholdSpinner);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
