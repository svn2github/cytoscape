/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.dialogs.preferences;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.util.ProxyHandler;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;


/**
 *
 */
public class ProxyServerDialog extends JDialog implements ActionListener, ItemListener {

	private static final long serialVersionUID = -2693844068486336199L;

	/** Creates new form URLimportAdvancedDialog */
	public ProxyServerDialog(javax.swing.JFrame pParent) {
		super(pParent, true);
		this.setTitle("Proxy server setting");
		this.setLocationRelativeTo(pParent);

		initComponents();
		initValues();
	}

	// Variables declaration - do not modify
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox chbUseProxy;
    private javax.swing.JComboBox cmbType;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lbHost;
    private javax.swing.JLabel lbPort;
    private javax.swing.JLabel lbType;
    private javax.swing.JLabel lbUseProxy;
    private javax.swing.JTextField tfHost;
    private javax.swing.JTextField tfPort;
	// End of variables declaration
    
	private void initComponents() {
		
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel3 = new javax.swing.JPanel();
        lbUseProxy = new javax.swing.JLabel();
        chbUseProxy = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        btnUpdate = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lbType = new javax.swing.JLabel();
        lbHost = new javax.swing.JLabel();
        lbPort = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox();
        tfHost = new javax.swing.JTextField();
        tfPort = new javax.swing.JTextField();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        lbUseProxy.setText("Use Proxy");
        jPanel3.add(lbUseProxy);

        chbUseProxy.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chbUseProxy.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanel3.add(chbUseProxy);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel3, gridBagConstraints);

        btnUpdate.setText("Update");
        jPanel1.add(btnUpdate);

        btnCancel.setText("Cancel");
        jPanel1.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Proxy Settings"));
        lbType.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel2.add(lbType, gridBagConstraints);

        lbHost.setText("Host name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel2.add(lbHost, gridBagConstraints);

        lbPort.setText("Port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel2.add(lbPort, gridBagConstraints);

        cmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HTTP", "SOCKS" }));
        cmbType.setMinimumSize(new java.awt.Dimension(61, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 5);
        jPanel2.add(cmbType, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel2.add(tfHost, gridBagConstraints);

        tfPort.setMinimumSize(new java.awt.Dimension(43, 19));
        tfPort.setPreferredSize(new java.awt.Dimension(43, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        jPanel2.add(tfPort, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanel2, gridBagConstraints);

		// add event listeners
		btnUpdate.addActionListener(this);
		btnCancel.addActionListener(this);
		//cmbType.addItemListener(this);
		chbUseProxy.addItemListener(this);

		pack();
		setSize(new Dimension(400, 200));
	} // </editor-fold>

	private void initValues() {
		Proxy p = ProxyHandler.getProxyServer();

		chbUseProxy.setSelected(true);
		cmbType.setEnabled(true);
		tfHost.setEnabled(true);
		tfPort.setEnabled(true);

		if (p == null) {
			tfHost.setText("");
			tfPort.setText("");
			chbUseProxy.setSelected(false);
    		cmbType.setEnabled(false);
    		tfHost.setEnabled(false);
    		tfPort.setEnabled(false);
			return;
		} else if (p.type() == Proxy.Type.HTTP) {
			cmbType.setSelectedItem("HTTP");
		} else if (p.type() == Proxy.Type.SOCKS) {
			cmbType.setSelectedItem("SOCKS");
		}

		InetSocketAddress address = (InetSocketAddress) p.address();
		tfHost.setText(address.getHostName());
		tfPort.setText(new Integer(address.getPort()).toString());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void itemStateChanged(ItemEvent e) {
		
        Object _actionObject = e.getSource();

        if (_actionObject instanceof javax.swing.JCheckBox)
        {
        	if (chbUseProxy.isSelected()) { // UseProxy is checked
            	// enable the setting panel
        		cmbType.setEnabled(true);
        		tfHost.setEnabled(true);
        		tfPort.setEnabled(true);
        	}
        	else
        	{// UseProxy is unchecked
        		// disable the setting panel
        		cmbType.setEnabled(false);
        		tfHost.setEnabled(false);
        		tfPort.setEnabled(false);
        	}
        }
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton) {
			JButton _btn = (JButton) _actionObject;

			if (_btn == btnCancel) {
				this.dispose();
			} else if (_btn == btnUpdate) {
				if (!updateProxyServer())
					return;

				this.dispose();
			}
		}
	}

	private boolean updateProxyServer() {
		Proxy.Type proxyType = Proxy.Type.valueOf(cmbType.getSelectedItem().toString());

		// If UseProxy is unchecked, that means NULL proxy sever
		if (!chbUseProxy.isSelected()) {
			tfHost.setText("");
			tfPort.setText("");
			// If Host or Port is empty, ProxyServer will be set to NULL
		}
		else { //UseProxy is checked
			// Try if we can create a proxyServer, if not, report error
			if (tfHost.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(this, "Host name is empty!", "Warning",
				                              JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
	
			int thePort;
	
			try {
				Integer tmpInteger = new Integer(tfPort.getText().trim());
				thePort = tmpInteger.intValue();
			} catch (Exception exp) {
				JOptionPane.showMessageDialog(this, "Port error!", "Warning",
				                              JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
	
			InetSocketAddress theAddress = new InetSocketAddress(tfHost.getText().trim(), thePort);
	
			try {
				new Proxy(proxyType, theAddress);
			} catch (Exception expProxy) {
				JOptionPane.showMessageDialog(this, "Proxy server error!", "Warning",
				                              JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			//Yes, got valid input for a proxy server
		}
		
		// Update the proxy server info 
		CytoscapeInit.getProperties().setProperty(ProxyHandler.PROXY_HOST_PROPERTY_NAME, tfHost.getText().trim());
		CytoscapeInit.getProperties().setProperty(ProxyHandler.PROXY_PORT_PROPERTY_NAME, tfPort.getText());
		CytoscapeInit.getProperties()
		             .setProperty(ProxyHandler.PROXY_TYPE_PROPERTY_NAME, cmbType.getSelectedItem().toString());

		Cytoscape.firePropertyChange(Cytoscape.PREFERENCES_UPDATED, null, null);

		return true;
	}
}
