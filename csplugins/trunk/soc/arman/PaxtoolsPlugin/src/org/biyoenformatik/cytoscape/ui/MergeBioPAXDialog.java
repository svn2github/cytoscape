package org.biyoenformatik.cytoscape.ui;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

import org.biyoenformatik.cytoscape.util.BioPAXUtil;
import org.biyoenformatik.cytoscape.PaxtoolsReader;
import org.biopax.paxtools.io.jena.JenaIOHandler;
import org.biopax.paxtools.io.jena.JenaEditorMap;
import org.biopax.paxtools.controller.Merger;
import org.biopax.paxtools.controller.EditorMap;
import org.biopax.paxtools.model.Model;

public class MergeBioPAXDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList list1;

    private ArrayList<CyNetwork> bpNetworks = new ArrayList<CyNetwork>();

    public MergeBioPAXDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        DefaultListModel listModel = new DefaultListModel();

        for(CyNetwork cyNetwork: Cytoscape.getNetworkSet() ) {
            if( BioPAXUtil.isBioPAXNetwork(cyNetwork) ) {
                listModel.addElement( cyNetwork.getTitle() );
                bpNetworks.add(cyNetwork);
            }
        }
        list1.setModel(listModel);

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
    }

    private void onOK() {
        if( list1.getSelectedIndices().length < 2 ) {
            JOptionPane.showMessageDialog(null,
                                        "Please select at least two networks to be merged.",
                                        "Missing selection",
                                        JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<CyNetwork> selectedNetworks = new ArrayList<CyNetwork>();
        for(int index: list1.getSelectedIndices())
            selectedNetworks.add( bpNetworks.get(index) );

        MergeBioPAXTask task = new MergeBioPAXTask(selectedNetworks);

        JTaskConfig jTaskConfig = new JTaskConfig();
        jTaskConfig.setOwner(Cytoscape.getDesktop());
        jTaskConfig.displayCloseButton(true);
        jTaskConfig.displayStatus(true);
        jTaskConfig.setAutoDispose(false);

        TaskManager.executeTask(task, jTaskConfig);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

}

class MergeBioPAXTask implements Task {
    private ArrayList<CyNetwork> selectedNetworks;
    private TaskMonitor taskMonitor;


    public MergeBioPAXTask(ArrayList<CyNetwork> selectedNetworks) {
        this.selectedNetworks = selectedNetworks;
    }

    public void run() {
        taskMonitor.setStatus("Merging BioPAX networks...");

        EditorMap editorMap = new JenaEditorMap();
        Merger merger = new Merger(editorMap);

        assert selectedNetworks.size() > 1;
        Model base = BioPAXUtil.getNetworkModel(selectedNetworks.get(0));
        selectedNetworks.remove(0);

        Model[] models = new Model[ selectedNetworks.size() ];
        int cnt = 0;
        for(CyNetwork cyNetwork: selectedNetworks)
            models[cnt++] = BioPAXUtil.getNetworkModel(cyNetwork);

        merger.merge(base, models);

        PaxtoolsReader paxtoolsReader = new PaxtoolsReader(base);
        CyNetwork newNetwork = Cytoscape.createNetwork(paxtoolsReader, true, null);
        newNetwork.setTitle("(Merged)" + newNetwork.getTitle());

        taskMonitor.setPercentCompleted(100);
	    taskMonitor.setStatus("Networks successfully merged.");
    }

    public void halt() {
        // No halt support
    }

    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Merging BioPAX networks";
    }
}
