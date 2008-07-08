package org.biyoenformatik.cytoscape.ui;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
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

import org.biyoenformatik.cytoscape.util.BioPAXUtil;
import org.biyoenformatik.cytoscape.PaxtoolsReader;
import org.biopax.paxtools.controller.Integrator;
import org.biopax.paxtools.controller.ConversionScore;
import org.biopax.paxtools.model.Model;

public class IntegrateBioPAXDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox firstNetworkComboBox;
    private JComboBox secondNetworkComboBox;
    private JSlider slider1;
    private JSpinner spinner1;
    private JTable table1;
    private JButton previewButton;

    private ArrayList<CyNetwork> bpNetworks = new ArrayList<CyNetwork>();
    private Integrator integrator = null;

    // Keep track to reduce CPU work
    private Model oldModel1 = null, oldModel2 = null;

    private int MAX_SCORE = 100;

    public IntegrateBioPAXDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        spinner1.setModel(new SpinnerNumberModel(MAX_SCORE/3, .0, MAX_SCORE, .1));
        slider1.setModel(new DefaultBoundedRangeModel(MAX_SCORE/3, 0, 0, MAX_SCORE));
        slider1.setMinorTickSpacing(5);
        slider1.setMajorTickSpacing(MAX_SCORE);

        ArrayList<String> comboList = new ArrayList<String>();

        for (CyNetwork cyNetwork : Cytoscape.getNetworkSet()) {
            if (BioPAXUtil.isBioPAXNetwork(cyNetwork)) {
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
                onPreview(true);
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
    }

    private void onPreview(boolean updateTable) {
        CyNetwork network1 = bpNetworks.get(firstNetworkComboBox.getSelectedIndex()),
                network2 = bpNetworks.get(secondNetworkComboBox.getSelectedIndex());

        Model model1 = BioPAXUtil.getNetworkModel(network1),
                model2 = BioPAXUtil.getNetworkModel(network2);

        if (model1 == null || model2 == null) {
            JOptionPane.showMessageDialog(null,
                    "BioPAX models cannot be accessed.",
                    "Invalid BioPAX",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Double threshold = (Double) spinner1.getValue();

        if (!(oldModel1 == model1 && oldModel2 == model2)) {
            integrator = new Integrator(model1, model2);
            integrator.setOnlyMapping(true);
            integrator.setScoresOver(MAX_SCORE);
        }
        integrator.setThreshold(threshold);

        IntegrateUpdateBioPAXTask task =
                new IntegrateUpdateBioPAXTask(this.table1, integrator);

        JTaskConfig jTaskConfig = new JTaskConfig();
        jTaskConfig.setOwner(Cytoscape.getDesktop());
        jTaskConfig.displayCloseButton(true);
        jTaskConfig.displayStatus(true);
        jTaskConfig.setAutoDispose(!updateTable);

        TaskManager.executeTask(task, jTaskConfig);

        oldModel1 = model1;
        oldModel2 = model2;
    }

    private void onOK() {
        CyNetwork network1 = bpNetworks.get(firstNetworkComboBox.getSelectedIndex());
        onPreview(false);
        integrator.setOnlyMapping(false);

        IntegrateBioPAXTask task = new IntegrateBioPAXTask(integrator, network1);

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
        gbc.gridy = 2;
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
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel5, gbc);
        panel5.setBorder(BorderFactory.createTitledBorder("Options"));
        final JLabel label3 = new JLabel();
        label3.setText("Score threshold");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(label3, gbc);
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
        panel5.add(slider1, gbc);
        spinner1 = new JSpinner();
        spinner1.setEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spinner1, gbc);
        previewButton = new JButton();
        previewButton.setText("Preview");
        previewButton.setMnemonic('P');
        previewButton.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel5.add(previewButton, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel6, gbc);
        panel6.setBorder(BorderFactory.createTitledBorder("Preview"));
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(scrollPane1, gbc);
        table1 = new JTable();
        table1.setAutoCreateRowSorter(false);
        table1.setPreferredScrollableViewportSize(new Dimension(450, 200));
        scrollPane1.setViewportView(table1);
        final JLabel label4 = new JLabel();
        label4.setText("Please select networks to be integrated.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add(label4, gbc);
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



class IntegrateUpdateBioPAXTask implements Task {
    private TaskMonitor taskMonitor;
    private Integrator integrator;
    private JTable scoresTable;

    public IntegrateUpdateBioPAXTask(JTable scoresTable, Integrator integrator) {
        this.integrator = integrator;
        this.scoresTable = scoresTable;
    }

    public void run() {
        taskMonitor.setStatus("Matching and scoring reactions...");
        List<ConversionScore> scores = integrator.integrate();
        taskMonitor.setStatus("Updating scores table...");

        Set<ConversionScore> toBeRemoved = new HashSet<ConversionScore>();
        for(ConversionScore aScore: scores)
            if( aScore.getScore() < integrator.getThreshold() )
                toBeRemoved.add(aScore);
        for(ConversionScore aScore: toBeRemoved)
            scores.remove(aScore);


        if (scores.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No matches. Have you tried to lower the threshold value?",
                    "No matches",
                    JOptionPane.INFORMATION_MESSAGE);

            scoresTable.setModel(new DefaultTableModel());
            taskMonitor.setPercentCompleted(100);
	        taskMonitor.setStatus("Scoring successful, but no results.");

            return;
        }

        String[][] tableData = new String[scores.size()][];

        int cnt = 0;
        for(ConversionScore aScore: scores) {
            String[] cols = { "" + aScore.getScore(),
                    BioPAXUtil.getShortNameSmart(aScore.getConversion1()),
                    BioPAXUtil.getShortNameSmart(aScore.getConversion2()) };
            tableData[cnt++] = cols;
        }

        String[] tableHeader = {"Score", "Name 1", "Name 2"};
        scoresTable.setModel(new DefaultTableModel(tableData, tableHeader));

        taskMonitor.setPercentCompleted(100);
	    taskMonitor.setStatus("Scoring successful.");
    }

    public void halt() {
        // No halt support
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Match and score";
    }
}

class IntegrateBioPAXTask implements Task {
    private TaskMonitor taskMonitor;
    private Integrator integrator;
    private CyNetwork network;

    public IntegrateBioPAXTask(Integrator integrator, CyNetwork network) {
        this.integrator = integrator;
        this.network = network;
    }

    public void run() {
        taskMonitor.setStatus("Integrating BioPAX networks...");
        integrator.integrate();

        Model integratedModel = BioPAXUtil.getNetworkModel(network);
        Cytoscape.createNetwork(new PaxtoolsReader(integratedModel), true, null);
        BioPAXUtil.resetNetworkModel(network);

        taskMonitor.setPercentCompleted(100);
	    taskMonitor.setStatus("Integration successful.");
    }

    public void halt() {
        // No halt support
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Integrating BioPAX networks";
    }
}