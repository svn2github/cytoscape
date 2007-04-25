package browser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JDialog;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import cytoscape.data.CyAttributes;

/*
 *  This panel will be added to JDialog for attribute modification
 *  Author: Peng-Liang wang    
 *  Date: 9/29/2006
 */

public class AttrSelectModPanel extends JPanel {

	public JButton btnOK = new JButton(" OK ");
	
	public AttrSelectModPanel(DataTable dataTable, CyAttributes data, DataTableModel tableModel, int tableObjectType) {

		SelectPanel selectionPanel = new SelectPanel(dataTable, tableObjectType);
		ModPanel modPanel = new ModPanel(data, tableModel, tableObjectType);
		
        setLayout(new java.awt.GridBagLayout());

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(selectionPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(modPanel, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(btnOK, gridBagConstraints);
        
		btnOK.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				
				JPanel thePanel = (JPanel) getParent();				
				JLayeredPane theLayeredPanel = (JLayeredPane) thePanel.getParent();
				JRootPane theRootPanel = (JRootPane) theLayeredPanel.getParent();
				JDialog theDialog = (JDialog) theRootPanel.getParent();
 
				theDialog.dispose();		
			}
		});

	}

}
