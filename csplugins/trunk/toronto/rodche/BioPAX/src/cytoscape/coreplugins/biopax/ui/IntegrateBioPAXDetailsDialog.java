package cytoscape.coreplugins.biopax.ui;

import org.biopax.paxtools.controller.ConversionScore;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level2.physicalEntityParticipant;

import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.style.BioPaxVisualStyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.data.CyAttributes;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

public class IntegrateBioPAXDetailsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTable matchTable;
    private JLabel cScore;
    private JLabel convName2;
    private JLabel convName1;

    public IntegrateBioPAXDetailsDialog(ConversionScore convScore) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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
        contentPane.registerKeyboardAction(
        		new ActionListener() {
        			public void actionPerformed(ActionEvent e) {
        				onCancel();
        			}
        		}, 
        		KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
        		JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        convName1.setText(BioPaxUtil.getNodeName(convScore.getConversion1()));
        convName2.setText(BioPaxUtil.getNodeName(convScore.getConversion2()));
        cScore.setText("" + convScore.getScore());

        String[] tableHeader = {"First Reaction Component", "->", "Second Reaction Component"};
        String[][] tableData = new String[convScore.getMatchedPEPs().size()][];

        int count = 0;
        for (physicalEntityParticipant pep : convScore.getMatchedPEPs()) {
            String nodeId1 = addNode(pep);
            String nodeId2 = addNode(convScore.getMatch(pep));

            CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
            String name1 =
                    nodeAttributes.getStringAttribute(nodeId1, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL),
                    name2 =
                            nodeAttributes.getStringAttribute(nodeId2, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
            String[] tableRow = {name1, "->", name2};
            tableData[count++] = tableRow;
        }

        matchTable.setModel(new DefaultTableModel(tableData, tableHeader) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        matchTable.getColumnModel().getColumn(1).setWidth(5);

    }

    private String addNode(BioPAXElement bpe) {
    	CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
    	String nodeID = BioPaxUtil.getLocalPartRdfId(bpe);
		CyNode node = Cytoscape.getCyNode(nodeID, true);
		MapBioPaxToCytoscape.setBasicNodeAttributes(node, bpe, null);
		MapBioPaxToCytoscape.mapNodeAttribute(bpe, nodeID, nodeAttributes);
        return node.getIdentifier();
    }
    
    
    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

}
