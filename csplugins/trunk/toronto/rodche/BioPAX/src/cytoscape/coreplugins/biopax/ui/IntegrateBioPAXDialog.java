package cytoscape.coreplugins.biopax.ui;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.text.DecimalFormat;

import org.biopax.paxtools.controller.Integrator;
import org.biopax.paxtools.controller.ConversionScore;
import org.biopax.paxtools.io.simpleIO.SimpleEditorMap;
import org.biopax.paxtools.model.Model;

public class IntegrateBioPAXDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox firstNetworkComboBox;
    private JComboBox secondNetworkComboBox;
    private JSlider slider1;
    private JSpinner spinner1;
    private JTable scoresTable;
    private JButton previewButton;
    private JCheckBox colorizeNodesAccordingToCheckBox;
    private JButton firstColorButton;
    private JButton secondColorButton;
    private JButton mergedColorButton;
    private JPanel thresholdPanel;
    private JPanel colorizeMain;
    private JPanel colorsPanel;
    private JCheckBox mergeSimilarReactionsCheckBox;
    private JPanel previewPanel;

    private ArrayList<CyNetwork> bpNetworks = new ArrayList<CyNetwork>();
    private Integrator integrator = null;
    private List<ConversionScore> scores;

    private Color firstColor = new Color(255, 0, 0),
            secondColor = new Color(0, 255, 0),
            mergedColor = new Color(255, 255, 0);

    private boolean isColorPanelEnabled = false;
    private boolean isScoringEnabled = true;

    private final int checkBoxColumn = 3;


    // Keep track to reduce CPU work
    private Model oldModel1 = null, oldModel2 = null;
    private Double oldThreshold;


    private int MAX_SCORE = 100;

    public IntegrateBioPAXDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setTitle("Integrate BioPAX networks");

        spinner1.setModel(new SpinnerNumberModel(1, .0, MAX_SCORE, .1));
        slider1.setModel(new DefaultBoundedRangeModel(1, 0, 0, MAX_SCORE));
        slider1.setMinorTickSpacing(5);
        slider1.setMajorTickSpacing(MAX_SCORE);

        colorsPanel.setVisible(isColorPanelEnabled);
        colorizeNodesAccordingToCheckBox.setSelected(isColorPanelEnabled);
        mergeSimilarReactionsCheckBox.setSelected(isScoringEnabled);
        thresholdPanel.setVisible(isScoringEnabled);
        previewPanel.setVisible(isScoringEnabled);

        firstColorButton.setBackground(firstColor);
        secondColorButton.setBackground(secondColor);
        mergedColorButton.setBackground(mergedColor);

        ArrayList<String> comboList = new ArrayList<String>();

        for (CyNetwork cyNetwork : Cytoscape.getNetworkSet()) {
            if (BioPaxUtil.isBioPAXNetwork(cyNetwork)) {
                comboList.add(cyNetwork.getTitle());
                bpNetworks.add(cyNetwork);
            }
        }
        ComboBoxModel comboModel1 = new DefaultComboBoxModel(comboList.toArray()),
                comboModel2 = new DefaultComboBoxModel(comboList.toArray());

        firstNetworkComboBox.setModel(comboModel1);
        secondNetworkComboBox.setModel(comboModel2);

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

        spinner1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Double val = (Double) spinner1.getValue();
                slider1.setValue((int) val.doubleValue());
            }
        });
        slider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double val = slider1.getValue();
                spinner1.setValue(val);
            }
        });

        scoresTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = scoresTable.getSelectedRow();
                    ConversionScore convScore = scores.get(index);
                    if (convScore == null)
                        return;

                    IntegrateBioPAXDetailsDialog dialog = new IntegrateBioPAXDetailsDialog(convScore);
                    dialog.setTitle("Match #" + (index + 1));
                    dialog.setSize(400, 400);
                    dialog.pack();
                    dialog.setVisible(true);

                }
            }
        });

        colorizeNodesAccordingToCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                colorsPanel.setVisible(colorizeNodesAccordingToCheckBox.isSelected());
            }
        });

        mergeSimilarReactionsCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                thresholdPanel.setVisible(mergeSimilarReactionsCheckBox.isSelected());
                previewPanel.setVisible(mergeSimilarReactionsCheckBox.isSelected());
            }
        });

        firstColorButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Color tempColor = JColorChooser.showDialog(IntegrateBioPAXDialog.this,
                        "Choose Node Color",
                        firstColor);

                if (tempColor != null)
                    firstColor = tempColor;

                firstColorButton.setBackground(firstColor);
            }
        });

        secondColorButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Color tempColor = JColorChooser.showDialog(IntegrateBioPAXDialog.this,
                        "Choose Node Color",
                        secondColor);

                if (tempColor != null)
                    secondColor = tempColor;

                secondColorButton.setBackground(secondColor);
            }
        });

        mergedColorButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Color tempColor = JColorChooser.showDialog(IntegrateBioPAXDialog.this,
                        "Choose Node Color",
                        mergedColor);

                if (tempColor != null)
                    mergedColor = tempColor;

                mergedColorButton.setBackground(mergedColor);
            }
        });


    }

    private void onPreview() {
        CyNetwork network1 = bpNetworks.get(firstNetworkComboBox.getSelectedIndex()),
                network2 = bpNetworks.get(secondNetworkComboBox.getSelectedIndex());

        Model model1 = BioPaxUtil.getNetworkModel(network1),
                model2 = BioPaxUtil.getNetworkModel(network2);

        if (model1 == null || model2 == null) {
            JOptionPane.showMessageDialog(null,
                    "BioPAX models cannot be accessed.",
                    "Invalid BioPAX",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Double threshold = (mergeSimilarReactionsCheckBox.isSelected())
                ? (Double) spinner1.getValue()
                : MAX_SCORE;

        if (!(oldModel1 == model1 && oldModel2 == model2)) {
            integrator = new Integrator(new SimpleEditorMap(), model1, model2);
            integrator.setOnlyMapping(true);
            integrator.setScoresOver(MAX_SCORE);
        }

        integrator.setThreshold(threshold);
        scores = integrator.integrate();

        Set<ConversionScore> toBeRemoved = new HashSet<ConversionScore>();
        for (ConversionScore aScore : scores)
            if (aScore.getScore() < integrator.getThreshold())
                toBeRemoved.add(aScore);
        for (ConversionScore aScore : toBeRemoved)
            scores.remove(aScore);

        if (scores.isEmpty()) {
            String[][] tableData = {{"No results"}};
            String[] tableHeader = {""};

            scoresTable.setModel(new DefaultTableModel(tableData, tableHeader));
            scoresTable.setEnabled(false);

            return;
        }

        scoresTable.setEnabled(true);
        Object[][] tableData = new Object[scores.size()][];

        DecimalFormat df = new DecimalFormat("###.00");

        int cnt = 0;
        for (ConversionScore aScore : scores) {
            Object[] cols = {
                    "" + df.format(aScore.getScore()),
                    BioPaxUtil.getNodeName(aScore.getConversion1()),
                    BioPaxUtil.getNodeName(aScore.getConversion2()),
                    true
            };
            tableData[cnt++] = cols;
        }

        String[] tableHeader = {
                "Score (over " /* + df.format(integrator.getScoresOver()) */ + ")",
                "Reaction Name 1",
                "Reaction Name 2",
                "Merge?"
        };

        scoresTable.setModel(new DefaultTableModel(tableData, tableHeader) {
            public boolean isCellEditable(int row, int column) {
                return (column == checkBoxColumn);
            }

            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        });
        scoresTable.getColumnModel().getColumn(0).setWidth(5);
        scoresTable.getColumnModel().getColumn(checkBoxColumn).setWidth(5);

        oldModel1 = model1;
        oldModel2 = model2;
        oldThreshold = threshold;
    }

    private void onOK() {
        CyNetwork network1 = bpNetworks.get(firstNetworkComboBox.getSelectedIndex()),
                network2 = bpNetworks.get(secondNetworkComboBox.getSelectedIndex());

        if (BioPaxUtil.getNetworkModel(network1) != oldModel1
                || BioPaxUtil.getNetworkModel(network2) != oldModel2
                || !integrator.getThreshold().equals(oldThreshold))
            onPreview();

        integrator.setOnlyMapping(false);

        List<ConversionScore> alternativeScores = new ArrayList<ConversionScore>();
        for (int rowCnt = 0; rowCnt < scoresTable.getRowCount(); rowCnt++) {
        	Boolean isMerge = false;
        	try{
        		isMerge = (Boolean) scoresTable.getValueAt(rowCnt, checkBoxColumn);
        	} catch (IndexOutOfBoundsException e) {
				// checkBoxColumn!
			}
        	
            if (isMerge) {
                alternativeScores.add(scores.get(rowCnt));
            }
        }

        boolean isColorize = colorizeNodesAccordingToCheckBox.isSelected();

        IntegrateBioPAXTask task = new IntegrateBioPAXTask(integrator,
                network1, network2,
                alternativeScores,
                isColorize,
                firstColor, secondColor, mergedColor);

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
        gbc.gridy = 3;
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
        buttonOK.setText("Integrate");
        buttonOK.setMnemonic('I');
        buttonOK.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
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
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonCancel, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel3, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(panel4, gbc);
        panel4.setBorder(BorderFactory.createTitledBorder("Networks"));
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
        label1.setText("First network");
        label1.setDisplayedMnemonic('F');
        label1.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(label1, gbc);
        firstNetworkComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(firstNetworkComboBox, gbc);
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
        label2.setText("Second network");
        label2.setDisplayedMnemonic('S');
        label2.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(label2, gbc);
        secondNetworkComboBox = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(secondNetworkComboBox, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel5, gbc);
        panel5.setBorder(BorderFactory.createTitledBorder("Options"));
        colorizeMain = new JPanel();
        colorizeMain.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(colorizeMain, gbc);
        colorizeMain.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        colorizeNodesAccordingToCheckBox = new JCheckBox();
        colorizeNodesAccordingToCheckBox.setText("Colorize nodes according to their source ");
        colorizeNodesAccordingToCheckBox.setMnemonic('N');
        colorizeNodesAccordingToCheckBox.setDisplayedMnemonicIndex(9);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        colorizeMain.add(colorizeNodesAccordingToCheckBox, gbc);
        colorsPanel = new JPanel();
        colorsPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        colorizeMain.add(colorsPanel, gbc);
        firstColorButton = new JButton();
        firstColorButton.setText(" ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        colorsPanel.add(firstColorButton, gbc);
        secondColorButton = new JButton();
        secondColorButton.setText(" ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        colorsPanel.add(secondColorButton, gbc);
        mergedColorButton = new JButton();
        mergedColorButton.setText(" ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        colorsPanel.add(mergedColorButton, gbc);
        final JLabel label3 = new JLabel();
        label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
        label3.setText("  First network  ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        colorsPanel.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, label4.getFont().getSize()));
        label4.setText("Second Network");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        colorsPanel.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setFont(new Font(label5.getFont().getName(), Font.BOLD, label5.getFont().getSize()));
        label5.setHorizontalAlignment(10);
        label5.setHorizontalTextPosition(10);
        label5.setText("      Merged      ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        colorsPanel.add(label5, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(panel6, gbc);
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        thresholdPanel = new JPanel();
        thresholdPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(thresholdPanel, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Score threshold");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        thresholdPanel.add(label6, gbc);
        slider1 = new JSlider();
        slider1.setMajorTickSpacing(100);
        slider1.setMinimum(0);
        slider1.setMinorTickSpacing(5);
        slider1.setPaintLabels(true);
        slider1.setPaintTicks(true);
        slider1.setPaintTrack(true);
        slider1.setSnapToTicks(false);
        slider1.setValue(100);
        slider1.putClientProperty("Slider.paintThumbArrowShape", Boolean.FALSE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        thresholdPanel.add(slider1, gbc);
        spinner1 = new JSpinner();
        spinner1.setEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        thresholdPanel.add(spinner1, gbc);
        previewButton = new JButton();
        previewButton.setText("Preview");
        previewButton.setMnemonic('P');
        previewButton.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        thresholdPanel.add(previewButton, gbc);
        final JLabel label7 = new JLabel();
        label7.setFont(new Font(label7.getFont().getName(), label7.getFont().getStyle(), 11));
        label7.setText("Raise the threshold to eliminate non-specific mathces");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        thresholdPanel.add(label7, gbc);
        mergeSimilarReactionsCheckBox = new JCheckBox();
        mergeSimilarReactionsCheckBox.setText("Merge similar reactions");
        mergeSimilarReactionsCheckBox.setMnemonic('M');
        mergeSimilarReactionsCheckBox.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel6.add(mergeSimilarReactionsCheckBox, gbc);
        previewPanel = new JPanel();
        previewPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(previewPanel, gbc);
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview of Similar Reactions"));
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        previewPanel.add(scrollPane1, gbc);
        scoresTable = new JTable();
        //scoresTable.setAutoCreateRowSorter(false);
        scoresTable.setAutoResizeMode(2);
        //scoresTable.setAutoCreateColumnsFromModel(false);
        //scoresTable.setFillsViewportHeight(true);
        scoresTable.setPreferredScrollableViewportSize(new Dimension(600, 200));
        scoresTable.setPreferredSize(new Dimension(600, 32));
        scrollPane1.setViewportView(scoresTable);
        final JLabel label8 = new JLabel();
        label8.setFont(new Font(label8.getFont().getName(), label8.getFont().getStyle(), 11));
        label8.setText("Double click on the row to see match details.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        previewPanel.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("Please select networks to be integrated.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add(label9, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add(spacer1, gbc);
        label1.setLabelFor(firstNetworkComboBox);
        label2.setLabelFor(secondNetworkComboBox);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
